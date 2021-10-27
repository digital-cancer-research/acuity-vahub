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

import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.Bin;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.EmptyBin;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.IntBin;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasContinuousRank;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputRangeChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.RangeChartSeries;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.RangeChartCalculationObject;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;

@Service
public class RangePlotUiModelService {

    public <T, G extends Enum<G> & GroupByOption<T>> List<TrellisedRangePlot<T, G>> toTrellisedRangePlot(
            Map<GroupByKey<T, G>, RangeChartCalculationObject> rangePlot,
            StatType statType) {
        final Map<GroupByKey<T, G>, Map<GroupByKey<T, G>, RangeChartCalculationObject>> groupedByTrellis = rangePlot.entrySet()
                .stream().collect(Collectors.groupingBy(
                        e -> e.getKey().limitedByTrellisOptions(),
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
                ));

        List<?> xCategories;
        final boolean axisContinuous = isAxisContinuous(rangePlot.keySet());
        if (isAxisBinned(rangePlot.keySet())) {
            final Set<Bin<? extends Comparable<?>>> xBinCategories = rangePlot.keySet().stream()
                    .map(k -> k.getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS))
                    .distinct().filter(e -> e instanceof Bin && !(e instanceof EmptyBin))
                    .map(e -> (Bin<? extends Comparable<?>>) e).collect(Collectors.toSet());
            xCategories = Attributes.getBinCategories(xBinCategories);
        } else {
            xCategories = rangePlot.keySet().stream().map(k -> k.getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS))
                    .distinct().filter(e -> e != null && !Attributes.DEFAULT_EMPTY_VALUE.equals(e.toString())).sorted().collect(Collectors.toList());
        }
        return groupedByTrellis.entrySet().stream().map(groupByTrellisEntry -> {
            final List<TrellisOption<T, G>> trellisOptions = groupByTrellisEntry.getKey().getTrellisByValues().entrySet().stream()
                    .map(option -> TrellisOption.of(option.getKey(), option.getValue())).collect(Collectors.toList());

            final Map<GroupByKey<T, G>, Map<GroupByKey<T, G>, RangeChartCalculationObject>> groupedBySeries = groupByTrellisEntry.getValue().entrySet()
                    .stream().collect(Collectors.groupingBy(
                            e -> e.getKey().limitedBySettings(SERIES_BY),
                            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
                    ));

            List<RangeChartSeries> stats = groupedBySeries.entrySet().stream()
                    .map(groupBySeries -> {
                        final Object name = groupBySeries.getKey().getValue(SERIES_BY);
                        final Map<Object, RangeChartCalculationObject> mapByXCategory = groupBySeries.getValue().entrySet().stream()
                                .filter(e -> {
                                    final Object value = e.getKey().getValue(X_AXIS);
                                    return !Attributes.DEFAULT_EMPTY_VALUE.equals(value == null ? null : value.toString());
                                })
                                .collect(Collectors.toMap(e -> e.getKey().getValue(X_AXIS), Map.Entry::getValue));

                        final List<OutputRangeChartEntry> entries = (axisContinuous ? mapByXCategory.keySet() : xCategories).stream()
                                .filter(Objects::nonNull)
                                .map(xAxisValue -> OutputRangeChartEntry.of(
                                        xAxisValue.toString(),
                                        getContinuousAxisValueRank(xAxisValue, () -> xCategories.indexOf(xAxisValue)),
                                        mapByXCategory.get(xAxisValue), statType))
                                .sorted(Comparator.comparing(OutputRangeChartEntry::getXRank)).collect(Collectors.toList());
                        return rangeChartSeries(name == null ? "All" : name.toString(), entries);
                    })
                    .sorted(Comparator.comparing(RangeChartSeries::getName))
                    .collect(Collectors.toList());
            return new TrellisedRangePlot<>(trellisOptions, stats);
        }).sorted(Comparator.comparing(TrellisedChart::getTrellisByString)).collect(Collectors.toList());

    }

    private <T, G extends Enum<G> & GroupByOption<T>> boolean isAxisContinuous(Collection<GroupByKey<T, G>> items) {
        return items.stream().allMatch(k -> {
            final Object value = k.getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS);
            return isContinuousAxisValue(value);
        });
    }

    private <T, G extends Enum<G> & GroupByOption<T>> boolean isAxisBinned(Collection<GroupByKey<T, G>> items) {
        return items.stream().anyMatch(k -> k.getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS) instanceof Bin);
    }

    private double getContinuousAxisValueRank(Object value, Supplier<Number> orElse) {
        if (value instanceof HasContinuousRank) {
            return ((HasContinuousRank) value).getRank();
        }
        return (isContinuousAxisValue(value)
                ? value instanceof Number ? ((Number) value).doubleValue()
                : (value instanceof IntBin ? ((IntBin) value).getStart() : 0)
                : orElse.get()).doubleValue();
    }

    protected RangeChartSeries rangeChartSeries(String name, List<OutputRangeChartEntry> entries) {
        return new RangeChartSeries(name, entries);
    }

    private boolean isContinuousAxisValue(Object value) {
        return value instanceof Number
                || (value instanceof IntBin && ((IntBin) value).getSize() == 1);
    }
}
