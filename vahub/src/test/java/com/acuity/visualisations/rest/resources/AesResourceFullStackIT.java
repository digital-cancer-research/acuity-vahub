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

package com.acuity.visualisations.rest.resources;

import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.visualisations.rest.model.request.aes.AesBarChartRequest;
import com.acuity.visualisations.rest.model.request.aes.AesBarChartSelectionRequest;
import com.acuity.visualisations.rest.model.request.aes.AesRequest;
import com.acuity.visualisations.rest.test.annotation.FullStackITSpringApplicationConfiguration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.jayway.restassured.RestAssured.given;

@RunWith(SpringJUnit4ClassRunner.class)
@FullStackITSpringApplicationConfiguration
public class AesResourceFullStackIT {

    private static final TimeZone TIME_ZONE = TimeZone.getTimeZone(DaysUtil.GMT_TIMEZONE);

    @Value("${local.server.port}")
    private int port;

    private static ObjectMapper mapper;
        
    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        final SimpleDateFormat dateFormat = new SimpleDateFormat(DaysUtil.JSON_TIMESTAMP_FORMAT);
        dateFormat.setTimeZone(TIME_ZONE);
        mapper.setDateFormat(dateFormat);
        mapper.setTimeZone(TIME_ZONE);
    }

    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetTrellisingOptionAsActualValue() throws Exception {

        AesRequest request = new AesRequest();
        request.setAesFilters(AeFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        String url = "/resources/aes/trellising";

        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(request))
                .when()
                .post(url)
                .asString();
        System.out.println(jsonResponse);

        List<TrellisOptions<AeGroupByOptions>> returnedResult = mapper.readValue(jsonResponse, new TypeReference<List<TrellisOptions<AeGroupByOptions>>>() {
        });

        softly.assertThat(returnedResult).hasSize(0);
    }


    @Test
    public void shouldGetBarchartData() throws Exception {

        AesBarChartRequest request = new AesBarChartRequest();
        final ChartGroupByOptions<Ae, AeGroupByOptions> settings = ChartGroupByOptions.<Ae, AeGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, AeGroupByOptions.HLT.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams())
                .withTrellisOption(AeGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();
        request.setSettings(ChartGroupByOptionsFiltered.builder(settings).build());
        request.setCountType(CountType.COUNT_OF_SUBJECTS);
        request.setAesFilters(AeFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        String url = "/resources/aes/countsbarchart";

        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(request))
                .when()
                .post(url)
                .asString();
        System.out.println(jsonResponse);

        List<TrellisedBarChart<Ae, AeGroupByOptions>> returnedResult = mapper.readValue(jsonResponse,
                new TypeReference<List<TrellisedBarChart<Ae, AeGroupByOptions>>>() {
        });

        softly.assertThat(returnedResult).hasSize(1);
    }


    @Test
    public void shouldGetBarchartSelection() throws Exception {

        AesBarChartSelectionRequest request = new AesBarChartSelectionRequest();
        final ChartGroupByOptions<Ae, AeGroupByOptions> settings = ChartGroupByOptions.<Ae, AeGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, AeGroupByOptions.PT.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams())
                .build();


        final HashMap<AeGroupByOptions, Object> selectedTrellises = new HashMap<>();

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "DIARRHOEA");
        selectedItems1.put(COLOR_BY, "CTC Grade 1");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(X_AXIS, "ALOPECIA");
        selectedItems2.put(COLOR_BY, "CTC Grade 2");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems3 = new HashMap<>();
        selectedItems3.put(X_AXIS, "RASH");
        selectedItems3.put(COLOR_BY, "CTC Grade 3");


        final List<ChartSelectionItem<Ae, AeGroupByOptions>> selectionItems = Arrays.asList(
                ChartSelectionItem.of(selectedTrellises, selectedItems1),
                ChartSelectionItem.of(selectedTrellises, selectedItems2),
                ChartSelectionItem.of(selectedTrellises, selectedItems3)
        );

        request.setSelection(ChartSelection.of(settings, selectionItems));
        request.setAesFilters(AeFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        String url = "/resources/aes/selection";

        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(request))
                .when()
                .post(url)
                .asString();
        System.out.println(jsonResponse);

        SelectionDetail returnedResult = mapper.readValue(jsonResponse, new TypeReference<SelectionDetail>() {
        });

        softly.assertThat(returnedResult.getEventIds()).hasSize(186);
        softly.assertThat(returnedResult.getSubjectIds()).hasSize(75);
        softly.assertThat(returnedResult.getTotalEvents()).isEqualTo(2076);
        softly.assertThat(returnedResult.getTotalSubjects()).isEqualTo(124);
    }

    @Test
    public void shouldGetColorByOptionAsActualValue() throws Exception {

        AesRequest request = new AesRequest();
        request.setAesFilters(AeFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        String url = "/resources/aes/colorby-options";

        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(request))
                .when()
                .post(url)
                .asString();
        System.out.println(jsonResponse);

        List<TrellisOptions<AeGroupByOptions>> returnedResult = mapper.readValue(jsonResponse, new TypeReference<List<TrellisOptions<AeGroupByOptions>>>() {
        });

        softly.assertThat(returnedResult).hasSize(1);
        softly.assertThat(returnedResult.get(0).getTrellisedBy()).isEqualTo(AeGroupByOptions.MAX_SEVERITY_GRADE);
        softly.assertThat(returnedResult.get(0).getTrellisOptions()).hasSize(6);
    }

    @Test
    public void shouldGetFilters() throws Exception {

        AesRequest request = new AesRequest();
        request.setAesFilters(AeFilters.empty());
        request.setPopulationFilters(PopulationFilters.empty());
        request.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        String url = "/resources/aes/filters";

        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(request))
                .when()
                .post(url)
                .asString();
        System.out.println(jsonResponse);

        AeFilters returnedResult = mapper.readValue(jsonResponse, new TypeReference<AeFilters>() {
        });

        final ZonedDateTime from = LocalDateTime.ofInstant(returnedResult.getStartDate().getFrom().toInstant(),
                ZoneId.of(DaysUtil.GMT_TIMEZONE)).atZone(ZoneId.of(DaysUtil.GMT_TIMEZONE));
        final ZonedDateTime to = LocalDateTime.ofInstant(returnedResult.getStartDate().getTo().toInstant(),
                ZoneId.of(DaysUtil.GMT_TIMEZONE)).atZone(ZoneId.of(DaysUtil.GMT_TIMEZONE));
        softly.assertThat(from.getYear()).isEqualTo(2014);
        softly.assertThat(from.getMonthValue()).isEqualTo(7);
        softly.assertThat(from.getDayOfMonth()).isEqualTo(26);
        softly.assertThat(to.getYear()).isEqualTo(2016);
        softly.assertThat(to.getMonthValue()).isEqualTo(10);
        softly.assertThat(to.getDayOfMonth()).isEqualTo(27);
    }
}
