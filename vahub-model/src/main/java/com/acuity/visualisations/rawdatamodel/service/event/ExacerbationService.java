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
import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartSupportService;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartXAxisSupport;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.plots.OverTimeChartSupportService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.OnsetLineChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.OvertimeChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.RangedColoredBarChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartService;
import com.acuity.visualisations.rawdatamodel.service.plots.RangedOptionService;
import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.IntBin;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.DateFormattedOptionTransformer;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.ExacerbationRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions.OVERTIME_DURATION;

@Service
public class ExacerbationService extends BasePlotEventService<ExacerbationRaw, Exacerbation, ExacerbationGroupByOptions>
        implements BarChartSupportService<Exacerbation, ExacerbationGroupByOptions>,
        BarChartXAxisSupport<Exacerbation, ExacerbationGroupByOptions>,
        OverTimeChartSupportService<Exacerbation, ExacerbationGroupByOptions> {

    @Autowired
    private OnsetLineChartUIModelService onsetLineChartUIModelService;
    @Autowired
    private BarChartService<Exacerbation, ExacerbationGroupByOptions> barChartService;
    @Qualifier("rangedColoredBarChartUIModelService")
    @Autowired
    private RangedColoredBarChartUIModelService uiService;
    @Autowired
    private RangedOptionService rangedOptionService;
    @Autowired
    private BarChartService<Subject, PopulationGroupByOptions> populationBarChartService;
    @Autowired
    private OvertimeChartUIModelService overtimeChartUIModelService;


    public AxisOptions<ExacerbationGroupByOptions> getAvailableOnsetLineChartXAxis(Datasets datasets,
                                                                                   Filters<Exacerbation> filters,
                                                                                   PopulationFilters populationFilters) {

        return getAxisOptions(datasets, filters, populationFilters, OVERTIME_DURATION);
    }

    /**
     * Returns colorby-options for onsetlinechart
     *
     * @param datasets          input datasets
     * @param filters           exacerbations filters
     * @param populationFilters population filters
     * @return list of available colorby-options
     */
    @ApplyUsedInTflFilter
    public List<TrellisOptions<ExacerbationGroupByOptions>> getOnsetLineChartColorByOptions(
            Datasets datasets,
            Filters<Exacerbation> filters,
            PopulationFilters populationFilters
    ) {
        FilterResult<Exacerbation> filteredData = getFilteredData(datasets, filters, populationFilters);
        return TrellisUtil.getTrellisOptions(filteredData.getFilteredEvents(), ExacerbationGroupByOptions.PLANNED_TREATMENT_ARM);
    }

    @ApplyUsedInTflFilter
    public SelectionDetail getOnSetLineChartSelectionDetails(Datasets datasetsObject, ExacerbationFilters exacerbationFilters,
                                                             PopulationFilters populationFilters,
                                                             ChartSelection<Exacerbation, ExacerbationGroupByOptions,
                                                                     ChartSelectionItem<Exacerbation,
                                                                             ExacerbationGroupByOptions>> selection, CountType countType) {
        FilterResult<Exacerbation> filtered = getFilteredData(datasetsObject, exacerbationFilters, populationFilters);

        return barChartService.getSelectionDetails(filtered,
                ChartSelection.of(
                        getOptionsWithContext(selection.getSettings(),
                                getContextSupplier(filtered, selection.getSettings(), countType)),
                        selection.getSelectionItems()));
    }

    /**
     * Returns values for linechart(onset chart) data
     *
     * @return list of trellised linefloatchart
     */
    @ApplyUsedInTflFilter
    public List<TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>> getOnsetLineChartValues(
            Datasets datasets,
            ChartGroupByOptionsFiltered<Exacerbation, ExacerbationGroupByOptions> settings,
            Filters<Exacerbation> filters,
            PopulationFilters populationFilters,
            CountType countType
    ) {
        FilterResult<Exacerbation> filteredData = getFilteredData(datasets, filters, populationFilters);
        List<TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>> trellisedBarCharts = onsetLineChartUIModelService.toTrellised(
                barChartService.getBarChart(getOptionsWithContext(settings.getSettings(),
                        getContextSupplier(filteredData, settings.getSettings(), countType)), countType, filteredData)
        );
        return trellisedBarCharts;
    }

    /**
     * Returns a list of x-axis options for {@param datasets} with {@param filters} and {@param populationFilters}.
     * List is data driven.
     *
     * @param datasets          datasets
     * @param filters           exacerbation filters
     * @param populationFilters subject filters
     * @return a list of x-axis options for.
     */
    @Override
    public AxisOptions<ExacerbationGroupByOptions> getAvailableBarChartXAxis(Datasets datasets,
                                                                             Filters<Exacerbation> filters,
                                                                             PopulationFilters populationFilters) {

        return getAxisOptions(datasets, filters, populationFilters,
                ExacerbationGroupByOptions.NONE,
                ExacerbationGroupByOptions.STUDY_ID,
                ExacerbationGroupByOptions.STUDY_NAME,
                ExacerbationGroupByOptions.STUDY_PART_ID,
                ExacerbationGroupByOptions.DURATION_ON_STUDY,
                ExacerbationGroupByOptions.RANDOMISATION_DATE,
                ExacerbationGroupByOptions.WITHDRAWAL,
                ExacerbationGroupByOptions.REASON_FOR_WITHDRAWAL,
                ExacerbationGroupByOptions.CENTRE,
                ExacerbationGroupByOptions.SEX,
                ExacerbationGroupByOptions.RACE,
                ExacerbationGroupByOptions.AGE,
                ExacerbationGroupByOptions.WEIGHT,
                ExacerbationGroupByOptions.HEIGHT,
                ExacerbationGroupByOptions.FIRST_TREATMENT_DATE,
                ExacerbationGroupByOptions.DEATH,
                ExacerbationGroupByOptions.DATE_OF_DEATH);
    }

    /**
     * Returns a list of color by options for {@param datasets} with {@param filters} and {@param populationFilters}.
     * List is data driven.
     *
     * @param datasets          datasets
     * @param filters           exacerbation filters
     * @param populationFilters subject filters
     * @return a  list of color by options.
     */
    public List<TrellisOptions<ExacerbationGroupByOptions>> getBarChartColorByOptions(Datasets datasets, Filters<Exacerbation> filters,
                                                                                      PopulationFilters populationFilters) {

        FilterResult<Exacerbation> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                ExacerbationGroupByOptions.EXACERBATION_SEVERITY,
                ExacerbationGroupByOptions.HOSPITALISATION,
                ExacerbationGroupByOptions.EMERGENCY_ROOM_VISIT,
                ExacerbationGroupByOptions.DEPOT_CORTICOSTEROID_TREATMENT,
                ExacerbationGroupByOptions.INCREASED_INHALED_CORTICOSTEROID_TREATMENT,
                ExacerbationGroupByOptions.SYSTEMIC_CORTICOSTEROID_TREATMENT,
                ExacerbationGroupByOptions.ANTIBIOTICS_TREATMENT);
    }

    /**
     * Returns counts bar chart data for {@param datasets} with to filters {@param filters} and {@param populationFilters}.
     *
     * @param datasets          datasets
     * @param settings          object within x-axis option, color-by option and trellising option
     * @param filters           exacerbation filters
     * @param populationFilters subject filters
     * @param countType         y-axis option type
     * @return counts bar chart data.
     */
    @Override
    public List<TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>> getBarChart(Datasets datasets,
                                                                                         ChartGroupByOptionsFiltered<Exacerbation,
                                                                                                 ExacerbationGroupByOptions> settings,
                                                                                         Filters<Exacerbation> filters,
                                                                                         PopulationFilters populationFilters,
                                                                                         CountType countType) {
        FilterResult<Exacerbation> filtered = getFilteredData(datasets, filters, populationFilters, settings);

        ExacerbationGroupByOptions xAxisOption = settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS) == null
                ? null : settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).getGroupByOption();

        Map<GroupByKey<Exacerbation, ExacerbationGroupByOptions>, BarChartCalculationObject<Exacerbation>> chartData =
                barChartService.getBarChart(getOptionsWithContext(settings.getSettings(),
                        getContextSupplier(filtered, xAxisOption)), countType, filtered);

        return uiService.toTrellisedBarChart(chartData, countType);
    }

    private Supplier<Map<ExacerbationGroupByOptions, Object>> getContextSupplier(FilterResult<Exacerbation> filteredData,
                                                                                 ChartGroupByOptions<Exacerbation,
                                                                                         ExacerbationGroupByOptions> settings, CountType countType) {
        return () -> {
            final Map<ExacerbationGroupByOptions, Object> context = new EnumMap<>(ExacerbationGroupByOptions.class);
            if (countType.isCumulativeType()) {
                filteredData.getFilteredResult()
                        .stream()
                        .filter(e -> Objects.nonNull(e.getEndDate()))
                        .map(e -> ((List) Attributes
                                .getBinnedAttribute("", settings.getOptions()
                                                .get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).getParams(),
                                        Exacerbation::getEndDate)
                                .getFunction().apply(e)).stream().min(Comparator.naturalOrder())
                                .orElse(null))
                        .filter(bin -> bin instanceof IntBin)
                        .max(Comparator.comparing(e -> ((IntBin) e).getEnd()))
                        .ifPresent(val -> context.put(OVERTIME_DURATION, val));
            }

            return context;
        };
    }

    private Supplier<Map<ExacerbationGroupByOptions, Object>> getContextSupplier(FilterResult<Exacerbation> filtered,
                                                                                 ExacerbationGroupByOptions xAxis) {
        return () -> {
            EnumMap<ExacerbationGroupByOptions, Object> map = new EnumMap<>(ExacerbationGroupByOptions.class);
            if (xAxis != null && GroupByOption.getRangeOptionAnnotation(xAxis) != null) {
                map.put(xAxis, rangedOptionService
                        .<Exacerbation, ExacerbationGroupByOptions>getRangeFunction(filtered, true, null)
                        .apply(xAxis));
            } else if (xAxis != null && GroupByOption.getDateFormattedAnnotation(xAxis) != null) {
                map.put(xAxis, DateFormattedOptionTransformer.getDateFormattedFunction(filtered));
            }
            return map;
        };
    }

    @Override
    public SelectionDetail getSelectionDetails(Datasets datasets, Filters<Exacerbation> filters,
                                               PopulationFilters populationFilters,
                                               ChartSelection<Exacerbation, ExacerbationGroupByOptions,
                                                       ChartSelectionItem<Exacerbation, ExacerbationGroupByOptions>> selection) {
        FilterResult<Exacerbation> filtered = getFilteredData(datasets, filters, populationFilters, null);

        ExacerbationGroupByOptions xAxisOption = selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS) == null
                ? null : selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).getGroupByOption();

        return barChartService.getSelectionDetails(filtered, ChartSelection.of(getOptionsWithContext(selection.getSettings(),
                getContextSupplier(filtered, xAxisOption)), selection.getSelectionItems()));
    }

    /**
     * Returns values for line bar chart data.
     *
     * @param datasets          datasets
     * @param eventSettings     object within x-axis option, color-by option and trellising option
     * @param filters           exacerbation filters
     * @param populationFilters subject filters
     * @return values line bar chart data
     */
    @ApplyUsedInTflFilter
    @Override
    public List<TrellisedOvertime<Exacerbation, ExacerbationGroupByOptions>> getLineBarChart(Datasets datasets, ChartGroupByOptionsFiltered<Exacerbation,
            ExacerbationGroupByOptions> eventSettings, Filters<Exacerbation> filters, PopulationFilters populationFilters) {
        FilterResult<Exacerbation> filtered = getFilteredData(datasets, filters, populationFilters, eventSettings);

        Map<GroupByKey<Exacerbation, ExacerbationGroupByOptions>, BarChartCalculationObject<Exacerbation>> chartData =
                barChartService.getBarChart(eventSettings.getSettings(), CountType.COUNT_OF_EVENTS, filtered);

        Map<GroupByKey<Subject, PopulationGroupByOptions>, BarChartCalculationObject<Subject>> lineData =
                populationBarChartService.getBarChart(getPopulationLineSettings(eventSettings, filtered.getFilteredResult()),
                        CountType.COUNT_OF_EVENTS, filtered.getPopulationFilterResult());

        return overtimeChartUIModelService.toTrellisedOvertime(chartData, lineData);
    }

    /**
     * Returns a list of x-axis options for {@param datasets} with {@param filters} and {@param populationFilters}.
     *
     * @param datasets          datasets
     * @param filters           exacerbation filters
     * @param populationFilters subject filters
     * @return a list of x-axis options
     */
    @ApplyUsedInTflFilter
    @Override
    public AxisOptions<ExacerbationGroupByOptions> getAvailableOverTimeChartXAxis(
            Datasets datasets, Filters<Exacerbation> filters, PopulationFilters populationFilters) {
        return getAxisOptions(datasets, filters, populationFilters, ExacerbationGroupByOptions.OVERTIME_DURATION);
    }

    public SelectionDetail getOverTimeLineBarChartSelectionDetails(Datasets datasetsObject, ExacerbationFilters exacerbationFilters,
                                                                   PopulationFilters populationFilters,
                                                                   ChartSelection<Exacerbation, ExacerbationGroupByOptions,
                                                                           ChartSelectionItem<Exacerbation,
                                                                                   ExacerbationGroupByOptions>> selection) {
        FilterResult<Exacerbation> filtered = getFilteredData(datasetsObject, exacerbationFilters, populationFilters);
        return barChartService.getSelectionDetails(filtered, selection);
    }

    /**
     * Returns colorby-options for exacerbation over time.
     *
     * @param datasets          input datasets
     * @param filters           exacerbations filters
     * @param populationFilters population filters
     * @return list of available color-by-options
     */
    @ApplyUsedInTflFilter
    public List<TrellisOptions<ExacerbationGroupByOptions>> getLineBarChartColorByOptions(
            Datasets datasets,
            Filters<Exacerbation> filters,
            PopulationFilters populationFilters
    ) {
        FilterResult<Exacerbation> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                ExacerbationGroupByOptions.EXACERBATION_SEVERITY,
                ExacerbationGroupByOptions.HOSPITALISATION,
                ExacerbationGroupByOptions.EMERGENCY_ROOM_VISIT,
                ExacerbationGroupByOptions.DEPOT_CORTICOSTEROID_TREATMENT,
                ExacerbationGroupByOptions.INCREASED_INHALED_CORTICOSTEROID_TREATMENT,
                ExacerbationGroupByOptions.SYSTEMIC_CORTICOSTEROID_TREATMENT,
                ExacerbationGroupByOptions.ANTIBIOTICS_TREATMENT);
    }
}
