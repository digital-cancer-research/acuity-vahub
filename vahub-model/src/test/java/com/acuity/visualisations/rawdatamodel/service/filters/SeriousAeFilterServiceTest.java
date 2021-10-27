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
import com.acuity.visualisations.rawdatamodel.dataproviders.SeriousAeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SeriousAeFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.annotation.SpringITTest;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.SeriousAeRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SeriousAe;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringITTest
public class SeriousAeFilterServiceTest {

    private static final Integer AE_NUMBER = 2;
    private static final String AE = "ae1";
    private static final String PT = "pt1";
    private static final Date START_DATE = DateUtils.toDateTime("03.08.2015 00:00");
    private static final Date END_DATE = DateUtils.toDateTime("08.08.2015 00:00");
    private static final Integer DAYS_FROM_FIRST_DOSE_TO_CRITERIA = 2;
    private static final String PRIMARY_DEATH_CAUSE = "cause1";
    private static final String SECONDARY_DEATH_CAUSE = "cause2";
    private static final String OTHER_MEDICATION = "Yes";
    private static final String CAUSED_BY_OTHER_MEDICATION = "No";
    private static final String STUDY_PROCEDURE = "Yes";
    private static final String CAUSED_BY_STUDY = "No";
    private static final String DESCRIPTION = "description1";
    private static final String RESULT_IN_DEATH = "No";
    private static final String HOSPITALIZATION_REQUIRED = "No";
    private static final String CONGENITAL_ANOMALY = "No";
    private static final String LIFE_THREATENING = "Yes";
    private static final String DISABILITY = "Yes";
    private static final String OTHER_SERIOUS_EVENT = "No";
    private static final String AD = "ad";
    private static final String CAUSED_BY_AD = "Yes";
    private static final String AD1 = "ad2";
    private static final String CAUSED_BY_AD1 = "No";
    private static final String AD2 = "ad5";
    private static final String CAUSED_BY_AD2 = "No";

