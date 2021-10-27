package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.va.security.acl.domain.Datasets;

import java.util.List;

/**
 * Created by knml167 on 6/16/2017.
 */
public interface RangePlotSupportService<T, G extends Enum<G> & GroupByOption<T>> extends StatsPlotSupportService<T, G> {

    List<TrellisedRangePlot<T, G>> getRangePlot(Datasets datasets,
                                                ChartGroupByOptionsFiltered<T, G> settings,
                                                Filters<T> filters,
                                                PopulationFilters populationFilters,
                                                StatType statType);

    AxisOptions<G> getAvailableRangePlotXAxis(Datasets datasets, Filters<T> filters,
                                              PopulationFilters populationFilters);
}
