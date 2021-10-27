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

import com.acuity.visualisations.rawdatamodel.service.timeline.ConmedsTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.SubjectConmedByClass;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.SubjectConmedByDrug;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.SubjectConmedSummary;
import com.acuity.visualisations.rest.model.request.conmeds.ConmedsTimelineRequest;
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
@Api(value = "/resources/timeline/conmeds/", description = "rest endpoints for for conmeds timeline")
@RequestMapping(value = "/resources/timeline/conmeds/",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class TimelineConmedsResource {

    @Autowired
    private ConmedsTimelineService conmedsTimelineService;

    @ApiOperation(
            value = "Gets the conmeds summary information for the timeline for the currently selected population and conmeds filters",
            nickname = "getConmedsSummaries",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping("conmedssummaries")
    @Cacheable
    public List<SubjectConmedSummary> getConmedsSummaries(
            @ApiParam(value = "TimelineConmdsRequest:  Conmeds and Population Filters e.g. {conmedsFilters: {}, populationFilters: {}}", required = true)
            @RequestBody @Valid ConmedsTimelineRequest requestBody) {

        return conmedsTimelineService.getConmedsSummaries(
                requestBody.getDatasetsObject(),
                requestBody.getConmedsFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg()
        );
    }

    @ApiOperation(
            value = "Gets the conmeds by class information for the timeline for the currently selected population and conmeds filters",
            nickname = "getConmedsClasses",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping("conmedsbyclass")
    @Cacheable
    public List<SubjectConmedByClass> getConmedsByClass(
            @ApiParam(value = "TimelineEcgRequest:  Conmeds and Population Filters e.g. {conmedsFilters: {}, populationFilters: {}}", required = true)
            @RequestBody @Valid ConmedsTimelineRequest requestBody) {

        return conmedsTimelineService.getConmedsByClass(
                requestBody.getDatasetsObject(),
                requestBody.getConmedsFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg()
        );
    }

    @ApiOperation(
            value = "Gets the conmeds by drug information for the timeline for the currently selected population and conmeds filters",
            nickname = "getConmedsByDrug",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping("conmedsbydrug")
    @Cacheable
    public List<SubjectConmedByDrug> getConmedsByDrug(
            @ApiParam(value = "TimelineLabsRequest:  Conmeds and Population Filters e.g. {conmedsFilters: {}, populationFilters: {}}", required = true)
            @RequestBody @Valid ConmedsTimelineRequest requestBody) {

        return conmedsTimelineService.getConmedsByDrug(
                requestBody.getDatasetsObject(),
                requestBody.getConmedsFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg()
        );
    }
}
