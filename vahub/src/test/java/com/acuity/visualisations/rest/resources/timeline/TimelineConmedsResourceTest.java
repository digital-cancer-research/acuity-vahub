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
import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.timeline.ConmedsTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.ConmedEventsByClass;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.ConmedEventsByDrug;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.ConmedSingleEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.ConmedSummary;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.ConmedSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.SubjectConmedByClass;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.SubjectConmedByDrug;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.SubjectConmedSummary;
import com.acuity.visualisations.rest.model.request.conmeds.ConmedsTimelineRequest;
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
public class TimelineConmedsResourceTest {

    @Mock
    private ConmedsTimelineService mockConmedsTimelineService;

    @InjectMocks
    private TimelineConmedsResource timelineConmedsResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(timelineConmedsResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static List<ConmedSummary> newSingletonListOfConmeds(String conmedName, double dose) {
        ConmedSummary conmed = new ConmedSummary();
        conmed.setConmed(conmedName);
        conmed.setDoses(Collections.singletonList(dose));
        return Collections.singletonList(conmed);
    }

    @Test
    public void shouldGetConmedsSummaries() throws Exception {

        SubjectConmedSummary scs = new SubjectConmedSummary();
        scs.setSubjectId("subject1");
        scs.setSubject("subject1");

        DateDayHour ddh = new DateDayHour(new Date(), 12.3);
        ddh.setStudyDayHourAsString("std_d_h");
        ddh.setDoseDayHour(2.0);

        ConmedSummaryEvent conmedSummaryEvent = new ConmedSummaryEvent();
        conmedSummaryEvent.setConmeds(newSingletonListOfConmeds("conmed1", 12.));
        conmedSummaryEvent.setOngoing(true);
        conmedSummaryEvent.setImputedEndDate(true);
        conmedSummaryEvent.setStart(ddh);
        conmedSummaryEvent.setEnd(ddh);

        ConmedSummaryEvent conmedSummaryEvent2 = new ConmedSummaryEvent();
        conmedSummaryEvent2.setConmeds(newSingletonListOfConmeds("conmed2", 2.));
        conmedSummaryEvent2.setOngoing(false);
        conmedSummaryEvent2.setImputedEndDate(false);
        conmedSummaryEvent2.setStart(ddh);
        conmedSummaryEvent2.setEnd(ddh);

        scs.setEvents(newArrayList(conmedSummaryEvent, conmedSummaryEvent2));

        List<SubjectConmedSummary> response = newArrayList(scs);

        ConmedsTimelineRequest timelineConmedsRequest = new ConmedsTimelineRequest();
        timelineConmedsRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineConmedsRequest.setPopulationFilters(new PopulationFilters());
        timelineConmedsRequest.setConmedsFilters(new ConmedFilters());
        timelineConmedsRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockConmedsTimelineService.getConmedsSummaries(any(Datasets.class),
                any(ConmedFilters.class), any(PopulationFilters.class), any(DayZeroType.class), anyString())
        ).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/conmeds/conmedssummaries").
                content(mapper.writeValueAsString(timelineConmedsRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(mockConmedsTimelineService, times(1)).getConmedsSummaries(eq(DUMMY_DETECT_DATASETS),
                any(ConmedFilters.class), any(PopulationFilters.class),
                eq(DayZeroType.DAYS_SINCE_FIRST_DOSE), anyString());
        verifyNoMoreInteractions(mockConmedsTimelineService);
    }

    @Test
    public void shouldGetConmedsByClass() throws Exception {

        SubjectConmedByClass scs = new SubjectConmedByClass();
        scs.setSubjectId("subject1");
        scs.setSubject("subject1");

        ConmedEventsByClass sebc = new ConmedEventsByClass();

        ConmedSummaryEvent conmedSummaryEvent = new ConmedSummaryEvent();
        conmedSummaryEvent.setConmeds(newSingletonListOfConmeds("conmed1", 12.));
        conmedSummaryEvent.setOngoing(true);
        conmedSummaryEvent.setImputedEndDate(true);
        conmedSummaryEvent.setStart(new DateDayHour(new Date(), 12.3));
        conmedSummaryEvent.setEnd(new DateDayHour(new Date(), 12.3));

        ConmedSummaryEvent conmedSummaryEvent2 = new ConmedSummaryEvent();
        conmedSummaryEvent2.setConmeds(newSingletonListOfConmeds("conmed2", 2.));
        conmedSummaryEvent2.setOngoing(false);
        conmedSummaryEvent2.setImputedEndDate(false);
        conmedSummaryEvent2.setStart(new DateDayHour(new Date(), 12.3));
        conmedSummaryEvent2.setEnd(new DateDayHour(new Date(), 12.3));

        sebc.setConmedClass("conmedClass1");
        sebc.setEvents(newArrayList(conmedSummaryEvent, conmedSummaryEvent2));

        List<SubjectConmedByClass> response = newArrayList(scs);

        ConmedsTimelineRequest timelineConmedsRequest = new ConmedsTimelineRequest();
        timelineConmedsRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineConmedsRequest.setPopulationFilters(new PopulationFilters());
        timelineConmedsRequest.setConmedsFilters(new ConmedFilters());
        timelineConmedsRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockConmedsTimelineService.getConmedsByClass(any(Datasets.class),
                any(ConmedFilters.class), any(PopulationFilters.class), any(DayZeroType.class), anyString())
        ).thenReturn(response);


        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/conmeds/conmedsbyclass").
                content(mapper.writeValueAsString(timelineConmedsRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(mockConmedsTimelineService, times(1)).getConmedsByClass(eq(DUMMY_DETECT_DATASETS),
                any(ConmedFilters.class), any(PopulationFilters.class),
                eq(DayZeroType.DAYS_SINCE_FIRST_DOSE), anyString());
        verifyNoMoreInteractions(mockConmedsTimelineService);
    }

    @Test
    public void shouldGetConmedsByDrug() throws Exception {

        SubjectConmedByDrug scs = new SubjectConmedByDrug();
        scs.setSubjectId("subject1");
        scs.setSubject("subject1");

        ConmedEventsByDrug sebd = new ConmedEventsByDrug();

        ConmedSingleEvent conmedSingleEvent = new ConmedSingleEvent();
        conmedSingleEvent.setConmed("conmed1");
        conmedSingleEvent.setDose(3.);
        conmedSingleEvent.setOngoing(true);
        conmedSingleEvent.setImputedEndDate(true);
        conmedSingleEvent.setStart(new DateDayHour(new Date(), 12.3));
        conmedSingleEvent.setEnd(new DateDayHour(new Date(), 12.3));

        sebd.setConmedMedication("ConmedMedication1");
        sebd.setEvents(newArrayList(conmedSingleEvent));

        List<SubjectConmedByDrug> response = newArrayList(scs);

        ConmedsTimelineRequest timelineConmedsRequest = new ConmedsTimelineRequest();
        timelineConmedsRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineConmedsRequest.setPopulationFilters(new PopulationFilters());
        timelineConmedsRequest.setConmedsFilters(new ConmedFilters());
        timelineConmedsRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockConmedsTimelineService.getConmedsByDrug(any(Datasets.class),
                any(ConmedFilters.class), any(PopulationFilters.class), any(DayZeroType.class), anyString())
        ).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/conmeds/conmedsbydrug").
                content(mapper.writeValueAsString(timelineConmedsRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();
        
        verify(mockConmedsTimelineService, times(1)).getConmedsByDrug(eq(DUMMY_DETECT_DATASETS),
                any(ConmedFilters.class), any(PopulationFilters.class),
                eq(DayZeroType.DAYS_SINCE_FIRST_DOSE), anyString());
        verifyNoMoreInteractions(mockConmedsTimelineService);
    }
}
