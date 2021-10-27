package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.AssessmentFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.AssessmentFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Assessment;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AssessmentFilterService extends AbstractEventFilterService<Assessment, Filters<Assessment>> {

    @Override
    protected AssessmentFilters getAvailableFiltersImpl(FilterResult<Assessment> filteredResult) {
        Collection<Assessment> filteredExposure = filteredResult.getFilteredResult();

        return filteredExposure.stream().collect(new FilterSummaryStatisticsCollector<>(
                AssessmentFilterSummaryStatistics.class)).getFilters();
    }
}

