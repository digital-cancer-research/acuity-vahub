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

import com.acuity.visualisations.rawdatamodel.filters.AlcoholFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Alcohol;

public class AlcoholFilterSummaryStatistics implements FilterSummaryStatistics<Alcohol> {
    private AlcoholFilters alcoholFilters = new AlcoholFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Alcohol alcohol = (Alcohol) value;

        alcoholFilters.getSubstanceCategory().completeWithValue(alcohol.getEvent().getSubstanceCategory());
        alcoholFilters.getSubstanceUseOccurrence().completeWithValue(alcohol.getEvent().getSubstanceUseOccurrence());
        alcoholFilters.getSubstanceType().completeWithValue(alcohol.getEvent().getSubstanceType());
        alcoholFilters.getOtherSubstanceTypeSpec().completeWithValue(alcohol.getEvent().getOtherSubstanceTypeSpec());
        alcoholFilters.getFrequency().completeWithValue(alcohol.getEvent().getFrequency());
        alcoholFilters.getSubstanceTypeUseOccurrence().completeWithValue(alcohol.getEvent().getSubstanceTypeUseOccurrence());
        alcoholFilters.getSubstanceConsumption().completeWithValue(alcohol.getEvent().getSubstanceConsumption());
        alcoholFilters.getStartDate().completeWithValue(alcohol.getEvent().getStartDate());
        alcoholFilters.getEndDate().completeWithValue(alcohol.getEvent().getEndDate());

        alcoholFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Alcohol> other) {
        AlcoholFilters otherFilters = (AlcoholFilters) other;

        this.alcoholFilters.getSubstanceCategory().complete(otherFilters.getSubstanceCategory());
        this.alcoholFilters.getSubstanceUseOccurrence().complete(otherFilters.getSubstanceUseOccurrence());
        this.alcoholFilters.getSubstanceType().complete(otherFilters.getSubstanceType());
        this.alcoholFilters.getOtherSubstanceTypeSpec().complete(otherFilters.getOtherSubstanceTypeSpec());
        this.alcoholFilters.getFrequency().complete(otherFilters.getFrequency());
        this.alcoholFilters.getSubstanceTypeUseOccurrence().complete(otherFilters.getSubstanceTypeUseOccurrence());
        this.alcoholFilters.getSubstanceConsumption().complete(otherFilters.getSubstanceConsumption());
        this.alcoholFilters.getStartDate().complete(otherFilters.getStartDate());
        this.alcoholFilters.getEndDate().complete(otherFilters.getEndDate());

        count += other.count();
        this.alcoholFilters.setMatchedItemsCount(count);
    }

    @Override
    public Filters<Alcohol> getFilters() {
        return alcoholFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<Alcohol> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Alcohol> newStatistics() {
        return new AlcoholFilterSummaryStatistics();
    }
}
