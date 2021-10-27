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

import com.acuity.visualisations.common.aspect.TimeMe;
import com.acuity.visualisations.rawdatamodel.aspect.ValidateChartOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByAttributes;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.plots.BoxPlotOutlier;
import com.acuity.visualisations.rawdatamodel.vo.plots.BoxplotCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.RangeChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.plots.ShiftPlotCalculationObject;
import org.apache.commons.math3.analysis.function.Sqrt;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import org.apache.commons.math3.stat.descriptive.rank.Median;
import org.apache.commons.math3.stat.descriptive.rank.Percentile;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.NAME;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.ALL;
import static com.acuity.visualisations.rawdatamodel.util.Constants.ROUNDING_PRECISION;
import static com.acuity.visualisations.rawdatamodel.util.ObjectUtil.keysEquals;
import static com.acuity.visualisations.rawdatamodel.util.ObjectUtil.toStringNormalizingNumbers;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * Created by knml167 on 8/23/2017.
 */
@Service
public class StatsPlotService<T extends HasStringId & HasSubject, G extends Enum<G> & GroupByOption<T>>
        implements SimpleSelectionSupportService<T, G> {

    @TimeMe
    @ValidateChartOptions(
            required = {ChartGroupByOptions.ChartGroupBySetting.Y_AXIS},
            optional = {X_AXIS})
    public Map<GroupByKey<T, G>, BoxplotCalculationObject> getBoxPlot(
            ChartGroupByOptions<T, G> settings, FilterResult<T> filtered) {
        //grouping all events by trellis + X_AXIS option
        final Map<GroupByKey<T, G>, Collection<T>> groupedEvents = GroupByAttributes.group(
                filtered.getFilteredResult(),
                settings.limitedBySettings(X_AXIS));
        return groupedEvents.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> getBoxPlotTransformation(settings).apply(e.getValue())));
    }

    private Function<Collection<T>, BoxplotCalculationObject> getBoxPlotTransformation(ChartGroupByOptions<T, G> settings) {
        return (Collection<T> events) -> {
            Percentile percentile = new Percentile().withEstimationType(Percentile.EstimationType.R_7);
            final BoxplotCalculationObject.BoxplotCalculationObjectBuilder builder = BoxplotCalculationObject.builder();
            final Map<T, GroupByKey<T, G>> mapped = events.stream().collect(toMap(e -> e, e -> Attributes.get(settings, e)))
                    .entrySet().stream().filter(e -> e.getValue().getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS) instanceof Double)
                    .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
            final double[] yValues = mapped.entrySet().stream().map(e -> e.getValue().getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS))
                    .mapToDouble(e -> (Double) e).sorted().toArray();
            if (yValues.length > 0) {


                double median = percentile.evaluate(yValues, 50);
                double upperQuartile = percentile.evaluate(yValues, 75);
                double lowerQuartile = percentile.evaluate(yValues, 25);
                double tukeyIqr = (1.5 * (upperQuartile - lowerQuartile));

                double lowerWhisker = Arrays.stream(yValues)
                        .filter(y -> y >= lowerQuartile - tukeyIqr)
                        .min()
                        .orElse(lowerQuartile - tukeyIqr);
                double upperWhisker = Arrays.stream(yValues)
                        .filter(y -> y <= upperQuartile + tukeyIqr)
                        .max()
                        .orElse(upperQuartile + tukeyIqr);

                final Set<BoxPlotOutlier> outliers = mapped.entrySet().stream().filter(
                        e -> (Double) e.getValue().getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS) < lowerWhisker
                                || (Double) e.getValue().getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS) > upperWhisker)
                        .map(e -> new BoxPlotOutlier(round((Double) e.getValue().getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS), 2),
                                e.getKey().getSubjectId())).collect(toSet());
                builder.subjectCount(mapped.keySet().stream().map(e -> e.getSubjectId()).distinct().count());
                builder.eventCount((long) mapped.size());
                builder.median(round(median, 2));
                builder.upperQuartile(round(upperQuartile, 2));
                builder.lowerQuartile(round(lowerQuartile, 2));
                builder.lowerWhisker(round(lowerWhisker, 2));
                builder.upperWhisker(round(upperWhisker, 2));
                builder.outliers(outliers);
            } else {
                builder.outliers(Collections.emptySet());
            }
            return builder.build();
        };
    }

    private static Double round(Double value, int scale) {
        return value == null ? null : Precision.round(value, scale);
    }

    @TimeMe
    @ValidateChartOptions(
            required = {ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, X_AXIS},
            optional = ChartGroupByOptions.ChartGroupBySetting.UNIT)
    public Map<GroupByKey<T, G>, ShiftPlotCalculationObject> getShiftPlot(
            ChartGroupByOptions<T, G> settings, FilterResult<T> filtered) {
        //grouping all events by trellis + X_AXIS option
        final Map<GroupByKey<T, G>, Collection<T>> groupedEvents = GroupByAttributes.group(
                filtered.getFilteredResult(),
                settings.limitedBySettings(X_AXIS));
        //applying transformation to each group
        return groupedEvents.entrySet().stream().collect(toMap(Map.Entry::getKey,
                e -> getShiftPlotTransformation(settings).apply(e.getValue())));
    }

    private Function<Collection<T>, ShiftPlotCalculationObject> getShiftPlotTransformation(ChartGroupByOptions<T, G> settings) {
        return (Collection<T> events) -> {
            final ShiftPlotCalculationObject.ShiftPlotCalculationObjectBuilder builder = ShiftPlotCalculationObject.builder();
            final List<GroupByKey<T, G>> mapped = events.stream().map(e -> Attributes.get(settings, e))
                    .filter(e -> e.getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS) instanceof Double)
                    .collect(toList());
            //we're taking simply first found unit, leaving consistency on responsibility of chart developer
            final Object unit = mapped.stream()
                    .map(e -> e.getValue(ChartGroupByOptions.ChartGroupBySetting.UNIT))
                    .filter(Objects::nonNull)
                    .findFirst().orElse(null);
            final double[] yValues = mapped.stream().mapToDouble(e -> (Double) e.getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS)).sorted().toArray();

            if (yValues.length > 0) {
                final double min = yValues[0];
                final double max = yValues[yValues.length - 1];

                builder.high(round(max, 2));
                builder.low(round(min, 2));
            }
            builder.unit(unit);
            return builder.build();
        };
    }

    @TimeMe
    public SelectionDetail getRangedSelectionDetails(FilterResult<T> filtered, ChartSelection<T, G, ChartSelectionItemRange<T, G, Double>> selection) {
        //to speed up we first filter on trellises found in selection
        final Set<Map<G, Object>> distinctTrellises = selection.getSelectionItems().stream()
                .map(ChartSelectionItem::getSelectedTrellises).collect(toSet());
        final ChartGroupByOptions<T, G> trellisSettings = selection.getSettings().limitedByTrellisOptions();
        final Map<GroupByKey<T, G>, List<T>> trellisGroups = filtered.getFilteredResult().stream()
                .collect(groupingBy(e -> Attributes.get(trellisSettings, e)));
        final Map<GroupByKey<T, G>, Collection<T>> groupedEvents = GroupByAttributes.group(trellisGroups.entrySet().stream()
                .filter(e -> distinctTrellises.stream().anyMatch(t -> keysEquals(e.getKey().getTrellisByValues(), t)))
                .flatMap(e -> e.getValue().stream()).collect(toList()), selection.getSettings());

        //then filtering matched trellisses items on selection details
        final Predicate<GroupByKey<T, G>> rangeSelectionMatchPredicate = getRangeSelectionMatchPredicate(selection);
        final List<T> matchedItems = groupedEvents.entrySet().parallelStream()
                .filter(e -> rangeSelectionMatchPredicate.test(e.getKey()))
                .flatMap(e -> e.getValue().stream()).collect(toList());
        //noinspection Convert2MethodRef
        final SelectionDetail selectionDetail = SelectionDetail.builder()
                .eventIds(matchedItems.stream().map(T::getId).collect(toSet()))
                //don't use T::getSubjectId here, see https://stackoverflow.com/questions/27031244/lambdaconversionexception-with-generics-jvm-bug
                .subjectIds(matchedItems.stream().map(t -> t.getSubjectId()).distinct().collect(toSet()))
                .totalEvents(filtered.getAllEvents().size())
                .totalSubjects(filtered.getPopulationFilterResult().size())
                .build();
        return selectionDetail;
    }

    private Predicate<GroupByKey<T, G>> getRangeSelectionMatchPredicate(
            ChartSelection<T, G, ChartSelectionItemRange<T, G, Double>> selection) {
        final List<ChartSelectionItemRange<T, G, Double>> normalized = selection.getSelectionItems().stream().map(i -> {
            final Map<G, Object> selectedTrellises = i.getSelectedTrellises().entrySet().stream()
                    .collect(HashMap::new, (m, v) -> m.put(v.getKey(), toStringNormalizingNumbers(v.getValue())), HashMap::putAll);

            final Map<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = i.getSelectedItems().entrySet().stream()
                    .collect(HashMap::new, (m, v) -> m.put(v.getKey(), toStringNormalizingNumbers(v.getValue())), HashMap::putAll);
            // .collect(Collectors.toMap(e -> e.getKey(), e -> toStringNormalizingNumbers(e.getValue()))); this fails with nulls
            return ChartSelectionItemRange.of(selectedTrellises, selectedItems, i.getRange().getMinimum(), i.getRange().getMaximum());
        }).collect(toList());
        //need to change something here some day, this is a workaround
        return e -> normalized.parallelStream().anyMatch(selectionItem -> {
            final Object yValue = e.getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS);
            return yValue instanceof Double && keysEquals(selectionItem.getSelectedTrellises(), e.getTrellisByValues())
                    && keysEquals(selectionItem.getSelectedItems(), e.limitedBySettings(X_AXIS, SERIES_BY).getValues())
                    && selectionItem.getRange().contains((Double) yValue);
        }) || selection.getSelectionItems().parallelStream().anyMatch(selectionItem -> {
            final Object yValue = e.getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS);
            return yValue instanceof Double && keysEquals(selectionItem.getSelectedTrellises(), e.getTrellisByValues())
                    && keysEquals(selectionItem.getSelectedItems(), e.limitedBySettings(X_AXIS, SERIES_BY).getValues())
                    && selectionItem.getRange().contains((Double) yValue);
        });
    }

    @Override
    public List<T> getMatchedItems(Collection<T> filtered, ChartSelection<T, G, ChartSelectionItem<T, G>> selection) {
        final Map<GroupByKey<T, G>, Collection<T>> groupedEvents = GroupByAttributes.group(filtered,
                selection.getSettings().limitedBySettings(X_AXIS, SERIES_BY));
        final Predicate<GroupByKey<T, G>> selectionMatchPredicate = getSelectionMatchPredicate(selection);
        return groupedEvents.entrySet().parallelStream()
                .filter(e -> selectionMatchPredicate.test(e.getKey()))
                .flatMap(e -> e.getValue().stream()).collect(toList());
    }

    @TimeMe
    @ValidateChartOptions(
            required = {ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, X_AXIS},
            optional = {ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, NAME})
    public Map<GroupByKey<T, G>, RangeChartCalculationObject> getRangePlot(
            ChartGroupByOptions<T, G> settings, FilterResult<T> filtered) {
        //grouping all events by trellis + X_AXIS and SERIES_BY option
        final Map<GroupByKey<T, G>, Collection<T>> groupedEvents = GroupByAttributes.group(
                filtered.getFilteredResult(),
                settings.limitedBySettings(X_AXIS, SERIES_BY));
        //applying transformation to each group
        return groupedEvents.entrySet().stream().collect(toMap(Map.Entry::getKey,
                e -> getRangePlotTransformation(settings).apply(e.getValue())));
    }

    private Function<Collection<T>, RangeChartCalculationObject> getRangePlotTransformation(ChartGroupByOptions<T, G> settings) {
        return (Collection<T> events) -> {
            final RangeChartCalculationObject.RangeChartCalculationObjectBuilder builder = RangeChartCalculationObject.builder();
            final List<GroupByKey<T, G>> mapped = events.stream().map(e -> Attributes.get(settings, e))
                    .filter(e -> e.getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS) instanceof Double)
                    .collect(toList());
            final double[] yValues = mapped.stream()
                    .mapToDouble(e -> (Double) e.getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS))
                    .sorted()
                    .toArray();

            builder.dataPoints(events.size());
            if (yValues.length > 0) {
                Double mean = calculateMean(yValues);
                Double median = calculateMedian(yValues);
                Double standardDeviation = calculateStandardDeviation(yValues);
                Double standardError = calculateStandardError(standardDeviation, events.size());
                Double min = yValues[0];
                Double max = yValues[yValues.length - 1];

                builder
                        .stdErr(standardError)
                        .min(min)
                        .max(max)
                        .stdDev(standardDeviation)
                        .median(median)
                        .mean(mean);
            }
            ChartGroupByOptions.GroupByOptionAndParams<T, G> nameParam = settings.getOptions()
                    .getOrDefault(NAME, settings.getOptions().get(SERIES_BY));
            String name = nameParam == null ? ALL
                    : events.stream().map(e -> Attributes.get(nameParam, e).toString()).distinct().sorted()
                    .collect(joining(", "));
            return builder.name(name).build();
        };
    }

    private Double calculateMean(double[] yValues) {
        return round(new Mean().evaluate(yValues), ROUNDING_PRECISION);
    }

    private Double calculateMedian(double[] yValues) {
        return round(new Median().evaluate(yValues), ROUNDING_PRECISION);
    }

    private Double calculateStandardDeviation(double[] yValues) {
        return round(new StandardDeviation().evaluate(yValues), ROUNDING_PRECISION);
    }

    private Double calculateStandardError(double standardDeviation, double numberOfDataPoints) {
        return round(standardDeviation / new Sqrt().value(numberOfDataPoints), ROUNDING_PRECISION);
    }

    private double[] getYValues(Collection<T> events, ChartGroupByOptions<T, G> settings) {
        final List<GroupByKey<T, G>> mapped = events.stream().map(e -> Attributes.get(settings, e))
                .filter(e -> e.getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS) instanceof Double)
                .collect(Collectors.toList());
        return mapped.stream()
                .mapToDouble(e -> (Double) e.getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS)).sorted().toArray();
    }

    @TimeMe
    @ValidateChartOptions(
            required = {ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, X_AXIS},
            optional = {ChartGroupByOptions.ChartGroupBySetting.SERIES_BY})
    public Map<GroupByKey<T, G>, RangeChartCalculationObject> getMedianRangePlot(
            ChartGroupByOptions<T, G> settings, FilterResult<T> filtered) {
        //grouping all events by trellis + X_AXIS and SERIES_BY option
        final Map<GroupByKey<T, G>, Collection<T>> groupedEvents = GroupByAttributes.group(
                filtered.getFilteredResult(),
                settings.limitedBySettings(X_AXIS, SERIES_BY));
        //applying transformation to each group
        return groupedEvents.entrySet().stream().collect(toMap(Map.Entry::getKey,
                e -> getMedianRangePlotTransformation(settings).apply(e.getValue())));
    }

    private Function<Collection<T>, RangeChartCalculationObject> getMedianRangePlotTransformation(ChartGroupByOptions<T, G> settings) {
        return (Collection<T> events) -> {
            final RangeChartCalculationObject.RangeChartCalculationObjectBuilder builder = RangeChartCalculationObject.builder();
            double[] yValues = getYValues(events, settings);

            builder.dataPoints(events.size());
            if (yValues.length > 0) {
                Double median = new Median().evaluate(yValues);
                Double mean = new Mean().evaluate(yValues);
                Double min = yValues[0];
                Double max = yValues[yValues.length - 1];

                builder
                        .min(round(min, 2))
                        .max(round(max, 2))
                        .mean(round(mean, 2))
                        .median(round(median, 2));
            }
            return builder.build();
        };
    }
}
