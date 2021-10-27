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

package com.acuity.visualisations.rest.resources.population;

import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rest.model.request.population.PopulationRequest;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.va.security.acl.domain.Datasets;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.SUBJECT_ID;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unchecked")
public class PopulationResourceTest {
    @Mock
    private PopulationService mockPopulationService;
    @InjectMocks
    private PopulationResource populationResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(populationResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetAvailableFilters() throws Exception {

        com.acuity.visualisations.rawdatamodel.filters.PopulationFilters populationFilters =
                new com.acuity.visualisations.rawdatamodel.filters.PopulationFilters();
        populationFilters.setDeath(new com.acuity.visualisations.rawdatamodel.filters.SetFilter<>(newArrayList("Y")));
        populationFilters.setSpecifiedEthnicGroup(new com.acuity.visualisations.rawdatamodel.filters.SetFilter<>(newArrayList("white")));
        final DateRangeFilter randomisationDate = new DateRangeFilter();
        randomisationDate.setFrom(new Date());
        randomisationDate.setTo(new Date());
        populationFilters.setRandomisationDate(randomisationDate);

        PopulationRequest populationRequest = new PopulationRequest();
        populationRequest.setPopulationFilters(populationFilters);
        populationRequest.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        when(mockPopulationService.getAvailableFilters(
                any(Datasets.class),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class))).
                thenReturn(populationFilters);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/population/filters").
                content(mapper.writeValueAsString(populationRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        com.acuity.visualisations.rawdatamodel.filters.PopulationFilters returnedPopulationFilters =
                mapper.readValue(result.getResponse().getContentAsString(), com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class);

        assertThat(returnedPopulationFilters).isEqualTo(populationFilters);
        verify(mockPopulationService, times(1)).getAvailableFilters(eq(DUMMY_ACUITY_DATASETS),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class));

        verifyNoMoreInteractions(mockPopulationService);
    }

    @Test
    public void shoudGetValueForSSV() throws Exception {
        SingleSubjectRequest request = new SingleSubjectRequest();
        request.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());
        request.setEventFilters(PopulationFilters.empty());
        request.setSubjectId(SUBJECT_ID);

        List<Map<String, String>> mockResponse = new ArrayList<>();
        mockResponse.add(new HashMap<>());

        when(mockPopulationService.getSingleSubjectData(eq(DUMMY_ACUITY_DATASETS), eq(SUBJECT_ID), any(PopulationFilters.class)))
                .thenReturn(mockResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/population/single-subject").
                content(mapper.writeValueAsString(request)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();


        List<Map<String, String>> response
                = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Map<String, String>>>() {
        });

        assertThat(response).isEqualTo(mockResponse);
        verify(mockPopulationService, times(1))
                .getSingleSubjectData(any(Datasets.class), any(String.class), any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class));
        verifyNoMoreInteractions(mockPopulationService);

    }
}
