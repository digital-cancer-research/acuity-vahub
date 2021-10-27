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
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.service.event.CIEventService;
import com.acuity.visualisations.rawdatamodel.service.event.CerebrovascularService;
import com.acuity.visualisations.rawdatamodel.service.event.CvotEndpointService;
import com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesAnyService;
import com.acuity.visualisations.rawdatamodel.service.ae.chord.AeChordDiagramService;
import com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesDeathOutcomeService;
import com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesSaeLeadToDiscService;
import com.acuity.visualisations.rawdatamodel.service.ae.summaries.AeSummariesMstCmnService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSummariesTable;
import com.acuity.visualisations.rawdatamodel.vo.AesTable;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputChordDiagramData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.ChordDiagramSelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae.TermLevel;
import com.acuity.visualisations.rest.model.request.aes.AeChordDownloadRequest;
import com.acuity.visualisations.rest.model.request.aes.AesAssociatedAesNumbersRequest;
import com.acuity.visualisations.rest.model.request.aes.AesBarChartRequest;
import com.acuity.visualisations.rest.model.request.aes.AesBarChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.aes.AesChordDetailsOnDemandRequest;
import com.acuity.visualisations.rest.model.request.aes.AesChordDiagramRequest;
import com.acuity.visualisations.rest.model.request.aes.AesChordSelectionRequest;
import com.acuity.visualisations.rest.model.request.aes.AesPlotRequest;
import com.acuity.visualisations.rest.model.request.aes.AesRequest;
import com.acuity.visualisations.rest.model.request.aes.AesTableRequest;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.va.security.acl.domain.DatasetsRequest;
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

import static com.acuity.visualisations.rest.resources.util.DetailsOnDemandCsvDownloadingUtils.setDownloadHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by ksnd199.
 */
