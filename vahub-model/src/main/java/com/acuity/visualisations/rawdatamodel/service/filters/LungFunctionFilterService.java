package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.LungFunctionFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LungFunctionFilterService extends AbstractEventFilterService<LungFunction, Filters<LungFunction>> {
    @Override
    protected LungFunctionFilters getAvailableFiltersImpl(FilterResult<LungFunction> filteredResult) {
        Collection<LungFunction> filteredEvents = filteredResult.getFilteredResult();

        return filteredEvents.parallelStream()
                .collect(new FilterSummaryStatisticsCollector<>(LungFunctionFilterSummaryStatistics.class))
                .getFilters();
    }
}
