package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.PathologyFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Pathology;
import org.springframework.stereotype.Service;

@Service
public class PathologyFilterService extends AbstractEventFilterService<Pathology, Filters<Pathology>> {
    @Override
    protected Filters<Pathology> getAvailableFiltersImpl(FilterResult<Pathology> filteredResult) {

        return filteredResult.stream().collect(new FilterSummaryStatisticsCollector<>(
                PathologyFilterSummaryStatistics.class)).getFilters();
    }
}
