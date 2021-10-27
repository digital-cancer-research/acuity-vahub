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

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.service.event.ExacerbationService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationOverTimeLineBarChartValuesRequest;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationRequest;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationSelectionRequest;
import com.acuity.visualisations.rest.model.response.respiratory.exacerbation.ExacerbationColorByOptionsResponse;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(description = "rest endpoints for exacerbations over time chart data")
@RequestMapping(value = "/resources/respiratory/exacerbation/over-time-line-bar-chart",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(PRE_AUTHORISE_VISUALISATION)
@RequiredArgsConstructor
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class ExacerbationOverTimeLineBarChartResource {

    private final ExacerbationService exacerbationService;

    @ApiOperation("Gets the available color-by options")
    @PostMapping("color-by-options")
    @Cacheable
    public ExacerbationColorByOptionsResponse getColorByOptions(
            @RequestBody ExacerbationRequest requestBody) {

        return new ExacerbationColorByOptionsResponse(exacerbationService.getLineBarChartColorByOptions(
                requestBody.getDatasetsObject(),
                requestBody.getExacerbationFilters(),
                requestBody.getPopulationFilters()));
    }

    @ApiOperation("Gets the available x-axis options")
    @PostMapping("x-axis")
    @Cacheable
    public AxisOptions<ExacerbationGroupByOptions> getXAxis(@RequestBody @Valid ExacerbationRequest requestBody) {
        return exacerbationService.getAvailableOverTimeChartXAxis(requestBody.getDatasetsObject(),
                requestBody.getExacerbationFilters(), requestBody.getPopulationFilters());
    }

    @ApiOperation("Gets the data for over time line bar chart in available exacerbation filters for the currently "
            + "selected exacerbation and population filters")
    @PostMapping("values")
    @Cacheable
    public List<TrellisedOvertime<Exacerbation, ExacerbationGroupByOptions>> getValues(
            @RequestBody @Valid ExacerbationOverTimeLineBarChartValuesRequest requestBody) {
        return exacerbationService.getLineBarChart(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getExacerbationFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation("Gets selection detail for exacerbation over time line bar chart")
    @PostMapping("selection")
    @Cacheable
    public SelectionDetail getSelection(@RequestBody @Valid ExacerbationSelectionRequest requestBody) {
        return exacerbationService.getOverTimeLineBarChartSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getExacerbationFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }
}
