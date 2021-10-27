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

package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.VitalDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.VitalRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.google.common.collect.ImmutableMap;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class VitalFilterServiceTest {

    @Autowired
    private VitalFilterService vitalFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private VitalDatasetsDataProvider vitalDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private final Map<String, Date> drugFirstDoseDate1 = ImmutableMap.<String, Date>builder().
            put("drug1", toDate("01.08.2015")).
            put("drug2", toDate("01.10.2015")).build();
    private Subject SUBJECT1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
            .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("02.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2016"))
            .drugFirstDoseDate(drugFirstDoseDate1).build();
    private Subject SUBJECT2 = Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test")
            .firstTreatmentDate(DateUtils.toDate("02.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("21.07.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2015"))
            .drugFirstDoseDate(drugFirstDoseDate1).build();

    private Vital vital1 = new Vital(VitalRaw.builder().id("1").vitalsMeasurement("vm1").plannedTimePoint("1").measurementDate(toDate("03.08.2015")).
            visitNumber(2.).scheduleTimepoint(null).unit("mg1").baselineFlag("N").analysisVisit(3.).studyPeriod("sp3").lastDoseAmount("20 mg").
            anatomicalLocation(null).sidesOfInterest("si1").physicalPosition("pp1").clinicallySignificant("cs1").lastDoseDate(DateUtils.toDate("08.08.2015")).
            baseline(null).resultValue(null).calcDaysSinceFirstDoseIfNull(true).calcChangeFromBaselineIfNull(true).
            subjectId("sid1").build().runPrecalculations(), SUBJECT1);
    private Vital vital2 = new Vital(VitalRaw.builder().id("1").vitalsMeasurement("vm2").plannedTimePoint(null).measurementDate(toDate("05.08.2015")).
            visitNumber(4.).scheduleTimepoint("2").unit(null).baselineFlag("Y").analysisVisit(4.).studyPeriod("sp1").lastDoseAmount("25 mg").
            anatomicalLocation("al2").sidesOfInterest("si2").physicalPosition("pp2").clinicallySignificant("cs2").lastDoseDate(DateUtils.toDate("09.08.2015")).
            baseline(20.).resultValue(6.).calcDaysSinceFirstDoseIfNull(true).calcChangeFromBaselineIfNull(true).
            subjectId("sid2").build().runPrecalculations(), SUBJECT2);
    private Vital vital3 = new Vital(VitalRaw.builder().id("1").vitalsMeasurement("vm1").plannedTimePoint("2").measurementDate(toDate("13.08.2015")).
            visitNumber(6.).scheduleTimepoint("4").unit("mg").baselineFlag("Y").analysisVisit(5.).studyPeriod(null).lastDoseAmount("30 mg").
            anatomicalLocation("al1").sidesOfInterest(null).physicalPosition("pp3").clinicallySignificant("cs3").lastDoseDate(DateUtils.toDate("10.08.2015")).
            baseline(8.).resultValue(9.).calcDaysSinceFirstDoseIfNull(true).calcChangeFromBaselineIfNull(true).
            subjectId("sid1").build().runPrecalculations(), SUBJECT1);
    private Vital vital4 = new Vital(VitalRaw.builder().id("1").vitalsMeasurement(null).plannedTimePoint("4").measurementDate(null).
            visitNumber(null).scheduleTimepoint("3").unit("mg").baselineFlag(null).analysisVisit(null).studyPeriod("sp2").lastDoseAmount(null).
            anatomicalLocation("al3").sidesOfInterest("si3").physicalPosition(null).clinicallySignificant(null).lastDoseDate(null).
            baseline(10.).resultValue(5.).calcDaysSinceFirstDoseIfNull(true).calcChangeFromBaselineIfNull(true).
            subjectId("sid2").build().runPrecalculations(), SUBJECT2);

    private List<Vital> events = newArrayList(vital1, vital2, vital3, vital4);

    @Test
    public void shouldGetVitalsMeasurements() {

        VitalFilters result = givenFilterSetup(filters -> filters.setVitalsMeasurements(new SetFilter(newArrayList("vm1"))));

        softly.assertThat(result.getVitalsMeasurements().getValues()).containsOnly("vm1");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetVitalsMeasurementsIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setVitalsMeasurements(new SetFilter(newArrayList("vm1"), true)));

        softly.assertThat(result.getVitalsMeasurements().getValues()).containsOnly("vm1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetplannedTimePoints() {
        VitalFilters result = givenFilterSetup(filters -> filters.setPlannedTimePoints(new SetFilter(newArrayList("2"))));

        softly.assertThat(result.getPlannedTimePoints().getValues()).containsOnly("2");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetplannedTimePointsIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setPlannedTimePoints(new SetFilter(newArrayList("2"), true)));

        softly.assertThat(result.getPlannedTimePoints().getValues()).containsOnly("2", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetmeasurementDate() {
        VitalFilters result = givenFilterSetup(filters -> filters.setMeasurementDate(new DateRangeFilter(toDate("04.08.2015"), toDate("12.08.2015"))));

        assertThat(result.getMeasurementDate().getFrom()).isInSameDayAs(DaysUtil.toDate("2015-08-05"));
        assertThat(result.getMeasurementDate().getTo()).isInSameDayAs(DaysUtil.toDate("2015-08-05 23:59"));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetmeasurementDateIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setMeasurementDate(new DateRangeFilter(toDate("04.08.2015"), toDate("12.08.2015"), true)));

        assertThat(result.getMeasurementDate().getFrom()).isInSameDayAs(DaysUtil.toDate("2015-08-05"));
        assertThat(result.getMeasurementDate().getTo()).isInSameDayAs(DaysUtil.toDate("2015-08-05 23:59"));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetlastDoseDate() {
        VitalFilters result = givenFilterSetup(filters -> filters.setLastDoseDate(new DateRangeFilter(toDate("08.08.2015"), toDate("09.08.2015"))));

        assertThat(result.getLastDoseDate().getFrom()).isInSameDayAs(DaysUtil.toDate("2015-08-08"));
        assertThat(result.getLastDoseDate().getTo()).isInSameDayAs(DaysUtil.toDate("2015-08-09 23:59"));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetlastDoseDateIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setLastDoseDate(new DateRangeFilter(toDate("08.08.2015"), toDate("09.08.2015"), true)));

        assertThat(result.getLastDoseDate().getFrom()).isInSameDayAs(DaysUtil.toDate("2015-08-08"));
        assertThat(result.getLastDoseDate().getTo()).isInSameDayAs(DaysUtil.toDate("2015-08-09 23:59"));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetDaysSinceFirstDose() {
        VitalFilters result = givenFilterSetup(filters -> filters.setDaysSinceFirstDose(new RangeFilter(2, 4)));

        softly.assertThat(result.getDaysSinceFirstDose().getFrom()).isEqualTo(2);
        softly.assertThat(result.getDaysSinceFirstDose().getTo()).isEqualTo(3);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetVisitNumber() {
        VitalFilters result = givenFilterSetup(filters -> filters.setVisitNumber(new RangeFilter(1., 3.)));

        softly.assertThat(result.getVisitNumber().getFrom()).isEqualTo(2.);
        softly.assertThat(result.getVisitNumber().getTo()).isEqualTo(2.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetVisitNumberIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setVisitNumber(new RangeFilter(1., 5., true)));

        softly.assertThat(result.getVisitNumber().getFrom()).isEqualTo(2.);
        softly.assertThat(result.getVisitNumber().getTo()).isEqualTo(4.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetscheduleTimepoints() {
        VitalFilters result = givenFilterSetup(filters -> filters.setScheduleTimepoints(new SetFilter(newArrayList("2"))));

        softly.assertThat(result.getScheduleTimepoints().getValues()).containsOnly("2");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetscheduleTimepointsIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setScheduleTimepoints(new SetFilter(newArrayList("2"), true)));

        softly.assertThat(result.getScheduleTimepoints().getValues()).containsOnly("2", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetunits() {
        VitalFilters result = givenFilterSetup(filters -> filters.setUnits(new SetFilter(newArrayList("mg"))));

        softly.assertThat(result.getUnits().getValues()).containsOnly("mg");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetunitsIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setUnits(new SetFilter(newArrayList("mg"), true)));

        softly.assertThat(result.getUnits().getValues()).containsOnly("mg", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetpercentageChangeFromBaseline() {
        VitalFilters result = givenFilterSetup(filters -> filters.setPercentageChangeFromBaseline(new RangeFilter(10., 33.)));

        softly.assertThat(result.getPercentageChangeFromBaseline().getFrom()).isEqualTo(12.5);
        softly.assertThat(result.getPercentageChangeFromBaseline().getTo()).isEqualTo(12.5);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetpercentageChangeFromBaselineIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setPercentageChangeFromBaseline(new RangeFilter(10., 33., true)));

        softly.assertThat(result.getPercentageChangeFromBaseline().getFrom()).isEqualTo(12.5);
        softly.assertThat(result.getPercentageChangeFromBaseline().getTo()).isEqualTo(12.5);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetChangeFromBaseline() {
        VitalFilters result = givenFilterSetup(filters -> filters.setChangeFromBaseline(new RangeFilter(-13.0, 0.)));

        softly.assertThat(result.getChangeFromBaseline().getFrom()).isEqualTo(-5.0);
        softly.assertThat(result.getChangeFromBaseline().getTo()).isEqualTo(-5.0);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetChangeFromBaselineIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setChangeFromBaseline(new RangeFilter(-13.0, 0., true)));

        softly.assertThat(result.getChangeFromBaseline().getFrom()).isEqualTo(-5.0);
        softly.assertThat(result.getChangeFromBaseline().getTo()).isEqualTo(-5.0);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetBaseline() {
        VitalFilters result = givenFilterSetup(filters -> filters.setBaseline(new RangeFilter(7.0, 11.)));

        softly.assertThat(result.getBaseline().getFrom()).isEqualTo(8.0);
        softly.assertThat(result.getBaseline().getTo()).isEqualTo(10.0);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetBaselineIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setBaseline(new RangeFilter(7.0, 11., true)));

        softly.assertThat(result.getBaseline().getFrom()).isEqualTo(8.0);
        softly.assertThat(result.getBaseline().getTo()).isEqualTo(10.0);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetBaselineFlags() {
        VitalFilters result = givenFilterSetup(filters -> filters.setBaselineFlags(new SetFilter(newArrayList("N"))));

        softly.assertThat(result.getBaselineFlags().getValues()).containsOnly("N");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetBaselineFlagsIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setBaselineFlags(new SetFilter(newArrayList("N"), true)));

        softly.assertThat(result.getBaselineFlags().getValues()).containsOnly("N", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetResultValue() {
        VitalFilters result = givenFilterSetup(filters -> filters.setResultValue(new RangeFilter(8.0, 10.)));

        softly.assertThat(result.getResultValue().getFrom()).isEqualTo(9.0);
        softly.assertThat(result.getResultValue().getTo()).isEqualTo(9.0);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetResultValueIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setResultValue(new RangeFilter(8.0, 10., true)));

        softly.assertThat(result.getResultValue().getFrom()).isEqualTo(9.0);
        softly.assertThat(result.getResultValue().getTo()).isEqualTo(9.0);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetanalysisVisit() {
        VitalFilters result = givenFilterSetup(filters -> filters.setAnalysisVisit(new RangeFilter(1.0, 5.)));

        softly.assertThat(result.getAnalysisVisit().getFrom()).isEqualTo(3.0);
        softly.assertThat(result.getAnalysisVisit().getTo()).isEqualTo(5.0);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetanalysisVisitIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setAnalysisVisit(new RangeFilter(1.0, 5., true)));

        softly.assertThat(result.getAnalysisVisit().getFrom()).isEqualTo(3.0);
        softly.assertThat(result.getAnalysisVisit().getTo()).isEqualTo(5.0);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldGetstudyPeriods() {
        VitalFilters result = givenFilterSetup(filters -> filters.setStudyPeriods(new SetFilter(newArrayList("sp1"))));

        softly.assertThat(result.getStudyPeriods().getValues()).containsOnly("sp1");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetstudyPeriodsIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setStudyPeriods(new SetFilter(newArrayList("sp1"), true)));

        softly.assertThat(result.getStudyPeriods().getValues()).containsOnly("sp1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetlastDoseAmounts() {
        VitalFilters result = givenFilterSetup(filters -> filters.setLastDoseAmounts(new SetFilter(newArrayList("20 mg"))));

        softly.assertThat(result.getLastDoseAmounts().getValues()).containsOnly("20 mg");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetlastDoseAmountsIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setLastDoseAmounts(new SetFilter(newArrayList("20 mg"), true)));

        softly.assertThat(result.getLastDoseAmounts().getValues()).containsOnly("20 mg", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetanatomicalLocations() {
        VitalFilters result = givenFilterSetup(filters -> filters.setAnatomicalLocations(new SetFilter(newArrayList("al1"))));

        softly.assertThat(result.getAnatomicalLocations().getValues()).containsOnly("al1");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetanatomicalLocationsIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setAnatomicalLocations(new SetFilter(newArrayList("al1"), true)));

        softly.assertThat(result.getAnatomicalLocations().getValues()).containsOnly("al1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetsidesOfInterest() {
        VitalFilters result = givenFilterSetup(filters -> filters.setSidesOfInterest(new SetFilter(newArrayList("si1"))));

        softly.assertThat(result.getSidesOfInterest().getValues()).containsOnly("si1");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetsidesOfInterestIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setSidesOfInterest(new SetFilter(newArrayList("si1"), true)));

        softly.assertThat(result.getSidesOfInterest().getValues()).containsOnly("si1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetPhysicalPositions() {
        VitalFilters result = givenFilterSetup(filters -> filters.setClinicallySignificant(new SetFilter(newArrayList("cs1"))));

        softly.assertThat(result.getClinicallySignificant().getValues()).containsOnly("cs1");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetPhysicalPositionsIncludingEmpty() {
        VitalFilters result = givenFilterSetup(filters -> filters.setClinicallySignificant(new SetFilter(newArrayList("cs1"), true)));

        softly.assertThat(result.getClinicallySignificant().getValues()).containsOnly("cs1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    private VitalFilters givenFilterSetup(final Consumer<VitalFilters> filterSetter) {
        final List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());

        VitalFilters vitalFilters = new VitalFilters();
        filterSetter.accept(vitalFilters);
        return (VitalFilters) vitalFilterService.getAvailableFilters(events, vitalFilters, subjects, PopulationFilters.empty());
    }
}
