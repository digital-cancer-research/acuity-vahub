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

import com.acuity.visualisations.rawdatamodel.filters.RadiotherapyFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;

public class RadiotherapyFilterSummaryStatistics implements FilterSummaryStatistics<Radiotherapy> {

    private RadiotherapyFilters radiotherapyFilters = new RadiotherapyFilters(false);
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Radiotherapy radiotherapy = (Radiotherapy) value;

        radiotherapyFilters.getTherapyStatus().completeWithValue(radiotherapy.getEvent().getTreatmentStatus());
        radiotherapyFilters.getRadiationDose().completeWithValue(radiotherapy.getRadiationDose());
        radiotherapyFilters.setMatchedItemsCount(count);
        radiotherapyFilters.setRadiotherapyEnabled(true);
    }

    @Override
    public void combine(FilterSummaryStatistics<Radiotherapy> other) {

        RadiotherapyFilterSummaryStatistics otherRadiotherapy = (RadiotherapyFilterSummaryStatistics) other;
        count += otherRadiotherapy.count;

        radiotherapyFilters.getTherapyStatus().complete(otherRadiotherapy.radiotherapyFilters.getTherapyStatus());
        radiotherapyFilters.getRadiationDose().complete(otherRadiotherapy.radiotherapyFilters.getRadiationDose());
        radiotherapyFilters.setMatchedItemsCount(count);
        radiotherapyFilters.setRadiotherapyEnabled(radiotherapyFilters.isRadiotherapyEnabled()
                && otherRadiotherapy.radiotherapyFilters.isRadiotherapyEnabled());
    }

    @Override
    public final RadiotherapyFilters getFilters() {
        return radiotherapyFilters;
    }

    @Override
    public final int count() {
        return count;
    }

    @Override
    public RadiotherapyFilterSummaryStatistics build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Radiotherapy> newStatistics() {
        return new RadiotherapyFilterSummaryStatistics();
    }

    @Override
    public String toString() {
        return String.format(
                "%s{radiotherapyFilters=%s}",
                this.getClass().getSimpleName(),
                this.radiotherapyFilters.toString());
    }
}
