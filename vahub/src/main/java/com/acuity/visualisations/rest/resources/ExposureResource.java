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

package com.acuity.visualisations.rest.resources;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.service.event.ExposureService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.TrellisedLineFloatChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import com.acuity.visualisations.rest.model.request.exposure.ExposureLineChartRequest;
import com.acuity.visualisations.rest.model.request.exposure.ExposureLineChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.exposure.ExposureRequest;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.NAME;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.ORDER_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static com.acuity.visualisations.rest.resources.util.DetailsOnDemandCsvDownloadingUtils.setDownloadHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/resources/exposure/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
public class ExposureResource {

    @Autowired
    private ExposureService exposureService;

    @RequestMapping(value = "/filters", method = POST)
    @Cacheable
    public Filters<Exposure> getAvailableFilters(@RequestBody @Valid ExposureLineChartRequest requestBody) {
        return exposureService.getAvailableFilters(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters(), getDefaultExposureLineChartSettings());
    }

    @ApiOperation(
            value = "Gets the available trellising and options",
            nickname = "availableTrellising",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/trellising", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<ExposureGroupByOptions>> getAvailableTrellising(
            @ApiParam(value =
                    "ExposureRequest:  Exposure and Population Filters e.g. {exposureFilters : {}, populationFilters: {}}",
                    required = true)
            @RequestBody @Valid ExposureRequest requestBody) {
        return exposureService.getTrellisOptions(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters()
        );
    }

    @ApiOperation(
            value = "Gets the the concentration of drug analytes over time to plot",
            nickname = "getLinePlot",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/concentration-over-time", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> getExposureLines(
            @ApiParam(value = "ExposureLineRequest: Request parameters for the line plots e.g. "
                    + "{trellising : [{trellisedBy: 'SUBJECT', options: ['subject1, subject2']}], "
                    + "exposureFilters: {}, populationFilters: {}}", required = true)
            @RequestBody ExposureLineChartRequest requestBody) {
        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings =
                getExposureLineChartSettings(requestBody.getSettings().getSettings());
        return exposureService.getLineChart(requestBody.getDatasetsObject(), settings, requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/selection", method = POST)
    public SelectionDetail getSelectionDetail(
            @RequestBody @Valid ExposureLineChartSelectionRequest requestBody) {

        ChartSelection<Exposure, ExposureGroupByOptions, ChartSelectionItem<Exposure, ExposureGroupByOptions>> selection = requestBody.getSelection();
        if (selection.getSettings() == null) {
            selection = ChartSelection.of(
                    getExposureLineChartSettings(selection.getSettings()).limitedBySettings(SERIES_BY, X_AXIS),
                    selection.getSelectionItems().stream()
                            .map(i -> ChartSelectionItem.of(
                                    i.getSelectedTrellises(),
                                    i.getSelectedItems().entrySet().stream().filter(e -> e.getKey() == SERIES_BY || e.getKey() == X_AXIS)
                                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))))
                            .collect(Collectors.toSet()));
        }


        return exposureService.getSelectionDetails(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters(), selection);
    }

    @RequestMapping(value = "/colorby-options", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<ExposureGroupByOptions>> getAvailableColorBy(
            @ApiParam(value = "ExposureTrellisingRequest:  Exposure and Population Filters e.g. {exposureFilters : {}, "
                    + "populationFilters: {}, countType: 'COUNT_OF_EVENTS'}",
                    required = true)
            @RequestBody @Valid ExposureLineChartRequest requestBody) {
        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings =
                getExposureLineChartSettings(requestBody.getSettings().getSettings());
        return exposureService.getLineChartColorBy(requestBody.getDatasetsObject(), settings);
    }

    @RequestMapping(value = "/filters-subjects", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<String> getSubjects(@RequestBody @Valid ExposureRequest requestBody) {
        return exposureService.getSubjects(requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    private ChartGroupByOptions<Exposure, ExposureGroupByOptions> getExposureLineChartSettings(ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings) {
        // COLOR_BY, SERIES_BY, X_AXIS, Y_AXIS come from front-end
        return settings.toBuilder()
                .withOption(NAME, ExposureGroupByOptions.ALL_INFO.getGroupByOptionAndParams())
                .withOption(ORDER_BY, ExposureGroupByOptions.TIME_FROM_ADMINISTRATION.getGroupByOptionAndParams())
                .build();
    }

    private ChartGroupByOptions<Exposure, ExposureGroupByOptions> getDefaultExposureLineChartSettings() {
        return ChartGroupByOptions.<Exposure, ExposureGroupByOptions>builder()
                .withOption(SERIES_BY, ExposureGroupByOptions.SUBJECT_CYCLE.getGroupByOptionAndParams())
                .withOption(COLOR_BY, ExposureGroupByOptions.CYCLE.getGroupByOptionAndParams())
                .withOption(NAME, ExposureGroupByOptions.ALL_INFO.getGroupByOptionAndParams())
                .withOption(X_AXIS, ExposureGroupByOptions.TIME_FROM_ADMINISTRATION.getGroupByOptionAndParams())
                .withOption(Y_AXIS, ExposureGroupByOptions.ANALYTE_CONCENTRATION.getGroupByOptionAndParams())
                .withOption(ORDER_BY, ExposureGroupByOptions.TIME_FROM_ADMINISTRATION.getGroupByOptionAndParams())
                .build();
    }

    @RequestMapping(value = "/details-on-demand", method = POST)
    public List<Map<String, String>> getDetailsOnDemandData(
            @ApiParam(value = "Details On Demand Request body: A list of event IDs to get the data for e.g. "
                    + "['ev-1', 'ev-2']", required = true)
            @RequestBody @Valid DetailsOnDemandRequest requestBody) {

        return exposureService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getEventIds(), requestBody.getSortAttrs(),
                requestBody.getStart(), (long) requestBody.getEnd() - requestBody.getStart());
    }

    @RequestMapping(value = "/download-details-on-demand", method = POST)
    public void downloadAllDetailsOnDemandData(@RequestBody @Valid ExposureRequest requestBody,
                                               HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        exposureService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Downloads data for the details on demand table for the selected IDs",
            nickname = "downloadSelectedDetailsOnDemandData",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/download-selected-details-on-demand", method = POST)
    public void downloadSelectedDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody,
                                                    HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        exposureService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(), response.getWriter());
    }
}
