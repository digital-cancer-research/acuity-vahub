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
import com.acuity.visualisations.rawdatamodel.filters.QtProlongationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.QtProlongationService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.QtProlongationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.QtProlongation;
import com.acuity.visualisations.rest.model.request.qtprolongation.QtProlongationBarChartRequest;
import com.acuity.visualisations.rest.model.request.qtprolongation.QtProlongationBarChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.qtprolongation.QtProlongationRequest;
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
import java.util.Set;

import static com.acuity.visualisations.rest.resources.util.DetailsOnDemandCsvDownloadingUtils.setDownloadHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(
        value = "/resources/qt-prolongation",
        consumes = APPLICATION_JSON_VALUE,
        produces = APPLICATION_JSON_VALUE)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
public class QtProlongationResource {
    @Autowired
    private QtProlongationService qtProlongationService;

    @ApiOperation(
            value = "Gets the bar charts for requested trellising",
            nickname = "getBarChartData",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/countsbarchart", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedBarChart<QtProlongation, QtProlongationGroupByOptions>> getBarChartData(
            @ApiParam(value = "QtProlongationBarChartRequest: Request parameters for the bar chart plots e.g. "
                    + "{trellising : [{trellisedBy: '', options: ['', '']}], "
                    + "qtProlongationFilters: {}, populationFilters: {}, countType:'COUNT_OF_EVENTS'", required = true)
            @RequestBody QtProlongationBarChartRequest requestBody) {
        return qtProlongationService.getBarChart(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getCountType());
    }

    @ApiOperation(
            value = "Gets the available trellising and options",
            nickname = "getAvailableTrellising",
            response = Set.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/trellising", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<QtProlongationGroupByOptions>> getAvailableTrellising(
            @ApiParam(value = "QtProlongationTrellisingRequest:  QtProlongation and Population Filters e.g. "
                    + "{qtProlongationFilters : {}, populationFilters: {}}",
                    required = true)
            @RequestBody @Valid QtProlongationRequest requestBody) {
        return qtProlongationService.getTrellisOptions(
                requestBody.getDatasetsObject(),
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
    public AxisOptions<QtProlongationGroupByOptions> getBarChartXAxis(@ApiParam(
            value = "QtProlongationRequest:  QtProlongation and Population Filters e.g."
                    + " {qtProlongationFilters : {}, populationFilters: {}}", required = true)
                                                                      @RequestBody
                                                                      @Valid QtProlongationRequest requestBody) {
        return qtProlongationService.getAvailableBarChartXAxis(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets selection details for bar chart",
            nickname = "getSelectionDetailWithinBarChart",
            response = SelectionDetail.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/selection", method = POST)
    public SelectionDetail getSelectionDetailWithinBarChart(
            @RequestBody @Valid QtProlongationBarChartSelectionRequest requestBody) {
        return qtProlongationService.getSelectionDetails(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }

    @ApiOperation(
            value = "Gets the subjects in available qtProlongation filters for "
                    + "the currently selected qtProlongation and population filters",
            nickname = "getSubjects",
            response = QtProlongationFilters.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/filters-subjects", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<String> getSubjects(
            @ApiParam(value = "QtProlongationRequest:"
                    + " QtProlongation and Population Filters e.g. {qtProlongationFilters : {}, populationFilters: {}}",
                    required = true)
            @RequestBody QtProlongationRequest requestBody) {
        return qtProlongationService.getSubjects(
                requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/colorby-options", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<QtProlongationGroupByOptions>> getAvailableColorBy(
            @ApiParam(value = "QtProlongationRequest: QtProlongation and Population Filters e.g. "
                    + "{qtProlongationFilters : {}, populationFilters: {}}", required = true)
            @RequestBody @Valid QtProlongationRequest requestBody) {
        return qtProlongationService.getBarChartColorBy(
                requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }


    @RequestMapping(value = "/details-on-demand", method = POST)
    public List<Map<String, String>> getDetailsOnDemandData(
            @ApiParam(value = "Details On Demand Request body: A list of event IDs to get the data for e.g. "
                    + "['ev-1', 'ev-2']", required = true)
            @RequestBody @Valid DetailsOnDemandRequest requestBody) {

        return qtProlongationService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(),
                requestBody.getEventIds(),
                requestBody.getSortAttrs(),
                requestBody.getStart(),
                (long) requestBody.getEnd() - requestBody.getStart());
    }

    @ApiOperation(
            value = "Downloads all of the data for the details on demand table",
            nickname = "downloadAllDetailsOnDemandData",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/download-details-on-demand", method = POST)
    public void downloadAllDetailsOnDemandData(@RequestBody @Valid QtProlongationRequest requestBody,
                                               HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        qtProlongationService.writeAllDetailsOnDemandCsv(
                requestBody.getDatasetsObject(),
                response.getWriter(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
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
        qtProlongationService.writeSelectedDetailsOnDemandCsv(
                requestBody.getDatasetsObject(),
                requestBody.getEventIds(),
                response.getWriter());
    }
}