    @Autowired
    private SeriousAeFilterService seriousAeFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private SeriousAeDatasetsDataProvider seriousAeDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private Subject SUBJECT1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
            .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("02.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2016")).build();

    private SeriousAe SERIOUS_AE1 = new SeriousAe(SeriousAeRaw.builder().id("id1").num(1).ae(AE).pt(PT).startDate(START_DATE)
            .endDate(END_DATE).primaryDeathCause("cause2").secondaryDeathCause("cause3").otherMedication(OTHER_MEDICATION)
            .causedByOtherMedication("Yes").studyProcedure(STUDY_PROCEDURE).causedByStudy("Yes").description(DESCRIPTION)
            .resultInDeath(RESULT_IN_DEATH).hospitalizationRequired("Yes").congenitalAnomaly(CONGENITAL_ANOMALY).lifeThreatening(LIFE_THREATENING)
            .disability("No").otherSeriousEvent("Yes").ad(AD).causedByAD(CAUSED_BY_AD).ad1(AD1).causedByAD1(CAUSED_BY_AD1).ad2("ad2")
            .causedByAD2("Yes").becomeSeriousDate(START_DATE).findOutDate(DateUtils.toDate("01.08.2015"))
            .hospitalizationDate(END_DATE).dischargeDate(DateUtils.toDate("07.08.2015"))
            .build(), SUBJECT1);
    private SeriousAe SERIOUS_AE2 = new SeriousAe(SeriousAeRaw.builder().id("id1").num(AE_NUMBER).ae("ae2").pt("pt2").startDate(DateUtils.toDate("04.08.2015"))
            .endDate(DateUtils.toDate("09.08.2015")).primaryDeathCause(PRIMARY_DEATH_CAUSE).secondaryDeathCause(SECONDARY_DEATH_CAUSE).otherMedication("No")
            .causedByOtherMedication(CAUSED_BY_OTHER_MEDICATION).studyProcedure("No").causedByStudy(CAUSED_BY_STUDY).description("description2")
            .resultInDeath("Yes").hospitalizationRequired(HOSPITALIZATION_REQUIRED).congenitalAnomaly("Yes").lifeThreatening("No")
            .disability(DISABILITY).otherSeriousEvent(OTHER_SERIOUS_EVENT).ad("ad3").causedByAD("No").ad1("ad4").causedByAD1("Yes").ad2("ad2")
            .causedByAD2("Yes").becomeSeriousDate(DateUtils.toDate("12.08.2015")).findOutDate(END_DATE)
            .hospitalizationDate(DateUtils.toDate("01.08.2015")).dischargeDate(START_DATE)
            .build(), SUBJECT1);

    private List<SeriousAe> SERIOUS_AES = Arrays.asList(SERIOUS_AE1, SERIOUS_AE2);

    private SeriousAeFilters givenFilterSetup(final Consumer<SeriousAeFilters> filterSetter) {
        final List<Subject> subjects = SERIOUS_AES.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList());

        SeriousAeFilters seriousAeFilters = new SeriousAeFilters();
        filterSetter.accept(seriousAeFilters);
        return (SeriousAeFilters) seriousAeFilterService.getAvailableFilters(SERIOUS_AES, seriousAeFilters, subjects, PopulationFilters.empty());
    }

    @Test
    public void shouldFilterByAeNumber() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setAeNumber(
                new RangeFilter<Integer>(AE_NUMBER, AE_NUMBER)));

        softly.assertThat(result.getAeNumber().getFrom()).isEqualTo(AE_NUMBER);
        softly.assertThat(result.getAeNumber().getTo()).isEqualTo(AE_NUMBER);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByPt() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setPt(
                new SetFilter<String>(Collections.singleton(PT))));

        softly.assertThat(result.getPt().getValues()).containsOnly(PT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStartDate() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setStartDate(
                new DateRangeFilter(START_DATE, START_DATE)));

        softly.assertThat(result.getStartDate().getFrom()).isInSameDayAs(START_DATE);
        softly.assertThat(result.getStartDate().getTo()).isInSameDayAs(
                org.apache.commons.lang3.time.DateUtils.addMilliseconds(DaysUtil.addDays(START_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByEndDate() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setEndDate(
                new DateRangeFilter(END_DATE, END_DATE)));

        softly.assertThat(result.getEndDate().getFrom()).isInSameDayAs(END_DATE);
        softly.assertThat(result.getEndDate().getTo()).isInSameDayAs(
                org.apache.commons.lang3.time.DateUtils.addMilliseconds(DaysUtil.addDays(END_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDaysFromFirstDoseToCriteria() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setDaysFromFirstDoseToCriteria(
                new RangeFilter<>(DAYS_FROM_FIRST_DOSE_TO_CRITERIA, DAYS_FROM_FIRST_DOSE_TO_CRITERIA)));

        softly.assertThat(result.getDaysFromFirstDoseToCriteria().getFrom()).isEqualTo(DAYS_FROM_FIRST_DOSE_TO_CRITERIA);
        softly.assertThat(result.getDaysFromFirstDoseToCriteria().getTo()).isEqualTo(DAYS_FROM_FIRST_DOSE_TO_CRITERIA);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByPrimaryDeathCause() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setPrimaryDeathCause(
                new SetFilter<String>(Collections.singleton(PRIMARY_DEATH_CAUSE))));

        softly.assertThat(result.getPrimaryDeathCause().getValues()).containsOnly(PRIMARY_DEATH_CAUSE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterBySecondaryDeathCause() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setSecondaryDeathCause(
                new SetFilter<String>(Collections.singleton(SECONDARY_DEATH_CAUSE))));

        softly.assertThat(result.getSecondaryDeathCause().getValues()).containsOnly(SECONDARY_DEATH_CAUSE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByOtherMedication() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setOtherMedication(
                new SetFilter<String>(Collections.singleton(OTHER_MEDICATION))));

        softly.assertThat(result.getOtherMedication().getValues()).containsOnly(OTHER_MEDICATION);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByCausedByOtherMedication() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setCausedByOtherMedication(
                new SetFilter<String>(Collections.singleton(CAUSED_BY_OTHER_MEDICATION))));

        softly.assertThat(result.getCausedByOtherMedication().getValues()).containsOnly(CAUSED_BY_OTHER_MEDICATION);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStudyProcedure() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setStudyProcedure(
                new SetFilter<String>(Collections.singleton(STUDY_PROCEDURE))));

        softly.assertThat(result.getStudyProcedure().getValues()).containsOnly(STUDY_PROCEDURE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByCausedByStudyProcedure() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setCausedByStudy(
                new SetFilter<String>(Collections.singleton(CAUSED_BY_STUDY))));

        softly.assertThat(result.getCausedByStudy().getValues()).containsOnly(CAUSED_BY_STUDY);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByCausedByDescription() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setDescription(
                new SetFilter<String>(Collections.singleton(DESCRIPTION))));

        softly.assertThat(result.getDescription().getValues()).containsOnly(DESCRIPTION);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByResultInDeath() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setResultInDeath(
                new SetFilter<String>(Collections.singleton(RESULT_IN_DEATH))));

        softly.assertThat(result.getResultInDeath().getValues()).containsOnly(RESULT_IN_DEATH);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByHospitalizationRequired() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setHospitalizationRequired(
                new SetFilter<String>(Collections.singleton(HOSPITALIZATION_REQUIRED))));

        softly.assertThat(result.getHospitalizationRequired().getValues()).containsOnly(HOSPITALIZATION_REQUIRED);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByCongenitalAnomaly() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setCongenitalAnomaly(
                new SetFilter<String>(Collections.singleton(CONGENITAL_ANOMALY))));

        softly.assertThat(result.getCongenitalAnomaly().getValues()).containsOnly(CONGENITAL_ANOMALY);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByLifeThreating() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setLifeThreatening(
                new SetFilter<String>(Collections.singleton(LIFE_THREATENING))));

        softly.assertThat(result.getLifeThreatening().getValues()).containsOnly(LIFE_THREATENING);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDisability() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setDisability(
                new SetFilter<String>(Collections.singleton(DISABILITY))));

        softly.assertThat(result.getDisability().getValues()).containsOnly(DISABILITY);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByOtherSeriousEvent() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setOtherSeriousEvent(
                new SetFilter<String>(Collections.singleton(OTHER_SERIOUS_EVENT))));

        softly.assertThat(result.getOtherSeriousEvent().getValues()).containsOnly(OTHER_SERIOUS_EVENT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByAd() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setAd(
                new SetFilter<String>(Collections.singleton(AD))));

        softly.assertThat(result.getAd().getValues()).containsOnly(AD);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByCausedByAd() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setCausedByAD(
                new SetFilter<String>(Collections.singleton(CAUSED_BY_AD))));

        softly.assertThat(result.getCausedByAD().getValues()).containsOnly(CAUSED_BY_AD);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByAd1() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setAd1(
                new SetFilter<String>(Collections.singleton(AD1))));

        softly.assertThat(result.getAd1().getValues()).containsOnly(AD1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByCausedByAd1() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setCausedByAD1(
                new SetFilter<String>(Collections.singleton(CAUSED_BY_AD1))));

        softly.assertThat(result.getCausedByAD1().getValues()).containsOnly(CAUSED_BY_AD1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByAd2() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setAd2(
                new SetFilter<String>(Collections.singleton(AD2))));

        softly.assertThat(result.getAd1().getValues()).isEmpty();
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(0);
    }

    @Test
    public void shouldFilterByCausedByAd2() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setCausedByAD2(
                new SetFilter<String>(Collections.singleton(CAUSED_BY_AD2))));

        softly.assertThat(result.getCausedByAD2().getValues()).isEmpty();
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(0);
    }

    @Test
    public void shouldFilterByBecomeSeriousDate() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setBecomeSeriousDate(
                new DateRangeFilter(START_DATE, START_DATE)));

        softly.assertThat(result.getBecomeSeriousDate().getFrom()).isInSameDayAs(START_DATE);
        softly.assertThat(result.getBecomeSeriousDate().getTo()).isInSameDayAs(
                org.apache.commons.lang3.time.DateUtils.addMilliseconds(DaysUtil.addDays(START_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByFindOutDate() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setFindOutDate(
                new DateRangeFilter(END_DATE, END_DATE)));

        softly.assertThat(result.getFindOutDate().getFrom()).isInSameDayAs(END_DATE);
        softly.assertThat(result.getFindOutDate().getTo()).isInSameDayAs(
                org.apache.commons.lang3.time.DateUtils.addMilliseconds(DaysUtil.addDays(END_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByHospitalizationDate() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setHospitalizationDate(
                new DateRangeFilter(END_DATE, END_DATE)));

        softly.assertThat(result.getHospitalizationDate().getFrom()).isInSameDayAs(END_DATE);
        softly.assertThat(result.getHospitalizationDate().getTo()).isInSameDayAs(
                org.apache.commons.lang3.time.DateUtils.addMilliseconds(DaysUtil.addDays(END_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDischargeDate() {
        SeriousAeFilters result = givenFilterSetup(filters -> filters.setDischargeDate(
                new DateRangeFilter(START_DATE, START_DATE)));

        softly.assertThat(result.getDischargeDate().getFrom()).isInSameDayAs(START_DATE);
        softly.assertThat(result.getDischargeDate().getTo()).isInSameDayAs(
                org.apache.commons.lang3.time.DateUtils.addMilliseconds(DaysUtil.addDays(START_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }
}
