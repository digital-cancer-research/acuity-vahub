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
import com.acuity.visualisations.rawdatamodel.filters.CardiacFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.timeline.EcgTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.ecg.EcgSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.ecg.SubjectEcgDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.ecg.SubjectEcgSummary;
import com.acuity.visualisations.rest.model.request.cardiac.EcgTimelineRequest;
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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
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
public class TimelineEcgResourceTest {

    @Mock
    private EcgTimelineService ecgTimelineService;

    @InjectMocks
    private TimelineEcgResource timelineEcgResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;
    private static final String BASE_URL = "/resources/timeline/ecg";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(timelineEcgResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetEcgSummaries() throws Exception {
        EcgSummaryEvent ecgSummaryEvent = new EcgSummaryEvent();
        SubjectEcgSummary scs = SubjectEcgSummary.builder().subject("subject1").subjectId("subject1")
                .events(Collections.singletonList(ecgSummaryEvent)).build();

        List<SubjectEcgSummary> response = Collections.singletonList(scs);

        EcgTimelineRequest timelineEcgRequest = new EcgTimelineRequest();
        timelineEcgRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineEcgRequest.setPopulationFilters(PopulationFilters.empty());
        timelineEcgRequest.setCardiacFilters(CardiacFilters.empty());
        timelineEcgRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(ecgTimelineService.getSummaries(any(Datasets.class), any(CardiacFilters.class), any(PopulationFilters.class),
                any(DayZeroType.class), any(String.class))).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(BASE_URL + "/summaries").
                content(mapper.writeValueAsString(timelineEcgRequest)).
                contentType(MediaType.APPLICATION_JSON);

        mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(ecgTimelineService, times(1)).getSummaries(eq(DUMMY_DETECT_DATASETS),
                any(CardiacFilters.class), any(PopulationFilters.class), eq(DayZeroType.DAYS_SINCE_FIRST_DOSE), any(String.class));
        verifyNoMoreInteractions(ecgTimelineService);
    }

    @Test
    public void shouldGetEcgDetail() throws Exception {
        SubjectEcgDetail scs = new SubjectEcgDetail();
        scs.setSubjectId("subject1");
        scs.setSubject("subject1");

        List<SubjectEcgDetail> response = newArrayList(scs);

        EcgTimelineRequest timelineEcgRequest = new EcgTimelineRequest();
        timelineEcgRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineEcgRequest.setPopulationFilters(PopulationFilters.empty());
        timelineEcgRequest.setCardiacFilters(CardiacFilters.empty());
        timelineEcgRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(ecgTimelineService.getDetails(any(Datasets.class), any(CardiacFilters.class), any(PopulationFilters.class),
                any(DayZeroType.class), any(String.class))).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(BASE_URL + "/details").
                content(mapper.writeValueAsString(timelineEcgRequest)).
                contentType(MediaType.APPLICATION_JSON);

        mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(ecgTimelineService, times(1)).getDetails(eq(DUMMY_DETECT_DATASETS),
                any(CardiacFilters.class), any(PopulationFilters.class), eq(DayZeroType.DAYS_SINCE_FIRST_DOSE), any(String.class));
        verifyNoMoreInteractions(ecgTimelineService);
    }
}
