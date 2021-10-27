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

package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.dataproviders.ExposureDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.ExposureFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors;
import com.acuity.visualisations.rawdatamodel.service.compatibility.ExposureLineChartColoringService;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Constants;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.ExposureRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputErrorLineChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.TrellisedLineFloatChart;
import com.acuity.visualisations.rawdatamodel.vo.exposure.Cycle;
import com.acuity.visualisations.rawdatamodel.vo.exposure.ExposureTooltip;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASET_42;
import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.COLORS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.ALL_INFO;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.ANALYTE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.CYCLE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.DAY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.DOSE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.DOSE_PER_CYCLE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.DOSE_PER_VISIT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.NONE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.SUBJECT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.SUBJECT_CYCLE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.VISIT;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ExposureServiceTest {

    @Autowired
    private ExposureService exposureService;
    @MockBean
    private ExposureDatasetsDataProvider exposureDatasetsDataProvider;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private ExposureLineChartColoringService coloringService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    // Double.toString(1d) yields "1.0" while decimalFormat.format(1d) yields "1"
    private final static DecimalFormat decimalFormat = new DecimalFormat();
    private final static String SUBJECT_1 = "subject1";
    private final static String SUBJECT_2 = "subject2";
    private final static String ANALYTE_1 = "analyte1";
    private final static String ANALYTE_2 = "analyte2";
    private final static String ANALYTE_3 = "analyte3";
    private final static String CYCLE_1 = "cycle1";
    private final static String CYCLE_2 = "cycle2";
    private final static String CYCLE_3 = "cycle3";
    private final static String CYCLE_4 = "cycle4";
    private final static String ML = "ml";

    private final Subject subject1 = Subject.builder()
            .clinicalStudyCode(String.valueOf(DUMMY_ACUITY_DATASET_42.getId()))
            .subjectId(SUBJECT_1).subjectCode(SUBJECT_1).build();
    private final Subject subject2 = Subject.builder()
            .clinicalStudyCode(String.valueOf(DUMMY_ACUITY_DATASET_42.getId()))
            .subjectId(SUBJECT_2).subjectCode(SUBJECT_2).build();
    private Exposure exposure1 = new Exposure(ExposureRaw.builder().id("id1").subjectId(SUBJECT_1)
            .analyte(ANALYTE_1).treatmentCycle(CYCLE_1).analyteConcentration(2.5).analyteUnit(ML).treatment("20 mg")
            .timeFromAdministration(4.0).protocolScheduleDay(1)
            .visitNumber(1).nominalHour(2.0).cycle(new Cycle(CYCLE_1, ANALYTE_1, 1, null, false))
            .build(), subject1);
    private Exposure exposure2 = new Exposure(ExposureRaw.builder().id("id2").subjectId(SUBJECT_1)
            .analyte(ANALYTE_1).treatmentCycle(CYCLE_1).analyteConcentration(4.6).analyteUnit(ML).treatment("200 mg")
            .timeFromAdministration(8.0).protocolScheduleDay(2)
            .visitNumber(1).nominalDay(2).nominalHour(1.0).nominalMinute(0).lowerLimit(1.1)
            .visitDate(DaysUtil.toDate("2000-01-01")).actualSamplingDate(DaysUtil.toDate("2000-02-02"))
            .cycle(new Cycle(CYCLE_1, ANALYTE_1, 1, null, false)).build(), subject1);
    private Exposure exposure3 = new Exposure(ExposureRaw.builder().id("id3").subjectId(SUBJECT_1)
            .analyte(ANALYTE_1).treatmentCycle(CYCLE_1).analyteConcentration(3.0).analyteUnit(ML)
            .timeFromAdministration(12.0)
            .visitNumber(1).cycle(new Cycle(CYCLE_1, ANALYTE_1, 1, null, false)).build(), subject1);
    private Exposure exposure3_2 = new Exposure(ExposureRaw.builder().id("id32").subjectId(SUBJECT_1)
            .analyte(ANALYTE_2).treatmentCycle(CYCLE_1).analyteConcentration(3.1).analyteUnit(ML)
            .timeFromAdministration(10.0)
            .visitNumber(1).cycle(new Cycle(CYCLE_1, ANALYTE_2, 1, null, false))
            .build(), subject1);
    private Exposure exposure4 = new Exposure(ExposureRaw.builder().id("id4").subjectId(SUBJECT_1)
            .analyte(ANALYTE_2).treatmentCycle(CYCLE_2).analyteConcentration(2.0).analyteUnit(ML)
            .timeFromAdministration(12.0)
            .visitNumber(1).cycle(new Cycle(CYCLE_2, ANALYTE_2, 1, null, false))
            .build(), subject1);
    private Exposure exposure4_2 = new Exposure(ExposureRaw.builder().id("id42").subjectId(SUBJECT_1)
            .analyte(ANALYTE_2).treatmentCycle(CYCLE_2).analyteConcentration(2.0).analyteUnit(ML)
            .timeFromAdministration(8.0)
            .visitNumber(1).cycle(new Cycle(CYCLE_2, ANALYTE_2, 1, null, false))
            .build(), subject1);
    private Exposure exposure5 = new Exposure(ExposureRaw.builder().id("id5").subjectId(SUBJECT_2)
            .analyte(ANALYTE_2).treatmentCycle(CYCLE_2).analyteConcentration(3.0).analyteUnit(ML)
            .timeFromAdministration(12.0).protocolScheduleDay(2)
            .visitNumber(2).cycle(new Cycle(CYCLE_2, ANALYTE_2, 2, null, false))
            .build(), subject2);
    private Exposure exposure6 = new Exposure(ExposureRaw.builder().id("id6").subjectId(SUBJECT_2)
            .analyte(ANALYTE_2).treatmentCycle(CYCLE_2).analyteConcentration(7.5).analyteUnit(ML)
            .timeFromAdministration(15.0)
            .visitNumber(2).cycle(new Cycle(CYCLE_2, ANALYTE_2, 2, null, false))
            .build(), subject2);
    private Exposure exposure7 = new Exposure(ExposureRaw.builder().id("id7").subjectId(SUBJECT_2)
            .analyte(ANALYTE_2).treatmentCycle(CYCLE_2).analyteConcentration(2.0).analyteUnit(ML)
            .timeFromAdministration(15.0)
            .visitNumber(3).cycle(new Cycle(CYCLE_2, ANALYTE_2, 3, null, false))
            .build(), subject2);
    private Exposure exposure8 = new Exposure(ExposureRaw.builder().id("id8").subjectId(SUBJECT_2)
            .analyte(ANALYTE_3).treatmentCycle(CYCLE_3).analyteConcentration(4.0).analyteUnit(ML)
            .timeFromAdministration(12.0)
            .visitNumber(3).cycle(new Cycle(CYCLE_3, ANALYTE_3, 3, null, false))
            .build(), subject2);
    private Exposure exposure8_2 = new Exposure(ExposureRaw.builder().id("id82").subjectId(SUBJECT_2)
            .analyte(ANALYTE_2).treatmentCycle(CYCLE_2).analyteConcentration(4.0).analyteUnit(ML)
            .timeFromAdministration(8.0)
            .visitNumber(3).cycle(new Cycle(CYCLE_2, ANALYTE_2, 3, null, false))
            .build(), subject2);
    private Exposure exposure9 = new Exposure(ExposureRaw.builder().id("id9").subjectId(SUBJECT_2)
            .analyte(ANALYTE_2).treatmentCycle(CYCLE_4).analyteConcentration(5.5).analyteUnit(ML)
            .timeFromAdministration(15.0)
            .visitNumber(3).cycle(new Cycle(CYCLE_4, ANALYTE_2, 3, null, false))
            .build(), subject2);
    private Exposure exposure9_1 = new Exposure(ExposureRaw.builder().id("id91").subjectId(SUBJECT_2)
            .analyte(ANALYTE_2).treatmentCycle(CYCLE_2).analyteConcentration(5.5).analyteUnit(ML)
            .timeFromAdministration(15.0)
            .visitNumber(3).cycle(new Cycle(CYCLE_2, ANALYTE_2, 3, null, false))
            .build(), subject2);
    private Exposure exposure9_2 = new Exposure(ExposureRaw.builder().id("id92").subjectId(SUBJECT_2)
            .analyte(ANALYTE_2).treatmentCycle(CYCLE_1).analyteConcentration(6.0).analyteUnit(ML)
            .timeFromAdministration(15.0)
            .visitNumber(3).cycle(new Cycle(CYCLE_1, ANALYTE_2, 3, null, false))
            .build(), subject2);
    private Exposure exposure9_3 = new Exposure(ExposureRaw.builder().id("id93").subjectId(SUBJECT_2)
            .analyte(ANALYTE_2).treatmentCycle(CYCLE_1).analyteConcentration(8.0).analyteUnit(ML)
            .timeFromAdministration(19.0)
            .visitNumber(3).cycle(new Cycle(CYCLE_1, ANALYTE_2, 3, null, false))
            .build(), subject2);
    private Exposure exposureNullVisit = new Exposure(ExposureRaw.builder().id("id10").subjectId(SUBJECT_1)
            .analyte(ANALYTE_1).treatmentCycle(CYCLE_1).analyteConcentration(2.5).analyteUnit(ML).treatment("23 mg")
            .timeFromAdministration(4.0).protocolScheduleDay(1).nominalHour(2.0)
            .cycle(new Cycle(CYCLE_1, ANALYTE_1, null, null, false)).build(), subject1);

    @Before
    public void setUp() {
        List<Exposure> events = newArrayList(exposure1, exposure2, exposure3, exposure4, exposure5,
                exposure6, exposure7, exposure8, exposure9, exposureNullVisit);
        when(exposureDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(newArrayList(subject1, subject2));
        //Given
        when(coloringService.getColor(any(), any())).thenReturn(COLORS[0]);
    }

    @Test
    public void shouldGetExposureLines() {

        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings = getExposureSettings(
                SUBJECT_CYCLE, SUBJECT);

        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> exposureLines =
                exposureService.getLineChart(new Datasets(DUMMY_ACUITY_DATASET_42), settings,
                        ExposureFilters.empty(), PopulationFilters.empty());

        softly.assertThat(exposureLines).hasSize(1);
        softly.assertThat(exposureLines.get(0).getData()).hasSize(2);

        List<OutputLineChartData> data1 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString()
                        .startsWith(SUBJECT_1)).collect(toList());
        softly.assertThat(data1).hasSize(1);
        softly.assertThat(data1)
                .filteredOn(d -> d.getSeriesBy().toString().equals("subject1, cycle1, analyte1, visit 1"))
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getExposureData().getSubject(),
                        o -> ((ExposureTooltip) o.getName()).getExposureData().getAnalyte(),
                        o -> ((ExposureTooltip) o.getName()).getExposureData().getTreatmentCycle(),
                        o -> ((ExposureTooltip) o.getName()).getExposureData().getVisit(),
                        o -> ((ExposureTooltip) o.getName()).getExposureData().getDay(),
                        o -> ((ExposureTooltip) o.getName()).getExposureData().getDose(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(4.0, 2.5, SUBJECT_1, ANALYTE_1, CYCLE_1, "1", "1", "20 mg", 0.0),
                tuple(8.0, 4.6, SUBJECT_1, ANALYTE_1, CYCLE_1, "1", "2", "200 mg", 0.0),
                tuple(12.0, 3.0, SUBJECT_1, ANALYTE_1, CYCLE_1, "1", "(Empty)", "(Empty)", 0.0)
        );

        List<OutputLineChartData> data2 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString()
                        .startsWith(SUBJECT_2)).collect(toList());
        softly.assertThat(data2).hasSize(1);
        softly.assertThat(data2)
                .filteredOn(d -> d.getSeriesBy().toString().equals("subject2, cycle2, analyte2, visit 2"))
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(12.0, 3.0, 0.0),
                tuple(15.0, 7.5, 0.0)
        );
    }

    @Test
    public void shouldGetExposureLinesWithTrellisOptions() {

        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings = getExposureSettings(
                SUBJECT, SUBJECT).toBuilder()
                .withTrellisOption(ANALYTE.getGroupByOptionAndParams())
                .build();

        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> exposureLines =
                exposureService.getLineChart(new Datasets(DUMMY_ACUITY_DATASET_42), settings,
                        ExposureFilters.empty(), PopulationFilters.empty());

        softly.assertThat(exposureLines).hasSize(2);
        softly.assertThat(exposureLines)
                .flatExtracting(TrellisedLineFloatChart::getTrellisedBy)
                .extracting(TrellisOption::getTrellisOption)
                .containsExactly(ANALYTE_1, ANALYTE_2);

        softly.assertThat(exposureLines.get(1).getData())
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(o -> ((ExposureTooltip) o.getName()).getExposureData().getAnalyte())
                .containsExactly(null, null);
    }

    @Test
    public void shouldGetExposureLinesSubjectAvg() {
        //Given

        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings = getExposureSettings(
                SUBJECT, SUBJECT);

        List<Exposure> events = newArrayList(exposure1, exposure2, exposure3, exposure4, exposure4_2,
                exposure5, exposure6, exposure7, exposure8, exposure8_2, exposure9);

        when(exposureDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);

        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> exposureLines =
                exposureService.getLineChart(new Datasets(DUMMY_ACUITY_DATASET_42), settings,
                        ExposureFilters.empty(), PopulationFilters.empty());

        softly.assertThat(exposureLines).hasSize(1);
        softly.assertThat(exposureLines.get(0).getData()).hasSize(2);

        List<OutputLineChartData> data1 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString()
                        .startsWith(SUBJECT_1)).collect(toList());
        softly.assertThat(data1).hasSize(1);
        softly.assertThat(data1).filteredOn(d -> d.getSeriesBy().toString().equals(SUBJECT_1))
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getExposureData().getAnalyte(),
                        o -> ((ExposureTooltip) o.getName()).getExposureData().getTreatmentCycle(),
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(4.0, 2.5, ANALYTE_1, CYCLE_1, 1, 0.0),
                tuple(8.0, 3.3, ANALYTE_1 + ", " + ANALYTE_2, CYCLE_1 + ", " + CYCLE_2, 2, 1.3),
                tuple(12.0, 2.5, ANALYTE_1 + ", " + ANALYTE_2, CYCLE_1 + ", " + CYCLE_2, 2, 0.5)
        );

        List<OutputLineChartData> data2 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString()
                        .startsWith(SUBJECT_2)).collect(toList());
        softly.assertThat(data2).hasSize(1);
        softly.assertThat(data2).filteredOn(d -> d.getSeriesBy().toString().equals(SUBJECT_2))
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 4.0, 0.0),
                tuple(12.0, 3.0, 0.0),
                tuple(15.0, 4.75, 2.75)
        );
    }

    @Test
    public void testAverageYAndDeviationTwoExposures() {
        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings = getExposureSettings(
                SUBJECT_CYCLE, SUBJECT);
        Exposure exposure1 = new Exposure(ExposureRaw.builder().id("id1").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(90.61).timeFromAdministration(8.0)
                .treatment("50 mg").visitNumber(1)
                .cycle(new Cycle(null, ANALYTE_1, 1, null, false))
                .build(), subject1);
        Exposure exposure2 = new Exposure(ExposureRaw.builder().id("id2").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(86.72).timeFromAdministration(8.0)
                .treatment("50 mg").visitNumber(1)
                .cycle(new Cycle(null, ANALYTE_1, 1, null, false))
                .build(), subject1);

        List<Exposure> events = newArrayList(exposure1, exposure2);

        when(exposureDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);

        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> exposureLines =
                exposureService.getLineChart(new Datasets(DUMMY_ACUITY_DATASET_42), settings,
                        ExposureFilters.empty(), PopulationFilters.empty());

        List<OutputLineChartData> data1 = new ArrayList<>(exposureLines.get(0).getData());
        softly.assertThat(data1)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getY,
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(88.67, 1.95)
        );
    }

    @Test
    public void testAverageYAndDeviationWithEqualExposures() {
        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings = getExposureSettings(
                SUBJECT_CYCLE, SUBJECT);
        Exposure exposure1 = new Exposure(ExposureRaw.builder().id("id1").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(5.6).timeFromAdministration(8.0)
                .treatment("50 mg").visitNumber(1)
                .cycle(new Cycle(null, ANALYTE_1, 1, null, false))
                .build(), subject1);

        List<Exposure> events = newArrayList(exposure1, exposure1);

        when(exposureDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);

        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> exposureLines =
                exposureService.getLineChart(new Datasets(DUMMY_ACUITY_DATASET_42), settings,
                        ExposureFilters.empty(), PopulationFilters.empty());

        List<OutputLineChartData> data1 = new ArrayList<>(exposureLines.get(0).getData());
        softly.assertThat(data1)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getY,
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(5.6, 0.0)
        );
    }

    @Test
    public void shouldGetExposureLinesAnalyteAvg() {
        //Given

        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings = getExposureSettings(
                ANALYTE, ANALYTE);

        List<Exposure> events = newArrayList(exposure1, exposure2, exposure3, exposure4, exposure4_2,
                exposure5, exposure6, exposure7, exposure8, exposure8_2, exposure9_1, exposure9_2, exposure9_3);

        when(exposureDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);

        //When
        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> exposureLines =
                exposureService.getLineChart(new Datasets(DUMMY_ACUITY_DATASET_42), settings,
                        ExposureFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(exposureLines).hasSize(1);
        softly.assertThat(exposureLines.get(0).getData()).hasSize(2);

        List<OutputLineChartData> data1 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString()
                        .startsWith(ANALYTE_2)).collect(toList());
        softly.assertThat(data1).hasSize(1);
        softly.assertThat(data1).filteredOn(d -> d.getSeriesBy().toString().equals(ANALYTE_2))
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getExposureData().getAnalyte(),
                        o -> ((ExposureTooltip) o.getName()).getExposureData().getTreatmentCycle(),
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 3.0, ANALYTE_2, CYCLE_2, 2, 1.0),
                tuple(12.0, 2.5, ANALYTE_2, CYCLE_2, 2, 0.5),
                tuple(15.0, 5.25, ANALYTE_2, CYCLE_1 + ", " + CYCLE_2, 4, 2.02),
                tuple(19.0, 8.0, ANALYTE_2, CYCLE_1, 1, 0.0)
        );

        List<OutputLineChartData> data2 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString()
                        .startsWith(ANALYTE_1)).collect(toList());
        softly.assertThat(data2).hasSize(1);
        softly.assertThat(data2).filteredOn(d -> d.getSeriesBy().toString().equals(ANALYTE_1))
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(4.0, 2.5, 0.0),
                tuple(8.0, 4.6, 0.0),
                tuple(12.0, 3.0, 0.0)
        );
    }

    @Test
    public void shouldGetExposureLinesDoseAvg() {

        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings = getExposureSettings(
                DOSE, DOSE).toBuilder()
                .withTrellisOption(ANALYTE.getGroupByOptionAndParams())
                .build();

        Exposure exposureDose1_1 = new Exposure(ExposureRaw.builder().id("id1").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(2.0).timeFromAdministration(8.0)
                .treatment("50 mg")
                .cycle(new Cycle(null, ANALYTE_1, null, null, false)).build(), subject1);
        Exposure exposureDose1_2 = new Exposure(ExposureRaw.builder().id("id2").subjectId(SUBJECT_2)
                .analyte(ANALYTE_1).analyteConcentration(4.0).timeFromAdministration(8.0)
                .treatment("50 mg")
                .cycle(new Cycle(null, ANALYTE_1, null, null, false)).build(), subject2);
        Exposure exposureDose1_3 = new Exposure(ExposureRaw.builder().id("id3").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(5.0).timeFromAdministration(12.0)
                .treatment("50 mg")
                .cycle(new Cycle(null, ANALYTE_1, null, null, false)).build(), subject1);
        Exposure exposureDose2_1 = new Exposure(ExposureRaw.builder().id("id4").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(8.0).timeFromAdministration(8.0)
                .treatment("100 mg")
                .cycle(new Cycle(null, ANALYTE_1, null, null, false)).build(), subject1);
        Exposure exposureDose2_2 = new Exposure(ExposureRaw.builder().id("id5").subjectId(SUBJECT_2)
                .analyte(ANALYTE_1).analyteConcentration(9.0).timeFromAdministration(12.0)
                .treatment("100 mg")
                .cycle(new Cycle(null, ANALYTE_1, null, null, false)).build(), subject2);
        Exposure exposureDoseEmpty_1 = new Exposure(ExposureRaw.builder().id("id6").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(9.0).timeFromAdministration(8.0)
                .cycle(new Cycle(null, ANALYTE_1, null, null, false)).build(), subject1);
        Exposure exposureDoseEmpty_2 = new Exposure(ExposureRaw.builder().id("id7").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(11.0).timeFromAdministration(12.0)
                .cycle(new Cycle(null, ANALYTE_1, null, null, false)).build(), subject1);

        List<Exposure> events = newArrayList(exposureDose1_1, exposureDose1_2, exposureDose1_3,
                exposureDose2_1, exposureDose2_2, exposureDoseEmpty_1, exposureDoseEmpty_2);

        when(exposureDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);

        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> exposureLines =
                exposureService.getLineChart(new Datasets(DUMMY_ACUITY_DATASET_42), settings,
                        ExposureFilters.empty(), PopulationFilters.empty());

        softly.assertThat(exposureLines).hasSize(1);
        softly.assertThat(exposureLines.get(0).getData()).flatExtracting(OutputLineChartData::getSeriesBy)
                .containsExactlyInAnyOrder("50 mg", "100 mg", "(Empty)");

        List<OutputLineChartData> dataWithDose1 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().equals("50 mg")).collect(toList());
        softly.assertThat(dataWithDose1)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((ExposureTooltip) o.getName()).getColorByValue(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 3.0, 2, "50 mg", 1.0),
                tuple(12.0, 5.0, 1, "50 mg", 0.0));

        List<OutputLineChartData> dataWithDose2 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().equals("100 mg")).collect(toList());
        softly.assertThat(dataWithDose2)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((ExposureTooltip) o.getName()).getColorByValue(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 8.0, 1, "100 mg", 0.0),
                tuple(12.0, 9.0, 1, "100 mg", 0.0)
        );

        List<OutputLineChartData> dataWithDoseEmpty = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().equals("(Empty)")).collect(toList());
        softly.assertThat(dataWithDoseEmpty)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((ExposureTooltip) o.getName()).getColorByValue(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 9.0, 1, "(Empty)", 0.0),
                tuple(12.0, 11.0, 1, "(Empty)", 0.0)
        );
    }

    @Test
    public void shouldGetExposureLinesVisitAvg() {

        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings = getExposureSettings(
                VISIT, VISIT);

        Exposure exposureVisit1_1 = new Exposure(ExposureRaw.builder().id("id1").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(2.0).timeFromAdministration(8.0)
                .visitNumber(1)
                .cycle(new Cycle(null, ANALYTE_1, 1, null, false)).build(), subject1);
        Exposure exposureVisit1_2 = new Exposure(ExposureRaw.builder().id("id2").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(4.0).timeFromAdministration(8.0)
                .visitNumber(1)
                .cycle(new Cycle(null, ANALYTE_1, 1, null, false))
                .build(), subject1);
        Exposure exposureVisit1_3 = new Exposure(ExposureRaw.builder().id("id3").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(5.0).timeFromAdministration(12.0)
                .visitNumber(1)
                .cycle(new Cycle(null, ANALYTE_1, 1, null, false))
                .build(), subject1);
        Exposure exposureVisit2_1 = new Exposure(ExposureRaw.builder().id("id4").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(8.0).timeFromAdministration(8.0)
                .visitNumber(2)
                .cycle(new Cycle(null, ANALYTE_1, 2, null, false))
                .build(), subject1);
        Exposure exposureVisit2_2 = new Exposure(ExposureRaw.builder().id("id5").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(9.0).timeFromAdministration(12.0)
                .visitNumber(2)
                .cycle(new Cycle(null, ANALYTE_1, 2, null, false))
                .build(), subject1);

        List<Exposure> events = newArrayList(exposureVisit1_1, exposureVisit1_2, exposureVisit1_3,
                exposureVisit2_1, exposureVisit2_2);

        when(exposureDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);

        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> exposureLines =
                exposureService.getLineChart(new Datasets(DUMMY_ACUITY_DATASET_42), settings,
                        ExposureFilters.empty(), PopulationFilters.empty());

        softly.assertThat(exposureLines.get(0).getData()).flatExtracting(OutputLineChartData::getSeriesBy)
                .containsExactlyInAnyOrder("1", "2");

        List<OutputLineChartData> dataWithVisit1 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().equals("1")).collect(toList());
        softly.assertThat(dataWithVisit1)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((ExposureTooltip) o.getName()).getColorByValue(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 3.0, 2, "1", 1.0),
                tuple(12.0, 5.0, 1, "1", 0.0));

        List<OutputLineChartData> dataWithVisit2 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().equals("2")).collect(toList());
        softly.assertThat(dataWithVisit2)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((ExposureTooltip) o.getName()).getColorByValue(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 8.0, 1, "2", 0.0),
                tuple(12.0, 9.0, 1, "2", 0.0)
        );
    }

    @Test
    public void shouldGetExposureLinesCycleAvg() {

        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings = getExposureSettings(
                CYCLE, CYCLE);

        Exposure exposureCycle1_1 = new Exposure(ExposureRaw.builder().id("id1").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(2.0).timeFromAdministration(8.0)
                .treatmentCycle(CYCLE_1).cycle(new Cycle(CYCLE_1, ANALYTE_1, null, null, false)).build(), subject1);
        Exposure exposureCycle1_2 = new Exposure(ExposureRaw.builder().id("id2").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(4.0).timeFromAdministration(8.0)
                .treatmentCycle(CYCLE_1).cycle(new Cycle(CYCLE_1, ANALYTE_1, null, null, false)).build(), subject1);
        Exposure exposureCycle1_3 = new Exposure(ExposureRaw.builder().id("id3").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(5.0).timeFromAdministration(12.0)
                .treatmentCycle(CYCLE_1).cycle(new Cycle(CYCLE_1, ANALYTE_1, null, null, false)).build(), subject1);
        Exposure exposureCycle2_1 = new Exposure(ExposureRaw.builder().id("id4").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(8.0).timeFromAdministration(8.0)
                .treatmentCycle(CYCLE_2).cycle(new Cycle(CYCLE_2, ANALYTE_1, null, null, false)).build(), subject1);
        Exposure exposureCycle2_2 = new Exposure(ExposureRaw.builder().id("id5").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(9.0).timeFromAdministration(12.0)
                .treatmentCycle(CYCLE_2).cycle(new Cycle(CYCLE_2, ANALYTE_1, null, null, false)).build(), subject1);
        Exposure exposureCycle2_3 = new Exposure(ExposureRaw.builder().id("id5").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(3.0).timeFromAdministration(12.0)
                .treatmentCycle(CYCLE_2).cycle(new Cycle(CYCLE_2, ANALYTE_1, null, null, false)).build(), subject1);

        List<Exposure> events = newArrayList(exposureCycle1_1, exposureCycle1_2, exposureCycle1_3,
                exposureCycle2_1, exposureCycle2_2, exposureCycle2_3);

        when(exposureDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);

        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> exposureLines =
                exposureService.getLineChart(new Datasets(DUMMY_ACUITY_DATASET_42), settings,
                        ExposureFilters.empty(), PopulationFilters.empty());

        softly.assertThat(exposureLines.get(0).getData()).flatExtracting(OutputLineChartData::getSeriesBy)
                .containsExactlyInAnyOrder("cycle1", "cycle2");

        List<OutputLineChartData> dataWithCycle1 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().equals("cycle1")).collect(toList());
        softly.assertThat(dataWithCycle1)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((ExposureTooltip) o.getName()).getColorByValue(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 3.0, 2, "cycle1", 1.0),
                tuple(12.0, 5.0, 1, "cycle1", 0.0));

        List<OutputLineChartData> dataWithCycle2 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().equals("cycle2")).collect(toList());
        softly.assertThat(dataWithCycle2)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((ExposureTooltip) o.getName()).getColorByValue(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 8.0, 1, "cycle2", 0.0),
                tuple(12.0, 6.0, 2, "cycle2", 3.0)
        );
    }

    @Test
    public void shouldGetExposureLinesDoseVisitAvg() {

        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings = getExposureSettings(
                DOSE_PER_VISIT, DOSE);

        Exposure exposureVisit1Dose1_1 = new Exposure(ExposureRaw.builder().id("id1").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(2.0).timeFromAdministration(8.0)
                .treatment("50 mg").visitNumber(1)
                .cycle(new Cycle(null, ANALYTE_1, 1, null, false))
                .build(), subject1);
        Exposure exposureVisit1Dose1_2 = new Exposure(ExposureRaw.builder().id("id2").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(4.0).timeFromAdministration(8.0)
                .visitNumber(1).treatment("50 mg")
                .cycle(new Cycle(null, ANALYTE_1, 1, null, false))
                .build(), subject1);
        Exposure exposureVisit1Dose1_3 = new Exposure(ExposureRaw.builder().id("id3").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(5.0).timeFromAdministration(12.0)
                .visitNumber(1).treatment("50 mg")
                .cycle(new Cycle(null, ANALYTE_1, 1, null, false))
                .build(), subject1);
        Exposure exposureVisit2Dose1_1 = new Exposure(ExposureRaw.builder().id("id4").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(8.0).timeFromAdministration(8.0)
                .visitNumber(2).treatment("50 mg")
                .cycle(new Cycle(null, ANALYTE_1, 2, null, false))
                .build(), subject1);
        Exposure exposureVisit2Dose1_2 = new Exposure(ExposureRaw.builder().id("id5").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(9.0).timeFromAdministration(12.0)
                .visitNumber(2).treatment("50 mg")
                .cycle(new Cycle(null, ANALYTE_1, 2, null, false))
                .build(), subject1);
        Exposure exposureVisit2Dose2_1 = new Exposure(ExposureRaw.builder().id("id6").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(3.0).timeFromAdministration(8.0)
                .visitNumber(2).treatment("100 mg")
                .cycle(new Cycle(null, ANALYTE_1, 2, null, false))
                .build(), subject1);
        Exposure exposureVisit2Dose2_2 = new Exposure(ExposureRaw.builder().id("id7").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(4.0).timeFromAdministration(12.0)
                .visitNumber(2).treatment("100 mg")
                .cycle(new Cycle(null, ANALYTE_1, 2, null, false))
                .build(), subject1);

        List<Exposure> events = newArrayList(exposureVisit1Dose1_1, exposureVisit1Dose1_2, exposureVisit1Dose1_3,
                exposureVisit2Dose1_1, exposureVisit2Dose1_2,
                exposureVisit2Dose2_1, exposureVisit2Dose2_2);

        when(exposureDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);

        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> exposureLines =
                exposureService.getLineChart(new Datasets(DUMMY_ACUITY_DATASET_42), settings,
                        ExposureFilters.empty(), PopulationFilters.empty());

        softly.assertThat(exposureLines.get(0).getData()).flatExtracting(OutputLineChartData::getSeriesBy)
                .containsExactlyInAnyOrder("50 mg, visit 1", "50 mg, visit 2", "100 mg, visit 2");

        List<OutputLineChartData> dataWithDose1Visit1 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().equals("50 mg, visit 1")).collect(toList());
        softly.assertThat(dataWithDose1Visit1)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((ExposureTooltip) o.getName()).getColorByValue(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 3.0, 2, "50 mg", 1.0),
                tuple(12.0, 5.0, 1, "50 mg", 0.0));

        List<OutputLineChartData> dataWithDose1Visit2 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().equals("50 mg, visit 2")).collect(toList());
        softly.assertThat(dataWithDose1Visit2)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((ExposureTooltip) o.getName()).getColorByValue(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 8.0, 1, "50 mg", 0.0),
                tuple(12.0, 9.0, 1, "50 mg", 0.0)
        );

        List<OutputLineChartData> dataWithDose2Visit2 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().equals("100 mg, visit 2")).collect(toList());
        softly.assertThat(dataWithDose2Visit2)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((ExposureTooltip) o.getName()).getColorByValue(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 3.0, 1, "100 mg", 0.0),
                tuple(12.0, 4.0, 1, "100 mg", 0.0)
        );
    }

    @Test
    public void shouldGetExposureLinesDoseCycleAvg() {

        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings = getExposureSettings(
                DOSE_PER_CYCLE, DOSE_PER_CYCLE);

        Exposure exposureCycle1Dose1_1 = new Exposure(ExposureRaw.builder().id("id1").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(2.0).timeFromAdministration(8.0)
                .treatment("50 mg").treatmentCycle(CYCLE_1)
                .cycle(new Cycle(CYCLE_1, ANALYTE_1, null, null, false))
                .build(), subject1);
        Exposure exposureCycle1Dose1_2 = new Exposure(ExposureRaw.builder().id("id2").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(4.0).timeFromAdministration(8.0)
                .treatmentCycle(CYCLE_1).treatment("50 mg")
                .cycle(new Cycle(CYCLE_1, ANALYTE_1, null, null, false))
                .build(), subject1);
        Exposure exposureCycle1Dose1_3 = new Exposure(ExposureRaw.builder().id("id3").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(5.0).timeFromAdministration(12.0)
                .treatmentCycle(CYCLE_1).treatment("50 mg")
                .cycle(new Cycle(CYCLE_1, ANALYTE_1, null, null, false))
                .build(), subject1);
        Exposure exposureCycle2Dose1_1 = new Exposure(ExposureRaw.builder().id("id4").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(8.0).timeFromAdministration(8.0)
                .treatmentCycle(CYCLE_2).treatment("50 mg")
                .cycle(new Cycle(CYCLE_2, ANALYTE_1, null, null, false))
                .build(), subject1);
        Exposure exposureCycle2Dose1_2 = new Exposure(ExposureRaw.builder().id("id5").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(9.0).timeFromAdministration(12.0)
                .treatmentCycle(CYCLE_2).treatment("50 mg")
                .cycle(new Cycle(CYCLE_2, ANALYTE_1, null, null, false))
                .build(), subject1);
        Exposure exposureCycle2Dose2_1 = new Exposure(ExposureRaw.builder().id("id6").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(3.0).timeFromAdministration(8.0)
                .treatmentCycle(CYCLE_2).treatment("100 mg")
                .cycle(new Cycle(CYCLE_2, ANALYTE_1, null, null, false)).build(), subject1);
        Exposure exposureCycle2Dose2_2 = new Exposure(ExposureRaw.builder().id("id7").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(4.0).timeFromAdministration(12.0)
                .treatmentCycle(CYCLE_2).treatment("100 mg")
                .cycle(new Cycle(CYCLE_2, ANALYTE_1, null, null, false))
                .build(), subject1);
        Exposure exposureCycle2Dose2_3 = new Exposure(ExposureRaw.builder().id("id7").subjectId(SUBJECT_1)
                .analyte(ANALYTE_1).analyteConcentration(6.0).timeFromAdministration(12.0)
                .treatmentCycle(CYCLE_2).treatment("100 mg")
                .cycle(new Cycle(CYCLE_2, ANALYTE_1, null, null, false))
                .build(), subject1);

        List<Exposure> events = newArrayList(exposureCycle1Dose1_1, exposureCycle1Dose1_2, exposureCycle1Dose1_3,
                exposureCycle2Dose1_1, exposureCycle2Dose1_2,
                exposureCycle2Dose2_1, exposureCycle2Dose2_2, exposureCycle2Dose2_3);

        when(exposureDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);

        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> exposureLines =
                exposureService.getLineChart(new Datasets(DUMMY_ACUITY_DATASET_42), settings,
                        ExposureFilters.empty(), PopulationFilters.empty());

        softly.assertThat(exposureLines.get(0).getData()).flatExtracting(OutputLineChartData::getSeriesBy)
                .containsExactlyInAnyOrder("50 mg, cycle1", "50 mg, cycle2", "100 mg, cycle2");

        List<OutputLineChartData> dataWithDose1Cycle1 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().equals("50 mg, cycle1")).collect(toList());
        softly.assertThat(dataWithDose1Cycle1)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((ExposureTooltip) o.getName()).getColorByValue(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 3.0, 2, "50 mg, cycle1", 1.0),
                tuple(12.0, 5.0, 1, "50 mg, cycle1", 0.0));

        List<OutputLineChartData> dataWithDose1Cycle2 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().equals("50 mg, cycle2")).collect(toList());
        softly.assertThat(dataWithDose1Cycle2)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((ExposureTooltip) o.getName()).getColorByValue(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 8.0, 1, "50 mg, cycle2", 0.0),
                tuple(12.0, 9.0, 1, "50 mg, cycle2", 0.0)
        );

        List<OutputLineChartData> dataWithDose2Cycle2 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().equals("100 mg, cycle2")).collect(toList());
        softly.assertThat(dataWithDose2Cycle2)
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY,
                        o -> ((ExposureTooltip) o.getName()).getDataPoints(),
                        o -> ((ExposureTooltip) o.getName()).getColorByValue(),
                        o -> ((OutputErrorLineChartEntry) o).getStandardDeviation()).containsExactly(
                tuple(8.0, 3.0, 1, "100 mg, cycle2", 0.0),
                tuple(12.0, 5.0, 2, "100 mg, cycle2", 1.0)
        );
    }

    @Test
    public void shouldGetAvailableFiltersWithFilteredSinglePoints() {
        //Given
        when(coloringService.getColor(any(), any())).thenReturn(COLORS[0]);

        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings = getExposureSettings(
                SUBJECT_CYCLE, SUBJECT);

        ExposureFilters filters = (ExposureFilters) exposureService
                .getAvailableFilters(new Datasets(DUMMY_ACUITY_DATASET_42), ExposureFilters.empty(),
                        PopulationFilters.empty(), settings);
        softly.assertThat(filters.getAnalyte().getValues()).containsExactly("analyte2", "analyte1");
        softly.assertThat(filters.getTreatment().getValues()).containsExactly(null, "200 mg", "20 mg");
        softly.assertThat(filters.getAnalyteConcentration().getFrom()).isEqualTo(2.5);
        softly.assertThat(filters.getAnalyteConcentration().getTo()).isEqualTo(7.5);
        softly.assertThat(filters.getAnalyteUnit().getValues()).containsExactly("ml");
        softly.assertThat(filters.getTimeFromAdministration().getFrom()).isEqualTo(4.0);
        softly.assertThat(filters.getTimeFromAdministration().getTo()).isEqualTo(15.0);
        softly.assertThat(filters.getTreatmentCycle().getValues()).containsExactly("cycle2", "cycle1");
        softly.assertThat(filters.getVisit().getValues()).containsExactly(1, 2);
        softly.assertThat(filters.getDay().getValues()).containsExactly(null, 1, 2);
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(5);
    }

    @Test
    public void shouldGetAvailableFiltersWithCorrectFilteringOrder() {
        //Given
        when(coloringService.getColor(any(), any())).thenReturn(COLORS[0]);

        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings = getExposureSettings(
                SUBJECT_CYCLE, SUBJECT);
        ExposureFilters exposureFilters = new ExposureFilters();
        exposureFilters.setTreatment(new SetFilter<>(newHashSet("200 mg")));
        // firstly - filter by single cycle series, then apply filters. As a result, single point can be shown in some cases
        ExposureFilters filters = (ExposureFilters) exposureService
                .getAvailableFilters(new Datasets(DUMMY_ACUITY_DATASET_42), exposureFilters,
                        PopulationFilters.empty(), settings);
        softly.assertThat(filters.getAnalyte().getValues()).containsExactly("analyte1");
        softly.assertThat(filters.getVisit().getValues()).containsExactly(1);

    }

    @Test
    public void shouldGetExposureLinesSeriesBySubjectCycleColoredBySubject() {
        shouldTestColoringExposureLinesSeriesByXColoredByY(SUBJECT_CYCLE, SUBJECT, SUBJECT_1,
                SUBJECT_2);
    }

    @Test
    public void shouldGetExposureLinesSeriesBySubjectCycleColoredByCycle() {
        shouldTestColoringExposureLinesSeriesByXColoredByY(SUBJECT_CYCLE, CYCLE, CYCLE_1, CYCLE_2);
    }

    @Test
    public void shouldGetExposureLinesSeriesBySubjectAvgColoredBySubject() {
        shouldTestColoringExposureLinesSeriesByXColoredByY(SUBJECT, SUBJECT, SUBJECT_1, SUBJECT_2);
    }

    @Test
    public void shouldGetExposureLinesSeriesByAnalyteAvgColoredByNone() {
        when(coloringService.getColor(eq(Constants.NONE), any())).thenReturn(Colors.SKYBLUE.getCode());

        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> exposureLines =
                prepareExposureLines(ANALYTE, NONE);

        List<? extends OutputLineChartData> data = exposureLines.get(0).getData();
        softly.assertThat(data.isEmpty())
                .isFalse();
        softly.assertThat(data)
                .flatExtracting(OutputLineChartData::getSeries)
                .allMatch(s -> Colors.SKYBLUE.getCode().equals(s.getColor()));
    }

    @Test
    public void shouldGetTrellisOptions() {
        List<TrellisOptions<ExposureGroupByOptions>> result = exposureService.getTrellisOptions(DATASETS,
                ExposureFilters.empty(), PopulationFilters.empty());

        softly.assertThat(result).extracting("trellisedBy").containsOnly(ANALYTE);
        // because there is single cycle series with ANALYTE_3
        softly.assertThat(result).filteredOn(t -> t.getTrellisedBy().equals(ANALYTE))
                .flatExtracting("trellisOptions").containsOnly(ANALYTE_1, ANALYTE_2);
    }

    private ChartGroupByOptions<Exposure, ExposureGroupByOptions> getExposureSettings(
            ExposureGroupByOptions seriesBy, ExposureGroupByOptions colorBy) {
        return ChartGroupByOptions.<Exposure, ExposureGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY,
                        seriesBy.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY,
                        colorBy.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.NAME,
                        ALL_INFO.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS,
                        ExposureGroupByOptions.TIME_FROM_ADMINISTRATION.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                        ExposureGroupByOptions.ANALYTE_CONCENTRATION.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.ORDER_BY,
                        ExposureGroupByOptions.TIME_FROM_ADMINISTRATION.getGroupByOptionAndParams())
                .build();
    }

    private void shouldTestColoringExposureLinesSeriesByXColoredByY(ExposureGroupByOptions seriesByX,
                                                                    ExposureGroupByOptions colorByY,
                                                                    String colorGroup1, String colorGroup2) {
        shouldTestColoringExposureLinesSeriesByXColoredByY(seriesByX, colorByY, colorGroup1, colorGroup2,
                COLORS[0], COLORS[1]);
    }

    private void shouldTestColoringExposureLinesSeriesByXColoredByY(ExposureGroupByOptions seriesByX,
                                                                    ExposureGroupByOptions colorByY,
                                                                    String colorGroup1, String colorGroup2,
                                                                    String colorValue1, String colorValue2) {
        //Given
        when(coloringService.getColor(eq(colorGroup1), anyObject())).thenReturn(colorValue1);
        when(coloringService.getColor(eq(colorGroup2), anyObject())).thenReturn(colorValue2);

        when(exposureDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(newArrayList(exposure1, exposure2,
                        exposure3, exposure4, exposure5, exposure6, exposure7, exposure8, exposure9));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(newArrayList(subject1, subject2));

        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> exposureLines =
                prepareExposureLines(seriesByX, colorByY);

        List<OutputLineChartData> data1 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().contains(colorGroup1)).collect(toList());
        softly.assertThat(data1.isEmpty())
                .isFalse();
        softly.assertThat(data1)
                .flatExtracting(OutputLineChartData::getSeries)
                .allMatch(s -> colorValue1.equals(s.getColor()));

        List<OutputLineChartData> data2 = exposureLines.get(0).getData().stream()
                .filter(d -> d.getSeriesBy().toString().contains(colorGroup2)).collect(toList());
        softly.assertThat(data2.isEmpty())
                .isFalse();
        softly.assertThat(data2)
                .flatExtracting(OutputLineChartData::getSeries)
                .allMatch(s -> colorValue2.equals(s.getColor()));
    }

    private List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> prepareExposureLines(ExposureGroupByOptions seriesByX, ExposureGroupByOptions colorByY) {
        ChartGroupByOptions<Exposure, ExposureGroupByOptions> settings = getExposureSettings(seriesByX,
                colorByY);

        when(exposureDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(newArrayList(exposure1, exposure2,
                        exposure3, exposure4, exposure5, exposure6, exposure7, exposure8, exposure9));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(newArrayList(subject1, subject2));

        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> exposureLines =
                exposureService.getLineChart(new Datasets(DUMMY_ACUITY_DATASET_42), settings,
                        ExposureFilters.empty(), PopulationFilters.empty());

        softly.assertThat(exposureLines).hasSize(1);
        softly.assertThat(exposureLines.get(0).getData()).hasSize(2);
        return exposureLines;
    }

    @Test
    public void testGetLineChartColorBySeriesByVisit() {

        List<TrellisOptions<ExposureGroupByOptions>> colorBy = exposureService.getLineChartColorBy(DUMMY_ACUITY_DATASETS,
                getExposureSettingsForSeriesBy(ExposureGroupByOptions.VISIT));
        softly.assertThat(colorBy).extracting(TrellisOptions::getTrellisedBy).containsExactly(ExposureGroupByOptions.VISIT);
        final List<?> trellisOptions = colorBy.get(0).getTrellisOptions();
        softly.assertThat(trellisOptions.containsAll((Arrays.asList("1", "2", "3", "(Empty)")))).isTrue();
    }

    @Test
    public void testGetLineChartColorBySeriesByDose() {

        List<TrellisOptions<ExposureGroupByOptions>> colorBy = exposureService.getLineChartColorBy(DUMMY_ACUITY_DATASETS,
                getExposureSettingsForSeriesBy(ExposureGroupByOptions.DOSE));
        softly.assertThat(colorBy).extracting(TrellisOptions::getTrellisedBy).containsExactly(ExposureGroupByOptions.DOSE);
    }

    @Test
    public void testGetLineChartColorBySeriesByDoseAndVisit() {

        List<TrellisOptions<ExposureGroupByOptions>> colorBy = exposureService.getLineChartColorBy(DUMMY_ACUITY_DATASETS,
                getExposureSettingsForSeriesBy(DOSE_PER_VISIT));
        softly.assertThat(colorBy).extracting(TrellisOptions::getTrellisedBy)
                .containsExactlyInAnyOrder(DOSE_PER_VISIT);
        final List<?> trellisOptions = colorBy.get(0).getTrellisOptions();
        softly.assertThat(trellisOptions.containsAll(Arrays.asList(
                "(Empty), visit 1",
                "(Empty), visit 2",
                "(Empty), visit 3",
                "20 mg, visit 1",
                "23 mg, visit (Empty)",
                "200 mg, visit 1"))).isTrue();
    }

    @Test
    public void testGetLineChartColorBySeriesByAnalyte() {

        List<TrellisOptions<ExposureGroupByOptions>> colorBy = exposureService.getLineChartColorBy(DUMMY_ACUITY_DATASETS,
                getExposureSettingsForSeriesBy(ANALYTE));
        softly.assertThat(colorBy).extracting(TrellisOptions::getTrellisedBy)
                .containsExactly(NONE);
    }

    @Test
    public void testGetLineChartColorBySeriesBySubject() {

        List<TrellisOptions<ExposureGroupByOptions>> colorBy = exposureService.getLineChartColorBy(DUMMY_ACUITY_DATASETS,
                getExposureSettingsForSeriesBy(SUBJECT));
        softly.assertThat(colorBy).extracting(TrellisOptions::getTrellisedBy)
                .containsExactly(SUBJECT);
    }

    @Test
    public void testGetLineChartColorBySeriesBySubjectCycle() {

        List<TrellisOptions<ExposureGroupByOptions>> colorBy = exposureService.getLineChartColorBy(DUMMY_ACUITY_DATASETS,
                getExposureSettingsForSeriesBy(SUBJECT_CYCLE));
        softly.assertThat(colorBy).extracting(TrellisOptions::getTrellisedBy)
                .containsExactly(SUBJECT, CYCLE, DOSE, DAY, VISIT);
    }

    private ChartGroupByOptions<Exposure, ExposureGroupByOptions> getExposureSettingsForSeriesBy(ExposureGroupByOptions seriesBy) {
        return ChartGroupByOptions.<Exposure, ExposureGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY,
                        seriesBy.getGroupByOptionAndParams()).build();
    }

    @Test
    public void shouldGetDetectDetailsOnDemand() {
        final List<Exposure> exposureList = Arrays.asList(exposure1, exposure2, exposure3, exposure4, exposureNullVisit);
        final Set<String> ids = exposureList.stream().map(Exposure::getId).collect(toSet());
        List<SortAttrs> exposureDefaultSortAttr = Arrays.asList(
                new SortAttrs("subjectId", false),
                new SortAttrs("visitNumber", false),
                new SortAttrs("cycle", false),
                new SortAttrs("nominalDay", false),
                new SortAttrs("nominalHour", false),
                new SortAttrs("nominalMinute", false));
        final List<Map<String, String>> dodData = exposureService.getDetailsOnDemandData(DUMMY_ACUITY_DATASETS, ids,
                exposureDefaultSortAttr, 0, Integer.MAX_VALUE);
        final Exposure exposure = exposureList.get(1);
        final ExposureRaw event = exposure.getEvent();
        final Map<String, String> dod = dodData.get(0);
        softly.assertThat(dodData).hasSize(exposureList.size());
        softly.assertThat(dod).hasSize(16);
        softly.assertThat(exposure.getSubjectCode()).isEqualTo(dod.get("subjectId"));
        softly.assertThat(exposure.getSubject().getClinicalStudyCode()).isEqualTo(dod.get("studyId"));
        softly.assertThat(event.getAnalyte()).isEqualTo(dod.get("analyte"));
        softly.assertThat(event.getVisitNumber().toString()).isEqualTo(dod.get("visitNumber"));
        softly.assertThat(event.getVisitDate()).isInSameDayAs(dod.get("visitDate"));
        softly.assertThat(event.getTreatmentCycle()).isEqualTo(dod.get("cycle"));
        softly.assertThat(event.getTreatment()).isEqualTo(dod.get("nominalDose"));
        softly.assertThat(event.getNominalDay().toString()).isEqualTo(dod.get("nominalDay"));
        softly.assertThat(decimalFormat.format(event.getNominalHour())).isEqualTo(dod.get("nominalHour"));
        softly.assertThat(event.getNominalMinute().toString()).isEqualTo(dod.get("nominalMinute"));
        softly.assertThat(decimalFormat.format(event.getAnalyteConcentration())).isEqualTo(dod.get("analyteConcentration"));
        softly.assertThat(event.getAnalyteUnit()).isEqualTo(dod.get("analyteUnit"));
        softly.assertThat(decimalFormat.format(event.getLowerLimit())).isEqualTo(dod.get("LLOQ"));
        softly.assertThat(event.getActualSamplingDate()).isInSameDayAs(dod.get("actualSamplingDate"));


    }
}
