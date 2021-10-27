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
import com.acuity.visualisations.rawdatamodel.filters.AssessedTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.TherapyFilters;
import com.acuity.visualisations.rawdatamodel.service.event.TumourColumnRangeService;
import com.acuity.visualisations.rawdatamodel.service.event.TumourLineChartService;
import com.acuity.visualisations.rawdatamodel.service.event.TumourWaterfallService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ATLGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AssessmentAxisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySettingsBuilder;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.GroupByOptionAndParams;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedColumnRangeChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedWaterfallChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.TrellisedLineFloatChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.visualisations.rest.model.request.tumour.TumourColumnRangeRequest;
import com.acuity.visualisations.rest.model.request.tumour.TumourColumnRangeSelectionRequest;
import com.acuity.visualisations.rest.model.request.tumour.TumourRequest;
import com.acuity.visualisations.rest.model.request.tumour.TumourSelectionRequest;
import com.acuity.visualisations.rest.model.request.tumour.TumourTherapyRequest;
import com.acuity.visualisations.rest.model.request.tumour.TumourTherapyRequestExtended;
import com.acuity.visualisations.rest.model.request.tumour.TumourTldRequest;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.model.request.SubjectIdsRequest;
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

import static com.acuity.visualisations.rawdatamodel.service.event.TumourWaterfallService.WITH_BEST_RESPONSE_EVENTS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ATLGroupByOptions.BEST_RESPONSE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param.ASSESSMENT_TYPE;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param.VALUE;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param.WEEK_NUMBER;
import static com.google.common.collect.Sets.newHashSet;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/resources/tumour/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
public class TumourResource {

    @Autowired
    private TumourWaterfallService tumourWaterfallService;
    @Autowired
    private TumourLineChartService tumourLineChartService;
    @Autowired
    private TumourColumnRangeService tumourColumnRangeService;

    @ApiOperation(
            value = "Gets the available trellising and options",
            nickname = "availableTrellising",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/trellising", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public Set<GroupByOption> getAvailableTrellising(@RequestBody @Valid TumourRequest requestBody) {
        return newHashSet(ATLGroupByOptions.values());
    }

    /**
     * Gets available target lesion filters
     *
     * @param requestBody selected target lesion filters by client
     * @return available target lesion filters
     */
    @ApiOperation(
            value = "Gets the available target lesion filters for the currently selected target lesion filters"
                    + "for the waterfall plot",
            nickname = "getAvailableWaterfallFilters",
            response = AssessedTargetLesionFilters.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/waterfall-filters", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public Filters<AssessedTargetLesion> getAvailableWaterfallFilters(
            @RequestBody TumourTldRequest requestBody) {
        GroupByOptionAndParams<AssessedTargetLesion, ATLGroupByOptions> yAxis = requestBody.getSettings()
                .getSettings()
                .getOptions()
                .get(Y_AXIS);
        GroupByOptionAndParams<AssessedTargetLesion, ATLGroupByOptions> colorBy = requestBody.getSettings()
                .getSettings().getOptions().get(COLOR_BY);
        boolean needToAddBestResponseEvents = BEST_RESPONSE.getGroupByOptionAndParams().equals(colorBy);

        return tumourWaterfallService.getAvailableWaterfallFilters(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters(), getWaterfallSettingsPatched(yAxis, null, needToAddBestResponseEvents));
    }

