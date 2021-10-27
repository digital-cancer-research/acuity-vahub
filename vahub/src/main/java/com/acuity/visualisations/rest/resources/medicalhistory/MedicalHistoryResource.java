package com.acuity.visualisations.rest.resources.medicalhistory;

import com.acuity.visualisations.rawdatamodel.filters.MedicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.service.event.MedicalHistoryService;
import com.acuity.visualisations.rest.model.request.medicalhistory.MedicalHistoryRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.util.Constants;
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
@RequestMapping("/resources/medicalhistory/")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@RequiredArgsConstructor
public class MedicalHistoryResource {

    private final MedicalHistoryService medicalHistoryService;

    @PostMapping("filters")
    @Cacheable(condition = "#requestBody.getMedicalHistoryFilters().isEmpty() && #requestBody.getPopulationFilters().isEmpty()")
    public MedicalHistoryFilters getAvailableFilters(
            @RequestBody @Valid MedicalHistoryRequest requestBody) {
        return (MedicalHistoryFilters) medicalHistoryService.getAvailableFilters(requestBody.getDatasetsObject(),
                requestBody.getMedicalHistoryFilters(), requestBody.getPopulationFilters());
    }

    @PostMapping("single-subject")
    @Cacheable(condition = "#requestBody.getEventFilters().isEmpty()")
    public DetailsOnDemandResponse getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<MedicalHistoryFilters> requestBody) {
        return new DetailsOnDemandResponse(medicalHistoryService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters()));
    }
}
