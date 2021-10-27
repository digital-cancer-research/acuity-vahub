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

package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.dataproviders.AssessedTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.AssessedTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ATLGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Base event service for AssessedTargetLesion entity
 */
@Service
@Primary
public class AssessedTargetLesionService extends BasePlotEventService<AssessedTargetLesionRaw, AssessedTargetLesion, ATLGroupByOptions> {
    /**
     * Get list of assessment week numbers present within particular datasets with population and event filters applied.
     * The calculation is based on assessment frequency and lesion dates.
     * @param datasets - Datasets object
     * @param filters - event filters
     * @param populationFilters - population filters
     * @param includeBaseLine - include baseline event
     * @return list of week numbers
     */
    List<Integer> getAssessmentWeeks(Datasets datasets, Filters<AssessedTargetLesion> filters, PopulationFilters populationFilters, boolean includeBaseLine) {
        FilterResult<AssessedTargetLesion> filteredData = getFilteredData(datasets, filters, populationFilters, null,
                AssessedTargetLesion::isNotBeforeBaseline);
        return getAssessmentWeeks(filteredData.getFilteredResult(), includeBaseLine);
    }

    /**
     * Get list of assessment week numbers present within particular set of data.
     * The calculation is based on assessment frequency and lesion dates.
     * @param assessedTargetLesions - list of assessed target lesions
     * @param includeBaseLine - include baseline event
     * @return list of week numbers
     */
    List<Integer> getAssessmentWeeks(Collection<AssessedTargetLesion> assessedTargetLesions, boolean includeBaseLine) {
        ATLGroupByOptions groupByOptions = includeBaseLine
                ? ATLGroupByOptions.ASSESSMENT_WEEK_WITH_INTEGER_BASELINE
                : ATLGroupByOptions.ASSESSMENT_WEEK;

        return assessedTargetLesions.stream()
                .map(e -> Attributes.get(groupByOptions.getAttribute(), e))
                .filter(Objects::nonNull)
                .map(o -> Integer.parseInt(o.toString()))
                .distinct().sorted().collect(toList());
    }

    /**
     * Filter input list of assessed target lesions so that it contained only one lesion per subject per assessment week.
     * Lesions to keep are calculated as lesions with dates closest to the "ideal assessment day", which is calculated
     * as week number * assessment frequency.
     * @param lesions - list of assessed target lesions to process
     * @param weeks - assessment weeks
     * @param atlGroupingFunction - function to group assessed target lesions: per subject or per combination of subject and lesion
     * @param includeBaseLine - include baseline event
     * @return list of filtered assessed target lesions
     */
    List<AssessedTargetLesion> getAtlsClosestToIdealAssessmentDaysAsList(Collection<AssessedTargetLesion> lesions,
                                                                         Collection<Integer> weeks,
                                                                         Function<Collection<AssessedTargetLesion>,
                                                                                 Map<String, List<AssessedTargetLesion>>> atlGroupingFunction,
                                                                         boolean includeBaseLine) {
        Map<String, Map<Integer, AssessedTargetLesion>> atlBySubjectByWeek = getAtlsClosestToIdealAssessmentDays(lesions,
                weeks, atlGroupingFunction, includeBaseLine);
        return atlBySubjectByWeek.values().stream().flatMap(atlByWeek -> atlByWeek.values().stream()).collect(toList());
    }

