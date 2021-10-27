package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.ExacerbationFiltersSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ExacerbationFilterService extends AbstractEventFilterService<Exacerbation, Filters<Exacerbation>> {
    @Override
    protected Filters<Exacerbation> getAvailableFiltersImpl(FilterResult<Exacerbation> filteredResult) {
        Collection<Exacerbation> filteredLabEvents = filteredResult.getFilteredResult();

        return filteredLabEvents.stream()
                .distinct()
                .collect(new FilterSummaryStatisticsCollector<>(ExacerbationFiltersSummaryStatistics.class))
                .getFilters();
    }
}
