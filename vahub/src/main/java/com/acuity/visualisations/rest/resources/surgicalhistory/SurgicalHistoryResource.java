package com.acuity.visualisations.rest.resources.surgicalhistory;

import com.acuity.visualisations.rawdatamodel.filters.SurgicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.service.event.SurgicalHistoryService;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.surgicalhistory.SurgicalHistoryRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@Api(description = "rest endpoints for surgical history")
@RequestMapping("/resources/surgical-history/")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@RequiredArgsConstructor
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class SurgicalHistoryResource {

    private final SurgicalHistoryService surgicalHistoryService;

    @PostMapping("filters")
    @Cacheable
    public SurgicalHistoryFilters getAvailableFilters(@RequestBody @Valid SurgicalHistoryRequest requestBody) {
        return (SurgicalHistoryFilters) surgicalHistoryService.getAvailableFilters(requestBody.getDatasetsObject(),
                requestBody.getSurgicalHistoryFilters(), requestBody.getPopulationFilters());
    }

    @PostMapping("single-subject")
    @Cacheable
    public DetailsOnDemandResponse getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<SurgicalHistoryFilters> requestBody) {
        return new DetailsOnDemandResponse(surgicalHistoryService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters()));
    }
}
