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

package com.acuity.visualisations.rest.resources;

import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.filters.CIEventFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.service.event.CIEventService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.ColoredOutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputOvertimeData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputOvertimeLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.cievents.BarChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.cievents.CIBarLineChartRequest;
import com.acuity.visualisations.rest.model.request.cievents.CIEventBarChartRequest;
import com.acuity.visualisations.rest.model.request.cievents.CIEventRequest;
import com.acuity.visualisations.rest.test.config.DisableAutowireRequiredInitializer;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
        initializers = DisableAutowireRequiredInitializer.class,
        classes = {MockServletContext.class}
)
@WebAppConfiguration
public class CIEventResourceTest {

    private static ObjectMapper mapper;
    @InjectMocks
    private CIEventResource ciEventResource;
    @Mock
    private CIEventService ciEventService;
    private MockMvc mvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(ciEventResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetTrellisOptions() throws Exception {
        CIEventRequest requestBody = new CIEventRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setCieventsFilters(CIEventFilters.empty());
        requestBody.setPopulationFilters(PopulationFilters.empty());

        List<TrellisOptions<CIEventGroupByOptions>> mockedResponse = newArrayList(
                new TrellisOptions<>(CIEventGroupByOptions.FINAL_DIAGNOSIS, Arrays.asList("fd1", "fd2")));

        when(ciEventService.getTrellisOptions(eq(DUMMY_DETECT_DATASETS), any(Filters.class), any(PopulationFilters.class)))
                .thenReturn(mockedResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cievents/trellising").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<TrellisOptions<CIEventGroupByOptions>> response
                = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<TrellisOptions<CIEventGroupByOptions>>>() {
        });

        verify(ciEventService, times(1)).getTrellisOptions(any(Datasets.class),
                any(CIEventFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(ciEventService);

        assertThat(response.size()).isEqualTo(1);
        assertThat(response.contains(mockedResponse));
    }

    @Test
    public void shouldGetBarChartXAxis() throws Exception {
        CIEventRequest requestBody = new CIEventRequest();
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setCieventsFilters(CIEventFilters.empty());

        AxisOptions<CIEventGroupByOptions> axes = new AxisOptions<>(
                Arrays.asList(
                        new AxisOption<>(CIEventGroupByOptions.CI_SYMPTOMS_DURATION, false, false, false, false),
                        new AxisOption<>(CIEventGroupByOptions.START_DATE, true, true, true, true)
                ), true, Arrays.asList("drug1"));

        when(ciEventService.getAvailableBarChartXAxis(eq(DUMMY_DETECT_DATASETS), any(CIEventFilters.class),
                any(PopulationFilters.class))).thenReturn(axes);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cievents/countsbarchart-xaxis").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        AxisOptions<CIEventGroupByOptions> response =
                mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<AxisOptions<CIEventGroupByOptions>>() {
                });
        verify(ciEventService, times(1)).getAvailableBarChartXAxis(any(Datasets.class), any(CIEventFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(ciEventService);

        assertThat(response.getOptions()).hasSize(2);
        assertThat(response.getOptions()).extracting(AxisOption::getGroupByOption, AxisOption::isBinableOption, AxisOption::isTimestampOption)
                .containsExactlyInAnyOrder(
                        tuple(CIEventGroupByOptions.CI_SYMPTOMS_DURATION, false, false),
                        tuple(CIEventGroupByOptions.START_DATE, true, true));
    }

    @Test
    public void shouldGetOverTimeChartXAxis() throws Exception {
        CIEventRequest requestBody = new CIEventRequest();
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setCieventsFilters(CIEventFilters.empty());

        AxisOptions<CIEventGroupByOptions> axes = new AxisOptions<>(
                Arrays.asList(
                        new AxisOption<>(CIEventGroupByOptions.CI_SYMPTOMS_DURATION, false, false, false, false),
                        new AxisOption<>(CIEventGroupByOptions.START_DATE, true, true, true, true)
                ), true, Arrays.asList("drug1"));

        when(ciEventService.getAvailableOverTimeChartXAxis(eq(DUMMY_DETECT_DATASETS), any(CIEventFilters.class),
                any(PopulationFilters.class))).thenReturn(axes);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cievents/overtime-xaxis").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        AxisOptions<CIEventGroupByOptions> response =
                mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<AxisOptions<CIEventGroupByOptions>>() {
                });
        verify(ciEventService, times(1)).getAvailableOverTimeChartXAxis(any(Datasets.class), any(CIEventFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(ciEventService);

        assertThat(response.getOptions()).hasSize(2);
        assertThat(response.getOptions()).extracting(AxisOption::getGroupByOption, AxisOption::isBinableOption,
                AxisOption::isSupportsDuration, AxisOption::isTimestampOption)
                .containsExactlyInAnyOrder(
                        tuple(CIEventGroupByOptions.CI_SYMPTOMS_DURATION, false, false, false),
                        tuple(CIEventGroupByOptions.START_DATE, true, true, true));
    }

    @Test
    public void shouldGetAvailableFilters() throws Exception {
        CIEventFilters ciEventFilters = new CIEventFilters();
        ciEventFilters.setFinalDiagnosis(new SetFilter<>(newHashSet("finalD1", "finalD2")));

        CIEventRequest requestBody = new CIEventRequest();
        requestBody.setCieventsFilters(ciEventFilters);
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(ciEventService.getAvailableFilters(any(), any(), any()))
                .thenReturn(ciEventFilters);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cievents/filters").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        CIEventFilters returnedFilters = mapper.readValue(result.getResponse().getContentAsString(), CIEventFilters.class);

        assertThat(returnedFilters).isEqualTo(ciEventFilters);
        verify(ciEventService, times(1)).getAvailableFilters(any(), any(), any());
        verifyNoMoreInteractions(ciEventService);
    }

    @Test
    public void shouldGetBarChartData() throws Exception {
        CIEventBarChartRequest requestBody = new CIEventBarChartRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setCieventsFilters(CIEventFilters.empty());
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setCountType(CountType.PERCENTAGE_OF_ALL_EVENTS);

        List<TrellisOption<CIEvent, CIEventGroupByOptions>> trellisedBy = newArrayList(
                TrellisOption.of(CIEventGroupByOptions.FINAL_DIAGNOSIS, "finD1"));
        List<ColoredOutputBarChartData> data = newArrayList(new ColoredOutputBarChartData("name", newArrayList("cat1"), newArrayList(), "pink"));
        List<TrellisedBarChart<CIEvent, CIEventGroupByOptions>> barCharts = newArrayList(new TrellisedBarChart<>(trellisedBy, data));

        when(ciEventService.getBarChart(any(Datasets.class), any(), any(CIEventFilters.class),
                any(PopulationFilters.class), any(CountType.class))).thenReturn(barCharts);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cievents/countsbarchart").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetBarLineChartData() throws Exception {
        ChartGroupByOptions<CIEvent, CIEventGroupByOptions> settings
                = ChartGroupByOptions.<CIEvent, CIEventGroupByOptions>builder().build();
        CIBarLineChartRequest requestBody = new CIBarLineChartRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setCieventsFilters(CIEventFilters.empty());
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setSettings(ChartGroupByOptionsFiltered.<CIEvent, CIEventGroupByOptions>builder(settings).build());

        List<TrellisOption<CIEvent, CIEventGroupByOptions>> trellisedBy = newArrayList(
                TrellisOption.of(CIEventGroupByOptions.FINAL_DIAGNOSIS, "finD1"));
        OutputOvertimeData data = new OutputOvertimeData(
                newArrayList(new OutputOvertimeLineChartData("line name", newArrayList(), "colour")), newArrayList("cat1"), newArrayList());
        List<TrellisedOvertime<CIEvent, CIEventGroupByOptions>> barCharts = newArrayList(new TrellisedOvertime<>(trellisedBy, data));

        when(ciEventService.getLineBarChart(
                any(Datasets.class), any(), any(CIEventFilters.class), any(PopulationFilters.class))).thenReturn(barCharts);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cievents/overtime").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<TrellisedOvertime<CIEvent, CIEventGroupByOptions>> response =
                mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<TrellisedOvertime<CIEvent, CIEventGroupByOptions>>>() {
                });

        assertThat(response.get(0).getTrellisedBy()).isEqualTo(trellisedBy);
        assertThat(response.get(0).getData()).isEqualTo(data);

        verify(ciEventService, times(1)).getLineBarChart(eq(DUMMY_DETECT_DATASETS), any(), any(CIEventFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(ciEventService);
    }

    @Test
    public void shouldGetSubjectsForCIEventFilters() throws Exception {

        CIEventRequest requestBody = new CIEventRequest();
        requestBody.setCieventsFilters(new CIEventFilters());
        requestBody.setPopulationFilters(new PopulationFilters());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        List<String> subjectsToReturn = newArrayList("subject1", "subject2");

        when(ciEventService.getSubjects(any(Datasets.class), any(CIEventFilters.class), any(PopulationFilters.class)))
                .thenReturn(subjectsToReturn);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cievents/filters-subjects").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);


        MvcResult mvcResult = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<String> subjectsResult = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<String>>() {
        });


        assertThat(subjectsResult).isEqualTo(subjectsToReturn);
        verify(ciEventService, times(1))
                .getSubjects(eq(DUMMY_DETECT_DATASETS), any(CIEventFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(ciEventService);
    }

    @Test
    public void shouldGetDetailsOnDemandData() throws Exception {
        DetailsOnDemandRequest requestBody = new DetailsOnDemandRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setEventIds(newHashSet("1"));
        requestBody.setSortAttrs(Collections.singletonList(new SortAttrs("subjectId", false)));
        requestBody.setStart(0);
        requestBody.setEnd(10);

        List<Map<String, String>> mockedResponse = new ArrayList();
        Map<String, String> mockedItem = new HashMap<String, String>();
        mockedItem.put("key1", "value1");
        mockedResponse.add(mockedItem);

        when(ciEventService.getDetailsOnDemandData(eq(DUMMY_DETECT_DATASETS), any(Set.class), any(), anyLong(), anyLong()))
                .thenReturn(mockedResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cievents/details-on-demand").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<Map<String, String>> response =
                mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Map<String, String>>>() {
                });

        assertThat(response.get(0).get("key1")).isEqualTo("value1");

        verify(ciEventService, times(1)).getDetailsOnDemandData(eq(DUMMY_DETECT_DATASETS),
                any(Set.class), any(), anyLong(), anyLong());
        verifyNoMoreInteractions(ciEventService);
    }

    @Test
    public void shouldGetBarChartSelection() throws Exception {
        BarChartSelectionRequest requestBody = new BarChartSelectionRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setCieventsFilters(CIEventFilters.empty());
        requestBody.setPopulationFilters(PopulationFilters.empty());
        ChartGroupByOptions<CIEvent, CIEventGroupByOptions> settings = ChartGroupByOptions.<CIEvent, CIEventGroupByOptions>builder().build();
        Collection<ChartSelectionItem<CIEvent, CIEventGroupByOptions>> items = Collections.emptySet();
        ChartSelection<CIEvent, CIEventGroupByOptions, ChartSelectionItem<CIEvent, CIEventGroupByOptions>> selection =
                ChartSelection.of(settings, items);
        requestBody.setSelection(selection);


        HashSet eventIds = new HashSet<>();
        eventIds.add("e1");
        HashSet subjectIds = new HashSet<>();
        subjectIds.add("s1");
        SelectionDetail mockedResponse = new SelectionDetail(eventIds, subjectIds, 10, 5);

        when(ciEventService.getSelectionDetails(eq(DUMMY_DETECT_DATASETS), any(CIEventFilters.class),
                any(PopulationFilters.class), any(ChartSelection.class))).thenReturn(mockedResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cievents/selection").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SelectionDetail response =
                mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<SelectionDetail>() {
                });

        assertThat(response.getEventIds().contains("e1")).isTrue();
        assertThat(response.getSubjectIds().contains("s1")).isTrue();
        assertThat(response.getEventIds().size()).isEqualTo(1);
        assertThat(response.getSubjectIds().size()).isEqualTo(1);
        assertThat(response.getTotalSubjects()).isEqualTo(5);
        assertThat(response.getTotalEvents()).isEqualTo(10);

        verify(ciEventService, times(1)).getSelectionDetails(eq(DUMMY_DETECT_DATASETS), any(CIEventFilters.class),
                any(PopulationFilters.class), any(ChartSelection.class));
        verifyNoMoreInteractions(ciEventService);
    }

    @Test
    public void shouldGetSingleSubjectData() throws Exception {
        SingleSubjectRequest<CIEventFilters> requestBody = new SingleSubjectRequest<CIEventFilters>();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setEventFilters(CIEventFilters.empty());
        requestBody.setSubjectId("1");

        List<Map<String, String>> mockedResponse = new ArrayList();
        Map<String, String> mockedItem = new HashMap<String, String>();
        mockedItem.put("key1", "value1");
        mockedResponse.add(mockedItem);

        when(ciEventService.getSingleSubjectData(eq(DUMMY_DETECT_DATASETS), anyString(), any(CIEventFilters.class))).thenReturn(mockedResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cievents/single-subject").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<Map<String, String>> response =
                mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Map<String, String>>>() {
                });

        assertThat(response.get(0).get("key1")).isEqualTo("value1");

        verify(ciEventService, times(1)).getSingleSubjectData(eq(DUMMY_DETECT_DATASETS), anyString(), any(CIEventFilters.class));
        verifyNoMoreInteractions(ciEventService);
    }
}
