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

import com.acuity.visualisations.rawdatamodel.dataproviders.LiverDiagDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DateRangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.LiverDiagFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.LiverDiagRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverDiag;
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
public class LiverDiagFilterServiceTest {

    private static final String LIVER_DIAG_INV = "Liver biopsy";
    private static final String LIVER_DIAG_INV_SPEC = "Bile Cytology";
    private static final Date LIVER_DIAG_INV_DATE = DateUtils.toDateTime("22.03.2017 00:00");
    private static final Integer STUDY_DAY_LIVER_DIAG_INV = 21;
    private static final String LIVER_DIAG_INV_RESULT = "atypical cells";
    private static final Integer POTENTIAL_HYS_LAW_CASE_NUM = 1;

    @Autowired
    private LiverDiagFilterService liverDiagFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private LiverDiagDatasetsDataProvider liverDiagDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private Subject SUBJECT1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
            .firstTreatmentDate(DateUtils.toDate("01.03.2017"))
            .dateOfRandomisation(DateUtils.toDate("02.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2016")).build();

    private LiverDiag LIVER_DIAG1 = new LiverDiag(LiverDiagRaw.builder().liverDiagInv(LIVER_DIAG_INV).liverDiagInvSpec("Anti-HCV")
            .liverDiagInvDate(DateUtils.toDateTime("23.03.2017 00:00")).liverDiagInvResult(LIVER_DIAG_INV_RESULT).potentialHysLawCaseNum(2)
            .build(), SUBJECT1);
    private LiverDiag LIVER_DIAG2 = new LiverDiag(LiverDiagRaw.builder().liverDiagInv("X-Ray").liverDiagInvSpec(LIVER_DIAG_INV_SPEC)
            .liverDiagInvDate(LIVER_DIAG_INV_DATE).liverDiagInvResult("Negative").potentialHysLawCaseNum(POTENTIAL_HYS_LAW_CASE_NUM)
            .build(), SUBJECT1);

    private List<LiverDiag> LIVER_DIAGS = Arrays.asList(LIVER_DIAG1, LIVER_DIAG2);

    private LiverDiagFilters givenFilterSetup(final Consumer<LiverDiagFilters> filterSetter) {
        final List<Subject> subjects = LIVER_DIAGS.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList());

        LiverDiagFilters liverDiagFilters = new LiverDiagFilters();
        filterSetter.accept(liverDiagFilters);
        return (LiverDiagFilters) liverDiagFilterService.getAvailableFilters(LIVER_DIAGS, liverDiagFilters, subjects, PopulationFilters.empty());
    }

    @Test
    public void shouldFilterByLiverDiagInv() {
        LiverDiagFilters result = givenFilterSetup(filters -> filters.setLiverDiagInv(
                new SetFilter<String>(Collections.singleton(LIVER_DIAG_INV))));

        softly.assertThat(result.getLiverDiagInv().getValues()).containsOnly(LIVER_DIAG_INV);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByLiverDiagInvSpec() {
        LiverDiagFilters result = givenFilterSetup(filters -> filters.setLiverDiagInvSpec(
                new SetFilter<String>(Collections.singleton(LIVER_DIAG_INV_SPEC))));

        softly.assertThat(result.getLiverDiagInvSpec().getValues()).containsOnly(LIVER_DIAG_INV_SPEC);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByLiverDiagInvDate() {
        LiverDiagFilters result = givenFilterSetup(filters -> filters.setLiverDiagInvDate(
                new DateRangeFilter(LIVER_DIAG_INV_DATE, LIVER_DIAG_INV_DATE)));

        softly.assertThat(result.getLiverDiagInvDate().getFrom()).isInSameDayAs(LIVER_DIAG_INV_DATE);
        softly.assertThat(result.getLiverDiagInvDate().getTo()).isInSameDayAs(
                org.apache.commons.lang3.time.DateUtils.addMilliseconds(DaysUtil.addDays(LIVER_DIAG_INV_DATE, 1), -1));
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStudyDayLiverDiagInv() {
        LiverDiagFilters result = givenFilterSetup(filters -> filters.setStudyDayLiverDiagInv(
                new RangeFilter<Integer>(STUDY_DAY_LIVER_DIAG_INV, STUDY_DAY_LIVER_DIAG_INV)));

        softly.assertThat(result.getStudyDayLiverDiagInv().getFrom()).isEqualTo(STUDY_DAY_LIVER_DIAG_INV);
        softly.assertThat(result.getStudyDayLiverDiagInv().getTo()).isEqualTo(STUDY_DAY_LIVER_DIAG_INV);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByStudyDayLiverResult() {
        LiverDiagFilters result = givenFilterSetup(filters -> filters.setLiverDiagInvResult(
                new SetFilter<String>(Collections.singleton(LIVER_DIAG_INV_RESULT))));

        softly.assertThat(result.getLiverDiagInvResult().getValues()).containsOnly(LIVER_DIAG_INV_RESULT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByPotentialHysLawCaseNumResult() {
        LiverDiagFilters result = givenFilterSetup(filters -> filters.setPotentialHysLawCaseNum(
                new RangeFilter<Integer>(POTENTIAL_HYS_LAW_CASE_NUM, POTENTIAL_HYS_LAW_CASE_NUM)));

        softly.assertThat(result.getPotentialHysLawCaseNum().getFrom()).isEqualTo(POTENTIAL_HYS_LAW_CASE_NUM);
        softly.assertThat(result.getPotentialHysLawCaseNum().getTo()).isEqualTo(POTENTIAL_HYS_LAW_CASE_NUM);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }
}
