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
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rest.model.request.renal.RenalRequest;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.google.common.collect.Sets.newHashSet;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = RenalDetailsOnDemandResource.class, secure = false)
public class RenalDetailsOnDemandResourceTest {

    private static final String RESOURCE_URL = "/resources/renal/details-on-demand";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RenalService renalService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldGetDetailsOnDemandData() throws Exception {

        DetailsOnDemandRequest requestBody = new DetailsOnDemandRequest();
        requestBody.setEventIds(newHashSet("123", "456"));
        requestBody.setSortAttrs(Collections.singletonList(new SortAttrs("subjectId", false)));
        requestBody.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        Map<String, String> dodMap = ImmutableMap.of("field1", "value1", "field2", "value2");
        List<Map<String, String>> detailsOnDemandData = Collections.singletonList(dodMap);

        when(renalService.getDetailsOnDemandData(
                eq(DUMMY_ACUITY_DATASETS),
                any(),
                any(),
                anyInt(),
                anyInt())).thenReturn(detailsOnDemandData);


        this.mvc.perform(post(RESOURCE_URL + "/data")
                .content(mapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dodData[0]", hasKey("field1")))
                .andExpect(jsonPath("$.dodData[0]", hasValue("value1")))
                .andExpect(jsonPath("$.dodData[0]", hasKey("field2")))
                .andExpect(jsonPath("$.dodData[0]", hasValue("value2")));

        verify(renalService, times(1))
                .getDetailsOnDemandData(eq(DUMMY_ACUITY_DATASETS), any(),
                        any(),
                        anyInt(),
                        anyInt());
        verifyNoMoreInteractions(renalService);
    }

    @Test
    public void shouldDownloadAllDetailsOnDemandData() throws Exception {
        RenalRequest requestBody = new RenalRequest(PopulationFilters.empty(), RenalFilters.empty());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        this.mvc.perform(post(RESOURCE_URL + "/all-csv")
                .content(mapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDownloadSelectedDetailsOnDemandData() throws Exception {
        RenalRequest requestBody = new RenalRequest(PopulationFilters.empty(), RenalFilters.empty());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        this.mvc.perform(post(RESOURCE_URL + "/selected-csv")
                .content(mapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
