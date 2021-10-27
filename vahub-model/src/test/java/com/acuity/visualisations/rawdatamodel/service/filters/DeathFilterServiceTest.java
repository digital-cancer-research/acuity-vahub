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

import com.acuity.visualisations.rawdatamodel.dataproviders.DeathDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DeathFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.DeathRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Death;
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
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class DeathFilterServiceTest {
    @Autowired
    private DeathFilterService deathFilterService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private DeathDatasetsDataProvider deathDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final String DEATH_CAUSE = "cause1";
    private static final String AUTOPSY_PERFORMED = "Yes";
    private static final String DESIGNATION = "designation1";
    private static final String DEATH_RELATED_TO_DISEASE = "Yes";
    private static final String HLT = "hlt";
    private static final String LLT = "llt";
    private static final String PT = "pt";
    private static final String SOC = "soc";
    private static final Integer DAYS_FROM_FIRST_DOSE_TO_DEATH = 9;

    private Subject SUBJECT1 = Subject.builder().subjectId("sid1").subjectCode("E01")
            .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("02.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2016")).build();
    private Subject SUBJECT2 = Subject.builder().subjectId("sid2").subjectCode("E02")
            .firstTreatmentDate(DateUtils.toDate("05.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("07.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("19.08.2016")).build();

    private Death DEATH1 = new Death(DeathRaw.builder().deathCause(DEATH_CAUSE).autopsyPerformed("No").designation(DESIGNATION)
            .diseaseUnderInvestigationDeath("No").hlt("hlt1").llt("llt1").preferredTerm("pt1").soc("soc1").dateOfDeath(DateUtils.toDate("10.08.2015"))
            .build(), SUBJECT1);
    private Death DEATH2 = new Death(DeathRaw.builder().deathCause("cause2").autopsyPerformed(AUTOPSY_PERFORMED).designation("designation2")
            .diseaseUnderInvestigationDeath(DEATH_RELATED_TO_DISEASE).hlt(HLT).llt(LLT).preferredTerm(PT).soc(SOC).dateOfDeath(DateUtils.toDate("19.08.2016"))
            .build(), SUBJECT2);

    private List<Death> DEATHS = Arrays.asList(DEATH1, DEATH2);

    private DeathFilters givenFilterSetup(final Consumer<DeathFilters> filterSetter) {
        final List<Subject> subjects = DEATHS.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList());

        DeathFilters deathFilters = new DeathFilters();
        filterSetter.accept(deathFilters);
        return (DeathFilters) deathFilterService.getAvailableFilters(DEATHS, deathFilters, subjects, PopulationFilters.empty());
    }

    @Test
    public void shouldFilterByDeathCause() {
        DeathFilters result = givenFilterSetup(filters -> filters.setDeathCause(
                new SetFilter<String>(Collections.singleton(DEATH_CAUSE))));

        softly.assertThat(result.getDeathCause().getValues()).containsOnly(DEATH_CAUSE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByAutopsyPerformed() {
        DeathFilters result = givenFilterSetup(filters -> filters.setAutopsyPerformed(
                new SetFilter<String>(Collections.singleton(AUTOPSY_PERFORMED))));

        softly.assertThat(result.getAutopsyPerformed().getValues()).containsOnly(AUTOPSY_PERFORMED);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDesignation() {
        DeathFilters result = givenFilterSetup(filters -> filters.setDesignation(
                new SetFilter<String>(Collections.singleton(DESIGNATION))));

        softly.assertThat(result.getDesignation().getValues()).containsOnly(DESIGNATION);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDeathRelatedToDisease() {
        DeathFilters result = givenFilterSetup(filters -> filters.setDeathRelatedToDisease(
                new SetFilter<String>(Collections.singleton(DEATH_RELATED_TO_DISEASE))));

        softly.assertThat(result.getDeathRelatedToDisease().getValues()).containsOnly(DEATH_RELATED_TO_DISEASE);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByHlt() {
        DeathFilters result = givenFilterSetup(filters -> filters.setHlt(
                new SetFilter<String>(Collections.singleton(HLT))));

        softly.assertThat(result.getHlt().getValues()).containsOnly(HLT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByLlt() {
        DeathFilters result = givenFilterSetup(filters -> filters.setLlt(
                new SetFilter<String>(Collections.singleton(LLT))));

        softly.assertThat(result.getLlt().getValues()).containsOnly(LLT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterBySoc() {
        DeathFilters result = givenFilterSetup(filters -> filters.setSoc(
                new SetFilter<String>(Collections.singleton(SOC))));

        softly.assertThat(result.getSoc().getValues()).containsOnly(SOC);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByPt() {
        DeathFilters result = givenFilterSetup(filters -> filters.setPt(
                new SetFilter<String>(Collections.singleton(PT))));

        softly.assertThat(result.getPt().getValues()).containsOnly(PT);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void shouldFilterByDaysSinceFirstDoseToDeath() {
        DeathFilters result = givenFilterSetup(filters -> filters.setDaysFromFirstDoseToDeath(
                new RangeFilter<>(DAYS_FROM_FIRST_DOSE_TO_DEATH, DAYS_FROM_FIRST_DOSE_TO_DEATH)));

        softly.assertThat(result.getDaysFromFirstDoseToDeath().getFrom()).isEqualTo(DAYS_FROM_FIRST_DOSE_TO_DEATH);
        softly.assertThat(result.getDaysFromFirstDoseToDeath().getTo()).isEqualTo(DAYS_FROM_FIRST_DOSE_TO_DEATH);
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }
}
