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

package com.acuity.visualisations.rawdatamodel.util;

import com.acuity.visualisations.common.util.ObjectConvertor;
import com.acuity.visualisations.common.util.OptionalUtils;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.Bin;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.GroupByOptionAndParams;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.BinableOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.HasDrugOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.PopulationGroupingOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.TimestampOption;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.googlecode.cqengine.attribute.Attribute;
import com.googlecode.cqengine.query.option.QueryOptions;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.TimestampType.DATE;
import static java.util.stream.Collectors.toMap;

@Slf4j
public final class Attributes {

    public static final String DEFAULT_EMPTY_VALUE = "(Empty)";
    public static final String DEFAULT_EMPTY_VALUE_TO_LOWER_CASE = "(empty)";

    private static final Map<? super Enum<?>, Boolean> IS_POPULATION_OPTION_CACHE = new ConcurrentHashMap<>();

    private Attributes() {
    }

    public static <T extends HasSubject> EntityAttribute<T> getBinnedAttribute(String name, GroupByOption.Params params, Function<T, ?> origin) {
        return getBinnedAttribute(name, params, origin, origin);
    }

    public static <T extends HasSubject> EntityAttribute<T> getBinnedAttribute(String name, GroupByOption.Params params,
                                                                               Function<T, ?> originStart, Bin binEnd) {
        return EntityAttribute.attribute(
                name,
                getBinnedAttributeExtractor(params, originStart, (T e, Integer binSize) -> binEnd));
    }

    public static <T extends HasSubject> EntityAttribute<T> getBinnedAttribute(String name, GroupByOption.Params params,
                                                                               Function<T, ?> originStart, Function<T, ?> originEnd) {
        return EntityAttribute.attribute(
                name,
                getBinnedAttributeExtractor(
                        params,
                        originStart,
                        (T e, Integer binSize) -> getBin(e, originEnd.apply(e) instanceof Date
                                ? getDateAttribute("END", params, originEnd)
                                : EntityAttribute.attribute("END", originEnd), binSize)));
    }

    private static <T extends HasSubject> Function<T, ?> getBinnedAttributeExtractor(GroupByOption.Params params,
                                                                                     Function<T, ?> originStart,
                                                                                     BiFunction<T, Integer, Bin> binEndExtractor) {
        return e -> {
            Integer binSize = params.getInt(GroupByOption.Param.BIN_SIZE);
            Bin binStart = getBin(e, originStart.apply(e) instanceof Date
                    ? getDateAttribute("START", params,  originStart)
                    : EntityAttribute.attribute("START", originStart), binSize);
            return params.getBool(GroupByOption.Param.BIN_INCL_DURATION)
                    ? getBinsBetween(binStart, binEndExtractor.apply(e, binSize))
                    : binStart;
        };
    }

    public static  <B extends Bin<? extends Comparable<?>>> List<? super B> getBinCategories(Collection<? extends B> bins) {
        Optional<B> firstBin = bins.stream().map(o -> (B) o).min(Comparator.naturalOrder());
        Optional<B> lastBin = bins.stream().map(o -> (B) o).max(Comparator.naturalOrder());
        if (lastBin.isPresent() && firstBin.isPresent()) {
            return Attributes.getBinsBetween(firstBin.get(), lastBin.get());
        }
        return Collections.emptyList();
    }

    public static <B extends Bin<? extends Comparable<?>>> List<? super B> getBinsBetween(final B start, final B end) {
        List<Bin<?>> bins = new ArrayList<>();
        if (!(start.isEmpty() || end.isEmpty() || start.compareTo(end) > 0)) {
            bins.add(start);
            Bin<? extends Comparable<?>> bin = start;
            while (bin.compareTo(end) < 0) {
                bin = bin.getNextBin();
                bins.add(bin);
            }
        } else {
            bins.add(Bin.empty());
        }
        return bins;
    }

    private static <T> Bin getBin(T e, EntityAttribute<T> origin, Integer binSize) {
        final Object value = Attributes.get(origin, e);
        return Bin.newInstance(value, binSize);
    }

