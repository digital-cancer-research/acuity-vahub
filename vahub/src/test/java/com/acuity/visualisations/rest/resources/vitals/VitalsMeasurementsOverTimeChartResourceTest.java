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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.*;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.acuity.visualisations.rest.model.request.vitals.VitalsRequest;
import com.acuity.visualisations.rest.model.request.vitals.VitalsSelectionRequest;
import com.acuity.visualisations.rest.model.request.vitals.VitalsTrellisRequest;
import com.acuity.visualisations.rest.model.request.vitals.VitalsValuesRequest;
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
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions.ARM;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions.MEASUREMENT;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class VitalsMeasurementsOverTimeChartResourceTest {
    private static final String RESOURCE_URL = "/resources/vitals/measurements-over-time-chart";

    private MockMvc mvc;
    @Mock
    private VitalService vitalService;
    @InjectMocks
    private VitalsMeasurementsOverTimeChartResource resource;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(resource).build();

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    @Test
    public void shouldGetXAxis() throws Exception {

        VitalsRequest request = new VitalsRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setVitalsFilters(VitalFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());

        when(vitalService.getAvailableBoxPlotXAxis(any(Datasets.class),
                any(VitalFilters.class), any(PopulationFilters.class)))
                .thenReturn(new AxisOptions<>(Lists.emptyList(), true, Lists.emptyList()));

        this.mvc.perform(post(RESOURCE_URL + "/x-axis")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasRandomization", is(true)))
                .andExpect(jsonPath("$.drugs", hasSize(0)))
                .andExpect(jsonPath("$.options", hasSize(0)));
    }

    @Test
    public void shouldGetValues() throws Exception {

        ChartGroupByOptions<Vital, VitalGroupByOptions> settings =
                ChartGroupByOptions.<Vital, VitalGroupByOptions>builder()
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS,
                                VitalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                                VitalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                        .withTrellisOption(VitalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                        .withTrellisOption(VitalGroupByOptions.ARM.getGroupByOptionAndParams())
                        .build();

        final HashMap<VitalGroupByOptions, Object> filterByTrellis = new HashMap<>();
        filterByTrellis.put(VitalGroupByOptions.MEASUREMENT, "Weight");
        filterByTrellis.put(VitalGroupByOptions.ARM, "Placebo");

        VitalsValuesRequest request = new VitalsValuesRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setVitalsFilters(VitalFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());

        request.setSettings(ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(filterByTrellis).build());

        when(vitalService.getBoxPlot(any(Datasets.class),
                any(ChartGroupByOptionsFiltered.class),
                any(VitalFilters.class),
                any(PopulationFilters.class)
        )).thenReturn(Arrays.asList(
                new TrellisedBoxPlot<>(),
                new TrellisedBoxPlot<>()
        ));

        this.mvc.perform(post(RESOURCE_URL + "/values")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void shouldGetTrellising() throws Exception {
        VitalsTrellisRequest request = new VitalsTrellisRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setVitalsFilters(VitalFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setYAxisOption(VitalGroupByOptions.PERCENTAGE_CHANGE_FROM_BASELINE);

        when(vitalService.getTrellisOptions(any(Datasets.class),
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
    public void shouldGetSelection() throws Exception {

        ChartGroupByOptions<Vital, VitalGroupByOptions> settings =
                ChartGroupByOptions.<Vital, VitalGroupByOptions>builder()
                        .withOption(X_AXIS,
                                VitalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                                VitalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                        .withTrellisOption(MEASUREMENT.getGroupByOptionAndParams())
                        .withTrellisOption(ARM.getGroupByOptionAndParams())
                        .build();

        HashMap<VitalGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "measurement1");
        selectedTrellises.put(ARM, "Placebo");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(X_AXIS, "2.0");

        final List<ChartSelectionItemRange<Vital, VitalGroupByOptions, Double>> selectionItems =
                Collections.singletonList(
                        ChartSelectionItemRange.of(selectedTrellises, selectedItems, 1.0, 2.0)
                );

        VitalsSelectionRequest request = new VitalsSelectionRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setVitalsFilters(VitalFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setSelection(ChartSelection.of(settings, selectionItems));


        when(vitalService.getRangedSelectionDetails(any(Datasets.class),
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
