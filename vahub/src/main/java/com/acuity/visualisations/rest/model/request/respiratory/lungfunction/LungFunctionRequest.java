package com.acuity.visualisations.rest.model.request.respiratory.lungfunction;

import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * Request for lung function charts
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LungFunctionRequest extends DatasetsRequest {
    @NotNull
    private PopulationFilters populationFilters;

    @NotNull
    private LungFunctionFilters lungFunctionFilters;
}

