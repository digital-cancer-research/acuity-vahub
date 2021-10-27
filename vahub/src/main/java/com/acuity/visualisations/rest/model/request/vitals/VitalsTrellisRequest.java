package com.acuity.visualisations.rest.model.request.vitals;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class VitalsTrellisRequest extends VitalsRequest {
    @NotNull
    @JsonProperty("yAxisOption")
    private VitalGroupByOptions yAxisOption;
}
