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
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.visualisations.rawdatamodel.service.timeline.VitalsTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.vitals.SubjectVitalsDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.vitals.SubjectVitalsSummary;
import com.acuity.visualisations.rawdatamodel.vo.timeline.vitals.VitalsSummaryEvent;
import com.acuity.visualisations.rest.model.request.vitals.VitalsTimelineRequest;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings("unchecked")
public class TimelineVitalsResourceTest {

    @Mock
    private VitalsTimelineService mockVitalsService;

    @InjectMocks
    private TimelineVitalsResource vitalsResource;

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
    public void shouldGetVitalsSummaries() throws Exception {

        SubjectVitalsSummary scs = new SubjectVitalsSummary();
        scs.setSubjectId("subject1");
        scs.setSubject("subject1");

        VitalsSummaryEvent summaryEvent = new VitalsSummaryEvent();

        scs.setEvents(newArrayList(summaryEvent));

        List<SubjectVitalsSummary> response = newArrayList(scs);

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList("subject1")));

        VitalsTimelineRequest timelineVitalsRequest = new VitalsTimelineRequest();
        timelineVitalsRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineVitalsRequest.setPopulationFilters(populationFilters);
        timelineVitalsRequest.setVitalsFilters(new VitalFilters());
        timelineVitalsRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockVitalsService.getVitalsSummaries(any(Datasets.class),
                any(VitalFilters.class), any(PopulationFilters.class),
                any(DayZeroType.class), anyString()))
                .thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/vitals/summaries").
                content(mapper.writeValueAsString(timelineVitalsRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        Mockito.verify(mockVitalsService, times(1)).getVitalsSummaries(eq(DUMMY_DETECT_DATASETS),
                any(VitalFilters.class), any(PopulationFilters.class),
                eq(DayZeroType.DAYS_SINCE_FIRST_DOSE), anyString());
        verifyNoMoreInteractions(mockVitalsService);
    }

    @Test
    public void shouldGetVitalsDetail() throws Exception {

        SubjectVitalsDetail scs = new SubjectVitalsDetail();
        scs.setSubjectId("subject1");
        scs.setSubject("subject1");

        List<SubjectVitalsDetail> response = newArrayList(scs);

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList("subject1")));

        VitalsTimelineRequest timelineVitalsRequest = new VitalsTimelineRequest();
        timelineVitalsRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineVitalsRequest.setPopulationFilters(populationFilters);
        timelineVitalsRequest.setVitalsFilters(new VitalFilters());
        timelineVitalsRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockVitalsService.getVitalsDetails(any(Datasets.class),
                any(VitalFilters.class), any(PopulationFilters.class),
                any(DayZeroType.class), anyString()))
                .thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/vitals/details").
                content(mapper.writeValueAsString(timelineVitalsRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        Mockito.verify(mockVitalsService, times(1)).getVitalsDetails(eq(DUMMY_DETECT_DATASETS),
                any(VitalFilters.class), any(PopulationFilters.class),
                eq(DayZeroType.DAYS_SINCE_FIRST_DOSE), anyString());
        verifyNoMoreInteractions(mockVitalsService);
    }
}
