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

package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByAttributes;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.GroupedData;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.Comparator.comparing;

public final class ScatterPlotUtils {
    private ScatterPlotUtils() {
    }

    /**
     * It groups events:
     * - by Arm (optionally), then
     * - by Measurement name (provided by keyExtractor), then
     * - by Subject
     * <p>
     * Each group contains only events having max values provided by valueExtractor
     */
    public static <T extends HasSubject, G extends Enum<G> & GroupByOption<T>, C extends Comparable<? super C>>
    Map<GroupByKey<T, G>, Map<Subject, List<T>>> maxSubjectEvents(
            Collection<T> events, ChartGroupByOptions<T, G> groupByOptions, Function<T, C> valueExtractor) {
        return GroupByAttributes.group(events, groupByOptions).entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey,
                        e -> e.getValue().stream().collect(
                                Collectors.groupingBy(HasSubject::getSubject, maxList(comparing(valueExtractor))))));
    }


    /**
     * It returns information about ranking of groups of items.
     * The maximum value will have the biggest rank number and etc.
     * If there is multiple elements in ranking group, then next rank number will be current rank + number of elements
     * Each rank group contains list of theme group namings.
     * <p>
     * Example:
     * Input data = {'A': 1, 'B': 2, 'C': 2, 'D': 3}, calculationMethod: extract value
     * Result rank groups: {1: 'A', 2: ['B', 'C'], 4: 'D'}
     */
    public static <K extends SubjectAwareWrapper, G>
    Map<Long, List<G>> getRankedInfo(GroupedData<K, G> groupedElements, BiFunction<List<Subject>, List<K>, Double> calculationMethod) {
        Map<Long, List<RankedObject>> rankedValues = groupedElements.getTotalSubjects().entrySet().stream()
                .map(e -> new RankedObject<>(e.getKey(), calculationMethod.apply(e.getValue(), groupedElements.getEvents().get(e.getKey()))))
                .sorted(comparing(RankedObject::getResultValue))
                .collect(rankedMap(RankedObject::getResultValue));

        return rankedValues.entrySet().stream()
                .collect(Collectors.toMap(e -> e.getKey(),
                        e -> e.getValue().stream().map((RankedObject r) -> (G) r.getIdentifier()).collect(Collectors.toList())));
    }

    @Data
    @AllArgsConstructor
    private static class RankedObject<T> {
        private T identifier;
        private Double resultValue;
    }

    private static <T, K> Collector<T, ?, SortedMap<Long, List<T>>> rankedMap(Function<T, K> valueExtractor) {
        return Collector.of(
                TreeMap::new,
                (rankMap, item) -> {
                    if (rankMap.isEmpty()) {
                        rankMap.put(1L, new ArrayList<T>());
                    } else {
                        long lastRank = rankMap.lastKey();
                        K currentValue = valueExtractor.apply(item);
                        List<T> lastRankItems = rankMap.get(lastRank);
                        K previousValue = valueExtractor.apply(lastRankItems.get(0));
                        if (!currentValue.equals(previousValue)) {
                            rankMap.put(lastRank + lastRankItems.size(), new ArrayList<>());
                        }
                    }
                    rankMap.get(rankMap.lastKey()).add(item);
                },
                (rankMap1, rankMap2) -> {
                    long lastRanking = rankMap1.lastKey();
                    long offset = lastRanking + rankMap1.get(lastRanking).size() - 1;
                    if (valueExtractor.apply(rankMap1.get(lastRanking).get(0))
                            == valueExtractor.apply(rankMap2.get(rankMap2.firstKey()).get(0))) {
                        rankMap1.get(lastRanking).addAll(rankMap2.get(rankMap2.firstKey()));
                        rankMap2.remove(rankMap2.firstKey());
                    }
                    rankMap2.forEach((rank, items) -> rankMap1.put(offset + rank, items));
                    return rankMap1;
                }
        );
    }


    private static <T> Collector<T, ?, List<T>> maxList(Comparator<? super T> comp) {
        return Collector.of(
                ArrayList::new,
                (list, t) -> {
                    int c;
                    if (list.isEmpty() || (c = comp.compare(t, list.get(0))) == 0) {
                        list.add(t);
                    } else if (c > 0) {
                        list.clear();
                        list.add(t);
                    }
                },
                (list1, list2) -> {
                    if (list1.isEmpty()) {
                        return list2;
                    }
                    if (list2.isEmpty()) {
                        return list1;
                    }
                    int r = comp.compare(list1.get(0), list2.get(0));
                    if (r < 0) {
                        return list2;
                    } else if (r > 0) {
                        return list1;
                    } else {
                        list1.addAll(list2);
                        return list1;
                    }
                });
    }
}
