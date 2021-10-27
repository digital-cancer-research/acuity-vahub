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

package com.acuity.visualisations.rest.resources.respiratory.exacerbation;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.ExacerbationService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationBarChartRequest;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationRequest;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationSelectionRequest;
import com.acuity.visualisations.rest.model.response.respiratory.exacerbation.ExacerbationBarChartResponse;
import com.acuity.visualisations.rest.model.response.respiratory.exacerbation.ExacerbationBarChartXAxisResponse;
import com.acuity.visualisations.rest.model.response.respiratory.exacerbation.ExacerbationColorByOptionsResponse;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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

public class ExacerbationBarChartResourceTest {
    private static final String EXACERBATION_BAR_CHART_END_POINT = "/resources/respiratory/exacerbation/bar-chart";
    @Mock
    private ExacerbationService mockExacerbationService;
    @InjectMocks
    private ExacerbationBarChartResource exacerbationBarChartResource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(exacerbationBarChartResource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetAvailableXAxis() throws Exception {
        ExacerbationFilters exacerbationFilters = new ExacerbationFilters();
        PopulationFilters populationFilters = new PopulationFilters();

        ExacerbationRequest requestBody = new ExacerbationRequest();
        requestBody.setExacerbationFilters(exacerbationFilters);
        requestBody.setPopulationFilters(populationFilters);
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        ExacerbationBarChartXAxisResponse mockedResponse = new ExacerbationBarChartXAxisResponse(
                new AxisOptions<>(Arrays.asList(new AxisOption<>(ExacerbationGroupByOptions.WEIGHT)), false, null));

        when(mockExacerbationService.getAvailableBarChartXAxis(any(Datasets.class), any(ExacerbationFilters.class), any(PopulationFilters.class)))
                .thenReturn(mockedResponse.getXaxis());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(EXACERBATION_BAR_CHART_END_POINT + "/x-axis").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON_VALUE);

        MvcResult mvcResult = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ExacerbationBarChartXAxisResponse result = mapper.readValue(mvcResult.getResponse().getContentAsString(), ExacerbationBarChartXAxisResponse.class);

        assertThat(result).isEqualTo(mockedResponse);
        verify(mockExacerbationService, times(1))
                .getAvailableBarChartXAxis(eq(DUMMY_DETECT_DATASETS), any(ExacerbationFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(mockExacerbationService);
    }

    @Test
    public void shouldGetAvailableColorByOptions() throws Exception {
        ExacerbationRequest requestBody = new ExacerbationRequest();
        requestBody.setExacerbationFilters(new ExacerbationFilters());
        requestBody.setPopulationFilters(new PopulationFilters());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        ExacerbationColorByOptionsResponse resultToReturn
                = new ExacerbationColorByOptionsResponse(asList(new TrellisOptions<>(
                ExacerbationGroupByOptions.HOSPITALISATION, asList("hosp_1", "hosp_2"))));

        when(mockExacerbationService.getBarChartColorByOptions(any(Datasets.class), any(ExacerbationFilters.class), any(PopulationFilters.class)))
                .thenReturn(resultToReturn.getTrellisOptions());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(EXACERBATION_BAR_CHART_END_POINT + "/color-by-options").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ExacerbationColorByOptionsResponse result = mapper.readValue(mvcResult.getResponse().getContentAsString(), ExacerbationColorByOptionsResponse.class);

        assertThat(result).isEqualTo(resultToReturn);
        verify(mockExacerbationService, times(1))
                .getBarChartColorByOptions(eq(DUMMY_DETECT_DATASETS), any(ExacerbationFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(mockExacerbationService);
    }

    @Test
    public void shouldGetValuesForBarChart() throws Exception {
        ExacerbationBarChartRequest requestBody = new ExacerbationBarChartRequest();
        requestBody.setExacerbationFilters(new ExacerbationFilters());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setCountType(CountType.COUNT_OF_SUBJECTS);
        ChartGroupByOptionsFiltered<Exacerbation, ExacerbationGroupByOptions> settings = new ChartGroupByOptionsFiltered<Exacerbation, ExacerbationGroupByOptions>(
                new ChartGroupByOptions(new HashMap<>(), new HashSet<>()), new ArrayList<>());
        requestBody.setSettings(settings);

        ExacerbationBarChartResponse mockResponse = new ExacerbationBarChartResponse(Collections.singletonList(
                new TrellisedBarChart<>(new ArrayList<>(), new ArrayList<>())));

        when(mockExacerbationService.getBarChart(any(Datasets.class), eq(settings), any(ExacerbationFilters.class),
                any(PopulationFilters.class), eq(CountType.COUNT_OF_SUBJECTS)))
                .thenReturn(mockResponse.getBarChartData());

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(EXACERBATION_BAR_CHART_END_POINT + "/values").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);


        MvcResult mvcResult = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        ExacerbationBarChartResponse result = mapper.readValue(mvcResult.getResponse().getContentAsString(), ExacerbationBarChartResponse.class);

        assertThat(result).isEqualTo(mockResponse);
        verify(mockExacerbationService, times(1))
                .getBarChart(eq(DUMMY_DETECT_DATASETS), eq(settings), any(ExacerbationFilters.class),
                        any(PopulationFilters.class), eq(CountType.COUNT_OF_SUBJECTS));
        verifyNoMoreInteractions(mockExacerbationService);
    }

    @Test
    public void shouldGetSelectionDetails() throws Exception {
        ExacerbationSelectionRequest requestBody = new ExacerbationSelectionRequest();
        requestBody.setExacerbationFilters(new ExacerbationFilters());
        requestBody.setPopulationFilters(new PopulationFilters());
        requestBody.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Exacerbation, ExacerbationGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, ExacerbationGroupByOptions.AGE.getGroupByOptionAndParams());
        settings.withOption(COLOR_BY, ExacerbationGroupByOptions.HOSPITALISATION.getGroupByOptionAndParams());
        final HashMap<ExacerbationGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(COLOR_BY, "hospitalisation_1");
        selectedItems.put(X_AXIS, 55);

        requestBody.setSelection(ChartSelection.of(settings.build(),
                singletonList(ChartSelectionItem.of(selectedTrellises, selectedItems))));

        SelectionDetail resultToReturn = new SelectionDetail();

        when(mockExacerbationService.getSelectionDetails(any(Datasets.class), any(ExacerbationFilters.class),
                any(PopulationFilters.class), any(ChartSelection.class)))
                .thenReturn(resultToReturn);

        MockHttpServletRequestBuilder post = MockMvcRequestBuilders.
                post(EXACERBATION_BAR_CHART_END_POINT + "/selection").
                content(mapper.writeValueAsString(requestBody)).
                contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mvc.perform(post)
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SelectionDetail result = mapper.readValue(mvcResult.getResponse().getContentAsString(), SelectionDetail.class);

        assertThat(result).isEqualTo(resultToReturn);
        verify(mockExacerbationService, times(1))
                .getSelectionDetails(eq(DUMMY_DETECT_DATASETS), any(ExacerbationFilters.class),
                        any(PopulationFilters.class), any(ChartSelection.class));
        verifyNoMoreInteractions(mockExacerbationService);
    }
}
