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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;

import java.util.Objects;

public enum RenalGroupByOptions implements GroupByOption<Renal> {
    SUBJECT(Renal.Attributes.SUBJECT),
    VISIT_NUMBER(Renal.Attributes.VISIT_NUMBER),
    VISIT_DESCRIPTION(Renal.Attributes.VISIT_DESCRIPTION) {
        @Override
        public EntityAttribute<Renal> getAttribute() {
            return EntityAttribute.attribute("VISIT_DESCRIPTION", l -> new VisitDescription(l.getEvent().getVisitDescription()));
        }
    },
    STUDY_DEFINED_WEEK(Renal.Attributes.ANALYSIS_VISIT),
    @TimestampOption
    @BinableOption
    MEASUREMENT_TIME_POINT(Renal.Attributes.MEASUREMENT_TIME_POINT) {
        @Override
        public EntityAttribute<Renal> getAttribute(Params params) {
            return params == null ? getAttribute() : Attributes.getBinnedAttribute("MEASUREMENT_TIME_POINT", params,
                    Renal::getAccessibleMeasurementTimePoint);
        }
    },
    ARM(Renal.Attributes.ARM),
    @AcceptsAttributeContext(required = false)
    MEASUREMENT(Renal.Attributes.LAB_CODE_WITH_UNIT) {
        @Override
        public EntityAttribute<Renal> getAttribute(Params params) {
            final Object context = params == null ? null : params.get(Param.CONTEXT);
            final String unit = context == null ? null : Objects.toString(context);
            return unit == null
                    ? Renal.Attributes.LAB_CODE_WITH_UNIT.getAttribute()
                    : EntityAttribute.attribute("MEASUREMENT",
                    (Renal l) -> l.getLabcodeWithoutUnit() + (l.getEvent().getUnit() == null ? "" : (" (" + unit + ")")));
        }
    },
    REF_RANGE_NORM_VALUE(Renal.Attributes.REF_RANGE_NORM_VALUE),
    TIMES_UPPER_REF_VALUE(Renal.Attributes.TIMES_UPPER_REF),
    TIMES_LOWER_REF_VALUE(Renal.Attributes.TIMES_LOWER_REF),
    ABSOLUTE_CHANGE_FROM_BASELINE(Renal.Attributes.CHANGE_FROM_BASELINE),
    PERCENTAGE_CHANGE_FROM_BASELINE(Renal.Attributes.PERCENT_CHANGE_FROM_BASELINE),
    ACTUAL_VALUE(Renal.Attributes.LAB_VALUE),
    CKD_STAGE_NAME(Renal.Attributes.CKD_STAGE_NAME);

    private Renal.Attributes attribute;

    RenalGroupByOptions(Renal.Attributes attribute) {
        this.attribute = attribute;
    }


    @Override
    public EntityAttribute<Renal> getAttribute() {
        return attribute.getAttribute();
    }
}
