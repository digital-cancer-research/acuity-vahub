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

package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.AcceptsAttributeContext;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.DateFormattedOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.PopulationGroupingOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.RangeOption;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.Validate;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * This is a marker interface for all entities grouping(axis) options.
 * It also provides some default implementations for commonly used methods
 * and some util methods implementations
 * */
public interface GroupByOption<T> extends Serializable {

    enum Param implements Serializable {
        TIMESTAMP_TYPE,
        DRUG_NAME,
        BIN_SIZE,
        BIN_INCL_DURATION,
        ASSESSMENT_TYPE,
        WEEK_NUMBER,
        VALUE,

        AXIS_START,
        AXIS_END,
        CONTEXT
    }

    @RequiredArgsConstructor
    @Getter
    enum TimestampType implements Serializable {
        DATE(PeriodType.NONE, StartDateType.NONE),
        DAYS_SINCE_FIRST_DOSE(PeriodType.DAYS, StartDateType.FIRST_DOSE),
        DAYS_HOURS_SINCE_FIRST_DOSE(PeriodType.DAYS_HOURS, StartDateType.FIRST_DOSE),
        WEEKS_SINCE_FIRST_DOSE(PeriodType.WEEKS, StartDateType.FIRST_DOSE),
        DAYS_SINCE_RANDOMISATION(PeriodType.DAYS, StartDateType.RANDOMISATION),
        DAYS_HOURS_SINCE_RANDOMISATION(PeriodType.DAYS_HOURS, StartDateType.RANDOMISATION),
        WEEKS_SINCE_RANDOMISATION(PeriodType.WEEKS, StartDateType.RANDOMISATION),
        DAYS_SINCE_FIRST_DOSE_OF_DRUG(PeriodType.DAYS, StartDateType.FIRST_DOSE_OF_DRUG),
        DAYS_HOURS_SINCE_FIRST_DOSE_OF_DRUG(PeriodType.DAYS_HOURS, StartDateType.FIRST_DOSE_OF_DRUG),
        WEEKS_SINCE_FIRST_DOSE_OF_DRUG(PeriodType.WEEKS, StartDateType.FIRST_DOSE_OF_DRUG);

        private final PeriodType periodType;
        private final StartDateType startDateType;

        public static TimestampType truncateTimestampHours(TimestampType timestampType) {

            switch (timestampType) {
                case DAYS_HOURS_SINCE_RANDOMISATION:
                    return DAYS_SINCE_RANDOMISATION;
                case DAYS_HOURS_SINCE_FIRST_DOSE:
                    return DAYS_SINCE_FIRST_DOSE;
                case DAYS_HOURS_SINCE_FIRST_DOSE_OF_DRUG:
                    return DAYS_SINCE_FIRST_DOSE_OF_DRUG;
                default:
                    return timestampType;
            }
        }

        public enum PeriodType {
            DAYS, DAYS_HOURS, WEEKS, NONE
        }

        public enum StartDateType {
            RANDOMISATION, FIRST_DOSE, FIRST_DOSE_OF_DRUG, NONE
        }
    }


    @NoArgsConstructor
    class Params implements Serializable {

        public static final class ParamsBuilder {
            private Map<Param, Object> params;

            private ParamsBuilder() {
                params = new HashMap<>();
            }

            private ParamsBuilder(Params params) {
                this.params = new HashMap<>(params.paramMap);
            }

            public Params.ParamsBuilder with(Param param, Object value) {
                params.put(param, value);
                return this;
            }

            public Params build() {
                return new Params(Collections.unmodifiableMap(params));
            }

        }
        @Getter
        private Map<Param, Object> paramMap;

        @JsonCreator
        public Params(Map<Param, Object> paramMap) {
            this.paramMap = Collections.unmodifiableMap(new HashMap<>(paramMap));
        }

        public static Params of(Map<Param, ?> params) {
            final ParamsBuilder builder = Params.builder();
            params.forEach(builder::with);
            return builder.build();
        }

        public static ParamsBuilder builder() {
            return new ParamsBuilder();
        }

        public Object get(Param param) {
            return paramMap.get(param);
        }

