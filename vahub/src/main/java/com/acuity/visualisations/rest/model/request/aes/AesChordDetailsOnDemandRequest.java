package com.acuity.visualisations.rest.model.request.aes;

import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.vo.plots.ChordContributor;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class AesChordDetailsOnDemandRequest extends DatasetsRequest {

    private Set<ChordContributor> eventIds;
    private List<SortAttrs> sortAttrs;
    private int start;
    private int end;
}
