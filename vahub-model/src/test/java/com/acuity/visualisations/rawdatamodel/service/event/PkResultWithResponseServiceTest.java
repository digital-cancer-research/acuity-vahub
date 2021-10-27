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
import com.acuity.visualisations.rawdatamodel.dataproviders.AssessedTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PkResultDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PkResultWithResponseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PkResultFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.BoxPlotTests;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AssessmentAxisOptions.AssessmentType;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Params;
import com.acuity.visualisations.rawdatamodel.vo.NonTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.PkResultRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBoxplotEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.pkresult.CycleDay;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.ANALYTE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.CYCLE_DAY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.MEASUREMENT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.MEASUREMENT_TIMEPOINT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.OVERALL_RESPONSE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.PARAMETER_VALUE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.VISIT_NUMBER;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param.ASSESSMENT_TYPE;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param.WEEK_NUMBER;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PkResultWithResponseServiceTest {
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Autowired
    @Qualifier("pkResultWithResponseService")
    private PkResultWithResponseService pkResultWithResponseService;
    @MockBean(name = "pkResultDatasetsDataProvider")
    private PkResultDatasetsDataProvider pkResultDatasetsDataProvider;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private PkResultWithResponseDatasetsDataProvider pkResultWithResponseDatasetsDataProvider;
    @MockBean
    private AssessedTargetLesionDatasetsDataProvider assessedTargetLesionDatasetsDataProvider;

    // Double.toString(1d) yields "1.0" while decimalFormat.format(1d) yields "1"
    private final static DecimalFormat decimalFormat = new DecimalFormat();
    private static Subject subject1 = Subject.builder().subjectId("sid1").subjectCode("E001").studyPart("Part2")
            .baselineDate(DateUtils.toDate("01.01.2000")).build();
    private static Subject subject2 = Subject.builder().subjectId("sid2").subjectCode("E002").baselineDate(DateUtils.toDate("01.02.2000")).build();
    private static PkResult pkResult11 = new PkResult(PkResultRaw.builder()
            .id("id11")
            .analyte("a1")
            .parameter("CMax")
            .parameterValue(10.0)
            .parameterUnit("mg")
            .treatment(20.)
            .protocolScheduleStartDay("20")
            .treatmentCycle("Cycle0")
            .actualDose(25.)
            .visit("Cycle 0 Day 1")
            .visitNumber(30)
            .bestOverallResponse("Complete Response")
            .build(), subject1);
    private static PkResult pkResult12 = new PkResult(PkResultRaw.builder()
            .id("id12")
            .analyte("a1")
            .parameter("CMax")
            .parameterValue(15.0)
            .parameterUnit("mg")
            .treatment(50.)
            .visitNumber(30)
            .bestOverallResponse("Missing Target Lesions")
            .build(), subject1);
    private static PkResult pkResult13 = new PkResult(PkResultRaw.builder()
            .id("id13")
            .analyte("a1")
            .parameter("Smth")
            .parameterValue(100.0)
            .parameterUnit("mg")
            .treatment(50.)
            .bestOverallResponse("Missing Target Lesions")
            .build(), subject1);
    private static PkResult pkResult21 = new PkResult(PkResultRaw.builder()
            .id("id21")
            .analyte("a1")
            .parameter("CMax")
            .parameterValue(20.0)
            .parameterUnit("mg")
            .treatment(20.)
            .visitNumber(30)
            .bestOverallResponse("Complete Response")
            .build(), subject2);
    private static PkResult pkResult22 = new PkResult(PkResultRaw.builder()
            .id("id22")
            .analyte("a1")
            .parameter("CMax")
            .parameterValue(30.0)
            .parameterUnit("mg")
            .treatment(50.)
            .visitNumber(30)
            .bestOverallResponse("Missing Target Lesions")
            .build(), subject2);
    private static PkResult pkResult23 = new PkResult(PkResultRaw.builder()
            .id("id23")
            .analyte("a1")
            .parameter("CMax")
            .parameterValue(25.0)
            .parameterUnit("mg")
            .treatment(40.)
            .visitNumber(30)
            .bestOverallResponse("Partial Response")
            .build(), subject2);
    private static PkResult pkResult24 = new PkResult(PkResultRaw.builder()
            .id("id24")
            .analyte("a1")
            .parameter("Smth")
            .parameterValue(30.0)
            .parameterUnit("mg")
            .treatment(55.)
            .treatmentCycle("Cycle1")
            .bestOverallResponse("bstResp2")
            .build(), subject2);
    private static PkResult pkResult25 = new PkResult(PkResultRaw.builder()
            .id("id25")
            .analyte("a1")
            .parameter("Smth")
            .parameterValue(25.0)
            .parameterUnit("mg")
            .treatment(45.)
            .treatmentCycle("Cycle2")
            .build(), subject2);
    private static PkResult pkResult26 = new PkResult(PkResultRaw.builder()
            .id("id26")
            .analyte("a2")
            .parameter("CMin")
            .parameterValue(25.0)
            .parameterUnit("ng/l")
            .treatment(55.)
            .visitNumber(4)
            .treatmentCycle("Cycle1")
            .build(), subject2);
    private static PkResult pkResult27 = new PkResult(PkResultRaw.builder()
            .id("id27")
            .analyte("a3")
            .parameter("CMin")
            .parameterValue(25.0)
            .parameterUnit("ng/l")
            .treatment(45.)
            .visitNumber(4)
            .treatmentCycle("Cycle1")
            .build(), subject2);

    private static AssessedTargetLesion atl11 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder()
            .id("atl11").response("Partial Response").assessmentFrequency(6)
            .targetLesionRaw(TargetLesionRaw.builder().lesionDate(DateUtils.toDate("15.02.2000")).build()).build(), subject1);
    private static AssessedTargetLesion atl12 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder()
            .id("atl12").response("Complete Response").assessmentFrequency(6)
            .targetLesionRaw(TargetLesionRaw.builder().lesionDate(DateUtils.toDate("25.03.2000")).build()).build(), subject1);
    private static AssessedTargetLesion atl13 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder()
            .id("atl13").response("Complete Response").assessmentFrequency(6)
            .targetLesionRaw(TargetLesionRaw.builder().lesionDate(DateUtils.toDate("28.03.2000")).build()).build(), subject1);
    private static AssessedTargetLesion atl14 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder()
            .id("atl14").response("Complete Response").bestResponse("Stable Disease").assessmentFrequency(6)
            .targetLesionRaw(TargetLesionRaw.builder().lesionDate(DateUtils.toDate("14.03.2000")).build())
            .nonTargetLesionRaw(NonTargetLesionRaw.builder().responseShort("NTL response").build())
            .assessmentRaw(AssessmentRaw.builder().responseShort("PD").newLesionSinceBaseline("yes").build()).build(), subject2);
    private static AssessedTargetLesion atl15 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder()
            .id("atl15").response("Partial Response").assessmentFrequency(6)
            .targetLesionRaw(TargetLesionRaw.builder().lesionDate(DateUtils.toDate("15.01.2000")).build()).build(), subject1);

    @Before
    public void setUp() {
        when(pkResultWithResponseDatasetsDataProvider.loadData(any()))
                .thenReturn(newArrayList(pkResult11, pkResult12, pkResult13, pkResult21, pkResult22,
                        pkResult23, pkResult24, pkResult25, pkResult26, pkResult27));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1, subject2));
        when(assessedTargetLesionDatasetsDataProvider.loadData((any()))).thenReturn(newArrayList(atl11, atl12));
    }

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotXAxisOptions() {
        AxisOptions<PkResultGroupByOptions> result
                = pkResultWithResponseService.getAvailableBoxPlotXAxis(DATASETS,
                PkResultFilters.empty(), PopulationFilters.empty());
        softly.assertThat(result.getOptions())
                .extracting(AxisOption::getGroupByOption)
                .containsExactlyInAnyOrder(OVERALL_RESPONSE);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void testGetBoxPlotData() {
        when(pkResultWithResponseDatasetsDataProvider.loadData(any())).thenReturn(newArrayList(pkResult26, pkResult27));

        // Given
        final ChartGroupByOptions<PkResult, PkResultGroupByOptions> settings = getPkResultSettings(VISIT_NUMBER);

        final ChartGroupByOptionsFiltered<PkResult, PkResultGroupByOptions> settingsFiltered
                = ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(MEASUREMENT, "CMax (mg)")
                .withFilterByTrellisOption(MEASUREMENT_TIMEPOINT, "4")
                .build();

        // When
        final List<TrellisedBoxPlot<PkResult, PkResultGroupByOptions>> result
                = pkResultWithResponseService.getBoxPlot(DATASETS, settingsFiltered,
                PkResultFilters.empty(), PopulationFilters.empty());

        // Then
        softly.assertThat(result)
                .flatExtracting(TrellisedBoxPlot::getStats).size().isEqualTo(0);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void testGetBoxPlotDataWeek6() {

        final ChartGroupByOptions<PkResult, PkResultGroupByOptions> settings = getPkResultSettings(VISIT_NUMBER,
                AssessmentType.WEEK, 6);
        final ChartGroupByOptionsFiltered<PkResult, PkResultGroupByOptions> settingsFiltered
                = ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(MEASUREMENT, "CMax (mg)")
                .withFilterByTrellisOption(MEASUREMENT_TIMEPOINT, "30")
                .build();

        // When
        final List<TrellisedBoxPlot<PkResult, PkResultGroupByOptions>> result
                = pkResultWithResponseService.getBoxPlot(DATASETS, settingsFiltered,
                PkResultFilters.empty(), PopulationFilters.empty());

        // Then
        softly.assertThat(result)
                .flatExtracting(TrellisedBoxPlot::getStats).extracting(
                OutputBoxplotEntry::getX,
                OutputBoxplotEntry::getSubjectCount,
                OutputBoxplotEntry::getEventCount,
                OutputBoxplotEntry::getMedian,
                OutputBoxplotEntry::getLowerQuartile, OutputBoxplotEntry::getUpperQuartile,
                OutputBoxplotEntry::getLowerWhisker, OutputBoxplotEntry::getUpperWhisker)
                .containsExactly(
                        tuple("Complete Response", null, null, null, null, null, null, null),
                        tuple("Partial Response", 1L, 2L, 12.5, 11.25, 13.75, 10.0, 15.0),
                        tuple("Stable Disease", null, null, null, null, null, null, null),
                        tuple("Progressive Disease", null, null, null, null, null, null, null),
                        tuple("Not Evaluable", null, null, null, null, null, null, null),
                        tuple("No Assessment", null, null, null, null, null, null, null),
                        tuple("Missing Target Lesions", null, null, null, null, null, null, null),
                        tuple("No Evidence of Disease", null, null, null, null, null, null, null));
    }

    @Test
    @Category(BoxPlotTests.class)
    public void testGetBoxPlotDataWithEmptyBestOverallResponses() {
        // Given
        final ChartGroupByOptions<PkResult, PkResultGroupByOptions> settings = getPkResultSettings(VISIT_NUMBER);

        final ChartGroupByOptionsFiltered<PkResult, PkResultGroupByOptions> settingsFiltered
                = ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(MEASUREMENT, "CMax (mg)")
                .withFilterByTrellisOption(MEASUREMENT_TIMEPOINT, "30")
                .build();

        // When
        final List<TrellisedBoxPlot<PkResult, PkResultGroupByOptions>> result
                = pkResultWithResponseService.getBoxPlot(DATASETS, settingsFiltered,
                PkResultFilters.empty(), PopulationFilters.empty());

        // Then
        softly.assertThat(result)
                .flatExtracting(TrellisedBoxPlot::getStats).extracting(
                OutputBoxplotEntry::getX,
                OutputBoxplotEntry::getSubjectCount,
                OutputBoxplotEntry::getEventCount,
                OutputBoxplotEntry::getMedian,
                OutputBoxplotEntry::getLowerQuartile, OutputBoxplotEntry::getUpperQuartile,
                OutputBoxplotEntry::getLowerWhisker, OutputBoxplotEntry::getUpperWhisker)
                .containsExactly(
                        tuple("Complete Response", 2L, 2L, 15.0, 12.5, 17.5, 10.0, 20.0),
                        tuple("Partial Response", 1L, 1L, 25.0, 25.0, 25.0, 25.0, 25.0),
                        tuple("Stable Disease", null, null, null, null, null, null, null),
                        tuple("Progressive Disease", null, null, null, null, null, null, null),
                        tuple("Not Evaluable", null, null, null, null, null, null, null),
                        tuple("No Assessment", null, null, null, null, null, null, null),
                        tuple("Missing Target Lesions", 2L, 2L, 22.5, 18.75, 26.25, 15.0, 30.0),
                        tuple("No Evidence of Disease", null, null, null, null, null, null, null));
    }

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotSelectionWhenTrellisedByMeasurement() {
        final ChartGroupByOptions<PkResult, PkResultGroupByOptions> settings = getPkResultSettings(CYCLE_DAY);

        final HashMap<PkResultGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(ANALYTE, "a1");
        selectedTrellises.put(MEASUREMENT, "CMax (mg)");
        selectedTrellises.put(MEASUREMENT_TIMEPOINT, new CycleDay("Cycle0", "20"));

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "Complete Response");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(X_AXIS, "Missing Target Lesions");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems3 = new HashMap<>();
        selectedItems3.put(X_AXIS, "Partial Response");

        PkResult pkResultOtherMeasurement = new PkResult(PkResultRaw.builder().analyte("a1").parameter("Other")
                .parameterValue(25.0)
                .parameterUnit("mg").treatment(20.)
                .bestOverallResponse("Partial Response")
                .id("id31")
                .build(), subject2);
        PkResult pkResultOtherTimepoint = new PkResult(PkResultRaw.builder().analyte("a1").parameter("CMax")
                .parameterValue(19.0)
                .parameterUnit("mg").treatment(20.).treatmentCycle("Cycle1")
                .bestOverallResponse("Complete Response")
                .id("id32")
                .build(), subject2);
        PkResult pkResultOtherAnalyte = new PkResult(PkResultRaw.builder().analyte("a2").parameter("CMax")
                .parameterValue(23.0)
                .parameterUnit("mg").treatment(20.)
                .bestOverallResponse("Partial Response")
                .id("id33")
                .build(), subject2);

        when(pkResultWithResponseDatasetsDataProvider.loadData(any()))
                .thenReturn(newArrayList(pkResult11, pkResult12, pkResult13, pkResult21, pkResult22,
                        pkResult23, pkResult24, pkResult25, pkResult26, pkResult27,
                        pkResultOtherMeasurement, pkResultOtherTimepoint, pkResultOtherAnalyte));

        final List<ChartSelectionItemRange<PkResult,
                PkResultGroupByOptions, Double>> selectionItems = Arrays.asList(
                ChartSelectionItemRange.of(selectedTrellises, selectedItems1, 10., 20.),
                ChartSelectionItemRange.of(selectedTrellises, selectedItems2, 19., 20.),
                ChartSelectionItemRange.of(selectedTrellises, selectedItems3, 22., 26.)
        );

        SelectionDetail result = pkResultWithResponseService.getRangedSelectionDetails(
                DATASETS, PkResultFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems));

        softly.assertThat(result.getEventIds()).containsOnly("id11");
        softly.assertThat(result.getSubjectIds()).containsOnly("sid1");
        softly.assertThat(result).extracting(SelectionDetail::getTotalEvents, SelectionDetail::getTotalSubjects)
                .containsExactly(13, 2);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void testGetBoxPlotSelectionOnWeek6() {
        final ChartGroupByOptions<PkResult, PkResultGroupByOptions> settings = getPkResultSettings(VISIT_NUMBER,
                AssessmentType.WEEK, 6);

        final HashMap<PkResultGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(ANALYTE, "a1");
        selectedTrellises.put(MEASUREMENT, "CMax (mg)");
        selectedTrellises.put(MEASUREMENT_TIMEPOINT, "30");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "Partial Response");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(X_AXIS, "No Assessment");

        final List<ChartSelectionItemRange<PkResult,
                PkResultGroupByOptions, Double>> selectionItems = Arrays.asList(
                ChartSelectionItemRange.of(selectedTrellises, selectedItems1, 10., 20.),
                ChartSelectionItemRange.of(selectedTrellises, selectedItems2, 20., 28.)
        );

        SelectionDetail result = pkResultWithResponseService.getRangedSelectionDetails(
                DATASETS, PkResultFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems));

        softly.assertThat(result.getEventIds()).containsExactlyInAnyOrder("id11", "id12");
        softly.assertThat(result.getSubjectIds()).containsExactlyInAnyOrder("sid1");
        softly.assertThat(result).extracting(SelectionDetail::getTotalEvents, SelectionDetail::getTotalSubjects)
                .containsExactly(10, 2);
    }

    @Test
    public void shouldGetDetailsOnDemand() {
        final List<PkResult> pkResultList = Arrays.asList(pkResult11, pkResult12, pkResult21, pkResult22);
        final Set<String> ids = pkResultList.stream().map(PkResult::getId).collect(toSet());
        List<SortAttrs> pkResultDefaultSortAttr = Arrays.asList(
                new SortAttrs("subjectId", false),
                new SortAttrs("analyte", false),
                new SortAttrs("cycle", false),
                new SortAttrs("nominalDay", false),
                new SortAttrs("parameter", false));
        final List<Map<String, String>> dodData = pkResultWithResponseService.getDetailsOnDemandData(DUMMY_ACUITY_DATASETS, ids,
                pkResultDefaultSortAttr, 0, Integer.MAX_VALUE);
        final PkResult pkResult = pkResultList.get(0);
        final PkResultRaw event = pkResult.getEvent();
        final Map<String, String> dod = dodData.get(0);
        softly.assertThat(dodData).hasSize(pkResultList.size());
        softly.assertThat(dod).hasSize(13);
        softly.assertThat(pkResult.getSubjectCode()).isEqualTo(dod.get("subjectId"));
        softly.assertThat(pkResult.getSubject().getClinicalStudyCode()).isEqualTo(dod.get("studyId"));
        softly.assertThat(event.getAnalyte()).isEqualTo(dod.get("analyte"));
        softly.assertThat(event.getParameter()).isEqualTo(dod.get("parameter"));
        softly.assertThat(event.getProtocolScheduleStartDay()).isEqualTo(dod.get("protocolScheduleStartDay"));
        softly.assertThat(decimalFormat.format(event.getTreatment())).isEqualTo(dod.get("treatment"));
        softly.assertThat(decimalFormat.format(event.getParameterValue())).isEqualTo(dod.get("parameterValue"));
        softly.assertThat(event.getTreatmentCycle()).isEqualTo(dod.get("cycle"));
        softly.assertThat(event.getParameterUnit()).isEqualTo(dod.get("parameterUnit"));
        softly.assertThat(decimalFormat.format(event.getActualDose())).isEqualTo(dod.get("actualDose"));
        softly.assertThat(event.getVisit()).isEqualTo(dod.get("visit"));
    }

    @Test
    public void shouldGetRecistDetailsOnDemand() {
        when(assessedTargetLesionDatasetsDataProvider.loadDataByVisit((any()))).thenReturn(newArrayList(atl11, atl12, atl13, atl14, atl15));

        final List<AssessedTargetLesion> assessedTargetLesions = Arrays.asList(atl11, atl12, atl14, atl15);
        final Set<String> ids = assessedTargetLesions.stream().map(AssessedTargetLesion::getId).collect(toSet());
        List<SortAttrs> pkResultDefaultSortAttr = Arrays.asList(
                new SortAttrs("subjectId", false),
                new SortAttrs("assesmentWeek", false));
        final List<Map<String, String>> dodData = pkResultWithResponseService.getRecistDetailsOnDemandData(DUMMY_ACUITY_DATASETS, ids,
                pkResultDefaultSortAttr, 0, Integer.MAX_VALUE);
        final Map<String, String> dodFromAtl11 = dodData.get(0);
        final Map<String, String> dodFromAtl12 = dodData.get(1);
        final Map<String, String> dodFromAtl14 = dodData.get(2);
        final String week6 = "6";
        final String week12 = "12";
        softly.assertThat(dodFromAtl11).hasSize(8);
        softly.assertThat(dodData).hasSize(3);  //because atl13 has an id, which is not present in event ids,
                                                //and atl15 is skipping because atl11 is closer to week 6, than atl15

        softly.assertThat(atl11.getSubjectCode()).isEqualTo(dodFromAtl11.get("subjectId"));
        softly.assertThat(atl11.getSubject().getStudyPart()).isEqualTo(dodFromAtl11.get("studyPart"));
        softly.assertThat(atl11.getSubject().getClinicalStudyCode()).isEqualTo(dodFromAtl11.get("studyId"));
        softly.assertThat(atl11.getEvent().getOverallResponse()).isEqualTo(dodFromAtl11.get("overallResponse"));
        softly.assertThat(atl11.getEvent().getNewLesions()).isEqualTo(dodFromAtl11.get("newLesions"));
        softly.assertThat(atl11.getEvent().getBestResponse()).isEqualTo(dodFromAtl11.get("bestOverallResponse"));
        softly.assertThat(atl11.getEvent().getNtlResponse()).isEqualTo(dodFromAtl11.get("ntlResponse"));
        softly.assertThat(week6).isEqualTo(dodFromAtl11.get("assessmentWeek"));

        softly.assertThat(atl12.getSubjectCode()).isEqualTo(dodFromAtl12.get("subjectId"));
        softly.assertThat(atl12.getSubject().getStudyPart()).isEqualTo(dodFromAtl12.get("studyPart"));
        softly.assertThat(atl12.getSubject().getClinicalStudyCode()).isEqualTo(dodFromAtl12.get("studyId"));
        softly.assertThat(atl12.getEvent().getOverallResponse()).isEqualTo(dodFromAtl12.get("overallResponse"));
        softly.assertThat(atl12.getEvent().getNewLesions()).isEqualTo(dodFromAtl12.get("newLesions"));
        softly.assertThat(atl12.getEvent().getBestResponse()).isEqualTo(dodFromAtl12.get("bestOverallResponse"));
        softly.assertThat(atl12.getEvent().getNtlResponse()).isEqualTo(dodFromAtl12.get("ntlResponse"));
        softly.assertThat(week12).isEqualTo(dodFromAtl12.get("assessmentWeek"));

        softly.assertThat(atl14.getSubjectCode()).isEqualTo(dodFromAtl14.get("subjectId"));
        softly.assertThat(atl14.getSubject().getStudyPart()).isEqualTo(dodFromAtl14.get("studyPart"));
        softly.assertThat(atl14.getSubject().getClinicalStudyCode()).isEqualTo(dodFromAtl14.get("studyId"));
        softly.assertThat(atl14.getEvent().getOverallResponse()).isEqualTo(dodFromAtl14.get("overallResponse"));
        softly.assertThat(atl14.getEvent().getNewLesions()).isEqualTo(dodFromAtl14.get("newLesions"));
        softly.assertThat("SD").isEqualTo(dodFromAtl14.get("bestOverallResponse"));
        softly.assertThat(atl14.getEvent().getNtlResponse()).isEqualTo(dodFromAtl14.get("ntlResponse"));
        softly.assertThat(week6).isEqualTo(dodFromAtl14.get("assessmentWeek"));
    }

    private ChartGroupByOptions<PkResult, PkResultGroupByOptions> getPkResultSettings(PkResultGroupByOptions timepointOption) {
        return getPkResultSettings(timepointOption, AssessmentType.BEST_CHANGE, 0);
    }

    private ChartGroupByOptions<PkResult, PkResultGroupByOptions> getPkResultSettings(PkResultGroupByOptions timepointOption,
                                                                                      AssessmentType assessmentType, int week) {
        return ChartGroupByOptions.<PkResult, PkResultGroupByOptions>builder()
                .withOption(X_AXIS, OVERALL_RESPONSE.getGroupByOptionAndParams(Params.builder()
                        .with(ASSESSMENT_TYPE, assessmentType)
                        .with(WEEK_NUMBER, week).build()))
                .withOption(Y_AXIS, PARAMETER_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(ANALYTE.getGroupByOptionAndParams())
                .withTrellisOption(MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(MEASUREMENT_TIMEPOINT.getGroupByOptionAndParams(getTimepointParams(timepointOption)))
                .build();
    }

    private Params getTimepointParams(PkResultGroupByOptions option) {
        return Params.builder().with(GroupByOption.Param.TIMESTAMP_TYPE, option).build();
    }
}
