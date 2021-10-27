package com.acuity.visualisations.rest.model.request.aes;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
public class AesChordDiagramRequest extends AesPlotRequest {
    @NotNull
    private Map<String, String> additionalSettings;
}
