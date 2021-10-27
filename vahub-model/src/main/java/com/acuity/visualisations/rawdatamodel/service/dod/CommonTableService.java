/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rawdatamodel.service.dod;

import com.acuity.visualisations.rawdatamodel.util.AlphanumEmptyLastComparator;
import com.acuity.visualisations.rawdatamodel.util.AnnotationUtil;
import com.acuity.visualisations.rawdatamodel.util.AnnotationWithFieldAndReader;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.util.MetadataCacheKey;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.EventWrapper;
import com.google.common.math.DoubleMath;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.cglib.reflect.FastClass;
import org.springframework.cglib.reflect.FastMethod;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import static com.acuity.visualisations.rawdatamodel.util.Column.Type;
import static com.acuity.visualisations.rawdatamodel.util.Constants.DOUBLE_DASH;
import static com.acuity.visualisations.rawdatamodel.util.DodUtil.EVENT_ID;
import static java.util.stream.Collectors.toMap;
import static org.apache.commons.lang3.ArrayUtils.EMPTY_STRING_ARRAY;

@Slf4j
public abstract class CommonTableService {

    public static final String NULL_VALUE = "null";

    private final Map<MetadataCacheKey, Map<String, ColumnMetadata>> classMetadataCache =
            new ConcurrentHashMap<>();
    private final Map<MetadataCacheKey, Map<String, Function<Object, Pair<ColumnMetadata, Object>>>> classColumnReadersCache =
            new ConcurrentHashMap<>();

    protected abstract Type getType();

    public <T> List<Map<String, String>> getColumnData(DatasetType datasetType, Collection<T> items, List<SortAttrs> sortAttrs,
                                                       long from, long count, boolean withEventId, Column.Type tableType) {
        if (items.isEmpty()) {
            return Collections.emptyList();
        }

        Collection<T> itemsList = sortItems(items, sortAttrs, datasetType, tableType);
        return itemsList.stream().skip(from).limit(count).map(event -> {
            Map<String, String> row = new HashMap<>();

            Map<String, Function<Object, Pair<ColumnMetadata, Object>>> columnReaders = getClassColumnReaders(event, datasetType, tableType);

            columnReaders.forEach((columnKey, columnReader) -> {
                Pair<ColumnMetadata, ?> res = columnReader.apply(event);
                Object valObj = res.getRight();
                if (valObj instanceof Map) {
                    ((Map<?, ?>) valObj).forEach((mapKey, mapValue) ->
                            row.put(mapKey.toString() + DOUBLE_DASH + columnKey,
                                    mapValue == null ? null : valuePostProcess(res.getLeft(), mapValue).toString()));
                } else {
                    String value = (valObj == null || NULL_VALUE.equals(valObj.toString())) ? null : valuePostProcess(res.getLeft(), valObj).toString();
                    row.put(columnKey, value);
                }
            });
            if (event instanceof HasStringId && withEventId) {
                row.put(EVENT_ID, ((HasStringId) event).getId());
            }
            return row;
        }).collect(Collectors.toList());
    }

    public <T> List<Map<String, String>> getColumnData(DatasetType datasetType, Collection<T> items, List<SortAttrs> sortAttrs,
                                                       long from, long count, boolean withEventId) {
        return getColumnData(datasetType, items, sortAttrs, from, count, withEventId, getType());
    }

    private <T> Collection<T> sortItems(Collection<T> items, List<SortAttrs> sortAttrs, DatasetType datasetType, Column.Type tableType) {
        Collection<T> itemsList = items;
        sortAttrs = CollectionUtils.isEmpty(sortAttrs) ? getDefaultSortBy(datasetType, items, tableType) : sortAttrs;
        if (!CollectionUtils.isEmpty(sortAttrs)) {
            List<T> sorted = items instanceof List ? (List<T>) items : new ArrayList<>(items);
            sorted.sort(getComplexComparator(datasetType, sortAttrs));
            itemsList = sorted;
        }
        return itemsList;
    }

