package com.acuity.visualisations.rest.model.request.tumour;

import com.acuity.visualisations.rawdatamodel.filters.AssessedTargetLesionFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class TumourRequest extends EventFilterRequestPopulationAware<AssessedTargetLesionFilters> {

    @NotNull
    private AssessedTargetLesionFilters tumourFilters;

    @Override
    public AssessedTargetLesionFilters getEventFilters() {
        return tumourFilters;
    }
}
