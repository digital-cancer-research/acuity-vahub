package com.acuity.visualisations.rest.model.request.tumour;

import com.acuity.visualisations.rawdatamodel.filters.TherapyFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class TumourTherapyRequest extends EventFilterRequestPopulationAware<TherapyFilters> {
    @NotNull
    private TherapyFilters therapyFilters;

    @Override
    public TherapyFilters getEventFilters() {
        return therapyFilters;
    }
}
