package com.acuity.visualisations.rest.model.request.labs;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class LabsTrellisRequest extends LabsRequest {

    @NotNull
    @JsonProperty("yAxisOption")
    private LabGroupByOptions yAxisOption;

}
