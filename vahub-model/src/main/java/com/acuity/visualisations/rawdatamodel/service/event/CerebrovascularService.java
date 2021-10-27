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
import com.acuity.visualisations.rawdatamodel.filters.CerebrovascularFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartSupportService;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.plots.OverTimeChartSupportService;
import com.acuity.visualisations.rawdatamodel.service.plots.TrellisSupportService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.ColoredBarChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.OvertimeChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartService;
import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.CerebrovascularRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.EVENT_TYPE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.INTRA_HEMORRHAGE_LOC;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.MRS_CURR_VISIT_OR_90D_AFTER;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.MRS_DURING_STROKE_HOSP;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.MRS_PRIOR_STROKE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.PRIMARY_ISCHEMIC_STROKE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.SYMPTOMS_DURATION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions.TRAUMATIC;

@Service
public class CerebrovascularService extends BasePlotEventService<CerebrovascularRaw, Cerebrovascular, CerebrovascularGroupByOptions>
        implements
        BarChartSupportService<Cerebrovascular, CerebrovascularGroupByOptions>,
        OverTimeChartSupportService<Cerebrovascular, CerebrovascularGroupByOptions>,
        TrellisSupportService<Cerebrovascular, CerebrovascularGroupByOptions> {

    @Autowired
    private BarChartService<Cerebrovascular, CerebrovascularGroupByOptions> barChartService;
    @Autowired
    private BarChartService<Subject, PopulationGroupByOptions> populationBarChartService;
    @Autowired
    private ColoredBarChartUIModelService uiModelService;
    @Autowired
    private OvertimeChartUIModelService overtimeChartUIModelService;

    @Override
    public List<TrellisedBarChart<Cerebrovascular, CerebrovascularGroupByOptions>> getBarChart(
            Datasets datasets, ChartGroupByOptionsFiltered<Cerebrovascular, CerebrovascularGroupByOptions> settings,
            Filters<Cerebrovascular> filters, PopulationFilters populationFilters, CountType countType) {
        FilterResult<Cerebrovascular> filtered = getFilteredData(datasets, filters, populationFilters);

        Map<GroupByKey<Cerebrovascular, CerebrovascularGroupByOptions>, BarChartCalculationObject<Cerebrovascular>> chartData =
                barChartService.getBarChart(settings.getSettings(), countType, filtered);

        return uiModelService.toTrellisedBarChart(chartData, countType);
    }

    @Override
    public List<TrellisedOvertime<Cerebrovascular, CerebrovascularGroupByOptions>> getLineBarChart(
            Datasets datasets,
            ChartGroupByOptionsFiltered<Cerebrovascular, CerebrovascularGroupByOptions> eventSettings,
            Filters<Cerebrovascular> filters,
            PopulationFilters populationFilters) {
        FilterResult<Cerebrovascular> filtered = getFilteredData(datasets, filters, populationFilters, eventSettings);

        Map<GroupByKey<Cerebrovascular, CerebrovascularGroupByOptions>, BarChartCalculationObject<Cerebrovascular>> chartData =
                barChartService.getBarChart(eventSettings.getSettings(), CountType.COUNT_OF_EVENTS, filtered);

        Map<GroupByKey<Subject, PopulationGroupByOptions>, BarChartCalculationObject<Subject>> lineData =
                populationBarChartService.getBarChart(getPopulationLineSettings(eventSettings, filtered.getFilteredResult()),
                        CountType.COUNT_OF_EVENTS, filtered.getPopulationFilterResult());

        return overtimeChartUIModelService.toTrellisedOvertime(chartData, lineData);
    }

    @Override
    public AxisOptions<CerebrovascularGroupByOptions> getAvailableOverTimeChartXAxis(Datasets datasets, Filters<Cerebrovascular> filters,
                                                                                     PopulationFilters populationFilters) {
        return getAxisOptions(datasets, filters, populationFilters, CerebrovascularGroupByOptions.START_DATE);
    }


    public List<TrellisOptions<CerebrovascularGroupByOptions>> getTrellisOptions(Datasets datasets, Filters<Cerebrovascular> filters,
                                                                                 PopulationFilters populationFilters) {

        FilterResult<Cerebrovascular> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                EVENT_TYPE,
                PRIMARY_ISCHEMIC_STROKE,
                TRAUMATIC,
                INTRA_HEMORRHAGE_LOC,
                SYMPTOMS_DURATION,
                MRS_PRIOR_STROKE,
                MRS_DURING_STROKE_HOSP,
                MRS_CURR_VISIT_OR_90D_AFTER);
    }

    /*this is just for compatibility*/
    public List<TrellisOptions<CerebrovascularGroupByOptions>> getBarChartColorBy(Datasets datasets, Filters<Cerebrovascular> filters,
                                                                                 PopulationFilters populationFilters) {

        FilterResult<Cerebrovascular> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                EVENT_TYPE,
                PRIMARY_ISCHEMIC_STROKE,
                TRAUMATIC,
                INTRA_HEMORRHAGE_LOC,
                SYMPTOMS_DURATION,
                MRS_PRIOR_STROKE,
                MRS_DURING_STROKE_HOSP,
                MRS_CURR_VISIT_OR_90D_AFTER);
    }

    public AxisOptions<CerebrovascularGroupByOptions> getAvailableBarChartXAxis(Datasets datasets, Filters<Cerebrovascular> filters,
                                                                                     PopulationFilters populationFilters) {

        return getAxisOptions(datasets, filters, populationFilters,
                EVENT_TYPE,
                PRIMARY_ISCHEMIC_STROKE,
                TRAUMATIC,
                INTRA_HEMORRHAGE_LOC,
                SYMPTOMS_DURATION,
                MRS_PRIOR_STROKE,
                MRS_DURING_STROKE_HOSP,
                MRS_CURR_VISIT_OR_90D_AFTER);
    }

    @Override
    public SelectionDetail getSelectionDetails(Datasets datasets, Filters<Cerebrovascular> filters,
                                               PopulationFilters populationFilters, ChartSelection<Cerebrovascular, CerebrovascularGroupByOptions,
                                               ChartSelectionItem<Cerebrovascular, CerebrovascularGroupByOptions>> selection) {
        FilterResult<Cerebrovascular> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return barChartService.getSelectionDetails(filtered, selection);
    }

    public List<String> getAssociatedAeNumbersFromEventIds(
            Datasets datasets, PopulationFilters populationFilters, List<String> eventIds) {

        return super.getAssociatedAeNumbersFromEventIds(datasets, CerebrovascularFilters.empty(), populationFilters, eventIds);
    }

}
