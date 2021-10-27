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

package com.acuity.visualisations.rest.resources.renal;

import com.acuity.visualisations.rawdatamodel.service.event.RenalService;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rest.model.request.renal.RenalMeanRangeSelectionRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalPlotValuesRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalTrellisRequest;
import com.acuity.visualisations.rest.model.response.renal.RenalMeanRangeChartResponse;
import com.acuity.visualisations.rest.model.response.renal.RenalTrellisResponse;
import com.acuity.visualisations.rest.model.response.renal.RenalXAxisResponse;
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

import javax.validation.Valid;

@RestController
@Api(description = "rest endpoints for mean range chart renal methods")
@RequestMapping("/resources/renal/mean-range-chart")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class RenalMeanRangeChartResource {

    @Autowired
    private RenalService renalService;

    @PostMapping("x-axis")
    @ApiOperation("Returns available x-axis options for the currently selected renal and population filters")
    @Cacheable
    public RenalXAxisResponse getXAxis(@RequestBody @Valid RenalRequest requestBody) {
        return new RenalXAxisResponse(renalService.getAvailableBoxPlotXAxis(
                requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters()));
    }

    @PostMapping("values")
    @ApiOperation("Gets the median and min/max margins for each x value across a trellis")
    @Cacheable
    public RenalMeanRangeChartResponse getValues(@RequestBody @Valid RenalPlotValuesRequest requestBody) {
        return new RenalMeanRangeChartResponse(renalService.getRangePlot(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters()));
    }

    @PostMapping("trellising")
    @ApiOperation("Gets the available trellising and options")
    @Cacheable
    public RenalTrellisResponse getTrellising(@RequestBody @Valid RenalTrellisRequest requestBody) {
        return new RenalTrellisResponse(renalService.getTrellisOptions(
                requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getYAxisOption()));
    }

    @PostMapping("selection")
    @ApiOperation("Gets selection details for median line plot")
    @Cacheable
    public SelectionDetail getSelection(@RequestBody @Valid RenalMeanRangeSelectionRequest requestBody) {
        return renalService.getSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }

    @PostMapping("coloring")
    @ApiOperation("Gets color-by options for median line plot")
    @Cacheable
    public RenalTrellisResponse getSelection(@RequestBody @Valid RenalRequest requestBody) {
        return new RenalTrellisResponse(renalService.getBarChartColorByOptions(
                requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters()));
    }
}
