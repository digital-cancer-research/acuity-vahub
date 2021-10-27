package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.filters.DiseaseExtentFilterSummaryStatistics;
import com.acuity.visualisations.rawdatamodel.statistics.filters.FilterSummaryStatisticsCollector;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DiseaseExtent;
import org.springframework.stereotype.Service;

@Service
public class DiseaseExtentFilterService extends AbstractEventFilterService<DiseaseExtent, Filters<DiseaseExtent>> {
    @Override
    protected Filters<DiseaseExtent> getAvailableFiltersImpl(FilterResult<DiseaseExtent> filteredResult) {
        return filteredResult.stream().collect(new FilterSummaryStatisticsCollector<>(
                DiseaseExtentFilterSummaryStatistics.class)).getFilters();
    }
}
