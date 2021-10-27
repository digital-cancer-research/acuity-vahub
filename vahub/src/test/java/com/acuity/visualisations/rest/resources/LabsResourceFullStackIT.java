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

import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBoxPlotOutlier;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedShiftPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.visualisations.rest.model.request.labs.LabMeanRangeSelectionRequest;
import com.acuity.visualisations.rest.model.request.labs.LabSelectionRequest;
import com.acuity.visualisations.rest.model.request.labs.LabStatsRequest;
import com.acuity.visualisations.rest.model.request.labs.LabsRequest;
import com.acuity.visualisations.rest.model.request.labs.LabsTrellisRequest;
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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions.MEASUREMENT;
import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.groups.Tuple.tuple;

@RunWith(SpringJUnit4ClassRunner.class)
@FullStackITSpringApplicationConfiguration
public class LabsResourceFullStackIT {

    private static ObjectMapper mapper;
    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Value("${local.server.port}")
    private int port;

    @Before
    public void setUp() throws Exception {
        RestAssured.port = port;

        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Test
    public void shouldGetBoxPlotData1() throws IOException {

        // Given
        LabStatsRequest requestBody = new LabStatsRequest();
        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.MEASUREMENT_TIME_POINT.getGroupByOptionAndParams(
                        GroupByOption.Params.builder().with(GroupByOption.Param.TIMESTAMP_TYPE, "WEEKS_SINCE_FIRST_DOSE").build()))
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();
        final HashMap<LabGroupByOptions, Object> filterByTrellis1 = new HashMap<>();
        filterByTrellis1.put(LabGroupByOptions.MEASUREMENT, "Alanine Aminotransferase (IU/L)");
        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(filterByTrellis1)
                .build();

        requestBody.setSettings(settingsFiltered);
        requestBody.setLabsFilters(LabFilters.empty());
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setAge(new RangeFilter<>(52, 60));
        requestBody.setPopulationFilters(populationFilters);
        requestBody.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        // When
        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(requestBody))
                .when()
                .post("/resources/labs/boxplot")
                .asString();

        List<TrellisedBoxPlot<Lab, LabGroupByOptions>> result = mapper.readValue(jsonResponse,
                new TypeReference<List<TrellisedBoxPlot<Lab, LabGroupByOptions>>>() {
                });

