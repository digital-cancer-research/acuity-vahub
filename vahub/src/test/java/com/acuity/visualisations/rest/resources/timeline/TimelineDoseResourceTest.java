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
import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.timeline.DrugDoseTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.DosingSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.MaxDoseType;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.PercentChange;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.SubjectDosingSummary;
import com.acuity.visualisations.rest.model.request.dose.TimelineDosingRequest;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = TimelineDoseResource.class, secure = false)
@SuppressWarnings("unchecked")
public class TimelineDoseResourceTest {
    private ObjectMapper mapper;

    @MockBean
    private DrugDoseTimelineService mockTimelineDoseService;

    @Autowired
    private TimelineDoseResource timelineDoseResource;

    private MockMvc mvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(timelineDoseResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetDosingSummaries() throws Exception {

        SubjectDosingSummary sds = new SubjectDosingSummary();
        sds.setSubjectId("subject1");

        DateDayHour ddh = new DateDayHour(new Date(), 20.4);
        ddh.setStudyDayHourAsString("std_d_h");
        ddh.setDoseDayHour(2.0);

        DosingSummaryEvent dse = new DosingSummaryEvent();
        dse.setEnd(ddh);
        dse.setStart(ddh);
        dse.setPercentChange(new PercentChange(10., 20.));
        dse.setImputedEndDate(true);
        dse.setOngoing(true);

        sds.setEvents(newArrayList(dse));

        List<SubjectDosingSummary> response = newArrayList(sds);

        TimelineDosingRequest timelineDosingRequest = new TimelineDosingRequest();
        timelineDosingRequest.setDayZero(new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE));
        timelineDosingRequest.setPopulationFilters(new com.acuity.visualisations.rawdatamodel.filters.PopulationFilters());
        timelineDosingRequest.setDoseFilters(new DrugDoseFilters());
        timelineDosingRequest.setMaxDoseType(com.acuity.visualisations.rawdatamodel.vo.timeline.dose.MaxDoseType.PER_STUDY);
        timelineDosingRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockTimelineDoseService.getDosingSummaries(any(Datasets.class), any(), any(),
                any(), any(DrugDoseFilters.class), any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class))).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/timeline/dosing/dose-summaries").
                content(mapper.writeValueAsString(timelineDosingRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        verify(mockTimelineDoseService, times(1)).getDosingSummaries(
                eq(DUMMY_DETECT_DATASETS),
                eq(DayZeroType.DAYS_SINCE_FIRST_DOSE),
                any(String.class),
                any(MaxDoseType.class),
                any(DrugDoseFilters.class),
                any(PopulationFilters.class)
        );

        verifyNoMoreInteractions(mockTimelineDoseService);
    }
}
