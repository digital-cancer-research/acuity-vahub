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
import com.acuity.visualisations.rawdatamodel.dataproviders.AssessedTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.AssessedTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.compatibility.TumorLineChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.service.plots.TLDLineChartService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ATLGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.TrellisedLineFloatChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.LineChartData;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ATLGroupByOptions.LESION_PERCENTAGE_CHANGE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static java.util.stream.Collectors.partitioningBy;
import static java.util.stream.Collectors.toList;

@Service
public class TumourLineChartService extends AssessedTargetLesionService {

    @Autowired
    private TumorLineChartUIModelService<AssessedTargetLesion, ATLGroupByOptions> lineChartUIModelService;
    @Autowired
    private TLDLineChartService lineChartService;

    public List<TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, OutputLineChartData>> getTumourAllChangesOnLinechart(
            Datasets datasets, AssessedTargetLesionFilters tumourFilters, PopulationFilters populationFilters,
            ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> settings) {

        FilterResult<AssessedTargetLesion> filtered = getTumourFilterResult(datasets, tumourFilters, populationFilters, settings.getSettings());

        final Map<GroupByKey<AssessedTargetLesion, ATLGroupByOptions>, LineChartData> res = lineChartService.getLineChart(filtered, settings.getSettings());

        return lineChartUIModelService.toTrellisedLineFloatChart(res);
    }

    public SelectionDetail getLineChartSelectionDetails(
            Datasets datasets,
            AssessedTargetLesionFilters tumourFilters,
            PopulationFilters populationFilters,
            ChartSelection<AssessedTargetLesion, ATLGroupByOptions, ChartSelectionItem<AssessedTargetLesion, ATLGroupByOptions>> selection) {

        FilterResult<AssessedTargetLesion> filtered = getTumourFilterResult(datasets, tumourFilters, populationFilters, selection.getSettings());

        return lineChartService.getSelectionDetails(filtered, selection);
    }

    public AxisOptions<ATLGroupByOptions> getAvailableLineChartXAxis(Datasets datasets, Filters<AssessedTargetLesion> filters,
                                                                     PopulationFilters populationFilters) {

        final FilterResult<AssessedTargetLesion> filteredData = getFilteredDataByVisit(datasets, filters, populationFilters, null);
        return getAxisOptions(filteredData.getFilteredResult(), ATLGroupByOptions.DAYS_SINCE_FIRST_DOSE,
                ATLGroupByOptions.ASSESSMENT_WEEK_WITH_BASELINE);

    }

    public AxisOptions<ATLGroupByOptions> getAvailableLineChartByLesionXAxis(Datasets datasets, 
                                                                             AssessedTargetLesionFilters filters, 
                                                                             PopulationFilters populationFilters) {
        return getAxisOptions(datasets, filters, populationFilters, null,
                getLineChartByLesionPredicate(),
                ATLGroupByOptions.DAYS_SINCE_FIRST_DOSE, ATLGroupByOptions.ASSESSMENT_WEEK_WITH_BASELINE);
    }

    public List<TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, OutputLineChartData>> getTumourChangesByLesionOnLinechart(
            Datasets datasets,
            AssessedTargetLesionFilters tumourFilters,
            PopulationFilters populationFilters,
            ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> settings) {

        FilterResult<AssessedTargetLesion> filtered = getFilteredData(datasets, tumourFilters, populationFilters, null,
                getLineChartByLesionPredicate());

        Collection<AssessedTargetLesion> postFilterApplied = postFilter(settings.getSettings(), filtered.getFilteredResult());
        filtered.withResults(filtered.getAllEvents(), postFilterApplied);
        final Map<GroupByKey<AssessedTargetLesion, ATLGroupByOptions>, LineChartData> res = lineChartService.getLineChart(filtered, settings.getSettings());

        return lineChartUIModelService.toTrellisedLineFloatChart(res);
    }

    @Override
    public List<Map<String, String>> getDetailsOnDemandData(Datasets datasets, Set ids, List<SortAttrs> sortAttrs, long from, long count) {

        final Collection<AssessedTargetLesion> events = ((AssessedTargetLesionDatasetsDataProvider) getEventDataProvider(datasets))
                .loadDataByVisit(datasets).stream().filter(e -> ids.contains(e.getId())).collect(toList());
        return doDCommonService.getColumnData(Column.DatasetType.fromDatasets(datasets), events, sortAttrs, from, count, true);
    }

