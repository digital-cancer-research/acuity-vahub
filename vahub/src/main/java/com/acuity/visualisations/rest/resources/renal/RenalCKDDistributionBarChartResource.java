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