    public static Object defaultNullableValue(Object o) {
        return o == null ? DEFAULT_EMPTY_VALUE : o;
    }

    public static Object defaultNullableValue(Object o, String def) {
        return o == null ? def : o;
    }

    /**
     * Gets the value from the object from the attribute
     * <p>
     * attribute=Lab.visitNumberAttribute, object = Lab(visitNumber=12), class Lab.class the returned value would be 12
     */
    public static Object get(Attribute attribute, Object object) {
        Iterable values = attribute.getValues(object, new QueryOptions());
        Iterator iterator = values.iterator();
        if (iterator.hasNext()) {
            return defaultNullableValue(iterator.next());
        }

        return DEFAULT_EMPTY_VALUE;
    }

    public static <T> Object get(EntityAttribute<T> attribute, T object) {
        return defaultNullableValue(attribute.getFunction().apply(object));
    }

    public static <T, G extends Enum<G> & GroupByOption<T>> Object get(GroupByOptionAndParams<T, G> groupByOption, T object) {
        if (groupByOption.getGroupByOption() == null) {
            return DEFAULT_EMPTY_VALUE;
        }
        if (GroupByOption.isPopulationOption(groupByOption.getGroupByOption()) && object instanceof Subject) {
            PopulationGroupByOptions correspondingSubjectOption = GroupByOption.getCorrespondingSubjectOption(groupByOption.getGroupByOption());
            EntityAttribute<Subject> attribute =
                    groupByOption.getParams() == null
                            ? correspondingSubjectOption.getAttribute()
                            : correspondingSubjectOption.getAttribute(groupByOption.getParams());
            return get(attribute, (Subject) object);
        } else {
            return get(groupByOption.getAttribute(), object);
        }
    }

    /**
     * Gets result of calculating ChartGroupByOptions grouped by attribute
     * and grouped by setting type {@link ChartGroupBySetting}
     */
    public static <T, G extends Enum<G> & GroupByOption<T>> GroupByKey<T, G> get(ChartGroupByOptions<T, G> groupByOptions, T object) {
        Map<ChartGroupBySetting, Object> v1 = groupByOptions.getOptions().entrySet().stream().collect(
                toMap(
                        Map.Entry::getKey,
                        v -> get(v.getValue(), object)
                )
        );
        Map<G, Object> v2 = groupByOptions.getTrellisOptions().stream().collect(
                toMap(
                        GroupByOptionAndParams::getGroupByOption,
                        v -> get(v, object)
                )
        );
        return new GroupByKey<>(v1, v2);
    }

    /**
     * Gets the values from the object from the attribute
     * <p>
     * attribute=Subject.attendedVisitNumbers, object = Lab(attendedVisitNumbers=[1, 2]), class Lab.class the returned value would be 12
     */
    public static <T> List<T> getValues(Attribute attribute, Object object) {
        List<T> target = new ArrayList<>();
        attribute.getValues(object, new QueryOptions()).forEach(e -> target.add((T) e));
        return target;
    }

    /**
     * Same as get but casts the result to a double
     */
    public static Double getDouble(Attribute attribute, Object object) {
        Object value = get(attribute, object);
        return ObjectConvertor.toDouble(value);
    }

    /**
     * Same as get but casts the result to a integer
     */
    public static Integer getInt(Attribute attribute, Object object) {
        Object value = get(attribute, object);
        return ObjectConvertor.toInt(value);
    }

    /**
     * Same as get but casts the result to a String
     */
    public static String getString(Attribute attribute, Object object) {
        Object value = get(attribute, object);
        return ObjectConvertor.toString(value);
    }

    /**
     * Same as get but casts the result to a String
     */
    public static Date getDate(Attribute attribute, Object object) {
        Object value = get(attribute, object);
        return ObjectConvertor.toDate(value);
    }

