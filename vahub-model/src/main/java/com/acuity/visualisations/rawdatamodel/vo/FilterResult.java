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

package com.acuity.visualisations.rawdatamodel.vo;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

@EqualsAndHashCode
public class FilterResult<T> {

    @Getter
    private FilterQuery<T> filterQuery;
    @Getter
    private Collection<T> allEvents = new ArrayList<>();
    @Getter
    private Collection<T> filteredEvents = new ArrayList<>();
    @Getter
    private FilterResult<Subject> populationFilterResult;

    public FilterResult(FilterQuery<T> filterQuery) {
        this.filterQuery = filterQuery;
    }

    public FilterResult<T> withPopulationFilteredResults(FilterResult<Subject> populationFilterResult) {
        if (!filterQuery.isEventFilterQuery()) {
            throw new IllegalStateException("populationFilters hasnt been set, cannot set the results");
        }
        this.populationFilterResult = populationFilterResult;
        return this;
    }

    public FilterResult<T> withResults(Collection<T> allEvents, Collection<T> justFilteredEvents) {
        this.allEvents = allEvents;
        this.filteredEvents = justFilteredEvents;
        return this;
    }

    public Collection<T> getFilteredResult() {
        return filteredEvents;
    }

    public int size() {
        return getFilteredResult().size();
    }

    public Stream<T> stream() {
        return getFilteredResult().stream();
    }

    public Stream<T> parallelStream() {
        return getFilteredResult().parallelStream();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).
                append("query", filterQuery).
                append("allEvents size", allEvents.size());

        builder.append("filteredEvents size", filteredEvents.size());

        return builder.toString();
    }
}
