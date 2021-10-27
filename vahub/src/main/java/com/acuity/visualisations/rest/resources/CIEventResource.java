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

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.CIEventFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.service.event.CIEventService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import com.acuity.visualisations.rest.model.request.cievents.BarChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.cievents.CIBarLineChartRequest;
import com.acuity.visualisations.rest.model.request.cievents.CIEventBarChartRequest;
import com.acuity.visualisations.rest.model.request.cievents.CIEventRequest;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.Api;
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
import java.util.Set;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/resources/cievents", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@Api(value = "/resources/cievents", description = "rest endpoints for cievent")
public class CIEventResource {

    @Autowired
    private CIEventService ciEventService;

    @ApiOperation(
            value = "Gets the bar charts for requested trellising",
            nickname = "getBarChartData",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/countsbarchart", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedBarChart<CIEvent, CIEventGroupByOptions>> getBarChartData(
            @ApiParam(value = "CIEventBarChartRequest: Request parameters for the bar chart plots e.g. "
                    + "{trellising : [{trellisedBy: 'FINAL_DIAGNOSIS', options: ['d1', 'd2']}], "
                    + "cieventFilters: {}, populationFilters: {}, countType:'COUNT_OF_SUBJECTS', demographicsType: {}", required = true)
            @RequestBody CIEventBarChartRequest requestBody) {

        return ciEventService.getBarChart(requestBody.getDatasetsObject(), requestBody.getSettings(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters(), requestBody.getCountType());
    }

    @ApiOperation(
            value = "Get additional suspected CI endpoints over time",
            nickname = "getLineBarChartData",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/overtime", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedOvertime<CIEvent, CIEventGroupByOptions>> getLineBarChartData(
            @ApiParam(value = "CIBarLineChartRequest: Request parameters for the bar line chart e.g. "
                    + "categoryType: {value: 'START_DATE', intarg: 1, stringarg: null}, {trellising : [], "
                    + "ciEndpointFilters: {}, populationFilters: {}", required = true)
            @RequestBody CIBarLineChartRequest requestBody) {

        return ciEventService.getLineBarChart(requestBody.getDatasetsObject(), requestBody.getSettings(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets the available trellising and options",
            nickname = "getAvailableTrellising",
            response = Set.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/trellising", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<CIEventGroupByOptions>> getAvailableTrellising(
            @ApiParam(value = "CIEventTrellisingRequest:  CIEvent and Population Filters e.g. {cieventFilters : {}, "
                    + "populationFilters: {}, countType: 'COUNT_OF_EVENTS'}",
                    required = true)
            @RequestBody @Valid CIEventRequest requestBody) {

        return ciEventService.getTrellisOptions(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/colorby-options", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<CIEventGroupByOptions>> getAvailableColorBy(
            @ApiParam(value = "CIEventTrellisingRequest:  CIEvent and Population Filters e.g. {cieventFilters : {}, "
                    + "populationFilters: {}, countType: 'COUNT_OF_EVENTS'}",
                    required = true)
            @RequestBody @Valid CIEventRequest requestBody) {

        return ciEventService.getBarChartColorBy(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets the available ci events filters for the currently selected ci events and population filters",
            nickname = "getAvailableFilters",
            response = CIEventFilters.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/filters", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public Filters<CIEvent> getAvailableFilters(@ApiParam(
            value = "CIEventRequest:  CIEvent and Population Filters e.g. {cieventFilters : {}, populationFilters: {}}", required = true)
                                                @RequestBody @Valid CIEventRequest requestBody) {

        return ciEventService.getAvailableFilters(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets the available bar chart axis",
            nickname = "getBarChartXAxis",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/countsbarchart-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<CIEventGroupByOptions> getBarChartXAxis(@ApiParam(
            value = "CIEventRequest:  CIEvent and Population Filters e.g. {cieventFilters : {}, populationFilters: {}}", required = true)
                                                               @RequestBody @Valid CIEventRequest requestBody) {
        return ciEventService.getAvailableBarChartXAxis(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets the available over time chart axis",
            nickname = "getOverTimeChartXAxis",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/overtime-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<CIEventGroupByOptions> getOverTimeChartXAxis(@ApiParam(
            value = "CIEventRequest:  CIEvent and Population Filters e.g. {cieventFilters : {}, populationFilters: {}}", required = true)
                                                                    @RequestBody @Valid CIEventRequest requestBody) {
        return ciEventService.getAvailableOverTimeChartXAxis(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/selection", method = POST)
    public SelectionDetail getSelectionDetailWithinBarChart(
            @RequestBody @Valid BarChartSelectionRequest requestBody) {
        return ciEventService.getSelectionDetails(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters(), requestBody.getSelection());
    }

    @RequestMapping(value = "/details-on-demand", method = POST)
    public List<Map<String, String>> getDetailsOnDemandData(
            @ApiParam(value = "Details On Demand Request body: A list of event IDs to get the data for e.g. "
                    + "['ev-1', 'ev-2']", required = true)
            @RequestBody @Valid DetailsOnDemandRequest requestBody) {

        return ciEventService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getEventIds(), requestBody.getSortAttrs(),
                requestBody.getStart(), (long) requestBody.getEnd() - requestBody.getStart());
    }

    @ApiOperation(
            value = "Downloads all of the data for the details on demand table",
            nickname = "getAllDetailsOnDemandCsv",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/download-details-on-demand", method = POST)
    public void downloadAllDetailsOnDemandData(@RequestBody @Valid CIEventRequest requestBody,
                                               HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        ciEventService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Downloads data for the details on demand table for the selected IDs",
            nickname = "getAllDetailsOnDemandCsv",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/download-selected-details-on-demand", method = POST)
    public void downloadSelectedDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody,
                                                    HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        ciEventService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(), response.getWriter());
    }

    @ApiOperation(
            value = "Gets all data for a single subject",
            nickname = "getSingleSubjectData",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/single-subject", method = POST)
    @Cacheable(condition = "#requestBody.getEventFilters().isEmpty()")
    public List<Map<String, String>> getSingleSubjectData(
            @ApiParam(value = "Single Subject Request body: The subject ID to get the data for", required = true)
            @RequestBody @Valid SingleSubjectRequest<CIEventFilters> requestBody) {

        return ciEventService.getSingleSubjectData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @ApiOperation(
            value = "Gets the subjects in available ci event filters for the currently selected ci event and population filters",
            nickname = "getSubjects",
            response = CIEventFilters.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/filters-subjects", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<String> getSubjects(
            @ApiParam(value = "CIEventRequest:  CI Event and Population Filters e.g. {cieventsFilters : {}, populationFilters: {}}", required = true)
            @RequestBody CIEventRequest requestBody) {
        return ciEventService.getSubjects(requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    private static void setDownloadHeaders(HttpServletResponse response) {
        response.addHeader("Content-disposition", "attachment;filename=details_on_demand.csv");
        response.setContentType("txt/csv");
    }
}
