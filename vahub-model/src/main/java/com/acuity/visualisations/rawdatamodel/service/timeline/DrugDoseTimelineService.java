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

package com.acuity.visualisations.rawdatamodel.service.timeline;

import com.acuity.visualisations.common.aspect.TimeMe;
import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.filters.DoseDiscFilters;
import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.DoseDiscService;
import com.acuity.visualisations.rawdatamodel.service.event.DrugDoseService;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.DoseAndFrequency;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.DosingSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.DrugDosingSummary;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.Frequency;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.MaxDoseType;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.PercentChange;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.SubjectDosingSummary;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.SubjectDrugDosingSummary;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.Range;
import lombok.extern.slf4j.Slf4j;
import one.util.streamex.StreamEx;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.acuity.visualisations.rawdatamodel.service.timeline.data.PercentChangeCalculator.calculate;
import static com.acuity.visualisations.rawdatamodel.util.DayHourUtils.extractDaysHours;
import static com.acuity.visualisations.rawdatamodel.vo.timeline.dose.PeriodType.ACTIVE;
import static com.acuity.visualisations.rawdatamodel.vo.timeline.dose.PeriodType.DISCONTINUED;
import static com.acuity.visualisations.rawdatamodel.vo.timeline.dose.PeriodType.INACTIVE;
import static com.acuity.visualisations.rawdatamodel.vo.timeline.dose.PeriodType.ONGOING;
import static com.google.common.collect.Range.open;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static java.util.Comparator.comparing;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * This is a replacement for TimelineDosingService from the timeline module.
 */
@Slf4j
@Service
public class DrugDoseTimelineService implements BaseEventTimelineService<DrugDose> {
    private final DrugDoseService drugDoseService;

    private final DoseDiscService doseDiscService;

    public DrugDoseTimelineService(DrugDoseService drugDoseService, DoseDiscService doseDiscService) {
        this.drugDoseService = drugDoseService;
        this.doseDiscService = doseDiscService;
    }

    public List<String> getSubjects(Datasets datasets, DrugDoseFilters filters, PopulationFilters populationFilters) {
        return drugDoseService.getSubjects(datasets, filters, populationFilters);
    }

    @TimeMe
    public List<SubjectDosingSummary> getDosingSummaries(
            Datasets datasetsObject,
            DayZeroType dayZeroType,
            String dayZeroOption,
            MaxDoseType maxDoseType,
            DrugDoseFilters doseFilters,
            PopulationFilters populationFilters
    ) {
        List<DrugDose> dosesForAllSubjects = getTimelineFilteredData(datasetsObject, doseFilters, populationFilters);
        Map<String, Map<String, List<Date>>> drugDiscontinuationDates = loadDrugDiscontinuationDates(datasetsObject, populationFilters, doseFilters);
        Function<String, Map<String, DoseAndFrequency>> maxDoseCollector = getMaxDoseCollector(maxDoseType, dosesForAllSubjects);

        return StreamEx.of(dosesForAllSubjects)
                .sorted(Comparator.comparing(dose -> dose.getSubject().getSubjectCode()))
                .mapToEntry(SubjectAwareWrapper::getSubject, Function.identity())
                .sorted(Comparator.comparing(entry -> entry.getKey().getSubjectCode()))
                .collapseKeys()
                .mapKeyValue((subject, doses) -> {
                    log.debug("Processing doses for subject [subject={} ({}), doses={}]", subject.getSubjectCode(),
                            subject.getSubjectId(), doses.size());

                    Map<String, List<Date>> drugToDiscontinuationDate = drugDiscontinuationDates.getOrDefault(subject.getSubjectCode(), emptyMap());

                    doses = insertInactiveDosings(doses, drugToDiscontinuationDate);

                    return toSubjectDosingSummary(
                            subject,
                            doses,
                            dayZeroType,
                            dayZeroOption,
                            maxDoseCollector.apply(subject.getSubjectId()),
                            drugToDiscontinuationDate,
                            false
                    );
                }).toList();
    }

