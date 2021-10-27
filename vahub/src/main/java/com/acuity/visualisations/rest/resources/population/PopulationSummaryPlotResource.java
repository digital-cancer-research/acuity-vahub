package com.acuity.visualisations.rest.resources.population;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rest.model.request.population.PopulationBarChartRequest;
import com.acuity.visualisations.rest.model.request.population.PopulationBarChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.population.PopulationRequest;
import com.acuity.visualisations.rest.util.Constants;
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
@RequestMapping(value = "/resources/population/summary-plot", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class PopulationSummaryPlotResource {
    @Autowired
    private PopulationService populationService;

    @PostMapping("x-axis")
    @Cacheable(condition = Constants.EMPTY_POPULATION_FILTER)
    public AxisOptions<PopulationGroupByOptions> getBarChartXAxis(@RequestBody @Valid PopulationRequest requestBody) {
        return populationService.getAvailableBarChartXAxisOptions(requestBody.getDatasetsObject(),
                requestBody.getPopulationFilters());
    }

    @PostMapping("color-by-options")
    @Cacheable(condition = Constants.EMPTY_POPULATION_FILTER)
    public List<TrellisOptions<PopulationGroupByOptions>> getAvailableBarChartColorBy(@RequestBody @Valid PopulationRequest requestBody) {
        return populationService.getBarChartColorByOptions(requestBody.getDatasetsObject(), requestBody.getPopulationFilters());
    }

    @PostMapping("values")
    @Cacheable(condition = Constants.EMPTY_POPULATION_FILTER)
    public List<TrellisedBarChart<Subject, PopulationGroupByOptions>> getValuesForBarChart(@RequestBody @Valid PopulationBarChartRequest requestBody) {
        return populationService.getBarChart(requestBody.getDatasetsObject(), requestBody.getSettings(),
                requestBody.getPopulationFilters(), requestBody.getCountType());
    }

    @PostMapping("selection")
    @Cacheable(condition = Constants.EMPTY_POPULATION_FILTER)
    public SelectionDetail getSelectionDetails(@RequestBody @Valid PopulationBarChartSelectionRequest requestBody) {
        return populationService.getSelectionDetails(requestBody.getDatasetsObject(), requestBody.getPopulationFilters(), requestBody.getSelection());
    }
}
