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

package com.acuity.visualisations.rest.resources.conmeds;

import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.service.event.ConmedsService;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.conmeds.ConmedsRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
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
@Api(description = "rest endpoints for conmeds")
@RequestMapping(value = "/resources/conmeds/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@RequiredArgsConstructor
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class ConmedsResource {

    private final ConmedsService conmedsService;

    @ApiOperation("Gets the available conmed filters for the currently selected conmeds and population filters")
    @PostMapping("filters")
    @Cacheable
    public ConmedFilters getAvailableFilters(
            @ApiParam("ConmedRequest:  Conmeds and Population Filters e.g. {conmedsFilters : {}, populationFilters: {}}")
            @RequestBody @Valid ConmedsRequest requestBody) {
        return (ConmedFilters) conmedsService.getAvailableFilters(requestBody.getDatasetsObject(),
                requestBody.getConmedsFilters(), requestBody.getPopulationFilters());
    }

    @ApiOperation("Gets the subjects in available conmed filters for the currently selected conmed and population filters")
    @PostMapping("filtered-subjects")
    @Cacheable
    public List<String> getSubjects(
            @ApiParam("ConmedsRequest:  conmeds and Population Filters e.g. {conmedsFilters : {}, populationFilters: {}}")
            @RequestBody ConmedsRequest requestBody) {
        return conmedsService.getSubjects(requestBody.getDatasetsObject(), requestBody.getConmedsFilters(),
                        requestBody.getPopulationFilters());
    }

    @ApiOperation("Gets all conmeds data for a single subject")
    @PostMapping("single-subject")
    @Cacheable
    public DetailsOnDemandResponse getSingleSubjectData(
            @ApiParam("Single Subject Request body: The subject ID to get the data for")
            @RequestBody @Valid SingleSubjectRequest<ConmedFilters> requestBody) {
        return new DetailsOnDemandResponse(conmedsService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(),
                requestBody.getSubjectId(),
                requestBody.getEventFilters()));
    }

}