    @TimeMe
    public List<SubjectDrugDosingSummary> getDosingSummariesByDrug(
            Datasets datasetsObject,
            DayZeroType dayZeroType,
            String dayZeroOption,
            MaxDoseType maxDoseType,
            DrugDoseFilters doseFilters,
            PopulationFilters populationFilters
    ) {
        List<DrugDose> dosesForAllSubjects = getTimelineFilteredData(datasetsObject, doseFilters, populationFilters);
        Map<String, Map<String, List<Date>>> drugDiscontinuationDates = loadDrugDiscontinuationDates(datasetsObject, populationFilters, doseFilters);
        Function<String, Map<String, DoseAndFrequency>> maxDoseCollector = getMaxDoseCollector(maxDoseType, dosesForAllSubjects);

        return StreamEx.of(dosesForAllSubjects)
                .sorted(Comparator.comparing(dose -> dose.getSubject().getSubjectCode()))
                .mapToEntry(SubjectAwareWrapper::getSubject, Function.identity())
                .sorted(Comparator.comparing(entry -> entry.getKey().getSubjectCode()))
                .collapseKeys()
                .mapToValue((subject, dosesForSubject) -> {
                    Map<String, List<Date>> drugToDiscontinuationDates = drugDiscontinuationDates.getOrDefault(subject.getSubjectCode(), emptyMap());

                    return StreamEx.of(dosesForSubject)
                            .mapToEntry(dose -> dose.getEvent().getDrugName(), Function.identity())
                            .sorted(Map.Entry.comparingByKey())
                            .collapseKeys()
                            .mapToValue((drug, originalDoses) -> insertInactiveDosings(originalDoses, drugToDiscontinuationDates))
                            .mapToValue((drug, processedDoses) -> toSubjectDosingSummary(
                                    subject,
                                    processedDoses,
                                    dayZeroType,
                                    dayZeroOption,
                                    singletonMap(drug, maxDoseCollector.apply(subject.getSubjectId()).get(drug)),
                                    singletonMap(drug, drugToDiscontinuationDates.getOrDefault(drug, emptyList())),
                                    true))
                            .mapKeyValue((drug, subjectDosingSummary) -> DrugDosingSummary.builder()
                                    .drug(drug)
                                    .ongoing(subjectDosingSummary.isOngoing())
                                    .events(subjectDosingSummary.getEvents()).build()).toList();
                })
                .mapKeyValue((subject, drugDosingSummaries) -> {
                    SubjectDrugDosingSummary subjectDrugDosingSummary = new SubjectDrugDosingSummary();

                    subjectDrugDosingSummary.setSubject(subject.getSubjectCode());
                    subjectDrugDosingSummary.setSubjectId(subject.getSubjectId());
                    subjectDrugDosingSummary.setDrugs(drugDosingSummaries);

                    return subjectDrugDosingSummary;
                }).toList();
    }

    private Map<String, Map<String, List<Date>>> loadDrugDiscontinuationDates(Datasets datasetsObject,
                                                                              PopulationFilters populationFilters, DrugDoseFilters doseFilters) {

        FilterResult<DoseDisc> discs = doseDiscService.getFilteredData(datasetsObject, DoseDiscFilters.empty(), populationFilters);

        Set<String> drugs = doseFilters.getStudyDrug().getValues();

        return StreamEx.of(discs.stream())
                .filter(e -> e.getDiscDate() != null)
                .filter(e -> (drugs.isEmpty() || drugs.contains(e.getStudyDrug())))
                .mapToEntry(SubjectAwareWrapper::getSubjectCode, Function.identity())
                .sorted(Map.Entry.comparingByKey())
                .collapseKeys(
                        groupingBy(DoseDisc::getStudyDrug,
                                Collectors.mapping(
                                        DoseDisc::getDiscDate,
                                        Collectors.toList())))
                .toMap();
    }

