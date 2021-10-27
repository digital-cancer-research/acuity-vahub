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

package com.acuity.visualisations.rest.resources.conmeds;

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.ConmedsService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ConmedGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import com.acuity.visualisations.rest.model.request.conmeds.ConmedsCountsBarChartRequest;
import com.acuity.visualisations.rest.model.request.conmeds.ConmedsRequest;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ConmedGroupByOptions.ARM;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@RunWith(SpringRunner.class)
@WebMvcTest(controllers = ConmedsCountsBarChartResource.class, secure = false)
public class ConmedsCountsBarChartResourceTest {

    private static final String BASE_URL = "/resources/conmeds/counts-bar-chart";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private ConmedsService conmedsService;

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldGetXAxis() throws Exception {

        ConmedsRequest request = new ConmedsRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setConmedsFilters(ConmedFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());

        when(conmedsService.getAvailableBarChartXAxis(any(Datasets.class),
                any(ConmedFilters.class), any(PopulationFilters.class)))
                .thenReturn(new AxisOptions<>(Lists.emptyList(), true, Lists.emptyList()));

        this.mvc.perform(post(BASE_URL + "/x-axis")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.xaxis.drugs", hasSize(0)))
                .andExpect(jsonPath("$.xaxis.options", hasSize(0)));
    }

    @Test
    public void shouldGetColoring() throws Exception {

        ConmedsRequest request = new ConmedsRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setConmedsFilters(ConmedFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());

        when(conmedsService.getBarChartColorByOptions(any(Datasets.class),
                any(ConmedFilters.class), any(PopulationFilters.class)))
                .thenReturn(new ArrayList<>());

        this.mvc.perform(post(BASE_URL + "/coloring")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void shouldGetTrellising() throws Exception {

        ConmedsRequest request = new ConmedsRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setConmedsFilters(ConmedFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());

        when(conmedsService.getTrellisOptions(any(Datasets.class),
                any(ConmedFilters.class), any(PopulationFilters.class)))
                .thenReturn(new ArrayList<>());

        this.mvc.perform(post(BASE_URL + "/trellising")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


    @Test
    public void shouldGetValues() throws Exception {

        ChartGroupByOptions<Conmed, ConmedGroupByOptions> settings =
                ChartGroupByOptions.<Conmed, ConmedGroupByOptions>builder()
                        .withOption(X_AXIS, ConmedGroupByOptions.MEDICATION_NAME.getGroupByOptionAndParams())
                        .withTrellisOption(ARM.getGroupByOptionAndParams())
                        .build();

        HashMap<ConmedGroupByOptions, Object> filterByTrellis = new HashMap<>();
        filterByTrellis.put(ARM, "Placebo");

        ConmedsCountsBarChartRequest request = new ConmedsCountsBarChartRequest();
        request.setDatasets(DUMMY_DETECT_DATASETS.getDatasetsList());
        request.setConmedsFilters(ConmedFilters.empty());
        request.setCountType(CountType.COUNT_OF_SUBJECTS);
        request.setPopulationFilters(PopulationFilters.empty());

        request.setSettings(ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(filterByTrellis).build());

        when(conmedsService.getBarChart(any(Datasets.class),
                any(ChartGroupByOptionsFiltered.class),
                any(ConmedFilters.class),
                any(PopulationFilters.class),
                any(CountType.class)
        )).thenReturn(Collections.singletonList(
                new TrellisedBarChart<>(new ArrayList<>(), new ArrayList<>())
        ));

        this.mvc.perform(post(BASE_URL + "/values")
                .content(mapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.barChartData", hasSize(1)));
    }

}
