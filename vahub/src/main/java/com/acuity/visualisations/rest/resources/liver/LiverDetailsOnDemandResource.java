package com.acuity.visualisations.rest.resources.liver;

import com.acuity.visualisations.rawdatamodel.service.event.LiverService;
import com.acuity.visualisations.rest.model.request.liver.LiverRequest;
import com.acuity.visualisations.rest.resources.DetailsOnDemandCsvDownloader;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
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

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@Api(value = "/resources/liver/", description = "rest endpoints for for liver")
@RequestMapping(value = "/resources/liver/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class LiverDetailsOnDemandResource extends DetailsOnDemandCsvDownloader {

    @Autowired
    private LiverService liverService;

    @ApiOperation(
            value = "Gets the data for the details on demand table",
            nickname = "getDetailsOnDemandData",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping("details-on-demand")
    @Cacheable
    public List<Map<String, String>> getDetailsOnDemandData(
            @ApiParam(value = "Details On Demand Request body: A list of event IDs to get the data for e.g. "
                    + "['ev-1', 'ev-2']", required = true)
            @RequestBody @Valid DetailsOnDemandRequest requestBody) throws NoSuchFieldException {

        return liverService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getEventIds(), requestBody.getSortAttrs(),
                requestBody.getStart(), (long) requestBody.getEnd() - requestBody.getStart());
    }

    @ApiOperation(
            value = "Downloads all of the data for the details on demand table",
            nickname = "getAllDetailsOnDemandCsv",
            response = List.class,
            httpMethod = "GET"
    )
    @PostMapping("download-details-on-demand")
    @Cacheable
    public void getAllDetailsOnDemandData(
            @RequestBody @Valid LiverRequest requestBody,
            HttpServletResponse response) throws IOException {

        setDownloadHeaders(response);
        liverService.writeAllDetailsOnDemandCsv(requestBody.getDatasetsObject(), response.getWriter(),
                requestBody.getEventFilters(), requestBody.getPopulationFilters());
    }


    @ApiOperation(
            value = "Downloads data for the details on demand table for the selected IDs",
            nickname = "getAllDetailsOnDemandCsv",
            response = List.class,
            httpMethod = "POST"
    )
    @PostMapping(value = "download-selected-details-on-demand")
    public void downloadSelectedDetailsOnDemandData(@RequestBody @Valid DetailsOnDemandRequest requestBody,
                                                    HttpServletResponse response) throws IOException {
        setDownloadHeaders(response);
        liverService.writeSelectedDetailsOnDemandCsv(requestBody.getDatasetsObject(), requestBody.getEventIds(), response.getWriter());
    }
}
