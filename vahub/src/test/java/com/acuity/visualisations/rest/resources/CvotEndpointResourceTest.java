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
import com.acuity.visualisations.rawdatamodel.filters.CvotEndpointFilters;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.service.event.CvotEndpointService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.ColoredOutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.cvot.endpoint.CvotEndpointBarChartRequest;
import com.acuity.visualisations.rest.model.request.cvot.endpoint.CvotEndpointBarChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.cvot.endpoint.CvotEndpointRequest;
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
public class CvotEndpointResourceTest {

    private static ObjectMapper mapper;
    @InjectMocks
    private CvotEndpointResource cvotEndpointResource;
    @Mock
    private CvotEndpointService cvotEndpointService;
    private MockMvc mvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(cvotEndpointResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetTrellisOptions() throws Exception {
        CvotEndpointRequest requestBody = new CvotEndpointRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setCvotEndpointFilters(CvotEndpointFilters.empty().empty());
        requestBody.setPopulationFilters(PopulationFilters.empty());

        List<TrellisOptions<CvotEndpointGroupByOptions>> mockedResponse = newArrayList(
                new TrellisOptions(CvotEndpointGroupByOptions.CATEGORY_1, Arrays.asList("c1", "c2")));

        when(cvotEndpointService.getTrellisOptions(eq(DUMMY_DETECT_DATASETS), any(Filters.class), any(PopulationFilters.class)))
                .thenReturn(mockedResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cvotendpoint/trellising").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<TrellisOptions<CvotEndpointGroupByOptions>> response
                = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<TrellisOptions<CvotEndpointGroupByOptions>>>() {
        });

        verify(cvotEndpointService, times(1)).getTrellisOptions(any(Datasets.class),
                any(CvotEndpointFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(cvotEndpointService);

        assertThat(response.size()).isEqualTo(1);
        assertThat(response.contains(mockedResponse));
    }

    @Test
    public void shouldGetBarChartXAxis() throws Exception {
        CvotEndpointRequest requestBody = new CvotEndpointRequest();
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setCvotEndpointFilters(CvotEndpointFilters.empty());

        AxisOptions<CvotEndpointGroupByOptions> axes = new AxisOptions<>(
                Arrays.asList(
                        new AxisOption<>(CvotEndpointGroupByOptions.CATEGORY_2, false, false, false, false),
                        new AxisOption<>(CvotEndpointGroupByOptions.START_DATE, true, true, true, true)
                ), true, Arrays.asList("drug1"));

        when(cvotEndpointService.getAvailableBarChartXAxis(eq(DUMMY_DETECT_DATASETS), any(CvotEndpointFilters.class),
                any(PopulationFilters.class))).thenReturn(axes);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cvotendpoint/countsbarchart-xaxis").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        AxisOptions<CvotEndpointGroupByOptions> response =
                mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<AxisOptions<CvotEndpointGroupByOptions>>() {
                });
        verify(cvotEndpointService, times(1)).getAvailableBarChartXAxis(any(Datasets.class), any(CvotEndpointFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(cvotEndpointService);


        assertThat(response.getOptions()).hasSize(2);
        assertThat(response.getOptions()).extracting(AxisOption::getGroupByOption, AxisOption::isBinableOption,
                AxisOption::isSupportsDuration, AxisOption::isTimestampOption)
                .containsExactlyInAnyOrder(
                        tuple(CvotEndpointGroupByOptions.CATEGORY_2, false, false, false),
                        tuple(CvotEndpointGroupByOptions.START_DATE, true, true, true));

    }

    @Test
    public void shouldGetAvailableFilters() throws Exception {
        CvotEndpointFilters cvotEndpointFilters = new CvotEndpointFilters();
        cvotEndpointFilters.setCategory1(new SetFilter<>(newHashSet("cat1", "cat2")));

        CvotEndpointRequest requestBody = new CvotEndpointRequest();
        requestBody.setCvotEndpointFilters(cvotEndpointFilters);
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(cvotEndpointService.getAvailableFilters(any(), any(CvotEndpointFilters.class), any(PopulationFilters.class)))
                .thenReturn(cvotEndpointFilters);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cvotendpoint/filters").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        CvotEndpointFilters returnedFilters = mapper.readValue(result.getResponse().getContentAsString(), CvotEndpointFilters.class);

        assertThat(returnedFilters).isEqualTo(cvotEndpointFilters);
        verify(cvotEndpointService, times(1)).getAvailableFilters(any(), any(CvotEndpointFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(cvotEndpointService);
    }

    @Test
    public void shouldGetBarChartData() throws Exception {
        ChartGroupByOptions<CvotEndpoint, CvotEndpointGroupByOptions> settings
                = ChartGroupByOptions.<CvotEndpoint, CvotEndpointGroupByOptions>builder().build();
        CvotEndpointBarChartRequest requestBody = new CvotEndpointBarChartRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setCvotEndpointFilters(CvotEndpointFilters.empty());
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setCountType(CountType.PERCENTAGE_OF_ALL_EVENTS);
        requestBody.setSettings(ChartGroupByOptionsFiltered.<CvotEndpoint, CvotEndpointGroupByOptions>builder(settings).build());

        List<TrellisOption<CvotEndpoint, CvotEndpointGroupByOptions>> trellisedBy = newArrayList(
                TrellisOption.of(CvotEndpointGroupByOptions.CATEGORY_2, "cat1"));
        List<ColoredOutputBarChartData> data = newArrayList(new ColoredOutputBarChartData("name", newArrayList("cat1"), newArrayList(), "pink"));
        List<TrellisedBarChart<CvotEndpoint, CvotEndpointGroupByOptions>> barCharts = newArrayList(new TrellisedBarChart<>(trellisedBy, data));

        when(cvotEndpointService.getBarChart(any(Datasets.class), any(ChartGroupByOptionsFiltered.class), any(CvotEndpointFilters.class),
                any(PopulationFilters.class), any(CountType.class))).thenReturn(barCharts);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cvotendpoint/countsbarchart").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetSubjectsForCvotEndpointFilters() throws Exception {
        CvotEndpointRequest requestBody = new CvotEndpointRequest();
        requestBody.setCvotEndpointFilters(new CvotEndpointFilters());
        requestBody.setPopulationFilters(new PopulationFilters());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        List<String> subjectsToReturn = newArrayList("subject1", "subject2");

        when(cvotEndpointService.getSubjects(any(Datasets.class), any(CvotEndpointFilters.class), any(PopulationFilters.class)))
                .thenReturn(subjectsToReturn);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cvotendpoint/filters-subjects").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);


        MvcResult mvcResult = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<String> subjectsResult = mapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<String>>() {
        });

        assertThat(subjectsResult).isEqualTo(subjectsToReturn);
        verify(cvotEndpointService, times(1))
                .getSubjects(eq(DUMMY_DETECT_DATASETS), any(CvotEndpointFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(cvotEndpointService);
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

        when(cvotEndpointService.getDetailsOnDemandData(eq(DUMMY_DETECT_DATASETS), any(Set.class), any(), anyLong(), anyLong()))
                .thenReturn(mockedResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cvotendpoint/details-on-demand").
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

        verify(cvotEndpointService, times(1)).getDetailsOnDemandData(eq(DUMMY_DETECT_DATASETS),
                any(Set.class), any(), anyLong(), anyLong());
        verifyNoMoreInteractions(cvotEndpointService);
    }

    @Test
    public void shouldGetSelection() throws Exception {
        CvotEndpointBarChartSelectionRequest requestBody = new CvotEndpointBarChartSelectionRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setCvotEndpointFilters(CvotEndpointFilters.empty());
        requestBody.setPopulationFilters(PopulationFilters.empty());
        ChartGroupByOptions<CvotEndpoint, CvotEndpointGroupByOptions> settings =
                ChartGroupByOptions.<CvotEndpoint, CvotEndpointGroupByOptions>builder().build();
        Collection<ChartSelectionItem<CvotEndpoint, CvotEndpointGroupByOptions>> items = Collections.emptySet();
        ChartSelection<CvotEndpoint, CvotEndpointGroupByOptions, ChartSelectionItem<CvotEndpoint, CvotEndpointGroupByOptions>> selection =
                ChartSelection.of(settings, items);
        requestBody.setSelection(selection);


        HashSet eventIds = new HashSet<>();
        eventIds.add("e1");
        HashSet subjectIds = new HashSet<>();
        subjectIds.add("s1");
        SelectionDetail mockedResponse = new SelectionDetail(eventIds, subjectIds, 10, 5);

        when(cvotEndpointService.getSelectionDetails(eq(DUMMY_DETECT_DATASETS), any(CvotEndpointFilters.class),
                any(PopulationFilters.class), any(ChartSelection.class))).thenReturn(mockedResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cvotendpoint/selection").
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

        verify(cvotEndpointService, times(1)).getSelectionDetails(eq(DUMMY_DETECT_DATASETS), any(CvotEndpointFilters.class),
                any(PopulationFilters.class), any(ChartSelection.class));
        verifyNoMoreInteractions(cvotEndpointService);
    }

    @Test
    public void shouldGetSingleSubjectData() throws Exception {
        SingleSubjectRequest<CvotEndpointFilters> requestBody = new SingleSubjectRequest<CvotEndpointFilters>();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setEventFilters(CvotEndpointFilters.empty());
        requestBody.setSubjectId("1");

        List<Map<String, String>> mockedResponse = new ArrayList();
        Map<String, String> mockedItem = new HashMap<String, String>();
        mockedItem.put("key1", "value1");
        mockedResponse.add(mockedItem);

        when(cvotEndpointService.getSingleSubjectData(eq(DUMMY_DETECT_DATASETS), anyString(), any(CvotEndpointFilters.class))).thenReturn(mockedResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/cvotendpoint/single-subject").
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

        verify(cvotEndpointService, times(1)).getSingleSubjectData(eq(DUMMY_DETECT_DATASETS), anyString(), any(CvotEndpointFilters.class));
        verifyNoMoreInteractions(cvotEndpointService);
    }
}
