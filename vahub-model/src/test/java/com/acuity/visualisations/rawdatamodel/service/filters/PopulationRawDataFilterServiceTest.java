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

import com.acuity.visualisations.rawdatamodel.filters.MapFilter;
import com.acuity.visualisations.rawdatamodel.filters.MultiValueSetFilter;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID_42;
import static com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProviderTest.SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Maps.newHashMap;

public class PopulationRawDataFilterServiceTest {

    private PopulationRawDataFilterService filterService = new PopulationRawDataFilterService();

    private static List<Subject> population;

    static {
        Subject subject1 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_VA_ID_42)).subjectCode("subject1").subjectId("subjectId1").age(30)
                .attendedVisitNumbers(newArrayList("1", "3", "7")).randomised("Yes").dateOfRandomisation(toDate("2000-01-01")).country("China").build();
        Subject subject2 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_VA_ID_42)).subjectCode("subject2").subjectId("subjectId2").age(60)
                .attendedVisitNumbers(newArrayList("1", "2", "8", "9")).randomised("Yes").dateOfRandomisation(toDate("2000-02-01")).country("Peru").build();
        Subject subject3 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_VA_ID_42)).subjectCode("subject3").subjectId("subjectId3").age(90)
                .attendedVisitNumbers(newArrayList("2", "4", "10")).randomised("No").dateOfRandomisation(toDate("2000-03-01")).country("Australia").build();
        Subject subject4 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_VA_ID_42)).subjectCode("subject4").subjectId("subjectId4").age(60)
                .attendedVisitNumbers(newArrayList("1", "2", "3", "5", null)).randomised("Yes").dateOfRandomisation(toDate("2000-04-01")).country("Japan")
                .build();
        Subject subject5 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_VA_ID_42)).subjectCode("subject5").subjectId("subjectId5").age(45)
                .attendedVisitNumbers(newArrayList()).dateOfRandomisation(toDate("2000-05-01")).country("Poland").build();

        population = newArrayList(subject1, subject2, subject3, subject4, subject5);
    }

    @Test
    public void testQueryEmptyFilters() throws IOException {
        //Given
        FilterQuery<Subject> filterQuery = new FilterQuery<>(SUBJECTS, PopulationFilters.empty());

        //When
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        //Then
        assertThat(filtered.getAllEvents()).containsExactly(SUBJECTS.get(0));
        assertThat(filtered.getFilteredResult()).containsExactly(SUBJECTS.get(0));
    }

    @Test
    public void testQueryAgeFilter() {
        PopulationFilters filters = new PopulationFilters();
        filters.getAge().setFrom(35);
        filters.getAge().setTo(70);
        FilterQuery<Subject> filterQuery = new FilterQuery<>(population, filters);
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(population.get(1), population.get(3), population.get(4));
    }

    @Test
    public void testQueryCountryFilter() {
        PopulationFilters filters = new PopulationFilters();
        filters.setCountry(new SetFilter<>(newHashSet("China", "Australia")));
        FilterQuery<Subject> filterQuery = new FilterQuery<>(population, filters);
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(population.get(0), population.get(2));
    }

    @Test
    public void testGetSubjectIds() throws IOException {
        //Given
        FilterQuery<Subject> filterQuery = new FilterQuery<>(SUBJECTS, PopulationFilters.empty());

        //When
        Collection<String> subjectIds = filterService.getPopulationFilterResult(filterQuery).getFilteredResult()
                .parallelStream().map(Subject::getSubjectId).collect(toSet());

        //Then
        assertThat(subjectIds).containsExactly(SUBJECTS.get(0).getSubjectId());
    }

    @Test
    public void testQueryDrugDosedFilter() {
        Map<String, String> drugsDosed1 = new HashMap<>();
        drugsDosed1.put("DRUG002", "No");
        drugsDosed1.put("DRUG001", "Yes");
        Map<String, String> drugsDosed2 = new HashMap<>();
        drugsDosed2.put("DRUG002", "Yes");
        drugsDosed2.put("DRUG001", "No");
        Map<String, String> drugsDosed3 = new HashMap<>();
        drugsDosed3.put("DRUG002", "Yes");
        drugsDosed3.put("DRUG001", "Yes");

        Subject subject1 = getSubjectBuilder().subjectId("sid1").drugsDosed(drugsDosed1).build();
        Subject subject2 = getSubjectBuilder().subjectId("sid2").drugsDosed(drugsDosed2).build();
        Subject subject3 = getSubjectBuilder().subjectId("sid3").drugsDosed(drugsDosed3).build();
        List<Subject> population = newArrayList(subject1, subject2, subject3);

        Map<String, SetFilter<String>> innerFilters = new HashMap<>();
        innerFilters.put("DRUG001", new SetFilter<>(newArrayList("Yes"), false));
        innerFilters.put("DRUG002", new SetFilter<>(newArrayList("Yes"), false));

        PopulationFilters filters = new PopulationFilters();
        filters.getDrugsDosed().setMap(innerFilters);
        FilterQuery<Subject> filterQuery = new FilterQuery<>(population, filters);
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(population.get(2));
    }

    @Test
    public void testQueryDrugsDiscontinuedFilter() {
        Map<String, String> drugsDiscontinued1 = new HashMap<>();
        drugsDiscontinued1.put("DRUG002", "No");
        drugsDiscontinued1.put("DRUG001", "Yes");
        Map<String, String> drugsDiscontinued2 = new HashMap<>();
        drugsDiscontinued2.put("DRUG002", "Yes");
        drugsDiscontinued2.put("DRUG001", "No");
        Map<String, String> drugsDiscontinued3 = new HashMap<>();
        drugsDiscontinued3.put("DRUG002", "Yes");
        drugsDiscontinued3.put("DRUG001", "Yes");

        Subject subject1 = getSubjectBuilder().subjectId("sid1").drugsDiscontinued(drugsDiscontinued1).build();
        Subject subject2 = getSubjectBuilder().subjectId("sid2").drugsDiscontinued(drugsDiscontinued2).build();
        Subject subject3 = getSubjectBuilder().subjectId("sid3").drugsDiscontinued(drugsDiscontinued3).build();
        Subject subject4 = getSubjectBuilder().subjectId("sid4").build();
        List<Subject> population = newArrayList(subject1, subject2, subject3, subject4);

        Map<String, SetFilter<String>> innerFilters = new HashMap<>();
        innerFilters.put("DRUG001", new SetFilter<>(newArrayList("Yes"), false));
        innerFilters.put("DRUG002", new SetFilter<>(newArrayList("Yes"), false));

        PopulationFilters filters = new PopulationFilters();
        filters.setDrugsDiscontinued(new MapFilter<>(innerFilters, SetFilter.class));
        FilterQuery<Subject> filterQuery = new FilterQuery<>(population, filters);
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(population.get(2));
    }

    @Test
    public void testQueryDrugsMaxDosesFilter() {
        Map<String, String> drugMaxDoses1 = new HashMap<>();
        drugMaxDoses1.put("DRUG002", "50 mg");
        drugMaxDoses1.put("DRUG001", "10 mg");
        Map<String, String> drugMaxDoses2 = new HashMap<>();
        drugMaxDoses2.put("DRUG002", "20 mg");
        drugMaxDoses2.put("DRUG001", "20 mg");

        Subject subject1 = getSubjectBuilder().subjectId("sid1").drugsMaxDoses(drugMaxDoses1).build();
        Subject subject2 = getSubjectBuilder().subjectId("sid2").drugsMaxDoses(drugMaxDoses2).build();
        Subject subject3 = getSubjectBuilder().subjectId("sid3").drugsMaxDoses(newHashMap("DRUG001", "10 mg")).build();
        Subject subject4 = getSubjectBuilder().subjectId("sid4").build();
        List<Subject> population = newArrayList(subject1, subject2, subject3, subject4);

        Map<String, SetFilter<String>> innerFilters = new HashMap<>();
        innerFilters.put("DRUG001", new SetFilter<>(newArrayList("10 mg"), false));
        innerFilters.put("DRUG002", new SetFilter<>(newArrayList("50 mg"), false));

        PopulationFilters filters = new PopulationFilters();
        filters.setDrugsMaxDoses(new MapFilter<String, SetFilter<String>>(innerFilters, SetFilter.class));
        FilterQuery<Subject> filterQuery = new FilterQuery<>(population, filters);
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(population.get(0));
    }

    @Test
    public void testQueryDrugsMaxDosesIncludeEmptyValuesFilter() {
        Map<String, String> drugMaxDoses1 = new HashMap<>();
        drugMaxDoses1.put("DRUG002", "50 mg");
        drugMaxDoses1.put("DRUG001", "10 mg");
        Map<String, String> drugMaxDoses2 = new HashMap<>();
        drugMaxDoses2.put("DRUG002", "20 mg");
        drugMaxDoses2.put("DRUG001", "20 mg");

        Subject subject1 = getSubjectBuilder().subjectId("sid1").drugsMaxDoses(drugMaxDoses1).build();
        Subject subject2 = getSubjectBuilder().subjectId("sid2").drugsMaxDoses(drugMaxDoses2).build();
        Subject subject3 = getSubjectBuilder().subjectId("sid3").drugsMaxDoses(newHashMap("DRUG001", "10 mg")).build();
        Subject subject4 = getSubjectBuilder().subjectId("sid4").build();

        List<Subject> population = newArrayList(subject1, subject2, subject3, subject4);

        Map<String, SetFilter<String>> innerFilters = new HashMap<>();
        innerFilters.put("DRUG001", new SetFilter<>(newArrayList("10 mg"), false));
        innerFilters.put("DRUG002", new SetFilter<>(newArrayList("50 mg"), true));

        PopulationFilters filters = new PopulationFilters();
        filters.setDrugsMaxDoses(new MapFilter<>(innerFilters, SetFilter.class));
        FilterQuery<Subject> filterQuery = new FilterQuery<>(population, filters);
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(population.get(0), population.get(2));
    }

    @Test
    public void testQueryDrugsMaxFrequenciesFilter() {
        Map<String, String> drugsMaxFrequencies1 = new HashMap<>();
        drugsMaxFrequencies1.put("DRUG002", "freq 4");
        drugsMaxFrequencies1.put("DRUG001", "freq 1");
        Map<String, String> drugsMaxFrequencies2 = new HashMap<>();
        drugsMaxFrequencies2.put("DRUG002", "freq 5");
        drugsMaxFrequencies2.put("DRUG001", "freq 2");

        Subject subject1 = getSubjectBuilder().subjectId("sid1").drugsMaxFrequencies(drugsMaxFrequencies1).build();
        Subject subject2 = getSubjectBuilder().subjectId("sid2").drugsMaxFrequencies(drugsMaxFrequencies2).build();
        Subject subject3 = getSubjectBuilder().subjectId("sid3").drugsMaxFrequencies(newHashMap("DRUG001", "freq 3")).build();
        Subject subject4 = getSubjectBuilder().subjectId("sid4").build();
        List<Subject> population = newArrayList(subject1, subject2, subject3, subject4);

        Map<String, SetFilter<String>> innerFilters = new HashMap<>();
        innerFilters.put("DRUG001", new SetFilter<>(newArrayList("freq 1", "freq 2"), false));
        innerFilters.put("DRUG002", new SetFilter<>(newArrayList("freq 5"), false));

        PopulationFilters filters = new PopulationFilters();
        filters.setDrugsMaxFrequencies(new MapFilter<>(innerFilters, SetFilter.class));
        FilterQuery<Subject> filterQuery = new FilterQuery<>(population, filters);
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(population.get(1));
    }

    @Test
    public void testQueryAttendetVisitsFilter() {
        PopulationFilters filters = new PopulationFilters();
        filters.setAttendedVisits(new MultiValueSetFilter(newHashSet("1", "3")));
        FilterQuery<Subject> filterQuery = new FilterQuery<>(population, filters);
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactlyInAnyOrder(population.get(0), population.get(1), population.get(3));
    }

    @Ignore("doesnt work with nulls")
    @Test
    public void testQueryAttendetVisitsIncludeEmptyValuesFilter() {
        PopulationFilters filters = new PopulationFilters();
        filters.setAttendedVisits(new MultiValueSetFilter(newArrayList("8", null)));
        FilterQuery<Subject> filterQuery = new FilterQuery<>(population, filters);
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactlyInAnyOrder(population.get(1), population.get(3), population.get(4));
    }

    @Test
    public void testRandomizedFilter() {
        PopulationFilters filters = new PopulationFilters();
        filters.setRandomised(new SetFilter<>(newArrayList("Yes")));
        FilterQuery<Subject> filterQuery = new FilterQuery<>(population, filters);
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactlyInAnyOrder(population.get(0), population.get(1), population.get(3));
    }

    @Test
    public void testRangeFilterWithNullFrom() {
        // Given
        PopulationFilters filters = new PopulationFilters();
        filters.getAge().setFrom(null);
        filters.getAge().setTo(70);
        FilterQuery<Subject> filterQuery = new FilterQuery<>(population, filters);

        // When
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        // Then
        assertThat(filtered.getFilteredResult()).containsExactly(population.get(0), population.get(1), population.get(3), population.get(4));
    }

    @Test
    public void testRangeFilterWithNullTo() {
        // Given
        PopulationFilters filters = new PopulationFilters();
        filters.getAge().setFrom(60);
        filters.getAge().setTo(null);
        FilterQuery<Subject> filterQuery = new FilterQuery<>(population, filters);

        // When
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        // Then
        assertThat(filtered.getFilteredResult()).containsExactly(population.get(1), population.get(2), population.get(3));
    }

    @Test
    public void testDateRangeFilterWithNullFrom() {
        // Given
        PopulationFilters filters = new PopulationFilters();
        filters.getRandomisationDate().setFrom(null);
        filters.getRandomisationDate().setTo(toDate("2000-03-01"));
        FilterQuery<Subject> filterQuery = new FilterQuery<>(population, filters);

        // When
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        // Then
        assertThat(filtered.getFilteredResult()).containsExactly(population.get(0), population.get(1), population.get(2));
    }

    @Test
    public void testDateRangeFilterWithNullTo() {
        // Given
        PopulationFilters filters = new PopulationFilters();
        filters.getRandomisationDate().setFrom(toDate("2000-03-01"));
        filters.getRandomisationDate().setTo(null);
        FilterQuery<Subject> filterQuery = new FilterQuery<>(population, filters);

        // When
        FilterResult<Subject> filtered = filterService.query(filterQuery);

        // Then
        assertThat(filtered.getFilteredResult()).containsExactly(population.get(2), population.get(3), population.get(4));
    }

    @Test
    public void testGetEmptyWithDoses() {
        // Given
        PopulationFilters filters = new PopulationFilters();

        MapFilter<String, SetFilter<String>> doseDosesFilter = new MapFilter<>();
        Map<String, SetFilter<String>> map = new HashMap<>();
        map.put("SuperDex 10 mg", new SetFilter<>(newArrayList("No")));
        map.put("SuperDex 20 mg", new SetFilter<>(newArrayList("No")));
        doseDosesFilter.setMap(map);
        filters.setDrugsDosed(doseDosesFilter);

        // Then
        assertThat(filters.getEmptyFilterNames()).contains("drugsDosed");
    }

    @Test
    public void testGetEmptyWithDoseCohort() {
        // Given
        PopulationFilters filters = new PopulationFilters();

        SetFilter<String> doseCohortFilter = new SetFilter<>();
        doseCohortFilter.setSortedValues(newArrayList("Default group", "Default group"));
        filters.setDoseCohort(doseCohortFilter);

        SetFilter<String> otherCohortFilter = new SetFilter<>();
        otherCohortFilter.setSortedValues(newArrayList("Default group", "Default group"));
        filters.setOtherCohort(otherCohortFilter);

        // Then
        assertThat(filters.getEmptyFilterNames()).contains("doseCohort", "otherCohort");
    }

    @Test
    public void testNotGetEmptyWithDoses() {
        // Given
        PopulationFilters filters = new PopulationFilters();

        MapFilter<String, SetFilter<String>> doseDosesFilter = new MapFilter<>();
        Map<String, SetFilter<String>> map = new HashMap<>();
        map.put("SuperDex 10 mg", new SetFilter<>(newArrayList("Yes")));
        map.put("SuperDex 20 mg", new SetFilter<>(newArrayList("No")));
        doseDosesFilter.setMap(map);
        filters.setDrugsDosed(doseDosesFilter);

        // Then
        assertThat(filters.getEmptyFilterNames()).doesNotContain("drugsDosed");
    }

    private Subject.SubjectBuilder getSubjectBuilder() {
        return Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_VA_ID_42));
    }

}
