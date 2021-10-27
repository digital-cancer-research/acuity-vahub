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

import com.acuity.visualisations.rawdatamodel.filters.ExposureFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.vo.ExposureRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.exposure.Cycle;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class ExposureFilterServiceTest {

    @InjectMocks
    private ExposureFilterService filterService;

    @Mock
    private PopulationRawDataFilterService populationFilterService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private static List<Exposure> exposures;

    static {
        Exposure e1 = new Exposure(ExposureRaw.builder().subjectId("subject1").analyte("analyte1").analyteUnit("unit1")
                .analyteConcentration(10.0).treatmentCycle("cycle1").protocolScheduleDay(10).treatment("10mg")
                .visitNumber(1).timeFromAdministration(10.0)
                .cycle(new Cycle("cycle1", "analyte1", 1, null, false))
                .build(), Subject.builder().subjectId("subject1").build());
        Exposure e2 = new Exposure(ExposureRaw.builder().subjectId("subject1").analyte("analyte2").analyteUnit("unit2")
                .analyteConcentration(0.1).treatmentCycle("cycle1").protocolScheduleDay(15).treatment("20mg")
                .visitNumber(1).timeFromAdministration(0.1)
                .cycle(new Cycle("cycle1", "analyte2", 1, null, false))
                .build(), Subject.builder().subjectId("subject1").build());
        Exposure e3 = new Exposure(ExposureRaw.builder().subjectId("subject1").analyte("analyte3").analyteUnit("unit2")
                .analyteConcentration(2.3).treatmentCycle("cycle2").protocolScheduleDay(15).treatment("30mg")
                .visitNumber(2).timeFromAdministration(2.3)
                .cycle(new Cycle("cycle2", "analyte3", 2, null, false))
                .build(), Subject.builder().subjectId("subject1").build());
        Exposure e4 = new Exposure(ExposureRaw.builder().subjectId("subject2").analyte("analyte4").analyteUnit("unit3")
                .analyteConcentration(6.5).treatmentCycle("cycle3").protocolScheduleDay(30).treatment("60mg")
                .visitNumber(6).timeFromAdministration(6.5)
                .cycle(new Cycle("cycle3", "analyte4", 6, null, false))
                .build(), Subject.builder().subjectId("subject2").build());
        Exposure e5 = new Exposure(ExposureRaw.builder().subjectId("subject2").analyte(null).analyteUnit(null).analyteConcentration(null).treatmentCycle(null)
                .visitNumber(null).timeFromAdministration(null)
                .cycle(new Cycle(null, null, null, null, false))
                .build(), Subject.builder().subjectId("subject2").build());

        exposures = newArrayList(e1, e2, e3, e4, e5);
    }

    private static List<Subject> population = newArrayList(Subject.builder().subjectId("subject1").build(),
            Subject.builder().subjectId("subject2").build());

    @Test
    public void testQueryEmptyFilters() throws IOException {

        FilterQuery<Exposure> filterQuery = new FilterQuery<>(exposures, ExposureFilters.empty(), new ArrayList<>(), PopulationFilters
                .empty());

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        FilterResult<Exposure> filtered = filterService.query(filterQuery);

        assertThat(filtered.getAllEvents()).containsAll(exposures);
        assertThat(filtered.getFilteredResult()).containsAll(exposures);
    }

    @Test
    public void testQueryEmptyFiltersWithFilteredPopulation() throws IOException {

        FilterQuery<Exposure> filterQuery = new FilterQuery<>(exposures, ExposureFilters.empty(), new ArrayList<>(), PopulationFilters
                .empty());

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, newArrayList(Subject.builder().subjectId("subject2").build()));

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        FilterResult<Exposure> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(exposures.get(3), exposures.get(4));
    }

    @Test
    public void testQueryEmptyFiltersWithEmptyPopulation() throws IOException {

        FilterQuery<Exposure> filterQuery = new FilterQuery<>(exposures, ExposureFilters.empty(), new ArrayList<>(), PopulationFilters
                .empty());

        List<Subject> population = new ArrayList<>();
        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        FilterResult<Exposure> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).isEmpty();
    }

    @Test
    public void testQueryAnalyteFilter() {
        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        ExposureFilters filters = new ExposureFilters();
        filters.setAnalyte(new SetFilter<>(newHashSet("analyte2", "analyte4")));
        FilterQuery<Exposure> filterQuery = new FilterQuery<>(exposures, filters, new ArrayList<>(), PopulationFilters.empty());
        FilterResult<Exposure> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(exposures.get(1), exposures.get(3));
    }

    @Test
    public void testQueryAnalyteConcentrationFilter() {
        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        ExposureFilters filters = new ExposureFilters();
        filters.getAnalyteConcentration().setFrom(0.2);
        filters.getAnalyteConcentration().setTo(8.9);
        FilterQuery<Exposure> filterQuery = new FilterQuery<>(exposures, filters, new ArrayList<>(), PopulationFilters.empty());
        FilterResult<Exposure> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(exposures.get(1), exposures.get(3));
    }

    @Test
    public void testQueryAnalyteUnitFilter() {
        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        ExposureFilters filters = new ExposureFilters();
        filters.setAnalyteUnit(new SetFilter<>(newHashSet("unit1", "unit2")));
        FilterQuery<Exposure> filterQuery = new FilterQuery<>(exposures, filters, new ArrayList<>(), PopulationFilters.empty());
        FilterResult<Exposure> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(exposures.get(0), exposures.get(1), exposures.get(2));
    }

    @Test
    public void testQueryTreatmentCycle() {
        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        ExposureFilters filters = new ExposureFilters();
        filters.setTreatmentCycle(new SetFilter<>(newHashSet("cycle1", "cycle3")));
        FilterQuery<Exposure> filterQuery = new FilterQuery<>(exposures, filters, new ArrayList<>(), PopulationFilters.empty());
        FilterResult<Exposure> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(exposures.get(0), exposures.get(1), exposures.get(3));
    }

    @Test
    public void testQueryTimeFromAdministrationFilter() {
        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        ExposureFilters filters = new ExposureFilters();
        filters.getTimeFromAdministration().setFrom(0.2);
        filters.getTimeFromAdministration().setTo(8.9);
        FilterQuery<Exposure> filterQuery = new FilterQuery<>(exposures, filters, new ArrayList<>(), PopulationFilters.empty());
        FilterResult<Exposure> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(exposures.get(1), exposures.get(3));
    }

    @Test
    public void testQueryVisitFilter() {
        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        ExposureFilters filters = new ExposureFilters();
        filters.setVisit(new SetFilter<>(newHashSet(1, 2)));
        FilterQuery<Exposure> filterQuery = new FilterQuery<>(exposures, filters, new ArrayList<>(), PopulationFilters.empty());
        FilterResult<Exposure> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(exposures.get(0), exposures.get(1), exposures.get(2));
    }

    @Test
    public void testQueryTreatmentFilter() {
        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        ExposureFilters filters = new ExposureFilters();
        filters.setTreatment(new SetFilter<>(newHashSet("60mg")));
        FilterQuery<Exposure> filterQuery = new FilterQuery<>(exposures, filters, new ArrayList<>(), PopulationFilters.empty());
        FilterResult<Exposure> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(exposures.get(3));
    }

    @Test
    public void testQueryDayFilter() {
        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(population, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(population, population);

        when(populationFilterService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        ExposureFilters filters = new ExposureFilters();
        filters.setDay(new SetFilter<>(newHashSet(15)));
        FilterQuery<Exposure> filterQuery = new FilterQuery<>(exposures, filters, new ArrayList<>(), PopulationFilters.empty());
        FilterResult<Exposure> filtered = filterService.query(filterQuery);

        assertThat(filtered.getFilteredResult()).containsExactly(exposures.get(1), exposures.get(2));
    }
}
