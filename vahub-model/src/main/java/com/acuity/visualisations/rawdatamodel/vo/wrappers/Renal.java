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
import com.acuity.visualisations.rawdatamodel.vo.RenalRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;

@ToString(callSuper = true)
public class Renal extends SubjectAwareWrapper<RenalRaw>
        implements HasValueAndBaseline, HasReferenceRange, HasDaysSinceFirstDose {

    public Renal(RenalRaw event, Subject subject) {
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

    @Column(columnName = "measurementTimePoint",
            order = 2,
            displayName = "Measurement time point",
            defaultSortBy = true,
            defaultSortOrder = 1)
    public Date getAccessibleMeasurementTimePoint() {
        return getEvent().getMeasurementTimePoint();

    }

    @Column(columnName = "daysOnStudy", order = 3, displayName = "Days on study")
    public Integer getDaysOnStudy() {
        return getDaysSinceFirstDose();
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
    @Column(displayName = "Upper ref range value", columnName = "upperRefRangeValue", order = 8, datasetType = Column.DatasetType.ACUITY)
    public Double getRefHigh() {
        return getEvent().getRefHigh();
    }

    @Override
    @Column(displayName = "Times upper ref value", columnName = "timesUpperRef", order = 7, datasetType = Column.DatasetType.ACUITY)
    public Double getTimesUpperReferenceRange() {
        return getEvent().getTimesUpperReferenceRange();
    }

    @Override
    public Double getResultValue() {
        return getEvent().getResultValue();
    }

    @Override
    public Double getBaselineValue() {
        return getEvent().getBaselineValue();
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
    public Double getChangeFromBaselineWithoutPrecision() {
        return getEvent().getChangeFromBaselineWithoutPrecision();
    }

    public enum Attributes implements GroupByOption<Renal> {
        ID(EntityAttribute.attribute("id", Renal::getId)),
        STUDY_ID(EntityAttribute.attribute("studyId", Renal::getStudyId)),
        STUDY_PART(EntityAttribute.attribute("studyPart", Renal::getStudyPart)),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", Renal::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", Renal::getSubjectCode)),
        CKD_STAGE_NAME(EntityAttribute.attribute("ckdStageName", (Renal r) -> r.getEvent().getCkdStageName())),
        LAB_CODE(EntityAttribute.attribute("labcode", l -> l.getEvent().getLabCode())),
        LAB_UNIT(EntityAttribute.attribute("unit", l -> l.getEvent().getUnit())),
        MEASUREMENT_TIME_POINT(EntityAttribute.attribute("measurementTimePoint", Renal::getAccessibleMeasurementTimePoint)),
        DAYS_ON_STUDY(EntityAttribute.attribute("daysOnStudy", Renal::getDaysSinceFirstDose)),
        ANALYSIS_VISIT(EntityAttribute.attribute("analysisVisit", l -> l.getEvent().getAnalysisVisit())),
        VISIT_NUMBER(EntityAttribute.attribute("visitNumber", l -> VisitNumber.fromValue(l.getEvent().getVisitNumber()))),
        VISIT_DESCRIPTION(EntityAttribute.attribute("visitNumber", l -> l.getEvent().getVisitDescription())),
        LAB_VALUE(EntityAttribute.attribute("value", l -> l.getEvent().getValue())),
        BASELINE_FLAG(EntityAttribute.attribute("baselineFlag", l -> l.getEvent().getBaselineFlag())),
        BASELINE_VALUE(EntityAttribute.attribute("baseline", l -> l.getEvent().getBaselineValue())),
        CHANGE_FROM_BASELINE(EntityAttribute.attribute("changeFromBaseline", Renal::getChangeFromBaseline)),
        PERCENT_CHANGE_FROM_BASELINE(EntityAttribute.attribute("percentChangeFromBaseline", Renal::getPercentChangeFromBaseline)),
        REF_RANGE_NORM_VALUE(EntityAttribute.attribute("refRangeNormValue", Renal::getReferenceRangeNormalisedValue)),
        TIMES_UPPER_REF(EntityAttribute.attribute("timesUpperRef", Renal::getTimesUpperReferenceRange)),
        TIMES_LOWER_REF(EntityAttribute.attribute("timesLowerRef", Renal::getTimesLowerReferenceRange)),
        UPPER_REF_RANGE(EntityAttribute.attribute("upperRefRange", l -> l.getEvent().getRefHigh())),
        LOWER_REF_RANGE(EntityAttribute.attribute("lowerRefRange", l -> l.getEvent().getRefLow())),
        STUDY_PERIOD(EntityAttribute.attribute("studyPeriods", l -> l.getEvent().getStudyPeriods())),
        LAB_CODE_WITH_UNIT(EntityAttribute.attribute("labcodeWithUnit", Renal::getLabCodeWithUnit)),
        ARM(EntityAttribute.attribute("arm", l -> l.getSubject().getActualArm()));

        @Getter
        private final EntityAttribute<Renal> attribute;

        Attributes(EntityAttribute<Renal> attribute) {
            this.attribute = attribute;
        }
    }

    private String getLabCodeWithUnit() {
        String unit = getEvent().getUnit();
        String labCode = getEvent().getLabCode();
        if (unit == null) {
            return labCode;
        } else {
            unit = " (" + unit + ")";
            return labCode.endsWith(unit) ? labCode : labCode + unit;
        }
    }

    public String getLabcodeWithoutUnit() {
        String unit = getEvent().getUnit();
        String labCode = getEvent().getLabCode();
        if (unit == null) {
            return labCode;
        } else {
            unit = " (" + unit + ")";
            return labCode.endsWith(unit) ? labCode.substring(0, labCode.length() - unit.length()) : labCode;
        }
    }
}
