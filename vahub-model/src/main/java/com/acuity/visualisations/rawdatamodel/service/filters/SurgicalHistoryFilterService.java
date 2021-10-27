package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.SurgicalHistoryFiltersSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurgicalHistory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class SurgicalHistoryFilterService extends AbstractEventFilterService<SurgicalHistory, Filters<SurgicalHistory>> {
    @Override
    protected Filters<SurgicalHistory> getAvailableFiltersImpl(FilterResult<SurgicalHistory> filteredResult) {
        Collection<SurgicalHistory> filteredLabEvents = filteredResult.getFilteredResult();

        return filteredLabEvents.parallelStream()
                .distinct()
                .collect(new FilterSummaryStatisticsCollector<>(SurgicalHistoryFiltersSummaryStatistics.class))
                .getFilters();
    }
}
