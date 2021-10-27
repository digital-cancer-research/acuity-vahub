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

package com.acuity.visualisations.rawdatamodel.service.timeline;

import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineBucket;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineCollector;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.HasStartEndDate;
import lombok.Value;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.function.BiFunction;

import static org.assertj.core.api.Assertions.assertThat;


public class TimelineCollectorTest {

    /*
     Subsequent events of the same type (1) with one-day gap:
     A[---] (1)
      |    B[--] (1)
      |   | |  |
      [A  ] [B ] <- Buckets
      1234567890 <- Days
     */
    @Test
    public void subsequentEventsOfTheSameType() {
        List<TimelineBucket<ContinuousDemoEvent>> buckets = TimelineCollector.collect(
                Arrays.asList(
                        new ContinuousDemoEvent("A", "2018-01-01", "2018-01-05", "Type1"),
                        new ContinuousDemoEvent("B", "2018-01-07", "2018-01-10", "Type1")
                ), classifierEquality, true);

        assertThat(buckets).hasSize(2);

        assertThat(buckets.get(0).getItems()).hasSize(1);
        assertThat(buckets.get(0).getItems().get(0).getId()).isEqualTo("A");
        assertThat(buckets.get(1).getItems()).hasSize(1);
        assertThat(buckets.get(1).getItems().get(0).getId()).isEqualTo("B");
    }

    /*
     Subsequent events of the different types (1 and 2):
     A[---] (1)
      |    B[--] (2)
      |   | |  |
      [A  ] [B ] <- Buckets
      1234567890 <- Days
     */
    @Test
    public void subsequentEventsOfTheDifferentType() {
        List<TimelineBucket<ContinuousDemoEvent>> buckets = TimelineCollector.collect(
                Arrays.asList(
                        new ContinuousDemoEvent("A", "2018-01-01", "2018-01-05", "Type1"),
                        new ContinuousDemoEvent("B", "2018-01-07", "2018-01-10", "Type2")
                ), classifierEquality, true);

        assertThat(buckets).hasSize(2);

        assertThat(buckets.get(0).getItems()).hasSize(1);
        assertThat(buckets.get(0).getItems().get(0).getId()).isEqualTo("A");
        assertThat(buckets.get(1).getItems()).hasSize(1);
        assertThat(buckets.get(1).getItems().get(0).getId()).isEqualTo("B");
    }

    /*
     Subsequent events of the different types (1 and 2) with small gap:
     A[---] (1)
      |   B[--] (2)
      |   ||  |
      [A  ][B ] <- Buckets
      1234567890 <- Days
     */
    @Test
    public void subsequentEventsOfTheDifferentTypeSmallGap() {
        List<TimelineBucket<ContinuousDemoEvent>> buckets = TimelineCollector.collect(
                Arrays.asList(
                        new ContinuousDemoEvent("A", "2018-01-01", "2018-01-05", "Type1"),
                        new ContinuousDemoEvent("B", "2018-01-06", "2018-01-09", "Type2")
                ), classifierEquality, true);

        assertThat(buckets).hasSize(2);

        assertThat(buckets.get(0).getItems()).hasSize(1);
        assertThat(buckets.get(0).getItems().get(0).getId()).isEqualTo("A");
        assertThat(buckets.get(1).getItems()).hasSize(1);
        assertThat(buckets.get(1).getItems().get(0).getId()).isEqualTo("B");
    }

    /*
     Subsequent events of the same types (1) and small gap (day or less):
     A[---] (1)
      |   B[---] (1)
      |   ||   |
      [A,B     ] <- Buckets
      1234567890 <- Days
     */
    @Test
    public void subsequentEventsOfTheSameTypeWithSmallGapsShouldBeMerged() {
        List<TimelineBucket<ContinuousDemoEvent>> buckets = TimelineCollector.collect(
                Arrays.asList(
                        new ContinuousDemoEvent("A", "2018-01-01", "2018-01-05", "Type1"),
                        new ContinuousDemoEvent("B", "2018-01-06", "2018-01-10", "Type1")
                ), classifierEquality, true);

        assertThat(buckets).hasSize(1);

        assertThat(buckets.get(0).getItems()).hasSize(2);
        assertThat(buckets.get(0).getItems()).extracting("id").containsOnly("A", "B");
    }

