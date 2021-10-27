package com.acuity.visualisations.rest.model.request;

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class EventFilterRequestPopulationAware<T> extends DatasetsRequest {
    @NotNull
    private PopulationFilters populationFilters;
    @JsonIgnore
    public abstract T getEventFilters();
}