    public <T> List<Map<String, String>> getColumnData(DatasetType datasetType, Collection<T> items, boolean withEventId) {
        return getColumnData(datasetType, items, Collections.emptyList(), 0, Long.MAX_VALUE, withEventId);
    }

    public <T> List<Map<String, String>> getColumnData(DatasetType datasetType, Collection<T> items, Column.Type tableType) {
        return getColumnData(datasetType, items, Collections.emptyList(), 0, Long.MAX_VALUE, true, tableType);
    }

    /**
     * Default implementation to retrieve data. Returns data as a list of maps, where key is columnName and value is column value
     */
    public <T> List<Map<String, String>> getColumnData(DatasetType datasetType, Collection<T> items) {
        return getColumnData(datasetType, items, Collections.emptyList(), 0, Long.MAX_VALUE, true);
    }

    /**
     * Returns an ordered by the order property map of available columns basing on provided item class and item's
     * wrapper class with column key as a columnName and column display name as a value
     */
    public <T, W> Map<String, String> getColumns(DatasetType datasetType, Class<T> itemClass, Class<W> rawEventClass) {
        Map<String, ColumnMetadata> columnsMetadata = getClassColumnMetadata(itemClass, rawEventClass, datasetType, getType());
        return getOrderedColumns(columnsMetadata);
    }

    /**
     * Returns an ordered map of available columns basing on provided item class with column key as a columnName and
     * column display name as a value
     */
    public <T> Map<String, String> getColumns(DatasetType datasetType, Class<T> itemClass) {
        Map<String, ColumnMetadata> columnsMetadata = getClassColumnMetadata(itemClass, datasetType);
        return getOrderedColumns(columnsMetadata);
    }

    private Map<String, String> getOrderedColumns(Map<String, ColumnMetadata> columnsMetadata) {
        return columnsMetadata.entrySet().stream()
                .sorted(Comparator.comparing(e -> e.getValue().getOrder()))
                .collect(toMap(Map.Entry::getKey, e -> e.getValue()
                        .getDisplayName(), (o1, o2) -> o1, LinkedHashMap::new));
    }

