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
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.RenalDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RenalFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.BoxPlotTests;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.RenalRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBoxplotEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.BoxplotCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASET;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASET;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.ACTUAL_VALUE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.ARM;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.CKD_STAGE_NAME;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.MEASUREMENT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.MEASUREMENT_TIME_POINT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.Param;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.Params;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.STUDY_DEFINED_WEEK;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.TimestampType;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.VISIT_DESCRIPTION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.RenalGroupByOptions.VISIT_NUMBER;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class RenalServiceTest {

    @Autowired
    private RenalService renalService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private RenalDatasetsDataProvider renalDatasetsDataProvider;
    @MockBean
    private InfoService mockInfoService;


    @Autowired
    private DoDCommonService tableService ;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    // Double.toString(1d) yields "1.0" while decimalFormat.format(1d) yields "1"
    private final static DecimalFormat decimalFormat = new DecimalFormat();
    private static final String MEASUREMENT_NAME = "creatinine clearence";
    private static final String SUBJECT_ID = "sid1";
    private static final String UNIT = "mg/l";
    private static final Double RESULT = 10.5;
    private static final Double ANALYSIS_VISIT = 1.5;
    private static final Double VISIT_NUMBER_FOR_DOD = 2.5;
    private static final Double REF_HIGH = 10.5;
    private static final Date MEASUREMENT_TIME_POINT_FOR_DOD = DateUtils.toDate("03.10.2018");
    private static final String STUDY_PART = "A";
    //??
    private static final Double TIMES_UPPER = 1.0;
    private static final String CKD_STAGE = "CKD Stage 5";
    private static final String CODE = "code1";

    private static final Subject SUBJECT_1_WITH_ARM = Subject.builder().subjectCode(SUBJECT_ID)
            .subjectId(SUBJECT_ID)
            .studyPart(STUDY_PART).clinicalStudyCode(CODE).actualArm("arm-1")
            .firstTreatmentDate(toDate("01.01.2000")).dateOfRandomisation(toDate("01.12.1999"))
            .build();

    private static final Subject SUBJECT_2_WITH_ARM = Subject.builder().subjectCode("subj-2")
            .subjectId("subj-2").clinicalStudyCode("study 1").actualArm("arm-2")
            .firstTreatmentDate(toDate("01.01.2000")).dateOfRandomisation(toDate("01.12.1999"))
            .build();
    private static final Subject SUBJECT3 = Subject.builder()
            .subjectId("sid1").baselineDate(DateUtils.toDateTime("2001-01-01T00:00:00")).build();
    private static final Subject SUBJECT4 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_DETECT_DATASET.getId()))
            .withdrawal("No").plannedArm("planed_arm").actualArm("actual_arm").country("China").region("Asia")
            .subjectId("sid4").subjectCode("subject1").firstTreatmentDate(toDate("31.05.2015"))
            .dateOfRandomisation(toDate("02.06.2017")).studyLeaveDate(toDate("10.09.2017")).build();
    private static final Subject SUBJECT5 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_DATASET.getId()))
            .withdrawal("No").plannedArm("planed_arm").actualArm("actual_arm").country("China").region("Asia")
            .subjectId("sid5").subjectCode("subject2").firstTreatmentDate(toDate("01.06.2015")).studyPart("A")
            .studyLeaveDate(toDate("10.09.2017")).build();


    private static final Renal RENAL = new Renal(RenalRaw.builder().id("renal1").value(RESULT)
            .unit(UNIT).labCode(MEASUREMENT_NAME).refHigh(REF_HIGH).measurementTimePoint(MEASUREMENT_TIME_POINT_FOR_DOD)
            .visitNumber(VISIT_NUMBER_FOR_DOD).analysisVisit(ANALYSIS_VISIT).build().runPrecalculations(), SUBJECT_1_WITH_ARM);

    private static final Renal RENAL1 = new Renal(RenalRaw.builder().id("lid1").labCode("code1")
            .measurementTimePoint(DateUtils.toDateTime("2001-01-01T00:00:00"))
            .value(7.0).refLow(3.0).refHigh(8.0).unit("%").analysisVisit(11.0).visitNumber(1.)
            .ckdStageNameRaw("ckd stage 1")
            .build().runPrecalculations(), SUBJECT_1_WITH_ARM);

    private static final Renal RENAL2 = new Renal(RenalRaw.builder().id("id2").labCode("ALBUMIN").unit("g/dL").value(1.).visitNumber(1.)
            .analysisVisit(1.).measurementTimePoint(toDate("01.01.2000")).calcDaysSinceFirstDoseIfNull(true)
            .ckdStageNameRaw("ckd stage 2")
            .calcCkgStageIfNull(true)
            .build().runPrecalculations(), SUBJECT_1_WITH_ARM);

    private static final Renal RENAL3 = new Renal(RenalRaw.builder().id("id3").labCode("ALT").unit("U/L").visitNumber(1.)
            .analysisVisit(1.).calcDaysSinceFirstDoseIfNull(true).measurementTimePoint(toDate("01.03.2000"))
            .build().runPrecalculations(), SUBJECT_2_WITH_ARM);

    private static final Renal RENAL4 = new Renal(RenalRaw.builder().id("lid15").labCode("code17")
            .measurementTimePoint(DateUtils.toDateTime("2001-01-01T00:00:00"))
            .value(7.0).refLow(3.0).refHigh(8.0).unit("%").visitDescription("visit_disc").build().runPrecalculations(), SUBJECT3);

    private static final Renal RENAL5 = new Renal(RenalRaw.builder().id("lid16").labCode("code17")
            .measurementTimePoint(DateUtils.toDateTime("2001-01-01T00:00:00"))
            .value(7.0).refLow(3.0).refHigh(8.0).unit("%").visitDescription("some_word 12").build().runPrecalculations(), SUBJECT3);

    private static final Renal RENAL6 = new Renal(RenalRaw.builder().id("lid17").labCode("code17")
            .measurementTimePoint(DateUtils.toDateTime("2001-01-01T00:00:00"))
            .value(7.0).refLow(3.0).refHigh(8.0).unit("%").visitDescription("").build().runPrecalculations(), SUBJECT3);

    private static final Renal RENAL7 = new Renal(RenalRaw.builder().id("e1").subjectId(SUBJECT_1_WITH_ARM.getSubjectId())
            .labCode("ALT").value(10.).unit("g/dL").visitNumber(1.).analysisVisit(4.).changeFromBaselineRaw(-90.)
            .baselineFlag("N").refHigh(10.).refLow(11.).measurementTimePoint(toDate("01.01.2000"))
            .calcDaysSinceFirstDoseIfNull(true).calcCkgStageIfNull(true).build().runPrecalculations(), SUBJECT_1_WITH_ARM);

    private static final Renal RENAL8 = new Renal(RenalRaw.builder().id("e2").subjectId(SUBJECT_2_WITH_ARM.getSubjectId())
            .labCode("ALT").value(11.).unit("g/dL").visitNumber(2.).analysisVisit(5.).changeFromBaselineRaw(-189.)
            .calcDaysSinceFirstDoseIfNull(true).baselineFlag("N").refHigh(10.).refLow(11.)
            .measurementTimePoint(toDate("01.01.2000")).build().runPrecalculations(), SUBJECT_2_WITH_ARM);

    private static final Renal RENAL9 = new Renal(RenalRaw.builder().id("e3").subjectId(SUBJECT_2_WITH_ARM.getSubjectId())
            .labCode("ALT").value(12.).unit("g/dL").visitNumber(3.).analysisVisit(6.).changeFromBaselineRaw(1.)
            .baselineFlag("N").calcDaysSinceFirstDoseIfNull(true).refHigh(10.).refLow(11.)
            .measurementTimePoint(toDate("01.03.2000")).build().runPrecalculations(), SUBJECT_2_WITH_ARM);

    private static final Renal RENAL10 = new Renal(RenalRaw.builder().id("e10").visitNumber(1.).analysisVisit(1.)
            .measurementTimePoint(toDate("03.06.2015")).value(2.).labCode("code1").unit("unit1").build().runPrecalculations(), SUBJECT4);

    private static final Renal RENAL11 = new Renal(RenalRaw.builder().id("e11").visitNumber(1.).visitDescription("visit1")
            .measurementTimePoint(toDate("03.06.2015")).value(28.).labCode("code1").unit("unit1").build().runPrecalculations(), SUBJECT5);

    private static final Renal RENAL12 = new Renal(RenalRaw.builder().id("e12").visitNumber(2.).visitDescription("visit1")
            .measurementTimePoint(toDate("06.06.2015")).value(3.).labCode("code2").unit("unit2").build().runPrecalculations(), SUBJECT5);

    private static final Renal RENAL13 = new Renal(RenalRaw.builder().id("e13").visitNumber(1.).visitDescription("visit1")
            .measurementTimePoint(toDate("07.06.2015")).value(83.).labCode("code1").unit("unit1").build().runPrecalculations(), SUBJECT5);

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotXAxisOptions() {
        when(renalDatasetsDataProvider.loadData(any())).thenReturn(Collections.singletonList(RENAL1));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT_1_WITH_ARM));

        AxisOptions<RenalGroupByOptions> result = renalService.getAvailableBoxPlotXAxis(DATASETS, RenalFilters.empty(), PopulationFilters.empty());

        softly.assertThat(result.getOptions())
                .extracting(AxisOption::getGroupByOption)
                .containsExactlyInAnyOrder(VISIT_NUMBER, STUDY_DEFINED_WEEK, MEASUREMENT_TIME_POINT);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotTrellisOptionsWithYAxisOption() {
        when(renalDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(RENAL2, RENAL3));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(SUBJECT_1_WITH_ARM, SUBJECT_2_WITH_ARM));

        List<TrellisOptions<RenalGroupByOptions>> result = renalService
                .getTrellisOptions(DATASETS, RenalFilters.empty(), PopulationFilters.empty(), ACTUAL_VALUE);

        softly.assertThat(result).extracting("trellisedBy").containsOnly(MEASUREMENT, ARM);
        softly.assertThat(result)
                .filteredOn(t -> t.getTrellisedBy().equals(MEASUREMENT))
                .flatExtracting("trellisOptions").containsOnly("ALBUMIN (g/dL)", "ALT (U/L)");
        softly.assertThat(result).filteredOn(t -> t.getTrellisedBy().equals(ARM))
                .flatExtracting("trellisOptions").containsOnly("arm-1", "arm-2");
    }

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotData() {
        when(renalDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(RENAL2, RENAL3));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(SUBJECT_1_WITH_ARM, SUBJECT_2_WITH_ARM));

        // Given
        final ChartGroupByOptions<Renal, RenalGroupByOptions> settings = ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, RenalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, RenalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();
        final ChartGroupByOptionsFiltered<Renal, RenalGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .build();
        // When

        final List<TrellisedBoxPlot<Renal, RenalGroupByOptions>> result = renalService.getBoxPlot(DATASETS, settingsFiltered,
                RenalFilters.empty(), PopulationFilters.empty());

        // Then
        softly.assertThat(result).isNotEmpty();
        softly.assertThat(result)
                .flatExtracting(TrellisedBoxPlot::getTrellisedBy)
                .isNotEmpty();
    }

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotDataWithVisitDiscXAxisOption() {
        // Given
        when(renalDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(RENAL4, RENAL5, RENAL6));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT3));

        final ChartGroupByOptions<Renal, RenalGroupByOptions> settings = ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, RenalGroupByOptions.VISIT_DESCRIPTION.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, RenalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();
        final ChartGroupByOptionsFiltered<Renal, RenalGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .build();
        // When
        final List<TrellisedBoxPlot<Renal, RenalGroupByOptions>> result = renalService.getBoxPlot(DATASETS, settingsFiltered,
                RenalFilters.empty(), PopulationFilters.empty());

        // Then
        softly.assertThat(result).hasSize(1);
        BoxplotCalculationObject boxplotCalculationObject = BoxplotCalculationObject.builder().subjectCount(1L).median(7.0d).upperQuartile(7.0d)
                .lowerQuartile(7.0d).upperWhisker(7.0d).lowerWhisker(7.0d).outliers(new HashSet<>()).build();
        softly.assertThat(result)
                .extracting("stats")
                .containsExactly(Arrays.asList(
                        OutputBoxplotEntry.of("some_word 12", 1.0d, boxplotCalculationObject),
                        OutputBoxplotEntry.of("", 2.0d, boxplotCalculationObject),
                        OutputBoxplotEntry.of("visit_disc", 3.0d, boxplotCalculationObject)));
    }

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotSelectionWhenTrellisedByMeasurementAndArm() {
        // Given
        when(renalDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(RENAL7, RENAL8, RENAL9));
        when(populationDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(SUBJECT_1_WITH_ARM, SUBJECT_2_WITH_ARM));

        final ChartGroupByOptions<Renal, RenalGroupByOptions> settings = ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, RenalGroupByOptions.STUDY_DEFINED_WEEK.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, RenalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();

        final HashMap<RenalGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "ALT (g/dL)");
        selectedTrellises.put(ARM, "arm-2");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "5.0");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(X_AXIS, "6.0");

        final List<ChartSelectionItemRange<Renal, RenalGroupByOptions, Double>> selectionItems = Arrays.asList(
                ChartSelectionItemRange.of(selectedTrellises, selectedItems1, 11., 12.),
                ChartSelectionItemRange.of(selectedTrellises, selectedItems2, 11., 12.)
        );

        // When
        SelectionDetail result = renalService.getRangedSelectionDetails(
                DATASETS, RenalFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems));

        // Then
        softly.assertThat(result.getEventIds()).containsOnly("e2", "e3");
        softly.assertThat(result.getSubjectIds()).containsOnly("subj-2");
        softly.assertThat(result.getTotalEvents()).isEqualTo(3);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotSelectionWhenSingleXMatch() {
        // Given
        when(renalDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(RENAL7, RENAL8, RENAL9));
        when(populationDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(SUBJECT_1_WITH_ARM, SUBJECT_2_WITH_ARM));

        final ChartGroupByOptions<Renal, RenalGroupByOptions> settings = ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, RenalGroupByOptions.STUDY_DEFINED_WEEK.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, RenalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();

        final HashMap<RenalGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(RenalGroupByOptions.MEASUREMENT, "ALT (g/dL)");
        selectedTrellises.put(RenalGroupByOptions.ARM, "arm-2");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "5.0");

        final List<ChartSelectionItemRange<Renal, RenalGroupByOptions, Double>> selectionItems = Arrays.asList(
                ChartSelectionItemRange.of(selectedTrellises, selectedItems1, 11., 11.)
        );

        // When
        SelectionDetail result = renalService.getRangedSelectionDetails(
                DATASETS, RenalFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems));

        // Then
        softly.assertThat(result.getEventIds()).containsOnly("e2");
        softly.assertThat(result.getSubjectIds()).containsOnly("subj-2");
        softly.assertThat(result.getTotalEvents()).isEqualTo(3);
    }

    @Test
    public void shouldGetAcuityDodColumnsInCorrectOrder() {
        when(renalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(RENAL));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(SUBJECT_1_WITH_ARM, SUBJECT_2_WITH_ARM));

        Map<String, String> columns = tableService.getColumns(Column.DatasetType.ACUITY, Renal.class,
                RenalRaw.class);

        softly.assertThat(columns.keySet())
                .containsExactly("studyId", "studyPart", "subjectId",
                        "measurementName", "measurementTimePoint", "daysOnStudy",
                        "visitNumber", "resultValue", "resultUnit",
                        "timesUpperRef", "upperRefRangeValue", "ckdStage");

        softly.assertThat(columns.values())
                .containsExactly("Study id", "Study part", "Subject id",
                        "Measurement name", "Measurement time point", "Days on study",
                        "Visit number", "Result value", "Result unit",
                        "Times upper ref value", "Upper ref range value", "Ckd stage");

    }

    @Test
    public void shouldGetDodValues() {
        when(renalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(RENAL));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(SUBJECT_1_WITH_ARM, SUBJECT_2_WITH_ARM));

        List<Map<String, String>> result = tableService.getColumnData(Column.DatasetType.ACUITY,
                Collections.singletonList(RENAL),
                true);
        softly.assertThat(result)
                .hasSize(1)
                //fixed. don't know if it's correct. compare this fields with az code
                .extracting("studyId", "subjectId", "measurementName", "visitNumber", "resultValue", "resultUnit", "timesUpperRef", "upperRefRangeValue", "ckdStage")
                .contains(tuple(CODE, SUBJECT_ID, MEASUREMENT_NAME, decimalFormat.format(VISIT_NUMBER_FOR_DOD), decimalFormat.format(RESULT), UNIT,
                        decimalFormat.format(TIMES_UPPER), decimalFormat.format(REF_HIGH), CKD_STAGE));
    }

    @Test
    public void shouldGetAvailableBarChartColorByOptions() {
        //Given
        List<Renal> events = Arrays.asList(RENAL1, RENAL2, RENAL3);
        when(renalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(SUBJECT_1_WITH_ARM, SUBJECT_2_WITH_ARM));

        //When
        List<TrellisOptions<RenalGroupByOptions>> result = renalService.getBarChartColorByOptions(
                DUMMY_ACUITY_DATASETS, RenalFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(result).hasSize(1);
        softly.assertThat(result).extracting(TrellisOptions::getTrellisOptions).asList().containsOnly(
                asList("Empty", "ckd stage 1", "ckd stage 2"));
    }

    @Test
    public void shouldGetTrellisOptions() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT_1_WITH_ARM));
        when(renalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(RENAL1));

        List<TrellisOptions<RenalGroupByOptions>> result = renalService.getTrellisOptions(DUMMY_ACUITY_DATASETS,
                RenalFilters.empty(), PopulationFilters.empty(), RenalGroupByOptions.ACTUAL_VALUE);

        softly.assertThat(result).hasSize(2);

        softly.assertThat(result).containsExactlyInAnyOrder(
                new TrellisOptions<>(MEASUREMENT, Arrays.asList("code1 (%)")),
                new TrellisOptions<>(ARM, Arrays.asList("arm-1"))
        );
    }

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetRangePlotSelectionWhenSingleXMatch() {
        // Given
        List<Renal> events = Arrays.asList(RENAL1, RENAL2, RENAL3);
        when(renalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(SUBJECT_1_WITH_ARM, SUBJECT_2_WITH_ARM));

        final ChartGroupByOptions<Renal, RenalGroupByOptions> settings = ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, RenalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, RenalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, RenalGroupByOptions.CKD_STAGE_NAME.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();

        final HashMap<RenalGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(RenalGroupByOptions.MEASUREMENT, "ALBUMIN (g/dL)");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "1");
        selectedItems1.put(SERIES_BY, "ckd stage 2");

        final List<ChartSelectionItem<Renal, RenalGroupByOptions>> selectionItems = Arrays.asList(
                ChartSelectionItem.of(selectedTrellises, selectedItems1));

        // When
        SelectionDetail result = renalService.getSelectionDetails(DATASETS, RenalFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems));

        // Then
        softly.assertThat(result.getEventIds()).containsOnly("id2");
        softly.assertThat(result.getSubjectIds()).containsOnly("sid1");
        softly.assertThat(result.getTotalEvents()).isEqualTo(3);
        softly.assertThat(result.getTotalSubjects()).isEqualTo(2);
    }

