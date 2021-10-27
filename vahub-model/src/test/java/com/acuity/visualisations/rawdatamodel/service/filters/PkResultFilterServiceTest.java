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

import com.acuity.visualisations.rawdatamodel.filters.PkResultFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.PkResultRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class PkResultFilterServiceTest {

    @Autowired
    private PkResultFilterService pkResultFilterService;

    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();
    private static final List<Subject> POPULATION = Arrays.asList(
            Subject.builder().subjectId("subject1").build(),
            Subject.builder().subjectId("subject2").build(),
            Subject.builder().subjectId("subject3").build(),
            Subject.builder().subjectId("subject4").build()
    );
    private static final List<PkResult> PKRESULTS = generatePkResultList();


    @Test
    public void testGetFiltersWithFilteredAnalyte() {
        PkResultFilters result = givenFilterSetup(filters ->
                filters.setAnalyte(new SetFilter<>(new HashSet<>(Arrays.asList("ONE", "TWO")))));
        softly.assertThat(result.getAnalyte().getValues()).containsOnly("ONE", "TWO");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(2);
    }

    @Test
    public void testGetFiltersWithFilteredAnalyteIncludingEmpty() {
        PkResultFilters result = givenFilterSetup(filters ->
                filters.setAnalyte(new SetFilter<>(new HashSet<>(Arrays.asList("ONE", null)))));
        softly.assertThat(result.getAnalyte().getValues()).containsOnly("ONE");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void testGetAvailableFilters() {
        PkResultFilters result = givenFilterSetup(filters ->
                filters.setAnalyte(new SetFilter<>()));
        softly.assertThat(result.getAnalyte().getValues()).containsOnly("ONE", "TWO", "THREE", "FOUR");
        softly.assertThat(result.getMatchedItemsCount()).isEqualTo(4);
    }

    @Test
    public void testQueryEmptyFilters() {
        FilterQuery<PkResult> filterQuery = new FilterQuery<>(
                PKRESULTS, PkResultFilters.empty(),
                POPULATION, PopulationFilters.empty()
        );
        FilterResult<PkResult> filterResult = pkResultFilterService.query(filterQuery);
        softly.assertThat(filterResult.getAllEvents()).size().isEqualTo(PKRESULTS.size());
        softly.assertThat(filterResult.getFilteredResult()).size().isEqualTo(PKRESULTS.size());
    }

    @Test
    public void testQueryEmptyFiltersWithFilteredPopulation() {
        List<Subject> filteredPopulations = POPULATION.stream()
                .filter(subj -> "subject1".equals(subj.getSubjectId()))
                .collect(Collectors.toList());
        List<PkResult> filteredEvents = PKRESULTS.stream()
                .filter(pkResult -> "subject1".equals(pkResult.getSubjectId()))
                .collect(toList());
        FilterQuery<PkResult> filterQuery = new FilterQuery<>(
                PKRESULTS, PkResultFilters.empty(),
                filteredPopulations, PopulationFilters.empty()
        );
        FilterResult<PkResult> filterResult = pkResultFilterService.query(filterQuery);
        softly.assertThat(filterResult.getAllEvents()).size().isEqualTo(PKRESULTS.size());
        softly.assertThat(filterResult.getFilteredResult()).size().isEqualTo(filteredEvents.size());
        softly.assertThat(filterResult.getFilteredResult()).containsExactlyElementsOf(filteredEvents);
    }

    @Test
    public void testQueryEmptyFiltersWithEmptyPopulation() {
        FilterQuery<PkResult> filterQuery = new FilterQuery<>(
                PKRESULTS, PkResultFilters.empty(),
                Collections.emptyList(), PopulationFilters.empty()
        );
        FilterResult<PkResult> filterResult = pkResultFilterService.query(filterQuery);
        softly.assertThat(filterResult.getAllEvents()).size().isEqualTo(PKRESULTS.size());
        softly.assertThat(filterResult.getFilteredResult()).size().isEqualTo(0);
    }

    @Test
    public void testQueryFiltersWithFilteredGene() {
        PkResultFilters filters = new PkResultFilters();
        filters.setAnalyte(new SetFilter<>(new HashSet<>(Arrays.asList("ONE", "THREE"))));
        FilterQuery<PkResult> filterQuery = new FilterQuery<>(
                PKRESULTS, filters,
                POPULATION, PopulationFilters.empty()
        );
        FilterResult<PkResult> filterResult = pkResultFilterService.query(filterQuery);
        softly.assertThat(filterResult.getAllEvents()).size().isEqualTo(PKRESULTS.size());
        softly.assertThat(filterResult.getFilteredResult()).size().isEqualTo(2);
        List<PkResult> filteredEvents = PKRESULTS.stream()
                .filter(pkResult -> "ONE".equals(pkResult.getEvent().getAnalyte())
                        || "THREE".equals(pkResult.getEvent().getAnalyte()))
                .collect(toList());
        softly.assertThat(filterResult.getFilteredResult()).containsExactlyElementsOf(filteredEvents);
    }

    private static List<PkResult> generatePkResultList() {
        Subject subject1 = POPULATION.get(0);
        String subjId1 = subject1.getSubjectId();
        Subject subject2 = POPULATION.get(1);
        String subjId2 = subject2.getSubjectId();
        Subject subject3 = POPULATION.get(2);
        String subjId3 = subject3.getSubjectId();
        Subject subject4 = POPULATION.get(3);
        String subjId4 = subject4.getSubjectId();

        PkResult pkResult1 = new PkResult(
                PkResultRaw.builder()
                        .subjectId(subjId1)
                        .analyte("ONE")
                        .build(),
                subject1
        );
        PkResult pkResult2 = new PkResult(
                PkResultRaw.builder()
                        .subjectId(subjId2)
                        .analyte("TWO")
                        .build(),
                subject2
        );
        PkResult pkResult3 = new PkResult(
                PkResultRaw.builder()
                        .subjectId(subjId3)
                        .analyte("THREE")
                        .build(),
                subject3
        );
        PkResult pkResult4 = new PkResult(
                PkResultRaw.builder()
                        .subjectId(subjId4)
                        .analyte("FOUR")
                        .build(),
                subject4
        );

        return Arrays.asList(pkResult1, pkResult2, pkResult3, pkResult4);
    }

    private PkResultFilters givenFilterSetup(final Consumer<PkResultFilters> filterSetter) {
        List<Subject> subjects = PKRESULTS.stream().map(PkResult::getSubject).collect(toList());
        PkResultFilters pkResultFilters = new PkResultFilters();
        filterSetter.accept(pkResultFilters);
        return (PkResultFilters) pkResultFilterService.getAvailableFilters(PKRESULTS, pkResultFilters,
                subjects, PopulationFilters.empty());
    }

}
