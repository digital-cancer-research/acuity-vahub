package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.TargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.TargetLesion;
import org.springframework.stereotype.Service;

@Service
public class TargetLesionFilterRawDataService extends AbstractEventFilterService<TargetLesion,
        Filters<TargetLesion>> {
    @Override
    protected Filters<TargetLesion> getAvailableFiltersImpl(FilterResult<TargetLesion> filteredResult) {
        return TargetLesionFilters.empty();
    }
}