    <T> ArrayList<SortAttrs> getDefaultSortBy(DatasetType datasetType, Collection<T> items, Column.Type tableType) {
        T event = items.iterator().next();
        Map<String, ColumnMetadata> columnsMetadata;
        if (event instanceof EventWrapper<?>) {
            Object wrappedEvent = ((EventWrapper<?>) event).getEvent();
            columnsMetadata = getClassColumnMetadata(event.getClass(), wrappedEvent.getClass(), datasetType, tableType);
        } else {
            columnsMetadata = getClassColumnMetadata(event.getClass(), datasetType);
        }
        return columnsMetadata.values().stream()
                .filter(ColumnMetadata::isDefaultSortBy)
                .sorted(Comparator.comparingDouble(o -> o.defaultSortOrder))
                .map(o -> new SortAttrs(o.columnName, o.defaultSortReversed))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private FastMethod getFastMethod(Class<?> clz, Method reader) {
        FastClass fc = FastClass.create(clz);
        return fc.getMethod(reader);
    }

    /**
     * Returns map of columnNames and {@link ColumnMetadata} pairs, according to {@link Column#order()}
     * annotation parameter
     */
    private Map<String, ColumnMetadata> getClassColumnMetadata(Class<?> eventClass, Class<?> rawItemClass,
                                                               DatasetType datasetType, Column.Type tableType) {
        Map<String, ColumnMetadata> columnsMetadata = getClassColumnMetadataImpl(eventClass, datasetType, tableType);
        Map<String, ColumnMetadata> wrappedSort = getClassColumnMetadataImpl(rawItemClass, datasetType, tableType);
        columnsMetadata.putAll(wrappedSort);
        return new HashMap<>(columnsMetadata);
    }

    /**
     * Returns map of columnNames and {@link ColumnMetadata} pairs, according to {@link Column#order()}
     * annotation parameter
     */
    private Map<String, ColumnMetadata> getClassColumnMetadata(Class<?> clazz, DatasetType datasetType) {
        return getClassColumnMetadataImpl(clazz, datasetType, getType());
    }

    private Map<String, ColumnMetadata> getClassColumnMetadata(Object event, Column.DatasetType datasetType, Column.Type tableType) {
        Map<String, ColumnMetadata> columnsMetadata = getClassColumnMetadataImpl(event, datasetType, tableType);
        if (event instanceof EventWrapper) {
            Object wrappedEvent = ((EventWrapper<?>) event).getEvent();
            columnsMetadata.putAll(getClassColumnMetadataImpl(wrappedEvent, datasetType, tableType));
        }
        return columnsMetadata;
    }

    /**
     * Returns map of columnNames and {@link ColumnMetadata} pairs, according to {@link Column#order()}
     * annotation parameter
     */
    Map<String, ColumnMetadata> getClassColumnMetadataImpl(Object event, Column.DatasetType datasetType, Column.Type tableType) {
        Class<?> clazz = event instanceof Class ? (Class<?>) event : event.getClass();
        MetadataCacheKey key = new MetadataCacheKey(clazz, datasetType, tableType);
        return classMetadataCache.computeIfAbsent(key, classMetadataExtractor(key)
        );
    }

    private Function<MetadataCacheKey, Map<String, ColumnMetadata>> classMetadataExtractor(MetadataCacheKey key) {
        return c -> AnnotationUtil.getAnnotatedMethods(key)
                .collect(toMap(this::getColumnName, columnMetadataExtractor(key.getEntity())));
    }

    private Function<AnnotationWithFieldAndReader<Column>, ColumnMetadata> columnMetadataExtractor(Class<?> clazz) {
        return elem -> {
            Column a = elem.getAnnotationObject();
            Method reader = elem.getFieldReader();
            return new ColumnMetadata(getFastMethod(clazz, reader),
                    a.order(),
                    getColumnName(elem),
                    a.displayName(),
                    a.dateFormat(),
                    a.defaultSortBy(),
                    a.defaultSortReversed(),
                    a.defaultSortOrder());
        };
    }

    private String getColumnName(AnnotationWithFieldAndReader<Column> a) {
        Member elem = a.getField() != null ? a.getField() : a.getFieldReader();
        return StringUtils.isNotBlank(a.getAnnotationObject().columnName()) ? a.getAnnotationObject().columnName() : elem.getName();
    }

    /**
     * Returns a map, where the key is column display name and value if function that can be applied on object to get this column's value.
     */
    Map<String, Function<Object, Pair<ColumnMetadata, Object>>> getClassColumnReaders(
            Object event, Column.DatasetType datasetType, Column.Type tableType) {
        return classColumnReadersCache.computeIfAbsent(new MetadataCacheKey(event.getClass(), datasetType, tableType), anything -> {
            Map<String, ColumnMetadata> columnMetadata = getClassColumnMetadata(event, datasetType, tableType);

            Map<String, Function<Object, Pair<ColumnMetadata, Object>>> result = columnMetadata.entrySet().stream()
                    .collect(toMap(Map.Entry::getKey, e -> o -> new ImmutablePair<>(e.getValue(), invokeMethod(e.getValue(), o))));
            if (event instanceof EventWrapper<?>) {
                Object wrappedEvent = ((EventWrapper<?>) event).getEvent();
                Map<String, ColumnMetadata> wrappedEventMethodMap = getClassColumnMetadata(wrappedEvent, datasetType, tableType);
                Map<String, Function<Object, Pair<ColumnMetadata, Object>>> collect = wrappedEventMethodMap.entrySet().stream()
                        .collect(toMap(Map.Entry::getKey,
                                e -> o -> new ImmutablePair<>(e.getValue(), invokeMethod(e.getValue(), ((EventWrapper<?>) o).getEvent()))));
                result.putAll(collect);
            }
            return result;
        });
    }

    public void writeCsv(List<Map<String, String>> data, Map<String, String> columnsNames, Writer writer) {
        if (data.isEmpty()) {
            return;
        }
        String[] fields = columnsNames.keySet().toArray(EMPTY_STRING_ARRAY);
        try (ICsvMapWriter csvMapWriter = new CsvMapWriter(writer, CsvPreference.EXCEL_PREFERENCE)) {
            csvMapWriter.writeHeader(columnsNames.values().toArray(new String[0]));
            for (Map<String, String> item : data) {
                csvMapWriter.write(item, fields);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static Object valuePostProcess(ColumnMetadata metadata, Object val) {
        if (val instanceof Date) {
            return dateFormat(val, metadata.getDateFormat());
        } else {
            if (val instanceof Double) {
                return doubleFormat((double) val);
            } else {
                return val;
            }
        }
    }

    private static Object invokeMethod(ColumnMetadata metadata, Object object) {
        FastMethod method = metadata.getReadMethod();
        try {
            return method.invoke(object, new Object[] {});
        } catch (InvocationTargetException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    private static String doubleFormat(double d) {
        if (DoubleMath.isMathematicalInteger(d)) {
            return String.format("%d", (long) d);
        } else {
            // DecimalFormat is not synchronized, it's JavaDoc recommends
            // to create separate format instances for each thread.
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.setMaximumFractionDigits(2);
            decimalFormat.setMinimumFractionDigits(0);
            decimalFormat.setGroupingUsed(false);
            return decimalFormat.format(d);
        }
    }

    private static String dateFormat(Object obj, String dateFormat) {
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        formatter.setTimeZone(TimeZone.getTimeZone(DaysUtil.GMT_TIMEZONE));
        return formatter.format(obj);
    }

    private <T> Comparator<T> getComplexComparator(DatasetType datasetType, List<SortAttrs> sortAttrs) {
        Iterator<SortAttrs> iter = sortAttrs.iterator();
        Comparator<T> comparator = null;
        while (iter.hasNext()) {
            comparator = (comparator == null) ? getSimpleComparator(datasetType, iter.next())
                    : comparator.thenComparing(getSimpleComparator(datasetType, iter.next()));
        }
        return comparator;
    }

    private <T> Comparator<T> getSimpleComparator(DatasetType datasetType, SortAttrs sortAttr) {
        Comparator<T> result = (o1, o2) -> {
            if (o1.getClass() != o2.getClass()) {
                return 0;
            }
            Function<Object, Pair<ColumnMetadata, Object>> sortByReader = getClassColumnReaders(o1, datasetType, getType())
                    .get(sortAttr.getSortBy());
            if (sortByReader == null) {
                return 0;
            }
            Object v1 = sortByReader.apply(o1).getValue();
            Object v2 = sortByReader.apply(o2).getValue();
            if ((v1 instanceof String) && (v2 instanceof String)) {
                return AlphanumEmptyLastComparator.getInstance().compare((String) v1, (String) v2);
            }
            if ((v1 == null || v1 instanceof Comparable) && (v2 == null || v2 instanceof Comparable)) {
                return ObjectUtils.compare((Comparable) v1, (Comparable) v2, true);
            }
            return 0;
        };
        if (sortAttr.isReversed()) {
            result = result.reversed();
        }
        return result;
    }

    @AllArgsConstructor
    @Getter
    @Builder(toBuilder = true)
    static class ColumnMetadata {
        private FastMethod readMethod;
        private Double order;
        private String columnName;
        private String displayName;
        private String dateFormat;
        private boolean defaultSortBy;
        private boolean defaultSortReversed;
        private double defaultSortOrder;
    }
}
