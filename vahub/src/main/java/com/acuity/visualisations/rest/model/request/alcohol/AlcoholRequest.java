package com.acuity.visualisations.rest.model.request.alcohol;

import com.acuity.visualisations.rawdatamodel.filters.AlcoholFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AlcoholRequest extends DatasetsRequest {
    @NotNull
    private PopulationFilters populationFilters;

    @NotNull
    private AlcoholFilters alcoholFilters;
}
