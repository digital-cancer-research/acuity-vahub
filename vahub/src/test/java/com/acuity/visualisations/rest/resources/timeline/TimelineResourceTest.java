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

package com.acuity.visualisations.rest.resources.timeline;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.axes.TAxes;
import com.acuity.visualisations.rawdatamodel.filters.*;
import com.acuity.visualisations.rawdatamodel.service.timeline.TimelineService;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import com.acuity.visualisations.rest.model.request.timeline.TimelineSubjectRequest;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.DatasetsRequest;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class TimelineResourceTest {
    private static final String BASE_URL = "/resources/timeline";

    @Mock
    private TimelineService mockTimelineService;

    @InjectMocks
    private TimelineResource timelineResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(timelineResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetAvailableOptions() throws Exception {
        DatasetsRequest request = new DatasetsRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockTimelineService.getAvailableOptions(any(Datasets.class)))
                .thenReturn(Lists.emptyList());

        this.mvc.perform(post(BASE_URL + "/available-options")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void shouldGetAvailableTracks() throws Exception {
        DatasetsRequest request = new DatasetsRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockTimelineService.getAvailableTracks(any(Datasets.class))).thenReturn(Lists.emptyList());

        this.mvc.perform(post(BASE_URL + "/available-tracks")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void shouldGetSubjectIds() throws Exception {
        ArrayList<String> response = newArrayList("subject1", "subject2", "subject3");

        TimelineSubjectRequest request = new TimelineSubjectRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setVisibleTracks(newArrayList(TimelineTrack.STATUS_SUMMARY, TimelineTrack.AES));
        request.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        request.setAesFilters(new AeFilters());
        request.setConmedsFilters(new ConmedFilters());
        request.setDoseFilters(new DrugDoseFilters());
        request.setCardiacFilters(new CardiacFilters());
        request.setLabsFilters(new LabFilters());
        request.setLungFunctionFilters(new LungFunctionFilters());
        request.setExacerbationsFilters(new ExacerbationFilters());
        request.setVitalsFilters(new VitalFilters());
        request.setPatientDataFilters(new PatientDataFilters());

        when(mockTimelineService.getSubjectsSortedByStudyDuration(
                any(Datasets.class),
                any(PopulationFilters.class),
                anyListOf(TimelineTrack.class),
                any(DayZeroType.class),
                anyString(),
                any(AeFilters.class),
                any(ConmedFilters.class),
                any(DrugDoseFilters.class),
                any(CardiacFilters.class),
                any(LabFilters.class),
                any(LungFunctionFilters.class),
                any(ExacerbationFilters.class),
                any(VitalFilters.class),
                any(PatientDataFilters.class))).thenReturn(response);

        this.mvc.perform(post(BASE_URL + "/subjects")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(3)));
    }
}
