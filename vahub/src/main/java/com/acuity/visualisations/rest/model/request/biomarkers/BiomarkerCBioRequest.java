package com.acuity.visualisations.rest.model.request.biomarkers;

import com.acuity.visualisations.rawdatamodel.filters.BiomarkerFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(callSuper = true)
public class BiomarkerCBioRequest extends DatasetsRequest {
    private PopulationFilters populationFilters;
    private BiomarkerFilters biomarkerFilters;
    private Set<String> eventIds;
}
