package com.acuity.visualisations.rest.resources.dosedisc;

import com.acuity.visualisations.rawdatamodel.filters.DoseDiscFilters;
import com.acuity.visualisations.rawdatamodel.service.event.DoseDiscService;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.dosedisc.DoseDiscRequest;
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
@Api("rest endpoints for dose discontinuation")
@RequestMapping("/resources/dose-disc")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class DoseDiscResource {

    @Autowired
    private DoseDiscService doseDiscService;

    @PostMapping("filters")
    @Cacheable
    public DoseDiscFilters getAvailableFilters(@RequestBody @Valid DoseDiscRequest requestBody) {
        return (DoseDiscFilters) doseDiscService.getAvailableFilters(requestBody.getDatasetsObject(),
                requestBody.getDoseDiscFilters(), requestBody.getPopulationFilters());
    }

    @PostMapping("single-subject")
    @Cacheable
    public DetailsOnDemandResponse getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<DoseDiscFilters> requestBody) {
        return new DetailsOnDemandResponse(doseDiscService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters()));
    }
}
