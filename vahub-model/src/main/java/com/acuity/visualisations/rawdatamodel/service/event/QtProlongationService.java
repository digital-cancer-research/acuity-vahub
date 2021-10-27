/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartSupportService;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.plots.TrellisSupportService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.ColoredBarChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartService;
import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.QtProlongationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.QtProlongationRaw;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.QtProlongation;
import com.acuity.va.security.acl.domain.Datasets;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import static com.acuity.visualisations.rawdatamodel.trellis.grouping.QtProlongationGroupByOptions.ALERT_LEVEL;

@Service
public class QtProlongationService
        extends BasePlotEventService<QtProlongationRaw, QtProlongation, QtProlongationGroupByOptions>
        implements BarChartSupportService<QtProlongation, QtProlongationGroupByOptions>,
        TrellisSupportService<QtProlongation, QtProlongationGroupByOptions> {
    @Autowired
    private BarChartService<QtProlongation, QtProlongationGroupByOptions> barChartService;
    @Autowired
    private ColoredBarChartUIModelService uiModelService;

    @Override
    public List<TrellisedBarChart<QtProlongation, QtProlongationGroupByOptions>> getBarChart(
            Datasets datasets, ChartGroupByOptionsFiltered<QtProlongation, QtProlongationGroupByOptions> settings,
            Filters<QtProlongation> filters, PopulationFilters populationFilters, CountType countType) {
        FilterResult<QtProlongation> filtered = getFilteredData(datasets, filters, populationFilters, settings);
        Map<GroupByKey<QtProlongation, QtProlongationGroupByOptions>, BarChartCalculationObject<QtProlongation>> chartData =
                barChartService.getBarChart(settings.getSettings(), countType, filtered);
        return uiModelService.toTrellisedBarChart(chartData, countType);
    }

    @Override
    public AxisOptions<QtProlongationGroupByOptions> getAvailableBarChartXAxis(
            Datasets datasets, Filters<QtProlongation> filters, PopulationFilters populationFilters) {
        return getAxisOptions(datasets, filters, populationFilters, ALERT_LEVEL);
    }

    @Override
    public SelectionDetail getSelectionDetails(
            Datasets datasets, Filters<QtProlongation> filters, PopulationFilters populationFilters,
            ChartSelection<QtProlongation, QtProlongationGroupByOptions, ChartSelectionItem<QtProlongation,
                    QtProlongationGroupByOptions>> selection) {
        FilterResult<QtProlongation> filtered = getFilteredData(datasets, filters, populationFilters);
        return barChartService.getSelectionDetails(filtered, selection);
    }

    @Override
    public List<TrellisOptions<QtProlongationGroupByOptions>> getTrellisOptions(
            Datasets datasets, Filters<QtProlongation> filters, PopulationFilters populationFilters) {
        FilterResult<QtProlongation> filtered = getFilteredData(datasets, filters, populationFilters);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(), ALERT_LEVEL);
    }

    public List<TrellisOptions<QtProlongationGroupByOptions>> getBarChartColorBy(
            Datasets datasets, Filters<QtProlongation> filters, PopulationFilters populationFilters) {
        FilterResult<QtProlongation> filtered = getFilteredData(datasets, filters, populationFilters);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(), ALERT_LEVEL);
    }
}
