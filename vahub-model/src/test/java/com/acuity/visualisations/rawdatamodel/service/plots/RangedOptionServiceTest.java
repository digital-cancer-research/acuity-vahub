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

import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.annotations.RangeOption;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubjectId;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartOptionRange;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.collections.MapUtils;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static java.util.Arrays.asList;

public class RangedOptionServiceTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final Subject SUBJECT1 = Subject.builder().subjectId("id1")
            .firstTreatmentDate(DateUtils.toDate("01.01.2016")).age(34).weight(45.7)
            .drugsRawMaxDose("drug1", 50.)
            .drugsRawMaxDose("drug2", 55.)
            .build();
    private static final Subject SUBJECT2 = Subject.builder().subjectId("id2")
            .firstTreatmentDate(DateUtils.toDate("06.01.2016")).age(74).weight(65.7)
            .drugsRawMaxDose("drug1", 40.)
            .drugsRawMaxDose("drug2", 45.)
            .build();
    private static final Subject SUBJECT3 = Subject.builder().subjectId("id3")
            .firstTreatmentDate(DateUtils.toDate("01.01.2015")).age(64).weight(45.7)
            .drugsRawMaxDose("drug1", 20.)
            .drugsRawMaxDose("drug2", 25.)
            .build();
    private static final Subject SUBJECT4 = Subject.builder().subjectId("id4")
            .firstTreatmentDate(DateUtils.toDate("06.11.2016")).age(54).weight(65.7)
            .drugsRawMaxDose("drug1", 30.)
            .drugsRawMaxDose("drug2", 35.)
            .build();
    private static final Subject SUBJECT5 = Subject.builder().subjectId("id5")
            .firstTreatmentDate(DateUtils.toDate("01.07.2014")).age(53).weight(85.7)
            .drugsRawMaxDose("drug3", 5.)
            .drugsRawMaxDose("drug4", 15.)
            .build();
    private static final Subject SUBJECT6 = Subject.builder().subjectId("id6")
            .firstTreatmentDate(DateUtils.toDate("26.01.2016")).age(74).weight(75.7)
            .drugsRawMaxDose("drug3", 35.)
            .drugsRawMaxDose("drug4", 10.)
            .build();
    private static final Subject SUBJECT7 = Subject.builder().subjectId("id7")
            .firstTreatmentDate(DateUtils.toDate("27.01.2016")).age(34).weight(55.0)
            .drugsRawMaxDose("drug1", 70.)
            .drugsRawMaxDose("drug3", 5.)
            .build();
    private static final Subject SUBJECT8 = Subject.builder().subjectId("id8")
            .firstTreatmentDate(DateUtils.toDate("28.01.2016")).age(45).weight(63.4)
            .drugsRawMaxDose("drug1", 50.)
            .drugsRawMaxDose("drug2", 55.)
            .drugsRawMaxDose("drug3", 100.)
            .drugsRawMaxDose("drug4", 100.)
            .build();
    private static final Subject SUBJECT9 = Subject.builder().subjectId("id9")
            .firstTreatmentDate(DateUtils.toDate("29.01.2016")).age(89).weight(100.0)
            .drugsRawMaxDose("drug1", 150.)
            .drugsRawMaxDose("drug4", 155.)
            .build();
    private static final Subject SUBJECT10 = Subject.builder().subjectId("id10")
            .firstTreatmentDate(DateUtils.toDate("06.01.2016")).age(4).weight(65.7)
            .drugsRawMaxDose("drug4", 40.)
            .drugsRawMaxDose("drug2", 200.)
            .build();
    private static final Subject SUBJECT11 = Subject.builder().subjectId("id11")
            .firstTreatmentDate(DateUtils.toDate("01.01.2016")).age(64).weight(86.6)
            .drugsRawMaxDose("drug2", 50.)
            .drugsRawMaxDose("drug3", 55.)
            .build();
    private static final Subject SUBJECT12 = Subject.builder().subjectId("id12")
            .firstTreatmentDate(DateUtils.toDate("06.01.2016")).age(25).weight(85.5)
            .drugsRawMaxDose("drug3", 50.)
            .drugsRawMaxDose("drug4", 55.)
            .build();

    private static final List<Subject> SUBJECTS = Arrays.asList(SUBJECT1, SUBJECT2, SUBJECT3, SUBJECT4,
            SUBJECT5, SUBJECT6, SUBJECT7, SUBJECT8, SUBJECT9, SUBJECT10, SUBJECT11, SUBJECT12);

    private static final List<Entity> EVENTS = asList(new Entity("1", SUBJECT1), new Entity("2", SUBJECT2),
            new Entity("3", SUBJECT3), new Entity("4", SUBJECT4), new Entity("5", SUBJECT5),
            new Entity("6", SUBJECT6), new Entity("7", SUBJECT7), new Entity("8", SUBJECT8),
            new Entity("9", SUBJECT9), new Entity("10", SUBJECT10), new Entity("11", SUBJECT11),
            new Entity("12", SUBJECT12), new Entity("13", SUBJECT3), new Entity("14", SUBJECT4),
            new Entity("15", SUBJECT5));

    private RangedOptionService rangedOptionService = new RangedOptionService();

    @Test
    public void shouldGetRangeFunction() {
        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(SUBJECTS, PopulationFilters.empty())).withResults(EVENTS, EVENTS)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(SUBJECTS, PopulationFilters.empty()))
                        .withResults(SUBJECTS, SUBJECTS));

        //When
        Function<SomeGroupByOptions, ?> rangedOptionFunction = rangedOptionService.getRangeFunction(filtered, true, null);
        Map<String, BarChartOptionRange<Date>> dateMap =
                (Map<String, BarChartOptionRange<Date>>) rangedOptionFunction.apply(SomeGroupByOptions.DATE_PROPERTY);
        Set<BarChartOptionRange<Date>> dateRanges = new HashSet<>(dateMap.values());
        softly.assertThat(dateRanges).containsOnly(
                new BarChartOptionRange<>(DateUtils.toDate("01.01.2015"), DateUtils.toDate("01.01.2015")),
                new BarChartOptionRange<>(DateUtils.toDate("01.01.2016"), DateUtils.toDate("06.01.2016")),
                new BarChartOptionRange<>(DateUtils.toDate("01.07.2014"), DateUtils.toDate("01.07.2014")),
                new BarChartOptionRange<>(DateUtils.toDate("06.11.2016"), DateUtils.toDate("06.11.2016")),
                new BarChartOptionRange<>(DateUtils.toDate("26.01.2016"), DateUtils.toDate("29.01.2016"))
        );

        Map<String, BarChartOptionRange<Long>> longMap =
                (Map<String, BarChartOptionRange<Long>>) rangedOptionFunction.apply(SomeGroupByOptions.LONG_PROPERTY);
        Set<BarChartOptionRange<Long>> longRanges = new HashSet<>(longMap.values());
        softly.assertThat(longRanges).containsOnly(
                new BarChartOptionRange<>(64L, 74L),
                new BarChartOptionRange<>(25L, 25L),
                new BarChartOptionRange<>(89L, 89L),
                new BarChartOptionRange<>(53L, 54L),
                new BarChartOptionRange<>(4L, 4L),
                new BarChartOptionRange<>(34L, 45L)
        );

        Map<String, BarChartOptionRange<Double>> doubleMap =
                (Map<String, BarChartOptionRange<Double>>) rangedOptionFunction.apply(SomeGroupByOptions.DOUBLE_PROPERTY);
        Set<BarChartOptionRange<Double>> doubleRanges = new HashSet<>(doubleMap.values());
        softly.assertThat(doubleRanges).containsOnly(
                new BarChartOptionRange<>(65.7, 65.7),
                new BarChartOptionRange<>(100.0, 100.0),
                new BarChartOptionRange<>(85.5, 86.6),
                new BarChartOptionRange<>(75.7, 75.7),
                new BarChartOptionRange<>(55.0, 63.4),
                new BarChartOptionRange<>(45.7, 45.7));
    }

    @Test
    public void shouldGetRangeFunctionForMapWithoutDrugName() {
        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(SUBJECTS, PopulationFilters.empty())).withResults(EVENTS, EVENTS)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(SUBJECTS, PopulationFilters.empty()))
                        .withResults(SUBJECTS, SUBJECTS));
        //When
        Function<SomeGroupByOptions, ?> rangedOptionFunction = rangedOptionService.getRangeFunction(filtered, false, null);
        Map<String, Map<String, BarChartOptionRange<Double>>> stringDoubleMap =
                (Map<String, Map<String, BarChartOptionRange<Double>>>) rangedOptionFunction.apply(SomeGroupByOptions.MAP_STRING_DOUBLE_PROPERTY);

        softly.assertThat(stringDoubleMap.keySet()).containsOnly("drug1", "drug2", "drug3", "drug4");

        softly.assertThat(stringDoubleMap.get("drug1").values()).containsOnly(
                new BarChartOptionRange<>(20., 40.),
                new BarChartOptionRange<>(50., 50.),
                new BarChartOptionRange<>(70., 70.),
                new BarChartOptionRange<>(150., 150.));
        softly.assertThat(stringDoubleMap.get("drug2").values()).containsOnly(
                new BarChartOptionRange<>(25., 50.),
                new BarChartOptionRange<>(200., 200.),
                new BarChartOptionRange<>(55., 55.));
        softly.assertThat(stringDoubleMap.get("drug3").values()).containsOnly(
                new BarChartOptionRange<>(50., 50.),
                new BarChartOptionRange<>(5., 5.),
                new BarChartOptionRange<>(100., 100.),
                new BarChartOptionRange<>(35., 35.),
                new BarChartOptionRange<>(55., 55.));
        softly.assertThat(stringDoubleMap.get("drug4").values()).containsOnly(
                new BarChartOptionRange<>(40., 55.),
                new BarChartOptionRange<>(10., 15.),
                new BarChartOptionRange<>(100., 100.),
                new BarChartOptionRange<>(155., 155.));
    }

    @Test
    public void shouldGetRangeFunctionForMapWithDrugName() {
        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(SUBJECTS, PopulationFilters.empty())).withResults(EVENTS, EVENTS)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(SUBJECTS, PopulationFilters.empty()))
                        .withResults(SUBJECTS, SUBJECTS));

        //When
        Function<SomeGroupByOptions, ?> rangedOptionFunction = rangedOptionService.getRangeFunction(filtered, false, "drug1");
        Map<String, Map<String, BarChartOptionRange<Double>>> stringDoubleMap =
                (Map<String, Map<String, BarChartOptionRange<Double>>>) rangedOptionFunction.apply(SomeGroupByOptions.MAP_STRING_DOUBLE_PROPERTY);

        softly.assertThat(stringDoubleMap.keySet()).containsOnly("drug1");

        softly.assertThat(stringDoubleMap.get("drug1").values()).containsOnly(
                new BarChartOptionRange<>(20., 40.),
                new BarChartOptionRange<>(50., 50.),
                new BarChartOptionRange<>(70., 70.),
                new BarChartOptionRange<>(150., 150.));
    }

    private enum SomeGroupByOptions implements GroupByOption<Entity> {
        SUBJECT {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("SUBJECT", e -> e.getSubject().getSubjectCode());
            }
        },

        @RangeOption(RangeOption.RangeOptionType.DATE)
        DATE_PROPERTY {
            @Override
            public EntityAttribute<Entity> getAttribute(Params params) {
                return getSubjectRangedAttribute(getAttribute(), "DATE_PROPERTY", params);
            }

            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("DATE_PROPERTY", e -> e.getSubject().getFirstTreatmentDate());
            }
        },
        @RangeOption(RangeOption.RangeOptionType.DOUBLE)
        DOUBLE_PROPERTY {
            @Override
            public EntityAttribute<Entity> getAttribute(Params params) {
                return getSubjectRangedAttribute(getAttribute(), "DOUBLE_PROPERTY", params);
            }

            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("DOUBLE_PROPERTY", e -> e.getSubject().getWeight());
            }
        },

        @RangeOption(RangeOption.RangeOptionType.MAP_STRING_DOUBLE)
        MAP_STRING_DOUBLE_PROPERTY {
            @Override
            public EntityAttribute<Entity> getAttribute(Params params) {
                return getSubjectRangedAttribute(getAttribute(), "MAP_STRING_DOUBLE_PROPERTY", params);
            }

            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("MAP_STRING_DOUBLE_PROPERTY", e -> e.getSubject().getDrugsRawMaxDoses());
            }
        },
        @RangeOption(RangeOption.RangeOptionType.LONG)
        LONG_PROPERTY {
            @Override
            public EntityAttribute<Entity> getAttribute(Params params) {
                return getSubjectRangedAttribute(getAttribute(), "LONG_PROPERTY", params);
            }

            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("LONG_PROPERTY", e -> e.getSubject().getAge());
            }
        };

        private static EntityAttribute<Entity> getSubjectRangedAttribute(EntityAttribute<Entity> attribute, String name, Params params) {
            final Map<String, BarChartOptionRange<?>> context = params == null ? null
                    : (Map<String, BarChartOptionRange<?>>) params.get(Param.CONTEXT);
            return MapUtils.isEmpty(context) ? attribute
                    : EntityAttribute.attribute(name, (Entity entity) -> context.get(entity.getSubjectId()));
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @Getter
    @ToString
    private static class Entity implements HasSubjectId, HasStringId {
        private String id;
        private Subject subject;

        @Override
        public String getSubjectId() {
            return subject.getSubjectId();
        }
    }
}
