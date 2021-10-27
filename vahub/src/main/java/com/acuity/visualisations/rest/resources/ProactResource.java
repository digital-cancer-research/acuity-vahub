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

import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rawdatamodel.service.proact.ProactPopulationService;
import com.acuity.visualisations.rawdatamodel.vo.proact.ProactPatient;
import com.acuity.visualisations.rawdatamodel.vo.proact.ProactStudy;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuityDataset;
import com.acuity.va.security.acl.domain.AcuitySidDetails;
import com.acuity.va.security.acl.domain.vasecurity.DatasetInfo;
import com.acuity.va.security.acl.permissions.AcuityCumulativePermissionsAsRoles;
import com.acuity.va.security.auth.common.ISecurityResourceClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Authorization;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.acuity.va.security.acl.permissions.AcuityPermissions.VIEW_PROACT_PACKAGE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * REST resource for PROACT inter-communication
 *
 * @author Alexander Gridin
 */
@RestController
@RequestMapping(value = "/resources/proact", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
@Api(value = "/proact", description = "PROACT integration API", authorizations = {
        @Authorization(value = "basic")
})
@Slf4j
public class ProactResource {

    @Autowired
    private ISecurityResourceClient securityResourceClient;

    @Autowired
    private InfoService infoService;


    @Autowired
    private ProactPopulationService proactPopulationService;


    /**
     * Gets the list of groups user belongs to.
     */
    @ApiOperation(value = "Gets the list of security identities user belongs to.",
            nickname = "getUserGroups",
            httpMethod = "GET",
            response = GrantedAuthoritySid.class)
    @RequestMapping(value = "/acl/groups/{user:.+}", method = GET)
    public List<GrantedAuthoritySid> getUserGroups(@ApiParam("User PRID") @PathVariable("user") String user) {
        final AcuitySidDetails acuityUserDetails;
        try {
            acuityUserDetails = securityResourceClient.loadUserByUsername(user);
            return acuityUserDetails.toSids().stream()
                    .filter(t -> t instanceof GrantedAuthoritySid).map(sid -> (GrantedAuthoritySid) sid).collect(Collectors.toList());
        } catch (IllegalAccessException | IOException e) {
            log.error(e.getMessage(), e);
        }
        return new ArrayList<>();
    }


    /**
     * Gets the list of studies the user has access to.
     */
    @ApiOperation(value = "Gets the list of studies the user has access to.",
            nickname = "getStudiesForUser",
            httpMethod = "GET",
            response = ProactStudy.class
    )
    @RequestMapping(value = "/acl/studies/{user:.+}", method = GET)
    public List<ProactStudy> getStudiesForUser(@ApiParam("User PRID") @PathVariable("user") String user) {
        return getAcuityDatasetsForUser(user).stream()
                .map(dataset -> infoService.getDatasetInfo(dataset))
                .filter(Objects::nonNull)
                .map(this::toStudy)
                .collect(Collectors.toList());
    }

    /**
     * Gets the study with patients.
     *
     * @param studyCode study code
     * @return ProactStudy
     */
    @ApiOperation(value = "Gets study with patients",
            nickname = "getStudyWithPatients",
            httpMethod = "GET",
            response = ProactStudy.class
    )
            @RequestMapping(value = "/user/{user:.+}/study/{code}/patients", method = GET)
    public ProactStudy getStudyWithPatients(@ApiParam("User PRID") @PathVariable("user") String user,
                                            @ApiParam("Study code") @PathVariable("code") String studyCode) {
        Dataset dataset = getAcuityDatasetsForUser(user).stream()
                .filter(acuityDataset -> StringUtils.equals(acuityDataset.getClinicalStudyCode(), studyCode))
                .findFirst().orElse(null);
        if (dataset == null) {
            log.error("Cannot find PROACT dataset by code={}", studyCode);
            return ProactStudy.builder().build();
        }
        List<ProactPatient> resultPatients = proactPopulationService.getProactPatientList(new Datasets(dataset));
        return ProactStudy.builder().patients(resultPatients).studyCode(studyCode)
                .projectName(dataset.getClinicalStudyName())
                .drugProgramme(dataset.getDrugProgramme()).build();
    }

    private List<AcuityDataset> getAcuityDatasetsForUser(String user) {
        return securityResourceClient.getAclsForUser(user).stream()
                .filter(roi -> roi.thisAcuityType() && roi.thisDatasetType())
                .map(d -> (AcuityDataset) d)
                .filter(this::hasProactPermissions)
                .collect(Collectors.toList());
    }

    private boolean hasProactPermissions(AcuityDataset dataset)  {
        return dataset.getViewPermissionMask() != null
                && AcuityCumulativePermissionsAsRoles.checkPermission(VIEW_PROACT_PACKAGE.getMask(), dataset.getViewPermissionMask());
    }

    private ProactStudy toStudy(DatasetInfo datasetInfo) {
        return datasetInfo != null ? ProactStudy.builder().studyCode(datasetInfo.getClinicalStudy()).projectName(datasetInfo.getName())
                .drugProgramme(datasetInfo.getDrugProgramme()).build() : null;
    }

}
