package com.acuity.visualisations.rest.model.request.cardiac;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.CardiacGroupByOptions;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CardiacTrellisRequest extends CardiacRequest {
    @NotNull
    @JsonProperty("yAxisOption")
    private CardiacGroupByOptions yAxisOption;
}
