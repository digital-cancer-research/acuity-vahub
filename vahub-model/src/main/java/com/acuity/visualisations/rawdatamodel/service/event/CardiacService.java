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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CardiacGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.CardiacRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.BoxplotCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.RangeChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CardiacGroupByOptions.MEASUREMENT_TIME_POINT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CardiacGroupByOptions.PERCENTAGE_CHANGE_FROM_BASELINE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CardiacGroupByOptions.STUDY_DEFINED_WEEK;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CardiacGroupByOptions.VISIT_DESCRIPTION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CardiacGroupByOptions.VISIT_NUMBER;

@Service
@RequiredArgsConstructor
public class CardiacService extends BasePlotEventService<CardiacRaw, Cardiac, CardiacGroupByOptions>
        implements RangePlotSupportService<Cardiac, CardiacGroupByOptions>,
        BoxPlotSupportService<Cardiac, CardiacGroupByOptions> {

    private final StatsPlotService<Cardiac, CardiacGroupByOptions> statsPlotService;
    private final RangePlotUiModelService rangePlotUiModelService;
    private final BoxPlotUiModelService boxPlotUiModelService;
    private final InfoService infoService;

    @ApplyUsedInTflFilter
    public List<TrellisOptions<CardiacGroupByOptions>> getMeanRangeTrellisOptions(
            Datasets datasets,
            Filters<Cardiac> filters,
            PopulationFilters populationFilters,
            CardiacGroupByOptions yAxisOption) {
        FilterResult<Cardiac> filtered = getFilteredData(datasets, filters, populationFilters, null);

        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                CardiacGroupByOptions.MEASUREMENT.<Cardiac,
                        CardiacGroupByOptions>getGroupByOptionAndParams()
                        .supplyContext(getContextSupplier(yAxisOption)),
                CardiacGroupByOptions.ARM.getGroupByOptionAndParams());
    }

    @Override
    @ApplyUsedInTflFilter
    public AxisOptions<CardiacGroupByOptions> getAvailableRangePlotXAxis(
            Datasets datasets,
            Filters<Cardiac> filters, PopulationFilters populationFilters) {
        return infoService.limitXAxisToVisit(datasets) ? getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER)
                : getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER, VISIT_DESCRIPTION, STUDY_DEFINED_WEEK, MEASUREMENT_TIME_POINT);
    }

    @ApplyUsedInTflFilter
    public List<TrellisedRangePlot<Cardiac, CardiacGroupByOptions>> getRangePlot(Datasets datasets,
                                                                                 ChartGroupByOptionsFiltered<Cardiac, CardiacGroupByOptions> settings,
                                                                                 Filters<Cardiac> filters, PopulationFilters populationFilters,
                                                                                 StatType statType) {
        ChartGroupByOptions<Cardiac, CardiacGroupByOptions> optionsWithContext = getOptionsWithContext(settings.getSettings(),
                getContextSupplier(settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));

        FilterResult<Cardiac> filtered = getFilteredData(datasets, filters, populationFilters, ChartGroupByOptionsFiltered
                .builder(optionsWithContext)
                .withFilterByTrellisOptions(settings.getFilterByTrellisOptions())
                .build());
        Map<GroupByKey<Cardiac, CardiacGroupByOptions>, RangeChartCalculationObject> rangePlot =
                statsPlotService.getMedianRangePlot(optionsWithContext, filtered);
        return rangePlotUiModelService.toTrellisedRangePlot(rangePlot, statType);
    }

    private Supplier<Map<CardiacGroupByOptions, Object>> getContextSupplier(
            CardiacGroupByOptions yAxisOption) {
        return () -> {
            Map<CardiacGroupByOptions, Object> context = new EnumMap<>(
                    CardiacGroupByOptions.class);
            if (yAxisOption == PERCENTAGE_CHANGE_FROM_BASELINE) {
                context.put(CardiacGroupByOptions.MEASUREMENT, "% change");
            }
            return context;
        };
    }

    @Override
    @ApplyUsedInTflFilter
    public SelectionDetail getRangedSelectionDetails(
            Datasets datasets, Filters<Cardiac> filters,
            PopulationFilters populationFilters,
            ChartSelection<Cardiac, CardiacGroupByOptions, ChartSelectionItemRange<Cardiac, CardiacGroupByOptions, Double>> selection) {
        FilterResult<Cardiac> filtered = getFilteredData(datasets, filters, populationFilters);

        ChartGroupByOptions<Cardiac, CardiacGroupByOptions> optionsWithContext = getOptionsWithContext(
                selection.getSettings(), getContextSupplier(
                        selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS)
                                .getGroupByOption()));
        return statsPlotService.getRangedSelectionDetails(filtered, ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    @Override
    @ApplyUsedInTflFilter
    public SelectionDetail getSelectionDetails(
            Datasets datasets, Filters<Cardiac> filters,
            PopulationFilters populationFilters,
            ChartSelection<Cardiac, CardiacGroupByOptions, ChartSelectionItem<Cardiac, CardiacGroupByOptions>> selection) {
        FilterResult<Cardiac> filtered = getFilteredData(datasets, filters, populationFilters);
        ChartGroupByOptions<Cardiac, CardiacGroupByOptions> optionsWithContext = getOptionsWithContext(selection.getSettings(),
                getContextSupplier(selection.getSettings().getOptions().get(
                        ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));
        return statsPlotService.getSelectionDetails(filtered,
                ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    @Override
    @ApplyUsedInTflFilter
    public List<TrellisedBoxPlot<Cardiac, CardiacGroupByOptions>> getBoxPlot(Datasets datasets,
                                                                             ChartGroupByOptionsFiltered<Cardiac, CardiacGroupByOptions> settings,
                                                                             Filters<Cardiac> filters, PopulationFilters populationFilters) {
        ChartGroupByOptions<Cardiac, CardiacGroupByOptions> optionsWithContext = getOptionsWithContext(settings.getSettings(),
                getContextSupplier(settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));

        FilterResult<Cardiac> filtered = getFilteredData(datasets, filters, populationFilters, ChartGroupByOptionsFiltered
                .builder(optionsWithContext)
                .withFilterByTrellisOptions(settings.getFilterByTrellisOptions())
                .build());

        Map<GroupByKey<Cardiac, CardiacGroupByOptions>, BoxplotCalculationObject> boxplot = statsPlotService.getBoxPlot(optionsWithContext, filtered);
        return boxPlotUiModelService.toTrellisedBoxPlot(boxplot);
    }

    @Override
    @ApplyUsedInTflFilter
    public AxisOptions<CardiacGroupByOptions> getAvailableBoxPlotXAxis(Datasets datasets, Filters<Cardiac> filters,
                                                                       PopulationFilters populationFilters) {
        return infoService.limitXAxisToVisit(datasets) ? getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER)
                : getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER, VISIT_DESCRIPTION,
                STUDY_DEFINED_WEEK, MEASUREMENT_TIME_POINT);
    }

    @ApplyUsedInTflFilter
    public List<TrellisOptions<CardiacGroupByOptions>> getTrellisOptions(
            Datasets datasets,
            Filters<Cardiac> filters,
            PopulationFilters populationFilters,
            CardiacGroupByOptions yAxisOption) {
        FilterResult<Cardiac> filtered = getFilteredData(datasets, filters, populationFilters, null);

        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                CardiacGroupByOptions.MEASUREMENT.<Cardiac,
                        CardiacGroupByOptions>getGroupByOptionAndParams()
                        .supplyContext(getContextSupplier(yAxisOption)),
                CardiacGroupByOptions.ARM.getGroupByOptionAndParams());
    }
}
