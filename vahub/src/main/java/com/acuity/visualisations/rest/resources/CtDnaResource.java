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
import com.acuity.visualisations.rawdatamodel.filters.CtDnaFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.service.event.CtDnaService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.TrellisedLineFloatChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import com.acuity.visualisations.rest.model.request.ctdna.CtDnaLineChartRequest;
import com.acuity.visualisations.rest.model.request.ctdna.CtDnaLineChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.ctdna.CtDnaRequest;
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

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.NAME;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rest.resources.util.DetailsOnDemandCsvDownloadingUtils.setDownloadHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/resources/ctdna/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
public class CtDnaResource {

    @Autowired
    private CtDnaService ctDnaService;

    @ApiOperation(
            value = "Gets the values for the CtDna linechart",
            nickname = "linechart",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/linechart", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedLineFloatChart<CtDna, CtDnaGroupByOptions, OutputLineChartData>> getLineChart(
            @ApiParam(value = "LineChartRequest: Request parameters for the linechart", required = true)
            @RequestBody CtDnaLineChartRequest requestBody) {

        return ctDnaService.getLineChart(
                requestBody.getDatasetsObject(),
                getCtDnaLineChartSettings(requestBody.getSettings().getSettings()),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters()
        );
    }

    @RequestMapping(value = "/linechart-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<CtDnaGroupByOptions> getBoxPlotXAxis(@RequestBody @Valid CtDnaRequest requestBody) {
        return ctDnaService.getAvailableLineChartXAxis(
                requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/colorby-options", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<CtDnaGroupByOptions>> getAvailableColorBy(
            @ApiParam(value = "CtDnaRequest:  CtDna and Population Filters e.g. {ctDnaFilters : {}, "
                    + "populationFilters: {}}",
                    required = true)
            @RequestBody @Valid CtDnaRequest requestBody) {

        return ctDnaService.getColorBy(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    /**
     * Gets available CtDna filters
     *
     * @param requestBody selected CtDna filters by client
     * @return available CtDna filters
     */
    @ApiOperation(
            value = "Gets the available CtDna filters for the currently selected CtDna filters",
            nickname = "availableFilters",
            response = CtDnaFilters.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/filters", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public Filters<CtDna> getAvailableFilters(
            @ApiParam(value = "CtDnaRequest: CtDna and Population Filters e.g. {ctDnaFilters : {}, populationFilters: {}}", required = true)
            @RequestBody CtDnaRequest requestBody) {

        return ctDnaService.getAvailableFilters(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets the subjects in available ctDNA filters for the currently selected ctDNA and population filters",
            nickname = "getSubjects",
            response = CtDnaFilters.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/filters-subjects", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<String> getSubjects(
            @ApiParam(value = "CtDnaRequest: ctDna and Population Filters e.g. {ctDnaFilters : {}, populationFilters: {}}", required = true)
            @RequestBody CtDnaRequest requestBody) {
        return ctDnaService.getSubjects(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    private ChartGroupByOptions<CtDna, CtDnaGroupByOptions> getCtDnaLineChartSettings(ChartGroupByOptions<CtDna, CtDnaGroupByOptions> settings) {
        // Y_AXIS and COLOR_BY come from the front-end
        return settings.toBuilder()
                .withOption(SERIES_BY, CtDnaGroupByOptions.SUBJECT_GENE_MUT.getGroupByOptionAndParams())
                .withOption(NAME, CtDnaGroupByOptions.SUBJECT_GENE_MUT_VAF.getGroupByOptionAndParams())
                .build();
    }

    @RequestMapping(value = "/selection", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public SelectionDetail getSelection(@RequestBody @Valid CtDnaLineChartSelectionRequest requestBody) {
        return ctDnaService.getLineChartSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }

    @RequestMapping(value = "/details-on-demand", method = POST)
    public List<Map<String, String>> getDetailsOnDemandData(
            @ApiParam(value = "Details On Demand Request body: A list of event IDs to get the data for e.g. "
                    + "['ev-1', 'ev-2']", required = true)
            @RequestBody @Valid DetailsOnDemandRequest requestBody) {

        return ctDnaService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getEventIds(), requestBody.getSortAttrs(),
                requestBody.getStart(), (long) requestBody.getEnd() - requestBody.getStart());
    }

    @ApiOperation(
            value = "Downloads all of the data for the details on demand table",
            nickname = "downloadAllDetailsOnDemandData",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/download-details-on-demand", method = POST)
    public void downloadAllDetailsOnDemandData(@RequestBody @Valid CtDnaRequest requestBody,
                                               HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        ctDnaService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
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
        ctDnaService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(), response.getWriter());
    }

}
