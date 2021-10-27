package com.acuity.visualisations.rest.model.request.biomarkers;

import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class BiomarkersSubjectIdsRequest extends DatasetsRequest {
    private Set<String> subjectIds;
}
