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

import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.service.event.CIEventService;
import com.acuity.visualisations.rawdatamodel.service.event.CerebrovascularService;
import com.acuity.visualisations.rawdatamodel.service.event.CvotEndpointService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.model.request.aes.AesAssociatedAesNumbersRequest;
import com.acuity.visualisations.rest.model.request.aes.AesRequest;
import com.acuity.visualisations.rest.test.config.DisableAutowireRequiredInitializer;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
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
public class AesResourceTest {

    @Mock
    @Qualifier("aeService")
    private AeService mockAesService;
    @Mock
    private CIEventService mockCIEventService;
    @Mock
    private CvotEndpointService mockCvotEndpointService;
    @Mock
    private CerebrovascularService mockCerebrovascularService;
    @InjectMocks
    private AesResource aesResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(aesResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetAvailableFilters() throws Exception {

        AeFilters aeFilters = new AeFilters();
        aeFilters.setCausality(new com.acuity.visualisations.rawdatamodel.filters.SetFilter<>(newArrayList("abc")));
        com.acuity.visualisations.rawdatamodel.filters.PopulationFilters populationFilters
                = new com.acuity.visualisations.rawdatamodel.filters.PopulationFilters();

        AesRequest aesRequest = new AesRequest();
        aesRequest.setAesFilters(aeFilters);
        aesRequest.setPopulationFilters(populationFilters);
        aesRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockAesService.getAvailableFilters(
                any(Datasets.class),
                any(AeFilters.class),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class))).
                thenReturn(aeFilters);
        //when(mockAesFilterService.getAvailableFilters(any(Datasets.class), any(AesFilters.class), any(PopulationFilters.class))).thenReturn(aesFilters);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/aes/filters").
                content(mapper.writeValueAsString(aesRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        AeFilters returnedAesFilters = mapper.readValue(result.getResponse().getContentAsString(), AeFilters.class);

        assertThat(returnedAesFilters).isEqualTo(aeFilters);
        verify(mockAesService, times(1)).getAvailableFilters(
                eq(DUMMY_DETECT_DATASETS),
                any(AeFilters.class),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class));
        verifyNoMoreInteractions(mockAesService);
    }

    @Test
    public void shouldGetDetailsOnDemandData() throws Exception {

        DetailsOnDemandRequest requestBody = new DetailsOnDemandRequest();
        requestBody.setEventIds(newHashSet("123", "456"));
        requestBody.setSortAttrs(Collections.singletonList(new SortAttrs("subjectId", false)));
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        List<Map<String, String>> mockResponse = Collections.emptyList();

        when(mockAesService.getDetailsOnDemandData(
                eq(DUMMY_DETECT_DATASETS),
                any(),
                any(),
                anyLong(),
                anyLong())).thenReturn(mockResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/aes/details-on-demand").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<Map<String, String>> response
                = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Map<String, String>>>() {
        });

        verify(mockAesService, times(1)).getDetailsOnDemandData(any(Datasets.class), any(), any(),
                anyLong(),
                anyLong());
        verifyNoMoreInteractions(mockAesService);
    }

    @Test
    public void shouldDownloadAllDetailsOnDemandData() throws Exception {
        AesRequest requestBody = new AesRequest();
        requestBody.setAesFilters(new AeFilters());
        requestBody.setPopulationFilters(new PopulationFilters());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/aes/download-details-on-demand").
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
                post("/resources/aes/download-selected-details-on-demand").
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
    public void shouldGetXAxisOptionsForAesCounts() throws Exception {
        AesRequest requestBody = new AesRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setAesFilters(AeFilters.empty());

        AxisOptions<AeGroupByOptions> mockResponse = new AxisOptions<>(
                Arrays.asList(new AxisOption<>(AeGroupByOptions.PT)),
                false, Arrays.asList("drug1")
        );

        when(mockAesService.getAvailableBarChartXAxis(eq(DUMMY_DETECT_DATASETS), any(AeFilters.class),
                any(PopulationFilters.class))).thenReturn(mockResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/aes/countsbarchart-xaxis").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        AxisOptions<AeGroupByOptions> response
                = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<AxisOptions<AeGroupByOptions>>() {
        });

        verify(mockAesService, times(1)).getAvailableBarChartXAxis(any(Datasets.class), any(AeFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(mockAesService);
    }

    @Test
    public void shouldGetAsscoiatedAesNumbersCerebrovascular() throws Exception {

        AesAssociatedAesNumbersRequest requestBody = new AesAssociatedAesNumbersRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setEventIds(newArrayList("12"));
        requestBody.setFromPlot("cerebrovascular");
        requestBody.setPopulationFilters(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.empty());

        List<String> mockCerebrovascularResponse = newArrayList("1");

        when(mockCerebrovascularService.getAssociatedAeNumbersFromEventIds(eq(DUMMY_DETECT_DATASETS),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class), any(List.class)))
                .thenReturn(mockCerebrovascularResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/aes/associatedaesnumbers").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<String>>() {
        });

        assertThat(response).isEqualTo(mockCerebrovascularResponse);

        verify(mockCerebrovascularService, times(1))
                .getAssociatedAeNumbersFromEventIds(eq(DUMMY_DETECT_DATASETS),
                        any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class), any(List.class));
        verifyNoMoreInteractions(mockCerebrovascularService);
        verifyNoMoreInteractions(mockCvotEndpointService);
        verifyNoMoreInteractions(mockCIEventService);
    }

    @Test
    public void shouldGetAsscoiatedAesNumbersCvot() throws Exception {

        AesAssociatedAesNumbersRequest requestBody = new AesAssociatedAesNumbersRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setEventIds(newArrayList("12"));
        requestBody.setFromPlot("cvot");
        requestBody.setPopulationFilters(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.empty());

        List<String> mockCvotResponse = newArrayList("2");

        when(mockCvotEndpointService.getAssociatedAeNumbersFromEventIds(eq(DUMMY_DETECT_DATASETS),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class), any(List.class)))
                .thenReturn(mockCvotResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/aes/associatedaesnumbers").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<String>>() {
        });

        assertThat(response).isEqualTo(mockCvotResponse);

        verify(mockCvotEndpointService, times(1))
                .getAssociatedAeNumbersFromEventIds(eq(DUMMY_DETECT_DATASETS),
                        any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class), any(List.class));
        verifyNoMoreInteractions(mockCerebrovascularService);
        verifyNoMoreInteractions(mockCvotEndpointService);
        verifyNoMoreInteractions(mockCIEventService);
    }

    @Test
    public void shouldGetAsscoiatedAesNumbersCiEvents() throws Exception {

        AesAssociatedAesNumbersRequest requestBody = new AesAssociatedAesNumbersRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setEventIds(newArrayList("12"));
        requestBody.setFromPlot("cievents");
        requestBody.setPopulationFilters(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.empty());

        List<String> mockCieventsResponse = newArrayList("3");

        when(mockCIEventService.getAssociatedAeNumbersFromEventIds(eq(DUMMY_DETECT_DATASETS),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class), any(List.class)))
                .thenReturn(mockCieventsResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/aes/associatedaesnumbers").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<String> response = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<String>>() {
        });

        assertThat(response).isEqualTo(mockCieventsResponse);

        verify(mockCIEventService, times(1))
                .getAssociatedAeNumbersFromEventIds(eq(DUMMY_DETECT_DATASETS),
                        any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class), any(List.class));
        verifyNoMoreInteractions(mockCerebrovascularService);
        verifyNoMoreInteractions(mockCvotEndpointService);
        verifyNoMoreInteractions(mockCIEventService);
    }
}
