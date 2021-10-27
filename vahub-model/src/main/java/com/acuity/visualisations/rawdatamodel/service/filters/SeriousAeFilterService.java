package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.SeriousAeFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SeriousAe;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class SeriousAeFilterService extends AbstractEventFilterService<SeriousAe, Filters<SeriousAe>> {
    @Override
    protected Filters<SeriousAe> getAvailableFiltersImpl(FilterResult<SeriousAe> filteredResult) {
        Collection<SeriousAe> filteredSeriousAeEvents = filteredResult.getFilteredResult();

        return filteredSeriousAeEvents.stream()
                .collect(new FilterSummaryStatisticsCollector<>(SeriousAeFilterSummaryStatistics.class))
                .getFilters();
    }
}
