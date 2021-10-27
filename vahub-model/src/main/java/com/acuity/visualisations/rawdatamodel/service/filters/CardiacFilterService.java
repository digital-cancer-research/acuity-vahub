package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.CardiacFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.CardiacFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CardiacFilterService extends AbstractEventFilterService<Cardiac, Filters<Cardiac>> {

    @Override
    protected CardiacFilters getAvailableFiltersImpl(FilterResult<Cardiac> filteredResult) {
        Collection<Cardiac> filteredCardiacEvents = filteredResult.getFilteredResult();

        return filteredCardiacEvents.parallelStream()
                .collect(new FilterSummaryStatisticsCollector<>(CardiacFilterSummaryStatistics.class))
                .getFilters();
    }
}
