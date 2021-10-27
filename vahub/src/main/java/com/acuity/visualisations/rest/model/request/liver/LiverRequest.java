package com.acuity.visualisations.rest.model.request.liver;

import com.acuity.visualisations.rawdatamodel.filters.LiverFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LiverRequest extends EventFilterRequestPopulationAware<LiverFilters> {
    private LiverFilters liverFilters;

    @Override
    public LiverFilters getEventFilters() {
        return liverFilters;
    }
}
