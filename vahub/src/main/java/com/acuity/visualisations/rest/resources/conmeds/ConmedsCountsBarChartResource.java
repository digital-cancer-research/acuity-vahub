package com.acuity.visualisations.rest.resources.conmeds;

import com.acuity.visualisations.rawdatamodel.service.event.ConmedsService;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rest.model.request.conmeds.ConmedsCountsBarChartRequest;
import com.acuity.visualisations.rest.model.request.conmeds.ConmedsRequest;
import com.acuity.visualisations.rest.model.request.conmeds.ConmedsSelectionRequest;
import com.acuity.visualisations.rest.model.response.conmeds.ConmedsBarChartResponse;
import com.acuity.visualisations.rest.model.response.conmeds.ConmedsBarChartXAxisResponse;
import com.acuity.visualisations.rest.model.response.conmeds.ConmedsTrellisingResponse;
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
@RequestMapping("/resources/conmeds/counts-bar-chart")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@RequiredArgsConstructor
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class ConmedsCountsBarChartResource {

    private final ConmedsService conmedsService;

    @PostMapping("x-axis")
    @Cacheable
    public ConmedsBarChartXAxisResponse getXAxis(@RequestBody @Valid ConmedsRequest requestBody) {
        return new ConmedsBarChartXAxisResponse(conmedsService.getAvailableBarChartXAxis(
                requestBody.getDatasetsObject(),
                requestBody.getConmedsFilters(),
                requestBody.getPopulationFilters()));
    }

    @PostMapping("values")
    @Cacheable
    public ConmedsBarChartResponse getValues(@RequestBody @Valid ConmedsCountsBarChartRequest requestBody) {
        return new ConmedsBarChartResponse(conmedsService.getBarChart(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getConmedsFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getCountType()));
    }

    @PostMapping("trellising")
    @Cacheable
    public ConmedsTrellisingResponse getTrellising(@RequestBody @Valid ConmedsRequest requestBody) {
        return new ConmedsTrellisingResponse(conmedsService.getTrellisOptions(
                requestBody.getDatasetsObject(),
                requestBody.getConmedsFilters(),
                requestBody.getPopulationFilters()));
    }

    @PostMapping("selection")
    @Cacheable
    public SelectionDetail getSelection(@RequestBody @Valid ConmedsSelectionRequest requestBody) {
        return conmedsService.getSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getConmedsFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }

    @PostMapping("coloring")
    @Cacheable
    public ConmedsTrellisingResponse getColorByOptions(@RequestBody ConmedsRequest requestBody) {
        return new ConmedsTrellisingResponse(conmedsService.getBarChartColorByOptions(
                requestBody.getDatasetsObject(),
                requestBody.getConmedsFilters(),
                requestBody.getPopulationFilters()));
    }
}
