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

package com.acuity.visualisations.rest.resources.vitals;

import com.acuity.visualisations.rawdatamodel.service.event.VitalService;
import com.acuity.visualisations.rest.model.request.vitals.VitalsRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "/resources/vitals/details-on-demand", description = "rest endpoints for vital's dod table")
@RequestMapping(value = "/resources/vitals/details-on-demand",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class VitalsDetailsOnDemandResource {

    @Autowired
    private VitalService vitalService;

    @ApiOperation(
            value = "Gets the data for the details on demand table",
            nickname = "getDetailsOnDemandData",
            response = DetailsOnDemandResponse.class,
            httpMethod = "POST"
    )
    @PostMapping("data")
    @Cacheable
    public DetailsOnDemandResponse getDetailsOnDemandData(
            @ApiParam(value = "Details On Demand Request body: A list of event IDs to get the data for e.g. "
                    + "['ev-1', 'ev-2']", required = true)
            @RequestBody @Valid DetailsOnDemandRequest requestBody) throws NoSuchFieldException {

        return new DetailsOnDemandResponse(vitalService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(),
                requestBody.getEventIds(),
                requestBody.getSortAttrs(),
                requestBody.getStart(),
                requestBody.getEnd()));
    }

    @ApiOperation(
            value = "Downloads all of the data for the details on demand table",
            nickname = "downloadAllDetailsOnDemandCsv",
            httpMethod = "POST"
    )
    @PostMapping("all-csv")
    @Cacheable
    public void getAllDetailsOnDemandData(@RequestBody @Valid VitalsRequest requestBody,
                                          HttpServletResponse response) throws IOException {
        Constants.setDownloadHeader(response);
        vitalService.writeAllDetailsOnDemandCsv(
                requestBody.getDatasetsObject(),
                response.getWriter(),
                requestBody.getVitalsFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Downloads data for the details on demand table for the selected IDs",
            nickname = "downloadSelectedDetailsOnDemandCsv",
            httpMethod = "POST"
    )
    @PostMapping("selected-csv")
    public void downloadSelectedDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody,
                                                    HttpServletResponse response) throws IOException {
        Constants.setDownloadHeader(response);
        vitalService.writeSelectedDetailsOnDemandCsv(
                requestBody.getDatasetsObject(),
                requestBody.getEventIds(),
                response.getWriter());
    }
}
