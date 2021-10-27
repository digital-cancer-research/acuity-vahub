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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.ObjectUtil;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.plots.ErrorLineChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.plots.LineChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;


import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.NAME;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.ORDER_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.STANDARD_DEVIATION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static com.acuity.visualisations.rawdatamodel.util.ObjectUtil.toDouble;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class ExposureLineChartService extends LineChartService<Exposure, ExposureGroupByOptions> {
    //assuming there will be only doubles on x axis, getting code faster
    @Override
    public Predicate<GroupByKey<Exposure, ExposureGroupByOptions>> getSelectionMatchPredicate(
            ChartSelection<Exposure, ExposureGroupByOptions, ChartSelectionItem<Exposure, ExposureGroupByOptions>> selection) {
        final List<ChartSelectionItem<Exposure, ExposureGroupByOptions>> normalized = selection.getSelectionItems().stream().map(i -> {
            final Map<ExposureGroupByOptions, Object> selectedTrellises = i.getSelectedTrellises().entrySet().stream()
                    .collect(toMap(e -> e.getKey(), e -> Objects.toString(e.getValue())));

            final Map<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = i.getSelectedItems().entrySet().stream()
                    .collect(toMap(e -> e.getKey(), e -> e.getKey() == ChartGroupByOptions.ChartGroupBySetting.X_AXIS
                            ? toDouble(e.getValue()) : Objects.toString(e.getValue())));
            return ChartSelectionItem.of(selectedTrellises, selectedItems);
        }).collect(toList());
        return e -> normalized.parallelStream()
                .anyMatch(selectionItem -> ObjectUtil.keysEquals(selectionItem.getSelectedTrellises(), e.getTrellisByValues())
                && keysEquals(selectionItem.getSelectedItems(), e.getValues()));
    }

    private static <K> boolean keysEquals(Map<K, Object> o1, Map<K, Object> o2) {
        return (o1.size() == 0 && o2.size() == 0) || (o1.size() == o2.size()
                && o1.entrySet().stream().allMatch(
                e -> {
                    final Object v1 = e.getValue() == null ? null : (e.getKey() == ChartGroupByOptions.ChartGroupBySetting.X_AXIS
                            ? (Double) e.getValue() : Objects.toString(e.getValue()));
                    final Object o = o2.get(e.getKey());
                    final Object v2 = o == null ? null : (e.getKey() == ChartGroupByOptions.ChartGroupBySetting.X_AXIS
                            ? (Double) o : Objects.toString(o));
                    return Objects.equals(v1, v2);
                }
        ));
    }

    @Override
    protected <T, G extends Enum<G> & GroupByOption<T>> List<LineChartEntry> getSeries(List<GroupByKey<T, G>> combined) {
        return combined.stream()
                       .map(key -> {
                           final Object x = key.getValue(X_AXIS);
                           final Object y = key.getValue(Y_AXIS);
                           final Object name = key.getValue(NAME);
                           final Object colorBy = key.getValue(COLOR_BY);
                           final Object orderBy = key.getValues().containsKey(ORDER_BY)
                                   ? key.getValue(ORDER_BY)
                                   : x;
                           final Double standardDeviation = (Double) key.getValue(STANDARD_DEVIATION);
                           return new ErrorLineChartEntry(x, y, name, colorBy, orderBy, standardDeviation);
                       })
                       .sorted()
                       .collect(Collectors.toList());
    }
}
