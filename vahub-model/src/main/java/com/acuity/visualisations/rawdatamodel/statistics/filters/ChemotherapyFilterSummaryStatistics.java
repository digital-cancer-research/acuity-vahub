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

import com.acuity.visualisations.rawdatamodel.filters.ChemotherapyFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;

public class ChemotherapyFilterSummaryStatistics implements FilterSummaryStatistics<Chemotherapy> {

    private ChemotherapyFilters chemotherapyFilters = new ChemotherapyFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Chemotherapy chemotherapy = (Chemotherapy) value;

        chemotherapyFilters.getTherapyStatus().completeWithValue(chemotherapy.getEvent().getTreatmentStatus());
        chemotherapyFilters.getPreferredMed().completeWithValue(chemotherapy.getEvent().getPreferredMed());
        chemotherapyFilters.getTherapyClass().completeWithValue(chemotherapy.getEvent().getTherapyClass());
        chemotherapyFilters.getReasonForChemotherapyFailure().completeWithValue(chemotherapy.getEvent().getFailureReason());
        chemotherapyFilters.getChemotherapyBestResponse().completeWithValue(chemotherapy.getEvent().getBestResponse());
        chemotherapyFilters.getNumberOfChemotherapyCycles().completeWithValue(chemotherapy.getEvent().getNumOfCycles());
        chemotherapyFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Chemotherapy> other) {

        ChemotherapyFilterSummaryStatistics otherChemotherapy = (ChemotherapyFilterSummaryStatistics) other;
        count += otherChemotherapy.count;

        chemotherapyFilters.getTherapyStatus().complete(otherChemotherapy.chemotherapyFilters.getTherapyStatus());
        chemotherapyFilters.getPreferredMed().complete(otherChemotherapy.chemotherapyFilters.getPreferredMed());
        chemotherapyFilters.getTherapyClass().complete(otherChemotherapy.chemotherapyFilters.getTherapyClass());
        chemotherapyFilters.getReasonForChemotherapyFailure().complete(otherChemotherapy.chemotherapyFilters.getReasonForChemotherapyFailure());
        chemotherapyFilters.getChemotherapyBestResponse().complete(otherChemotherapy.chemotherapyFilters.getChemotherapyBestResponse());
        chemotherapyFilters.getNumberOfChemotherapyCycles().complete(otherChemotherapy.chemotherapyFilters.getNumberOfChemotherapyCycles());
        chemotherapyFilters.setMatchedItemsCount(count);
    }

    @Override
    public final ChemotherapyFilters getFilters() {
        return chemotherapyFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public ChemotherapyFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Chemotherapy> newStatistics() {
        return new ChemotherapyFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{chemotherapyFilters=%s}",
                this.getClass().getSimpleName(),
                this.chemotherapyFilters.toString());
    }
}
