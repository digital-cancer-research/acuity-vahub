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
import com.acuity.visualisations.rawdatamodel.dataproviders.PkResultDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PkResultWithResponseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PkResultFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.PkResultService.PkResultTrellisOptions;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.BoxPlotTests;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Params;
import com.acuity.visualisations.rawdatamodel.vo.PkResultRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBoxplotEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.pkresult.CycleDay;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import com.acuity.va.security.acl.domain.Datasets;
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

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.ACTUAL_DOSE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.ANALYTE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.CYCLE_DAY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.DOSE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.MEASUREMENT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.MEASUREMENT_TIMEPOINT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.PARAMETER_VALUE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.VISIT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions.VISIT_NUMBER;
import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PkResultServiceTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Autowired
    private PkResultService pkResultService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean(name = "pkResultDatasetsDataProvider")
    private PkResultDatasetsDataProvider pkResultDatasetsDataProvider;
    @MockBean(name = "pkResultWithResponseDatasetsDataProvider")
    private PkResultWithResponseDatasetsDataProvider pkResultWithResponseDatasetsDataProvider;

    // Double.toString(1d) yields "1.0" while decimalFormat.format(1d) yields "1"
    private final static DecimalFormat decimalFormat = new DecimalFormat();
    private final static String SUBJECT_1 = "subject1";

    private static Subject subject1 = Subject.builder().subjectId("sid1").build();
    private static Subject subject2 = Subject.builder().subjectId("sid2").build();
    private static PkResult pkResult11 = new PkResult(PkResultRaw.builder().id("id11").analyte("a1").parameter("CMax")
            .parameterValue(10.0).parameterUnit("mg").treatment(20.)
            .treatmentCycle("Cycle 3").protocolScheduleStartDay("1")
            .visit("Visit 1").visitNumber(1).actualDose(15.).build(), subject1);
    private static PkResult pkResult12 = new PkResult(PkResultRaw.builder().id("id12").analyte("a1").parameter("CMax")
            .parameterValue(15.0).parameterUnit("mg").treatment(50.)
            .treatmentCycle("Cycle 3").protocolScheduleStartDay("1").visit("Visit 2").visitNumber(1).actualDose(55.).build(), subject1);
    private static PkResult pkResult13 = new PkResult(PkResultRaw.builder().id("id13").analyte("a1").parameter("Smth")
            .parameterValue(100.0).parameterUnit("mg").treatment(50.)
            .treatmentCycle("Cycle 3").actualDose(45.).build(), subject1);
    private static PkResult pkResult21 = new PkResult(PkResultRaw.builder().id("id21").analyte("a1").parameter("CMax")
            .parameterValue(20.0).parameterUnit("mg").treatment(20.)
            .treatmentCycle("Cycle 3").protocolScheduleStartDay("1").visit("Visit 1").actualDose(15.).build(), subject2);
    private static PkResult pkResult22 = new PkResult(PkResultRaw.builder().id("id22").analyte("a1").parameter("CMax")
            .parameterValue(30.0).parameterUnit("mg").treatment(50.)
            .treatmentCycle("Cycle 3").protocolScheduleStartDay("1").actualDose(45.).build(), subject2);
    private static PkResult pkResult23 = new PkResult(PkResultRaw.builder().id("id23").analyte("a1").parameter("CMax")
            .parameterValue(25.0).parameterUnit("mg").treatment(40.)
            .treatmentCycle("Cycle 3").protocolScheduleStartDay("1").actualDose(45.).build(), subject2);
    private static PkResult pkResult24 = new PkResult(PkResultRaw.builder().id("id24").analyte("a1").parameter("Smth")
            .parameterValue(30.0).parameterUnit("mg").treatment(55.).actualDose(55.).treatmentCycle("Cycle1")
            .visit("Visit 1").build(), subject2);
    private static PkResult pkResult25 = new PkResult(PkResultRaw.builder().id("id25").analyte("a1").parameter("Smth")
            .parameterValue(25.0).parameterUnit("mg").treatment(45.).actualDose(45.).treatmentCycle("Cycle2")
            .build(), subject2);
    private static PkResult pkResult26 = new PkResult(PkResultRaw.builder().id("id26").analyte("a2").parameter("CMin")
            .parameterValue(25.0).parameterUnit("ng/l").treatment(55.).actualDose(55.)
            .protocolScheduleStartDay("1").visitNumber(5).build(), subject2);
    private static PkResult pkResult27 = new PkResult(PkResultRaw.builder().id("id27").analyte("a3").parameter("CMin")
            .protocolScheduleStartDay("2").visitNumber(5).visit("Visit 1").subjectId(SUBJECT_1).parameterValue(25.0).parameterUnit("ng/l")
            .treatment(45.).actualDose(35.).build(), subject2);
    private static PkResult pkResultEmptyTimepoints = new PkResult(PkResultRaw.builder().id("id30").analyte("a1")
            .parameter("CMax").parameterValue(10.0).parameterUnit("mg").treatment(20.).build(), subject1);

    @Before
    public void setUp() {
        when(pkResultDatasetsDataProvider.loadData(any())).thenReturn(newArrayList(pkResult11, pkResult12, pkResult13,
                pkResult21, pkResult22, pkResult23, pkResult24, pkResult25, pkResult26, pkResult27, pkResultEmptyTimepoints));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1, subject2));
    }

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotXAxisOptions() {
        AxisOptions<PkResultGroupByOptions> result = pkResultService.getAvailableBoxPlotXAxis(DATASETS,
                PkResultFilters.empty(), PopulationFilters.empty());

        softly.assertThat(result.getOptions())
                .extracting(AxisOption::getGroupByOption)
                .containsExactlyInAnyOrder(DOSE, ACTUAL_DOSE);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void testGetBoxPlotOptionsTimepointCycleDay() {
        List<PkResultTrellisOptions> result = testGetBoxPlotOptionsForOption(CYCLE_DAY.toString());

        @SuppressWarnings("unchecked")
        Map<String, Set<String>> optionsForCycleDay = (Map<String, Set<String>>) result.get(0)
                .getTimepointsByParameter().get("CMax (mg)").get(CYCLE_DAY);

        softly.assertThat(result.get(0).getTimepointsByParameter().get("CMax (mg)").keySet()).containsExactly(CYCLE_DAY);
        softly.assertThat(optionsForCycleDay).containsKeys("Cycle 3", DEFAULT_EMPTY_VALUE);
        softly.assertThat(optionsForCycleDay.get("Cycle 3")).containsExactly("1");
        softly.assertThat(optionsForCycleDay.get(DEFAULT_EMPTY_VALUE)).containsExactly(DEFAULT_EMPTY_VALUE);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void testGetBoxPlotOptionsTimepointVisit() {
        List<PkResultTrellisOptions> result = testGetBoxPlotOptionsForOption(VISIT.toString());

        @SuppressWarnings("unchecked")
        List<String> optionsForVisit = (List<String>) result.get(0)
                .getTimepointsByParameter().get("CMax (mg)").get(VISIT);

        softly.assertThat(result.get(0).getTimepointsByParameter().get("CMax (mg)").keySet()).containsExactly(VISIT);
        softly.assertThat(optionsForVisit).containsExactly("Visit 1", "Visit 2", DEFAULT_EMPTY_VALUE);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void testGetBoxPlotOptionsTimepointVisitNumber() {
        List<PkResultTrellisOptions> result = testGetBoxPlotOptionsForOption(VISIT_NUMBER.toString());

        @SuppressWarnings("unchecked")
        List<String> optionsForVisitNumber = (List<String>) result.get(0)
                .getTimepointsByParameter().get("CMax (mg)").get(VISIT_NUMBER);

        softly.assertThat(result.get(0).getTimepointsByParameter().get("CMax (mg)").keySet()).containsExactly(VISIT_NUMBER);
        softly.assertThat(optionsForVisitNumber).containsExactly("1", DEFAULT_EMPTY_VALUE);
    }

    private List<PkResultTrellisOptions> testGetBoxPlotOptionsForOption(String timepointType) {
        List<PkResultTrellisOptions> result = pkResultService.getBoxPlotOptions(DATASETS,
                PkResultFilters.empty(), PopulationFilters.empty(), timepointType);

        softly.assertThat(result)
                .extracting(PkResultTrellisOptions::getTrellisedBy,
                        PkResultTrellisOptions::getTimepointTrellisedBy)
                .containsExactly(tuple(MEASUREMENT, MEASUREMENT_TIMEPOINT));
        softly.assertThat(result.get(0).getTrellisOptions().stream().map(Object::toString).collect(toList()))
                .containsExactly("CMax (mg)", "CMin (ng/l)", "Smth (mg)");
        softly.assertThat(result.get(0).getTimepointsByParameter().keySet())
                .containsExactly("CMax (mg)", "CMin (ng/l)", "Smth (mg)");
        return result;
    }

    @Test
    @Category(BoxPlotTests.class)
    public void testGetBoxPlotDataTimepointCycleDay() {

        final ChartGroupByOptions<PkResult, PkResultGroupByOptions> settings = getPkResultSettings(CYCLE_DAY);
        final ChartGroupByOptionsFiltered<PkResult, PkResultGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered
                .builder(settings)
                .withFilterByTrellisOption(MEASUREMENT, "CMax (mg)")
                .withFilterByTrellisOption(MEASUREMENT_TIMEPOINT, new CycleDay("Cycle 3", "1"))
                .build();

        final Tuple[] expectedResult = {tuple("20.0", 2L, 2L, 15.0, 12.5, 17.5, 10.0, 20.0),
                tuple("40.0", 1L, 1L, 25.0, 25.0, 25.0, 25.0, 25.0),
                tuple("50.0", 2L, 2L, 22.5, 18.75, 26.25, 15.0, 30.0)};

        testBoxplotIsCorrect(settingsFiltered, expectedResult);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void testGetBoxPlotDataTimepointVisit() {

        final ChartGroupByOptions<PkResult, PkResultGroupByOptions> settings = getPkResultSettings(VISIT);
        final ChartGroupByOptionsFiltered<PkResult, PkResultGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered
                .builder(settings)
                .withFilterByTrellisOption(MEASUREMENT, "CMax (mg)")
                .withFilterByTrellisOption(MEASUREMENT_TIMEPOINT, "Visit 1")
                .build();

        final Tuple[] expectedResult = {tuple("20.0", 2L, 2L, 15.0, 12.5, 17.5, 10.0, 20.0)};
        testBoxplotIsCorrect(settingsFiltered, expectedResult);
    }

    @Test
    @Category(BoxPlotTests.class)
    public void testGetBoxPlotDataTimepointVisitNumber() {

        final ChartGroupByOptions<PkResult, PkResultGroupByOptions> settings = getPkResultSettings(VISIT_NUMBER);
        final ChartGroupByOptionsFiltered<PkResult, PkResultGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered
                .builder(settings)
                .withFilterByTrellisOption(MEASUREMENT, "CMax (mg)")
                .withFilterByTrellisOption(MEASUREMENT_TIMEPOINT, 1)
                .build();

        final Tuple[] expectedResult = {tuple("20.0", 1L, 1L, 10.0, 10.0, 10.0, 10.0, 10.0),
                tuple("50.0", 1L, 1L, 15.0, 15.0, 15.0, 15.0, 15.0)};
        testBoxplotIsCorrect(settingsFiltered, expectedResult);
    }

    private void testBoxplotIsCorrect(ChartGroupByOptionsFiltered<PkResult, PkResultGroupByOptions> settingsFiltered, Tuple[] expectedResult) {
        final List<TrellisedBoxPlot<PkResult, PkResultGroupByOptions>> result = pkResultService.getBoxPlot(DATASETS, settingsFiltered,
                PkResultFilters.empty(), PopulationFilters.empty());

        softly.assertThat(result)
                .flatExtracting(TrellisedBoxPlot::getStats).extracting(
                OutputBoxplotEntry::getX,
                OutputBoxplotEntry::getSubjectCount,
                OutputBoxplotEntry::getEventCount,
                OutputBoxplotEntry::getMedian,
                OutputBoxplotEntry::getLowerQuartile, OutputBoxplotEntry::getUpperQuartile,
                OutputBoxplotEntry::getLowerWhisker, OutputBoxplotEntry::getUpperWhisker)
                .containsExactly(expectedResult);
    }


    @Test
    @Category(BoxPlotTests.class)
    public void testGetBoxPlotActualAndNominalDoses() {

        final ChartGroupByOptions<PkResult, PkResultGroupByOptions> settings = ChartGroupByOptions.<PkResult, PkResultGroupByOptions>builder()
                .withOption(X_AXIS, PkResultGroupByOptions.DOSE.getGroupByOptionAndParams())
                .withOption(Y_AXIS, PARAMETER_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(DOSE.getGroupByOptionAndParams())
                .withTrellisOption(ACTUAL_DOSE.getGroupByOptionAndParams())
                .build();

        final ChartGroupByOptionsFiltered<PkResult, PkResultGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered
                .builder(settings)
                .build();
        final List<TrellisedBoxPlot<PkResult, PkResultGroupByOptions>> result = pkResultService.getBoxPlot(DATASETS, settingsFiltered,
                PkResultFilters.empty(), PopulationFilters.empty());

        softly.assertThat(result)
                .flatExtracting(TrellisedBoxPlot::getTrellisedBy).extracting(
                TrellisOption::getTrellisedBy,
                TrellisOption::getTrellisOption)
                .containsExactly(
                        tuple(DOSE, 20.),
                        tuple(ACTUAL_DOSE, DEFAULT_EMPTY_VALUE),
                        tuple(DOSE, 20.),
                        tuple(ACTUAL_DOSE, 15.0),
                        tuple(DOSE, 40.),
                        tuple(ACTUAL_DOSE, 45.0),
                        tuple(DOSE, 45.),
                        tuple(ACTUAL_DOSE, 35.0),
                        tuple(DOSE, 45.),
                        tuple(ACTUAL_DOSE, 45.0),
                        tuple(DOSE, 50.),
                        tuple(ACTUAL_DOSE, 45.0),
                        tuple(DOSE, 50.),
                        tuple(ACTUAL_DOSE, 55.0),
                        tuple(DOSE, 55.),
                        tuple(ACTUAL_DOSE, 55.0));
    }

    @Test
    @Category(BoxPlotTests.class)
    public void testGetTrellisOptions() {
        final List<TrellisOptions<PkResultGroupByOptions>> result
                = pkResultService.getTrellisOptions(DATASETS, PkResultFilters.empty(), PopulationFilters.empty());
        softly.assertThat(result).extracting("trellisedBy").containsOnly(ANALYTE);
        softly.assertThat(result)
              .filteredOn(t -> t.getTrellisedBy().equals(ANALYTE))
              .flatExtracting("trellisOptions")
              .containsOnly("a1", "a2", "a3");
    }

    @Test
    @Category(BoxPlotTests.class)
    public void testGetBoxPlotSelection() {

        final ChartGroupByOptions<PkResult, PkResultGroupByOptions> settings = getPkResultSettings(VISIT_NUMBER);

        final HashMap<PkResultGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(ANALYTE, "a2");
        selectedTrellises.put(MEASUREMENT, "CMin (ng/l)");
        selectedTrellises.put(MEASUREMENT_TIMEPOINT, 5);

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, 55.00);

        final List<ChartSelectionItemRange<PkResult,
                PkResultGroupByOptions, Double>> selectionItems = Collections.singletonList(
                ChartSelectionItemRange.of(selectedTrellises, selectedItems1, 20., 60.)
        );

        SelectionDetail result = pkResultService.getRangedSelectionDetails(
                DATASETS, PkResultFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems));

        softly.assertThat(result.getEventIds()).containsExactly("id26");
        softly.assertThat(result.getSubjectIds()).containsExactly("sid2");
        softly.assertThat(result).extracting(SelectionDetail::getTotalEvents, SelectionDetail::getTotalSubjects)
                .containsExactly(11, 2);
    }

    @Test
    public void shouldGetDetailsOnDemand() {
        final List<PkResult> pkResultList = Arrays.asList(pkResult11, pkResult27, pkResult21);
        final Set<String> ids = pkResultList.stream().map(PkResult::getId).collect(toSet());
        List<SortAttrs> pkResultDefaultSortAttr = Arrays.asList(
                new SortAttrs("subjectId", false),
                new SortAttrs("analyte", false),
                new SortAttrs("cycle", false),
                new SortAttrs("protocolScheduleStartDay", false),
                new SortAttrs("parameter", false));
        final List<Map<String, String>> dodData = pkResultService.getDetailsOnDemandData(DUMMY_ACUITY_DATASETS, ids,
                pkResultDefaultSortAttr, 0, Integer.MAX_VALUE);
        final PkResult pkResult = pkResultList.get(0);
        final PkResultRaw event = pkResult.getEvent();
        final Map<String, String> dod = dodData.get(1);
        softly.assertThat(dodData).hasSize(pkResultList.size());
        softly.assertThat(dod).hasSize(13);
        softly.assertThat(pkResult.getSubjectCode()).isEqualTo(dod.get("subjectId"));
        softly.assertThat(pkResult.getSubject().getClinicalStudyCode()).isEqualTo(dod.get("studyId"));
        softly.assertThat(event.getProtocolScheduleStartDay()).isEqualTo(dod.get("protocolScheduleStartDay"));
        softly.assertThat(event.getTreatmentCycle()).isEqualTo(dod.get("cycle"));
        softly.assertThat(decimalFormat.format(event.getTreatment())).isEqualTo(dod.get("treatment"));
        softly.assertThat(event.getAnalyte()).isEqualTo(dod.get("analyte"));
        softly.assertThat(event.getParameterUnit()).isEqualTo(dod.get("parameterUnit"));
        softly.assertThat(event.getParameter()).isEqualTo(dod.get("parameter"));
        softly.assertThat(event.getVisit()).isEqualTo(dod.get("visit"));
        softly.assertThat(decimalFormat.format(event.getActualDose())).isEqualTo(dod.get("actualDose"));
        softly.assertThat(dod.get("bestOverallResponse")).isNull();
    }


    private ChartGroupByOptions<PkResult, PkResultGroupByOptions> getPkResultSettings(PkResultGroupByOptions timepointOption) {
        return ChartGroupByOptions.<PkResult, PkResultGroupByOptions>builder()
                .withOption(X_AXIS, DOSE.getGroupByOptionAndParams())
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
