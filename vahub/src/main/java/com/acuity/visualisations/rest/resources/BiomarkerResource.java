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

import com.acuity.visualisations.rawdatamodel.filters.BiomarkerFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.service.event.BiomarkerService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.BiomarkerGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.vo.CBioData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedHeatMap;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import com.acuity.visualisations.rest.model.request.biomarkers.BiomarkerCBioRequest;
import com.acuity.visualisations.rest.model.request.biomarkers.BiomarkerRequest;
import com.acuity.visualisations.rest.model.request.biomarkers.BiomarkersHeatMapRequest;
import com.acuity.visualisations.rest.model.request.biomarkers.BiomarkersHeatMapSelectionRequest;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.acuity.visualisations.rest.resources.util.DetailsOnDemandCsvDownloadingUtils.setDownloadHeaders;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping(value = "/resources/biomarker/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
public class BiomarkerResource {

    @Autowired
    private BiomarkerService biomarkerService;

    private ChartGroupByOptionsFiltered<Biomarker, BiomarkerGroupByOptions> getHeatMapSettings() {
        ChartGroupByOptions<Biomarker, BiomarkerGroupByOptions> settings = ChartGroupByOptions.<Biomarker, BiomarkerGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                        BiomarkerGroupByOptions.GENE_PERCENTAGE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, BiomarkerGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.VALUE, BiomarkerGroupByOptions.BIOMARKER_DATA.getGroupByOptionAndParams())
                .build();
        return ChartGroupByOptionsFiltered.builder(settings).build();
    }

    private ChartGroupByOptionsFiltered<Biomarker, BiomarkerGroupByOptions> getHeatMapSelectionSettings() {
        ChartGroupByOptions<Biomarker, BiomarkerGroupByOptions> settings = ChartGroupByOptions.<Biomarker, BiomarkerGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                        BiomarkerGroupByOptions.GENE_PERCENTAGE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, BiomarkerGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .build();
        return ChartGroupByOptionsFiltered.builder(settings).build();
    }

    @ApiOperation(
            value = "Gets the available trellising and options",
            nickname = "availableTrellising",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/trellising", method = POST)
    public Set<TrellisOptions<BiomarkerGroupByOptions>> getAvailableTrellising(
            @ApiParam(value = "Population Filters e.g. {populationFilters: {}}", required = true)
            @RequestBody BiomarkerRequest requestBody) {

        return new HashSet<>();
    }

    @ApiOperation(
            value = "Gets the values for the biomarker heatmap",
            nickname = "heatmap",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/heatmap", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisedHeatMap<Biomarker, BiomarkerGroupByOptions>> getHeatMapValues(
            @ApiParam(value = "BiomarkersHeatMapRequest: Request parameters for the heat maps", required = true)
            @RequestBody BiomarkersHeatMapRequest requestBody) {

        return biomarkerService.getBiomarkerHeatMap(
                requestBody.getDatasetsObject(),
                (requestBody.getSettings() != null ? requestBody.getSettings() : getHeatMapSettings()),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters()
        );
    }

    /**
     * Gets available biomarker filters
     *
     * @param requestBody selected biomarker filters by client
     * @return available biomarker filters
     */
    @ApiOperation(
            value = "Gets the available biomarker filters for the currently selected biomarker filters",
            nickname = "availablePopulationFilters",
            response = BiomarkerFilters.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/filters", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public Filters<Biomarker> getAvailableFilters(
            @ApiParam(value = "BiomarkerRequest: Biomarker and Population Filters e.g. {biomarkerFilters : {}, populationFilters: {}}", required = true)
            @RequestBody BiomarkerRequest requestBody) {

        return biomarkerService.getAvailableFilters(requestBody.getDatasetsObject(), requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Get selection details for chosen biomarkers range",
            nickname = "getSelectionDetails",
            response = SelectionDetail.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/selection-details", method = POST)
    public SelectionDetail getSelectionDetails(
            @ApiParam(value = "HeatMapSelectionRequest")
            @RequestBody BiomarkersHeatMapSelectionRequest requestBody
    ) {
        if (requestBody.getSelection().getSettings() == null) {
            ChartSelection<Biomarker, BiomarkerGroupByOptions, ChartSelectionItem<Biomarker, BiomarkerGroupByOptions>> patched
                    = new ChartSelection<>(getHeatMapSelectionSettings().getSettings(), requestBody.getSelection().getSelectionItems());
            requestBody.setSelection(patched);
        }
        return biomarkerService.getSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection()
        );
    }

    @RequestMapping(value = "/details-on-demand", method = POST)
    public List<Map<String, String>> getDetailsOnDemandData(
            @ApiParam(value = "Details On Demand Request body: A list of event IDs to get the data for e.g. "
                    + "['ev-1', 'ev-2']", required = true)
            @RequestBody @Valid DetailsOnDemandRequest requestBody) {

        return biomarkerService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getEventIds(), requestBody.getSortAttrs(),
                requestBody.getStart(), (long) requestBody.getEnd() - requestBody.getStart());
    }

    @ApiOperation(
            value = "Gets details for the cBio",
            nickname = "cBio",
            response = CBioData.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/cbio-details", method = POST)
    public CBioData getSelectedCBioData(@RequestBody @Valid BiomarkerCBioRequest requestBody) {
        return biomarkerService.getCBioData(requestBody.getDatasetsObject(), requestBody.getEventIds(),
                requestBody.getBiomarkerFilters(), requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Downloads all of the data for the details on demand table",
            nickname = "getAllDetailsOnDemandCsv",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/download-details-on-demand", method = POST)
    public void downloadAllDetailsOnDemandData(@RequestBody @Valid BiomarkerRequest requestBody,
                                               HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        biomarkerService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
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
        biomarkerService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(), response.getWriter());
    }

    @ApiOperation(
            value = "Gets the subjects in available Biomarker filters for the currently selected Biomarker and population filters",
            nickname = "getSubjects",
            response = BiomarkerFilters.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/filters-subjects", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<String> getSubjects(
            @ApiParam(value = "BiomarkerRequest: Biomarker and Population Filters e.g. {biomarkerFilters : {}, populationFilters: {}}", required = true)
            @RequestBody BiomarkerRequest requestBody) {
        return biomarkerService.getSubjects(requestBody.getDatasetsObject(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }

    @RequestMapping(value = "/selection-by-subjectids", method = POST)
    public SelectionDetail getSelectionBySubjectIds(
            @ApiParam(value = "SubjectIdsRequest: Current datasets and subjects ids", required = true)
            @RequestBody SubjectIdsRequest requestBody) {
        return biomarkerService.getSelectionBySubjectIds(requestBody.getDatasetsObject(), requestBody.getSubjectIds());
    }

    @RequestMapping(value = "/colorby-options", method = POST)
    @Cacheable(condition = Constants.EMPTY_EVENT_AND_POPULATION_FILTER)
    public List<TrellisOptions<BiomarkerGroupByOptions>> getAvailableHeatmapColorBy(
            @ApiParam(value = "BiomarkerRequest:  Biomarkers and Population Filters e.g. {BiomarkerFilters : {}, populationFilters: {}}", required = true)
            @RequestBody BiomarkerRequest requestBody) {

        return biomarkerService.getHeatmapColorByOptions(
                requestBody.getDatasetsObject(),
                requestBody.getEventFilters(),
                requestBody.getPopulationFilters());
    }

}
