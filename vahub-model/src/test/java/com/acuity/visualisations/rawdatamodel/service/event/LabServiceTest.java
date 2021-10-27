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

import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.dataproviders.LabDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.BoxPlotTests;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.LabTests;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.RangeChartTests;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.ShiftPlotTests;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.Device;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.StudyRules;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.RangeChartSeries;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedShiftPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuityDataset;

import org.apache.commons.math3.util.Precision;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions.ARM;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions.MEASUREMENT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions.MEASUREMENT_TIME_POINT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions.STUDY_DEFINED_WEEK;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions.VISIT_NUMBER;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonMap;
import static java.util.stream.Collectors.toSet;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Category(LabTests.class)
public class LabServiceTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Autowired
    private LabService labService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private LabDatasetsDataProvider mockLabDatasetsDataProvider;
    @MockBean
    private InfoService mockInfoService;

    private DoDCommonService doDCommonService = new DoDCommonService();

    private static final Subject SUBJECT_1_WITH_ARM = Subject.builder()
            .subjectCode("subj-1")
            .subjectId("subj-1")
            .clinicalStudyCode("study 1")
            .actualArm("arm-1")
            .firstTreatmentDate(DateUtils.toDate("01.01.2000"))
            .dateOfRandomisation(DateUtils.toDate("01.12.1999"))
            .build();
    private static final Subject SUBJECT_2_WITH_ARM = Subject.builder()
            .subjectCode("subj-2")
            .subjectId("subj-2")
            .clinicalStudyCode("study 1")
            .actualArm("arm-2")
            .firstTreatmentDate(DateUtils.toDate("01.01.2000"))
            .dateOfRandomisation(DateUtils.toDate("01.12.1999"))
            .build();
    private static final Subject SUBJECT_1_NO_ARM = Subject.builder()
            .subjectCode("subj-1")
            .subjectId("subj-1")
            .clinicalStudyCode("study 1")
            .firstTreatmentDate(DateUtils.toDate("01.01.2000"))
            .dateOfRandomisation(DateUtils.toDate("01.12.1999"))
            .build();
    private static final Subject SUBJECT_2_NO_ARM = Subject.builder()
            .subjectCode("subj-2")
            .subjectId("subj-2")
            .clinicalStudyCode("study 1")
            .firstTreatmentDate(DateUtils.toDate("01.01.2000"))
            .dateOfRandomisation(DateUtils.toDate("01.12.1999"))
            .build();

    private static Subject subject = Subject.builder().subjectId("sid1").baselineDate(DateUtils.toDateTime("2001-01-01T00:00:00")).build();

    private static Lab lab1 = new Lab(LabRaw.builder().id("lid1").labCode("code1").measurementTimePoint(DateUtils.toDateTime("2001-01-01T00:00:00"))
            .value(7.0).refLow(3.0).refHigh(8.0).unit("%").build(), subject);
    private static Lab lab2 = new Lab(LabRaw.builder().id("lid2").labCode("code2").measurementTimePoint(DateUtils.toDateTime("2002-01-01T00:00:00"))
            .value(9.0).refLow(8.0).refHigh(18.0).unit("%").build(), subject);
    private static Lab lab3 = new Lab(LabRaw.builder().id("lid3").labCode("code3").measurementTimePoint(DateUtils.toDateTime("2001-01-01T23:59:59"))
            .value(11.0).refLow(15.0).refHigh(19.0).unit("%").build(), subject);
    private static Lab lab4 = new Lab(LabRaw.builder().id("lid4").labCode("code4").measurementTimePoint(DateUtils.toDateTime("2002-12-31T23:59:59"))
            .value(4.0).refLow(5.0).refHigh(8.0).unit("%").build(), subject);
    private static Lab lab5 = new Lab(LabRaw.builder().id("lid5").labCode("code5").measurementTimePoint(DateUtils.toDateTime("1999-12-20T23:59:59"))
            .value(9.0).refLow(5.0).refHigh(8.0).unit("mmol/L").build(), subject);
    private static Lab lab6 = new Lab(LabRaw.builder().id("lid6").labCode("code6").measurementTimePoint(DateUtils.toDateTime("2001-01-01T00:00:00"))
            .value(14.0).refLow(12.0).refHigh(13.00).unit("%").build(), subject);
    private static Lab lab7 = new Lab(LabRaw.builder().id("lid7").labCode("code7").measurementTimePoint(DateUtils.toDateTime("2000-12-31T23:59:59"))
            .value(9.0).refLow(10.0).unit("%").build(), subject);
    private static Lab lab8 = new Lab(LabRaw.builder().id("lid8").labCode("code8").measurementTimePoint(DateUtils.toDateTime("2001-12-21T23:59:58"))
            .value(9.0).refLow(10.0).unit("%").build(), subject);
    private static Lab lab9 = new Lab(LabRaw.builder().id("lid9").labCode("code9").measurementTimePoint(DateUtils.toDateTime("2000-12-31T23:59:59"))
            .value(11.0).refHigh(12.0).unit("sec").build(), subject);
    private static Lab lab10 = new Lab(LabRaw.builder().id("lid10").labCode("code10").measurementTimePoint(DateUtils.toDateTime("2002-06-30T23:59:59"))
            .value(15.0).refHigh(12.0).unit("%").build(), subject);
    private static Lab lab11 = new Lab(LabRaw.builder().id("lid11").labCode("code11").measurementTimePoint(DateUtils.toDateTime("1999-12-31T23:59:59"))
            .refLow(7.0).refHigh(12.0).build(), subject);
    private static Lab lab12 = new Lab(LabRaw.builder().id("lid12").labCode("code12").measurementTimePoint(DateUtils.toDateTime("1999-12-31T23:59:59"))
            .value(11.0).unit("%").build(), subject);
    private static Lab lab13 = new Lab(LabRaw.builder().id("lid13").labCode("code3").measurementTimePoint(DateUtils.toDateTime("2000-12-31T23:59:59"))
            .value(14.0).refLow(15.0).refHigh(19.0).unit("%").build(), subject);
    private static Lab lab14 = new Lab(LabRaw.builder().id("lid14").labCode("code3").value(14.0).refLow(15.0).refHigh(19.0).unit("%").build(), subject);

    private static Lab[] labs = {lab1, lab2, lab3, lab4, lab5, lab6, lab7, lab8, lab9, lab10, lab11, lab12, lab13, lab14};
    private static StudyRules studyRules = new StudyRules();


    @Before
    public void setUp() {
        Lab lab1 = new Lab(LabRaw.builder()
                .labCode("ALBUMIN")
                .unit("g/dL")
                .value(1.)
                .visitNumber(1.)
                .analysisVisit(1.)
                .measurementTimePoint(DateUtils.toDate("01.01.2000"))
                .calcDaysSinceFirstDoseIfNull(true)
                .sourceType("Sponsor")
                .build(), SUBJECT_1_WITH_ARM);
        Lab lab2 = new Lab(LabRaw.builder()
                .labCode("ALT")
                .unit("U/L")
                .visitNumber(1.)
                .analysisVisit(1.)
                .calcDaysSinceFirstDoseIfNull(true)
                .measurementTimePoint(DateUtils.toDate("01.03.2000"))
                .sourceType("Patient")
                .build(), SUBJECT_2_WITH_ARM);

        when(mockLabDatasetsDataProvider.loadData(any())).thenReturn(newArrayList(lab1, lab2));
        studyRules.setLimitXAxisToVisit(false);
        when(mockInfoService.getStudyRules(any())).thenReturn(studyRules);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT_1_WITH_ARM, SUBJECT_2_WITH_ARM));
    }

    /*************************
     *  Trellising tests
     */

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotTrellisOptions() {
        List<TrellisOptions<LabGroupByOptions>> result = labService.getTrellisOptions(DATASETS, LabFilters.empty(), PopulationFilters.empty());

        softly.assertThat(result).extracting("trellisedBy").containsOnly(MEASUREMENT, ARM);
        softly.assertThat(result)
                .filteredOn(t -> t.getTrellisedBy().equals(MEASUREMENT)).flatExtracting("trellisOptions").containsOnly("ALBUMIN (g/dL)", "ALT (U/L)");
        softly.assertThat(result).filteredOn(t -> t.getTrellisedBy().equals(ARM)).flatExtracting("trellisOptions").containsOnly("arm-1", "arm-2");
    }

    /*************************
     *  x-axis option tests
     */

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotXAxisOptions() {
        AxisOptions<LabGroupByOptions> result = labService.getAvailableBoxPlotXAxis(DATASETS, LabFilters.empty(), PopulationFilters.empty());

        softly.assertThat(result.getOptions())
                .extracting(AxisOption::getGroupByOption)
                .containsExactlyInAnyOrder(VISIT_NUMBER, STUDY_DEFINED_WEEK, MEASUREMENT_TIME_POINT);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotXAxisOptionsWithJustVisitNumber() {
        studyRules.setLimitXAxisToVisit(true);
        AcuityDataset ds = new AcuityDataset(101L);
        ds.setClinicalStudyCode("testStudyCode");
        final Datasets dataset = new Datasets(ds);
        when(mockInfoService.limitXAxisToVisit(dataset)).thenReturn(true);

        AxisOptions<LabGroupByOptions> result = labService.getAvailableBoxPlotXAxis(
                dataset,
                LabFilters.empty(),
                PopulationFilters.empty()
        );

        softly.assertThat(result.getOptions())
                .extracting(AxisOption::getGroupByOption)
                .containsExactlyInAnyOrder(VISIT_NUMBER);
        studyRules.setLimitXAxisToVisit(false);
    }

    @Test
    @Category(RangeChartTests.class)
    public void shouldGetRangeSeriesByOptions() {
        List<TrellisOptions<LabGroupByOptions>> result = labService.getRangeSeriesByOptions(DATASETS, LabFilters.empty(), PopulationFilters.empty());

        softly.assertThat(result)
                .extracting(TrellisOptions::getTrellisedBy)
                .containsExactlyInAnyOrder(LabGroupByOptions.SOURCE_TYPE);

    }

    /*************************
     *  Chart tests
     */

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotData() {
        // Given

        final ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();
        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .build();
        // When

        final List<TrellisedBoxPlot<Lab, LabGroupByOptions>> result = labService.getBoxPlot(DATASETS, settingsFiltered,
                LabFilters.empty(), PopulationFilters.empty());

        // Then
        softly.assertThat(result).isNotEmpty();
        softly.assertThat(result)
                .flatExtracting(TrellisedBoxPlot::getTrellisedBy)
                .isNotEmpty();
    }

    @Test
    @Category(ShiftPlotTests.class)
    public void shouldGetShiftPlotData() {
        // Given
        final ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.BASELINE_VALUE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.UNIT, LabGroupByOptions.UNIT.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();
        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .build();

        // When
        List<TrellisedShiftPlot<Lab, LabGroupByOptions>> result =
                labService.getShiftPlot(DATASETS, settingsFiltered, LabFilters.empty(), PopulationFilters.empty());

        // Then
        softly.assertThat(result).isNotEmpty();
        softly.assertThat(result)
                .flatExtracting(TrellisedShiftPlot::getTrellisedBy)
                .isNotEmpty();
    }

    @Test
    @Category(RangeChartTests.class)
    public void shouldGetRangePlotData() {

        final ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, LabGroupByOptions.ARM_AND_SOURCE_TYPE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.NAME, LabGroupByOptions.SOURCE_TYPE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();
        final Map<LabGroupByOptions, Object> filterByTrellis1 = singletonMap(LabGroupByOptions.MEASUREMENT, "ALBUMIN (g/dL)");
        final Map<LabGroupByOptions, Object> filterByTrellis2 = singletonMap(LabGroupByOptions.MEASUREMENT, "ALT (U/L)");

        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(filterByTrellis1)
                .withFilterByTrellisOption(filterByTrellis2)
                .build();
        // When
        List<TrellisedRangePlot<Lab, LabGroupByOptions>> result =
                labService.getRangePlot(DATASETS, settingsFiltered, LabFilters.empty(), PopulationFilters.empty(), StatType.MEDIAN);

        // Then
        softly.assertThat(result)
                .flatExtracting(TrellisedRangePlot::getTrellisedBy)
                .extracting(o -> o.getTrellisOption().toString())
                .containsExactly("ALBUMIN (g/dL)", "ALT (U/L)");
        softly.assertThat(result)
                .flatExtracting(TrellisedRangePlot::getData)
                .extracting(RangeChartSeries::getName)
                .containsExactlyInAnyOrder("arm-1, Sponsor", "arm-2, Patient");
    }

    @Test
    @Category(RangeChartTests.class)
    public void shouldGetRangePlotDataRefValueFilterByTrellis() {

        final ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.REF_RANGE_NORM_VALUE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, LabGroupByOptions.ARM_AND_SOURCE_TYPE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.NAME, LabGroupByOptions.SOURCE_TYPE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();
        final Map<LabGroupByOptions, Object> filterByTrellis = singletonMap(LabGroupByOptions.MEASUREMENT, "ALBUMIN (Ref. ranges)");

        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(filterByTrellis)
                .build();
        // When
        List<TrellisedRangePlot<Lab, LabGroupByOptions>> result =
                labService.getRangePlot(DATASETS, settingsFiltered, LabFilters.empty(), PopulationFilters.empty(), StatType.MEDIAN);

        // Then
        softly.assertThat(result).isNotEmpty();
        softly.assertThat(result)
                .flatExtracting(TrellisedRangePlot::getTrellisedBy)
                .extracting(o -> o.getTrellisOption().toString())
                .containsExactly("ALBUMIN (Ref. ranges)");
        softly.assertThat(result)
                .flatExtracting(TrellisedRangePlot::getData)
                .extracting(RangeChartSeries::getName)
                .containsExactlyInAnyOrder("arm-1, Sponsor");
    }


/*************************
     *  Selection tests
***********************/


    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotSelectionWhenTrellisedByMeasurement() {
        // Given
        setUpMockData();

        final ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();


        final HashMap<LabGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "ALT (g/dL)");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "1");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(X_AXIS, "2");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems3 = new HashMap<>();
        selectedItems3.put(X_AXIS, "3");


        final List<ChartSelectionItemRange<Lab, LabGroupByOptions, Double>> selectionItems = Arrays.asList(
                ChartSelectionItemRange.of(selectedTrellises, selectedItems1, 10., 11.),
                ChartSelectionItemRange.of(selectedTrellises, selectedItems2, 10., 11.),
                ChartSelectionItemRange.of(selectedTrellises, selectedItems3, 10., 11.)
        );


        // When
        SelectionDetail result = labService.getRangedSelectionDetails(
                DATASETS, LabFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems));

        // Then
        softly.assertThat(result.getEventIds()).containsOnly("e1", "e2", "e4");
        softly.assertThat(result.getSubjectIds()).containsOnly("subj-1", "subj-2");
        softly.assertThat(result.getTotalEvents()).isEqualTo(4);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotSelectionWhenTrellisedByMeasurementAndArm() {
        // Given
        setUpMockData();

        final ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.STUDY_DEFINED_WEEK.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();

        final HashMap<LabGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "ALT (g/dL)");
        selectedTrellises.put(ARM, "arm-2");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "5.0");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(X_AXIS, "6.0");

        final List<ChartSelectionItemRange<Lab, LabGroupByOptions, Double>> selectionItems = Arrays.asList(
                ChartSelectionItemRange.of(selectedTrellises, selectedItems1, 11., 12.),
                ChartSelectionItemRange.of(selectedTrellises, selectedItems2, 11., 12.)
        );


        // When
        SelectionDetail result = labService.getRangedSelectionDetails(
                DATASETS, LabFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems));

        // Then
        softly.assertThat(result.getEventIds()).containsOnly("e2", "e3", "e4");
        softly.assertThat(result.getSubjectIds()).containsOnly("subj-2");
        softly.assertThat(result.getTotalEvents()).isEqualTo(4);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotSelectionWhenSingleXMatch() {
        // Given
        setUpMockData();

        final ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.STUDY_DEFINED_WEEK.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();

        final HashMap<LabGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "ALT (g/dL)");
        selectedTrellises.put(ARM, "arm-2");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "5.0");

        final List<ChartSelectionItemRange<Lab, LabGroupByOptions, Double>> selectionItems = Arrays.asList(
                ChartSelectionItemRange.of(selectedTrellises, selectedItems1, 11., 11.)
        );

        // When
        SelectionDetail result = labService.getRangedSelectionDetails(
                DATASETS, LabFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems));

        // Then
        softly.assertThat(result.getEventIds()).containsOnly("e2", "e4");
        softly.assertThat(result.getSubjectIds()).containsOnly("subj-2");
        softly.assertThat(result.getTotalEvents()).isEqualTo(4);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetRangePlotSelectionWhenSingleXMatch() {
        // Given
        setUpMockData();

        final ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.STUDY_DEFINED_WEEK.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();

        final HashMap<LabGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "ALT (g/dL)");
        selectedTrellises.put(ARM, "arm-2");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "5.0");

        final List<ChartSelectionItem<Lab, LabGroupByOptions>> selectionItems = Arrays.asList(
                ChartSelectionItem.of(selectedTrellises, selectedItems1)
        );

        // When
        SelectionDetail result = labService.getSelectionDetails(
                DATASETS, LabFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems));

        // Then
        softly.assertThat(result.getEventIds()).containsOnly("e2", "e4");
        softly.assertThat(result.getSubjectIds()).containsOnly("subj-2");
        softly.assertThat(result.getTotalEvents()).isEqualTo(4);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotSelectionWhenXAxisIsBinned() {
        // Given
        setUpMockData();
        final ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.MEASUREMENT_TIME_POINT.getGroupByOptionAndParams(
                        GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_SIZE, 2)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()
                ))
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();

        final HashMap<LabGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "ALT (g/dL)");
        selectedTrellises.put(ARM, "arm-2");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "0 - 1");

        final List<ChartSelectionItemRange<Lab, LabGroupByOptions, Double>> selectionItems = Arrays.asList(
                ChartSelectionItemRange.of(selectedTrellises, selectedItems1, 11., 12.)
        );

        // When
        SelectionDetail result = labService.getRangedSelectionDetails(
                DATASETS, LabFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems));

        // Then
        softly.assertThat(result.getEventIds()).containsOnly("e2", "e4");
        softly.assertThat(result.getSubjectIds()).containsOnly("subj-2");
        softly.assertThat(result.getTotalEvents()).isEqualTo(4);
    }

    @Test
    public void shouldGetLinePlotSelectionWhenSourceType() {
        // Given
        setUpMockData();

        final ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.STUDY_DEFINED_WEEK.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, LabGroupByOptions.SOURCE_TYPE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();

        final HashMap<LabGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "ALT (g/dL)");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "5.0");
        selectedItems1.put(SERIES_BY, "Sponsor");

        final List<ChartSelectionItem<Lab, LabGroupByOptions>> selectionItems = Collections.singletonList(
                ChartSelectionItem.of(selectedTrellises, selectedItems1)
        );

        // When
        SelectionDetail result = labService.getSelectionDetails(
                DATASETS, LabFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems));

        // Then
        softly.assertThat(result.getEventIds()).containsOnly("e4");
    }

    @Test
    @Category(ShiftPlotTests.class)
    public void shouldGetShiftPlotSelectionWhenTrellisedByMeasurement() {
        // Given
        setUpMockData();

        final ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.BASELINE_VALUE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();

        final HashMap<LabGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "ALT (g/dL)");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "100.2423543443546456455");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(X_AXIS, "200.0");

        final List<ChartSelectionItemRange<Lab, LabGroupByOptions, Double>> selectionItems = Arrays.asList(
                ChartSelectionItemRange.of(selectedTrellises, selectedItems1, 10., 11.),
                ChartSelectionItemRange.of(selectedTrellises, selectedItems2, 10., 11.)
        );
        // When
        SelectionDetail result = labService.getRangedSelectionDetails(
                DATASETS, LabFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems));


        // Then
        softly.assertThat(result.getEventIds()).containsOnly("e1", "e2", "e4");
        softly.assertThat(result.getSubjectIds()).containsOnly("subj-1", "subj-2");
        softly.assertThat(result.getTotalEvents()).isEqualTo(4);
    }

    @Test
    public void shouldGetDetectDetailsOnDemand() throws Exception {

        // Given
        List<Lab> labs = setUpMockData();
        Set<String> labsIds = labs.stream().map(Lab::getId).collect(toSet());

        // When
        List<Map<String, String>> doDData = labService.getDetailsOnDemandData(DUMMY_DETECT_DATASETS, labsIds,
                Collections.emptyList(), 0, Integer.MAX_VALUE);

        // Then
        Lab lab = labs.get(0);
        Map<String, String> dod = doDData.get(0);

        softly.assertThat(doDData).hasSize(labs.size());
        softly.assertThat(dod.size()).isEqualTo(25);
        softly.assertThat(lab.getSubject().getSubjectCode()).isEqualTo(dod.get("subjectId"));
        softly.assertThat(lab.getSubject().getClinicalStudyCode()).isEqualTo(dod.get("studyId"));

        softly.assertThat(lab.getEvent().getLabCode()).isEqualTo(dod.get("measurementName"));
        softly.assertThat(lab.getEvent().getCategory()).isEqualTo(dod.get("measurementCategory"));
        softly.assertThat(Double.parseDouble(lab.getEvent().getValue().toString())).isEqualTo(Double.parseDouble(dod.get("resultValue")));
        softly.assertThat(lab.getEvent().getUnit()).isEqualTo(dod.get("resultUnit"));
        softly.assertThat(Double.parseDouble(Double.toString(Precision.round(lab.getEvent().getBaseline(), 2))))
                .isEqualTo(Double.parseDouble(dod.get("baselineValue")));
        softly.assertThat(Double.parseDouble(lab.getEvent().getChangeFromBaseline().toString())).isEqualTo(Double.parseDouble(dod.get("changeFromBaseline")));
        softly.assertThat(Double.parseDouble(lab.getPercentChangeFromBaseline().toString()))
                .isEqualTo(Double.parseDouble(dod.get("percentChangeFromBaseline")));
        softly.assertThat(lab.getEvent().getBaselineFlag()).isEqualTo(dod.get("baselineFlag"));
        softly.assertThat(Double.parseDouble(lab.getEvent().getRefHigh().toString())).isEqualTo(Double.parseDouble(dod.get("upperRefRangeValue")));
        softly.assertThat(Double.parseDouble(lab.getEvent().getRefLow().toString())).isEqualTo(Double.parseDouble(dod.get("lowerRefRangeValue")));
        softly.assertThat(Double.parseDouble(lab.getReferenceRangeNormalisedValue().toString())).isEqualTo(Double.parseDouble(dod.get("refRangeNormValue")));
        softly.assertThat(Double.parseDouble(lab.getTimesLowerReferenceRange().toString())).isEqualTo(Double.parseDouble(dod.get("timesLowerRefValue")));
        softly.assertThat(Double.parseDouble(lab.getTimesUpperReferenceRange().toString())).isEqualTo(Double.parseDouble(dod.get("timesUpperRefValue")));
        softly.assertThat(lab.getDaysSinceFirstDose().toString()).isEqualTo(dod.get("daysOnStudy"));

        softly.assertThat(DaysUtil.toDateTimeString(lab.getEvent().getMeasurementTimePoint())).isEqualTo(dod.get("measurementTimePoint"));
        softly.assertThat(Double.parseDouble(lab.getEvent().getVisitNumber().toString())).isEqualTo(Double.parseDouble(dod.get("visitNumber")));
        softly.assertThat(Double.parseDouble(lab.getEvent().getAnalysisVisit().toString())).isEqualTo(Double.parseDouble(dod.get("analysisVisit")));
        softly.assertThat(lab.getEvent().getSourceType()).isEqualTo(dod.get("sourceType"));
        softly.assertThat(lab.getEvent().getDeviceName()).isEqualTo(dod.get("deviceName"));
        softly.assertThat(lab.getEvent().getDeviceVersion()).isEqualTo(dod.get("deviceVersion"));
        softly.assertThat(lab.getEvent().getDeviceType()).isEqualTo(dod.get("deviceType"));
    }

    @Test
    public void shouldGetDetectDetailsOnDemandColumnsInCorrectOrder() throws Exception {

        // Given
        List<Lab> labs = setUpMockData();

        // When
        Map<String, String> columns = doDCommonService.getDoDColumns(Column.DatasetType.DETECT, labs);

        // Then
        softly.assertThat(columns.keySet())
                .containsExactly("studyId", "subjectId", "measurementCategory", "measurementName", "measurementTimePoint", "daysOnStudy",
                        "analysisVisit", "visitNumber", "resultValue", "resultUnit", "baselineValue",
                        "changeFromBaseline", "percentChangeFromBaseline", "baselineFlag", "refRangeNormValue",
                        "timesUpperRefValue", "timesLowerRefValue", "lowerRefRangeValue", "upperRefRangeValue", "sourceType",
                        "deviceName", "deviceVersion", "deviceType");
    }

    @Test
    public void shouldGetAcuityDetailsOnDemand() throws Exception {

        // Given
        List<Lab> labs = setUpMockData();
        Set<String> labsIds = labs.stream().map(Lab::getId).collect(toSet());

        // When
        List<Map<String, String>> doDData = labService.getDetailsOnDemandData(DUMMY_ACUITY_DATASETS, labsIds,
                Collections.emptyList(), 0, Integer.MAX_VALUE);

        // Then
        Lab lab = labs.get(0);
        Map<String, String> dod = doDData.get(0);

        softly.assertThat(doDData).hasSize(labs.size());
        softly.assertThat(dod.size()).isEqualTo(26);
        softly.assertThat(lab.getSubject().getSubjectCode()).isEqualTo(dod.get("subjectId"));
        softly.assertThat(lab.getSubject().getClinicalStudyCode()).isEqualTo(dod.get("studyId"));

        softly.assertThat(lab.getEvent().getLabCode()).isEqualTo(dod.get("measurementName"));
        softly.assertThat(lab.getEvent().getCategory()).isEqualTo(dod.get("measurementCategory"));
        softly.assertThat(Double.parseDouble(lab.getEvent().getValue().toString())).isEqualTo(Double.parseDouble(dod.get("resultValue")));
        softly.assertThat(lab.getEvent().getUnit()).isEqualTo(dod.get("resultUnit"));
        softly.assertThat(Double.parseDouble(Double.toString(Precision.round(lab.getEvent().getBaseline(), 2))))
                .isEqualTo(Double.parseDouble(dod.get("baselineValue")));
        softly.assertThat(Double.parseDouble(lab.getEvent().getChangeFromBaseline().toString())).
                isEqualTo(Double.parseDouble(dod.get("changeFromBaseline")));
        softly.assertThat(Double.parseDouble(lab.getPercentChangeFromBaseline().toString()))
                .isEqualTo(Double.parseDouble(dod.get("percentChangeFromBaseline")));
        softly.assertThat(lab.getEvent().getBaselineFlag()).isEqualTo(dod.get("baselineFlag"));
        softly.assertThat(Double.parseDouble(lab.getEvent().getRefHigh().toString())).isEqualTo(Double.parseDouble(dod.get("upperRefRangeValue")));
        softly.assertThat(Double.parseDouble(lab.getEvent().getRefLow().toString())).isEqualTo(Double.parseDouble(dod.get("lowerRefRangeValue")));
        softly.assertThat(Double.parseDouble(lab.getReferenceRangeNormalisedValue().toString())).isEqualTo(Double.parseDouble(dod.get("refRangeNormValue")));
        softly.assertThat(Double.parseDouble(lab.getTimesLowerReferenceRange().toString())).isEqualTo(Double.parseDouble(dod.get("timesLowerRefValue")));
        softly.assertThat(Double.parseDouble(lab.getTimesUpperReferenceRange().toString())).isEqualTo(Double.parseDouble(dod.get("timesUpperRefValue")));
        softly.assertThat(lab.getDaysSinceFirstDose().toString()).isEqualTo(dod.get("daysOnStudy"));
        final Date measurementTimePoint = lab.getEvent().getMeasurementTimePoint();
        softly.assertThat(DaysUtil.toDateTimeString(measurementTimePoint)).isEqualTo(dod.get("measurementTimePoint"));
        softly.assertThat(Double.parseDouble(lab.getEvent().getVisitNumber().toString())).isEqualTo(Double.parseDouble(dod.get("visitNumber")));
        softly.assertThat(lab.getEvent().getProtocolScheduleTimepoint()).isEqualTo(dod.get("protocolScheduleTimepoint"));
        softly.assertThat(lab.getEvent().getValueDipstick()).isEqualTo(dod.get("valueDipstick"));
        softly.assertThat(lab.getEvent().getSourceType()).isEqualTo(dod.get("sourceType"));
        softly.assertThat(lab.getEvent().getDeviceName()).isEqualTo(dod.get("deviceName"));
        softly.assertThat(lab.getEvent().getDeviceVersion()).isEqualTo(dod.get("deviceVersion"));
    }

    @Test
    public void shouldGetAcuityDetailsOnDemandColumnsInCorrectOrder() throws Exception {

        // Given
        List<Lab> labs = setUpMockData();

        // When
        Map<String, String> columns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, labs);

        // Then
        softly.assertThat(columns.keySet())
                .containsExactly("studyId", "subjectId", "measurementCategory", "measurementName", "measurementTimePoint", "daysOnStudy",
                        "visitNumber", "resultValue", "resultUnit", "baselineValue",
                        "changeFromBaseline", "percentChangeFromBaseline", "baselineFlag", "refRangeNormValue",
                        "timesUpperRefValue", "timesLowerRefValue", "lowerRefRangeValue", "upperRefRangeValue",
                        "protocolScheduleTimepoint", "valueDipstick", "sourceType", "deviceName", "deviceVersion", "deviceType");
    }


    private List<Lab> setUpMockData() {
        Lab lab1 = new Lab(LabRaw.builder()
                .id("e1")
                .subjectId(SUBJECT_1_WITH_ARM.getSubjectId())
                .labCode("ALT")
                .category("BLOOD")
                .value(10.)
                .unit("g/dL")
                .visitNumber(1.)
                .analysisVisit(4.)
                .baseline(100.2423543443546456455)
                .changeFromBaselineRaw(-90.)
                .baselineFlag("N")
                .refHigh(10.)
                .refLow(11.)
                .measurementTimePoint(DateUtils.toDate("01.01.2000"))
                .calcDaysSinceFirstDoseIfNull(true)
                .protocolScheduleTimepoint("Now")
                .valueDipstick("Y")
                .sourceType("Sponsor")
                .device(Device.builder().name("Device name").type("Sponsor").version("1").build())
                .build(), SUBJECT_1_WITH_ARM);
        Lab lab2 = new Lab(LabRaw.builder()
                .id("e2")
                .subjectId(SUBJECT_2_WITH_ARM.getSubjectId())
                .labCode("ALT")
                .category("BLOOD")
                .value(11.)
                .unit("g/dL")
                .visitNumber(2.)
                .analysisVisit(5.)
                .baseline(200.)
                .changeFromBaselineRaw(-189.)
                .calcDaysSinceFirstDoseIfNull(true)
                .baselineFlag("N")
                .refHigh(10.)
                .refLow(11.)
                .protocolScheduleTimepoint("Now")
                .valueDipstick("Y")
                .sourceType("Patient")
                .measurementTimePoint(DateUtils.toDate("01.01.2000"))
                .build(), SUBJECT_2_WITH_ARM);
        Lab lab3 = new Lab(LabRaw.builder()
                .id("e3")
                .subjectId(SUBJECT_2_WITH_ARM.getSubjectId())
                .labCode("ALT")
                .category("BLOOD")
                .value(12.)
                .unit("g/dL")
                .visitNumber(3.)
                .analysisVisit(6.)
                .baseline(11.)
                .changeFromBaselineRaw(1.)
                .baselineFlag("N")
                .calcDaysSinceFirstDoseIfNull(true)
                .refHigh(10.)
                .refLow(11.)
                .protocolScheduleTimepoint("Now")
                .valueDipstick("Y")
                .sourceType("Patient")
                .measurementTimePoint(DateUtils.toDate("01.03.2000"))
                .build(), SUBJECT_2_WITH_ARM);
        Lab lab4 = new Lab(LabRaw.builder()
                .id("e4")
                .subjectId(SUBJECT_2_WITH_ARM.getSubjectId())
                .labCode("ALT")
                .category("BLOOD")
                .value(11.)
                .unit("g/dL")
                .visitNumber(2.)
                .analysisVisit(5.)
                .baseline(200.)
                .changeFromBaselineRaw(-189.)
                .calcDaysSinceFirstDoseIfNull(true)
                .baselineFlag("N")
                .refHigh(10.)
                .refLow(11.)
                .protocolScheduleTimepoint("Now")
                .valueDipstick("Y")
                .sourceType("Sponsor")
                .measurementTimePoint(DateUtils.toDate("01.01.2000"))
                .build(), SUBJECT_2_WITH_ARM);
        List<Lab> data = Arrays.asList(lab1, lab2, lab3, lab4);
        when(mockLabDatasetsDataProvider.loadData(any())).thenReturn(data);
        when(populationDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(SUBJECT_1_WITH_ARM, SUBJECT_2_WITH_ARM));
        return data;
    }

    private List<Lab> setUpMockDataNoArm() {
        Lab lab1 = new Lab(LabRaw.builder()
                .id("e1")
                .labCode("ALT")
                .value(10.)
                .unit("g/dL")
                .visitNumber(2.)
                .analysisVisit(4.)
                .baseline(100.)
                .measurementTimePoint(DateUtils.toDate("01.01.2000"))
                .calcDaysSinceFirstDoseIfNull(true)
                .build(), SUBJECT_1_NO_ARM);
        Lab lab2 = new Lab(LabRaw.builder()
                .id("e2")
                .labCode("ALT")
                .value(11.)
                .unit("g/dL")
                .visitNumber(2.)
                .analysisVisit(5.)
                .baseline(200.)
                .measurementTimePoint(DateUtils.toDate("01.01.2000"))
                .calcDaysSinceFirstDoseIfNull(true)
                .build(), SUBJECT_2_NO_ARM);
        Lab lab3 = new Lab(LabRaw.builder()
                .id("e3")
                .labCode("ALT")
                .value(12.)
                .unit("g/dL")
                .visitNumber(3.)
                .analysisVisit(6.)
                .baseline(11.)
                .measurementTimePoint(DateUtils.toDate("01.03.2000"))
                .calcDaysSinceFirstDoseIfNull(true)
                .build(), SUBJECT_2_NO_ARM);

        List<Lab> data = newArrayList(lab1, lab2, lab3);
        when(mockLabDatasetsDataProvider.loadData(any())).thenReturn(data);
        return data;
    }

    @Test
    public void testGetOutOfRangeSingleSubjectData() {

        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(mockLabDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(labs));

        List<Map<String, String>> singleSubjectData = labService.getOutOfRangeSingleSubjectData(DATASETS, "sid1", LabFilters.empty());

        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "code", "measurementTimePoint", "value", "resultUnit", "lowerRefRangeValue", "upperRefRangeValue", "eventId"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("code"), e -> e.get("measurementTimePoint"), e -> e.get("value"), e -> e.get("resultUnit"),
                        e -> e.get("lowerRefRangeValue"), e -> e.get("upperRefRangeValue"))
                .contains(
                        Tuple.tuple("code5", "1999-12-20T23:59:59", "9", "mmol/L", "5", "8"),
                        Tuple.tuple("code12", "1999-12-31T23:59:59", "11", "%", null, null),
                        Tuple.tuple("code3", "2000-12-31T23:59:59", "14", "%", "15", "19"),
                        Tuple.tuple("code7", "2000-12-31T23:59:59", "9", "%", "10", null),
                        Tuple.tuple("code9", "2000-12-31T23:59:59", "11", "sec", null, "12")
                );
    }

    @Test
    public void testGetDetailsOnDemandData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(mockLabDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(labs));
        List<Map<String, String>> dod = labService.getDetailsOnDemandData(DUMMY_ACUITY_DATASETS, "sid1", LabFilters.empty());
        softly.assertThat(dod).extracting(e -> e.get("measurementTimePoint")).containsExactly(
                "1999-12-20T23:59:59",
                "1999-12-31T23:59:59",
                "1999-12-31T23:59:59",
                "2000-12-31T23:59:59",
                "2000-12-31T23:59:59",
                "2000-12-31T23:59:59",
                "2001-01-01T00:00:00",
                "2001-01-01T00:00:00",
                "2001-01-01T23:59:59",
                "2001-12-21T23:59:58",
                "2002-01-01T00:00:00",
                "2002-06-30T23:59:59",
                "2002-12-31T23:59:59",
                null
        );
    }

}
