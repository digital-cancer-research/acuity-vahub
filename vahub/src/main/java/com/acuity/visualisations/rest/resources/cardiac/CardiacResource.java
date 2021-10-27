package com.acuity.visualisations.rest.resources.cardiac;

import com.acuity.visualisations.rawdatamodel.filters.CardiacFilters;
import com.acuity.visualisations.rawdatamodel.service.event.CardiacService;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.cardiac.CardiacRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
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
import java.util.List;

@RestController
@RequestMapping("/resources/cardiac")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@RequiredArgsConstructor
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class CardiacResource {

    private final CardiacService cardiacService;

    @PostMapping("new-filters")
    @Cacheable
    public CardiacFilters getFilters(@RequestBody CardiacRequest requestBody) {

        return (CardiacFilters) cardiacService.getAvailableFilters(
                requestBody.getDatasetsObject(),
                requestBody.getCardiacFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("filtered-subjects")
    @Cacheable
    public List<String> getSubjects(@RequestBody CardiacRequest requestBody) {

        return cardiacService.getSubjects(requestBody.getDatasetsObject(),
                        requestBody.getCardiacFilters(),
                        requestBody.getPopulationFilters());
    }

    @PostMapping("single-subject")
    public DetailsOnDemandResponse getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<CardiacFilters> requestBody) {

        return new DetailsOnDemandResponse(cardiacService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters()));
    }
}
