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

import com.acuity.visualisations.rawdatamodel.filters.AssessedTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.acuity.visualisations.rawdatamodel.util.Constants.NO;
import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class AssessedTargetLesionFilterServiceTest {

    private static List<AssessedTargetLesion> atls;
    private static List<Subject> population;

    private static Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1").firstTreatmentDate(toDate("2000-04-05"))
            .lastTreatmentDate(toDate("2000-05-20")).build();
    private static Subject subject2 = Subject.builder().subjectId("subjectId2").subjectCode("subject2").firstTreatmentDate(toDate("2000-01-10"))
            .lastTreatmentDate(toDate("2000-05-21")).build();

    private static Date visit1Date =  DaysUtil.toDate("2000-05-15");
    private static Date visit2Date =  DaysUtil.toDate("2000-05-25");

    static {       
        AssessedTargetLesion t1 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id1")
                .targetLesionRaw(TargetLesionRaw.builder().visitNumber(1).lesionDate(visit1Date)
                        .sumBestPercentageChangeFromBaseline(20.)
                .subjectId(subject1.getId()).visitDate(visit1Date).build())
                .bestResponse("Complete Response").nonTargetLesionsPresent(NO).subjectId(subject1.getId()).build(),
                subject1);
        AssessedTargetLesion t2 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id2")
                .targetLesionRaw(TargetLesionRaw.builder().visitNumber(2).lesionDate(visit2Date)
                        .sumBestPercentageChangeFromBaseline(-10.)
                        .subjectId(subject1.getId()).visitDate(visit2Date).build())
                .bestResponse("Missing target lesions").nonTargetLesionsPresent(NO).subjectId(subject1.getId()).build(),
                subject1);
        AssessedTargetLesion t3 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id3")
                .targetLesionRaw(TargetLesionRaw.builder().visitNumber(1).lesionDate(visit1Date)
                        .sumBestPercentageChangeFromBaseline(10.)
                        .subjectId(subject2.getId()).visitDate(visit1Date).build())
                .subjectId(subject2.getId()).bestResponse("Partial Response").nonTargetLesionsPresent(NO).build(),
                subject2);
        AssessedTargetLesion t4 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id4")
                .targetLesionRaw(TargetLesionRaw.builder().visitNumber(1).lesionDate(visit2Date)
                        .sumBestPercentageChangeFromBaseline(18.)
                        .subjectId(subject2.getId()).visitDate(visit2Date).build())
                .subjectId(subject2.getId()).bestResponse("Complete Response").nonTargetLesionsPresent(YES).build(),
                subject2);

        atls = newArrayList(t1, t2, t3, t4);
        population = newArrayList(subject1, subject2);
    }

    @Mock
    private PopulationRawDataFilterService populationFilterService;

    @InjectMocks
    private AssessedTargetLesionFilterService filterService;

    @Rule
    public JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        setUpPopulationFilterResult();
    }

    @Test
    public void testQueryEmptyFilters() throws IOException {

        FilterQuery<AssessedTargetLesion> filterQuery = new FilterQuery<>(atls, AssessedTargetLesionFilters.empty(),
                population, PopulationFilters.empty());

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        FilterResult<AssessedTargetLesion> filtered = filterService.query(filterQuery);

        softly.assertThat(filtered.getAllEvents()).containsExactlyElementsOf(atls);
        softly.assertThat(filtered.getFilteredResult()).containsExactlyElementsOf(atls);
    }

    @Test
    public void testQueryEmptyFiltersWithFilteredPopulation() throws IOException {

        FilterQuery<AssessedTargetLesion> filterQuery = new FilterQuery<>(atls, AssessedTargetLesionFilters.empty(),
                population, PopulationFilters.empty());

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, newArrayList(Subject.builder().subjectId("subjectId2").build()));

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        FilterResult<AssessedTargetLesion> filtered = filterService.query(filterQuery);

        softly.assertThat(filtered.getFilteredResult()).containsExactly(atls.get(2), atls.get(3));
    }

    @Test
    public void testQueryEmptyFiltersWithEmptyPopulation() throws IOException {

        FilterQuery<AssessedTargetLesion> filterQuery = new FilterQuery<>(atls, AssessedTargetLesionFilters.empty(),
                population, PopulationFilters.empty());

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, newArrayList());

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        FilterResult<AssessedTargetLesion> filtered = filterService.query(filterQuery);

        softly.assertThat(filtered.getFilteredResult()).isEmpty();
    }

    @Test
    public void testQueryBestPercentageChangeFromBaselineFilter() {

        AssessedTargetLesionFilters filters = new AssessedTargetLesionFilters();
        filters.setBestPercentageChangeFromBaseline(new RangeFilter<>(15.0, 30.0));
        FilterResult<AssessedTargetLesion> filtered = applyFilters(filters);

        softly.assertThat(filtered.getFilteredResult()).containsExactly(atls.get(0), atls.get(3));
    }

    @Test
    public void testQueryBestResponseFilter() {

        AssessedTargetLesionFilters filters = new AssessedTargetLesionFilters();
        filters.setBestResponse(new SetFilter<>(newArrayList("Missing target lesions")));
        FilterResult<AssessedTargetLesion> filtered = applyFilters(filters);

        softly.assertThat(filtered.getFilteredResult()).containsExactly(atls.get(1));
    }

    @Test
    public void testQueryNonTargetLesionsPresentFilter() {

        AssessedTargetLesionFilters filters = new AssessedTargetLesionFilters();
        filters.setNonTargetLesionsPresent(new SetFilter<>(newArrayList(YES)));
        FilterResult<AssessedTargetLesion> filtered = applyFilters(filters);

        softly.assertThat(filtered.getFilteredResult()).containsExactly(atls.get(3));
    }

    @Test
    public void testGetAvailableFilters() {
        AssessedTargetLesionFilters result = (AssessedTargetLesionFilters) filterService.getAvailableFilters(atls,
                AssessedTargetLesionFilters.empty(), population, PopulationFilters.empty());
        softly.assertThat(result.getBestPercentageChangeFromBaseline().getFrom()).isEqualTo(-10.);
        softly.assertThat(result.getBestPercentageChangeFromBaseline().getTo()).isEqualTo(20.);
        softly.assertThat(result.getBestResponse().getSortedValues()).containsExactly("Complete Response",
                "Missing target lesions", "Partial Response");
        softly.assertThat(result.getNonTargetLesionsPresent().getSortedValues())
                .containsExactly(NO, YES);
        softly.assertThat(result.getNonTargetLesionsPresent().getIncludeEmptyValues()).isFalse();
    }

    private void setUpPopulationFilterResult() {

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);
    }

    private FilterResult<AssessedTargetLesion> applyFilters(AssessedTargetLesionFilters filters) {

        Collection<Subject> population = new ArrayList<>();
        FilterQuery<AssessedTargetLesion> filterQuery = new FilterQuery<>(atls, filters, population,
                PopulationFilters.empty());
        return filterService.query(filterQuery);
    }
}
