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

import com.acuity.visualisations.rawdatamodel.filters.CIEventFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.generators.CIEventGenerator;
import com.acuity.visualisations.rawdatamodel.generators.SubjectGenerator;
import com.acuity.visualisations.rawdatamodel.vo.CIEventRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class CIEventFilterServiceTest {

    @InjectMocks
    private CIEventFilterService ciEventFilterService;

    @Mock
    private PopulationRawDataFilterService subjectService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetAvailableFiltersImpl() {
        //Given
        CIEvent ciEvent1 = new CIEvent(CIEventRaw.builder().aeNumber(2).subjectId("subjectId1").finalDiagnosis("finalDiagnosis1").build(), new Subject());
        CIEvent ciEvent2 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").aeNumber(1).finalDiagnosis("finalDiagnosis2").build(), new Subject());
        CIEvent ciEvent3 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").aeNumber(1).finalDiagnosis("finalDiagnosis3").build(), new Subject());
        List<CIEvent> events = newArrayList(ciEvent1, ciEvent2, ciEvent3);

        FilterQuery<CIEvent> filterQuery = new FilterQuery<>(events, new CIEventFilters(),
                newArrayList(), new PopulationFilters());

        FilterResult<CIEvent> filterResult = new FilterResult<>(filterQuery);
        filterResult.withResults(events, events);

        //When
        CIEventFilters result = ciEventFilterService.getAvailableFiltersImpl(filterResult);

        //Then
        softly.assertThat(result.getFinalDiagnosis().getValues().size()).isEqualTo(3);
        softly.assertThat(result.getAeNumber().getValues().size()).isEqualTo(2);
        softly.assertThat(result.getEcgAtTheEventTime().getValues().size()).isEqualTo(2);
        softly.assertThat(result.getEcgAtTheEventTime().getValues()).contains("Yes", null);
    }

    @Test
    public void shouldGetAvailableFilters() throws Exception {
        //Given
        CIEvent ciEvent1 = new CIEvent(CIEventRaw.builder().aeNumber(2).subjectId("subjectId1").finalDiagnosis("finalDiagnosis1").build(),
                Subject.builder().subjectId("01").build());
        CIEvent ciEvent2 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").aeNumber(1).finalDiagnosis("finalDiagnosis2").build(),
                Subject.builder().subjectId("01").build());
        CIEvent ciEvent3 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").aeNumber(1).finalDiagnosis("finalDiagnosis3").build(),
                Subject.builder().subjectId("01").build());
        List<CIEvent> events = Arrays.asList(ciEvent1, ciEvent2, ciEvent3);

        final List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());
        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(subjects, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(subjects, subjects);

        when(subjectService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        //When
        CIEventFilters result = (CIEventFilters) ciEventFilterService.getAvailableFilters(new FilterQuery<>(events, new CIEventFilters(),
                subjects, new PopulationFilters()));

        //Then
        softly.assertThat(result.getFinalDiagnosis().getValues().size()).isEqualTo(3);
        softly.assertThat(result.getAeNumber().getValues().size()).isEqualTo(2);
        softly.assertThat(result.getEcgAtTheEventTime().getValues().size()).isEqualTo(2);
        softly.assertThat(result.getEcgAtTheEventTime().getValues()).contains(null, "Yes");
    }

    @Test
    public void shouldQuery() {
        //Given
        List<CIEvent> events = CIEventGenerator.generateAnyCIEventListOfTwo();
        List<Subject> subjects = SubjectGenerator.generateSubjectListOfTwoWithSubjectIds();

        FilterQuery<CIEvent> trellisFilteredQuery = new FilterQuery<>(events, CIEventFilters.empty(),
                subjects, PopulationFilters.empty());

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(subjects, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(subjects, subjects);

        when(subjectService.getPopulationFilterResult(any())).thenReturn(filteredPopulation);

        //When
        FilterResult<CIEvent> filterResult = ciEventFilterService.query(trellisFilteredQuery);

        //Then
        softly.assertThat(filterResult.getAllEvents().size()).isEqualTo(2);
        softly.assertThat(filterResult.getFilteredResult().size()).isEqualTo(2);
        softly.assertThat(filterResult.getFilterQuery()).isEqualTo(trellisFilteredQuery);
    }
}
