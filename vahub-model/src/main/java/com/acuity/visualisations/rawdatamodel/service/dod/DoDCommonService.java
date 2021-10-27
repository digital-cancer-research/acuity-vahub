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

import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import lombok.SneakyThrows;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.acuity.visualisations.rawdatamodel.util.Constants.DOUBLE_DASH;

/**
 * Created by knml167 on 5/23/2017. Service providing methods to transform raw objects collections into Details On Demand data Data is sorted, limited and
 * transformed
 */
@Service
public class DoDCommonService extends CommonTableService {
    private static final String EMPTY_VALUE = "";

    /**
     * Returns an ordered map of available DoD columns basing on what's available at provided collection with
     * column key as a columnName and column display name as a value
     */
    public <T> Map<String, String> getDoDColumns(DatasetType datasetType, Collection<T> items) {
        return getDoDColumns(datasetType, items, getType());
    }

    public <T> Map<String, String> getDoDColumns(DatasetType datasetType, Collection<T> items, Column.Type tableType) {
        if (!items.isEmpty()) {
            Map<String, ColumnMetadata> available = getAvailableColumns(datasetType, items, tableType);

            return available.entrySet().stream()
                    .sorted(Comparator.comparing(e -> e.getValue().getOrder()))
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().getDisplayName(), (o1, o2) -> o1, LinkedHashMap::new));
        }
        return new LinkedHashMap<>();
    }

    @SneakyThrows
    private <T> Map<String, ColumnMetadata> getAvailableColumns(DatasetType datasetType, Collection<T> items, Column.Type tableType) {
        if (items.isEmpty()) {
            return Collections.emptyMap();
        }
        // !!! IMPORTANT !!!
        //here we assume that ALL map columns will contain consistent keys across merged datasets (it's a case for Subject)
        //unless we have to break this assumption and need to parse the whole set of data we can use any element from list to get columns metadata
        T anyItem = items.stream().findAny().orElse(null);
        Map<String, Function<Object, Pair<ColumnMetadata, Object>>> classColumnReaders = getClassColumnReaders(anyItem, datasetType, tableType);

        return getColumns(classColumnReaders, items, anyItem);
    }

    private <T> Map<String, ColumnMetadata> getColumns(Map<String, Function<Object, Pair<ColumnMetadata, Object>>> classColumnReaders,
                                                       Collection<T> items, T anyItem) {
        return classColumnReaders.entrySet()
                .stream()
                .filter(reader -> items.stream().map(i -> reader.getValue().apply(i).getValue())
                        .anyMatch(val -> val != null && !EMPTY_VALUE.equals(val)))
                .flatMap(reader -> {
                    //again, here we pick a random item from merged datasets assuming map keys are consistent, see comment above
                    Pair<ColumnMetadata, ?> res = reader.getValue().apply(anyItem);
                    Object valObj = res.getValue();
                    if (valObj instanceof Map) {
                        Map<?, ?> valMap = (Map<?, ?>) valObj;
                        return valMap.keySet().stream()
                                .map(k -> new ImmutablePair<>(k + DOUBLE_DASH + reader.getKey(),
                                        res.getLeft().toBuilder().displayName(k + " " + res.getLeft().getDisplayName()).build()));
                    } else {
                        return Stream.of(new ImmutablePair<>(reader.getKey(), res.getLeft()));
                    }
                })
                .collect(Collectors.toMap(Pair::getKey, Pair::getValue));
    }

    @Override
    protected Column.Type getType() {
        return Column.Type.DOD;
    }
}
