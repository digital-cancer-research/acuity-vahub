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

import com.acuity.visualisations.rawdatamodel.filters.DoseDiscFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;

public class DoseDiscFilterSummaryStatistics implements FilterSummaryStatistics<DoseDisc> {

    private DoseDiscFilters doseDiscFilters = new DoseDiscFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        DoseDisc doseDisc = (DoseDisc) value;
        doseDiscFilters.getDiscSpec().completeWithValue(doseDisc.getEvent().getIpDiscSpec());
        doseDiscFilters.getDiscDate().completeWithValue(doseDisc.getEvent().getDiscDate());
        doseDiscFilters.getDiscMainReason().completeWithValue(doseDisc.getEvent().getDiscReason());
        doseDiscFilters.getStudyDayAtDisc().completeWithValue(doseDisc.getStudyDayAtIpDiscontinuation());
        doseDiscFilters.getStudyDrug().completeWithValue(doseDisc.getEvent().getStudyDrug());
        doseDiscFilters.getSubjectDecisionSpec().completeWithValue(doseDisc.getEvent().getSubjectDecisionSpec());
        doseDiscFilters.getSubjectDecisionSpecOther().completeWithValue(doseDisc.getEvent().getSubjectDecisionSpecOther());

        doseDiscFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<DoseDisc> other) {
        DoseDiscFilterSummaryStatistics otherDoseDisc = (DoseDiscFilterSummaryStatistics) other;
        doseDiscFilters.getStudyDrug().complete(otherDoseDisc.doseDiscFilters.getStudyDrug());
        doseDiscFilters.getStudyDayAtDisc().complete(otherDoseDisc.doseDiscFilters.getStudyDayAtDisc());
        doseDiscFilters.getSubjectDecisionSpecOther().complete(otherDoseDisc.doseDiscFilters.getSubjectDecisionSpecOther());
        doseDiscFilters.getSubjectDecisionSpec().complete(otherDoseDisc.doseDiscFilters.getSubjectDecisionSpec());
        doseDiscFilters.getDiscSpec().complete(otherDoseDisc.doseDiscFilters.getDiscSpec());
        doseDiscFilters.getDiscDate().complete(otherDoseDisc.doseDiscFilters.getDiscDate());
        doseDiscFilters.getDiscMainReason().complete(otherDoseDisc.doseDiscFilters.getDiscMainReason());

        count += other.count();
        doseDiscFilters.setMatchedItemsCount(count);
    }

    @Override
    public Filters<DoseDisc> getFilters() {
        return doseDiscFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<DoseDisc> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<DoseDisc> newStatistics() {
        return new DoseDiscFilterSummaryStatistics();
    }
}
