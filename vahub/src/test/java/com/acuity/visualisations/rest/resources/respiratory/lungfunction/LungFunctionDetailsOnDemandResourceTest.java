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

package com.acuity.visualisations.rest.resources.respiratory.lungfunction;

import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.LungFunctionService;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionRequest;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = LungFunctionDetailsOnDemandResource.class, secure = false)
public class LungFunctionDetailsOnDemandResourceTest {

    private static final String BASE_URL = "/resources/respiratory/lung-function/details-on-demand";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LungFunctionService lungFunctionService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldDetailsOnDemandData() throws Exception {

        DetailsOnDemandRequest request = new DetailsOnDemandRequest();
        request.setEventIds(Collections.singleton("1"));
        request.setSortAttrs(ImmutableList.of(new SortAttrs("subjectId", true)));
        request.setStart(0);
        request.setEnd(10);
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        Map<String, Object> dodMap = new HashMap<>();
        dodMap.put("column_1", "value_1");
        dodMap.put("column_2", "value_2");

        List<Map<String, Object>> detailsOnDemandData = Collections.singletonList(dodMap);

        when(lungFunctionService.getDetailsOnDemandData(any(Datasets.class), anySet(), anyList(), anyInt(), anyInt()))
                .thenReturn(detailsOnDemandData);

        this.mvc.perform(post(BASE_URL + "/data")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dodData[0]", hasKey("column_2")))
                .andExpect(jsonPath("$.dodData[0]", hasValue("value_2")))
                .andExpect(jsonPath("$.dodData[0]", hasKey("column_1")))
                .andExpect(jsonPath("$.dodData[0]", hasValue("value_1")));
    }


    @Test
    public void shouldDownloadAllDetailsOnDemandData() throws Exception {
        LungFunctionRequest request = new LungFunctionRequest();
        request.setLungFunctionFilters(LungFunctionFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        this.mvc.perform(post(BASE_URL + "/all-csv")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldDownloadSelectedDetailsOnDemandData() throws Exception {
        LungFunctionRequest request = new LungFunctionRequest();
        request.setLungFunctionFilters(LungFunctionFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        this.mvc.perform(post(BASE_URL + "/selected-csv")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}
