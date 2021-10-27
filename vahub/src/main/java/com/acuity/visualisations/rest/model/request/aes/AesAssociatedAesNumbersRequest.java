package com.acuity.visualisations.rest.model.request.aes;

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AesAssociatedAesNumbersRequest extends DatasetsRequest {

    private PopulationFilters populationFilters;
    private String fromPlot;
    private List<String> eventIds;
}
