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

import com.acuity.visualisations.rawdatamodel.filters.ExposureFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.BasePlotEventService;
import com.acuity.visualisations.rawdatamodel.service.plots.LineChartSupportService;
import com.acuity.visualisations.rawdatamodel.service.compatibility.ExposureLineChartUIModelService;
import com.acuity.visualisations.rawdatamodel.service.plots.ExposureLineChartService;
import com.acuity.visualisations.rawdatamodel.service.ssv.ColorInitializer;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByAttributes;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.ExposureRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.TrellisedLineFloatChart;
import com.acuity.visualisations.rawdatamodel.vo.exposure.ExposureData;
import com.acuity.visualisations.rawdatamodel.vo.exposure.ExposureTooltip;
import com.acuity.visualisations.rawdatamodel.vo.plots.LineChartData;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import com.acuity.va.security.acl.domain.Datasets;
import com.axibase.math.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.NAME;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.STANDARD_DEVIATION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.ANALYTE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.SUBJECT_CYCLE;
import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE;
import static java.util.stream.Collectors.joining;

@Service
public class ExposureService extends BasePlotEventService<ExposureRaw, Exposure, ExposureGroupByOptions>
        implements LineChartSupportService<Exposure, ExposureGroupByOptions>, ColorInitializer {

    @Autowired
    private ExposureLineChartUIModelService<Exposure, ExposureGroupByOptions> lineChartUIModelService;
    @Autowired
    private ExposureLineChartService lineChartService;

    public List<TrellisOptions<ExposureGroupByOptions>> getTrellisOptions(Datasets datasets, Filters<Exposure> filters,
                                                                          PopulationFilters populationFilters) {
        FilterResult<Exposure> filtered = getFilteredDataNoSingleCycleSeries(datasets, filters, populationFilters,
                ChartGroupByOptions.<Exposure, ExposureGroupByOptions>builder().build());
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(), ANALYTE);
    }

    /**
     * Returns available filters excluding 1-point series
     */
    public Filters<Exposure> getAvailableFilters(Datasets datasets, Filters<Exposure> eventFilters, PopulationFilters populationFilters,
                                                 ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings) {
        FilterResult<Exposure> filtered = getFilteredDataNoSingleCycleSeries(datasets, eventFilters, populationFilters, settings);
        return eventFilterService.getAvailableFilters(filtered);
    }

    @Override
    public List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> getLineChart(
            Datasets datasets,
            ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings,
            ExposureFilters filters,
            PopulationFilters populationFilters) {
        FilterResult<Exposure> filtered = getFilteredDataNoSingleCycleSeries(datasets, filters, populationFilters, settings);
        final Map<GroupByKey<Exposure, ExposureGroupByOptions>, LineChartData> res = lineChartService
                .getLineChart(filtered, settings, this::calculateAggregatedPoint, X_AXIS);
        return lineChartUIModelService.toTrellisedLineFloatChart(res, datasets, settings.getOptions().get(COLOR_BY));
    }

    private GroupByKey<Exposure, ExposureGroupByOptions> calculateAggregatedPoint(Collection<GroupByKey<Exposure, ExposureGroupByOptions>> c) {
        GroupByKey<Exposure, ExposureGroupByOptions> any = c.stream().findAny().get();
        any = withAggregatedName(c, any);
        any = withAverageYAndDeviation(c, any);
        return any;
    }

    /**
     * SubjectCycle in NAME option is updated due to possibility of several treatment cycles and analytes
     * at one point in case of averaging (setting Line Aggregation) on the analyte concentration plot
     *
     * @param pointEvents - all events grouped by particular set of options that will form one point on a plot
     * @param keyToModify
     * @return
     */
    private GroupByKey<Exposure, ExposureGroupByOptions> withAggregatedName(Collection<GroupByKey<Exposure, ExposureGroupByOptions>> pointEvents,
                                                                            GroupByKey<Exposure, ExposureGroupByOptions> keyToModify) {

        List<ExposureData> cycleList = collectData(pointEvents);
        String cycles = cycleList.stream()
                .map(ExposureData::getTreatmentCycle)
                .distinct()
                .sorted()
                .collect(joining(", "));
        String analytes = keyToModify.getTrellisByValues().containsKey(ExposureGroupByOptions.ANALYTE)
                ? null
                : cycleList.stream().map(ExposureData::getAnalyte).distinct().sorted().collect(joining(", "));
        final ExposureData exposureData = (ExposureData) keyToModify.getValues().get(NAME);
        final int dataPoints = pointEvents.size();
        final ExposureTooltip exposureTooltip = ExposureTooltip.builder()
                .exposureData(exposureData.toBuilder().treatmentCycle(cycles).analyte(analytes).build())
                .dataPoints(dataPoints)
                .colorByValue(Objects.toString(keyToModify.getValues()
                        .get(COLOR_BY), DEFAULT_EMPTY_VALUE)).build();
        keyToModify = keyToModify.copyReplacing(NAME, exposureTooltip);
        return keyToModify;
    }

    /**
     * Y axis value is calculated as an average of Y values of all points that are going to form the point
     * Standard deviation is calculated to form error bars
     *
     * @param pointEvents - all events grouped by particular set of options that will form one point on a plot
     * @param keyToModify
     * @return
     */
    private GroupByKey<Exposure, ExposureGroupByOptions> withAverageYAndDeviation(Collection<GroupByKey<Exposure, ExposureGroupByOptions>> pointEvents,
                                                                                  GroupByKey<Exposure, ExposureGroupByOptions> keyToModify) {
        BigDecimal[] yValues = pointEvents.stream()
                .map(e -> new BigDecimal(e.getValue(Y_AXIS).toString()))
                .toArray(BigDecimal[]::new);
        DescriptiveStatistics stats = new DescriptiveStatistics(yValues);
        BigDecimal avg = stats.getMean();
        BigDecimal standardDeviation = stats.getPopulationStandardDeviation();
        keyToModify = keyToModify.copyReplacing(Y_AXIS, avg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue());
        keyToModify = keyToModify.copyReplacing(STANDARD_DEVIATION, standardDeviation.setScale(2, BigDecimal.ROUND_HALF_UP)
                .doubleValue());
        return keyToModify;

    }

    public FilterResult<Exposure> getFilteredDataNoSingleCycleSeries(Datasets datasets, Filters<Exposure> filters, PopulationFilters populationFilters,
                                                                     ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings) {
        // firstly, filter by single cycle series, then apply exposure filters. As a result, single point can be shown in some cases
        final FilterResult<Exposure> filteredData = getFilteredData(datasets, ExposureFilters.empty(), PopulationFilters.empty());
        FilterResult<Exposure> filteredDataNoSingleSeries = filteredData.withResults(filteredData.getAllEvents(),
                GroupByAttributes.group(filteredData.getFilteredEvents(), ChartGroupByOptions.<Exposure,
                        ExposureGroupByOptions>builder().withTrellisOptions(settings.getTrellisOptions())
                        .withOption(SERIES_BY, SUBJECT_CYCLE.getGroupByOptionAndParams()).build())
                        .entrySet().stream()
                        .filter(e -> e.getValue().size() > 1)
                        .flatMap(e -> e.getValue().stream())
                        .collect(Collectors.toList()));
        //apply exposure filters
        return getFilteredData(filteredDataNoSingleSeries.getFilteredEvents(), datasets, filters, populationFilters);
    }

    @Override
    public SelectionDetail getSelectionDetails(Datasets datasets, Filters<Exposure> filters, PopulationFilters populationFilters,
                                               ChartSelection<Exposure, ExposureGroupByOptions,
                                                       ChartSelectionItem<Exposure, ExposureGroupByOptions>> selection) {
        FilterResult<Exposure> filtered = getFilteredDataNoSingleCycleSeries(datasets, filters, populationFilters, selection
                .getSettings());


        return lineChartService.getSelectionDetails(filtered, selection);
    }

    public List<TrellisOptions<ExposureGroupByOptions>> getLineChartColorBy(Datasets datasets,
                                                                            ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings) {
        FilterResult<Exposure> filtered = getFilteredData(datasets, ExposureFilters.empty(), PopulationFilters.empty());
        ExposureGroupByOptions seriesByOption = settings.getOptions().get(SERIES_BY).getGroupByOption();

        Validate.isTrue(seriesByOption != null, "Series by option cannot be empty");
        return TrellisUtil.getTrellisOptions(filtered.getFilteredResult(), seriesByOption.getColorByOptions());
    }

    @Override
    public void generateColors(Datasets datasets) {
        Collection<Exposure> events = getEventDataProvider(datasets).loadData(datasets);
        List<TrellisOptions<ExposureGroupByOptions>> colorByOptions = TrellisUtil.getTrellisOptions(events, SUBJECT_CYCLE
                .getColorByOptions());
        lineChartUIModelService.generateColors(datasets, colorByOptions);
    }

    private List<ExposureData> collectData(Collection<GroupByKey<Exposure, ExposureGroupByOptions>> c) {
        return c.stream()
                .map(val -> ((ExposureData) val.getValues().get(NAME)))
                .collect(Collectors.toList());
    }
}