    /**
     * Filter input list of assessed target lesions so that it contained only one lesion per subject per assessment week.
     * Lesions to keep are calculated as lesions with dates closest to the "ideal assessment day", which is calculated
     * as week number * assessment frequency.
     * @param lesions - list of assessed target lesions to process
     * @param weeks - assessment weeks
     * @param atlGroupingFunction - function to group assessed target lesions: per subject or per combination of subject and lesion
     * @param includeBaseLine - include baseline event
     * @return map that has subject ids as keys and map of week to assessed target lesion closest to the ideal assessment day
     * as values
     */
    Map<String, Map<Integer, AssessedTargetLesion>> getAtlsClosestToIdealAssessmentDays(Collection<AssessedTargetLesion> lesions,
                                                                                        Collection<Integer> weeks,
                                                                                        Function<Collection<AssessedTargetLesion>,
                                                                                                Map<String, List<AssessedTargetLesion>>> atlGroupingFunction,
                                                                                        boolean includeBaseLine) {
        Map<String, List<AssessedTargetLesion>> tumoursBySubject = atlGroupingFunction.apply(lesions);
        return tumoursBySubject.entrySet().stream()
                .collect(toMap(Map.Entry::getKey,
                        subjectTumours -> {
                            TreeMap<Date, AssessedTargetLesion> groupedByLesionDate = subjectTumours
                                    .getValue().stream()
                                    .collect(toMap((AssessedTargetLesion t) -> t.getEvent().getLesionDate(), t -> t, (t1, t2) -> t1, TreeMap::new));

                            Map<Integer, AssessedTargetLesion> atlByWeek = new HashMap<>();
                            weeks.forEach(weekNumber -> {
                                AssessedTargetLesion atl = getTumourClosestToIdealAssessmentDateByWeekNumber(groupedByLesionDate, weekNumber, includeBaseLine);
                                if (atl != null) {
                                    atlByWeek.put(weekNumber, atl);
                                }
                            });

                            return atlByWeek;
                        }));
    }

