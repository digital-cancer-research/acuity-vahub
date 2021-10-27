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
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.visualisations.rawdatamodel.service.event.VitalService;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import com.acuity.visualisations.rawdatamodel.util.Constants;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.VitalRaw;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.vitals.SubjectVitalsDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.vitals.SubjectVitalsSummary;
import com.acuity.visualisations.rawdatamodel.vo.timeline.vitals.VitalsDetailEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.vitals.VitalsSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.vitals.VitalsTests;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.BaselineUtil.chooseSummaryBaselineDate;
import static com.acuity.visualisations.rawdatamodel.util.DayHourUtils.extractDaysHours;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class VitalsTimelineService implements BaseEventTimelineService<Vital> {

    private final VitalService vitalService;

    public List<SubjectVitalsSummary> getVitalsSummaries(Datasets datasets,
                                                         VitalFilters vitalsFilters,
                                                         PopulationFilters populationFilters,
                                                         DayZeroType dayZeroType,
                                                         String dayZeroOption) {
        Collection<Vital> vitals = getTimelineFilteredData(datasets, vitalsFilters, populationFilters);

        Map<Subject, Map<Date, List<Vital>>> subjectDateGroups = vitals.stream()
                .collect(groupingBy(Vital::getSubject,
                        groupingBy(Vital::getEventDate)));

        return subjectDateGroups.entrySet().stream().map(subjectDateGroup -> {
            Subject subject = subjectDateGroup.getKey();
            List<VitalsSummaryEvent> events = subjectDateGroup.getValue().values().stream().map(vitalList -> {
                Vital sampleVital = vitalList.get(0);
                DateDayHour start = extractDaysHours(sampleVital.getSubject(), dayZeroType, dayZeroOption,
                        sampleVital.getEventDate());

                Double maxPercentChange = calculateMaxPercentChangeFromBaseline(vitalList);

                return toVitalsSummaryEvent(sampleVital.getEvent(), start, maxPercentChange);
            }).sorted(Comparator.comparing(s -> s.getStart().getDate())).collect(toList());

            Date eventsBaselineDate = chooseSummaryBaselineDate(vitals, subject);
            DateDayHour baseline = extractDaysHours(subject, dayZeroType, dayZeroOption, eventsBaselineDate);

            return toSubjectVitalsSummary(subject, events, baseline);
        }).sorted(Comparator.comparing(SubjectVitalsSummary::getSubjectId)).collect(Collectors.toList());
    }

    public List<SubjectVitalsDetail> getVitalsDetails(Datasets datasets,
                                                      VitalFilters vitalsFilters,
                                                      PopulationFilters populationFilters,
                                                      DayZeroType dayZeroType,
                                                      String dayZeroOption) {
        Collection<Vital> vitals = getTimelineFilteredData(datasets, vitalsFilters, populationFilters);

        Map<Subject, Map<String, List<Vital>>> subjectTestGroups = vitals.stream()
                .collect(groupingBy(Vital::getSubject,
                        groupingBy(vital -> vital.getEvent().getVitalsMeasurement())));

        return subjectTestGroups.entrySet().stream().map(subjectTestGroup -> {
            Subject subject = subjectTestGroup.getKey();

            List<VitalsTests> tests = subjectTestGroup.getValue().entrySet().stream()
                    .map(testGroup -> {
                        List<VitalsDetailEvent> events = testGroup.getValue().stream().map(vital -> {
                            DateDayHour start = extractDaysHours(subject, dayZeroType, dayZeroOption,
                                    vital.getEventDate());
                            return toVitalsDetailEvent(vital.getEvent(), start);
                        }).sorted(Comparator.comparing(s -> s.getStart().getDate())).collect(toList());
                        DateDayHour baseline = extractDaysHours(subject, dayZeroType, dayZeroOption,
                                testGroup.getValue().get(0).getBaselineDate());
                        return toVitalsTests(testGroup.getKey(), events, baseline);
                    }).sorted(Comparator.comparing(VitalsTests::getTestName)).collect(Collectors.toList());

            return toSubjectVitalsDetail(subject, tests);

        }).sorted(Comparator.comparing(SubjectVitalsDetail::getSubjectId)).collect(Collectors.toList());
    }

    private static Double calculateMaxPercentChangeFromBaseline(List<Vital> vitals) {
        return vitals.stream()
                .map(Vital::getPercentChangeFromBaseline)
                .filter(Objects::nonNull)
                .max(Comparator.comparingDouble(Math::abs)).orElse(null);
    }

    private static VitalsTests toVitalsTests(String testName, List<VitalsDetailEvent> events,
                                             DateDayHour baseline) {
        VitalsTests out = new VitalsTests();
        out.setTestName(testName);
        out.setEvents(events);
        out.setBaseline(baseline);
        return out;
    }

    private static SubjectVitalsSummary toSubjectVitalsSummary(Subject subject, List<VitalsSummaryEvent> events,
                                                               DateDayHour baseline) {
        SubjectVitalsSummary out = new SubjectVitalsSummary();
        out.setSubject(subject.getSubjectCode());
        out.setSubjectId(subject.getSubjectId());
        out.setSex(subject.getSex());
        out.setBaseline(baseline);
        out.setEvents(events);
        return out;
    }

    private static SubjectVitalsDetail toSubjectVitalsDetail(Subject subject, List<VitalsTests> tests) {
        SubjectVitalsDetail out = new SubjectVitalsDetail();
        out.setSubject(subject.getSubjectCode());
        out.setSubjectId(subject.getSubjectId());
        out.setSex(subject.getSex());
        out.setTests(tests);
        return out;
    }

    private static VitalsSummaryEvent toVitalsSummaryEvent(VitalRaw vitalRaw, DateDayHour start,
                                                           Double maxPercentChange) {
        VitalsSummaryEvent out = new VitalsSummaryEvent();
        out.setVisitNumber(vitalRaw.getVisitNumber());
        out.setStart(start);
        out.setMaxValuePercentChange(maxPercentChange);
        return out;
    }

    private static VitalsDetailEvent toVitalsDetailEvent(VitalRaw vitalRaw, DateDayHour start) {
        VitalsDetailEvent out = new VitalsDetailEvent();
        out.setVisitNumber(vitalRaw.getVisitNumber());
        out.setStart(start);
        out.setBaselineValue(vitalRaw.getBaselineValue());
        out.setBaselineFlag(Constants.BASELINE_FLAG_YES.equals(vitalRaw.getBaselineFlag()));
        out.setValueRaw(vitalRaw.getResultValue());
        out.setUnitRaw(vitalRaw.getUnit());
        out.setValueChangeFromBaseline(vitalRaw.getChangeFromBaseline());
        out.setUnitChangeFromBaseline(vitalRaw.getUnit());
        out.setValuePercentChangeFromBaseline(vitalRaw.getPercentChangeFromBaseline());
        out.setUnitPercentChangeFromBaseline("%");
        return out;
    }

    @Override
    public List<Vital> getTimelineFilteredData(Datasets datasets, Filters<Vital> filters, PopulationFilters populationFilters) {
        FilterResult<Vital> results = vitalService.getFilteredData(datasets,
                filters == null ? VitalFilters.empty() : filters, populationFilters);

        return results.getFilteredResult().stream().filter(e -> e.getEventDate() != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Subject> getTimelineFilteredSubjects(Datasets datasets, Filters<Vital> filters, PopulationFilters populationFilters) {
        return getTimelineFilteredData(datasets, filters, populationFilters)
                .stream()
                .filter(e -> e.getStartDate() != null)
                .map(SubjectAwareWrapper::getSubject)
                .distinct()
                .collect(toList());
    }

    @Override
    public TimelineTrack getTimelineTrack() {
        return TimelineTrack.VITALS;
    }
}
