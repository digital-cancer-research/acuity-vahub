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

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RadiotherapyFilters;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.RadiotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class RadiotherapyFilterServiceTest {

    Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1").firstTreatmentDate(DaysUtil.toDate("2000-04-05"))
            .lastTreatmentDate(DaysUtil.toDate("2000-05-20")).build();
    Subject subject2 = Subject.builder().subjectId("subjectId2").subjectCode("subject2").firstTreatmentDate(DaysUtil.toDate("2000-01-10"))
            .lastTreatmentDate(DaysUtil.toDate("2000-05-21")).build();
    Subject subject3 = Subject.builder().subjectId("subjectId3").subjectCode("subject3").firstTreatmentDate(DaysUtil.toDate("2000-02-21"))
            .lastTreatmentDate(DaysUtil.toDate("2000-05-21")).build();

    List<Subject> population = Arrays.asList(subject1, subject2, subject3);

    private List<Radiotherapy> radioData = Arrays.asList(new Radiotherapy(RadiotherapyRaw.builder().subjectId(subject1.getSubjectId())
                    .startDate(DaysUtil.toDate("2000-01-01")).endDate(DaysUtil.toDate("2000-03-20"))
                    .dose(10.2).numOfDoses(5).treatmentStatus("status 2").build(), subject1),
            new Radiotherapy(RadiotherapyRaw.builder().subjectId(subject2.getSubjectId())
                    .endDate(DaysUtil.toDate("1999-12-01"))
                    .dose(2.5).numOfDoses(6).treatmentStatus("status 3")
                    .build(), subject2),
            new Radiotherapy(RadiotherapyRaw.builder().subjectId(subject2.getSubjectId())
                    .startDate(DaysUtil.toDate("2000-01-05")).endDate(DaysUtil.toDate("2000-01-09"))
                    .treatmentStatus("status 4").build(), subject2));

    @Mock
    private PopulationRawDataFilterService populationFilterService;

    @InjectMocks
    private RadiotherapyFilterService filterService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

//    @Test
//    public void testQueryEmptyFilters() throws IOException {
//
//        FilterQuery<Biomarker> filterQuery = new FilterQuery<>(BIOMARKERS, BiomarkerFilters.empty(),
//                population, PopulationFilters.empty());
//
//        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
//        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
//        filteredPopulation.withResults(population, population);
//
//        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);
//
//        FilterResult<Biomarker> filtered = filterService.query(filterQuery);
//
//        assertThat(filtered.getAllEvents()).containsExactly(BIOMARKERS.get(0), BIOMARKERS.get(1));
//        assertThat(filtered.getFilteredResult()).containsExactly(BIOMARKERS.get(0), BIOMARKERS.get(1));
//    }
//
//    @Test
//    public void testQueryEmptyFiltersWithFilteredPopulation() throws IOException {
//
//        FilterQuery<Biomarker> filterQuery = new FilterQuery<>(BIOMARKERS, BiomarkerFilters.empty(),
//                population, PopulationFilters.empty());
//
//        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
//        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
//        filteredPopulation.withResults(population, newArrayList(Subject.builder().subjectId("subjectId2").build()));
//
//        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);
//
//        FilterResult<Biomarker> filtered = filterService.query(filterQuery);
//
//        assertThat(filtered.getFilteredResult()).containsExactly(BIOMARKERS.get(1));
//    }

    @Test
    public void testQueryHasRadiotherapy() {

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        RadiotherapyFilters filters = new RadiotherapyFilters();
        filters.setRadiotherapyEnabled(false);
        FilterQuery<Radiotherapy> filterQuery = new FilterQuery<>(radioData, filters, new ArrayList<>(),
                PopulationFilters.empty());
        FilterResult<Radiotherapy> filtered = filterService.query(filterQuery);
        assertThat(filtered.getFilteredResult()).isEmpty();

        filters.setRadiotherapyEnabled(true);
        filtered = filterService.query(filterQuery);
        assertThat(filtered.getFilteredResult()).containsExactlyElementsOf(radioData);
    }
}
