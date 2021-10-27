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
import com.acuity.va.security.acl.domain.ClinicalStudy;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.DrugProgramme;
import com.acuity.va.security.acl.domain.AcuityObjectIdentity;
import com.acuity.va.security.acl.domain.AcuityObjectIdentityImpl;
import com.acuity.va.security.acl.domain.vasecurity.ClinicalStudyInfo;
import com.acuity.va.security.acl.domain.vasecurity.DatasetInfo;
import com.acuity.va.security.acl.domain.vasecurity.DrugProgrammeInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

/**
 * Created by ksnd199.
 */
@RestController
@Api(value = "/resources/security/info", description = "data va security")
@RequestMapping(value = "/resources/security/info", consumes = APPLICATION_JSON_VALUE, produces = APPLICATION_JSON_VALUE)
public class VASecurityInfoResource {

    @Autowired
    private InfoService infoService;

    @ApiOperation(
            value = "Gets a list of all of the AcuityObjectIdentities in Detect",
            nickname = "acuityObjectIdentities",
            response = AcuityObjectIdentity.class,
            responseContainer = "List",
            httpMethod = "GET"
    )
    @RequestMapping(value = "/rois", method = GET)
    public List<AcuityObjectIdentity> getRois() {
        return infoService.generateObjectIdentities();
    }

    @ApiOperation(
            value = "Gets the information regarding a drug programme in Detect",
            nickname = "drugProgrammeInfo",
            response = DrugProgrammeInfo.class,
            httpMethod = "GET"
    )
    @RequestMapping(value = "/DrugProgramme/{drugProgramme}/{drugProgrammeId}", method = GET)
    public DrugProgrammeInfo getDrugProgrammeInfo(@PathVariable("drugProgramme") String drugProgramme,
                                                  @PathVariable("drugProgrammeId") Long drugProgrammeId) {

        DrugProgramme drugProgrammeObj = AcuityObjectIdentityImpl.createDrugProgramme(drugProgramme, drugProgrammeId);
        return infoService.getDrugProgrammeInfo(drugProgrammeObj);
    }

    @ApiOperation(
            value = "Gets the information regarding a clinical study in Detect",
            nickname = "clinicalStudyInfo",
            response = ClinicalStudyInfo.class,
            httpMethod = "GET"
    )
    @RequestMapping(value = "/ClinicalStudy/{clinicalStudy}/{clinicalStudyId}", method = GET)
    public ClinicalStudyInfo getClinicalStudyInfo(@PathVariable("clinicalStudy") String clinicalStudy,
                                                  @PathVariable("clinicalStudyId") Long clinicalStudyId) {

        ClinicalStudy clinicalStudyObj = AcuityObjectIdentityImpl.createClinicalStudy(clinicalStudy, clinicalStudyId);
        return infoService.getClinicalStudyInfo(clinicalStudyObj);
    }

    @ApiOperation(
            value = "Gets the information regarding a dataset in Detect",
            nickname = "datasetInfo",
            response = DatasetInfo.class,
            httpMethod = "GET"
    )
    @RequestMapping(value = "/Dataset/{dataset}/{datasetId}", method = GET)
    public DatasetInfo getDatasetInfo(@PathVariable("dataset") String dataset,
                                      @PathVariable("datasetId") Long datasetId) {

        Dataset datasetObj = AcuityObjectIdentityImpl.createDataset(dataset, datasetId);
        return infoService.getDatasetInfo(datasetObj);
    }
}