    /*
     Subsequent events of the same types (1) without gap:
     A[---] (1)
      |  B[---] (1)
      |   |   |
      [A,B    ] <- Buckets
      1234567890 <- Days
     */
    @Test
    public void subsequentEventsOfTheSameTypeWithoutGapsShouldBeMerged() {
        List<TimelineBucket<ContinuousDemoEvent>> buckets = TimelineCollector.collect(
                Arrays.asList(
                        new ContinuousDemoEvent("A", "2018-01-01", "2018-01-05", "Type1"),
                        new ContinuousDemoEvent("B", "2018-01-05", "2018-01-10", "Type1")
                ), classifierEquality, true);

        assertThat(buckets).hasSize(1);

        assertThat(buckets.get(0).getItems()).hasSize(2);
        assertThat(buckets.get(0).getItems()).extracting("id").containsOnly("A", "B");
    }

    /*
     Subsequent events of the same types (1) without gap, reversed order:
     B[---] (1)
      |  A[---] (1)
      |   |   |
      [B,A    ] <- Buckets
      1234567890 <- Days
     */
    @Test
    public void subsequentEventsOfTheSameTypeWithoutGapsShouldBeMerged2() {
        List<TimelineBucket<ContinuousDemoEvent>> buckets = TimelineCollector.collect(
                Arrays.asList(
                        new ContinuousDemoEvent("B", "2018-01-05", "2018-01-10", "Type1"),
                        new ContinuousDemoEvent("A", "2018-01-01", "2018-01-05", "Type1")
                ), classifierEquality, true);

        assertThat(buckets).hasSize(1);

        assertThat(buckets.get(0).getItems()).hasSize(2);
        assertThat(buckets.get(0).getItems()).extracting("id").containsOnly("A", "B");
    }

    /*
     Subsequent events of the same types (1) with overlap:
     A[---] (1)
      | B[---] (1)
      |  ||  |
      [A,B   ] <- Buckets
      1234567890 <- Days
     */
    @Test
    public void subsequentEventsOfTheSameTypeWithOverlapShouldBeMerged() {
        List<TimelineBucket<ContinuousDemoEvent>> buckets = TimelineCollector.collect(
                Arrays.asList(
                        new ContinuousDemoEvent("A", "2018-01-01", "2018-01-05", "Type1"),
                        new ContinuousDemoEvent("B", "2018-01-04", "2018-01-08", "Type1")
                ), classifierEquality, true);

        assertThat(buckets).hasSize(1);

        assertThat(buckets.get(0).getItems()).hasSize(2);
        assertThat(buckets.get(0).getItems()).extracting("id").containsOnly("A", "B");
    }

    /*
     Continuous events, all different type:
     A[-----]
      |B[-----------]
      | |   |C[-----]
      | |   |D[-----------]   I[----------------->
      | |   | |     |E[-----]  |
      | |   | |     | |   | |  | F[-----]
      | |   | |     | |   | |  | G[-----]
      | |   | |     | |   | |  |  |     | H[---]
      [A|A,B|B|B,C,D|D|D,E|E]  [I |I,F,G|I |I,H|I> <- Buckets
      12345678901234567890123456789012345678901234 <- Days
     */
    @Test
    public void shouldWorkWithIntersectionsOfDifferentTypes() {
        List<TimelineBucket<ContinuousDemoEvent>> buckets = TimelineCollector.collect(
                Arrays.asList(
                        new ContinuousDemoEvent("A", "2018-01-01", "2018-01-07", "Type1"),
                        new ContinuousDemoEvent("B", "2018-01-03", "2018-01-15", "Type2"),
                        new ContinuousDemoEvent("C", "2018-01-09", "2018-01-15", "Type3"),
                        new ContinuousDemoEvent("D", "2018-01-09", "2018-01-21", "Type4"),
                        new ContinuousDemoEvent("E", "2018-01-17", "2018-01-23", "Type5"),
                        new ContinuousDemoEvent("F", "2018-01-29", "2018-02-05", "Type6"),
                        new ContinuousDemoEvent("G", "2018-01-29", "2018-02-05", "Type7"),
                        new ContinuousDemoEvent("H", "2018-02-08", "2018-02-12", "Type8"),
                        new ContinuousDemoEvent("I", "2018-01-26", null, "Type9")
                ), classifierEquality, true);

        assertThat(buckets).hasSize(12);

        assertThat(buckets.get(0).getItems()).hasSize(1);
        assertThat(buckets.get(0).getItems().get(0).getId()).isEqualTo("A");

        assertThat(buckets.get(1).getItems()).hasSize(2);
        assertThat(buckets.get(1).getItems()).extracting("id").containsOnly("A", "B");

        assertThat(buckets.get(2).getItems()).hasSize(1);
        assertThat(buckets.get(2).getItems().get(0).getId()).isEqualTo("B");

        assertThat(buckets.get(3).getItems()).hasSize(3);
        assertThat(buckets.get(3).getItems()).extracting("id").containsOnly("B", "C", "D");

        assertThat(buckets.get(4).getItems()).hasSize(1);
        assertThat(buckets.get(4).getItems().get(0).getId()).isEqualTo("D");

        assertThat(buckets.get(5).getItems()).hasSize(2);
        assertThat(buckets.get(5).getItems()).extracting("id").containsOnly("D", "E");

        assertThat(buckets.get(6).getItems()).hasSize(1);
        assertThat(buckets.get(6).getItems().get(0).getId()).isEqualTo("E");

        assertThat(buckets.get(7).getItems()).hasSize(1);
        assertThat(buckets.get(7).getItems().get(0).getId()).isEqualTo("I");

        assertThat(buckets.get(8).getItems()).hasSize(3);
        assertThat(buckets.get(8).getItems()).extracting("id").containsOnly("I", "F", "G");

        assertThat(buckets.get(9).getItems()).hasSize(1);
        assertThat(buckets.get(9).getItems().get(0).getId()).isEqualTo("I");

        assertThat(buckets.get(10).getItems()).hasSize(2);
        assertThat(buckets.get(10).getItems()).extracting("id").containsOnly("I", "H");
        assertThat(buckets.get(10).getEndDate()).isNotNull();

        assertThat(buckets.get(11).getItems()).hasSize(1);
        assertThat(buckets.get(11).getItems().get(0).getId()).isEqualTo("I");
        assertThat(buckets.get(11).getEndDate()).isNull();
    }

