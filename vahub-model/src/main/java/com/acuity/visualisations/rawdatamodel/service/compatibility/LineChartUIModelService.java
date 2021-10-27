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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.GroupByOptionAndParams;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.TrellisedLineFloatChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.LineChartData;
import com.acuity.va.security.acl.domain.Datasets;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

public abstract class LineChartUIModelService<T, G extends Enum<G> & GroupByOption<T>> {

    public List<TrellisedLineFloatChart<T, G, OutputLineChartData>> toTrellisedLineFloatChart(
            Map<GroupByKey<T, G>, LineChartData> chartData) {
        return toTrellisedLineFloatChart(chartData, null, null);
    }

    public List<TrellisedLineFloatChart<T, G, OutputLineChartData>> toTrellisedLineFloatChart(
            Map<GroupByKey<T, G>, LineChartData> chartData, Datasets datasets,
            GroupByOptionAndParams<T, G> colorByOption) {
        final Map<GroupByKey<T, G>, List<Entry<GroupByKey<T, G>, LineChartData>>> groupedByTrellis =
                chartData.entrySet().stream().collect(Collectors.groupingBy(e -> e.getKey().limitedByTrellisOptions()));

        return groupedByTrellis.entrySet().stream()
                               .map(trellisEntry -> {
                                   List<OutputLineChartData> lineChartData = getLineChartData(datasets, colorByOption, trellisEntry);
                                   return getTrellisedLineFloatChart(lineChartData, trellisEntry);
                               }).sorted(Comparator.comparing(TrellisedChart::getTrellisByString))
                               .collect(Collectors.toList());
    }

    protected TrellisedLineFloatChart<T, G, OutputLineChartData> getTrellisedLineFloatChart(List<OutputLineChartData> lineChartData,
                                                                                            Entry<GroupByKey<T, G>,
                                                                                                    List<Entry<GroupByKey<T, G>,
                                                                                                            LineChartData>>> trellisEntry) {
        final List<TrellisOption<T, G>> trellisOptions
                = trellisEntry.getKey()
                              .getTrellisByValues()
                              .entrySet().stream()
                              .map(option ->
                                      TrellisOption.of(option.getKey(), option.getValue()))
                              .collect(Collectors.toList());

        return new TrellisedLineFloatChart<>(trellisOptions, lineChartData);
    }

    protected List<OutputLineChartData> getLineChartData(Datasets datasets,
                                                                       GroupByOptionAndParams<T, G> colorByOption,
                                                                       Entry<GroupByKey<T, G>,
                                                                               List<Entry<GroupByKey<T, G>,
                                                                                       LineChartData>>> trellisEntry) {
        return trellisEntry.getValue().stream()
                           .map(e -> {
                               final List<OutputLineChartEntry> series
                                       = e.getValue()
                                          .getSeries().stream()
                                          .map(se -> new OutputLineChartEntry(se, getColor(se.getColorBy(),
                                                  datasets, colorByOption)))
                                          .collect(Collectors.toList());
                               return new OutputLineChartData(Objects.toString(e.getValue().getSeriesBy(),
                                       Attributes.DEFAULT_EMPTY_VALUE), series);
                           }).collect(Collectors.toList());
    }

    /**
     * Implement this method to use datasets and colorByOption info for coloring, i.e. to store
     * colors separately per dataset and option
     */
    protected abstract String getColor(Object colorBy, Datasets datasets, GroupByOptionAndParams<T, G> colorByOption);
}
