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

package com.acuity.visualisations.rest.resources.liver;

import com.acuity.visualisations.rawdatamodel.filters.LiverFilters;
import com.acuity.visualisations.rawdatamodel.service.event.LiverService;
import com.acuity.visualisations.rest.model.request.liver.LiverRequest;
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

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "/resources/liver/", description = "rest endpoints for for liver")
@RequestMapping(value = "/resources/liver/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class LiverResource {

    @Autowired
    private LiverService liverService;

    @ApiOperation(
            value = "Gets the available liver filters for the currently selected population filters",
            nickname = "availableLiverFilters",
            response = LiverFilters.class,
            httpMethod = "POST"
    )
    @PostMapping("filters")
    @Cacheable
    public LiverFilters getAvailableFilters(
            @ApiParam(value = "LiverRequest:  Liver and Population Filters e.g. {liverFilters : {}, populationFilters: {}}", required = true)
            @RequestBody LiverRequest requestBody) {

        return (LiverFilters) liverService.getAvailableFilters(requestBody.getDatasetsObject(),
                requestBody.getLiverFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets the subjects in available liver filters for the currently selected population filters",
            nickname = "getSubjects",
            response = LiverFilters.class,
            httpMethod = "POST"
    )
    @PostMapping("filters-subjects")
    @Cacheable
    public List<String> getSubjects(
            @ApiParam(value = "LiverRequest:  Liver and Population Filters e.g. {liverFilters : {}, populationFilters: {}}", required = true)
            @RequestBody LiverRequest requestBody) {

        return liverService.getSubjects(requestBody.getDatasetsObject(), requestBody.getLiverFilters(), requestBody.getPopulationFilters());
    }
}
