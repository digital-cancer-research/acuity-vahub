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

package com.acuity.visualisations.rest.resources.respiratory.exacerbation;


import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.ExacerbationService;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
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

import static com.acuity.visualisations.rest.util.Constants.PRE_AUTHORISE_VISUALISATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(description = "rest endpoints for exacerbation")
@RequestMapping(value = "/resources/respiratory/exacerbation",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@RequiredArgsConstructor
public class ExacerbationResource {

    private final ExacerbationService exacerbationService;

    @ApiOperation("Gets the available exacerbation filters for the currently selected lung function and population filters")
    @PostMapping("filters")
    @Cacheable
    public ExacerbationFilters getExacerbationFilters(
            @ApiParam(value = "exacerbation and Population Filters e.g. {exacerbationFilters : {}, populationFilters: {}}",
                    required = true)
            @RequestBody ExacerbationRequest requestBody) {

        return (ExacerbationFilters) exacerbationService.getAvailableFilters(requestBody.getDatasetsObject(),
                requestBody.getExacerbationFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation("Gets the subjects in available exacerbation filters for the currently selected exacerbation and population filters")
    @PostMapping("filtered-subjects")
    @Cacheable
    public List<String> getSubjectsForExacerbations(
            @ApiParam(value = "ExacerbationsRequest:  exacerbation and Population Filters e.g. {exacerbationsFilters : {}, populationFilters: {}}",
                    required = true)
            @RequestBody ExacerbationRequest requestBody) {

        return exacerbationService.getSubjects(requestBody.getDatasetsObject(), requestBody.getExacerbationFilters(),
                        requestBody.getPopulationFilters());
    }

    @ApiOperation("Gets all exacerbation data for a single subject")
    @PostMapping("single-subject")
    @Cacheable
    public DetailsOnDemandResponse getExacerbationsSingleSubjectData(
            @ApiParam(value = "Single Subject Request body: The subject ID to get the data for", required = true)
            @RequestBody @Valid SingleSubjectRequest<ExacerbationFilters> requestBody) {

        return new DetailsOnDemandResponse(exacerbationService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters()));
    }
}
