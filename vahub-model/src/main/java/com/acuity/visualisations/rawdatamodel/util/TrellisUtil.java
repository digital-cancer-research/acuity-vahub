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

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.HasDrugOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.RangeOption;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartOptionRange;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static com.acuity.visualisations.rawdatamodel.util.Constants.DOUBLE_DASH;
import static java.util.stream.Collectors.toList;

@SuppressWarnings("checkstyle:HideUtilityClassConstructor")
@UtilityClass
public final class TrellisUtil {

    /**
     * Creates a set of all trellis options for specified events.
     * For example, trellis items contains two enum elements: RESULT_UNIT and TREATMENT_CYCLE.
     * All events will be searched for all available values of corresponding attributes.
     * Result could contain following TrellisOptions:
     * new TrellisOptions("RESULT_UNIT", newArrayList("g/dL", "mmol/L")),
     * new TrellisOptions("TREATMENT_CYCLE", newArrayList("Cycle1", "Cycle2"))
     * If trellisesItems contains values of EmptyTrellises, empty set is returned
     *
     * @param filteredEvents - collection of events
     * @param trellisesItems - items of enum that extends ITrellises
     * @param <T>            - events type
     * @param <G>            - trellises enum type
     * @return set of available trellis options
     */
    @SafeVarargs
    public static <T, G extends Enum<G> & GroupByOption<T>> List<TrellisOptions<G>> getTrellisOptions(Collection<T> filteredEvents, G... trellisesItems) {
        List<TrellisOptions<G>> trellises = new ArrayList<>();

        for (G trellisItem : trellisesItems) {
            final TrellisOptions<G> optionsForTrellisesItem = getOptionsForTrellisItem(filteredEvents, trellisItem.<T, G>getGroupByOptionAndParams());
            if (optionsForTrellisesItem.getTrellisOptions().stream().filter(i -> i != null && !i.equals(Attributes.DEFAULT_EMPTY_VALUE)).count() > 0) {
                trellises.add(optionsForTrellisesItem);
            }
        }
        return trellises;
    }

    /**
     * Gets trellis options
     *
     * @param filteredEvents events that used to get trelisses options
     * @param trellisesItems trellises items
     * @param <T>            items type
     * @param <G>            group options type
     * @return list of available trellis options
     */
    @SafeVarargs
    public static <T, G extends Enum<G> & GroupByOption<T>> List<TrellisOptions<G>> getTrellisOptions(
            Collection<T> filteredEvents, ChartGroupByOptions.GroupByOptionAndParams<T, G>... trellisesItems) {
        List<TrellisOptions<G>> trellises = new ArrayList<>();

        for (ChartGroupByOptions.GroupByOptionAndParams<T, G> trellisItem : trellisesItems) {
            final TrellisOptions<G> optionsForTrellisesItem = getOptionsForTrellisItem(filteredEvents, trellisItem);
            if (optionsForTrellisesItem.getTrellisOptions().stream().filter(i -> i != null && !i.equals(Attributes.DEFAULT_EMPTY_VALUE)).count() > 0) {
                trellises.add(optionsForTrellisesItem);
            }
        }
        return trellises;
    }

    /**
     * This method returns attribute results without params,
     * to support more dynamic trellising we need to request trellis option per exact user selection with params (bin size, etc)
     */
    @SneakyThrows
    public static <T, G extends Enum<G> & GroupByOption<T>> TrellisOptions<G> getOptionsForTrellisItem(
            Collection<T> filteredEvents, ChartGroupByOptions.GroupByOptionAndParams<T, G> trellisesItem) {
        G groupByOption = trellisesItem.getGroupByOption();
        Stream<Object> trellisItems = filteredEvents.parallelStream()
                .map(e -> Attributes.get(trellisesItem, e));
        Stream<String> trellisOptions;
        if (groupByOption.getClass().getField(groupByOption.name()).isAnnotationPresent(RangeOption.class)) {
            trellisOptions = trellisItems
                    .flatMap(e -> e instanceof List ? ((List<BarChartOptionRange>) e).stream() : Stream.of(e))
                    .map(e -> e instanceof String
                            && Attributes.DEFAULT_EMPTY_VALUE.equalsIgnoreCase((String) e)
                            ? BarChartOptionRange.empty() : (BarChartOptionRange) e)
                    .map(BarChartOptionRange::toString);
        } else if (groupByOption.getClass().getField(groupByOption.name()).isAnnotationPresent(HasDrugOption.class)) {
            trellisOptions = trellisItems
                    .filter(e -> e instanceof Map)
                    .flatMap(e -> ((Map<String, String>) e).entrySet().stream())
                    .filter(a -> StringUtils.isNotEmpty(a.getKey()))
                    .map(a -> a.getKey() + DOUBLE_DASH + a.getValue());
        } else {
            trellisOptions = trellisItems
                    .filter(Objects::nonNull)
                    .map(Object::toString);
        }

        return new TrellisOptions<>(trellisesItem.getGroupByOption(),
                trellisOptions
                        .distinct()
                        .sorted(AlphanumEmptyLastComparator.getInstance())
                        .collect(toList()));
    }
}
