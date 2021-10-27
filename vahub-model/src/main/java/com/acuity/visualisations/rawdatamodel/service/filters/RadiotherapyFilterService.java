package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.RadiotherapyFilters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.RadiotherapyFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class RadiotherapyFilterService extends AbstractEventFilterService<Radiotherapy, Filters<Radiotherapy>> {
    @Override
    protected RadiotherapyFilters getAvailableFiltersImpl(FilterResult<Radiotherapy> filteredResult) {
        Collection<Radiotherapy> filteredRadiotherapies = filteredResult.getFilteredResult();

        return filteredRadiotherapies.stream().collect(new FilterSummaryStatisticsCollector<>(
                RadiotherapyFilterSummaryStatistics.class)).getFilters();
    }
}
