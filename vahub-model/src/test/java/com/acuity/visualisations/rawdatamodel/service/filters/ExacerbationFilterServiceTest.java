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

import com.acuity.visualisations.rawdatamodel.dataproviders.ExacerbationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.ExacerbationRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
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
public class ExacerbationFilterServiceTest {
    private static final String ANTIBIOTICS_TRT = "antibiotics_trt_1";
    private static final String CLASSIFICATION = "classification_1";
    private static final Integer DAYS_ON_STUD_AT_START = 3;
    private static final Integer DAYS_ON_STUD_AT_START_2 = 4;
    private static final Integer DAYS_ON_STUD_AT_END = 13;
    private static final Integer DAYS_ON_STUD_AT_END_2 = 14;
    private static final String DEPOT_GCS_TRT = "depot_gcs_trt_1";
    private static final Integer DURATION = 10;
    private static final Integer DURATION_2 = 11;
    private static final String EMERGENCY_ROOM_VISIT = "emergency_room_visit_1";
    private static final String HOSPITALIZATION = "hospitalisation_2";
    private static final Date START_DATE = toDate("02.01.2000");
    private static final Date START_DATE_2 = toDate("03.01.2000");
    private static final Date END_DATE = toDate("12.01.2000");
    private static final Date END_DATE_2 = toDate("13.01.2000");
    private static final String START_PRIOR_TO_RANDOMISATION = "start_prior_to_randomisation_1";
    private static final String END_PRIOR_TO_RANDOMISATION = "end_prior_to_randomisation_1";
    private static final String INC_INHALED_CORT_TRT = "inc_inhaled_cort_trt_1";
    private static final String SYS_CORT_TRT = "sys_cort_trt_1";

