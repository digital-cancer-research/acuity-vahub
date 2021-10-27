package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedShiftPlot;
import com.acuity.va.security.acl.domain.Datasets;

import java.util.List;

/**
 * Created by knml167 on 6/16/2017.
 */
public interface ShiftPlotSupportService<T, G extends Enum<G> & GroupByOption<T>> extends StatsPlotSupportService<T, G> {

    List<TrellisedShiftPlot<T, G>> getShiftPlot(Datasets datasets,
                                                ChartGroupByOptionsFiltered<T, G> settings,
                                                Filters<T> filters, PopulationFilters populationFilters);

    AxisOptions<G> getAvailableShiftPlotXAxis(Datasets datasets, Filters<T> filters,
                                            PopulationFilters populationFilters);
}
