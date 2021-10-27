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
import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.ConmedRangedColoredBarChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartService;
import com.acuity.visualisations.rawdatamodel.service.plots.RangedOptionService;
import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ConmedGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.ConmedRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
public class ConmedsService extends BasePlotEventService<ConmedRaw, Conmed, ConmedGroupByOptions> implements SsvSummaryTableService {

    private final BarChartService<Conmed, ConmedGroupByOptions> barChartService;
    private final ConmedRangedColoredBarChartUIModelService uiService;
    private final RangedOptionService rangedOptionService;

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, ConmedFilters.empty());
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, Conmed.class, ConmedRaw.class);
    }

    @Override
    public String getSsvTableName() {
        return "conmeds";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "CONMEDS";
    }

    @Override
    public String getHeaderName() {
        return "PATIENT HISTORY";
    }

    @Override
    public double getOrder() {
        return 6;
    }

    @ApplyUsedInTflFilter
    public AxisOptions<ConmedGroupByOptions> getAvailableBarChartXAxis(
            Datasets datasets, Filters<Conmed> filters, PopulationFilters populationFilters) {
        return getAxisOptions(datasets, filters, populationFilters,
                ConmedGroupByOptions.MEDICATION_NAME,
                ConmedGroupByOptions.ATC_CODE
        );
    }

    @ApplyUsedInTflFilter
    public List<TrellisedBarChart<Conmed, ConmedGroupByOptions>> getBarChart(
            Datasets datasets, ChartGroupByOptionsFiltered<Conmed, ConmedGroupByOptions> settings,
            Filters<Conmed> filters, PopulationFilters populationFilters, CountType countType) {

        FilterResult<Conmed> filtered = getFilteredData(datasets, filters, populationFilters, settings);

        ConmedGroupByOptions colorByOption = settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY) == null
                ? null : settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY).getGroupByOption();
        ConmedGroupByOptions xAxisOption = settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS) == null
                ? null : settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).getGroupByOption();

        Map<GroupByKey<Conmed, ConmedGroupByOptions>, BarChartCalculationObject<Conmed>> chartData =
                barChartService.getBarChart(getOptionsWithContext(
                        settings.getSettings(), getContextSupplier(filtered, colorByOption, xAxisOption)),
                        countType, filtered);

        return uiService.toTrellisedBarChart(chartData, countType);
    }

    private Supplier<Map<ConmedGroupByOptions, Object>> getContextSupplier(FilterResult<Conmed> filtered,
                                                                           ConmedGroupByOptions colorByOption,
                                                                           ConmedGroupByOptions xAxisOption) {

        return () -> {
            final Map<ConmedGroupByOptions, Object> context = new EnumMap<>(ConmedGroupByOptions.class);
            if (xAxisOption == ConmedGroupByOptions.ATC_CODE) {
                context.put(ConmedGroupByOptions.ATC_CODE,
                        filtered.getFilteredEvents().stream().filter(c -> c.getEvent().getAtcCode() != null).collect(
                                Collectors.toMap(c -> c.getEvent().getAtcCode(),
                                        c -> c.getEvent().getAtcText() == null ? "" : c.getEvent().getAtcText(),
                                        (oldValue, newValue) -> "".equals(newValue) || oldValue.contains(oldValue)
                                                ? oldValue : oldValue + ", " + newValue)));
            }

            context.putAll(Stream.of(ConmedGroupByOptions.values())
                    .filter(o -> GroupByOption.getRangeOptionAnnotation(o) != null)
                    .filter(o -> o == colorByOption)
                    .collect(toMap(o -> o, rangedOptionService.getRangeFunction(filtered, false, null),
                            (u, v) -> {
                                throw new IllegalStateException(String.format("Duplicate key %s", u));
                            },
                            () -> new EnumMap<>(ConmedGroupByOptions.class))));
            return context;
        };
    }

    @ApplyUsedInTflFilter
    public List<TrellisOptions<ConmedGroupByOptions>> getTrellisOptions(
            Datasets datasets,
            Filters<Conmed> filters,
            PopulationFilters populationFilters) {
        FilterResult<Conmed> filtered = getFilteredData(datasets, filters, populationFilters, null);

        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(), ConmedGroupByOptions.ARM);
    }

    @ApplyUsedInTflFilter
    public SelectionDetail getSelectionDetails(
            Datasets datasets, Filters<Conmed> filters, PopulationFilters populationFilters,
            ChartSelection<Conmed, ConmedGroupByOptions, ChartSelectionItem<Conmed, ConmedGroupByOptions>> selection) {
        FilterResult<Conmed> filtered = getFilteredData(datasets, filters, populationFilters, null);

        ConmedGroupByOptions colorByOption = selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY) == null
                ? null : selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY).getGroupByOption();

        ChartGroupByOptions<Conmed, ConmedGroupByOptions> optionsWithContext =
                getOptionsWithContext(selection.getSettings(), getContextSupplier(filtered, colorByOption, null));
        return barChartService.getSelectionDetails(filtered, ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    @ApplyUsedInTflFilter
    public List<TrellisOptions<ConmedGroupByOptions>> getBarChartColorByOptions(
            Datasets datasets, Filters<Conmed> filters, PopulationFilters populationFilters) {
        FilterResult<Conmed> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                ConmedGroupByOptions.ANATOMICAL_GROUP.getGroupByOptionAndParams(),
                ConmedGroupByOptions.DOSE.<Conmed, ConmedGroupByOptions>getGroupByOptionAndParams()
                        .supplyContext(getContextSupplier(filtered, ConmedGroupByOptions.DOSE, null)),
                ConmedGroupByOptions.DOSE_UNITS.getGroupByOptionAndParams(),
                ConmedGroupByOptions.ONGOING.getGroupByOptionAndParams(),
                ConmedGroupByOptions.CONMED_STARTED_PRIOR_TO_STUDY.getGroupByOptionAndParams(),
                ConmedGroupByOptions.CONMED_ENDED_PRIOR_TO_STUDY.getGroupByOptionAndParams());
    }
}
