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

import com.acuity.visualisations.rawdatamodel.filters.QtProlongationFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.QtProlongation;

public class QtProlongationFilterSummaryStatistics implements FilterSummaryStatistics<QtProlongation> {
    private QtProlongationFilters qtProlongationFilters = new QtProlongationFilters();
    private int count = 0;


    @Override
    public void accept(Object value) {
        count++;
        qtProlongationFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<QtProlongation> other) {
        QtProlongationFilterSummaryStatistics otherQtProlongation = (QtProlongationFilterSummaryStatistics) other;
        count += otherQtProlongation.count;
    }

    @Override
    public QtProlongationFilters getFilters() {
        return qtProlongationFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public QtProlongationFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<QtProlongation> newStatistics() {
        return new QtProlongationFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{qtProlongationFilters=%s}",
                this.getClass().getSimpleName(),
                this.qtProlongationFilters.toString());
    }
}
