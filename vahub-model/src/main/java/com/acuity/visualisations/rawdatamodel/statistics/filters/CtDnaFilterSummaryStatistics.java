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

import com.acuity.visualisations.rawdatamodel.filters.CtDnaFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;

import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna.ONLY_TRACKED_MUTATIONS;

public class CtDnaFilterSummaryStatistics implements FilterSummaryStatistics<CtDna> {

    private CtDnaFilters ctDnaFilters = new CtDnaFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        CtDna ctDna = (CtDna) value;
        ctDnaFilters.getGene().completeWithValue(ctDna.getEvent().getGene());
        ctDnaFilters.getMutation().completeWithValue(ctDna.getEvent().getMutation());
        if (YES.equals(ctDna.getEvent().getTrackedMutation())) {
            ctDnaFilters.getTrackedMutation().completeWithValue(ONLY_TRACKED_MUTATIONS);
        }
        ctDnaFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<CtDna> other) {
        CtDnaFilterSummaryStatistics otherCtDna = (CtDnaFilterSummaryStatistics) other;
        count += otherCtDna.count;
        ctDnaFilters.getGene().complete(otherCtDna.ctDnaFilters.getGene());
        ctDnaFilters.getMutation().complete(otherCtDna.ctDnaFilters.getMutation());
        ctDnaFilters.setMatchedItemsCount(count);
    }

    @Override
    public final CtDnaFilters getFilters() {
        return ctDnaFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public CtDnaFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<CtDna> newStatistics() {
        return new CtDnaFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{ctDnaFilters=%s}",
                this.getClass().getSimpleName(),
                this.ctDnaFilters.toString());
    }
}
