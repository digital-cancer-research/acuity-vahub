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

package com.acuity.visualisations.rest.resources.timeline;

import com.acuity.visualisations.rawdatamodel.service.timeline.LabTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.SubjectLabsCategories;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.SubjectLabsDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.SubjectLabsSummary;
import com.acuity.visualisations.rest.model.request.labs.LabsTimelineRequest;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "/resources/timeline/labs/", description = "rest endpoints for for labs timeline")
@RequestMapping(value = "/resources/timeline/labs/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class TimelineLabsResource {

    @Autowired
    private LabTimelineService labTimelineService;

    @ApiOperation(
            "Gets the labs summary information for the timeline for the currently selected population and labs filters"
    )
    @PostMapping("summaries")
    @Cacheable
    public List<SubjectLabsSummary> getLabsSummaries(
            @ApiParam("TimelineLabsRequest:  Labs and Population Filters e.g. {labs: {}, populationFilters: {}}")
            @RequestBody @Valid LabsTimelineRequest requestBody) {
        return labTimelineService.getTimelineSummaries(
                requestBody.getDatasetsObject(),
                requestBody.getLabsFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg());
    }

    @ApiOperation(
            "Gets the labs detail information for the timeline for the currently selected population and labs filters"
    )
    @PostMapping("details")
    @Cacheable
    public List<SubjectLabsDetail> getLabsDetails(
            @ApiParam("TimelineStatusRequest:  Labs and Population Filters e.g. {labs: {}, populationFilters: {}}")
            @RequestBody @Valid LabsTimelineRequest requestBody) {
        return labTimelineService.getTimelineDetails(
                requestBody.getDatasetsObject(),
                requestBody.getLabsFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg());
    }

    @ApiOperation(
            "Gets the labs category summary information for the timeline for the currently selected population and labs filters"
    )
    @PostMapping("categories")
    @Cacheable
    public List<SubjectLabsCategories> getLabsCategories(
            @ApiParam("TimelineStatusRequest:  Labs and Population Filters e.g. {labs: {}, populationFilters: {}}")
            @RequestBody @Valid LabsTimelineRequest requestBody) {
        return labTimelineService.getTimelineCategories(
                requestBody.getDatasetsObject(),
                requestBody.getLabsFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg());
    }
}
