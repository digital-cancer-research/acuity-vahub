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

import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by knml167 on 8/23/2017.
 */
@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public final class ChartGroupByOptionsFiltered<T, G extends Enum<G> & GroupByOption<T>> implements Serializable {
    //@NonNull FIXME: find a way to make Liver work with this annotation
    private ChartGroupByOptions<T, G> settings;
    private Collection<Map<G, Object>> filterByTrellisOptions;


    private ChartGroupByOptionsFiltered() {
    }

    public static <T, G extends Enum<G> & GroupByOption<T>> ChartGroupBySettingsFilteredBuilder<T, G> builder(ChartGroupByOptions<T, G> settings) {
        return new ChartGroupBySettingsFilteredBuilder<>(settings);
    }

    public ChartGroupBySettingsFilteredBuilder<T, G> toBuilder() {
        final ChartGroupBySettingsFilteredBuilder<T, G> builder = new ChartGroupBySettingsFilteredBuilder<>(this.settings);
        builder.filterByTrellisOptions = this.filterByTrellisOptions;
        return builder;
    }


    public static class ChartGroupBySettingsFilteredBuilder<T, G extends Enum<G> & GroupByOption<T>> {
        private ChartGroupByOptions<T, G> settings;
        private Collection<Map<G, Object>> filterByTrellisOptions = new ArrayList<>();

        public ChartGroupBySettingsFilteredBuilder(ChartGroupByOptions<T, G> settings) {
            this.settings = settings;
        }

        public ChartGroupBySettingsFilteredBuilder(ChartGroupByOptionsFiltered<T, G> settingsFiltered) {
            this.settings = settingsFiltered.settings;
            this.filterByTrellisOptions = settingsFiltered.filterByTrellisOptions;
        }

        public ChartGroupBySettingsFilteredBuilder<T, G> withFilterByTrellisOption(Map<G, Object> optionValues) {
            filterByTrellisOptions.add(Collections.unmodifiableMap(optionValues));
            return this;
        }

        public ChartGroupBySettingsFilteredBuilder<T, G> withFilterByTrellisOption(G option, Object optionValue) {
            Map<G, Object> optionValues = new HashMap<>();
            optionValues.put(option, optionValue);
            filterByTrellisOptions.add(Collections.unmodifiableMap(optionValues));
            return this;
        }

        public ChartGroupBySettingsFilteredBuilder<T, G> withFilterByTrellisOptions(Collection<Map<G, Object>> filterByTrellisOptions) {
            this.filterByTrellisOptions = filterByTrellisOptions.stream().map(Collections::unmodifiableMap).collect(Collectors.toList());
            return this;
        }

        public ChartGroupByOptionsFiltered<T, G> build() {
            return new ChartGroupByOptionsFiltered<T, G>(settings, Collections.unmodifiableCollection(filterByTrellisOptions));
        }
    }
}
