package com.acuity.visualisations.rest.resources.renal;

import com.acuity.visualisations.rawdatamodel.filters.RenalFilters;
import com.acuity.visualisations.rawdatamodel.service.event.RenalService;
import com.acuity.visualisations.rest.model.request.renal.RenalRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@Api(description = "rest endpoints for common renal methods")
@RequestMapping(value = "/resources/renal")
@RequiredArgsConstructor
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
public class RenalResource {

    private final RenalService renalService;

    @ApiOperation("Gets the available renal filters for the currently selected renal and population filters")
    @PostMapping("filters")
    public RenalFilters getFilters(@RequestBody RenalRequest requestBody) {

        return (RenalFilters) renalService.getAvailableFilters(
                requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation("Gets the subjects in available renal filters for the currently selected renal and population filters")
    @PostMapping("filters-subjects")
    public List<String> getSubjects(@RequestBody RenalRequest requestBody) {

        return renalService.getSubjects(
                requestBody.getDatasetsObject(),
                requestBody.getRenalFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("single-subject")
    public DetailsOnDemandResponse getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<RenalFilters> requestBody) {

        return new DetailsOnDemandResponse(renalService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters()));
    }

}
