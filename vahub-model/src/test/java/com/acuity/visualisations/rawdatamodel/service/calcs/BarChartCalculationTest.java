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

package com.acuity.visualisations.rawdatamodel.service.calcs;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.MockitoAnnotations;

public class BarChartCalculationTest {
/*

    @InjectMocks
    private BarChartCalculations barChartCalculations;
*/

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldRunPerGroupedChartWithCountOfEvents() {
      /*  //Given
        CIEvent event1 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").subjectId("subjectId1").build(),
                Subject.builder().subjectId("subjectId1").build());
        CIEvent event2 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").subjectId("subjectId2").build(),
                Subject.builder().subjectId("subjectId2").build());
        List<CIEvent> events = Arrays.asList(event1, event2);

        AttributeStorage storage = new AttributeStorage();
        storage.setGroupByAttribute(CIEvent.Attributes.ECG_AT_THE_EVENT_TIME.getAttribute());
        storage.setCountType(CountType.COUNT_OF_EVENTS);

        //When
        BarChartCalculationObject result = barChartCalculations.runPerGroupedChart(createGroupByEvents(events, events), storage);

        //Then
        softly.assertThat(result.getValue()).isEqualTo(2.0);
    }

    @Test
    public void shouldRunPerGroupedChartWithCountOfSubjects() {
        //Given
        CIEvent event1 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").subjectId("subjectId1").build(),
                Subject.builder().subjectId("subjectId1").build());
        CIEvent event2 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").subjectId("subjectId1").build(),
                Subject.builder().subjectId("subjectId1").build());
        List<CIEvent> events = Arrays.asList(event1, event2);

        AttributeStorage storage = new AttributeStorage();
        storage.setGroupByAttribute(CIEvent.Attributes.ECG_AT_THE_EVENT_TIME.getAttribute());
        storage.setCountType(CountType.COUNT_OF_SUBJECTS);

        //When
        BarChartCalculationObject result = barChartCalculations.runPerGroupedChart(createGroupByEvents(events, events), storage);

        //Then
        softly.assertThat(result.getValue()).isEqualTo(1.0);
    }

    @Test
    public void shouldRunPerGroupedChartWithPercentageOfAllEvents() {
        //Given
        CIEvent event1 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").subjectId("subjectId1").build(),
                Subject.builder().subjectId("subjectId1").build());
        CIEvent event2 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").subjectId("subjectId2").build(),
                Subject.builder().subjectId("subjectId2").build());
        CIEvent event3 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("No").subjectId("subjectId2").build(),
                Subject.builder().subjectId("subjectId2").build());
        List<CIEvent> events = Arrays.asList(event1, event2, event3);
        List<CIEvent> gropedEvents = Arrays.asList(event1, event2);

        AttributeStorage storage = new AttributeStorage();
        storage.setGroupByAttribute(CIEvent.Attributes.ECG_AT_THE_EVENT_TIME.getAttribute());
        storage.setCountType(CountType.PERCENTAGE_OF_ALL_EVENTS);

        //When
        BarChartCalculationObject result = barChartCalculations.runPerGroupedChart(createGroupByEvents(events, gropedEvents), storage);

        //Then
        softly.assertThat(result.getValue()).isEqualTo(66.67);
    }

    @Test
    public void shouldRunPerGroupedChartWithPercentageOfAllSubjects() {
        //Given
        CIEvent event1 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").subjectId("subjectId1").build(),
                Subject.builder().subjectId("subjectId1").build());
        CIEvent event2 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").subjectId("subjectId2").build(),
                Subject.builder().subjectId("subjectId2").build());
        CIEvent event3 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("No").subjectId("subjectId3").build(),
                Subject.builder().subjectId("subjectId3").build());
        List<CIEvent> events = Arrays.asList(event1, event2, event3);
        List<CIEvent> groupedEvents = Arrays.asList(event1, event2);

        AttributeStorage storage = new AttributeStorage();
        storage.setGroupByAttribute(CIEvent.Attributes.ECG_AT_THE_EVENT_TIME.getAttribute());
        storage.setCountType(CountType.PERCENTAGE_OF_ALL_SUBJECTS);

        //When
        BarChartCalculationObject result = barChartCalculations.runPerGroupedChart(createGroupByEvents(events, groupedEvents), storage);

        //Then
        softly.assertThat(result.getValue()).isEqualTo(40.);
    }

    @Test
    public void shouldRunPerGroupedChartWithPercentageOfEventsWithinPlot() {
        //Given
        CIEvent event1 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").subjectId("subjectId1").build(),
                Subject.builder().subjectId("subjectId1").build());
        CIEvent event2 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").subjectId("subjectId2").build(),
                Subject.builder().subjectId("subjectId2").build());
        CIEvent event3 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("No").subjectId("subjectId3").build(),
                Subject.builder().subjectId("subjectId3").build());
        List<CIEvent> events = Arrays.asList(event1, event2, event3);
        List<CIEvent> groupedEvents = Arrays.asList(event1, event2);

        AttributeStorage storage = new AttributeStorage();
        storage.setGroupByAttribute(CIEvent.Attributes.ECG_AT_THE_EVENT_TIME.getAttribute());
        storage.setCountType(CountType.PERCENTAGE_OF_EVENTS_WITHIN_PLOT);

        //When
        BarChartCalculationObject result = barChartCalculations.runPerGroupedChart(createGroupByEvents(events, groupedEvents), storage);

        //Then
        softly.assertThat(result.getValue()).isEqualTo(66.67);
    }

    @Test
    public void shouldRunPerGroupedChartWithPercentageOfSubjectsWithinPlot() {
        //Given
        CIEvent event1 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").subjectId("subjectId1").build(),
                Subject.builder().subjectId("subjectId1").build());
        CIEvent event2 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("Yes").subjectId("subjectId1").build(),
                Subject.builder().subjectId("subjectId1").build());
        CIEvent event3 = new CIEvent(CIEventRaw.builder().ecgAtTheEventTime("No").subjectId("subjectId3").build(),
                Subject.builder().subjectId("subjectId3").build());
        List<CIEvent> events = Arrays.asList(event1, event2, event3);
        List<CIEvent> groupedEvents = Arrays.asList(event1, event2);

        AttributeStorage storage = new AttributeStorage();
        storage.setGroupByAttribute(CIEvent.Attributes.ECG_AT_THE_EVENT_TIME.getAttribute());
        storage.setCountType(CountType.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT);

        //When
        BarChartCalculationObject result = barChartCalculations.runPerGroupedChart(createGroupByEvents(events, groupedEvents), storage);

        //Then
        softly.assertThat(result.getValue()).isEqualTo(50.0);
    }

    private GroupedByEvents<CIEvent, CIEventGroupByOptions> createGroupByEvents(List<CIEvent> events, List<CIEvent> groupedEvents) {

        List<Subject> subjects = SubjectGenerator.generateSubjectListOfFiveWithSubjectIds();

        FilterQuery<Subject, PopulationGroupByOptions> populationFilteredQuery = new FilterQuery<>(subjects, PopulationFilters.empty());
        FilterResult<Subject, PopulationGroupByOptions> filteredPopulation = new FilterResult<>(populationFilteredQuery);
        filteredPopulation.withResults(subjects, subjects);

        FilterQuery<CIEvent, CIEventGroupByOptions> filterQuery = new FilterQuery<>(
                events, CIEventFilters.empty(), subjects, PopulationFilters.empty(), Collections.emptySet());
        FilterResult<CIEvent, CIEventGroupByOptions> filterResult = new FilterResult<>(filterQuery);
        filterResult.withResults(events, events);
        filterResult.withPopulationFilteredResults(filteredPopulation);

        TrellisOption<CIEventGroupByOptions> trellisOption = new TrellisOption<>(ECG_AT_THE_EVENT_TIME, "Yes");
        TrellisChart<CIEvent, CIEventGroupByOptions> trellisChart = new TrellisChart<>(newArrayList(trellisOption), filterResult);

        AttributeStorage storage = new AttributeStorage();
        storage.setGroupByAttribute(CIEvent.Attributes.ECG_AT_THE_EVENT_TIME.getAttribute());
        storage.setCountType(CountType.COUNT_OF_EVENTS);

        return new GroupedByEvents<>(trellisChart, "Yes", groupedEvents);
*/    }
}
