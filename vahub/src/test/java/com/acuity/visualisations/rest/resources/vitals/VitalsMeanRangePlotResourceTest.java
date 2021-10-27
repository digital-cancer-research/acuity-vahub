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

package com.acuity.visualisations.rest.resources.vitals;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.visualisations.rawdatamodel.service.event.VitalService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.acuity.visualisations.rest.model.request.vitals.VitalsMeanRangeSelectionRequest;
import com.acuity.visualisations.rest.model.request.vitals.VitalsMeanRangeValuesRequest;
import com.acuity.visualisations.rest.model.request.vitals.VitalsRequest;
import com.acuity.visualisations.rest.model.request.vitals.VitalsTrellisRequest;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions.ARM;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions.MEASUREMENT;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class VitalsMeanRangePlotResourceTest {
    private static final String RESOURCE_URL = "/resources/vitals/mean-range-plot";

    @Mock
    private VitalService mockVitalService;

    @InjectMocks
    private VitalsMeanRangePlotResource resource;

    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(resource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldCorrectlyGetXAxis() throws Exception {
        VitalsRequest axisRequest = new VitalsRequest();
        axisRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        axisRequest.setVitalsFilters(VitalFilters.empty());
        axisRequest.setPopulationFilters(PopulationFilters.empty());
        when(mockVitalService.getAvailableRangePlotXAxis(any(Datasets.class),
                any(VitalFilters.class), any(PopulationFilters.class)))
                .thenReturn(new AxisOptions<>(Lists.emptyList(), true, Lists.emptyList()));

        this.mvc.perform(post(RESOURCE_URL + "/x-axis")
                .content(mapper.writeValueAsString(axisRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.hasRandomization", is(true)))
                .andExpect(jsonPath("$.drugs", hasSize(0)))
                .andExpect(jsonPath("$.options", hasSize(0)));
    }

    @Test
    public void shouldGetTrellising() throws Exception {
        VitalsTrellisRequest request = new VitalsTrellisRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setVitalsFilters(VitalFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setYAxisOption(VitalGroupByOptions.PERCENTAGE_CHANGE_FROM_BASELINE);

        when(mockVitalService.getMeanRangeTrellisOptions(any(Datasets.class),
                any(VitalFilters.class), any(PopulationFilters.class),
                any(VitalGroupByOptions.class)))
                .thenReturn(Collections.singletonList(
                        new TrellisOptions<>(MEASUREMENT,
                                Collections.singletonList("Pulse rate (% change)"))
                ));

        this.mvc.perform(post(RESOURCE_URL + "/trellising")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].trellisedBy", is("MEASUREMENT")))
                .andExpect(jsonPath("$[0].trellisOptions", hasSize(1)))
                .andExpect(jsonPath("$[0].trellisOptions", contains("Pulse rate (% change)")));
    }

    @Test
    public void shouldCorrectlyGetMeanRangePlotValues() throws Exception {
        ChartGroupByOptions<Vital, VitalGroupByOptions> settings =
                ChartGroupByOptions.<Vital, VitalGroupByOptions>builder()
                        .withOption(X_AXIS,
                                VitalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                                VitalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                        .withTrellisOption(MEASUREMENT.getGroupByOptionAndParams())
                        .build();

        HashMap<VitalGroupByOptions, Object> filterByTrellis = new HashMap<>();
        filterByTrellis.put(MEASUREMENT, "Weight");
        filterByTrellis.put(ARM, "Placebo");

        VitalsMeanRangeValuesRequest request = new VitalsMeanRangeValuesRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setVitalsFilters(VitalFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());

        request.setSettings(ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(filterByTrellis).build());

        when(mockVitalService.getRangePlot(any(Datasets.class),
                any(ChartGroupByOptionsFiltered.class),
                any(VitalFilters.class),
                any(PopulationFilters.class),
                any(StatType.class)
        )).thenReturn(Arrays.asList(
                new TrellisedRangePlot<>(),
                new TrellisedRangePlot<>()
        ));

        this.mvc.perform(post(RESOURCE_URL + "/values")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void shouldGetSelection() throws Exception {

        ChartGroupByOptions<Vital, VitalGroupByOptions> settings =
                ChartGroupByOptions.<Vital, VitalGroupByOptions>builder()
                        .withOption(X_AXIS,
                                VitalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                                VitalGroupByOptions.PERCENTAGE_CHANGE_FROM_BASELINE.getGroupByOptionAndParams())
                        .withTrellisOption(VitalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                        .build();

        HashMap<VitalGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "measurement1");
        selectedTrellises.put(ARM, "Placebo");

        HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(X_AXIS, "10.0");

        VitalsMeanRangeSelectionRequest request = new VitalsMeanRangeSelectionRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setVitalsFilters(VitalFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setSelection(ChartSelection.of(settings,
                Collections.singletonList(ChartSelectionItem.of(selectedTrellises, selectedItems))));

        when(mockVitalService.getSelectionDetails(any(Datasets.class),
                any(VitalFilters.class),
                any(PopulationFilters.class),
                any(ChartSelection.class)
        )).thenReturn(
                new SelectionDetail(
                        Collections.singleton("event1"),
                        Collections.singleton("subject1"),
                        10,
                        1
                )
        );

        this.mvc.perform(post(RESOURCE_URL + "/selection")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventIds[0]", equalTo("event1")))
                .andExpect(jsonPath("$.subjectIds[0]", equalTo("subject1")))
                .andExpect(jsonPath("$.totalEvents", equalTo(10)))
                .andExpect(jsonPath("$.totalSubjects", equalTo(1)));
    }
}
