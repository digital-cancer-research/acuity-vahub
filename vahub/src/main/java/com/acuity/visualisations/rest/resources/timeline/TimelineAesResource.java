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

import com.acuity.visualisations.rawdatamodel.service.timeline.AeTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.aes.SubjectAesDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.aes.SubjectAesSummary;
import com.acuity.visualisations.rest.model.request.aes.AesTimelineRequest;
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

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by ksnd199.
 */
@RestController
@Api(value = "/resources/timeline/aes/", description = "rest endpoints for for aes timeline")
@RequestMapping(value = "/resources/timeline/aes/",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class TimelineAesResource {

    @Autowired
    private AeTimelineService timelineAesService;

    /**
     * Gets list of aes summaries
     *
     * @param requestBody selected population filters and aes filters by client
     * @return list of summaries
     */
    @ApiOperation(
            value = "Gets the aes summary information for the timeline for the currently selected population and aes filters",
            nickname = "getAesSummaries",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/aessummaries", method = POST)
    @Cacheable
    public List<SubjectAesSummary> getAesSummaries(
            @ApiParam(value = "TimelineAesRequest:  Aes and Population Filters e.g. {aes: {}, populationFilters: {}}", required = true)
            @RequestBody @Valid AesTimelineRequest requestBody) {

        return timelineAesService.
                getAesSummaries(requestBody.getDatasetsObject(), requestBody.getAesFilters(),
                        requestBody.getPopulationFilters(), requestBody.getDayZero()
                                .getValue(), requestBody.getDayZero().getStringarg()
                );
    }

    /**
     * Gets list of aes details
     *
     * @param requestBody selected population filters and aes filters by client
     * @return list of summaries
     */
    @ApiOperation(
            value = "Gets the aes detail information for the timeline for the currently selected population and aes filters",
            nickname = "getAesDetails",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/aesdetails", method = POST)
    @Cacheable
    public List<SubjectAesDetail> getAesDetails(
            @ApiParam(value = "AesTimelineRequest:  Aes and Population Filters e.g. {aes: {}, populationFilters: {}}", required = true)
            @RequestBody @Valid AesTimelineRequest requestBody) {

        return timelineAesService.
                getAesDetails(requestBody.getDatasetsObject(), requestBody.getAesFilters(),
                        requestBody.getPopulationFilters(), requestBody.getDayZero()
                                .getValue(), requestBody.getDayZero().getStringarg()
                );
    }
}
