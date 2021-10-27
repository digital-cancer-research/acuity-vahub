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

package com.acuity.visualisations.rawdatamodel.vo.wrappers;

import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasDaysSinceFirstDose;
import com.acuity.visualisations.rawdatamodel.vo.HasReferenceRange;
import com.acuity.visualisations.rawdatamodel.vo.HasValueAndBaseline;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class Lab extends SubjectAwareWrapper<LabRaw>
        implements HasValueAndBaseline, HasReferenceRange, HasDaysSinceFirstDose, Serializable {

    public Lab(LabRaw event, Subject subject) {
        super(event, subject);
    }

    @Override
    public Double getResultValue() {
        return getEvent().getValue();
    }

    @Override
    public Double getRefLow() {
        return getEvent().getRefLow();
    }

    @Override
    public Double getRefHigh() {
        return getEvent().getRefHigh();
    }

    @Override
    public Double getBaselineValue() {
        return getEvent().getBaseline();
    }

    @Override
    public Double getChangeFromBaselineRaw() {
        return getEvent().getChangeFromBaselineRaw();
    }

    @Override
    public Boolean getCalcChangeFromBaselineIfNull() {
        return getEvent().getCalcChangeFromBaselineIfNull();
    }

    @Override
    public Date getFirstTreatmentDate() {
        return getSubject().getFirstTreatmentDate();
    }

    @Override
    public Date getMeasurementTimePoint() {
        return getEvent().getMeasurementTimePoint();
    }

    @Override
    public Integer getDaysSinceFirstDoseRaw() {
        return getEvent().getDaysSinceFirstDose();
    }

    @Override
    public Boolean isCalcDaysSinceFirstDoseIfNull() {
        return getEvent().getCalcDaysSinceFirstDoseIfNull();
    }

    @Column(columnName = "daysOnStudy", order = 4, displayName = "Days on study")
    public Integer getDaysOnStudy() {
        return getDaysSinceFirstDose();
    }


    /* Here goes a block of methods having calcs that need to be cached (according to CPU profiling) */

    @Override
    public String getOutOfRefRange() {
        return getEvent().getOutOfRefRange();
    }

    @Override
    @Column(columnName = "refRangeNormValue", order = 13, displayName = "Ref range norm value")
    public Double getReferenceRangeNormalisedValue() {
        return getEvent().getReferenceRangeNormalisedValue();
    }

    @Override
    @Column(columnName = "timesUpperRefValue", order = 14, displayName = "Times upper ref value")
    public Double getTimesUpperReferenceRange() {
        return getEvent().getTimesUpperReferenceRange();
    }

    @Override
    @Column(columnName = "timesLowerRefValue", order = 15, displayName = "Times lower ref value")
    public Double getTimesLowerReferenceRange() {
        return getEvent().getTimesLowerReferenceRange();
    }

    @Override
    @Column(columnName = "percentChangeFromBaseline", order = 11, displayName = "Percent change from baseline")
    public Double getPercentChangeFromBaseline() {
        return getEvent().getPercentChangeFromBaseline();
    }

    @Override
    @Column(columnName = "changeFromBaseline", order = 10, displayName = "Change from baseline")
    public Double getChangeFromBaseline() {
        return getEvent().getChangeFromBaseline();
    }

    @Override
    public Double getChangeFromBaselineWithoutPrecision() {
        return getEvent().getChangeFromBaselineWithoutPrecision();
    }

    public enum Attributes implements GroupByOption<Lab> {

        ID(EntityAttribute.attribute("id", Lab::getId)),
        STUDY_ID(EntityAttribute.attribute("studyId", Lab::getStudyId)),
        STUDY_PART(EntityAttribute.attribute("studyPart", Lab::getStudyPart)),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", Lab::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", Lab::getSubjectCode)),
        ARM(EntityAttribute.attribute("arm", (Lab l) -> l.getSubject().getActualArm())),

        LAB_CODE(EntityAttribute.attribute("labcode", (Lab l) -> l.getEvent().getLabCode())),
        LAB_CATEGORY(EntityAttribute.attribute("category", (Lab l) -> l.getEvent().getCategory())),
        LAB_VALUE(EntityAttribute.attribute("value", (Lab l) -> l.getEvent().getValue())),
        LAB_UNIT(EntityAttribute.attribute("unit", (Lab l) -> l.getEvent().getUnit())),
        LAB_CODE_WITH_UNIT(EntityAttribute.attribute("labcodeWithUnit", (Lab l) -> l.getEvent().getLabCode()
                + (l.getEvent().getUnit() == null ? "" : (" (" + l.getEvent().getUnit() + ")")))),
        BASELINE_FLAG(EntityAttribute.attribute("baselineFlag", (Lab l) -> l.getEvent().getBaselineFlag())),
        BASELINE_VALUE(EntityAttribute.attribute("baseline", (Lab l) -> l.getEvent().getBaseline())),
        CHANGE_FROM_BASELINE(EntityAttribute.attribute("changeFromBaseline", Lab::getChangeFromBaseline)),
        PERCENT_CHANGE_FROM_BASELINE(EntityAttribute.attribute("percentChangeFromBaseline", Lab::getPercentChangeFromBaseline)),
        UPPER_REF_RANGE(EntityAttribute.attribute("upperRefRange", (Lab l) -> l.getEvent().getRefHigh())),
        LOWER_REF_RANGE(EntityAttribute.attribute("lowerRefRange", (Lab l) -> l.getEvent().getRefLow())),
        OUT_OF_REF_RANGE(EntityAttribute.attribute("outOfRefRange", Lab::getOutOfRefRange)),
        REF_RANGE_NORM_VALUE(EntityAttribute.attribute("refRangeNormValue", Lab::getReferenceRangeNormalisedValue)),
        TIMES_UPPER_REF(EntityAttribute.attribute("timesUpperRef", Lab::getTimesUpperReferenceRange)),
        TIMES_LOWER_REF(EntityAttribute.attribute("timesLowerRef", Lab::getTimesLowerReferenceRange)),

        MEASUREMENT_TIME_POINT(EntityAttribute.attribute("measurementTimePoint", Lab::getMeasurementTimePoint)),
        DAYS_ON_STUDY(EntityAttribute.attribute("daysOnStudy", Lab::getDaysSinceFirstDose)),
        VISIT_NUMBER(EntityAttribute.attribute("visitNumber", (Lab l) -> VisitNumber.fromValue(l.getEvent().getVisitNumber()))),
        VISIT_DESCRIPTION(EntityAttribute.attribute("visitDescription", (Lab l) -> l.getEvent().getVisitDescription())),
        ANALYSIS_VISIT(EntityAttribute.attribute("analysisVisit", (Lab l) -> l.getEvent().getAnalysisVisit())),

        COMMENT(EntityAttribute.attribute("comment", (Lab l) -> l.getEvent().getComment())),
        PROTOCOL_SCHEDULE_TIMEPOINT(EntityAttribute.attribute("protocolScheduleTimepoint", (Lab l) -> l.getEvent().getProtocolScheduleTimepoint())),
        VALUE_DIPSTICK(EntityAttribute.attribute("valueDipstick", (Lab l) -> l.getEvent().getValueDipstick())),
        STUDY_PERIOD(EntityAttribute.attribute("studyPeriods", (Lab l) -> l.getEvent().getStudyPeriods())),

        USED_IN_TFL(EntityAttribute.attribute("usedInTfl", (Lab e) -> e.getEvent().getUsedInTfl())),
        SOURCE_TYPE(EntityAttribute.attribute("sourceType", (Lab e) -> e.getEvent().getSourceType())),
        ARM_AND_SOURCE_TYPE(EntityAttribute.attribute("armAndSourceType",
                (Lab e) -> new ArmAndSourceType(e.getEvent().getSourceType(), e.getSubject().getActualArm())));

        @Getter
        private final EntityAttribute<Lab> attribute;

        Attributes(EntityAttribute<Lab> attribute) {
            this.attribute = attribute;
        }
    }

    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class ArmAndSourceType implements Serializable, Comparable<ArmAndSourceType> {
        private String sourceType;
        private String actualArm;

        @Getter(lazy = true)
        private final String asString = actualArm == null ? sourceType : String.format("%s, %s", actualArm, sourceType);

        @Override
        public String toString() {
            return getAsString();
        }

        @Override
        public int compareTo(ArmAndSourceType o) {
            return Comparator.comparing(ArmAndSourceType::getSourceType)
                    .thenComparing(ArmAndSourceType::getActualArm).compare(this, o);
        }
    }
}
