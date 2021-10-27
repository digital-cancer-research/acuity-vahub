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
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.plots.BoxPlotSupportService;
import com.acuity.visualisations.rawdatamodel.service.plots.RangePlotSupportService;
import com.acuity.visualisations.rawdatamodel.service.plots.ShiftPlotSupportService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.BoxPlotUiModelService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.ColoredRangePlotUiModelService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.ShiftPlotUiModelService;
import com.acuity.visualisations.rawdatamodel.service.plots.StatsPlotService;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedShiftPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.BoxplotCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.RangeChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.plots.ShiftPlotCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions.MEASUREMENT_TIME_POINT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions.STUDY_DEFINED_WEEK;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions.VISIT_DESCRIPTION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions.VISIT_NUMBER;
import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import static java.util.stream.Collectors.toSet;

@Service
public class LabService extends BasePlotEventService<LabRaw, Lab, LabGroupByOptions>
        implements
        BoxPlotSupportService<Lab, LabGroupByOptions>,
        ShiftPlotSupportService<Lab, LabGroupByOptions>,
        RangePlotSupportService<Lab, LabGroupByOptions>,
        SsvSummaryTableService {

    @Autowired
    private StatsPlotService<Lab, LabGroupByOptions> statsPlotService;
    @Autowired
    private BoxPlotUiModelService boxPlotUiModelService;
    @Autowired
    private ShiftPlotUiModelService shiftPlotUiModelService;
    @Autowired
    private ColoredRangePlotUiModelService rangePlotUiModelService;
    @Autowired
    private InfoService infoService;


    /**********************
     * Get trellis options
     */

    @ApplyUsedInTflFilter
    public List<TrellisOptions<LabGroupByOptions>> getTrellisOptions(Datasets datasets, Filters<Lab> filters,
                                                                     PopulationFilters populationFilters, LabGroupByOptions yAxisOption) {
        FilterResult<Lab> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                LabGroupByOptions.MEASUREMENT.<Lab, LabGroupByOptions>getGroupByOptionAndParams()
                        .supplyContext(getContextSupplier(yAxisOption)),
                LabGroupByOptions.ARM.getGroupByOptionAndParams());
    }

    @ApplyUsedInTflFilter
    public List<TrellisOptions<LabGroupByOptions>> getTrellisOptions(Datasets datasets, Filters<Lab> filters,
                                                                     PopulationFilters populationFilters) {
        FilterResult<Lab> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                LabGroupByOptions.MEASUREMENT, LabGroupByOptions.ARM);
    }

    @ApplyUsedInTflFilter
    public List<TrellisOptions<LabGroupByOptions>> getRangeTrellisOptions(Datasets datasets, Filters<Lab> filters,
                                                                          PopulationFilters populationFilters, LabGroupByOptions yAxisOption) {
        FilterResult<Lab> filtered = getFilteredData(datasets, filters, populationFilters);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                LabGroupByOptions.MEASUREMENT.<Lab, LabGroupByOptions>getGroupByOptionAndParams()
                        .supplyContext(getContextSupplier(yAxisOption)));
    }

    @ApplyUsedInTflFilter
    public List<TrellisOptions<LabGroupByOptions>> getRangeSeriesByOptions(Datasets datasets, Filters<Lab> filters, PopulationFilters populationFilters) {
        FilterResult<Lab> filtered = getFilteredData(datasets, filters, populationFilters);

        if (datasets.isAcuityType()) {
            return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(), LabGroupByOptions.SOURCE_TYPE);
        } else {
            return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                    LabGroupByOptions.ARM, LabGroupByOptions.SOURCE_TYPE, LabGroupByOptions.ARM_AND_SOURCE_TYPE);
        }
    }

    /**********************
     * Get x axis options
     */

    @Override
    @ApplyUsedInTflFilter
    public AxisOptions<LabGroupByOptions> getAvailableBoxPlotXAxis(Datasets datasets, Filters<Lab> filters,
                                                                   PopulationFilters populationFilters) {
        return getAvailablePlotXAxis(datasets, filters, populationFilters);
    }


    @Override
    @ApplyUsedInTflFilter
    public AxisOptions<LabGroupByOptions> getAvailableShiftPlotXAxis(Datasets datasets, Filters<Lab> filters, PopulationFilters populationFilters) {
        return getAvailablePlotXAxis(datasets, filters, populationFilters);
    }

    @Override
    @ApplyUsedInTflFilter
    public AxisOptions<LabGroupByOptions> getAvailableRangePlotXAxis(Datasets datasets, Filters<Lab> filters, PopulationFilters populationFilters) {
        return getAvailablePlotXAxis(datasets, filters, populationFilters);
    }

    private AxisOptions<LabGroupByOptions> getAvailablePlotXAxis(Datasets datasets, Filters<Lab> filters, PopulationFilters populationFilters) {
        return infoService.limitXAxisToVisit(datasets) ? getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER)
                : getAxisOptions(datasets, filters, populationFilters, VISIT_NUMBER, VISIT_DESCRIPTION, STUDY_DEFINED_WEEK, MEASUREMENT_TIME_POINT);
    }

    /**********************
     * Get graph data
     */

    @ApplyUsedInTflFilter
    @Override
    public List<TrellisedBoxPlot<Lab, LabGroupByOptions>> getBoxPlot(Datasets datasets, ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settings,
                                                                     Filters<Lab> filters, PopulationFilters populationFilters) {

        final ChartGroupByOptions<Lab, LabGroupByOptions> optionsWithContext = getOptionsWithContext(settings.getSettings(),
                getContextSupplier(settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));

        FilterResult<Lab> filtered = getFilteredData(datasets, filters, populationFilters, ChartGroupByOptionsFiltered
                .builder(optionsWithContext)
                .withFilterByTrellisOptions(settings.getFilterByTrellisOptions())
                .build());


        Map<GroupByKey<Lab, LabGroupByOptions>, BoxplotCalculationObject> boxplot = statsPlotService.getBoxPlot(optionsWithContext, filtered);
        return boxPlotUiModelService.toTrellisedBoxPlot(boxplot);
    }


    @ApplyUsedInTflFilter
    @Override
    public List<TrellisedShiftPlot<Lab, LabGroupByOptions>> getShiftPlot(Datasets datasets, ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settings,
                                                                         Filters<Lab> filters, PopulationFilters populationFilters) {
        final ChartGroupByOptions<Lab, LabGroupByOptions> optionsWithContext = getOptionsWithContext(settings.getSettings(),
                getContextSupplier(settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));

        FilterResult<Lab> filtered = getFilteredData(datasets, filters, populationFilters, ChartGroupByOptionsFiltered
                .builder(optionsWithContext)
                .withFilterByTrellisOptions(settings.getFilterByTrellisOptions())
                .build());
        Map<GroupByKey<Lab, LabGroupByOptions>, ShiftPlotCalculationObject> shiftPlot = statsPlotService.getShiftPlot(optionsWithContext, filtered);
        return shiftPlotUiModelService.toTrellisedBoxPlot(shiftPlot);
    }


    @ApplyUsedInTflFilter
    public List<TrellisedRangePlot<Lab, LabGroupByOptions>> getRangePlot(Datasets datasets,
                                                                         ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settings,
                                                                         Filters<Lab> filters, PopulationFilters populationFilters,
                                                                         StatType statType) {
        final ChartGroupByOptions<Lab, LabGroupByOptions> optionsWithContext = getOptionsWithContext(settings.getSettings(),
                getContextSupplier(settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));

        FilterResult<Lab> filtered = getFilteredData(datasets, filters, populationFilters, ChartGroupByOptionsFiltered
                .builder(optionsWithContext)
                .withFilterByTrellisOptions(settings.getFilterByTrellisOptions())
                .build());
        Map<GroupByKey<Lab, LabGroupByOptions>, RangeChartCalculationObject> rangePlot = statsPlotService.getRangePlot(optionsWithContext, filtered);
        return rangePlotUiModelService.toTrellisedRangePlot(rangePlot, statType);
    }

    @Override
    @ApplyUsedInTflFilter
    public SelectionDetail getRangedSelectionDetails(Datasets datasets, Filters<Lab> filters,
                                                     PopulationFilters populationFilters,
                                                     ChartSelection<Lab, LabGroupByOptions,
                                                             ChartSelectionItemRange<Lab, LabGroupByOptions, Double>> selection) {
        FilterResult<Lab> filtered = getFilteredData(datasets, filters, populationFilters);
        final ChartGroupByOptions<Lab, LabGroupByOptions> optionsWithContext = getOptionsWithContext(selection.getSettings(),
                getContextSupplier(selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));
        return statsPlotService.getRangedSelectionDetails(filtered, ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    @Override
    @ApplyUsedInTflFilter
    public SelectionDetail getSelectionDetails(Datasets datasets, Filters<Lab> filters,
                                               PopulationFilters populationFilters,
                                               ChartSelection<Lab, LabGroupByOptions,
                                                       ChartSelectionItem<Lab, LabGroupByOptions>> selection) {
        FilterResult<Lab> filtered = getFilteredData(datasets, filters, populationFilters);
        final ChartGroupByOptions<Lab, LabGroupByOptions> optionsWithContext = getOptionsWithContext(selection.getSettings(),
                getContextSupplier(selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).getGroupByOption()));
        return statsPlotService.getSelectionDetails(filtered, ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    /**********************
     * Single Subject View methods
     */

    public List<Map<String, String>> getOutOfRangeSingleSubjectData(Datasets datasets, String subjectId, Filters<Lab> eventFilters) {
        final FilterResult<Lab> filteredData = getFilteredData(datasets, eventFilters, PopulationFilters.empty(), null,
                s -> s.getSubjectId().equals(subjectId) || subjectId.equals(s.getSubjectCode()));

        Comparator<Lab> sortByDate = Comparator.comparing(lc -> lc.getEvent().getMeasurementTimePoint());
        Comparator<Lab> sortByLabCode = Comparator.comparing(lc -> lc.getEvent().getLabCode(), Comparator.nullsLast(Comparator.naturalOrder()));
        Predicate<Lab> pastEvents = lab -> lab.getEvent().getMeasurementTimePoint() != null && lab.getEvent().getValue() != null
                && lab.getSubject().getBaselineDate() != null && lab.getEvent().getMeasurementTimePoint().before(lab.getSubject().getBaselineDate());

        Map<String, Optional<Lab>> groupedByLabCode = filteredData.stream()
                .filter(pastEvents)
                .collect(
                        Collectors.groupingBy(lab -> lab.getEvent().getLabCode(),
                                Collectors.maxBy(sortByDate))
                );

        List<Lab> outOfRangeLabs = groupedByLabCode.values().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(l -> l.getEvent().isOutOfNormalRange())
                .sorted(sortByDate.thenComparing(sortByLabCode))
                .collect(Collectors.toList());
        return ssvCommonService.getColumnData(DatasetType.fromDatasets(datasets), outOfRangeLabs);
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getOutOfRangeSingleSubjectData(datasets, subjectId, LabFilters.empty());
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return ssvCommonService.getColumns(datasetType, Lab.class, LabRaw.class);
    }

    @Override
    public String getSsvTableName() {
        return "labs";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "OUT OF RANGE LAB DATA OF CLINICAL SIGNIFICANCE";
    }

    @Override
    public String getHeaderName() {
        return "PATIENT HISTORY";
    }

    @Override
    public double getOrder() {
        return 11;
    }

    private static final Map<LabGroupByOptions, String> Y_AXIS_UNIT_NAMES;

    static {
        Map<LabGroupByOptions, String> names = new HashMap<>();
        names.put(LabGroupByOptions.REF_RANGE_NORM_VALUE, "Ref. ranges");
        names.put(LabGroupByOptions.TIMES_UPPER_REF_VALUE, "Times upper ref.");
        names.put(LabGroupByOptions.TIMES_LOWER_REF_VALUE, "Times lower ref.");
        names.put(LabGroupByOptions.PERCENTAGE_CHANGE_FROM_BASELINE, "% change");

        Y_AXIS_UNIT_NAMES = Collections.unmodifiableMap(names);
    }

    private Supplier<Map<LabGroupByOptions, Object>> getContextSupplier(LabGroupByOptions yAxisOption) {
        return () -> {
            final Map<LabGroupByOptions, Object> context = new EnumMap<>(LabGroupByOptions.class);
            if (Y_AXIS_UNIT_NAMES.containsKey(yAxisOption)) {
                String unit = Y_AXIS_UNIT_NAMES.get(yAxisOption);
                context.put(LabGroupByOptions.MEASUREMENT, unit);
            }
            return context;
        };
    }

    public Set<String> getJumpToLabs(Datasets datasets, Set<String> labCodes) {
        FilterResult<Lab> filtered = getFilteredData(datasets, LabFilters.empty(), PopulationFilters.empty());
        return filtered.getFilteredEvents().stream()
                .map(lab -> lab.getEvent().getLabCode())
                .filter(labcode -> labcode != null && labCodes.contains(labcode.toLowerCase()))
                .collect(toSet());
    }

    public Set<String> getJumpToNormalizedLabs(Datasets datasets, Set<String> normalizedLabCodes) {
        FilterResult<Lab> filtered = getFilteredData(datasets, LabFilters.empty(), PopulationFilters.empty());
        return filtered.getFilteredEvents().stream()
                .filter(lab -> lab.getEvent().getNormalizedLabCode() != null
                        && normalizedLabCodes.contains(lab.getEvent().getNormalizedLabCode().toLowerCase()))
                .map(ae -> ae.getEvent().getLabCode())
                .collect(toSet());
    }

    @Override
    protected Predicate<Lab> composeEventTypeSpecificXAxisBasedPredicate(ChartGroupByOptions.GroupByOptionAndParams<Lab, LabGroupByOptions> xAxisOption) {
        if (xAxisOption.getGroupByOption() == VISIT_NUMBER) {
            return l -> xAxisOption.getGroupByOption().getAttribute().getFunction().apply(l) != null;
        }
        return l -> true;
    }
}
