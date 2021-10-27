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

import com.acuity.visualisations.rawdatamodel.trellis.grouping.ATLGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static com.acuity.visualisations.rawdatamodel.util.ObjectUtil.toDouble;
import static com.acuity.visualisations.rawdatamodel.util.ObjectUtil.toInteger;

/**
 * Service to build a line chart for Target Lesion Diameters over time plot.
 * Assuming there will be only doubles or integers on y axis,
 * getting code faster and avoiding problem with numbers without decimal part.
 * Trellis settings are omitted for now.
 */
@Service
public class TLDLineChartService extends LineChartService<AssessedTargetLesion, ATLGroupByOptions> {

    @Override
    public Predicate<GroupByKey<AssessedTargetLesion, ATLGroupByOptions>> getSelectionMatchPredicate(
            ChartSelection<AssessedTargetLesion, ATLGroupByOptions, ChartSelectionItem<AssessedTargetLesion, ATLGroupByOptions>> selection) {

        ATLGroupByOptions yOption = selection.getSettings().getOptions().get(Y_AXIS).getGroupByOption();

        final List<ChartSelectionItem<AssessedTargetLesion, ATLGroupByOptions>> normalized = selection.getSelectionItems().stream().map(i -> {
            final Map<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = i.getSelectedItems().entrySet().stream()
                    .collect(Collectors.toMap(Map.Entry::getKey, e -> getNormalizedObject(e, yOption)));
            return ChartSelectionItem.of(new HashMap<ATLGroupByOptions, Object>(), selectedItems);
        }).collect(Collectors.toList());
        return e -> normalized.parallelStream()
                .anyMatch(selectionItem -> keysEquals(selectionItem.getSelectedItems(), e.getValues()));
    }

    private Object getNormalizedObject(Map.Entry<ChartGroupByOptions.ChartGroupBySetting, Object> e, ATLGroupByOptions yOption) {

        final Object normalizedValue;
        if (e.getKey() == Y_AXIS) {
            if (yOption == ATLGroupByOptions.PERCENTAGE_CHANGE) {
                normalizedValue = toDouble(e.getValue());
            } else {
                normalizedValue = toInteger(e.getValue());
            }
        } else {
            normalizedValue = Objects.toString(e.getValue());
        }
        return normalizedValue;
    }

    private static <K> boolean keysEquals(Map<K, Object> o1, Map<K, Object> o2) {
        return (o1.size() == 0 && o2.size() == 0) || (o1.size() == o2.size()
                && o1.entrySet().stream().allMatch(
                e -> {
                    final Object v1 = e.getValue() == null ? null : (e.getKey() == Y_AXIS
                            ? e.getValue() : Objects.toString(e.getValue()));
                    final Object o = o2.get(e.getKey());
                    final Object v2 = o == null ? null : (e.getKey() == Y_AXIS
                            ? o : Objects.toString(o));
                    return Objects.equals(v1, v2);
                }
        ));
    }

    /**
     * Series must include not only baseline, but also post-baseline data.
     * The logic is the same as 'events.stream().anyMatch(e -> !e.getEvent().isBaseline());',
     * because it must be always one and only one baseline point in a group.
     * The size check is used for performance optimization
     */
    @Override
    protected boolean isSeriesValid(List<AssessedTargetLesion> events) {
        return events.size() > 1;
    }
}
