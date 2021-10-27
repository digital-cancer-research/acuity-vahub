package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.SurvivalStatusFilters;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurvivalStatus;
import org.springframework.stereotype.Service;

@Service
public class SurvivalStatusFilterService extends AbstractEventFilterService<SurvivalStatus, Filters<SurvivalStatus>> {
    @Override
    protected Filters<SurvivalStatus> getAvailableFiltersImpl(FilterResult<SurvivalStatus> filteredResult) {
        return SurvivalStatusFilters.empty();
    }
}
