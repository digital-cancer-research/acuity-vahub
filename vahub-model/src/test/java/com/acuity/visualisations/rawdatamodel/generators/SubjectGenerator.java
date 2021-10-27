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
import com.acuity.visualisations.rawdatamodel.vo.Subject;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public abstract class SubjectGenerator {

    public static Subject SUBJECT1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1")
            .firstTreatmentDate(DaysUtil.toDate("2000-04-05")).baselineDate(DaysUtil.toDate("2000-05-01"))
            .build();

    public static List<Subject> generateSubjectListOfTwoWithSubjectIds() {
        Subject subject1 = Subject.builder().subjectId("subjectId1").age(60).clinicalStudyCode("studyId1").build();
        Subject subject2 = Subject.builder().subjectId("subjectId2").clinicalStudyCode("studyId2").attendedVisitNumbers(newArrayList("600")).build();
        return newArrayList(subject1, subject2);
    }

    public static List<Subject> generateSubjectListOfFiveWithSubjectIds() {
        Subject subject1 = Subject.builder().subjectId("subjectId1").age(60).weight(76.).height(180.)
                .sex("Male").race("White").clinicalStudyCode("studyId1").build();
        Subject subject2 = Subject.builder().subjectId("subjectId2").age(49).weight(64.).height(167.)
                .sex("Female").race("White").clinicalStudyCode("studyId2").attendedVisitNumbers(newArrayList("600")).build();
        Subject subject3 = Subject.builder().subjectId("subjectId3").age(28).weight(72.).height(190.)
                .sex("Male").race("Other").clinicalStudyCode("studyId2").build();
        Subject subject4 = Subject.builder().subjectId("subjectId4").age(99).weight(55.).height(159.)
                .sex("Female").race("Asian").clinicalStudyCode("studyId2").build();
        Subject subject5 = Subject.builder().subjectId("subjectId5").age(71).weight(71.).height(171.)
                .sex("Male").race("Black").clinicalStudyCode("studyId2").build();
        return newArrayList(subject1, subject2, subject3, subject4, subject5);
    }
}