//    @Test
//    @Category(RangeChartTests.class)
//    public void shouldGetRangePlotData() {
//        when(renalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(RENAL1, RENAL2, RENAL3));
//        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
//                .thenReturn(Arrays.asList(SUBJECT_1_WITH_ARM, SUBJECT_2_WITH_ARM));
//
//        final ChartGroupByOptions<Renal, RenalGroupByOptions> settings = ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
//                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, RenalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
//                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, RenalGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
//                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, RenalGroupByOptions.CKD_STAGE_NAME.getGroupByOptionAndParams())
//                .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
//                .build();
//        final ChartGroupByOptionsFiltered<Renal, RenalGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
//                .build();
//        // When
//        List<TrellisedRangePlot<Renal, RenalGroupByOptions>> result =
//                renalService.getRangePlot(DUMMY_ACUITY_DATASETS, settingsFiltered, RenalFilters.empty(), PopulationFilters.empty());
//
//        //Then
//        TrellisedRangePlot<Renal, RenalGroupByOptions> data = result.get(0);
//        softly.assertThat(data.getTrellisedBy()).hasSize(1);
//        softly.assertThat(data.getTrellisedBy().get(0).getTrellisOption()).isEqualTo("ALBUMIN (g/dL)");
//        softly.assertThat(data.getData()).hasSize(1);
//        softly.assertThat(data.getData().get(0).getName()).isEqualTo("ckd stage 2");
//        softly.assertThat(data.getData().get(0).getData()).extracting("x", "xRank", "dataPoints", "y", "min", "max")
//                .containsExactly(Assertions.tuple("1.0", 1.0, 1, 1., 1.0, 1.0));
//    }


    @Test
    public void shouldGetAllBarChartXAxisOptions() {
        when(renalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(RENAL10, RENAL11));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT4, SUBJECT5));

        AxisOptions<RenalGroupByOptions> availableBarChartXAxis = renalService.getAvailableBarChartXAxis(DUMMY_DETECT_DATASETS,
                RenalFilters.empty(), PopulationFilters.empty());

        List<AxisOption<RenalGroupByOptions>> options = availableBarChartXAxis.getOptions();
        softly.assertThat(options).hasSize(4);
        softly.assertThat(options).extracting(AxisOption::getGroupByOption).containsExactly(
                VISIT_NUMBER,
                VISIT_DESCRIPTION,
                STUDY_DEFINED_WEEK,
                MEASUREMENT_TIME_POINT);
    }

    @Test
    public void shouldGetAvailableBarChartXAxisOptions() {
        when(renalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(RENAL10));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJECT4));

        AxisOptions<RenalGroupByOptions> availableBarChartXAxis = renalService.getAvailableBarChartXAxis(DUMMY_DETECT_DATASETS,
                RenalFilters.empty(), PopulationFilters.empty());

        List<AxisOption<RenalGroupByOptions>> options = availableBarChartXAxis.getOptions();
        softly.assertThat(options).hasSize(3);

        softly.assertThat(options).extracting(AxisOption::getGroupByOption).containsExactly(
                VISIT_NUMBER,
                STUDY_DEFINED_WEEK,
                MEASUREMENT_TIME_POINT);
    }

    @Test
    public void shouldGetBarChartTrellisOptions() {
        when(renalDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(RENAL2, RENAL7));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Collections.singletonList(SUBJECT_1_WITH_ARM));

        List<TrellisOptions<RenalGroupByOptions>> result = renalService
                .getTrellisOptions(DATASETS, RenalFilters.empty(), PopulationFilters.empty());

        softly.assertThat(result).extracting("trellisedBy").containsOnly(MEASUREMENT, ARM);
        softly.assertThat(result)
                .filteredOn(t -> t.getTrellisedBy().equals(MEASUREMENT))
                .flatExtracting("trellisOptions").containsOnly("ALBUMIN (g/dL)", "ALT (g/dL)");
        softly.assertThat(result).filteredOn(t -> t.getTrellisedBy().equals(ARM))
                .flatExtracting("trellisOptions").containsOnly("arm-1");
    }

    @Test
    public void shouldGetBarChartColorByOptions() {
        when(renalDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(RENAL2, RENAL7));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Collections.singletonList(SUBJECT_1_WITH_ARM));

        List<TrellisOptions<RenalGroupByOptions>> result = renalService
                .getBarChartColorByOptions(DATASETS, RenalFilters.empty(), PopulationFilters.empty());

        softly.assertThat(result).extracting("trellisedBy").containsOnly(CKD_STAGE_NAME);
        softly.assertThat(result).flatExtracting("trellisOptions").containsOnly("ckd stage 2", "CKD Stage 5");
    }

    @Test
    public void shouldGetValuesForBarChartTrellised() {
        when(renalDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(RENAL1, RENAL3));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(SUBJECT_1_WITH_ARM, SUBJECT_2_WITH_ARM));

        // Given
        final ChartGroupByOptions<Renal, RenalGroupByOptions> settings = ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, RenalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, RenalGroupByOptions.CKD_STAGE_NAME.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();
        final ChartGroupByOptionsFiltered<Renal, RenalGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .build();

        // When
        List<TrellisedBarChart<Renal, RenalGroupByOptions>> result = renalService.getBarChart(DATASETS, settingsFiltered,
                RenalFilters.empty(), PopulationFilters.empty(), CountType.PERCENTAGE_OF_SUBJECTS_100_STACKED);

        // Then
        softly.assertThat(result).isNotEmpty();
        softly.assertThat(result)
                .flatExtracting(TrellisedBarChart::getTrellisedBy)
                .isNotEmpty();
    }

    @Test
    public void shouldGetSumOfValuesForBarChartData() {
        when(renalDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(RENAL10, RENAL11, RENAL12, RENAL13));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(SUBJECT4, SUBJECT5));

        //Given
        final ChartGroupByOptions<Renal, RenalGroupByOptions> settings = ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, RenalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, RenalGroupByOptions.CKD_STAGE_NAME.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();
        final ChartGroupByOptionsFiltered<Renal, RenalGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .build();

        //When
        List<TrellisedBarChart<Renal, RenalGroupByOptions>> results = renalService.getBarChart(DATASETS, settingsFiltered,
                RenalFilters.empty(), PopulationFilters.empty(), CountType.PERCENTAGE_OF_SUBJECTS_100_STACKED);

        //Then
        softly.assertThat(results).hasSize(2);
        softly.assertThat(results.get(0).getTrellisedBy().get(0).getTrellisOption()).isEqualTo("actual_arm");
        softly.assertThat(results.get(0).getTrellisedBy().get(1).getTrellisOption()).isEqualTo("code1 (unit1)");
        softly.assertThat(results.get(1).getTrellisedBy().get(0).getTrellisOption()).isEqualTo("actual_arm");
        softly.assertThat(results.get(1).getTrellisedBy().get(1).getTrellisOption()).isEqualTo("code2 (unit2)");
        List<String> categories = results.stream()
                .flatMap(result -> result.getData().stream())
                .flatMap(data -> data.getSeries().stream())
                .map(OutputBarChartEntry::getCategory)
                .distinct()
                .collect(Collectors.toList());

        categories.forEach(category -> {
                    Double sum = results.stream()
                            .flatMap(result -> result.getData().stream())
                            .flatMap(data -> data.getSeries().stream())
                            .filter(s -> s.getCategory().equals(category))
                            .mapToDouble(OutputBarChartEntry::getValue)
                            .sum();
                    softly.assertThat(sum).isCloseTo(100D, within(0.1));
                }
        );
    }

    @Test
    public void shouldGetValuesForBarChartData() {
        when(renalDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(RENAL11, RENAL13));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT5));

        //Given
        final ChartGroupByOptions<Renal, RenalGroupByOptions> settings = ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, RenalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, RenalGroupByOptions.CKD_STAGE_NAME.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();
        final ChartGroupByOptionsFiltered<Renal, RenalGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .build();

        //When
        List<TrellisedBarChart<Renal, RenalGroupByOptions>> results = renalService.getBarChart(DATASETS, settingsFiltered,
                RenalFilters.empty(), PopulationFilters.empty(), CountType.PERCENTAGE_OF_SUBJECTS_100_STACKED);

        //Then
        softly.assertThat(results.get(0).getTrellisedBy()).hasSize(2);
        softly.assertThat(results.get(0).getTrellisedBy().get(0).getTrellisOption()).isEqualTo("actual_arm");
        softly.assertThat(results.get(0).getTrellisedBy().get(1).getTrellisOption()).isEqualTo("code1 (unit1)");
        softly.assertThat(results.get(0).getData().get(0).getSeries()).containsExactly(new OutputBarChartEntry("1", 1, 50.0, 1));
        softly.assertThat(results.get(0).getData().get(1).getSeries()).containsExactly(new OutputBarChartEntry("1", 1, 50.0, 1));
    }

    @Test
    public void shouldGetBarChartSelectionWhenTrellisedByMeasurementAndArm() {
        // Given
        when(renalDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(RENAL10, RENAL11, RENAL12, RENAL13));
        when(populationDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(SUBJECT4, SUBJECT5));

        final ChartGroupByOptions<Renal, RenalGroupByOptions> settings = ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, RenalGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, RenalGroupByOptions.CKD_STAGE_NAME.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();

        final HashMap<RenalGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "code1 (unit1)");
        selectedTrellises.put(ARM, "actual_arm");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "1");
        selectedItems1.put(COLOR_BY, "CKD Stage 2");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(X_AXIS, "1");
        selectedItems2.put(COLOR_BY, "CKD Stage 5");

        final List<ChartSelectionItem<Renal, RenalGroupByOptions>> selectionItems = Arrays.asList(
                ChartSelectionItem.of(selectedTrellises, selectedItems1),
                ChartSelectionItem.of(selectedTrellises, selectedItems2)
        );

        // When
        SelectionDetail result = renalService.getBarChartSelectionDetails(
                DATASETS, RenalFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems));

        // Then
        softly.assertThat(result.getEventIds()).containsOnly("e10", "e13");
        softly.assertThat(result.getSubjectIds()).containsOnly("sid4", "sid5");
        softly.assertThat(result.getTotalEvents()).isEqualTo(4);
        softly.assertThat(result.getTotalSubjects()).isEqualTo(2);
    }

    @Test
    public void shouldGetCategoriesForContinuesXAxisOption() {
        when(renalDatasetsDataProvider.loadData(any())).thenReturn(Arrays.asList(RENAL11, RENAL13));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT5));

        //Given
        final ChartGroupByOptions<Renal, RenalGroupByOptions> settings = ChartGroupByOptions.<Renal, RenalGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, RenalGroupByOptions.MEASUREMENT_TIME_POINT.getGroupByOptionAndParams(
                        Params.builder()
                                .with(Param.BIN_SIZE, 1)
                                .with(Param.TIMESTAMP_TYPE, TimestampType.DAYS_SINCE_FIRST_DOSE)
                                .build()))
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, RenalGroupByOptions.CKD_STAGE_NAME.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(RenalGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();
        final ChartGroupByOptionsFiltered<Renal, RenalGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .build();

        //When
        List<TrellisedBarChart<Renal, RenalGroupByOptions>> results = renalService.getBarChart(DATASETS, settingsFiltered,
                RenalFilters.empty(), PopulationFilters.empty(), CountType.PERCENTAGE_OF_SUBJECTS_100_STACKED);

        //Then
        softly.assertThat(results.get(0).getData().get(0).getCategories())
                .containsExactly("2", "3", "4", "5", "6");
    }
}
