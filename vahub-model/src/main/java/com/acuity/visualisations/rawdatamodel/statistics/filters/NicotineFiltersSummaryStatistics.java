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
import com.acuity.visualisations.rawdatamodel.filters.NicotineFilters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Nicotine;

public class NicotineFiltersSummaryStatistics implements FilterSummaryStatistics<Nicotine> {
    private NicotineFilters nicotineFilters = new NicotineFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Nicotine nicotine = (Nicotine) value;
        nicotineFilters.getCategory().completeWithValue(nicotine.getEvent().getCategory());
        nicotineFilters.getType().completeWithValue(nicotine.getEvent().getType());
        nicotineFilters.getOtherTypeSpec().completeWithValue(nicotine.getEvent().getOtherTypeSpec());
        nicotineFilters.getCurrentUseSpec().completeWithValue(nicotine.getEvent().getCurrentUseSpec());
        nicotineFilters.getUseOccurrence().completeWithValue(nicotine.getEvent().getUseOccurrence());
        nicotineFilters.getSubTypeUseOccurrence().completeWithValue(nicotine.getEvent().getSubTypeUseOccurrence());
        nicotineFilters.getStartDate().completeWithValue(nicotine.getStartDate());
        nicotineFilters.getEndDate().completeWithValue(nicotine.getEndDate());
        nicotineFilters.getConsumption().completeWithValue(nicotine.getEvent().getConsumption());
        nicotineFilters.getFrequencyInterval().completeWithValue(nicotine.getEvent().getFrequencyInterval());
        nicotineFilters.getNumberPackYears().completeWithValue(nicotine.getEvent().getNumberPackYears());

        nicotineFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Nicotine> other) {
        NicotineFiltersSummaryStatistics otherNicotine = (NicotineFiltersSummaryStatistics) other;
        nicotineFilters.getCategory().complete(otherNicotine.nicotineFilters.getCategory());
        nicotineFilters.getType().complete(otherNicotine.nicotineFilters.getType());
        nicotineFilters.getOtherTypeSpec().complete(otherNicotine.nicotineFilters.getOtherTypeSpec());
        nicotineFilters.getUseOccurrence().complete(otherNicotine.nicotineFilters.getUseOccurrence());
        nicotineFilters.getCurrentUseSpec().complete(otherNicotine.nicotineFilters.getCurrentUseSpec());
        nicotineFilters.getSubTypeUseOccurrence().complete(otherNicotine.nicotineFilters.getSubTypeUseOccurrence());
        nicotineFilters.getStartDate().complete(otherNicotine.nicotineFilters.getStartDate());
        nicotineFilters.getEndDate().complete(otherNicotine.nicotineFilters.getEndDate());
        nicotineFilters.getConsumption().complete(otherNicotine.nicotineFilters.getConsumption());
        nicotineFilters.getFrequencyInterval().complete(otherNicotine.nicotineFilters.getFrequencyInterval());
        nicotineFilters.getNumberPackYears().complete(otherNicotine.nicotineFilters.getNumberPackYears());

        count += other.count();
        nicotineFilters.setMatchedItemsCount(count);
    }

    @Override
    public Filters<Nicotine> getFilters() {
        return nicotineFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<Nicotine> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Nicotine> newStatistics() {
        return new NicotineFiltersSummaryStatistics();
    }
}
