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
import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.timeline.LungFunctionTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.LungFunctionCodes;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.LungFunctionDetailsEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.LungFunctionSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.SubjectLungFunctionDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.SubjectLungFunctionSummary;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionTimelineRequest;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings("unchecked")
public class TimelineLungFunctionResourceTest {

    @Mock
    private LungFunctionTimelineService mockTimelineLungFunctionService;

    @InjectMocks
    private TimelineLungFunctionResource timelineLungFunctionResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(timelineLungFunctionResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetLungFunctionSummaries() throws Exception {
        DateDayHour ddh1 = new DateDayHour();
        ddh1.setDate(new Date());
        ddh1.setDayHour(12.3);
        ddh1.setStudyDayHourAsString("std_d_h");
        ddh1.setDoseDayHour(2.0);

        DateDayHour ddh2 = new DateDayHour();
        ddh1.setDate(new Date());
        ddh1.setDayHour(1.3);
        ddh2.setStudyDayHourAsString("std_d_h");
        ddh2.setDoseDayHour(2.0);

        LungFunctionSummaryEvent lungFunctionSummaryEvent = LungFunctionSummaryEvent.builder()
                .start(ddh1)
                .visitNumber(12.).build();

        LungFunctionSummaryEvent lungFunctionSummaryEvent2 = LungFunctionSummaryEvent.builder()
                .start(ddh2)
                .visitNumber(1.).build();

        SubjectLungFunctionSummary sls = SubjectLungFunctionSummary.builder()
                .subjectId("subject1")
                .subject("subject1")
                .events(newArrayList(lungFunctionSummaryEvent, lungFunctionSummaryEvent2)).build();


        List<SubjectLungFunctionSummary> response = newArrayList(sls);

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList("subject1")));

        LungFunctionTimelineRequest timelineRequest = new LungFunctionTimelineRequest();
        timelineRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineRequest.setPopulationFilters(populationFilters);
        timelineRequest.setLungFunctionFilters(new LungFunctionFilters());
        timelineRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockTimelineLungFunctionService.getLungFunctionSummaries(any(Datasets.class),
                any(LungFunctionFilters.class),
                any(PopulationFilters.class),
                any(DayZeroType.class),
                anyString())).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/lung-function/summaries").
                content(mapper.writeValueAsString(timelineRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(mockTimelineLungFunctionService, times(1)).getLungFunctionSummaries(
                eq(DUMMY_DETECT_DATASETS),
                any(LungFunctionFilters.class),
                any(PopulationFilters.class),
                eq(DayZeroType.DAYS_SINCE_FIRST_DOSE),
                anyString());
        verifyNoMoreInteractions(mockTimelineLungFunctionService);
    }

    @Test
    public void shouldGetLungFunctionDetails() throws Exception {

        DateDayHour ddh = new DateDayHour();
        ddh.setDate(new Date());
        ddh.setDayHour(124.4);
        ddh.setStudyDayHourAsString("std_d_h");
        ddh.setDoseDayHour(2.0);

        LungFunctionDetailsEvent lungFunctionDetailsEvent = LungFunctionDetailsEvent.builder()
                .baselineValue(10.2)
                .start(ddh)
                .unitChangeFromBaseline("mg")
                .unitPercentChangeFromBaseline("%")
                .unitRaw("mg")
                .valueChangeFromBaseline(12.4)
                .valuePercentChangeFromBaseline(50.9)
                .valueRaw(12.3)
                .visitNumber(4.).build();

        LungFunctionCodes lfCodes = LungFunctionCodes.builder()
                .code("labcode1")
                .baseline(ddh)
                .events(Collections.singletonList(lungFunctionDetailsEvent)).build();

        SubjectLungFunctionDetail sld = SubjectLungFunctionDetail.builder()
                .codes(Collections.singletonList(lfCodes))
                .sex("f")
                .subject("subject1")
                .subjectId("subject1").build();

        List<SubjectLungFunctionDetail> response = newArrayList(sld);

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList("subject1")));

        LungFunctionTimelineRequest timelineRequest = new LungFunctionTimelineRequest();
        timelineRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineRequest.setPopulationFilters(populationFilters);
        timelineRequest.setLungFunctionFilters(new LungFunctionFilters());
        timelineRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockTimelineLungFunctionService.getLungFunctionDetails(any(Datasets.class),
                any(LungFunctionFilters.class),
                any(PopulationFilters.class),
                any(DayZeroType.class),
                anyString())).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/lung-function/details").
                content(mapper.writeValueAsString(timelineRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(mockTimelineLungFunctionService, times(1)).getLungFunctionDetails(
                eq(DUMMY_DETECT_DATASETS),
                any(LungFunctionFilters.class),
                any(PopulationFilters.class),
                any(DayZeroType.class),
                anyString());
        verifyNoMoreInteractions(mockTimelineLungFunctionService);
    }
}