    private List<DrugDose> insertInactiveDosings(
            List<DrugDose> allDoses,
            Map<String, List<Date>> discontinuationDates
    ) {
        return StreamEx.of(allDoses)
                .mapToEntry(dose -> dose.getEvent().getDrugName(), Function.identity())
                .filterKeys(Objects::nonNull)
                .sorted(Map.Entry.comparingByKey())
                .collapseKeys()
                .peekValues(doses -> doses.sort(Comparator.comparing(d -> d.getEvent().getStartDate())))
                .mapToKey((drug, doses) -> discontinuationDates.get(drug))
                .mapToKey((discontinuationDatesForDrug, doses) -> new TreeSet<>(Optional.ofNullable(discontinuationDatesForDrug).orElse(emptyList())))
                .flatMapKeyValue((sortedDiscontinuationDatesForDrug, doses) ->
                        StreamEx.of(doses)
                                .append((DrugDose) null)
                                .pairMap(ImmutablePair::of)
                                .mapToEntry(ImmutablePair::getLeft, ImmutablePair::getRight)
                                .flatMapKeyValue((DrugDose dose, DrugDose nextDose) -> {
                                    Date discontinuationDate = sortedDiscontinuationDatesForDrug.ceiling(dose.getStartDate());
                                    DrugDose adjustedDose = adjustDose(dose, nextDose, discontinuationDate);

                                    boolean nextDoseIsActive = nextDose == null || isActive(nextDose);
                                    boolean currentDoseIsActiveOrNotLast = nextDose != null || isActive(dose);
                                    boolean currentDoseIsNotOngoing = !ONGOING.getDbValue().equals(dose.getEvent().getSubsequentPeriodType());

                                    StreamEx<DrugDose> compiledDoses = StreamEx.of(adjustedDose);

                                    if (nextDoseIsActive && currentDoseIsActiveOrNotLast && currentDoseIsNotOngoing) {
                                        return compiledDoses.append(
                                                prepareInactiveDoseToInsert(adjustedDose, nextDose, discontinuationDate));
                                    }

                                    return compiledDoses;
                                }))
                .toList();
    }

    private DrugDose adjustDose(DrugDose dose, DrugDose nextDose, Date discontinuationDate) {
        DrugDoseRaw doseWithAdjustedEndDate = DrugDoseRaw.builder()
                .startDate(dose.getStartDate())
                .endDate(getEndDateForCurrentDose(dose, nextDose, discontinuationDate))
                .dose(dose.getEvent().getDose())
                .doseUnit(dose.getEvent().getDoseUnit())
                .frequency(dose.getEvent().getFrequency())
                .frequencyName(dose.getEvent().getFrequencyName())
                .drug(dose.getEvent().getDrugName())
                .periodType(dose.getEvent().getPeriodType())
                .subsequentPeriodType(dose.getEvent().getSubsequentPeriodType())
                .build();

        if (log.isTraceEnabled()) {
            log.trace("Added actual {} period [{}, {}]", isActive(dose) ? ACTIVE : INACTIVE,
                    dose.getStartDate(), dose.getEndDate());
        }

        return new DrugDose(doseWithAdjustedEndDate, dose.getSubject());
    }

    private Stream<DrugDose> prepareInactiveDoseToInsert(DrugDose adjustedDose, DrugDose nextDose, Date discontinuationDate) {

        Date inactiveStartDate = adjustedDose.getEvent().getEndDate();
        Date inactiveEndDate;

        boolean discontinuedEvent = false;

        if (nextDose == null) {
            // Sometimes the end date of the active period may be equal to the discontinuation date.
            if (discontinuationDate != null && !discontinuationDate.before(adjustedDose.getEvent().getEndDate())) {
                inactiveEndDate = discontinuationDate;
                discontinuedEvent = true;
            } else {
                // The drug was discontinued at some point but then resumed, hence considered ongoing.
                inactiveEndDate = adjustedDose.getSubject().getLastEtlDate();
            }
        } else {
            inactiveEndDate = nextDose.getStartDate();

            if (discontinuationDate != null && discontinuationDate.before(inactiveEndDate)) {
                discontinuedEvent = true;
                inactiveEndDate = discontinuationDate;
            }
        }

        if (inactiveEndDate == null || !inactiveEndDate.after(inactiveStartDate)) {
            return Stream.empty();
        }

        DrugDoseRaw event = DrugDoseRaw.builder()
                .startDate(inactiveStartDate)
                .endDate(inactiveEndDate)
                .dose(0.0)
                .drug(adjustedDose.getEvent().getDrugName())
                .periodType(discontinuedEvent ? DISCONTINUED.getDbValue() : INACTIVE.getDbValue())
                .subsequentPeriodType(ACTIVE.getDbValue())
                .build();

        log.trace("Added computed {} period [{}, {}]", event.getPeriodType(), event.getStartDate(), event.getEndDate());

        return Stream.of(new DrugDose(event, adjustedDose.getSubject()));
    }

