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
import com.acuity.visualisations.rest.model.request.renal.RenalBarChartRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalBarChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalRequest;
import com.acuity.visualisations.rest.model.response.renal.RenalBarChartResponse;
import com.acuity.visualisations.rest.model.response.renal.RenalColorByOptionsResponse;
import com.acuity.visualisations.rest.model.response.renal.RenalTrellisResponse;
import com.acuity.visualisations.rest.model.response.renal.RenalXAxisResponse;
import com.acuity.visualisations.rest.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/resources/renal/ckd-distribution-bar-chart")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
public class RenalCKDDistributionBarChartResource {

    @Autowired
    private RenalService renalService;

    @PostMapping("x-axis")
    public RenalXAxisResponse getXAxis(@RequestBody @Valid RenalRequest requestBody) {
        return new RenalXAxisResponse(renalService.getAvailableBarChartXAxis(requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters()));
    }

    @PostMapping("trellising")
    public RenalTrellisResponse getAvailableTrellising(@RequestBody RenalRequest requestBody) {
        return new RenalTrellisResponse(renalService.getTrellisOptions(
                requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters()));
    }

    @PostMapping("color-by-options")
    public RenalColorByOptionsResponse getAvailableBarChartColorBy(@RequestBody RenalRequest requestBody) {
        return new RenalColorByOptionsResponse(renalService.getBarChartColorByOptions(
                requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters()));
    }

    @PostMapping("values")
    public RenalBarChartResponse getValuesForBarChart(@RequestBody RenalBarChartRequest requestBody) {
        return new RenalBarChartResponse(renalService.getBarChart(requestBody.getDatasetsObject(), requestBody.getSettings(),
                requestBody.getRenalFilters(), requestBody.getPopulationFilters(), requestBody.getCountType()));
    }

    @PostMapping("selection")
    public SelectionDetail getSelectionDetails(@RequestBody @Valid RenalBarChartSelectionRequest requestBody) {
        return renalService.getBarChartSelectionDetails(requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }
}
