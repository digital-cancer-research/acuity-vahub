package com.acuity.visualisations.rest.resources.population;

import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rest.model.request.population.PopulationRequest;
import com.acuity.visualisations.rest.resources.DetailsOnDemandCsvDownloader;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.util.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping(value = "/resources/population/details-on-demand", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class PopulationDetailsOnDemandResource extends DetailsOnDemandCsvDownloader {
    @Autowired
    private PopulationService populationService;

    @PostMapping("data")
    public List<Map<String, String>> getDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody) {
        return populationService.getDetailsOnDemandData(requestBody.getDatasetsObject(), requestBody.getEventIds(),
                requestBody.getSortAttrs(), requestBody.getStart(), (long) requestBody.getEnd() - requestBody.getStart());
    }

    @PostMapping("all-csv")
    public void getAllDetailsOnDemandData(@RequestBody @Valid PopulationRequest requestBody,
                                          HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        populationService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getPopulationFilters(), requestBody.getPopulationFilters());
    }

    @PostMapping("selected-csv")
    public void downloadSelectedDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody,
                                                    HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        populationService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(), response.getWriter());
    }
}
