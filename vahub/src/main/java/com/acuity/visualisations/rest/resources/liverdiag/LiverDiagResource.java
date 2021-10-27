package com.acuity.visualisations.rest.resources.liverdiag;

import com.acuity.visualisations.rawdatamodel.filters.LiverDiagFilters;
import com.acuity.visualisations.rawdatamodel.service.event.LiverDiagService;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.liverdiag.LiverDiagRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.util.Constants;
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
@RequestMapping("/resources/liver-diag")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class LiverDiagResource {

    @Autowired
    private LiverDiagService liverDiagService;

    @PostMapping("filters")
    @Cacheable
    public LiverDiagFilters getFilters(@RequestBody @Valid LiverDiagRequest requestBody) {

        return (LiverDiagFilters) liverDiagService.getAvailableFilters(
                requestBody.getDatasetsObject(),
                requestBody.getLiverDiagFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("single-subject")
    @Cacheable
    public DetailsOnDemandResponse getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<LiverDiagFilters> requestBody) {

        return new DetailsOnDemandResponse(liverDiagService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters()));
    }
}
