package com.acuity.visualisations.rest.resources.sae;

import com.acuity.visualisations.rawdatamodel.filters.SeriousAeFilters;
import com.acuity.visualisations.rawdatamodel.service.event.SeriousAeService;
import com.acuity.visualisations.rest.model.request.sae.SeriousAeRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import javax.validation.Valid;

import com.acuity.visualisations.rest.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources/sae")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
public class SeriousAeResource {

    @Autowired
    private SeriousAeService seriousAeService;

    @PostMapping("filters")
    public SeriousAeFilters getFilters(@RequestBody @Valid SeriousAeRequest requestBody) {

        return (SeriousAeFilters) seriousAeService.getAvailableFilters(
                requestBody.getDatasetsObject(),
                requestBody.getSeriousAeFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("single-subject")
    public DetailsOnDemandResponse getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<SeriousAeFilters> requestBody) {

        return new DetailsOnDemandResponse(seriousAeService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters()));
    }
}
