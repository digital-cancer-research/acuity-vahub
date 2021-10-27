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
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.service.event.LabService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedShiftPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.visualisations.rest.model.request.labs.LabMeanRangeSelectionRequest;
import com.acuity.visualisations.rest.model.request.labs.LabSelectionRequest;
import com.acuity.visualisations.rest.model.request.labs.LabStatsRequest;
import com.acuity.visualisations.rest.model.request.labs.LabsRequest;
import com.acuity.visualisations.rest.model.request.labs.LabsTrellisRequest;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.resources.util.DetailsOnDemandCsvDownloadingUtils;
import com.acuity.va.security.acl.domain.DatasetsRequest;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by ksnd199.
 */
@RestController
@RequestMapping(value = "/resources/labs", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class LabsResource {

    @Autowired
    private LabService labService;


    @RequestMapping(value = "/filters", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public Filters<Lab> getAvailableFilters(@RequestBody @Valid LabsRequest requestBody) {

        return labService.getAvailableFilters(
                requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/filters-subjects", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<String> getSubjects(@RequestBody @Valid LabsRequest requestBody) {

        return labService.getSubjects(requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/trellising", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<LabGroupByOptions>> getAvailableTrellising(@RequestBody @Valid LabsTrellisRequest requestBody) {
        return labService.getTrellisOptions(
                requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters(), requestBody.getYAxisOption());
    }

    @RequestMapping(value = "/range-trellising", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<LabGroupByOptions>> getAvailableRangeTrellising(@RequestBody @Valid LabsTrellisRequest requestBody) {
        return labService.getRangeTrellisOptions(
                requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters(), requestBody.getYAxisOption());
    }

    @RequestMapping(value = "/range-series-by-options", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<LabGroupByOptions>> getRangeSeriesByOptions(@RequestBody @Valid LabsRequest requestBody) {
        return labService.getRangeSeriesByOptions(
                requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/boxplot-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<LabGroupByOptions> getBoxPlotXAxis(@RequestBody @Valid LabsRequest requestBody) {
        return labService.getAvailableBoxPlotXAxis(
                requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/shift-plot-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<LabGroupByOptions> getShiftPlotXAxis(@RequestBody @Valid LabsRequest requestBody) {
        return labService.getAvailableShiftPlotXAxis(
                requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/range-plot-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<LabGroupByOptions> getRangePlotXAxis(@RequestBody @Valid LabsRequest requestBody) {
        return labService.getAvailableRangePlotXAxis(
                requestBody.getDatasetsObject(), requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/boxplot", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedBoxPlot<Lab, LabGroupByOptions>> getBoxPlotData(@RequestBody @Valid LabStatsRequest requestBody) {
        return labService.getBoxPlot(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/boxplot-selection", method = POST)
    public SelectionDetail getSelection(@RequestBody @Valid LabSelectionRequest requestBody) {
        return labService.getRangedSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }

    @RequestMapping(value = "/mean-range-selection", method = POST)
    public SelectionDetail getMeanRangeSelection(@RequestBody @Valid LabMeanRangeSelectionRequest requestBody) {
        return labService.getSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }

    @RequestMapping(value = "/shift-selection", method = POST)
    public SelectionDetail getShiftPlotSelection(@RequestBody @Valid LabSelectionRequest requestBody) {

        ChartSelection<Lab, LabGroupByOptions, ChartSelectionItemRange<Lab, LabGroupByOptions, Double>> selection = requestBody.getSelection();
        if (selection.getSettings() == null) {
            selection = ChartSelection.of(getShiftPlotSettings(requestBody.getDatasetsObject().isDetectType()), selection.getSelectionItems());
        }
        return labService.getRangedSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                selection);
    }

    @RequestMapping(value = "/mean-range-plot", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedRangePlot<Lab, LabGroupByOptions>> getMeanRangePlot(@RequestBody @Valid LabStatsRequest requestBody) {

        return labService.getRangePlot(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getStatType());
    }

    @RequestMapping(value = "/shift-plot", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedShiftPlot<Lab, LabGroupByOptions>> getDataForErrorChart(@RequestBody LabStatsRequest requestBody) {

        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settings = ChartGroupByOptionsFiltered
                .builder(getShiftPlotSettings(requestBody.getDatasetsObject().isDetectType()))
                .withFilterByTrellisOptions(requestBody.getSettings().getFilterByTrellisOptions())
                .build();
        return labService.getShiftPlot(
                requestBody.getDatasetsObject(), settings, requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    private ChartGroupByOptions<Lab, LabGroupByOptions> getShiftPlotSettings(boolean withArmTrellis) {
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Lab, LabGroupByOptions> builder = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.BASELINE_VALUE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.UNIT, LabGroupByOptions.UNIT.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams());
        if (withArmTrellis) {
            builder.withTrellisOption(LabGroupByOptions.ARM.getGroupByOptionAndParams());
        }
        return builder.build();
    }

    @RequestMapping(value = "/details-on-demand", method = POST)
    public List<Map<String, String>> getDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody) throws NoSuchFieldException {

        return labService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(),
                requestBody.getEventIds(),
                requestBody.getSortAttrs(),
                requestBody.getStart(),
                requestBody.getEnd());
    }

    @RequestMapping(value = "/download-details-on-demand", method = POST)
    public void getAllDetailsOnDemandData(@RequestBody @Valid LabsRequest requestBody, HttpServletResponse response) throws IOException {

        DetailsOnDemandCsvDownloadingUtils.setDownloadHeaders(response);
        labService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/download-selected-details-on-demand", method = POST)
    public void downloadSelectedDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody, HttpServletResponse response) throws IOException {
        DetailsOnDemandCsvDownloadingUtils.setDownloadHeaders(response, "details_on_demand.csv");
        labService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(), response.getWriter());
    }

    @RequestMapping(value = "/single-subject", method = POST)
    @Cacheable(condition = "#requestBody.getEventFilters().isEmpty()")
    public List<Map<String, String>> getSingleSubjectData(@RequestBody @Valid SingleSubjectRequest<LabFilters> requestBody) throws NoSuchFieldException {

        return labService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @ApiOperation(
            value = "Gets Labs Table data for Azure Machine Learning needs",
            nickname = "getLabMLTableDataCsv",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/lab-ml-export", method = POST)
    public void getLabMLTableDataCsv(
            @ApiParam(value = "Datasets to get the export data for Azure ML needs", required = true)
            @RequestBody @Valid DatasetsRequest requestBody, HttpServletResponse response) throws IOException {
        DetailsOnDemandCsvDownloadingUtils.setDownloadHeaders(response, "labs_table.csv");
        labService.writeAMLDataCsv(requestBody.getDatasetsObject(), response.getWriter(), Lab.class, LabRaw.class);
    }
}
