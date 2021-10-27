package com.acuity.visualisations.rest.model.request.death;

import com.acuity.visualisations.rawdatamodel.filters.DeathFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class DeathRequest extends DatasetsRequest {
    @NotNull
    private PopulationFilters populationFilters;

    @NotNull
    private DeathFilters deathFilters;
}
