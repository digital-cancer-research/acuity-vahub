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
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByAttributes;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import org.apache.commons.math3.util.Precision;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
public class BarChartService<T extends HasStringId & HasSubject, G extends Enum<G> & GroupByOption<T>>
        implements SimpleSelectionSupportService<T, G> {

    @TimeMe
    @ValidateChartOptions(optional = {ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, ChartGroupByOptions.ChartGroupBySetting.X_AXIS})
    public Map<GroupByKey<T, G>, BarChartCalculationObject<T>> getBarChart(
            ChartGroupByOptions<T, G> settings, CountType countType, FilterResult<T> filtered) {
        //Main thing to do - group all events by all chart grouping options (trellis, color by, x-axis, etc)
        final Map<GroupByKey<T, G>, Collection<T>> groupedFilteredEvents = GroupByAttributes.group(filtered.getFilteredResult(), settings);
        final Supplier<Map<GroupByKey<T, G>, Collection<T>>> groupedByXAxisOptionEventCount = Suppliers.memoize(
                () -> GroupByAttributes.group(filtered.getFilteredResult(), settings.limitedBySettings(ChartGroupByOptions.ChartGroupBySetting.X_AXIS)));
        // Need to distinct subject by color-by groups through x-axis options groups, as there can be situations when one subject is in multiple color-by
        // option groups in one x-axis option group.
        final Supplier<Map<GroupByKey<T, G>, Integer>> groupedByXAxisByOptionSubjectCount = Suppliers.memoize(
                () -> groupedByXAxisOptionEventCount.get().entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, (Map.Entry<GroupByKey<T, G>, Collection<T>> e) -> {
                            Map<GroupByKey<T, G>, Collection<T>> xAxisEventsGroupedByColorBy = GroupByAttributes.group(e.getValue(),
                                    settings.limitedBySettings(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY));
                            return xAxisEventsGroupedByColorBy.entrySet().stream()
                                    .mapToInt(colorGroup -> (int) colorGroup.getValue().stream().map(t -> t.getSubject()).distinct().count())
                                    .sum();
                        })));

        //Barchart specific pre-processing - groupedByTrellisEvents and groupedByTrellisSubjects, need for specific calculations
        //This gets events grouped by all trellis options
        final Supplier<Map<GroupByKey<T, G>, Collection<T>>> groupedByTrellisEvents = Suppliers.memoize(() -> GroupByAttributes
                .group(filtered.getFilteredResult(), settings.limitedByTrellisOptions()));

        //This gets subjects grouped by only population trellis options
        final Supplier<Map<GroupByKey<Subject, PopulationGroupByOptions>, Collection<Subject>>> groupedByTrellisSubjects =
                Suppliers.memoize(() -> GroupByAttributes.group(filtered.getPopulationFilterResult().getFilteredResult(),
                        settings.limitedByPopulationTrellisOptions()));

        //applying transformation to each group
        return groupedFilteredEvents.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey,
                e -> getBarChartTransformation(filtered, groupedByTrellisEvents, groupedByTrellisSubjects,
                        groupedByXAxisOptionEventCount, groupedByXAxisByOptionSubjectCount, countType)
                        .apply(e.getKey(), e.getValue())));
    }

    private BiFunction<GroupByKey<T, G>, Collection<T>, BarChartCalculationObject<T>> getBarChartTransformation(
            FilterResult<T> filtered,
            Supplier<Map<GroupByKey<T, G>, Collection<T>>> groupedByTrellisEvents,
            Supplier<Map<GroupByKey<Subject, PopulationGroupByOptions>, Collection<Subject>>> groupedByTrellisSubjects,
            Supplier<Map<GroupByKey<T, G>, Collection<T>>> groupedByXAxisOptionEventCount,
            Supplier<Map<GroupByKey<T, G>, Integer>> groupedByXAxisAndColorByOptionSubjectCount,
            CountType countType) {
        return (GroupByKey<T, G> group, Collection<T> events) -> {
            Double value = null;
            Set<String> subjectsCodes = events.stream().map(t -> t.getSubjectCode()).collect(Collectors.toSet());
            final double eventCount = (double) events.size();
            final double totalEventsNumber = (double) filtered.size();
            //don't use T::getSubject here, see https://stackoverflow.com/questions/27031244/lambdaconversionexception-with-generics-jvm-bug
            final double subjectCount = (double) events.stream().map(t -> t.getSubject()).distinct().count();
            final double totalSubjectCount = (double) filtered.getPopulationFilterResult().size();
            switch (countType) {
                case CUMULATIVE_COUNT_OF_EVENTS:
                case COUNT_OF_EVENTS:
                    value = eventCount;
                    break;
                case CUMULATIVE_COUNT_OF_SUBJECTS:
                case COUNT_OF_SUBJECTS:
                    value = subjectCount;
                    break;
                case PERCENTAGE_OF_ALL_EVENTS:
                    value = totalEventsNumber == 0 ? 0 : (eventCount * 100 / totalEventsNumber);
                    break;
                case PERCENTAGE_OF_ALL_SUBJECTS:
                    value = totalSubjectCount == 0 ? 0 : (subjectCount * 100 / totalSubjectCount);
                    break;
                case PERCENTAGE_OF_EVENTS_WITHIN_PLOT:
                    int eventsWithinPlot = groupedByTrellisEvents.get().get(group.limitedByTrellisOptions()).size();
                    value = eventsWithinPlot == 0 ? 0 : (eventCount * 100 / eventsWithinPlot);
                    break;
                case PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT:
                    int subjectsWithinPlot = groupedByTrellisSubjects.get().get(
                            group.limitedByPopulationTrellisOptions()).size();
                    value = subjectsWithinPlot == 0 ? 0 : (subjectCount * 100 / subjectsWithinPlot);
                    break;
                case PERCENTAGE_OF_EVENTS_100_STACKED:
                    int events100Stacked = groupedByXAxisOptionEventCount.get()
                            .get(group.limitedBySettings(ChartGroupByOptions.ChartGroupBySetting.X_AXIS)).size();
                    value = events100Stacked == 0 ? 0 : (eventCount * 100 / events100Stacked);
                    break;
                case PERCENTAGE_OF_SUBJECTS_100_STACKED:
                case PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED:

                    int subjects100Stacked = groupedByXAxisAndColorByOptionSubjectCount.get()
                            .get(group.limitedBySettings(ChartGroupByOptions.ChartGroupBySetting.X_AXIS));
                    value = subjects100Stacked == 0 ? 0 : (subjectCount * 100 / subjects100Stacked);
                    break;
                default:
                    break;
            }
            return new BarChartCalculationObject<T>(subjectsCodes, events, (int) eventCount, Precision.round(value, 2), (int) subjectCount);
        };
    }
}
