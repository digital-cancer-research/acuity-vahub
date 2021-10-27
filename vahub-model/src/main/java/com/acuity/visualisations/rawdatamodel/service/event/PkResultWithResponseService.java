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
import com.acuity.visualisations.rawdatamodel.dataproviders.PkResultWithResponseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.AssessedTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PkResultFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.compatibility.PkResultWithResponseUiModelService;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.service.plots.StatsPlotService;
import com.acuity.visualisations.rawdatamodel.service.event.data.AssessedTLWithWeek;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AssessmentAxisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AssessmentAxisOptions.AssessmentType;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.PkResultRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.BoxplotCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
public class PkResultWithResponseService extends PkResultService {
    @Autowired
    private AssessedTargetLesionService assessedTargetLesionService;
    @Autowired
    private StatsPlotService<PkResult, PkResultGroupByOptions> statsPlotService;
    @Autowired
    private PkResultWithResponseUiModelService pkResultUiModelService;
    @Autowired
    private PkResultWithResponseDatasetsDataProvider pkResultWithResponseDatasetsDataProvider;

    @Override
    protected SubjectAwareDatasetsDataProvider<PkResultRaw, PkResult> getEventDataProvider(Datasets datasets, Filters<PkResult> filters) {
        return pkResultWithResponseDatasetsDataProvider;
    }

    @Override
    public List<TrellisedBoxPlot<PkResult,
            PkResultGroupByOptions>> getBoxPlot(Datasets datasets,
                                                ChartGroupByOptionsFiltered<PkResult,
                                                        PkResultGroupByOptions> settings,
                                                Filters<PkResult> filters,
                                                PopulationFilters populationFilters) {

        FilterResult<PkResult> filtered = getFilteredData(datasets, filters, populationFilters, settings);
        final ChartGroupByOptions<PkResult, PkResultGroupByOptions> optionsWithContext = getOptionsWithContext(settings.getSettings(),
                getContextSupplier(datasets, populationFilters, settings.getSettings()));
        Map<GroupByKey<PkResult, PkResultGroupByOptions>, BoxplotCalculationObject> boxPlot
                = statsPlotService.getBoxPlot(optionsWithContext, filtered);
        return pkResultUiModelService.toTrellisedBoxPlot(boxPlot);
    }

