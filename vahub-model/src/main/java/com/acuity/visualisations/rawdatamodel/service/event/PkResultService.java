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
import com.acuity.visualisations.rawdatamodel.dataproviders.PkResultDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.BoxPlotUiModelService;
import com.acuity.visualisations.rawdatamodel.service.plots.BoxPlotSupportService;
import com.acuity.visualisations.rawdatamodel.service.plots.StatsPlotService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.AlphanumEmptyLastComparator;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.PkResultRaw;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.pkresult.CycleDay;
import com.acuity.visualisations.rawdatamodel.vo.plots.BoxplotCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.ANALYTE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.CYCLE_DAY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.MEASUREMENT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.MEASUREMENT_TIMEPOINT;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class PkResultService extends BasePlotEventService<PkResultRaw, PkResult, PkResultGroupByOptions>
        implements
        BoxPlotSupportService<PkResult, PkResultGroupByOptions> {

    @Autowired
    private StatsPlotService<PkResult, PkResultGroupByOptions> statsPlotService;
    @Autowired
    private BoxPlotUiModelService boxPlotUiModelService;
    @Autowired
    private PkResultDatasetsDataProvider pkResultDatasetsDataProvider;

    @Override
    protected SubjectAwareDatasetsDataProvider<PkResultRaw, PkResult> getEventDataProvider(Datasets datasets, Filters<PkResult> filters) {
        return pkResultDatasetsDataProvider;
    }

    @Override
    public List<TrellisedBoxPlot<PkResult, PkResultGroupByOptions>> getBoxPlot(Datasets datasets,
                                                                               ChartGroupByOptionsFiltered<PkResult,
                                                                                       PkResultGroupByOptions> settings,
                                                                               Filters<PkResult> filters,
                                                                               PopulationFilters populationFilters) {

        FilterResult<PkResult> filtered = getFilteredData(datasets, filters, populationFilters, settings);

        Map<GroupByKey<PkResult, PkResultGroupByOptions>, BoxplotCalculationObject> boxPlot = statsPlotService
                .getBoxPlot(settings.getSettings(), filtered);
        return boxPlotUiModelService.toTrellisedBoxPlot(boxPlot);
    }

    @Override
    public AxisOptions<PkResultGroupByOptions> getAvailableBoxPlotXAxis(Datasets datasets, Filters<PkResult> filters,
                                                                        PopulationFilters populationFilters) {
        return getAxisOptions(datasets, filters, populationFilters, PkResultGroupByOptions.DOSE, PkResultGroupByOptions.ACTUAL_DOSE);
    }

    public List<TrellisOptions<PkResultGroupByOptions>> getTrellisOptions(Datasets datasets, Filters<PkResult> filters,
                                                                          PopulationFilters populationFilters) {
        FilterResult<PkResult> filtered = getFilteredData(datasets, filters, populationFilters);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(), ANALYTE);
    }

    public List<PkResultTrellisOptions> getBoxPlotOptions(Datasets datasets, Filters<PkResult> filters,
                                                          PopulationFilters populationFilters, String timepointType) {
        FilterResult<PkResult> filtered = getFilteredData(datasets, filters, populationFilters);
        TrellisOptions<PkResultGroupByOptions> measurementOption = TrellisUtil.getOptionsForTrellisItem(filtered.getFilteredResult(),
                MEASUREMENT.<PkResult, PkResultGroupByOptions>getGroupByOptionAndParams());

        Map<String, Map<PkResultGroupByOptions, Object>> timepointsByMeasurement = measurementOption.getTrellisOptions()
                .stream().collect(toMap(Object::toString,
                        measurement -> getTimepointOptionsPerMeasurement(datasets, filters, populationFilters,
                                measurement.toString(), timepointType),
                        (e1, e2) -> e1,
                        TreeMap::new));
        return Collections.singletonList(new PkResultTrellisOptions(measurementOption, MEASUREMENT_TIMEPOINT,
                timepointsByMeasurement));
    }

    @Override
    public SelectionDetail getRangedSelectionDetails(Datasets datasets, Filters<PkResult> filters,
                                                     PopulationFilters populationFilters,
                                                     ChartSelection<PkResult, PkResultGroupByOptions,
                                                             ChartSelectionItemRange<PkResult, PkResultGroupByOptions, Double>> selection) {
        FilterResult<PkResult> filtered = getFilteredData(datasets, filters, populationFilters);
        return statsPlotService.getRangedSelectionDetails(filtered, selection);
    }

    @Override
    public SelectionDetail getSelectionDetails(Datasets datasets, Filters<PkResult> filters,
                                               PopulationFilters populationFilters,
                                               ChartSelection<PkResult, PkResultGroupByOptions,
                                                       ChartSelectionItem<PkResult, PkResultGroupByOptions>> selection) {
        return null;
    }

    private Map<PkResultGroupByOptions, Object> getTimepointOptionsPerMeasurement(Datasets datasets, Filters<PkResult> filters,
                                                             PopulationFilters populationFilters,
                                                             String measurementValue,
                                                             String timepointType) {
        ChartGroupByOptionsFiltered<PkResult, PkResultGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered
                .builder(ChartGroupByOptions.<PkResult, PkResultGroupByOptions>builder().build())
                .withFilterByTrellisOption(MEASUREMENT, measurementValue)
                .build();

        FilterResult<PkResult> filteredByMeasurement = getFilteredData(datasets, filters, populationFilters, settingsFiltered);
        final PkResultGroupByOptions timepointOption = PkResultGroupByOptions.valueOf(timepointType);
        Map<PkResultGroupByOptions, Object> timepointsByOption = new HashMap<>();
        if (timepointOption.equals(CYCLE_DAY)) {
            List<CycleDay> optionsForCycleDay = filteredByMeasurement.getFilteredResult()
                    .stream()
                    .map(e -> (CycleDay) Attributes.get(CYCLE_DAY.<PkResult, PkResultGroupByOptions>getGroupByOptionAndParams(), e))
                    .collect(toList());
            Map<String, Set<String>> timepointsPerCycleDay = new TreeMap<>(AlphanumEmptyLastComparator.getInstance());
            optionsForCycleDay.forEach(cycleDay -> timepointsPerCycleDay.computeIfAbsent(cycleDay.getCycle(),
                    key -> new TreeSet<>(AlphanumEmptyLastComparator.getInstance())).add(cycleDay.getDay()));
            timepointsByOption.put(timepointOption, timepointsPerCycleDay);
        } else {
            TrellisOptions<PkResultGroupByOptions> optionsForTrellisItem = TrellisUtil
                    .getOptionsForTrellisItem(filteredByMeasurement.getFilteredResult(),
                            timepointOption.<PkResult, PkResultGroupByOptions>getGroupByOptionAndParams());
            timepointsByOption.put(timepointOption, optionsForTrellisItem.getTrellisOptions());
        }
        return timepointsByOption;
    }

    @Getter
    public static class PkResultTrellisOptions extends TrellisOptions<PkResultGroupByOptions> {
        private PkResultGroupByOptions timepointTrellisedBy;
        private Map<String, Map<PkResultGroupByOptions, Object>> timepointsByParameter;

        PkResultTrellisOptions(TrellisOptions<PkResultGroupByOptions> trellisOptions,
                               PkResultGroupByOptions timepointTrellisedBy,
                               Map<String, Map<PkResultGroupByOptions, Object>> timepointsByParameter) {
            super(trellisOptions.getTrellisedBy(), trellisOptions.getTrellisOptions());
            this.timepointTrellisedBy = timepointTrellisedBy;
            this.timepointsByParameter = timepointsByParameter;
        }
    }
}
