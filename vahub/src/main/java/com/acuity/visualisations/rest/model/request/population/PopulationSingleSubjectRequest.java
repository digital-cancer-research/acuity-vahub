package com.acuity.visualisations.rest.model.request.population;

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import com.esotericsoftware.kryo.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class PopulationSingleSubjectRequest extends DatasetsRequest {
    @NotNull
    private String subjectId;
    @NotNull
    private PopulationFilters populationFilters;
}
