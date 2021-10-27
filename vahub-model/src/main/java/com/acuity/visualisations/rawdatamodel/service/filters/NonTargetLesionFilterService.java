package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.NonTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.NonTargetLesion;
import org.springframework.stereotype.Service;

@Service
public class NonTargetLesionFilterService extends AbstractEventFilterService<NonTargetLesion, Filters<NonTargetLesion>> {
    @Override
    protected NonTargetLesionFilters getAvailableFiltersImpl(FilterResult<NonTargetLesion> filteredResult) {
        return NonTargetLesionFilters.empty();
    }
}
