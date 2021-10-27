package com.acuity.visualisations.rest.model.request.aes;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.axes.TAxes;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class AesTimelineRequest extends DatasetsRequest {

    @NotNull
    private TAxes<DayZeroType> dayZero;

    @NotNull
    private PopulationFilters populationFilters;

    @NotNull
    private AeFilters aesFilters;
}
