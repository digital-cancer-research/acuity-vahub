package com.acuity.visualisations.rest.resources.cardiac;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.service.event.CardiacService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CardiacGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
import com.acuity.visualisations.rest.model.request.cardiac.CardiacRequest;
import com.acuity.visualisations.rest.model.request.cardiac.CardiacSelectionRequest;
import com.acuity.visualisations.rest.model.request.cardiac.CardiacTrellisRequest;
import com.acuity.visualisations.rest.model.request.cardiac.CardiacValuesRequest;
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
@RequestMapping("/resources/cardiac/measurements-over-time-chart")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@RequiredArgsConstructor
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class CardiacMeasurementsOverTimeChartResource {

    private final CardiacService cardiacService;

    @PostMapping("x-axis")
    @Cacheable
    public AxisOptions<CardiacGroupByOptions> getXAxis(
            @RequestBody @Valid CardiacRequest requestBody) {
        return cardiacService.getAvailableBoxPlotXAxis(requestBody.getDatasetsObject(),
                requestBody.getCardiacFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("values")
    @Cacheable
    public List<TrellisedBoxPlot<Cardiac, CardiacGroupByOptions>> getValues(
            @RequestBody @Valid CardiacValuesRequest requestBody) {
        return cardiacService.getBoxPlot(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getCardiacFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("trellising")
    @Cacheable
    public List<TrellisOptions<CardiacGroupByOptions>> getTrellising(
            @RequestBody @Valid CardiacTrellisRequest requestBody) {
        return cardiacService.getTrellisOptions(
                requestBody.getDatasetsObject(),
                requestBody.getCardiacFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getYAxisOption());
    }

    @PostMapping("selection")
    @Cacheable
    public SelectionDetail getSelection(
            @RequestBody @Valid CardiacSelectionRequest requestBody) {
        return cardiacService.getRangedSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getCardiacFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }
}
