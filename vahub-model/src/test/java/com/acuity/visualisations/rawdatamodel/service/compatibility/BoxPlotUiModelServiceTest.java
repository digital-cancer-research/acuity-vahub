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

package com.acuity.visualisations.rawdatamodel.service.compatibility;

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.plots.StatsPlotService;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.BoxPlotTests;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.BoxplotCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@Category(BoxPlotTests.class)
public class BoxPlotUiModelServiceTest {

    @Autowired
    private StatsPlotService<Lab, LabGroupByOptions> statsPlotService;

    @Autowired
    private BoxPlotUiModelService boxPlotUiModelService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldConvertsTrellisingToCorrectFormat() {
        // Given
        final Map<GroupByKey<Lab, LabGroupByOptions>, BoxplotCalculationObject> data = getBoxPlotDataTrellisedByMeasurementAndArm();

        // When
        final List<TrellisedBoxPlot<Lab, LabGroupByOptions>> result = boxPlotUiModelService.toTrellisedBoxPlot(data);

        // Then
        assertThat(result)
                .filteredOn(r -> r.getTrellisedBy().stream().anyMatch(t -> t.getTrellisOption().equals("arm-1")))
                .flatExtracting("trellisedBy")
                .extracting("trellisOption")
                .containsExactlyInAnyOrder("ALT (g/dL)", "arm-1");
        assertThat(result)
                .filteredOn(r -> r.getTrellisedBy().stream().anyMatch(t -> t.getTrellisOption().equals("arm-2")))
                .flatExtracting("trellisedBy")
                .extracting("trellisOption")
                .containsExactlyInAnyOrder("ALT (g/dL)", "arm-2");
    }

    @Test
    public void shouldConvertDataToCorrectFormat() {
        // Given
        final Map<GroupByKey<Lab, LabGroupByOptions>, BoxplotCalculationObject> data = getBoxPlotDataTrellisedByMeasurementAndArm();

        // When
        final List<TrellisedBoxPlot<Lab, LabGroupByOptions>> result = boxPlotUiModelService.toTrellisedBoxPlot(data);

        // Then
        assertThat(result)
                .filteredOn(r -> r.getTrellisedBy().stream().anyMatch(t -> t.getTrellisOption().equals("arm-1")))
                .flatExtracting(TrellisedBoxPlot::getStats)
                .extracting("x", "upperWhisker", "upperQuartile", "median", "lowerQuartile", "lowerWhisker", "eventCount")
                .contains(
                        tuple("10", 1.0, 1.0, 1.0, 1.0, 1.0, 1L),
                        tuple("11", 3.0, 3.0, 3.0, 3.0, 3.0, 1L)
                );
        assertThat(result)
                .filteredOn(r -> r.getTrellisedBy().stream().anyMatch(t -> t.getTrellisOption().equals("arm-2")))
                .flatExtracting(TrellisedBoxPlot::getStats)
                .extracting("x", "upperWhisker", "upperQuartile", "median", "lowerQuartile", "lowerWhisker", "eventCount")
                .contains(
                        tuple("10", 2.0, 2.0, 2.0, 2.0, 2.0, 1L),
                        tuple("11", 4.0, 4.0, 4.0, 4.0, 4.0, 1L)
                );
    }

    @Test
    public void shouldGetContinuousBinnedData() {
        //Given
        final Map<GroupByKey<Lab, LabGroupByOptions>, BoxplotCalculationObject> data = getBinnedBoxPlotDataTrellisedByMeasurement();

        //When
        final List<TrellisedBoxPlot<Lab, LabGroupByOptions>> result = boxPlotUiModelService.toTrellisedBoxPlot(data);

        //Then
        softly.assertThat(result).hasSize(1);
        softly.assertThat(result.get(0).getStats())
                .extracting(stat -> stat.getX().toString())
                .containsOnly("0 - 1", "2 - 3", "4 - 5");
        softly.assertThat(result.get(0).getStats())
                .extracting("xRank", "median")
                .containsOnly(
                        tuple(0.0, 1.5),
                        tuple(1.0, null),
                        tuple(2.0, 3.5)
                );
    }

