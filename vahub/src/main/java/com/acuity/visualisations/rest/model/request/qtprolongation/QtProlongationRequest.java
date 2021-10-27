package com.acuity.visualisations.rest.model.request.qtprolongation;

import com.acuity.visualisations.rawdatamodel.filters.QtProlongationFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class QtProlongationRequest extends EventFilterRequestPopulationAware<QtProlongationFilters> {
    @NotNull
    private QtProlongationFilters qtProlongationFilters;

    @Override
    public QtProlongationFilters getEventFilters() {
        return qtProlongationFilters;
    }
}
