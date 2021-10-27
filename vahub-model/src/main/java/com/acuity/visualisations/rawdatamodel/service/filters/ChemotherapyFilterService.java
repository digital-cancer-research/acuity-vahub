package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.ChemotherapyFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.ChemotherapyFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ChemotherapyFilterService extends AbstractEventFilterService<Chemotherapy, Filters<Chemotherapy>> {
    @Override
    protected ChemotherapyFilters getAvailableFiltersImpl(FilterResult<Chemotherapy> filteredResult) {
        Collection<Chemotherapy> filteredChemotherapies = filteredResult.getFilteredResult();

        return filteredChemotherapies.stream().collect(new FilterSummaryStatisticsCollector<>(
                ChemotherapyFilterSummaryStatistics.class)).getFilters();
    }
}
