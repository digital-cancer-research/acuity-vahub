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
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartSupportService;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartXAxisSupport;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.plots.BoxPlotSupportService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.BoxPlotUiModelService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.RangePlotUiModelService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.RenalBarChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartService;
import com.acuity.visualisations.rawdatamodel.service.plots.StatsPlotService;
import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.RenalRaw;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.BoxplotCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.RangeChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.ImmutableMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.MEASUREMENT_TIME_POINT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.STUDY_DEFINED_WEEK;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.VISIT_DESCRIPTION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.VISIT_NUMBER;

@Service
public class RenalService extends BasePlotEventService<RenalRaw, Renal, RenalGroupByOptions>
        implements BoxPlotSupportService<Renal, RenalGroupByOptions>,
        BarChartSupportService<Renal, RenalGroupByOptions>,
        BarChartXAxisSupport<Renal, RenalGroupByOptions> {

    @Autowired
    private StatsPlotService<Renal, RenalGroupByOptions> statsPlotService;
    @Autowired
    private BoxPlotUiModelService boxPlotUiModelService;
    @Autowired
    private BarChartService<Renal, RenalGroupByOptions> barChartService;
    @Autowired
    private RenalBarChartUIModelService uiModelService;
    @Autowired
    private RangePlotUiModelService rangePlotUiModelService;
    @Autowired
    private InfoService infoService;

    @ApplyUsedInTflFilter
    @Override
    public SelectionDetail getRangedSelectionDetails(
            Datasets datasets,
            Filters<Renal> filters,
            PopulationFilters populationFilters,
            ChartSelection<Renal, RenalGroupByOptions, ChartSelectionItemRange<Renal, RenalGroupByOptions, Double>> selection) {

        FilterResult<Renal> filtered = getFilteredData(datasets, filters, populationFilters);
        final ChartGroupByOptions<Renal, RenalGroupByOptions> optionsWithContext = getOptionsWithContext(selection.getSettings(),
                getContextSupplier(selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));
        return statsPlotService.getRangedSelectionDetails(filtered, ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    @ApplyUsedInTflFilter
    @Override
    public SelectionDetail getSelectionDetails(
            Datasets datasets,
            Filters<Renal> filters,
            PopulationFilters populationFilters,
            ChartSelection<Renal, RenalGroupByOptions,
                    ChartSelectionItem<Renal, RenalGroupByOptions>> selection) {
        FilterResult<Renal> filtered = getFilteredData(datasets, filters, populationFilters);
        final ChartGroupByOptions<Renal, RenalGroupByOptions> optionsWithContext = getOptionsWithContext(selection.getSettings(),
                getContextSupplier(selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));
        return statsPlotService.getSelectionDetails(filtered, ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    @ApplyUsedInTflFilter
    @Override
    public List<TrellisedBoxPlot<Renal, RenalGroupByOptions>> getBoxPlot(
            Datasets datasets,
            ChartGroupByOptionsFiltered<Renal, RenalGroupByOptions> settings,
            Filters<Renal> filters,
            PopulationFilters populationFilters) {
        final ChartGroupByOptions<Renal, RenalGroupByOptions> optionsWithContext = getOptionsWithContext(settings.getSettings(),
                getContextSupplier(settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));

        FilterResult<Renal> filtered = getFilteredData(datasets, filters, populationFilters, ChartGroupByOptionsFiltered
                .builder(optionsWithContext)
                .withFilterByTrellisOptions(settings.getFilterByTrellisOptions())
                .build());

        Map<GroupByKey<Renal, RenalGroupByOptions>, BoxplotCalculationObject> boxplot =
                statsPlotService.getBoxPlot(optionsWithContext, filtered);

        return boxPlotUiModelService.toTrellisedBoxPlot(boxplot);
    }

    @ApplyUsedInTflFilter
    @Override
    public AxisOptions<RenalGroupByOptions> getAvailableBoxPlotXAxis(Datasets datasets,
                                                                     Filters<Renal> filters,
                                                                     PopulationFilters populationFilters) {
        return infoService.limitXAxisToVisit(datasets) ? getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER)
                : getAxisOptions(datasets, filters, populationFilters, VISIT_DESCRIPTION, VISIT_NUMBER, STUDY_DEFINED_WEEK, MEASUREMENT_TIME_POINT);
    }

    @ApplyUsedInTflFilter
    public List<TrellisOptions<RenalGroupByOptions>> getTrellisOptions(Datasets datasets, Filters<Renal> filters,
                                                                       PopulationFilters populationFilters) {
        FilterResult<Renal> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(), RenalGroupByOptions.MEASUREMENT, RenalGroupByOptions.ARM);
    }

    @ApplyUsedInTflFilter
    public List<TrellisOptions<RenalGroupByOptions>> getTrellisOptions(Datasets datasets, Filters<Renal> filters,
                                                                       PopulationFilters populationFilters, RenalGroupByOptions yAxisOption) {
        FilterResult<Renal> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                RenalGroupByOptions.MEASUREMENT.<Renal, RenalGroupByOptions>getGroupByOptionAndParams()
                        .supplyContext(getContextSupplier(yAxisOption)),
                RenalGroupByOptions.ARM.getGroupByOptionAndParams());
    }

    @ApplyUsedInTflFilter
    public List<TrellisedRangePlot<Renal, RenalGroupByOptions>> getRangePlot(
            Datasets datasets,
            ChartGroupByOptionsFiltered<Renal, RenalGroupByOptions> settings,
            Filters<Renal> filters, PopulationFilters populationFilters) {
        final ChartGroupByOptions<Renal, RenalGroupByOptions> optionsWithContext = getOptionsWithContext(settings.getSettings(),
                getContextSupplier(settings.getSettings().getOptions()
                        .get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));

        FilterResult<Renal> filtered = getFilteredData(datasets, filters, populationFilters,
                ChartGroupByOptionsFiltered
                        .builder(optionsWithContext)
                        .withFilterByTrellisOptions(settings.getFilterByTrellisOptions())
                        .build());
        Map<GroupByKey<Renal, RenalGroupByOptions>, RangeChartCalculationObject> rangePlot
                = statsPlotService.getMedianRangePlot(optionsWithContext, filtered);

        return rangePlotUiModelService.toTrellisedRangePlot(rangePlot, StatType.MEDIAN);
    }

    @ApplyUsedInTflFilter
    public List<TrellisOptions<RenalGroupByOptions>> getBarChartColorByOptions(
            Datasets datasets, Filters<Renal> filters, PopulationFilters populationFilters) {
        FilterResult<Renal> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                RenalGroupByOptions.CKD_STAGE_NAME);
    }

    private static final Map<RenalGroupByOptions, String> Y_AXIS_UNIT_NAMES = ImmutableMap.of(
            RenalGroupByOptions.REF_RANGE_NORM_VALUE, "Ref. ranges",
            RenalGroupByOptions.TIMES_UPPER_REF_VALUE, "Times upper ref.",
            RenalGroupByOptions.TIMES_LOWER_REF_VALUE, "Times lower ref.",
            RenalGroupByOptions.PERCENTAGE_CHANGE_FROM_BASELINE, "% change"
    );

    private Supplier<Map<RenalGroupByOptions, Object>> getContextSupplier(RenalGroupByOptions yAxisOption) {
        return () -> {
            final Map<RenalGroupByOptions, Object> context = new EnumMap<>(RenalGroupByOptions.class);
            if (Y_AXIS_UNIT_NAMES.containsKey(yAxisOption)) {
                String unit = Y_AXIS_UNIT_NAMES.get(yAxisOption);
                context.put(RenalGroupByOptions.MEASUREMENT, unit);
            }
            return context;
        };
    }

    @ApplyUsedInTflFilter
    @Override
    public List<TrellisedBarChart<Renal, RenalGroupByOptions>> getBarChart(Datasets datasets,
                                                                           ChartGroupByOptionsFiltered<Renal, RenalGroupByOptions> settings,
                                                                           Filters<Renal> filters, PopulationFilters populationFilters,
                                                                           CountType countType) {
        FilterResult<Renal> filtered = getFilteredData(datasets, filters, populationFilters, settings);

        Map<GroupByKey<Renal, RenalGroupByOptions>, BarChartCalculationObject<Renal>> chartData =
                barChartService.getBarChart(settings.getSettings(), countType, filtered);

        return uiModelService.toTrellisedBarChart(chartData, countType);
    }

    @ApplyUsedInTflFilter
    @Override
    public AxisOptions<RenalGroupByOptions> getAvailableBarChartXAxis(Datasets datasets, Filters<Renal> filters, PopulationFilters populationFilters) {
        return infoService.limitXAxisToVisit(datasets) ? getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER)
                : getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER, VISIT_DESCRIPTION, STUDY_DEFINED_WEEK, MEASUREMENT_TIME_POINT);
    }

    @ApplyUsedInTflFilter
    public SelectionDetail getBarChartSelectionDetails(
            Datasets datasets,
            Filters<Renal> filters,
            PopulationFilters populationFilters,
            ChartSelection<Renal, RenalGroupByOptions, ChartSelectionItem<Renal, RenalGroupByOptions>> selection) {
        FilterResult<Renal> filtered = getFilteredData(datasets, filters, populationFilters);
        return barChartService.getSelectionDetails(filtered, selection);
    }

    @Override
    protected Predicate<Renal> composeEventTypeSpecificXAxisBasedPredicate(ChartGroupByOptions.GroupByOptionAndParams<Renal, RenalGroupByOptions> xAxisOption) {
        if (xAxisOption.getGroupByOption() == RenalGroupByOptions.VISIT_NUMBER) {
            return l -> xAxisOption.getGroupByOption().getAttribute().getFunction().apply(l) != null;
        }
        return l -> true;
    }
}
