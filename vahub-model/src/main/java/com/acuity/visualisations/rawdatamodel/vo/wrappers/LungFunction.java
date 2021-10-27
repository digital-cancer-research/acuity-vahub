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
import com.acuity.visualisations.rawdatamodel.vo.HasValueAndBaseline;
import com.acuity.visualisations.rawdatamodel.vo.LungFunctionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

@ToString(callSuper = true)
public class LungFunction extends SubjectAwareWrapper<LungFunctionRaw>
        implements HasDaysSinceFirstDose, HasValueAndBaseline, HasBaselineDate, Serializable {

    public LungFunction(LungFunctionRaw event, Subject subject) {
        super(event, subject);
    }

    @Override
    public Date getBaselineDate() {
        return getEvent().getBaselineDate();
    }

    public enum Attributes implements GroupByOption<LungFunction> {
        SUBJECT_ID(EntityAttribute.attribute("subjectId", LungFunction::getSubjectId)),
        MEASUREMENT(EntityAttribute.attribute("measurement", LungFunction::getMeasurement)),
        MEASUREMENT_NAME(EntityAttribute.attribute("measurementName", LungFunction::getMeasurementName)),
        MEASUREMENT_TIME_POINT(EntityAttribute.attribute("measurementTimePoint", LungFunction::getAccessibleMeasurementTimePoint)),
        DAYS_ON_STUDY(EntityAttribute.attribute("daysOnStudy", LungFunction::getDaysOnStudy)),
        PROTOCOL_SCHEDULE_TIMEPOINT(EntityAttribute.attribute("protocolScheduleTimepoint", LungFunction::getProtocolScheduleTimepoint)),
        VISIT_NUMBER(EntityAttribute.attribute("visitNumber", LungFunction::getVisitNumber)),
        ARM(EntityAttribute.attribute("arm", (LungFunction l) -> l.getSubject().getActualArm())),
        VISIT_DESCRIPTION(EntityAttribute.attribute("visitDescription", (LungFunction l) -> l.getEvent().getVisitDescription())),
        RESULT_VALUE(EntityAttribute.attribute("resultValue", LungFunction::getResultValue)),
        RESULT_UNIT(EntityAttribute.attribute("resultUnit", LungFunction::getResultUnit)),
        BASELINE_VALUE(EntityAttribute.attribute("baselineValue", LungFunction::getBaselineValue)),
        CHANGE_FROM_BASELINE(EntityAttribute.attribute("changeFromBaseline", LungFunction::getChangeFromBaseline)),
        PERCENT_CHANGE_FROM_BASELINE(EntityAttribute.attribute("percentChangeFromBaseline", LungFunction::getPercentChangeFromBaseline)),
        BASELINE_FLAG(EntityAttribute.attribute("baselineFlag", LungFunction::getBaselineFlag)),
        VISIT_DATE(EntityAttribute.attribute("visitDate", (LungFunction l) -> l.getEvent().getVisitDate()));

        @Getter
        private final EntityAttribute<LungFunction> attribute;

        Attributes(EntityAttribute<LungFunction> attribute) {
            this.attribute = attribute;
        }
    }


    @Override
    public Date getFirstTreatmentDate() {
        return getSubject().getFirstTreatmentDate();
    }

    @Override
    public Integer getDaysSinceFirstDoseRaw() {
        return getEvent().getDaysSinceFirstDose();
    }

    @Override
    public Boolean isCalcDaysSinceFirstDoseIfNull() {
        return getEvent().getCalcDaysSinceFirstDoseIfNull();
    }

    public String getMeasurementName() {
        return getEvent().getMeasurementName();
    }

    @Override
    public Date getMeasurementTimePoint() {
        return getEvent().getMeasurementTimePoint();
    }

    @Column(displayName = "Measurement time point",
            columnName = "measurementTimePoint",
            order = 5,
            datasetType = Column.DatasetType.ACUITY,
            defaultSortBy = true,
            defaultSortOrder = 1)
    public Date getAccessibleMeasurementTimePoint() {
        return getSubject().getStudyInfo().isLimitXAxisToVisitNumber()
                ? null
                : getEvent().getMeasurementTimePoint();
    }

    @Column(displayName = "Days on study", columnName = "daysOnStudy", order = 6, datasetType = Column.DatasetType.ACUITY)
    public Integer getDaysOnStudy() {
        return getDaysSinceFirstDose();
    }

    public String getProtocolScheduleTimepoint() {
        return getEvent().getProtocolScheduleTimepoint();
    }

    @Column(displayName = "Visit number", columnName = "visitNumber", order = 8, datasetType = Column.DatasetType.ACUITY)
    public VisitNumber getVisitNumber() {
        return VisitNumber.fromValue(getEvent().getVisit());
    }

    @Override
    public Double getResultValue() {
        return getEvent().getResultValue();
    }

    public String getResultUnit() {
        return getEvent().getUnit();
    }

    @Override
    public Double getBaselineValue() {
        return getEvent().getBaselineValue();
    }

    @Override
    public Double getChangeFromBaseline() {
        return getEvent().getChangeFromBaseline();
    }

    @Override
    public Double getPercentChangeFromBaseline() {
        return getEvent().getPercentChangeFromBaseline();
    }

    public String getBaselineFlag() {
        return getEvent().getBaselineFlag();
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
    public Date getEventDate() {
        return getEvent().getEventDate();
    }

    public String getUnit() {
        return getEvent().getUnit();
    }

    public String getCode() {
        return String.format("%s (%s) %s", getMeasurementName(), getUnit(), getEvent().getProtocolScheduleTimepoint());
    }

    /**
     * @return Measurement name combined with a unit.
     * For example: FEV1 (mL)
     */
    public String getMeasurement() {
        return getEvent().getMeasurementName()
                + (getEvent().getUnit() == null ? "" : " (" + getEvent().getUnit() + ")");
    }
}
