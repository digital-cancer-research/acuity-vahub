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

import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.Bin;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.EmptyBin;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.ColoredOutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartData;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartEntry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static java.util.stream.Collectors.toList;


/**
 * De facto this class belongs to BarChartUIModelService hierarchy.
 * We decided to keep it intact in the process of migrating from AZ to dECMT.
 * There is a good probability that the logic of this class is similar to BarChartUIModelService,
 * but this research is out of scope of the current migration process
 */
@Service
public class OnsetLineChartUIModelService {

    public <T extends HasStringId & HasSubject, G extends Enum<G> & GroupByOption<T>> List<TrellisedBarChart<T, G>> toTrellised(
            Map<GroupByKey<T, G>, BarChartCalculationObject<T>> barChart) {

        final Map<List<TrellisOption<T, G>>, ? extends List<? extends OutputBarChartData>> trellisedBarChart =
                toTrellisedBarChart(barChart).stream().collect(Collectors.toMap(TrellisedBarChart::getTrellisedBy, TrellisedBarChart::getData));
        if (trellisedBarChart.isEmpty()) {
            return Collections.emptyList();
        }
        trellisedBarChart.values().forEach((bar) -> bar.forEach(bars -> {
            List<String> categories = bars.getCategories();
            if (categories != null) {
                for (int i = 0; i < categories.size(); i++) {
                    String category = categories.get(i);
                    if (bars.getSeries().size() <= i || !Objects.equals(category, (bars.getSeries().get(i).getCategory()))) {
                        bars.getSeries().add(i, new OutputBarChartEntry(category, i + 1, 0.0d, 0));
                    } else {
                        bars.getSeries().get(i).setRank(i + 1);
                    }

                }
            }
        }));
        return trellisedBarChart.entrySet().stream().map(e -> new TrellisedBarChart<>(e.getKey(), e.getValue())).collect(toList());
    }

    private <T extends HasStringId & HasSubject, G extends Enum<G> & GroupByOption<T>> List<TrellisedBarChart<T, G>> toTrellisedBarChart(
            Map<GroupByKey<T, G>, BarChartCalculationObject<T>> barChart) {
        Map<GroupByKey<T, G>, Map<GroupByKey<T, G>, BarChartCalculationObject<T>>> groupedByTrellis = barChart.entrySet()
                .stream().collect(Collectors.groupingBy(
                        e -> e.getKey().limitedByTrellisOptions(),
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
                ));
        final Set<Bin<? extends Comparable<?>>> xCategories = barChart.keySet().stream().map(k -> k.getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS))
                .distinct().filter(e -> e instanceof Bin && !(e instanceof EmptyBin)).map(e -> (Bin<? extends Comparable<?>>) e).collect(Collectors.toSet());
        final List<? super Bin<? extends Comparable<?>>> binCategories = Attributes.getBinCategories(xCategories);
        return groupedByTrellis.entrySet().stream().map(groupByTrellisEntry -> {
            final List<TrellisOption<T, G>> trellisOptions = groupByTrellisEntry.getKey().getTrellisByValues().entrySet().stream()
                    .map(option -> TrellisOption.of(option.getKey(), option.getValue())).collect(Collectors.toList());
            Map<GroupByKey<T, G>, Map<GroupByKey<T, G>, BarChartCalculationObject<T>>> groupByColorBy = groupByTrellisEntry.getValue().entrySet().stream()
                    .collect(Collectors.groupingBy(
                            e -> e.getKey().limitedBySettings(COLOR_BY),
                            Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
                    ));
            final List<BarChartData> barChartDataList = groupByColorBy.entrySet().stream().map(groupByColorByEntry -> {
                final Object colorByValue = groupByColorByEntry.getKey().getValue(COLOR_BY);
                List<BarChartEntry<T>> barChartEntries = groupByColorByEntry.getValue().entrySet().stream()
                        .map(e -> new BarChartEntry<T>(getDefaultedGroupName(e.getKey().getValue(X_AXIS)),
                                e.getValue().getValue(), e.getValue().getTotalSubject(), e.getValue().getEventSet()))
                        .collect(Collectors.toList());
                return new BarChartData(
                        getDefaultedGroupName(colorByValue),
                        new ArrayList<>(binCategories),
                        barChartEntries);
            }).sorted().collect(Collectors.toList());
            return new TrellisedBarChart<>(trellisOptions, collectToOutputData(barChartDataList));
        }).collect(Collectors.toList());
    }


    protected List<OutputBarChartData> collectToOutputData(List<BarChartData> allData) {
        //fill output data with ranks
        return allData.stream().sorted().map(bar -> {
            List<OutputBarChartEntry> seriesWithRank = bar.getSeries().stream()
                    .filter(e -> !(e.getCategory() instanceof EmptyBin))
                    .sorted()
                    .map(e -> new OutputBarChartEntry(e, 0)).collect(toList());

            final Object name = bar.getName();
            String nameStr;
            if (name instanceof ImmutablePair) {
                nameStr = ((ImmutablePair) name).getLeft() == null ? "" : ((ImmutablePair) name).getLeft().toString();
                String colorIndex = ((ImmutablePair) name).getRight() == null ? "" : ((ImmutablePair) name).getRight().toString();

                if ("".equals(nameStr)) {
                    if ("".equals(colorIndex) || "0".equals(colorIndex)) {
                        nameStr = "(Empty)";
                    } else {
                        nameStr = colorIndex;
                    }
                }
            } else {
                nameStr = name == null ? "" : name.toString();
            }

            // TODO: set return type to OutputBarChartData when coloring will be
            //  moved to the front fully.
            //  (Correct behavior should be returning OutputBarChartData (as it was before)
            //  and color should be set by front-end, but it was decided to set color here,
            //  because it would take a lot off effort and time, which we did not have)
            return new ColoredOutputBarChartData(
                    nameStr,
                    bar.getCategories().stream()
                            .filter(e -> e instanceof Bin && !(e instanceof EmptyBin))
                            .sorted()
                            .map(Object::toString)
                            .collect(toList()),
                    seriesWithRank,
                    ColoringService.COLORS[1]);
        }).filter(d -> !d.getSeries().isEmpty()).collect(toList());
    }

    private Object getDefaultedGroupName(Object colorByValue) {
        return colorByValue == null ? "All" : colorByValue;
    }

}
