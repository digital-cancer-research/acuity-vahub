package com.acuity.visualisations.rest.resources.respiratory.lungfunction;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.service.event.LungFunctionService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionMeanRangeSelectionRequest;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionRequest;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionTrellisRequest;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionValuesRequest;
import io.swagger.annotations.Api;
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

import static com.acuity.visualisations.rest.util.Constants.PRE_AUTHORISE_VISUALISATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(description = "rest endpoints for Lung Function Mean Range Chart methods")
@RequestMapping(value = "/resources/respiratory/lung-function/mean-range-chart",
        consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
@RequiredArgsConstructor
public class LungMeanRangeChartResource {

    private final LungFunctionService lungFunctionService;

    @PostMapping("x-axis")
    @Cacheable
    public AxisOptions<LungFunctionGroupByOptions> getXAxis(
            @RequestBody @Valid LungFunctionRequest requestBody) {

        return lungFunctionService.getAvailableRangePlotXAxis(requestBody.getDatasetsObject(),
                requestBody.getLungFunctionFilters(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("values")
    @Cacheable
    public List<TrellisedRangePlot<LungFunction, LungFunctionGroupByOptions>> getValues(
            @RequestBody @Valid LungFunctionValuesRequest requestBody) {
        return lungFunctionService.getRangePlot(
                requestBody.getDatasetsObject(),
                requestBody.getSettings(),
                requestBody.getLungFunctionFilters(),
                requestBody.getPopulationFilters(),
                StatType.MEDIAN);
    }

    @PostMapping("trellising")
    @Cacheable
    public List<TrellisOptions<LungFunctionGroupByOptions>> getTrellising(
            @RequestBody @Valid LungFunctionTrellisRequest requestBody) {
        return lungFunctionService.getTrellisOptions(
                requestBody.getDatasetsObject(),
                requestBody.getLungFunctionFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getYAxisOption());
    }

    @PostMapping("selection")
    @Cacheable
    public SelectionDetail getSelection(
            @RequestBody @Valid LungFunctionMeanRangeSelectionRequest requestBody) {
        return lungFunctionService.getSelectionDetails(
                requestBody.getDatasetsObject(),
                requestBody.getLungFunctionFilters(),
                requestBody.getPopulationFilters(),
                requestBody.getSelection());
    }
}
