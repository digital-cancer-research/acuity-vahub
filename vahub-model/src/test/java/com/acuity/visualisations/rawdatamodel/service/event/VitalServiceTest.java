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
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.VitalDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.RangeChartTests;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.VitalRaw;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASET;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASET;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions.ARM;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions.MEASUREMENT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions.MEASUREMENT_TIME_POINT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions.VISIT_NUMBER;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class VitalServiceTest {

    @Autowired
    private VitalService vitalService;
    @MockBean
    private VitalDatasetsDataProvider vitalDatasetsDataProvider;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private InfoService mockInfoService;
    @Autowired
    private DoDCommonService tableService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private final Subject subject1 = Subject.builder()
            .clinicalStudyCode(String.valueOf(DUMMY_DETECT_DATASET.getId()))
            .withdrawal("No")
            .plannedArm("planed_arm")
            .actualArm("actual_arm")
            .country("China")
            .region("Asia")
            .subjectId("subject1")
            .subjectCode("subject1")
            .firstTreatmentDate(toDate("31.05.2015"))
            .studyLeaveDate(toDate("10.09.2017")).build();
    private final Subject subject2 = Subject.builder()
            .clinicalStudyCode(String.valueOf(DUMMY_ACUITY_DATASET.getId()))
            .withdrawal("No")
            .plannedArm("planed_arm")
            .actualArm("actual_arm")
            .country("China")
            .region("Asia")
            .subjectId("subject2")
            .subjectCode("subject2")
            .firstTreatmentDate(toDate("01.06.2015"))
            .studyPart("A")
            .studyLeaveDate(toDate("10.09.2017")).build();

    private Vital vital1 = new Vital(VitalRaw.builder()
            .id("vital1")
            .visitNumber(1.)
            .resultValue(61.)
            .baseline(40.)
            .unit("unit1")
            .measurementDate(toDate("01.06.2015"))
            .vitalsMeasurement("measurement1")
            .daysSinceFirstDose(1)
            .baseline(50.)
            .analysisVisit(1.)
            .baselineFlag("Y")
            .plannedTimePoint("plannedTimePoint1")
            .studyPeriod("period1")
            .lastDoseAmount("lastDoseAmount1")
            .physicalPosition("position1")
            .clinicallySignificant("Y")
            .build().runPrecalculations(), subject1);
    private Vital vital2 = new Vital(VitalRaw.builder()
            .id("vital2")
            .visitNumber(2.)
            .resultValue(20.)
            .baseline(10.)
            .unit("unit2")
            .measurementDate(toDate("08.06.2015"))
            .vitalsMeasurement("measurement2")
            .plannedTimePoint("plannedTimePoint2")
            .scheduleTimepoint("timePoint1")
            .analysisVisit(2.)
            .lastDoseDate(toDate("18.06.2015"))
            .anatomicalLocation("anatomicLocation1")
            .sidesOfInterest("sideOfInterest1")
            .physicalPosition("position2")
            .clinicallySignificant("N")
            .build().runPrecalculations(), subject1);
    private Vital vital3 = new Vital(VitalRaw.builder()
            .id("vital3")
            .visitNumber(1.)
            .resultValue(21.)
            .unit("unit3")
            .measurementDate(toDate("08.08.2015"))
            .vitalsMeasurement("measurement3")
            .analysisVisit(4.)
            .baselineFlag("N")
            .baseline(100.2)
            .studyPeriod("period2")
            .daysSinceFirstDose(68)
            .lastDoseDate(toDate("13.12.2015"))
            .anatomicalLocation("anatomicLocation2")
            .sidesOfInterest("sideOfInterest2")
            .lastDoseAmount("lastDoseAmount1")
            .build().runPrecalculations(), subject2);
    private Vital vital4 = new Vital(VitalRaw.builder()
            .id("vital4")
            .build().runPrecalculations(), subject2);
    private Vital vital5 = new Vital(VitalRaw.builder()
            .id("vital5")
            .build().runPrecalculations(), subject1);
    private Vital vital6 = new Vital(VitalRaw.builder()
            .id("vital6")
            .visitNumber(1.)
            .resultValue(54.8)
            .measurementDate(toDate("07.06.2015"))
            .vitalsMeasurement("measurement1")
            .unit("unit1")
            .build().runPrecalculations(), subject1);
    private Vital vital7 = new Vital(VitalRaw.builder()
            .id("vital7")
            .visitNumber(1.)
            .resultValue(34.)
            .measurementDate(toDate("14.06.2015"))
            .vitalsMeasurement("measurement1")
            .unit("unit1")
            .build().runPrecalculations(), subject1);

    @Test
    public void shouldGetAllMeanRangeXAxisOptions() {
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(vital1, vital2));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject1));

        AxisOptions<VitalGroupByOptions> availableMeanRangeXAxis = vitalService.getAvailableRangePlotXAxis(DUMMY_DETECT_DATASETS,
                VitalFilters.empty(), PopulationFilters.empty());

        List<AxisOption<VitalGroupByOptions>> options = availableMeanRangeXAxis.getOptions();
        softly.assertThat(options).hasSize(2);
        softly.assertThat(options).extracting(AxisOption::getGroupByOption).containsExactly(
                VISIT_NUMBER,
                MEASUREMENT_TIME_POINT);
    }

    @Test
    public void shouldGetAvailableMeanRangeXAxisOptions() {
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(vital3, vital4));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject2));

        AxisOptions<VitalGroupByOptions> availableMeanRangeXAxis = vitalService.getAvailableRangePlotXAxis(DUMMY_DETECT_DATASETS,
                VitalFilters.empty(), PopulationFilters.empty());

        List<AxisOption<VitalGroupByOptions>> options = availableMeanRangeXAxis.getOptions();
        softly.assertThat(options).hasSize(2);
        softly.assertThat(options).extracting(AxisOption::getGroupByOption).containsExactly(
                VISIT_NUMBER,
                MEASUREMENT_TIME_POINT);
    }

    @Test
    @Category(RangeChartTests.class)
    public void shouldGetRangePlotData() {
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(vital1, vital6, vital7));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject1));

        final ChartGroupByOptions<Vital, VitalGroupByOptions> settings = ChartGroupByOptions.<Vital, VitalGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, VitalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, VitalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(VitalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(VitalGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();
        final ChartGroupByOptionsFiltered<Vital, VitalGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .build();
        // When
        List<TrellisedRangePlot<Vital, VitalGroupByOptions>> result =
                vitalService.getRangePlot(DUMMY_ACUITY_DATASETS, settingsFiltered, VitalFilters.empty(), PopulationFilters.empty(), StatType.MEDIAN);

        //Then
        TrellisedRangePlot<Vital, VitalGroupByOptions> data = result.get(0);
        softly.assertThat(data.getTrellisedBy()).hasSize(2);
        softly.assertThat(data.getTrellisedBy().get(0).getTrellisOption()).isEqualTo("actual_arm");
        softly.assertThat(data.getTrellisedBy().get(1).getTrellisOption()).isEqualTo("measurement1 (unit1)");
        softly.assertThat(data.getData()).hasSize(1);
        softly.assertThat(data.getData().get(0).getName()).isEqualTo("All");
        softly.assertThat(data.getData().get(0).getData()).extracting("x", "xRank", "dataPoints", "y", "min", "max")
                .containsExactly(tuple("1", 1.0, 3, 54.8, 34.0, 61.0));
    }

    @Test
    @Category(RangeChartTests.class)
    public void shouldGetRangePlotDataWithCorrectXOrder() {
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(vital1, vital6, vital7));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject1));

        final ChartGroupByOptions<Vital, VitalGroupByOptions> settings = ChartGroupByOptions.<Vital, VitalGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, VitalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, VitalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(VitalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(VitalGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();
        final ChartGroupByOptionsFiltered<Vital, VitalGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .build();
        // When
        List<TrellisedRangePlot<Vital, VitalGroupByOptions>> result =
                vitalService.getRangePlot(DUMMY_ACUITY_DATASETS, settingsFiltered, VitalFilters.empty(), PopulationFilters.empty(), StatType.MEDIAN);

        //Then
        TrellisedRangePlot<Vital, VitalGroupByOptions> data = result.get(0);
        softly.assertThat(data.getTrellisedBy()).hasSize(2);
        softly.assertThat(data.getTrellisedBy().get(0).getTrellisOption()).isEqualTo("actual_arm");
        softly.assertThat(data.getTrellisedBy().get(1).getTrellisOption()).isEqualTo("measurement1 (unit1)");
        softly.assertThat(data.getData()).hasSize(1);
        softly.assertThat(data.getData().get(0).getName()).isEqualTo("All");
        softly.assertThat(data.getData().get(0).getData().get(0)).extracting("x", "xRank", "dataPoints", "y", "min", "max")
                .containsExactly("measurement1 (unit1)", 0.0, 3, 54.8, 34.0, 61.0);
    }

    @Test
    public void shouldGetSelectionAcuityDataset() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1, subject2));
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(vital1, vital2));

        ChartGroupByOptions<Vital, VitalGroupByOptions> settings =
                ChartGroupByOptions.<Vital, VitalGroupByOptions>builder()
                        .withOption(X_AXIS,
                                VitalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                                VitalGroupByOptions.PERCENTAGE_CHANGE_FROM_BASELINE.getGroupByOptionAndParams())
                        .withTrellisOption(MEASUREMENT.getGroupByOptionAndParams())
                        .withTrellisOption(ARM.getGroupByOptionAndParams())
                        .build();

        HashMap<VitalGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "measurement2 (% change)");
        selectedTrellises.put(ARM, "actual_arm");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(X_AXIS, "2");

        List<ChartSelectionItem<Vital, VitalGroupByOptions>> selectionItems = Collections.singletonList(
                ChartSelectionItem.of(selectedTrellises, selectedItems)
        );

        SelectionDetail selectionResult = vitalService.getSelectionDetails(
                DUMMY_ACUITY_DATASETS,
                VitalFilters.empty(),
                PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems)
        );

        softly.assertThat(selectionResult.getEventIds()).hasSize(1);
        softly.assertThat(selectionResult.getSubjectIds()).hasSize(1);
        softly.assertThat(selectionResult.getTotalEvents()).isEqualTo(2);
        softly.assertThat(selectionResult.getTotalSubjects()).isEqualTo(2);
    }



    @Test
    public void shouldGetAllAcuityDetailsOnDemandColumnsInCorrectOrder() {

        // When
        Map<String, String> columns = tableService.getColumns(Column.DatasetType.ACUITY, Vital.class);

        // Then
        softly.assertThat(columns.keySet())
                .containsExactly("studyId", "studyPart", "subjectId", "measurementName", "measurementTimePoint", "daysOnStudy",
                        "visitNumber", "startDate","resultValue", "resultUnit", "baselineValue", "changeFromBaseline",
                        "percentChangeFromBaseline", "baselineFlag",
                        "physicalPosition");
        softly.assertThat(columns.values())
                .containsExactly("Study id", "Study part", "Subject id", "Measurement name", "Measurement time point", "Days on study",
                        "Visit number", "Start date", "Result value", "Result unit", "Baseline value", "Change from baseline",
                        "Percent change from baseline", "Baseline flag", "Physical position");
    }


    @Test
    public void shouldGetAcuityDetailsOnDemand() {
        // Given
        List<Vital> vitals = Arrays.asList(vital3, vital4);
        Set<String> ids = vitals.stream().map(Vital::getId).collect(toSet());
        when(vitalDatasetsDataProvider.loadData(any())).thenReturn(vitals);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(subject2));

        // When
        List<Map<String, String>> doDData = vitalService.getDetailsOnDemandData(DUMMY_ACUITY_DATASETS, ids, null, 0, Integer.MAX_VALUE);

        // Then
        Vital vital = vitals.get(0);
        Map<String, String> dod = doDData.get(0);

        softly.assertThat(doDData).hasSize(1);
        softly.assertThat(vital.getStudyId()).isEqualTo(dod.get("studyId"));
        softly.assertThat(vital.getStudyPart()).isEqualTo(dod.get("studyPart"));
        softly.assertThat(vital.getSubjectId()).isEqualTo(dod.get("subjectId"));
        softly.assertThat(vital.getEvent().getVitalsMeasurement()).isEqualTo(dod.get("measurementName"));
        softly.assertThat(vital.getMeasurementTimePoint()).isInSameDayAs(DaysUtil.toDate(dod.get("measurementTimePoint").toString()));
        softly.assertThat(vital.getDaysSinceFirstDose().toString()).isEqualTo(dod.get("daysOnStudy"));
        softly.assertThat(vital.getEvent().getVisitNumber()).isEqualTo(((Number) Double.parseDouble(dod.get("visitNumber"))).doubleValue());
        softly.assertThat(vital.getResultValue()).isEqualTo(((Number) Double.parseDouble(dod.get("resultValue"))).doubleValue());
        softly.assertThat(vital.getEvent().getUnit()).isEqualTo(dod.get("resultUnit"));
        softly.assertThat(vital.getBaselineValue().toString()).isEqualTo(dod.get("baselineValue"));
        softly.assertThat(vital.getChangeFromBaseline().toString()).isEqualTo(dod.get("changeFromBaseline"));
        softly.assertThat(vital.getPercentChangeFromBaseline().toString()).isEqualTo(dod.get("percentChangeFromBaseline"));
        softly.assertThat(vital.getEvent().getBaselineFlag()).isEqualTo(dod.get("baselineFlag"));
        softly.assertThat(vital.getEvent().getPhysicalPosition()).isEqualTo(dod.get("physicalPosition"));
        softly.assertThat(vital.getEvent().getScheduleTimepoint()).isEqualTo(dod.get("scheduleTimepoint"));
    }

    @Test
    public void shouldGetAvailableMeasurementOverTimeChartXAxis() {
        List<Vital> events = Collections.singletonList(vital3);
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(subject2));

        AxisOptions<VitalGroupByOptions> axisOptions = vitalService
                .getAvailableBoxPlotXAxis(DUMMY_ACUITY_DATASETS, VitalFilters.empty(), PopulationFilters.empty());
        softly.assertThat(axisOptions.getOptions())
                .extracting(AxisOption::getGroupByOption)
                .containsExactly(VISIT_NUMBER, MEASUREMENT_TIME_POINT);
    }

    @Test
    public void shouldGetBoxPlotForAcuityDataset() {

        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1));
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(vital1, vital6, vital7));

        ChartGroupByOptions<Vital, VitalGroupByOptions> settings =
                ChartGroupByOptions.<Vital, VitalGroupByOptions>builder()
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS,
                                VitalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                                VitalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                        .withTrellisOption(MEASUREMENT.getGroupByOptionAndParams())
                        .withTrellisOption(ARM.getGroupByOptionAndParams())
                        .build();

        final HashMap<VitalGroupByOptions, Object> filterByTrellis = new HashMap<>();
        filterByTrellis.put(MEASUREMENT, "measurement1 (unit1)");
        filterByTrellis.put(ARM, "actual_arm");

        List<TrellisedBoxPlot<Vital, VitalGroupByOptions>> result = vitalService.getBoxPlot(
                DUMMY_ACUITY_DATASETS,
                ChartGroupByOptionsFiltered.builder(settings).withFilterByTrellisOption(filterByTrellis).build(),
                VitalFilters.empty(),
                PopulationFilters.empty()
        );

        softly.assertThat(result.get(0).getTrellisedBy())
                .extracting("trellisedBy")
                .containsOnly(MEASUREMENT, ARM);
        softly.assertThat(result.get(0).getStats()).hasSize(1);
        softly.assertThat(result.get(0).getStats().get(0).getX()).isEqualTo("1");
        softly.assertThat(result.get(0).getStats().get(0).getXRank()).isEqualTo(1.0);
        softly.assertThat(result.get(0).getStats().get(0).getMedian()).isEqualTo(54.8);
        softly.assertThat(result.get(0).getStats().get(0).getUpperQuartile()).isEqualTo(57.9);
        softly.assertThat(result.get(0).getStats().get(0).getLowerQuartile()).isEqualTo(44.4);
        softly.assertThat(result.get(0).getStats().get(0).getUpperWhisker()).isEqualTo(61.0);
        softly.assertThat(result.get(0).getStats().get(0).getLowerWhisker()).isEqualTo(34.0);
    }



    @Test
    public void shouldGetRangedSelection() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1));
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(vital1, vital6));

        ChartGroupByOptions<Vital, VitalGroupByOptions> settings =
                ChartGroupByOptions.<Vital, VitalGroupByOptions>builder()
                        .withOption(X_AXIS,
                                VitalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                                VitalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                        .withTrellisOption(MEASUREMENT.getGroupByOptionAndParams())
                        .withTrellisOption(ARM.getGroupByOptionAndParams())
                        .build();

        HashMap<VitalGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "measurement1 (unit1)");
        selectedTrellises.put(ARM, "actual_arm");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(X_AXIS, "1");

        final List<ChartSelectionItemRange<Vital, VitalGroupByOptions, Double>> selectionItems =
                Collections.singletonList(
                        ChartSelectionItemRange.of(selectedTrellises, selectedItems, 60.0, 64.0)
                );

        SelectionDetail selectionDetail = vitalService.getRangedSelectionDetails(
                DUMMY_ACUITY_DATASETS,
                VitalFilters.empty(),
                PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems)
        );

        softly.assertThat(selectionDetail.getEventIds()).hasSize(1);
        softly.assertThat(selectionDetail.getSubjectIds()).hasSize(1);
    }

    @Test
    public void shouldGetTrellisOptions() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1));
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(vital1));

        List<TrellisOptions<VitalGroupByOptions>> result = vitalService.getTrellisOptions(
                DUMMY_ACUITY_DATASETS,
                VitalFilters.empty(),
                PopulationFilters.empty(),
                VitalGroupByOptions.ACTUAL_VALUE
        );

        softly.assertThat(result).hasSize(2);

        softly.assertThat(result).containsExactlyInAnyOrder(
                new TrellisOptions<>(MEASUREMENT, Arrays.asList("measurement1 (unit1)")),
                new TrellisOptions<>(ARM, Arrays.asList("actual_arm"))
        );
    }

    @Test
    public void shouldGetSubjects() {
        //Given
        List<Vital> events = Arrays.asList(vital1, vital2);
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(subject1, subject2));

        //When
        List<String> results = vitalService.getSubjects(DUMMY_ACUITY_DATASETS, VitalFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(results).hasSize(1);
        softly.assertThat(results).containsOnly("subject1");
    }

    @Test
    public void shouldGetAvailableFilters() {
        //Given
        List<Vital> events = Arrays.asList(vital1, vital2, vital3);
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(subject1, subject2));

        //When
        VitalFilters result = (VitalFilters) vitalService.getAvailableFilters(
                DUMMY_ACUITY_DATASETS, VitalFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(result.getStudyPeriods().getValues()).containsOnly(null, "period1", "period2");
        softly.assertThat(result.getVitalsMeasurements().getValues()).containsOnly("measurement1", "measurement2", "measurement3");
        softly.assertThat(result.getPlannedTimePoints().getValues()).containsOnly(null, "plannedTimePoint1", "plannedTimePoint2");
        softly.assertThat(result.getMeasurementDate().getFrom()).isInSameDayAs(toDate("01.06.2015"));

        softly.assertThat(ZonedDateTime.ofInstant(result.getMeasurementDate().getTo().toInstant(), ZoneId.of("GMT")))
                .isEqualToIgnoringHours(ZonedDateTime.ofInstant(toDate("08.08.2015").toInstant(), ZoneId.of("GMT")));

        softly.assertThat(result.getDaysSinceFirstDose().getFrom()).isEqualTo(1);
        softly.assertThat(result.getDaysSinceFirstDose().getTo()).isEqualTo(68);
        softly.assertThat(result.getVisitNumber().getFrom()).isEqualTo(1);
        softly.assertThat(result.getVisitNumber().getTo()).isEqualTo(2);
        softly.assertThat(result.getScheduleTimepoints().getValues()).containsOnly(null, "timePoint1");
        softly.assertThat(result.getUnits().getValues()).containsOnly("unit1", "unit2", "unit3");
        softly.assertThat(result.getPercentageChangeFromBaseline().getFrom()).isEqualTo(-79.04);
        softly.assertThat(result.getPercentageChangeFromBaseline().getTo()).isEqualTo(100.);
        softly.assertThat(result.getBaseline().getFrom()).isEqualTo(10.);
        softly.assertThat(result.getBaseline().getTo()).isEqualTo(100.2);
        softly.assertThat(result.getBaselineFlags().getValues()).containsOnly(null, "Y", "N");
        softly.assertThat(result.getResultValue().getFrom()).isEqualTo(20.);
        softly.assertThat(result.getResultValue().getTo()).isEqualTo(61.);
        softly.assertThat(result.getAnalysisVisit().getFrom()).isEqualTo(1);
        softly.assertThat(result.getAnalysisVisit().getTo()).isEqualTo(4);
        softly.assertThat(result.getLastDoseDate().getFrom()).isInSameDayAs(toDate("18.06.2015"));

        softly.assertThat(ZonedDateTime.ofInstant(result.getLastDoseDate().getTo().toInstant(), ZoneId.of("GMT")))
                .isEqualToIgnoringHours(ZonedDateTime.ofInstant(toDate("13.12.2015").toInstant(), ZoneId.of("GMT")));

        softly.assertThat(result.getLastDoseAmounts().getValues()).containsOnly(null, "lastDoseAmount1");
        softly.assertThat(result.getAnatomicalLocations().getValues()).containsOnly(null, "anatomicLocation1", "anatomicLocation2");
        softly.assertThat(result.getSidesOfInterest().getValues()).containsOnly(null, "sideOfInterest1", "sideOfInterest2");
        softly.assertThat(result.getPhysicalPositions().getValues()).containsOnly(null, "position1", "position2");
        softly.assertThat(result.getClinicallySignificant().getValues()).containsOnly(null, "Y", "N");
    }
}
