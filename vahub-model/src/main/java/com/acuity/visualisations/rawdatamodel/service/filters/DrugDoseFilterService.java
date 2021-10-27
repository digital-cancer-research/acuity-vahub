package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.DrugDoseFiltersSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DrugDoseFilterService extends AbstractEventFilterService<DrugDose, Filters<DrugDose>> {
    @Override
    protected Filters<DrugDose> getAvailableFiltersImpl(FilterResult<DrugDose> filteredResult) {
        Collection<DrugDose> filteredEvents = filteredResult.getFilteredResult();

        return filteredEvents.stream()
                .distinct()
                .collect(new FilterSummaryStatisticsCollector<>(DrugDoseFiltersSummaryStatistics.class))
                .getFilters();
    }
}
