package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.va.security.acl.domain.Datasets;

import java.util.List;

/**
 * Created by knml167 on 6/16/2017.
 */
public interface TrellisSupportService<T, G extends Enum<G> & GroupByOption> {
    List<TrellisOptions<G>> getTrellisOptions(Datasets datasets, Filters<T> filters,
                                              PopulationFilters populationFilters);
}
