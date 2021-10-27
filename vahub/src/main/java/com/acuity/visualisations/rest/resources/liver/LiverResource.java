package com.acuity.visualisations.rest.resources.liver;

import com.acuity.visualisations.rawdatamodel.filters.LiverFilters;
import com.acuity.visualisations.rawdatamodel.service.event.LiverService;
import com.acuity.visualisations.rest.model.request.liver.LiverRequest;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "/resources/liver/", description = "rest endpoints for for liver")
@RequestMapping(value = "/resources/liver/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class LiverResource {

    @Autowired
    private LiverService liverService;

    @ApiOperation(
            value = "Gets the available liver filters for the currently selected population filters",
            nickname = "availableLiverFilters",
            response = LiverFilters.class,
            httpMethod = "POST"
    )
    @PostMapping("filters")
    @Cacheable
    public LiverFilters getAvailableFilters(
            @ApiParam(value = "LiverRequest:  Liver and Population Filters e.g. {liverFilters : {}, populationFilters: {}}", required = true)
            @RequestBody LiverRequest requestBody) {

        return (LiverFilters) liverService.getAvailableFilters(requestBody.getDatasetsObject(),
                requestBody.getLiverFilters(),
                requestBody.getPopulationFilters());
    }

    @ApiOperation(
            value = "Gets the subjects in available liver filters for the currently selected population filters",
            nickname = "getSubjects",
            response = LiverFilters.class,
            httpMethod = "POST"
    )
    @PostMapping("filters-subjects")
    @Cacheable
    public List<String> getSubjects(
            @ApiParam(value = "LiverRequest:  Liver and Population Filters e.g. {liverFilters : {}, populationFilters: {}}", required = true)
            @RequestBody LiverRequest requestBody) {

        return liverService.getSubjects(requestBody.getDatasetsObject(), requestBody.getLiverFilters(), requestBody.getPopulationFilters());
    }
}
