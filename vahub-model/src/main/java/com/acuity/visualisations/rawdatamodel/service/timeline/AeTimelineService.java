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

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.DayHourUtils;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.timeline.aes.AeDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.aes.AeDetailEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.aes.AeMaxCtcEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.aes.SubjectAesDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.aes.SubjectAesSummary;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.getGreatestDate;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
public class AeTimelineService extends BasePlotEventService<AeRaw, Ae, AeGroupByOptions> implements BaseEventTimelineService<Ae> {

    @Autowired
    private AeService aeService;

    public List<SubjectAesSummary> getAesSummaries(Datasets datasets,
                                                   AeFilters aeFilters,
                                                   PopulationFilters populationFilters,
                                                   DayZeroType value,
                                                   String dayZeroOption) {
        List<SubjectAesDetail> subjectAesDetails = getAesDetails(datasets, aeFilters, populationFilters, value, dayZeroOption);

        List<SubjectAesSummary> subjectAesMaxCtcTimelines = new CopyOnWriteArrayList<>();

        subjectAesDetails.forEach(subjectAesTimeline -> {
            List<AeDetailEvent> allSubjectsAeEvents = subjectAesTimeline.getAes().stream()
                    .flatMap(aes -> aes.getEvents().stream())
                    .filter(aes -> DayHourUtils.isValid(aes.getStart(), aes.getEnd()))
                    .sorted(Comparator.comparing(r -> r.getStart().getDayHour()))
                    .collect(toList());

            Map<Double, DateDayHour> allStartMap = allSubjectsAeEvents.stream().collect(
                    toMap(aeEvent -> aeEvent.getStart()
                            .getDayHour(), AeDetailEvent::getStart, (oldValue, newValue) -> oldValue));
            Map<Double, DateDayHour> allEndMap = allSubjectsAeEvents.stream().collect(
                    toMap(aeEvent -> aeEvent.getEnd()
                            .getDayHour(), AeDetailEvent::getEnd, (oldValue, newValue) -> oldValue));
            Map<Date, Double> allDoseStartMap = allSubjectsAeEvents.stream().collect(
                    toMap(aeEvent -> aeEvent.getStart().getDate(), aeEvent -> aeEvent.getStart()
                            .getDoseDayHour(), (oldValue, newValue) -> oldValue));
            Map<Date, Double> allDoseEndMap = allSubjectsAeEvents.stream().collect(
                    toMap(aeEvent -> aeEvent.getEnd().getDate(), aeEvent -> aeEvent.getEnd()
                            .getDoseDayHour(), (oldValue, newValue) -> oldValue));

            Map<Double, DateDayHour> allDistinctDayHoursDateMap = new TreeMap<>();
            allDistinctDayHoursDateMap.putAll(allStartMap);
            allDistinctDayHoursDateMap.putAll(allEndMap);

            Map<Date, Double> allDistinctDoseDayHoursDateMap = new TreeMap<>();
            allDistinctDoseDayHoursDateMap.putAll(allDoseStartMap);
            allDistinctDoseDayHoursDateMap.putAll(allDoseEndMap);

            Double maxEndDayHour = allSubjectsAeEvents.stream()
                    .map(aeEvent -> aeEvent.getEnd().getDayHour())
                    .max(Double::compareTo).orElse(null);
            List<AeMaxCtcEvent> aeMaxCtcEvents = new ArrayList<>();

            Map.Entry<Double, DateDayHour> lastDayHour = null;
            for (Map.Entry<Double, DateDayHour> dayHour : allDistinctDayHoursDateMap.entrySet()) {
                if (lastDayHour == null) {
                    lastDayHour = dayHour;
                } else if (lastDayHour != dayHour) {
                    double start = lastDayHour.getKey();
                    double end = dayHour.getKey();

                    // find max ctc for these dayHours
                    Set<AeDetailEvent> events = allSubjectsAeEvents.stream()
                            .filter(e -> e.getStart().getDayHour() <= start && e.getEnd().getDayHour() >= end)
                            .collect(toSet());

                    if (!events.isEmpty()) {
                        int maxNum = events.stream().mapToInt(AeDetailEvent::getSeverityGradeNum).max().orElse(0);
                        DateDayHour startDDH = buildDateDayHourFrom(lastDayHour, allDistinctDoseDayHoursDateMap);
                        DateDayHour endDDH = buildDateDayHourFrom(dayHour, allDistinctDoseDayHoursDateMap);
                        AeDetailEvent lastVisitEvent = events.stream()
                                .max((e1, e2) -> ObjectUtils.compare(e1.getEndDate(), e2.getEndDate())).get();
                        aeMaxCtcEvents.add(
                                AeMaxCtcEvent.builder()
                                        .start(startDDH)
                                        .end(endDDH)
                                        .duration(DayHourUtils.getDuration(startDDH, endDDH))
                                        .maxSeverityGradeNum(maxNum)
                                        .maxSeverityGrade(events.stream()
                                                .filter(e -> e.getSeverityGradeNum() == maxNum)
                                                .findFirst()
                                                .map(AeDetailEvent::getSeverityGrade)
                                                .orElse("Empty"))
                                        .pts(events.stream().map(AeDetailEvent::getPt).collect(toSet()))
                                        .numberOfEvents(events.size())
                                        // only set for last event for onGoing, if any events have ongoing then set as ongoing
                                        .ongoing(maxEndDayHour != null && end == maxEndDayHour && events.stream()
                                                .anyMatch(AeDetailEvent::isOngoing))
                                        .endType(lastVisitEvent.getEndType())
                                        .lastVisitNumber(lastVisitEvent.getLastVisitNumber())
                                        .build());
                    }
                    lastDayHour = dayHour;
                }
            }

            subjectAesMaxCtcTimelines.add(SubjectAesSummary.builder()
                    .subject(subjectAesTimeline.getSubject())
                    .subjectId(subjectAesTimeline.getSubjectId())
                    .events(aeMaxCtcEvents)
                    .build());
        });
        return subjectAesMaxCtcTimelines;
    }

