package com.acuity.visualisations.rest.model.request.biomarkers;

import com.acuity.visualisations.rawdatamodel.filters.BiomarkerFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class BiomarkerRequest extends EventFilterRequestPopulationAware<BiomarkerFilters> {
    @NotNull
    private BiomarkerFilters biomarkerFilters;

    @Override
    public BiomarkerFilters getEventFilters() {
        return biomarkerFilters;
    }
}
