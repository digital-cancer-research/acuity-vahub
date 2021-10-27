package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.ExposureFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.ExposureFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ExposureFilterService extends AbstractEventFilterService<Exposure, Filters<Exposure>> {

    @Override
    protected ExposureFilters getAvailableFiltersImpl(FilterResult<Exposure> filteredResult) {
        Collection<Exposure> filteredExposure = filteredResult.getFilteredResult();

        return filteredExposure.stream().collect(new FilterSummaryStatisticsCollector<>(
                ExposureFilterSummaryStatistics.class)).getFilters();
    }
}
