package com.acuity.visualisations.rest.resources.renal;

import com.acuity.visualisations.rawdatamodel.service.event.RenalService;
import com.acuity.visualisations.rest.resources.DetailsOnDemandCsvDownloader;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@RequestMapping("/resources/renal/details-on-demand")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
public class RenalDetailsOnDemandResource extends DetailsOnDemandCsvDownloader {

    @Autowired
    private RenalService renalService;

    @ApiOperation("Gets the data for the details on demand table")
    @PostMapping("data")
    public DetailsOnDemandResponse getDetailsOnDemandData(
            @RequestBody @Valid DetailsOnDemandRequest requestBody) {

        return new DetailsOnDemandResponse(renalService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(),
                requestBody.getEventIds(),
                requestBody.getSortAttrs(),
                requestBody.getStart(),
                requestBody.getEnd()));
    }

    @ApiOperation("Downloads all of the data for the details on demand table")
    @PostMapping("all-csv")
    public void getAllDetailsOnDemandData(
            @RequestBody @Valid RenalRequest requestBody,
            HttpServletResponse response) throws IOException {

        setDownloadHeaders(response);
        renalService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getRenalFilters(), requestBody.getPopulationFilters());
    }

    @ApiOperation("Downloads data for the details on demand table for the selected IDs")
    @PostMapping("selected-csv")
    public void downloadSelectedDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody,
                                                    HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        renalService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(),
                requestBody.getEventIds(), response.getWriter());
    }
}