    @Autowired
    private ExacerbationFilterService exacerbationFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean(name = "eventDataProvider")
    private ExacerbationDatasetsDataProvider exacerbationDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldFilterByAntibioticsTrt() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setAntibioticsTreatment(
                new SetFilter<>(Collections.singletonList(ANTIBIOTICS_TRT))));

        softly.assertThat(result.getAntibioticsTreatment().getValues()).containsOnly(ANTIBIOTICS_TRT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByAntibioticsTrtIncludeEmpty() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setAntibioticsTreatment(
                new SetFilter<>(Arrays.asList(ANTIBIOTICS_TRT), true)));

        softly.assertThat(result.getAntibioticsTreatment().getValues()).containsOnly(ANTIBIOTICS_TRT, null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByClassification() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setExacerbationClassification(
                new SetFilter<>(Collections.singletonList(CLASSIFICATION))));

        softly.assertThat(result.getExacerbationClassification().getValues()).containsOnly(CLASSIFICATION);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByClassificationIncludeEmpty() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setExacerbationClassification(
                new SetFilter<>(Arrays.asList(CLASSIFICATION), true)));

        softly.assertThat(result.getExacerbationClassification().getValues()).containsOnly(CLASSIFICATION, null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByHospitalization() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setHospitalisation(
                new SetFilter<>(Collections.singletonList(HOSPITALIZATION))));

        softly.assertThat(result.getHospitalisation().getValues()).containsOnly(HOSPITALIZATION);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByHospitalizationIncludeEmpty() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setHospitalisation(
                new SetFilter<>(Arrays.asList(HOSPITALIZATION), true)));

        softly.assertThat(result.getHospitalisation().getValues()).containsOnly(HOSPITALIZATION, null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByDuration() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setDuration(
                new RangeFilter<Integer>(DURATION, DURATION)));

        softly.assertThat(result.getDuration().getFrom()).isEqualTo(DURATION);
        softly.assertThat(result.getDuration().getTo()).isEqualTo(DURATION);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDurations() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setDuration(
                new RangeFilter<Integer>(DURATION, DURATION_2)));

        softly.assertThat(result.getDuration().getFrom()).isEqualTo(DURATION);
        softly.assertThat(result.getDuration().getTo()).isEqualTo(DURATION_2);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByEmergencyRoomVisit() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setEmergencyRoomVisit(
                new SetFilter<>(Collections.singletonList(EMERGENCY_ROOM_VISIT))));

        softly.assertThat(result.getEmergencyRoomVisit().getValues()).containsOnly(EMERGENCY_ROOM_VISIT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByEmergencyRoomVisitIncludeEmpty() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setEmergencyRoomVisit(
                new SetFilter<>(Arrays.asList(EMERGENCY_ROOM_VISIT), true)));

        softly.assertThat(result.getEmergencyRoomVisit().getValues()).containsOnly(EMERGENCY_ROOM_VISIT, null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByDepotGcsTrt() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setDepotCorticosteroidTreatment(
                new SetFilter<>(Collections.singletonList(DEPOT_GCS_TRT))));

        softly.assertThat(result.getDepotCorticosteroidTreatment().getValues()).containsOnly(DEPOT_GCS_TRT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDepotGcsTrtIncludeEmpty() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setDepotCorticosteroidTreatment(
                new SetFilter<>(Arrays.asList(DEPOT_GCS_TRT), true)));

        softly.assertThat(result.getDepotCorticosteroidTreatment().getValues()).containsOnly(DEPOT_GCS_TRT, null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByIncInhaledCortTrt() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setIncreasedInhaledCorticosteroidTreatment(
                new SetFilter<>(Collections.singletonList(INC_INHALED_CORT_TRT))));

        softly.assertThat(result.getIncreasedInhaledCorticosteroidTreatment().getValues()).containsOnly(INC_INHALED_CORT_TRT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByIncInhaledCortTrtIncludeEmpty() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setIncreasedInhaledCorticosteroidTreatment(
                new SetFilter<>(Arrays.asList(INC_INHALED_CORT_TRT), true)));

        softly.assertThat(result.getIncreasedInhaledCorticosteroidTreatment().getValues()).containsOnly(INC_INHALED_CORT_TRT, null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterBySysCortTrt() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setSystemicCorticosteroidTreatment(
                new SetFilter<>(Collections.singletonList(SYS_CORT_TRT))));

        softly.assertThat(result.getSystemicCorticosteroidTreatment().getValues()).containsOnly(SYS_CORT_TRT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterBySysCortTrtIncludeEmpty() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setSystemicCorticosteroidTreatment(
                new SetFilter<>(Arrays.asList(SYS_CORT_TRT), true)));

        softly.assertThat(result.getSystemicCorticosteroidTreatment().getValues()).containsOnly(SYS_CORT_TRT, null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByStartPriorToRandomisation() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setStartPriorToRandomisation(
                new SetFilter<>(Collections.singletonList(START_PRIOR_TO_RANDOMISATION))));

        softly.assertThat(result.getStartPriorToRandomisation().getValues()).containsOnly(START_PRIOR_TO_RANDOMISATION);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStartPriorToRandomisationIncludeEmpty() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setStartPriorToRandomisation(
                new SetFilter<>(Arrays.asList(START_PRIOR_TO_RANDOMISATION), true)));

        softly.assertThat(result.getStartPriorToRandomisation().getValues()).containsOnly(START_PRIOR_TO_RANDOMISATION, null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByEndPriorToRandomisation() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setEndPriorToRandomisation(
                new SetFilter<>(Collections.singletonList(END_PRIOR_TO_RANDOMISATION))));

        softly.assertThat(result.getEndPriorToRandomisation().getValues()).containsOnly(END_PRIOR_TO_RANDOMISATION);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByEndPriorToRandomisationIncludeEmpty() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setEndPriorToRandomisation(
                new SetFilter<>(Arrays.asList(END_PRIOR_TO_RANDOMISATION), true)));

        softly.assertThat(result.getEndPriorToRandomisation().getValues()).containsOnly(END_PRIOR_TO_RANDOMISATION, null);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByDaysOnStudyAtStart() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setDaysOnStudyAtStart(
                new RangeFilter<>(DAYS_ON_STUD_AT_START, DAYS_ON_STUD_AT_START)));

        softly.assertThat(result.getDaysOnStudyAtStart().getFrom()).isEqualTo(DAYS_ON_STUD_AT_START);
        softly.assertThat(result.getDaysOnStudyAtStart().getTo()).isEqualTo(DAYS_ON_STUD_AT_START);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDaysOnStudyAtStarts() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setDaysOnStudyAtStart(
                new RangeFilter<>(DAYS_ON_STUD_AT_START, DAYS_ON_STUD_AT_START_2)));

        softly.assertThat(result.getDaysOnStudyAtStart().getFrom()).isEqualTo(DAYS_ON_STUD_AT_START);
        softly.assertThat(result.getDaysOnStudyAtStart().getTo()).isEqualTo(DAYS_ON_STUD_AT_START_2);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByDaysOnStudyAtEnd() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setDaysOnStudyAtEnd(
                new RangeFilter<>(DAYS_ON_STUD_AT_END, DAYS_ON_STUD_AT_END)));

        softly.assertThat(result.getDaysOnStudyAtEnd().getFrom()).isEqualTo(DAYS_ON_STUD_AT_END);
        softly.assertThat(result.getDaysOnStudyAtEnd().getTo()).isEqualTo(DAYS_ON_STUD_AT_END);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDaysOnStudyAtEnds() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setDaysOnStudyAtEnd(
                new RangeFilter<>(DAYS_ON_STUD_AT_END, DAYS_ON_STUD_AT_END_2)));

        softly.assertThat(result.getDaysOnStudyAtEnd().getFrom()).isEqualTo(DAYS_ON_STUD_AT_END);
        softly.assertThat(result.getDaysOnStudyAtEnd().getTo()).isEqualTo(DAYS_ON_STUD_AT_END_2);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByStartDate() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setStartDate(
                new DateRangeFilter(START_DATE, START_DATE)));

        softly.assertThat(result.getStartDate().getFrom()).isInSameDayAs(START_DATE);
        softly.assertThat(result.getStartDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(START_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStartDates() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setStartDate(
                new DateRangeFilter(START_DATE, START_DATE_2)));

        softly.assertThat(result.getStartDate().getFrom()).isInSameDayAs(START_DATE);
        softly.assertThat(result.getStartDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(START_DATE_2, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByStartDatesIncludeEmpty() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setStartDate(
                new DateRangeFilter(START_DATE, START_DATE_2, true)));

        softly.assertThat(result.getStartDate().getFrom()).isInSameDayAs(START_DATE);
        softly.assertThat(result.getStartDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(START_DATE_2, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldFilterByEndDate() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setEndDate(
                new DateRangeFilter(END_DATE, END_DATE)));

        softly.assertThat(result.getEndDate().getFrom()).isInSameDayAs(END_DATE);
        softly.assertThat(result.getEndDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(END_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByEndDates() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setEndDate(
                new DateRangeFilter(END_DATE, END_DATE_2)));

        softly.assertThat(result.getEndDate().getFrom()).isInSameDayAs(END_DATE);
        softly.assertThat(result.getEndDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(END_DATE_2, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByEndDatesIncludeEmpty() {
        ExacerbationFilters result = givenFilterSetup(filters -> filters.setEndDate(
                new DateRangeFilter(END_DATE, END_DATE_2, true)));

        softly.assertThat(result.getEndDate().getFrom()).isInSameDayAs(END_DATE);
        softly.assertThat(result.getEndDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(END_DATE_2, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    private ExacerbationFilters givenFilterSetup(final Consumer<ExacerbationFilters> filterSetter) {
        List<Exacerbation> events = createExacerbationEvents();
        final List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());

        ExacerbationFilters exacerbationFilters = new ExacerbationFilters();
        filterSetter.accept(exacerbationFilters);
        return (ExacerbationFilters) exacerbationFilterService.getAvailableFilters(events, exacerbationFilters, subjects, PopulationFilters.empty());
    }

    private List<Exacerbation> createExacerbationEvents() {
        Subject subj1 = Subject.builder()
                .subjectId("subj-1")
                .firstTreatmentDate(toDate("01.01.2000"))
                .build();
        Exacerbation exacerbation1 = new Exacerbation(ExacerbationRaw.builder()
                .id("1")
                .exacerbationClassification(CLASSIFICATION)
                .antibioticsTreatment(ANTIBIOTICS_TRT)
                .daysOnStudyAtStart(DAYS_ON_STUD_AT_START)
                .daysOnStudyAtEnd(DAYS_ON_STUD_AT_END)
                .depotCorticosteroidTreatment(DEPOT_GCS_TRT)
                .duration(DURATION)
                .emergencyRoomVisit(EMERGENCY_ROOM_VISIT)
                .hospitalisation("hospitalisation_1")
                .startDate(START_DATE)
                .endDate(END_DATE)
                .startPriorToRandomisation(START_PRIOR_TO_RANDOMISATION)
                .endPriorToRandomisation(END_PRIOR_TO_RANDOMISATION)
                .increasedInhaledCorticosteroidTreatment(INC_INHALED_CORT_TRT)
                .systemicCorticosteroidTreatment(SYS_CORT_TRT)
                .build(), subj1);
        Exacerbation exacerbation2 = new Exacerbation(ExacerbationRaw.builder()
                .id("2")
                .exacerbationClassification("classification_2")
                .antibioticsTreatment("antibiotics_trt_2")
                .daysOnStudyAtStart(DAYS_ON_STUD_AT_START_2)
                .daysOnStudyAtEnd(DAYS_ON_STUD_AT_END_2)
                .depotCorticosteroidTreatment("depot_gcs_trt_2")
                .duration(DURATION_2)
                .emergencyRoomVisit("emergency_room_visit_2")
                .hospitalisation(HOSPITALIZATION)
                .startDate(START_DATE_2)
                .endDate(END_DATE_2)
                .startPriorToRandomisation("start_prior_to_randomisation_2")
                .endPriorToRandomisation("end_prior_to_randomisation_2")
                .increasedInhaledCorticosteroidTreatment("inc_inhaled_cort_trt_2")
                .systemicCorticosteroidTreatment("sys_cort_trt_2")
                .build(), subj1);
        Exacerbation exacerbWithEmptyValues = new Exacerbation(ExacerbationRaw.builder()
                .id("3")
                .build(), subj1);
        return Arrays.asList(exacerbation1, exacerbation2, exacerbWithEmptyValues);
    }
}
