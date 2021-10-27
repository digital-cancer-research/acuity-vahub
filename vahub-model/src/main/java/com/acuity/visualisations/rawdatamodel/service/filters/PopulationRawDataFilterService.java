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
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.PopulationFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PopulationRawDataFilterService extends AbstractFilterService<Subject, PopulationFilters> {

    @TimeMe
    @Override
    public FilterResult<Subject> query(FilterQuery<Subject> filterQuery) {
        Validate.isTrue(filterQuery.isPopulationFilterQuery(), "FilterQuery needs to be of type population filter and not event filter");

        // pass no subjectIds in as its a pop filter query
        return queryImpl(filterQuery);
    }

    @TimeMe
    public FilterResult<Subject> getPopulationFilterResult(FilterQuery<Subject> filterQuery) {
        return queryImpl(filterQuery);
    }

    @Override
    protected PopulationFilters getAvailableFiltersImpl(FilterResult<Subject> filteredResult) {

        Collection<Subject> filteredSubjects = filteredResult.getFilteredResult();

        return filteredSubjects.stream().collect(new FilterSummaryStatisticsCollector<>(
                PopulationFilterSummaryStatistics.class)).getFilters();
    }
}
