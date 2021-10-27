package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.va.security.acl.domain.Datasets;

public interface StatsPlotSupportService<T,  G extends Enum<G> & GroupByOption<T>> {
    SelectionDetail getRangedSelectionDetails(Datasets datasets, Filters<T> filters,
                                        PopulationFilters populationFilters, ChartSelection<T, G, ChartSelectionItemRange<T, G, Double>> selection);
    SelectionDetail getSelectionDetails(Datasets datasets, Filters<T> filters,
                                        PopulationFilters populationFilters, ChartSelection<T, G, ChartSelectionItem<T, G>> selection);
}
