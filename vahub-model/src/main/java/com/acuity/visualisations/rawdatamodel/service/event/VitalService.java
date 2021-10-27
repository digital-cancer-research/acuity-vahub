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

import com.acuity.visualisations.rawdatamodel.aspect.ApplyUsedInTflFilter;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.plots.BoxPlotSupportService;
import com.acuity.visualisations.rawdatamodel.service.plots.RangePlotSupportService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.BoxPlotUiModelService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.RangePlotUiModelService;
import com.acuity.visualisations.rawdatamodel.service.plots.StatsPlotService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.VitalRaw;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.BoxplotCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.RangeChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions.MEASUREMENT_TIME_POINT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions.PERCENTAGE_CHANGE_FROM_BASELINE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions.VISIT_NUMBER;

@Service
public class VitalService extends BasePlotEventService<VitalRaw, Vital, VitalGroupByOptions>
        implements RangePlotSupportService<Vital, VitalGroupByOptions>, BoxPlotSupportService<Vital, VitalGroupByOptions> {

    @Autowired
    private StatsPlotService<Vital, VitalGroupByOptions> statsPlotService;

    @Autowired
    private RangePlotUiModelService rangePlotUiModelService;

    @Autowired
    private BoxPlotUiModelService boxPlotUiModelService;

    @Autowired
    private InfoService infoService;

    @ApplyUsedInTflFilter
    @Override
    public List<TrellisedBoxPlot<Vital, VitalGroupByOptions>> getBoxPlot(Datasets datasets,
                                                                         ChartGroupByOptionsFiltered<Vital, VitalGroupByOptions> settings,
                                                                         Filters<Vital> filters, PopulationFilters populationFilters) {
        final ChartGroupByOptions<Vital, VitalGroupByOptions> optionsWithContext = getOptionsWithContext(settings.getSettings(),
                getContextSupplier(
                        settings
                        .getSettings()
                        .getOptions()
                        .get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS)
                        .getGroupByOption()));

        FilterResult<Vital> filtered = getFilteredData(datasets, filters, populationFilters, ChartGroupByOptionsFiltered
                .builder(optionsWithContext)
                .withFilterByTrellisOptions(settings.getFilterByTrellisOptions())
                .build());

        Map<GroupByKey<Vital, VitalGroupByOptions>, BoxplotCalculationObject> boxplot = statsPlotService.getBoxPlot(optionsWithContext, filtered);
        return boxPlotUiModelService.toTrellisedBoxPlot(boxplot);

    }
    @ApplyUsedInTflFilter
    @Override
    public AxisOptions<VitalGroupByOptions> getAvailableRangePlotXAxis(Datasets datasets, Filters<Vital> filters, PopulationFilters populationFilters) {
        return infoService.limitXAxisToVisit(datasets) ? getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER)
                : getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER, MEASUREMENT_TIME_POINT);
    }

    @ApplyUsedInTflFilter
    public List<TrellisOptions<VitalGroupByOptions>> getMeanRangeTrellisOptions(
            Datasets datasets,
            Filters<Vital> filters,
            PopulationFilters populationFilters,
            VitalGroupByOptions yAxisOption) {
        FilterResult<Vital> filtered = getFilteredData(datasets, filters, populationFilters, null);

        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                VitalGroupByOptions.MEASUREMENT.<Vital,
                        VitalGroupByOptions>getGroupByOptionAndParams()
                        .supplyContext(getContextSupplier(yAxisOption)),
                VitalGroupByOptions.ARM.getGroupByOptionAndParams());
    }

    @ApplyUsedInTflFilter
    @Override
    public List<TrellisedRangePlot<Vital, VitalGroupByOptions>> getRangePlot(Datasets datasets,
                                                                             ChartGroupByOptionsFiltered<Vital, VitalGroupByOptions> settings,
                                                                             Filters<Vital> filters, PopulationFilters populationFilters,
                                                                             StatType statType) {
        final ChartGroupByOptions<Vital, VitalGroupByOptions> optionsWithContext = getOptionsWithContext(settings.getSettings(),
                getContextSupplier(settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));

        FilterResult<Vital> filtered = getFilteredData(datasets, filters, populationFilters, ChartGroupByOptionsFiltered
                .builder(optionsWithContext)
                .withFilterByTrellisOptions(settings.getFilterByTrellisOptions())
                .build());
        Map<GroupByKey<Vital, VitalGroupByOptions>, RangeChartCalculationObject> rangePlot = statsPlotService.getMedianRangePlot(optionsWithContext, filtered);
        return rangePlotUiModelService.toTrellisedRangePlot(rangePlot, statType);
    }

    @ApplyUsedInTflFilter
    @Override
    public SelectionDetail getSelectionDetails(Datasets datasets,
                                               Filters<Vital> filters,
                                               PopulationFilters populationFilters,
                                               ChartSelection<Vital, VitalGroupByOptions,
                                                       ChartSelectionItem<Vital, VitalGroupByOptions>> selection) {
        FilterResult<Vital> filtered = getFilteredData(datasets, filters, populationFilters);
        final ChartGroupByOptions<Vital, VitalGroupByOptions> optionsWithContext = getOptionsWithContext(selection.getSettings(),
                getContextSupplier(
                        selection
                        .getSettings()
                        .getOptions()
                        .get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS)
                        .getGroupByOption()));
        return statsPlotService.getSelectionDetails(filtered, ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    @ApplyUsedInTflFilter
    public List<TrellisOptions<VitalGroupByOptions>> getTrellisOptions(
            Datasets datasets,
            Filters<Vital> filters,
            PopulationFilters populationFilters,
            VitalGroupByOptions yAxisOption) {
        FilterResult<Vital> filtered = getFilteredData(datasets, filters, populationFilters, null);

        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                VitalGroupByOptions.MEASUREMENT.<Vital,
                        VitalGroupByOptions>getGroupByOptionAndParams()
                        .supplyContext(getContextSupplier(yAxisOption)),
                VitalGroupByOptions.ARM.getGroupByOptionAndParams());
    }


    @ApplyUsedInTflFilter
    @Override
    public SelectionDetail getRangedSelectionDetails(Datasets datasets,
                                                     Filters<Vital> filters,
                                                     PopulationFilters populationFilters,
                                                     ChartSelection<Vital, VitalGroupByOptions,
                                                             ChartSelectionItemRange<Vital, VitalGroupByOptions, Double>> selection) {
        FilterResult<Vital> filtered = getFilteredData(datasets, filters, populationFilters);
        final ChartGroupByOptions<Vital, VitalGroupByOptions> optionsWithContext = getOptionsWithContext(selection.getSettings(),
                getContextSupplier(selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));
        return statsPlotService.getRangedSelectionDetails(filtered, ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    @ApplyUsedInTflFilter
    @Override
    public AxisOptions<VitalGroupByOptions> getAvailableBoxPlotXAxis(Datasets datasets, Filters<Vital> filters,
                                                                     PopulationFilters populationFilters) {
        return infoService.limitXAxisToVisit(datasets) ? getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER)
                : getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER, MEASUREMENT_TIME_POINT);
    }

    private Supplier<Map<VitalGroupByOptions, Object>> getContextSupplier(VitalGroupByOptions yAxisOption) {
        return () -> {
            final Map<VitalGroupByOptions, Object> context = new EnumMap<>(VitalGroupByOptions.class);
            if (yAxisOption == PERCENTAGE_CHANGE_FROM_BASELINE) {
                context.put(VitalGroupByOptions.MEASUREMENT, "% change");
            }
            return context;
        };
    }
}
