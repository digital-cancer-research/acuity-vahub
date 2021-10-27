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
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

/**
 * Base class for events filters, ie Labs and Vitals. Not population
 */
public abstract class AbstractEventFilterService<T, V extends Filters<T>> extends AbstractFilterService<T, V> {

    @Getter(AccessLevel.PROTECTED)
    @Autowired
    private PopulationRawDataFilterService subjectService;

    @TimeMe
    @Override
    public FilterResult<T> query(FilterQuery<T> filterQuery) {
        Validate.isTrue(filterQuery.isEventFilterQuery(), "FilterQuery needs to be of type event filter and not population filter");
        
        FilterResult<Subject> filteredSubjects = subjectService.getPopulationFilterResult(filterQuery.getPopulationFilterQuery());
        return queryImpl(filterQuery, filteredSubjects);
    }

    public V getAvailableFilters(Collection<T> events, Filters<T> eventFilters, Collection<Subject> population, PopulationFilters populationFilters) {
        return getAvailableFilters(new FilterQuery<>(events, eventFilters, population, populationFilters));
    }
}
