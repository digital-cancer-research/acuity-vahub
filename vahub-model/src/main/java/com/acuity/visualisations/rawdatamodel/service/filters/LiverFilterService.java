package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.LiverFilters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.statistics.filters.LiverFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class LiverFilterService extends AbstractEventFilterService<Liver, Filters<Liver>> {

    @Override
    protected LiverFilters getAvailableFiltersImpl(FilterResult<Liver> filteredResult) {
        Collection<Liver> filteredLiverEvents = filteredResult.getFilteredResult();

        return filteredLiverEvents.parallelStream()
                .collect(new FilterSummaryStatisticsCollector<>(LiverFilterSummaryStatistics.class))
                .getFilters();
    }

}
