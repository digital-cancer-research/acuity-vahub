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

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.service.event.LungFunctionService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionRequest;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionSelectionRequest;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionTrellisRequest;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionValuesRequest;
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

@RestController
@Api(description = "rest endpoints for Lung Function Measurements Over Time chart data")
@RequestMapping(value = "/resources/respiratory/lung-function/measurements-over-time-chart",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class LungFunctionMeasurementsOverTimeChartResource {

    @Autowired
    private LungFunctionService lungFunctionService;


    @PostMapping("x-axis")
    @Cacheable
    public AxisOptions<LungFunctionGroupByOptions> getXAxis(
            @RequestBody @Valid LungFunctionRequest requestBody) {

        return lungFunctionService.getAvailableBoxPlotXAxis(requestBody.getDatasetsObject(),
                requestBody.getLungFunctionFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("values")
    @Cacheable
    public List<TrellisedBoxPlot<LungFunction, LungFunctionGroupByOptions>> getValues(
            @RequestBody @Valid LungFunctionValuesRequest requestBody) {
        return lungFunctionService.getBoxPlot(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getLungFunctionFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("trellising")
    @Cacheable
    public List<TrellisOptions<LungFunctionGroupByOptions>> getTrellising(
            @RequestBody LungFunctionTrellisRequest requestBody) {
        return lungFunctionService.getTrellisOptions(
                requestBody.getDatasetsObject(),
                requestBody.getLungFunctionFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getYAxisOption());
    }

    @PostMapping("selection")
    public SelectionDetail getSelection(@RequestBody @Valid LungFunctionSelectionRequest requestBody) {
        return lungFunctionService.getRangedSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getLungFunctionFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }
}
