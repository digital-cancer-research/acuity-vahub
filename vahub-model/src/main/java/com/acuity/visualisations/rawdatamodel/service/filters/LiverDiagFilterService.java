package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.LiverDiagFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverDiag;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LiverDiagFilterService extends AbstractEventFilterService<LiverDiag, Filters<LiverDiag>> {
    @Override
    protected Filters<LiverDiag> getAvailableFiltersImpl(FilterResult<LiverDiag> filteredResult) {
        Collection<LiverDiag> filteredLiverDiagEvents = filteredResult.getFilteredResult();

        return filteredLiverDiagEvents.stream()
                .collect(new FilterSummaryStatisticsCollector<>(LiverDiagFilterSummaryStatistics.class))
                .getFilters();
    }
}
