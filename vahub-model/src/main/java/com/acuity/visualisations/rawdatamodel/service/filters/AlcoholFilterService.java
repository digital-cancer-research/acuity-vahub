package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.AlcoholFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Alcohol;
import java.util.Collection;
import org.springframework.stereotype.Service;

@Service
public class AlcoholFilterService extends AbstractEventFilterService<Alcohol, Filters<Alcohol>> {
    @Override
    protected Filters<Alcohol> getAvailableFiltersImpl(FilterResult<Alcohol> filteredResult) {
        Collection<Alcohol> filteredAlcohols = filteredResult.getFilteredResult();

        return filteredAlcohols.stream()
                .collect(new FilterSummaryStatisticsCollector<>(AlcoholFilterSummaryStatistics.class))
                .getFilters();
    }
}
