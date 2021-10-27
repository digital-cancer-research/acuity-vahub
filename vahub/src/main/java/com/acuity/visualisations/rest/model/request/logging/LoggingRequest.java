package com.acuity.visualisations.rest.model.request.logging;

import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@ToString
public class LoggingRequest extends DatasetsRequest {
    @NotNull
    private String analyticsSessionId;
    @NotNull
    private Date analyticsSessionDate;
    @NotNull
    private String viewName;
    @NotNull
    private String visualisationName;
}
