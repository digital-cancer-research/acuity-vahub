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

import com.acuity.visualisations.rawdatamodel.dataproviders.DrugDoseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.MultiValueIntRangeSetFilter;
import com.acuity.visualisations.rawdatamodel.filters.MultiValueSetFilter;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
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
public class DrugDoseFilterServiceTest {

    @Autowired
    private DrugDoseFilterService drugDoseFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean(name = "eventDataProvider")
    private DrugDoseDatasetsDataProvider drugDoseDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final String STUDY_DRUG = "study_drug_1";
    private static final String STUDY_DRUG_CATEGORY = "category_1";
    private static final Date START_DATE = DaysUtil.toDate("2017-12-11");
    private static final Date END_DATE = DaysUtil.toDate("2018-12-11");
    private static final String DOSE_UNIT = "dose_unit_1";
    private static final String DOSE_FREQ = "dose_freq_1";
    private static final Double DOSE_PER_ADMIN = 10d;
    private static final Double TOTAL_DAILY_DOSE = 100d;
    private static final Double PLANNED_DOSE = 50d;
    private static final Integer PLANNED_NO_DAYS_TREATMENT = 5;
    private static final String FORMULATION = "formulation_1";
    private static final String ROUTE = "route_1";
    private static final String ACTION_TAKEN = "action_taken_2";
    private static final String MAIN_REASON_FOR_ACTION_TAKEN = "main_reason_for_action_taken_2";
    private static final String MAIN_REASON_FOR_ACTION_TAKEN_SPEC = "main_reason_for_action_taken_spec_2";
    private static final String REASON_FOR_THERAPY = "reason_for_therapy_2";
    private static final String TREATMENT_CYCLE_DELAYED = "treatment_cycle_delayed_2";
    private static final String REASON_TREATMENT_CYCLE_DELAYED = "reason_treatment_cycle_delayed_2";
    private static final String REASON_TREATMENT_CYCLE_DELAYED_OTHER = "reason_treatment_cycle_delayed_other_2";
    private static final String MEDICATION_CODE = "medication_code_2";
    private static final String MEDICATION_TEXT = "medication_text_2";
    private static final String MEDICATION_PT = "medication_pt_2";
    private static final String MEDICATION_GROUPING_NAME = "medication_grouping_name_2";
    private static final String ATC_CODE = "atc_code_2";
    private static final String ATC_TEXT = "atc_text_2";
    private static final String ACTIVE_INGREDIENTS = "active_ingredients_2";
    private static final String DOSE_DESC = "dose_desc";


    private static final List<String> AE_PT_LIST_1 = Arrays.asList("pt_1", "pt_2");
    private static final List<String> AE_PT_LIST_2 = Arrays.asList("pt_3", "pt_2");
    private static final List<String> AE_PT_LIST_3 = Arrays.asList("pt_4", "pt_2");
    private static final List<Integer> AE_NUM_LIST_1 = Arrays.asList(6);
    private static final List<Integer> AE_NUM_LIST_2 = Arrays.asList(2, 19);
    private static final List<Integer> AE_NUM_LIST_3 = Arrays.asList(7, 19);

    private static final Subject SUBJ_1 = Subject.builder()
            .subjectId("subj-1")
            .dateOfRandomisation(toDate("01.01.2000"))
            .firstTreatmentDate(toDate("01.01.2000"))
            .build();

    private static final DrugDose DOSE_1 = new DrugDose(
            DrugDoseRaw.builder()
                    .id("id1")
                    .subjectId("subj-1")
                    .drug(STUDY_DRUG)
                    .doseDescription(DOSE_DESC)
                    .studyDrugCategory(STUDY_DRUG_CATEGORY)
                    .doseUnit(DOSE_UNIT)
                    .frequencyName(DOSE_FREQ)
                    .dose(DOSE_PER_ADMIN)
                    .formulation(FORMULATION)
                    .route(ROUTE)
                    .totalDailyDose(TOTAL_DAILY_DOSE)
                    .plannedDose(PLANNED_DOSE)
                    .plannedNoDaysTreatment(PLANNED_NO_DAYS_TREATMENT)
                    .startDate(START_DATE)
                    .endDate(END_DATE)
                    .actionTaken("action_taken_1")
                    .reasonForActionTaken("main_reason_1")
                    .mainReasonForActionTakenSpec("main_reason_spec_1")
                    .reasonForTherapy("reason_for_therapy_1")
                    .treatmentCycleDelayed("treatment_cycle_1")
                    .reasonTreatmentCycleDelayed("reason_treatment_cycle_delayed_1")
                    .reasonTreatmentCycleDelayedOther("reason_treatment_cycle_delayed_other_1")
                    .medicationCode("medication_code_1")
                    .medicationDictionaryText("medication_text_1")
                    .medicationGroupingName("medication_grouping_name_1")
                    .medicationPt("medication_pt_1")
                    .atcCode("atc_code_1")
                    .atcDictionaryText("atc_text_1")
                    .activeIngredient("active_ingredients_1")
                    .aeNumCausedActionTaken(AE_NUM_LIST_1)
                    .aeNumCausedTreatmentCycleDelayed(AE_NUM_LIST_1)
                    .aePtCausedActionTaken(AE_PT_LIST_1)
                    .aePtCausedTreatmentCycleDelayed(AE_PT_LIST_1)
                    .build(), SUBJ_1);

