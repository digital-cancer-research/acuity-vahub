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
import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.LungFunctionService;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import com.acuity.visualisations.rawdatamodel.util.Constants;
import com.acuity.visualisations.rawdatamodel.util.DayHourUtils;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.LungFunctionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.timeline.SubjectSummary;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.LungFunctionCodes;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.LungFunctionDetailsEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.LungFunctionSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.SubjectLungFunctionDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.SubjectLungFunctionSummary;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.BaselineUtil.chooseSummaryBaselineDate;
import static com.acuity.visualisations.rawdatamodel.util.DayHourUtils.extractDaysHours;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class LungFunctionTimelineService implements BaseEventTimelineService<LungFunction> {

    private final LungFunctionService lungFunctionService;

    public List<SubjectLungFunctionSummary> getLungFunctionSummaries(Datasets datasets,
                                                                     LungFunctionFilters lungFunctionFilters,
                                                                     PopulationFilters populationFilters,
                                                                     DayZeroType dayZeroType,
                                                                     String dayZeroOption) {
        Collection<LungFunction> lungFunctions = getTimelineFilteredData(datasets, lungFunctionFilters, populationFilters);
        Map<Subject, Map<Date, List<LungFunction>>> subjectDateGroups = lungFunctions.stream()
                .collect(Collectors.groupingBy(LungFunction::getSubject,
                        Collectors.groupingBy(LungFunction::getMeasurementTimePoint)));

        return subjectDateGroups.entrySet().stream()
                .map(subjectGroup -> composeSummary(subjectGroup, dayZeroType, dayZeroOption))
                .sorted(Comparator.comparing(SubjectSummary::getSubjectId))
                .collect(Collectors.toList());
    }

    private SubjectLungFunctionSummary composeSummary(Map.Entry<Subject, Map<Date, List<LungFunction>>> subjectGroup,
                                                      DayZeroType dayZeroType,
                                                      String dayZeroOption) {
        Subject subject = subjectGroup.getKey();
        List<LungFunctionSummaryEvent> events = subjectGroup.getValue().entrySet().stream()
                .flatMap(dateGroup -> {
                    DateDayHour start = DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption, dateGroup.getKey());
                    Double maxPercentChangeFromBaseline = calcMaxPercentChangeFromBaseline(dateGroup.getValue());

                    return dateGroup.getValue().stream()
                            .collect(Collectors.groupingBy(l -> Optional.ofNullable(l.getVisitNumber())))
                            .values().stream()
                            .map(functions -> functions.get(0))
                            .map(sampleLungFunction -> toLungFunctionSummaryEvent(sampleLungFunction.getEvent(), start, maxPercentChangeFromBaseline));
                })
                .filter(l -> l.getStart() != null && l.getStart().getDayHour() != null)
                .sorted(Comparator.comparing(LungFunctionSummaryEvent::getVisitNumber,
                        Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(date -> date.getStart().getDayHour())).collect(Collectors.toList());

        Date eventsBaselineDate = chooseSummaryBaselineDate(
                subjectGroup.getValue().values().stream().flatMap(Collection::stream).collect(Collectors.toList()),
                subject);

        DateDayHour baseline = DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption, eventsBaselineDate);

        return toSubjectLungFunctionSummary(subject, events, baseline);
    }

    public List<SubjectLungFunctionDetail> getLungFunctionDetails(Datasets datasets,
                                                                  LungFunctionFilters lungFunctionFilters,
                                                                  PopulationFilters populationFilters,
                                                                  DayZeroType dayZeroType,
                                                                  String dayZeroOption) {
        Collection<LungFunction> lungFunctions = getTimelineFilteredData(datasets, lungFunctionFilters, populationFilters);

        Map<Subject, Map<String, List<LungFunction>>> subjectCodeGroup = lungFunctions.stream()
                .collect(groupingBy(LungFunction::getSubject, groupingBy(LungFunction::getCode)));

        return subjectCodeGroup.entrySet().stream().map(subjectGroup -> {
            Subject subject = subjectGroup.getKey();

            List<LungFunctionCodes> codes = subjectGroup.getValue().entrySet().stream()
                    .map(codeGroup -> {
                        List<LungFunctionDetailsEvent> events = codeGroup.getValue().stream().map(lungFunction -> {
                            DateDayHour start = extractDaysHours(subject, dayZeroType, dayZeroOption,
                                    lungFunction.getMeasurementTimePoint());
                            return toLungFunctionDetailsEvent(lungFunction.getEvent(), start);
                        }).sorted(Comparator.comparing(LungFunctionDetailsEvent::getVisitNumber,
                                Comparator.nullsLast(Comparator.naturalOrder()))).collect(toList());
                        Date eventsBaseline = codeGroup.getValue().get(0).getBaselineDate();
                        DateDayHour baseline = DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption, eventsBaseline);
                        return toLungFunctionCodes(codeGroup.getKey(), events, baseline);
                    }).sorted(Comparator.comparing(LungFunctionCodes::getCode)).collect(Collectors.toList());

            return toSubjectLungFunctionDetail(subject, codes);

        }).sorted(Comparator.comparing(SubjectLungFunctionDetail::getSubjectId)).collect(Collectors.toList());

    }

    private LungFunctionDetailsEvent toLungFunctionDetailsEvent(LungFunctionRaw lungFunctionRaw,
                                                                DateDayHour start) {
        return LungFunctionDetailsEvent.builder()
                .start(start)
                .visitNumber(lungFunctionRaw.getVisit())
                .valueRaw(lungFunctionRaw.getResultValue())
                .unitRaw(lungFunctionRaw.getUnit())
                .baselineValue(lungFunctionRaw.getBaselineValue())
                .baselineFlag(Constants.BASELINE_FLAG_YES.equals(lungFunctionRaw.getBaselineFlag()))
                .valueChangeFromBaseline(lungFunctionRaw.getChangeFromBaseline())
                .unitChangeFromBaseline(lungFunctionRaw.getUnit())
                .valuePercentChangeFromBaseline(lungFunctionRaw.getPercentChangeFromBaseline())
                .unitPercentChangeFromBaseline("%")
                .build();
    }

    private LungFunctionSummaryEvent toLungFunctionSummaryEvent(LungFunctionRaw lungFunctionRaw,
                                                                DateDayHour start,
                                                                Double maxPercentChangeFromBaseline) {
        return LungFunctionSummaryEvent.builder()
                .visitNumber(lungFunctionRaw.getVisit())
                .start(start)
                .maxValuePercentChange(maxPercentChangeFromBaseline)
                .build();
    }

    private SubjectLungFunctionSummary toSubjectLungFunctionSummary(Subject subject,
                                                                    List<LungFunctionSummaryEvent> lungFunctions,
                                                                    DateDayHour baseline) {
        return SubjectLungFunctionSummary.builder()
                .subject(subject.getSubjectCode())
                .subjectId(subject.getSubjectId())
                .sex(subject.getSex())
                .events(lungFunctions)
                .baseline(baseline).build();
    }

    private LungFunctionCodes toLungFunctionCodes(String code,
                                                  List<LungFunctionDetailsEvent> lungFunctionDetailsEvents,
                                                  DateDayHour baseline) {
        return LungFunctionCodes.builder()
                .code(code)
                .events(lungFunctionDetailsEvents)
                .baseline(baseline)
                .build();
    }

    private SubjectLungFunctionDetail toSubjectLungFunctionDetail(Subject subject,
                                                                  List<LungFunctionCodes> codes) {
        return SubjectLungFunctionDetail.builder()
                .subject(subject.getSubjectCode())
                .subjectId(subject.getSubjectId())
                .sex(subject.getSex())
                .codes(codes).build();
    }

    private static Double calcMaxPercentChangeFromBaseline(List<LungFunction> lungFunctions) {
        return lungFunctions.stream()
                .map(LungFunction::getPercentChangeFromBaseline)
                .filter(Objects::nonNull)
                .max(Comparator.comparingDouble(Math::abs)).orElse(null);
    }

    @Override
    public List<LungFunction> getTimelineFilteredData(Datasets datasets, Filters<LungFunction> filters, PopulationFilters populationFilters) {
        FilterResult<LungFunction> lungFunctions = lungFunctionService.getFilteredData(datasets,
                filters == null ? LungFunctionFilters.empty() : filters, populationFilters);
        return lungFunctions.getFilteredResult().stream()
                .filter(e -> e.getEvent().getMeasurementTimePoint() != null && e.getEvent().getResultValue() != null)
                .collect(Collectors.toList());
    }

    @Override
    public List<Subject> getTimelineFilteredSubjects(Datasets datasets, Filters<LungFunction> filters, PopulationFilters populationFilters) {
        return getTimelineFilteredData(datasets, filters, populationFilters)
                .stream()
                .map(SubjectAwareWrapper::getSubject)
                .distinct()
                .collect(toList());
    }

    @Override
    public TimelineTrack getTimelineTrack() {
        return TimelineTrack.SPIROMETRY;
    }
}
