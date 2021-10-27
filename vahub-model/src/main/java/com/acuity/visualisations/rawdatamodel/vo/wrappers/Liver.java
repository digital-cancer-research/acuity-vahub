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
import com.acuity.visualisations.rawdatamodel.vo.LiverRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString(callSuper = true)
public class Liver extends SubjectAwareWrapper<LiverRaw>
        implements HasValueAndBaseline, HasReferenceRange, HasDaysSinceFirstDose, Serializable {

    public Liver(LiverRaw event, Subject subject) {
        super(event, subject);
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

    @Override
    public Double getRefLow() {
        return getEvent().getRefLow();
    }

    @Override
    public Double getRefHigh() {
        return getEvent().getRefHigh();
    }

    @Override
    public Double getResultValue() {
        return getEvent().getValue();
    }

    @Override
    public Double getBaselineValue() {
        return getEvent().getBaselineValue();
    }

    @Override
    @Column(columnName = "refRangeNormValue", order = 11, displayName = "Ref range norm value")
    public Double getReferenceRangeNormalisedValue() {
        return getEvent().getReferenceRangeNormalisedValue();
    }

    @Override
    @Column(columnName = "timesUpperRefValue", order = 12, displayName = "Times upper ref value")
    public Double getTimesUpperReferenceRange() {
        return getEvent().getTimesUpperReferenceRange();
    }

    @Override
    @Column(columnName = "timesLowerRefValue", order = 13, displayName = "Times lower ref value")
    public Double getTimesLowerReferenceRange() {
        return getEvent().getTimesLowerReferenceRange();
    }

    @Override
    @Column(columnName = "changeFromBaseline", order = 8, displayName = "Change from baseline")
    public Double getChangeFromBaselineRaw() {
        return getEvent().getChangeFromBaseline();
    }

    @Override
    @Column(columnName = "percentChangeFromBaseline", order = 9, displayName = "Percent change from baseline")
    public Double getPercentChangeFromBaseline() {
        return getEvent().getPercentChangeFromBaseline();
    }

    @Override
    public Boolean getCalcChangeFromBaselineIfNull() {
        return getEvent().getCalcChangeFromBaselineIfNull();
    }

    @Column(columnName = "measurementTimePoint", order = 2, displayName = "Measurement time point", defaultSortBy = true)
    public Date getAccessibleMeasurementTimePoint() {
        return getEvent().getMeasurementTimePoint();

    }

    @Column(columnName = "daysOnStudy", order = 3, displayName = "Days on study")
    public Integer getDaysOnStudy() {
        return getDaysSinceFirstDose();
    }

    public Double getNormalizedValue() {
        return getEvent().getNormalizedValue();
    }

    public enum LiverCode {
        ALT, AST, BILI, ALP
    }


    public enum Attributes implements GroupByOption<Liver> {
        ID(EntityAttribute.attribute("id", Liver::getId)),
        STUDY_ID(EntityAttribute.attribute("studyId", Liver::getStudyId)),
        STUDY_PART(EntityAttribute.attribute("studyPart", Liver::getStudyPart)),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", Liver::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", Liver::getSubjectCode)),
        ARM(EntityAttribute.attribute("arm", (Liver l) -> l.getSubject().getActualArm())),

        LAB_CODE(EntityAttribute.attribute("labcode", (Liver l) -> l.getEvent().getLabCode())),
        NORMALIZED_LAB_CODE(EntityAttribute.attribute("normalizedLabCode", (Liver l) -> l.getEvent().getNormalizedLabCode())),
        LAB_UNIT(EntityAttribute.attribute("unit", (Liver l) -> l.getEvent().getUnit())),
        MEASUREMENT_TIME_POINT(EntityAttribute.attribute("measurementTimePoint", Liver::getAccessibleMeasurementTimePoint)),
        DAYS_ON_STUDY(EntityAttribute.attribute("daysOnStudy", Liver::getDaysSinceFirstDose)),
        VISIT_NUMBER(EntityAttribute.attribute("visitNumber", (Liver l) -> l.getEvent().getVisitNumber())),
        LAB_VALUE(EntityAttribute.attribute("value", (Liver l) -> l.getEvent().getValue())),
        BASELINE_FLAG(EntityAttribute.attribute("baselineFlag", (Liver l) -> l.getEvent().getBaselineFlag())),
        BASELINE_VALUE(EntityAttribute.attribute("baseline", (Liver l) -> l.getEvent().getBaseline())),
        CHANGE_FROM_BASELINE(EntityAttribute.attribute("changeFromBaseline", Liver::getChangeFromBaseline)),
        PERCENT_CHANGE_FROM_BASELINE(EntityAttribute.attribute("percentChangeFromBaseline", Liver::getPercentChangeFromBaseline)),
        REF_RANGE_NORM_VALUE(EntityAttribute.attribute("refRangeNormValue", Liver::getReferenceRangeNormalisedValue)),
        TIMES_UPPER_REF(EntityAttribute.attribute("timesUpperRef", Liver::getTimesUpperReferenceRange)),
        TIMES_LOWER_REF(EntityAttribute.attribute("timesLowerRef", Liver::getTimesLowerReferenceRange)),
        UPPER_REF_RANGE(EntityAttribute.attribute("upperRefRange", (Liver l) -> l.getEvent().getRefHigh())),
        LOWER_REF_RANGE(EntityAttribute.attribute("lowerRefRange", (Liver l) -> l.getEvent().getRefLow()));

        @Getter
        private final EntityAttribute<Liver> attribute;

        Attributes(EntityAttribute<Liver> attribute) {
            this.attribute = attribute;
        }
    }
}
