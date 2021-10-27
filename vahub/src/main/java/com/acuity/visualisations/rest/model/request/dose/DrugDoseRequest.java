package com.acuity.visualisations.rest.model.request.dose;

import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class DrugDoseRequest extends DatasetsRequest {
    @NotNull
    private PopulationFilters populationFilters;

    @NotNull
    private DrugDoseFilters doseFilters;
}
