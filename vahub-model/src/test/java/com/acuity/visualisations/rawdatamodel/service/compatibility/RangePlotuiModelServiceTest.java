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
import com.acuity.visualisations.rawdatamodel.suites.interfaces.RangeChartTests;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputRangeChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.RangeChartCalculationObject;
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

import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.groups.Tuple.tuple;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@Category(RangeChartTests.class)
public class RangePlotuiModelServiceTest {

    @Autowired
    private RangePlotUiModelService rangePlotUiModelService;

    @Autowired
    private StatsPlotService<Lab, LabGroupByOptions> statsPlotService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldConvertTrellisingToCorrectFormat() {
        // Given
        final Map<GroupByKey<Lab, LabGroupByOptions>, RangeChartCalculationObject> data = getRangePlotDataTrellisedByMeasurementAndArm();

        // When
        final List<TrellisedRangePlot<Lab, LabGroupByOptions>> result = rangePlotUiModelService.toTrellisedRangePlot(data, StatType.MEAN);

        // Then
        softly.assertThat(result)
                .flatExtracting(r -> r.getTrellisedBy())
                .extracting(t -> t.getTrellisedBy(), t -> t.getTrellisOption())
                .containsOnly(tuple(LabGroupByOptions.MEASUREMENT, "ALT (g/dL)"));
    }

    @Test
    public void shouldConvertDataToCorrectFormatWhenTrellisedByMeasurementAndArm() {
        // Given
        final Map<GroupByKey<Lab, LabGroupByOptions>, RangeChartCalculationObject> data = getRangePlotDataTrellisedByMeasurementAndArm();

        // When
        final List<TrellisedRangePlot<Lab, LabGroupByOptions>> result = rangePlotUiModelService.toTrellisedRangePlot(data, StatType.MEAN);

        // Then
        List<OutputRangeChartEntry> arm1ExpectedData = newArrayList(
                OutputRangeChartEntry.builder().x("10").xRank(10d).dataPoints(2).y(3.).min(1.).max(5.).stdDev(2.83)
                        .stdErr(2.).name("Sponsor").build(),
                OutputRangeChartEntry.builder().x("11").xRank(11d).dataPoints(2).y(4.).min(2.).max(6.).stdDev(2.83)
                        .stdErr(2.).name("Sponsor").build()
        );
        List<OutputRangeChartEntry> arm2ExpectedData = newArrayList(
                OutputRangeChartEntry.builder().x("10").xRank(10d).dataPoints(2).y(5.).min(3.).max(7.).stdDev(2.83)
                        .stdErr(2.).name("Sponsor").build(),
                OutputRangeChartEntry.builder().x("11").xRank(11d).dataPoints(2).y(6.).min(4.).max(8.).stdDev(2.83)
                        .stdErr(2.).name("Sponsor").build()
                //new RangeChartCalculationObject("10.0", 10, 2, 5., 3., 7., 2.83, 2.),
                //new RangeChartCalculationObject("11.0", 11, 2, 6., 4., 8., 2.83, 2.)
        );
        softly.assertThat(result.get(0).getData())
                .extracting(e -> e.getName(), e -> e.getData())
                .contains(tuple("arm-1", arm1ExpectedData), tuple("arm-2", arm2ExpectedData));
    }

    @Test
    public void shouldConvertDataToCorrectFormatWhenTrellisedByMeasurement() {
        final Map<GroupByKey<Lab, LabGroupByOptions>, RangeChartCalculationObject> data = getRangePlotDataTrellisedByMeasurement();

        // When
        final List<TrellisedRangePlot<Lab, LabGroupByOptions>> result = rangePlotUiModelService.toTrellisedRangePlot(data, StatType.MEAN);

        // Then
        List<OutputRangeChartEntry> expectedData = newArrayList(
                OutputRangeChartEntry.builder().x("10").xRank(10d).dataPoints(4).y(4.).min(2.71).max(5.29).stdDev(2.58)
                        .stdErr(1.29).name("Sponsor").build(),
                OutputRangeChartEntry.builder().x("11").xRank(11d).dataPoints(4).y(5.).min(3.71).max(6.29).stdDev(2.58)
                        .stdErr(1.29).name("Sponsor").build()
        );
        softly.assertThat(result.get(0).getData())
                .extracting("name", "data")
                .contains(tuple("All", expectedData));
        softly.assertThat(result.get(0).getTrellisedBy()).extracting(t -> t.getTrellisOption())
                .containsOnly("ALT (g/dL)");
    }

