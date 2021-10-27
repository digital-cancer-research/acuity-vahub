package com.acuity.visualisations.rest.model.request.nicotine;

import com.acuity.visualisations.rawdatamodel.filters.NicotineFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class NicotineRequest extends DatasetsRequest {
    @NotNull
    private PopulationFilters populationFilters;
    @NotNull
    private NicotineFilters nicotineFilters;
}
