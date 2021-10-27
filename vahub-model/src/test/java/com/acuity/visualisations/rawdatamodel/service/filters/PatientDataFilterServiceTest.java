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

import com.acuity.visualisations.rawdatamodel.filters.PatientDataFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.PatientDataRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class PatientDataFilterServiceTest {
    private static List<Subject> population = newArrayList(Subject.builder().subjectId("subjectId1").build(),
            Subject.builder().subjectId("subjectId2").build());

    @Mock
    private PopulationRawDataFilterService populationFilterService;

    @InjectMocks
    private PatientDataFilterService filterService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private static final List<PatientData> PATIENTDATA = generatePatientData();
    private static final List<PatientData> PATIENTDATA_LIST;

    static {
        PatientData patientData1 = new PatientData(PatientDataRaw.builder()
                .id("pr1")
                .subjectId("subjectId1")
                .measurementName("activity")
                .unit("step")
                .value(1000.0)
                .build(), Subject.builder().subjectId("subjectId1").build());
        PatientData patientData2 = new PatientData(PatientDataRaw.builder()
                .id("pr2")
                .subjectId("subjectId2")
                .measurementName("pulse")
                .unit("heartbeat")
                .value(70.0)
                .build(), Subject.builder().subjectId("subjectId2").build());
        PatientData patientData3 = new PatientData(PatientDataRaw.builder()
                .id("pr3")
                .subjectId("subjectId2")
                .measurementName("puls")
                .unit("heartbeat")
                .value(79.0)
                .build(), Subject.builder().subjectId("subjectId2").build());
        PatientData patientData4 = new PatientData(PatientDataRaw.builder()
                .id("pr4")
                .subjectId("subjectId1")
                .measurementName("activity")
                .unit("step")
                .value(5000.0)
                .build(), Subject.builder().subjectId("subjectId1").build());
        PatientData patientData5 = new PatientData(PatientDataRaw.builder()
                .id("pr5")
                .subjectId("subjectId2")
                .measurementName("puls")
                .unit("heartbeat")
                .value(83.0)
                .build(), Subject.builder().subjectId("subjectId2").build());
        PatientData patientData6 = new PatientData(PatientDataRaw.builder()
                .id("pr6")
                .subjectId("subjectId1")
                .measurementName("activity")
                .unit("step")
                .value(3000.0)
                .build(), Subject.builder().subjectId("subjectId1").build());
        PATIENTDATA_LIST = newArrayList(patientData1, patientData2, patientData3,
                patientData4, patientData5, patientData6);
    }

    private static List<PatientData> generatePatientData() {
        PatientData patientData1 = new PatientData(PatientDataRaw.builder()
                .id("pr1")
                .subjectId("subjectId1")
                .measurementName("activity")
                .unit("step")
                .value(1000.0)
                .build(), Subject.builder().subjectId("subjectId1").build());
        PatientData patientData2 = new PatientData(PatientDataRaw.builder()
                .id("pr2")
                .subjectId("subjectId2")
                .measurementName("pulse")
                .unit("heartbeat")
                .value(70.0)
                .build(), Subject.builder().subjectId("subjectId2").build());
        return newArrayList(patientData1, patientData2);
    }

    @Test
    public void testQueryEmptyFilters() {

        FilterQuery<PatientData> filterQuery = new FilterQuery<>(PATIENTDATA, PatientDataFilters.empty(),
                population, PopulationFilters.empty());

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        FilterResult<PatientData> filtered = filterService.query(filterQuery);

        assertThat(filtered.getAllEvents()).containsExactly(PATIENTDATA.get(0), PATIENTDATA.get(1));
        assertThat(filtered.getFilteredResult()).containsExactly(PATIENTDATA.get(0), PATIENTDATA.get(1));
    }

    @Test
    public void testQueryEmptyFiltersWithFilteredPopulation() {

        FilterQuery<PatientData> filterQuery = new FilterQuery<>(PATIENTDATA, PatientDataFilters.empty(),
                population, PopulationFilters.empty());

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, newArrayList(Subject.builder().subjectId("subjectId1").build()));

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        FilterResult<PatientData> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(PATIENTDATA.get(0));
    }

    @Test
    public void testQueryEmptyFiltersWithEmptyPopulation() {
        Collection<Subject> population = new ArrayList<>();
        FilterQuery<PatientData> filterQuery = new FilterQuery<>(PATIENTDATA, PatientDataFilters.empty(),
                population, PopulationFilters.empty());

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        FilterResult<PatientData> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).isEmpty();
    }

    @Test
    public void testQueryMeasurementNameFilter() {

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        PatientDataFilters filters = new PatientDataFilters();
        filters.setMeasurementName(new SetFilter<>(newHashSet("activity")));
        FilterQuery<PatientData> filterQuery = new FilterQuery<>(PATIENTDATA_LIST, filters, new ArrayList<>(),
                PopulationFilters.empty());

        FilterResult<PatientData> filtered = filterService.query(filterQuery);
        assertThat(filtered.getFilteredResult()).containsExactly(PATIENTDATA_LIST.get(0), PATIENTDATA_LIST.get(3), PATIENTDATA_LIST.get(5));
    }
}
