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

package com.acuity.visualisations.rest.resources.respiratory.lungfunction;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.LungFunctionService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionMeanRangeSelectionRequest;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionRequest;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionTrellisRequest;
import com.acuity.visualisations.rest.model.request.respiratory.lungfunction.LungFunctionValuesRequest;
import com.acuity.va.security.acl.domain.Datasets;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.util.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions.MEASUREMENT;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = LungMeanRangeChartResource.class, secure = false)
public class LungFunctionMeanRangeChartResourceTest {

    private static final String BASE_URL = "/resources/respiratory/lung-function/mean-range-chart";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private LungFunctionService lungFunctionService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldGetXAxis() throws Exception {

        LungFunctionRequest request = new LungFunctionRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setLungFunctionFilters(LungFunctionFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());

        when(lungFunctionService.getAvailableRangePlotXAxis(any(Datasets.class),
                any(LungFunctionFilters.class), any(PopulationFilters.class)))
                .thenReturn(new AxisOptions<>(Lists.emptyList(), true, Lists.emptyList()));

        this.mvc.perform(post(BASE_URL + "/x-axis")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.hasRandomization", is(true)))
                .andExpect(jsonPath("$.drugs", hasSize(0)))
                .andExpect(jsonPath("$.options", hasSize(0)));
    }

    @Test
    public void shouldGetValues() throws Exception {

        ChartGroupByOptions<LungFunction, LungFunctionGroupByOptions> settings =
                ChartGroupByOptions.<LungFunction, LungFunctionGroupByOptions>builder()
                        .withOption(X_AXIS,
                                LungFunctionGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                                LungFunctionGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                        .withTrellisOption(MEASUREMENT.getGroupByOptionAndParams())
                        .build();

        HashMap<LungFunctionGroupByOptions, Object> filterByTrellis = new HashMap<>();
        filterByTrellis.put(MEASUREMENT, "FEV1P");

        LungFunctionValuesRequest request = new LungFunctionValuesRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setLungFunctionFilters(LungFunctionFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());

        request.setSettings(ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(filterByTrellis).build());

        when(lungFunctionService.getRangePlot(any(Datasets.class),
                any(ChartGroupByOptionsFiltered.class),
                any(LungFunctionFilters.class),
                any(PopulationFilters.class),
                any(StatType.class)
        )).thenReturn(Arrays.asList(
                new TrellisedRangePlot<>(),
                new TrellisedRangePlot<>()
        ));

        this.mvc.perform(post(BASE_URL + "/values")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void shouldGetTrellising() throws Exception {
        LungFunctionTrellisRequest request = new LungFunctionTrellisRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setLungFunctionFilters(LungFunctionFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setYAxisOption(LungFunctionGroupByOptions.ACTUAL_VALUE);

        when(lungFunctionService.getTrellisOptions(any(Datasets.class),
                any(LungFunctionFilters.class), any(PopulationFilters.class),
                any(LungFunctionGroupByOptions.class)))
                .thenReturn(Arrays.asList(
                        new TrellisOptions<>(MEASUREMENT,
                                Arrays.asList("FEV1", "FEV1P")),
                        new TrellisOptions<>(LungFunctionGroupByOptions.ARM, Collections.emptyList())
                ));

        this.mvc.perform(post(BASE_URL + "/trellising")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].trellisedBy", is("MEASUREMENT")))
                .andExpect(jsonPath("$[0].trellisOptions", hasSize(2)))
                .andExpect(jsonPath("$[0].trellisOptions", contains("FEV1", "FEV1P")))
                .andExpect(jsonPath("$[1].trellisedBy", is("ARM")));
    }


    @Test
    public void shouldGetSelection() throws Exception {

        ChartGroupByOptions<LungFunction, LungFunctionGroupByOptions> settings =
                ChartGroupByOptions.<LungFunction, LungFunctionGroupByOptions>builder()
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS,
                                LungFunctionGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                                LungFunctionGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                        .withTrellisOption(LungFunctionGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                        .build();

        HashMap<LungFunctionGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "FEV1P");

        HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(X_AXIS, "2.0");

        LungFunctionMeanRangeSelectionRequest request = new LungFunctionMeanRangeSelectionRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setLungFunctionFilters(LungFunctionFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setSelection(ChartSelection.of(settings,
                Collections.singletonList(ChartSelectionItemRange.of(selectedTrellises, selectedItems,
                        1.0, 2.0))));


        when(lungFunctionService.getSelectionDetails(any(Datasets.class),
                any(LungFunctionFilters.class),
                any(PopulationFilters.class),
                any(ChartSelection.class)
        )).thenReturn(
                new SelectionDetail(
                        Collections.singleton("event01"),
                        Collections.singleton("subject01"),
                        10,
                        1
                )
        );

        this.mvc.perform(post(BASE_URL + "/selection")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventIds[0]", equalTo("event01")))
                .andExpect(jsonPath("$.subjectIds[0]", equalTo("subject01")))
                .andExpect(jsonPath("$.totalEvents", equalTo(10)))
                .andExpect(jsonPath("$.totalSubjects", equalTo(1)));
    }

}
