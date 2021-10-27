package com.acuity.visualisations.rest.model.request.cerebrovascular;

import com.acuity.visualisations.rawdatamodel.filters.CerebrovascularFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CerebrovascularRequest extends EventFilterRequestPopulationAware<CerebrovascularFilters> {

    @NotNull
    private CerebrovascularFilters cerebrovascularFilters;

    @Override
    public CerebrovascularFilters getEventFilters() {
        return cerebrovascularFilters;
    }
}
