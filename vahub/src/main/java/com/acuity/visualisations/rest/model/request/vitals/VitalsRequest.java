package com.acuity.visualisations.rest.model.request.vitals;

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import com.esotericsoftware.kryo.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class VitalsRequest extends DatasetsRequest {

    @NotNull
    private PopulationFilters populationFilters;
    @NotNull
    private VitalFilters vitalsFilters;
}

