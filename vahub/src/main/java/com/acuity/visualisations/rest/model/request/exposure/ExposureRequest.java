package com.acuity.visualisations.rest.model.request.exposure;

import com.acuity.visualisations.rawdatamodel.filters.ExposureFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class ExposureRequest extends EventFilterRequestPopulationAware<ExposureFilters> {
    @NotNull
    private ExposureFilters exposureFilters;

    @Override
    public ExposureFilters getEventFilters() {
        return exposureFilters;
    }
}