    /*
     Continuous events, all same type:
     A[-----]
     |B[-----------]
     | |   |C[-----]
     | |   |D[-----------]   I[----------------->
     | |   | |     |E[-----]  |
     | |   | |     | |   | |  | F[-----]
     | |   | |     | |   | |  | G[-----]
     | |   | |     | |   | |  |  |     | H[---]
     [A,B,C,D,E            ]  [I,F,G,H          > <- Buckets
     12345678901234567890123456789012345678901234 <- Days
    */
    @Test
    public void shouldWorkWithIntersectionsOfTheSameType() {
        List<TimelineBucket<ContinuousDemoEvent>> buckets = TimelineCollector.collect(
                Arrays.asList(
                        new ContinuousDemoEvent("A", "2018-01-01", "2018-01-07", "Type1"),
                        new ContinuousDemoEvent("B", "2018-01-03", "2018-01-15", "Type1"),
                        new ContinuousDemoEvent("C", "2018-01-09", "2018-01-15", "Type1"),
                        new ContinuousDemoEvent("D", "2018-01-09", "2018-01-21", "Type1"),
                        new ContinuousDemoEvent("E", "2018-01-17", "2018-01-23", "Type1"),
                        new ContinuousDemoEvent("F", "2018-01-29", "2018-02-05", "Type1"),
                        new ContinuousDemoEvent("G", "2018-01-29", "2018-02-05", "Type1"),
                        new ContinuousDemoEvent("H", "2018-02-08", "2018-02-12", "Type1"),
                        new ContinuousDemoEvent("I", "2018-01-26", null, "Type1")
                ), classifierEquality, true);

        assertThat(buckets).hasSize(2);

        assertThat(buckets.get(0).getItems()).hasSize(5);
        assertThat(buckets.get(0).getItems()).extracting("id").containsOnly("A", "B", "C", "D", "E");
        assertThat(buckets.get(0).getEndDate()).isNotNull();

        assertThat(buckets.get(1).getItems()).hasSize(4);
        assertThat(buckets.get(1).getItems()).extracting("id").containsOnly("I", "F", "G", "H");
        assertThat(buckets.get(1).getEndDate()).isNull();
    }

    private static BiFunction<ContinuousDemoEvent, ContinuousDemoEvent, Boolean> classifierEquality =
            (a, b) -> a.getClassifier().equals(b.getClassifier());

    @Value
    private static final class ContinuousDemoEvent implements HasStartEndDate {
        private String id;
        private Date startDate;
        private Date endDate;
        private String classifier;

        private ContinuousDemoEvent(String id, String startDate, String endDate, String classifier) {
            this.id = id;
            this.startDate = DaysUtil.toDate(startDate);
            this.endDate = endDate == null ? null : DaysUtil.toDate(endDate);
            this.classifier = classifier;
        }
    }
}
