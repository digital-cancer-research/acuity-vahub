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
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;
import java.util.OptionalInt;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class DrugDose extends SubjectAwareWrapper<DrugDoseRaw> implements HasStartEndDate, Serializable {
    public DrugDose(DrugDoseRaw event, Subject subject) {
        super(event, subject);
    }

    @Override
    public Date getStartDate() {
        return getEvent().getStartDate();
    }

    @Override
    public Date getEndDate() {
        return getEvent().getEndDate();
    }

    @Column(order = 1, displayName = "Study day at start", type = Column.Type.SSV)
    public Integer getStudyDay() {
        OptionalInt studyDay = DaysUtil.daysBetween(getSubject().getFirstTreatmentDate(), getStartDate());
        return studyDay.isPresent() ? studyDay.getAsInt() : null;
    }

    public boolean isActive() {
        return getEvent().getDose() != null && getEvent().getDose() > 0;
    }

    public enum Attributes implements GroupByOption<DrugDose> {

        ID(EntityAttribute.attribute("id", (DrugDose e) -> e.getEvent().getId())),
        STUDY_ID(EntityAttribute.attribute("studyId", (DrugDose e) -> e.getSubject().getClinicalStudyCode())),
        STUDY_PART(EntityAttribute.attribute("studyPart", (DrugDose e) -> e.getSubject().getStudyPart())),
        SUBJECT_ID(EntityAttribute.attribute("subjectId", (DrugDose e) -> e.getSubject().getSubjectId())),
        SUBJECT(EntityAttribute.attribute("subject", (DrugDose e) -> e.getSubject().getSubjectCode())),

        STUDY_DRUG(EntityAttribute.attribute("studyDrug", (DrugDose e) -> e.getEvent().getDrugName())),
        STUDY_DRUG_CATEGORY(EntityAttribute.attribute("studyDrugCategory", (
                DrugDose e) -> e.getEvent().getStudyDrugCategory())),
        START_DATE(EntityAttribute.attribute("startDate", e -> e.getEvent().getStartDate())),
        END_DATE(EntityAttribute.attribute("endDate", e -> e.getEvent().getEndDate())),
        DOSE_PER_ADMIN(EntityAttribute.attribute("dosePerAdmin", e -> e.getEvent().getDose())),
        DOSE_UNIT(EntityAttribute.attribute("doseUnit", e -> e.getEvent().getDoseUnit())),
        DOSE_FREQ_WITH_UNIT(EntityAttribute.attribute("doseFreqWithUnit", DrugDose::getDoseFrequencyWithUnit)),
        DOSE_FREQ(EntityAttribute.attribute("doseFreq", e -> e.getEvent().getFrequencyName())),
        TOTAL_DAILY_DOSE(EntityAttribute.attribute("totalDailyDose", e -> e.getEvent().getTotalDailyDose())),
        PLANNED_DOSE(EntityAttribute.attribute("plannedDose", e -> e.getEvent().getPlannedDose())),
        PLANNED_DOSE_UNITS(EntityAttribute.attribute("plannedDoseUnits",
                e -> e.getEvent().getPlannedDoseUnits())),
        PLANNED_NO_DAYS_TREATMENT(EntityAttribute.attribute("plannedNoDaysTreatment",
                e -> e.getEvent().getPlannedNoDaysTreatment())),
        FORMULATION(EntityAttribute.attribute("formulation", e -> e.getEvent().getFormulation())),
        ROUTE(EntityAttribute.attribute("route", e -> e.getEvent().getRoute())),
        ACTION_TAKEN(EntityAttribute.attribute("actionTaken", e -> e.getEvent().getActionTaken())),
        MAIN_REASON_FOR_ACTION_TAKEN(EntityAttribute.attribute("mainReasonForActionTaken",
                e -> e.getEvent().getReasonForActionTaken())),
        MAIN_REASON_FOR_ACTION_TAKEN_SPEC(EntityAttribute.attribute("mainReasonForActionTakenSpec",
                e -> e.getEvent().getMainReasonForActionTakenSpec())),
        AE_NUM_CAUSED_ACTION_TAKEN(EntityAttribute.attribute("aeNumCausedActionTaken",
                (DrugDose e) -> e.getEvent().getAeNumCausedActionTaken(), Integer.class)),
        AE_PT_CAUSED_ACTION_TAKEN(EntityAttribute.attribute("aePtCausedActionTaken",
                (DrugDose e) -> e.getEvent().getAePtCausedActionTaken(), String.class)),
        REASON_FOR_THERAPY(EntityAttribute.attribute("reasonForTherapy", e ->
                e.getEvent().getReasonForTherapy())),
        TREATMENT_CYCLE_DELAYED(EntityAttribute.attribute("treatmentCycleDelayed",
                e -> e.getEvent().getTreatmentCycleDelayed())),
        REASON_TREATMENT_CYCLE_DELAYED(EntityAttribute.attribute("reasonTreatmentCycleDelayed",
                e -> e.getEvent().getReasonTreatmentCycleDelayed())),
        REASON_TREATMENT_CYCLE_DELAYED_OTHER(EntityAttribute.attribute("reasonTreatmentCycleDelayedOther",
                e -> e.getEvent().getReasonTreatmentCycleDelayedOther())),
        AE_NUM_CAUSED_TREATMENT_CYCLED_DELAYED(EntityAttribute.attribute("aeNumCausedTreatmentCycleDelayed",
                (DrugDose e) -> e.getEvent().getAeNumCausedTreatmentCycleDelayed(), Integer.class)),
        AE_PT_CAUSED_TREATMENT_CYCLE_DELAYED(EntityAttribute.attribute("aePtCausedTreatmentCycleDelayed",
                (DrugDose e) -> e.getEvent().getAePtCausedTreatmentCycleDelayed(), String.class)),
        MEDICATION_CODE(EntityAttribute.attribute("medicationCode", e -> e.getEvent().getMedicationCode())),
        MEDICATION_DICTIONARY_TEXT(EntityAttribute.attribute("medicationDictionaryText",
                e -> e.getEvent().getMedicationDictionaryText())),
        ATC_CODE(EntityAttribute.attribute("atcCode", e -> e.getEvent().getAtcCode())),
        ATC_DICTIONARY_TEXT(EntityAttribute.attribute("atcDictionaryText",
                e -> e.getEvent().getAtcDictionaryText())),
        MEDICATION_PT(EntityAttribute.attribute("medicationPt", e -> e.getEvent().getMedicationPt())),
        MEDICATION_GROUPING_NAME(EntityAttribute.attribute("medicationGroupingName",
                e -> e.getEvent().getMedicationGroupingName())),
        ACTIVE_INGREDIENTS(EntityAttribute.attribute("activeIngredients",
                (DrugDose e) -> e.getEvent().getActiveIngredient()));

        @Getter
        private final EntityAttribute<DrugDose> attribute;

        Attributes(EntityAttribute<DrugDose> attribute) {
            this.attribute = attribute;
        }
    }

    public String getDoseFrequencyWithUnit() {
        String unit = getEvent().getDoseUnit();
        String freq = getEvent().getFrequencyName();
        return freq == null || unit == null ? freq : freq + " (" + unit + ")";
    }
}
