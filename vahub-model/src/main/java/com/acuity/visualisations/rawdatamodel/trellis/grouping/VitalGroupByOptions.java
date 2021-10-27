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

import com.acuity.visualisations.rawdatamodel.trellis.TrellisCategories;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.AcceptsAttributeContext;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.BinableOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.TimestampOption;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;

import java.util.Objects;

public enum VitalGroupByOptions implements GroupByOption<Vital> {

    ARM(Vital.Attributes.TREATMENT_ARM),
    START_DATE(Vital.Attributes.MEASUREMENT_DATE) {
        @Override
        public EntityAttribute<Vital> getAttribute(Params params) {
            return Attributes.getBinnedAttribute("MEASUREMENT_DATE", params, Vital::getStartDate);
        }
    },

    VISIT_NUMBER(Vital.Attributes.VISIT_NUMBER),
    @BinableOption
    @TimestampOption
    MEASUREMENT_TIME_POINT(Vital.Attributes.MEASUREMENT_DATE) {
        @Override
        public EntityAttribute<Vital> getAttribute(Params params) {
            return params == null ? getAttribute() : Attributes.getBinnedAttribute("MEASUREMENT_TIME_POINT", params,
                    vital -> vital.getSubject().getStudyInfo().isLimitXAxisToVisitNumber() ? null : vital.getEventDate());
        }
    },
    @AcceptsAttributeContext(required = false)
    MEASUREMENT(Vital.Attributes.MEASUREMENT_WITH_UNIT) {
        @Override
        public EntityAttribute<Vital> getAttribute(Params params) {
            final Object context = params == null ? null : params.get(Param.CONTEXT);
            final String unit = context == null ? null : Objects.toString(context);
            return unit == null
                    ? Vital.Attributes.MEASUREMENT_WITH_UNIT.getAttribute()
                    : EntityAttribute.attribute("MEASUREMENT",
                    (Vital v) -> v.getEvent().getVitalsMeasurement() + (v.getEvent().getUnit() == null ? "" : (" (" + unit + ")")));
        }
    },
    PERCENTAGE_CHANGE_FROM_BASELINE(Vital.Attributes.PERCENTAGE_CHANGE_FROM_BASELINE),
    ABSOLUTE_CHANGE_FROM_BASELINE(Vital.Attributes.CHANGE_FROM_BASELINE),
    ACTUAL_VALUE(Vital.Attributes.RESULT_VALUE);

    @Override
    public EntityAttribute<Vital> getAttribute() {
        return attribute.getAttribute();
    }

    public TrellisCategories category() {
        return TrellisCategories.NON_MANDATORY_SERIES;
    }

    private Vital.Attributes attribute;

    VitalGroupByOptions(Vital.Attributes attribute) {
        this.attribute = attribute;
    }
}
