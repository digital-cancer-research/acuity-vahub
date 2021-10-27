package com.acuity.visualisations.rest.model.request.ctdna;

import com.acuity.visualisations.rawdatamodel.filters.CtDnaFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CtDnaRequest extends EventFilterRequestPopulationAware<CtDnaFilters> {
    @NotNull
    private CtDnaFilters ctDnaFilters;

    @Override
    public CtDnaFilters getEventFilters() {
        return ctDnaFilters;
    }
}
