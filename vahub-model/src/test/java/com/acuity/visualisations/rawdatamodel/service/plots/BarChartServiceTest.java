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

package com.acuity.visualisations.rawdatamodel.service.plots;

import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.plots.vo.BarChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.Bin;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.GroupByKey;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.PopulationGroupingOption;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static org.assertj.core.groups.Tuple.tuple;

/**
 * Created by knml167 on 9/26/2017.
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class BarChartServiceTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldCountEventsAndSelect() {
        //Given
        final Subject subject1 = Subject.builder().subjectId("id1").subjectCode("E01").build();
        final Subject subject2 = Subject.builder().subjectId("id2").subjectCode("E02").build();
        final List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("05.01.2016"), null, "prop1", subject1),
                new Entity("2", DateUtils.toDate("01.01.2016"), DateUtils.toDate("06.01.2016"), null, "prop2", subject1),
                new Entity("3", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject2),
                new Entity("4", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject2)
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2);

        BarChartService<Entity, SomeGroupByOptions> barChartService = new BarChartService<>();

        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));
        final ChartGroupByOptions<Entity, SomeGroupByOptions> settings = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(X_AXIS, SomeGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .withOption(COLOR_BY, SomeGroupByOptions.PROPERTY.getGroupByOptionAndParams())
                .build();

        final HashMap<SomeGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(COLOR_BY, "prop3");
        selectedItems1.put(X_AXIS, "E01");
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(COLOR_BY, "prop3");
        selectedItems2.put(X_AXIS, "E02");
        final List<ChartSelectionItem<Entity, SomeGroupByOptions>> selectionItems = Arrays.asList(
                ChartSelectionItem.of(selectedTrellises, selectedItems1),
                ChartSelectionItem.of(selectedTrellises, selectedItems2)
        );

        //When
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChartCount = barChartService.getBarChart(
                settings, CountType.COUNT_OF_EVENTS, filtered);
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChartPercentage = barChartService.getBarChart(
                settings, CountType.PERCENTAGE_OF_ALL_EVENTS, filtered);

        final SelectionDetail selectionDetails = barChartService.getSelectionDetails(filtered, ChartSelection.of(settings, selectionItems));

        //Then
        softly.assertThat(barChartCount.entrySet())
                .extracting(e -> e.getKey().getValue(X_AXIS), e -> e.getKey().getValue(COLOR_BY),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple("E02", "prop3", 2.0, 1),
                        tuple("E01", "prop1", 1.0, 1),
                        tuple("E01", "prop2", 1.0, 1)
                );

        softly.assertThat(barChartPercentage.entrySet())
                .extracting(e -> e.getKey().getValue(X_AXIS), e -> e.getKey().getValue(COLOR_BY),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple("E01", "prop2", 25.0, 1),
                        tuple("E02", "prop3", 50.0, 1),
                        tuple("E01", "prop1", 25.0, 1)
                );


        softly.assertThat(selectionDetails.getEventIds()).containsExactlyInAnyOrder("3", "4");
        softly.assertThat(selectionDetails.getSubjectIds()).containsExactly("id2");
    }

    @Test
    public void shouldCountEventsAndSelectWithBinnedDateAttribute() {
        //Given
        final Subject subject1 = Subject.builder().subjectId("id1").subjectCode("E01").firstTreatmentDate(DateUtils.toDate("01.01.2016")).build();
        final Subject subject2 = Subject.builder().subjectId("id2").subjectCode("E02").firstTreatmentDate(DateUtils.toDate("06.01.2016")).build();
        final List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("05.01.2016"), null, "prop1", subject1),
                new Entity("2", DateUtils.toDate("01.01.2016"), DateUtils.toDate("06.01.2016"), null, "prop2", subject1),
                new Entity("3", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject2),
                new Entity("4", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject2),
                new Entity("5", DateUtils.toDate("15.01.2016"), DateUtils.toDate("15.01.2016"), null, "prop3", subject2)
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2);

        BarChartService<Entity, SomeGroupByOptions> barChartService = new BarChartService<>();

        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));
        final ChartGroupByOptions<Entity, SomeGroupByOptions> settings = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(X_AXIS, SomeGroupByOptions.DURATION.getGroupByOptionAndParams(
                        GroupByOption.Params.builder()
                                .with(GroupByOption.Param.BIN_SIZE, 2)
                                .with(GroupByOption.Param.BIN_INCL_DURATION, true)
                                .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                                .build()))
                .build();

        final HashMap<SomeGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "0 - 1");
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(X_AXIS, "2 - 3");

        final List<ChartSelectionItem<Entity, SomeGroupByOptions>> selectionItems = Arrays.asList(
                ChartSelectionItem.of(selectedTrellises, selectedItems1),
                ChartSelectionItem.of(selectedTrellises, selectedItems2)
        );

        //When
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChartCount = barChartService.getBarChart(
                settings, CountType.COUNT_OF_EVENTS, filtered);
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChartPercentage = barChartService.getBarChart(
                settings, CountType.PERCENTAGE_OF_ALL_EVENTS, filtered);
        final SelectionDetail selectionDetails = barChartService.getSelectionDetails(filtered, ChartSelection.of(settings, selectionItems));

        //Then
        softly.assertThat(barChartCount.entrySet())
                .extracting(e -> e.getKey().getValue(X_AXIS),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple(Bin.newInstance(-2, 2), 2.0, 1),
                        tuple(Bin.newInstance(0, 2), 4.0, 2),
                        tuple(Bin.newInstance(2, 2), 2.0, 1),
                        tuple(Bin.newInstance(4, 2), 2.0, 1),
                        tuple(Bin.newInstance(8, 2), 1.0, 1)
                );

        softly.assertThat(barChartPercentage.entrySet())
                .extracting(e -> e.getKey().getValue(X_AXIS),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple(Bin.newInstance(4, 2), 40.0, 1),
                        tuple(Bin.newInstance(0, 2), 80.0, 2),
                        tuple(Bin.newInstance(8, 2), 20.0, 1),
                        tuple(Bin.newInstance(2, 2), 40.0, 1),
                        tuple(Bin.newInstance(-2, 2), 40.0, 1)
                );


        softly.assertThat(selectionDetails.getEventIds()).containsExactlyInAnyOrder("1", "2", "3", "4");
        softly.assertThat(selectionDetails.getSubjectIds()).containsExactlyInAnyOrder("id2", "id1");
    }

    @Test
    public void shouldCountEventsAndSelectWithBinnedDateAndMultiValueAttributes() {
        //Given
        final Subject subject1 = Subject.builder().subjectId("id1").subjectCode("E01").firstTreatmentDate(DateUtils.toDate("01.01.2016")).build();
        final Subject subject2 = Subject.builder().subjectId("id2").subjectCode("E02").firstTreatmentDate(DateUtils.toDate("06.01.2016")).build();
        final List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("02.01.2016"), Arrays.asList("v1", "v2"), "prop1", subject1),
                new Entity("2", DateUtils.toDate("01.01.2016"), DateUtils.toDate("03.01.2016"), Arrays.asList("v1", "v2", "v3"), "prop2", subject1),
                new Entity("3", DateUtils.toDate("31.12.2015"), DateUtils.toDate("02.01.2016"), Arrays.asList("v3", "v4"), "prop3", subject1),
                new Entity("4", DateUtils.toDate("05.01.2016"), DateUtils.toDate("08.01.2016"), Arrays.asList("v1", "v2"), "prop3", subject2),
                new Entity("5", DateUtils.toDate("15.01.2016"), DateUtils.toDate("15.01.2016"), Arrays.asList("v2", "v3"), "prop3", subject2)
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2);

        BarChartService<Entity, SomeGroupByOptions> barChartService = new BarChartService<>();

        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));
        final ChartGroupByOptions<Entity, SomeGroupByOptions> settings = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(COLOR_BY, SomeGroupByOptions.CATEGORY.getGroupByOptionAndParams())
                .withOption(X_AXIS, SomeGroupByOptions.DURATION.getGroupByOptionAndParams(
                        GroupByOption.Params.builder()
                                .with(GroupByOption.Param.BIN_SIZE, 1)
                                .with(GroupByOption.Param.BIN_INCL_DURATION, true)
                                .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                                .build()))
                .build();

        final HashMap<SomeGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(X_AXIS, "0");
        selectedItems1.put(COLOR_BY, "v1");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(X_AXIS, "0");
        selectedItems2.put(COLOR_BY, "v3");

        final List<ChartSelectionItem<Entity, SomeGroupByOptions>> selectionItems2 = Arrays.asList(
                ChartSelectionItem.of(selectedTrellises, selectedItems2)
        );

        final List<ChartSelectionItem<Entity, SomeGroupByOptions>> selectionItems1 = Arrays.asList(
                ChartSelectionItem.of(selectedTrellises, selectedItems1)
        );

        //When
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChartCount = barChartService.getBarChart(
                settings, CountType.COUNT_OF_EVENTS, filtered);
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChartPercentage = barChartService.getBarChart(
                settings, CountType.PERCENTAGE_OF_ALL_EVENTS, filtered);
        final SelectionDetail selectionDetails1 = barChartService.getSelectionDetails(filtered, ChartSelection.of(settings, selectionItems1));
        final SelectionDetail selectionDetails2 = barChartService.getSelectionDetails(filtered, ChartSelection.of(settings, selectionItems2));

        //Then
        softly.assertThat(barChartCount.entrySet())
                .extracting(e -> e.getKey().getValue(X_AXIS), e -> e.getKey().getValue(COLOR_BY),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple(Bin.newInstance(-1, 1), "v1", 1.0, 1),
                        tuple(Bin.newInstance(-1, 1), "v2", 1.0, 1),
                        tuple(Bin.newInstance(-1, 1), "v3", 1.0, 1),
                        tuple(Bin.newInstance(-1, 1), "v4", 1.0, 1),
                        tuple(Bin.newInstance(0, 1), "v1", 3.0, 2),
                        tuple(Bin.newInstance(0, 1), "v2", 3.0, 2),
                        tuple(Bin.newInstance(0, 1), "v3", 2.0, 1),
                        tuple(Bin.newInstance(0, 1), "v4", 1.0, 1),
                        tuple(Bin.newInstance(1, 1), "v1", 3.0, 2),
                        tuple(Bin.newInstance(1, 1), "v2", 3.0, 2),
                        tuple(Bin.newInstance(1, 1), "v3", 2.0, 1),
                        tuple(Bin.newInstance(1, 1), "v4", 1.0, 1),
                        tuple(Bin.newInstance(2, 1), "v1", 2.0, 2),
                        tuple(Bin.newInstance(2, 1), "v2", 2.0, 2),
                        tuple(Bin.newInstance(2, 1), "v3", 1.0, 1),
                        tuple(Bin.newInstance(9, 1), "v2", 1.0, 1),
                        tuple(Bin.newInstance(9, 1), "v3", 1.0, 1)
                );

        softly.assertThat(barChartPercentage.entrySet())
                .extracting(e -> e.getKey().getValue(X_AXIS), e -> e.getKey().getValue(COLOR_BY),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple(Bin.newInstance(-1, 1), "v1", 20.0, 1),
                        tuple(Bin.newInstance(-1, 1), "v2", 20.0, 1),
                        tuple(Bin.newInstance(-1, 1), "v3", 20.0, 1),
                        tuple(Bin.newInstance(-1, 1), "v4", 20.0, 1),
                        tuple(Bin.newInstance(0, 1), "v1", 60.0, 2),
                        tuple(Bin.newInstance(0, 1), "v2", 60.0, 2),
                        tuple(Bin.newInstance(0, 1), "v3", 40.0, 1),
                        tuple(Bin.newInstance(0, 1), "v4", 20.0, 1),
                        tuple(Bin.newInstance(1, 1), "v1", 60.0, 2),
                        tuple(Bin.newInstance(1, 1), "v2", 60.0, 2),
                        tuple(Bin.newInstance(1, 1), "v3", 40.0, 1),
                        tuple(Bin.newInstance(1, 1), "v4", 20.0, 1),
                        tuple(Bin.newInstance(2, 1), "v1", 40.0, 2),
                        tuple(Bin.newInstance(2, 1), "v2", 40.0, 2),
                        tuple(Bin.newInstance(2, 1), "v3", 20.0, 1),
                        tuple(Bin.newInstance(9, 1), "v2", 20.0, 1),
                        tuple(Bin.newInstance(9, 1), "v3", 20.0, 1)
                );


        softly.assertThat(selectionDetails1.getEventIds()).containsExactlyInAnyOrder("1", "2", "4");
        softly.assertThat(selectionDetails1.getSubjectIds()).containsExactlyInAnyOrder("id2", "id1");
        softly.assertThat(selectionDetails2.getEventIds()).containsExactlyInAnyOrder("2", "3");
        softly.assertThat(selectionDetails2.getSubjectIds()).containsExactlyInAnyOrder("id1");
    }


    @Test
    public void shouldCountSubjects() {
        //Given
        final Subject subject1 = Subject.builder().subjectId("id1").subjectCode("E01").build();
        final Subject subject2 = Subject.builder().subjectId("id2").subjectCode("E02").build();
        final List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("05.01.2016"), null, "prop1", subject1),
                new Entity("2", DateUtils.toDate("01.01.2016"), DateUtils.toDate("06.01.2016"), null, "prop2", subject1),
                new Entity("3", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject1),
                new Entity("4", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject2)
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2);

        BarChartService<Entity, SomeGroupByOptions> barChartService = new BarChartService<>();

        final ChartGroupByOptions<Entity, SomeGroupByOptions> settings = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(X_AXIS, SomeGroupByOptions.PROPERTY.getGroupByOptionAndParams())
                .build();
        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));

        //When
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChartCount = barChartService.getBarChart(
                settings, CountType.COUNT_OF_SUBJECTS, filtered);
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChartPercentage = barChartService.getBarChart(
                settings, CountType.PERCENTAGE_OF_ALL_SUBJECTS, filtered);

        //Then
        softly.assertThat(barChartCount.entrySet())
                .extracting(e -> e.getKey().getValue(X_AXIS),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple("prop1", 1.0, 1),
                        tuple("prop2", 1.0, 1),
                        tuple("prop3", 2.0, 2)
                );
        softly.assertThat(barChartPercentage.entrySet())
                .extracting(e -> e.getKey().getValue(X_AXIS),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple("prop1", 50.0, 1),
                        tuple("prop2", 50.0, 1),
                        tuple("prop3", 100.0, 2)
                );
    }

    @Test
    public void shouldCountEventsTrellised() {
        //Given
        final Subject subject1 = Subject.builder().subjectId("id1").subjectCode("E01").race("R1").build();
        final Subject subject2 = Subject.builder().subjectId("id2").subjectCode("E02").race("R1").build();
        final Subject subject3 = Subject.builder().subjectId("id3").subjectCode("E03").race("R2").build();
        final List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("05.01.2016"), null, "prop1", subject1),
                new Entity("2", DateUtils.toDate("01.01.2016"), DateUtils.toDate("06.01.2016"), null, "prop2", subject1),
                new Entity("3", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject2),
                new Entity("4", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop1", subject3),
                new Entity("5", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop2", subject3)
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2, subject3);

        BarChartService<Entity, SomeGroupByOptions> barChartService = new BarChartService<>();

        final ChartGroupByOptions<Entity, SomeGroupByOptions> settings = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(X_AXIS, SomeGroupByOptions.PROPERTY.getGroupByOptionAndParams())
                .withTrellisOption(SomeGroupByOptions.RACE.getGroupByOptionAndParams())
                .build();
        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));

        //When
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChart = barChartService.getBarChart(
                settings, CountType.COUNT_OF_EVENTS, filtered);

        //Then
        softly.assertThat(barChart.entrySet())
                .extracting(
                        e -> e.getKey().getTrellisByValues().get(SomeGroupByOptions.RACE),
                        e -> e.getKey().getValue(X_AXIS),
                        e -> e.getKey().getValue(COLOR_BY),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple("R2", "prop2", null, 1.0, 1),
                        tuple("R1", "prop2", null, 1.0, 1),
                        tuple("R1", "prop3", null, 1.0, 1),
                        tuple("R2", "prop1", null, 1.0, 1),
                        tuple("R1", "prop1", null, 1.0, 1)
                );
    }

    @Test
    public void shouldCountEventPercentageTrellised() {
        //Given
        final Subject subject1 = Subject.builder().subjectId("id1").subjectCode("E01").race("R1").build();
        final Subject subject2 = Subject.builder().subjectId("id2").subjectCode("E02").race("R1").build();
        final Subject subject3 = Subject.builder().subjectId("id3").subjectCode("E03").race("R2").build();
        final List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("05.01.2016"), null, "prop1", subject1),
                new Entity("2", DateUtils.toDate("01.01.2016"), DateUtils.toDate("06.01.2016"), null, "prop2", subject1),
                new Entity("3", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject2),
                new Entity("4", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop1", subject3),
                new Entity("5", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop2", subject3)
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2, subject3);

        BarChartService<Entity, SomeGroupByOptions> barChartService = new BarChartService<>();

        final ChartGroupByOptions<Entity, SomeGroupByOptions> settings = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(X_AXIS, SomeGroupByOptions.PROPERTY.getGroupByOptionAndParams())
                .withTrellisOption(SomeGroupByOptions.RACE.getGroupByOptionAndParams())
                .build();
        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));

        //When
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChart = barChartService.getBarChart(
                settings, CountType.PERCENTAGE_OF_EVENTS_WITHIN_PLOT, filtered);

        //Then
        softly.assertThat(barChart.entrySet())
                .extracting(
                        e -> e.getKey().getTrellisByValues().get(SomeGroupByOptions.RACE),
                        e -> e.getKey().getValue(X_AXIS),
                        e -> e.getKey().getValue(COLOR_BY),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple("R1", "prop2", null, 33.33, 1),
                        tuple("R1", "prop3", null, 33.33, 1),
                        tuple("R1", "prop1", null, 33.33, 1),
                        tuple("R2", "prop1", null, 50.0, 1),
                        tuple("R2", "prop2", null, 50.0, 1)
                );
    }


    @Test
    public void shouldCountEventPercentage100StackedTrellised() {
        //Given
        final Subject subject1 = Subject.builder().subjectId("id1").subjectCode("E01").race("R1").build();
        final Subject subject2 = Subject.builder().subjectId("id2").subjectCode("E02").race("R1").build();
        final Subject subject3 = Subject.builder().subjectId("id3").subjectCode("E03").race("R2").build();
        final Subject subject4 = Subject.builder().subjectId("id4").subjectCode("E04").race("R2").build();
        final Subject subject5 = Subject.builder().subjectId("id5").subjectCode("E05").race("R2").build();
        final List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("05.01.2016"), null, "prop1", subject1, "color1"),
                new Entity("2", DateUtils.toDate("01.01.2016"), DateUtils.toDate("06.01.2016"), null, "prop2", subject1, "color2"),
                new Entity("3", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject2, "color1"),
                new Entity("4", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop1", subject3, "color2"),
                new Entity("5", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop2", subject3, "color1"),
                new Entity("6", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject4, "color1"),
                new Entity("7", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop1", subject5, "color1"),
                new Entity("8", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop2", subject4, "color2"),
                new Entity("9", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject5, "color1"),
                new Entity("10", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop1", subject3, "color2"),
                new Entity("11", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop1", subject4, "color2")
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2, subject3);

        BarChartService<Entity, SomeGroupByOptions> barChartService = new BarChartService<>();

        final ChartGroupByOptions<Entity, SomeGroupByOptions> settings = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(X_AXIS, SomeGroupByOptions.PROPERTY.getGroupByOptionAndParams())
                .withOption(COLOR_BY, SomeGroupByOptions.COLOR_BY_PROPERTY.getGroupByOptionAndParams())
                .withTrellisOption(SomeGroupByOptions.RACE.getGroupByOptionAndParams())
                .build();
        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));

        //When
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChart = barChartService.getBarChart(
                settings, CountType.PERCENTAGE_OF_EVENTS_100_STACKED, filtered);

        //Then
        softly.assertThat(barChart.entrySet())
                .extracting(
                        e -> e.getKey().getTrellisByValues().get(SomeGroupByOptions.RACE),
                        e -> e.getKey().getValue(X_AXIS),
                        e -> e.getKey().getValue(COLOR_BY),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple("R1", "prop2", "color2", 100.0, 1),
                        tuple("R1", "prop3", "color1", 100.0, 1),
                        tuple("R1", "prop1", "color1", 100.0, 1),
                        tuple("R2", "prop1", "color1", 25.0, 1),
                        tuple("R2", "prop1", "color2", 75.0, 2),
                        tuple("R2", "prop2", "color1", 50.0, 1),
                        tuple("R2", "prop2", "color2", 50.0, 1),
                        tuple("R2", "prop3", "color1", 100.0, 2)
                );
    }

    @Test
    public void shouldCountSubjectsPercentage100StackedTrellised() {
        //Given
        final Subject subject1 = Subject.builder().subjectId("id1").subjectCode("E01").race("R1").build();
        final Subject subject2 = Subject.builder().subjectId("id2").subjectCode("E02").race("R1").build();
        final Subject subject3 = Subject.builder().subjectId("id3").subjectCode("E03").race("R2").build();
        final Subject subject4 = Subject.builder().subjectId("id4").subjectCode("E04").race("R2").build();
        final Subject subject5 = Subject.builder().subjectId("id5").subjectCode("E05").race("R2").build();
        final List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("05.01.2016"), null, "prop1", subject1, "color1"),
                new Entity("2", DateUtils.toDate("01.01.2016"), DateUtils.toDate("06.01.2016"), null, "prop2", subject1, "color2"),
                new Entity("3", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject2, "color1"),
                new Entity("4", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop1", subject3, "color2"),
                new Entity("5", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop2", subject3, "color1"),
                new Entity("6", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject4, "color1"),
                new Entity("8", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop2", subject4, "color2"),
                new Entity("9", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject5, "color1"),
                new Entity("10", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop1", subject3, "color2")
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2, subject3);

        BarChartService<Entity, SomeGroupByOptions> barChartService = new BarChartService<>();

        final ChartGroupByOptions<Entity, SomeGroupByOptions> settings = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(X_AXIS, SomeGroupByOptions.PROPERTY.getGroupByOptionAndParams())
                .withOption(COLOR_BY, SomeGroupByOptions.COLOR_BY_PROPERTY.getGroupByOptionAndParams())
                .withTrellisOption(SomeGroupByOptions.RACE.getGroupByOptionAndParams())
                .build();
        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));

        //When
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChart = barChartService.getBarChart(
                settings, CountType.PERCENTAGE_OF_SUBJECTS_100_STACKED, filtered);

        //Then
        softly.assertThat(barChart.entrySet())
                .extracting(
                        e -> e.getKey().getTrellisByValues().get(SomeGroupByOptions.RACE),
                        e -> e.getKey().getValue(X_AXIS),
                        e -> e.getKey().getValue(COLOR_BY),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple("R1", "prop2", "color2", 100.0, 1),
                        tuple("R1", "prop3", "color1", 100.0, 1),
                        tuple("R1", "prop1", "color1", 100.0, 1),
                        tuple("R2", "prop1", "color2", 100.0, 1),
                        tuple("R2", "prop2", "color1", 50.0, 1),
                        tuple("R2", "prop2", "color2", 50.0, 1),
                        tuple("R2", "prop3", "color1", 100.0, 2)
                );
    }

    @Test
    public void shouldCountSubjectsPercentageTrellised() {
        //Given
        final Subject subject1 = Subject.builder().subjectId("id1").subjectCode("E01").race("R1").build();
        final Subject subject2 = Subject.builder().subjectId("id2").subjectCode("E02").race("R1").build();
        final Subject subject3 = Subject.builder().subjectId("id3").subjectCode("E03").race("R1").build();
        final Subject subject4 = Subject.builder().subjectId("id4").subjectCode("E04").race("R2").build();
        final List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("05.01.2016"), null, "prop1", subject1),
                new Entity("2", DateUtils.toDate("01.01.2016"), DateUtils.toDate("06.01.2016"), null, "prop1", subject1),
                new Entity("3", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop1", subject2),
                new Entity("4", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop2", subject3),
                new Entity("5", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop3", subject3),
                new Entity("6", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null, "prop2", subject4)
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2, subject3, subject4);

        BarChartService<Entity, SomeGroupByOptions> barChartService = new BarChartService<>();

        final ChartGroupByOptions<Entity, SomeGroupByOptions> settings = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(X_AXIS, SomeGroupByOptions.PROPERTY.getGroupByOptionAndParams())
                .withTrellisOption(SomeGroupByOptions.RACE.getGroupByOptionAndParams())
                .build();
        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));

        //When
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChartCountWithinChart = barChartService.getBarChart(
                settings, CountType.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT, filtered);
        final Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChartCountWithinAll = barChartService.getBarChart(
                settings, CountType.PERCENTAGE_OF_ALL_SUBJECTS, filtered);

        //Then
        softly.assertThat(barChartCountWithinAll.entrySet())
                .extracting(
                        e -> e.getKey().getTrellisByValues().get(SomeGroupByOptions.RACE),
                        e -> e.getKey().getValue(X_AXIS),
                        e -> e.getKey().getValue(COLOR_BY),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple("R1", "prop1", null, 50.0, 2),
                        tuple("R1", "prop2", null, 25.0, 1),
                        tuple("R1", "prop3", null, 25.0, 1),
                        tuple("R2", "prop2", null, 25.0, 1)
                );
        softly.assertThat(barChartCountWithinChart.entrySet())
                .extracting(
                        e -> e.getKey().getTrellisByValues().get(SomeGroupByOptions.RACE),
                        e -> e.getKey().getValue(X_AXIS),
                        e -> e.getKey().getValue(COLOR_BY),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple("R1", "prop1", null, 66.67, 2),
                        tuple("R1", "prop2", null, 33.33, 1),
                        tuple("R1", "prop3", null, 33.33, 1),
                        tuple("R2", "prop2", null, 100.0, 1)
                );
    }

    @Test
    public void shouldCountEventsMultiValueAttrOnXAxis() {
        //Given
        final Subject subject1 = Subject.builder().subjectId("id1").subjectCode("E01").build();
        final Subject subject2 = Subject.builder().subjectId("id2").subjectCode("E02").build();
        final List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("05.01.2016"), Arrays.asList("v1", "v2"), "prop1", subject1),
                new Entity("2", DateUtils.toDate("01.01.2016"), DateUtils.toDate("06.01.2016"), Arrays.asList("v2", "v3"), "prop2", subject1),
                new Entity("3", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), Arrays.asList("v3", "v5", "v6"), "prop3", subject2),
                new Entity("4", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), Arrays.asList("v4", "v2"), "prop3", subject2)
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2);

        BarChartService<Entity, SomeGroupByOptions> barChartService = new BarChartService<>();

        final ChartGroupByOptions<Entity, SomeGroupByOptions> settings = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(X_AXIS, SomeGroupByOptions.CATEGORY.getGroupByOptionAndParams())
                .build();
        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));

        //When
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChart = barChartService.getBarChart(
                settings, CountType.COUNT_OF_EVENTS, filtered);

        //Then
        softly.assertThat(barChart.entrySet())
                .extracting(e -> e.getKey().getValue(X_AXIS),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple("v1", 1.0, 1),
                        tuple("v2", 3.0, 2),
                        tuple("v3", 2.0, 2),
                        tuple("v4", 1.0, 1),
                        tuple("v5", 1.0, 1),
                        tuple("v6", 1.0, 1)
                );
    }

    @Test
    public void shouldCountEventsAndSelectMultiValueAttr() {
        //Given
        final Subject subject1 = Subject.builder().subjectId("id1").subjectCode("E01").race("R1").build();
        final Subject subject2 = Subject.builder().subjectId("id2").subjectCode("E02").race("R2").build();
        final List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("05.01.2016"), Arrays.asList("v1", "v2"), "prop1", subject1),
                new Entity("2", DateUtils.toDate("01.01.2016"), DateUtils.toDate("06.01.2016"), Arrays.asList("v2", "v3"), "prop2", subject1),
                new Entity("3", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), Arrays.asList("v3", "v5", "v6"), "prop3", subject2),
                new Entity("4", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), Arrays.asList("v4", "v2"), "prop3", subject2)
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2);

        BarChartService<Entity, SomeGroupByOptions> barChartService = new BarChartService<>();

        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));
        final ChartGroupByOptions<Entity, SomeGroupByOptions> settings = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(X_AXIS, SomeGroupByOptions.CATEGORY.getGroupByOptionAndParams())
                .build();
        final HashMap<SomeGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(X_AXIS, "v2");
        final List<ChartSelectionItem<Entity, SomeGroupByOptions>> selectionItems = Collections.singletonList(
                ChartSelectionItem.of(selectedTrellises, selectedItems)
        );

        //When
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChart = barChartService.getBarChart(
                settings, CountType.COUNT_OF_EVENTS, filtered);
        final SelectionDetail selectionDetails = barChartService.getSelectionDetails(filtered, ChartSelection.of(settings, selectionItems));

        //Then
        softly.assertThat(barChart.entrySet())
                .extracting(e -> e.getKey().getValue(X_AXIS),
                        e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactlyInAnyOrder(
                        tuple("v1", 1.0, 1),
                        tuple("v2", 3.0, 2),
                        tuple("v3", 2.0, 2),
                        tuple("v4", 1.0, 1),
                        tuple("v5", 1.0, 1),
                        tuple("v6", 1.0, 1)
                );
        softly.assertThat(selectionDetails.getEventIds()).containsExactlyInAnyOrder("1", "2", "4");
        softly.assertThat(selectionDetails.getSubjectIds()).containsExactlyInAnyOrder("id1", "id2");
    }

    @Test
    public void shouldCountEventsNoAttrs() {
        //Given
        final Subject subject1 = Subject.builder().subjectId("id1").subjectCode("E01").race("R1").build();
        final Subject subject2 = Subject.builder().subjectId("id2").subjectCode("E02").race("R2").build();
        final List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("05.01.2016"), Arrays.asList("v1", "v2"), "prop1", subject1),
                new Entity("2", DateUtils.toDate("01.01.2016"), DateUtils.toDate("06.01.2016"), Arrays.asList("v2", "v3"), "prop2", subject1),
                new Entity("3", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), Arrays.asList("v3", "v5", "v6"), "prop3", subject2),
                new Entity("4", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), Arrays.asList("v4", "v2"), "prop3", subject2)
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2);

        BarChartService<Entity, SomeGroupByOptions> barChartService = new BarChartService<>();

        final ChartGroupByOptions<Entity, SomeGroupByOptions> settings = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .build();
        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));

        //When
        Map<GroupByKey<Entity, SomeGroupByOptions>, BarChartCalculationObject<Entity>> barChart = barChartService.getBarChart(
                settings, CountType.COUNT_OF_EVENTS, filtered);

        //Then
        softly.assertThat(barChart.entrySet()).hasSize(1);
        softly.assertThat(barChart.entrySet())
                .extracting(e -> e.getValue().getValue(), e -> e.getValue().getTotalSubject())
                .containsExactly(
                        tuple(4.0, 2)
                );
    }

    private enum SomeGroupByOptions implements GroupByOption<Entity> {
        SUBJECT {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("SUBJECT", e -> e.getSubject().getSubjectCode());
            }
        },
        @PopulationGroupingOption(PopulationGroupByOptions.RACE)
        RACE {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("RACE", e -> e.getSubject().getRace());
            }
        },
        PROPERTY {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("PROPERTY", e -> e.getSomeProperty());
            }
        },
        COLOR_BY_PROPERTY {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("COLOR_BY_PROPERTY", e -> e.getColorByProperty());
            }
        },
        DURATION {
            @Override
            public EntityAttribute<Entity> getAttribute(Params params) {
                return Attributes.getBinnedAttribute("DURATION", params, Entity::getStartDate, Entity::getEndDate);
            }

            @Override
            public EntityAttribute<Entity> getAttribute() {
                throw new IllegalStateException("params are required");
            }
        },
        START_DATE {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return Attributes.getBinnedAttribute("START_DATE", Params.builder().with(Param.BIN_SIZE, 1).build(), Entity::getStartDate);
            }
        },
        CATEGORY {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("CATEGORY", Entity::getCategories);
            }
        };
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @Getter
    @ToString
    private static class Entity implements HasStringId, HasSubject {
        private String id;
        private Date startDate;
        private Date endDate;
        private List<String> categories;
        private String someProperty;
        private Subject subject;
        private String colorByProperty;

        Entity(String id, Date startDate, Date endDate, List<String> categories, String someProperty, Subject subject) {
            this.id = id;
            this.startDate = startDate;
            this.endDate = endDate;
            this.categories = categories;
            this.someProperty = someProperty;
            this.subject = subject;
        }

        @Override
        public String getSubjectId() {
            return subject.getSubjectId();
        }
    }
}
