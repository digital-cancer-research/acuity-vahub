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

package com.acuity.visualisations.rawdatamodel.service.compatibility;

import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartData;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartOptionRange;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;

/**
 * For now ranged bar chart can only be colored. If we move coloring
 * logic to front-end, we should change class hierarchy, making
 * {@link BarChartUIModelService} parent of this class.
 */
@Service
public class RangedColoredBarChartUIModelService extends ColoredBarChartUIModelService {

    public RangedColoredBarChartUIModelService(BarChartColoringService coloringService) {
        super(coloringService);
    }

    @Override
    <T extends HasSubject & HasStringId, G extends Enum<G> & GroupByOption<T>> List<BarChartData> getBarChartDataList(
            Map.Entry<GroupByKey<T, G>, Map<GroupByKey<T, G>, BarChartCalculationObject<T>>> groupByTrellisEntry, List<?> sortedCategories) {

        Iterator<Map.Entry<GroupByKey<T, G>, BarChartCalculationObject<T>>> iterator = groupByTrellisEntry.getValue().entrySet().iterator();
        Object value = iterator.hasNext() ? iterator.next().getKey().getValue(COLOR_BY) : null;
        value = value instanceof String && ((String) value).equalsIgnoreCase(Attributes.DEFAULT_EMPTY_VALUE) && iterator.hasNext()
                ? iterator.next().getKey().getValue(COLOR_BY) : value;

        if (value instanceof BarChartOptionRange) {

            Map<BarChartOptionRange, Map<GroupByKey<T, G>, BarChartCalculationObject<T>>> groupByColorByRanges =
                    groupByTrellisEntry.getValue().entrySet().stream().collect(Collectors.groupingBy(
                            e -> e.getKey().limitedBySettings(COLOR_BY).getValue(COLOR_BY) instanceof String
                                    && ((String) e.getKey().limitedBySettings(COLOR_BY).getValue(COLOR_BY)).equalsIgnoreCase(Attributes.DEFAULT_EMPTY_VALUE)
                                    ? BarChartOptionRange.empty() : (BarChartOptionRange) e.getKey().limitedBySettings(COLOR_BY).getValue(COLOR_BY),
                            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

            List<BarChartOptionRange> ranges = new ArrayList<>(groupByColorByRanges.keySet());
            Collections.sort(ranges);

            Map<BarChartOptionRange, Map<GroupByKey<T, G>, BarChartCalculationObject<T>>> sortedGroupByColorBy = new LinkedHashMap<>();
            for (BarChartOptionRange range : ranges) {
                sortedGroupByColorBy.put(range, groupByColorByRanges.get(range));
            }

            return sortedGroupByColorBy.entrySet().stream().map(groupByColorByEntry -> {
                List<BarChartEntry<T>> barChartEntries = groupByColorByEntry.getValue().entrySet().stream()
                        .map(e -> new BarChartEntry<T>(getCategoryName(e.getKey().getValue(X_AXIS)),
                                e.getValue().getValue(), e.getValue().getTotalSubject(), e.getValue().getEventSet()))
                        .collect(Collectors.toList());
                return new BarChartData(
                        getCategoryName(groupByColorByEntry.getKey()),
                        sortedCategories,
                        barChartEntries);
            }).collect(Collectors.toList());
        } else {
            return super.getBarChartDataList(groupByTrellisEntry, sortedCategories);
        }
    }

    @Override
    <T extends HasStringId & HasSubject, G extends Enum<G> & GroupByOption<T>> List<String> sortCategories(
            Map<GroupByKey<T, G>, BarChartCalculationObject<T>> barChart, CountType countType) {
        Iterator<Map.Entry<GroupByKey<T, G>, BarChartCalculationObject<T>>> iterator = barChart.entrySet().iterator();
        Object value = iterator.hasNext() ? iterator.next().getKey().getValue(X_AXIS) : null;
        value = (value instanceof String && Attributes.DEFAULT_EMPTY_VALUE.equals(value.toString()))
                && iterator.hasNext() ? iterator.next().getKey().getValue(X_AXIS) : value;
        if (value instanceof BarChartOptionRange) {

            List<BarChartOptionRange<? extends Comparable<?>>> categories = new ArrayList<>();
            for (Map.Entry<GroupByKey<T, G>, BarChartCalculationObject<T>> entry : barChart.entrySet()) {
                BarChartOptionRange<? extends Comparable> pair =
                        entry.getKey().getValue(X_AXIS).toString().equals(Attributes.DEFAULT_EMPTY_VALUE) ? null
                                : (BarChartOptionRange) entry.getKey().getValue(X_AXIS);
                BarChartOptionRange<? extends Comparable<?>> range = pair == null ? BarChartOptionRange.empty()
                        : new BarChartOptionRange<>((Comparable) pair.getLeft(), (Comparable) pair.getRight());
                categories.add(range);
            }
            final List<String> collect = (List<String>) (categories.stream()
                    .distinct()
                    .sorted(Comparator.<Comparable>nullsLast(Comparator.naturalOrder()))
                    .map(Objects::toString)
                    .collect(Collectors.toList()));
            return collect;
        } else {
            return super.sortCategories(barChart, countType);
        }
    }

    @Override
    Object getCategoryName(Object colorByValue) {
        if (colorByValue instanceof BarChartOptionRange) {
            return Objects.toString(colorByValue);
        } else {
            return super.getCategoryName(colorByValue);
        }
    }
}
