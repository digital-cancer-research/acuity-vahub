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
import com.acuity.visualisations.rawdatamodel.filters.CardiacFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.CardiacService;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import com.acuity.visualisations.rawdatamodel.vo.CardiacRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.ecg.EcgDetailEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.ecg.EcgSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.ecg.EcgTest;
import com.acuity.visualisations.rawdatamodel.vo.timeline.ecg.SubjectEcgDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.ecg.SubjectEcgSummary;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.BaselineUtil.chooseSummaryBaselineDate;
import static com.acuity.visualisations.rawdatamodel.util.Constants.ROUNDING_PRECISION;
import static com.acuity.visualisations.rawdatamodel.util.DayHourUtils.extractDaysHours;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class EcgTimelineService implements BaseEventTimelineService<Cardiac> {

    private static final String LVEF = "LVEF";
    private static final String MS = "ms";
    private static final String MALE = "Male";
    private static final String YES = "Yes";
    private static final String NO = "No";
    private static final String ABNORMAL = "ABNORMAL";
    private static final List<String> QTCF_MEASUREMENT_NAME_ACUITY = Arrays.asList("QTCF", "QTCF - FRIDERICIA'S CORRECTION FORMULA");
    private static final String BASELINE_FLAG = "Y";
    private static final String PERCENT = "%";

    private final CardiacService cardiacService;


    public List<SubjectEcgSummary> getSummaries(Datasets datasets, CardiacFilters cardiacFilters,
                                                PopulationFilters populationFilters, DayZeroType value, String stringarg) {
        Map<Subject, List<Cardiac>> cardiacs = groupEventsBySubject(datasets, cardiacFilters, populationFilters);
        Set<QtcfValue> qtcfSet = getQtcfSet(datasets);
        return cardiacs.entrySet().stream()
                .map(cardiac -> buildSubjectEcgSummary(cardiac, value, stringarg, qtcfSet))
                .collect(Collectors.toList());
    }

    public List<SubjectEcgDetail> getDetails(Datasets datasets, CardiacFilters cardiacFilters,
                                             PopulationFilters populationFilters, DayZeroType value, String stringarg) {
        Map<Subject, List<Cardiac>> cardiacs = groupEventsBySubject(datasets, cardiacFilters, populationFilters);
        return cardiacs.entrySet().stream()
                .map(cardiac -> buildSubjectEcgDetail(cardiac.getKey(), cardiac.getValue(), value, stringarg))
                .collect(Collectors.toList());
    }

    private Set<QtcfValue> getQtcfSet(Datasets datasets) {
        return cardiacService.getFilteredData(datasets, CardiacFilters.empty(), PopulationFilters.empty()).stream()
                .filter(c -> c.getEvent().getMeasurementName() != null
                        && QTCF_MEASUREMENT_NAME_ACUITY.contains(c.getEvent().getMeasurementName().toUpperCase()))
                .map(c -> {
                    Double changeFromBaseline = c.getChangeFromBaseline();
                    changeFromBaseline = changeFromBaseline == null ? null
                            : Precision.round(changeFromBaseline, ROUNDING_PRECISION);

                    return QtcfValue.builder()
                            .key(QtcfKey.builder()
                                    .subjectId(c.getSubjectId())
                                    .ecgDate(c.getEventDate())
                                    .visitNumber(c.getEvent().getVisitNumber())
                                    .build())
                            .resultValue(c.getResultValue())
                            .testName(c.getEvent().getMeasurementName())
                            .changeFromBaseline(changeFromBaseline)
                            .abnormality(c.getEvent().getEcgEvaluation() == null ? null
                                    : ABNORMAL.equalsIgnoreCase(c.getEvent().getEcgEvaluation()) ? YES : NO)
                            .significance(c.getEvent().getClinicallySignificant())
                            .build();
                }).collect(Collectors.toSet());
    }

    private SubjectEcgDetail buildSubjectEcgDetail(Subject subject, List<Cardiac> cardiacs, DayZeroType dayZeroType,
                                                   String option) {
        List<EcgTest> tests = cardiacs.stream()
                .filter(c -> c.getEvent().getMeasurementName() != null
                        || c.getEvent().getShortMeasurementName() != null).collect(
                        groupingBy(c -> c.getEvent().getMeasurementName()))
                .entrySet().stream()
                .map(e -> buildEcgTest(e.getValue(), e.getKey(), dayZeroType, option, subject))
                .collect(Collectors.toList());
        return SubjectEcgDetail.builder()
                .subject(subject.getSubjectCode())
                .subjectId(subject.getSubjectId())
                .sex(subject.getSex())
                .tests(tests)
                .build();
    }

    private EcgTest buildEcgTest(List<Cardiac> cardiacs, String testName, DayZeroType dayZeroType, String option, Subject subject) {
        List<EcgDetailEvent> events = cardiacs.stream()
                .map(c -> buildEcgDetailEvent(c, dayZeroType, option))
                .collect(Collectors.toList());
        return EcgTest.builder()
                .testName(testName)
                .baseline(extractDaysHours(subject, dayZeroType, option, cardiacs.get(0).getBaselineDate()))
                .events(events)
                .build();
    }

    private EcgDetailEvent buildEcgDetailEvent(Cardiac c, DayZeroType dayZeroType, String option) {
        CardiacRaw cardiacRaw = c.getEvent();
        Subject subject = c.getSubject();

        String significant = YES.equalsIgnoreCase(c.getEvent().getClinicallySignificant()) ? YES : NO;
        Double baselineValue = cardiacRaw.getBaselineValue() == null ? null
                : Precision.round(cardiacRaw.getBaselineValue(), ROUNDING_PRECISION);

        Double changeFromBaseline = c.getChangeFromBaseline();
        changeFromBaseline = changeFromBaseline == null ? null
                : Precision.round(changeFromBaseline, ROUNDING_PRECISION);

        Double percentChangeFromBaseline = c.getPercentChangeFromBaseline();
        percentChangeFromBaseline = percentChangeFromBaseline == null ? null
                : Precision.round(percentChangeFromBaseline, ROUNDING_PRECISION);

        Double resultValue = cardiacRaw.getResultValue() == null ? null
                : Precision.round(cardiacRaw.getResultValue(), ROUNDING_PRECISION);

        return EcgDetailEvent.builder()
                .abnormality(c.getEvent().getEcgEvaluation() == null ? null
                        : ABNORMAL.equalsIgnoreCase(c.getEvent().getEcgEvaluation()) ? YES : NO)
                .significant(c.getEvent().getClinicallySignificant() == null ? null : significant)
                .baselineFlag(BASELINE_FLAG.equals(cardiacRaw.getBaselineFlag()))
                .baselineValue(baselineValue)
                .start(extractDaysHours(subject, dayZeroType, option, cardiacRaw.getEventDate()))
                .unitChangeFromBaseline(MS)
                .unitPercentChangeFromBaseline(PERCENT)
                .unitRaw(MS)
                .valueChangeFromBaseline(changeFromBaseline)
                .valuePercentChangeFromBaseline(percentChangeFromBaseline)
                .valueRaw(resultValue)
                .visitNumber(cardiacRaw.getVisitNumber())
                .build();
    }

    private Map<Subject, List<Cardiac>> groupEventsBySubject(Datasets datasets, CardiacFilters cardiacFilters,
                                                             PopulationFilters populationFilters) {

        return getTimelineFilteredData(datasets, cardiacFilters, populationFilters).stream()
                .collect(groupingBy(Cardiac::getSubject));
    }

    private SubjectEcgSummary buildSubjectEcgSummary(Map.Entry<Subject, List<Cardiac>> cardiac,
                                                     DayZeroType dayZeroType, String option,
                                                     Set<QtcfValue> qtcfValues) {
        Subject subject = cardiac.getKey();
        List<Cardiac> cardiacs = cardiac.getValue();

        Map<Date, List<Double>> testDateChangeFromBaseline = cardiacs.stream()
                .filter(c -> c.getPercentChangeFromBaseline() != null)
                .collect(Collectors.groupingBy(Cardiac::getEventDate, Collectors.mapping(
                        Cardiac::getPercentChangeFromBaseline, Collectors.toList())));

        Map<Date, Double> maxValuePercentChange = new HashMap<>();
        testDateChangeFromBaseline.forEach((key, value) -> {
            if (!value.isEmpty()) {
                maxValuePercentChange.put(key, value.stream().max(Comparator.comparingDouble(Math::abs)).orElse(null));
            }
        });

        List<EcgSummaryEvent> summaryEvents = cardiac.getValue().stream()
                .map(c -> buildEcgSummaryEvent(c, dayZeroType, option,
                        maxValuePercentChange.get(c.getEventDate()),
                        buildQtcfValue(qtcfValues, cardiacs, c)))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        Date eventsBaselineDate = chooseSummaryBaselineDate(cardiacs, subject);

        return SubjectEcgSummary.builder()
                .subjectId(subject.getSubjectId())
                .subject(subject.getSubjectCode())
                .events(summaryEvents)
                .sex(subject.getSex() == null ? MALE : subject.getSex())
                .baseline(extractDaysHours(subject, dayZeroType, option, eventsBaselineDate))
                .build();
    }

    private QtcfValue buildQtcfValue(Set<QtcfValue> qtcfValues, List<Cardiac> subjectCardiacs, Cardiac c) {
        Map<Date, List<Cardiac>> testDateCardiacs = subjectCardiacs.stream().collect(groupingBy(Cardiac::getEventDate));
        QtcfKey cardiacKey = QtcfKey.builder()
                .subjectId(c.getSubjectId())
                .ecgDate(c.getEventDate())
                .visitNumber(c.getEvent().getVisitNumber())
                .build();

        return qtcfValues.stream()
                .filter(q -> q.getKey().equals(cardiacKey))
                .findAny()
                .map(QtcfValue::toBuilder)
                .map(builder -> fillAbnormality(c, testDateCardiacs, builder))
                .map(builder -> fillSignificance(c, testDateCardiacs, builder))
                .map(QtcfValue.QtcfValueBuilder::build)
                .orElse(null);
    }

    private QtcfValue.QtcfValueBuilder fillAbnormality(Cardiac cardiac, Map<Date, List<Cardiac>> testDateCardiacs, QtcfValue.QtcfValueBuilder builder) {
        if (cardiac.getEvent().getEcgEvaluation() != null) {
            builder.abnormality(
                    testDateCardiacs.get(cardiac.getEventDate()).stream()
                            .anyMatch(c1 -> ABNORMAL.equalsIgnoreCase(c1.getEvent().getEcgEvaluation()))
                            ? YES : NO);
        }
        return builder;
    }

    private QtcfValue.QtcfValueBuilder fillSignificance(Cardiac c, Map<Date, List<Cardiac>> testDateCardiacs, QtcfValue.QtcfValueBuilder builder) {
        if (c.getEvent().getClinicallySignificant() != null) {
            builder.significance(
                    testDateCardiacs.get(c.getEventDate()).stream()
                            .anyMatch(c1 -> YES.equalsIgnoreCase(c1.getEvent().getClinicallySignificant()))
                            ? YES : NO);
        }
        return builder;
    }

    private EcgSummaryEvent buildEcgSummaryEvent(Cardiac c,
                                                 DayZeroType type,
                                                 String option,
                                                 Double maxValuePercentChange,
                                                 QtcfValue qtcfValue) {
        CardiacRaw cardiacRaw = c.getEvent();
        DateDayHour start = extractDaysHours(c.getSubject(), type, option, c.getEventDate());
        maxValuePercentChange = maxValuePercentChange == null ? null
                : Precision.round(maxValuePercentChange, ROUNDING_PRECISION);

        EcgSummaryEvent.EcgSummaryEventBuilder builder = EcgSummaryEvent.builder()
                .maxValuePercentChange(maxValuePercentChange)
                .qtcfUnit(MS)
                .start(start)
                .visitNumber(cardiacRaw.getVisitNumber());
        if (qtcfValue != null) {
            builder.qtcfValue(qtcfValue.getResultValue())
                    .qtcfChange(qtcfValue.getChangeFromBaseline())
                    .abnormality(qtcfValue.getAbnormality())
                    .significant(qtcfValue.getSignificance());
        }
        return builder.build();
    }

    @Override
    public List<Cardiac> getTimelineFilteredData(Datasets datasets, Filters<Cardiac> filters, PopulationFilters populationFilters) {
        return cardiacService.getFilteredData(datasets, filters == null ? CardiacFilters.empty() : filters, populationFilters)
                .getFilteredEvents().stream()
                .filter(
                        cardiac -> (!LVEF.equalsIgnoreCase(cardiac.getEvent().getMeasurementName())
                                && cardiac.getEventDate() != null))
                .collect(Collectors.toList());
    }

    @Override
    public List<Subject> getTimelineFilteredSubjects(Datasets datasets, Filters<Cardiac> filters, PopulationFilters populationFilters) {
        return getTimelineFilteredData(datasets, filters, populationFilters).stream()
                .map(SubjectAwareWrapper::getSubject)
                .distinct()
                .collect(toList());
    }

    @Override
    public TimelineTrack getTimelineTrack() {
        return TimelineTrack.ECG;
    }

    @Getter
    @Builder(toBuilder = true)
    @EqualsAndHashCode(of = {"key", "testName"})
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QtcfValue {
        private QtcfKey key;
        private Double resultValue;
        private Double changeFromBaseline;
        private String abnormality;
        private String significance;
        private String testName;
    }

    @Getter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QtcfKey {
        private String subjectId;
        private Date ecgDate;
        private Double visitNumber;
    }
}