    private List<Range<Date>> getRangesForSingleDrug(List<DrugDose> doses) {
        return StreamEx.of(doses)
                .filter(d -> d.getStartDate() != null && !d.getStartDate().equals(d.getEndDate()))
                .mapToEntry(
                        d -> Range.open(d.getStartDate(), d.getEndDate()),
                        Function.identity())
                .sorted(Comparator.comparing(entry -> entry.getKey().lowerEndpoint()))
                .collapseKeys((left, right) -> left)
                .map(entry -> entry)
                .prepend((Map.Entry<Range<Date>, DrugDose>) null)
                .pairMap((previousRangeEntry, rangeEntry) -> {
                    Range<Date> originalRange = rangeEntry.getKey();
                    DrugDose dose = rangeEntry.getValue();
                    return (previousRangeEntry != null && !isActive(dose))
                            ? Range.open(previousRangeEntry.getKey().upperEndpoint(), originalRange.upperEndpoint())
                            : originalRange;
                })
                .toList();
    }

    private List<Range<Date>> getRangesForMultipleDrugs(List<DrugDose> doses) {

        Optional<Date> maxEndOfInactivePeriod = doses.stream()
                .filter(dose -> !isActive(dose))
                .map(DrugDose::getEndDate)
                .max(Comparator.naturalOrder());

        return StreamEx.of(doses)
                .flatMap(dose -> isActive(dose)
                        ? Stream.of(dose.getStartDate(), dose.getEndDate())
                        : Stream.of(dose.getStartDate()))
                .append(StreamEx.of(maxEndOfInactivePeriod))
                .distinct()
                .sorted()
                .pairMap(Range::open)
                .toList();
    }

