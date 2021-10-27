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

package com.acuity.visualisations.rest.resources.renal;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RenalFilters;
import com.acuity.visualisations.rawdatamodel.service.event.RenalService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import com.acuity.visualisations.rest.model.request.renal.RenalPlotValuesRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalSelectionRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalTrellisRequest;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = RenalCreatinineClearanceResource.class, secure = false)
public class RenalCreatinineClearanceResourceTest {

    private static final String RESOURCE_URL = "/resources/renal/creatinine-clearance-box-plot";
    @MockBean
    private RenalService mockRenalService;

    @Autowired
    private MockMvc mvc;
    private static ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetAvailableXAxisOptions() throws Exception {
        RenalRequest request = new RenalRequest(PopulationFilters.empty(), RenalFilters.empty());
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(mockRenalService.getAvailableBoxPlotXAxis(any(Datasets.class),
                any(RenalFilters.class),
                any(PopulationFilters.class)
        )).thenReturn(new AxisOptions<>(Lists.emptyList(), true, Lists.emptyList()));

        this.mvc.perform(post(RESOURCE_URL + "/x-axis")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.xaxis.hasRandomization", is(true)))
                .andExpect(jsonPath("$.xaxis.drugs", hasSize(0)))
                .andExpect(jsonPath("$.xaxis.options", hasSize(0)));

        verify(mockRenalService, times(1))
                .getAvailableBoxPlotXAxis(eq(DUMMY_DETECT_DATASETS), any(RenalFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(mockRenalService);
    }

    @Test
    public void shouldGetAvailableTrellising() throws Exception {
        RenalTrellisRequest request = new RenalTrellisRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setRenalFilters(RenalFilters.empty());
        request.setYAxisOption(RenalGroupByOptions.ACTUAL_VALUE);

        when(mockRenalService.getTrellisOptions(any(Datasets.class), any(RenalFilters.class), any(PopulationFilters.class),
                any(RenalGroupByOptions.class)))
                .thenReturn(Collections.singletonList(
                        new TrellisOptions<>(RenalGroupByOptions.MEASUREMENT, Collections.singletonList("Pulse rate (% change)"))));

        this.mvc.perform(post(RESOURCE_URL + "/trellising")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trellisOptions", hasSize(1)))
                .andExpect(jsonPath("$.trellisOptions[0].trellisedBy", is("MEASUREMENT")))
                .andExpect(jsonPath("$.trellisOptions[0].trellisOptions", hasSize(1)))
                .andExpect(jsonPath("$.trellisOptions[0].trellisOptions", contains("Pulse rate (% change)")));

        verify(mockRenalService, times(1))
                .getTrellisOptions(eq(DUMMY_DETECT_DATASETS), any(RenalFilters.class),
                        any(PopulationFilters.class), any(RenalGroupByOptions.class));
        verifyNoMoreInteractions(mockRenalService);
    }

    @Test
    public void shouldGetBoxPlot() throws Exception {
        ChartGroupByOptions<Renal, RenalGroupByOptions> settings =
                ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS,
                                RenalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                                RenalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                        .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                        .withTrellisOption(RenalGroupByOptions.ARM.getGroupByOptionAndParams())
                        .build();

        final HashMap<RenalGroupByOptions, Object> filterByTrellis = new HashMap<>();
        filterByTrellis.put(RenalGroupByOptions.MEASUREMENT, "Weight");
        filterByTrellis.put(RenalGroupByOptions.ARM, "Placebo");

        RenalPlotValuesRequest requestBody = new RenalPlotValuesRequest();
        requestBody.setRenalFilters(RenalFilters.empty());
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());
        requestBody.setSettings(ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(filterByTrellis).build());

        when(mockRenalService.getBoxPlot(any(Datasets.class), any(ChartGroupByOptionsFiltered.class),
                any(RenalFilters.class), any(PopulationFilters.class)))
                .thenReturn(Arrays.asList(new TrellisedBoxPlot<>(), new TrellisedBoxPlot<>()));

        this.mvc.perform(post(RESOURCE_URL + "/boxplot")
                .content(mapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.boxPlotData", hasSize(2)));

        verify(mockRenalService, times(1)).getBoxPlot(any(Datasets.class),
                any(ChartGroupByOptionsFiltered.class), any(RenalFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(mockRenalService);
    }

    @Test
    public void shouldGetSelection() throws Exception {
        ChartGroupByOptions<Renal, RenalGroupByOptions> settings = ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
                .withOption(X_AXIS, RenalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                        RenalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();

        HashMap<RenalGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(RenalGroupByOptions.MEASUREMENT, "measurement1");
        selectedTrellises.put(RenalGroupByOptions.ARM, "Placebo");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(X_AXIS, "2.0");

        final Collection<ChartSelectionItemRange<Renal, RenalGroupByOptions, Double>> selectionItems =
                Collections.singletonList(ChartSelectionItemRange.of(selectedTrellises, selectedItems, 1.0, 2.0));

        RenalSelectionRequest request = new RenalSelectionRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setRenalFilters(RenalFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setSelection(ChartSelection.of(settings, selectionItems));


        when(mockRenalService.getRangedSelectionDetails(any(Datasets.class), any(RenalFilters.class),
                any(PopulationFilters.class), any(ChartSelection.class)))
                .thenReturn(new SelectionDetail(Collections.singleton("event1"), Collections.singleton("subject1"), 10, 1));

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
