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
import com.acuity.visualisations.rawdatamodel.suites.interfaces.BoxPlotTests;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.RangeChartTests;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.ShiftPlotTests;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
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
import com.acuity.visualisations.rawdatamodel.vo.HasValueAndBaseline;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.plots.BoxplotCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.RangeChartCalculationObject;
import com.acuity.visualisations.rawdatamodel.vo.plots.ShiftPlotCalculationObject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.groups.Tuple.tuple;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
@Category({BoxPlotTests.class, RangeChartTests.class, ShiftPlotTests.class})
public class StatsServiceTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetBoxplotStats() {
        final Subject subject1 = Subject.builder().subjectId("id1").firstTreatmentDate(DateUtils.toDate("01.01.2016")).subjectCode("E01").build();
        final Subject subject2 = Subject.builder().subjectId("id2").firstTreatmentDate(DateUtils.toDate("03.01.2016")).subjectCode("E02").build();
        final Subject subject3 = Subject.builder().subjectId("id3").firstTreatmentDate(DateUtils.toDate("05.01.2016")).subjectCode("E03").build();
        final Subject subject4 = Subject.builder().subjectId("id4").firstTreatmentDate(DateUtils.toDate("05.01.2016")).subjectCode("E04").build();
        final List<Entity> events = Arrays.asList(
                new Entity("01", DateUtils.toDate("05.01.2016"), 01d, 0d, "", "prop1", subject1),
                new Entity("02", DateUtils.toDate("05.01.2016"), 04d, 0d, "", "prop1", subject2),
                new Entity("03", DateUtils.toDate("05.01.2016"), 07d, 0d, "", "prop1", subject3),
                new Entity("04", DateUtils.toDate("05.01.2016"), 08d, 3d, "", "prop1", subject1),
                new Entity("05", DateUtils.toDate("05.01.2016"), 10d, 3d, "", "prop1", subject2),
                new Entity("06", DateUtils.toDate("05.01.2016"), 11d, 3d, "", "prop1", subject3),

                new Entity("07", DateUtils.toDate("08.01.2016"), 01d, 0d, "", "prop3", subject1),
                new Entity("08", DateUtils.toDate("08.01.2016"), 02d, 0d, "", "prop3", subject2),
                new Entity("09", DateUtils.toDate("08.01.2016"), 02d, 0d, "", "prop3", subject3),
                new Entity("10", DateUtils.toDate("08.01.2016"), 15d, 0d, "", "prop3", subject4)
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2);

