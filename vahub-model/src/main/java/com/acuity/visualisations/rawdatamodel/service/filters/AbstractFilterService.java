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

package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.common.aspect.TimeMe;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.util.FilterWrapper;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.query.Query;
import com.googlecode.cqengine.resultset.ResultSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toSet;

/**
 * Base class for all filter services.
 * <p>
 * Note: AbstractFilterService and AbstractEventFilterService need to be refactored. The PopulationFilterService exposes methods from AbstractFilterService
 * which it cant use, so its guarded by the first if statement in AbstractFilterService.query.
 */
@TimeMe
public abstract class AbstractFilterService<T, F extends Filters<T>> {

    @TimeMe
    public F getAvailableFilters(FilterQuery<T> filterQuery) {

        FilterResult<T> filteredResults = query(filterQuery);
        return getAvailableFilters(filteredResults);
    }

    @TimeMe
    public F getAvailableFilters(FilterResult<T> filteredResults) {
        return getAvailableFiltersImpl(filteredResults);
    }

    public <T> IndexedCollection<T> wrap(Collection<T> events) {
        return FilterWrapper.wrap(events);
    }

    protected abstract F getAvailableFiltersImpl(FilterResult<T> filteredResult);

    public abstract FilterResult<T> query(FilterQuery<T> filterQuery);

    /*
     * General query for pop and event filters. Maybe needs to be done better.
     *
     * <code>
     * Either:
     * pass subjectIds (pop filter already applied to get subjectIds) and EventFilter, ie LabFilter
     * or
     * pass empty subjectIds (no pop filter applied) and PopulationFilter *
     * </code>
     */
    @TimeMe
    protected FilterResult<T> queryImpl(FilterQuery<T> filterQuery) {
        
        FilterResult<Subject> emptyPopulation = new FilterResult<>(
                new FilterQuery<Subject>(new ArrayList<>(), PopulationFilters.empty()));
        emptyPopulation.withResults(new ArrayList<>(), new ArrayList<>());
        
        return queryImpl(filterQuery, emptyPopulation);
    }

    @TimeMe
    protected FilterResult<T> queryImpl(FilterQuery<T> filterQuery, FilterResult<Subject> populationFilterResult) {

        FilterResult<T> filteredResult = new FilterResult<>(filterQuery).withPopulationFilteredResults(populationFilterResult);

        IndexedCollection<T> wrappedAllEvents = wrap(filterQuery.getEvents());

        if (!filterQuery.isPopulationFilterQuery() && populationFilterResult.getFilteredResult().isEmpty()) {
            //if population is empty it's obviously empty result, no need to query
            filteredResult.withResults(filterQuery.getEvents(), Collections.emptyList());
        } else {
            Query<T> justFilteredQuery = filterQuery.getFilters().getQuery(
                    populationFilterResult.getFilteredResult().stream().map(Subject::getSubjectId).collect(toSet())); // if subjectIds null it ignores it

            try (ResultSet<T> justFilteredResultSet = wrappedAllEvents.retrieve(justFilteredQuery)) {

                List<T> justFilteredResult = newArrayList(justFilteredResultSet);
                List<T> allEvents = newArrayList(wrappedAllEvents);

                filteredResult.withResults(allEvents, justFilteredResult);
            }
        }

        return filteredResult;
    }
}
