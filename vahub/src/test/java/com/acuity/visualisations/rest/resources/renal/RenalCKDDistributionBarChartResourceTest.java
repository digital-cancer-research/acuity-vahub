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
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RenalFilters;
import com.acuity.visualisations.rawdatamodel.service.event.RenalService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import com.acuity.visualisations.rest.model.request.renal.RenalBarChartRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalBarChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.renal.RenalRequest;
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RenalCKDDistributionBarChartResourceTest {
    private static final String RESOURCE_URL = "/resources/renal/ckd-distribution-bar-chart";

    private MockMvc mvc;
    @Mock
    private RenalService renalService;
    @InjectMocks
    private RenalCKDDistributionBarChartResource resource;
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
        RenalRequest axisRequest = new RenalRequest(PopulationFilters.empty(), RenalFilters.empty());
        axisRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        when(renalService.getAvailableBarChartXAxis(any(Datasets.class), any(RenalFilters.class), any(PopulationFilters.class)))
        .thenReturn(new AxisOptions<>(Lists.emptyList(), true, Lists.emptyList()));

        this.mvc.perform(post(RESOURCE_URL + "/x-axis")
                .content(mapper.writeValueAsString(axisRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.xaxis.hasRandomization", is(true)))
                .andExpect(jsonPath("$.xaxis.drugs", hasSize(0)))
                .andExpect(jsonPath("$.xaxis.options", hasSize(0)));
    }

    @Test
    public void shouldGetAvailableTrellising() throws Exception {
        RenalRequest request = new RenalRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setRenalFilters(RenalFilters.empty());

        when(renalService.getTrellisOptions(any(Datasets.class), any(RenalFilters.class), any(PopulationFilters.class)))
                .thenReturn(Collections.singletonList(
                        new TrellisOptions<>(RenalGroupByOptions.MEASUREMENT, Collections.singletonList("ACUITY calculated crcl, egfr (ml/min)"))));

        this.mvc.perform(post(RESOURCE_URL + "/trellising")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trellisOptions", hasSize(1)))
                .andExpect(jsonPath("$.trellisOptions[0].trellisedBy", is("MEASUREMENT")))
                .andExpect(jsonPath("$.trellisOptions[0].trellisOptions", hasSize(1)))
                .andExpect(jsonPath("$.trellisOptions[0].trellisOptions", contains("ACUITY calculated crcl, egfr (ml/min)")));

        verify(renalService, times(1))
                .getTrellisOptions(eq(DUMMY_DETECT_DATASETS), any(RenalFilters.class),
                        any(PopulationFilters.class));
        verifyNoMoreInteractions(renalService);
    }

    @Test
    public void shouldGetAvailableColorByOptions() throws Exception {
        RenalRequest request = new RenalRequest();
        request.setRenalFilters(new RenalFilters());
        request.setPopulationFilters(new PopulationFilters());
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());

        when(renalService.getBarChartColorByOptions(any(Datasets.class), any(RenalFilters.class), any(PopulationFilters.class)))
                .thenReturn(Collections.singletonList(
                        new TrellisOptions<>(RenalGroupByOptions.CKD_STAGE_NAME, Collections.singletonList("CKD Stage 1"))));

        this.mvc.perform(post(RESOURCE_URL + "/color-by-options")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trellisOptions", hasSize(1)))
                .andExpect(jsonPath("$.trellisOptions[0].trellisedBy", is("CKD_STAGE_NAME")))
                .andExpect(jsonPath("$.trellisOptions[0].trellisOptions", hasSize(1)))
                .andExpect(jsonPath("$.trellisOptions[0].trellisOptions", contains("CKD Stage 1")));


        verify(renalService, times(1))
                .getBarChartColorByOptions(eq(DUMMY_DETECT_DATASETS), any(RenalFilters.class), any(PopulationFilters.class));
        verifyNoMoreInteractions(renalService);
    }

    @Test
    public void shouldGetValuesForBarChart() throws Exception {

        final ChartGroupByOptions<Renal, RenalGroupByOptions> settings = ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, RenalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, RenalGroupByOptions.CKD_STAGE_NAME.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();
        final ChartGroupByOptionsFiltered<Renal, RenalGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .build();

        RenalBarChartRequest request = new RenalBarChartRequest();
        request.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());
        request.setSettings(settingsFiltered);
        request.setCountType(CountType.PERCENTAGE_OF_SUBJECTS_100_STACKED);
        request.setRenalFilters(new RenalFilters());
        request.setPopulationFilters(new PopulationFilters());

        final OutputBarChartData ckd1 = new OutputBarChartData("CKD Stage 1", null, Collections.singletonList(
                new OutputBarChartEntry("2", 21, 22.22, null)));
        final List<TrellisOption<Renal, RenalGroupByOptions>> trellisOptions = asList(
                TrellisOption.of(RenalGroupByOptions.MEASUREMENT, Collections.singletonList("ACUITY calculated crcl, egfr (ml/min)")),
                TrellisOption.of(RenalGroupByOptions.ARM, Collections.singletonList("arm1")));
        final List<TrellisedBarChart<Renal, RenalGroupByOptions>> mockResponse =
                Collections.singletonList(new TrellisedBarChart<>(trellisOptions, Collections.singletonList(ckd1)));

        when(renalService.getBarChart(
                any(Datasets.class), any(), any(RenalFilters.class),
                any(PopulationFilters.class), any(CountType.class))).thenReturn(mockResponse);

        this.mvc.perform(post(RESOURCE_URL + "/values")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.barChartData[0].trellisedBy", hasSize(2)))
                .andExpect(jsonPath("$.barChartData[0].trellisedBy[0].trellisedBy", is("MEASUREMENT")))
                .andExpect(jsonPath("$.barChartData[0].trellisedBy[0].trellisOption", hasSize(1)))
                .andExpect(jsonPath("$.barChartData[0].trellisedBy[0].trellisOption", contains("ACUITY calculated crcl, egfr (ml/min)")))
                .andExpect(jsonPath("$.barChartData[0].trellisedBy[1].trellisedBy", is("ARM")))
                .andExpect(jsonPath("$.barChartData[0].trellisedBy[1].trellisOption", hasSize(1)))
                .andExpect(jsonPath("$.barChartData[0].trellisedBy[1].trellisOption", contains("arm1")))
                .andExpect(jsonPath("$.barChartData[0].data", hasSize(1)))
                .andExpect(jsonPath("$.barChartData[0].data[0].name", is("CKD Stage 1")))
                .andExpect(jsonPath("$.barChartData[0].data[0].series", hasSize(1)))
                .andExpect(jsonPath("$.barChartData[0].data[0].series[0].category", is("2")))
                .andExpect(jsonPath("$.barChartData[0].data[0].series[0].rank", is(21)))
                .andExpect(jsonPath("$.barChartData[0].data[0].series[0].value", is(22.22)));

        verify(renalService, times(1)).getBarChart(
                any(Datasets.class), any(), any(RenalFilters.class), any(PopulationFilters.class), any(CountType.class));
        verifyNoMoreInteractions(renalService);
    }

    @Test
    public void shouldGetSelection() throws Exception {
        final ChartGroupByOptions<Renal, RenalGroupByOptions> settings = ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, RenalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, RenalGroupByOptions.CKD_STAGE_NAME.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();

        final HashMap<RenalGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(RenalGroupByOptions.MEASUREMENT, "measurement1");
        selectedTrellises.put(RenalGroupByOptions.ARM, "Placebo");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(X_AXIS, "2.0");
        selectedItems.put(COLOR_BY, "CKD Stage 1");

        final Collection<ChartSelectionItem<Renal, RenalGroupByOptions>> selectionItems =
                Collections.singletonList(ChartSelectionItem.of(selectedTrellises, selectedItems));

        RenalBarChartSelectionRequest request = new RenalBarChartSelectionRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setRenalFilters(RenalFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setSelection(ChartSelection.of(settings, selectionItems));

        when(renalService.getBarChartSelectionDetails(any(Datasets.class), any(RenalFilters.class),
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
