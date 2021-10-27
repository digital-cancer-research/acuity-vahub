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

package com.acuity.visualisations.rest.resources.vitals;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.visualisations.rawdatamodel.service.event.VitalService;
import com.acuity.visualisations.rest.model.request.vitals.VitalsRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
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

import java.io.Writer;
import java.util.Arrays;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anySet;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class VitalsDetailsOnDemandResourceTest {
    private static final String ROOT_URL = "/resources/vitals/details-on-demand";

    @Mock
    private VitalService mockVitalService;

    @InjectMocks
    private VitalsDetailsOnDemandResource vitalsResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(vitalsResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetDetailsOnDemandData() throws Exception {

        DetailsOnDemandRequest requestBody = new DetailsOnDemandRequest();
        requestBody.setEventIds(newHashSet("1", "2"));
        requestBody.setStart(0);
        requestBody.setEnd(100);
        requestBody.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        Map<String, String> dodColumns = ImmutableMap.of("column1", "value1", "column2", "value2");

        DetailsOnDemandResponse mockResponse = new DetailsOnDemandResponse(Arrays.asList(dodColumns));

        when(mockVitalService.getDetailsOnDemandData(
                any(Datasets.class),
                anySet(),
                anyList(),
                anyLong(),
                anyLong())).thenReturn(mockResponse.getDodData());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(ROOT_URL + "/data").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dodData[0]", hasKey("column2")))
                .andExpect(jsonPath("$.dodData[0]", hasValue("value2")))
                .andExpect(jsonPath("$.dodData[0]", hasKey("column1")))
                .andExpect(jsonPath("$.dodData[0]", hasValue("value1")))
                .andReturn();

        verify(mockVitalService, times(1))
                .getDetailsOnDemandData(
                       eq(DUMMY_ACUITY_DATASETS),
                        anySet(),
                        anyList(),
                        anyLong(),
                        anyLong());
        verifyNoMoreInteractions(mockVitalService);
    }

    @Test
    public void shouldDownloadAllDetailsOnDemandData() throws Exception {
        VitalsRequest requestBody = new VitalsRequest();
        requestBody.setVitalsFilters(new VitalFilters());
        requestBody.setPopulationFilters(new com.acuity.visualisations.rawdatamodel.filters.PopulationFilters());
        requestBody.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(ROOT_URL + "/all-csv").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        verify(mockVitalService, times(1))
                .writeAllDetailsOnDemandCsv(
                        eq(DUMMY_ACUITY_DATASETS),
                        any(Writer.class),
                        any(Filters.class),
                        any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class));
        verifyNoMoreInteractions(mockVitalService);
    }

    @Test
    public void shouldDownloadSelectedDetailsOnDemandData() throws Exception {
        DetailsOnDemandRequest requestBody = new DetailsOnDemandRequest();
        requestBody.setEventIds(newHashSet("ev-1"));
        requestBody.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(ROOT_URL + "/selected-csv").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        verify(mockVitalService, times(1))
                .writeSelectedDetailsOnDemandCsv(
                        eq(DUMMY_ACUITY_DATASETS),
                        anySet(),
                        any(Writer.class));
        verifyNoMoreInteractions(mockVitalService);
    }
}
