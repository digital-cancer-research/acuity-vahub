package com.acuity.visualisations.rest.model.request.respiratory.exacerbation;

import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExacerbationRequest extends DatasetsRequest {
    @NotNull
    private PopulationFilters populationFilters;
    @NotNull
    private ExacerbationFilters exacerbationFilters;
}
