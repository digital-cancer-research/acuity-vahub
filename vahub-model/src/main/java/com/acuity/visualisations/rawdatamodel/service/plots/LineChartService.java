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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.plots.LineChartData;
import com.acuity.visualisations.rawdatamodel.vo.plots.LineChartEntry;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.NAME;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.ORDER_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.STANDARD_DEVIATION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static java.util.stream.Collectors.toMap;

/**
 * Created by knml167 on 9/26/2017.
 */
@Service
@Primary
public class LineChartService<T extends HasStringId & HasSubject, G extends Enum<G> & GroupByOption<T>>
        implements SimpleSelectionSupportService<T, G> {

    @TimeMe
    @ValidateChartOptions(required = {Y_AXIS, X_AXIS, SERIES_BY, NAME}, optional = {ORDER_BY, COLOR_BY})
    public Map<GroupByKey<T, G>, LineChartData> getLineChart(
            FilterResult<T> filtered, ChartGroupByOptions<T, G> settings) {
        return getLineChart(filtered, settings, c -> c.stream().findAny().orElse(null));
    }

    @TimeMe
    @ValidateChartOptions(required = {Y_AXIS, X_AXIS, SERIES_BY, NAME},
            optional = {ORDER_BY, COLOR_BY, STANDARD_DEVIATION})
    public Map<GroupByKey<T, G>, LineChartData> getLineChart(
            FilterResult<T> filtered, ChartGroupByOptions<T, G> settings,
            Function<Collection<GroupByKey<T, G>>, GroupByKey<T, G>> groupedEntriesCombiner,
            ChartGroupByOptions.ChartGroupBySetting... groupEntriesBy) {
        //grouping by trellis + SERIES_BY
        final Map<GroupByKey<T, G>, List<T>> groupedBySeriesEvents =
                filtered.getFilteredResult().stream().collect(
                        Collectors.groupingBy(e -> Attributes.get(settings.limitedBySettings(SERIES_BY), e),
                                Collectors.toList())
                )
                        .entrySet().stream()
                        .filter(e -> isSeriesValid(e.getValue()))
                        .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

        return groupedBySeriesEvents.entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> getLineChartTransformation(settings, groupedEntriesCombiner, groupEntriesBy)
                        .apply(e.getKey(), e.getValue())));
    }

    private BiFunction<GroupByKey<T, G>, Collection<T>, LineChartData> getLineChartTransformation(
            ChartGroupByOptions<T, G> settings,
            Function<Collection<GroupByKey<T, G>>, GroupByKey<T, G>> groupedEntriesCombiner,
            ChartGroupByOptions.ChartGroupBySetting... groupEntriesBy) {
        return (GroupByKey<T, G> group, Collection<T> events) -> {
            final Map<GroupByKey<T, G>, List<GroupByKey<T, G>>> grouped = events.stream()
                    .map(e -> Attributes.get(settings, e))
                    .collect(Collectors.groupingBy(k -> groupEntriesBy.length == 0 ? k : k.limitedBySettings(groupEntriesBy)));
            final List<GroupByKey<T, G>> combined = grouped.values().stream()
                    .map(c -> groupedEntriesCombiner.apply(c))
                    .collect(Collectors.toList());

            List<LineChartEntry> series = getSeries(combined);
            return new LineChartData(group.getValue(SERIES_BY), series);
        };
    }

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
                    return new LineChartEntry(x, y, name, colorBy, orderBy);
                })
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Override this method, if some filtration of series is required, i.e. excluding single entry groups (series)
     */
    protected boolean isSeriesValid(List<T> events) {
        return true;
    }
}
