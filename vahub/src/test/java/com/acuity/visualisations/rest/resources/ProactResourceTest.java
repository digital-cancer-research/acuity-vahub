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

import com.acuity.va.security.acl.domain.AcuitySidDetails;
import com.acuity.visualisations.common.util.Security;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rawdatamodel.service.proact.ProactPopulationService;
import com.acuity.visualisations.rawdatamodel.vo.proact.ProactPatient;
import com.acuity.visualisations.rawdatamodel.vo.proact.ProactStudy;
import com.acuity.visualisations.rest.security.GrantedAuthorityDTO;
import com.acuity.visualisations.rest.test.config.DisableAutowireRequiredInitializer;
import com.acuity.va.security.acl.domain.DetectDataset;
import com.acuity.va.security.acl.domain.AcuityDataset;
import com.acuity.va.security.acl.domain.AcuityObjectIdentityWithPermission;
import com.acuity.va.security.acl.domain.vasecurity.DatasetInfo;
import com.acuity.va.security.acl.permissions.AcuityPermissions;
import com.acuity.va.security.auth.common.ISecurityResourceClient;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.model.Sid;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        initializers = DisableAutowireRequiredInitializer.class,
        classes = {MockServletContext.class}
)
@WebAppConfiguration
@SuppressWarnings("unchecked")
public class ProactResourceTest {

    private static final String DEVELOPMENT_GROUP = "DEVELOPMENT_GROUP";
    private static final String STUDY_CODE = "STUDYID001";
    private static final String SUBJECT_CODE1 = "E0001";
    private static final String STUDY_NAME = "Dummy study";

    @Mock
    private ISecurityResourceClient securityResourceClient;

    @Mock
    private InfoService infoService;

    @Mock
    private ProactPopulationService proactPopulationService;

    @InjectMocks
    private ProactResource proactResource;

    @Mock
    private Security security;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Mock
    private AcuitySidDetails acuitySidDetails;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(proactResource).build();

