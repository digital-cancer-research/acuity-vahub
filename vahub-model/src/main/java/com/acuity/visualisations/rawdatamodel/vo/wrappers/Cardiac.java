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
import com.acuity.visualisations.rawdatamodel.vo.CardiacRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasBaselineDate;
import com.acuity.visualisations.rawdatamodel.vo.HasDaysSinceFirstDose;
import com.acuity.visualisations.rawdatamodel.vo.HasValueAndBaseline;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType.ACUITY;

@ToString(callSuper = true)
public class Cardiac extends SubjectAwareWrapper<CardiacRaw> implements HasValueAndBaseline,
        HasBaselineDate, HasDaysSinceFirstDose, Serializable {

    public Cardiac(CardiacRaw event, Subject subject) {
        super(event, subject);
    }

    @Override
    public Date getEventDate() {
        return getEvent().getMeasurementTimePoint();
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
    @Column(columnName = "changeFromBaseline", order = 9, displayName = "Change from baseline", datasetType = ACUITY)
    public Double getChangeFromBaseline() {
        return getEvent().getChangeFromBaseline();
    }

    @Override
    @Column(columnName = "percentChangeFromBaseline", order = 10, displayName = "Percent change from baseline", datasetType = ACUITY)
    public Double getPercentChangeFromBaseline() {
        return getEvent().getPercentChangeFromBaseline();
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
    @Column(columnName = "measurementTimePoint", order = 3, displayName = "Measurement time point", datasetType = ACUITY, defaultSortBy = true)
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

    public Date getAccessibleMeasurementTimePoint() {
        if (getSubject().getStudyInfo().isLimitXAxisToVisitNumber()) {
            return null;
        } else {
            return getEvent().getMeasurementTimePoint();
        }
    }

    @Column(columnName = "daysOnStudy", order = 4, displayName = "Days on study", datasetType = ACUITY)
    public Integer getDaysOnStudy() {
        return getDaysSinceFirstDose();
    }

    @Override
    public Date getBaselineDate() {
        return getEvent().getBaselineDate();
    }

    public enum Attributes implements GroupByOption<Cardiac> {
        ID(EntityAttribute.attribute("id", Cardiac::getId)),
        STUDY_ID(EntityAttribute.attribute("studyId", Cardiac::getStudyId)),
        STUDY_PART(EntityAttribute.attribute("studyPart", Cardiac::getStudyPart)),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", Cardiac::getSubjectId)),
        SUBJECT(EntityAttribute.attribute("subject", Cardiac::getSubjectCode)),
        ARM(EntityAttribute.attribute("arm", (Cardiac l) -> l.getSubject().getActualArm())),
        MEASUREMENT_NAME(EntityAttribute.attribute("measurementName", (Cardiac l) -> l.getEvent().getMeasurementName())),
        MEASUREMENT_CATEGORY(EntityAttribute.attribute("measurementCategory", (Cardiac l) -> l.getEvent().getMeasurementCategory())),
        MEASUREMENT_TIME_POINT(EntityAttribute.attribute("measurementTimePoint", Cardiac::getAccessibleMeasurementTimePoint)),
        RESULT_UNIT(EntityAttribute.attribute("resultUnit", (Cardiac l) -> l.getEvent().getResultUnit())),
        MEASUREMENT_WITH_UNIT(EntityAttribute.attribute("measurementWithUnit", (Cardiac v) -> v.getEvent().getMeasurementName()
                + (v.getEvent().getResultUnit() == null ? "" : " (" + v.getEvent().getResultUnit() + ")"))),
        RESULT_VALUE(EntityAttribute.attribute("resultValue", (Cardiac l) -> l.getEvent().getResultValue())),
        CLINICALLY_SIGNIFICANT(EntityAttribute.attribute("clinicallySignificant", (Cardiac l) -> l.getEvent().getClinicallySignificant())),
        DAYS_ON_STUDY(EntityAttribute.attribute("daysOnStudy", Cardiac::getDaysSinceFirstDose)),
        ANALYSIS_VISIT(EntityAttribute.attribute("analysisVisit", (Cardiac l) -> l.getEvent().getAnalysisVisit())),
        VISIT_NUMBER(EntityAttribute.attribute("visitNumber", (Cardiac l) -> VisitNumber.fromValue(l.getEvent().getVisitNumber()))),
        VISIT_DESCRIPTION(EntityAttribute.attribute("visitDescription", (Cardiac e) -> e.getEvent().getVisitDescription())),
        STUDY_PERIOD(EntityAttribute.attribute("studyPeriods", (Cardiac l) -> l.getEvent().getStudyPeriods())),
        BEAT_GROUP_NUMBER(EntityAttribute.attribute("beatGroupNumber", (Cardiac l) -> l.getEvent().getBeatGroupNumber())),
        BEAT_NUMBER_WITHIN_BEAT_GROUP(EntityAttribute.attribute("beatNumberWithinBeatGroup", (Cardiac l) -> l.getEvent().getBeatNumberWithinBeatGroup())),
        BEATS_NUMBER_AVG_BEAT(EntityAttribute.attribute("numberOfBeatsInAverageBeat", (Cardiac l) -> l.getEvent().getNumberOfBeatsInAverageBeat())),
        BEAT_GROUP_LENGTH_IN_SEC(EntityAttribute.attribute("beatGroupLengthInSec", (Cardiac l) -> l.getEvent().getBeatGroupLengthInSec())),
        PROTOCOL_SCHEDULE_TIMEPOINT(EntityAttribute.attribute("protocolScheduleTimepoint", (Cardiac l) -> l.getEvent().getProtocolScheduleTimepoint())),
        COMMENT(EntityAttribute.attribute("comment", (Cardiac l) -> l.getEvent().getComment())),
        WAVE(EntityAttribute.attribute("wave", (Cardiac l) -> l.getEvent().getWave())),
        METHOD(EntityAttribute.attribute("method", (Cardiac l) -> l.getEvent().getMethod())),
        REASON_ABNORMAL_CONDUCTION(EntityAttribute.attribute("reasonAbnormalConduction", (Cardiac l) -> l.getEvent().getReasonAbnormalConduction())),
        STT_CHANGES(EntityAttribute.attribute("sttChanges", (Cardiac l) -> l.getEvent().getSttChanges())),
        ST_SEGMENT(EntityAttribute.attribute("stSegment", (Cardiac l) -> l.getEvent().getStSegment())),
        HEART_RHYTHM(EntityAttribute.attribute("heartRhythm", (Cardiac l) -> l.getEvent().getHeartRhythm())),
        HEART_RHYTHM_OTHER(EntityAttribute.attribute("heartRhythmOther", (Cardiac l) -> l.getEvent().getHeartRhythmOther())),
        EXTRA_SYSTOLES(EntityAttribute.attribute("extraSystoles", (Cardiac l) -> l.getEvent().getExtraSystoles())),
        SPECIFY_EXTRA_SYSTOLES(EntityAttribute.attribute("specifyExtraSystoles", (Cardiac l) -> l.getEvent().getSpecifyExtraSystoles())),
        CONDUCTION(EntityAttribute.attribute("conduction", (Cardiac l) -> l.getEvent().getConduction())),
        TYPE_OF_CONDUCTION(EntityAttribute.attribute("typeOfConduction", (Cardiac l) -> l.getEvent().getTypeOfConduction())),
        SINUS_RHYTHM(EntityAttribute.attribute("sinusRhythm", (Cardiac l) -> l.getEvent().getSinusRhythm())),
        REASON_NO_SINUS_RHYTHM(EntityAttribute.attribute("reasonNoSinusRhythm", (Cardiac l) -> l.getEvent().getReasonNoSinusRhythm())),
        ATRIAL_FIBRILLATION(EntityAttribute.attribute("atrialFibrillation", (Cardiac l) -> l.getEvent().getAtrialFibrillation())),
        LAST_DOSE_DATE(EntityAttribute.attribute("dateOfLastDose", (Cardiac l) -> l.getEvent().getDateOfLastDose())),
        LAST_DOSE_AMOUNT(EntityAttribute.attribute("lastDoseAmount", (Cardiac l) -> l.getEvent().getLastDoseAmount())),
        BASELINE_FLAG(EntityAttribute.attribute("baselineFlag", (Cardiac l) -> l.getEvent().getBaselineFlag())),
        BASELINE_VALUE(EntityAttribute.attribute("baseline", (Cardiac l) -> l.getEvent().getBaselineValue())),
        CHANGE_FROM_BASELINE(EntityAttribute.attribute("changeFromBaseline", Cardiac::getChangeFromBaseline)),
        PERCENT_CHANGE_FROM_BASELINE(EntityAttribute.attribute("percentChangeFromBaseline", Cardiac::getPercentChangeFromBaseline));

        @Getter
        private final EntityAttribute<Cardiac> attribute;

        Attributes(EntityAttribute<Cardiac> attribute) {
            this.attribute = attribute;
        }
    }
}
