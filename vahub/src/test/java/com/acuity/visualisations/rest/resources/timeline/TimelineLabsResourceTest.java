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
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.timeline.LabTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.Categories;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.Labcodes;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.LabsDetailsEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.LabsSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.SubjectLabsCategories;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.SubjectLabsDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.SubjectLabsSummary;
import com.acuity.visualisations.rest.model.request.labs.LabsTimelineRequest;
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

import java.util.List;
import java.util.Set;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings("unchecked")
public class TimelineLabsResourceTest {

    @Mock
    private LabTimelineService mockTimelineLabsService;

    @InjectMocks
    private TimelineLabsResource timelineLabsResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(timelineLabsResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetLabsSummaries() throws Exception {

        SubjectLabsSummary sls = new SubjectLabsSummary();
        sls.setSubjectId("subject1");
        sls.setSubject("subject1");

        DateDayHour ddh = new DateDayHour();
        ddh.setStudyDayHourAsString("std_d_h");
        ddh.setDoseDayHour(2.0);

        LabsSummaryEvent labsSummaryEvent = new LabsSummaryEvent();
        labsSummaryEvent.setNumBelowReferenceRange(1);
        labsSummaryEvent.setNumAboveReferenceRange(3);
        labsSummaryEvent.setStart(ddh);
        labsSummaryEvent.setVisitNumber(12.);

        LabsSummaryEvent labsSummaryEvent2 = new LabsSummaryEvent();
        labsSummaryEvent.setNumBelowReferenceRange(3);
        labsSummaryEvent.setNumAboveReferenceRange(1);
        labsSummaryEvent2.setStart(ddh);
        labsSummaryEvent2.setVisitNumber(1.);

        sls.setEvents(newArrayList(labsSummaryEvent, labsSummaryEvent2));

        List<SubjectLabsSummary> response = newArrayList(sls);

        Set<String> subjectIds = newHashSet("subject1");

        LabsTimelineRequest timelineLabsRequest = new LabsTimelineRequest();
        timelineLabsRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineLabsRequest.setPopulationFilters(new PopulationFilters());
        timelineLabsRequest.setLabsFilters(new LabFilters());
        timelineLabsRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockTimelineLabsService.getTimelineSummaries(any(Datasets.class),
                any(LabFilters.class), any(PopulationFilters.class),
                any(DayZeroType.class), anyString()
        )).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/labs/summaries").
                content(mapper.writeValueAsString(timelineLabsRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(mockTimelineLabsService, times(1)).getTimelineSummaries(
                eq(DUMMY_DETECT_DATASETS),
                any(LabFilters.class),
                any(PopulationFilters.class),
                eq(DayZeroType.DAYS_SINCE_FIRST_DOSE), anyString()
        );
        verifyNoMoreInteractions(mockTimelineLabsService);
    }

    @Test
    public void shouldGetLabsSummaryCategories() throws Exception {

        SubjectLabsCategories slc = new SubjectLabsCategories();
        slc.setSubjectId("subject1");
        slc.setSubject("subject1");

        DateDayHour ddh1 = new DateDayHour();
        ddh1.setStudyDayHourAsString("std_d_h");
        ddh1.setDoseDayHour(2.0);

        DateDayHour ddh2 = new DateDayHour();
        ddh2.setStudyDayHourAsString("std_d_h");
        ddh2.setDoseDayHour(2.0);

        LabsSummaryEvent labsSummaryEvent = new LabsSummaryEvent();
        labsSummaryEvent.setNumBelowReferenceRange(1);
        labsSummaryEvent.setNumAboveReferenceRange(3);
        labsSummaryEvent.setStart(ddh1);
        labsSummaryEvent.setVisitNumber(12.);

        LabsSummaryEvent labsSummaryEvent2 = new LabsSummaryEvent();
        labsSummaryEvent.setNumBelowReferenceRange(3);
        labsSummaryEvent.setNumAboveReferenceRange(1);
        labsSummaryEvent2.setStart(ddh2);
        labsSummaryEvent2.setVisitNumber(1.);

        LabsSummaryEvent labsSummaryEvent3 = new LabsSummaryEvent();
        labsSummaryEvent.setNumBelowReferenceRange(13);
        labsSummaryEvent.setNumAboveReferenceRange(21);
        labsSummaryEvent3.setStart(ddh2);
        labsSummaryEvent3.setVisitNumber(6.);

        Categories categories1 = new Categories();
        categories1.setCategory("labcode1");
        categories1.setEvents(newArrayList(labsSummaryEvent, labsSummaryEvent2));

        Categories categories2 = new Categories();
        categories2.setCategory("labcode2");
        categories2.setEvents(newArrayList(labsSummaryEvent3));

        slc.setLabcodes(newArrayList(categories1, categories2));

        List<SubjectLabsCategories> response = newArrayList(slc);

        LabsTimelineRequest timelineLabsRequest = new LabsTimelineRequest();
        timelineLabsRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineLabsRequest.setPopulationFilters(new PopulationFilters());
        timelineLabsRequest.setLabsFilters(new LabFilters());
        timelineLabsRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockTimelineLabsService.getTimelineCategories(any(Datasets.class),
                any(LabFilters.class), any(PopulationFilters.class),
                any(DayZeroType.class), anyString())).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/labs/categories").
                content(mapper.writeValueAsString(timelineLabsRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(mockTimelineLabsService, times(1)).getTimelineCategories(
                eq(DUMMY_DETECT_DATASETS),
                any(LabFilters.class), any(PopulationFilters.class),
                eq(DayZeroType.DAYS_SINCE_FIRST_DOSE), anyString()
        );
        verifyNoMoreInteractions(mockTimelineLabsService);
    }

    @Test
    public void shouldGetLabsDetails() throws Exception {

        SubjectLabsDetail sld = new SubjectLabsDetail();
        sld.setSubjectId("subject1");
        sld.setSubject("subject1");

        Labcodes labCodes = new Labcodes();
        labCodes.setLabcode("labcode1");

        DateDayHour ddh = new DateDayHour();
        ddh.setStudyDayHourAsString("std_d_h");
        ddh.setDoseDayHour(2.0);

        LabsDetailsEvent labsDetailsEvent = new LabsDetailsEvent();
        labsDetailsEvent.setBaselineValue(10.2);
        labsDetailsEvent.setLabcode("labcode1");
        labsDetailsEvent.setNumBelowReferenceRange(2);
        labsDetailsEvent.setNumAboveReferenceRange(4);
        labsDetailsEvent.setStart(ddh);
        labsDetailsEvent.setUnitChangeFromBaseline("mg");
        labsDetailsEvent.setUnitPercentChangeFromBaseline("%");
        labsDetailsEvent.setUnitRaw("mg");
        labsDetailsEvent.setValueChangeFromBaseline(12.4);
        labsDetailsEvent.setValuePercentChangeFromBaseline(50.9);
        labsDetailsEvent.setValueRaw(12.3);
        labsDetailsEvent.setVisitNumber(4.);

        labCodes.setEvents(newArrayList(labsDetailsEvent));

        sld.setLabcodes(newArrayList(labCodes));

        List<SubjectLabsDetail> response = newArrayList(sld);

        LabsTimelineRequest timelineLabsRequest = new LabsTimelineRequest();
        timelineLabsRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineLabsRequest.setPopulationFilters(new PopulationFilters());
        timelineLabsRequest.setLabsFilters(new LabFilters());
        timelineLabsRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockTimelineLabsService.getTimelineDetails(any(Datasets.class),
                any(LabFilters.class), any(PopulationFilters.class),
                any(DayZeroType.class), anyString()
        )).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/labs/details").
                content(mapper.writeValueAsString(timelineLabsRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(mockTimelineLabsService, times(1)).getTimelineDetails(
                eq(DUMMY_DETECT_DATASETS),
                any(LabFilters.class),
                any(PopulationFilters.class),
                eq(DayZeroType.DAYS_SINCE_FIRST_DOSE), anyString()
        );
        verifyNoMoreInteractions(mockTimelineLabsService);
    }
}
