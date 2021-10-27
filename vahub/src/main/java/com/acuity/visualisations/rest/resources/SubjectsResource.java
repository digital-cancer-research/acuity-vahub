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

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.ssv.SSVSummaryService;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputSSVSummaryData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputSSVSummaryMetadata;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(description = "rest endpoints for subject details")
@RequestMapping("/resources/subjects/")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class SubjectsResource {

    @Autowired
    private SSVSummaryService ssvSummaryService;

    @ApiOperation("Gets details for a list of subjects")
    @PostMapping("/metadata")
    @Cacheable
    public OutputSSVSummaryMetadata getMetadata(@RequestBody DatasetsRequest requestBody) {
        return ssvSummaryService.getSingleSubjectMetadata(requestBody.getDatasetsObject());
    }

    @ApiOperation("Gets details for a list of subjects")
    @PostMapping("/detail")
    @Cacheable
    public OutputSSVSummaryData getDetails(@RequestBody SingleSubjectRequest<PopulationFilters> requestBody) {
        return ssvSummaryService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId());
    }

}
