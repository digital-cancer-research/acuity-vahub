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

import com.acuity.visualisations.rawdatamodel.vo.compatibility.ColoredOutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartData;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartEntry;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@Primary
public class ColoredBarChartUIModelService extends BarChartUIModelService {

    protected BarChartColoringService coloringService;

    public ColoredBarChartUIModelService(BarChartColoringService coloringService) {
        this.coloringService = coloringService;
    }

    /**
     * Fill output data with ranks, sorted categories and sorted series.
     * Sort {@link BarChartData#getSeries()} according to the order of
     * {@link BarChartData#getCategories()}.
     * @param allData list of BarChartData
     * @return list of {@link ColoredOutputBarChartData} with sorted series
     * and selected color.
     */
    @Override
    List<OutputBarChartData> collectToOutputData(List<BarChartData> allData, Supplier<Double> totalSubjectsCountSupplier) {
        return IntStream
                .range(0, allData.size())
                .mapToObj((int i) -> {
                    BarChartData bar = allData.get(i);
                    Map<String, Integer> categoriesWithIndexes = IntStream
                            .range(0, bar.getCategories().size())
                            .boxed()
                            .collect(getLinkedHashMapCollector(bar));

                    List<OutputBarChartEntry> seriesSortedByCategoryIndex = bar.getSeries().stream()
                            .sorted(Comparator.comparing(barChartEntry -> categoriesWithIndexes.get(barChartEntry.getCategory().toString())))
                            .map(barChartEntry -> mapToOutputBarchartEntry(barChartEntry, categoriesWithIndexes, totalSubjectsCountSupplier))
                            .collect(toList());

                    final String name = bar.getName() == null ? "" : bar.getName().toString();

                    return new ColoredOutputBarChartData(
                            name,
                            new ArrayList<>(categoriesWithIndexes.keySet()),
                            seriesSortedByCategoryIndex,
                            getColor(i, name));
                })
                .collect(toList());
    }

    protected OutputBarChartEntry mapToOutputBarchartEntry(BarChartEntry barChartEntry,
                                                           Map<String, Integer> categoriesWithIndexes,
                                                           Supplier<Double> totalSubjectsCountSupplier) {
        return new OutputBarChartEntry(barChartEntry, categoriesWithIndexes.get(barChartEntry.getCategory().toString()) + 1);
    }

    /**
     * Use LinkedHashMap in order to preserve keySet order. This
     * ensures {@link ColoredOutputBarChartData#getCategories()}
     * will be in the same order as in original
     * {@link BarChartData#getCategories()}.
     */
    private Collector<Integer, ?, LinkedHashMap<String, Integer>> getLinkedHashMapCollector(BarChartData bar) {
        return toMap(
                i -> bar.getCategories().get(i).toString(),
                Function.identity(),
                (k1, k2) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", k1));
                },
                LinkedHashMap::new);
    }

    public String getColor(int i, String name) {
        return coloringService.getColor(i, name);
    }
}
