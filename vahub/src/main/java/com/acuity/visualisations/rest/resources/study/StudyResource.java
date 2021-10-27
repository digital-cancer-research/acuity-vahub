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

package com.acuity.visualisations.rest.resources.study;

import com.acuity.va.auditlogger.annotation.LogArg;
import com.acuity.va.auditlogger.annotation.LogOperation;
import com.acuity.va.security.acl.domain.AcuityDataset;
import com.acuity.va.security.acl.domain.AcuityObjectIdentity;
import com.acuity.va.security.acl.domain.AcuitySidDetails;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import com.acuity.visualisations.common.study.metadata.InstanceMetadata;
import com.acuity.visualisations.common.study.metadata.MultiMetadataService;
import com.acuity.visualisations.common.util.Security;
import com.acuity.visualisations.config.async.executor.context.MDCTaskContextCapturer;
import com.acuity.visualisations.config.async.executor.context.TaskContext;
import com.acuity.visualisations.rawdatamodel.dataproviders.StudyInfoDataProvider;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rawdatamodel.dataset.info.vo.CombinedStudyInfo;
import com.acuity.visualisations.rawdatamodel.dataset.info.vo.StudySelectionDatasetInfo;
import com.acuity.visualisations.rawdatamodel.dataset.info.vo.StudyWarnings;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.visualisations.rest.util.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.ALL_VALUE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

/**
 * Created by ksnd199.
 */
