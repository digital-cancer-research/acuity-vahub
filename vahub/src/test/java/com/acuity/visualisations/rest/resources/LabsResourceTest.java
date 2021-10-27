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

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.service.event.LabService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.visualisations.rest.model.request.DetailsOnDemandRequest;
import com.acuity.visualisations.rest.model.request.SingleSubjectRequest;
import com.acuity.visualisations.rest.model.request.labs.LabSelectionRequest;
import com.acuity.visualisations.rest.model.request.labs.LabStatsRequest;
import com.acuity.visualisations.rest.model.request.labs.LabsRequest;
import com.acuity.visualisations.rest.model.request.labs.LabsRequestSqlImpl;
import com.acuity.visualisations.rest.model.request.labs.LabsTrellisRequest;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions.ARM;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions.MEASUREMENT;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.anySet;
import static org.mockito.Mockito.eq;
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
@SuppressWarnings("unchecked")
public class LabsResourceTest {

    @Mock
    private LabService mockLabsJavaService;

    @InjectMocks
    private LabsResource labsResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(labsResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetAvailableFilters() throws Exception {

        LabFilters labsFilters = new LabFilters();

        LabsRequest labsRequest = new LabsRequest();
        labsRequest.setLabsFilters(labsFilters);
        labsRequest.setPopulationFilters(new com.acuity.visualisations.rawdatamodel.filters.PopulationFilters());
        labsRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockLabsJavaService.getAvailableFilters(
                any(Datasets.class), any(LabFilters.class),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class)))
            .thenReturn(labsFilters);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/labs/filters").
                content(mapper.writeValueAsString(labsRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        LabFilters returnedLabsFilters = mapper.readValue(result.getResponse().getContentAsString(), LabFilters.class);

        assertThat(returnedLabsFilters).isEqualTo(labsFilters);
        verify(mockLabsJavaService, times(1)).getAvailableFilters(
                eq(DUMMY_DETECT_DATASETS),
                any(LabFilters.class),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class));
        verifyNoMoreInteractions(mockLabsJavaService);
    }

