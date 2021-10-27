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
