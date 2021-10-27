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

import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.AcceptsAttributeContext;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.pkresult.CycleDay;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;

import java.util.HashMap;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.defaultNullableValue;

public enum PkResultGroupByOptions implements GroupByOption<PkResult> {

    SUBJECT(PkResult.Attributes.SUBJECT),
    SUBJECT_ID(PkResult.Attributes.SUBJECT_ID),
    PARAMETER(PkResult.Attributes.PARAMETER),
    PARAMETER_VALUE(PkResult.Attributes.PARAMETER_VALUE),
    PARAMETER_UNIT(PkResult.Attributes.PARAMETER_UNIT),
    MEASUREMENT(PkResult.Attributes.PARAMETER_WITH_UNIT),
    DOSE(PkResult.Attributes.DOSE),
    ANALYTE(PkResult.Attributes.ANALYTE),
    VISIT(PkResult.Attributes.VISIT),
    DAY(null) {
        @Override
        public EntityAttribute<PkResult> getAttribute() {
            return EntityAttribute.attribute("DAY",
                    e -> defaultNullableValue(e.getEvent().getProtocolScheduleStartDay()));
        }
    },
    CYCLE(PkResult.Attributes.TREATMENT_CYCLE),
    VISIT_NUMBER(PkResult.Attributes.VISIT_NUMBER),
    ACTUAL_DOSE(PkResult.Attributes.ACTUAL_DOSE),
    CYCLE_DAY(null) {
        @Override
        public EntityAttribute<PkResult> getAttribute() {
            return EntityAttribute.attribute("CYCLE_DAY",
                    e -> new CycleDay(e.getEvent().getTreatmentCycle(),
                            e.getEvent().getProtocolScheduleStartDay()));
        }
    },
    MEASUREMENT_TIMEPOINT(null) {
        @Override
        public EntityAttribute<PkResult> getAttribute() {
            Params params = Params.builder()
                    .with(Param.TIMESTAMP_TYPE, CYCLE_DAY)
                    .build();
            return getAttribute(params);
        }

        @Override
        public EntityAttribute<PkResult> getAttribute(Params params) {
            if (params == null || params.getParamMap().isEmpty()) {
                return getAttribute();
            }
            return PkResultGroupByOptions.valueOf(String.valueOf(params.get(Param.TIMESTAMP_TYPE))).getAttribute();
        }
    },
    /**
     * This attribute should be supplied with {@code java.util.Map<String, Map<Integer, String>>}
     * object in {@code com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param.CONTEXT} parameter.
     * This map contains subject ids as keys and maps of assessment week to assessment response on that week as values
     */
    @AcceptsAttributeContext(required = false)
    OVERALL_RESPONSE(PkResult.Attributes.BEST_OVERALL_RESPONSE) {
        @Override
        @SuppressWarnings("unchecked")
        public EntityAttribute<PkResult> getAttribute(Params params) {
            if (params == null || !params.getParamMap().containsKey(Param.CONTEXT)) {
                return getAttribute();
            }
            int weekNumber = (int) params.getParamMap().getOrDefault(GroupByOption.Param.WEEK_NUMBER, 0);
            final Object o = params.get(Param.CONTEXT);
            Map<String, Map<Integer, String>> weekResponsePerSubject = (Map<String, Map<Integer, String>>) o;
            return EntityAttribute.attribute("WEEK_RESPONSE",
                    (PkResult b) -> weekResponsePerSubject.getOrDefault(b.getSubjectId(), new HashMap<>())
                            .get(weekNumber));
        }
    };

    private PkResult.Attributes attribute;

    PkResultGroupByOptions(PkResult.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<PkResult> getAttribute() {
        return attribute.getAttribute();
    }
}

