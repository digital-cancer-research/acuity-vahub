package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.DeathFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Death;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DeathFilterService extends AbstractEventFilterService<Death, Filters<Death>> {
    @Override
    protected Filters<Death> getAvailableFiltersImpl(FilterResult<Death> filteredResult) {
        Collection<Death> filteredDeaths = filteredResult.getFilteredResult();

        return filteredDeaths.stream()
                .collect(new FilterSummaryStatisticsCollector<>(DeathFilterSummaryStatistics.class))
                .getFilters();
    }
}
