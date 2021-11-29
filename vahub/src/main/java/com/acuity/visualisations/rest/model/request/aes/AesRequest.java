package com.acuity.visualisations.rest.model.request.aes;

import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rest.model.request.EventFilterRequestPopulationAware;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AesRequest extends EventFilterRequestPopulationAware<AeFilters> {

    private AeFilters aesFilters;

    @Override
    public AeFilters getEventFilters() {
        return aesFilters;
    }
}
