package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.NicotineFiltersSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Nicotine;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class NicotineFilterService extends AbstractEventFilterService<Nicotine, Filters<Nicotine>> {
    @Override
    protected Filters<Nicotine> getAvailableFiltersImpl(FilterResult<Nicotine> filteredResult) {
        Collection<Nicotine> filteredEvents = filteredResult.getFilteredResult();

        return filteredEvents.parallelStream()
                .distinct()
                .collect(new FilterSummaryStatisticsCollector<>(NicotineFiltersSummaryStatistics.class))
                .getFilters();
    }
}
