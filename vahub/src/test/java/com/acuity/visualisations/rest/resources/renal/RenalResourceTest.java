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

package com.acuity.visualisations.rest.resources.renal;

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RenalFilters;
import com.acuity.visualisations.rawdatamodel.service.event.RenalService;
import com.acuity.visualisations.rest.model.request.renal.RenalRequest;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RenalResourceTest {
    private static final String RESOURCE_URL = "/resources/renal";

    @InjectMocks
    private RenalResource renalResource;

    @Mock
    private RenalService mockRenalService;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(renalResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetFilters() throws Exception {
        RenalRequest request = new RenalRequest(PopulationFilters.empty(), RenalFilters.empty());
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockRenalService.getAvailableFilters(any(Datasets.class),
                any(RenalFilters.class),
                any(PopulationFilters.class)
        )).thenReturn(RenalFilters.empty());

        mvc.perform(post(RESOURCE_URL + "/filters")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(mockRenalService, times(1))
                .getAvailableFilters(eq(DUMMY_DETECT_DATASETS), any(RenalFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(mockRenalService);
    }

    @Test
    public void shouldGetFilteredSubjects() throws Exception {
        RenalRequest request = new RenalRequest(PopulationFilters.empty(), RenalFilters.empty());
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        List<String> subjectsToReturn = Arrays.asList("subject1", "subject2");

        when(mockRenalService.getSubjects(any(Datasets.class), any(RenalFilters.class), any(PopulationFilters.class)))
                .thenReturn(subjectsToReturn);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(RESOURCE_URL + "/filters-subjects").
                content(mapper.writeValueAsString(request)).
                contentType(MediaType.APPLICATION_JSON);

        mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", equalTo("subject1")))
                .andExpect(jsonPath("$[1]", equalTo("subject2")))
                .andReturn();

        verify(mockRenalService, times(1))
                .getSubjects(eq(DUMMY_DETECT_DATASETS), any(RenalFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(mockRenalService);
    }
}
