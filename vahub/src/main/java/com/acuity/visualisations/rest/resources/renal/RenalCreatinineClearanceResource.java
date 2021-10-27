package com.acuity.visualisations.rest.resources.renal;

import com.acuity.visualisations.rawdatamodel.service.event.RenalService;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rest.model.request.renal.RenalPlotValuesRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalSelectionRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalTrellisRequest;
import com.acuity.visualisations.rest.model.response.renal.RenalBoxPlotResponse;
import com.acuity.visualisations.rest.model.response.renal.RenalTrellisResponse;
import com.acuity.visualisations.rest.model.response.renal.RenalXAxisResponse;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Api(description = "rest endpoints for box plot renal methods")
@RequestMapping("/resources/renal/creatinine-clearance-box-plot")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
public class RenalCreatinineClearanceResource {

    @Autowired
    private RenalService renalService;

    @ApiOperation("Returns available renal box plot x-axis options for the currently selected renal and population filters")
    @PostMapping("x-axis")
    public RenalXAxisResponse getAvailableXAxisOptions(@RequestBody RenalRequest requestBody) {
        return new RenalXAxisResponse(renalService.getAvailableBoxPlotXAxis(
                requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters()));
    }

    @ApiOperation("Returns the available trellising and options")
    @PostMapping("trellising")
    public RenalTrellisResponse getAvailableTrellising(@RequestBody RenalTrellisRequest requestBody) {
        return new RenalTrellisResponse(renalService.getTrellisOptions(
                requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getYAxisOption()));
    }

    @ApiOperation("Gets the statistics for the renal box plots")
    @PostMapping("boxplot")
    public RenalBoxPlotResponse getBoxPlotData(@RequestBody @Valid RenalPlotValuesRequest requestBody) {
        return new RenalBoxPlotResponse(renalService.getBoxPlot(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters()));
    }

    @ApiOperation("Gets selection details for renal box plot")
    @PostMapping("selection")
    public SelectionDetail getSelection(@RequestBody @Valid RenalSelectionRequest requestBody) {
        return renalService.getRangedSelectionDetails(requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }
}
