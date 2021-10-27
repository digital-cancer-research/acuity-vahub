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

import com.acuity.visualisations.rawdatamodel.filters.PathologyFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Pathology;

public class PathologyFilterSummaryStatistics implements FilterSummaryStatistics<Pathology> {

    private PathologyFilters pathologyFilters = new PathologyFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Pathology pathology = (Pathology) value;

        pathologyFilters.getDiagnosisDate().completeWithValue(pathology.getEvent().getDate());
        pathologyFilters.getDaysFromOriginalDiagnosis().completeWithValue(pathology.getDaysFromOriginalDiagnosis());
        pathologyFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Pathology> other) {

        PathologyFilterSummaryStatistics otherPathology = (PathologyFilterSummaryStatistics) other;
        count += otherPathology.count;

        pathologyFilters.getDiagnosisDate().complete(otherPathology.pathologyFilters.getDiagnosisDate());
        pathologyFilters.getDaysFromOriginalDiagnosis().complete(otherPathology.pathologyFilters.getDaysFromOriginalDiagnosis());
        pathologyFilters.setMatchedItemsCount(count);
    }

    @Override
    public final PathologyFilters getFilters() {
        return pathologyFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public PathologyFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Pathology> newStatistics() {
        return new PathologyFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{pathologyFilters=%s}",
                this.getClass().getSimpleName(),
                this.pathologyFilters.toString());
    }
}