    private static final DrugDose DOSE_2 = new DrugDose(
            DrugDoseRaw.builder()
                    .id("id2")
                    .subjectId("subj-1")
                    .drug("study_drug_2")
                    .drug("study_drug_2")
                    .studyDrugCategory("category_2")
                    .doseUnit("dose_unit_2")
                    .frequencyName("freq_2")
                    .dose(7d)
                    .formulation("formulation_@")
                    .route("route_2")
                    .totalDailyDose(1000d)
                    .plannedDose(200d)
                    .plannedNoDaysTreatment(20)
                    .startDate(new Date())
                    .endDate(new Date())
                    .actionTaken(ACTION_TAKEN)
                    .reasonForActionTaken(MAIN_REASON_FOR_ACTION_TAKEN)
                    .mainReasonForActionTakenSpec(MAIN_REASON_FOR_ACTION_TAKEN_SPEC)
                    .reasonForTherapy(REASON_FOR_THERAPY)
                    .treatmentCycleDelayed(TREATMENT_CYCLE_DELAYED)
                    .reasonTreatmentCycleDelayed(REASON_TREATMENT_CYCLE_DELAYED)
                    .reasonTreatmentCycleDelayedOther(REASON_TREATMENT_CYCLE_DELAYED_OTHER)
                    .medicationCode(MEDICATION_CODE)
                    .medicationDictionaryText(MEDICATION_TEXT)
                    .medicationGroupingName(MEDICATION_GROUPING_NAME)
                    .medicationPt(MEDICATION_PT)
                    .atcCode(ATC_CODE)
                    .atcDictionaryText(ATC_TEXT)
                    .activeIngredient(ACTIVE_INGREDIENTS)
                    .aeNumCausedActionTaken(AE_NUM_LIST_2)
                    .aeNumCausedTreatmentCycleDelayed(AE_NUM_LIST_2)
                    .aePtCausedActionTaken(AE_PT_LIST_2)
                    .aePtCausedTreatmentCycleDelayed(AE_PT_LIST_2)
                    .build(),
            SUBJ_1);

    private static final DrugDose DOSE_3 = new DrugDose(
            DrugDoseRaw.builder()
                    .id("id3")
                    .subjectId("subj-1")
                    .aeNumCausedActionTaken(AE_NUM_LIST_3)
                    .aeNumCausedTreatmentCycleDelayed(AE_NUM_LIST_3)
                    .aePtCausedActionTaken(AE_PT_LIST_3)
                    .aePtCausedTreatmentCycleDelayed(AE_PT_LIST_3)
                    .build(),
            SUBJ_1);

    private static final List<DrugDose> DOSE_LIST = Arrays.asList(DOSE_1, DOSE_2, DOSE_3);

