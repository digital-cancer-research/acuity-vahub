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

package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;

public class DrugDoseFiltersSummaryStatistics implements FilterSummaryStatistics<DrugDose> {

    private DrugDoseFilters drugDoseFilters = new DrugDoseFilters();

    private int count;

    @Override
    public void accept(Object value) {
        count++;

        DrugDose drugDose = (DrugDose) value;

        DrugDoseRaw raw = drugDose.getEvent();

        drugDoseFilters.getStudyDrug().completeWithValue(raw.getDrugName());
        drugDoseFilters.getStudyDrugCategory().completeWithValue(raw.getStudyDrugCategory());
        drugDoseFilters.getStartDate().completeWithValue(drugDose.getStartDate());
        drugDoseFilters.getEndDate().completeWithValue(drugDose.getEndDate());
        drugDoseFilters.getDosePerAdmin().completeWithValue(raw.getDose());
        drugDoseFilters.getDoseUnit().completeWithValue(raw.getDoseUnit());
        drugDoseFilters.getDoseFreq().completeWithValue(raw.getFrequencyName());
        drugDoseFilters.getTotalDailyDose().completeWithValue(raw.getTotalDailyDose());
        drugDoseFilters.getPlannedDose().completeWithValue(raw.getPlannedDose());
        drugDoseFilters.getPlannedDoseUnits().completeWithValue(raw.getPlannedDoseUnits());
        drugDoseFilters.getPlannedNoDaysTreatment().completeWithValue(raw.getPlannedNoDaysTreatment());
        drugDoseFilters.getFormulation().completeWithValue(raw.getFormulation());
        drugDoseFilters.getRoute().completeWithValue(raw.getRoute());
        drugDoseFilters.getActionTaken().completeWithValue(raw.getActionTaken());
        drugDoseFilters.getMainReasonForActionTaken().completeWithValue(raw.getReasonForActionTaken());
        drugDoseFilters.getMainReasonForActionTakenSpec().completeWithValue(raw.getMainReasonForActionTakenSpec());
        drugDoseFilters.getAeNumCausedActionTaken().completeWithValues(raw.getAeNumCausedActionTaken());
        drugDoseFilters.getAePtCausedActionTaken().completeWithValues(raw.getAePtCausedActionTaken());
        drugDoseFilters.getReasonForTherapy().completeWithValue(raw.getReasonForTherapy());
        drugDoseFilters.getTreatmentCycleDelayed().completeWithValue(raw.getTreatmentCycleDelayed());
        drugDoseFilters.getReasonTreatmentCycleDelayed().completeWithValue(raw.getReasonTreatmentCycleDelayed());
        drugDoseFilters.getReasonTreatmentCycleDelayedOther().completeWithValue(raw.getReasonTreatmentCycleDelayedOther());
        drugDoseFilters.getAeNumCausedTreatmentCycleDelayed().completeWithValues(raw.getAeNumCausedTreatmentCycleDelayed());
        drugDoseFilters.getAePtCausedTreatmentCycleDelayed().completeWithValues(raw.getAePtCausedTreatmentCycleDelayed());
        drugDoseFilters.getMedicationCode().completeWithValue(raw.getMedicationCode());
        drugDoseFilters.getMedicationDictionaryText().completeWithValue(raw.getMedicationDictionaryText());
        drugDoseFilters.getAtcCode().completeWithValue(raw.getAtcCode());
        drugDoseFilters.getAtcDictionaryText().completeWithValue(raw.getAtcDictionaryText());
        drugDoseFilters.getMedicationPt().completeWithValue(raw.getMedicationPt());
        drugDoseFilters.getMedicationGroupingName().completeWithValue(raw.getMedicationGroupingName());
        drugDoseFilters.getActiveIngredients().completeWithValue(raw.getActiveIngredient());

        drugDoseFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<DrugDose> other) {
        DrugDoseFiltersSummaryStatistics otherStats = (DrugDoseFiltersSummaryStatistics) other;

        drugDoseFilters.getStudyDrug().complete(otherStats.drugDoseFilters.getStudyDrug());
        drugDoseFilters.getStudyDrugCategory().complete(otherStats.drugDoseFilters.getStudyDrugCategory());
        drugDoseFilters.getStartDate().complete(otherStats.drugDoseFilters.getStartDate());
        drugDoseFilters.getEndDate().complete(otherStats.drugDoseFilters.getEndDate());
        drugDoseFilters.getDosePerAdmin().complete(otherStats.drugDoseFilters.getDosePerAdmin());
        drugDoseFilters.getDoseUnit().complete(otherStats.drugDoseFilters.getDoseUnit());
        drugDoseFilters.getDoseFreq().complete(otherStats.drugDoseFilters.getDoseFreq());
        drugDoseFilters.getTotalDailyDose().complete(otherStats.drugDoseFilters.getTotalDailyDose());
        drugDoseFilters.getPlannedDose().complete(otherStats.drugDoseFilters.getPlannedDose());
        drugDoseFilters.getPlannedDoseUnits().complete(otherStats.drugDoseFilters.getPlannedDoseUnits());
        drugDoseFilters.getPlannedNoDaysTreatment().complete(otherStats.drugDoseFilters.getPlannedNoDaysTreatment());
        drugDoseFilters.getFormulation().complete(otherStats.drugDoseFilters.getFormulation());
        drugDoseFilters.getRoute().complete(otherStats.drugDoseFilters.getRoute());
        drugDoseFilters.getActionTaken().complete(otherStats.drugDoseFilters.getActionTaken());
        drugDoseFilters.getMainReasonForActionTaken().complete(otherStats.drugDoseFilters.getMainReasonForActionTaken());
        drugDoseFilters.getMainReasonForActionTakenSpec().complete(otherStats.drugDoseFilters.getMainReasonForActionTakenSpec());
        drugDoseFilters.getAeNumCausedActionTaken().complete(otherStats.drugDoseFilters.getAeNumCausedActionTaken());
        drugDoseFilters.getAePtCausedActionTaken().complete(otherStats.drugDoseFilters.getAePtCausedActionTaken());
        drugDoseFilters.getReasonForTherapy().complete(otherStats.drugDoseFilters.getReasonForTherapy());
        drugDoseFilters.getTreatmentCycleDelayed().complete(otherStats.drugDoseFilters.getTreatmentCycleDelayed());
        drugDoseFilters.getReasonTreatmentCycleDelayed().complete(otherStats.drugDoseFilters.getReasonTreatmentCycleDelayed());
        drugDoseFilters.getReasonTreatmentCycleDelayedOther().complete(otherStats.drugDoseFilters.getReasonTreatmentCycleDelayedOther());
        drugDoseFilters.getAeNumCausedTreatmentCycleDelayed().complete(otherStats.drugDoseFilters.getAeNumCausedTreatmentCycleDelayed());
        drugDoseFilters.getAePtCausedTreatmentCycleDelayed().complete(otherStats.drugDoseFilters.getAePtCausedTreatmentCycleDelayed());
        drugDoseFilters.getMedicationCode().complete(otherStats.drugDoseFilters.getMedicationCode());
        drugDoseFilters.getMedicationDictionaryText().complete(otherStats.drugDoseFilters.getMedicationDictionaryText());
        drugDoseFilters.getAtcCode().complete(otherStats.drugDoseFilters.getAtcCode());
        drugDoseFilters.getAtcDictionaryText().complete(otherStats.drugDoseFilters.getAtcDictionaryText());
        drugDoseFilters.getMedicationPt().complete(otherStats.drugDoseFilters.getMedicationPt());
        drugDoseFilters.getMedicationGroupingName().complete(otherStats.drugDoseFilters.getMedicationGroupingName());
        drugDoseFilters.getActiveIngredients().complete(otherStats.drugDoseFilters.getActiveIngredients());

        drugDoseFilters.setMatchedItemsCount(count);
    }

    @Override
    public Filters<DrugDose> getFilters() {
        return drugDoseFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<DrugDose> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<DrugDose> newStatistics() {
        return new DrugDoseFiltersSummaryStatistics();
    }
}
