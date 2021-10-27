package com.acuity.visualisations.rest.resources.dose;

import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.service.event.DrugDoseService;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.dose.DrugDoseRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

import static com.acuity.visualisations.rest.resources.util.DetailsOnDemandCsvDownloadingUtils.setDownloadHeaders;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@RequestMapping("/resources/dose/")
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class DrugDoseResource {
    private final DrugDoseService drugDoseService;

    public DrugDoseResource(DrugDoseService drugDoseService) {
        this.drugDoseService = drugDoseService;
    }

    @PostMapping("filters")
    @Cacheable
    public DrugDoseFilters getAvailableFilters(@RequestBody @Valid DrugDoseRequest requestBody) {
        return (DrugDoseFilters) drugDoseService.getAvailableFilters(requestBody.getDatasetsObject(),
                requestBody.getDoseFilters(), requestBody.getPopulationFilters());
    }

    @PostMapping("single-subject")
    @Cacheable
    public DetailsOnDemandResponse getSingleSubjectData(
            @RequestBody @Valid SingleSubjectRequest<DrugDoseFilters> requestBody) {

        return new DetailsOnDemandResponse(drugDoseService.getDetailsOnDemandData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters()));
    }

    @ApiOperation(
            value = "Gets Drug Dose Table data for Azure Machine Learning needs",
            nickname = "getDoseMLTableDataCsv",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/dose-ml-export", method = POST)
    public void getDoseMLTableDataCsv(
            @ApiParam(value = "Datasets to get the export data for Azure ML needs", required = true)
            @RequestBody @Valid DatasetsRequest requestBody, HttpServletResponse response) throws IOException {
        setDownloadHeaders(response, "dose_table.csv");
        drugDoseService.writeAMLDataCsv(requestBody.getDatasetsObject(), response.getWriter(), DrugDose.class, DrugDoseRaw.class);
    }

}
