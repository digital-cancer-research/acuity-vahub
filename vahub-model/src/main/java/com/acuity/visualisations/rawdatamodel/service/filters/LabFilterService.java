package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.LabFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LabFilterService extends AbstractEventFilterService<Lab, Filters<Lab>> {
    @Override
    protected LabFilters getAvailableFiltersImpl(FilterResult<Lab> filteredResult) {
        Collection<Lab> filteredLabEvents = filteredResult.getFilteredResult();

        return filteredLabEvents.stream()
                .distinct()
                .collect(new FilterSummaryStatisticsCollector<>(LabFilterSummaryStatistics.class))
                .getFilters();
    }
}
