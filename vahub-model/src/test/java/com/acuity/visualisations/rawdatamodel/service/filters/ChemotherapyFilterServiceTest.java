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

import com.acuity.visualisations.rawdatamodel.filters.ChemotherapyFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.ChemotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;
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

public class ChemotherapyFilterServiceTest {

    Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1").firstTreatmentDate(DaysUtil.toDate("2000-04-05"))
            .lastTreatmentDate(DaysUtil.toDate("2000-05-20")).build();
    Subject subject2 = Subject.builder().subjectId("subjectId2").subjectCode("subject2").firstTreatmentDate(DaysUtil.toDate("2000-01-10"))
            .lastTreatmentDate(DaysUtil.toDate("2000-05-21")).build();
    Subject subject3 = Subject.builder().subjectId("subjectId3").subjectCode("subject3").firstTreatmentDate(DaysUtil.toDate("2000-02-21"))
            .lastTreatmentDate(DaysUtil.toDate("2000-05-21")).build();

    List<Subject> population = Arrays.asList(subject1, subject2, subject3);

    private List<Chemotherapy> chemoData = Arrays.asList(
            new Chemotherapy(ChemotherapyRaw.builder().subjectId(subject1.getSubjectId())
                    .startDate(DaysUtil.toDate("2000-02-20")).endDate(DaysUtil.toDate("2000-03-30"))
                    .therapyClass("class 1").preferredMed("med 2").treatmentStatus("status 1").bestResponse("bestResponse")
                    .failureReason("failureReason")
                    .numOfCycles(5)
                    .build(),
                    subject1),

            new Chemotherapy(ChemotherapyRaw.builder().subjectId(subject2.getSubjectId())
                    .endDate(DaysUtil.toDate("2000-01-09"))
                    .therapyClass("class 1").preferredMed("med 1").bestResponse("bestResponse 2")
                    .failureReason("failureReason 2").numOfCycles(10).build(), subject2),

            new Chemotherapy(ChemotherapyRaw.builder().subjectId(subject3.getSubjectId())
                    .startDate(DaysUtil.toDate("2000-02-10")).endDate(subject3.getFirstTreatmentDate()).build(), subject3));

    @Mock
    private PopulationRawDataFilterService populationFilterService;

    @InjectMocks
    private ChemotherapyFilterService filterService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void testQueryHasRadiotherapy() {

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        ChemotherapyFilters filters = new ChemotherapyFilters();
        FilterQuery<Chemotherapy> filterQuery = new FilterQuery<>(chemoData, filters, new ArrayList<>(),
                PopulationFilters.empty());
        FilterResult<Chemotherapy> filtered =  filterService.query(filterQuery);
        assertThat(filtered.getFilteredResult()).containsExactlyElementsOf(chemoData);
    }
}
