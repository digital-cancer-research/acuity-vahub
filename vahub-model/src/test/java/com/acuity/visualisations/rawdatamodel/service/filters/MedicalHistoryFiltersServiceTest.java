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

import com.acuity.visualisations.rawdatamodel.dataproviders.MedicalHistoryDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.MedicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.annotation.SpringITTest;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.MedicalHistoryRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.MedicalHistory;
import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
@SpringITTest
public class MedicalHistoryFiltersServiceTest {

    @Autowired
    private MedicalHistoryFilterService medicalHistoryFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean(name = "eventDataProvider")
    private MedicalHistoryDatasetsDataProvider medicalHistoryDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final String CURRENT_MEDICATION = "current_medication_1";
    private static final String TERM = "term_1";
    private static final String CATEGORY = "category_1";
    private static final String STATUS = "status_1";
    private static final Date START_DATE = toDate("02.01.2000");
    private static final Date END_DATE = toDate("02.01.2001");
    private static final String HLT = "hlt_2";
    private static final String SOC = "soc_2";
    private static final String PT = "pt_2";

    private static final Subject SUBJ_1 = Subject.builder()
            .subjectId("subj-1")
            .dateOfRandomisation(toDate("01.01.2000"))
            .firstTreatmentDate(toDate("01.01.2000"))
            .build();

    private static final MedicalHistory MEDICAL_HISTORY_1 = new MedicalHistory(
            MedicalHistoryRaw.builder()
                    .id("id1")
                    .subjectId("subj-1")
                    .currentMedication(CURRENT_MEDICATION)
                    .term(TERM)
                    .conditionStatus(STATUS)
                    .category(CATEGORY)
                    .start(START_DATE)
                    .end(END_DATE)
                    .hlt("hlt_1")
                    .preferredTerm("pt_1")
                    .soc("soc_1")
                    .build(), SUBJ_1);

    private static final MedicalHistory MEDICAL_HISTORY_2 = new MedicalHistory(
            MedicalHistoryRaw.builder()
                    .id("id2")
                    .subjectId("subj-1")
                    .currentMedication("current_medication_2")
                    .term("term_2")
                    .category("category_2")
                    .conditionStatus("status_2")
                    .start(new Date())
                    .end(new Date())
                    .hlt(HLT)
                    .preferredTerm(PT)
                    .soc(SOC)
                    .build(),
            SUBJ_1);
    private static final MedicalHistory MEDICAL_HISTORY_3 = new MedicalHistory(
            MedicalHistoryRaw.builder()
                    .id("id3")
                    .subjectId("subj-1")
                    .build(),
            SUBJ_1);

    private static final List<MedicalHistory> MEDICAL_HISTORY_LIST =
            Arrays.asList(MEDICAL_HISTORY_1, MEDICAL_HISTORY_2, MEDICAL_HISTORY_3);

    @Test
    public void shouldFilterByTerm() {
        MedicalHistoryFilters result = givenFilterSetup(filters -> filters.setTerm(
                new SetFilter<>(Collections.singletonList(TERM))));

        softly.assertThat(result.getTerm().getValues()).containsOnly(TERM);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByCurrentMedication() {
        MedicalHistoryFilters result = givenFilterSetup(filters -> filters.setCurrentMedication(
                new SetFilter<>(Collections.singletonList(CURRENT_MEDICATION))));

        softly.assertThat(result.getCurrentMedication().getValues()).containsOnly(CURRENT_MEDICATION);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByConditionStatus() {
        MedicalHistoryFilters result = givenFilterSetup(filters -> filters.setConditionStatus(
                new SetFilter<>(Collections.singletonList(STATUS))));

        softly.assertThat(result.getConditionStatus().getValues()).containsOnly(STATUS);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByCategory() {
        MedicalHistoryFilters result = givenFilterSetup(filters -> filters.setCategory(
                new SetFilter<>(Collections.singletonList(CATEGORY))));

        softly.assertThat(result.getCategory().getValues()).containsOnly(CATEGORY);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStartDate() {
        MedicalHistoryFilters result = givenFilterSetup(filters -> filters.setStart(
                new DateRangeFilter(START_DATE, START_DATE)));

        softly.assertThat(result.getStart().getFrom()).isInSameDayAs(START_DATE);
        softly.assertThat(result.getStart().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(START_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByEndDate() {
        MedicalHistoryFilters result = givenFilterSetup(filters -> filters.setEnd(
                new DateRangeFilter(END_DATE, END_DATE)));

        softly.assertThat(result.getEnd().getFrom()).isInSameDayAs(END_DATE);
        softly.assertThat(result.getEnd().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(END_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterBySoc() {
        MedicalHistoryFilters result = givenFilterSetup(filters -> filters.setSoc(
                new SetFilter<>(Collections.singletonList(SOC))));

        softly.assertThat(result.getSoc().getValues()).containsOnly(SOC);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByPt() {
        MedicalHistoryFilters result = givenFilterSetup(filters -> filters.setPreferredTerm(
                new SetFilter<>(Collections.singletonList(PT))));

        softly.assertThat(result.getPreferredTerm().getValues()).containsOnly(PT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByHlt() {
        MedicalHistoryFilters result = givenFilterSetup(filters -> filters.setHlt(
                new SetFilter<>(Collections.singletonList(HLT))));

        softly.assertThat(result.getHlt().getValues()).containsOnly(HLT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    private MedicalHistoryFilters givenFilterSetup(final Consumer<MedicalHistoryFilters> filterSetter) {
        List<MedicalHistory> events = MEDICAL_HISTORY_LIST;
        final List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());

        MedicalHistoryFilters medicalHistoryFilters = new MedicalHistoryFilters();
        filterSetter.accept(medicalHistoryFilters);
        return (MedicalHistoryFilters) medicalHistoryFilterService
                .getAvailableFilters(events, medicalHistoryFilters, subjects, PopulationFilters.empty());
    }
}
