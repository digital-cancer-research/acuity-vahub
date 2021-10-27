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

package com.acuity.visualisations.rawdatamodel.service.event;


import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.LiverFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.plots.ScatterPlotUtils;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LiverGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.LiverRaw;
import com.acuity.visualisations.rawdatamodel.vo.SelectionBox;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputScatterPlotEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedScatterPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
public class LiverService extends BasePlotEventService<LiverRaw, Liver, LiverGroupByOptions> {

    public List<TrellisOptions<LiverGroupByOptions>> getTrellisOptions(Datasets datasets, Filters<Liver> filters,
                                                                       PopulationFilters populationFilters) {
        FilterResult<Liver> filtered = getFilteredData(datasets, filters, populationFilters, null);

        if (filtered.getFilteredEvents().isEmpty()) {
            return Collections.emptyList();
        } else {
            List<TrellisOptions<LiverGroupByOptions>> trellises = new ArrayList<>();
            trellises.add(new TrellisOptions<>(LiverGroupByOptions.MEASUREMENT,
                    Arrays.asList(Liver.LiverCode.AST.name(), Liver.LiverCode.ALT.name())));

            List<TrellisOptions<LiverGroupByOptions>> armTrellises = TrellisUtil.getTrellisOptions(
                    filtered.getFilteredResult(), LiverGroupByOptions.ARM);

            if (!armTrellises.isEmpty()) {
                trellises.addAll(armTrellises);
            }
            return trellises;
        }
    }

    public SelectionDetail getPlotSelection(Datasets datasets,
                                            Collection<Map<LiverGroupByOptions, Object>> filterByTrellisOptions,
                                            LiverFilters liverFilters, PopulationFilters populationFilters,
                                            SelectionBox selectionBox) {

        ChartGroupByOptionsFiltered<Liver, LiverGroupByOptions> settings = toGroupOptions(
                filterByTrellisOptions);
        FilterResult<Liver> filtered = getFilteredData(datasets, liverFilters, populationFilters);

        Map<GroupByKey<Liver, LiverGroupByOptions>, Map<Subject, List<Liver>>> maxSubjectMeasurements =
                ScatterPlotUtils.maxSubjectEvents(filtered.getFilteredResult(),
                        settings.getSettings(), Liver::getNormalizedValue);

        Set<String> subjectIds = new HashSet<>();
        Set<String> eventIds = new HashSet<>();

        filterByTrellisOptions.forEach(options -> {
            GroupByKey<Liver, LiverGroupByOptions> groupByKey = new GroupByKey<>(Collections.emptyMap(), options);
            Map<Subject, List<Liver>> maxSubjectBiliMeasurements = maxSubjectMeasurements.getOrDefault(
                    groupByKey.copyReplacing(LiverGroupByOptions.MEASUREMENT, Liver.LiverCode.BILI.name()), emptyMap());
            Map<Subject, List<Liver>> maxSubjectThisMeasurements = maxSubjectMeasurements.getOrDefault(
                    groupByKey, emptyMap());

            Map<Subject, List<Liver>> maxBilirubinMap = maxSubjectBiliMeasurements
                    .entrySet().stream().filter(entry -> !entry.getValue().isEmpty()
                            && entry.getValue().get(0).getNormalizedValue() >= selectionBox.getXMin()
                            && entry.getValue().get(0).getNormalizedValue() <= selectionBox.getXMax()
                    ).collect(toMap(Map.Entry::getKey, Map.Entry::getValue));

            maxSubjectThisMeasurements.entrySet().stream().filter(entry -> !entry.getValue().isEmpty()
                    && entry.getValue().get(0).getNormalizedValue() >= selectionBox.getYMin()
                    && entry.getValue().get(0).getNormalizedValue() <= selectionBox.getYMax()
                    && maxBilirubinMap.containsKey(entry.getKey())
            ).forEach(entry -> {
                subjectIds.add(entry.getKey().getSubjectId());
                eventIds.addAll(entry.getValue().stream().map(Liver::getId).collect(toList()));
                eventIds.addAll(maxBilirubinMap.get(entry.getKey()).stream().map(Liver::getId).collect(toList()));
            });
        });

        SelectionDetail selectionDetail = new SelectionDetail();
        selectionDetail.setSubjectIds(subjectIds);
        selectionDetail.setEventIds(eventIds);
        selectionDetail.setTotalEvents(filtered.getAllEvents().size());
        selectionDetail.setTotalSubjects(filtered.getPopulationFilterResult().size());
        return selectionDetail;
    }

