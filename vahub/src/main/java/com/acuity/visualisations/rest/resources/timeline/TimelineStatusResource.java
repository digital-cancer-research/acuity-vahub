package com.acuity.visualisations.rest.resources.timeline;

import com.acuity.visualisations.rawdatamodel.service.timeline.StatusSummaryTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.statussummary.SubjectStatusSummary;
import com.acuity.visualisations.rest.model.request.statussummary.StatusSummaryTimelineRequest;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "/resources/timeline/status/", description = "rest endpoints for for status timeline")
@RequestMapping(value = "/resources/timeline/status/",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@RequiredArgsConstructor
public class TimelineStatusResource {

    private final StatusSummaryTimelineService statusSummaryTimelineService;

    /**
     * Gets list of status summaries
     *
     * @param requestBody selected population filters by client
     * @return list of summaries
     */
    @ApiOperation(
            value = "Gets the status summary information for the timeline for the currently selected population filters",
            nickname = "getStatusSummaries",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping(value = "summaries")
    @Cacheable
    public List<SubjectStatusSummary> getStatusSummaries(
            @ApiParam(value = "TimelineStatusRequest:  Population Filters e.g. {populationFilters: {}}", required = true)
            @RequestBody @Valid StatusSummaryTimelineRequest requestBody) {

        return statusSummaryTimelineService.getStatusSummaries(
                requestBody.getDatasetsObject(),
                requestBody.getPopulationFilters(),
                requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg());
    }
}
