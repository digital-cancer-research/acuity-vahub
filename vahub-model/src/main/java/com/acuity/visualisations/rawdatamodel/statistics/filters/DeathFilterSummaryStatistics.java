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

import com.acuity.visualisations.rawdatamodel.filters.DeathFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Death;

public class DeathFilterSummaryStatistics implements FilterSummaryStatistics<Death> {

    private DeathFilters deathFilters = new DeathFilters();
    private int count = 0;

    @Override
    public void accept(Object value) {
        count++;
        Death death = (Death) value;

        deathFilters.getDeathCause().completeWithValue(death.getEvent().getDeathCause());
        deathFilters.getAutopsyPerformed().completeWithValue(death.getEvent().getAutopsyPerformed());
        deathFilters.getDesignation().completeWithValue(death.getEvent().getDesignation());
        deathFilters.getDeathRelatedToDisease().completeWithValue(death.getEvent().getDiseaseUnderInvestigationDeath());
        deathFilters.getHlt().completeWithValue(death.getEvent().getHlt());
        deathFilters.getLlt().completeWithValue(death.getEvent().getLlt());
        deathFilters.getPt().completeWithValue(death.getEvent().getPreferredTerm());
        deathFilters.getSoc().completeWithValue(death.getEvent().getSoc());
        deathFilters.getDaysFromFirstDoseToDeath().completeWithValue(death.getDaysFromFirstDoseToDeath());

        deathFilters.setMatchedItemsCount(count);
    }

    @Override
    public void combine(FilterSummaryStatistics<Death> other) {
        DeathFilters otherFilters = (DeathFilters) other.getFilters();

        deathFilters.getDeathCause().complete(otherFilters.getDeathCause());
        deathFilters.getAutopsyPerformed().complete(otherFilters.getAutopsyPerformed());
        deathFilters.getDesignation().complete(otherFilters.getDesignation());
        deathFilters.getDeathRelatedToDisease().complete(otherFilters.getDeathRelatedToDisease());
        deathFilters.getHlt().complete(otherFilters.getHlt());
        deathFilters.getLlt().complete(otherFilters.getLlt());
        deathFilters.getPt().complete(otherFilters.getPt());
        deathFilters.getSoc().complete(otherFilters.getSoc());
        deathFilters.getDaysFromFirstDoseToDeath().complete(otherFilters.getDaysFromFirstDoseToDeath());

        count += other.count();
        deathFilters.setMatchedItemsCount(count);
    }

    @Override
    public Filters<Death> getFilters() {
        return deathFilters;
    }

    @Override
    public int count() {
        return count;
    }

    @Override
    public FilterSummaryStatistics<Death> build() {
        return this;
    }

    @Override
    public FilterSummaryStatistics<Death> newStatistics() {
        return new DeathFilterSummaryStatistics();
    }
}
