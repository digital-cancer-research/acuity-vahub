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
import com.acuity.visualisations.rawdatamodel.dataproviders.LabDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.service.StudyInfoService;
import com.acuity.visualisations.rawdatamodel.util.DayHourUtils;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.timeline.statussummary.SubjectStatusSummary;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class StatusSummaryTimelineService {
    private static final String ONGOING = "ongoing";

    private final PopulationService populationService;
    private final LabDatasetsDataProvider labDatasetsDataProvider;
    private final StudyInfoService studyInfoService;

    public List<SubjectStatusSummary> getStatusSummaries(Datasets datasets, PopulationFilters populationFilters,
                                                         DayZeroType dayZeroType, String dayZeroOption) {
        Date lastStudyDate = studyInfoService.getStudyInfo(datasets)
                .map(StudyInfo::getLastUpdatedDate)
                .orElse(null);

        Collection<Lab> filteredLabData = labDatasetsDataProvider.loadData(datasets);
        Map<Subject, List<Date>> groupedLabMeasurementDates = filteredLabData.stream()
                .filter(lab -> lab.getMeasurementTimePoint() != null)
                .collect(Collectors.groupingBy(SubjectAwareWrapper::getSubject,
                        Collectors.mapping(Lab::getMeasurementTimePoint, toSortedList(Date::compareTo))));

        List<SubjectStatusSummary> statusSummaries = getTimelineFilteredData(datasets, populationFilters).stream()
                .map(subject -> {
                            Date endDate = getMinDate(subject.getDateOfWithdrawal(), subject.getDateOfDeath(), lastStudyDate);
                            List<Date> labDates = groupedLabMeasurementDates.get(subject);
                            return SubjectStatusSummary.builder()
                                    .subjectId(subject.getSubjectId())
                                    .subject(subject.getSubjectCode())
                                    .firstVisit(DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption,
                                            labDates == null ? null : labDates.get(0)))
                                    .lastVisit(DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption,
                                            labDates == null ? null : labDates.get(labDates.size() - 1)))
                                    .completion(DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption, subject.getDateOfWithdrawal()))

                                    .firstTreatment(DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption, subject.getFirstTreatmentDate()))
                                    .randomisation(DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption, subject.getDateOfRandomisation()))
                                    .lastTreatment(DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption, subject.getLastTreatmentDate()))
                                    .death(DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption, subject.getDateOfDeath()))
                                    .ongoing(isOngoing(subject)
                                            ? DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption, lastStudyDate)
                                            : null)
                                    .cutoff(DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption, lastStudyDate))
                                    .endDate(DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption, endDate))
                                    .drugs(new ArrayList<>(subject.getDrugsDosed().keySet()))
                                    .phases(new ArrayList<>())
                                    .build();
                        }
                ).collect(toList());
        statusSummaries.parallelStream().forEach(SubjectStatusSummary::calculatePhases);
        return statusSummaries;
    }

    private static <T> Collector<T, ?, List<T>> toSortedList(Comparator<? super T> c) {
        return Collectors.collectingAndThen(
                Collectors.toCollection(ArrayList::new), e -> {
                    e.sort(c);
                    return e;
                }
        );
    }

    private static boolean isOngoing(Subject subject) {
        return ONGOING.equalsIgnoreCase(subject.getStudyStatus())
                && subject.getDateOfWithdrawal() == null && subject.getDateOfDeath() == null;
    }

    private static Date getMinDate(Date... dates) {
        return Stream.of(dates)
                .filter(Objects::nonNull)
                .min(Date::compareTo).orElse(null);
    }

    public List<Subject> getTimelineFilteredSubjects(Datasets datasets, PopulationFilters populationFilters) {
        return getTimelineFilteredData(datasets, populationFilters);
    }

    public List<Subject> getTimelineFilteredData(Datasets datasets, PopulationFilters populationFilters) {
        FilterResult<Subject> results = populationService.getFilteredData(datasets, populationFilters);

        return results.getFilteredResult().stream()
                .filter(e -> e.getFirstTreatmentDate() != null)
                .collect(toList());
    }
}
