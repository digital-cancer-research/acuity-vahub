package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PkResultFilters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.PkResultFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PkResultFilterService extends AbstractEventFilterService<PkResult, Filters<PkResult>> {

    @Override
    protected PkResultFilters getAvailableFiltersImpl(FilterResult<PkResult> filteredResult) {
        Collection<PkResult> filteredPatientData = filteredResult.getFilteredResult();

        return filteredPatientData.stream().collect(new FilterSummaryStatisticsCollector<>(
                PkResultFilterSummaryStatistics.class)).getFilters();
    }
}

