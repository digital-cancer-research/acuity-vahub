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

import com.acuity.visualisations.common.aspect.TimeMe;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.filters.CvotEndpointFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.plots.TrellisSupportService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.ColoredBarChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.OvertimeChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartService;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartSupportService;
import com.acuity.visualisations.rawdatamodel.service.plots.OverTimeChartSupportService;
import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.CvotEndpointRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions.CATEGORY_1;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions.CATEGORY_2;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions.CATEGORY_3;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions.DESCRIPTION_1;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions.DESCRIPTION_2;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions.DESCRIPTION_3;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions.START_DATE;

/**
 * Created by knml167 on 6/2/2017.
 */
@Service
public class CvotEndpointService extends BasePlotEventService<CvotEndpointRaw, CvotEndpoint, CvotEndpointGroupByOptions>
        implements
        BarChartSupportService<CvotEndpoint, CvotEndpointGroupByOptions>,
        TrellisSupportService<CvotEndpoint, CvotEndpointGroupByOptions>,
        OverTimeChartSupportService<CvotEndpoint, CvotEndpointGroupByOptions> {

    @Autowired
    private BarChartService<CvotEndpoint, CvotEndpointGroupByOptions> barChartService;
    @Autowired
    private BarChartService<Subject, PopulationGroupByOptions> populationBarChartService;
    @Autowired
    private ColoredBarChartUIModelService uiModelService;
    @Autowired
    private OvertimeChartUIModelService overtimeChartUIModelService;

    @TimeMe
    @Override
    public List<TrellisedBarChart<CvotEndpoint, CvotEndpointGroupByOptions>> getBarChart(
            Datasets datasets,
            ChartGroupByOptionsFiltered<CvotEndpoint, CvotEndpointGroupByOptions> settings,
            Filters<CvotEndpoint> filters, PopulationFilters populationFilters,
            CountType countType) {
        FilterResult<CvotEndpoint> filtered = getFilteredData(datasets, filters, populationFilters, settings);

        Map<GroupByKey<CvotEndpoint, CvotEndpointGroupByOptions>, BarChartCalculationObject<CvotEndpoint>> chartData =
                barChartService.getBarChart(settings.getSettings(), countType, filtered);

        return uiModelService.toTrellisedBarChart(chartData, countType);
    }

    @Override
    @TimeMe
    public List<TrellisedOvertime<CvotEndpoint, CvotEndpointGroupByOptions>> getLineBarChart(
            Datasets datasets,
            ChartGroupByOptionsFiltered<CvotEndpoint, CvotEndpointGroupByOptions> eventSettings,
            Filters<CvotEndpoint> filters, PopulationFilters populationFilters) {
        FilterResult<CvotEndpoint> filtered = getFilteredData(datasets, filters, populationFilters, eventSettings);

        Map<GroupByKey<CvotEndpoint, CvotEndpointGroupByOptions>, BarChartCalculationObject<CvotEndpoint>> chartData =
                barChartService.getBarChart(eventSettings.getSettings(), CountType.COUNT_OF_EVENTS, filtered);

        Map<GroupByKey<Subject, PopulationGroupByOptions>, BarChartCalculationObject<Subject>> lineData =
                populationBarChartService.getBarChart(getPopulationLineSettings(eventSettings, filtered.getFilteredResult()),
                        CountType.COUNT_OF_EVENTS, filtered.getPopulationFilterResult());

        return overtimeChartUIModelService.toTrellisedOvertime(chartData, lineData);
    }

    @Override
    public AxisOptions<CvotEndpointGroupByOptions> getAvailableOverTimeChartXAxis(Datasets datasets, Filters<CvotEndpoint> filters,
                                                                                  PopulationFilters populationFilters) {
        return getAxisOptions(datasets, filters, populationFilters, START_DATE);
    }


    @TimeMe
    public AxisOptions<CvotEndpointGroupByOptions> getAvailableBarChartXAxis(Datasets datasets, Filters<CvotEndpoint> filters,
                                                                                  PopulationFilters populationFilters) {
        return getAxisOptions(datasets, filters, populationFilters, CATEGORY_1, CATEGORY_2, CATEGORY_3, DESCRIPTION_1, DESCRIPTION_2, DESCRIPTION_3);
    }

    public List<TrellisOptions<CvotEndpointGroupByOptions>> getTrellisOptions(Datasets datasets, Filters<CvotEndpoint> filters,
                                                                              PopulationFilters populationFilters) {

        FilterResult<CvotEndpoint> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                CATEGORY_1, CATEGORY_2, CATEGORY_3, DESCRIPTION_1, DESCRIPTION_2, DESCRIPTION_3);
    }

    /*this is just for compatibility*/
    public List<TrellisOptions<CvotEndpointGroupByOptions>> getBarChartColorBy(Datasets datasets, Filters<CvotEndpoint> filters,
                                                                              PopulationFilters populationFilters) {

        FilterResult<CvotEndpoint> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                CATEGORY_1, CATEGORY_2, CATEGORY_3, DESCRIPTION_1, DESCRIPTION_2, DESCRIPTION_3);
    }

    public List<String> getAssociatedAeNumbersFromEventIds(
            Datasets datasets, PopulationFilters populationFilters, List<String> eventIds) {
        return super.getAssociatedAeNumbersFromEventIds(datasets, CvotEndpointFilters.empty(), populationFilters, eventIds);
    }

    public SelectionDetail getSelectionDetails(Datasets datasets, Filters<CvotEndpoint> filters, PopulationFilters populationFilters,
                                               ChartSelection<CvotEndpoint, CvotEndpointGroupByOptions,
                                                       ChartSelectionItem<CvotEndpoint, CvotEndpointGroupByOptions>> selection) {
        FilterResult<CvotEndpoint> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return barChartService.getSelectionDetails(filtered, selection);
    }

}
