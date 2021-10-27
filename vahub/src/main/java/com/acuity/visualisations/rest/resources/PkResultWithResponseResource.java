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
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.service.event.PkResultService.PkResultTrellisOptions;
import com.acuity.visualisations.rawdatamodel.service.event.PkResultWithResponseService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import com.acuity.visualisations.rest.model.request.pkresult.PkResultBoxPlotRequest;
import com.acuity.visualisations.rest.model.request.pkresult.PkResultBoxPlotSelectionRequest;
import com.acuity.visualisations.rest.model.request.pkresult.PkResultOptionsRequest;
import com.acuity.visualisations.rest.model.request.pkresult.PkResultRequest;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.util.Constants;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "resources/pkresultwithresponse",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class PkResultWithResponseResource extends DetailsOnDemandCsvDownloader {
    @Autowired
    private PkResultWithResponseService pkResultWithResponseService;

    @RequestMapping(value = "/filters", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public Filters<PkResult> getAvailableFilters(@RequestBody
                                                 @Valid PkResultRequest requestBody) {
        return pkResultWithResponseService.getAvailableFilters(
                requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/trellising", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<PkResultGroupByOptions>> getAvailableTrellising(@RequestBody
                                                                               @Valid PkResultRequest requestBody) {
        return pkResultWithResponseService.getTrellisOptions(
                requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/boxplot-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<PkResultGroupByOptions> getBoxPlotXAxis(@RequestBody
                                                               @Valid PkResultRequest requestBody) {
        return pkResultWithResponseService.getAvailableBoxPlotXAxis(
                requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/boxplot-options", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<PkResultTrellisOptions> getBoxPlotOptions(
            @RequestBody @Valid PkResultOptionsRequest requestBody) {
        return pkResultWithResponseService.getBoxPlotOptions(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getTimepointType());
    }

    @RequestMapping(value = "/boxplot", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedBoxPlot<PkResult, PkResultGroupByOptions>> getBoxPlotData(@RequestBody
                                                                @Valid PkResultBoxPlotRequest requestBody) {
        return pkResultWithResponseService.getBoxPlot(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/boxplot-selection", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public SelectionDetail getSelection(@RequestBody @Valid PkResultBoxPlotSelectionRequest requestBody) {
        return pkResultWithResponseService.getRangedSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }

    @RequestMapping(value = "/filters-subjects", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<String> getSubjects(
            @ApiParam(value = "PkResultRequest: pkResult and Population Filters e.g."
                    + " {pkResultFilters : {}, populationFilters: {}}", required = true)
            @RequestBody PkResultRequest requestBody) {
        return pkResultWithResponseService.getSubjects(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/details-on-demand", method = POST)
    public List<Map<String, String>> getDetailsOnDemandData(
            @ApiParam(value = "Details On Demand Request body: A list of event IDs to get the data for e.g. "
                    + "['ev-1', 'ev-2']", required = true)
            @RequestBody @Valid DetailsOnDemandRequest requestBody) {

        return pkResultWithResponseService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getEventIds(), requestBody.getSortAttrs(),
                requestBody.getStart(), (long) requestBody.getEnd() - requestBody.getStart());
    }

    @RequestMapping(value = "/recist-details-on-demand", method = POST)
    public List<Map<String, String>> getRecistDetailsOnDemandData(
            @ApiParam(value = "Details On Demand Request body: A list of event IDs to get the data for e.g. "
                    + "['ev-1', 'ev-2']", required = true)
            @RequestBody @Valid DetailsOnDemandRequest requestBody) {

        return pkResultWithResponseService.getRecistDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getEventIds(), requestBody.getSortAttrs(),
                requestBody.getStart(), (long) requestBody.getEnd() - requestBody.getStart());
    }

    @RequestMapping(value = "/download-details-on-demand", method = POST)
    public void getAllDetailsOnDemandData(@RequestBody @Valid PkResultRequest requestBody, HttpServletResponse response) throws IOException {

        setDownloadHeaders(response);
        pkResultWithResponseService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/download-selected-details-on-demand", method = POST)
    public void downloadSelectedDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody,
                                                    HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        pkResultWithResponseService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(),
                requestBody.getEventIds(), response.getWriter());
    }

    @RequestMapping(value = "/download-recist-details-on-demand", method = POST)
    public void getAllRecistDetailsOnDemandData(@RequestBody @Valid PkResultRequest requestBody, HttpServletResponse response) throws IOException {

        setDownloadHeaders(response);
        pkResultWithResponseService.writeAllRecistDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/download-recist-selected-details-on-demand", method = POST)
    public void downloadRecistSelectedDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody,
                                                    HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        pkResultWithResponseService.writeRecistSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(),
                requestBody.getEventIds(), response.getWriter());
    }
}
