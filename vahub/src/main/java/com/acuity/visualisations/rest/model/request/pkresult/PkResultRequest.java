package com.acuity.visualisations.rest.model.request.pkresult;

import com.acuity.visualisations.rawdatamodel.filters.PkResultFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class PkResultRequest extends EventFilterRequestPopulationAware<PkResultFilters> {
    @NotNull
    private PkResultFilters pkResultFilters;

    @Override
    public PkResultFilters getEventFilters() {
        return pkResultFilters;
    }
}
