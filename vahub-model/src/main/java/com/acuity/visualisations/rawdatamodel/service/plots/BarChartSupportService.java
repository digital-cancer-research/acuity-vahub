package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.va.security.acl.domain.Datasets;

import java.util.List;

/**
 * Created by knml167 on 6/16/2017.
 */
public interface BarChartSupportService<T, G extends Enum<G> & GroupByOption<T>> {

    List<TrellisedBarChart<T, G>> getBarChart(Datasets datasets,
                                           ChartGroupByOptionsFiltered<T, G> settings,
                                           Filters<T> filters, PopulationFilters populationFilters,
                                           CountType countType);

    AxisOptions<G> getAvailableBarChartXAxis(Datasets datasets, Filters<T> filters,
                                             PopulationFilters populationFilters);

    SelectionDetail getSelectionDetails(Datasets datasets, Filters<T> filters,
            PopulationFilters populationFilters, ChartSelection<T, G, ChartSelectionItem<T, G>> selection);
}
