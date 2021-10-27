package com.acuity.visualisations.rest.model.request.respiratory.lungfunction;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * Request for lung function trellising
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LungFunctionTrellisRequest extends LungFunctionRequest {
    @NotNull
    @JsonProperty("resultType")
    private LungFunctionGroupByOptions yAxisOption;
}

