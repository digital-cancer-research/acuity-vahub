package com.acuity.visualisations.rest.model.request.liverrisk;

import com.acuity.visualisations.rawdatamodel.filters.LiverRiskFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LiverRiskRequest extends DatasetsRequest {
    @NotNull
    private PopulationFilters populationFilters;
    @NotNull
    private LiverRiskFilters liverRiskFilters;
}
