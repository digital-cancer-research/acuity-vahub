package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.VitalFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class VitalFilterService extends AbstractEventFilterService<Vital, Filters<Vital>> {

    @Override
    protected VitalFilters getAvailableFiltersImpl(FilterResult<Vital> filteredResult) {
        Collection<Vital> filteredVitalEvents = filteredResult.getFilteredResult();

        return filteredVitalEvents.stream().collect(new FilterSummaryStatisticsCollector<>(
                VitalFilterSummaryStatistics.class)).getFilters();
    }
}
