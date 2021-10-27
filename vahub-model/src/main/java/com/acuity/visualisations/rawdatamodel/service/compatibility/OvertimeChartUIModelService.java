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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LimitableBySettings;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.AlphanumEmptyLastComparator;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.ObjectUtil;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.ColoredOutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputOvertimeData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputOvertimeLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartData;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartEntry;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@Primary
public class OvertimeChartUIModelService {

    private static final String OVERTIME_LINE_NAME = "SUBJECTS";

    public OvertimeChartUIModelService(@Autowired BarChartColoringService coloringService) {
        this.coloringService = coloringService;
    }

    private BarChartColoringService coloringService;

    public <T extends HasStringId & HasSubject, G extends Enum<G> & GroupByOption<T>> List<TrellisedOvertime<T, G>> toTrellisedOvertime(
            Map<GroupByKey<T, G>, BarChartCalculationObject<T>> barChart,
            Map<GroupByKey<Subject, PopulationGroupByOptions>, BarChartCalculationObject<Subject>> lineData) {

        final Map<List<TrellisOption<T, G>>, ? extends List<? extends OutputBarChartData>> trellisedBarChart =
                toTrellisedBarChart(barChart).stream().collect(Collectors.toMap(TrellisedBarChart::getTrellisedBy, TrellisedBarChart::getData));
        List<TrellisedOvertime<T, G>> output = new ArrayList<>();

        if (trellisedBarChart.isEmpty()) {
            return output;
        }

        final Set<GroupByKey<T, G>> trellisOptions = barChart.keySet().stream().map(LimitableBySettings::limitedByTrellisOptions).collect(Collectors.toSet());

        final Set<Bin<? extends Comparable<?>>> xCategories = barChart.keySet().stream().map(k -> k.getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS))
                .distinct().filter(e -> e instanceof Bin && !(e instanceof EmptyBin)).map(e -> (Bin<? extends Comparable<?>>) e).collect(Collectors.toSet());


        final List<? super Bin<? extends Comparable<?>>> binCategories = Attributes.getBinCategories(xCategories);

        Map<GroupByKey<Subject, PopulationGroupByOptions>, Map<Object, BarChartEntry<Subject>>> lineByTrellisAndXCategory =
                lineData.entrySet().stream().collect(
                        groupingBy(e -> e.getKey().limitedByTrellisOptions(),
                                toMap(e -> e.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS),
                                        e -> new BarChartEntry<>(e.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS),
                                                e.getValue().getValue(), e.getValue().getTotalSubject(), e.getValue().getEventSet()))
                        ));

        final Map<GroupByKey<Subject, PopulationGroupByOptions>, List<OutputBarChartEntry>> lineSeriesWithRankByTrellis =
                trellisOptions.stream().flatMap(trellis -> binCategories.stream().sorted()
                        .map(bin -> new ImmutableTriple<>(trellis.limitedByPopulationTrellisOptions(), bin, binCategories.indexOf(bin) + 1)))
                        .map(c ->
                                new ImmutablePair<>(c.getLeft(), new OutputBarChartEntry(
                                        lineByTrellisAndXCategory.getOrDefault(c.getLeft(), new HashMap<>()).getOrDefault(c.getMiddle(),
                                                new BarChartEntry<>(c.getMiddle(), 0d)), c.getRight()))
                        ).collect(groupingBy(ImmutablePair::getLeft, mapping(Pair::getRight, toList())));

        Map<GroupByKey<Subject, PopulationGroupByOptions>, OutputOvertimeLineChartData> lineByTrellis =
                lineSeriesWithRankByTrellis.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                        e -> new OutputOvertimeLineChartData(OVERTIME_LINE_NAME, e.getValue(), ColoringService.Colors.BLACK.getCode())));

        final List<TrellisedOvertime<T, G>> charts = trellisOptions.stream().map(
                trellis -> TrellisedOvertime.of(trellis,
                        new OutputOvertimeData(
                                ObjectUtil.optionalSingletonList(lineByTrellis.get(trellis.limitedByPopulationTrellisOptions())),
                                binCategories.stream().map(Object::toString).collect(Collectors.toList()),
                                trellisedBarChart.get(trellis.getTrellisByValues().entrySet().stream()
                                        .map(t -> TrellisOption.of(t.getKey(), t.getValue())).collect(Collectors.toList())).stream()
                                        .sorted(Comparator.comparing(OutputBarChartData::getName,
                                                AlphanumEmptyLastComparator.getInstance())).collect(toList())
                        ))
        ).filter(c -> !c.getData().getSeries().isEmpty()).sorted(Comparator.comparing(TrellisedChart::getTrellisByString)).collect(Collectors.toList());

        output.addAll(charts);
        return output;
    }

    private <T extends HasSubject & HasStringId, G extends Enum<G> & GroupByOption<T>> List<TrellisedBarChart<T, G>> toTrellisedBarChart(
            Map<GroupByKey<T, G>, BarChartCalculationObject<T>> barChart) {
        final Map<GroupByKey<T, G>, Map<GroupByKey<T, G>, BarChartCalculationObject<T>>> groupedByTrellis = barChart.entrySet()
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
        final AtomicInteger colorCounter = new AtomicInteger(0);

        final List<? super Bin<? extends Comparable<?>>> allCategories = Attributes.getBinCategories(allData.stream()
                .flatMap(e -> e.getCategories().stream())
                .filter(e -> e instanceof Bin && !(e instanceof EmptyBin)).map(e -> (Bin<? extends Comparable<?>>) e)
                .collect(Collectors.toCollection(TreeSet::new)));

        //fill output data with ranks
        return allData.stream().sorted().map(bar -> {
            List<OutputBarChartEntry> seriesWithRank = bar.getSeries().stream()
                    .filter(e -> !(e.getCategory() instanceof EmptyBin))
                    .sorted()
                    .map(e -> new OutputBarChartEntry(e, allCategories.indexOf(e.getCategory()) + 1)).collect(toList());

            final Object name = bar.getName();
            final String nameStr = name == null ? "" : name.toString();
            return new ColoredOutputBarChartData(nameStr,
                    bar.getCategories().stream().filter(e -> e instanceof Bin && !(e instanceof EmptyBin)).sorted().map(Object::toString).collect(toList()),
                    seriesWithRank,
                    getColor(colorCounter.incrementAndGet(), nameStr));
        }).filter(d -> !d.getSeries().isEmpty()).collect(toList());
    }

    protected String getColor(int i, String nameStr) {
        return coloringService.getColor(i, nameStr);
    }

    private Object getDefaultedGroupName(Object colorByValue) {
        return colorByValue == null ? "All" : colorByValue;
    }

}
