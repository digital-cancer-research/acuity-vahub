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
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.timeline.AeTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.aes.*;
import com.acuity.visualisations.rest.model.request.aes.AesTimelineRequest;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unchecked")
public class TimelineAesResourceTest {

    @Mock
    private AeTimelineService mockAeTimelineService;

    @InjectMocks
    private TimelineAesResource timelineAesResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(timelineAesResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetAesSummaries() throws Exception {
        DateDayHour ddh = new DateDayHour(new Date(), 20.4);
        ddh.setStudyDayHourAsString("std_d_h");
        ddh.setDoseDayHour(2.0);

        AeMaxCtcEvent maxEvent = AeMaxCtcEvent.builder()
                .duration(10)
                .start(ddh)
                .end(ddh)
                .maxSeverityGradeNum(2)
                .maxSeverityGrade("MILD")
                .pts(Collections.singleton("pt1"))
                .numberOfEvents(5)
                .imputedEndDate(true)
                .ongoing(true)
                .build();

        SubjectAesSummary sas = SubjectAesSummary.builder().subjectId("subject1").events(Collections.singletonList(maxEvent)).build();
        List<SubjectAesSummary> response = Collections.singletonList(sas);

        AesTimelineRequest timelineAesRequest = new AesTimelineRequest();
        timelineAesRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineAesRequest.setPopulationFilters(new PopulationFilters());
        timelineAesRequest.setAesFilters(new AeFilters());
        timelineAesRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockAeTimelineService.getAesSummaries(any(Datasets.class),
                any(AeFilters.class), any(PopulationFilters.class), any(DayZeroType.class), anyString())).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/aes/aessummaries").
                content(mapper.writeValueAsString(timelineAesRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(mockAeTimelineService, times(1)).getAesSummaries(eq(DUMMY_DETECT_DATASETS),
                any(AeFilters.class), any(PopulationFilters.class), eq(DayZeroType.DAYS_SINCE_FIRST_DOSE), anyString());
        verifyNoMoreInteractions(mockAeTimelineService);
    }

    @Test
    public void shouldGetAesDetails() throws Exception {
        AeDetailEvent aeEvent = AeDetailEvent.builder().build();

        AeDetail ae = AeDetail.builder().hlt("htl1").pt("pt1").soc("soc1").events(Collections.singletonList(aeEvent)).build();

        SubjectAesDetail sad = SubjectAesDetail.builder().subjectId("subject1").aes(Collections.singletonList(ae)).build();

        List<SubjectAesDetail> response = Collections.singletonList(sad);

        AesTimelineRequest timelineAesRequest = new AesTimelineRequest();
        timelineAesRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineAesRequest.setPopulationFilters(PopulationFilters.empty());
        timelineAesRequest.setAesFilters(AeFilters.empty());
        timelineAesRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockAeTimelineService.getAesDetails(Matchers.any(Datasets.class), Matchers.any(AeFilters.class),
                Matchers.any(PopulationFilters.class), Matchers.any(DayZeroType.class), anyString())).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/aes/aesdetails").
                content(mapper.writeValueAsString(timelineAesRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(mockAeTimelineService, times(1)).getAesDetails(eq(DUMMY_DETECT_DATASETS),
                Matchers.any(AeFilters.class), Matchers.any(PopulationFilters.class), Matchers.any(DayZeroType.class), anyString());
        verifyNoMoreInteractions(mockAeTimelineService);
    }
}
