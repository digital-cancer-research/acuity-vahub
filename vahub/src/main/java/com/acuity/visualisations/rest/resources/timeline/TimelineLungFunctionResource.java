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

import com.acuity.visualisations.rawdatamodel.service.timeline.LungFunctionTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.SubjectLungFunctionDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.SubjectLungFunctionSummary;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionTimelineRequest;
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
@Api(value = "/resources/timeline/lung-function/", description = "rest endpoints for for lungfunction timeline")
@RequestMapping(value = "/resources/timeline/lung-function/",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class TimelineLungFunctionResource {

    @Autowired
    private LungFunctionTimelineService timelineLungFunctionService;

    @ApiOperation(
            value = "Gets the lungfunction summary information for the timeline for the currently selected population and lungfunction filters",
            nickname = "getLungFunctionSummaries",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping("summaries")
    @Cacheable
    public List<SubjectLungFunctionSummary> getLungFunctionSummaries(
            @ApiParam(value = "TimelineLungFunctionRequest:  LungFunction and Population Filters e.g. {lungfunction: {}, populationFilters: {}}",
                    required = true)
            @RequestBody @Valid LungFunctionTimelineRequest requestBody) {

        return timelineLungFunctionService.getLungFunctionSummaries(
                requestBody.getDatasetsObject(),
                requestBody.getLungFunctionFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg());
    }

    @ApiOperation(
            value = "Gets the lungfunction detail information for the timeline for the currently selected population and lungfunction filters",
            nickname = "getLungFunctionDetails",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping("details")
    @Cacheable
    public List<SubjectLungFunctionDetail> getLungFunctionDetails(
            @ApiParam(value = "TimelineStatusRequest:  LungFunction and Population Filters e.g. {lungfunction: {}, populationFilters: {}}", required = true)
            @RequestBody @Valid LungFunctionTimelineRequest requestBody) {

        return timelineLungFunctionService.getLungFunctionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getLungFunctionFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg());
    }
}
