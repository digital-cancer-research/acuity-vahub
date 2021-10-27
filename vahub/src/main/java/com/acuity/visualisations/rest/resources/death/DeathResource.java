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

package com.acuity.visualisations.rest.resources.death;

import com.acuity.visualisations.rawdatamodel.filters.DeathFilters;
import com.acuity.visualisations.rawdatamodel.service.event.DeathService;
import com.acuity.visualisations.rest.model.request.death.DeathRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.resources.DetailsOnDemandCsvDownloader;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
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
@Api(value = "/resources/death/AcuityDataset/vaSecurityId", description = "rest endpoints for death")
@RequestMapping(value = "/resources/death/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class DeathResource extends DetailsOnDemandCsvDownloader {
    @Autowired
    private DeathService deathService;

    @ApiOperation(
            value = "Gets the available death filters for the currently selected death and population filters",
            nickname = "availableDeathFilters",
            response = DeathFilters.class,
            httpMethod = "POST"
    )
    @PostMapping("filters")
    @Cacheable
    public DeathFilters getAvailableFilters(
            @ApiParam(value =
                    "DeathRequest:  Death and Population Filters e.g. {deathFilters : {}, populationFilters: {}}",
                    required = true)
            @RequestBody @Valid DeathRequest requestBody) {
        return (DeathFilters) deathService.getAvailableFilters(
                requestBody.getDatasetsObject(),
                requestBody.getDeathFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets all data for a single subject",
            nickname = "getSingleSubjectData",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping("single-subject")
    @Cacheable
    public DetailsOnDemandResponse getSingleSubjectData(
            @ApiParam(value = "Single Subject Request body: The subject ID to get the data for", required = true)
            @RequestBody @Valid SingleSubjectRequest<DeathFilters> requestBody) {
        return new DetailsOnDemandResponse(deathService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(),
                requestBody.getSubjectId(),
                requestBody.getEventFilters()));
    }
}
