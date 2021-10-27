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

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.SurgicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurgicalHistory;

public class SurgicalHistoryFiltersSummaryStatistics implements FilterSummaryStatistics<SurgicalHistory> {
    private SurgicalHistoryFilters surgicalHistoryFilters = new SurgicalHistoryFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        SurgicalHistory surgicalHistory = (SurgicalHistory) value;
        surgicalHistoryFilters.getSurgicalProcedure().completeWithValue(surgicalHistory.getEvent().getSurgicalProcedure());
        surgicalHistoryFilters.getStart().completeWithValue(surgicalHistory.getStartDate());
        surgicalHistoryFilters.getCurrentMedication().completeWithValue(surgicalHistory.getEvent().getCurrentMedication());
        surgicalHistoryFilters.getSoc().completeWithValue(surgicalHistory.getEvent().getSoc());
        surgicalHistoryFilters.getHlt().completeWithValue(surgicalHistory.getEvent().getHlt());
        surgicalHistoryFilters.getPreferredTerm().completeWithValue(surgicalHistory.getEvent().getPreferredTerm());

        surgicalHistoryFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<SurgicalHistory> other) {
        SurgicalHistoryFiltersSummaryStatistics otherSurgicalHistory = (SurgicalHistoryFiltersSummaryStatistics) other;
        surgicalHistoryFilters.getSurgicalProcedure().complete(otherSurgicalHistory.surgicalHistoryFilters.getSurgicalProcedure());
        surgicalHistoryFilters.getStart().complete(otherSurgicalHistory.surgicalHistoryFilters.getStart());
        surgicalHistoryFilters.getCurrentMedication().complete(otherSurgicalHistory.surgicalHistoryFilters.getCurrentMedication());
        surgicalHistoryFilters.getSoc().complete(otherSurgicalHistory.surgicalHistoryFilters.getSoc());
        surgicalHistoryFilters.getPreferredTerm().complete(otherSurgicalHistory.surgicalHistoryFilters.getPreferredTerm());
        surgicalHistoryFilters.getHlt().complete(otherSurgicalHistory.surgicalHistoryFilters.getHlt());

        count += other.count();
        surgicalHistoryFilters.setMatchedItemsCount(count);
    }

    @Override
    public Filters<SurgicalHistory> getFilters() {
        return surgicalHistoryFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<SurgicalHistory> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<SurgicalHistory> newStatistics() {
        return new SurgicalHistoryFiltersSummaryStatistics();
    }
}
