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

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.ExacerbationService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputOvertimeData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputOvertimeLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationOverTimeLineBarChartValuesRequest;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationRequest;
import com.acuity.visualisations.rest.model.request.respiratory.exacerbation.ExacerbationSelectionRequest;
import com.acuity.visualisations.rest.model.response.respiratory.exacerbation.ExacerbationColorByOptionsResponse;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ExacerbationOverTimeLineBarChartResourceTest {

    public static final String RESOURCE_URL = "/resources/respiratory/exacerbation/over-time-line-bar-chart";

    private MockMvc mvc;
    @Mock
    @Qualifier("exacerbationService")
    private ExacerbationService exacerbationService;
    @InjectMocks
    private ExacerbationOverTimeLineBarChartResource resource;
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
        when(exacerbationService.getAvailableOverTimeChartXAxis(any(Datasets.class),
                any(ExacerbationFilters.class), any(PopulationFilters.class)))
                .thenReturn(new AxisOptions<ExacerbationGroupByOptions>(Lists.emptyList(), true, Lists.emptyList()));

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
    public void shouldCorrectlyGetColorByOptions() throws Exception {
        ExacerbationRequest axisRequest = new ExacerbationRequest();
        axisRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        axisRequest.setExacerbationFilters(ExacerbationFilters.empty());
        axisRequest.setPopulationFilters(PopulationFilters.empty());

        ExacerbationColorByOptionsResponse resultToReturn
                = new ExacerbationColorByOptionsResponse(singletonList(new TrellisOptions<>(
                ExacerbationGroupByOptions.ANTIBIOTICS_TREATMENT, asList("Y", "N"))));

        when(exacerbationService.getLineBarChartColorByOptions(any(Datasets.class), any(ExacerbationFilters.class),
                any(PopulationFilters.class))).thenReturn(resultToReturn.getTrellisOptions());

        this.mvc.perform(post(RESOURCE_URL + "/color-by-options")
                .content(mapper.writeValueAsString(axisRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.trellisOptions", hasSize(1)))
                .andExpect(jsonPath("$.trellisOptions[0].trellisedBy", is("ANTIBIOTICS_TREATMENT")))
                .andExpect(jsonPath("$.trellisOptions[0].trellisOptions", hasSize(2)))
                .andExpect(jsonPath("$.trellisOptions[0].trellisOptions", contains("Y", "N")));
    }

    @Test
    public void shouldCorrectlyGetValues() throws Exception {
        ExacerbationOverTimeLineBarChartValuesRequest valuesRequest = new ExacerbationOverTimeLineBarChartValuesRequest();
        valuesRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        valuesRequest.setExacerbationFilters(ExacerbationFilters.empty());
        valuesRequest.setPopulationFilters(PopulationFilters.empty());
        ChartGroupByOptionsFiltered<Exacerbation, ExacerbationGroupByOptions> settings =
                new ChartGroupByOptionsFiltered<Exacerbation, ExacerbationGroupByOptions>(
                        new ChartGroupByOptions<>(new HashMap<>(), new HashSet<>()), new ArrayList<>());
        valuesRequest.setSettings(settings);
        List<TrellisOption<Exacerbation, ExacerbationGroupByOptions>> trellisedBy = newArrayList(
                TrellisOption.of(ExacerbationGroupByOptions.ANTIBIOTICS_TREATMENT, "Y"));
        OutputOvertimeData data = new OutputOvertimeData(
                newArrayList(new OutputOvertimeLineChartData("line name", newArrayList(), "someColor")), newArrayList("cat1"), newArrayList());
        List<TrellisedOvertime<Exacerbation, ExacerbationGroupByOptions>> mockResponse =
                singletonList(new TrellisedOvertime<>(trellisedBy, data));
        when(exacerbationService.getLineBarChart(any(Datasets.class),
                eq(settings), any(ExacerbationFilters.class), any(PopulationFilters.class)))
                .thenReturn(mockResponse);

        this.mvc.perform(post(RESOURCE_URL + "/values")
                .content(mapper.writeValueAsString(valuesRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].trellisedBy", hasSize(1)))
                .andExpect(jsonPath("$[0].trellisedBy[0].trellisedBy", is("ANTIBIOTICS_TREATMENT")))
                .andExpect(jsonPath("$[0].trellisedBy[0].trellisOption", is("Y")))
                .andExpect(jsonPath("$[0].data.lines", hasSize(1)))
                .andExpect(jsonPath("$[0].data.lines[0].name", is("line name")))
                .andExpect(jsonPath("$[0].data.lines[0].series", hasSize(0)))
                .andExpect(jsonPath("$[0].data.lines[0].color", is("someColor")))
                .andExpect(jsonPath("$[0].data.categories", hasSize(1)))
                .andExpect(jsonPath("$[0].data.categories[0]", is("cat1")))
                .andExpect(jsonPath("$[0].data.series", hasSize(0)));
    }

    @Test
    public void  shouldCorrectlyGetSelection() throws Exception {
        final HashMap<ExacerbationGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(X_AXIS, 1);
        selectedItems.put(COLOR_BY, "GRADE 1");

        ChartGroupByOptions<Exacerbation, ExacerbationGroupByOptions> settings =
                ChartGroupByOptions.<Exacerbation, ExacerbationGroupByOptions>builder()
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS,
                                ExacerbationGroupByOptions.OVERTIME_DURATION.getGroupByOptionAndParams(GroupByOption.Params.builder()
                                        .with(GroupByOption.Param.BIN_INCL_DURATION, false)
                                        .with(GroupByOption.Param.BIN_SIZE, 1)
                                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                                        .build()))
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY,
                                ExacerbationGroupByOptions.EXACERBATION_SEVERITY.getGroupByOptionAndParams())
                        .build();

        ExacerbationSelectionRequest selectionRequest = new ExacerbationSelectionRequest();
        selectionRequest.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        selectionRequest.setExacerbationFilters(ExacerbationFilters.empty());
        selectionRequest.setPopulationFilters(PopulationFilters.empty());
        selectionRequest.setSelection(ChartSelection.of(settings,
                singletonList(ChartSelectionItem.of(selectedTrellises, selectedItems))));

        when(exacerbationService.getOverTimeLineBarChartSelectionDetails(any(Datasets.class),
                any(ExacerbationFilters.class),
                any(PopulationFilters.class),
                any(ChartSelection.class)
        )).thenReturn(
                new SelectionDetail(
                        newHashSet("event1"),
                        newHashSet("subject1"),
                        10,
                        5
                )
        );

        this.mvc.perform(post(RESOURCE_URL + "/selection")
                .content(mapper.writeValueAsString(selectionRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.eventIds[0]", is("event1")))
                .andExpect(jsonPath("$.subjectIds[0]", is("subject1")))
                .andExpect(jsonPath("$.totalEvents", equalTo(10)))
                .andExpect(jsonPath("$.totalSubjects", equalTo(5)));
    }
}
