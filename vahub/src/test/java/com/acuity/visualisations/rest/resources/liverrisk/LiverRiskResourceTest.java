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

package com.acuity.visualisations.rest.resources.liverrisk;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acuity.visualisations.rawdatamodel.filters.LiverRiskFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.event.LiverRiskService;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.liverrisk.LiverRiskRequest;
import com.acuity.visualisations.rest.model.response.DetailsOnDemandResponse;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = LiverRiskResource.class, secure = false)
public class LiverRiskResourceTest {
    private static final String BASE_URL = "/resources/liver-risk";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LiverRiskService liverRiskService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldGetAvailableFilters() throws Exception {
        LiverRiskFilters filters = new LiverRiskFilters();
        filters.setComment(new SetFilter<>(Collections.singleton("comment_1")));

        LiverRiskRequest request = new LiverRiskRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setLiverRiskFilters(filters);
        request.setPopulationFilters(PopulationFilters.empty());

        when(liverRiskService.getAvailableFilters(any(Datasets.class),
                any(LiverRiskFilters.class), any(PopulationFilters.class)))
                .thenReturn(filters);

        this.mvc.perform(post(BASE_URL + "/filters")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.comment.values[0]", equalTo("comment_1")))
                .andReturn();

        verify(liverRiskService, times(1))
                .getAvailableFilters(eq(DUMMY_DETECT_DATASETS), any(LiverRiskFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(liverRiskService);
    }

    @Test
    public void shouldGetSingleSubjectData() throws Exception {
        SingleSubjectRequest<LiverRiskFilters> requestBody = new SingleSubjectRequest();
        requestBody.setEventFilters(LiverRiskFilters.empty());
        requestBody.setSubjectId("subject_id");
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        Map<String, String> dodMap = new HashMap<>();
        dodMap.put("column_1", "value_1");
        dodMap.put("column_2", "value_2");

        DetailsOnDemandResponse responseToReturn = new DetailsOnDemandResponse(Arrays.asList(dodMap));

        when(liverRiskService.getDetailsOnDemandData(any(Datasets.class), anyString(), any(LiverRiskFilters.class)))
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

        verify(liverRiskService, times(1))
                .getDetailsOnDemandData(eq(DUMMY_DETECT_DATASETS), anyString(), any(LiverRiskFilters.class));
        verifyNoMoreInteractions(liverRiskService);
    }
}