    public List<SubjectAesDetail> getAesDetails(Datasets datasets,
                                                AeFilters aeFilters,
                                                PopulationFilters populationFilters,
                                                DayZeroType dayZeroType,
                                                String dayZeroOption) {

        return filterAndGroupBySubjects(datasets, aeFilters, populationFilters).entrySet().stream()
                .map(e -> buildSubjectAeDetailEventFrom(e.getKey(), e.getValue(), dayZeroType, dayZeroOption))
                .sorted(Comparator.comparing(SubjectAesDetail::getSubjectId))
                .collect(Collectors.toList());
    }

    private DateDayHour buildDateDayHourFrom(Map.Entry<Double, DateDayHour> dateDayHour,
                                             Map<Date, Double> allDistinctDoseDayHoursDateMap) {
        DateDayHour result = new DateDayHour();
        result.setDate(dateDayHour.getValue().getDate());
        result.setDayHour(dateDayHour.getKey());
        result.setDayHourAsString(dateDayHour.getValue().getDayHourAsString());
        result.setStudyDayHourAsString(dateDayHour.getValue().getStudyDayHourAsString());
        result.setDoseDayHour(allDistinctDoseDayHoursDateMap.get(dateDayHour.getValue().getDate()));
        return result;
    }

    private Map<Subject, List<Ae>> filterAndGroupBySubjects(Datasets datasets,
                                                            AeFilters aeFilters,
                                                            PopulationFilters populationFilters) {
        return getTimelineFilteredData(datasets, aeFilters, populationFilters).stream()
                .collect(groupingBy(SubjectAwareWrapper::getSubject));
    }

    private AeDetailEvent buildAeDetailEventFrom(Ae ae, Map<String, List<Ae>> ptSortedAes, DayZeroType dayZeroType,
                                                 String dayZeroOption, Date maxAllAesEndDate, Date maxAllAeStartDate) {
        AeRaw event = ae.getEvent();

        List<Ae> sortedAe = ptSortedAes.get(event.getPt() == null ? Attributes.DEFAULT_EMPTY_VALUE : event.getPt());
        int startDateIndex = sortedAe.indexOf(ae);
        Date nextStartDate = startDateIndex + 1 < sortedAe.size() ? sortedAe.get(startDateIndex + 1)
                .getStartDate() : null;

        DateDayHour start = DayHourUtils.extractDaysHours(ae.getSubject(), dayZeroType, dayZeroOption, ae.getStartDate());
        Date endDate = getImputedEndDate(ae, nextStartDate, maxAllAesEndDate, maxAllAeStartDate);
        DateDayHour end = DayHourUtils.extractDaysHours(ae.getSubject(), dayZeroType, dayZeroOption, endDate);
        return AeDetailEvent.builder()
                .id(ae.getId())
                .severityGradeNum(event.getMaxAeSeverityNum() == null ? 0 : event.getMaxAeSeverityNum())
                .severityGrade(event.getMaxAeSeverity())
                .pt(event.getPt())
                .serious(event.getSerious())
                .causality(event.getCausality() == null ? ae.getCausalityAsString() : event.getCausality())
                .actionTaken(event.getActionTaken() == null ? ae.getActionTakenAsString() : event.getActionTaken())
                .start(start)
                .end(end)
                .duration(DayHourUtils.getDuration(start, end))
                .ongoing(isAeOngoing(ae))
                .endType(ae.getEvent().getAeSeverities().stream()
                        .max((e1, e2) -> ObjectUtils.compare(e1.getEndDate(), e2.getEndDate()))
                        .get().getEndType())
                .lastVisitNumber(ae.getSubject().getLastVisitNumber())
                .imputedEndDate(ae.getEndDate() == null)
                .build();
    }