    @Override
    public void writeAllDetailsOnDemandCsv(Datasets datasets, Writer writer, Filters<AssessedTargetLesion> filters,
                                           PopulationFilters populationFilters) throws IOException {
        final FilterResult<AssessedTargetLesion> filteredData = getFilteredDataByVisit(datasets, filters, populationFilters, null);
        writeEventsDoDCsv(writer, Column.DatasetType.fromDatasets(datasets), filteredData.getFilteredResult());
    }

    @Override
    public void writeSelectedDetailsOnDemandCsv(Datasets datasets, Set ids, Writer writer) throws IOException {
        final Collection<AssessedTargetLesion> events = ((AssessedTargetLesionDatasetsDataProvider) getEventDataProvider(datasets))
                .loadDataByVisit(datasets).stream()
                .filter(e -> ids.contains(e.getId())).collect(toList());
        writeEventsDoDCsv(writer, Column.DatasetType.fromDatasets(datasets), events);
    }

    /**
     * Returns list of lesions (one lesion per subject's lesion date), because one visit is considered as one event for now.
     * Every AssessedTargetLesion contains all necessary aggregate info about the visit
     */
    private FilterResult<AssessedTargetLesion> getTumourFilterResult(Datasets datasets, AssessedTargetLesionFilters tumourFilters,
                                                                     PopulationFilters populationFilters,
                                                                     ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> settings) {
        FilterResult<AssessedTargetLesion> filtered = getFilteredDataByVisit(datasets, tumourFilters, populationFilters, null);
        Collection<AssessedTargetLesion> withPostFilterApplied = postFilter(settings, filtered.getFilteredResult());
        filtered.withResults(filtered.getAllEvents(), withPostFilterApplied);
        return filtered;
    }

    private Collection<AssessedTargetLesion> postFilter(ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> settings,
                                                        Collection<AssessedTargetLesion> filtered) {

        if (filtered.isEmpty()) {
            return filtered;
        }

        ATLGroupByOptions xAxisOption = settings.getOptions().get(X_AXIS).getGroupByOption();
        ATLGroupByOptions yAxisOption = settings.getOptions().get(Y_AXIS).getGroupByOption();

        Stream<AssessedTargetLesion> filteredStream = filtered.stream();
        switch (xAxisOption) {
            case ASSESSMENT_WEEK_WITH_BASELINE:
                filteredStream = yAxisOption == LESION_PERCENTAGE_CHANGE
                        ? closestToIdealAssessmentDayFilter(filtered, groupTumoursBySubjectAndLesion())
                        : closestToIdealAssessmentDayFilter(filtered, groupTumoursBySubject());
                break;
            case DAYS_SINCE_FIRST_DOSE:
            default:
        }
        switch (yAxisOption) {
            case PERCENTAGE_CHANGE:
            case ABSOLUTE_CHANGE:
                filteredStream = filteredStream.filter(atl -> atl.getEvent().getSumPercentageChangeFromBaseline() != null);
                break;
            case LESION_PERCENTAGE_CHANGE:
                break;
            case ABSOLUTE_SUM:
            default:
                filteredStream = filteredStream.filter(atl -> atl.getEvent().getLesionsDiameterPerAssessment() != null);
        }
        return filteredStream.collect(toList());
    }

    private Stream<AssessedTargetLesion> closestToIdealAssessmentDayFilter(
            Collection<AssessedTargetLesion> filtered,
            Function<Collection<AssessedTargetLesion>, Map<String, List<AssessedTargetLesion>>> atlGroupingFunction) {

        Map<Boolean, List<AssessedTargetLesion>> isBaselineLesion = filtered.stream()
                .collect(partitioningBy(e -> e.getEvent().isBaseline()));

        List<Integer> weeks = getAssessmentWeeks(filtered, false);
        List<AssessedTargetLesion> closestToIdealAssessmentDate = getAtlsClosestToIdealAssessmentDaysAsList(isBaselineLesion.get(false),
                weeks, atlGroupingFunction, false);

        return Stream.concat(isBaselineLesion.get(true).stream(),
                closestToIdealAssessmentDate.stream());
    }



    private Predicate<AssessedTargetLesion> getLineChartByLesionPredicate() {
        return e -> e.getEvent().getTargetLesionRaw().getLesionPercentageChangeFromBaseline() != null;
    }
}
