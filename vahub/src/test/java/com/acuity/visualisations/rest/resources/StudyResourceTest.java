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

import com.acuity.visualisations.common.util.Security;
import com.acuity.visualisations.rawdatamodel.dataproviders.StudyInfoDataProvider;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rawdatamodel.dataset.info.vo.CombinedStudyInfo;
import com.acuity.visualisations.rawdatamodel.dataset.info.vo.StudySelectionDatasetInfo;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.visualisations.rest.resources.study.PermissionsStrategy;
import com.acuity.visualisations.rest.resources.study.StudyResource;
import com.acuity.va.security.acl.domain.ClinicalStudy;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.DrugProgramme;
import com.acuity.va.security.acl.domain.AcuityDataset;
import com.acuity.va.security.acl.domain.AcuityObjectIdentity;
import com.acuity.va.security.acl.domain.AcuityObjectIdentityWithPermission;
import com.acuity.va.security.acl.domain.AcuitySidDetails;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class StudyResourceTest {

    @Mock
    private InfoService infoService;
    @Mock
    private Security security;
    @Mock
    private PermissionsStrategy permissionsStrategy;
    @Mock
    private AcuitySidDetails userDetails;
    @Mock
    private StudyInfoDataProvider studyInfoDataProvider;

    @InjectMocks
    private StudyResource studyResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(studyResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetCombinedStudyInfo() throws Exception {

        AcuityObjectIdentityWithPermission acuityClinicalStudy = new ClinicalStudy(1L);
        AcuityObjectIdentityWithPermission detectClinicalStudy = new ClinicalStudy(2L);
        AcuityObjectIdentityWithPermission acuityDataset = new AcuityDataset(3L, "AcuityDataset");
        AcuityObjectIdentityWithPermission acuityNotCachedDataset = new AcuityDataset(4L, "AcuityDatasetNotCached");
        AcuityObjectIdentityWithPermission acuityDrugProgramme = new DrugProgramme(5L);
        List<AcuityObjectIdentity> rois = Arrays.asList(
                acuityClinicalStudy, detectClinicalStudy, acuityDataset, acuityNotCachedDataset, acuityDrugProgramme);

        StudySelectionDatasetInfo acuityNotCachedDatasetInfo = StudySelectionDatasetInfo.builder()
                .datasetId(4L).build();
        Date dco1 = new Date(987654321);
        Date lre1 = new Date(987654321);
        StudySelectionDatasetInfo acuityDatasetInfo = StudySelectionDatasetInfo.builder()
                .datasetId(3L).numberOfDosedSubjects(2).dataCutoffDate(dco1)
                .lastRecordedEventDate(lre1).build();

        when(security.getAcuityUserDetails()).thenReturn(userDetails);
        when(userDetails.getSidAsString()).thenReturn("Sid98765");
        when(permissionsStrategy.getAcuityObjectIdentities(eq("Sid98765"))).thenReturn(rois);

        StudyInfo studyInfoR = StudyInfo.builder()
                .dataCutoffDate(dco1)
                .lastEventDate(lre1)
                .blinded(true)
                .ctcaeVersion("v5.0")
                .randomised(true)
                .regulatory(true)
                .numberOfDosedSubjects(2)
                .build();

        when(studyInfoDataProvider.getData(Mockito.argThat(new ArgumentMatcher<Dataset>() {
            @Override
            public boolean matches(Object item) {
                return item instanceof Dataset && ((Dataset) item).getId() == 3L;
            }
        })))
                .thenReturn(Collections.singletonList(studyInfoR));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.
                get("/resources/study/available_study_info");

        MvcResult result = mvc.perform(request)
                .andExpect(status().isOk())
                .andDo(print()).andReturn();

        CombinedStudyInfo<AcuityObjectIdentityWithPermission> returnedCombinedStudyInfo
                = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<CombinedStudyInfo<AcuityObjectIdentityWithPermission>>() {
        });

        assertThat(returnedCombinedStudyInfo.getRoisWithPermission()).isEqualTo(rois);
        assertThat(returnedCombinedStudyInfo.getStudySelectionDatasetInfo())
                .isEqualTo(new HashSet<>(Arrays.asList(acuityDatasetInfo, acuityNotCachedDatasetInfo)));
        verify(permissionsStrategy).getAcuityObjectIdentities(eq("Sid98765"));
        verify(studyInfoDataProvider).getData(eq(new AcuityDataset(3L)));
        verify(studyInfoDataProvider).getData(eq(new AcuityDataset(4L)));
        verifyNoMoreInteractions(studyInfoDataProvider);
        verifyNoMoreInteractions(permissionsStrategy);
        verifyNoMoreInteractions(infoService);
    }
}
