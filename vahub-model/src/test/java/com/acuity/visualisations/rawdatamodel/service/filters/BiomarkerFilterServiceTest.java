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

import com.acuity.visualisations.rawdatamodel.filters.BiomarkerFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID_42;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class BiomarkerFilterServiceTest {

    public static final List<Biomarker> BIOMARKER_LIST;
    public static final int PROFILE_BITMASK = 3093; // 0th, 2d, 4th, 10th, 11th bits;

    private static List<Biomarker> generateBiomarkerstList() {
        Biomarker biomarker1 = new Biomarker(BiomarkerRaw.builder()
                .id("bm1")
                .subjectId("subjectId1")
                .gene("ATR")
                .mutation("Amplification")
                .somaticStatus("known")
                .profilesMask(PROFILE_BITMASK)
                .build(), Subject.builder().subjectCode("subjectId1").subjectId("subjectId1").build());
        Biomarker biomarker2 = new Biomarker(BiomarkerRaw.builder()
                .id("bm2")
                .subjectId("subjectId2")
                .gene("BCOR")
                .somaticStatus("likely")
                .mutation("Fusion")
                .profilesMask(PROFILE_BITMASK)
                .build(), Subject.builder().subjectCode("subjectId2").subjectId("subjectId2").build());
        return newArrayList(biomarker1, biomarker2);
    }
    public static final List<Biomarker> BIOMARKERS = generateBiomarkerstList();

    static {
        Biomarker bm1 = new Biomarker(BiomarkerRaw.builder()
                .id("bm1").subjectId("subjectId1").gene("gene1").mutation("mutation1").profilesMask(PROFILE_BITMASK).build(),
                Subject.builder().subjectId("subjectId2").clinicalStudyCode(String.valueOf(DUMMY_ACUITY_VA_ID_42)).subjectCode("subject1").build());
        Biomarker bm2 = new Biomarker(BiomarkerRaw.builder()
                .id("bm2").subjectId("subjectId1").gene("gene2").mutation("mutation2").profilesMask(PROFILE_BITMASK).build(),
                Subject.builder().subjectId("subjectId2").clinicalStudyCode(String.valueOf(DUMMY_ACUITY_VA_ID_42)).subjectCode("subject1").build());
        Biomarker bm3 = new Biomarker(BiomarkerRaw.builder()
                .id("bm3").subjectId("subjectId1").gene("gene3").mutation("mutation3").profilesMask(PROFILE_BITMASK).build(),
                Subject.builder().subjectId("subjectId1").clinicalStudyCode(String.valueOf(DUMMY_ACUITY_VA_ID_42)).subjectCode("subject1").build());
        Biomarker bm4 = new Biomarker(BiomarkerRaw.builder()
                .id("bm4").subjectId("subjectId2").gene("gene4").mutation("mutation4").profilesMask(PROFILE_BITMASK).build(),
                Subject.builder().subjectId("subjectId2").clinicalStudyCode(String.valueOf(DUMMY_ACUITY_VA_ID_42)).subjectCode("subject2").build());
        Biomarker bm5 = new Biomarker(BiomarkerRaw.builder()
                .id("bm5").subjectId("subjectId2").gene("gene5").mutation("mutation5").profilesMask(PROFILE_BITMASK).build(),
                Subject.builder().subjectId("subjectId2").clinicalStudyCode(String.valueOf(DUMMY_ACUITY_VA_ID_42)).subjectCode("subject2").build());
        Biomarker bm6 = new Biomarker(BiomarkerRaw.builder()
                .id("bm6").subjectId("subjectId2").gene("gene1").mutation("mutation5").profilesMask(PROFILE_BITMASK).build(),
                Subject.builder().subjectId("subjectId2").clinicalStudyCode(String.valueOf(DUMMY_ACUITY_VA_ID_42)).subjectCode("subject2").build());
        Biomarker bm7 = new Biomarker(BiomarkerRaw.builder()
                .id("bm7").subjectId("subjectId3").gene("gene1").mutation("mutation3").profilesMask(PROFILE_BITMASK).build(),
                Subject.builder().subjectId("subjectId3").clinicalStudyCode(String.valueOf(DUMMY_ACUITY_VA_ID_42)).subjectCode("subject3").build());
        Biomarker bm8 = new Biomarker(BiomarkerRaw.builder()
                .id("bm8").subjectId("subjectId4").gene("gene3").mutation("mutation2").profilesMask(PROFILE_BITMASK).build(),
                Subject.builder().subjectId("subjectId4").clinicalStudyCode(String.valueOf(DUMMY_ACUITY_VA_ID_42)).subjectCode("subject4").build());


        BIOMARKER_LIST = newArrayList(bm1, bm2, bm3, bm4, bm5, bm6, bm7, bm8);
    }

    private static List<Subject> population = newArrayList(Subject.builder().subjectId("subjectId1").build(),
            Subject.builder().subjectId("subjectId2").build());

    @Mock
    private PopulationRawDataFilterService populationFilterService;

    @InjectMocks
    private BiomarkerFilterService filterService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testQueryEmptyFilters() throws IOException {

        FilterQuery<Biomarker> filterQuery = new FilterQuery<>(BIOMARKERS, BiomarkerFilters.empty(),
                population, PopulationFilters.empty());

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        FilterResult<Biomarker> filtered = filterService.query(filterQuery);

        assertThat(filtered.getAllEvents()).containsExactly(BIOMARKERS.get(0), BIOMARKERS.get(1));
        assertThat(filtered.getFilteredResult()).containsExactly(BIOMARKERS.get(0), BIOMARKERS.get(1));
    }

    @Test
    public void testQueryEmptyFiltersWithFilteredPopulation() throws IOException {

        FilterQuery<Biomarker> filterQuery = new FilterQuery<>(BIOMARKERS, BiomarkerFilters.empty(),
                population, PopulationFilters.empty());

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, newArrayList(Subject.builder().subjectId("subjectId2").build()));

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        FilterResult<Biomarker> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(BIOMARKERS.get(1));
    }

    @Test
    public void testQueryEmptyFiltersWithEmptyPopulation() throws IOException {
        Collection<Subject> population = new ArrayList<>();
        FilterQuery<Biomarker> filterQuery = new FilterQuery<>(BIOMARKERS, BiomarkerFilters.empty(),
                population, PopulationFilters.empty());

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        FilterResult<Biomarker> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).isEmpty();
    }

    @Test
    public void testQueryGeneFilter() {

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        BiomarkerFilters filters = new BiomarkerFilters();
        filters.setGene(new SetFilter<>(newHashSet("gene2", "gene4")));
        FilterQuery<Biomarker> filterQuery = new FilterQuery<>(BIOMARKER_LIST, filters, new ArrayList<>(),
                PopulationFilters.empty());

        FilterResult<Biomarker> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(BIOMARKER_LIST.get(1), BIOMARKER_LIST.get(3));
    }

    @Test
    public void testQueryMutationFilter() {

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        BiomarkerFilters filters = new BiomarkerFilters();
        filters.setMutation(new SetFilter<>(newHashSet("mutation2", "mutation4")));
        FilterQuery<Biomarker> filterQuery = new FilterQuery<>(BIOMARKER_LIST, filters,  new ArrayList<>(),
                PopulationFilters.empty());

        FilterResult<Biomarker> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(BIOMARKER_LIST.get(1), BIOMARKER_LIST.get(3));
    }
}
