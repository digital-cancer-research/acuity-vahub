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

package com.acuity.visualisations.rest.resources.respiratory.lungfunction;

import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.service.event.LungFunctionService;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import io.swagger.annotations.Api;
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
@Api(description = "rest endpoints for common Lung Function methods")
@RequestMapping(value = "/resources/respiratory/lung-function",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(PRE_AUTHORISE_VISUALISATION)
@RequiredArgsConstructor
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class LungFunctionResource {

    private final LungFunctionService lungFunctionService;

    @PostMapping("filters")
    @Cacheable
    public LungFunctionFilters getFilters(@RequestBody LungFunctionRequest requestBody) {

        return (LungFunctionFilters) lungFunctionService.getAvailableFilters(
                requestBody.getDatasetsObject(),
                requestBody.getLungFunctionFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("filtered-subjects")
    @Cacheable
    public List<String> getSubjects(@RequestBody LungFunctionRequest requestBody) {

        return lungFunctionService.getSubjects(requestBody.getDatasetsObject(),
                        requestBody.getLungFunctionFilters(),
                        requestBody.getPopulationFilters());
    }

    @PostMapping(value = "single-subject")
    @Cacheable
    public DetailsOnDemandResponse getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<LungFunctionFilters> requestBody) {

        return new DetailsOnDemandResponse(lungFunctionService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters()));
    }

}
