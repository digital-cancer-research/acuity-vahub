package com.acuity.visualisations.rest.model.request.pkresult;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class PkResultOptionsRequest extends PkResultRequest {
    @NotNull
    private String timepointType;
}
