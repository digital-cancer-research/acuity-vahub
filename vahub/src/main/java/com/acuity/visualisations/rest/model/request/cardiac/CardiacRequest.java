package com.acuity.visualisations.rest.model.request.cardiac;

import com.acuity.visualisations.rawdatamodel.filters.CardiacFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import com.esotericsoftware.kryo.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CardiacRequest extends DatasetsRequest {

    @NotNull
    private PopulationFilters populationFilters;

    @NotNull
    private CardiacFilters cardiacFilters;
}

