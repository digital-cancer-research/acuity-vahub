package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.va.security.acl.domain.Datasets;

public interface BarChartXAxisSupport<T, G extends Enum<G> & GroupByOption<T>> {
    AxisOptions<G> getAvailableBarChartXAxis(Datasets datasets, Filters<T> filters,
                                             PopulationFilters populationFilters);
}