    @Test
    public void shouldFilterByAeNumCausedActionTaken() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setAeNumCausedActionTaken(
                new MultiValueIntRangeSetFilter(19, 19)));

        softly.assertThat(result.getAeNumCausedActionTaken().getFrom()).isEqualTo(2);
        softly.assertThat(result.getAeNumCausedActionTaken().getTo()).isEqualTo(19);
        softly.assertThat(result.getAeNumCausedActionTaken().getValues()).containsOnly(2, 19, 7);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void shouldFilterByAeNumCausedTreatmentCycleDelayed() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setAeNumCausedTreatmentCycleDelayed(
                new MultiValueIntRangeSetFilter(6, 6)));

        softly.assertThat(result.getAeNumCausedTreatmentCycleDelayed().getFrom()).isEqualTo(6);
        softly.assertThat(result.getAeNumCausedTreatmentCycleDelayed().getTo()).isEqualTo(6);
        softly.assertThat(result.getAeNumCausedTreatmentCycleDelayed().getValues()).containsOnly(6);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByAePtCausedActionTaken() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setAePtCausedActionTaken(
                new MultiValueSetFilter<>(Collections.singletonList("pt_2"))));

        softly.assertThat(result.getAePtCausedActionTaken().getValues()).containsOnly("pt_1", "pt_2", "pt_3", "pt_4");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldFilterByAePtCausedTreatmentCycleDelayed() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setAePtCausedTreatmentCycleDelayed(
                new MultiValueSetFilter<>(Collections.singletonList("pt_2"))));

        softly.assertThat(result.getAePtCausedTreatmentCycleDelayed().getValues()).containsOnly("pt_1", "pt_2", "pt_3", "pt_4");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(3);
    }

    @Test
    public void shouldFilterByStudyDrugWhenDoseDescNonNull() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setStudyDrug(
                new SetFilter<>(Collections.singletonList(STUDY_DRUG))));

        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(0);
    }

    @Test
    public void shouldFilterByStudyDrugWithDoseDesc() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setStudyDrug(
                new SetFilter<>(Collections.singletonList(DOSE_DESC))));

        softly.assertThat(result.getStudyDrug().getValues()).containsOnly(DOSE_DESC);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStudyDrugCategory() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setStudyDrugCategory(
                new SetFilter<>(Collections.singletonList(STUDY_DRUG_CATEGORY))));

        softly.assertThat(result.getStudyDrugCategory().getValues()).containsOnly(STUDY_DRUG_CATEGORY);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStartDate() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setStartDate(
                new DateRangeFilter(START_DATE, START_DATE)));

        softly.assertThat(result.getStartDate().getFrom()).isInSameDayAs(START_DATE);
        softly.assertThat(result.getStartDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(START_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByEndDate() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setEndDate(
                new DateRangeFilter(END_DATE, END_DATE)));

        softly.assertThat(result.getEndDate().getFrom()).isInSameDayAs(END_DATE);
        softly.assertThat(result.getEndDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(END_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDoseUnit() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setDoseUnit(
                new SetFilter<>(Collections.singletonList(DOSE_UNIT))));

        softly.assertThat(result.getDoseUnit().getValues()).containsOnly(DOSE_UNIT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDoseFreq() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setDoseFreq(
                new SetFilter<>(Collections.singletonList(DOSE_FREQ))));

        softly.assertThat(result.getDoseFreq().getValues()).containsOnly(DOSE_FREQ);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDosePerAdmin() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setDosePerAdmin(
                new RangeFilter<>(DOSE_PER_ADMIN, DOSE_PER_ADMIN)));

        softly.assertThat(result.getDosePerAdmin().getFrom()).isEqualTo(DOSE_PER_ADMIN);
        softly.assertThat(result.getDosePerAdmin().getTo()).isEqualTo(DOSE_PER_ADMIN);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByTotalDailyDose() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setTotalDailyDose(
                new RangeFilter<>(TOTAL_DAILY_DOSE, TOTAL_DAILY_DOSE)));

        softly.assertThat(result.getTotalDailyDose().getFrom()).isEqualTo(TOTAL_DAILY_DOSE);
        softly.assertThat(result.getTotalDailyDose().getTo()).isEqualTo(TOTAL_DAILY_DOSE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByPlannedDose() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setPlannedDose(
                new RangeFilter<>(PLANNED_DOSE, PLANNED_DOSE)));

        softly.assertThat(result.getPlannedDose().getFrom()).isEqualTo(PLANNED_DOSE);
        softly.assertThat(result.getPlannedDose().getTo()).isEqualTo(PLANNED_DOSE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByPlannedDoseUnits() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setDoseUnit(
                new SetFilter<>(Collections.singletonList(DOSE_UNIT))));

        softly.assertThat(result.getDoseUnit().getValues()).containsOnly(DOSE_UNIT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByPlannedNoDaysTreatment() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setPlannedNoDaysTreatment(
                new RangeFilter<>(PLANNED_NO_DAYS_TREATMENT, PLANNED_NO_DAYS_TREATMENT)));

        softly.assertThat(result.getPlannedNoDaysTreatment().getFrom()).isEqualTo(PLANNED_NO_DAYS_TREATMENT);
        softly.assertThat(result.getPlannedNoDaysTreatment().getTo()).isEqualTo(PLANNED_NO_DAYS_TREATMENT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByFormulation() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setFormulation(
                new SetFilter<>(Collections.singletonList(FORMULATION))));

        softly.assertThat(result.getFormulation().getValues()).containsOnly(FORMULATION);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByRoute() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setRoute(
                new SetFilter<>(Collections.singletonList(ROUTE))));

        softly.assertThat(result.getRoute().getValues()).containsOnly(ROUTE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByActionTaken() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setActionTaken(
                new SetFilter<>(Collections.singletonList(ACTION_TAKEN))));

        softly.assertThat(result.getActionTaken().getValues()).containsOnly(ACTION_TAKEN);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByMainReasonForActionTaken() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setMainReasonForActionTaken(
                new SetFilter<>(Collections.singletonList(MAIN_REASON_FOR_ACTION_TAKEN))));

        softly.assertThat(result.getMainReasonForActionTaken().getValues()).containsOnly(MAIN_REASON_FOR_ACTION_TAKEN);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByMainReasonForActionTakenSpec() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setMainReasonForActionTakenSpec(
                new SetFilter<>(Collections.singletonList(MAIN_REASON_FOR_ACTION_TAKEN_SPEC))));

        softly.assertThat(result.getMainReasonForActionTakenSpec().getValues()).containsOnly(MAIN_REASON_FOR_ACTION_TAKEN_SPEC);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByReasonForTherapy() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setReasonForTherapy(
                new SetFilter<>(Collections.singletonList(REASON_FOR_THERAPY))));

        softly.assertThat(result.getReasonForTherapy().getValues()).containsOnly(REASON_FOR_THERAPY);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByTreatmentCycleDelayed() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setTreatmentCycleDelayed(
                new SetFilter<>(Collections.singletonList(TREATMENT_CYCLE_DELAYED))));

        softly.assertThat(result.getTreatmentCycleDelayed().getValues()).containsOnly(TREATMENT_CYCLE_DELAYED);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByReasonTreatmentCycleDelayed() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setReasonTreatmentCycleDelayed(
                new SetFilter<>(Collections.singletonList(REASON_TREATMENT_CYCLE_DELAYED))));

        softly.assertThat(result.getReasonTreatmentCycleDelayed().getValues()).containsOnly(REASON_TREATMENT_CYCLE_DELAYED);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByReasonTreatmentCycleDelayedOther() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setReasonTreatmentCycleDelayedOther(
                new SetFilter<>(Collections.singletonList(REASON_TREATMENT_CYCLE_DELAYED_OTHER))));

        softly.assertThat(result.getReasonTreatmentCycleDelayedOther().getValues()).containsOnly(REASON_TREATMENT_CYCLE_DELAYED_OTHER);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByMedicationPt() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setMedicationPt(
                new SetFilter<>(Collections.singletonList(MEDICATION_PT))));

        softly.assertThat(result.getMedicationPt().getValues()).containsOnly(MEDICATION_PT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByMedicationCode() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setMedicationCode(
                new SetFilter<>(Collections.singletonList(MEDICATION_CODE))));

        softly.assertThat(result.getMedicationCode().getValues()).containsOnly(MEDICATION_CODE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByMedicationGroupingName() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setMedicationGroupingName(
                new SetFilter<>(Collections.singletonList(MEDICATION_GROUPING_NAME))));

        softly.assertThat(result.getMedicationGroupingName().getValues()).containsOnly(MEDICATION_GROUPING_NAME);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByMedicationText() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setMedicationDictionaryText(
                new SetFilter<>(Collections.singletonList(MEDICATION_TEXT))));

        softly.assertThat(result.getMedicationDictionaryText().getValues()).containsOnly(MEDICATION_TEXT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByAtcText() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setAtcDictionaryText(
                new SetFilter<>(Collections.singletonList(ATC_TEXT))));

        softly.assertThat(result.getAtcDictionaryText().getValues()).containsOnly(ATC_TEXT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByAtcCode() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setAtcCode(
                new SetFilter<>(Collections.singletonList(ATC_CODE))));

        softly.assertThat(result.getAtcCode().getValues()).containsOnly(ATC_CODE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByActiveIngredients() {
        DrugDoseFilters result = givenFilterSetup(filters -> filters.setActiveIngredients(
                new SetFilter<>(Collections.singletonList(ACTIVE_INGREDIENTS))));

        softly.assertThat(result.getActiveIngredients().getValues()).containsOnly(ACTIVE_INGREDIENTS);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    private DrugDoseFilters givenFilterSetup(final Consumer<DrugDoseFilters> filterSetter) {
        List<DrugDose> events = DOSE_LIST;
        final List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());

        DrugDoseFilters drugDoseFilters = new DrugDoseFilters();
        filterSetter.accept(drugDoseFilters);
        return (DrugDoseFilters) drugDoseFilterService
                .getAvailableFilters(events, drugDoseFilters, subjects, PopulationFilters.empty());
    }
}
