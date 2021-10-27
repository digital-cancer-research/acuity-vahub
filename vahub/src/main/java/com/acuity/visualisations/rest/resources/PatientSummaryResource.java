/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rest.resources;

import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.AssessmentFilters;
import com.acuity.visualisations.rawdatamodel.filters.ChemotherapyFilters;
import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.filters.DiseaseExtentFilters;
import com.acuity.visualisations.rawdatamodel.filters.DoseDiscFilters;
import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.MedicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.filters.NonTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PathologyFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RadiotherapyFilters;
import com.acuity.visualisations.rawdatamodel.filters.SecondTimeOfProgressionFilters;
import com.acuity.visualisations.rawdatamodel.filters.SurgicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.filters.SurvivalStatusFilters;
import com.acuity.visualisations.rawdatamodel.filters.TargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.service.event.AssessmentService;
import com.acuity.visualisations.rawdatamodel.service.event.ConmedsService;
import com.acuity.visualisations.rawdatamodel.service.event.CurrentMedicalHistoryService;
import com.acuity.visualisations.rawdatamodel.service.event.DiseaseExtentService;
import com.acuity.visualisations.rawdatamodel.service.event.DoseDiscService;
import com.acuity.visualisations.rawdatamodel.service.event.DoseLimitingService;
import com.acuity.visualisations.rawdatamodel.service.event.DrugDoseService;
import com.acuity.visualisations.rawdatamodel.service.event.LabService;
import com.acuity.visualisations.rawdatamodel.service.event.NonTargetLesionService;
import com.acuity.visualisations.rawdatamodel.service.event.PastChemotherapyService;
import com.acuity.visualisations.rawdatamodel.service.event.PastMedicalHistoryService;
import com.acuity.visualisations.rawdatamodel.service.event.PathologyService;
import com.acuity.visualisations.rawdatamodel.service.event.PatientOutcomeSummaryService;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.service.event.PostChemotherapyService;
import com.acuity.visualisations.rawdatamodel.service.event.RadiotherapyService;
import com.acuity.visualisations.rawdatamodel.service.event.SecondTimeOfProgressionService;
import com.acuity.visualisations.rawdatamodel.service.event.SurgicalHistoryService;
import com.acuity.visualisations.rawdatamodel.service.event.SurvivalStatusService;
import com.acuity.visualisations.rawdatamodel.service.event.TargetLesionService;
import com.acuity.visualisations.rawdatamodel.service.ssv.PatientSummaryDocumentService;
import com.acuity.visualisations.rawdatamodel.service.ssv.SingleSubjectViewSummaryService;
import com.acuity.visualisations.rest.model.request.patient.summary.PatientSummaryDocumentRequest;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.bind.JAXBException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.acuity.visualisations.rawdatamodel.service.ssv.SingleSubjectViewSummaryService.SsvTableMetadata;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RestController
@Api(value = "/resources/summary/", description = "rest endpoints for for single subject summary")
@RequestMapping(value = "/resources/summary/", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
@CacheConfig(keyGenerator = "datasetsKeyGenerator", cacheResolver = "refreshableCacheResolver")
public class PatientSummaryResource {

    @Autowired
    private PopulationService populationService;
    @Autowired
    private PatientOutcomeSummaryService patientSummaryService;
    @Autowired
    private PastMedicalHistoryService pastMedicalHistoryService;
    @Autowired
    private CurrentMedicalHistoryService currentMedicalHistoryService;
    @Autowired
    private NonTargetLesionService nonTargetLesionService;
    @Autowired
    private PermissionEvaluator permissionEvaluator;
    @Autowired
    private AssessmentService assessmentService;
    @Autowired
    private SurgicalHistoryService surgicalHistoryService;
    @Autowired
    private ConmedsService conmedService;
    @Autowired
    private LabService labService;
    @Autowired
    private PathologyService pathologyService;
    @Autowired
    private DiseaseExtentService diseaseExtentService;
    @Autowired
    private PastChemotherapyService pastChemotherapyService;
    @Autowired
    private SingleSubjectViewSummaryService singleSubjectViewSummaryService;
    @Autowired
    private PatientSummaryDocumentService documentService;
    @Autowired
    private RadiotherapyService radiotherapyService;
    @Autowired
    private PostChemotherapyService postChemotherapyService;
    @Autowired
    private TargetLesionService targetLesionService;
    @Autowired
    private DoseDiscService doseDiscService;
    @Autowired
    private DrugDoseService drugDoseService;
    @Autowired
    private DoseLimitingService doseLimitingService;
    @Autowired
    private SecondTimeOfProgressionService secondTimeOfProgressionService;
    @Autowired
    private AeService aeService;
    @Autowired
    private SurvivalStatusService survivalStatusService;


    @ApiOperation(
            value = "Generates a .docx document that contains summary data for a single subject",
            nickname = "getDocument",
            httpMethod = "POST"
    )
    @RequestMapping(value = "/document", method = POST)
    public void getDocument(@RequestBody @Valid PatientSummaryDocumentRequest requestBody, HttpServletResponse response)
            throws JAXBException, IOException, Docx4JException {

        final Optional<ByteArrayOutputStream> byteArrayOutputStream = documentService.generateDocument(requestBody.getDatasetsObject(),
                requestBody.getSubjectId(), hasTumourAccess(requestBody.getDatasets()), requestBody.getTimeZoneOffset());

        if (byteArrayOutputStream.isPresent()) {
            response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
            response.getOutputStream().write(byteArrayOutputStream.get().toByteArray());
            response.flushBuffer();
        } else {
            response.sendError(HttpStatus.NOT_FOUND.value(), "Subject not found");
        }
    }


    @ApiOperation(
            value = "Gets details for a single subject tables",
            nickname = "getMetadata",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/metadata", method = POST)
    public List<SsvTableMetadata> getMetadata(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<PopulationFilters> requestBody) {
        return singleSubjectViewSummaryService.getMetadata(requestBody.getDatasetsObject(), hasTumourAccess(requestBody.getDatasets()));
    }

    @ApiOperation(
            value = "Gets demography data for a single subject",
            nickname = "getDemography",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/demography", method = POST)
    @Cacheable
    public List<Map<String, String>> getDemography(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<PopulationFilters> requestBody) {
        return populationService.getSingleSubjectData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @ApiOperation(
            value = "Gets outcome summary data for a single subject",
            nickname = "getOutcomeSummary",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/outcome-summary", method = POST)
    public List<Map<String, String>> getOutcomeSummary(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<PopulationFilters> requestBody) {

        return patientSummaryService.getSingleSubjectData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), hasTumourAccess(requestBody.getDatasets()));
    }

    private boolean hasTumourAccess(List<Dataset> datasets) {
        return permissionEvaluator.hasPermission(SecurityContextHolder.getContext().getAuthentication(),
                datasets, "VIEW_ONCOLOGY_PACKAGE");
    }

    @ApiOperation(
            value = "Gets past medical history data for a single subject",
            nickname = "getPastMedicalHistory",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/past-medical-history", method = POST)
    @Cacheable
    public List<Map<String, String>> getPastMedicalHistory(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<MedicalHistoryFilters> requestBody) {
        return pastMedicalHistoryService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @ApiOperation(
            value = "Gets RECIST assessment data for a single subject",
            nickname = "getAssessment",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/assessment", method = POST)
    public List<Map<String, String>> getAssessment(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<AssessmentFilters> requestBody) {
        return assessmentService.getSingleSubjectData(
                requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @ApiOperation(
            value = "Gets surgical history data for single subject",
            nickname = "getSurgicalHistory",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/surgical-history", method = POST)
    @Cacheable
    public List<Map<String, String>> getSurgicalHistory(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<SurgicalHistoryFilters> requestBody) {
        return surgicalHistoryService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @ApiOperation(
            value = "Gets concurrent conditions at study entry for single subject",
            nickname = "getConcurrentConditionAtStudyEntry",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/concurrent-conditions", method = POST)
    @Cacheable
    public List<Map<String, String>> getConcurrentConditionsAtStudyEntry(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<MedicalHistoryFilters> requestBody) {
        return currentMedicalHistoryService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }

    @ApiOperation(
            value = "Gets conmeds for single subject",
            nickname = "getConmeds",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/conmeds", method = POST)
    @Cacheable
    public List<Map<String, String>> getConmeds(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<ConmedFilters> requestBody) {
        return conmedService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @ApiOperation(
            value = "Gets out of range lab data for single subject",
            nickname = "getLabs",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/labs", method = POST)
    @Cacheable
    public List<Map<String, String>> getLabs(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<LabFilters> requestBody) {
        return labService.getOutOfRangeSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @ApiOperation(
            value = "Gets pathology data for single subject",
            nickname = "getPathology",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/pathgen", method = POST)
    public List<Map<String, String>> getPathology(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<PathologyFilters> requestBody) {
        return pathologyService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @ApiOperation(
            value = "Gets disease extent data for single subject",
            nickname = "getDiseaseExtent",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/disease-extent", method = POST)
    public List<Map<String, String>> getDiseaseExtent(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<DiseaseExtentFilters> requestBody) {
        return diseaseExtentService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @ApiOperation(
            value = "Gets past anti-cancer chemotherapy data for single subject",
            nickname = "getPastChemotherapy",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/past-chemotherapy", method = POST)
    public List<Map<String, String>> getPastChemotherapy(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<ChemotherapyFilters> requestBody) {
        return pastChemotherapyService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @ApiOperation(
            value = "Gets past anti-cancer radiotherapy data for single subject",
            nickname = "getPastRadiotherapy",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/past-radiotherapy", method = POST)
    public List<Map<String, String>> getPastRadiotherapy(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<RadiotherapyFilters> requestBody) {
        return radiotherapyService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @ApiOperation(
            value = "Gets targets lesions data for single subject",
            nickname = "getTargetLesion",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/target-lesion", method = POST)
    public List<Map<String, String>> getTargetLesion(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<TargetLesionFilters> requestBody) {
        return targetLesionService.getSingleSubjectData(requestBody.getDatasetsObject(),
                requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @ApiOperation(
            value = "Gets assessed non target lesion data for a single subject",
            nickname = "getAssessedNonTargetLesion",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/non-target-lesion", method = POST)
    @Cacheable
    public List<Map<String, String>> getNonTargetLesion(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<NonTargetLesionFilters> requestBody) {
        return nonTargetLesionService.getSingleSubjectData(requestBody.getDatasetsObject(),
                requestBody.getSubjectId(), requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @ApiOperation(
            value = "Gets current anti-cancer chemotherapy data for single subject",
            nickname = "getPostChemotherapy",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/post-chemotherapy", method = POST)
    public List<Map<String, String>> getPostChemotherapy(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<ChemotherapyFilters> requestBody) {
        return postChemotherapyService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }

    @ApiOperation(
            value = "Gets discontinuation of drug data for single subject",
            nickname = "getDrugDisc",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/dosedisc", method = POST)
    @Cacheable
    public List<Map<String, String>> getDrugDisc(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<DoseDiscFilters> requestBody) {
        return doseDiscService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }

    @ApiOperation(
            value = "Gets study drug administration for single subject",
            nickname = "getDrugDos",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/drug-dose", method = POST)
    @Cacheable
    public List<Map<String, String>> getDrugDos(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<DrugDoseFilters> requestBody) {
        return drugDoseService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }

    @ApiOperation(
            value = "Gets dose limiting toxicities for single subject",
            nickname = "getDoseLimiting",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/dose-limiting", method = POST)
    @Cacheable
    public List<Map<String, String>> getDoseLimiting(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<AeFilters> requestBody) {
        return doseLimitingService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @ApiOperation(
            value = "Gets second time of progression for single subject",
            nickname = "getSecondTimeOfProgression",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/second-time-of-progression", method = POST)
    public List<Map<String, String>> getSecondTimeOfProgression(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<SecondTimeOfProgressionFilters> requestBody) {
        return secondTimeOfProgressionService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }

    @ApiOperation(
            value = "Gets adverse events for single subject",
            nickname = "getAes",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/aes", method = POST)
    @Cacheable
    public List<Map<String, String>> getAes(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<AeFilters> requestBody) {
        return aeService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }

    @ApiOperation(
            value = "Gets header info for single subject",
            nickname = "getHeader",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/header", method = POST)
    @Cacheable
    public List<Map<String, String>> getHeader(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<SecondTimeOfProgressionFilters> requestBody) {
        return singleSubjectViewSummaryService.getHeaderData(requestBody.getDatasetsObject(), requestBody.getSubjectId());
    }

    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION_AND_ONCOLOGY)
    @ApiOperation(
            value = "Gets survival status for single subject",
            nickname = "getSurvivalStatus",
            response = List.class,
            httpMethod = "POST"
    )
    @RequestMapping(value = "/survival-status", method = POST)
    public List<Map<String, String>> getSurvivalStatus(
            @ApiParam(value = "SingleSubjectRequest: The subject ID to get the data for, "
                    + "e.g. {datasets: [], eventFilters : {}, subjectId: 'SUBJECT0003'}", required = true)
            @RequestBody @Valid SingleSubjectRequest<SurvivalStatusFilters> requestBody) {
        return survivalStatusService.getSingleSubjectData(requestBody.getDatasetsObject(), requestBody.getSubjectId(),
                requestBody.getEventFilters());
    }
}
