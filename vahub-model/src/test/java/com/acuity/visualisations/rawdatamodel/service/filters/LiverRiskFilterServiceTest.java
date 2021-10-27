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

import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;

import com.acuity.visualisations.rawdatamodel.dataproviders.LiverRiskDatasetDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.LiverRiskFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.LiverRiskRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverRisk;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class LiverRiskFilterServiceTest {

    @Autowired
    private LiverRiskFilterService liverRiskFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean(name = "eventDataProvider")
    private LiverRiskDatasetDataProvider liverRiskDatasetDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final Date FIRST_TREATMENT_DATE = toDate("01.01.2000");

    private static final String VALUE = "value_1";
    private static final String COMMENT = "comment_1";
    private static final String DETAILS = "details_1";
    private static final String REFERENCE_PERIOD = "preference_period_1";
    private static final String OCCURRENCE = "occurrence_1";
    private static final Date START_DATE = toDate("02.01.2000");
    private static final Date STOP_DATE = toDate("02.01.2001");
    private static final Integer POTENTIAL_NUM = 10;

    private static final Integer DAYS_AT_START = DaysUtil.daysBetween(FIRST_TREATMENT_DATE, START_DATE).getAsInt();
    private static final Integer DAYS_AT_STOP = DaysUtil.daysBetween(FIRST_TREATMENT_DATE, STOP_DATE).getAsInt();

    private static final Subject SUBJ_1 = Subject.builder()
            .subjectId("subj-1")
            .dateOfRandomisation(toDate("01.01.2000"))
            .firstTreatmentDate(FIRST_TREATMENT_DATE)
            .build();

    private static final LiverRisk LIVER_RISK_1 = new LiverRisk(
            LiverRiskRaw.builder()
                    .id("id1")
                    .subjectId("subj-1")
                    .value(VALUE)
                    .comment(COMMENT)
                    .details(DETAILS)
                    .referencePeriod(REFERENCE_PERIOD)
                    .occurrence(OCCURRENCE)
                    .startDate(new Date())
                    .stopDate(new Date())
                    .potentialHysLawCaseNum(1)
                    .build(), SUBJ_1);

    private static final LiverRisk LIVER_RISK_2 = new LiverRisk(
            LiverRiskRaw.builder()
                    .id("id1")
                    .subjectId("subj-1")
                    .value("value_2")
                    .comment("comment_2")
                    .details("details_2")
                    .referencePeriod("period_2")
                    .occurrence("occurence_2")
                    .startDate(START_DATE)
                    .stopDate(STOP_DATE)
                    .potentialHysLawCaseNum(POTENTIAL_NUM)
                    .build(), SUBJ_1);

    private static final LiverRisk LIVER_RISK_3 = new LiverRisk(
            LiverRiskRaw.builder()
                    .id("id1")
                    .subjectId("subj-1")
                    .build(), SUBJ_1);

    private static final List<LiverRisk> LIVER_RISK_LIST =
            Arrays.asList(LIVER_RISK_1, LIVER_RISK_2, LIVER_RISK_3);

    @Test
    public void shouldFilterByValue() {
        LiverRiskFilters result = givenFilterSetup(filters -> filters.setValue(
                new SetFilter<>(Collections.singletonList(VALUE))));

        softly.assertThat(result.getValue().getValues()).containsOnly(VALUE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDetails() {
        LiverRiskFilters result = givenFilterSetup(filters -> filters.setDetails(
                new SetFilter<>(Collections.singletonList(DETAILS))));

        softly.assertThat(result.getDetails().getValues()).containsOnly(DETAILS);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByOccurrence() {
        LiverRiskFilters result = givenFilterSetup(filters -> filters.setOccurrence(
                new SetFilter<>(Collections.singletonList(OCCURRENCE))));

        softly.assertThat(result.getOccurrence().getValues()).containsOnly(OCCURRENCE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByReferencePeriod() {
        LiverRiskFilters result = givenFilterSetup(filters -> filters.setReferencePeriod(
                new SetFilter<>(Collections.singletonList(REFERENCE_PERIOD))));

        softly.assertThat(result.getReferencePeriod().getValues()).containsOnly(REFERENCE_PERIOD);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByComment() {
        LiverRiskFilters result = givenFilterSetup(filters -> filters.setComment(
                new SetFilter<>(Collections.singletonList(COMMENT))));

        softly.assertThat(result.getComment().getValues()).containsOnly(COMMENT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStartDate() {
        LiverRiskFilters result = givenFilterSetup(filters -> filters.setStartDate(
                new DateRangeFilter(START_DATE, START_DATE)));

        softly.assertThat(result.getStartDate().getFrom()).isInSameDayAs(START_DATE);
        softly.assertThat(result.getStartDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(START_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStopDate() {
        LiverRiskFilters result = givenFilterSetup(filters -> filters.setStopDate(
                new DateRangeFilter(STOP_DATE, STOP_DATE)));

        softly.assertThat(result.getStopDate().getFrom()).isInSameDayAs(STOP_DATE);
        softly.assertThat(result.getStopDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(STOP_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByPotentialHysLawCaseNum() {
        LiverRiskFilters result = givenFilterSetup(filters -> filters.setPotentialHysLawCaseNum(
                new RangeFilter<>(POTENTIAL_NUM, POTENTIAL_NUM)));

        softly.assertThat(result.getPotentialHysLawCaseNum().getFrom()).isEqualTo(POTENTIAL_NUM);
        softly.assertThat(result.getPotentialHysLawCaseNum().getTo()).isEqualTo(POTENTIAL_NUM);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStudyDayAtStart() {
        LiverRiskFilters result = givenFilterSetup(filters -> filters.setStudyDayAtStart(
                new RangeFilter<>(DAYS_AT_START, DAYS_AT_START)));

        softly.assertThat(result.getStudyDayAtStart().getFrom()).isEqualTo(DAYS_AT_START);
        softly.assertThat(result.getStudyDayAtStart().getTo()).isEqualTo(DAYS_AT_START);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStudyDayAtStop() {
        LiverRiskFilters result = givenFilterSetup(filters -> filters.setStudyDayAtStop(
                new RangeFilter<>(DAYS_AT_STOP, DAYS_AT_STOP)));

        softly.assertThat(result.getStudyDayAtStop().getFrom()).isEqualTo(DAYS_AT_STOP);
        softly.assertThat(result.getStudyDayAtStop().getTo()).isEqualTo(DAYS_AT_STOP);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    private LiverRiskFilters givenFilterSetup(final Consumer<LiverRiskFilters> filterSetter) {
        List<LiverRisk> events = LIVER_RISK_LIST;
        final List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());

        LiverRiskFilters liverRiskFilters = new LiverRiskFilters();
        filterSetter.accept(liverRiskFilters);
        return (LiverRiskFilters) liverRiskFilterService
                .getAvailableFilters(events, liverRiskFilters, subjects, PopulationFilters.empty());
    }
}