@RestController
@Api(value = "/resources/aes/", description = "rest endpoints for for aes")
@RequestMapping(value = "/resources/aes/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class AesResource {

    @Autowired
    private AeSummariesAnyService aeSummariesAnyService;
    @Autowired
    private AeSummariesMstCmnService aeSummariesMstCmnService;
    @Autowired
    private AeSummariesDeathOutcomeService aeSummariesDeathOutcomeService;
    @Autowired
    private AeSummariesSaeLeadToDiscService aeSummariesSaeLeadToDiscService;
    @Autowired
    private AeService aesService;
    @Autowired
    private AeChordDiagramService aeChordDiagramService;
    @Autowired
    private CIEventService cIEventService;
    @Autowired
    private CvotEndpointService cvotEndpointService;
    @Autowired
    private CerebrovascularService cerebrovascularService;

    /**
     * Gets available aes filters for the specified detect VA security id
     *
     * @param requestBody selected aes and population filters by client
     * @return available aes filters
     */
    @ApiOperation(
            value = "Gets the available aes filters for the currently selected aes and population filters",
            nickname = "availableAesFilters",
            response = AeFilters.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/filters", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AeFilters getAvailableFilters(
            @ApiParam(value = "AesRequest:  Aes and Population Filters e.g. {aesFilters : {}, populationFilters: {}}", required = true)
            @RequestBody AesRequest requestBody) {

        return (AeFilters) aesService.
                getAvailableFilters(requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets the subjects in available aes filters for the currently selected aes and population filters",
            nickname = "getSubjects",
            response = AeFilters.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/filters-subjects", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<String> getSubjects(
            @ApiParam(value = "AesRequest:  Aes and Population Filters e.g. {aesFilters : {}, populationFilters: {}}", required = true)
            @RequestBody AesRequest requestBody) {

        return aesService.getSubjects(requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets the available trellising and options",
            nickname = "availableTrellising",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/trellising", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<AeGroupByOptions>> getAvailableTrellising(
            @ApiParam(value = "AesRequest:  Aes and Population Filters e.g. {aesFilters : {}, populationFilters: {}}", required = true)
            @RequestBody AesRequest requestBody) {

        return aesService.getTrellisOptions(
                requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/colorby-options", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<AeGroupByOptions>> getAvailableBarChartColorBy(
            @ApiParam(value = "AesRequest:  Aes and Population Filters e.g. {aesFilters : {}, populationFilters: {}}", required = true)
            @RequestBody AesRequest requestBody) {

        return aesService.getBarChartColorByOptions(
                requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "chord/colorby-options", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<AeGroupByOptions>> getAvailableChordDiagramColorBy(
            @ApiParam(value = "AesRequest:  Aes, Population Filters and settings e.g. {aesFilters : {}, populationFilters : {}, settings : {}", required = true)
            @RequestBody AesPlotRequest requestBody) {

        return aeChordDiagramService.getChordDiagramColorByOptions(
                requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSettings().getSettings());
    }

    @ApiOperation(
            value = "Gets the values for the aes subject counts barcharts",
            nickname = "getValuesForBarChart",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/countsbarchart", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedBarChart<Ae, AeGroupByOptions>> getValuesForBarChart(
            @ApiParam(value = "AesBarChartRequest: Request parameters for the box plot stats e.g. "
                    + "{trellising : [{trellisedBy: 'ARM',"
                    + " options: ['Placebo']}], countType: 'COUNT', categoryType: 'PT',"
                    + " aesFilters: {}, populationFilters: {}}", required = true)
            @RequestBody @Valid AesBarChartRequest requestBody) {

        return aesService.getBarChart(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getCountType()
        );
    }

    @RequestMapping(value = "/countsbarchart-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<AeGroupByOptions> getBarChartXAxis(
            @RequestBody @Valid AesRequest requestBody) {
        return aesService.getAvailableBarChartXAxis(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets selection details for aes counts and overtime",
            nickname = "getSelectionDetails",
            response = SelectionDetail.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/selection", method = POST)
    public SelectionDetail getSelectionDetails(
            @ApiParam(value = "BarChartSelectionRequest: Request parameters for the bar chart selection e.g. "
                    + "{trellising : [{trellisedBy: 'ARM', option: 'Placebo'}], series: [{trellisedBy: 'SEX', options: ['M', 'F']}], "
                    + "countType: 'ACTUAL_SUBJECT_COUNT', categoryType: 'PT', xMin: 0, xMax: 10, yMin: 10.0, yMax: 12.0, "
                    + "populationFilters: {}, aesFilters: {}}", required = true)
            @RequestBody @Valid AesBarChartSelectionRequest requestBody) {

        return aesService.getSelectionDetails(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection(),
                requestBody.getCountType());
    }

    @RequestMapping(value = "/overtime-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<AeGroupByOptions> getOvertimeXAxis(
            @RequestBody @Valid AesRequest requestBody) {
        return aesService.getAvailableOverTimeChartXAxis(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets data for aes over time chart",
            nickname = "getValuesForBarLineChart",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/overtime", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedOvertime<Ae, AeGroupByOptions>> getValuesForBarLineChart(
            @ApiParam(value = "Request parameters for the aes over time trellising e.g. "
                    + "{trellising : [{trellisedBy: 'ARM', options: ['Placebo']}],"
                    + "periodType: 'DAYS_SINCE_FIRST_TREATMENT', categoryType: 'COUNTS_INCLUDING_DURATION',"
                    + "populationFilters: {}, aesFilters: {}}", required = true)
            @RequestBody @Valid AesPlotRequest requestBody) {

        return aesService.getLineBarChart(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters()
        );

    }

    @ApiOperation(
            value = "Gets data for aes chord plot",
            nickname = "getChordDiagram",
            response = OutputChordDiagramData.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/chord", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public Map<TermLevel, OutputChordDiagramData> getChordDiagram(
            @ApiParam(value = "Request parameters for the aes chord plot", required = true)
            @RequestBody @Valid AesChordDiagramRequest requestBody) {
        return aeChordDiagramService.getAesOnChordDiagram(
                requestBody.getDatasetsObject(),
                requestBody.getAdditionalSettings(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters()
        );
    }

    @ApiOperation(
            value = "Gets selection details for the chord diagram",
            nickname = "getChordSelectionDetails",
            response = ChordDiagramSelectionDetail.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/chord-selection", method = POST)
    public ChordDiagramSelectionDetail getChordSelectionDetails(
            @ApiParam(value = "AesChordSelectionRequest: Request parameters for the chord diagram selection", required = true)
            @RequestBody @Valid AesChordSelectionRequest requestBody) {
        return aeChordDiagramService.getChordDiagramSelectionDetails(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection(),
                requestBody.getAdditionalSettings());
    }

    @ApiOperation(
            value = "Gets the data for the details on demand table for the chord diagram",
            nickname = "getChordDetailsOnDemandData",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/chord-details-on-demand", method = POST)
    public List<Map<String, String>> getChordDetailsOnDemandData(
            @ApiParam(value = "Details On Demand Request body: A list of event IDs to get the data for e.g. "
                    + "['ev-1', 'ev-2']", required = true)
            @RequestBody @Valid AesChordDetailsOnDemandRequest requestBody) throws NoSuchFieldException {

        return aeChordDiagramService.getChordDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getEventIds(), requestBody.getSortAttrs(),
                requestBody.getStart(), (long) requestBody.getEnd() - requestBody.getStart());
    }

    @ApiOperation(
            value = "Downloads all of the data for the chord diagram details on demand table",
            nickname = "downloadAllChordDetailsOnDemandData",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/chord-download-details-on-demand", method = POST)
    public void downloadAllChordDetailsOnDemandData(@RequestBody @Valid AeChordDownloadRequest requestBody,
                                               HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        aeChordDiagramService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters(), requestBody.getAdditionalSettings());
    }

    @ApiOperation(
            value = "Downloads data for the chord diagram details on demand table for the selected IDs",
            nickname = "downloadSelectedChordDetailsOnDemandData",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/chord-download-selected-details-on-demand", method = POST)
    public void downloadSelectedChordDetailsOnDemandData(@RequestBody @Valid AesChordDetailsOnDemandRequest requestBody,
                                                    HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        aeChordDiagramService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(),
                response.getWriter());
    }

    @ApiOperation(
            value = "Gets the data for the details on demand table",
            nickname = "getDetailsOnDemandData",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/details-on-demand", method = POST)
    public List<Map<String, String>> getDetailsOnDemandData(
            @ApiParam(value = "Details On Demand Request body: A list of event IDs to get the data for e.g. "
                    + "['ev-1', 'ev-2']", required = true)
            @RequestBody @Valid DetailsOnDemandRequest requestBody) throws NoSuchFieldException {

        return aesService.getDetailsOnDemandData(
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
    public void downloadAllDetailsOnDemandData(@RequestBody @Valid AesRequest requestBody,
                                               HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        aesService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
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
        aesService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(), response.getWriter());
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
            @RequestBody @Valid SingleSubjectRequest<AeFilters> requestBody) throws NoSuchFieldException {

        return aesService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @ApiOperation(
            value = "Gets data for the AEs Table tab",
            nickname = "getAesTableData",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/aes-table", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<AesTable> getAesTableData(
            @ApiParam(value = "Filters and AE Level to get the data for", required = true)
            @RequestBody @Valid AesTableRequest requestBody) throws NoSuchFieldException {

        return aesService.getAesTableData(
                requestBody.getDatasetsObject(), requestBody.getAeLevel(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets the Associated Aes numbers from event ids",
            nickname = "getAssociatedAesNumbersFromEventIds",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/associatedaesnumbers", method = POST)
    public List<String> getAssociatedAesNumbersFromEventIds(
            @ApiParam(value = "Filters and other info to get the data for", required = true)
            @RequestBody @Valid AesAssociatedAesNumbersRequest requestBody) throws NoSuchFieldException {

        if ("cerebrovascular".equals(requestBody.getFromPlot())) {
            return cerebrovascularService.getAssociatedAeNumbersFromEventIds(
                    requestBody.getDatasetsObject(), requestBody.getPopulationFilters(), requestBody.getEventIds());

        } else if ("cievents".equals(requestBody.getFromPlot())) {
            return cIEventService.getAssociatedAeNumbersFromEventIds(
                    requestBody.getDatasetsObject(), requestBody.getPopulationFilters(), requestBody.getEventIds());

        } else if ("cvot".equals(requestBody.getFromPlot())) {
            return cvotEndpointService.getAssociatedAeNumbersFromEventIds(
                    requestBody.getDatasetsObject(), requestBody.getPopulationFilters(), requestBody.getEventIds());
        }

        throw new IllegalStateException("Unknown fromPlot: " + requestBody.getFromPlot());
    }

    @ApiOperation(
            value = "Gets data for the AEs Table export",
            nickname = "getAesTableExportData",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/aes-table-export", method = POST)
    public void getAesTableExportData(
            @ApiParam(value = "Filters and AE Level to get the export data for", required = true)
            @RequestBody @Valid AesTableRequest requestBody, HttpServletResponse response) throws IOException {
        setDownloadHeaders(response, "aes_table.csv");
        aesService.writeAesTableToCsv(
                requestBody.getDatasetsObject(),
                requestBody.getAeLevel(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                response.getWriter());
    }
    @ApiOperation(
            value = "Gets AEs Table data for Azure Machine Learning needs",
            nickname = "getAesMLTableDataCsv",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/aes-ml-export", method = POST)
    public void getAesMLTableDataCsv(
            @ApiParam(value = "Datasets to get the export data for Azure ML needs", required = true)
            @RequestBody @Valid DatasetsRequest requestBody, HttpServletResponse response) throws IOException {
        setDownloadHeaders(response, "aes_table.csv");
        aesService.writeAMLDataCsv(requestBody.getDatasetsObject(), response.getWriter(), Ae.class, AeRaw.class);
    }

    @RequestMapping(value = "/summaries-any-category", method = POST)
    public List<AeSummariesTable> getAesSummariesAnyCategoryTable(
            @ApiParam(value = "DatasetsRequest: {datasets : {}}", required = true)
            @RequestBody @Valid DatasetsRequest requestBody) {
        return aeSummariesAnyService.getAesSummariesTable(requestBody.getDatasetsObject());
    }

    @RequestMapping(value = "/summaries-mst-cmn", method = POST)
    public List<AeSummariesTable> getAesSummariesMostCommonTable(
            @ApiParam(value = "DatasetsRequest: {datasets : {}}", required = true)
            @RequestBody @Valid DatasetsRequest requestBody) {
        return aeSummariesMstCmnService.getAesSummariesTable(requestBody.getDatasetsObject());
    }

    @RequestMapping(value = "/summaries-death-outcome", method = POST)
    public List<AeSummariesTable> getAesSummariesDeathOutcomeTable(
            @ApiParam(value = "DatasetsRequest: {datasets : {}}", required = true)
            @RequestBody @Valid DatasetsRequest requestBody) {
        return aeSummariesDeathOutcomeService.getAesSummariesTable(requestBody.getDatasetsObject());
    }

    @RequestMapping(value = "/summaries-sae-lead-to-disc", method = POST)
    public List<AeSummariesTable> getAesSummariesSaeLeadToDiscOfStudyTreatment(
            @ApiParam(value = "DatasetsRequest: {datasets : {}}", required = true)
            @RequestBody @Valid DatasetsRequest requestBody) {
        return aeSummariesSaeLeadToDiscService
                .getAesSummariesTable(requestBody.getDatasetsObject());
    }
}
