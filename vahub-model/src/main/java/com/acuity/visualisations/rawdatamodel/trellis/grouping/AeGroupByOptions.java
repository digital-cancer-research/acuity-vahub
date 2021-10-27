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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.BinableOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.PopulationGroupingOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.TimestampOption;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverity;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;

import java.util.Map;

public enum AeGroupByOptions implements GroupByOption<Ae> {

    NONE(null) {
        @Override
        public EntityAttribute<Ae> getAttribute() {
            return EntityAttribute.attribute("ALL", e -> "All");
        }
    },
    SUBJECT(Ae.Attributes.SUBJECT),
    SUBJECT_ID(Ae.Attributes.SUBJECT_ID),
    @PopulationGroupingOption(PopulationGroupByOptions.SEX)
    SEX(Ae.Attributes.SEX),
    PT(Ae.Attributes.PT),
    HLT(Ae.Attributes.HLT),
    SOC(Ae.Attributes.SOC),
    SPECIAL_INTEREST_GROUP(Ae.Attributes.SPECIAL_INTEREST_GROUP),
    CUSTOM(Ae.Attributes.SPECIAL_INTEREST_GROUP),
    @AcceptsAttributeContext(required = false)
    @SuppressWarnings("unchecked")
    MAX_SEVERITY_GRADE(Ae.Attributes.SEVERITY) {
        @Override
        public EntityAttribute<Ae> getAttribute() {
            return EntityAttribute.attribute(MAX_SEVERITY_GRADE_ATTRIBUTE_NAME, (Ae ae) -> {
                final AeSeverity maxSeverity = ae.getEvent().getMaxSeverity();
                return Attributes.defaultNullableValue(maxSeverity == null ? null : maxSeverity.getWebappSeverity(), Attributes.DEFAULT_EMPTY_VALUE);
            });
        }

        @Override
        public EntityAttribute<Ae> getAttribute(Params params) {
            //we use  params.get(Param.CONTEXT) to get max grades within subject AEs, when counting subject counts bar chart
            //if not set, just using severity assigned to AE
            final Object context = params == null ? null : params.get(Param.CONTEXT);
            return context == null ? EntityAttribute.attribute(MAX_SEVERITY_GRADE_ATTRIBUTE_NAME, (Ae ae) -> {
                final AeSeverity maxSeverity = ae.getEvent().getMaxSeverity();
                return Attributes.defaultNullableValue(maxSeverity == null ? null : maxSeverity.getWebappSeverity(), Attributes.DEFAULT_EMPTY_VALUE);
            }) : EntityAttribute.attribute(
                    MAX_SEVERITY_GRADE_ATTRIBUTE_NAME,
                    (Ae ae) -> Attributes.defaultNullableValue(((Map) context).get(ae.getId()), Attributes.DEFAULT_EMPTY_VALUE));
        }
    },
    @PopulationGroupingOption(PopulationGroupByOptions.ACTUAL_TREATMENT_ARM)
    ARM(Ae.Attributes.TREATMENT_ARM),
    @TimestampOption
    @BinableOption
    START_DATE(Ae.Attributes.START_DATE) {
        @Override
        public EntityAttribute<Ae> getAttribute(Params params) {
            return Attributes.getBinnedAttribute("START_DATE", params,
                    Ae.Attributes.START_DATE.getAttribute().getFunction());
        }
    },
    @TimestampOption(hasDuration = true)
    @BinableOption
    OVERTIME_DURATION(Ae.Attributes.START_DATE) {
        @Override
        public EntityAttribute<Ae> getAttribute(Params params) {
            return params == null ? getAttribute()
                    : Attributes.getBinnedAttribute("OVERTIME_DURATION", params,
                    Ae::getStartDate, Ae::getEndDate);
        }
    };

    private Ae.Attributes attribute;

    private static final String MAX_SEVERITY_GRADE_ATTRIBUTE_NAME = "MAX_SEVERITY_GRADE";

    AeGroupByOptions(Ae.Attributes attribute) {
        this.attribute = attribute;
    }

    @Override
    public EntityAttribute<Ae> getAttribute() {
        return attribute.getAttribute();
    }
}
