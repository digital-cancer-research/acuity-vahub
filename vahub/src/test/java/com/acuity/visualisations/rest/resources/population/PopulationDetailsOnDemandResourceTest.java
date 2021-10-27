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

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rest.model.request.population.PopulationRequest;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unchecked")
public class PopulationDetailsOnDemandResourceTest {
    @Mock
    private PopulationService mockPopulationService;
    @InjectMocks
    private PopulationDetailsOnDemandResource populationResource;

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
    public void shouldGetDetailsOnDemandData() throws Exception {

        DetailsOnDemandRequest requestBody = new DetailsOnDemandRequest();
        requestBody.setEventIds(newHashSet("Subj-1"));
        requestBody.setSortAttrs(Collections.singletonList(new SortAttrs("subjectId", true)));
        requestBody.setStart(0);
        requestBody.setEnd(1000);
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        Map<String, Object> dod = new HashMap<>();
        dod.put("subjectId", "Subj-1");
        dod.put("plannedArm", "planned_arm");

        List<Map<String, Object>> mockResponse = Arrays.asList(dod);

        when(mockPopulationService.getDetailsOnDemandData(
                any(Datasets.class),
                anySet(),
                anyList(),
                anyLong(),
                anyLong())).thenReturn(mockResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/population/details-on-demand/data").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<Map<String, String>> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<Map<String, String>>>() {
                });

        assertThat(response).isEqualTo(mockResponse);
        verify(mockPopulationService, times(1)).getDetailsOnDemandData(
                any(Datasets.class),
                anySet(),
                anyList(),
                anyLong(),
                anyLong());
        verifyNoMoreInteractions(mockPopulationService);
    }

    @Test
    public void shouldDownloadAllDetailsOnDemandData() throws Exception {
        PopulationRequest requestBody = new PopulationRequest();
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/population/details-on-demand/all-csv").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        assertThat(response).isEqualTo("");
    }

    @Test
    public void shouldDownloadSelectedDetailsOnDemandData() throws Exception {
        DetailsOnDemandRequest requestBody = new DetailsOnDemandRequest();
        requestBody.setEventIds(newHashSet("ev-1"));
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/population/details-on-demand/selected-csv").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        assertThat(response).isEqualTo("");
    }
}
