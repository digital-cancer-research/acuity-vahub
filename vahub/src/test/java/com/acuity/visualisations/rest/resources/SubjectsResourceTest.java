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

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.ssv.SSVSummaryService;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputSSVSummaryData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputSSVSummaryMetadata;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASET;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SubjectsResourceTest {

    private static final String SSV_SUMMARY_PATH = "/resources/subjects/";

    @Mock
    private SSVSummaryService mockSSVSummaryService;

    @InjectMocks
    private SubjectsResource subjectsResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    private String SUBJECT_ID1 = DUMMY_DETECT_DATASET.getId() + "-7916598662";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(subjectsResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldReturnSubjectDetailMetadata() throws Exception {

        DatasetsRequest sr = new DatasetsRequest();
        sr.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        OutputSSVSummaryMetadata ssvSummaryMetadata = OutputSSVSummaryMetadata.builder().build();

        when(mockSSVSummaryService.getSingleSubjectMetadata(any(Datasets.class))).thenReturn(ssvSummaryMetadata);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(SSV_SUMMARY_PATH + "metadata").
                content(mapper.writeValueAsString(sr)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andExpect(status().isOk())
                .andDo(print()).andReturn();

        OutputSSVSummaryMetadata returnedSSVMetadataMetadata = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<OutputSSVSummaryMetadata>() {
                });

        assertThat(returnedSSVMetadataMetadata).isEqualTo(ssvSummaryMetadata);
        verify(mockSSVSummaryService, times(1)).getSingleSubjectMetadata(eq(DUMMY_DETECT_DATASETS));
        verifyNoMoreInteractions(mockSSVSummaryService);
    }

    @Test
    public void shouldListSubjectDetail() throws Exception {

        SingleSubjectRequest<PopulationFilters> sr = new SingleSubjectRequest<>();
        sr.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        sr.setSubjectId(SUBJECT_ID1);

        OutputSSVSummaryData ssvSummaryData = OutputSSVSummaryData.builder().subjectId(SUBJECT_ID1).build();

        when(mockSSVSummaryService.getSingleSubjectData(any(Datasets.class), anyString())).thenReturn(ssvSummaryData);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(SSV_SUMMARY_PATH + "detail").
                content(mapper.writeValueAsString(sr)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andExpect(status().isOk())
                .andDo(print()).andReturn();

        OutputSSVSummaryData returnedOutputSSVSummaryData = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<OutputSSVSummaryData>() {
                });

        assertThat(returnedOutputSSVSummaryData).isEqualTo(ssvSummaryData);
        verify(mockSSVSummaryService, times(1)).getSingleSubjectData(eq(DUMMY_DETECT_DATASETS), eq(SUBJECT_ID1));
        verifyNoMoreInteractions(mockSSVSummaryService);
    }

}
