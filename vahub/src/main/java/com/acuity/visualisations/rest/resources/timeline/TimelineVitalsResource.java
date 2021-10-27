package com.acuity.visualisations.rest.resources.timeline;

import com.acuity.visualisations.rawdatamodel.service.timeline.VitalsTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.vitals.SubjectVitalsDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.vitals.SubjectVitalsSummary;
import com.acuity.visualisations.rest.model.request.vitals.VitalsTimelineRequest;
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

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "/resources/timeline/vitals", description = "rest endpoints for for vitals timeline")
@RequestMapping(value = "/resources/timeline/vitals/",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class TimelineVitalsResource {

    @Autowired
    private VitalsTimelineService vitalsTimelineService;

    @ApiOperation(
            value = "Gets the vitals summary information for the timeline for the currently selected population and vitals filters",
            nickname = "getVitalsSummary",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping("summaries")
    @Cacheable
    public List<SubjectVitalsSummary> getVitalsSummaries(
            @ApiParam(value = "TimelineVitalsRequest:  Vitals and Population Filters e.g. {timelineVitalsFilters: {}, populationFilters: {}}", required = true)
            @RequestBody @Valid VitalsTimelineRequest requestBody) {

        return vitalsTimelineService.getVitalsSummaries(
                requestBody.getDatasetsObject(),
                requestBody.getVitalsFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg()
        );
    }

    @ApiOperation(
            value = "Gets the vitals by class information for the timeline for the currently selected population and vitals filters",
            nickname = "getVitalsDetail",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping("details")
    @Cacheable
    public List<SubjectVitalsDetail> getVitalsDetail(
            @ApiParam(value = "TimelineVitalsRequest:  Vitals and Population Filters e.g. {timelineVitalsFilters: {}, populationFilters: {}}", required = true)
            @RequestBody @Valid VitalsTimelineRequest requestBody) {

        return vitalsTimelineService.getVitalsDetails(
                requestBody.getDatasetsObject(),
                requestBody.getVitalsFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg()
        );
    }
}