    private SubjectDosingSummary toSubjectDosingSummary(
            Subject subject,
            List<DrugDose> doses,
            DayZeroType dayZeroType,
            String dayZeroOption,
            Map<String, DoseAndFrequency> maxDoses,
            Map<String, List<Date>> discontinuationDates,
            boolean isDosingSummariesByDrug
    ) {
        List<Range<Date>> ranges = isDosingSummariesByDrug
                ? getRangesForSingleDrug(doses)
                : getRangesForMultipleDrugs(doses);

        if (log.isTraceEnabled()) {
            log.trace("Building events for periods: {}",
                    ranges.stream()
                            .map(range -> Arrays.asList(
                                    requireNonNull(extractDaysHours(subject, dayZeroType, dayZeroOption,
                                            range.lowerEndpoint())).getDayHourAsString(),
                                    requireNonNull(extractDaysHours(subject, dayZeroType, dayZeroOption,
                                            range.upperEndpoint())).getDayHourAsString()
                            ))
                            .collect(toList()));
        }

        List<DosingSummaryEvent> events = StreamEx.of(ranges)
                .mapToEntry(range -> new DosingSummaryEvent())
                .peekKeyValue((range, event) -> {
                    event.setStart(extractDaysHours(subject, dayZeroType, dayZeroOption, range.lowerEndpoint()));
                    event.setEnd(extractDaysHours(subject, dayZeroType, dayZeroOption, range.upperEndpoint()));
                })
                .peekKeyValue((range, event) ->
                        event.setDrugDoses(doses.stream()
                                .filter(d -> range.isConnected(open(d.getStartDate(), d.getEndDate())))
                                .map(DrugDose::getEvent)
                                .sorted(Comparator.comparing(DrugDoseRaw::getDrugName)
                                        .thenComparing(Comparator.comparing(DrugDoseRaw::getDose).reversed()))
                                .map(this::toDoseAndFrequency)
                                .distinct()
                                .collect(toList())))
                .peekValues(event -> {
                    Map<String, DoseAndFrequency> activeDoseAndFrequencies = event.getDrugDoses().stream()
                            .filter(d -> Double.compare(d.getDose(), 0) > 0)
                            .collect(toMap(DoseAndFrequency::getDrug, Function.identity(),
                                    (left, right) -> left.getDose() < right.getDose() ? right : left));

                    event.setPeriodType(activeDoseAndFrequencies.isEmpty() ? INACTIVE : ACTIVE);

                    event.setPercentChange(
                            new PercentChange(
                                    calculate(maxDoses, activeDoseAndFrequencies, DoseAndFrequency::getDosePerDay),
                                    calculate(maxDoses, activeDoseAndFrequencies, DoseAndFrequency::getDosePerAdmin)));
                })
                .peekKeyValue((range, event) -> {
                    event.setSubsequentPeriodType(INACTIVE);
                    //The end of dosing event should be calculated as earliest non-null date after event start date stored in db,
                    //but for inactive periods there is also calculated event start date which is the end date of previous dosing period.
                    //Correct end date should be calculated based on stored value not calculated
                    //Here actual start date (stored value) is taken and then earliest date is calculated based on this value
                    final DrugDose dose = doses.stream().filter(d -> range.isConnected(open(d.getStartDate(), d.getEndDate()))).findFirst().orElse(null);
                    if (dose != null) {
                        Range<Date> openClosed = Range.openClosed(dose.getStartDate(), dose.getEndDate());
                        if (discontinuationDates != null
                                && discontinuationDates.values().stream().flatMap(Collection::stream).anyMatch(openClosed::contains)) {
                            Date lastDate = isDosingSummariesByDrug ? new TreeSet<>(discontinuationDates.values().stream()
                                    .flatMap(Collection::stream).collect(toList())).ceiling(openClosed.lowerEndpoint()) : range.upperEndpoint();
                            if (doses.stream().noneMatch(d -> Objects.equals(lastDate, d.getStartDate()) && d.getEvent().getDose() > 0)) {
                                event.setSubsequentPeriodType(DISCONTINUED);
                                event.setEnd(extractDaysHours(subject, dayZeroType, dayZeroOption, lastDate));
                            }
                        }
                    }
                })
                .values()
                .peek(event -> {
                    if (log.isTraceEnabled()) {
                        log.trace("Making event [type={}, start={}, end={}, drugs={}]", event.getPeriodType(),
                                requireNonNull(event.getStart()).getDayHourAsString(), requireNonNull(event.getEnd()).getDayHourAsString(),
                                event.getDrugDoses().stream().map(DoseAndFrequency::getDrug).collect(toList()));
                    }
                })
                .toList();

        SubjectDosingSummary summary = new SubjectDosingSummary();

        summary.setSubjectId(subject.getSubjectId());
        summary.setSubject(subject.getSubjectCode());
        summary.setEvents(events);

        if (CollectionUtils.isNotEmpty(events)) {
            DosingSummaryEvent lastEvent = events.get(events.size() - 1);
            doses.stream()
                    .filter(d -> Objects.equals(d.getEndDate(), lastEvent.getEnd().getDate()))
                    .filter(d -> ONGOING.getDbValue().equals(d.getEvent().getSubsequentPeriodType())
                            || lastEvent.getEnd().getDate().equals(d.getSubject().getLastEtlDate()))
                    .findAny()
                    .ifPresent(d -> lastEvent.setOngoing(true));
            summary.setOngoing(lastEvent.isOngoing());
        }

        return summary;
    }


    private Function<String, Map<String, DoseAndFrequency>> getMaxDoseCollector(MaxDoseType maxDoseType, List<DrugDose> doses) {
        if (maxDoseType == MaxDoseType.PER_STUDY) {
            Map<String, DoseAndFrequency> maxDoses = getMaxDosesPerDrug(doses);

            return subjectId -> maxDoses;
        } else {
            Map<String, Map<String, DoseAndFrequency>> maxDosesPerSubject = StreamEx.of(doses)
                    .mapToEntry(SubjectAwareWrapper::getSubjectId, Function.identity())
                    .sorted(Map.Entry.comparingByKey())
                    .collapseKeys()
                    .mapToValue((subject, subjectDoses) -> getMaxDosesPerDrug(subjectDoses))
                    .toMap();

            return maxDosesPerSubject::get;
        }
    }

    private Map<String, DoseAndFrequency> getMaxDosesPerDrug(List<DrugDose> doses) {
        return StreamEx.of(doses)
                .map(DrugDose::getEvent)
                .mapToEntry(DrugDoseRaw::getDrugName, Function.identity())
                .sorted(Map.Entry.comparingByKey())
                .collapseKeys(BinaryOperator.maxBy(comparing(this::toDailyDose)))
                .values()
                .collect(toMap(DrugDoseRaw::getDrugName, this::toDoseAndFrequency));
    }

