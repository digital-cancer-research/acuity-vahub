package com.acuity.visualisations.rest.resources.timeline;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.axes.TAxes;
import com.acuity.visualisations.rawdatamodel.service.timeline.TimelineService;
import com.acuity.visualisations.rest.model.request.timeline.TimelineSubjectRequest;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import com.acuity.visualisations.rest.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/resources/timeline")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class TimelineResource {

    @Autowired
    private TimelineService timelineService;

    @PostMapping("available-options")
    @Cacheable
    public List<TAxes<DayZeroType>> getAvailableOptions(@RequestBody DatasetsRequest requestBody) {
        return timelineService.getAvailableOptions(requestBody.getDatasetsObject());
    }

    @PostMapping("available-tracks")
    @Cacheable
    public List<String> getAvailableTracks(@RequestBody DatasetsRequest requestBody) {
        return timelineService.getAvailableTracks(requestBody.getDatasetsObject());
    }

    @PostMapping("subjects")
    @Cacheable
    public List<String> getSubjectsSortedByStudyDuration(@RequestBody TimelineSubjectRequest requestBody) {
        return timelineService.getSubjectsSortedByStudyDuration(requestBody.getDatasetsObject(),
                requestBody.getPopulationFilters(),
                requestBody.getVisibleTracks(),
                requestBody.getDayZero().getValue(),
                requestBody.getDayZero().getStringarg(),
                requestBody.getAesFilters(),
                requestBody.getConmedsFilters(),
                requestBody.getDoseFilters(),
                requestBody.getCardiacFilters(),
                requestBody.getExacerbationsFilters(),
                requestBody.getLabsFilters(),
                requestBody.getLungFunctionFilters(),
                requestBody.getVitalsFilters(),
                requestBody.getPatientDataFilters());
    }
}
