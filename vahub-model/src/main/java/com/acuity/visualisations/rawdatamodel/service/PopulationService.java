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

package com.acuity.visualisations.rawdatamodel.service;

import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.compatibility.RangedColoredBarChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDService;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.service.dod.SsvCommonService;
import com.acuity.visualisations.rawdatamodel.service.filters.PopulationRawDataFilterService;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartService;
import com.acuity.visualisations.rawdatamodel.service.plots.RangedOptionService;
import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import com.acuity.visualisations.rawdatamodel.util.ObjectUtil;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Patient;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.AGE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.CENTRE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.COUNTRY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.DATE_OF_DEATH;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.DEATH;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.DURATION_ON_STUDY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.FIRST_TREATMENT_DATE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.HEIGHT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.NONE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.RACE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.RANDOMISATION_DATE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.REASON_FOR_WITHDRAWAL;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.SEX;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.STUDY_CODE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.STUDY_NAME;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.STUDY_PART_ID;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.WEIGHT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.WITHDRAWAL;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class PopulationService implements DoDService<Subject>, SsvSummaryTableService {

    @Autowired
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @Autowired
    private PopulationRawDataFilterService populationFilterService;
    @Autowired
    private DoDCommonService doDCommonService;
    @Autowired
    private SsvCommonService ssvCommonService;
    @Autowired
    private PopulationRawDataFilterService populationRawDataFilterService;
    @Autowired
    private RangedOptionService rangedOptionService;
    @Autowired
    private BarChartService<Subject, PopulationGroupByOptions> barChartService;
    @Qualifier("rangedColoredBarChartUIModelService")
    @Autowired
    private RangedColoredBarChartUIModelService uiModelService;


    public Optional<Subject> getSubject(Datasets datasets, String subjectId) {
        return populationDatasetsDataProvider.loadData(datasets).stream().filter(s -> s.getId().equals(subjectId)).findFirst();
    }

    public FilterResult<Subject> getFilteredData(Datasets datasets, PopulationFilters populationFilters) {

        FilterQuery<Subject> filterQuery = getFilterQuery(datasets, populationFilters);
        return populationRawDataFilterService.query(filterQuery);
    }

    public FilterResult<Subject> getFilteredData(Datasets datasets, Filters<Subject> populationFilters, Predicate<Subject> eventPredicate) {
        FilterQuery<Subject> filterQuery = getFilterQuery(datasets, populationFilters, eventPredicate);
        return populationFilterService.query(filterQuery);
    }

    public FilterResult<Subject> getFilteredData(Datasets datasets, PopulationFilters populationFilters,
                                                 ChartGroupByOptionsFiltered<Subject, PopulationGroupByOptions> eventSettings) {
        FilterQuery<Subject> filterQuery = getFilterQuery(datasets, populationFilters, eventSettings, null);
        return populationFilterService.query(filterQuery);
    }
    public FilterResult<Subject> getFilteredData(Datasets datasets, Filters<Subject> populationFilters) {

        FilterQuery<Subject> filterQuery = getFilterQuery(datasets, populationFilters);
        return populationRawDataFilterService.query(filterQuery);
    }

    private FilterQuery<Subject> getFilterQuery(Datasets datasets, PopulationFilters populationFilters,
                                                ChartGroupByOptionsFiltered<Subject, PopulationGroupByOptions> eventSettings,
                                                Predicate<Subject> eventPredicate) {

        final Stream<Subject> eventStream = populationDatasetsDataProvider.loadData(datasets).stream();
        //if eventSettings are provided, filtering on selected trellis items
        Stream<Subject> filteredEventStream = eventSettings == null || eventSettings.getFilterByTrellisOptions().isEmpty() ? eventStream : eventStream
                .filter(e -> eventSettings.getFilterByTrellisOptions().stream()
                        .anyMatch(filterItem -> filterItem.entrySet().stream().allMatch(
                                optionValue -> {
                                    final Object eventValue = Attributes.get(
                                            eventSettings.getSettings().getTrellisOption(optionValue.getKey()), e);
                                    return ObjectUtil.stringEquals(optionValue.getValue(), eventValue);
                                })
                        ));
        //if eventPredicate is provided, filtering on this predicate
        filteredEventStream = eventPredicate == null ? filteredEventStream : eventStream.filter(eventPredicate);
        Collection<Subject> events = filteredEventStream.collect(toList());
        return new FilterQuery<>(events, populationFilters);
    }

    public PopulationFilters getAvailableFilters(Datasets datasets, PopulationFilters populationFilters) {
        FilterQuery<Subject> filterQuery = getFilterQuery(datasets, populationFilters);

        return populationRawDataFilterService.getAvailableFilters(filterQuery);
    }

    protected FilterQuery<Subject> getFilterQuery(Datasets datasets, Filters<Subject> populationFilters,
                                                  Predicate<Subject> eventPredicate) {

        Collection<Subject> subjects = populationDatasetsDataProvider.loadData(datasets);
        Collection<Subject> filteredSubjects = eventPredicate == null ? subjects : subjects.stream().filter(eventPredicate).collect(toList());
        return new FilterQuery<>(filteredSubjects, populationFilters);
    }

    protected FilterQuery<Subject> getFilterQuery(Datasets datasets, PopulationFilters populationFilters) {

        return getFilterQuery(datasets, populationFilters, null);
    }

    protected FilterQuery<Subject> getFilterQuery(Datasets datasets, Filters<Subject> populationFilters) {
        return getFilterQuery(datasets, populationFilters, null);
    }

    public AxisOptions<PopulationGroupByOptions> getAvailableBarChartXAxisOptions(Datasets datasets, PopulationFilters populationFilters) {
        return getAxisOptions(datasets, populationFilters, NONE, STUDY_CODE, STUDY_NAME, STUDY_PART_ID, DURATION_ON_STUDY, RANDOMISATION_DATE, WITHDRAWAL,
                REASON_FOR_WITHDRAWAL, CENTRE, COUNTRY, SEX, RACE, AGE, WEIGHT, HEIGHT, FIRST_TREATMENT_DATE, DEATH, DATE_OF_DEATH);
    }

    public List<TrellisOptions<PopulationGroupByOptions>> getBarChartColorByOptions(Datasets datasets, PopulationFilters populationFilters) {
        FilterResult<Subject> filtered = getFilteredData(datasets, populationFilters);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(),
                STUDY_CODE.getGroupByOptionAndParams(),
                STUDY_NAME.getGroupByOptionAndParams(),
                STUDY_PART_ID.getGroupByOptionAndParams(),
                DURATION_ON_STUDY.<Subject, PopulationGroupByOptions>getGroupByOptionAndParams().supplyContext(
                        getContextSupplier(filtered, null, DURATION_ON_STUDY)),
                RANDOMISATION_DATE.<Subject, PopulationGroupByOptions>getGroupByOptionAndParams().supplyContext(
                        getContextSupplier(filtered, null, RANDOMISATION_DATE)),
                WITHDRAWAL.getGroupByOptionAndParams(),
                REASON_FOR_WITHDRAWAL.getGroupByOptionAndParams(),
                CENTRE.getGroupByOptionAndParams(),
                COUNTRY.getGroupByOptionAndParams(),
                SEX.getGroupByOptionAndParams(),
                RACE.getGroupByOptionAndParams(),
                AGE.<Subject, PopulationGroupByOptions>getGroupByOptionAndParams().supplyContext(getContextSupplier(filtered, null, AGE)),
                WEIGHT.<Subject, PopulationGroupByOptions>getGroupByOptionAndParams().supplyContext(getContextSupplier(filtered, null, WEIGHT)),
                HEIGHT.<Subject, PopulationGroupByOptions>getGroupByOptionAndParams().supplyContext(getContextSupplier(filtered, null, HEIGHT)),
                FIRST_TREATMENT_DATE.<Subject, PopulationGroupByOptions>getGroupByOptionAndParams().supplyContext(
                        getContextSupplier(filtered, null, FIRST_TREATMENT_DATE)),
                DEATH.getGroupByOptionAndParams(),
                DATE_OF_DEATH.<Subject, PopulationGroupByOptions>getGroupByOptionAndParams().supplyContext(getContextSupplier(filtered, null, DATE_OF_DEATH)));
    }

    public List<TrellisedBarChart<Subject, PopulationGroupByOptions>> getBarChart(Datasets datasets,
                                                                                  ChartGroupByOptionsFiltered<Subject, PopulationGroupByOptions> settings,
                                                                                  PopulationFilters populationFilters, CountType countType) {
        FilterResult<Subject> filtered = getFilteredData(datasets, populationFilters, settings);
        filtered.withPopulationFilteredResults(new FilterResult<>(
                filtered.getFilterQuery()).withResults(filtered.getAllEvents(), filtered.getFilteredResult()));

        PopulationGroupByOptions xAxisOption = settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS) == null
                ? null : settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).getGroupByOption();
        PopulationGroupByOptions colorByOption = settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY) == null
                ? null : settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY).getGroupByOption();

        Map<GroupByKey<Subject, PopulationGroupByOptions>, BarChartCalculationObject<Subject>> chartData =
                barChartService.getBarChart(getOptionsWithContext(settings.getSettings(),
                        getContextSupplier(filtered, xAxisOption, colorByOption)), countType, filtered);

        return uiModelService.toTrellisedBarChart(chartData, countType);
    }

    public SelectionDetail getSelectionDetails(Datasets datasets, PopulationFilters populationFilters,
                                               ChartSelection<Subject, PopulationGroupByOptions, ChartSelectionItem<Subject,
                                                       PopulationGroupByOptions>> selection) {
        FilterResult<Subject> filtered = getFilteredData(datasets, populationFilters);
        filtered.withPopulationFilteredResults(new FilterResult<>(
                filtered.getFilterQuery()).withResults(filtered.getAllEvents(), filtered.getFilteredResult()));

        PopulationGroupByOptions xAxisOption = selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS) == null
                ? null : selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).getGroupByOption();
        PopulationGroupByOptions colorByOption = selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY) == null
                ? null : selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY).getGroupByOption();

        return barChartService.getSelectionDetails(filtered, ChartSelection.of(getOptionsWithContext(selection.getSettings(),
                getContextSupplier(filtered, xAxisOption, colorByOption)), selection.getSelectionItems()));
    }

    @Override
    public List<Map<String, String>> getDetailsOnDemandData(Datasets datasets, Set ids, List<SortAttrs> sortAttrs, long from, long count) {
        final Collection<Subject> events = populationDatasetsDataProvider.loadData(datasets).stream()
                .filter(e -> ids.contains(e.getId())).collect(Collectors.toList());

        return doDCommonService.getColumnData(DatasetType.fromDatasets(datasets), events, sortAttrs, from, count, true);
    }

    @Override
    public List<Map<String, String>> getDetailsOnDemandData(Datasets datasets, String subjectId, Filters<Subject> populationFilters) {

        final FilterResult<Subject> filteredData = getFilteredData(datasets, populationFilters,
                e -> Objects.equals(subjectId, e.getSubjectCode()) || Objects.equals(subjectId, e.getSubjectId()));
        return doDCommonService.getColumnData(DatasetType.fromDatasets(datasets), filteredData.getFilteredEvents(),
                Collections.singletonList(new SortAttrs("", false)), 0, Integer.MAX_VALUE, true);
    }

    @Override
    public void writeAllDetailsOnDemandCsv(Datasets datasets, Writer writer, Filters<Subject> filters, PopulationFilters populationFilters) {
        final FilterResult<Subject> filteredData = getFilteredData(datasets, populationFilters);

        Map<String, String> columnsTitles = doDCommonService.getDoDColumns(DatasetType.fromDatasets(datasets), filteredData.getFilteredResult());
        List<Map<String, String>> data = doDCommonService.getColumnData(DatasetType.fromDatasets(datasets), filteredData.getFilteredResult());
        doDCommonService.writeCsv(data, columnsTitles, writer);
    }

    @Override
    public void writeSelectedDetailsOnDemandCsv(Datasets datasets, Set ids, Writer writer) throws IOException {
        final Collection<Subject> subjects = getFilteredData(datasets, PopulationFilters.empty(),
                s -> Objects.nonNull(s.getSubjectId()) && ids.contains(s.getSubjectId())).getFilteredResult();
        final DatasetType datasetType = DatasetType.fromDatasets(datasets);
        Map<String, String> columnsTitles = doDCommonService.getDoDColumns(datasetType, subjects);
        List<Map<String, String>> data = doDCommonService.getColumnData(datasetType, subjects);
        doDCommonService.writeCsv(data, columnsTitles, writer);
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, Filters<Subject> populationFilters) {
        final FilterResult<Subject> filteredData = getFilteredData(datasets, populationFilters,
                s -> subjectId.equals(s.getSubjectId()) || subjectId.equals(s.getSubjectCode()));
        return ssvCommonService.getColumnData(DatasetType.fromDatasets(datasets), filteredData.getFilteredResult());
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, PopulationFilters.empty());
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {

        return ssvCommonService.getColumns(datasetType, Subject.class);
    }

    @Override
    public String getSsvTableName() {
        return "demography";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "DEMOGRAPHY";
    }

    @Override
    public String getHeaderName() {
        return "DEMOGRAPHY";
    }

    @Override
    public double getOrder() {
        return 1;
    }

    public boolean hasSafetyAsNoInPopulation(Datasets datasets) {
        Collection<Subject> subjects = populationDatasetsDataProvider.loadData(datasets);

        return subjects.stream().filter(s -> s.getSafetyPopulation() != null && "N".equals(s.getSafetyPopulation())).count() > 1;
    }

    public List<Patient> getPatientList(Datasets datasets) {
        Collection<Subject> subjects = populationDatasetsDataProvider.loadData(datasets);

        return subjects.stream().map(s -> new Patient(s.getId(), s.getSubjectCode())).collect(toList());
    }

    private AxisOptions<PopulationGroupByOptions> getAxisOptions(Datasets datasets, PopulationFilters populationFilters,
                                                                 PopulationGroupByOptions... options) {
        final FilterResult<Subject> filteredData = getFilteredData(datasets, populationFilters);
        //getting distinct drugs to provide with drug-aware options like "days since first dose of <drug>"
        final List<String> drugs = filteredData.stream()
                .flatMap(e -> e.getDrugFirstDoseDate().entrySet().stream()
                        .filter(d -> d.getKey() != null && d.getValue() != null)
                        .map(Map.Entry::getKey)).distinct().sorted().collect(Collectors.toList());
        //checking if any subjects have randomisation date to decide on are randomisation-related options available
        final boolean hasRand = filteredData.stream().anyMatch(e -> e.getDateOfRandomisation() != null);

        return new AxisOptions<>(
                Stream.of(options)
                        .map(o -> new AxisOption<>(o,
                                Attributes.isTimestampOption(o),
                                Attributes.isTimestampOptionSupportDuration(o),
                                Attributes.isBinableOption(o),
                                Attributes.hasDrugOption(o)))
                        .collect(Collectors.toList()), hasRand, drugs);
    }

    private Supplier<Map<PopulationGroupByOptions, Object>> getContextSupplier(FilterResult<Subject> filtered,
                                                                               PopulationGroupByOptions xAxis,
                                                                               PopulationGroupByOptions colorBy) {
        return () -> Stream.of(PopulationGroupByOptions.values())
                .filter(o -> GroupByOption.getRangeOptionAnnotation(o) != null)
                .filter(o -> o == xAxis || o == colorBy)
                .collect(toMap(o -> o, rangedOptionService.getRangeFunction(filtered, true, null),
                        (u, v) -> {
                            throw new IllegalStateException(String.format("Duplicate key %s", u));
                        },
                        () -> new EnumMap<>(PopulationGroupByOptions.class)));
    }

    public final ChartGroupByOptions<Subject, PopulationGroupByOptions> getOptionsWithContext(ChartGroupByOptions<Subject, PopulationGroupByOptions> options,
                                                                                              Supplier<Map<PopulationGroupByOptions, Object>> supplier) {
        final Map<PopulationGroupByOptions, Object> attributesContext = supplier.get();
        return attributesContext == null || attributesContext.isEmpty() ? options : options.supplyContext(attributesContext);
    }
}
