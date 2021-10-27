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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Comparator;
import java.util.Objects;

public enum LabGroupByOptions implements GroupByOption<Lab> {

    @AcceptsAttributeContext(required = false)
    MEASUREMENT(Lab.Attributes.LAB_CODE_WITH_UNIT) {
        @Override
        public EntityAttribute<Lab> getAttribute(Params params) {
            final Object context = params == null ? null : params.get(Param.CONTEXT);
            final String unit = context == null ? null : Objects.toString(context);
            return unit == null
                    ? Lab.Attributes.LAB_CODE_WITH_UNIT.getAttribute()
                    : EntityAttribute.attribute("MEASUREMENT", (Lab l) -> l.getEvent().getLabCode()
                    + (l.getEvent().getUnit() == null ? "" : (" (" + unit + ")")));
        }
    },
    UNIT(Lab.Attributes.LAB_UNIT),
    BASELINE_VALUE(Lab.Attributes.BASELINE_VALUE),
    ARM(Lab.Attributes.ARM),
    VISIT_NUMBER(Lab.Attributes.VISIT_NUMBER),
    VISIT_DESCRIPTION(Lab.Attributes.VISIT_DESCRIPTION) {
        @Override
        public EntityAttribute<Lab> getAttribute() {
            return EntityAttribute.attribute("VISIT_DESCRIPTION", l -> new VisitDescription(l.getEvent().getVisitDescription(), l.getEvent().getVisitNumber()));
        }
    },
    STUDY_DEFINED_WEEK(Lab.Attributes.ANALYSIS_VISIT),
    @TimestampOption
    @BinableOption
    MEASUREMENT_TIME_POINT(Lab.Attributes.MEASUREMENT_TIME_POINT) {
        @Override
        public EntityAttribute<Lab> getAttribute(Params params) {
            return params == null ? getAttribute() : Attributes.getBinnedAttribute("MEASUREMENT_TIME_POINT", params, Lab::getMeasurementTimePoint);
        }
    },
    REF_RANGE_NORM_VALUE(Lab.Attributes.REF_RANGE_NORM_VALUE),
    TIMES_UPPER_REF_VALUE(Lab.Attributes.TIMES_UPPER_REF),
    TIMES_LOWER_REF_VALUE(Lab.Attributes.TIMES_LOWER_REF),
    ABSOLUTE_CHANGE_FROM_BASELINE(Lab.Attributes.CHANGE_FROM_BASELINE),
    PERCENTAGE_CHANGE_FROM_BASELINE(Lab.Attributes.PERCENT_CHANGE_FROM_BASELINE),
    ACTUAL_VALUE(Lab.Attributes.LAB_VALUE),
    SOURCE_TYPE(Lab.Attributes.SOURCE_TYPE),
    ARM_AND_SOURCE_TYPE(Lab.Attributes.ARM_AND_SOURCE_TYPE);

    private Lab.Attributes originAttribute;

    LabGroupByOptions(Lab.Attributes attribute) {
        this.originAttribute = attribute;
    }

    @Override
    public EntityAttribute<Lab> getAttribute() {
        return originAttribute.getAttribute();
    }

    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class VisitDescription implements Comparable<VisitDescription> {
        private String visitDescription;
        private Double visitNumber;

        @Override
        public int compareTo(VisitDescription o) {
            return Objects.compare(this.getVisitNumber(), o.getVisitNumber(), Comparator.naturalOrder());
        }

        @Override
        public String toString() {
            return visitDescription == null ? Attributes.DEFAULT_EMPTY_VALUE : visitDescription;
        }
    }

}
