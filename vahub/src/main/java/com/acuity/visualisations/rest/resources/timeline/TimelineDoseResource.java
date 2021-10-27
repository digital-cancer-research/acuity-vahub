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

import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.service.timeline.DrugDoseTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.SubjectDosingSummary;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.SubjectDrugDosingSummary;
import com.acuity.visualisations.rest.model.request.dose.DrugDoseRequest;
import com.acuity.visualisations.rest.model.request.dose.TimelineDosingRequest;
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
@Api(value = "/resources/timeline/dosing/", description = "rest endpoints for for dosing timeline")
@RequestMapping(value = "/resources/timeline/dosing",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class TimelineDoseResource {
    @Autowired
    private DrugDoseTimelineService drugDoseTimelineService;

    @ApiOperation(
            value = "Gets subjects in the available dosing filters for the currently selected dosing and population filters",
            nickname = "getSubjects",
            response = DrugDoseFilters.class,
            httpMethod = "POST"
    )
    @PostMapping("filters-subjects")
    @Cacheable
    public List<String> getSubjects(
            @ApiParam(value = "DosingRequest:  Dosing and Population Filters e.g. {doseFilters: {}, populationFilters: {}}", required = true)
            @RequestBody DrugDoseRequest requestBody) {
        return drugDoseTimelineService.getSubjects(
                requestBody.getDatasetsObject(),
                requestBody.getDoseFilters(),
                requestBody.getPopulationFilters()
        );
    }

    @ApiOperation(
            value = "Gets the dosing summary information for the timeline for the currently selected population and dosing filters",
            nickname = "getDosingSummaries",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping("dose-summaries")
    @Cacheable
    public List<SubjectDosingSummary> getDosingSummaries(
            @ApiParam(value = "TimelineDosingRequest:  Dosing and Population Filters e.g. {doseFilters: {}, populationFilters: {}}", required = true)
            @RequestBody @Valid TimelineDosingRequest requestBody) {
        return drugDoseTimelineService.getDosingSummaries(
                requestBody.getDatasetsObject(),
                requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg(),
                requestBody.getMaxDoseType(),
                requestBody.getDoseFilters(),
                requestBody.getPopulationFilters()
        );
    }

    @ApiOperation(
            value = "Gets the dosing summary by drug information for the timeline for the currently selected population and dosing filters",
            nickname = "getDosingSummariesByDrug",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping("dose-summaries-by-drug")
    @Cacheable
    public List<SubjectDrugDosingSummary> getDosingSummariesByDrug(
            @ApiParam(value = "TimelineDosingRequest:  Dosing and Population Filters e.g. {doseFilters: {}, populationFilters: {}}", required = true)
            @RequestBody @Valid TimelineDosingRequest requestBody) {
        return drugDoseTimelineService.getDosingSummariesByDrug(
                requestBody.getDatasetsObject(),
                requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg(),
                requestBody.getMaxDoseType(),
                requestBody.getDoseFilters(),
                requestBody.getPopulationFilters()
        );
    }
}