        public Integer getInt(Param param) {
            final Object res = paramMap.get(param);
            if (res instanceof String) {
                return Integer.valueOf(((String) res));
            }
            if (!(res instanceof Integer || res == null)) {
                throw new IllegalStateException("Unexpected value object type for " + param.name());
            }
            return (Integer) res;
        }

        public String getStr(Param param) {
            final Object res = paramMap.get(param);
            return res == null ? null : res.toString();
        }

        @JsonIgnore
        public TimestampType getTimestampType() {
            final Object res = paramMap.get(Param.TIMESTAMP_TYPE);
            if (res == null) {
                return TimestampType.DATE;
            } else if (res instanceof TimestampType) {
                return (TimestampType) res;
            } else if (res instanceof String) {
                return TimestampType.valueOf((String) res);
            }
            throw new IllegalStateException("Unknown object type for TimestampType");
        }

        public Boolean getBool(Param param) {
            final Object res = paramMap.get(param);
            if (res == null) {
                //maybe null if for false by default? to be discussed
                return false;
            }
            if (res instanceof String) {
                return Boolean.valueOf(((String) res));
            }
            if (!(res instanceof Boolean)) {
                throw new IllegalStateException("Unexpected value object type for " + param.name());
            }
            return (Boolean) res;
        }

        public ParamsBuilder toBuilder() {
            return new ParamsBuilder(this);
        }
    }

    @SneakyThrows
    static <T, G extends Enum<G> & GroupByOption<T>> boolean isPopulationOption(G option) {
        return option != null && Attributes.isPopulationOption(option);
    }

    @SneakyThrows
    static <T, G extends Enum<G> & GroupByOption<T>> RangeOption getRangeOptionAnnotation(G option) {
        final Field field = option.getClass().getField(option.name());
        return field.isAnnotationPresent(RangeOption.class) ? field.getAnnotation(RangeOption.class) : null;
    }

    @SneakyThrows
    static <T, G extends Enum<G> & GroupByOption<T>> boolean acceptsAttributeContext(G option) {
        return option != null && option.getClass().getField(option.name()).getAnnotation(AcceptsAttributeContext.class) != null;
    }

    @SneakyThrows
    static <T, G extends Enum<G> & GroupByOption<T>> boolean isAttributeContextRequired(G option) {
        final AcceptsAttributeContext annotation = option.getClass().getField(option.name()).getAnnotation(AcceptsAttributeContext.class);
        return annotation != null && annotation.required();
    }

    Map<GroupByOption, PopulationGroupByOptions> CORRESPONDING_POP_OPTIONS = new ConcurrentHashMap<>();

    @SneakyThrows
    static <T, G extends Enum<G> & GroupByOption<T>> PopulationGroupByOptions getCorrespondingSubjectOption(G option) {
        return CORRESPONDING_POP_OPTIONS.computeIfAbsent(option, new Function<GroupByOption, PopulationGroupByOptions>() {
            @Override
            @SneakyThrows
            public PopulationGroupByOptions apply(GroupByOption o) {
                final PopulationGroupingOption annotation = option.getClass().getField(option.name()).getAnnotation(PopulationGroupingOption.class);
                Validate.notNull(annotation);
                return annotation.value();
            }
        });
    }

    @SneakyThrows
    static <T, G extends Enum<G> & GroupByOption<T>> DateFormattedOption getDateFormattedAnnotation(G option) {
        final Field field = option.getClass().getField(option.name());
        return field.isAnnotationPresent(DateFormattedOption.class) ? field.getAnnotation(DateFormattedOption.class) : null;
    }

    default <T, G extends Enum<G> & GroupByOption<T>> ChartGroupByOptions.GroupByOptionAndParams<T, G> getGroupByOptionAndParams() {
        return getGroupByOptionAndParams(null);
    }

    @SuppressWarnings("unchecked")
    default <T, G extends Enum<G> & GroupByOption<T>> ChartGroupByOptions.GroupByOptionAndParams<T, G> getGroupByOptionAndParams(Params params) {
        return new ChartGroupByOptions.GroupByOptionAndParams<>((G) this, params);
    }

    default EntityAttribute<T> getAttribute(Params params) {
        //by default args not used
        return getAttribute();
    }

    EntityAttribute<T> getAttribute();
}
