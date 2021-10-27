package com.acuity.visualisations.rest.model.request;

import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class DetailsOnDemandRequest extends DatasetsRequest {

    private Set<String> eventIds;
    private List<SortAttrs> sortAttrs;
    private int start;
    private int end;
}
