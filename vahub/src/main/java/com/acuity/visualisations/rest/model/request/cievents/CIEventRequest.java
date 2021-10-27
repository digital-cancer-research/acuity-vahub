package com.acuity.visualisations.rest.model.request.cievents;

import com.acuity.visualisations.rawdatamodel.filters.CIEventFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CIEventRequest extends EventFilterRequestPopulationAware<CIEventFilters> {

    @NotNull
    private CIEventFilters cieventsFilters;

    @Override
    public CIEventFilters getEventFilters() {
        return cieventsFilters;
    }
}
