package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.SecondTimeOfProgressionFilters;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SecondTimeOfProgression;
import org.springframework.stereotype.Service;

@Service
public class SecondTimeOfProgressionFilterService extends AbstractEventFilterService<SecondTimeOfProgression, Filters<SecondTimeOfProgression>> {
    @Override
    protected Filters<SecondTimeOfProgression> getAvailableFiltersImpl(FilterResult<SecondTimeOfProgression> filteredResult) {
        return SecondTimeOfProgressionFilters.empty();
    }
}
