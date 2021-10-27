package com.acuity.visualisations.rest.model.request;

import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class SubjectIdsRequest extends DatasetsRequest {
    private Set<String> subjectIds;
}
