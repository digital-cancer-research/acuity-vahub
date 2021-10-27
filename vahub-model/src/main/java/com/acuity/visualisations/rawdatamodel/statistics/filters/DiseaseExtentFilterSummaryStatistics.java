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

import com.acuity.visualisations.rawdatamodel.filters.DiseaseExtentFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DiseaseExtent;

public class DiseaseExtentFilterSummaryStatistics implements FilterSummaryStatistics<DiseaseExtent> {

    private DiseaseExtentFilters diseaseExtentFilters = new DiseaseExtentFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        DiseaseExtent pathology = (DiseaseExtent) value;

        diseaseExtentFilters.getRecentProgressionDate().completeWithValue(pathology.getEvent().getRecentProgressionDate());
        diseaseExtentFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<DiseaseExtent> other) {

        DiseaseExtentFilterSummaryStatistics otherPathology = (DiseaseExtentFilterSummaryStatistics) other;
        count += otherPathology.count;

        diseaseExtentFilters.getRecentProgressionDate().complete(otherPathology.diseaseExtentFilters.getRecentProgressionDate());
        diseaseExtentFilters.setMatchedItemsCount(count);
    }

    @Override
    public final DiseaseExtentFilters getFilters() {
        return diseaseExtentFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public DiseaseExtentFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<DiseaseExtent> newStatistics() {
        return new DiseaseExtentFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{diseaseExtentFilters=%s}",
                this.getClass().getSimpleName(),
                this.diseaseExtentFilters.toString());
    }
}
