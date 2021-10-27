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

package com.acuity.visualisations.rawdatamodel.service.ae.chord;

import com.acuity.visualisations.rawdatamodel.aspect.ApplyUsedInTflFilter;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.SubjectAwareDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.service.BaseEventService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.AeChordDiagramColoringService;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.service.plots.SimpleSelectionMatchingService;
import com.acuity.visualisations.rawdatamodel.service.ssv.ColorInitializer;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChordGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.AlphanumEmptyLastComparator;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.ColorbyCategoriesUtil;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputChordDiagramData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputChordDiagramEntry;
import com.acuity.visualisations.rawdatamodel.vo.plots.ChordCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.ChordContributor;
import com.acuity.visualisations.rawdatamodel.vo.plots.ChordDiagramSelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.EventWrapper;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;


import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions.PT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.periodsOverlappedWithShift;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
public class AeChordDiagramService extends BaseEventService<AeRaw, Ae, AeGroupByOptions>
        implements SimpleSelectionMatchingService<ChordCalculationObject, ChordGroupByOptions>, ColorInitializer {

    public static final String PERCENTAGE_OF_LINKS = "percentageOfLinks";
    public static final String TIME_FRAME = "timeFrame";
    public static final String TERM_LEVEL = "termLevel";
    public static final Integer MAX_TIME_FRAME_AE_CHORDS = 30;

    @Autowired
    private AeService aeService;
    @Autowired
    private AeChordDiagramColoringService aeChordDiagramColoringService;

    private static final String MIN_CHORD_WIDTH_FACTOR = "10";

    @Override
    protected SubjectAwareDatasetsDataProvider<AeRaw, Ae> getEventDataProvider(Datasets datasets, Filters<Ae> filters) {
        return aeService.getAeEventDataProvider(datasets, filters);
    }

    /**
     * This method calculate data to represent them on the chord plot. A chord connects two terms of adverse events.
     * The connection means that two adverse events occurred at an intersected period of time after a dose.
     * The width of the chord is defined by sum of the aes co-occurring over patients.
     * For instance, patient1 and patient2 had aes intersected by dates with term1 and term2.
     * Then all of the patients contribute into the term1-term2 chord (fot these patients width = 2)
     */
    public Map<Ae.TermLevel, OutputChordDiagramData> getAesOnChordDiagram(Datasets datasets,
                                                                          Map<String, String> additionalSettings,
                                                                          AeFilters eventFilters, PopulationFilters populationFilters) {
        FilterResult<Ae> filtered = getFilteredData(datasets, eventFilters, populationFilters);
        Map<Ae.TermLevel, Collection<ChordCalculationObject>> chords
                = calculateChords(filtered.getFilteredResult(), additionalSettings);
        return transformToOutputChordDiagramData(chords, datasets);
    }

    public ChordDiagramSelectionDetail getChordDiagramSelectionDetails(Datasets datasets, Filters<Ae> filters,
                                                                       PopulationFilters populationFilters,
                                                                       ChartSelection<ChordCalculationObject, ChordGroupByOptions,
                                                                               ChartSelectionItem<ChordCalculationObject,
                                                                                       ChordGroupByOptions>> selection,
                                                                       Map<String, String> additionalSettings) {
        FilterResult<Ae> filtered = getFilteredData(datasets, filters, populationFilters);
        return getSelectionDetails(filtered, selection, additionalSettings);
    }

    @ApplyUsedInTflFilter
    public List<TrellisOptions<AeGroupByOptions>> getChordDiagramColorByOptions(Datasets datasets,
                                                                                Filters<Ae> filters,
                                                                                PopulationFilters populationFilters,
                                                                                ChartGroupByOptions<Ae, AeGroupByOptions> settings) {

        FilterResult<Ae> filtered = getFilteredData(datasets, filters, populationFilters, null);
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(), settings.getOptions()
                .get(SERIES_BY)
                .getGroupByOption());
    }

    private Collection<ChordCalculationObject> filterLinksByMinWidth(Collection<ChordCalculationObject> chords,
                                                                     int minChordWidthFactor) {
        int maxWidth = chords.stream()
                .mapToInt(ChordCalculationObject::getWidth)
                .max().orElse(Integer.MAX_VALUE);
        double bottom = maxWidth * (minChordWidthFactor / 100.);
        return chords.stream().filter(e -> e.getWidth() >= bottom).collect(toList());
    }

    private Map<Ae.TermLevel, OutputChordDiagramData> transformToOutputChordDiagramData(Map<Ae.TermLevel,
            Collection<ChordCalculationObject>> chordsByTerms, Datasets datasets) {
        return chordsByTerms.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, entry -> {
                    Collection<ChordCalculationObject> v = entry.getValue();

                    List<OutputChordDiagramEntry> data = v.stream()
                            .map(e -> OutputChordDiagramEntry.builder()
                                    .start(e.getStart()).end(e.getEnd()).width(e.getWidth())
                                    .contributors(e.getContributors()
                                    .entrySet().stream().collect(toMap(Map.Entry::getKey, c -> c.getValue().size())))
                                    .build())
                            .sorted(Comparator.comparing(OutputChordDiagramEntry::getStart)) // from requirements
                            .collect(toList());

                    Map<String, String> colorBook = v.stream()
                            .map(e -> Stream.of(e.getStart(), e.getEnd()))
                            .flatMap(e -> e)
                            .distinct()
                            //colors must be assigned to sorted events to keep the legend's colors ordered
                            .sorted(AlphanumEmptyLastComparator.getInstance())
                            .collect(toMap(e -> e, e -> aeChordDiagramColoringService.getColor(e,
                                    ColorbyCategoriesUtil.getDatasetColorByOption(datasets, entry.getKey()))));

                    return OutputChordDiagramData.builder().data(data).colorBook(colorBook).build();
                }));
    }

    private Map<Ae.TermLevel, Collection<ChordCalculationObject>> calculateChords(Collection<Ae> aes,
                                                                                  Map<String, String> additionalSettings) {
        return isCalculationRequired(aes) ? calculateChordsOverSubjects(aes, additionalSettings) : getEmptyChords();
    }

    private Map<Ae.TermLevel, Collection<ChordCalculationObject>> calculateChordsOverSubjects(Collection<Ae> aes,
                                                                                              Map<String, String> additionalSettings) {
        final int shift = Integer.parseInt(additionalSettings.getOrDefault(TIME_FRAME, "0"));
        Assert.isTrue(shift >= 0 && shift <= MAX_TIME_FRAME_AE_CHORDS, "Number of days should be in [0, 30]");
        Map<String, Map<Ae.TermLevel, Map<String, ChordCalculationObject>>> chordsPerSubjectPerTerm =
                calculateChordsPerSubjectPerTerm(aes, shift);

        // calculate co-occurring's over all patients (sum the chords' widths over patients)
        Map<Ae.TermLevel, Collection<ChordCalculationObject>> chords = chordsPerSubjectPerTerm.values().stream()
                .flatMap(e -> e.entrySet().stream())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (m1, m2) -> {
                    m2.forEach((key, value) -> m1.merge(key, value, ChordCalculationObject::merge));
                    return m1;
                })).entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().values()));

        int percentageOfVisibleLinks = Integer.valueOf(additionalSettings.getOrDefault(PERCENTAGE_OF_LINKS, MIN_CHORD_WIDTH_FACTOR));
        chords = chords.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> filterLinksByMinWidth(e.getValue(), percentageOfVisibleLinks)));

        return chords;
    }

    private Map<String, Map<Ae.TermLevel, Map<String, ChordCalculationObject>>> calculateChordsPerSubjectPerTerm(
            Collection<Ae> aes, int shift) {
        Map<String, List<Ae>> aesBySubject = aes.stream()
                .filter(e -> e.getStartDate() != null)
                .collect(groupingBy(SubjectAwareWrapper::getSubjectCode));

        // calculate co-occurring's (chords) for every subject separately
        return aesBySubject.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> calculateChords(e.getValue(), shift)));
    }

    /**
     * Calculates chords
     *
     * @param events             adverse events belong to one patient
     * @return chords - map of term level to the map where concatenated terms are a key and ChordCalculationObject is a value
     */
    private Map<Ae.TermLevel, Map<String, ChordCalculationObject>> calculateChords(List<Ae> events,
                                                                                   int shift) {
        Map<String, List<Ae>> aesByPt =
                events.stream()
                        .collect(groupingBy(o -> {
                            Object pt = Attributes.get(PT.getAttribute(), o);
                            return pt.toString();
                        }, TreeMap::new, toList()));

        Map<String, List<AesMergingWrapper>> aesWrappersByTerm = new TreeMap<>();
        aesByPt.forEach((k, v) -> aesWrappersByTerm.put(k, getWrappersFromAes(v)));

        // merge aes intersected with the days shift
        aesWrappersByTerm.keySet()
                .forEach(key -> aesWrappersByTerm.put(key, mergeIntersectedWithShift(aesWrappersByTerm.get(key), shift)));

        List<String> termSorted = new ArrayList<>(aesWrappersByTerm.keySet());

        ChordTermWrapper termsWrapper = ChordTermWrapper.empty();
        // we need to compare all Preferred Terms of adverse events with all to find intersections by dates
        // any intersection contributes into the chord's width
        termSorted.forEach(term -> {
            List<AesMergingWrapper> aesWrapperPerTerm = aesWrappersByTerm.remove(term);
            aesWrappersByTerm.forEach((key, value) -> {
                List<ChordContributor> width = countAesPeriodsIntersectionsWithShift(aesWrapperPerTerm, value, shift);
                if (!width.isEmpty()) {
                    Map<String, List<ChordContributor>> contributors = new HashMap<>();
                    contributors.put(aesWrapperPerTerm.iterator().next().getSubjectCode(), width);
                    // calculate PTs
                    termsWrapper.populateWith(Ae.TermLevel.PT, term, key, width.size(), contributors);
                    // calculate HTLs
                    String term1 = extractValue(aesWrapperPerTerm, e -> e.getEvent().getHlt());
                    String term2 = extractValue(value, e -> e.getEvent().getHlt());
                    termsWrapper.populateWith(Ae.TermLevel.HLT, term1, term2, width.size(), contributors);
                    // calculate SOCs
                    term1 = extractValue(aesWrapperPerTerm, e -> e.getEvent().getSoc());
                    term2 = extractValue(value, e -> e.getEvent().getSoc());
                    termsWrapper.populateWith(Ae.TermLevel.SOC, term1, term2, width.size(), contributors);
                }
            });
        });
        return termsWrapper.getChords();
    }

    private String extractValue(List<AesMergingWrapper> mergedAes, Function<Ae, String> extractor) {
        return mergedAes.stream()
                .flatMap(e -> e.getAes().stream())
                .map(extractor)
                .filter(Objects::nonNull)
                .findAny().orElse(DEFAULT_EMPTY_VALUE);
    }

    private List<AesMergingWrapper> mergeIntersectedWithShift(List<AesMergingWrapper> aes, int shift) {
        List<AesMergingWrapper> sorted = aes.stream().sorted(Comparator.comparing(AesMergingWrapper::getStartDate))
                .collect(toList());
        if (sorted.size() > 1) {
            for (int i = 0; i < sorted.size() - 1; i++) {
                if (periodsOverlappedWithShift(sorted.get(i).getStartDate(), sorted.get(i).getEndDate(),
                        sorted.get(i + 1).getStartDate(), sorted.get(i + 1).getEndDate(), shift)) {
                    sorted.set(i + 1, new AesMergingWrapper(sorted.get(i), sorted.get(i + 1)));
                    sorted.set(i, null);
                }
            }
        }
        return sorted.stream().filter(Objects::nonNull).collect(toList());
    }

    /**
     * Counts how many aes from {@code aes1} have intersections with {@code aes2} taking into account the {@code shift} param
     *
     * @param aes1  first list of aes
     * @param aes2  second list of aes
     * @param shift allows shift a period to check intersection, for instance 11.01.18-13.01.18 and 15.01.18-17.01.18
     *              intersected  with shift 2+ days
     * @return amount of intersections
     */
    private List<ChordContributor> countAesPeriodsIntersectionsWithShift(List<AesMergingWrapper> aes1, List<AesMergingWrapper> aes2, int shift) {
        return aes1.stream().map(ae1 -> aes2.stream()
                .filter(ae2 -> periodsOverlappedWithShift(ae1.getStartDate(), ae1.getEndDate(), ae2.getStartDate(), ae2.getEndDate(), shift))
                .map(ae2 -> ChordContributor.builder()
                        .startEventIds(ae1.getAes().stream().map(EventWrapper::getId).collect(toSet()))
                        .endEventIds(ae2.getAes().stream().map(EventWrapper::getId).collect(toSet()))
                        .build())
                .collect(toList()))
                .flatMap(Collection::stream).collect(toList());
    }

    private List<AesMergingWrapper> getWrappersFromAes(List<Ae> aes) {
        return aes.stream().map(AesMergingWrapper::new).collect(toList());
    }

    public ChordDiagramSelectionDetail getSelectionDetails(FilterResult<Ae> filtered,
                                                           ChartSelection<ChordCalculationObject,
                                                                   ChordGroupByOptions,
                                                                   ChartSelectionItem<ChordCalculationObject,
                                                                           ChordGroupByOptions>> selection,
                                                           Map<String, String> additionalSettings) {

        Map<Ae.TermLevel, Collection<ChordCalculationObject>> chordsPerSubjectPerTerm =
                calculateChords(filtered.getFilteredResult(), additionalSettings);
        Ae.TermLevel termLevel = getTermLevel(additionalSettings);
        final List<ChordCalculationObject> matchedItems = getMatchedItems(chordsPerSubjectPerTerm.get(termLevel),
                selection);
        final Set<ChordContributor> eventIds = matchedItems.stream()
                .map(i -> i.getContributors().values())
                .flatMap(Collection::stream)
                .flatMap(Collection::stream)
                .collect(toSet());

        return ChordDiagramSelectionDetail.builder()
                .eventIds(eventIds)
                .eventCount(getContributingEventsCount(eventIds))
                .subjectIds(getSubjectIds(matchedItems, filtered.getPopulationFilterResult()))
                .totalEvents(filtered.getAllEvents().size())
                .totalSubjects(filtered.getPopulationFilterResult().size())
                .build();
    }

    private int getContributingEventsCount(Set<ChordContributor> eventIds) {
        return (int) eventIds.stream()
                .flatMap(e -> Stream.of(e.getStartEventIds(), e.getEndEventIds())
                        .flatMap(Collection::stream)
                ).distinct().count();
    }

    private Set<String> getSubjectIds(List<ChordCalculationObject> matchedItems, FilterResult<Subject> filteredPopulation) {

        Map<String, String> subjectIdBySubjectCode = filteredPopulation
                .stream()
                .collect(Collectors.toMap(Subject::getSubjectCode, Subject::getSubjectId));
        return matchedItems.stream().map(i -> i.getContributors().keySet())
                .flatMap(Collection::stream)
                .distinct()
                .map(subjectIdBySubjectCode::get)
                .collect(toSet());
    }

    public List<Map<String, String>> getChordDetailsOnDemandData(Datasets datasets, Set<ChordContributor> eventIds,
                                                            List<SortAttrs> sortAttrs, long from, long count) {

        Collection<AeChordContributor> contributors = getAeChordContributors(datasets, eventIds);
        return doDCommonService.getColumnData(DatasetType.fromDatasets(datasets), contributors,
                sortAttrs, from, count, true);
    }

    public Map<String, String> getDoDColumns(Datasets datasets, Map<String, String> additionalSettings) {
        FilterResult<Ae> filtered = getFilteredData(datasets, AeFilters.empty(), PopulationFilters.empty());
        if (filtered.getFilteredResult().isEmpty()) {
            return Collections.emptyMap();
        }

        Map<Ae.TermLevel, Collection<ChordCalculationObject>> chords
                = calculateChords(filtered.getFilteredResult(), additionalSettings);

        final Set<ChordContributor> chordContributors = chords.get(Ae.TermLevel.PT).stream()
                .flatMap(e -> e.getContributors().values().stream())
                .flatMap(Collection::stream).collect(toSet());

        Collection<AeChordContributor> contributors = getAeChordContributors(datasets,
                chordContributors);
        return doDCommonService.getDoDColumns(Column.DatasetType.fromDatasets(datasets), contributors);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void writeSelectedDetailsOnDemandCsv(Datasets datasets, Set ids, Writer writer) {
        writeChordEventsDoDCsv(writer, datasets, ids);
    }

    @Override
    public void writeAllDetailsOnDemandCsv(Datasets datasets, Writer writer, Filters<Ae> filters,
                                           PopulationFilters populationFilters) {
        writeAllDetailsOnDemandCsv(datasets, writer, filters, populationFilters, null);
    }

    public void writeAllDetailsOnDemandCsv(Datasets datasets, Writer writer, Filters<Ae> filters,
                                           PopulationFilters populationFilters,
                                           Map<String, String> additionalSettings) {
        if (additionalSettings == null || additionalSettings.isEmpty()) {
           throw new IllegalArgumentException("additionalSettings need to be passed");
        }

        FilterResult<Ae> filtered = getFilteredData(datasets, filters, populationFilters);
        Map<Ae.TermLevel, Collection<ChordCalculationObject>> chords
                = calculateChords(filtered.getFilteredResult(), additionalSettings);

        Ae.TermLevel termLevel = getTermLevel(additionalSettings);

        final Set<ChordContributor> chordContributors = chords.get(termLevel).stream()
                .flatMap(e -> e.getContributors().values().stream())
                .flatMap(Collection::stream).collect(toSet());
        writeChordEventsDoDCsv(writer, datasets, chordContributors);
    }

    private Collection<AeChordContributor> getAeChordContributors(Datasets datasets, Set<ChordContributor> eventIds) {

        Set<String> aeIds = new HashSet<>();
        eventIds.forEach(e -> {
            aeIds.addAll(e.getStartEventIds());
            aeIds.addAll(e.getEndEventIds());
        });
        // The set of ids is unique to either Incidence and Severity data providers
        Map<String, Ae> eventsById = aeService.getAeData(datasets, e -> aeIds.contains(e.getId()))
                .stream().collect(toMap(EventWrapper::getId, Function.identity()));

        return eventIds.stream()
                .map(c -> new AeChordContributor(c.getStartEventIds().stream().map(eventsById::get).collect(toSet()),
                        c.getEndEventIds().stream().map(eventsById::get).collect(toSet()))).collect(toList());
    }

    private void writeChordEventsDoDCsv(Writer writer, Datasets datasets, Set<ChordContributor> eventIds) {
        Collection<AeChordContributor> contributors = getAeChordContributors(datasets,
                eventIds);
        DatasetType datasetType = DatasetType.fromDatasets(datasets);
        Map<String, String> columnsTitles = doDCommonService.getDoDColumns(datasetType, contributors);
        List<Map<String, String>> data = doDCommonService.getColumnData(datasetType, contributors);
        doDCommonService.writeCsv(data, columnsTitles, writer);
    }

    @Override
    public void generateColors(Datasets datasets) {
        getAesOnChordDiagram(datasets, new HashMap<>(), AeFilters.empty(), PopulationFilters.empty());
    }

    private Ae.TermLevel getTermLevel(Map<String, String> additionalSettings) {
        return Optional.ofNullable(additionalSettings.get(TERM_LEVEL))
                .map(Ae.TermLevel::valueOf).orElse(Ae.TermLevel.PT);
    }

    private boolean isCalculationRequired(Collection<Ae> aes) {
        return aes.stream().anyMatch(e -> e.getStartDate() != null);
    }

    private Map<Ae.TermLevel, Collection<ChordCalculationObject>> getEmptyChords() {
        return ChordTermWrapper.empty()
                               .getChords().keySet().stream()
                               .collect(toMap(Function.identity(), e -> Collections.emptyList()));
    }
}