    /**
     * Get subject's assessed target lesion with lesion date closest to the "ideal assessment day" for particular week,
     * which is calculated as week number * assessment frequency
     * @param groupedByLesionDate - map of subject's assessed target lesions grouped by lesion date
     * @param weekNumber - assessment week number
     * @param includeBaseLine - include baseline event
     * @return assessed target lesion with lesion date closest to the "ideal assessment day" for particular week
     */
    private AssessedTargetLesion getTumourClosestToIdealAssessmentDateByWeekNumber(TreeMap<Date, AssessedTargetLesion> groupedByLesionDate,
                                                                           Integer weekNumber, boolean includeBaseLine) {
        ATLGroupByOptions groupByOptions = includeBaseLine
                ? ATLGroupByOptions.ASSESSMENT_WEEK_WITH_INTEGER_BASELINE
                : ATLGroupByOptions.ASSESSMENT_WEEK;

        TreeMap<Date, AssessedTargetLesion> groupedByLesionDateByWeek = groupedByLesionDate.entrySet()
                .stream().filter(entry -> groupByOptions.getAttribute()
                        .getFunction().apply(entry.getValue()).equals(weekNumber))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (t1, t2) -> t1, TreeMap::new));
        return getTumourByWeekNumberFromAtlsFilteredByWeek(groupedByLesionDateByWeek, weekNumber);
    }

    /**
     * Get subject's assessed target lesion with lesion date closest to the "ideal assessment day" for particular week,
     * which is calculated as week number * assessment frequency.
     * A lesion with minimal number of days between its lesion date and ideal assessment day is picked.
     * If there are two lesions with minimal number of days between their lesion dates and ideal assessment day,
     * the latest is picked.
     * @param groupedByLesionDate - subject's lesion that correspond to particular assessment week, grouped by lesion date
     * @param weekNumber - assessment week number
     * @return assessed target lesion with lesion date closest to the "ideal assessment day" for particular week
     */
    private AssessedTargetLesion getTumourByWeekNumberFromAtlsFilteredByWeek(TreeMap<Date, AssessedTargetLesion> groupedByLesionDate,
                                                                             Integer weekNumber) {
        if (groupedByLesionDate.isEmpty()) {
            return null;
        }
        Date idealAssessmentDay = DaysUtil.addDays(groupedByLesionDate.values()
                .stream().findAny().get().getSubject().getBaselineDate(), weekNumber * 7L);

        Map.Entry<Date, AssessedTargetLesion> ceiling = groupedByLesionDate.ceilingEntry(idealAssessmentDay);
        Map.Entry<Date, AssessedTargetLesion> floor = groupedByLesionDate.floorEntry(idealAssessmentDay);

        if (ceiling == null && floor != null) {
            return floor.getValue();
        }
        if (ceiling != null && floor == null) {
            return ceiling.getValue();
        }

        int daysToCeiling = DaysUtil.daysBetween(idealAssessmentDay, ceiling.getKey()).getAsInt();
        if (daysToCeiling == 0) {
            return ceiling.getValue();
        }
        int daysToFloor = DaysUtil.daysBetween(floor.getKey(), idealAssessmentDay).getAsInt();
        return daysToFloor < daysToCeiling ? floor.getValue() : ceiling.getValue();
    }

    /**
     * For some plots we display data by visit, not all individual lesions, so group by lesion date to exclude "duplicates"
     * in sense of that plots
     */
    private FilterQuery<AssessedTargetLesion> getFilterQueryByVisit(Datasets datasets, Filters<AssessedTargetLesion> filters,
                                                                      PopulationFilters populationFilters,
                                                                      Predicate<AssessedTargetLesion> eventPredicate) {

        Stream<AssessedTargetLesion> eventStream = ((AssessedTargetLesionDatasetsDataProvider) getEventDataProvider(datasets, filters))
                .loadDataByVisit(datasets).stream();

        //if eventPredicate is provided, filtering on this predicate
        eventStream = eventPredicate == null ? eventStream : eventStream.filter(eventPredicate);
        Collection<AssessedTargetLesion> events = eventStream.collect(toList());
        Collection<Subject> subjects = getPopulationDatasetsDataProvider().loadData(datasets);
        return new FilterQuery<>(events, filters, subjects, populationFilters);
    }

    /**
     * For some plots we display data by visit, not all individual lesions, so group by lesion date to exclude "duplicates"
     * in sense of that plots
     */
    FilterResult<AssessedTargetLesion> getFilteredDataByVisit(Datasets datasets, Filters<AssessedTargetLesion> filters,
                                                         PopulationFilters populationFilters,
                                                         Predicate<AssessedTargetLesion> eventPredicate) {

        FilterQuery<AssessedTargetLesion> filterQuery = getFilterQueryByVisit(datasets, filters, populationFilters, eventPredicate);
        return eventFilterService.query(filterQuery);
    }

    Function<Collection<AssessedTargetLesion>, Map<String, List<AssessedTargetLesion>>> groupTumoursBySubject() {
        return atlList -> atlList.stream().collect(groupingBy(AssessedTargetLesion::getSubjectId));
    }

    Function<Collection<AssessedTargetLesion>,
            Map<String, List<AssessedTargetLesion>>> groupTumoursBySubjectAndLesion() {
        return atlList -> atlList.stream()
                .collect(groupingBy(atl -> atl.getSubjectId() + atl.getEvent().getLesionNumber()));
    }

    public SelectionDetail getSelectionBySubjectIds(Datasets datasets, Set<String> subjectIds) {
        final FilterResult<AssessedTargetLesion> filteredDataByVisit =
                getFilteredDataByVisit(datasets, AssessedTargetLesionFilters.empty(), PopulationFilters.empty(), null);
        List<Integer> weeks = getAssessmentWeeks(filteredDataByVisit.getFilteredEvents(), true);
        final List<AssessedTargetLesion> atlsClosestToIdealAssessmentDaysAsList =
                getAtlsClosestToIdealAssessmentDaysAsList(filteredDataByVisit.getFilteredEvents(), weeks, groupTumoursBySubject(), true);
        return getSelectionBySubjectIds(filteredDataByVisit.withResults(filteredDataByVisit.getAllEvents(),
                atlsClosestToIdealAssessmentDaysAsList), subjectIds);
    }
}
