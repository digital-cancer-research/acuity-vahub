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
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationOnSetLineChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationRequest;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationValuesRequest;
import com.acuity.visualisations.rest.model.response.respiratory.exacerbation.ExacerbationXAxisResponse;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExacerbationOnsetLineChartResourceTest {

    public static final String RESOURCE_URL = "/resources/respiratory/exacerbation/on-set-line-chart";

    private MockMvc mvc;
    @Mock
    @Qualifier("exacerbationService")
    private ExacerbationService exacerbationService;
    @InjectMocks
    private ExacerbationOnsetLineChartResource resource;
    private ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(resource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldCorrectlyGetXAxis() throws Exception {
        ExacerbationRequest axisRequest = new ExacerbationRequest();
        axisRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        axisRequest.setExacerbationFilters(ExacerbationFilters.empty());
        axisRequest.setPopulationFilters(PopulationFilters.empty());
        when(exacerbationService.getAvailableOnsetLineChartXAxis(any(Datasets.class),
                any(ExacerbationFilters.class), any(PopulationFilters.class)))
                .thenReturn(new ExacerbationXAxisResponse(Lists.emptyList(), true, Lists.emptyList()));

        this.mvc.perform(post(RESOURCE_URL + "/x-axis")
                .content(mapper.writeValueAsString(axisRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{hasRandomization=true, drugs=[], options=[]}"));
    }

    @Test
    public void shouldCorrectlyGetColorByOptions() throws Exception {
        ExacerbationRequest axisRequest = new ExacerbationRequest();
        axisRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        axisRequest.setExacerbationFilters(ExacerbationFilters.empty());
        axisRequest.setPopulationFilters(PopulationFilters.empty());
        when(exacerbationService.getOnsetLineChartColorByOptions(any(Datasets.class),
                any(ExacerbationFilters.class), any(PopulationFilters.class)))
                .thenReturn(Collections.singletonList(new TrellisOptions<>(
                        ExacerbationGroupByOptions.PLANNED_TREATMENT_ARM,
                        Collections.singletonList("TEST"))));

        this.mvc.perform(post(RESOURCE_URL + "/color-by-options")
                .content(mapper.writeValueAsString(axisRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content()
                        .json("{trellisOptions: ["
                                + "{trellisedBy: \"PLANNED_TREATMENT_ARM\""
                                + ",\"trellisOptions\": [\"TEST\"]}]}"));
    }

    @Test
    public void shouldCorrectlyGetValues() throws Exception {
        ExacerbationValuesRequest exacerbationValuesRequest = new ExacerbationValuesRequest();
        exacerbationValuesRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        exacerbationValuesRequest.setExacerbationFilters(ExacerbationFilters.empty());
        exacerbationValuesRequest.setPopulationFilters(PopulationFilters.empty());
        exacerbationValuesRequest.setCountType(CountType.COUNT_OF_EVENTS);
        ChartGroupByOptionsFiltered<Exacerbation, ExacerbationGroupByOptions> settings =
                new ChartGroupByOptionsFiltered<Exacerbation, ExacerbationGroupByOptions>(
                        new ChartGroupByOptions<>(new HashMap<>(), new HashSet<>()), new ArrayList<>());
        exacerbationValuesRequest.setSettings(settings);
        List<TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>> mockResponse =
                Collections.singletonList(new TrellisedBarChart<>(
                        new ArrayList<>(),
                        new ArrayList<>()
                ));
        when(exacerbationService.getOnsetLineChartValues(any(Datasets.class),
                eq(settings), any(ExacerbationFilters.class), any(PopulationFilters.class), eq(CountType.COUNT_OF_EVENTS)))
                .thenReturn(mockResponse);

        this.mvc.perform(post(RESOURCE_URL + "/values")
                .content(mapper.writeValueAsString(exacerbationValuesRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[{'trellisedBy': [], 'data': []}]"));
        verify(exacerbationService, times(1)).getOnsetLineChartValues(eq(DUMMY_DETECT_DATASETS), any(), any(), any(), eq(CountType.COUNT_OF_EVENTS));
    }


    @Test
    public void shouldCorrectlyGetSelection() throws Exception {
        ExacerbationOnSetLineChartSelectionRequest
                getSelectionRequest = new ExacerbationOnSetLineChartSelectionRequest();
        getSelectionRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        getSelectionRequest.setExacerbationFilters(ExacerbationFilters.empty());
        getSelectionRequest.setPopulationFilters(PopulationFilters.empty());
        getSelectionRequest.setCountType(CountType.COUNT_OF_EVENTS);
        ChartGroupByOptionsFiltered<Exacerbation, ExacerbationGroupByOptions> settings =
                new ChartGroupByOptionsFiltered<Exacerbation, ExacerbationGroupByOptions>(
                        new ChartGroupByOptions<>(new HashMap<>(), new HashSet<>()), new ArrayList<>());
        ChartSelection<Exacerbation, ExacerbationGroupByOptions,
                ChartSelectionItem<Exacerbation, ExacerbationGroupByOptions>> selection = new ChartSelection<>(settings.getSettings(),
                Collections.emptyList());
        getSelectionRequest.setSelection(selection);
        SelectionDetail selectionDetail = new SelectionDetail(new HashSet<>(), new HashSet<>(), 20, 20);
        when(exacerbationService.getOnSetLineChartSelectionDetails(any(Datasets.class),
                any(ExacerbationFilters.class),
                any(PopulationFilters.class), eq(selection), eq(CountType.COUNT_OF_EVENTS)))
                .thenReturn(selectionDetail);

        this.mvc.perform(post(RESOURCE_URL + "/selection")
                .content(mapper.writeValueAsString(getSelectionRequest))
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(content().json("{eventIds: [], subjectIds: [] , totalEvents: 20, totalSubjects: 20}"));
        verify(exacerbationService, times(1)).getOnSetLineChartSelectionDetails(eq(DUMMY_DETECT_DATASETS),
                any(), any(), any(), eq(CountType.COUNT_OF_EVENTS));
    }
}
