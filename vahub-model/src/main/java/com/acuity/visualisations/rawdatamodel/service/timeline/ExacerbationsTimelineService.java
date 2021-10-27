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
import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.ExacerbationService;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import com.acuity.visualisations.rawdatamodel.util.DayHourUtils;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.exacerbations.ExacerbationSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.exacerbations.SubjectExacerbationSummary;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;

@Service
public class ExacerbationsTimelineService implements BaseEventTimelineService<Exacerbation> {

    @Autowired
    private ExacerbationService exacerbationService;

    public List<SubjectExacerbationSummary> getExacerbationsSummary(Datasets datasets, DayZeroType dayZeroType, String dayZeroOption,
                                                                    PopulationFilters populationFilters,
                                                                    Filters<Exacerbation> exacerbationsFilters) {
        return getTimelineFilteredData(datasets, exacerbationsFilters, populationFilters).stream()
                .filter(e -> e.getStartDate() != null)
                .collect(Collectors.groupingBy(SubjectAwareWrapper::getSubject, Collectors.toList()))
                .entrySet().stream().map(ses -> {
                    Subject subject = ses.getKey();
                    List<Exacerbation> exacerbations = ses.getValue();
                    exacerbations.sort(Comparator.nullsLast(Comparator.comparing(Exacerbation::getStartDate)));

                    return SubjectExacerbationSummary.builder()
                            .subject(subject.getSubjectCode())
                            .subjectId(subject.getSubjectId())
                            .events(IntStream.range(0, ses.getValue().size()).mapToObj(i -> {
                                Exacerbation exacerbation = exacerbations.get(i);

                                Exacerbation nextExacerbation = (i + 1 < exacerbations.size()) ? exacerbations.get(i + 1) : null;

                                Date endDate = exacerbation.getEvent().getEndDate();
                                if (endDate == null) {
                                    endDate = subject.getStudyInfo().getLastUpdatedDate();
                                }

                                boolean ongoing = subject.getDateOfDeath() == null && exacerbation.getEvent().getEndDate() == null && nextExacerbation == null;

                                DateDayHour start = DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption, exacerbation.getStartDate());
                                DateDayHour end = DayHourUtils.extractDaysHours(subject, dayZeroType, dayZeroOption, endDate);
                                return start != null && end != null ? ExacerbationSummaryEvent.builder()
                                        .start(start)
                                        .end(end)
                                        .imputedEndDate(exacerbation.getEvent().getEndDate() == null)
                                        .numberOfDoseReceived(exacerbation.getEvent().getNumberOfDosesReceived())
                                        .severityGrade(exacerbation.getEvent().getExacerbationClassification())
                                        .ongoing(ongoing)
                                        .build() : null;
                            }).filter(Objects::nonNull).collect(Collectors.toList())).build();
                }).collect(Collectors.toList());
    }

    @Override
    public List<Exacerbation> getTimelineFilteredData(Datasets datasets, Filters<Exacerbation> filters, PopulationFilters populationFilters) {
        return new ArrayList<>(exacerbationService.getFilteredData(datasets,
                filters == null ? ExacerbationFilters.empty() : filters, populationFilters).getFilteredEvents());
    }

    @Override
    public List<Subject> getTimelineFilteredSubjects(Datasets datasets, Filters<Exacerbation> filters, PopulationFilters populationFilters) {
        return getTimelineFilteredData(datasets, filters, populationFilters)
                .stream()
                .filter(e -> e.getStartDate() != null)
                .map(SubjectAwareWrapper::getSubject)
                .distinct()
                .collect(toList());
    }

    @Override
    public TimelineTrack getTimelineTrack() {
        return TimelineTrack.EXACERBATIONS;
    }
}
