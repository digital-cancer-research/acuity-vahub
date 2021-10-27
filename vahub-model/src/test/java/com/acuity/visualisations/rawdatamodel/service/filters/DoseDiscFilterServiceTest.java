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

import com.acuity.visualisations.rawdatamodel.dataproviders.DoseDiscDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.DoseDiscFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.DoseDiscRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;
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
public class DoseDiscFilterServiceTest {

    @Autowired
    private DoseDiscontinuationFilterService doseDiscFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean(name = "eventDataProvider")
    private DoseDiscDatasetsDataProvider doseDiscDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final String STUDY_DRUG = "study_drug_1";
    private static final String DISC_SPEC = "disc_spec_1";
    private static final Date DISC_DATE = DaysUtil.toDate("2017-12-11");
    private static final String DISC_MAIN_REASON = "disc_main_reason_1";
    private static final String SUBJECT_DECISION_SPEC = "subject_disc_spec_2";
    private static final String SUBJECT_DECISION_SPEC_OTHER = "subject_disc_spec_other_2";

    private static final Subject SUBJ_1 = Subject.builder()
            .subjectId("subj-1")
            .dateOfRandomisation(toDate("01.01.2000"))
            .firstTreatmentDate(toDate("01.01.2000"))
            .build();

    private static final DoseDisc DOSE_DISC_1 = new DoseDisc(
            DoseDiscRaw.builder()
                    .id("id1")
                    .subjectId("subj-1")
                    .discDate(DISC_DATE)
                    .discReason(DISC_MAIN_REASON)
                    .studyDrug(STUDY_DRUG)
                    .subjectDecisionSpec("subject_decision_spec_1")
                    .subjectDecisionSpecOther("subject_decision_spec_other_1")
                    .ipDiscSpec(DISC_SPEC)
                    .build(), SUBJ_1);

    private static final DoseDisc DOSE_DISC_2 = new DoseDisc(
            DoseDiscRaw.builder()
                    .id("id2")
                    .subjectId("subj-1")
                    .discDate(new Date())
                    .discReason("disc_main_reason_2")
                    .studyDrug("study_drug_2")
                    .subjectDecisionSpec(SUBJECT_DECISION_SPEC)
                    .subjectDecisionSpecOther(SUBJECT_DECISION_SPEC_OTHER)
                    .ipDiscSpec("disc_spec_2")
                    .build(),
            SUBJ_1);
    private static final DoseDisc DOSE_DISC_3 = new DoseDisc(
            DoseDiscRaw.builder()
                    .id("id3")
                    .subjectId("subj-1")
                    .build(),
            SUBJ_1);

    private static final List<DoseDisc> DOSE_DISC_LIST = Arrays.asList(DOSE_DISC_1, DOSE_DISC_2, DOSE_DISC_3);

    @Test
    public void shouldFilterByStudyDrug() {
        DoseDiscFilters result = givenFilterSetup(filters -> filters.setStudyDrug(
                new SetFilter<>(Collections.singletonList(STUDY_DRUG))));

        softly.assertThat(result.getStudyDrug().getValues()).containsOnly(STUDY_DRUG);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDiscSpec() {
        DoseDiscFilters result = givenFilterSetup(filters -> filters.setDiscSpec(
                new SetFilter<>(Collections.singletonList(DISC_SPEC))));

        softly.assertThat(result.getDiscSpec().getValues()).containsOnly(DISC_SPEC);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDiscDate() {
        DoseDiscFilters result = givenFilterSetup(filters -> filters.setDiscDate(
                new DateRangeFilter(DISC_DATE, DISC_DATE)));

        softly.assertThat(result.getDiscDate().getFrom()).isInSameDayAs(DISC_DATE);
        softly.assertThat(result.getDiscDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(DISC_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDiscMainReason() {
        DoseDiscFilters result = givenFilterSetup(filters -> filters.setDiscMainReason(
                new SetFilter<>(Collections.singletonList(DISC_MAIN_REASON))));

        softly.assertThat(result.getDiscMainReason().getValues()).containsOnly(DISC_MAIN_REASON);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStudyDayAtDisc() {
        Integer studyDayAtDisc = DOSE_DISC_1.getStudyDayAtIpDiscontinuation();
        DoseDiscFilters result = givenFilterSetup(filters -> filters.setStudyDayAtDisc(
                new RangeFilter<>(studyDayAtDisc, studyDayAtDisc)));

        softly.assertThat(result.getStudyDayAtDisc().getFrom()).isEqualTo(studyDayAtDisc);
        softly.assertThat(result.getStudyDayAtDisc().getTo()).isEqualTo(studyDayAtDisc);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterBySubjectDiscSpec() {
        DoseDiscFilters result = givenFilterSetup(filters -> filters.setSubjectDecisionSpec(
                new SetFilter<>(Collections.singletonList(SUBJECT_DECISION_SPEC))));

        softly.assertThat(result.getSubjectDecisionSpec().getValues()).containsOnly(SUBJECT_DECISION_SPEC);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterBySubjectDiscSpecOther() {
        DoseDiscFilters result = givenFilterSetup(filters -> filters.setSubjectDecisionSpecOther(
                new SetFilter<>(Collections.singletonList(SUBJECT_DECISION_SPEC_OTHER))));

        softly.assertThat(result.getSubjectDecisionSpecOther().getValues()).containsOnly(SUBJECT_DECISION_SPEC_OTHER);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    private DoseDiscFilters givenFilterSetup(final Consumer<DoseDiscFilters> filterSetter) {
        List<DoseDisc> events = DOSE_DISC_LIST;
        final List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());

        DoseDiscFilters doseDiscFilters = new DoseDiscFilters();
        filterSetter.accept(doseDiscFilters);
        return (DoseDiscFilters) doseDiscFilterService
                .getAvailableFilters(events, doseDiscFilters, subjects, PopulationFilters.empty());
    }
}
