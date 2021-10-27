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

package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Getter;
import lombok.ToString;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by knml167 on 8/23/2017.
 * This is a generic VO to store calculated event settings according to {@link ChartGroupByOptions} applied to event
 */
@ToString
public final class GroupByKey<T, G extends Enum<G> & GroupByOption<T>>
        implements LimitableBySettings<GroupByKey<T, G>, GroupByKey<Subject, PopulationGroupByOptions>> {
    @Getter
    private Map<ChartGroupBySetting, Object> values;

    @Getter
    private Map<G, Object> trellisByValues;

    private final Integer hash;

    public Object getValue(ChartGroupBySetting setting) {
        return values.get(setting);
    }

    public GroupByKey(Map<ChartGroupBySetting, Object> values, Map<G, Object> trellisByValues) {
        this.values = Collections.unmodifiableMap(values.isEmpty() ? Collections.emptyMap() : new EnumMap<>(values));
        this.trellisByValues = Collections.unmodifiableMap(trellisByValues.isEmpty() ? Collections.emptyMap() : new EnumMap<>(trellisByValues));
        hash = getHash();
    }

    @Override
    public GroupByKey<T, G> limitedBySettings(Set<ChartGroupBySetting> settings) {
        return new GroupByKey<>(values.entrySet().stream().filter(e -> settings.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), trellisByValues);
    }

    @Override
    public GroupByKey<Subject, PopulationGroupByOptions> limitedByPopulationTrellisOptions() {
        final Map<PopulationGroupByOptions, Object> res = trellisByValues.entrySet().stream()
                .filter(e -> GroupByOption.isPopulationOption(e.getKey())).collect(
                        Collectors.toMap(e -> GroupByOption.getCorrespondingSubjectOption(e.getKey()), Map.Entry::getValue));
        return new GroupByKey<>(new HashMap<>(), res);
    }

    public GroupByKey<T, G> copyReplacing(ChartGroupBySetting setting, Object value) {
        Map<ChartGroupBySetting, Object> newValues = values.isEmpty() ? Collections.emptyMap() : new EnumMap<>(values);
        newValues.put(setting, value);
        return new GroupByKey<>(newValues, trellisByValues);
    }
    public GroupByKey<T, G> copyReplacing(G option, Object value) {
        Map<G, Object> newTrellisByValues = new HashMap<>(trellisByValues);
        newTrellisByValues.put(option, value);
        return new GroupByKey<>(values, newTrellisByValues);
    }

    /*
    * Need custom equals and hashcode here due to performance optimisations
    * */

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (o.hashCode() != this.hashCode()) {
            return false;
        }
        GroupByKey<?, ?> that = (GroupByKey<?, ?>) o;

        if (!values.equals(that.values)) {
            return false;
        }
        if (!trellisByValues.equals(that.trellisByValues)) {
            return false;
        }
        return hash.equals(that.hash);
    }

    public int hashCode() {
        return hash;
    }

    private int getHash() {
        final int prime = 59;
        int result = 1;
        final Object valuesObj = this.values;
        result = result * prime + (valuesObj == null ? 43 : valuesObj.hashCode());
        final Object trellisByValuesObj = this.trellisByValues;
        result = result * prime + (trellisByValuesObj == null ? 43 : trellisByValuesObj.hashCode());
        return result;
    }
}
