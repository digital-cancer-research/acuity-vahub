package com.acuity.visualisations.rest.model.request.labs;

import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class LabsRequest extends EventFilterRequestPopulationAware<LabFilters> {

    @NotNull
    private LabFilters labsFilters;

    @Override
    public LabFilters getEventFilters() {
        return labsFilters;
    }
}
