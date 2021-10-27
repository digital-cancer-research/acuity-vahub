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
import com.acuity.visualisations.rawdatamodel.service.timeline.StatusSummaryTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.statussummary.StudyPhase;
import com.acuity.visualisations.rawdatamodel.vo.timeline.statussummary.SubjectStatusSummary;
import com.acuity.visualisations.rest.model.request.statussummary.StatusSummaryTimelineRequest;
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

import java.util.Date;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TimelineStatusResourceTest {

    @Mock
    private StatusSummaryTimelineService mockTimelineStatusService;

    @InjectMocks
    private TimelineStatusResource timelineStatusResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(timelineStatusResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @SuppressWarnings("deprecation")
    @Test
    public void shouldGetStatusSummaries() throws Exception {

        SubjectStatusSummary sss = new SubjectStatusSummary();
        sss.setSubjectId("subject1");

        DateDayHour ddh1 = new DateDayHour(new Date(100, 1, 1), 10.2);
        ddh1.setStudyDayHourAsString("std_d_h");
        ddh1.setDoseDayHour(2.0);

        DateDayHour ddh2 = new DateDayHour(new Date(100, 1, 1), 12.3);
        ddh2.setStudyDayHourAsString("std_d_h");
        ddh2.setDoseDayHour(2.0);

        DateDayHour ddh3 = new DateDayHour(new Date(), 12.3);
        ddh3.setStudyDayHourAsString("std_d_h");
        ddh3.setDoseDayHour(2.0);

        DateDayHour ddh4 = new DateDayHour(new Date(), 12.);
        ddh4.setStudyDayHourAsString("std_d_h");
        ddh4.setDoseDayHour(2.0);

        StudyPhase sp = new StudyPhase(StudyPhase.PhaseType.ON_STUDY_DRUG);
        sp.setStart(ddh1);
        sp.setEnd(ddh2);
        sss.setFirstTreatment(ddh3);
        sss.setFirstVisit(ddh3);
        sss.setLastTreatment(ddh3);
        sss.setLastVisit(ddh4);
        sss.setDeath(ddh3);
        sss.setDrugs(newArrayList("Drug1"));
        sss.setPhases(newArrayList(sp));

        List<SubjectStatusSummary> response = newArrayList(sss);

        StatusSummaryTimelineRequest statusSummaryTimelineRequest = new StatusSummaryTimelineRequest();
        statusSummaryTimelineRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        statusSummaryTimelineRequest.setPopulationFilters(new PopulationFilters());
        statusSummaryTimelineRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockTimelineStatusService.getStatusSummaries(any(Datasets.class), any(PopulationFilters.class),
                any(DayZeroType.class), anyString())).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/status/summaries").
                content(mapper.writeValueAsString(statusSummaryTimelineRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(mockTimelineStatusService, times(1)).getStatusSummaries(eq(DUMMY_DETECT_DATASETS),
                any(PopulationFilters.class), eq(DayZeroType.DAYS_SINCE_FIRST_DOSE), anyString());
        verifyNoMoreInteractions(mockTimelineStatusService);
    }
}
