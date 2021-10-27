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

package com.acuity.visualisations.rawdatamodel.util;

import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.TumourTherapy;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputColumnRangeChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.acuity.visualisations.rawdatamodel.util.Constants.SUMMARY;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.weeksBetween;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy.RADIOTHERAPY_LABEL;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public final class TumourTherapyUtil {

    private TumourTherapyUtil() {
    }

    /**
     * Method returns the last therapy (therapy with the latest end date) per subject.
     * If chemo- or radiotherapy event with the latest end date overlaps with other event(s),
     * these events are combined: start date is set as earliest of start dates, end date is set as latest of end dates.
     * @param therapies - input chemo- and radiotherapy data
     * @return map which has subject code as a key and merged last therapy object as a value
     */
    public static Map<String, TumourTherapy> getLastTherapies(List<TumourTherapy> therapies) {

        return therapies.stream()
                .sorted(Comparator.comparing(TumourTherapy::getEndDate).reversed())
                .collect(Collectors.toMap(c -> c.getSubject().getSubjectCode(),
                        Function.identity(),
                        therapyMerger(),
                        () -> new TreeMap<>(Collections.reverseOrder())));
    }

    /**
     * Method returns grouped by subject merged therapies. Important that straightforward merge used, it means that
     * therapies with mutually non-overlapping periods are kept as is and therapies with overlapping periods are merged.
     * Supposed to be used on filtered merged therapies not to lose events if "connecting" therapy event was filtered out.
     * Example: subject1 has 4 events, that are combined to one "last therapy" event:
     *   1) chemotherapy1 (Jan 01 - Jan 10)
     *   2) radiotherapy (Jan 05 - Jan 15)
     *   3) chemotherapy2 (Jan 12 - Jan 20)
     *   4) chemotherapy3 (Jan 18 - Jan 22)
     * Combined TumourTherapy has start date Jan 01 and end date Jan 22. Then radiotherapy is filtered out.
     * Expected "last therapy" must now have 2 events: chemotherapy1 (Jan 01 - Jan 10)
     * and merged chemotherapy2 + chemotherapy3 (Jan 12 - Jan 22), which do not overlap.
     *
     * @param therapies - filtered TumourTherapy events grouped by subject code
     * @return map of merged therapies grouped by subject code
     */
    public static Map<String, List<TumourTherapy>> getLastTherapiesNotCrossedAllowed(Map<String, List<TumourTherapy>> therapies) {
        return therapies.entrySet().stream()
                .collect(toMap(Map.Entry::getKey,
                        e -> straightforwardMerge(e.getValue().stream()
                                .sorted(Comparator.comparing(TumourTherapy::getEndDate).reversed())
                                .collect(toList()), false),
                        (v1, v2) -> {
                            throw new IllegalStateException();
                        },
                        TreeMap::new));
    }

    /**
     * Get list of TumourTherapy events, each of that is based on one chemo- or radiotherapy event.
    */
    public static List<TumourTherapy> getTherapiesFromChemoRadio(Collection<Chemotherapy> chemotherapy,
                                                                 Collection<Radiotherapy> radiotherapy) {
        List<TumourTherapy> therapies = new ArrayList<>();
        therapies.addAll(chemotherapy.stream()
                .filter(TumourTherapyUtil::endsBeforeFirstTreatmentDate)
                .map(ct -> {
                    TumourTherapy therapyFromChemo = TumourTherapy.from(ct);
                    therapyFromChemo.setPreviousChemoTherapies(Collections.singleton(ct));
                    therapyFromChemo.setName(ct.getEvent().getPreferredMedOrEmpty());
                    return therapyFromChemo;
                })
                .collect(toList()));
        therapies.addAll(radiotherapy.stream()
                .filter(TumourTherapyUtil::endsBeforeFirstTreatmentDate)
                .map(rt -> {
                    TumourTherapy therapyFromRadio = TumourTherapy.from(rt);
                    therapyFromRadio.setPreviousRadioTherapies(Collections.singleton(rt));
                    therapyFromRadio.setName(RADIOTHERAPY_LABEL);
                    return therapyFromRadio;
                }).collect(toList()));
        return therapies;
    }

    public static Map<String, List<TumourTherapy>> withEmptyStartDatesPopulatedBySubject(List<TumourTherapy> therapies) {
        return therapies.stream().collect(groupingBy(HasSubject::getSubjectCode))
                .entrySet().stream().peek(subjectTherapies -> {
                    boolean hasEmptyStartDates = subjectTherapies.getValue().stream().anyMatch(e -> e.getStartDate() == null);
                    if (!hasEmptyStartDates) {
                        return;
                    }
                    Date startDate = calculateStartDate(subjectTherapies.getValue());
                    subjectTherapies.getValue().stream().filter(t -> t.getStartDate() == null)
                            .forEach(t -> {
                                t.setStartDate(startDate);
                                t.setNoStartDate(true);
                            });

                })
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * If the earliest of the start dates among all subject's events is before the earliest of the end
     * dates among all events, it is used. If there are no start dates, "first end date - 1 week" is used as start date,
     * where "first end date" is calculated as the earliest end dates of chemo- and radiotherapy events
     * that particular TumourTherapy includes
     *
     * @param subjectTherapies - list of subject's therapies
     * @return list of subject's therapies with start date populated
     */
    private static Date calculateStartDate(List<TumourTherapy> subjectTherapies) {
        final Date firstEndDate = subjectTherapies.stream()
                .flatMap(t -> Stream.of(t.getPreviousChemoTherapies().stream().map(Chemotherapy::getEndDate),
                        t.getPreviousRadioTherapies().stream().map(Radiotherapy::getEndDate))
                        .flatMap(datesStream -> datesStream))
                .filter(Objects::nonNull)
                .min(Date::compareTo).get(); // cannot be null

        final Optional<Date> firstStartDateBeforeFirstEndDate = subjectTherapies.stream()
                .flatMap(t -> Stream.of(t.getPreviousChemoTherapies().stream().map(Chemotherapy::getStartDate),
                        t.getPreviousRadioTherapies().stream().map(Radiotherapy::getStartDate))
                        .flatMap(datesStream -> datesStream))
                .filter(Objects::nonNull)
                .min(Date::compareTo)
                .filter(date -> date.before(firstEndDate));

        return firstStartDateBeforeFirstEndDate.orElse(DaysUtil.addDays(firstEndDate, -7));
    }

    public static <T extends SubjectAwareWrapper & HasStartEndDate> boolean endsBeforeFirstTreatmentDate(T event) {
        return event.getEndDate() != null && !event.getEndDate().after(event.getSubject().getFirstTreatmentDate());
    }

    /**
     * Returns sorted map of therapies and toc's by subject. First list of therapies for every subject contains merged therapies.
     * Add toc to the first list to place them on the same line on the chart
     */
    public static Map<String, List<List<TumourTherapy>>> mergeTocAndTherapies(Map<String, TumourTherapy> tocBySubject,
                                                                              Map<String, List<List<TumourTherapy>>> therapiesBySubject) {
        Map<String, List<List<TumourTherapy>>> mergedTocAndTherapiesBySubject = new TreeMap<>(Comparator.reverseOrder());
        // time on compound always exists for a subject, therapies - not necessary
        tocBySubject.forEach((k, v) -> {
            List<List<TumourTherapy>> therapies = therapiesBySubject.getOrDefault(k, Collections.singletonList(new ArrayList<>()));
            // 0th list contains merged therapies and time on compound, others therapies by subject go without toc
            therapies.get(0).add(v);
            mergedTocAndTherapiesBySubject.put(k, therapies);
        });
        return mergedTocAndTherapiesBySubject;
    }

    public static OutputColumnRangeChartEntry getOutputColumnRangeChartEntry(int x, TumourTherapy value) {
        int high = weeksBetween(value.getSubject().getFirstTreatmentDate(), value.getEndDate()).orElse(0);
        int low = weeksBetween(value.getSubject().getFirstTreatmentDate(), value.getStartDate()).orElse(0);

        return new OutputColumnRangeChartEntry(x, high, low, value.getColor(),
                value.isNoStartDate(), value.getTherapiesTooltip(), value.getName());
    }

    public static List<TumourTherapy> straightforwardMerge(List<TumourTherapy> therapies, boolean isSummary) {
        List<TumourTherapy> merged = therapies.stream().map(t -> t.toBuilder().build()).collect(toList()); // defencive copy
        if (merged.size() > 1) {
            for (int i = 0; i < merged.size() - 1; i++) {
                if (DaysUtil.periodsOverlapped(merged.get(i).getStartDate(), merged.get(i).getEndDate(),
                        merged.get(i + 1).getStartDate(), merged.get(i + 1).getEndDate())) {
                    merged.set(i + 1, getMergedTherapy(merged.get(i), merged.get(i + 1)));
                    merged.set(i, null);
                }
            }
        }
        merged = merged.stream()
                .filter(Objects::nonNull)
                .peek(t -> {
                    if (isSummary) {
                        t.setName(SUMMARY);
                    }
                })
                .collect(toList());
        return merged;
    }

    private static TumourTherapy getMergedTherapy(TumourTherapy t1, TumourTherapy t2) {
        Date earliestStartDate = DaysUtil.getMinDate(t1.getStartDate(), t2.getStartDate()); // nulls are possible
        Date latestEndDate = t1.getEndDate().compareTo(t2.getEndDate()) <= 0 ? t2.getEndDate() : t1.getEndDate(); // no nulls
        TumourTherapy mergedTherapy = new TumourTherapy(earliestStartDate, latestEndDate, t1.getSubject());
        mergedTherapy.setPreviousChemoTherapies(Stream.concat(t1.getPreviousChemoTherapies().stream(),
                t2.getPreviousChemoTherapies().stream()).collect(toSet()));
        mergedTherapy.setPreviousRadioTherapies(Stream.concat(t1.getPreviousRadioTherapies().stream(),
                t2.getPreviousRadioTherapies().stream()).collect(toSet()));
        boolean hasNoStartDate = t1.isNoStartDate() || t2.isNoStartDate();
        mergedTherapy.setNoStartDate(hasNoStartDate);
        return mergedTherapy;
    }

    private static BinaryOperator<TumourTherapy> therapyMerger() {
        return (t1, t2) -> {
            if (!DaysUtil.periodsOverlapped(t1.getStartDate(), t1.getEndDate(), t2.getStartDate(), t2.getEndDate())) {
                return t1.getEndDate().compareTo(t2.getEndDate()) >= 0 ? t1 : t2;
            } else {
                return getMergedTherapy(t1, t2);
            }
        };
    }
}
