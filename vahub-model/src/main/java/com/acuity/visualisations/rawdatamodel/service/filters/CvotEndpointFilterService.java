package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.CvotEndpointFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.CvotEndpointFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CvotEndpointFilterService extends AbstractEventFilterService<CvotEndpoint, Filters<CvotEndpoint>> {

    @Override
    protected CvotEndpointFilters getAvailableFiltersImpl(FilterResult<CvotEndpoint> filteredResult) {
        Collection<CvotEndpoint> filteredExposure = filteredResult.getFilteredResult();

        return filteredExposure.stream().collect(new FilterSummaryStatisticsCollector<>(
                CvotEndpointFilterSummaryStatistics.class)).getFilters();
    }
}