    public static <T extends HasSubject> EntityAttribute<T> getDateAttribute(String name, GroupByOption.Params params, Function<T, ?> getDate) {
        GroupByOption.TimestampType timestampType = params == null ? DATE : params.getTimestampType();

        if (timestampType == DATE) {
            return EntityAttribute.attribute(name, getDate);
        }

        return EntityAttribute.attribute(name, (T e) -> calculatePeriodLength(
                timestampType.getPeriodType(),
                calculateDateFrom(timestampType.getStartDateType(), e, params),
                (Date) getDate.apply(e))
                .orElse(null));
    }

    private static Optional<? extends Number> calculatePeriodLength(GroupByOption.TimestampType.PeriodType periodType, Date dateFrom, Date dateTo) {
        switch (periodType) {
            case DAYS:
                return OptionalUtils.intToOptional(DaysUtil.daysBetween(dateFrom, dateTo));
            case DAYS_HOURS:
                return OptionalUtils.doubleToOptional(DaysUtil.dayHoursSinceDate(dateFrom, dateTo));
            case WEEKS:
                return OptionalUtils.intToOptional(DaysUtil.weeksBetween(dateFrom, dateTo));
            default:
                throw new CantGetDateAttributeException("Can't handle period type: " + periodType);
        }
    }

    private static <T extends HasSubject> Date calculateDateFrom(GroupByOption.TimestampType.StartDateType startDateType, T e, GroupByOption.Params params) {
        switch (startDateType) {
            case FIRST_DOSE:
                return e.getSubject().getFirstTreatmentDate();
            case RANDOMISATION:
                return e.getSubject().getDateOfRandomisation();
            case FIRST_DOSE_OF_DRUG:
                String drug = params.getStr(GroupByOption.Param.DRUG_NAME);
                return e.getDateOfFirstDoseOfDrug(drug);
            default:
                throw new CantGetDateAttributeException("Can't handle start date type: " + startDateType);
        }
    }

    @SneakyThrows
    public static <T, G extends Enum<G> & GroupByOption<T>> boolean isPopulationOption(G option) {
        //this is a bit hacked to tune performance
        Boolean res = IS_POPULATION_OPTION_CACHE.get(option);
        if (res == null) {
                res = IS_POPULATION_OPTION_CACHE.computeIfAbsent(
                option,
                o -> {
                    try {
                        return option.getClass().getField(option.name()).getAnnotation(PopulationGroupingOption.class) != null;
                    } catch (Exception e) {
                        log.error("Ignored exception", e);
                        return false;
                    }
                });
        }

        return res;
    }

    static class CantGetDateAttributeException extends RuntimeException {
        CantGetDateAttributeException(String message) {
            super(message);
        }
    }

    /**
     * This tells that option has {@link com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.BinableOption} annotation
     * that means that option supports {@link GroupByOption.Param#BIN_SIZE} parameter
     * */
    @SneakyThrows
    public static  <T, G extends Enum<G> & GroupByOption<T>> boolean isBinableOption(G option) {
        return option.getClass().getField(option.name()).getAnnotation(BinableOption.class) != null;
    }

    /**
     * This tells that option has {@link com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.HasDrugOption} annotation
     * that means that option supports {@link GroupByOption.Param#DRUG_NAME} parameter
     */
    @SneakyThrows
    public static <T, G extends Enum<G> & GroupByOption<T>> boolean hasDrugOption(G option) {
        return option.getClass().getField(option.name()).getAnnotation(HasDrugOption.class) != null;
    }

    /**
     * This tells that option has {@link com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.TimestampOption} annotation
     * that means that option supports {@link GroupByOption.TimestampType} parameter
     * */
    @SneakyThrows
    public static  <T, G extends Enum<G> & GroupByOption<T>> boolean isTimestampOption(G option) {
        return option.getClass().getField(option.name()).getAnnotation(TimestampOption.class) != null;
    }

    /**
     * This tells that option has {@link com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.TimestampOption} annotation
     * that means that option supports {@link GroupByOption.TimestampType} parameter
     * */
    @SneakyThrows
    public static  <T, G extends Enum<G> & GroupByOption<T>> boolean isTimestampOptionSupportDuration(G option) {
        final TimestampOption annotation = option.getClass().getField(option.name()).getAnnotation(TimestampOption.class);
        return annotation != null && annotation.hasDuration();
    }
}