    @Override
    public SelectionDetail getRangedSelectionDetails(Datasets datasets, Filters<PkResult> filters,
                                                     PopulationFilters populationFilters,
                                                     ChartSelection<PkResult, PkResultGroupByOptions,
                                                             ChartSelectionItemRange<PkResult, PkResultGroupByOptions, Double>> selection) {
        FilterResult<PkResult> filtered = getFilteredData(datasets, filters, populationFilters);
        final ChartGroupByOptions<PkResult, PkResultGroupByOptions> optionsWithContext = getOptionsWithContext(selection.getSettings(),
                getContextSupplier(datasets, populationFilters, selection.getSettings()));
        return statsPlotService.getRangedSelectionDetails(filtered, ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    @Override
    public AxisOptions<PkResultGroupByOptions> getAvailableBoxPlotXAxis(Datasets datasets,
                                                                        Filters<PkResult> filters,
                                                                        PopulationFilters populationFilters) {

        AxisOptions<PkResultGroupByOptions> axisOptions = getAxisOptions(datasets, filters, populationFilters,
                PkResultGroupByOptions.OVERALL_RESPONSE);
        List<Integer> weeks = assessedTargetLesionService.getAssessmentWeeks(datasets, AssessedTargetLesionFilters.empty(),
                populationFilters, true);

        // AssessmentType.WEEK is not passed to assessmentTypes because weeks are already transformed into "Week N" option on the front-end
        return new AssessmentAxisOptions<>(axisOptions, weeks, new AssessmentType[] {AssessmentType.BEST_CHANGE});
    }


    public List<Map<String, String>> getRecistDetailsOnDemandData(Datasets datasets, Set recistIds, List<SortAttrs> sortAttrs, long from, long count) {
        final Collection<AssessedTargetLesion> recistEvents = assessedTargetLesionService
                .getFilteredDataByVisit(datasets, AssessedTargetLesionFilters.empty(), PopulationFilters.empty(),
                        r -> recistIds.contains(r.getId())).getAllEvents();
        return doDCommonService.getColumnData(Column.DatasetType.fromDatasets(datasets), getATLWithUniqueWeek(recistEvents), sortAttrs, from, count, true);
    }

    private Supplier<Map<PkResultGroupByOptions, Object>> getContextSupplier(Datasets datasets, PopulationFilters populationFilters,
                                                                             ChartGroupByOptions<PkResult, PkResultGroupByOptions> settings) {
        return () -> {
            final Map<PkResultGroupByOptions, Object> context = new EnumMap<>(PkResultGroupByOptions.class);
            Map<GroupByOption.Param, Object> xAxisParams = settings.getOptions().get(X_AXIS).getParamMap();
            AssessmentType assessmentType = AssessmentAxisOptions.getAssessmentType(xAxisParams);
            if (assessmentType == AssessmentType.WEEK) {
                context.put(PkResultGroupByOptions.OVERALL_RESPONSE, getWeekResponseBySubject(datasets, populationFilters));
            }
            return context;
        };
    }

    private Map<String, Map<Integer, String>> getWeekResponseBySubject(Datasets datasets, PopulationFilters populationFilters) {
        Collection<AssessedTargetLesion> assessedTargetLesions = assessedTargetLesionService.getFilteredData(datasets,
                AssessedTargetLesionFilters.empty(), populationFilters).getFilteredResult();
        List<Integer> weeks = assessedTargetLesionService.getAssessmentWeeks(assessedTargetLesions, true);
        Map<String, Map<Integer, AssessedTargetLesion>> atlsClosestToIdealAssessmentDay = assessedTargetLesionService
                .getAtlsClosestToIdealAssessmentDays(assessedTargetLesions, weeks, assessedTargetLesionService.groupTumoursBySubject(), true);

        return atlsClosestToIdealAssessmentDay.entrySet().stream()
                .collect(toMap(Map.Entry::getKey,
                        atlBySubjectByWeek -> atlBySubjectByWeek.getValue().entrySet().stream()
                                .collect(toMap(Map.Entry::getKey,
                                        atlByWeek -> atlByWeek.getValue().getEvent().getResponse()))));
    }

    public Map<String, String> getRecistDoDColumns(Datasets datasets) {
        final Collection<AssessedTargetLesion> allEvents = assessedTargetLesionService
                .getFilteredData(datasets, AssessedTargetLesionFilters.empty(), PopulationFilters.empty())
                .getAllEvents();
        return doDCommonService.getDoDColumns(Column.DatasetType.fromDatasets(datasets), getATLWithUniqueWeek(allEvents));
    }

    public void writeAllRecistDetailsOnDemandCsv(Datasets datasets, PrintWriter writer, PkResultFilters eventFilters, PopulationFilters populationFilters) {
        final Set<String> subjectIds = getFilteredData(datasets, eventFilters, populationFilters)
                .getPopulationFilterResult().stream().map(Subject::getSubjectId).collect(toSet());
        final Collection<AssessedTargetLesion> events = assessedTargetLesionService
                .getFilteredDataByVisit(datasets, AssessedTargetLesionFilters.empty(),
                        populationFilters, t -> subjectIds.contains(t.getSubjectId()))
                .getFilteredResult();
        writeRecistDodCsv(writer, Column.DatasetType.fromDatasets(datasets), getATLWithUniqueWeek(events));
    }

    public void writeRecistSelectedDetailsOnDemandCsv(Datasets datasets, Set eventIds, PrintWriter writer) {
        final Collection<AssessedTargetLesion> events = assessedTargetLesionService
                .getFilteredDataByVisit(datasets, AssessedTargetLesionFilters.empty(),
                        PopulationFilters.empty(), t -> eventIds.contains(t.getId()))
                .getFilteredResult();
        writeRecistDodCsv(writer, Column.DatasetType.fromDatasets(datasets), getATLWithUniqueWeek(events));
    }
    private void writeRecistDodCsv(Writer writer, Column.DatasetType datasetType, Collection<AssessedTLWithWeek> atlWithWeeks) {
        Map<String, String> columnsTitles = doDCommonService.getDoDColumns(datasetType, atlWithWeeks);
        List<Map<String, String>> data = doDCommonService.getColumnData(datasetType, atlWithWeeks);
        doDCommonService.writeCsv(data, columnsTitles, writer);
    }

    private List<AssessedTLWithWeek> getATLWithUniqueWeek(Collection<AssessedTargetLesion> events) {
        List<Integer> weeks = assessedTargetLesionService.getAssessmentWeeks(events, true);
        Map<String, Map<Integer, AssessedTargetLesion>> atlsClosestToIdealAssessmentDay = assessedTargetLesionService
                .getAtlsClosestToIdealAssessmentDays(events, weeks, assessedTargetLesionService.groupTumoursBySubject(), true);
        return atlsClosestToIdealAssessmentDay.values().stream()
                .flatMap(atlByWeek -> atlByWeek.entrySet()
                        .stream()
                        .map(es -> new AssessedTLWithWeek(es.getValue(), es.getKey())))
                .collect(toList());
    }
}