        StatsPlotService<Entity, SomeGroupByOptions> statsPlotService = new StatsPlotService<>();

        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));


        ChartGroupByOptions<Entity, SomeGroupByOptions> settingsDate = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, SomeGroupByOptions.EVENT_DATE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, SomeGroupByOptions.VALUE.getGroupByOptionAndParams())
                .withTrellisOption(SomeGroupByOptions.PROPERTY.getGroupByOptionAndParams())
                .build();

        final Map<GroupByKey<Entity, SomeGroupByOptions>, BoxplotCalculationObject> boxPlot = statsPlotService.getBoxPlot(settingsDate, filtered);
        softly.assertThat(boxPlot).isNotEmpty();
        softly.assertThat(boxPlot.entrySet())
                .filteredOn(
                        t -> "prop1".equals(t.getKey().getTrellisByValues().get(SomeGroupByOptions.PROPERTY))
                                && "2016-01-05".equals(t.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).toString())
                )
                .extracting(Map.Entry::getValue)
                .extracting(
                        BoxplotCalculationObject::getLowerWhisker,
                        BoxplotCalculationObject::getLowerQuartile,
                        BoxplotCalculationObject::getMedian,
                        BoxplotCalculationObject::getUpperQuartile,
                        BoxplotCalculationObject::getUpperWhisker,
                        BoxplotCalculationObject::getSubjectCount,
                        BoxplotCalculationObject::getEventCount,
                        BoxplotCalculationObject::getOutliers
                ).containsExactly(tuple(1.0, 4.75, 7.5, 9.5, 11.0, 3L, 6L, Collections.emptySet()));
        softly.assertThat(boxPlot.entrySet())
                .filteredOn(
                        t -> "prop3".equals(t.getKey().getTrellisByValues().get(SomeGroupByOptions.PROPERTY))
                                && "2016-01-08".equals(t.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).toString())
                )
                .extracting(Map.Entry::getValue)
                .extracting(
                        BoxplotCalculationObject::getLowerWhisker,
                        BoxplotCalculationObject::getLowerQuartile,
                        BoxplotCalculationObject::getMedian,
                        BoxplotCalculationObject::getUpperQuartile,
                        BoxplotCalculationObject::getUpperWhisker,
                        BoxplotCalculationObject::getSubjectCount,
                        BoxplotCalculationObject::getEventCount
                ).containsExactly(tuple(1.0, 1.75, 2.0, 5.25, 2.0, 4L, 4L)
        );
        softly.assertThat(boxPlot.entrySet())
                .filteredOn(
                        t -> "prop3".equals(t.getKey().getTrellisByValues().get(SomeGroupByOptions.PROPERTY))
                                && "2016-01-08".equals(t.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).toString())
                )
                .extracting(Map.Entry::getValue)
                .flatExtracting(s -> s.getOutliers())
                .extracting(o -> o.getOutlierValue(), o -> o.getSubjectId())
                .containsExactly(tuple(15.0, "id4"));
    }


    @Test
    public void shouldGetShiftPlotStats() {
        final Subject subject1 = Subject.builder().subjectId("id1").firstTreatmentDate(DateUtils.toDate("01.01.2016")).subjectCode("E01").build();
        final Subject subject2 = Subject.builder().subjectId("id2").firstTreatmentDate(DateUtils.toDate("03.01.2016")).subjectCode("E02").build();
        final Subject subject3 = Subject.builder().subjectId("id3").firstTreatmentDate(DateUtils.toDate("05.01.2016")).subjectCode("E03").build();
        final Subject subject4 = Subject.builder().subjectId("id4").firstTreatmentDate(DateUtils.toDate("05.01.2016")).subjectCode("E04").build();
        final List<Entity> events = Arrays.asList(
                new Entity("01", DateUtils.toDate("01.01.2016"), 01d, 00d, "units", "prop1", subject1),
                new Entity("02", DateUtils.toDate("02.01.2016"), 04d, 00d, "units", "prop1", subject2),
                new Entity("03", DateUtils.toDate("03.01.2016"), 07d, 00d, "other", "prop1", subject3),

                new Entity("04", DateUtils.toDate("04.01.2016"), 08d, 01d, "units", "prop1", subject1),
                new Entity("05", DateUtils.toDate("05.01.2016"), 10d, 01d, "units", "prop1", subject2),
                new Entity("06", DateUtils.toDate("06.01.2016"), 11d, 01d, "units", "prop1", subject3),
                new Entity("07", DateUtils.toDate("07.01.2016"), 02d, 01d, "units", "prop1", subject1),

                new Entity("08", DateUtils.toDate("08.01.2016"), 02d, 04d, "units", "prop1", subject2),
                new Entity("09", DateUtils.toDate("09.01.2016"), 06d, 04d, "units", "prop1", subject3),

                new Entity("10", DateUtils.toDate("10.01.2016"), 15d, 05d, "other", "prop2", subject4)
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2);

        StatsPlotService<Entity, SomeGroupByOptions> statsPlotService = new StatsPlotService<>();

        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));


        ChartGroupByOptions<Entity, SomeGroupByOptions> settingsDate = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, SomeGroupByOptions.BASELINE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, SomeGroupByOptions.VALUE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.UNIT, SomeGroupByOptions.UNIT.getGroupByOptionAndParams())
                .build();

        final Map<GroupByKey<Entity, SomeGroupByOptions>, ShiftPlotCalculationObject> shiftPlot = statsPlotService.getShiftPlot(settingsDate, filtered);
        softly.assertThat(shiftPlot).isNotEmpty();
        softly.assertThat(shiftPlot.keySet()).extracting(k -> k.getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS))
                .containsExactlyInAnyOrder(0d, 1d, 4d, 5d);
        softly.assertThat(shiftPlot.entrySet())
                .filteredOn(
                        t -> Objects.equals(t.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS), 0d)
                )
                .extracting(Map.Entry::getValue)
                .extracting(
                        ShiftPlotCalculationObject::getLow,
                        ShiftPlotCalculationObject::getHigh,
                        ShiftPlotCalculationObject::getUnit
                ).containsExactly(tuple(1.0, 7.0, "units"));

        softly.assertThat(shiftPlot.entrySet())
                .filteredOn(
                        t -> Objects.equals(t.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS), 1d)
                )
                .extracting(Map.Entry::getValue)
                .extracting(
                        ShiftPlotCalculationObject::getLow,
                        ShiftPlotCalculationObject::getHigh,
                        ShiftPlotCalculationObject::getUnit
                ).containsExactly(tuple(2.0, 11.0, "units"));

        softly.assertThat(shiftPlot.entrySet())
                .filteredOn(
                        t -> Objects.equals(t.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS), 4d)
                )
                .extracting(Map.Entry::getValue)
                .extracting(
                        ShiftPlotCalculationObject::getLow,
                        ShiftPlotCalculationObject::getHigh,
                        ShiftPlotCalculationObject::getUnit
                ).containsExactly(tuple(2.0, 6.0, "units"));

        softly.assertThat(shiftPlot.entrySet())
                .filteredOn(
                        t -> Objects.equals(t.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS), 5d)
                )
                .extracting(Map.Entry::getValue)
                .extracting(
                        ShiftPlotCalculationObject::getLow,
                        ShiftPlotCalculationObject::getHigh,
                        ShiftPlotCalculationObject::getUnit
                ).containsExactly(tuple(15.0, 15.0, "other"));

    }

    @Test
    public void shouldGetRangePlotStats() {
        final Subject subject1 = Subject.builder().subjectId("id1").firstTreatmentDate(DateUtils.toDate("01.01.2016")).subjectCode("E01").build();
        final Subject subject2 = Subject.builder().subjectId("id2").firstTreatmentDate(DateUtils.toDate("03.01.2016")).subjectCode("E02").build();
        final Subject subject3 = Subject.builder().subjectId("id3").firstTreatmentDate(DateUtils.toDate("05.01.2016")).subjectCode("E03").build();
        final Subject subject4 = Subject.builder().subjectId("id4").firstTreatmentDate(DateUtils.toDate("05.01.2016")).subjectCode("E04").build();
        final List<Entity> events = Arrays.asList(
                new Entity("01", DateUtils.toDate("05.01.2016"), 01d, 0d, "", "prop1", subject1),
                new Entity("02", DateUtils.toDate("05.01.2016"), 04d, 0d, "", "prop1", subject2),
                new Entity("03", DateUtils.toDate("05.01.2016"), 07d, 0d, "", "prop1", subject3),
                new Entity("04", DateUtils.toDate("05.01.2016"), 08d, 3d, "", "prop1", subject1),
                new Entity("05", DateUtils.toDate("05.01.2016"), 10d, 3d, "", "prop1", subject2),
                new Entity("06", DateUtils.toDate("05.01.2016"), 11d, 3d, "", "prop1", subject3),

                new Entity("07", DateUtils.toDate("08.01.2016"), 01d, 0d, "", "prop3", subject1),
                new Entity("08", DateUtils.toDate("08.01.2016"), 02d, 0d, "", "prop3", subject2),
                new Entity("09", DateUtils.toDate("08.01.2016"), 04d, 0d, "", "prop3", subject3),
                new Entity("10", DateUtils.toDate("08.01.2016"), 05d, 0d, "", "prop3", subject4)
        );
        final List<Subject> subjects = Arrays.asList(subject1, subject2);

        StatsPlotService<Entity, SomeGroupByOptions> statsPlotService = new StatsPlotService<>();

        final FilterResult<Entity> filtered = new FilterResult<>(new FilterQuery<Entity>(subjects, PopulationFilters.empty())).withResults(events, events)
                .withPopulationFilteredResults(new FilterResult<>(new FilterQuery<Subject>(subjects, PopulationFilters.empty()))
                        .withResults(subjects, subjects));


        ChartGroupByOptions<Entity, SomeGroupByOptions> settingsDate = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, SomeGroupByOptions.EVENT_DATE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, SomeGroupByOptions.VALUE.getGroupByOptionAndParams())
                .withTrellisOption(SomeGroupByOptions.PROPERTY.getGroupByOptionAndParams())
                .build();

        final Map<GroupByKey<Entity, SomeGroupByOptions>, RangeChartCalculationObject> shiftPlot = statsPlotService.getRangePlot(settingsDate, filtered);
        softly.assertThat(shiftPlot).isNotEmpty();
        softly.assertThat(shiftPlot.entrySet())
                .filteredOn(
                        t -> "prop1".equals(t.getKey().getTrellisByValues().get(SomeGroupByOptions.PROPERTY))
                                && "2016-01-05".equals(t.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).toString())
                )
                .extracting(Map.Entry::getValue)
                .extracting(
                        RangeChartCalculationObject::getDataPoints,
                        RangeChartCalculationObject::getMean,
                        RangeChartCalculationObject::getMin,
                        RangeChartCalculationObject::getMax,
                        RangeChartCalculationObject::getStdDev,
                        RangeChartCalculationObject::getStdErr
                ).containsExactly(tuple(6, 6.83, 1.0, 11.0, 3.76, 1.54));

        softly.assertThat(shiftPlot.entrySet())
                .filteredOn(
                        t -> "prop3".equals(t.getKey().getTrellisByValues().get(SomeGroupByOptions.PROPERTY))
                                && "2016-01-08".equals(t.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).toString())
                )
                .extracting(Map.Entry::getValue)
                .extracting(
                        RangeChartCalculationObject::getDataPoints,
                        RangeChartCalculationObject::getMean,
                        RangeChartCalculationObject::getMin,
                        RangeChartCalculationObject::getMax,
                        RangeChartCalculationObject::getStdDev,
                        RangeChartCalculationObject::getStdErr
                ).containsExactly(tuple(4, 3.0, 1.0, 5.0, 1.83, 0.92));

    }


    private enum SomeGroupByOptions implements GroupByOption<Entity> {
        SUBJECT {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("SUBJECT", e -> e.getSubject().getSubjectCode());
            }
        },
        VALUE {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("VALUE", e -> e.getValue());
            }
        },
        UNIT {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("UNIT", e -> e.getUnit());
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
        EVENT_DATE {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return Attributes.getBinnedAttribute("EVENT_DATE", Params.builder().with(Param.BIN_SIZE, 1).build(), Entity::getEventDate);
            }

            @Override
            public EntityAttribute<Entity> getAttribute(Params params) {
                return Attributes.getBinnedAttribute("EVENT_DATE", params, Entity::getEventDate);
            }
        },
        BASELINE {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("BASELINE", Entity::getBaseline);
            }
        }
    }

    @AllArgsConstructor
    @EqualsAndHashCode
    @Getter
    @ToString
    private static class Entity implements HasStringId, HasSubject, HasValueAndBaseline {
        private String id;
        private Date eventDate;
        private Double value;
        private Double baseline;
        private String unit;
        private String someProperty;
        private Subject subject;

        @Override
        public String getSubjectId() {
            return subject.getSubjectId();
        }

        @Override
        public Double getResultValue() {
            return value;
        }

        @Override
        public Double getBaselineValue() {
            return baseline;
        }

        @Override
        public Double getChangeFromBaselineRaw() {
            return null;
        }

        @Override
        public Boolean getCalcChangeFromBaselineIfNull() {
            return true;
        }
    }
}
