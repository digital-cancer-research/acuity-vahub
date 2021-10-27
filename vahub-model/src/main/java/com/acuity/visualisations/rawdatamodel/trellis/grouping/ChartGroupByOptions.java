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

import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by knml167 on 8/23/2017.
 * This is a generic model for storing chart settings:
 * trellis options set and  a set of {@link ChartGroupBySetting} options
 */
@Getter
@EqualsAndHashCode
@ToString
public class ChartGroupByOptions<T, G extends Enum<G> & GroupByOption<T>>
        implements LimitableBySettings<ChartGroupByOptions<T, G>, ChartGroupByOptions<Subject, PopulationGroupByOptions>> {

    public enum ChartGroupBySetting {
        COLOR_BY, X_AXIS, Y_AXIS, SERIES_BY, VALUE, NAME, ORDER_BY, UNIT, START, END, STANDARD_DEVIATION
    }

    /**
     * This class stores grouping option and its params
     */
    @EqualsAndHashCode
    @ToString
    @NoArgsConstructor
    public static class GroupByOptionAndParams<T, G extends Enum<G> & GroupByOption<T>> implements Serializable {
        @NonNull
        @Getter
        private G groupByOption;
        @Getter
        @JsonIgnore
        private GroupByOption.Params params;

        @JsonIgnore
        public EntityAttribute<T> getAttribute() {
            return params == null ? groupByOption.getAttribute() : groupByOption.getAttribute(params);
        }

        @JsonProperty("params")
        public Map<GroupByOption.Param, Object> getParamMap() {
            return params == null ? null : params.getParamMap();
        }

        @JsonCreator
        public GroupByOptionAndParams(@JsonProperty("groupByOption") G groupByOption, @JsonProperty("params") Map<GroupByOption.Param, ?> params) {
            this.groupByOption = groupByOption;
            this.params = params == null ? null : GroupByOption.Params.of(params);
        }

        public GroupByOptionAndParams(G groupByOption, GroupByOption.Params params) {
            this.groupByOption = groupByOption;
            this.params = params;
        }

        public GroupByOptionAndParams<T, G> supplyContext(Supplier<Map<G, Object>> supplier) {
            return supplyContext(supplier.get());

        }

        public GroupByOptionAndParams<T, G> supplyContext(Map<G, Object> attributesContext) {
            final GroupByOption.Params.ParamsBuilder paramsBuilder = this.getParams() == null ? GroupByOption.Params.builder() : this.getParams().toBuilder();
            final Object attrContext = attributesContext.get(this.getGroupByOption());
            if (attrContext == null && GroupByOption.isAttributeContextRequired(this.getGroupByOption())) {
                throw new IllegalStateException(
                        String.format("Chart attribute %s requires context to be provided", this.getGroupByOption().name()));
            }
            paramsBuilder.with(GroupByOption.Param.CONTEXT, attrContext);
            return this.getGroupByOption().getGroupByOptionAndParams(paramsBuilder.build());
        }
    }

    @NonNull
    @Getter
    private Map<ChartGroupBySetting, GroupByOptionAndParams<T, G>> options;
    @Getter
    private Set<GroupByOptionAndParams<T, G>> trellisOptions;
    @JsonIgnore
    private Map<G, GroupByOptionAndParams<T, G>> trellisOptionsMap;

    public GroupByOptionAndParams<T, G> getTrellisOption(G option) {
        return trellisOptionsMap.get(option);
    }

    @JsonCreator
    public ChartGroupByOptions(@JsonProperty("options") Map<ChartGroupBySetting, GroupByOptionAndParams<T, G>> options,
                               @JsonProperty("trellisOptions") Set<GroupByOptionAndParams<T, G>> trellisOptions) {
        this.options = Collections.unmodifiableMap(new HashMap<>(options));
        this.trellisOptions = Collections.unmodifiableSet(new HashSet<>(trellisOptions));
        this.trellisOptionsMap = Collections.unmodifiableMap(trellisOptions.stream()
                .collect(Collectors.toMap(GroupByOptionAndParams::getGroupByOption, e -> e)));
    }

    @Override
    public ChartGroupByOptions<T, G> limitedBySettings(Set<ChartGroupBySetting> settings) {
        return new ChartGroupByOptions<>(this.options.entrySet().stream().filter(e -> settings.contains(e.getKey()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)), trellisOptions);
    }

    @Override
    public ChartGroupByOptions<Subject, PopulationGroupByOptions> limitedByPopulationTrellisOptions() {
        return new ChartGroupByOptions<>(new HashMap<>(), trellisOptions.stream()
                .filter(e -> GroupByOption.isPopulationOption(e.getGroupByOption()))
                .map(e -> new GroupByOptionAndParams<>(GroupByOption.getCorrespondingSubjectOption(e.getGroupByOption()), e.getParams()))
                .collect(Collectors.toSet()));
    }

    public ChartGroupBySettingsBuilder<T, G> toBuilder() {
        return new ChartGroupBySettingsBuilder<T, G>(this.getOptions(), this.getTrellisOptions());
    }

    public static <T, G extends Enum<G> & GroupByOption<T>> ChartGroupBySettingsBuilder<T, G> builder() {
        return new ChartGroupBySettingsBuilder<>();
    }

    /**
     * It will extend annotated with {@link AcceptsAttributeContext} annotation grouping options params with
     * {@link com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param#CONTEXT} param
     * using provided attributesContext object and return updated settings
     */
    public ChartGroupByOptions<T, G> supplyContext(Map<G, Object> attributesContext) {
        final ChartGroupBySettingsBuilder<T, G> builder = new ChartGroupBySettingsBuilder<>();
        //processing trellis options
        this.trellisOptions.forEach(optionAndParams -> {
            if (GroupByOption.acceptsAttributeContext(optionAndParams.getGroupByOption())) {
                final GroupByOptionAndParams<T, G> newOption = optionAndParams.supplyContext(attributesContext);
                builder.withTrellisOption(newOption);
            } else {
                builder.withTrellisOption(optionAndParams);
            }
        });
        //processing chart options
        this.options.forEach((key, optionAndParams) -> {
            if (GroupByOption.acceptsAttributeContext(optionAndParams.getGroupByOption())) {
                final GroupByOptionAndParams<T, G> newOption = optionAndParams.supplyContext(attributesContext);
                builder.withOption(key, newOption);
            } else {
                builder.withOption(key, optionAndParams);
            }

        });
        return builder.build();
    }

    @NoArgsConstructor
    public static class ChartGroupBySettingsBuilder<T, G extends Enum<G> & GroupByOption<T>> {
        private Map<ChartGroupBySetting, GroupByOptionAndParams<T, G>> options = new HashMap<>();
        private Set<GroupByOptionAndParams<T, G>> trellisOptions = new HashSet<>();

        public ChartGroupBySettingsBuilder(Map<ChartGroupBySetting, GroupByOptionAndParams<T, G>> options, Set<GroupByOptionAndParams<T, G>> trellisOptions) {
            this.options = new HashMap<>(options);
            this.trellisOptions = new HashSet<>(trellisOptions);
        }

        public ChartGroupBySettingsBuilder<T, G> withOption(ChartGroupByOptions.ChartGroupBySetting setting, GroupByOptionAndParams<T, G> option) {
            options.put(setting, option);
            return this;
        }

        public ChartGroupBySettingsBuilder<T, G> withTrellisOptions(Set<GroupByOptionAndParams<T, G>> trellisOptions) {
            this.trellisOptions.addAll(trellisOptions);
            return this;
        }

        public ChartGroupBySettingsBuilder<T, G> withTrellisOption(GroupByOptionAndParams<T, G> trellisOption) {
            this.trellisOptions.add(trellisOption);
            return this;
        }

        public ChartGroupByOptions<T, G> build() {
            return new ChartGroupByOptions<>(options, trellisOptions == null ? new HashSet<>() : trellisOptions);
        }
    }
}
