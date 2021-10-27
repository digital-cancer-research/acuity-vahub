package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.LiverRiskFiltersSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverRisk;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class LiverRiskFilterService extends AbstractEventFilterService<LiverRisk, Filters<LiverRisk>> {
    @Override
    protected Filters<LiverRisk> getAvailableFiltersImpl(FilterResult<LiverRisk> filteredResult) {
        Collection<LiverRisk> filteredLabEvents = filteredResult.getFilteredResult();

        return filteredLabEvents.parallelStream()
                .distinct()
                .collect(new FilterSummaryStatisticsCollector<>(LiverRiskFiltersSummaryStatistics.class))
                .getFilters();
    }
}
