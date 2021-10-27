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
import com.acuity.visualisations.rawdatamodel.axes.TAxes;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.va.security.acl.domain.Datasets;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.acuity.visualisations.common.vo.DayZeroType.DAYS_SINCE_FIRST_TREATMENT;
import static com.acuity.visualisations.common.vo.DayZeroType.DAYS_SINCE_RANDOMISATION;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class TimelineService {

    private final PopulationService populationService;
    private final StatusSummaryTimelineService statusSummaryTimelineService;
    private final List<BaseEventTimelineService> timelineServices;

    public List<TAxes<DayZeroType>> getAvailableOptions(Datasets datasets) {

        final FilterResult<Subject> filteredData = populationService.getFilteredData(datasets, PopulationFilters.empty());
        final List<String> drugs = filteredData.stream()
                .flatMap(e -> e.getDrugFirstDoseDate().entrySet().stream()
                        .filter(d -> d.getValue() != null)
                        .map(Map.Entry::getKey)).distinct().sorted().collect(Collectors.toList());
        final boolean hasRand = filteredData.stream().anyMatch(e -> e.getDateOfRandomisation() != null);

        List<TAxes<DayZeroType>> availableOptions = new ArrayList<>();
        availableOptions.add(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        if (hasRand) {
            availableOptions.add(new TAxes<>(DayZeroType.DAYS_SINCE_RANDOMISATION));
        }
        if (datasets.isAcuityType() && drugs.size() > 1) {
            drugs.forEach(e -> availableOptions.add(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_TREATMENT, null, e)));
        }

        return availableOptions;
    }

    public List<String> getAvailableTracks(Datasets datasets) {
        //noinspection unchecked
        List<String> tracks = timelineServices.stream()
                .filter(e -> !e.getTimelineFilteredData(datasets, null, PopulationFilters.empty()).isEmpty())
                .map(e -> e.getTimelineTrack().name()).collect(Collectors.toList());

        if (!statusSummaryTimelineService.getTimelineFilteredData(datasets, PopulationFilters.empty()).isEmpty()) {
            tracks.add(TimelineTrack.STATUS_SUMMARY.name());
        }

        return tracks;
    }

    public List<String> getSubjectsSortedByStudyDuration(Datasets datasets,
                                                         PopulationFilters populationFilters,
                                                         List<TimelineTrack> visibleTracks,
                                                         DayZeroType dayZeroType,
                                                         String dayZeroOption,
                                                         Filters<?>... eventFilters) {
        Set<Subject> filteredSubjects = timelineServices.stream()
                .filter(service -> visibleTracks.contains(service.getTimelineTrack()))
                .map(service -> getFilteredSubjects(service, datasets, populationFilters, eventFilters))
                .flatMap(Collection::stream)
                .map(e -> (HasSubject) e)
                .map(HasSubject::getSubject)
                .collect(Collectors.toSet());

        if (visibleTracks.contains(TimelineTrack.STATUS_SUMMARY)) {
            filteredSubjects.addAll(statusSummaryTimelineService.getTimelineFilteredSubjects(datasets, populationFilters)
                    .stream()
                    .map(HasSubject::getSubject)
                    .collect(Collectors.toSet()));
        }

        List<Subject> subjectsWithDayZero = filteredSubjects.stream()
                .filter(s -> !(dayZeroType.equals(DAYS_SINCE_RANDOMISATION) && s.getDateOfRandomisation() == null
                        || (dayZeroType.equals(DAYS_SINCE_FIRST_TREATMENT) && s.getDateOfFirstDoseOfDrug(dayZeroOption) == null)))
                .collect(toList());

        return subjectsWithDayZero.stream()
                .sorted(Comparator.comparing(Subject::getDurationOnStudy,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .map(Subject::getSubjectCode)
                .collect(Collectors.toList());
    }

    private List<Subject> getFilteredSubjects(
            BaseEventTimelineService service,
            Datasets datasets,
            PopulationFilters populationFilters,
            Filters<?>[] eventFilters) {
        ResolvableType serviceType = ResolvableType.forClass(service.getClass())
                .as(BaseEventTimelineService.class);
        Class<?> serviceEventClass = serviceType.getGeneric(0).resolve();

        // We can't generify this call so as we match generic types
        // of service and filters in runtime using reflection.
        //noinspection unchecked
        return service.getTimelineFilteredSubjects(
                datasets,
                getFiltersWithSameTypeAsService(eventFilters, serviceEventClass),
                populationFilters);
    }

    private Filters getFiltersWithSameTypeAsService(Filters<?>[] eventFilters, Class<?> serviceEventClass) {
        return Stream.of(eventFilters)
                .filter(e -> {
                    ResolvableType filterType = ResolvableType.forClass(e.getClass());
                    Class<?> filterEventClass = filterType
                            .as(Filters.class).getGeneric(0).resolve();
                    return filterEventClass.isAssignableFrom(serviceEventClass);
                })
                .findFirst()
                .orElse(null);
    }
}
