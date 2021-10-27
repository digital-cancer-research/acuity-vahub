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
import com.acuity.visualisations.rawdatamodel.dataproviders.AeIncidenceDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.AeSeverityChangeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartSupportService;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.plots.OverTimeChartSupportService;
import com.acuity.visualisations.rawdatamodel.service.plots.TrellisSupportService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.AeColoredBarChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.AeOvertimeChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.service.plots.BarChartService;
import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.service.ssv.SsvSummaryTableService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.GroupByOptionAndParams;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByAttributes;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.AeTableUtil;
import com.acuity.visualisations.rawdatamodel.util.AlphanumEmptyLastComparator;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.util.PartitionByCount;
import com.acuity.visualisations.rawdatamodel.util.SQLike;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverity;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSubjectTermArmMaxSeverity;
import com.acuity.visualisations.rawdatamodel.vo.AeTermArm;
import com.acuity.visualisations.rawdatamodel.vo.AeTermArmMaxSeverity;
import com.acuity.visualisations.rawdatamodel.vo.AesTable;
import com.acuity.visualisations.rawdatamodel.vo.AesTable.AesTableBuilder;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.EventWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions.ARM;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions.HLT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions.PT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions.SOC;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions.SPECIAL_INTEREST_GROUP;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
@Primary
public class AeService extends BasePlotEventService<AeRaw, Ae, AeGroupByOptions>
        implements
        BarChartSupportService<Ae, AeGroupByOptions>,
        TrellisSupportService<Ae, AeGroupByOptions>,
        OverTimeChartSupportService<Ae, AeGroupByOptions>,
        SsvSummaryTableService {

    @Autowired
    private BarChartService<Ae, AeGroupByOptions> barChartService;
    @Autowired
    private BarChartService<Subject, PopulationGroupByOptions> populationBarChartService;

    @Autowired
    private AeOvertimeChartUIModelService overtimeChartUIModelService;
    @Autowired
    private AeColoredBarChartUIModelService uiModelService;
    @Autowired
    private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsDataProvider;
    @Autowired(required = false)
    private AeSeverityChangeDatasetsDataProvider aeSeverityChangeDatasetsDataProvider;
    @Autowired
    @Qualifier("aeIncidenceDatasetsDataProvider")
    protected SubjectAwareDatasetsDataProvider<AeRaw, Ae> eventDataProvider;

    private static final String[] COLUMN_NAMES = {"preferredTerm", "hlt", "soc", "severity", "maxSeverity",
            "startDate", "endDate", "duration", "serious", "causality", "description", "immuneMediated"};
    private static final String[] COLUMN_DESCRIPTIONS = {"Preferred term", "High level term", "System organ class", "Severity", "Max severity",
            "Start date", "End date", "Duration", "Serious", "Causality", "Description", "Immune mediated ae"};

    public SubjectAwareDatasetsDataProvider<AeRaw, Ae> getAeEventDataProvider(Datasets datasets, Filters<Ae> filters) {
        return getEventDataProvider(datasets, filters);
    }

    @Override
    protected SubjectAwareDatasetsDataProvider<AeRaw, Ae> getEventDataProvider(Datasets datasets, Filters<Ae> filters) {
        if (filters != null && (CollectionUtils.isEmpty(newArrayList(filters)) || ((AeFilters) filters).isAePerIncidence())) {
            return aeIncidenceDatasetsDataProvider;
        } else {
            return aeSeverityChangeDatasetsDataProvider;
        }
    }

    @Override
    @ApplyUsedInTflFilter
    public List<TrellisedBarChart<Ae, AeGroupByOptions>> getBarChart(Datasets datasets, ChartGroupByOptionsFiltered<Ae, AeGroupByOptions> settings,
                                                                     Filters<Ae> filters, PopulationFilters populationFilters, CountType countType) {
        FilterResult<Ae> filtered = getFilteredData(datasets, filters, populationFilters, settings);

        Map<GroupByKey<Ae, AeGroupByOptions>, BarChartCalculationObject<Ae>> chartData =
                barChartService.getBarChart(getOptionsWithContext(settings.getSettings(),
                        () -> composeContext(filtered, settings.getSettings().getTrellisOptions(),
                                settings.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS), countType)),
                        countType, filtered);

        return uiModelService.toTrellisedBarChart(chartData, countType);
    }


    /**
     * When counting subjects on AEs barchart we need to calculate max severity special way.
     * Max grade should be calculated as max grade found for subject within current X-axis option.
     * This method implements calculation of {@link AeGroupByOptions#MAX_SEVERITY_GRADE} grouping option context
     * providing map having ae ids as keys and properly calculated severity grades as values
     */
    private Map<AeGroupByOptions, Object> composeContext(FilterResult<Ae> filtered,
                                                                       Set<GroupByOptionAndParams<Ae, AeGroupByOptions>> trellisOptions,
                                                                       GroupByOptionAndParams<Ae, AeGroupByOptions> xAxisOption,
                                                                       CountType countType) {
        Map<AeGroupByOptions, Object> context = new EnumMap<>(AeGroupByOptions.class);
        if (countType == CountType.COUNT_OF_SUBJECTS
                || countType == CountType.PERCENTAGE_OF_ALL_SUBJECTS
                || countType == CountType.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT) {
            ChartGroupByOptions<Ae, AeGroupByOptions> groupByTrellisSubjectAndXOptions = ChartGroupByOptions.<Ae, AeGroupByOptions>builder()
                    .withTrellisOptions(trellisOptions)
                    .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, xAxisOption)
                    .withOption(ChartGroupByOptions.ChartGroupBySetting.NAME, AeGroupByOptions.SUBJECT_ID.getGroupByOptionAndParams())
                    .build();
            Map<GroupByKey<Ae, AeGroupByOptions>, Collection<Ae>> grouped = GroupByAttributes
                    .group(filtered.getFilteredResult(), groupByTrellisSubjectAndXOptions);
            Map<GroupByKey<Ae, AeGroupByOptions>, AeSeverity> maxSeveritiesByGroup = new HashMap<>();
            grouped.forEach((key, value) -> maxSeveritiesByGroup.put(key, value.stream()
                    .flatMap(ae -> ae.getAeSeverities().stream()).filter(Objects::nonNull)
                    .max(Comparator.comparing(AeSeverity::getSeverityNum)).orElse(null)));
            Map<String, String> aesMaxSeverities = filtered.stream().collect(toMap(EventWrapper::getId, ae -> {
                GroupByKey<Ae, AeGroupByOptions> key = Attributes.get(groupByTrellisSubjectAndXOptions, ae);
                // here we need to support multi-value attr, like special interest group. Logic may need clarification.
                AeSeverity aeSeverity = GroupByAttributes.expandKeyNestedCollections(key)
                        .stream().map(maxSeveritiesByGroup::get)
                        .filter(Objects::nonNull)
                        .max(Comparator.comparing(AeSeverity::getSeverityNum)).orElse(null);
                return Attributes.defaultNullableValue(aeSeverity == null ? null : aeSeverity.getWebappSeverity(), Attributes.DEFAULT_EMPTY_VALUE).toString();
            }));
            context.put(AeGroupByOptions.MAX_SEVERITY_GRADE, aesMaxSeverities);
        }
        return context;
    }

    @Override
    @ApplyUsedInTflFilter
    public List<TrellisedOvertime<Ae, AeGroupByOptions>> getLineBarChart(Datasets datasets, ChartGroupByOptionsFiltered<Ae, AeGroupByOptions> eventSettings,
                                                                         Filters<Ae> filters, PopulationFilters populationFilters) {
        FilterResult<Ae> filtered = getFilteredData(datasets, filters, populationFilters, eventSettings);

        Map<GroupByKey<Ae, AeGroupByOptions>, BarChartCalculationObject<Ae>> chartData =
                barChartService.getBarChart(eventSettings.getSettings(), CountType.COUNT_OF_EVENTS, filtered);

        Map<GroupByKey<Subject, PopulationGroupByOptions>, BarChartCalculationObject<Subject>> lineData =
                populationBarChartService.getBarChart(getPopulationLineSettings(eventSettings, filtered.getFilteredResult()),
                        CountType.COUNT_OF_EVENTS, filtered.getPopulationFilterResult());

        return overtimeChartUIModelService.toTrellisedOvertime(chartData, lineData);
    }

    @ApplyUsedInTflFilter
    @Override
    public List<TrellisOptions<AeGroupByOptions>> getTrellisOptions(Datasets datasets, Filters<Ae> filters,
                                                                    PopulationFilters populationFilters) {

        FilterResult<Ae> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(), ARM);
    }

    @ApplyUsedInTflFilter
    public List<TrellisOptions<AeGroupByOptions>> getBarChartColorByOptions(Datasets datasets, Filters<Ae> filters,
                                                                            PopulationFilters populationFilters) {

        FilterResult<Ae> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(), AeGroupByOptions.MAX_SEVERITY_GRADE);
    }

    @ApplyUsedInTflFilter
    public AxisOptions<AeGroupByOptions> getAvailableBarChartXAxis(Datasets datasets, Filters<Ae> filters, PopulationFilters populationFilters) {

        return getAxisOptions(datasets, filters, populationFilters,
                PT, HLT, SOC, SPECIAL_INTEREST_GROUP);
    }

    @Override
    @ApplyUsedInTflFilter
    public SelectionDetail getSelectionDetails(Datasets datasets, Filters<Ae> filters, PopulationFilters populationFilters,
                                               ChartSelection<Ae, AeGroupByOptions, ChartSelectionItem<Ae, AeGroupByOptions>> selection) {
        FilterResult<Ae> filtered = getFilteredData(datasets, filters, populationFilters, null);

        final CountType countType = CountType.COUNT_OF_EVENTS;
        final ChartGroupByOptions<Ae, AeGroupByOptions> optionsWithContext = getOptionsWithContext(selection.getSettings(),
                () -> composeContext(filtered, selection.getSettings().getTrellisOptions(),
                        selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS), countType));

        return barChartService.getSelectionDetails(filtered, ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    @ApplyUsedInTflFilter
    public SelectionDetail getSelectionDetails(Datasets datasets, Filters<Ae> filters, PopulationFilters populationFilters,
                                               ChartSelection<Ae, AeGroupByOptions, ChartSelectionItem<Ae, AeGroupByOptions>> selection,
                                               CountType countType) {
        FilterResult<Ae> filtered = getFilteredData(datasets, filters, populationFilters, null);

        final ChartGroupByOptions<Ae, AeGroupByOptions> optionsWithContext = getOptionsWithContext(selection.getSettings(),
                () -> composeContext(filtered, selection.getSettings().getTrellisOptions(),
                        selection.getSettings().getOptions().get(ChartGroupByOptions.ChartGroupBySetting.X_AXIS),
                        countType == null ? CountType.COUNT_OF_EVENTS : countType));

        return barChartService.getSelectionDetails(filtered, ChartSelection.of(optionsWithContext, selection.getSelectionItems()));
    }

    /*
     * Overrides this from BaseEventService just for the problem with having Incidence and Severity data providers, and no fitlers been passed into the query.
     * The set of ids is unique to either Incidence and Severity data providers so we check Incidence first as this currently has the dod table.
     */
    @Override
    public List<Map<String, String>> getDetailsOnDemandData(Datasets datasets, Set ids, List<SortAttrs> sortAttrs, long from, long count) {

        // The set of ids is unique to either Incidence and Severity data providers
        Collection<Ae> events = getAeData(datasets, e -> ids.contains(e.getId()));

        return doDCommonService.getColumnData(DatasetType.fromDatasets(datasets), events, sortAttrs, from, count, true);
    }

    /**
     * The set of ids is unique to either Incidence and Severity data providers so we check Incidence first as this currently has the dod table.
     */
    public Collection<Ae> getAeData(Datasets datasets, Predicate<Ae> filter) {

        //  check incidence first as this currently has dod
        final Collection<Ae> events = aeIncidenceDatasetsDataProvider.loadData(datasets).stream()
                .filter(filter)
                .collect(toList());

        if (CollectionUtils.isEmpty(events)) {
            return aeSeverityChangeDatasetsDataProvider.loadData(datasets).stream()
                    .filter(filter)
                    .collect(toList());
        } else {
            return events;
        }
    }

    /*
     * Overrides this from BaseEventService just for the problem with having Incidence and Severity data providers, and no fitlers been passed into the query.
     * The set of ids is unique to either Incidence and Severity data providers so we check Incidence first as this currently has the dod table.
     */
    @Override
    public void writeSelectedDetailsOnDemandCsv(Datasets datasets, Set ids, Writer writer) {
        // The set of ids is unique to either Incidence and Severity data providers
        Collection<Ae> events = getAeData(datasets, e -> ids.contains(e.getId()));

        final DatasetType datasetType = DatasetType.fromDatasets(datasets);
        Map<String, String> columnsTitles = doDCommonService.getDoDColumns(datasetType, events);
        List<Map<String, String>> data = doDCommonService.getColumnData(datasetType, events);
        doDCommonService.writeCsv(data, columnsTitles, writer);
    }

    @Override
    public void writeAMLDataCsv(Datasets datasets, Writer writer, Class<Ae> entityCls, Class<AeRaw> entityRawCls) {
        writeAMLDataCsv(datasets, writer, entityCls, entityRawCls, ds -> getAeData(ds, e -> true));
    }

    @ApplyUsedInTflFilter
    public void writeAesTableToCsv(Datasets datasets, AeGroupByOptions aeLevel, AeFilters aesFilters,
                                   PopulationFilters populationFilters, Writer writer) {
        List<AesTable> aesTableData = getAesTableData(datasets, aeLevel, aesFilters, populationFilters);
        AeTableUtil.writeAesTableToCsv(aesTableData, writer);
    }

    @ApplyUsedInTflFilter
    public List<AesTable> getAesTableData(
            Datasets datasets, AeGroupByOptions termAttribute, Filters<Ae> aefilters, PopulationFilters populationFilters) {

        final FilterResult<Ae> allAeData = getFilteredData(datasets, aefilters, populationFilters);

        // need all subjects, and not just aes filters subjects
        // Also adding an identical subject with All arm to help with counting
        List<Subject> subjects = allAeData.getPopulationFilterResult().parallelStream().
                flatMap(subj -> {
                    Subject allArmSubject = subj.toBuilder().actualArm("All").build();
                    if (datasets.isDetectType()) {
                        return newArrayList(subj, allArmSubject).stream();
                    }
                    return newArrayList(allArmSubject).stream();
                }).
                collect(toList());

        // Also adding an identical subject with All arm to help with counting.  THis is like joining in sql when you return more
        // rows becuase of the join.
        List<Ae> filteredAes = allAeData.parallelStream().
                //filter(ae -> ae.getEvent().getMaxAeSeverityNum() != null). //  remove nulls MaxAeSeverityNum, sql did this
                        flatMap((Ae ae) -> {

                    List<Ae> aesPerTerm = newArrayList(ae);

                    // Flatmap SPECIAL_INTEREST_GROUP into multiple aes with one ae per SPECIAL_INTEREST_GROUP
                    if (termAttribute == AeGroupByOptions.SPECIAL_INTEREST_GROUP) {
                        //  f SpecialInterest Gorups returns [SI1, SI2]
                        List<String> sigs = (List) (termAttribute.getAttribute().getFunction().apply(ae));

                        // create a new Ae with one each
                        aesPerTerm = sigs.stream().map(sig -> {
                            AeRaw perTerm = ae.getEvent().toBuilder().specialInterestGroups(newArrayList(sig)).build();
                            return new Ae(perTerm, ae.getSubject());
                        }).collect(toList());
                    }

                    // Flatmap all aes with multiple SPECIAL_INTEREST_GROUP or single PT, HLT, SOC then add All subjects
                    return aesPerTerm.stream().flatMap(aePerTerm -> {
                        List<Ae> newAes = newArrayList();

                        Subject allArmSubject = aePerTerm.getSubject().toBuilder().actualArm("All").build();
                        newAes.add(new Ae(aePerTerm.getEvent(), allArmSubject));
                        if (datasets.isDetectType()) {
                            newAes.add(aePerTerm); // add with its actual arm
                        }

                        return newAes.stream();
                    });
                }).
                        collect(toList());

        // 1) Count all subjects by arm,
        List<PartitionByCount<String>> allSubjectArmCounts = getAllSubjectCounts(subjects);

        // 2) Find max severity per subject, term and arm,including ALL arm
        // <Subject, Term, Arm> = Max Severity
        List<AeSubjectTermArmMaxSeverity> allAesBySubjectTermAndArmGetMaxSeverity
                = partitionAesBySubjectTermAndArmGetMaxSeverity(filteredAes, termAttribute);

        // 3) From 2) count all subjects by term and arm, including ALL arm
        // <Term, Arm> = Subject Count
        List<PartitionByCount<AeTermArm>> allSubjectCountsPerTermAndArm
                = partitionAesBySubjectTermAndArmCount(allAesBySubjectTermAndArmGetMaxSeverity);

        // 4) From 2) count all subjects by term and arm and max severity, including ALL arm
        // <Term, Arm, Max Severity> = Subject Count
        List<PartitionByCount<AeTermArmMaxSeverity>> subjectCountsPerTermAndArmAndSeverity
                = SQLike.on(allAesBySubjectTermAndArmGetMaxSeverity.parallelStream()).partitionByMapAndDistinctCount(
                i -> new AeTermArmMaxSeverity(
                        i.getTerm(),
                        i.getTreatmentArm(),
                        i.getMaxSeverity()
                ),
                AeSubjectTermArmMaxSeverity::getSubjectId
        );

        // 5) Sort by term and severity
        Comparator<PartitionByCount<AeTermArmMaxSeverity>> comparator = Comparator.comparing(
                e -> e.getKey().getTerm(),
                AlphanumEmptyLastComparator.getInstance());
        comparator = comparator.thenComparing(e -> e.getKey().getMaxSeverity().getWebappSeverity(), AlphanumEmptyLastComparator.getInstance());

        return subjectCountsPerTermAndArmAndSeverity.
                stream().
                sorted(comparator).
                map(e -> {
                            AesTableBuilder builder = AesTable.builder().
                                    grade(e.getKey().getMaxSeverity().getWebappSeverity()).
                                    term(e.getKey().getTerm()).
                                    treatmentArm(e.getKey().getTreatmentArm());

                            builder.subjectCountPerGrade(e.getCount().intValue());
                            builder.subjectCountPerArm(allSubjectArmCounts.parallelStream().
                                    filter(
                                            sge -> sge.getKey().equals(e.getKey().getTreatmentArm())
                                    ).
                                    map(PartitionByCount::getCount).
                                    findFirst().get().intValue());

                            builder.subjectCountPerTerm(allSubjectCountsPerTermAndArm.parallelStream().
                                    filter(
                                            sge -> sge.getKey().getTerm().equals(e.getKey().getTerm())
                                                    && sge.getKey().getTreatmentArm().equals(e.getKey().getTreatmentArm())
                                    ).
                                    map(PartitionByCount::getCount).findFirst().get().intValue());
                            return builder.build();
                        }
                ).
                collect(toList());
    }

    private List<PartitionByCount<String>> getAllSubjectCounts(List<Subject> subjects) {

        return SQLike.on(subjects.parallelStream()).partitionByMapAndDistinctCount(
                Subject::getActualArm,
                Subject::getSubject
        );
    }

    private List<AeSubjectTermArmMaxSeverity> partitionAesBySubjectTermAndArmGetMaxSeverity(List<Ae> filteredAes, AeGroupByOptions termAttribute) {

        return SQLike.on(filteredAes.parallelStream()).partitionByAndThen(
                ae -> new ImmutableTriple<>(
                        ae.getSubject().getSubjectCode(),
                        attrToString(Attributes.<Ae, AeGroupByOptions>get(termAttribute.getGroupByOptionAndParams(), ae)),
                        ae.getSubject().getActualArm()
                ),
                maxBy(comparing((Ae ae) -> ae.getEvent().getMaxAeSeverityNum(), Comparator.nullsLast(naturalOrder()))),
                op -> op.map((Ae ae) -> ae.getEvent().getMaxSeverity()).orElse(null)
        ).stream().map(e -> new AeSubjectTermArmMaxSeverity(e.getKey().left, e.getKey().middle, e.getKey().right, e.getValue())).collect(toList());
    }

    private String attrToString(Object o) {
        return o instanceof Collection ? ((Collection<?>) o).stream().map(Object::toString).collect(joining(", ")) : Objects.toString(o);
    }

    private List<PartitionByCount<AeTermArm>> partitionAesBySubjectTermAndArmCount(List<AeSubjectTermArmMaxSeverity> subjectSeverityAndArmMaxSeverity) {

        return SQLike.on(subjectSeverityAndArmMaxSeverity.parallelStream()).partitionByMapAndDistinctCount(
                i -> new AeTermArm(i.getTerm(), i.getTreatmentArm()),
                AeSubjectTermArmMaxSeverity::getSubjectId
        ).stream().map(e -> new PartitionByCount<>(e.getKey(), e.getCount())).collect(toList());
    }

    @Override
    public AxisOptions<AeGroupByOptions> getAvailableOverTimeChartXAxis(Datasets datasets, Filters<Ae> filters, PopulationFilters populationFilters) {
        return getAxisOptions(datasets, filters, populationFilters, AeGroupByOptions.OVERTIME_DURATION);
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId, Filters<Ae> filters) {
        List<Map<String, String>> output = new ArrayList<>();
        FilterResult<Ae> filtered = getFilteredData(datasets, filters, PopulationFilters.empty(),
                null, s -> s.getSubjectCode().equals(subjectId) || s.getSubjectId().equals(subjectId));
        filtered.stream()
                .sorted(Comparator.comparing(Ae::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .forEach(ae -> {
                    AeRaw aeRaw = ae.getEvent();
                    output.addAll(aeRaw.getAeSeverities().stream()
                            .sorted(Comparator.comparing(AeSeverityRaw::getStartDate, Comparator.nullsLast(Comparator.naturalOrder())))
                            .map(raw -> getAeRow(ae, raw))
                            .collect(Collectors.toList()));
                });
        return output;
    }

    @Override
    public List<Map<String, String>> getSingleSubjectData(Datasets datasets, String subjectId) {
        return getSingleSubjectData(datasets, subjectId, AeFilters.empty());
    }

    @Override
    public Map<String, String> getSingleSubjectColumns(DatasetType datasetType) {
        return IntStream.range(0, COLUMN_NAMES.length).boxed()
                .collect(Collectors.toMap(i -> COLUMN_NAMES[i], i -> COLUMN_DESCRIPTIONS[i],
                        (i1, i2) -> i1, LinkedHashMap::new));
    }

    @Override
    public String getSsvTableName() {
        return "adverseEvents";
    }

    @Override
    public String getSsvTableDisplayName() {
        return "ADVERSE EVENTS";
    }

    @Override
    public String getHeaderName() {
        return "ADVERSE EVENTS";
    }

    @Override
    public double getOrder() {
        return 15;
    }

    private Map<String, String> getAeRow(Ae ae, AeSeverityRaw raw) {
        OptionalInt aeDuration = DaysUtil.daysBetween(raw.getStartDate(), raw.getEndDate());
        AeRaw aeRaw = ae.getEvent();
        Optional<AeSeverityRaw> aeSeverityRawOpt = Optional.of(raw);
        String[] values = ArrayUtils.toArray(
                aeRaw.getPt(),
                aeRaw.getHlt(),
                aeRaw.getSoc(),
                aeSeverityRawOpt.map(AeSeverityRaw::getSeverity).map(AeSeverity::getWebappSeverity).orElse(null),
                aeRaw.getMaxAeSeverity(),
                aeSeverityRawOpt.map(AeSeverityRaw::getStartDate).map(DaysUtil::toDateTimeString).orElse(null),
                aeSeverityRawOpt.map(AeSeverityRaw::getEndDate).map(DaysUtil::toDateTimeString).orElse(null),
                aeDuration.isPresent() ? String.valueOf(aeDuration.getAsInt() + 1) : null,
                aeRaw.getSerious(),
                ae.getCausalityAsString(),
                aeRaw.getText(),
                aeRaw.getImmuneMediated());
        return IntStream.range(0, COLUMN_NAMES.length).boxed()
                .collect(Collectors.toMap(i -> COLUMN_NAMES[i], i -> values[i] == null ? "" : values[i]));
    }

    public Set<String> getJumpToAesSocs(Datasets datasets, Set<String> aesSocs) {
        FilterResult<Ae> filtered = getFilteredData(datasets, AeFilters.empty(), PopulationFilters.empty());
        return filtered.getFilteredEvents().stream()
                .map(ae -> ae.getEvent().getSoc())
                .filter(soc -> soc != null && aesSocs.contains(soc.toLowerCase()))
                .collect(toSet());
    }
}

