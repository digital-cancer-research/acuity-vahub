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
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.Bin;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.EmptyBin;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.AlphanumEmptyLastComparator;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TooltipInfoOutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartData;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartDateFormattedOption;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartEntry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static java.util.stream.Collectors.toList;

@Service
@Primary
public class BarChartUIModelService {

    List<OutputBarChartData> collectToOutputData(List<BarChartData> allData, Supplier<Double> totalSubjectsCountSupplier) {

        //fill output data with ranks, sorted categories and sorted series
        return allData.stream().map(bar -> {

            final List<String> categories = bar.getCategories().stream()
                    .map(Objects::toString)
                    .collect(toList());

            boolean isCategoryMap = bar.getSeries().get(0).getCategory() instanceof Map;

            List<OutputBarChartEntry> series = bar.getSeries().stream()
                    .sorted(Comparator.comparing(e -> categories.indexOf(
                            isCategoryMap ? Objects.toString(((Map) e.getCategory()).keySet().iterator().next())
                                    : e.getCategory().toString())))
                    .map(e -> isCategoryMap
                            ? new TooltipInfoOutputBarChartEntry(e,
                            categories.indexOf(Objects.toString(((Map) e.getCategory()).keySet().iterator().next())) + 1)
                            : new OutputBarChartEntry(e, categories.indexOf(e.getCategory().toString()) + 1)).collect(toList());

            String name;
            String colorIndex;
            if (bar.getName() instanceof ImmutablePair) {
                name = bar.getName() == null ? ""
                        : ((ImmutablePair) bar.getName()).getLeft() == null ? "" : ((ImmutablePair) bar.getName()).getLeft().toString();
                colorIndex = bar.getName() == null ? ""
                        : ((ImmutablePair) bar.getName()).getRight() == null ? "" : ((ImmutablePair) bar.getName()).getRight().toString();

                if ("".equals(name)) {
                    if ("".equals(colorIndex) || "0".equals(colorIndex)) {
                        name = "(Empty)";
                    } else {
                        name = colorIndex;
                    }
                }
            } else {
                name = bar.getName() == null ? "" : bar.getName().toString();
            }
            return new OutputBarChartData(name, categories, series);
        }).collect(toList());
    }

    public <T extends HasStringId & HasSubject, G extends Enum<G> & GroupByOption<T>> List<TrellisedBarChart<T, G>> toTrellisedBarChart(
            Map<GroupByKey<T, G>, BarChartCalculationObject<T>> barChart, CountType countType) {
        List<?> sortedCategoriesList;
        if (isAxisBinned(barChart.keySet())) {
            final Set<Object> xAxisOptions = barChart.keySet().stream()
                    .map(k -> k.getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS)).collect(Collectors.toSet());

            Set<Bin<? extends Comparable<?>>> xBinCategories = xAxisOptions.stream().filter(e -> e instanceof Bin && !(e instanceof EmptyBin))
                    .map(e -> (Bin<? extends Comparable<?>>) e).collect(Collectors.toSet());

            sortedCategoriesList = Attributes.getBinCategories(xBinCategories);

            if (xAxisOptions.stream().anyMatch(e -> e instanceof EmptyBin)) {
                ((List<Bin>) sortedCategoriesList).add(Bin.empty());
            }
        } else {
            sortedCategoriesList = sortCategories(barChart, countType);
        }

        Map<GroupByKey<T, G>, Map<GroupByKey<T, G>, BarChartCalculationObject<T>>> groupedByTrellis = barChart.entrySet()
                .stream().collect(Collectors.groupingBy(
                        e -> e.getKey().limitedByTrellisOptions(),
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
                ));