    private boolean isAeOngoing(Ae ae) {
        return ae.getEvent().getAeSeverities().stream().anyMatch(AeSeverityRaw::isOngoing);
    }


    private Date getImputedEndDate(Ae ae, Date nextStartDate, Date maxAllAesEndDate, Date maxAllAeStartDate) {
        return ae.getEndDate() == null
                ? nextStartDate == null ? getGreatestDate(ae.getSubject()
                .getDateOfWithdrawal(), maxAllAesEndDate, maxAllAeStartDate) : nextStartDate
                : ae.getEndDate();
    }


    private SubjectAesDetail buildSubjectAeDetailEventFrom(Subject subject, List<Ae> aes, DayZeroType dayZeroType,
                                                           String dayZeroOption) {
        Map<AeDetail, List<Ae>> map = aes.stream().collect(groupingBy(ae -> AeDetail.builder()
                .hlt(ae.getEvent().getHlt())
                .pt(ae.getEvent().getPt())
                .soc(ae.getEvent().getSoc())
                .build()));

        Date maxAllAesEndDate = aes.stream()
                .map(Ae::getEndDate)
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .orElse(null);

        Date maxAllAesStartDate = aes.stream()
                .map(Ae::getStartDate)
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .orElse(null);

        Map<String, List<Ae>> ptSortedAes = aes.stream()
                .sorted(Comparator.comparing(Ae::getStartDate, Comparator.nullsLast(Date::compareTo)))
                .collect(Collectors.groupingBy(ae ->
                        ae.getEvent().getPt() == null ? Attributes.DEFAULT_EMPTY_VALUE : ae.getEvent().getPt()));

        List<AeDetail> aeDetails = map.entrySet().stream().map(entry -> {
            List<AeDetailEvent> aeEvents = entry.getValue()
                    .stream()
                    .map(ae -> buildAeDetailEventFrom(ae, ptSortedAes, dayZeroType, dayZeroOption, maxAllAesEndDate,
                            maxAllAesStartDate))
                    .sorted(Comparator.comparing(aeDetailEvent -> aeDetailEvent.getStart().getDate()))
                    .collect(Collectors.toList());

            return AeDetail.builder()
                    .pt(entry.getKey().getPt() == null ? Attributes.DEFAULT_EMPTY_VALUE : entry.getKey().getPt())
                    .soc(entry.getKey().getSoc())
                    .hlt(entry.getKey().getHlt())
                    .events(aeEvents).build();
        }).sorted(Comparator.comparing(AeDetail::getPt)).collect(Collectors.toList());

        return SubjectAesDetail.builder()
                .subject(subject.getSubjectCode())
                .subjectId(subject.getSubjectId())
                .aes(aeDetails).build();
    }

    @Override
    public TimelineTrack getTimelineTrack() {
        return TimelineTrack.AES;
    }

    @Override
    public List<Ae> getTimelineFilteredData(Datasets datasets, Filters<Ae> filters, PopulationFilters populationFilters) {
        Collection<Ae> result = aeService.getFilteredData(datasets,
                filters == null ? AeFilters.empty() : filters, populationFilters).getFilteredEvents();
        return result.stream()
                .filter(ae -> ae.getStartDate() != null && ae.getSubject().getFirstTreatmentDate() != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Subject> getTimelineFilteredSubjects(Datasets datasets, Filters<Ae> filters, PopulationFilters populationFilters) {
        return getTimelineFilteredData(datasets, filters, populationFilters)
                .stream()
                .map(SubjectAwareWrapper::getSubject)
                .distinct()
                .collect(toList());
    }
}
