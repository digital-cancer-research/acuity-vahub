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

import com.acuity.visualisations.rawdatamodel.service.event.ExacerbationService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationOnSetLineChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationRequest;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationValuesRequest;
import com.acuity.visualisations.rest.model.response.respiratory.exacerbation.ExacerbationColorByOptionsResponse;
import com.acuity.visualisations.rest.model.response.respiratory.exacerbation.ExacerbationXAxisResponse;
import io.swagger.annotations.Api;
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

import static com.acuity.visualisations.rest.util.Constants.PRE_AUTHORISE_VISUALISATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * Endpoints for data related to
 *
 * @author khnp879
 */
@RestController
@Api(description = "rest endpoints for exacerbatios onsetline chart data")
@RequestMapping(value = "/resources/respiratory/exacerbation/on-set-line-chart",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class ExacerbationOnsetLineChartResource {

    private ExacerbationService exacerbationService;

    @Autowired
    public ExacerbationOnsetLineChartResource(ExacerbationService exacerbationService) {
        this.exacerbationService = exacerbationService;
    }

    @PostMapping("x-axis")
    @Cacheable
    public ExacerbationXAxisResponse getXAxis(@RequestBody @Valid ExacerbationRequest requestBody) {
        return new ExacerbationXAxisResponse(exacerbationService.getAvailableOnsetLineChartXAxis(requestBody.getDatasetsObject(),
                requestBody.getExacerbationFilters(), requestBody.getPopulationFilters()));
    }


    @PostMapping("color-by-options")
    @Cacheable
    public ExacerbationColorByOptionsResponse getColorsByOptions(@RequestBody @Valid ExacerbationRequest requestBody) {
        return new ExacerbationColorByOptionsResponse(exacerbationService.getOnsetLineChartColorByOptions(
                requestBody.getDatasetsObject(),
                requestBody.getExacerbationFilters(),
                requestBody.getPopulationFilters()
        ));
    }

    @PostMapping("selection")
    @Cacheable
    public SelectionDetail getSelection(@RequestBody @Valid ExacerbationOnSetLineChartSelectionRequest
                                                requestBody) {
        return exacerbationService.getOnSetLineChartSelectionDetails(requestBody.getDatasetsObject(),
                requestBody.getExacerbationFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection(), requestBody.getCountType());
    }

    @PostMapping("values")
    @Cacheable
    public List<TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>> getValues(@RequestBody @Valid ExacerbationValuesRequest requestBody) {
        return exacerbationService.getOnsetLineChartValues(requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getExacerbationFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getCountType());
    }
}
