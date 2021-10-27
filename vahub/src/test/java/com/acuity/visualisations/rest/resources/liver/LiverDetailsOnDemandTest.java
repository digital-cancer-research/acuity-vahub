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

package com.acuity.visualisations.rest.resources.liver;

import com.acuity.visualisations.rawdatamodel.filters.LiverFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.service.event.LiverService;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.model.request.liver.LiverRequest;
import com.acuity.visualisations.rest.test.config.DisableAutowireRequiredInitializer;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.mock.mockito.MockBean;
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

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
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
public class LiverDetailsOnDemandTest {
    private static final String URL_ROOT = "/resources/liver";

    private MockMvc mvc;

    @MockBean
    private LiverService liverService;

    @InjectMocks
    private LiverDetailsOnDemandResource liverResource;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(liverResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetDetailsOnDemandData() throws Exception {

        DetailsOnDemandRequest requestBody = new DetailsOnDemandRequest();
        requestBody.setEventIds(newHashSet("123", "456"));
        requestBody.setSortAttrs(Collections.singletonList(new SortAttrs("subjectId", false)));
        requestBody.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        List<Map<String, String>> mockResponse = Collections.emptyList();

        when(liverService.getDetailsOnDemandData(
                eq(DUMMY_ACUITY_DATASETS),
                any(),
                any(),
                anyInt(),
                anyInt())).thenReturn(mockResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders
                .post(URL_ROOT + "/details-on-demand")
                .content(mapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<Map<String, String>> response = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<Map<String, String>>>() {
                });

        assertThat(response).isEqualTo(mockResponse);
        verify(liverService, times(1)).getDetailsOnDemandData(any(Datasets.class), any(), any(), anyInt(), anyInt());
        verifyNoMoreInteractions(liverService);
    }

    @Test
    public void shouldDownloadAllDetailsOnDemandData() throws Exception {
        LiverRequest requestBody = new LiverRequest();
        requestBody.setLiverFilters(new LiverFilters());
        requestBody.setPopulationFilters(new PopulationFilters());
        requestBody.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(URL_ROOT + "/download-details-on-demand").
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
        requestBody.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(URL_ROOT + "/download-selected-details-on-demand").
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