        return groupedByTrellis.entrySet().stream().map(groupByTrellisEntry -> {
            final List<TrellisOption<T, G>> trellisOptions = groupByTrellisEntry.getKey().getTrellisByValues().entrySet().stream()
                    .map(option -> TrellisOption.of(option.getKey(), option.getValue())).collect(Collectors.toList());
            Supplier<Double> totalSubjectsCountSupplier = () ->
                    (double) groupByTrellisEntry.getValue().values().stream()
                            .map(BarChartCalculationObject::getSubjects)
                            .flatMap(Collection::stream)
                            .distinct()
                            .count();

            return new TrellisedBarChart<>(
                    trellisOptions,
                    collectToOutputData(getBarChartDataList(groupByTrellisEntry, sortedCategoriesList), totalSubjectsCountSupplier));
        }).sorted(Comparator.comparing(TrellisedChart::getTrellisByString)).collect(Collectors.toList());
    }

    <T extends HasStringId & HasSubject, G extends Enum<G> & GroupByOption<T>> List<BarChartData> getBarChartDataList(
            Map.Entry<GroupByKey<T, G>, Map<GroupByKey<T, G>, BarChartCalculationObject<T>>> groupByTrellisEntry, List<?> sortedCategories) {

        Map<GroupByKey<T, G>, Map<GroupByKey<T, G>, BarChartCalculationObject<T>>> groupByColorBy = groupByTrellisEntry.getValue().entrySet().stream()
                .collect(Collectors.groupingBy(
                        e -> e.getKey().limitedBySettings(COLOR_BY),
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
                ));

        return groupByColorBy.entrySet().stream().map(groupByColorByEntry -> {
            Object colorByValue = groupByColorByEntry.getKey().getValue(COLOR_BY);
            List<BarChartEntry<T>> barChartEntries = groupByColorByEntry.getValue().entrySet().stream()
                    .map(e -> new BarChartEntry<T>(getCategoryName(e.getKey().getValue(X_AXIS)),
                            e.getValue().getValue(), e.getValue().getTotalSubject(), e.getValue().getEventSet()))
                    .collect(Collectors.toList());
            return new BarChartData(
                    getCategoryName(colorByValue),
                    sortedCategories,
                    barChartEntries);
        }).sorted(getColorByOptionComparator()).collect(Collectors.toList());
    }

    <T extends BarChartData> Comparator<T> getColorByOptionComparator() {
        return (o1, o2) -> AlphanumEmptyLastComparator.getInstance().compare(o1.getName().toString(), o2.getName().toString());
    }

    <T extends HasStringId & HasSubject, G extends Enum<G> & GroupByOption<T>> List<String> sortCategories(
            Map<GroupByKey<T, G>, BarChartCalculationObject<T>> barChart, CountType countType) {
        if (barChart.entrySet().stream().anyMatch(e -> e.getKey().getValue(X_AXIS) instanceof BarChartDateFormattedOption)) {
            return barChart.entrySet().stream()
                    .sorted(Comparator.comparing(e -> (BarChartDateFormattedOption) e.getKey().getValue(X_AXIS)))
                    .map(e -> e.getKey().getValue(X_AXIS).toString())
                    .distinct()
                    .collect(Collectors.toList());
        } else {
            Function<BarChartCalculationObject<T>, Integer> countExtractor;
            switch (countType.getCountBase()) {
                case SUBJECT:
                    countExtractor = b -> b.getSubjects().size();
                    break;
                case EVENT:
                    countExtractor = BarChartCalculationObject::getEvents;
                    break;
                default:
                    throw new RuntimeException("Unknown Y_AXIS option: " + countType.toString());
            }

            Map<String, Integer> groupedAndSortedBarCharts = barChart.entrySet().stream()
                    .collect(Collectors.groupingBy(
                            this::groupByXAxis,
                            Collectors.summingInt(e -> countExtractor.apply(e.getValue()))));

            return groupedAndSortedBarCharts.entrySet()
                    .stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue()
                            .reversed()
                            .thenComparing(Map.Entry::getKey, AlphanumEmptyLastComparator.getInstance()))
                    .map(Map.Entry::getKey)
                    .collect(toList());
        }
    }

    private <T extends HasStringId & HasSubject, G extends Enum<G> & GroupByOption<T>> String groupByXAxis(
            Map.Entry<GroupByKey<T, G>, BarChartCalculationObject<T>> e) {
        Object xAxisValue = e.getKey().getValue(X_AXIS);
        Object category = xAxisValue instanceof Map
                ? ((Map) xAxisValue).keySet().iterator().next()
                : xAxisValue;
        return Objects.toString(getDefaultedGroupName(category));
    }

    Object getDefaultedGroupName(Object colorByValue) {
        return colorByValue == null ? "All" : colorByValue;
    }

    Object getCategoryName(Object colorByValue) {
        return getDefaultedGroupName(colorByValue);
    }

    private <T, G extends Enum<G> & GroupByOption<T>> boolean isAxisBinned(Collection<GroupByKey<T, G>> items) {
        return items.stream().anyMatch(k -> k.getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS) instanceof Bin);
    }
}
