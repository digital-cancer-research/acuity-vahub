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

package com.acuity.visualisations.rest.resources.population;

import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rest.model.request.population.PopulationBarChartRequest;
import com.acuity.visualisations.rest.model.request.population.PopulationRequest;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.core.type.TypeReference;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.AGE;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unchecked")
public class PopulationSummaryPlotResourceTest {
    @Mock
    private PopulationService mockPopulationService;
    @InjectMocks
    private PopulationSummaryPlotResource populationResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(populationResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetAvailableBarChartXAxis() throws Exception {
        PopulationRequest requestBody = new PopulationRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setPopulationFilters(PopulationFilters.empty());

        AxisOptions<PopulationGroupByOptions> mockResponse = new AxisOptions<>(asList(new AxisOption<>(AGE)), true, asList("STDY4321", "DRUG002"));

        when(mockPopulationService.getAvailableBarChartXAxisOptions(eq(DUMMY_DETECT_DATASETS),
                any(PopulationFilters.class))).thenReturn(mockResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/population/summary-plot/x-axis").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        AxisOptions<PopulationGroupByOptions> response
                = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<AxisOptions<PopulationGroupByOptions>>() {
        });

        assertThat(response).isEqualTo(mockResponse);
        verify(mockPopulationService, times(1))
                .getAvailableBarChartXAxisOptions(any(Datasets.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(mockPopulationService);
    }

    @Test
    public void shouldGetValuesForBarChart() throws Exception {
        PopulationBarChartRequest requestBody = new PopulationBarChartRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setCountType(CountType.COUNT_OF_SUBJECTS);
        ChartGroupByOptionsFiltered<Subject, PopulationGroupByOptions> settings = new ChartGroupByOptionsFiltered<>(
                new ChartGroupByOptions(new HashMap<>(), new HashSet<>()), new ArrayList());
        requestBody.setSettings(settings);

        List<TrellisedBarChart<Subject, PopulationGroupByOptions>> mockResponse = new ArrayList<>();
        mockResponse.add(new TrellisedBarChart<>(new ArrayList<>(), new ArrayList<>()));

        when(mockPopulationService.getBarChart(eq(DUMMY_DETECT_DATASETS), eq(settings),
                any(PopulationFilters.class), eq(CountType.COUNT_OF_SUBJECTS))).thenReturn(mockResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/population/summary-plot/values").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<TrellisedBarChart<Subject, PopulationGroupByOptions>> response
                = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<TrellisedBarChart<Subject, PopulationGroupByOptions>>>() {
        });

        assertThat(response).isEqualTo(mockResponse);
        verify(mockPopulationService, times(1)).getBarChart(eq(DUMMY_DETECT_DATASETS), eq(settings),
                any(PopulationFilters.class), eq(CountType.COUNT_OF_SUBJECTS));
        verifyNoMoreInteractions(mockPopulationService);
    }

    @Test
    public void shouldGetBarChartColorByOptions() throws Exception {
        PopulationRequest requestBody = new PopulationRequest();
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setPopulationFilters(PopulationFilters.empty());

        List<TrellisOptions<PopulationGroupByOptions>> mockResponse = new ArrayList<>();
        mockResponse.add(new TrellisOptions<>(AGE, asList("43", "45")));

        when(mockPopulationService.getBarChartColorByOptions(eq(DUMMY_DETECT_DATASETS),
                any(PopulationFilters.class))).thenReturn(mockResponse);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post("/resources/population/summary-plot/color-by-options").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult result = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        List<TrellisOptions<PopulationGroupByOptions>> response
                = mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<TrellisOptions<PopulationGroupByOptions>>>() {
        });

        assertThat(response).isEqualTo(mockResponse);
        verify(mockPopulationService, times(1))
                .getBarChartColorByOptions(any(Datasets.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(mockPopulationService);
    }
}