    @Test
    public void shouldGetAvailableSubjects() throws Exception {

        LabFilters labsFilters = new LabFilters();

        LabsRequest labsRequest = new LabsRequest();
        labsRequest.setLabsFilters(labsFilters);
        labsRequest.setPopulationFilters(new com.acuity.visualisations.rawdatamodel.filters.PopulationFilters());
        labsRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        List<String> mockResponse = newArrayList("subj-1", "subj2");

        when(mockLabsJavaService.getSubjects(
                any(Datasets.class), any(LabFilters.class),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class)))
        .thenReturn(mockResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/labs/filters-subjects").
                content(mapper.writeValueAsString(labsRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<String> response = mapper.readValue(result.getResponse().getContentAsString(), List.class);

        assertThat(response).isEqualTo(mockResponse);
        verify(mockLabsJavaService, times(1)).getSubjects(
                eq(DUMMY_DETECT_DATASETS),
                any(LabFilters.class), any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class));
        verifyNoMoreInteractions(mockLabsJavaService);
    }

    @Test
    public void shouldGetAvailableTrellising() throws Exception {

        LabFilters labsFilters = new LabFilters();
        com.acuity.visualisations.rawdatamodel.filters.PopulationFilters populationFilters =
                new com.acuity.visualisations.rawdatamodel.filters.PopulationFilters();

        LabsTrellisRequest requestBody = new LabsTrellisRequest();
        requestBody.setLabsFilters(labsFilters);
        requestBody.setPopulationFilters(populationFilters);
        requestBody.setYAxisOption(LabGroupByOptions.ACTUAL_VALUE);
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        //requestBody.setSettings(ChartGroupByOptionsFiltered.<Lab, LabGroupByOptions>builder(null).build());

        List<com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions<LabGroupByOptions>> response =
                newArrayList(new com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions<LabGroupByOptions>());

        when(mockLabsJavaService.getTrellisOptions(any(Datasets.class), any(Filters.class),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class), any())).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/labs/trellising").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions<LabGroupByOptions>> returnedValue
                = mapper.readValue(result.getResponse().getContentAsString(),
                    new TypeReference<List<TrellisOptions<LabGroupByOptions>>>() {
        });

        assertThat(returnedValue).isEqualTo(response);
        verify(mockLabsJavaService, times(1))
                .getTrellisOptions(eq(DUMMY_DETECT_DATASETS),
                        any(Filters.class), any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class), any());
        verifyNoMoreInteractions(mockLabsJavaService);
    }

    @Test
    public void shouldGetLabBoxPlotStatistics() throws Exception {

        LabStatsRequest labsBoxPlotRequest = new LabStatsRequest();
        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.MEASUREMENT_TIME_POINT.getGroupByOptionAndParams(
                        GroupByOption.Params.builder()
                                .with(GroupByOption.Param.BIN_SIZE, 10)
                                .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                                .build()
                ))
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();
        final HashMap<LabGroupByOptions, Object> filterByTrellis1 = new HashMap<>();
        filterByTrellis1.put(LabGroupByOptions.MEASUREMENT, "ALKALINE PHOSPHATASE (U/L)");
        filterByTrellis1.put(LabGroupByOptions.ARM, "Placebo");
        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(filterByTrellis1)
                .build();

        labsBoxPlotRequest.setSettings(settingsFiltered);
        labsBoxPlotRequest.setLabsFilters(new LabFilters());
        labsBoxPlotRequest.setPopulationFilters(new com.acuity.visualisations.rawdatamodel.filters.PopulationFilters());
        labsBoxPlotRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        labsBoxPlotRequest.setStatType(StatType.MEDIAN);

        List<com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot<Lab, LabGroupByOptions>> response =
                newArrayList(new com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot<>());

        when(mockLabsJavaService.getBoxPlot(any(Datasets.class), any(ChartGroupByOptionsFiltered.class), any(Filters.class),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class))).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/labs/boxplot").
                content(mapper.writeValueAsString(labsBoxPlotRequest)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<TrellisedBoxPlot<Lab, LabGroupByOptions>> returnedBoxPlotStats
                = mapper.readValue(result.getResponse().getContentAsString(),
                    new TypeReference<List<TrellisedBoxPlot<Lab, LabGroupByOptions>>>() {
                });

        assertThat(returnedBoxPlotStats).isEqualTo(response);
        verify(mockLabsJavaService, times(1)).getBoxPlot(any(Datasets.class), any(ChartGroupByOptionsFiltered.class), any(Filters.class),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class));
        verifyNoMoreInteractions(mockLabsJavaService);
    }

    @Test
    public void shouldGetDetailsOnDemandData() throws Exception {

        DetailsOnDemandRequest requestBody = new DetailsOnDemandRequest();
        requestBody.setEventIds(newHashSet("123", "456"));
        requestBody.setSortAttrs(Collections.singletonList(new SortAttrs("subjectId", false)));
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        List<Map<String, String>> mockResponse = newArrayList();

        when(mockLabsJavaService.getDetailsOnDemandData(eq(DUMMY_DETECT_DATASETS), anySet(), any(), anyLong(), anyLong()))
                .thenReturn(mockResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/labs/details-on-demand").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<Map<String, String>> response
                = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Map<String, String>>>() {
                });

        assertThat(response).isEqualTo(mockResponse);
        verify(mockLabsJavaService, times(1)).getDetailsOnDemandData(eq(DUMMY_DETECT_DATASETS),
                anySet(), any(), anyLong(), anyLong());
        verifyNoMoreInteractions(mockLabsJavaService);
    }

    @Test
    public void shouldDownloadAllDetailsOnDemandData() throws Exception {
        LabsRequestSqlImpl requestBody = new LabsRequestSqlImpl();
        requestBody.setLabsFilters(new LabFilters());
        requestBody.setPopulationFilters(new PopulationFilters());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/labs/download-details-on-demand").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        assertThat(response).isEqualTo("");
    }

    @Test
    public void shouldDownloadSelectedDetailsOnDemandData() throws Exception {
        DetailsOnDemandRequest requestBody = new DetailsOnDemandRequest();
        requestBody.setEventIds(newHashSet("ev-1"));
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/labs/download-selected-details-on-demand").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();

        assertThat(response).isEqualTo("");
    }

    @Test
    public void shouldGetSingleSubjectTableData() throws Exception {

        SingleSubjectRequest requestBody = new SingleSubjectRequest();
        requestBody.setSubjectId("Subj-1");
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setEventFilters(LabFilters.empty());

        List<Map<String, String>> mockResponse = newArrayList();

        when(mockLabsJavaService.getSingleSubjectData(eq(DUMMY_DETECT_DATASETS), eq("Subj-1"), any(LabFilters.class))).thenReturn(mockResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/labs/single-subject").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<Map<String, String>> response
                = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Map<String, String>>>() {
        });

        verify(mockLabsJavaService, times(1)).getDetailsOnDemandData(any(Datasets.class), eq("Subj-1"), any(LabFilters.class));
        verifyNoMoreInteractions(mockLabsJavaService);
    }

    @Test
    public void shouldGetBoxPlotSelectionData() throws Exception {

        LabSelectionRequest requestBody = new LabSelectionRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setLabsFilters(LabFilters.empty());
        requestBody.setPopulationFilters(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.empty());

        final HashMap<LabGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "PH-HYPO");
        selectedTrellises.put(ARM, "SuperDex 20 mg");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "5.0");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(X_AXIS, "5.5");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems3 = new HashMap<>();
        selectedItems3.put(X_AXIS, "7.0");


        final List<ChartSelectionItemRange<Lab, LabGroupByOptions, Double>> selectionItems = Arrays.asList(
                ChartSelectionItemRange.of(selectedTrellises, selectedItems1, 1., 11.),
                ChartSelectionItemRange.of(selectedTrellises, selectedItems2, 1., 11.),
                ChartSelectionItemRange.of(selectedTrellises, selectedItems3, 1., 11.)
        );

        requestBody.setSelection(ChartSelection.of(null, selectionItems));

        com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail mockResponse = new com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail();

        when(mockLabsJavaService.getSelectionDetails(eq(DUMMY_DETECT_DATASETS), any(Filters.class), any(PopulationFilters.class), any()))
                .thenReturn(mockResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/labs/mean-range-selection").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail response = mapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail>() {
        });

        assertThat(response).isEqualTo(mockResponse);
        verify(mockLabsJavaService, times(1)).getSelectionDetails(eq(DUMMY_DETECT_DATASETS), any(Filters.class), any(PopulationFilters.class), any());
        verifyNoMoreInteractions(mockLabsJavaService);
    }

    @Test
    public void shouldGetShiftPlot() throws Exception {
        ChartGroupByOptions<Lab, LabGroupByOptions> settings
                = ChartGroupByOptions.<Lab, LabGroupByOptions>builder().build();
        LabStatsRequest requestBody = new LabStatsRequest();
        requestBody.setSettings(ChartGroupByOptionsFiltered.<Lab, LabGroupByOptions>builder(settings).build());
        requestBody.setLabsFilters(new LabFilters());
        requestBody.setPopulationFilters(new com.acuity.visualisations.rawdatamodel.filters.PopulationFilters());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        List<com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedShiftPlot<Lab, LabGroupByOptions>> response =
                newArrayList(new com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedShiftPlot<>());

        when(mockLabsJavaService.getShiftPlot(any(Datasets.class), any(ChartGroupByOptionsFiltered.class), any(Filters.class),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class))).thenReturn(response);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/labs/shift-plot").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedShiftPlot<Lab, LabGroupByOptions>> returnedBoxPlotStats
                = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedShiftPlot<Lab, LabGroupByOptions>>>() {
                });

        assertThat(returnedBoxPlotStats).isEqualTo(response);
        verify(mockLabsJavaService, times(1)).getShiftPlot(any(Datasets.class), any(ChartGroupByOptionsFiltered.class), any(Filters.class),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class));
        verifyNoMoreInteractions(mockLabsJavaService);
    }

    @Test
    public void shouldGetRangePlotData() throws Exception {
        ChartGroupByOptions<Lab, LabGroupByOptions> settings
                = ChartGroupByOptions.<Lab, LabGroupByOptions>builder().build();
        LabStatsRequest requestBody = new LabStatsRequest();
        requestBody.setSettings(ChartGroupByOptionsFiltered.<Lab, LabGroupByOptions>builder(settings).build());
        requestBody.setLabsFilters(new LabFilters());
        requestBody.setPopulationFilters(new com.acuity.visualisations.rawdatamodel.filters.PopulationFilters());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setStatType(StatType.MEDIAN);

        List<TrellisedRangePlot<Lab, LabGroupByOptions>> mockResponse =
                newArrayList(new TrellisedRangePlot<Lab, LabGroupByOptions>());

        when(mockLabsJavaService.getRangePlot(any(Datasets.class), any(ChartGroupByOptionsFiltered.class), any(Filters.class),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class), any(StatType.class))
        ).thenReturn(mockResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/labs/mean-range-plot").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        List<TrellisedRangePlot<Lab, LabGroupByOptions>> response
                = mapper.readValue(result.getResponse().getContentAsString(),
                new TypeReference<List<TrellisedRangePlot<Lab, LabGroupByOptions>>>() {
                });

        assertThat(response).isEqualTo(mockResponse);
        verify(mockLabsJavaService, times(1)).getRangePlot(
                any(Datasets.class),
                any(ChartGroupByOptionsFiltered.class), any(Filters.class),
                any(com.acuity.visualisations.rawdatamodel.filters.PopulationFilters.class),
                any(StatType.class));
    }
}
