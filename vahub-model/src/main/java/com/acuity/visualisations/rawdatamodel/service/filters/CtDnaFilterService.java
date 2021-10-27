package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.CtDnaFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.CtDnaFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CtDnaFilterService extends AbstractEventFilterService<CtDna, Filters<CtDna>> {

    @Override
    protected CtDnaFilters getAvailableFiltersImpl(FilterResult<CtDna> filteredResult) {
        Collection<CtDna> filteredPatientData = filteredResult.getFilteredResult();

        return filteredPatientData.stream().collect(new FilterSummaryStatisticsCollector<>(
                CtDnaFilterSummaryStatistics.class)).getFilters();
    }
}

