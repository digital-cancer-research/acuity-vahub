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

package com.acuity.visualisations.rest.resources.population;

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rest.model.request.population.PopulationRequest;
import com.acuity.visualisations.rest.model.request.population.PopulationSingleSubjectRequest;
import com.acuity.visualisations.rest.util.Constants;
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
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/resources/population/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class PopulationResource {
    @Autowired
    private PopulationService populationService;

    @PostMapping("filters")
    @Cacheable(condition = Constants.EMPTY_POPULATION_FILTER)
    public PopulationFilters getAvailableFilters(@RequestBody @Valid PopulationRequest requestBody) {
        return populationService.getAvailableFilters(requestBody.getDatasetsObject(), requestBody.getPopulationFilters());
    }

    @PostMapping("filtered-subjects")
    @Cacheable(condition = Constants.EMPTY_POPULATION_FILTER)
    public List<String> getSubjects(@RequestBody @Valid PopulationRequest requestBody) {
        return populationService.getFilteredData(requestBody.getDatasetsObject(), requestBody.getPopulationFilters())
                .stream().map(Subject::getSubjectCode).distinct().collect(Collectors.toList());
    }

    @PostMapping(value = "single-subject")
    @Cacheable(condition = Constants.EMPTY_POPULATION_FILTER)
    public List<Map<String, String>> getSingleSubjectData(@RequestBody @Valid PopulationSingleSubjectRequest requestBody) {
        return populationService.getSingleSubjectData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getPopulationFilters());
    }
}
