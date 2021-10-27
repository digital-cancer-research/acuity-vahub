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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByAttributes;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.ObjectUtil.keysEquals;
import static com.acuity.visualisations.rawdatamodel.util.ObjectUtil.toStringNormalizingNumbers;
import static java.util.stream.Collectors.toMap;

public interface SimpleSelectionMatchingService<T, G extends Enum<G> & GroupByOption<T>> {

    default List<T> getMatchedItems(Collection<T> filteredEvents, ChartSelection<T, G, ChartSelectionItem<T, G>> selection) {
        //to speed up we first filter on trellises found in selection
        Set<Map<G, Object>> distinctTrellises = selection.getSelectionItems().stream()
                .map(ChartSelectionItem::getSelectedTrellises).collect(Collectors.toSet());
        ChartGroupByOptions<T, G> trellisSettings = selection.getSettings().limitedByTrellisOptions();
        Map<GroupByKey<T, G>, List<T>> trellisGroups = filteredEvents.stream()
                .collect(Collectors.groupingBy(e -> Attributes.get(trellisSettings, e)));
        Map<GroupByKey<T, G>, Collection<T>> groupedEvents = GroupByAttributes.group(trellisGroups.entrySet().stream()
                .filter(e -> distinctTrellises.stream().anyMatch(t -> keysEquals(e.getKey().getTrellisByValues(), t)))
                .flatMap(e -> e.getValue().stream()).collect(Collectors.toList()), selection.getSettings());
        //then filtering rest on selection details
        Predicate<GroupByKey<T, G>> selectionMatchPredicate = getSelectionMatchPredicate(selection);
        return groupedEvents.entrySet().stream()
                .filter(e -> selectionMatchPredicate.test(e.getKey())).flatMap(e -> e.getValue().stream()).collect(Collectors.toList());
    }

    default Predicate<GroupByKey<T, G>> getSelectionMatchPredicate(
            ChartSelection<T, G, ChartSelectionItem<T, G>> selection) {
        List<ChartSelectionItem<T, G>> normalized = selection.getSelectionItems().stream().map(i -> {
            Map<G, Object> selectedTrellises = i.getSelectedTrellises().entrySet().stream()
                    .collect(toMap(Map.Entry::getKey, e -> toStringNormalizingNumbers(e.getValue())));

            Map<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = i.getSelectedItems().entrySet().stream()
                    .collect(toMap(Map.Entry::getKey, e -> toStringNormalizingNumbers(e.getValue())));
            return ChartSelectionItem.of(selectedTrellises, selectedItems);
        }).collect(Collectors.toList());
        return e -> normalized.parallelStream().anyMatch(
                selectionItem -> keysEquals(selectionItem.getSelectedTrellises(), e.getTrellisByValues())
                        && keysEquals(selectionItem.getSelectedItems(), e.getValues()))
                || selection.getSelectionItems().parallelStream().anyMatch(
                selectionItem -> keysEquals(selectionItem.getSelectedTrellises(), e.getTrellisByValues())
                        && keysEquals(selectionItem.getSelectedItems(), e.getValues()));
    }
}
