package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.ConmedFiltersSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ConmedFilterService extends AbstractEventFilterService<Conmed, Filters<Conmed>> {

    @Override
    protected Filters<Conmed> getAvailableFiltersImpl(FilterResult<Conmed> filteredResult) {
        Collection<Conmed> filteredLabEvents = filteredResult.getFilteredResult();

        return filteredLabEvents.parallelStream()
                .distinct()
                .collect(new FilterSummaryStatisticsCollector<>(ConmedFiltersSummaryStatistics.class))
                .getFilters();
    }
}
