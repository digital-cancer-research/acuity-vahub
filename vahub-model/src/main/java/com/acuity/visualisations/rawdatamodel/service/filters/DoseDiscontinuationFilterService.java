package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.DoseDiscFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class DoseDiscontinuationFilterService extends AbstractEventFilterService<DoseDisc, Filters<DoseDisc>> {
    @Override
    protected Filters<DoseDisc> getAvailableFiltersImpl(FilterResult<DoseDisc> filteredResult) {
        Collection<DoseDisc> filteredDoseDiscEvents = filteredResult.getFilteredResult();

        return filteredDoseDiscEvents.parallelStream()
                .distinct()
                .collect(new FilterSummaryStatisticsCollector<>(DoseDiscFilterSummaryStatistics.class))
                .getFilters();
    }
}
