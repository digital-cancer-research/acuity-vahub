package com.acuity.visualisations.rest.model.request.cvot.endpoint;

import com.acuity.visualisations.rawdatamodel.filters.CvotEndpointFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CvotEndpointRequest extends EventFilterRequestPopulationAware<CvotEndpointFilters> {

    @NotNull
    private CvotEndpointFilters cvotEndpointFilters;

    @Override
    public CvotEndpointFilters getEventFilters() {
        return cvotEndpointFilters;
    }
}
