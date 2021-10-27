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

package com.acuity.visualisations.rawdatamodel.generators;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;

import java.util.Arrays;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public class AssessedTargetLesionGenerator {

    private static Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1").firstTreatmentDate(DaysUtil.toDate("2000-04-05"))
            .baselineDate(DaysUtil.toDate("2000-05-01")).build();
    private static Subject subject2 = Subject.builder().subjectId("subjectId2").subjectCode("subject2").firstTreatmentDate(DaysUtil.toDate("2000-01-01"))
            .baselineDate(DaysUtil.toDate("2000-01-01")).build();
    private static Subject subject3 = Subject.builder().subjectId("subjectId3").subjectCode("subject3").firstTreatmentDate(DaysUtil.toDate("2000-01-01"))
            .baselineDate(DaysUtil.toDate("2000-01-01")).build();
    private static Subject subject4 = Subject.builder().subjectId("subjectId4").subjectCode("subject4").firstTreatmentDate(DaysUtil.toDate("2000-01-01"))
            .baselineDate(DaysUtil.toDate("2000-01-01")).build();

    public static List<Subject> generateTumourPopulation() {
        return Arrays.asList(subject1, subject2, subject3, subject4);
    }

    public static List<AssessedTargetLesion> generateTumours() {
        final AssessedTargetLesionRaw subject1bestResponseAtl = AssessedTargetLesionRaw.builder().id("id3").subjectId("subjectId1")
                .targetLesionRaw(TargetLesionRaw.builder()
                        .baseline(false).lesionCountAtVisit(2).visitNumber(3).lesionDate(DaysUtil.toDate("2000-05-31"))
                        .sumPercentageChangeFromBaseline(-25.0).bestPercentageChange(true).build())
                .bestResponse("Partial Response").response("Partial Response")
                .assessmentFrequency(2)
                .build();
        final AssessedTargetLesionRaw subject2bestResponseAtl = AssessedTargetLesionRaw.builder().id("id5").subjectId("subjectId2")
                .targetLesionRaw(TargetLesionRaw.builder()
                        .baseline(false).lesionCountAtVisit(3).visitNumber(3).lesionDate(DaysUtil.toDate("2000-02-05"))
                        .sumPercentageChangeFromBaseline(-80.0).bestPercentageChange(false).build())
                .bestResponse("Complete Response").response("Complete Response")
                .assessmentFrequency(2)
                .build();
        final AssessedTargetLesionRaw subject4bestResponseAtl = AssessedTargetLesionRaw.builder().id("id9").subjectId("subjectId4")
                .targetLesionRaw(TargetLesionRaw.builder()
                        .baseline(false).lesionCountAtVisit(1).visitNumber(null).lesionDate(DaysUtil.toDate("2000-04-30"))
                        .sumPercentageChangeFromBaseline(-30.0).bestPercentageChange(false).build())
                .bestResponse("Partial Response").response("Partial Response")
                .assessmentFrequency(2)
                .build();
        return newArrayList(
                new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id1").subjectId("subjectId1")
                        .targetLesionRaw(TargetLesionRaw.builder().visitNumber(1).visitDate(DaysUtil.toDate("2000-05-15")).sumPercentageChangeFromBaseline(0.0)
                                .baseline(true).lesionCountAtVisit(2).bestPercentageChange(false).lesionDate(DaysUtil.toDate("2000-05-15")).build())
                        .assessmentFrequency(2)
                        .bestResponse("Partial Response").response("Partial Response")
                        .bestResponseEvent(subject1bestResponseAtl)
                        .build(), subject1),
                new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id2").subjectId("subjectId1").targetLesionRaw(TargetLesionRaw.builder()
                        .baseline(false).lesionCountAtVisit(2).visitNumber(2).visitDate(DaysUtil.toDate("2000-05-25"))
                        .lesionDate(DaysUtil.toDate("2000-05-25"))
                        .sumPercentageChangeFromBaseline(-20.0).bestPercentageChange(false).build())
                        .bestResponse("Partial Response").response("Partial Response")
                        .bestResponseEvent(subject1bestResponseAtl)
                        .assessmentFrequency(2)
                        .build(), subject1),
                new AssessedTargetLesion(subject1bestResponseAtl.toBuilder()
                        .bestResponseEvent(subject1bestResponseAtl).build(), subject1),
                new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id4").subjectId("subjectId2")
                        .targetLesionRaw(TargetLesionRaw.builder()
                        .baseline(true).lesionCountAtVisit(3).visitNumber(2).lesionDate(DaysUtil.toDate("2000-01-28"))
                        .sumPercentageChangeFromBaseline(-30.0).bestPercentageChange(false).build())
                        .bestResponse("Complete Response").response("Partial Response")
                        .bestResponseEvent(subject2bestResponseAtl)
                        .assessmentFrequency(2)
                        .build(), subject2),
                new AssessedTargetLesion(subject2bestResponseAtl.toBuilder()
                        .bestResponseEvent(subject2bestResponseAtl).build(), subject2),
                new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id6").subjectId("subjectId2")
                        .targetLesionRaw(TargetLesionRaw.builder()
                        .baseline(false).lesionCountAtVisit(3).visitNumber(5).lesionDate(DaysUtil.toDate("2000-03-10"))
                        .sumPercentageChangeFromBaseline(-100.0).bestPercentageChange(true).build())
                        .bestResponse("Complete Response").response("Partial Response")
                        .bestResponseEvent(subject2bestResponseAtl)
                        .assessmentFrequency(2)
                        .build(), subject2),
                new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id7").subjectId("subjectId3")
                        .targetLesionRaw(TargetLesionRaw.builder()
                        .baseline(true).lesionCountAtVisit(3).visitNumber(1).lesionDate(DaysUtil.toDate("2000-01-10"))
                        .sumPercentageChangeFromBaseline(-30.0).bestPercentageChange(true).build())
                        .bestResponse("Missing Target Lesions")
                        .response("Partial Response")
                        .assessmentFrequency(2)
                        .build(), subject3),
                new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id8").subjectId("subjectId4")
                        .targetLesionRaw(TargetLesionRaw.builder()
                        .baseline(true).lesionCountAtVisit(1).visitNumber(5).lesionDate(DaysUtil.toDate("2000-04-10"))
                        .sumPercentageChangeFromBaseline(-30.0).bestPercentageChange(true).build())
                        .bestResponse("Partial Response").response("Partial Response")
                        .bestResponseEvent(subject4bestResponseAtl)
                        .assessmentFrequency(2)
                        .build(), subject4),
                new AssessedTargetLesion(subject4bestResponseAtl.toBuilder()
                        .bestResponseEvent(subject4bestResponseAtl).build(), subject4));
    }
}
