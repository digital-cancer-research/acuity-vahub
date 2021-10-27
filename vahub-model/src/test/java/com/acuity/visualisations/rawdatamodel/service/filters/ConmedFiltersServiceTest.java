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

import com.acuity.visualisations.rawdatamodel.dataproviders.ConmedDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.ConmedRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import org.apache.commons.lang3.time.DateUtils;
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

import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class ConmedFiltersServiceTest {
    private static final String ATC_CODE = "atc_code";
    private static final String ATC_TEXT = "atc_text";
    private static final String REASON = "reason";
    private static final String ONGOING = "Yes";
    private static final Date START_DATE = toDate("02.01.2000");
    private static final Date END_DATE = toDate("04.01.2000");
    private static final Integer DURATION = 3;
    private static final Double DOSE = 10.0;
    private static final String DOSE_UNITS = "mg";
    private static final String DOSE_FREQUENCY = "weekly";
    private static final String MEDICATION_NAME = "name";
    private static final String STUDY_PERIOD = "start";

    @Autowired
    private ConmedFilterService conmedFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean(name = "eventDataProvider")
    private ConmedDatasetsDataProvider conmedDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldFilterByAtcCode() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setAtcCode(
                new SetFilter<>(Collections.singletonList(ATC_CODE))));

        softly.assertThat(result.getAtcCode().getValues()).containsOnly(ATC_CODE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByAtcText() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setAtcText(
                new SetFilter<>(Collections.singletonList(ATC_TEXT))));

        softly.assertThat(result.getAtcText().getValues()).containsOnly(ATC_TEXT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDose() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setDose(
                new RangeFilter<>(DOSE, DOSE)));

        softly.assertThat(result.getDose().getFrom()).isEqualTo(DOSE);
        softly.assertThat(result.getDose().getTo()).isEqualTo(DOSE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDoseUnits() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setDoseUnits(
                new SetFilter<>(Collections.singletonList(DOSE_UNITS))));

        softly.assertThat(result.getDoseUnits().getValues()).containsOnly(DOSE_UNITS);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDoseFrequency() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setDoseFrequency(
                new SetFilter<>(Collections.singletonList(DOSE_FREQUENCY))));

        softly.assertThat(result.getDoseFrequency().getValues()).containsOnly(DOSE_FREQUENCY);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByReason() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setTreatmentReason(
                new SetFilter<>(Collections.singletonList(REASON))));

        softly.assertThat(result.getTreatmentReason().getValues()).containsOnly(REASON);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByIsOngoing() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setOngoing(
                new SetFilter<>(Collections.singletonList(ONGOING))));

        softly.assertThat(result.getOngoing().getValues()).containsOnly(ONGOING);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStartDate() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setStartDate(
                new DateRangeFilter(START_DATE, START_DATE)));

        softly.assertThat(result.getStartDate().getFrom()).isInSameDayAs(START_DATE);
        softly.assertThat(result.getStartDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(START_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByEndDate() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setEndDate(
                new DateRangeFilter(END_DATE, END_DATE)));

        softly.assertThat(result.getEndDate().getFrom()).isInSameDayAs(END_DATE);
        softly.assertThat(result.getEndDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(END_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByMedicationName() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setMedicationName(
                new SetFilter<>(Collections.singletonList(MEDICATION_NAME))));

        softly.assertThat(result.getMedicationName().getValues()).containsOnly(MEDICATION_NAME);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDuration() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setDuration(
                new RangeFilter<>(DURATION, DURATION)));

        softly.assertThat(result.getDuration().getFrom()).isEqualTo(DURATION);
        softly.assertThat(result.getDuration().getTo()).isEqualTo(DURATION);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStartPriorToRandomisation() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setStartPriorToRandomisation(
                new SetFilter<>(Collections.singletonList("No"))));

        softly.assertThat(result.getStartPriorToRandomisation().getValues()).containsOnly("No");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByEndPriorToRandomisation() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setEndPriorToRandomisation(
                new SetFilter<>(Collections.singletonList("No"))));

        softly.assertThat(result.getEndPriorToRandomisation().getValues()).containsOnly("No");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByStudyDayAtStart() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setStudyDayAtConmedStart(
                new RangeFilter<>(1, 1)));

        softly.assertThat(result.getStudyDayAtConmedStart().getFrom()).isEqualTo(1);
        softly.assertThat(result.getStudyDayAtConmedStart().getTo()).isEqualTo(1);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStudyDayAtEnd() {
        ConmedFilters result = givenFilterSetup(filters -> filters.setStudyDayAtConmedEnd(
                new RangeFilter<>(3, 3)));

        softly.assertThat(result.getStudyDayAtConmedEnd().getFrom()).isEqualTo(3);
        softly.assertThat(result.getStudyDayAtConmedEnd().getTo()).isEqualTo(3);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    private ConmedFilters givenFilterSetup(final Consumer<ConmedFilters> filterSetter) {
        List<Conmed> events = createConmedEvents();
        final List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());

        ConmedFilters conmedFilters = new ConmedFilters();
        filterSetter.accept(conmedFilters);
        return (ConmedFilters) conmedFilterService.getAvailableFilters(events, conmedFilters, subjects, PopulationFilters.empty());
    }

    private List<Conmed> createConmedEvents() {
        Subject subj1 = Subject.builder()
                .subjectId("subj-1")
                .dateOfRandomisation(toDate("01.01.2000"))
                .firstTreatmentDate(toDate("01.01.2000"))
                .build();
        Conmed conmed1 = new Conmed(
                ConmedRaw.builder()
                        .id("id1")
                        .subjectId("subj-1")
                        .atcCode(ATC_CODE)
                        .atcText("atc_text_2")
                        .dose(DOSE)
                        .doseUnits(DOSE_UNITS)
                        .doseFrequency("daily")
                        .medicationName(MEDICATION_NAME)
                        .treatmentReason("reason_2")
                        .startDate(START_DATE)
                        .endDate(END_DATE)
                        .studyPeriods("end")
                        .doseUnitsOther("DUO")
                        .frequencyOther("DFO")
                        .doseTotal(123d)
                        .route("R")
                        .therapyReason("TR")
                        .therapyReasonOther("ORFT")
                        .otherProphylaxisSpec("OPS")
                        .infectionBodySystem("IBS")
                        .infectionBodySystemOther("IBSO")
                        .activeIngredient1("AI1")
                        .activeIngredient2("AI2")
                        .reasonForTreatmentStop("RFTS")
                        .reasonForTreatmentStopOther("RFTSO")
                        .aePt("AEPT")
                        .aeNum(11)
                        .build(),
                subj1);
        Conmed conmed2 = new Conmed(
                ConmedRaw.builder()
                        .id("id2")
                        .subjectId("subj-1")
                        .atcCode("atc_code_2")
                        .atcText(ATC_TEXT)
                        .dose(15.0)
                        .doseUnits("ml")
                        .doseFrequency(DOSE_FREQUENCY)
                        .medicationName("name_2")
                        .treatmentReason(REASON)
                        .startDate(toDate("05.06.2015"))
                        .endDate(toDate("15.06.2015"))
                        .studyPeriods(STUDY_PERIOD)
                        .build(),
                subj1);
        Conmed conmed3 = new Conmed(
                ConmedRaw.builder()
                        .id("id3")
                        .subjectId("subj-1")
                        .build(),
                subj1);
        return Arrays.asList(conmed1, conmed2, conmed3);
    }
}
