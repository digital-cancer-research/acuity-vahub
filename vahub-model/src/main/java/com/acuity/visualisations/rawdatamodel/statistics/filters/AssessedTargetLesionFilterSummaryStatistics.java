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

import com.acuity.visualisations.rawdatamodel.filters.AssessedTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;

public class AssessedTargetLesionFilterSummaryStatistics implements FilterSummaryStatistics<AssessedTargetLesion> {

    private AssessedTargetLesionFilters atlFilters = new AssessedTargetLesionFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        AssessedTargetLesion atl = (AssessedTargetLesion) value;

        atlFilters.getBestResponse().completeWithValue(atl.getEvent().getBestResponse());
        atlFilters.getBestPercentageChangeFromBaseline().completeWithValue(atl
                .getEvent().getSumBestPercentageChangeFromBaseline());
        atlFilters.getNonTargetLesionsPresent().completeWithValue(atl.getEvent().getNonTargetLesionsPresent());
        atlFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<AssessedTargetLesion> other) {

        AssessedTargetLesionFilterSummaryStatistics otherAtl = (AssessedTargetLesionFilterSummaryStatistics) other;
        count += otherAtl.count;

        atlFilters.getBestResponse().complete(otherAtl.atlFilters.getBestResponse());
        atlFilters.getBestPercentageChangeFromBaseline()
                .complete(otherAtl.atlFilters.getBestPercentageChangeFromBaseline());
        atlFilters.getNonTargetLesionsPresent()
                .complete(otherAtl.atlFilters.getNonTargetLesionsPresent());
        atlFilters.setMatchedItemsCount(count);
    }

    @Override
    public final AssessedTargetLesionFilters getFilters() {
        return atlFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public AssessedTargetLesionFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<AssessedTargetLesion> newStatistics() {
        return new AssessedTargetLesionFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{atlFilters=%s}",
                this.getClass().getSimpleName(),
                this.atlFilters.toString());
    }
}