        List<Sid> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthoritySid(DEVELOPMENT_GROUP));

        when(acuitySidDetails.toSids()).thenReturn(authorities);

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private String USER_PRID = "test1234";

    @Test
    public void shouldGetAllowedProactStudies() throws Exception {
        List<AcuityObjectIdentityWithPermission> rois = generateAcuityObjectIdentityWithPermissions();
        when(securityResourceClient.getAclsForUser(eq(USER_PRID))).thenReturn(rois);
        DatasetInfo datasetInfo1 = createDatasetInfo("STDY4321");
        DatasetInfo datasetInfo2 = createDatasetInfo("AZD5432");
        when(infoService.getDatasetInfo((AcuityDataset) rois.get(0))).thenReturn(datasetInfo1);
        when(infoService.getDatasetInfo((AcuityDataset) rois.get(2))).thenReturn(datasetInfo2);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.
                get("/resources/proact/acl/studies/" + USER_PRID).contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isOk())
                .andDo(print()).andReturn();

        List<ProactStudy> studies = mapper.readValue(result.getResponse().getContentAsString(), mapper.getTypeFactory()
                .constructCollectionType(List.class, ProactStudy.class));

        assertThat(studies.size()).isEqualTo(1);
        assertThat(studies.get(0).getStudyCode()).isEqualTo(datasetInfo1.getClinicalStudy());
        assertThat(studies.get(0).getDrugProgramme()).isEqualTo(datasetInfo1.getDrugProgramme());
        assertThat(studies.get(0).getProjectName()).isEqualTo(datasetInfo1.getName());
        verify(infoService, times(1)).getDatasetInfo(any());
        verify(securityResourceClient, times(1)).getAclsForUser(any());
    }

    @Test
    public void shouldGetStudyWithPatients() throws Exception {
        List<AcuityObjectIdentityWithPermission> rois = generateAcuityObjectIdentityWithPermissions();
        AcuityDataset dataset = (AcuityDataset) rois.get(0);
        when(securityResourceClient.getAclsForUser(eq(USER_PRID))).thenReturn(rois);
        when(proactPopulationService.getProactPatientList(any())).thenReturn(generateProactPatients());
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.
                get("/resources/proact/user/" + USER_PRID + "/study/" + STUDY_CODE + "/patients").contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isOk())
                .andDo(print()).andReturn();

        ProactStudy study = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<ProactStudy>() {
        });

        assertThat(study.getStudyCode()).isEqualTo(dataset.getClinicalStudyCode());
        assertThat(study.getProjectName()).isEqualTo(dataset.getClinicalStudyName());
        assertThat(study.getDrugProgramme()).isEqualTo(dataset.getDrugProgramme());
        assertThat(study.getPatients().get(0).getSubjectCode()).isEqualTo(SUBJECT_CODE1);
        verify(securityResourceClient, times(1)).getAclsForUser(any());
        verify(proactPopulationService, times(1)).getProactPatientList(any());
    }

    private List<ProactPatient> generateProactPatients() {
        List<ProactPatient> res = new ArrayList<>();
        ProactPatient patient1 = new ProactPatient();
        patient1.setSubjectCode(SUBJECT_CODE1);
        res.add(patient1);
        return res;
    }

    @Test
    public void shouldGetUserGroups() throws Exception {

        List<AcuityObjectIdentityWithPermission> rois = generateAcuityObjectIdentityWithPermissions();
        when(securityResourceClient.loadUserByUsername(eq(USER_PRID))).thenReturn(acuitySidDetails);
        DatasetInfo datasetInfo1 = createDatasetInfo("STDY4321");
        DatasetInfo datasetInfo2 = createDatasetInfo("AZD5432");
        when(infoService.getDatasetInfo((AcuityDataset) rois.get(0))).thenReturn(datasetInfo1);
        when(infoService.getDatasetInfo((AcuityDataset) rois.get(2))).thenReturn(datasetInfo2);
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.
                get("/resources/proact/acl/groups/" + USER_PRID).contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(request)
                .andExpect(status().isOk())
                .andDo(print()).andReturn();

        List<GrantedAuthorityDTO> grantedAuthoritySids = mapper.readValue(result.getResponse().getContentAsString(), mapper.getTypeFactory()
                .constructCollectionType(List.class, GrantedAuthorityDTO.class));

        assertThat(grantedAuthoritySids.size()).isEqualTo(1);
        assertThat(grantedAuthoritySids.get(0).getGrantedAuthority()).isEqualTo(DEVELOPMENT_GROUP);
        verify(securityResourceClient, times(1)).loadUserByUsername(USER_PRID);
        verify(acuitySidDetails, times(1)).toSids();
    }

    private DatasetInfo createDatasetInfo(String clinicalStudy) {
        DatasetInfo d = new DatasetInfo();
        d.setName("1234");
        d.setClinicalStudy(clinicalStudy);
        d.setDrugProgramme("AZD");
        return d;
    }

    private List<AcuityObjectIdentityWithPermission> generateAcuityObjectIdentityWithPermissions() {
        AcuityDataset acuityDataset = new AcuityDataset(3L, "d3");
        acuityDataset.setViewPermissionMask(AcuityPermissions.VIEW_PROACT_PACKAGE.getMask());
        acuityDataset.setClinicalStudyCode(STUDY_CODE);
        acuityDataset.setClinicalStudyName(STUDY_NAME);
        AcuityObjectIdentityWithPermission detectDataset = new DetectDataset(4L, "d4");
        detectDataset.setViewPermissionMask(AcuityPermissions.VIEW_VISUALISATIONS.getMask());
        AcuityObjectIdentityWithPermission acuityDataset2 = new AcuityDataset(5L, "d5");
        acuityDataset2.setViewPermissionMask(AcuityPermissions.VIEW_ONCOLOGY_PACKAGE.getMask()
                | AcuityPermissions.VIEW_VISUALISATIONS.getMask());
        return Arrays.asList(acuityDataset, detectDataset, acuityDataset2);
    }


}
