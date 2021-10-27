package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.AssessedTargetLesionFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class AssessedTargetLesionFilterService extends AbstractEventFilterService<AssessedTargetLesion,
        Filters<AssessedTargetLesion>> {
    @Override
    protected Filters<AssessedTargetLesion> getAvailableFiltersImpl(FilterResult<AssessedTargetLesion> filteredResult) {
        Collection<AssessedTargetLesion> filteredPatientData = filteredResult.getFilteredResult();

        return filteredPatientData.stream().collect(new FilterSummaryStatisticsCollector<>(
                AssessedTargetLesionFilterSummaryStatistics.class)).getFilters();
    }
}
