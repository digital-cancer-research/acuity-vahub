package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.SubjectExtFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectExt;
import org.springframework.stereotype.Service;

@Service
public class SubjectExtFilterService extends AbstractEventFilterService<SubjectExt, Filters<SubjectExt>> {
    @Override
    protected Filters<SubjectExt> getAvailableFiltersImpl(FilterResult<SubjectExt> filteredResult) {
        return filteredResult.stream().collect(new FilterSummaryStatisticsCollector<>(
                SubjectExtFilterSummaryStatistics.class)).getFilters();
    }
}
