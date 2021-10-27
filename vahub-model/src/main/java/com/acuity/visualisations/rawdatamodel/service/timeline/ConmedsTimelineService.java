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
import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.ConmedsService;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineBucket;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineCollector;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import com.acuity.visualisations.rawdatamodel.util.Constants;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.ConmedRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.ConmedEventsByClass;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.ConmedEventsByDrug;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.ConmedSingleEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.ConmedSummary;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.ConmedSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.SubjectConmedByClass;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.SubjectConmedByDrug;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.SubjectConmedSummary;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.acuity.visualisations.rawdatamodel.util.DayHourUtils.extractDaysHours;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class ConmedsTimelineService implements BaseEventTimelineService<Conmed> {

    @Autowired
    private ConmedsService conmedsService;

    public List<String> getSubjects(Datasets datasets,
                                    ConmedFilters conmedsFilters,
                                    PopulationFilters populationFilters) {
        Collection<Conmed> conmeds = getTimelineFilteredData(datasets, conmedsFilters, populationFilters);
        return conmeds.stream().map(HasSubject::getSubjectId).distinct().collect(toList());
    }

    public List<SubjectConmedSummary> getConmedsSummaries(Datasets datasets,
                                                          ConmedFilters conmedsFilters,
                                                          PopulationFilters populationFilters,
                                                          DayZeroType dayZeroType,
                                                          String dayZeroOption) {
        Collection<Conmed> conmeds = getTimelineFilteredData(datasets, conmedsFilters, populationFilters);

        Map<Subject, List<Conmed>> subjectGroups = conmeds.stream()
                .collect(groupingBy(Conmed::getSubject));

        return subjectGroups.entrySet().stream().map(subjectGroup -> {
            Subject subject = subjectGroup.getKey();

            Date maxDate = getMaxPossibleDate(subject, subjectGroup.getValue());

            List<TimelineBucket<Conmed>> buckets = collectToMedicationBuckets(
                    subjectGroup.getValue(), datasets, maxDate);

            List<ConmedSummaryEvent> events = toConmedSummaryEvents(
                    buckets, subject, dayZeroType, dayZeroOption);

            return toSubjectConmedSummary(subject, events);
        }).sorted(Comparator.comparing(SubjectSummary::getSubjectId)).collect(toList());
    }

    public List<SubjectConmedByClass> getConmedsByClass(Datasets datasets,
                                                        ConmedFilters conmedsFilters,
                                                        PopulationFilters populationFilters,
                                                        DayZeroType dayZeroType,
                                                        String dayZeroOption) {
        Collection<Conmed> conmeds = getTimelineFilteredData(datasets, conmedsFilters, populationFilters);

        Map<Subject, Map<String, List<Conmed>>> subjectClassGroups = conmeds.stream()
                .collect(groupingBy(Conmed::getSubject,
                        groupingBy(conmed -> valueOrEmpty(conmed.getEvent().getMedicationClass()))));

        return subjectClassGroups.entrySet().stream().map(subjectClassGroup -> {
            Subject subject = subjectClassGroup.getKey();

            Date maxDate = getMaxPossibleDate(subject, subjectClassGroup.getValue().values());

            List<ConmedEventsByClass> classes = subjectClassGroup.getValue().entrySet().stream()
                    .map(classGroup -> {
                        List<TimelineBucket<Conmed>> buckets = collectToMedicationBuckets(
                                classGroup.getValue(), datasets, maxDate);
                        List<ConmedSummaryEvent> events = toConmedSummaryEvents(
                                buckets, subject, dayZeroType, dayZeroOption);
                        return toConmedEventsByClass(classGroup.getKey(), events);
                    }).sorted(Comparator.comparing(ConmedEventsByClass::getConmedClass)).collect(Collectors.toList());

            return toSubjectConmedByClass(subject, classes);
        }).sorted(Comparator.comparing(SubjectSummary::getSubjectId)).collect(toList());
    }

    public List<SubjectConmedByDrug> getConmedsByDrug(Datasets datasets,
                                                      ConmedFilters conmedsFilters,
                                                      PopulationFilters populationFilters,
                                                      DayZeroType dayZeroType,
                                                      String dayZeroOption) {
        Collection<Conmed> conmeds = getTimelineFilteredData(datasets, conmedsFilters, populationFilters);

        Map<Subject, Map<String, List<Conmed>>> subjectDrugGroups = conmeds.stream()
                .collect(groupingBy(Conmed::getSubject,
                        groupingBy(conmed -> valueOrEmpty(conmed.getEvent().getMedicationName()))));

        return subjectDrugGroups.entrySet().stream().map(subjectDrugGroup -> {
            Subject subject = subjectDrugGroup.getKey();
            Date maxDate = getMaxPossibleDate(subject, subjectDrugGroup.getValue().values());

            List<ConmedEventsByDrug> medications = subjectDrugGroup.getValue().entrySet().stream()
                    .map(drugGroup -> {
                        List<TimelineBucket<Conmed>> buckets = collectToMedicationBuckets(
                                drugGroup.getValue(), datasets, maxDate);

                        List<ConmedSingleEvent> events = buckets.stream().map(bucket -> {

                            DateDayHour start = extractDaysHours(subject, dayZeroType, dayZeroOption, bucket.getStartDate());
                            DateDayHour end = extractDaysHours(subject, dayZeroType, dayZeroOption, bucket.getEndDate());
                            ConmedRaw sample = bucket.getItems().get(0).getEvent();
                            return toConmedSingleEvent(start, end, bucket.isOngoing(),
                                    drugGroup.getKey(),
                                    sample.getTreatmentReason(), sample.getDoseFrequency(), sample.getDose());
                        }).sorted(Comparator.comparing(s -> s.getStart().getDate())).collect(toList());

                        return toConmedEventsByDrug(drugGroup.getKey(), events);
                    }).sorted(Comparator.comparing(ConmedEventsByDrug::getConmedMedication)).collect(Collectors.toList());

            return toSubjectConmedByDrug(subject, medications);
        }).sorted(Comparator.comparing(SubjectSummary::getSubjectId)).collect(toList());
    }

    private static String valueOrEmpty(String value) {
        return value == null ? Constants.EMPTY : value;
    }

    private static Date getMaxPossibleDate(Subject subject, List<Conmed> events) {
        return Stream.concat(events.stream().flatMap(
                conmed -> Stream.of(conmed.getStartDate(), conmed.getEndDate())),
                Stream.of(subject.getStudyLeaveDate()))
                .filter(Objects::nonNull)
                .max(Date::compareTo).get();
    }

    private static Date getMaxPossibleDate(Subject subject, Collection<List<Conmed>> events) {
        return getMaxPossibleDate(subject, events.stream().flatMap(Collection::stream).collect(toList()));
    }

    private static List<TimelineBucket<Conmed>> collectToMedicationBuckets(Collection<Conmed> conmeds,
                                                                           Datasets datasets,
                                                                           Date maxDate) {
        List<TimelineBucket<Conmed>> buckets = TimelineCollector.collect(conmeds,
                (conmed1, conmed2) -> Objects.equals(
                        conmed1.getEvent().getMedicationName(),
                        conmed2.getEvent().getMedicationName()
                ), datasets.isDetectType());

        if (!buckets.isEmpty()) {
            TimelineBucket<Conmed> lastBucket = buckets.get(buckets.size() - 1);
            if (lastBucket.getEndDate() == null) {
                lastBucket.setEndDate(DaysUtil.addDays(DaysUtil.truncLocalTime(maxDate), 1));
                lastBucket.setOngoing(true);
            }
        }
        return buckets;
    }

    private static List<ConmedSummaryEvent> toConmedSummaryEvents(List<TimelineBucket<Conmed>> buckets,
                                                                  Subject subject,
                                                                  DayZeroType dayZeroType,
                                                                  String dayZeroOption) {
        return buckets.stream().map(bucket -> {

            DateDayHour start = extractDaysHours(subject, dayZeroType, dayZeroOption, bucket.getStartDate());

            DateDayHour end = extractDaysHours(subject, dayZeroType, dayZeroOption, bucket.getEndDate());

            List<ConmedSummary> conmedSummaries = bucket.getItems().stream()
                    .collect(groupingBy(c -> valueOrEmpty(c.getEvent().getMedicationName())))
                    .entrySet().stream().map(entry -> toConmedSummary(entry.getKey(), entry.getValue()))
                    .sorted(Comparator.comparing(ConmedSummary::getConmed))
                    .collect(toList());

            return toConmedSummaryEvent(start, end, bucket.isOngoing(), false, conmedSummaries);
        }).sorted(Comparator.comparing(s -> s.getStart().getDate(), Comparator.nullsLast(Date::compareTo))).collect(toList());
    }

    private static ConmedSummary toConmedSummary(String medicationName, List<Conmed> conmeds) {
        ConmedSummary out = new ConmedSummary();
        out.setConmed(medicationName);

        out.setDoses(conmeds.stream().map(c -> c.getEvent().getDose())
                .filter(Objects::nonNull).distinct().collect(toList()));

        out.setIndications(conmeds.stream().map(c -> c.getEvent().getTreatmentReason())
                .filter(Objects::nonNull).distinct().collect(toList()));

        out.setFrequencies(conmeds.stream()
                .map(c -> StringUtils.defaultString(c.getEvent().getDoseFrequency(), Constants.NA))
                .distinct().collect(toList()));

        return out;
    }

    private static ConmedSingleEvent toConmedSingleEvent(DateDayHour start,
                                                         DateDayHour end,
                                                         boolean ongoing,
                                                         String conmed,
                                                         String indication,
                                                         String frequency,
                                                         Double dose
    ) {
        ConmedSingleEvent out = new ConmedSingleEvent();
        out.setStart(start);
        out.setEnd(end);
        out.setOngoing(ongoing);
        out.setConmed(conmed);
        out.setIndication(indication);
        out.setFrequency(StringUtils.defaultString(frequency, Constants.NA));
        out.setDose(dose);
        return out;
    }

    private static ConmedSummaryEvent toConmedSummaryEvent(DateDayHour start,
                                                           DateDayHour end,
                                                           boolean ongoing,
                                                           boolean imputedEndDate,
                                                           List<ConmedSummary> conmeds) {
        ConmedSummaryEvent out = new ConmedSummaryEvent();
        out.setStart(start);
        out.setEnd(end);
        out.setOngoing(ongoing);
        out.setImputedEndDate(imputedEndDate);
        out.setConmeds(conmeds);
        return out;
    }

    private static ConmedEventsByDrug toConmedEventsByDrug(String medicationName,
                                                           List<ConmedSingleEvent> conmeds) {
        ConmedEventsByDrug out = new ConmedEventsByDrug();
        out.setConmedMedication(medicationName);
        out.setEvents(conmeds);
        return out;
    }

    private static ConmedEventsByClass toConmedEventsByClass(String className,
                                                             List<ConmedSummaryEvent> conmeds) {
        ConmedEventsByClass out = new ConmedEventsByClass();
        out.setConmedClass(className);
        out.setEvents(conmeds);
        return out;
    }

    private static SubjectConmedByDrug toSubjectConmedByDrug(Subject subject,
                                                             List<ConmedEventsByDrug> conmedMedications) {
        SubjectConmedByDrug out = new SubjectConmedByDrug();
        out.setSubject(subject.getSubjectCode());
        out.setSubjectId(subject.getSubjectId());
        out.setConmedMedications(conmedMedications);
        return out;
    }

    private static SubjectConmedSummary toSubjectConmedSummary(Subject subject,
                                                               List<ConmedSummaryEvent> events) {
        SubjectConmedSummary out = new SubjectConmedSummary();
        out.setSubject(subject.getSubjectCode());
        out.setSubjectId(subject.getSubjectId());
        out.setEvents(events);
        return out;
    }

    private static SubjectConmedByClass toSubjectConmedByClass(Subject subject,
                                                               List<ConmedEventsByClass> events) {
        SubjectConmedByClass out = new SubjectConmedByClass();
        out.setSubject(subject.getSubjectCode());
        out.setSubjectId(subject.getSubjectId());
        out.setConmedClasses(events);
        return out;
    }

    @Override
    public TimelineTrack getTimelineTrack() {
        return TimelineTrack.CONMEDS;
    }

    @Override
    public List<Conmed> getTimelineFilteredData(Datasets datasets, Filters<Conmed> filters, PopulationFilters populationFilters) {
        FilterResult<Conmed> results = conmedsService.getFilteredData(datasets,
                filters == null ? ConmedFilters.empty() : filters, populationFilters);

        return results.getFilteredResult().stream()
                .filter(e -> e.getStartDate() != null)
                .collect(toList());
    }

    @Override
    public List<Subject> getTimelineFilteredSubjects(Datasets datasets, Filters<Conmed> filters, PopulationFilters populationFilters) {
        return getTimelineFilteredData(datasets, filters, populationFilters)
                .stream()
                .map(SubjectAwareWrapper::getSubject)
                .distinct()
                .collect(toList());
    }
}
