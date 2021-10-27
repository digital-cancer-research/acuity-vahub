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

package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.util.CombinedQueryBuilder;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.googlecode.cqengine.query.Query;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.Collection;

import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.ACTION_TAKEN;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.ACTIVE_INGREDIENTS;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.AE_NUM_CAUSED_ACTION_TAKEN;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.AE_NUM_CAUSED_TREATMENT_CYCLED_DELAYED;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.AE_PT_CAUSED_ACTION_TAKEN;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.AE_PT_CAUSED_TREATMENT_CYCLE_DELAYED;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.ATC_CODE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.ATC_DICTIONARY_TEXT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.DOSE_FREQ;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.DOSE_PER_ADMIN;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.DOSE_UNIT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.END_DATE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.FORMULATION;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.MAIN_REASON_FOR_ACTION_TAKEN;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.MAIN_REASON_FOR_ACTION_TAKEN_SPEC;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.MEDICATION_CODE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.MEDICATION_DICTIONARY_TEXT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.MEDICATION_GROUPING_NAME;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.MEDICATION_PT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.PLANNED_DOSE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.PLANNED_DOSE_UNITS;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.PLANNED_NO_DAYS_TREATMENT;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.REASON_FOR_THERAPY;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.REASON_TREATMENT_CYCLE_DELAYED;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.REASON_TREATMENT_CYCLE_DELAYED_OTHER;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.ROUTE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.START_DATE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.STUDY_DRUG;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.STUDY_DRUG_CATEGORY;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.SUBJECT_ID;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.TOTAL_DAILY_DOSE;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose.Attributes.TREATMENT_CYCLE_DELAYED;

@Data
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public class DrugDoseFilters extends Filters<DrugDose> {
    private SetFilter<String> studyDrug = new SetFilter<>();
    private SetFilter<String> studyDrugCategory = new SetFilter<>();
    private DateRangeFilter startDate = new DateRangeFilter();
    private DateRangeFilter endDate = new DateRangeFilter();
    private RangeFilter<Double> dosePerAdmin = new RangeFilter<>();
    private SetFilter<String> doseUnit = new SetFilter<>();
    private SetFilter<String> doseFreq = new SetFilter<>();
    private RangeFilter<Double> totalDailyDose = new RangeFilter<>();
    private RangeFilter<Double> plannedDose = new RangeFilter<>();
    private SetFilter<String> plannedDoseUnits = new SetFilter<>();
    private RangeFilter<Integer> plannedNoDaysTreatment = new RangeFilter<>();
    private SetFilter<String> formulation = new SetFilter<>();
    private SetFilter<String> route = new SetFilter<>();
    private SetFilter<String> actionTaken = new SetFilter<>();
    private SetFilter<String> mainReasonForActionTaken = new SetFilter<>();
    private SetFilter<String> mainReasonForActionTakenSpec = new SetFilter<>();
    private MultiValueIntRangeSetFilter aeNumCausedActionTaken = new MultiValueIntRangeSetFilter();
    private MultiValueSetFilter<String> aePtCausedActionTaken = new MultiValueSetFilter<>();
    private SetFilter<String> reasonForTherapy = new SetFilter<>();
    private SetFilter<String> treatmentCycleDelayed = new SetFilter<>();
    private SetFilter<String> reasonTreatmentCycleDelayed = new SetFilter<>();
    private SetFilter<String> reasonTreatmentCycleDelayedOther = new SetFilter<>();
    private MultiValueIntRangeSetFilter aeNumCausedTreatmentCycleDelayed = new MultiValueIntRangeSetFilter();
    private MultiValueSetFilter<String> aePtCausedTreatmentCycleDelayed = new MultiValueSetFilter<>();
    private SetFilter<String> medicationCode = new SetFilter<>();
    private SetFilter<String> medicationDictionaryText = new SetFilter<>();
    private SetFilter<String> atcCode = new SetFilter<>();
    private SetFilter<String> atcDictionaryText = new SetFilter<>();
    private SetFilter<String> medicationPt = new SetFilter<>();
    private SetFilter<String> medicationGroupingName = new SetFilter<>();
    private SetFilter<String> activeIngredients = new SetFilter<>();

    public static DrugDoseFilters empty() {
        return new DrugDoseFilters();
    }

    @Override
    public Query<DrugDose> getQuery(Collection<String> subjectIds) {
        return new CombinedQueryBuilder<DrugDose>(DrugDose.class)
                .add(getFilterQuery(SUBJECT_ID, new SetFilter<>(subjectIds)))
                .add(getFilterQuery(STUDY_DRUG, studyDrug))
                .add(getFilterQuery(STUDY_DRUG_CATEGORY, studyDrugCategory))
                .add(getFilterQuery(START_DATE, startDate))
                .add(getFilterQuery(END_DATE, endDate))
                .add(getFilterQuery(DOSE_PER_ADMIN, dosePerAdmin))
                .add(getFilterQuery(DOSE_UNIT, doseUnit))
                .add(getFilterQuery(DOSE_FREQ, doseFreq))
                .add(getFilterQuery(TOTAL_DAILY_DOSE, totalDailyDose))
                .add(getFilterQuery(PLANNED_DOSE, plannedDose))
                .add(getFilterQuery(PLANNED_DOSE_UNITS, plannedDoseUnits))
                .add(getFilterQuery(PLANNED_NO_DAYS_TREATMENT, plannedNoDaysTreatment))
                .add(getFilterQuery(FORMULATION, formulation))
                .add(getFilterQuery(ROUTE, route))
                .add(getFilterQuery(ACTION_TAKEN, actionTaken))
                .add(getFilterQuery(MAIN_REASON_FOR_ACTION_TAKEN, mainReasonForActionTaken))
                .add(getFilterQuery(MAIN_REASON_FOR_ACTION_TAKEN_SPEC, mainReasonForActionTakenSpec))
                .add(getFilterQuery(AE_NUM_CAUSED_ACTION_TAKEN, aeNumCausedActionTaken))
                .add(getFilterQuery(AE_PT_CAUSED_ACTION_TAKEN, aePtCausedActionTaken))
                .add(getFilterQuery(REASON_FOR_THERAPY, reasonForTherapy))
                .add(getFilterQuery(TREATMENT_CYCLE_DELAYED, treatmentCycleDelayed))
                .add(getFilterQuery(REASON_TREATMENT_CYCLE_DELAYED, reasonTreatmentCycleDelayed))
                .add(getFilterQuery(REASON_TREATMENT_CYCLE_DELAYED_OTHER, reasonTreatmentCycleDelayedOther))
                .add(getFilterQuery(AE_NUM_CAUSED_TREATMENT_CYCLED_DELAYED, aeNumCausedTreatmentCycleDelayed))
                .add(getFilterQuery(AE_PT_CAUSED_TREATMENT_CYCLE_DELAYED, aePtCausedTreatmentCycleDelayed))
                .add(getFilterQuery(MEDICATION_CODE, medicationCode))
                .add(getFilterQuery(MEDICATION_DICTIONARY_TEXT, medicationDictionaryText))
                .add(getFilterQuery(ATC_CODE, atcCode))
                .add(getFilterQuery(ATC_DICTIONARY_TEXT, atcDictionaryText))
                .add(getFilterQuery(MEDICATION_PT, medicationPt))
                .add(getFilterQuery(MEDICATION_GROUPING_NAME, medicationGroupingName))
                .add(getFilterQuery(ACTIVE_INGREDIENTS, activeIngredients))
                .build();
    }
}
