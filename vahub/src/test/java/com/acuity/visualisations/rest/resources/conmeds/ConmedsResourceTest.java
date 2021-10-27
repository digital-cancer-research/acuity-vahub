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

package com.acuity.visualisations.rest.resources.conmeds;

import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.event.ConmedsService;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.conmeds.ConmedsRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ConmedsResource.class, secure = false)
public class ConmedsResourceTest {
    private static final String BASE_URL = "/resources/conmeds";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ConmedsService conmedsService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldGetAvailableFilters() throws Exception {
        ConmedFilters filters = new ConmedFilters();
        filters.setAtcCode(new SetFilter<>(ImmutableList.of("atc_code")));

        ConmedsRequest request = new ConmedsRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setConmedsFilters(filters);
        request.setPopulationFilters(PopulationFilters.empty());

        when(conmedsService.getAvailableFilters(any(Datasets.class),
                any(ConmedFilters.class), any(PopulationFilters.class)))
                .thenReturn(filters);

        this.mvc.perform(post(BASE_URL + "/filters")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.atcCode.values[0]", equalTo("atc_code")))
                .andReturn();

        verify(conmedsService, times(1))
                .getAvailableFilters(eq(DUMMY_DETECT_DATASETS), any(ConmedFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(conmedsService);
    }

    @Test
    public void shouldGetSingleSubjectData() throws Exception {
        SingleSubjectRequest<ConmedFilters> requestBody = new SingleSubjectRequest<>();
        requestBody.setEventFilters(ConmedFilters.empty());
        requestBody.setSubjectId("subject_id");
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        Map<String, String> dodMap = new HashMap<>();
        dodMap.put("column_1", "value_1");
        dodMap.put("column_2", "value_2");

        DetailsOnDemandResponse responseToReturn = new DetailsOnDemandResponse(Collections.singletonList(dodMap));

        when(conmedsService.getDetailsOnDemandData(any(Datasets.class), anyString(), any(ConmedFilters.class)))
                .thenReturn(responseToReturn.getDodData());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(BASE_URL + "/single-subject").
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

        verify(conmedsService, times(1))
                .getDetailsOnDemandData(eq(DUMMY_DETECT_DATASETS), anyString(), any(ConmedFilters.class));
        verifyNoMoreInteractions(conmedsService);
    }
}
