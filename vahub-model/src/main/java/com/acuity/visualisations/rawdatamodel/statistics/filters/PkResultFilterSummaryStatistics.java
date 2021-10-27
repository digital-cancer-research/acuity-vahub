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

import com.acuity.visualisations.rawdatamodel.filters.PkResultFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;

public class PkResultFilterSummaryStatistics implements FilterSummaryStatistics<PkResult> {

    private PkResultFilters pkResultFilters = new PkResultFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        PkResult pkResult = (PkResult) value;
        pkResultFilters.setMatchedItemsCount(count);
        pkResultFilters.getAnalyte().completeWithValue(pkResult.getEvent().getAnalyte());
    }

    @Override
    public void combine(FilterSummaryStatistics<PkResult> other) {

        PkResultFilterSummaryStatistics otherPkResult = (PkResultFilterSummaryStatistics) other;
        count += otherPkResult.count;

        pkResultFilters.getAnalyte().complete(otherPkResult.pkResultFilters.getAnalyte());
    }

    @Override
    public final PkResultFilters getFilters() {
        return pkResultFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public PkResultFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<PkResult> newStatistics() {
        return new PkResultFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{pkResultFilters=%s}",
                this.getClass().getSimpleName(),
                this.pkResultFilters.toString());
    }
}
