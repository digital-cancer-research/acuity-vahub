package com.acuity.visualisations.rawdatamodel.filters;

import com.acuity.visualisations.rawdatamodel.service.filters.AbstractEventFilterService;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.RenalFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class RenalFilterService extends AbstractEventFilterService<Renal, Filters<Renal>> {
    @Override
    protected Filters<Renal> getAvailableFiltersImpl(FilterResult<Renal> filteredResult) {
        Collection<Renal> filteredRenalEvents = filteredResult.getFilteredResult();

        return filteredRenalEvents.parallelStream()
                .collect(new FilterSummaryStatisticsCollector<>(RenalFilterSummaryStatistics.class))
                .getFilters();
    }

}
