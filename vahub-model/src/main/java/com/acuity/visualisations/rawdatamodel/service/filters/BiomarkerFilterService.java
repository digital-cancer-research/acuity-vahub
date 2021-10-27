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

import com.acuity.visualisations.rawdatamodel.filters.BiomarkerFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.BiomarkerFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import org.apache.commons.lang3.Validate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.BiomarkerGroupByOptions.GenePercentage.getGenePercentageMap;

@Service
public class BiomarkerFilterService extends AbstractEventFilterService<Biomarker, Filters<Biomarker>> {

    @Override
    public BiomarkerFilters getAvailableFilters(FilterQuery<Biomarker> filterQuery) {

        FilterResult<Biomarker> filtered = query(filterQuery);
        BiomarkerFilters availableFilters = getAvailableFiltersImpl(filtered);
        getGenePercentageMap(filtered).values().forEach(percent -> availableFilters.getGenePercentage().completeWithValue(percent));
        return availableFilters;
    }

    @Override
    public FilterResult<Biomarker> query(FilterQuery<Biomarker> filterQuery) {
        Validate.isTrue(filterQuery.isEventFilterQuery(), "FilterQuery needs to be of type event filter and not population filter");

        FilterResult<Subject> filteredSubjects = getSubjectService().getPopulationFilterResult(filterQuery.getPopulationFilterQuery());
        FilterResult<Biomarker> filtered = queryImpl(filterQuery, filteredSubjects);

        Map<String, Integer> genePercentage = getGenePercentageMap(filtered);
        BiomarkerFilters bmFilters = (BiomarkerFilters) filterQuery.getFilters();
        filtered.withResults(filtered.getAllEvents(), filtered.stream().filter(b -> {
            int percent = genePercentage.get(b.getEvent().getGene());
            final Integer from = bmFilters.getGenePercentage().getFrom();
            final Integer to = bmFilters.getGenePercentage().getTo();
            return (from == null || from.compareTo(percent) <= 0)
                    && (to == null || to.compareTo(percent) >= 0);
        }).collect(Collectors.toList()));

        return filtered;
    }

    @Override
    protected BiomarkerFilters getAvailableFiltersImpl(FilterResult<Biomarker> filteredResult) {
        Collection<Biomarker> filteredBiomarkers = filteredResult.getFilteredResult();

        return filteredBiomarkers.stream().collect(new FilterSummaryStatisticsCollector<>(
                BiomarkerFilterSummaryStatistics.class)).getFilters();
    }
}
