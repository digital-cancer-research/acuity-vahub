package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.QtProlongationFilters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.QtProlongationFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.QtProlongation;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class QtProlongationFilterService extends AbstractEventFilterService<QtProlongation, Filters<QtProlongation>> {

    @Override
    protected QtProlongationFilters getAvailableFiltersImpl(FilterResult<QtProlongation> filteredResult) {
        Collection<QtProlongation> filteredPatientData = filteredResult.getFilteredResult();
        return filteredPatientData.stream()
                                  .collect(new FilterSummaryStatisticsCollector<>(
                                          QtProlongationFilterSummaryStatistics.class)).getFilters();
    }
}