    @ApiOperation(
            value = "Gets the subjects in available target lesion filters for the currently selected target lesion and population filters",
            nickname = "getSubjects",
            response = TumourRequest.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/filters-subjects", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<String> getSubjects(
            @ApiParam(value = "TumourRequest: Target lesion and Population Filters e.g. {tumourFilters : {}, populationFilters: {}}", required = true)
            @RequestBody TumourRequest requestBody) {
        return tumourWaterfallService.getSubjects(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/therapy-filters", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public TherapyFilters getAvailableFilters(
            @RequestBody TumourTherapyRequestExtended requestBody) {

        return tumourColumnRangeService.
                getAvailableTherapyFilters(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                        requestBody.getPopulationFilters(), requestBody.getTherapiesSettings());
    }

    @ApiOperation(
            value = "Gets the subjects in available 'Prior therapy vs ToC' filters for the currently selected therapy and population filters",
            nickname = "getTherapySubjects",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/therapy-filters-subjects", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<String> getTherapySubjects(
            @ApiParam(value = "TherapyRequest: Therapy and Population Filters e.g. {therapyFilters : {}, populationFilters: {}}", required = true)
            @RequestBody TumourTherapyRequest requestBody) {
        return tumourColumnRangeService.getSubjects(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets the best change in target lesion diameter for each subject on the waterfall chart",
            nickname = "getTumourDataOnWaterfall",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/waterfall", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedWaterfallChart<AssessedTargetLesion, ATLGroupByOptions>> getTumourBestChangeOnWaterfall(
            @ApiParam(value = "TumourRequest: Request parameters for plots e.g. "
                    + "{trellising : [{trellisedBy: 'SUBJECT', options: ['subject1, subject2']}], "
                    + "tumourFilters: {}, populationFilters: {}}", required = true)
            @RequestBody TumourTldRequest requestBody) {

        return tumourWaterfallService.getTumourDataOnWaterfall(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters(),
                getWaterfallSettingsPatched(requestBody.getSettings()));
    }

    @RequestMapping(value = "/waterfall-selection", method = POST)
    public SelectionDetail getWaterfallSelectionDetails(
            @RequestBody @Valid TumourSelectionRequest requestBody) {

        ChartSelection<AssessedTargetLesion, ATLGroupByOptions, ChartSelectionItem<AssessedTargetLesion, ATLGroupByOptions>> patchedSelection
                = new ChartSelection<>(getWaterfallSelectionSettings(), requestBody.getSelection().getSelectionItems());

        final GroupByOptionAndParams<AssessedTargetLesion, ATLGroupByOptions> yAxis = requestBody
                .getSelection().getSettings().getOptions().get(Y_AXIS);
        final GroupByOptionAndParams<AssessedTargetLesion, ATLGroupByOptions> colorBy = requestBody
                .getSelection().getSettings().getOptions().get(COLOR_BY);
        boolean needToAddBestResponseEvents = BEST_RESPONSE.getGroupByOptionAndParams().equals(colorBy);

        return tumourWaterfallService.getWaterfallSelectionDetails(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters(), patchedSelection, getWaterfallSettingsPatched(yAxis, null, needToAddBestResponseEvents));
    }

    @RequestMapping(value = "/waterfall-yaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AssessmentAxisOptions<ATLGroupByOptions> getOvertimeXAxis(
            @RequestBody @Valid TumourRequest requestBody) {
        return tumourWaterfallService.getAvailableWaterfallYAxis(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets the time on compound vs previous therapy for subjects on the column range chart",
            nickname = "getTumourTherapyOnColumnRange",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/prior-therapy", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedColumnRangeChart<Subject, PopulationGroupByOptions>> getTumourTherapyOnColumnRange(
            @ApiParam(value = "TumourRequest: Request parameters for plots e.g. "
                    + "{trellising : [{trellisedBy: 'SUBJECT', options: ['subject1, subject2']}], "
                    + "therapyFilters: {}, populationFilters: {}}", required = true)
            @RequestBody TumourColumnRangeRequest requestBody) {

        return tumourColumnRangeService.getTumourTherapyOnColumnRange(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters(), requestBody.getTocSettings(),
                requestBody.getTherapiesSettings());
    }

    /**
     * Available colorBy options for the right part of "Prior Therapy vs Time on Compound" plot
     */
    @RequestMapping(value = "/prior-therapy-toc-colorby-options", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<PopulationGroupByOptions>> getTOCColorBy(
            @RequestBody @Valid TumourRequest requestBody) {

        return tumourColumnRangeService.getTOCColorBy(requestBody.getDatasetsObject(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/waterfall-colorby-options", method = POST)
    public List<TrellisOptions<ATLGroupByOptions>> getAvailableWaterfallColorBy(
            @RequestBody TumourTldRequest requestBody) {

        return tumourWaterfallService.getWaterfallColorBy(
                requestBody.getDatasetsObject(),
                requestBody.getPopulationFilters(),
                requestBody.getEventFilters(),
                getWaterfallSettingsPatched(requestBody.getSettings()));
    }

    @RequestMapping(value = "/prior-therapy-selection", method = POST)
    public SelectionDetail getTumourTherapySelectionDetails(
            @RequestBody @Valid TumourColumnRangeSelectionRequest requestBody) {

        return tumourColumnRangeService.getSelectionDetails(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters(), requestBody.getSelection());
    }

    @ApiOperation(
            value = "Gets all changes in target lesion diameter for each subject over time on linechart",
            nickname = "getTumourChangesOverTimeOnLinechart",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/linechart", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, OutputLineChartData>> getTumourChangesOverTimeOnLinechart(
            @ApiParam(value = "TumourLinechartRequest: Request parameters for the waterfall plots e.g. "
                    + "{trellising : [{trellisedBy: 'SUBJECT', options: ['subject1, subject2']}], "
                    + "tumourFilters: {}, populationFilters: {}}", required = true)
            @RequestBody TumourTldRequest requestBody) {

        return tumourLineChartService.getTumourAllChangesOnLinechart(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters(), getLineChartSettingsPatched(requestBody
                        .getSettings()));
    }

    @ApiOperation(
            value = "Gets all changes in target lesion diameter for each subject over time on linechart",
            nickname = "getTumourChangesOverTimeOnLinechart",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/linechart-by-lesion", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, OutputLineChartData>> getTLDOverTimeLinechartByLesion(
            @RequestBody TumourTldRequest requestBody) {

        return tumourLineChartService.getTumourChangesByLesionOnLinechart(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters(), getLesionLineChartSettingsPatched(requestBody
                        .getSettings()));
    }

    private ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> getWaterfallSelectionSettings() {
        return ChartGroupByOptions.<AssessedTargetLesion, ATLGroupByOptions>builder()
                .withOption(X_AXIS, ATLGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .build();
    }

    private ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> getTumourLineChartSelectionSettingsPatched(ChartGroupByOptions<AssessedTargetLesion,
            ATLGroupByOptions> input) {
        return ChartGroupByOptions.<AssessedTargetLesion, ATLGroupByOptions>builder()
                .withOption(SERIES_BY, ATLGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .withOption(X_AXIS, input.getOptions()
                        .getOrDefault(X_AXIS, ATLGroupByOptions.DAYS_SINCE_FIRST_DOSE.getGroupByOptionAndParams()))
                .withOption(Y_AXIS, input.getOptions()
                        .getOrDefault(Y_AXIS, ATLGroupByOptions.PERCENTAGE_CHANGE.getGroupByOptionAndParams()))
                .build();
    }

    private ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> getWaterfallSettingsPatched(ChartGroupByOptionsFiltered<AssessedTargetLesion,
            ATLGroupByOptions> input) {
        GroupByOptionAndParams<AssessedTargetLesion, ATLGroupByOptions> colorBy = input.getSettings()
                .getOptions()
                .getOrDefault(COLOR_BY, BEST_RESPONSE.getGroupByOptionAndParams());
        GroupByOptionAndParams<AssessedTargetLesion, ATLGroupByOptions> yAxis = input
                .getSettings()
                .getOptions()
                .get(Y_AXIS);
        return getWaterfallSettingsPatched(yAxis, colorBy, false);
    }

    private ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> getWaterfallSettingsPatched(
            GroupByOptionAndParams<AssessedTargetLesion, ATLGroupByOptions> yAxis,
            GroupByOptionAndParams<AssessedTargetLesion, ATLGroupByOptions> colorBy,
            boolean needToAddBestResponseEvents) {

        ChartGroupBySettingsBuilder<AssessedTargetLesion, ATLGroupByOptions> settings = ChartGroupByOptions.<AssessedTargetLesion, ATLGroupByOptions>builder()
                .withOption(Y_AXIS,
                        ATLGroupByOptions.PERCENTAGE_CHANGE.getGroupByOptionAndParams(GroupByOption.Params.builder()
                                .with(ASSESSMENT_TYPE, yAxis.getParamMap()
                                        .getOrDefault(ASSESSMENT_TYPE, AssessmentAxisOptions.AssessmentType.BEST_CHANGE))
                                .with(WEEK_NUMBER, yAxis.getParamMap().getOrDefault(WEEK_NUMBER, 0))
                                .with(VALUE, needToAddBestResponseEvents ? WITH_BEST_RESPONSE_EVENTS : "")
                                .build()))
                .withOption(X_AXIS, ATLGroupByOptions.SUBJECT.getGroupByOptionAndParams());
        if (colorBy != null) {
            settings.withOption(COLOR_BY, colorBy.getGroupByOption().getGroupByOptionAndParams());
        }
        return ChartGroupByOptionsFiltered.builder(settings.build()).build();
    }

    private ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> getLineChartSettingsPatched(ChartGroupByOptionsFiltered<AssessedTargetLesion,
            ATLGroupByOptions> input) {
        ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> settings = ChartGroupByOptions.<AssessedTargetLesion, ATLGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, ATLGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, ATLGroupByOptions.ASSESSMENT_RESPONSE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.NAME, ATLGroupByOptions.ASSESSMENT_RESPONSE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS,
                        input.getSettings()
                                .getOptions()
                                .getOrDefault(X_AXIS, ATLGroupByOptions.DAYS_SINCE_FIRST_DOSE.getGroupByOptionAndParams()))
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                        input.getSettings()
                                .getOptions()
                                .getOrDefault(Y_AXIS, ATLGroupByOptions.PERCENTAGE_CHANGE.getGroupByOptionAndParams()))
                .build();
        return ChartGroupByOptionsFiltered.builder(settings).build();
    }

    private ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> getLesionLineChartSettingsPatched(
            ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> input) {
        ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> settings = ChartGroupByOptions.<AssessedTargetLesion, ATLGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, ATLGroupByOptions.SUBJECT_LESION.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, ATLGroupByOptions.ASSESSMENT_RESPONSE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.NAME, ATLGroupByOptions.ASSESSMENT_RESPONSE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS,
                        input.getSettings()
                                .getOptions()
                                .getOrDefault(X_AXIS, ATLGroupByOptions.DAYS_SINCE_FIRST_DOSE.getGroupByOptionAndParams()))
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                        input.getSettings()
                                .getOptions()
                                .getOrDefault(Y_AXIS, ATLGroupByOptions.LESION_PERCENTAGE_CHANGE.getGroupByOptionAndParams()))
                .build();
        return ChartGroupByOptionsFiltered.builder(settings).build();
    }

    @ApiOperation(
            value = "Retrieve selection details over selected TL diameters at linechart",
            nickname = "getSelectionDetailOverLinechartDiameters",
            response = SelectionDetail.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/linechart/selection", method = POST)
    public SelectionDetail getSelectionDetailOverLinechartDiameters(
            @ApiParam(value = "TumourSelectionRequest: Request parameters for obtaining selection details", required = true)
            @RequestBody
                    TumourSelectionRequest requestBody) {

        ChartSelection<AssessedTargetLesion, ATLGroupByOptions, ChartSelectionItem<AssessedTargetLesion, ATLGroupByOptions>> patched
                = new ChartSelection<>(getTumourLineChartSelectionSettingsPatched(requestBody.getSelection()
                .getSettings()),
                requestBody.getSelection().getSelectionItems());
        requestBody.setSelection(patched);
        return tumourLineChartService.getLineChartSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection()
        );
    }

    @RequestMapping(value = "/linechart-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<ATLGroupByOptions> getLineChartXAxis(@ApiParam(
            value = "TumourRequest:  Tumour and Population Filters e.g. {tumourFilters : {}, populationFilters: {}}", required = true)
                                                            @RequestBody @Valid TumourRequest requestBody) {
        return tumourLineChartService.getAvailableLineChartXAxis(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/linechart-by-lesion-xaxis", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public AxisOptions<ATLGroupByOptions> getLineChartByLesionXAxis(@ApiParam(
            value = "TumourRequest:  Tumour and Population Filters e.g. {tumourFilters : {}, populationFilters: {}}", required = true)
                                                                    @RequestBody @Valid TumourRequest requestBody) {
        return tumourLineChartService.getAvailableLineChartByLesionXAxis(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/details-on-demand", method = POST)
    public List<Map<String, String>> getDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody) {

        return tumourLineChartService.getDetailsOnDemandData(
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
    public void downloadAllDetailsOnDemandData(@RequestBody @Valid TumourRequest requestBody,
                                               HttpServletResponse response) throws IOException {

        setDodHeaders(response);
        tumourLineChartService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    private void setDodHeaders(HttpServletResponse response) {
        response.addHeader("Content-disposition", "attachment;filename=details_on_demand.csv");
        response.setContentType("txt/csv");
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

        setDodHeaders(response);
        tumourLineChartService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(), response
                .getWriter());
    }
    @RequestMapping(value = "/selection-by-subjectids", method = POST)
    public SelectionDetail getSelectionBySubjectIds(
            @ApiParam(value = "SubjectIdsRequest: Current datasets and subjects ids", required = true)
            @RequestBody SubjectIdsRequest requestBody) {
        return tumourLineChartService.getSelectionBySubjectIds(requestBody.getDatasetsObject(), requestBody.getSubjectIds());
    }
}
