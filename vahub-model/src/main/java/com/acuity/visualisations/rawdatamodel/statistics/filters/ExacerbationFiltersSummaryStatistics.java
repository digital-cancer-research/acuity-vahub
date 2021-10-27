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

import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;

public class ExacerbationFiltersSummaryStatistics implements FilterSummaryStatistics<Exacerbation> {

    private ExacerbationFilters exacerbationFilters = new ExacerbationFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Exacerbation exacerbation = (Exacerbation) value;
        exacerbationFilters.getExacerbationClassification().completeWithValue(exacerbation.getEvent().getExacerbationClassification());
        exacerbationFilters.getStartDate().completeWithValue(exacerbation.getEvent().getStartDate());
        exacerbationFilters.getEndDate().completeWithValue(exacerbation.getEvent().getEndDate());
        exacerbationFilters.getDaysOnStudyAtStart().completeWithValue(exacerbation.getEvent().getDaysOnStudyAtStart());
        exacerbationFilters.getDaysOnStudyAtEnd().completeWithValue(exacerbation.getEvent().getDaysOnStudyAtEnd());
        exacerbationFilters.getDuration().completeWithValue(exacerbation.getEvent().getDuration());
        exacerbationFilters.getStartPriorToRandomisation().completeWithValue(exacerbation.getEvent().getStartPriorToRandomisation());
        exacerbationFilters.getEndPriorToRandomisation().completeWithValue(exacerbation.getEvent().getEndPriorToRandomisation());
        exacerbationFilters.getHospitalisation().completeWithValue(exacerbation.getEvent().getHospitalisation());
        exacerbationFilters.getEmergencyRoomVisit().completeWithValue(exacerbation.getEvent().getEmergencyRoomVisit());
        exacerbationFilters.getAntibioticsTreatment().completeWithValue(exacerbation.getEvent().getAntibioticsTreatment());
        exacerbationFilters.getDepotCorticosteroidTreatment().completeWithValue(exacerbation.getEvent().getDepotCorticosteroidTreatment());
        exacerbationFilters.getSystemicCorticosteroidTreatment().completeWithValue(exacerbation.getEvent().getSystemicCorticosteroidTreatment());
        exacerbationFilters.getIncreasedInhaledCorticosteroidTreatment()
                .completeWithValue(exacerbation.getEvent().getIncreasedInhaledCorticosteroidTreatment());

        exacerbationFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Exacerbation> other) {
        ExacerbationFiltersSummaryStatistics otherExacerbation = (ExacerbationFiltersSummaryStatistics) other;
        exacerbationFilters.getExacerbationClassification().complete(otherExacerbation.exacerbationFilters.getExacerbationClassification());
        exacerbationFilters.getStartDate().complete(otherExacerbation.exacerbationFilters.getStartDate());
        exacerbationFilters.getEndDate().complete(otherExacerbation.exacerbationFilters.getEndDate());
        exacerbationFilters.getDaysOnStudyAtStart().complete(otherExacerbation.exacerbationFilters.getDaysOnStudyAtStart());
        exacerbationFilters.getDaysOnStudyAtEnd().complete(otherExacerbation.exacerbationFilters.getDaysOnStudyAtEnd());
        exacerbationFilters.getDuration().complete(otherExacerbation.exacerbationFilters.getDuration());
        exacerbationFilters.getStartPriorToRandomisation().complete(otherExacerbation.exacerbationFilters.getStartPriorToRandomisation());
        exacerbationFilters.getEndPriorToRandomisation().complete(otherExacerbation.exacerbationFilters.getEndPriorToRandomisation());
        exacerbationFilters.getHospitalisation().complete(otherExacerbation.exacerbationFilters.getHospitalisation());
        exacerbationFilters.getEmergencyRoomVisit().complete(otherExacerbation.exacerbationFilters.getEmergencyRoomVisit());
        exacerbationFilters.getAntibioticsTreatment().complete(otherExacerbation.exacerbationFilters.getAntibioticsTreatment());
        exacerbationFilters.getDepotCorticosteroidTreatment().complete(otherExacerbation.exacerbationFilters.getDepotCorticosteroidTreatment());
        exacerbationFilters.getSystemicCorticosteroidTreatment().complete(otherExacerbation.exacerbationFilters.getSystemicCorticosteroidTreatment());
        exacerbationFilters.getIncreasedInhaledCorticosteroidTreatment().complete(
                otherExacerbation.exacerbationFilters.getIncreasedInhaledCorticosteroidTreatment());

        exacerbationFilters.setMatchedItemsCount(count);
    }

    @Override
    public Filters<Exacerbation> getFilters() {
        return exacerbationFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<Exacerbation> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Exacerbation> newStatistics() {
        return new ExacerbationFiltersSummaryStatistics();
    }
}
