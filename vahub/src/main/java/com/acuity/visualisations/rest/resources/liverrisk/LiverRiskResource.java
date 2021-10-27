package com.acuity.visualisations.rest.resources.liverrisk;

import com.acuity.visualisations.rawdatamodel.filters.LiverRiskFilters;
import com.acuity.visualisations.rawdatamodel.service.event.LiverRiskService;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.liverrisk.LiverRiskRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.util.Constants;
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

@RestController
@Api(description = "rest endpoints for liver risk")
@RequestMapping("/resources/liver-risk/")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class LiverRiskResource {

    @Autowired
    private LiverRiskService liverRiskService;

    @PostMapping("filters")
    @Cacheable
    public LiverRiskFilters getAvailableFilters(@RequestBody @Valid LiverRiskRequest requestBody) {
        return (LiverRiskFilters) liverRiskService.getAvailableFilters(requestBody.getDatasetsObject(),
                requestBody.getLiverRiskFilters(), requestBody.getPopulationFilters());
    }

    @PostMapping("single-subject")
    @Cacheable
    public DetailsOnDemandResponse getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<LiverRiskFilters> requestBody) {
        return new DetailsOnDemandResponse(liverRiskService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters()));
    }
}
