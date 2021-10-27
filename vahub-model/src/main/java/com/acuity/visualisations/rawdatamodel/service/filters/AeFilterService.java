package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.AeFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AeFilterService extends AbstractEventFilterService<Ae, Filters<Ae>> {

    @Override
    protected AeFilters getAvailableFiltersImpl(FilterResult<Ae> filteredResult) {
        Collection<Ae> filteredAeEvents = filteredResult.getFilteredResult();

        return filteredAeEvents.stream().collect(new FilterSummaryStatisticsCollector<>(
                AeFilterSummaryStatistics.class)).getFilters();
    }
}
