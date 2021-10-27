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

import com.acuity.visualisations.rawdatamodel.dataproviders.CvotEndpointDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.CvotEndpointFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.generators.SubjectGenerator;
import com.acuity.visualisations.rawdatamodel.vo.CvotEndpointRaw;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class CvotEndpointFilterServiceTest {

    @InjectMocks
    private CvotEndpointFilterService cvotEndpointFilterService;

    @Mock
    private CvotEndpointDatasetsDataProvider cvotEndpointDatasetsDataProvider;
    @Mock
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @Mock
    private PopulationRawDataFilterService subjectService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static List<CvotEndpoint> getDummyCvotEndpoints() {
        CvotEndpoint cvotEndpoint1 = new CvotEndpoint(CvotEndpointRaw.builder().category1("cat1").category2("cat").aeNumber(1).description1("desc1")
                .startDate(new Date()).subjectId("subjectId1").term("term1").build(), Subject.builder().subjectId("subjectId1").build());
        CvotEndpoint cvotEndpoint2 = new CvotEndpoint(CvotEndpointRaw.builder().category1("cat2").category2("cat").aeNumber(1).description1("desc2")
                .startDate(new Date()).subjectId("subjectId1").term("term2").build(), Subject.builder().subjectId("subjectId1").build());
        CvotEndpoint cvotEndpoint3 = new CvotEndpoint(CvotEndpointRaw.builder().category1("cat3").category2("cat").aeNumber(1).description1("desc3")
                .startDate(new Date()).subjectId("subjectId2").term("term3").build(), Subject.builder().subjectId("subjectId2").build());
        CvotEndpoint cvotEndpoint4 = new CvotEndpoint(CvotEndpointRaw.builder().category1("cat3").category2("cat").aeNumber(1).description1("desc3")
                .startDate(new Date()).subjectId("subjectId3").term("term3").build(), Subject.builder().subjectId("subjectId3").build());
        return newArrayList(cvotEndpoint1, cvotEndpoint2, cvotEndpoint3, cvotEndpoint4);
    }

    @Test
    public void shouldGetAvailableFilters() throws Exception {
        List<CvotEndpoint> events = getDummyCvotEndpoints();

        final List<Subject> subjects = events.stream().map(e -> e.getSubject()).collect(Collectors.toList());
        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(subjects, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(subjects, subjects);

        when(subjectService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        //When
        final FilterQuery<CvotEndpoint> filterQuery = new FilterQuery<>(events, new CvotEndpointFilters(),
                newArrayList(), new PopulationFilters());
        CvotEndpointFilters result = (CvotEndpointFilters) cvotEndpointFilterService.getAvailableFilters(filterQuery);


        //Then
        softly.assertThat(result.getDescription1().getValues().size()).isEqualTo(3);
        softly.assertThat(result.getSubjectId().getValues().size()).isEqualTo(3);
        softly.assertThat(result.getCategory2().getValues().size()).isEqualTo(1);
        softly.assertThat(result.getCategory1().getValues().size()).isEqualTo(3);
    }

    @Test
    public void shouldQuery() {
        List<CvotEndpoint> events = getDummyCvotEndpoints();
        List<Subject> subjects = SubjectGenerator.generateSubjectListOfTwoWithSubjectIds();
        List<String> subjectIds = subjects.stream().map(Subject::getSubjectId).collect(Collectors.toList());

        FilterQuery<CvotEndpoint> trellisFilteredQuery = new FilterQuery<>(events, CvotEndpointFilters.empty(),
                subjects, PopulationFilters.empty());

        FilterQuery<Subject> populationFilteredQuery = new FilterQuery<>(subjects, PopulationFilters.empty());
        FilterResult<Subject> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(subjects, subjects);

        when(subjectService.getPopulationFilterResult(any(FilterQuery.class))).thenReturn(filteredPopulation);

        //When
        FilterResult<CvotEndpoint> filterResult = cvotEndpointFilterService.query(trellisFilteredQuery);

        System.out.println(filterResult.getFilteredResult().toString());

        //Then
        softly.assertThat(filterResult.getAllEvents().size()).isEqualTo(4);
        softly.assertThat(filterResult.getFilteredResult().size()).isEqualTo(3);
        softly.assertThat(filterResult.getFilterQuery()).isEqualTo(trellisFilteredQuery);
    }
}
