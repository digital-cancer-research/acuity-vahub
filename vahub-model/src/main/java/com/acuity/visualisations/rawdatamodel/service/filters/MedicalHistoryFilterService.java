package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.MedicalHistoryFiltersSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.MedicalHistory;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class MedicalHistoryFilterService extends AbstractEventFilterService<MedicalHistory, Filters<MedicalHistory>> {
    @Override
    protected Filters<MedicalHistory> getAvailableFiltersImpl(FilterResult<MedicalHistory> filteredResult) {
        Collection<MedicalHistory> filteredLabEvents = filteredResult.getFilteredResult();

        return filteredLabEvents.parallelStream()
                .distinct()
                .collect(new FilterSummaryStatisticsCollector<>(MedicalHistoryFiltersSummaryStatistics.class))
                .getFilters();
    }
}
