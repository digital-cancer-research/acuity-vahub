package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.CerebrovascularFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.CerebrovascularFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class CerebrovascularFilterService extends AbstractEventFilterService<Cerebrovascular, Filters<Cerebrovascular>> {

    @Override
    protected CerebrovascularFilters getAvailableFiltersImpl(FilterResult<Cerebrovascular> filteredResult) {
        Collection<Cerebrovascular> filteredCerebrovasculars = filteredResult.getFilteredResult();

        return filteredCerebrovasculars.stream().collect(new FilterSummaryStatisticsCollector<>(
                CerebrovascularFilterSummaryStatistics.class)).getFilters();
    }
}
