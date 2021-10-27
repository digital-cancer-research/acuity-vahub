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

import com.acuity.visualisations.rawdatamodel.dataproviders.LabDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.LabTests;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.google.common.collect.Lists.newArrayList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@Category(LabTests.class)
public class LabFilterServiceTest {

    @Autowired
    private LabFilterService labFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean(name = "eventDataProvider")
    private LabDatasetsDataProvider labDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetLabCodes() {
        LabFilters result = givenFilterSetup(filters -> filters.setLabcode(new SetFilter<>(newArrayList("ALT"))));

        softly.assertThat(result.getLabcode().getValues()).containsOnly("ALT");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetLabCodesIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setLabcode(new SetFilter<>(newArrayList("ALT"), true)));

        softly.assertThat(result.getLabcode().getValues()).containsOnly("ALT", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetLabCategories() {
        LabFilters result = givenFilterSetup(filters -> filters.setLabCategory(new SetFilter<>(newArrayList("Chemistry"))));

        softly.assertThat(result.getLabCategory().getValues()).containsOnly("Chemistry");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetLabCategoriesIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setLabCategory(new SetFilter<>(newArrayList("Chemistry"), true)));

        softly.assertThat(result.getLabCategory().getValues()).containsOnly("Chemistry", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetLabValues() {
        LabFilters result = givenFilterSetup(filters -> filters.setLabValue(new RangeFilter<>(9., 10.)));

        softly.assertThat(result.getLabValue().getFrom()).isEqualTo(10.);
        softly.assertThat(result.getLabValue().getTo()).isEqualTo(10.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetLabValuesIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setLabValue(new RangeFilter<>(9., 10., true)));

        softly.assertThat(result.getLabValue().getFrom()).isEqualTo(10.);
        softly.assertThat(result.getLabValue().getTo()).isEqualTo(10.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetLabUnits() {
        LabFilters result = givenFilterSetup(filters -> filters.setLabUnit(new SetFilter<>(newArrayList("U/L"))));

        softly.assertThat(result.getLabUnit().getValues()).containsOnly("U/L");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetLabUnitsIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setLabUnit(new SetFilter<>(newArrayList("U/L"), true)));

        softly.assertThat(result.getLabUnit().getValues()).containsOnly("U/L", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetBaselineValues() {
        LabFilters result = givenFilterSetup(filters -> filters.setBaselineValue(new RangeFilter<>(10., 20.)));

        softly.assertThat(result.getBaselineValue().getFrom()).isEqualTo(20.);
        softly.assertThat(result.getBaselineValue().getTo()).isEqualTo(20.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetBaselineValuesIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setBaselineValue(new RangeFilter<>(10., 20., true)));

        softly.assertThat(result.getBaselineValue().getFrom()).isEqualTo(20.);
        softly.assertThat(result.getBaselineValue().getTo()).isEqualTo(20.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetChangeFromBaselineValues() {
        LabFilters result = givenFilterSetup(filters -> filters.setChangeFromBaselineValue(new RangeFilter<>(-10., -9.)));

        softly.assertThat(result.getChangeFromBaselineValue().getFrom()).isEqualTo(-10.);
        softly.assertThat(result.getChangeFromBaselineValue().getTo()).isEqualTo(-10.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetChangeFromBaselineValuesIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setChangeFromBaselineValue(new RangeFilter<>(-10., -9., true)));

        softly.assertThat(result.getChangeFromBaselineValue().getFrom()).isEqualTo(-10.);
        softly.assertThat(result.getChangeFromBaselineValue().getTo()).isEqualTo(-10.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetPercentChangeFromBaselineValues() {
        LabFilters result = givenFilterSetup(filters -> filters.setPercentageChangeFromBaselineValue(new RangeFilter<>(-100., 100.)));

        softly.assertThat(result.getPercentageChangeFromBaselineValue().getFrom()).isEqualTo(-50.);
        softly.assertThat(result.getPercentageChangeFromBaselineValue().getTo()).isEqualTo(100.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetPercentChangeFromBaselineValuesIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setPercentageChangeFromBaselineValue(new RangeFilter<>(-100., 100., true)));

        softly.assertThat(result.getPercentageChangeFromBaselineValue().getFrom()).isEqualTo(-50.);
        softly.assertThat(result.getPercentageChangeFromBaselineValue().getTo()).isEqualTo(100.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetLowerRefValues() {
        LabFilters result = givenFilterSetup(filters -> filters.setLowerRefValue(new RangeFilter<>(0., 5.)));

        softly.assertThat(result.getLowerRefValue().getFrom()).isEqualTo(3.);
        softly.assertThat(result.getLowerRefValue().getTo()).isEqualTo(5.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetLowerRefValuesIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setLowerRefValue(new RangeFilter<>(0., 5., true)));

        softly.assertThat(result.getLowerRefValue().getFrom()).isEqualTo(3.);
        softly.assertThat(result.getLowerRefValue().getTo()).isEqualTo(5.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetUpperRefValues() {
        LabFilters result = givenFilterSetup(filters -> filters.setUpperRefValue(new RangeFilter<>(20., 30.)));

        softly.assertThat(result.getUpperRefValue().getFrom()).isEqualTo(25.);
        softly.assertThat(result.getUpperRefValue().getTo()).isEqualTo(25.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetUpperRefValuesIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setUpperRefValue(new RangeFilter<>(20., 30., true)));

        softly.assertThat(result.getUpperRefValue().getFrom()).isEqualTo(25.);
        softly.assertThat(result.getUpperRefValue().getTo()).isEqualTo(25.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetOutOfRefRange() {
        LabFilters result = givenFilterSetup(filters -> filters.setOutOfRefRange(new SetFilter<>(newArrayList("false"))));

        softly.assertThat(result.getOutOfRefRange().getValues()).containsOnly("false");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetOutOfRefRangeIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setOutOfRefRange(new SetFilter<>(newArrayList("false"), true)));

        softly.assertThat(result.getOutOfRefRange().getValues()).containsOnly("false");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetRefRangeNormValues() {
        LabFilters result = givenFilterSetup(filters -> filters.setRefRangeNormValue(new RangeFilter<>(0., 0.25)));

        softly.assertThat(result.getRefRangeNormValue().getFrom()).isEqualTo(0.25);
        softly.assertThat(result.getRefRangeNormValue().getTo()).isEqualTo(0.25);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetRefRangeNormIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setRefRangeNormValue(new RangeFilter<>(0., 0.25, true)));

        softly.assertThat(result.getRefRangeNormValue().getFrom()).isEqualTo(0.25);
        softly.assertThat(result.getRefRangeNormValue().getTo()).isEqualTo(0.25);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldGetValueOverUpperRef() {
        LabFilters result = givenFilterSetup(filters -> filters.setLabValueOverUpperRefValue(new RangeFilter<>(1., 2.)));

        softly.assertThat(result.getLabValueOverUpperRefValue().getFrom()).isEqualTo(1);
        softly.assertThat(result.getLabValueOverUpperRefValue().getTo()).isEqualTo(1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetValueOverUpperRefIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setLabValueOverUpperRefValue(new RangeFilter<>(1., 2., true)));

        softly.assertThat(result.getLabValueOverUpperRefValue().getFrom()).isEqualTo(1);
        softly.assertThat(result.getLabValueOverUpperRefValue().getTo()).isEqualTo(1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetValueOverLowerRef() {
        LabFilters result = givenFilterSetup(filters -> filters.setLabValueOverLowerRefValue(new RangeFilter<>(2., 3.)));

        softly.assertThat(result.getLabValueOverLowerRefValue().getFrom()).isEqualTo(2.);
        softly.assertThat(result.getLabValueOverLowerRefValue().getTo()).isEqualTo(2.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetValueOverLowerRefIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setLabValueOverLowerRefValue(new RangeFilter<>(2., 3., true)));

        softly.assertThat(result.getLabValueOverLowerRefValue().getFrom()).isEqualTo(2.);
        softly.assertThat(result.getLabValueOverLowerRefValue().getTo()).isEqualTo(2.);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetMeasurementTimePoint() {
        LabFilters result = givenFilterSetup(filters -> 
                filters.setMeasurementTimePoint(new DateRangeFilter(toDate("01.01.2000"), toDate("31.01.2000"))));

        softly.assertThat(result.getMeasurementTimePoint().getFrom()).isInSameDayAs(DaysUtil.toDate("2000-01-02"));
        softly.assertThat(result.getMeasurementTimePoint().getTo()).isInSameDayAs(DaysUtil.toDate("2000-01-02 23:59"));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetDaysOnStudy() {
        LabFilters result = givenFilterSetup(filters -> filters.setDaysOnStudy(new RangeFilter<>(0, 10)));

        softly.assertThat(result.getDaysOnStudy().getFrom()).isEqualTo(1);
        softly.assertThat(result.getDaysOnStudy().getTo()).isEqualTo(1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetDaysOnStudyIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setDaysOnStudy(new RangeFilter<>(0, 10, true)));

        softly.assertThat(result.getDaysOnStudy().getFrom()).isEqualTo(1);
        softly.assertThat(result.getDaysOnStudy().getTo()).isEqualTo(1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetVisitNumber() {
        LabFilters result = givenFilterSetup(filters -> filters.setVisitNumber(new RangeFilter<>(1., 2.)));

        softly.assertThat(result.getVisitNumber().getFrom()).isEqualTo(1);
        softly.assertThat(result.getVisitNumber().getTo()).isEqualTo(1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetVisitNumberIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setVisitNumber(new RangeFilter<>(1., 2., true)));

        softly.assertThat(result.getVisitNumber().getFrom()).isEqualTo(1);
        softly.assertThat(result.getVisitNumber().getTo()).isEqualTo(1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetAnalysisVisit() {
        LabFilters result = givenFilterSetup(filters -> filters.setAnalysisVisit(new RangeFilter<>(1., 2.)));

        softly.assertThat(result.getAnalysisVisit().getFrom()).isEqualTo(1);
        softly.assertThat(result.getAnalysisVisit().getTo()).isEqualTo(1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetAnalysisVisitIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setAnalysisVisit(new RangeFilter<>(1., 2., true)));

        softly.assertThat(result.getAnalysisVisit().getFrom()).isEqualTo(1);
        softly.assertThat(result.getAnalysisVisit().getTo()).isEqualTo(1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetProtocolScheduledTimepoint() {
        LabFilters result = givenFilterSetup(filters -> filters.setProtocolScheduleTimepoint(new SetFilter<>(newArrayList("ABC"))));

        softly.assertThat(result.getProtocolScheduleTimepoint().getValues()).containsOnly("ABC");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetProtocolScheduledTimepointIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setProtocolScheduleTimepoint(new SetFilter<>(newArrayList("ABC"), true)));

        softly.assertThat(result.getProtocolScheduleTimepoint().getValues()).containsOnly("ABC", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetValueDipstick() {
        LabFilters result = givenFilterSetup(filters -> filters.setValueDipstick(new SetFilter<>(newArrayList("val-1"))));

        softly.assertThat(result.getValueDipstick().getValues()).containsOnly("val-1");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetValueDipstickIncludingEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setValueDipstick(new SetFilter<>(newArrayList("val-1"), true)));

        softly.assertThat(result.getValueDipstick().getValues()).containsOnly("val-1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetStudyPeriods() {
        LabFilters result = givenFilterSetup(filters -> filters.setStudyPeriods(new SetFilter<>(newArrayList("period-1"))));

        softly.assertThat(result.getStudyPeriods().getValues()).containsOnly("period-1");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetStudyPeriodsIncludeEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setStudyPeriods(new SetFilter<>(newArrayList("period-1"), true)));

        softly.assertThat(result.getStudyPeriods().getValues()).containsOnly("period-1", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldGetSourceTypePatient() {
        LabFilters result = givenFilterSetup(filters -> filters.setSourceType(new SetFilter<>(newArrayList("Patient"))));

        softly.assertThat(result.getSourceType().getValues()).containsOnly("Patient");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldGetSourceTypeIncludeEmpty() {
        LabFilters result = givenFilterSetup(filters -> filters.setSourceType(new SetFilter<>(newArrayList("Patient"), true)));

        softly.assertThat(result.getSourceType().getValues()).containsOnly("Patient", null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    private List<Lab> createLabEvents() {
        Subject subj1 = Subject.builder()
                .subjectId("subj-1")
                .firstTreatmentDate(toDate("01.01.2000"))
                .build();
        Lab lab1 = new Lab(LabRaw.builder()
                .id("1")
                .labCode("ALT")
                .category("Chemistry")
                .value(10.)
                .unit("U/L")
                .baseline(20.)
                .changeFromBaselineRaw(-10.)
                .refLow(5.)
                .refHigh(25.)
                .calcDaysSinceFirstDoseIfNull(true)
                .measurementTimePoint(toDate("02.01.2000"))
                .visitNumber(1.)
                .analysisVisit(1.)
                .protocolScheduleTimepoint("ABC")
                .valueDipstick("val-1")
                .studyPeriods("period-1")
                .sourceType("Patient")
                .build(), subj1);
        Lab lab2 = new Lab(LabRaw.builder()
                .id("2")
                .labCode("B-Haemoglobin")
                .category("Hematology")
                .value(3.)
                .unit("g/dL")
                .baseline(3.)
                .changeFromBaselineRaw(3.)
                .refLow(3.)
                .refHigh(3.)
                .calcDaysSinceFirstDoseIfNull(true)
                .measurementTimePoint(toDate("01.02.2000"))
                .visitNumber(3.)
                .analysisVisit(3.)
                .protocolScheduleTimepoint("XYZ")
                .valueDipstick("val-2")
                .studyPeriods("period-2")
                .sourceType("Sponsor")
                .build(), subj1);
        Lab labWithEmptyValues = new Lab(LabRaw.builder()
                .id("3")
                .labCode(null)
                .category(null)
                .value(99.)
                .baseline(null)
                .changeFromBaselineRaw(null)
                .refLow(null)
                .refHigh(null)
                .calcDaysSinceFirstDoseIfNull(true)
                .measurementTimePoint(null)
                .visitNumber(null)
                .analysisVisit(null)
                .protocolScheduleTimepoint(null)
                .valueDipstick(null)
                .studyPeriods(null)
                .sourceType(null)
                .build(), subj1);
        return newArrayList(lab1, lab2, labWithEmptyValues);
    }

    private LabFilters givenFilterSetup(final Consumer<LabFilters> filterSetter) {
        List<Lab> events = createLabEvents();
        final List<Subject> subjects = events.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList());

        LabFilters labFilters = new LabFilters();
        filterSetter.accept(labFilters);
        return (LabFilters) labFilterService.getAvailableFilters(events, labFilters, subjects, PopulationFilters.empty());
    }
}
