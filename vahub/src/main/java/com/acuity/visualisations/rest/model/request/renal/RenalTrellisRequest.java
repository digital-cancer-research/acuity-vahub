package com.acuity.visualisations.rest.model.request.renal;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class RenalTrellisRequest extends RenalRequest {
    @NotNull
    @JsonProperty("yAxisOption")
    private RenalGroupByOptions yAxisOption;
}
