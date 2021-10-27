package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.AssessedNonTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedNonTargetLesion;
import org.springframework.stereotype.Service;

@Service
public class AssessedNonTargetLesionFilterService extends AbstractEventFilterService<AssessedNonTargetLesion,
        Filters<AssessedNonTargetLesion>> {
    @Override
    protected Filters<AssessedNonTargetLesion> getAvailableFiltersImpl(FilterResult<AssessedNonTargetLesion> filteredResult) {
        return AssessedNonTargetLesionFilters.empty();
    }
}