    private Map<GroupByKey<Lab, LabGroupByOptions>, RangeChartCalculationObject> getRangePlotDataTrellisedByMeasurementAndArm() {
        // Given -- filtered data
        Subject subj1 = Subject.builder().subjectCode("E01").subjectId("01").actualArm("arm-1").build();
        Subject subj2 = Subject.builder().subjectCode("E02").subjectId("02").actualArm("arm-2").build();
        Subject subj3 = Subject.builder().subjectCode("E03").subjectId("03").actualArm("arm-1").build();
        Subject subj4 = Subject.builder().subjectCode("E04").subjectId("04").actualArm("arm-2").build();
        Lab lab1 = new Lab(LabRaw.builder().subjectId(subj1.getSubjectId()).id("e01").labCode("ALT").unit("g/dL")
                .value(1.).visitNumber(10.).sourceType("Sponsor").build(),
                subj1);
        Lab lab2 = new Lab(LabRaw.builder().subjectId(subj1.getSubjectId())
                .id("e02").labCode("ALT").unit("g/dL").value(2.).visitNumber(11.).sourceType("Sponsor").build(), subj1);
        Lab lab3 = new Lab(LabRaw.builder().subjectId(subj2.getSubjectId())
                .id("e03").labCode("ALT").unit("g/dL").value(3.).visitNumber(10.).sourceType("Sponsor").build(), subj2);
        Lab lab4 = new Lab(LabRaw.builder().subjectId(subj2.getSubjectId())
                .id("e04").labCode("ALT").unit("g/dL").value(4.).visitNumber(11.).sourceType("Sponsor").build(), subj2);
        Lab lab5 = new Lab(LabRaw.builder().subjectId(subj3.getSubjectId())
                .id("e05").labCode("ALT").unit("g/dL").value(5.).visitNumber(10.).sourceType("Sponsor").build(), subj3);
        Lab lab6 = new Lab(LabRaw.builder().subjectId(subj3.getSubjectId())
                .id("e06").labCode("ALT").unit("g/dL").value(6.).visitNumber(11.).sourceType("Sponsor").build(), subj3);
        Lab lab7 = new Lab(LabRaw.builder().subjectId(subj4.getSubjectId())
                .id("e07").labCode("ALT").unit("g/dL").value(7.).visitNumber(10.).sourceType("Sponsor").build(), subj4);
        Lab lab8 = new Lab(LabRaw.builder().subjectId(subj4.getSubjectId())
                .id("e08").labCode("ALT").unit("g/dL").value(8.).visitNumber(11.).sourceType("Sponsor").build(), subj4);
        List<Lab> events = Arrays.asList(lab1, lab2, lab3, lab4, lab5, lab6, lab7, lab8);
        List<Subject> subjects = Arrays.asList(subj1, subj2, subj3, subj4);

        final FilterResult<Lab> filtered = new FilterResult<>(new FilterQuery<Lab>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));


        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, LabGroupByOptions.ARM.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.NAME, LabGroupByOptions.SOURCE_TYPE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();

        // Given -- box plot data
        return statsPlotService.getRangePlot(settings, filtered);
    }

    private Map<GroupByKey<Lab, LabGroupByOptions>, RangeChartCalculationObject> getRangePlotDataTrellisedByMeasurement() {
        // Given -- filtered data
        Subject subj1 = Subject.builder().subjectCode("E01").subjectId("01").build();
        Subject subj2 = Subject.builder().subjectCode("E02").subjectId("02").build();
        Subject subj3 = Subject.builder().subjectCode("E03").subjectId("03").build();
        Subject subj4 = Subject.builder().subjectCode("E04").subjectId("04").build();
        Lab lab1 = new Lab(LabRaw.builder().subjectId(subj1.getSubjectId()).id("e01").labCode("ALT").unit("g/dL")
                .value(1.).visitNumber(10.).sourceType("Sponsor").build(), subj1);
        Lab lab2 = new Lab(LabRaw.builder().subjectId(subj1.getSubjectId())
                .id("e02").labCode("ALT").unit("g/dL").value(2.).visitNumber(11.).sourceType("Sponsor").build(), subj1);
        Lab lab3 = new Lab(LabRaw.builder().subjectId(subj2.getSubjectId())
                .id("e03").labCode("ALT").unit("g/dL").value(3.).visitNumber(10.).sourceType("Sponsor").build(), subj2);
        Lab lab4 = new Lab(LabRaw.builder().subjectId(subj2.getSubjectId())
                .id("e04").labCode("ALT").unit("g/dL").value(4.).visitNumber(11.).sourceType("Sponsor").build(), subj2);
        Lab lab5 = new Lab(LabRaw.builder().subjectId(subj3.getSubjectId())
                .id("e05").labCode("ALT").unit("g/dL").value(5.).visitNumber(10.).sourceType("Sponsor").build(), subj3);
        Lab lab6 = new Lab(LabRaw.builder().subjectId(subj3.getSubjectId())
                .id("e06").labCode("ALT").unit("g/dL").value(6.).visitNumber(11.).sourceType("Sponsor").build(), subj3);
        Lab lab7 = new Lab(LabRaw.builder().subjectId(subj4.getSubjectId())
                .id("e07").labCode("ALT").unit("g/dL").value(7.).visitNumber(10.).sourceType("Sponsor").build(), subj4);
        Lab lab8 = new Lab(LabRaw.builder().subjectId(subj4.getSubjectId())
                .id("e08").labCode("ALT").unit("g/dL").value(8.).visitNumber(11.).sourceType("Sponsor").build(), subj4);
        List<Lab> events = Arrays.asList(lab1, lab2, lab3, lab4, lab5, lab6, lab7, lab8);
        List<Subject> subjects = Arrays.asList(subj1, subj2, subj3, subj4);
        final FilterResult<Lab> filtered = new FilterResult<>(new FilterQuery<Lab>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));


        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.NAME, LabGroupByOptions.SOURCE_TYPE.getGroupByOptionAndParams())
                .build();

        // Given -- box plot data
        return statsPlotService.getRangePlot(settings, filtered);
    }
}
