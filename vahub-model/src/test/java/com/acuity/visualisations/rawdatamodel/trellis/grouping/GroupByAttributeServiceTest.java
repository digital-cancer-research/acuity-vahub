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

package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.EntityAttribute;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class GroupByAttributeServiceTest {
    @Test
    public void shouldGroupBySimpleAttribute() {
        //Given
        List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("05.01.2016"), null),
                new Entity("2", DateUtils.toDate("01.01.2016"), DateUtils.toDate("06.01.2016"), null),
                new Entity("3", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null)
        );
        //When
        final ChartGroupByOptions<Entity, SomeGroupByOptions> options = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, SomeGroupByOptions.START_DATE.getGroupByOptionAndParams())
                .build();
        final Map<GroupByKey<Entity, SomeGroupByOptions>, Collection<Entity>> res = GroupByAttributes.group(events, options);
        //Then
        assertThat(res.size()).isEqualTo(2);
        assertThat(res.entrySet()).extracting(
                e -> e.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).toString(),
                e -> e.getValue().size()
        ).containsExactlyInAnyOrder(
                tuple("2016-01-01", 2),
                tuple("2016-01-05", 1)
        );
    }

    @Test
    public void shouldGroupByCollectionAttribute() {
        /*
         * When some attribute returns collection, event should be included into multiple groups per each collection element
         * */
        //Given
        List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("05.01.2016"), null),
                new Entity("2", DateUtils.toDate("02.01.2016"), DateUtils.toDate("06.01.2016"), null),
                new Entity("3", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), null)
        );
        //When
        final ChartGroupByOptions<Entity, SomeGroupByOptions> options = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, SomeGroupByOptions.DURATION.getGroupByOptionAndParams())
                .build();
        final Map<GroupByKey<Entity, SomeGroupByOptions>, Collection<Entity>> res = GroupByAttributes.group(events, options);
        //Then
        assertThat(res.size()).isEqualTo(7);
        assertThat(res.entrySet()).extracting(
                e -> e.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).toString(),
                e -> e.getValue().size()
        ).containsExactlyInAnyOrder(
                tuple("2016-01-02", 2),
                tuple("2016-01-01", 1),
                tuple("2016-01-04", 2),
                tuple("2016-01-03", 2),
                tuple("2016-01-06", 2),
                tuple("2016-01-05", 3),
                tuple("2016-01-07", 1)
        );
    }

    @Test
    public void shouldGroupByMultipleCollectionAttributes1() {
        /*
         * When more than one grouping attributes returns collection,
         * grouping should include event into keys for each combination of its collections elements (cross join)
         * */

        //Given
        List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("03.01.2016"), Arrays.asList("cat1", "cat2"))
        );
        //When
        final ChartGroupByOptions<Entity, SomeGroupByOptions> options = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, SomeGroupByOptions.DURATION.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, SomeGroupByOptions.CATEGORY.getGroupByOptionAndParams())
                .build();
        final Map<GroupByKey<Entity, SomeGroupByOptions>, Collection<Entity>> res = GroupByAttributes.group(events, options);
        //Then
        assertThat(res.size()).isEqualTo(6); //3 days * 2 categories should be 6
        assertThat(res.entrySet()).extracting(
                e -> e.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).toString() + " : "
                        + e.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).toString(),
                e -> e.getValue().size()
        ).containsExactlyInAnyOrder(
                tuple("2016-01-02 : cat2", 1),
                tuple("2016-01-02 : cat1", 1),
                tuple("2016-01-03 : cat2", 1),
                tuple("2016-01-03 : cat1", 1),
                tuple("2016-01-01 : cat2", 1),
                tuple("2016-01-01 : cat1", 1)
        );
    }

    @Test
    public void shouldGroupByMultipleCollectionAttributes2() {
        /*
         * When more than one grouping attributes returns collection,
         * grouping should include event into keys for each combination of its collections elements (cross join)
         * */

        //Given
        List<Entity> events = Arrays.asList(
                new Entity("1", DateUtils.toDate("01.01.2016"), DateUtils.toDate("05.01.2016"), Arrays.asList("cat1", "cat2")),
                new Entity("2", DateUtils.toDate("02.01.2016"), DateUtils.toDate("06.01.2016"), Arrays.asList("cat2", "cat3")),
                new Entity("3", DateUtils.toDate("05.01.2016"), DateUtils.toDate("07.01.2016"), Arrays.asList("cat2", "cat4"))
        );
        //When
        final ChartGroupByOptions<Entity, SomeGroupByOptions> options = ChartGroupByOptions.<Entity, SomeGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, SomeGroupByOptions.DURATION.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, SomeGroupByOptions.CATEGORY.getGroupByOptionAndParams())
                .build();
        final Map<GroupByKey<Entity, SomeGroupByOptions>, Collection<Entity>> res = GroupByAttributes.group(events, options);
        //Then
        assertThat(res.size()).isEqualTo(20);
        assertThat(res.entrySet()).extracting(
                e -> e.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.X_AXIS).toString() + " : "
                        + e.getKey().getValue(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS).toString(),
                e -> e.getValue().size()
        ).containsExactlyInAnyOrder(
                tuple("2016-01-03 : cat2", 2),
                tuple("2016-01-02 : cat1", 1),
                tuple("2016-01-07 : cat2", 1),
                tuple("2016-01-03 : cat3", 1),
                tuple("2016-01-05 : cat4", 1),
                tuple("2016-01-06 : cat3", 1),
                tuple("2016-01-04 : cat3", 1),
                tuple("2016-01-01 : cat1", 1),
                tuple("2016-01-02 : cat3", 1),
                tuple("2016-01-04 : cat2", 2),
                tuple("2016-01-06 : cat2", 2),
                tuple("2016-01-05 : cat1", 1),
                tuple("2016-01-07 : cat4", 1),
                tuple("2016-01-03 : cat1", 1),
                tuple("2016-01-02 : cat2", 2),
                tuple("2016-01-06 : cat4", 1),
                tuple("2016-01-05 : cat3", 1),
                tuple("2016-01-01 : cat2", 1),
                tuple("2016-01-04 : cat1", 1),
                tuple("2016-01-05 : cat2", 3)
        );
    }


    private enum SomeGroupByOptions implements GroupByOption<Entity> {
        DURATION {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return Attributes.getBinnedAttribute("duration", Params.builder().with(Param.BIN_SIZE, 1).with(Param.BIN_INCL_DURATION, true).build(),
                        Entity::getStartDate, Entity::getEndDate);
            }
        },
        START_DATE {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return Attributes.getBinnedAttribute("startDate", Params.builder().with(Param.BIN_SIZE, 1).with(Param.BIN_INCL_DURATION, true).build(),
                        Entity::getStartDate);
            }
        },
        CATEGORY {
            @Override
            public EntityAttribute<Entity> getAttribute() {
                return EntityAttribute.attribute("category", Entity::getCategories);
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

        @Override
        public Subject getSubject() {
            return null;
        }

        @Override
        public String getSubjectId() {
            return "-";
        }
    }
}
