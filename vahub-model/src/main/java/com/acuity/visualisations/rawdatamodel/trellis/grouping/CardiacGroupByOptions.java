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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.TimestampOption;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;

import java.util.Objects;

public enum CardiacGroupByOptions implements GroupByOption<Cardiac> {

    ARM(Cardiac.Attributes.ARM),

    VISIT_NUMBER(Cardiac.Attributes.VISIT_NUMBER),
    VISIT_DESCRIPTION(Cardiac.Attributes.VISIT_DESCRIPTION) {
        @Override
        public EntityAttribute<Cardiac> getAttribute() {
            return EntityAttribute.attribute("VISIT_DESCRIPTION", e -> new VisitDescription(e.getEvent().getVisitDescription()));
        }
    },
    STUDY_DEFINED_WEEK(Cardiac.Attributes.ANALYSIS_VISIT),

    @AcceptsAttributeContext(required = false)
    MEASUREMENT(Cardiac.Attributes.MEASUREMENT_WITH_UNIT) {
        @Override
        public EntityAttribute<Cardiac> getAttribute(Params params) {
            final Object context = params == null ? null : params.get(Param.CONTEXT);
            final String unit = context == null ? null : Objects.toString(context);
            return unit == null
                    ? Cardiac.Attributes.MEASUREMENT_WITH_UNIT.getAttribute()
                    : EntityAttribute.attribute("MEASUREMENT",
                    (Cardiac v) -> v.getEvent().getMeasurementName() + (v.getEvent().getResultUnit() == null ? "" : (" (" + unit + ")")));
        }
    },
    @BinableOption
    @TimestampOption
    MEASUREMENT_TIME_POINT(Cardiac.Attributes.MEASUREMENT_TIME_POINT) {
        @Override
        public EntityAttribute<Cardiac> getAttribute(Params params) {
            return params == null ? getAttribute() : Attributes.getBinnedAttribute("MEASUREMENT_TIME_POINT", params,
                    Cardiac::getAccessibleMeasurementTimePoint);
        }
    },
    PERCENTAGE_CHANGE_FROM_BASELINE(Cardiac.Attributes.PERCENT_CHANGE_FROM_BASELINE),
    ABSOLUTE_CHANGE_FROM_BASELINE(Cardiac.Attributes.CHANGE_FROM_BASELINE),
    ACTUAL_VALUE(Cardiac.Attributes.RESULT_VALUE);;

    @Override
    public EntityAttribute<Cardiac> getAttribute() {
        return attribute.getAttribute();
    }

    private Cardiac.Attributes attribute;

    CardiacGroupByOptions(Cardiac.Attributes attribute) {
        this.attribute = attribute;
    }
}