@RestController
@Api(value = "/resources/study", description = "rest endpoints for for a study")
@RequestMapping(value = "/resources/study",
        consumes = {APPLICATION_JSON_VALUE, ALL_VALUE},
        produces = APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
@Slf4j
public class StudyResource {

    private final MultiMetadataService multiMetadataService;
    private final Security security;
    private final InfoService infoService;
    private final StudyInfoDataProvider studyInfoDataProvider;
    private final PermissionsStrategy permissionsStrategy;

    @ApiOperation(
            value = "Gets the information for the study",
            nickname = "getMetadataInfo",
            response = String.class,
            httpMethod = "POST"
    )
    @PreAuthorize(Constants.PRE_AUTHORISE_VISUALISATION)
    @RequestMapping(value = "/info", method = POST)
    @LogOperation(name = "STUDY_METADATA", logOnlyOnSuccess = true, value = {
            @LogArg(arg = 0, name = "DATASETS", expression = "getDatasetsObject().getDatasetsLoggingObject()", isDatasetsLoggingObject = true)
    })
    public String getMetadataInfo(@RequestBody DatasetsRequest requestBody) {

        // static cached dataset info
        String generatedMetadata = multiMetadataService.generateMetadata(requestBody.getDatasetsObject());

        // user dependent spotfire info
        InstanceMetadata instanceMetadata = InstanceMetadata.read(generatedMetadata);

        security.getAcuityUserDetails();


        return instanceMetadata.build();
    }

    @ApiOperation("Gets all user study info for cached datasets")
    @GetMapping(value = "/available_study_info")
    public CombinedStudyInfo<AcuityObjectIdentity> getUserStudyInfo() {
        AcuitySidDetails acuityUserDetails = security.getAcuityUserDetails();
        log.debug("GET: get study info for User " + acuityUserDetails);
        return cachedCombinedStudyInfo(permissionsStrategy.getAcuityObjectIdentities(acuityUserDetails.getSidAsString()));
    }

    @GetMapping("/all_study_info")
    public CombinedStudyInfo<AcuityObjectIdentity> getAllStudyInfo() {
        log.debug("GET: get all study info");
        List<AcuityObjectIdentity> rois = infoService.generateObjectIdentities();
        return cachedCombinedStudyInfo(rois).toBuilder().counts(infoService.countROIs(rois)).build();
    }

    private <T extends AcuityObjectIdentity> CombinedStudyInfo<T> cachedCombinedStudyInfo(List<T> rois) {
        Map<AcuityObjectIdentity, Optional<StudyInfo>> datasetsStudyInfo = rois.parallelStream()
                .filter(AcuityObjectIdentity::thisDatasetType)
                .collect(Collectors.toMap(roi -> roi,
                        getMdcWrappedFunction(roi -> studyInfoDataProvider.getData(
                                Dataset.createDataset(roi.getClass().getSimpleName(), roi.getId(), roi.getName()))
                        .stream()
                        .findAny())));

        return getCombinedStudyInfo(rois, datasetsStudyInfo);
    }

    /*
     * Here we capture current thread mdc context, and then set it up in the context of actual function invocation
     * Need this to preserve username from security context in logs
     * */
    private <T extends AcuityObjectIdentity> Function<T, Optional<StudyInfo>> getMdcWrappedFunction(
            Function<T, Optional<StudyInfo>> supplyFunction) {
        TaskContext context = new MDCTaskContextCapturer().capture();
        return roi -> {
            context.setup();
            try {
                return supplyFunction.apply(roi);
            } finally {
                context.teardown();
            }
        };
    }

    private static <T extends AcuityObjectIdentity> CombinedStudyInfo<T> getCombinedStudyInfo(
            List<T> rois, Map<AcuityObjectIdentity, Optional<StudyInfo>> datasetsStudyInfo) {
        CombinedStudyInfo<T> res = new CombinedStudyInfo<>();
        res.setRoisWithPermission(rois);

        Set<StudySelectionDatasetInfo> studySelectionDatasetInfo = datasetsStudyInfo.entrySet().stream()
                .map(entry -> {
                    StudySelectionDatasetInfo.StudySelectionDatasetInfoBuilder datasetInfo = StudySelectionDatasetInfo.builder();
                    datasetInfo.datasetId(entry.getKey().getId());
                    if (entry.getValue().isPresent()) {
                        StudyInfo studyInfo = entry.getValue().get();
                        datasetInfo.numberOfDosedSubjects((int) studyInfo.getNumberOfDosedSubjects());
                        datasetInfo.dataCutoffDate(studyInfo.getDataCutoffDate());
                        datasetInfo.lastRecordedEventDate(studyInfo.getLastEventDate());
                    }
                    return datasetInfo.build();
                })
                .collect(Collectors.toSet());

        res.setStudySelectionDatasetInfo(studySelectionDatasetInfo);
        res.setStudyWarnings(getStudyWarnings(datasetsStudyInfo));
        return res;
    }

    private static Set<StudyWarnings> getStudyWarnings(Map<AcuityObjectIdentity, Optional<StudyInfo>> roiSubjects) {
        Map<String, StudyWarnings> result = roiSubjects.entrySet().stream()
                .map(entry -> {
                    String name = getStudyNameFromAcuityDataset(entry.getKey());
                    StudyWarnings studyWarnings = new StudyWarnings();
                    entry.getValue().ifPresent(studyInfo -> {
                        studyWarnings.setStudyId(name);
                        studyWarnings.setBlinded(studyInfo.isBlinded());
                        studyWarnings.setRandomised(studyInfo.isRandomised());
                        studyWarnings.setForRegulatoryPurposes(studyInfo.isRegulatory());
                    });
                    return studyWarnings;
                })
                .collect(Collectors.toMap(
                        StudyWarnings::getStudyId,
                        Function.identity(),
                        (existing, replacement) -> {
                            // If a study has 2 or more datasets.
                            existing.setRandomised(existing.isRandomised() && replacement.isRandomised());
                            existing.setBlinded(existing.isBlinded() && replacement.isBlinded());
                            existing.setForRegulatoryPurposes(existing.isForRegulatoryPurposes() && replacement.isForRegulatoryPurposes());
                            return existing;
                        }
                ));
        return new HashSet<>(result.values());
    }

    private static String getStudyNameFromAcuityDataset(AcuityObjectIdentity acuityObjectIdentity) {
        if (acuityObjectIdentity instanceof AcuityDataset) {
            return ((AcuityDataset) acuityObjectIdentity).getClinicalStudyName();
        }
        return null;
    }
}
