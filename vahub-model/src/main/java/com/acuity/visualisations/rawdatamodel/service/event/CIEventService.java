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
import com.acuity.visualisations.rawdatamodel.filters.CIEventFilters;
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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.CIEventRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.CI_SYMPTOMS_DURATION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.CORONARY_ANGIOGRAPHY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.DID_SYMPTOMS_PROMPT_UNS_HOSP;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.ECG_AT_THE_EVENT_TIME;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.EVENT_SUSP_DUE_TO_STENT_THROMB;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.FINAL_DIAGNOSIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.ISHEMIC_SYMTOMS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.PREVIOUS_ECG_AVAILABLE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions.WERE_LOCAL_CARDIAC_BIOMARKERS_DRAWN;

@Service
public class CIEventService extends BasePlotEventService<CIEventRaw, CIEvent, CIEventGroupByOptions>
        implements
        BarChartSupportService<CIEvent, CIEventGroupByOptions>,
        TrellisSupportService<CIEvent, CIEventGroupByOptions>,
        OverTimeChartSupportService<CIEvent, CIEventGroupByOptions> {

    @Autowired
    private BarChartService<CIEvent, CIEventGroupByOptions> barChartService;
    @Autowired
    private BarChartService<Subject, PopulationGroupByOptions> populationBarChartService;
    @Autowired
    private ColoredBarChartUIModelService uiModelService;
    @Autowired
    private OvertimeChartUIModelService overtimeChartUIModelService;


    @Override
    public List<TrellisedBarChart<CIEvent, CIEventGroupByOptions>> getBarChart(Datasets datasets,
                                                                               ChartGroupByOptionsFiltered<CIEvent, CIEventGroupByOptions> settings,
                                                                               Filters<CIEvent> filters, PopulationFilters populationFilters,
                                                                               CountType countType) {
        FilterResult<CIEvent> filtered = getFilteredData(datasets, filters, populationFilters, settings);

        Map<GroupByKey<CIEvent, CIEventGroupByOptions>, BarChartCalculationObject<CIEvent>> chartData =
                barChartService.getBarChart(settings.getSettings(), countType, filtered);

        return uiModelService.toTrellisedBarChart(chartData, countType);
    }


    @Override
    public List<TrellisedOvertime<CIEvent, CIEventGroupByOptions>> getLineBarChart(Datasets datasets,
                                                                                   ChartGroupByOptionsFiltered<CIEvent, CIEventGroupByOptions> eventSettings,
                                                                                   Filters<CIEvent> filters, PopulationFilters populationFilters) {

        FilterResult<CIEvent> filtered = getFilteredData(datasets, filters, populationFilters, eventSettings);

        Map<GroupByKey<CIEvent, CIEventGroupByOptions>, BarChartCalculationObject<CIEvent>> chartData =
                barChartService.getBarChart(eventSettings.getSettings(), CountType.COUNT_OF_EVENTS, filtered);

        Map<GroupByKey<Subject, PopulationGroupByOptions>, BarChartCalculationObject<Subject>> lineData =
                populationBarChartService.getBarChart(getPopulationLineSettings(eventSettings, filtered.getFilteredResult()),
                        CountType.COUNT_OF_EVENTS, filtered.getPopulationFilterResult());

        return overtimeChartUIModelService.toTrellisedOvertime(chartData, lineData);
    }

    @Override
    public AxisOptions<CIEventGroupByOptions> getAvailableOverTimeChartXAxis(Datasets datasets, Filters<CIEvent> filters, PopulationFilters populationFilters) {
        return getAxisOptions(datasets, filters, populationFilters, CIEventGroupByOptions.START_DATE);
    }

    public List<TrellisOptions<CIEventGroupByOptions>> getTrellisOptions(Datasets datasets, Filters<CIEvent> filters,
                                                                         PopulationFilters populationFilters) {

        FilterResult<CIEvent> filtered = getFilteredData(datasets, filters, populationFilters, null);

        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                FINAL_DIAGNOSIS,
                ISHEMIC_SYMTOMS,
                CI_SYMPTOMS_DURATION,
                DID_SYMPTOMS_PROMPT_UNS_HOSP,
                EVENT_SUSP_DUE_TO_STENT_THROMB,
                PREVIOUS_ECG_AVAILABLE,
                ECG_AT_THE_EVENT_TIME,
                WERE_LOCAL_CARDIAC_BIOMARKERS_DRAWN,
                CORONARY_ANGIOGRAPHY);
    }

    /*this is just for compatibility*/
    public List<TrellisOptions<CIEventGroupByOptions>> getBarChartColorBy(Datasets datasets, Filters<CIEvent> filters,
                                                                         PopulationFilters populationFilters) {

        FilterResult<CIEvent> filtered = getFilteredData(datasets, filters, populationFilters, null);

        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                FINAL_DIAGNOSIS,
                ISHEMIC_SYMTOMS,
                CI_SYMPTOMS_DURATION,
                DID_SYMPTOMS_PROMPT_UNS_HOSP,
                EVENT_SUSP_DUE_TO_STENT_THROMB,
                PREVIOUS_ECG_AVAILABLE,
                ECG_AT_THE_EVENT_TIME,
                WERE_LOCAL_CARDIAC_BIOMARKERS_DRAWN,
                CORONARY_ANGIOGRAPHY);
    }

    public AxisOptions<CIEventGroupByOptions> getAvailableBarChartXAxis(Datasets datasets, Filters<CIEvent> filters,
                                                                             PopulationFilters populationFilters) {

        return getAxisOptions(datasets, filters, populationFilters,
                FINAL_DIAGNOSIS,
                ISHEMIC_SYMTOMS,
                CI_SYMPTOMS_DURATION,
                DID_SYMPTOMS_PROMPT_UNS_HOSP,
                EVENT_SUSP_DUE_TO_STENT_THROMB,
                PREVIOUS_ECG_AVAILABLE,
                ECG_AT_THE_EVENT_TIME,
                WERE_LOCAL_CARDIAC_BIOMARKERS_DRAWN,
                CORONARY_ANGIOGRAPHY);
    }

    public List<String> getAssociatedAeNumbersFromEventIds(Datasets datasets, PopulationFilters populationFilters, List<String> eventIds) {
        return super.getAssociatedAeNumbersFromEventIds(datasets, CIEventFilters.empty(), populationFilters, eventIds);
    }

    public SelectionDetail getSelectionDetails(Datasets datasets, Filters<CIEvent> filters, PopulationFilters populationFilters,
                                               ChartSelection<CIEvent, CIEventGroupByOptions, ChartSelectionItem<CIEvent, CIEventGroupByOptions>> selection) {
        FilterResult<CIEvent> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return barChartService.getSelectionDetails(filtered, selection);
    }

}
