package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.CIEventFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.CIEventFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CIEventFilterService extends AbstractEventFilterService<CIEvent, Filters<CIEvent>> {

    @Override
    protected CIEventFilters getAvailableFiltersImpl(FilterResult<CIEvent> filteredResult) {
        Collection<CIEvent> filteredCIEvents = filteredResult.getFilteredResult();

        return filteredCIEvents.stream().collect(new FilterSummaryStatisticsCollector<>(
                CIEventFilterSummaryStatistics.class)).getFilters();
    }
}
