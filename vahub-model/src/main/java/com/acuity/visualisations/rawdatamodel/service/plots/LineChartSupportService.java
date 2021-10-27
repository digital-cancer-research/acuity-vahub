package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.filters.ExposureFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.TrellisedLineFloatChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.va.security.acl.domain.Datasets;

import java.util.List;

public interface LineChartSupportService<T, G extends Enum<G> & GroupByOption<T>> {
    List<TrellisedLineFloatChart<T, G, OutputLineChartData>> getLineChart(Datasets datasets,
                                                                          ChartGroupByOptions<T, G> settings,
                                                                          ExposureFilters filters,
                                                                          PopulationFilters populationFilters);

    SelectionDetail getSelectionDetails(Datasets datasets, Filters<T> filters,
                                        PopulationFilters populationFilters, ChartSelection<T, G, ChartSelectionItem<T, G>> selection);
}
