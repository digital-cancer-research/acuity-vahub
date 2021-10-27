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
import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.timeline.ExacerbationsTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.exacerbations.ExacerbationSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.exacerbations.SubjectExacerbationSummary;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.TimelineExacerbationsRequest;
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

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SuppressWarnings("unchecked")
public class TimelineExacerbationsResourceTest {

    @Mock
    private ExacerbationsTimelineService mockTimelineExacerbationsService;

    @InjectMocks
    private TimelineExacerbationsResource timelineExacerbationsResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(timelineExacerbationsResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetConmedsSummaries() throws Exception {


        DateDayHour ddh = new DateDayHour();

        ExacerbationSummaryEvent exacerbationSummaryEvent = ExacerbationSummaryEvent.builder()
                .numberOfDoseReceived(4)
                .ongoing(true)
                .imputedEndDate(true)
                .start(ddh)
                .end(ddh)
                .build();

        SubjectExacerbationSummary ses = SubjectExacerbationSummary.builder()
                .subjectId("subject1")
                .subject("subject1")
                .events(newArrayList(exacerbationSummaryEvent)).build();


        List<SubjectExacerbationSummary> response = newArrayList(ses);

        TimelineExacerbationsRequest timelineExacerbationsRequest =
                new TimelineExacerbationsRequest();
        timelineExacerbationsRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineExacerbationsRequest.setPopulationFilters(new PopulationFilters());
        timelineExacerbationsRequest.setExacerbationFilters(new ExacerbationFilters());
        timelineExacerbationsRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockTimelineExacerbationsService.getExacerbationsSummary(any(Datasets.class), any(DayZeroType.class),
                any(String.class), any(PopulationFilters.class), any(ExacerbationFilters.class))).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/exacerbations/summaries").
                content(mapper.writeValueAsString(timelineExacerbationsRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(mockTimelineExacerbationsService, times(1)).getExacerbationsSummary(eq(DUMMY_DETECT_DATASETS),
                eq(DayZeroType.DAYS_SINCE_FIRST_DOSE), eq(null), any(PopulationFilters.class), any(ExacerbationFilters.class));
        verifyNoMoreInteractions(mockTimelineExacerbationsService);
    }
}
