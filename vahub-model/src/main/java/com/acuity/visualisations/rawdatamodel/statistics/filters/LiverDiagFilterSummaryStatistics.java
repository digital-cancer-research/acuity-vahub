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
import com.acuity.visualisations.rawdatamodel.filters.LiverDiagFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverDiag;

public class LiverDiagFilterSummaryStatistics implements FilterSummaryStatistics<LiverDiag> {
    private LiverDiagFilters liverDiagFilters = new LiverDiagFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        LiverDiag liverDiag = (LiverDiag) value;

        liverDiagFilters.getLiverDiagInv().completeWithValue(liverDiag.getEvent().getLiverDiagInv());
        liverDiagFilters.getLiverDiagInvSpec().completeWithValue(liverDiag.getEvent().getLiverDiagInvSpec());
        liverDiagFilters.getLiverDiagInvDate().completeWithValue(liverDiag.getEvent().getLiverDiagInvDate());
        liverDiagFilters.getStudyDayLiverDiagInv().completeWithValue(liverDiag.getStudyDayLiverDiagInv());
        liverDiagFilters.getLiverDiagInvResult().completeWithValue(liverDiag.getEvent().getLiverDiagInvResult());
        liverDiagFilters.getPotentialHysLawCaseNum().completeWithValue(liverDiag.getEvent().getPotentialHysLawCaseNum());

        liverDiagFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<LiverDiag> other) {
        LiverDiagFilters otherFilters = (LiverDiagFilters) other.getFilters();

        liverDiagFilters.getLiverDiagInv().complete(otherFilters.getLiverDiagInv());
        liverDiagFilters.getLiverDiagInvSpec().complete(otherFilters.getLiverDiagInvSpec());
        liverDiagFilters.getLiverDiagInvDate().complete(otherFilters.getLiverDiagInvDate());
        liverDiagFilters.getStudyDayLiverDiagInv().complete(otherFilters.getStudyDayLiverDiagInv());
        liverDiagFilters.getLiverDiagInvResult().complete(otherFilters.getLiverDiagInvResult());
        liverDiagFilters.getPotentialHysLawCaseNum().complete(otherFilters.getPotentialHysLawCaseNum());

        count += other.count();
        liverDiagFilters.setMatchedItemsCount(count);
    }

    @Override
    public Filters<LiverDiag> getFilters() {
        return liverDiagFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<LiverDiag> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<LiverDiag> newStatistics() {
        return new LiverDiagFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{filters=%s}",
                this.getClass().getSimpleName(),
                this.liverDiagFilters.toString());
    }
}
