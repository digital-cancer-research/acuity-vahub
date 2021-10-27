package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PatientDataFilters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.PatientDataFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class PatientDataFilterService extends AbstractEventFilterService<PatientData, Filters<PatientData>> {

    @Override
    protected PatientDataFilters getAvailableFiltersImpl(FilterResult<PatientData> filteredResult) {
        Collection<PatientData> filteredPatientData = filteredResult.getFilteredResult();

        return filteredPatientData.stream().collect(new FilterSummaryStatisticsCollector<>(
                PatientDataFilterSummaryStatistics.class)).getFilters();
    }
}