    private boolean isActive(DrugDose d) {
        return Double.compare(d.getEvent().getDose(), 0) > 0;
    }

    private Date getEndDateForCurrentDose(DrugDose dose, DrugDose nextDose, Date discontinuationDate) {
        return getMinDateAfterStartDate(dose, nextDose == null ? null : nextDose.getStartDate(),
                dose.getEndDate(), discontinuationDate, dose.getSubject().getLastEtlDate());
    }

    private Date getMinDateAfterStartDate(DrugDose dose, Date... dates) {
        return Stream.of(dates)
                .filter(Objects::nonNull)
                .filter(e -> e.after(dose.getStartDate()))
                .min(Comparator.naturalOrder()).orElse(null);
    }

    private double rankFromString(String frequency) {
        switch (frequency.toLowerCase()) {
            case "every 4 weeks":
                return 0.0357;
            case "every 3 weeks":
                return 0.0476;
            case "every 2 weeks":
                return 0.0714;
            case "every week":
                return 0.1429;
            case "2 times per week":
                return 0.2857;
            case "3 times per week":
                return 0.4285;
            case "4 times per week":
                return 0.5714;
            case "once":
                return 0.0027;
            case "qm":
                return 0.0333;
            case "qod":
                return 0.5;
            case "qd":
                return 1;
            case "bid":
                return 2;
            case "tid":
                return 3;
            case "qid":
                return 4;
            case "q4h":
                return 6;
            case "q2h":
                return 12;
            case "qh":
                return 24;
            case "prn":
                return 0.0000000002;
            case "other":
                return 0.0000000001;
            default:
                return 0;
        }
    }

    private Double toDailyDose(DrugDoseRaw d) {
        if (d.getFrequencyName() == null || d.getFrequency() == null) {
            return d.getDose();
        } else {
            Integer frequency = d.getFrequency();
            return d.getDose() * (frequency != null ? frequency : rankFromString(d.getFrequencyName()));
        }
    }

    private DoseAndFrequency toDoseAndFrequency(DrugDoseRaw dose) {
        double rank;

        if (dose.getFrequencyName() != null) {
            rank = rankFromString(dose.getFrequencyName());
        } else {
            rank = dose.getFrequency() != null ? dose.getFrequency() : 0;
        }

        String displayedName = dose.getFrequencyName() != null ? dose.getFrequencyName() : "N/A";
        Frequency frequency = new Frequency(displayedName, rank);

        return new DoseAndFrequency(dose.getDrugName(), dose.getDose(), dose.getDoseUnit(), frequency);
    }

    @Override
    public TimelineTrack getTimelineTrack() {
        return TimelineTrack.DOSING;
    }

    @Override
    public List<DrugDose> getTimelineFilteredData(Datasets datasets, Filters<DrugDose> filters, PopulationFilters populationFilters) {
        FilterResult<DrugDose> results = drugDoseService.getFilteredData(datasets,
                filters == null ? DrugDoseFilters.empty() : filters, populationFilters);

        log.debug("Loaded {} dose events [datasets = {}, dose filters = {}, population filters = {}]",
                results.size(), datasets, filters, populationFilters);

        return results.getFilteredResult().stream()
                .filter(this::validateDates)
                .collect(toList());
    }

    private boolean validateDates(DrugDose dose) {
        if (dose.getStartDate() == null) {
            return false;
        }

        Subject subject = dose.getSubject();

        Date maxDate = subject.getLastEtlDate();

        if (maxDate != null && !maxDate.after(dose.getStartDate())) {
            return false;
        }

        if (dose.getEndDate() != null) {
            if (dose.getEndDate().before(dose.getStartDate())) {
                return false;
            }

            if (maxDate != null) {
                return !dose.getEndDate().after(maxDate);
            }
        }

        return true;
    }

    @Override
    public List<Subject> getTimelineFilteredSubjects(Datasets datasets, Filters<DrugDose> filters, PopulationFilters populationFilters) {
        return getTimelineFilteredData(datasets, filters, populationFilters)
                .stream()
                .map(SubjectAwareWrapper::getSubject)
                .distinct()
                .collect(toList());
    }
}
