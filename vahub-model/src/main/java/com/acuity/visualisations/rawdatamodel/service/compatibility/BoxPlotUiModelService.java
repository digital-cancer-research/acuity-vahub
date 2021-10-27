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
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBoxplotEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.BoxplotCalculationObject;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@SuppressWarnings("squid:CommentedOutCodeLine")
public class BoxPlotUiModelService {

    public <T, G extends Enum<G> & GroupByOption<T>> List<TrellisedBoxPlot<T, G>> toTrellisedBoxPlot(
            Map<GroupByKey<T, G>, BoxplotCalculationObject> boxplot) {

        final Map<GroupByKey<T, G>, Map<GroupByKey<T, G>, BoxplotCalculationObject>> groupedByTrellis = boxplot.entrySet()
                .stream().collect(Collectors.groupingBy(
                        e -> e.getKey().limitedByTrellisOptions(),
                        Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)
                ));

        List<?> xCategories;
        boolean doesEmptyExist;
        if (isAxisBinned(boxplot.keySet())) {
            final Set<Object> xAxisOptions = boxplot.keySet().stream()
                    .map(k -> k.getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS)).collect(Collectors.toSet());

            doesEmptyExist = xAxisOptions.stream().anyMatch(e -> e instanceof EmptyBin);

            Set<Bin<? extends Comparable<?>>> xBinCategories = xAxisOptions.stream().filter(e -> e instanceof Bin && !(e instanceof EmptyBin))
                    .map(e -> (Bin<? extends Comparable<?>>) e).collect(Collectors.toSet());

            xCategories = Attributes.getBinCategories(xBinCategories);
        } else {
            Set<Object> categories = boxplot.keySet().stream()
                    .map(k -> k.getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS))
                    .collect(toSet());

            doesEmptyExist = categories.contains(Attributes.DEFAULT_EMPTY_VALUE) || categories.contains(null)
                    || categories.stream().anyMatch(v -> Attributes.DEFAULT_EMPTY_VALUE.equals(v.toString()));

            xCategories = getXCategories(categories);
        }
        return groupedByTrellis.entrySet().stream().map(groupByTrellisEntry -> {
            final List<TrellisOption<T, G>> trellisOptions = groupByTrellisEntry.getKey().getTrellisByValues().entrySet().stream()
                    .map(option -> TrellisOption.of(option.getKey(), option.getValue())).collect(Collectors.toList());

            List<BoxplotCalculationObject> emptyXAxisCalculation = new ArrayList<>();
            final Map<Object, BoxplotCalculationObject> mapByXCategory = groupByTrellisEntry.getValue().entrySet().stream()
                    .filter(e -> {
                        final Object value = e.getKey().getValue(X_AXIS);
                        if (doesEmptyCategoryExist(value)) {
                            emptyXAxisCalculation.add(e.getValue());
                        }
                        return e != null && !Attributes.DEFAULT_EMPTY_VALUE.equals(value == null ? null : value.toString());
                    })
                    .collect(Collectors.toMap(e -> e.getKey().getValue(X_AXIS), e -> e.getValue()));

                  List<OutputBoxplotEntry> stats = xCategories.stream()
                    .filter(getXCategoriesFilter(mapByXCategory))
                    .map(xAxisValue -> {
                        final BoxplotCalculationObject statEntry = mapByXCategory.get(xAxisValue);
                        Double xRank = getContinuousAxisValueRank(xAxisValue, () -> xCategories.indexOf(xAxisValue));
                        return OutputBoxplotEntry.of(xAxisValue == null ? Attributes.DEFAULT_EMPTY_VALUE : xAxisValue.toString(), xRank, statEntry);
                    }).sorted(Comparator.comparing(OutputBoxplotEntry::getXRank)).collect(toList());

            if (doesEmptyExist && isNeededToShowEmptyCategories() && !emptyXAxisCalculation.isEmpty()) {
                int stasSize = stats.size();
                stats.add(OutputBoxplotEntry.of(
                        Attributes.DEFAULT_EMPTY_VALUE,
                        stasSize == 0 ? 0.0 : stats.get(stasSize - 1).getXRank() + 1,
                        emptyXAxisCalculation.get(0)));
            }
            return new TrellisedBoxPlot<>(trellisOptions, stats);
        }).sorted(Comparator.comparing(TrellisedChart::getTrellisByString)).collect(Collectors.toList());
    }

    Predicate<Object> getXCategoriesFilter(Map<Object, BoxplotCalculationObject> mapByXCategory) {
        return xAxisValue -> xAxisValue instanceof Bin || mapByXCategory.get(xAxisValue) != null;
    }

    List getXCategories(Set<Object> categories) {
        return categories.stream().filter(e -> e != null
                && !Attributes.DEFAULT_EMPTY_VALUE.equals(e.toString())).sorted().collect(Collectors.toList());
    }

    boolean isNeededToShowEmptyCategories() {
        return true;
    }

    private <T, G extends Enum<G> & GroupByOption<T>> boolean isAxisBinned(Collection<GroupByKey<T, G>> items) {
        return items.stream().anyMatch(k -> k.getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS) instanceof Bin);
    }

    private boolean isContinuousAxisValue(Object value) {
        return value instanceof Number
                || (value instanceof IntBin && ((IntBin) value).getSize() == 1);
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

    private boolean doesEmptyCategoryExist(Object value) {
        return value == null || Attributes.DEFAULT_EMPTY_VALUE.equals(value) || EmptyBin.empty().equals(value)
                || Attributes.DEFAULT_EMPTY_VALUE.equals(value.toString());
    }
}
