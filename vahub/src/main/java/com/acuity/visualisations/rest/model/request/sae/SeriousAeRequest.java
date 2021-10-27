package com.acuity.visualisations.rest.model.request.sae;

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SeriousAeFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SeriousAeRequest extends DatasetsRequest {
    @NotNull
    private PopulationFilters populationFilters;

    @NotNull
    private SeriousAeFilters seriousAeFilters;
}
