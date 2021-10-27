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
import com.acuity.visualisations.rawdatamodel.vo.HasBaselineDate;
import com.acuity.visualisations.rawdatamodel.vo.HasDaysSinceFirstDose;
import com.acuity.visualisations.rawdatamodel.vo.HasStartDate;
import com.acuity.visualisations.rawdatamodel.vo.HasValueAndBaseline;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.VitalRaw;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

import static com.acuity.visualisations.rawdatamodel.util.Column.Type.DOD;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class Vital extends SubjectAwareWrapper<VitalRaw> implements HasDaysSinceFirstDose, HasValueAndBaseline,
        HasBaselineDate, HasStartDate, Serializable {

    public Vital(VitalRaw event, Subject subject) {
        super(event, subject);
    }

    @Column(columnName = "startDate", order = 6, displayName = "Start date")
    @Override
    public Date getStartDate() {
        return getEvent().getMeasurementDate();
    }

    @Override
    public Double getResultValue() {
        return getEvent().getResultValue();
    }

    @Override
    public Double getBaselineValue() {
        return getEvent().getBaseline();
    }

    @Override
    public Boolean getCalcChangeFromBaselineIfNull() {
        return getEvent().getCalcChangeFromBaselineIfNull();
    }

    @Override
    public Double getChangeFromBaselineRaw() {
        return getEvent().getChangeFromBaseline();
    }

    @Override
    public Boolean isCalcDaysSinceFirstDoseIfNull() {
        return getEvent().getCalcDaysSinceFirstDoseIfNull();
    }

    @Override
    public Integer getDaysSinceFirstDoseRaw() {
        return getEvent().getDaysSinceFirstDose();
    }

    @Override
    public Date getFirstTreatmentDate() {
        return getSubject().getFirstTreatmentDate();
    }

    @Override
    public Date getMeasurementTimePoint() {
        return getEvent().getMeasurementDate();
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

    @Override
    public Date getEventDate() {
        return getEvent().getMeasurementDate();
    }

    @Override
    public Date getBaselineDate() {
        return getEvent().getBaselineDate();
    }

    @Column(columnName = "daysOnStudy", order = 3, displayName = "Days on study")
    public Integer getDaysOnStudy() {
        return getDaysSinceFirstDose();
    }


    @Column(columnName = "measurementTimePoint",
            order = 2,
            displayName = "Measurement time point",
            type = DOD,
            defaultSortBy = true,
            defaultSortOrder = 1)
    public Date getAccessibleMeasurementTimePoint() {
        if (getSubject().getStudyInfo().isLimitXAxisToVisitNumber()) {
            return null;
        } else {
            return getEvent().getMeasurementDate();
        }
    }

    public enum Attributes implements GroupByOption<Vital> {
        ID(EntityAttribute.attribute("ID", Vital::getId)),
        STUDY_ID(EntityAttribute.attribute("STUDY_ID", Vital::getStudyId)),
        SUBJECT(EntityAttribute.attribute("SUBJECT", Vital::getSubject)),
        SUBJECT_ID(EntityAttribute.attribute("SUBJECT_ID", Vital::getSubjectId)),
        MEASUREMENT(EntityAttribute.attribute("MEASUREMENT", (Vital e) -> e.getEvent().getVitalsMeasurement())),
        PLANNED_TIMEPOINT(EntityAttribute.attribute("PLANNED_TIMEPOINT", (Vital e) -> e.getEvent().getPlannedTimePoint())),
        MEASUREMENT_DATE(EntityAttribute.attribute("MEASUREMENT_DATE", Vital::getMeasurementTimePoint)),
        START_DATE(EntityAttribute.attribute("START_DATE", (Vital e) -> e.getEvent().getMeasurementDate())),
        VISIT_NUMBER(EntityAttribute.attribute("VISIT_NUMBER", (Vital e) -> VisitNumber.fromValue(e.getEvent().getVisitNumber()))),
        DAYS_SINCE_FIRST_DOSE(EntityAttribute.attribute("DAYS_SINCE_FIRST_DOSE", Vital::getDaysSinceFirstDose)),
        SCHEDULE_TIMEPOINT(EntityAttribute.attribute("SCHEDULE_TIMEPOINT", (Vital e) -> e.getEvent().getScheduleTimepoint())),
        UNIT(EntityAttribute.attribute("UNIT", (Vital e) -> e.getEvent().getUnit())),
        PERCENTAGE_CHANGE_FROM_BASELINE(EntityAttribute.attribute("PERCENTAGE_CHANGE_FROM_BASELINE", Vital::getPercentChangeFromBaseline)),
        CHANGE_FROM_BASELINE(EntityAttribute.attribute("CHANGE_FROM_BASELINE", Vital::getChangeFromBaseline)),
        BASELINE(EntityAttribute.attribute("BASELINE", Vital::getBaselineValue)),
        BASELINE_FLAG(EntityAttribute.attribute("BASELINE_FLAG", (Vital e) -> e.getEvent().getBaselineFlag())),
        RESULT_VALUE(EntityAttribute.attribute("RESULT_VALUE", Vital::getResultValue)),
        ANALYSIS_VISIT(EntityAttribute.attribute("ANALYSIS_VISIT", (Vital e) -> e.getEvent().getAnalysisVisit())),
        STUDY_PERIOD(EntityAttribute.attribute("STUDY_PERIOD", (Vital e) -> e.getEvent().getStudyPeriod())),
        LAST_DOSE_DATE(EntityAttribute.attribute("LAST_DOSE_DATE", (Vital e) -> e.getEvent().getLastDoseDate())),
        LAST_DOSE_AMOUNT(EntityAttribute.attribute("LAST_DOSE_AMOUNT", (Vital e) -> e.getEvent().getLastDoseAmount())),
        SIDES_OF_INTEREST(EntityAttribute.attribute("SIDES_OF_INTEREST", (Vital e) -> e.getEvent().getSidesOfInterest())),
        PHYSICAL_POSITION(EntityAttribute.attribute("PHYSICAL_POSITION", (Vital e) -> e.getEvent().getPhysicalPosition())),
        ANATOMICAL_LOCATION(EntityAttribute.attribute("ANATOMICAL_LOCATION", (Vital e) -> e.getEvent().getAnatomicalLocation())),
        CLINICALLY_SIGNIFICANT(EntityAttribute.attribute("CLINICALLY_SIGNIFICANT", (Vital e) -> e.getEvent().getClinicallySignificant())),
        TREATMENT_ARM(EntityAttribute.attribute("TREATMENT_ARM", (Vital e) -> e.getSubject().getActualArm())),
        MEASUREMENT_WITH_UNIT(EntityAttribute.attribute("measurementWithUnit", (Vital v) -> v.getEvent().getVitalsMeasurement()
                + (v.getEvent().getUnit() == null ? "" : (" (" + v.getEvent().getUnit() + ")"))));
        @Getter
        private final EntityAttribute<Vital> attribute;

        Attributes(EntityAttribute<Vital> attribute) {
            this.attribute = attribute;
        }
    }
}