    private Map<GroupByKey<Lab, LabGroupByOptions>, BoxplotCalculationObject> getBoxPlotDataTrellisedByMeasurementAndArm() {
        // Given -- filtered data
        final Subject e01 = Subject.builder().subjectCode("E01").subjectId("01").actualArm("arm-1").build();
        Lab lab1 = new Lab(LabRaw.builder().subjectId("01").id("e01").labCode("ALT").unit("g/dL").value(1.).visitNumber(10.).build(),
                e01);
        final Subject e02 = Subject.builder().subjectCode("E02").subjectId("02").actualArm("arm-2").build();
        Lab lab2 = new Lab(LabRaw.builder().subjectId("02").id("e02").labCode("ALT").unit("g/dL").value(2.).visitNumber(10.).build(),
                e02);
        final Subject e03 = Subject.builder().subjectCode("E03").subjectId("03").actualArm("arm-1").build();
        Lab lab3 = new Lab(LabRaw.builder().subjectId("03").id("e03").labCode("ALT").unit("g/dL").value(3.).visitNumber(11.).build(),
                e03);
        final Subject e04 = Subject.builder().subjectCode("E04").subjectId("04").actualArm("arm-2").build();
        Lab lab4 = new Lab(LabRaw.builder().subjectId("04").id("e04").labCode("ALT").unit("g/dL").value(4.).visitNumber(11.).build(),
                e04);
        List<Lab> events = Arrays.asList(lab1, lab2, lab3, lab4);
        List<Subject> subjects = Arrays.asList(e01, e02, e03, e04);

        // Given -- final data
        final FilterResult<Lab> filtered = new FilterResult<>(new FilterQuery<Lab>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));

        // Given -- attributes

        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();
        // Given -- box plot data
        return statsPlotService.getBoxPlot(settings, filtered);
    }

    private Map<GroupByKey<Lab, LabGroupByOptions>, BoxplotCalculationObject> getBinnedBoxPlotDataTrellisedByMeasurement() {
        // Given -- filtered data
        Subject subj1 = Subject.builder().subjectCode("E01").subjectId("01").dateOfRandomisation(DateUtils.toDate("01.01.2000")).build();
        Subject subj2 = Subject.builder().subjectCode("E02").subjectId("02").dateOfRandomisation(DateUtils.toDate("01.01.2000")).build();
        Subject subj3 = Subject.builder().subjectCode("E02").subjectId("03").dateOfRandomisation(DateUtils.toDate("01.01.2000")).build();
        Subject subj4 = Subject.builder().subjectCode("E04").subjectId("04").dateOfRandomisation(DateUtils.toDate("01.01.2000")).build();
        Lab lab1 = new Lab(LabRaw.builder().subjectId("01").id("L01").labCode("ALT").unit("g/dL").value(1.).measurementTimePoint(DateUtils.toDate("01.01.2000"))
                .build(), subj1);
        Lab lab2 = new Lab(LabRaw.builder().subjectId("02").id("L02").labCode("ALT").unit("g/dL").value(2.).measurementTimePoint(DateUtils.toDate("02.01.2000"))
                .build(), subj2);
        Lab lab3 = new Lab(LabRaw.builder().subjectId("03").id("L03").labCode("ALT").unit("g/dL").value(3.).measurementTimePoint(DateUtils.toDate("05.01.2000"))
                .build(), subj3);
        Lab lab4 = new Lab(LabRaw.builder().subjectId("04").id("L04").labCode("ALT").unit("g/dL").value(4.).measurementTimePoint(DateUtils.toDate("05.01.2000"))
                .build(), subj4);
        List<Lab> events = Arrays.asList(lab1, lab2, lab3, lab4);
        List<Subject> subjects = Arrays.asList(subj1, subj2, subj3, subj4);

        final FilterResult<Lab> filtered = new FilterResult<>(new FilterQuery<Lab>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));


        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.MEASUREMENT_TIME_POINT.getGroupByOptionAndParams(
                        GroupByOption.Params.builder()
                                .with(GroupByOption.Param.BIN_SIZE, 2)
                                .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_RANDOMISATION)
                                .build()))
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();

        // Given -- box plot data
        return statsPlotService.getBoxPlot(settings, filtered);
    }
}