        // Then
        softly.assertThat(result).hasSize(1);
        softly.assertThat(result.get(0).getStats()).extracting("x", "upperWhisker", "upperQuartile", "median", "lowerQuartile", "lowerWhisker")
                .containsExactly(
                        tuple("-2", 9.0, 9.0, 9.0, 9.0, 9.0),
                        tuple("-1", 130.0, 77.5, 25.0, 23.5, 22.0),
                        tuple("0", 26.0, 54.25, 26.0, 23.75, 17.0),
                        tuple("1", 35.0, 52.75, 33.5, 27.5, 14.0),
                        tuple("2", 19.0, 19.0, 19.0, 19.0, 19.0),
                        tuple("3", 66.0, 43.5, 21.0, 20.5, 20.0),
                        tuple("4", 23.0, 36.5, 22.0, 20.25, 18.0),
                        tuple("5", 65.0, 60.0, 28.0, 22.0, 16.0),
                        tuple("7", 33.0, 40.75, 31.0, 26.25, 18.0),
                        tuple("8", 67.0, 44.5, 22.0, 20.0, 18.0),
                        tuple("9", 62.0, 40.0, 18.0, 17.5, 17.0),
                        tuple("11", 72.0, 53.0, 34.0, 23.0, 12.0),
                        tuple("12", 70.0, 57.75, 45.5, 33.25, 21.0),
                        tuple("13", 18.0, 30.0, 17.5, 15.5, 11.0),
                        tuple("15", 55.0, 37.5, 20.0, 18.5, 17.0),
                        tuple("16", 55.0, 46.25, 37.5, 28.75, 20.0),
                        tuple("17", 59.0, 39.0, 19.0, 18.5, 18.0),
                        tuple("18", 14.0, 14.0, 14.0, 14.0, 14.0),
                        tuple("19", 46.0, 40.0, 34.0, 28.0, 22.0),
                        tuple("20", 55.0, 46.5, 38.0, 29.5, 21.0),
                        tuple("21", 60.0, 49.25, 38.5, 27.75, 17.0),
                        tuple("23", 44.0, 38.25, 32.5, 26.75, 21.0),
                        tuple("24", 18.0, 18.0, 18.0, 18.0, 18.0),
                        tuple("25", 19.0, 19.0, 19.0, 19.0, 19.0),
                        tuple("27", 21.0, 21.0, 21.0, 21.0, 21.0),
                        tuple("31", 39.0, 39.0, 39.0, 39.0, 39.0),
                        tuple("35", 140.0, 140.0, 140.0, 140.0, 140.0)
                );
        softly.assertThat(result.get(0).getStats()).flatExtracting(s -> s.getOutliers()).extracting(o -> o.getSubjectId(), o -> o.getOutlierValue())
                .containsExactlyInAnyOrder(
                        tuple("5322e3c585904097b6eba4a32ea78970", 66.0),
                        tuple("5322e3c585904097b6eba4a32ea78970", 139.0),
                        tuple("5322e3c585904097b6eba4a32ea78970", 106.0),
                        tuple("5322e3c585904097b6eba4a32ea78970", 77.0),
                        tuple("5322e3c585904097b6eba4a32ea78970", 64.0)
                );
    }

    @Test
    public void shouldGetBoxPlotData2() throws IOException {

        // Given
        LabStatsRequest requestBody = new LabStatsRequest();
        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();
        final HashMap<LabGroupByOptions, Object> filterByTrellis1 = new HashMap<>();
        filterByTrellis1.put(LabGroupByOptions.MEASUREMENT, "Alanine Aminotransferase (IU/L)");
        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(filterByTrellis1)
                .build();

        requestBody.setSettings(settingsFiltered);
        requestBody.setLabsFilters(LabFilters.empty());
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        // When
        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(requestBody))
                .when()
                .post("/resources/labs/boxplot")
                .asString();

        List<TrellisedBoxPlot<Lab, LabGroupByOptions>> result = mapper.readValue(jsonResponse,
                new TypeReference<List<TrellisedBoxPlot<Lab, LabGroupByOptions>>>() {
                });

        // Then
        softly.assertThat(result).hasSize(1);
        softly.assertThat(result.get(0).getStats()).hasSize(68);
        softly.assertThat(result.get(0).getStats()).flatExtracting(s -> s.getOutliers()).extracting(o -> o.getSubjectId(), o -> o.getOutlierValue())
                .containsExactlyInAnyOrder(
                        tuple("007203b4305a4c94b9d8c789c0f256d9", 112.0),
                        tuple("5322e3c585904097b6eba4a32ea78970", 130.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 78.0),
                        tuple("007203b4305a4c94b9d8c789c0f256d9", 133.0),
                        tuple("5322e3c585904097b6eba4a32ea78970", 139.0),
                        tuple("5322e3c585904097b6eba4a32ea78970", 106.0),
                        tuple("007203b4305a4c94b9d8c789c0f256d9", 164.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 96.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 102.0),
                        tuple("5322e3c585904097b6eba4a32ea78970", 77.0),
                        tuple("c7cbf94ff3b54e7cbad2becfe40a7368", 92.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 112.0),
                        tuple("5322e3c585904097b6eba4a32ea78970", 64.0),
                        tuple("5322e3c585904097b6eba4a32ea78970", 67.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 75.0),
                        tuple("5322e3c585904097b6eba4a32ea78970", 72.0),
                        tuple("5322e3c585904097b6eba4a32ea78970", 70.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 60.0),
                        tuple("5322e3c585904097b6eba4a32ea78970", 66.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 66.0),
                        tuple("5322e3c585904097b6eba4a32ea78970", 55.0),
                        tuple("8c4ac8e70edd4da59d0c970af168b8c7", 76.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 61.0),
                        tuple("8c4ac8e70edd4da59d0c970af168b8c7", 54.0),
                        tuple("8c4ac8e70edd4da59d0c970af168b8c7", 91.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 62.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 35.0),
                        tuple("007203b4305a4c94b9d8c789c0f256d9", 16.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 75.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 53.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 41.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 46.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 48.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 61.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 48.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 37.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 29.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 55.0),
                        tuple("8c4ac8e70edd4da59d0c970af168b8c7", 74.0),
                        tuple("08b918ad4e654964b79639b34c72bd7c", 140.0),
                        tuple("c7cbf94ff3b54e7cbad2becfe40a7368", 75.0),
                        tuple("5435ce8dff424648bb7c5243660240fc", 35.0)
                );
    }

    @Test
    public void shouldGetBoxPlotDataBinned() throws IOException {

        // Given
        LabStatsRequest requestBody = new LabStatsRequest();
        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.MEASUREMENT_TIME_POINT.getGroupByOptionAndParams(
                        GroupByOption.Params.builder()
                                .with(GroupByOption.Param.BIN_SIZE, 10)
                                .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                                .build()
                ))
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();
        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(LabGroupByOptions.MEASUREMENT, "Alanine Aminotransferase (IU/L)")
                .build();

        requestBody.setSettings(settingsFiltered);
        final LabFilters labsFilters = new LabFilters();
        labsFilters.setDaysOnStudy(new RangeFilter<>(10, 99));
        requestBody.setLabsFilters(labsFilters);
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        // When
        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(requestBody))
                .when()
                .post("/resources/labs/boxplot")
                .asString();

        List<TrellisedBoxPlot<Lab, LabGroupByOptions>> result = mapper.readValue(jsonResponse,
                new TypeReference<List<TrellisedBoxPlot<Lab, LabGroupByOptions>>>() {
                });

        // Then
        softly.assertThat(result).hasSize(1);
        softly.assertThat(result.get(0).getStats()).extracting("x", "upperWhisker", "upperQuartile", "median", "lowerQuartile", "lowerWhisker")
                .containsExactly(
                        tuple("10 - 19", 60.0, 49.0, 31.0, 21.0, 11.0),
                        tuple("20 - 29", 55.0, 35.5, 23.0, 20.5, 9.0),
                        tuple("30 - 39", 46.0, 34.5, 29.0, 19.5, 11.0),
                        tuple("40 - 49", 75.0, 45.5, 29.0, 19.0, 8.0),
                        tuple("50 - 59", 48.0, 34.0, 29.0, 18.0, 6.0),
                        tuple("60 - 69", 67.0, 38.5, 28.0, 19.25, 6.0),
                        tuple("70 - 79", 41.0, 35.0, 28.0, 14.0, 6.0),
                        tuple("80 - 89", 45.0, 37.0, 31.0, 22.0, 12.0),
                        tuple("90 - 99", 40.0, 32.0, 23.0, 17.0, 9.0)
                );
        softly.assertThat(result.get(0).getStats()).flatExtracting(s -> s.getOutliers() == null ? Collections.emptyList() : s.getOutliers())
                .extracting(OutputBoxPlotOutlier::getOutlierValue)
                .containsExactlyInAnyOrder(
                        106.0, 164.0, 197.0, 96.0, 66.0, 80.0, 92.0, 102.0, 77.0, 112.0, 75.0, 64.0, 75.0, 70.0, 65.0, 60.0, 72.0, 66.0, 60.0
                );
    }

    @Test
    public void shouldGetBoxPlotAxisOptions() throws IOException {

        // When
        LabsRequest requestBody = new LabsRequest();
        requestBody.setLabsFilters(LabFilters.empty());
        requestBody.setPopulationFilters(PopulationFilters.empty());
        requestBody.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());
        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(requestBody))
                .when()
                .post("/resources/labs/boxplot-xaxis")
                .asString();

        System.out.println(jsonResponse);
        AxisOptions<LabGroupByOptions> result = mapper.readValue(jsonResponse,
                new TypeReference<AxisOptions<LabGroupByOptions>>() {
                });

        // Then
        softly.assertThat(result.getOptions()).hasSize(2);

    }

    @Test
    public void shouldGetTrellisingOptionAsActualValue() throws Exception {
        LabsTrellisRequest labsRequest = new LabsTrellisRequest();
        labsRequest.setLabsFilters(new LabFilters());
        labsRequest.setPopulationFilters(new PopulationFilters());
        labsRequest.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());
        labsRequest.setYAxisOption(LabGroupByOptions.ACTUAL_VALUE);

        String url = "/resources/labs/trellising";
        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(labsRequest))
                .when()
                .post(url)
                .asString();
        System.out.println(jsonResponse);

        List<TrellisOptions<LabGroupByOptions>> returnedResult = mapper.readValue(jsonResponse, new TypeReference<List<TrellisOptions<LabGroupByOptions>>>() {
        });

        softly.assertThat(returnedResult).hasSize(1);
        softly.assertThat(returnedResult).extracting(TrellisOptions::getTrellisedBy)
                .containsExactlyInAnyOrder(LabGroupByOptions.MEASUREMENT);
    }

    @Test
    public void shouldGetFilters() throws Exception {
        LabFilters labsFilters = new LabFilters();
        com.acuity.visualisations.rawdatamodel.filters.PopulationFilters populationFilters
                = new com.acuity.visualisations.rawdatamodel.filters.PopulationFilters();

        LabsRequest requestBody = new LabsRequest();
        requestBody.setLabsFilters(labsFilters);
        requestBody.setPopulationFilters(populationFilters);
        requestBody.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        String url = "/resources/labs/filters";

        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(requestBody))
                .when()
                .post(url)
                .asString();
        System.out.println(jsonResponse);

        LabFilters returnedResult = mapper.readValue(jsonResponse, LabFilters.class);

        softly.assertThat(returnedResult.getLabValue().getFrom()).isEqualTo(0);
        softly.assertThat(returnedResult.getLabValue().getTo()).isEqualTo(7154);
    }

    @Test
    public void shouldGetShiftPlotValues() throws Exception {
        LabFilters labsFilters = new LabFilters();
        PopulationFilters populationFilters = new PopulationFilters();

        LabStatsRequest labsRequest = new LabStatsRequest();
        labsRequest.setLabsFilters(labsFilters);
        labsRequest.setPopulationFilters(populationFilters);

        labsRequest.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered
                .<Lab, LabGroupByOptions>builder(new ChartGroupByOptions<>(Collections.emptyMap(), Collections.emptySet()))
                .withFilterByTrellisOption(LabGroupByOptions.MEASUREMENT, "Gamma-Glutamyltransferase (U/L)")
                .build();

        labsRequest.setSettings(settingsFiltered);

        String url = "/resources/labs/shift-plot";

        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(labsRequest))
                .when()
                .post(url)
                .asString();
        System.out.println(jsonResponse);

        List<TrellisedShiftPlot<Lab, LabGroupByOptions>> returnedResult =
                mapper.readValue(jsonResponse, new TypeReference<List<TrellisedShiftPlot<Lab, LabGroupByOptions>>>() {
                });

        softly.assertThat(returnedResult).hasSize(1);
    }

    @Test
    public void shouldGetShiftPlotSelection() throws Exception {
        LabFilters labsFilters = new LabFilters();
        PopulationFilters populationFilters = new PopulationFilters();

        LabSelectionRequest labsRequest = new LabSelectionRequest();
        labsRequest.setLabsFilters(labsFilters);
        labsRequest.setPopulationFilters(populationFilters);

        labsRequest.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        final HashMap<LabGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "Alanine Aminotransferase (IU/L)");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "12.0");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(X_AXIS, "14.0");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems3 = new HashMap<>();
        selectedItems3.put(X_AXIS, "16.0");


        final List<ChartSelectionItemRange<Lab, LabGroupByOptions, Double>> selectionItems = Arrays.asList(
                ChartSelectionItemRange.of(selectedTrellises, selectedItems1, 0., 180.),
                ChartSelectionItemRange.of(selectedTrellises, selectedItems2, 0., 180.),
                ChartSelectionItemRange.of(selectedTrellises, selectedItems3, 0., 180.)
        );

        labsRequest.setSelection(ChartSelection.of(null, selectionItems));

        String url = "/resources/labs/shift-selection";

        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(labsRequest))
                .when()
                .post(url)
                .asString();
        System.out.println(jsonResponse);

        SelectionDetail returnedResult =
                mapper.readValue(jsonResponse, new TypeReference<SelectionDetail>() {
                });

        softly.assertThat(returnedResult.getEventIds()).hasSize(36);
        softly.assertThat(returnedResult.getSubjectIds()).hasSize(3);
        softly.assertThat(returnedResult.getTotalEvents()).isEqualTo(75317);
        softly.assertThat(returnedResult.getTotalSubjects()).isEqualTo(124);
    }

    @Test
    public void shouldGetBoxPlotSelection() throws Exception {
        LabFilters labsFilters = new LabFilters();
        PopulationFilters populationFilters = new PopulationFilters();

        LabSelectionRequest labsRequest = new LabSelectionRequest();
        labsRequest.setLabsFilters(labsFilters);
        labsRequest.setPopulationFilters(populationFilters);

        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.MEASUREMENT_TIME_POINT.getGroupByOptionAndParams(
                        GroupByOption.Params.builder()
                                .with(GroupByOption.Param.BIN_SIZE, 10)
                                .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                                .build()
                ))
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();


        labsRequest.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        final HashMap<LabGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "Alanine Aminotransferase (IU/L)");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "10 - 19");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(X_AXIS, "20 - 29");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems3 = new HashMap<>();
        selectedItems3.put(X_AXIS, "30 - 39");


        final List<ChartSelectionItemRange<Lab, LabGroupByOptions, Double>> selectionItems = Arrays.asList(
                ChartSelectionItemRange.of(selectedTrellises, selectedItems1, 0., 20.),
                ChartSelectionItemRange.of(selectedTrellises, selectedItems2, 0., 30.),
                ChartSelectionItemRange.of(selectedTrellises, selectedItems3, 0., 40.)
        );

        labsRequest.setSelection(ChartSelection.of(settings, selectionItems));

        String url = "/resources/labs/boxplot-selection";

        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(labsRequest))
                .when()
                .post(url)
                .asString();
        System.out.println(jsonResponse);

        SelectionDetail returnedResult =
                mapper.readValue(jsonResponse, new TypeReference<SelectionDetail>() {
                });

        softly.assertThat(returnedResult.getEventIds()).hasSize(36);
        softly.assertThat(returnedResult.getSubjectIds()).hasSize(18);
        softly.assertThat(returnedResult.getTotalEvents()).isEqualTo(75317);
        softly.assertThat(returnedResult.getTotalSubjects()).isEqualTo(124);
    }

    @Test
    public void shouldGetMeanRangePlotSelection() throws Exception {
        LabFilters labsFilters = new LabFilters();
        PopulationFilters populationFilters = new PopulationFilters();

        LabMeanRangeSelectionRequest labsRequest = new LabMeanRangeSelectionRequest();
        labsRequest.setLabsFilters(labsFilters);
        labsRequest.setPopulationFilters(populationFilters);

        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.MEASUREMENT_TIME_POINT.getGroupByOptionAndParams(
                        GroupByOption.Params.builder()
                                .with(GroupByOption.Param.BIN_SIZE, 10)
                                .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                                .build()
                ))
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();


        labsRequest.setDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        final HashMap<LabGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "Alanine Aminotransferase (IU/L)");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "10 - 19");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(X_AXIS, "20 - 29");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems3 = new HashMap<>();
        selectedItems3.put(X_AXIS, "30 - 39");


        final List<ChartSelectionItem<Lab, LabGroupByOptions>> selectionItems = Arrays.asList(
                ChartSelectionItem.of(selectedTrellises, selectedItems1),
                ChartSelectionItem.of(selectedTrellises, selectedItems2),
                ChartSelectionItem.of(selectedTrellises, selectedItems3)
        );

        labsRequest.setSelection(ChartSelection.of(settings, selectionItems));

        String url = "/resources/labs/mean-range-selection";

        String jsonResponse = given()
                .contentType("application/json")
                .body(mapper.writeValueAsString(labsRequest))
                .when()
                .post(url)
                .asString();
        System.out.println(jsonResponse);

        SelectionDetail returnedResult =
                mapper.readValue(jsonResponse, new TypeReference<SelectionDetail>() {
                });

        softly.assertThat(returnedResult.getEventIds()).hasSize(71);
        softly.assertThat(returnedResult.getSubjectIds()).hasSize(23);
        softly.assertThat(returnedResult.getTotalEvents()).isEqualTo(75317);
        softly.assertThat(returnedResult.getTotalSubjects()).isEqualTo(124);
    }
}
