package com.acuity.visualisations.rest.model.request;

import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SingleSubjectRequest<T> extends DatasetsRequest {

    private String subjectId;
    private T eventFilters;
}
