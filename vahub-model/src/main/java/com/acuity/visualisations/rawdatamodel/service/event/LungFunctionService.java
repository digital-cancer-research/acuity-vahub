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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.LungFunctionRaw;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.BoxplotCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.RangeChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions.MEASUREMENT_TIME_POINT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions.PERCENTAGE_CHANGE_FROM_BASELINE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions.VISIT_DESCRIPTION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions.VISIT_NUMBER;


@Service
public class LungFunctionService
        extends BasePlotEventService<LungFunctionRaw, LungFunction, LungFunctionGroupByOptions>
        implements BoxPlotSupportService<LungFunction, LungFunctionGroupByOptions>,
        RangePlotSupportService<LungFunction, LungFunctionGroupByOptions> {

    @Autowired
    private BoxPlotUiModelService boxPlotUiModelService;

    @Autowired
    private RangePlotUiModelService rangePlotUiModelService;

    @Autowired
    private StatsPlotService<LungFunction, LungFunctionGroupByOptions> statsPlotService;

    @Autowired
    private InfoService infoService;

    @ApplyUsedInTflFilter
    @Override
    public List<TrellisedBoxPlot<LungFunction, LungFunctionGroupByOptions>> getBoxPlot(
            Datasets datasets,
            ChartGroupByOptionsFiltered<LungFunction, LungFunctionGroupByOptions> settings,
            Filters<LungFunction> filters,
            PopulationFilters populationFilters) {

        final ChartGroupByOptions<LungFunction, LungFunctionGroupByOptions> optionsWithContext =
                getOptionsWithContext(settings.getSettings(),
                        getContextSupplier(settings.getSettings().getOptions().get(
                                ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));

        FilterResult<LungFunction> filtered = getFilteredData(datasets,
                filters, populationFilters,
                ChartGroupByOptionsFiltered
                        .builder(optionsWithContext)
                        .withFilterByTrellisOptions(settings.getFilterByTrellisOptions())
                        .build());

        Map<GroupByKey<LungFunction, LungFunctionGroupByOptions>, BoxplotCalculationObject> boxplot =
                statsPlotService.getBoxPlot(optionsWithContext, filtered);
        return boxPlotUiModelService.toTrellisedBoxPlot(boxplot);
    }

    @ApplyUsedInTflFilter
    @Override
    public AxisOptions<LungFunctionGroupByOptions> getAvailableBoxPlotXAxis(
            Datasets datasets,
            Filters<LungFunction> filters,
            PopulationFilters populationFilters) {
        return infoService.limitXAxisToVisit(datasets) ? getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER)
                : getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER, VISIT_DESCRIPTION, MEASUREMENT_TIME_POINT);
    }

    @Override
    @ApplyUsedInTflFilter
    public AxisOptions<LungFunctionGroupByOptions> getAvailableRangePlotXAxis(Datasets datasets,
                                                                              Filters<LungFunction> filters,
                                                                              PopulationFilters populationFilters) {
        return infoService.limitXAxisToVisit(datasets) ? getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER)
                : getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER, VISIT_DESCRIPTION, MEASUREMENT_TIME_POINT);
    }

    @ApplyUsedInTflFilter
    @Override
    public SelectionDetail getRangedSelectionDetails(
            Datasets datasets,
            Filters<LungFunction> filters,
            PopulationFilters populationFilters,
            ChartSelection<LungFunction, LungFunctionGroupByOptions,
                    ChartSelectionItemRange<LungFunction, LungFunctionGroupByOptions, Double>> selection) {
        FilterResult<LungFunction> filtered = getFilteredData(datasets, filters, populationFilters);
        final ChartGroupByOptions<LungFunction, LungFunctionGroupByOptions> optionsWithContext = getOptionsWithContext(selection.getSettings(),
                getContextSupplier(selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));
        return statsPlotService.getRangedSelectionDetails(filtered, ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    @ApplyUsedInTflFilter
    @Override
    public SelectionDetail getSelectionDetails(
            Datasets datasets,
            Filters<LungFunction> filters,
            PopulationFilters populationFilters,
            ChartSelection<LungFunction, LungFunctionGroupByOptions,
                    ChartSelectionItem<LungFunction, LungFunctionGroupByOptions>> selection) {
        FilterResult<LungFunction> filtered = getFilteredData(datasets, filters, populationFilters);
        final ChartGroupByOptions<LungFunction, LungFunctionGroupByOptions> optionsWithContext = getOptionsWithContext(selection.getSettings(),
                getContextSupplier(selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));
        return statsPlotService.getSelectionDetails(filtered, ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    @ApplyUsedInTflFilter
    public List<TrellisOptions<LungFunctionGroupByOptions>> getTrellisOptions(
            Datasets datasets,
            Filters<LungFunction> filters,
            PopulationFilters populationFilters,
            LungFunctionGroupByOptions yAxisOption) {
        FilterResult<LungFunction> filtered = getFilteredData(datasets, filters, populationFilters, null);

        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                LungFunctionGroupByOptions.MEASUREMENT.<LungFunction,
                        LungFunctionGroupByOptions>getGroupByOptionAndParams()
                        .supplyContext(getContextSupplier(yAxisOption)),
                LungFunctionGroupByOptions.ARM.getGroupByOptionAndParams());
    }

    @ApplyUsedInTflFilter
    public List<TrellisedRangePlot<LungFunction, LungFunctionGroupByOptions>> getRangePlot(
            Datasets datasets,
            ChartGroupByOptionsFiltered<LungFunction, LungFunctionGroupByOptions> settings,
            Filters<LungFunction> filters,
            PopulationFilters populationFilters,
            StatType statType) {
        final ChartGroupByOptions<LungFunction, LungFunctionGroupByOptions> optionsWithContext = getOptionsWithContext(settings.getSettings(),
                getContextSupplier(settings.getSettings().getOptions()
                        .get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));

        FilterResult<LungFunction> filtered = getFilteredData(datasets, filters, populationFilters,
                ChartGroupByOptionsFiltered
                        .builder(optionsWithContext)
                        .withFilterByTrellisOptions(settings.getFilterByTrellisOptions())
                        .build());
        Map<GroupByKey<LungFunction, LungFunctionGroupByOptions>, RangeChartCalculationObject> rangePlot
                = statsPlotService.getMedianRangePlot(optionsWithContext, filtered);

        return rangePlotUiModelService.toTrellisedRangePlot(rangePlot, StatType.MEDIAN);
    }

    private Supplier<Map<LungFunctionGroupByOptions, Object>> getContextSupplier(LungFunctionGroupByOptions yAxisOption) {
        return () -> {
            final Map<LungFunctionGroupByOptions, Object> context = new EnumMap<>(LungFunctionGroupByOptions.class);
            if (yAxisOption == PERCENTAGE_CHANGE_FROM_BASELINE) {
                context.put(LungFunctionGroupByOptions.MEASUREMENT, "% change");
            }
            return context;
        };
    }
}
