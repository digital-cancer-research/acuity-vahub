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

import com.acuity.visualisations.rawdatamodel.filters.BiomarkerFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;

public class BiomarkerFilterSummaryStatistics implements FilterSummaryStatistics<Biomarker> {

    private BiomarkerFilters biomarkerFilters = new BiomarkerFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Biomarker biomarker = (Biomarker) value;

        biomarkerFilters.getGene().completeWithValue(biomarker.getEvent().getGene());
        biomarkerFilters.getMutation().completeWithValue(biomarker.getEvent().getMutation());
        biomarkerFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Biomarker> other) {

        BiomarkerFilterSummaryStatistics otherExposure = (BiomarkerFilterSummaryStatistics) other;
        count += otherExposure.count;

        biomarkerFilters.getGene().complete(otherExposure.biomarkerFilters.getGene());
        biomarkerFilters.getMutation().complete(otherExposure.biomarkerFilters.getMutation());
        biomarkerFilters.setMatchedItemsCount(count);
    }

    @Override
    public final BiomarkerFilters getFilters() {
        return biomarkerFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public BiomarkerFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Biomarker> newStatistics() {
        return new BiomarkerFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{biomarkerFilters=%s}",
                this.getClass().getSimpleName(),
                this.biomarkerFilters.toString());
    }
}
