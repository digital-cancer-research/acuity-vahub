package com.acuity.visualisations.rest.model.request.cohort;

import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public class CohortEditorDeleteRequest extends DatasetsRequest {

    @NotNull
    private Long savedFilterId;
}
