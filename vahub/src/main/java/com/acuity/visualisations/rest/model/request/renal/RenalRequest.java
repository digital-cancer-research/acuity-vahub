package com.acuity.visualisations.rest.model.request.renal;

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RenalFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class RenalRequest extends DatasetsRequest {

    @NotNull
    private PopulationFilters populationFilters;

    @NotNull
    private RenalFilters renalFilters;
}
