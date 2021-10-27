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

import com.acuity.visualisations.rawdatamodel.service.timeline.EcgTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.ecg.SubjectEcgDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.ecg.SubjectEcgSummary;
import com.acuity.visualisations.rest.model.request.cardiac.EcgTimelineRequest;
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

/**
 * Created by ksnd199.
 */
@RestController
@Api(value = "/resources/timeline/ecg", description = "rest endpoints for for ecg timeline")
@RequestMapping(value = "/resources/timeline/ecg",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class TimelineEcgResource {

    @Autowired
    private EcgTimelineService ecgTimelineService;

    @ApiOperation(
            value = "Gets the ecg summary information for the timeline for the currently selected population and ecg filters",
            nickname = "getEcgSummary",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping("summaries")
    @Cacheable
    public List<SubjectEcgSummary> getEcgSummaries(
            @ApiParam(value = "TimelineConmdsRequest:  Ecg and Population Filters e.g. {ecgFilters: {}, populationFilters: {}}", required = true)
            @RequestBody @Valid EcgTimelineRequest requestBody) {
        return ecgTimelineService.getSummaries(requestBody.getDatasetsObject(), requestBody.getCardiacFilters(),
                requestBody.getPopulationFilters(), requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg());
    }

    @ApiOperation(
            value = "Gets the ecg by class information for the timeline for the currently selected population and ecg filters",
            nickname = "getEcgDetail",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping("details")
    @Cacheable
    public List<SubjectEcgDetail> getEcgDetail(
            @ApiParam(value = "TimelineEcgRequest:  Ecg and Population Filters e.g. {ecgFilters: {}, populationFilters: {}}", required = true)
            @RequestBody @Valid EcgTimelineRequest requestBody) {
        return ecgTimelineService.getDetails(requestBody.getDatasetsObject(), requestBody.getCardiacFilters(),
                requestBody.getPopulationFilters(), requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg());
    }
}
