package com.acuity.visualisations.rest.resources.alcohol;

import com.acuity.visualisations.rawdatamodel.filters.AlcoholFilters;
import com.acuity.visualisations.rawdatamodel.service.event.AlcoholService;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.alcohol.AlcoholRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import javax.validation.Valid;

import com.acuity.visualisations.rest.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resources/alcohol")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class AlcoholResource {
    @Autowired
    private AlcoholService alcoholService;

    @PostMapping("/filters")
    @Cacheable(condition = "#requestBody.getAlcoholFilters().isEmpty() && #requestBody.getPopulationFilters().isEmpty()")
    public AlcoholFilters getFilters(@RequestBody @Valid AlcoholRequest requestBody) {

        return (AlcoholFilters) alcoholService.getAvailableFilters(
                requestBody.getDatasetsObject(),
                requestBody.getAlcoholFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("/single-subject")
    @Cacheable(condition = "#requestBody.getEventFilters().isEmpty()")
    public DetailsOnDemandResponse getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<AlcoholFilters> requestBody) {

        return new DetailsOnDemandResponse(alcoholService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters()));
    }
}
