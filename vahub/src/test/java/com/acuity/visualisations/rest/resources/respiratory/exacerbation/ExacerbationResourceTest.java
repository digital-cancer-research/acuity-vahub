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

package com.acuity.visualisations.rest.resources.respiratory.exacerbation;

import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.event.ExacerbationService;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ExacerbationResourceTest {
    private static final String ROOT_URL = "/resources/respiratory/exacerbation";
    @Mock
    private ExacerbationService mockExacerbationService;
    @InjectMocks
    private ExacerbationResource exacerbationResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(exacerbationResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetAvailableExacerbationFilters() throws Exception {
        ExacerbationFilters exacerbationFilters = ExacerbationFilters.empty();
        exacerbationFilters.setHospitalisation(new SetFilter<>(Arrays.asList("yes")));

        ExacerbationRequest requestBody = new ExacerbationRequest();
        requestBody.setExacerbationFilters(exacerbationFilters);
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockExacerbationService.getAvailableFilters(any(Datasets.class), any(ExacerbationFilters.class), any(PopulationFilters.class)))
                .thenReturn(exacerbationFilters);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(ROOT_URL + "/filters").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);


        mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath(".hospitalisation.values[0]", equalTo(Arrays.asList("yes"))))
                .andReturn();

        verify(mockExacerbationService, times(1))
                .getAvailableFilters(eq(DUMMY_DETECT_DATASETS), any(ExacerbationFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(mockExacerbationService);
    }

    @Test
    public void shouldGetSubjectsForExacerbationFilters() throws Exception {
        ExacerbationRequest requestBody = new ExacerbationRequest();
        requestBody.setExacerbationFilters(ExacerbationFilters.empty());
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        List<String> subjectsToReturn = Arrays.asList("subject1", "subject2");

        when(mockExacerbationService.getSubjects(any(Datasets.class), any(ExacerbationFilters.class), any(PopulationFilters.class)))
                .thenReturn(subjectsToReturn);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(ROOT_URL + "/filtered-subjects").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);


        mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", equalTo("subject1")))
                .andExpect(jsonPath("$[1]", equalTo("subject2")))
                .andReturn();

        verify(mockExacerbationService, times(1))
                .getSubjects(eq(DUMMY_DETECT_DATASETS), any(ExacerbationFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(mockExacerbationService);
    }

    @Test
    public void shouldGetSingleSubjectDataForExacerbation() throws Exception {
        SingleSubjectRequest<ExacerbationFilters> requestBody = new SingleSubjectRequest();
        requestBody.setEventFilters(ExacerbationFilters.empty());
        requestBody.setSubjectId("subject_id");
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        Map<String, String> dodMap = new HashMap<>();
        dodMap.put("column_1", "value_1");
        dodMap.put("column_2", "value_2");

        DetailsOnDemandResponse responseToReturn = new DetailsOnDemandResponse(Arrays.asList(dodMap));

        when(mockExacerbationService.getDetailsOnDemandData(any(Datasets.class), anyString(), any(ExacerbationFilters.class)))
                .thenReturn(responseToReturn.getDodData());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(ROOT_URL + "/single-subject").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dodData[0]", hasKey("column_2")))
                .andExpect(jsonPath("$.dodData[0]", hasValue("value_2")))
                .andExpect(jsonPath("$.dodData[0]", hasKey("column_1")))
                .andExpect(jsonPath("$.dodData[0]", hasValue("value_1")))
                .andReturn();

        verify(mockExacerbationService, times(1))
                .getDetailsOnDemandData(eq(DUMMY_DETECT_DATASETS), anyString(), any(ExacerbationFilters.class));
        verifyNoMoreInteractions(mockExacerbationService);
    }
}
