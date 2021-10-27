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
import com.acuity.visualisations.rawdatamodel.dataproviders.RenalDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.RenalFilterService;
import com.acuity.visualisations.rawdatamodel.filters.RenalFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.RenalRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class RenalFilterServiceTest {

    @Autowired
    private RenalFilterService renalFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private RenalDatasetsDataProvider renalDatasetsDataProvider;

    private static final String STUDY_PERIOD = "period1";
    private static final String LAB_CODE = "creatinine clearance";
    private static final String LAB_UNIT = "umol/l";
    private static final Double MIN_LAB_VALUE = 20.0;
    private static final Double MAX_LAB_VALUE = 30.0;
    private static final Date MIN_DATE = DateUtils.toDate("03.08.2015");
    private static final Date MAX_DATE = DateUtils.toDate("08.08.2015");
    private static final Integer MIN_DATE_ON_STUDY = 2;
    private static final Integer MAX_DATE_ON_STUDY = 6;
    private static final Double MIN_ANALYSIS_VISIT = 1.0;
    private static final Double MAX_ANALYSIS_VISIT = 3.0;
    private static final Double MIN_UPPER_REF_VALUE = 20.;
    private static final Double MAX_UPPER_REF_VALUE = 40.;
    private static final Double MIN_LAB_VALUE_OVER_UPPER_REF_VALUE = 1.0;
    private static final Double MAX_LAB_VALUE_OVER_UPPER_REF_VALUE = 2.0;
    private static final String CKD_STAGE = "CKD Stage 4";

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private Subject SUBJECT1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
            .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("02.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2016")).build();
    private Subject SUBJECT2 = Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test")
            .firstTreatmentDate(DateUtils.toDate("02.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("21.07.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2015")).build();

    private Renal RENAL1 = new Renal(RenalRaw.builder().id("rid1").labCode("creatinine").value(MIN_LAB_VALUE).unit("mg/dl")
            .refHigh(7.0).baselineValue(MAX_LAB_VALUE).analysisVisit(MIN_ANALYSIS_VISIT)
            .measurementTimePoint(MIN_DATE).visitNumber(2.0).studyPeriods("period1")
            .refHigh(MIN_UPPER_REF_VALUE).build().runPrecalculations(), SUBJECT1);

    private Renal RENAL2 = new Renal(RenalRaw.builder().id("rid2").labCode("creatinine").value(MAX_LAB_VALUE).unit(LAB_UNIT)
            .refHigh(20.0).baselineValue(MAX_LAB_VALUE).analysisVisit(MAX_ANALYSIS_VISIT)
            .measurementTimePoint(MAX_DATE).visitNumber(6.0).studyPeriods("period2")
            .refHigh(MIN_UPPER_REF_VALUE).build().runPrecalculations(), SUBJECT1);

    private Renal RENAL3 = new Renal(RenalRaw.builder().id("rid3").labCode(LAB_CODE).value(50.0).unit(LAB_UNIT)
            .refHigh(200.0).baselineValue(MIN_LAB_VALUE).measurementTimePoint(MAX_DATE)
            .visitNumber(6.0).refHigh(MAX_UPPER_REF_VALUE).build().runPrecalculations(), SUBJECT2);

    private Renal RENAL4 = new Renal(RenalRaw.builder().id("rid4").labCode("creatinine").value(50.0).unit(LAB_UNIT)
            .refHigh(200.0).baselineValue(MIN_LAB_VALUE).measurementTimePoint(MIN_DATE)
            .visitNumber(6.0).refHigh(MAX_UPPER_REF_VALUE).build().runPrecalculations(), SUBJECT2);


    private List<Renal> RENALS = Arrays.asList(RENAL1, RENAL2, RENAL3, RENAL4);


    @Test
    public void shouldFilterByStudyPeriod() {
        RenalFilters result = givenFilterSetup(filters -> filters.setStudyPeriods(
                new SetFilter<>(Collections.singletonList(STUDY_PERIOD))));

        softly.assertThat(result.getStudyPeriods().getValues()).containsOnly(STUDY_PERIOD);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByLabCode() {
        RenalFilters result = givenFilterSetup(filters -> filters.setMeasurementName(
                new SetFilter<>(Collections.singletonList(LAB_CODE))));

        softly.assertThat(result.getMeasurementName().getValues()).containsOnly(LAB_CODE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByLabUnit() {
        RenalFilters result = givenFilterSetup(filters -> filters.setLabUnit(
                new SetFilter<>(Collections.singletonList(LAB_UNIT))));

        softly.assertThat(result.getLabUnit().getValues()).containsOnly(LAB_UNIT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldFilterByValues() {
        RenalFilters result = givenFilterSetup(filters -> filters.setLabValue(
                new RangeFilter<Double>(MIN_LAB_VALUE, MAX_LAB_VALUE)));

        softly.assertThat(result.getLabValue().getFrom()).isEqualTo(MIN_LAB_VALUE);
        softly.assertThat(result.getLabValue().getTo()).isEqualTo(MAX_LAB_VALUE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByTimePoint() {
        RenalFilters result = givenFilterSetup(filters -> filters.setMeasurementTimePoint(
                new DateRangeFilter(MIN_DATE, MAX_DATE)));

        softly.assertThat(result.getMeasurementTimePoint().getFrom()).isInSameDayAs(MIN_DATE);
        softly.assertThat(result.getMeasurementTimePoint().getTo()).isInSameDayAs(
                org.apache.commons.lang3.time.DateUtils.addMilliseconds(DaysUtil.addDays(MAX_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldFilterByDaysOnStudy() {
        RenalFilters result = givenFilterSetup(filters -> filters.setDaysOnStudy(
                new RangeFilter<Integer>(MIN_DATE_ON_STUDY, MAX_DATE_ON_STUDY)));

        softly.assertThat(result.getDaysOnStudy().getFrom()).isEqualTo(MIN_DATE_ON_STUDY);
        softly.assertThat(result.getDaysOnStudy().getTo()).isEqualTo(MAX_DATE_ON_STUDY);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByAnalysisVisit() {
        RenalFilters result = givenFilterSetup(filters -> filters.setAnalysisVisit(
                new RangeFilter<Double>(MIN_ANALYSIS_VISIT, MAX_ANALYSIS_VISIT)));

        softly.assertThat(result.getAnalysisVisit().getFrom()).isEqualTo(MIN_ANALYSIS_VISIT);
        softly.assertThat(result.getAnalysisVisit().getTo()).isEqualTo(MAX_ANALYSIS_VISIT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByUpperRefValue() {
        RenalFilters result = givenFilterSetup(filters -> filters.setUpperRefValue(
                new RangeFilter<Double>(MIN_UPPER_REF_VALUE, MAX_UPPER_REF_VALUE)));

        softly.assertThat(result.getUpperRefValue().getFrom()).isEqualTo(MIN_UPPER_REF_VALUE);
        softly.assertThat(result.getUpperRefValue().getTo()).isEqualTo(MAX_UPPER_REF_VALUE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void shouldFilterByCKDStage() {
        RenalFilters result = givenFilterSetup(filters -> filters.setCkdStage(
                new SetFilter<>(Collections.singletonList(CKD_STAGE))));

        softly.assertThat(result.getCkdStage().getValues()).containsOnly(CKD_STAGE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByVisitNumber() {
        RenalFilters result = givenFilterSetup(filters -> filters.setVisitNumber(new RangeFilter<>(1.0, 4.0)));
        RenalFilters result2 = givenFilterSetup(filters -> filters.setVisitNumber(new RangeFilter<>(5.0, 7.0)));
        RenalFilters result3 = givenFilterSetup(filters -> filters.setVisitNumber(new RangeFilter<>(7.0, 8.0)));

        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
        softly.assertThat(result2.getMatchedItemsCount()).isEqualTo(3);
        softly.assertThat(result3.getMatchedItemsCount()).isEqualTo(0);
    }

    @Test
    public void shouldFilterOverByUpperRefValue() {
        RenalFilters result = givenFilterSetup(filters -> filters.setUpperRefValue(
                new RangeFilter<Double>(MIN_LAB_VALUE_OVER_UPPER_REF_VALUE, MAX_LAB_VALUE_OVER_UPPER_REF_VALUE)));

        softly.assertThat(result.getUpperRefValue().getFrom()).isNull();
        softly.assertThat(result.getUpperRefValue().getTo()).isNull();
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(0);
    }

    private RenalFilters givenFilterSetup(final Consumer<RenalFilters> filterSetter) {
        final List<Subject> subjects = RENALS.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList());

        RenalFilters renalFilters = new RenalFilters();
        filterSetter.accept(renalFilters);
        return (RenalFilters) renalFilterService.getAvailableFilters(RENALS, renalFilters, subjects, PopulationFilters.empty());
    }
}