    public List<TrellisedScatterPlot<Liver, LiverGroupByOptions>> getPlotValues(Datasets datasets,
                                                                                Collection<Map<LiverGroupByOptions, Object>> filterByTrellisOptions,
                                                                                Filters<Liver> liverFilters,
                                                                                PopulationFilters populationFilters) {
        ChartGroupByOptionsFiltered<Liver, LiverGroupByOptions> settings = toGroupOptions(
                filterByTrellisOptions);
        FilterResult<Liver> filtered = getFilteredData(datasets, liverFilters, populationFilters, settings);

        Map<GroupByKey<Liver, LiverGroupByOptions>, Map<Subject, List<Liver>>> maxSubjectMeasurements =
                ScatterPlotUtils.maxSubjectEvents(filtered.getFilteredResult(),
                        settings.getSettings(), Liver::getNormalizedValue);

        return filterByTrellisOptions.stream().map(options -> {
            GroupByKey<Liver, LiverGroupByOptions> groupByKey = new GroupByKey<>(Collections.emptyMap(), options);
            Map<Subject, List<Liver>> maxSubjectBiliMeasurements = maxSubjectMeasurements.getOrDefault(
                    groupByKey.copyReplacing(LiverGroupByOptions.MEASUREMENT, Liver.LiverCode.BILI.name()), emptyMap());
            Map<Subject, List<Liver>> maxSubjectThisMeasurements = maxSubjectMeasurements.getOrDefault(
                    groupByKey, emptyMap());

            List<TrellisOption<Liver, LiverGroupByOptions>> trellisOptions = options.entrySet().stream()
                    .map(e -> TrellisOption.of(e.getKey(), e.getValue())).collect(toList());

            TrellisedScatterPlot<Liver, LiverGroupByOptions> plot = new TrellisedScatterPlot<>();
            plot.setTrellisedBy(trellisOptions);
            plot.setXaxisLabel("Max. normalised bilirubin");
            plot.setData(measurementsAgainstBilirubin(maxSubjectThisMeasurements, maxSubjectBiliMeasurements));

            if (Liver.LiverCode.AST.name().equals(options.get(LiverGroupByOptions.MEASUREMENT))) {
                plot.setYaxisLabel("Max. normalised AST");
            } else if (Liver.LiverCode.ALT.name().equals(options.get(LiverGroupByOptions.MEASUREMENT))) {
                plot.setYaxisLabel("Max. normalised ALT");
            }
            return plot;
        }).collect(toList());
    }

    private static List<OutputScatterPlotEntry> measurementsAgainstBilirubin(
            Map<Subject, List<Liver>> maxMeasurements, Map<Subject, List<Liver>> maxBilirubin) {
        return maxBilirubin.entrySet().stream().filter(e -> maxMeasurements.containsKey(e.getKey()))
                .map(e -> {
                    double x = e.getValue().get(0).getNormalizedValue();
                    double y = maxMeasurements.get(e.getKey()).get(0).getNormalizedValue();
                    OutputScatterPlotEntry entry = new OutputScatterPlotEntry();
                    entry.setName(e.getKey().getSubjectId());
                    entry.setX(x);
                    entry.setY(y);
                    entry.setColor(measurementAgainstBilirubinColor(y, x));
                    return entry;
                }).sorted((Comparator.comparing(OutputScatterPlotEntry::getX))).collect(toList());
    }

    private static String measurementAgainstBilirubinColor(double y, double x) {
        if (x < 2d && y < 3d) {
            return "#27ae60";
        } else if ((x > 2d && y < 3d) || (x < 2d && y > 3d)) {
            return "#e67e22";
        } else {
            return "#c0392b";
        }
    }

    private static ChartGroupByOptionsFiltered<Liver, LiverGroupByOptions> toGroupOptions(
            Collection<Map<LiverGroupByOptions, Object>> filterByTrellisOptions) {

        List<Map<LiverGroupByOptions, Object>> filterOptions = new ArrayList<>(filterByTrellisOptions);

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Liver, LiverGroupByOptions> builder =
                ChartGroupByOptions.<Liver, LiverGroupByOptions>builder()
                        .withTrellisOption(LiverGroupByOptions.MEASUREMENT.getGroupByOptionAndParams());
        filterOptions.add(ImmutableMap.of(LiverGroupByOptions.MEASUREMENT, Liver.LiverCode.BILI.name()));

        return ChartGroupByOptionsFiltered.builder(builder.build()).withFilterByTrellisOptions(filterOptions).build();
    }
}
