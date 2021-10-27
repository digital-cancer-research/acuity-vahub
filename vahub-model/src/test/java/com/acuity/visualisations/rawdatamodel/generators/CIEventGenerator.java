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

import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.CIEventRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import com.google.common.collect.Sets;

import java.util.*;

import static com.google.common.collect.Lists.newArrayList;

public abstract class CIEventGenerator {

    public static List<CIEvent> generateAnyCIEventListOfTwo() {
        CIEvent ciEvent1 = new CIEvent(CIEventRaw.builder().subjectId("subjectId1")
                .description1("desc1").ecgAtTheEventTime("Yes").build(),
                Subject.builder().subjectId("subjectId1").clinicalStudyCode("studyId1").build());
        CIEvent ciEvent2 = new CIEvent(CIEventRaw.builder().subjectId("subjectId2")
                .description1("desc2").ecgAtTheEventTime("No").build(),
                Subject.builder().subjectId("subjectId2").clinicalStudyCode("studyId2").build());
        return newArrayList(ciEvent1, ciEvent2);
    }

    public static List<CIEvent> generateCIEventListOfSevenWithEcgAtTheEventTimeAndFinalDiagnosis() {
        CIEvent ciEvent1 = new CIEvent(CIEventRaw.builder().id("id1").startDate(DateUtils.toDate("01.10.2016"))
                .description1("desc1").ecgAtTheEventTime("Yes").aeNumber(1).finalDiagnosis("finalDiagnosis1").build(),
                Subject.builder().subjectCode("subject1").subjectId("subjectId1").clinicalStudyCode("studyId1").build());
        CIEvent ciEvent2 = new CIEvent(CIEventRaw.builder().id("id2").startDate(DateUtils.toDate("02.10.2016"))
                .description1("desc2").ecgAtTheEventTime("No").aeNumber(2).finalDiagnosis("finalDiagnosis1").build(),
                Subject.builder().subjectCode("subject2").subjectId("subjectId2").clinicalStudyCode("studyId2").build());
        CIEvent ciEvent3 = new CIEvent(CIEventRaw.builder().id("id3").startDate(DateUtils.toDate("03.10.2016"))
                .description1("desc3").finalDiagnosis("finalDiagnosis1").build(),
                Subject.builder().subjectCode("subject3").subjectId("subjectId3").clinicalStudyCode("studyId3").build());
        CIEvent ciEvent4 = new CIEvent(CIEventRaw.builder().id("id4").startDate(DateUtils.toDate("04.10.2016"))
                .description1("desc1").ecgAtTheEventTime("Yes").finalDiagnosis("finalDiagnosis2").build(),
                Subject.builder().subjectCode("subject1").subjectId("subjectId1").clinicalStudyCode("studyId1").build());
        CIEvent ciEvent5 = new CIEvent(CIEventRaw.builder().id("id5").startDate(DateUtils.toDate("05.10.2016"))
                .description1("desc2").ecgAtTheEventTime("No").finalDiagnosis("finalDiagnosis2").build(),
                Subject.builder().subjectCode("subject2").subjectId("subjectId2").clinicalStudyCode("studyId2").build());
        CIEvent ciEvent6 = new CIEvent(CIEventRaw.builder().id("id6").startDate(DateUtils.toDateTime("06.10.2016 12:34"))
                .description1("desc3").finalDiagnosis("finalDiagnosis2").build(),
                Subject.builder().subjectCode("subject3").subjectId("subjectId3").clinicalStudyCode("studyId3").build());
        CIEvent ciEvent7 = new CIEvent(CIEventRaw.builder().id("id7").startDate(DateUtils.toDate("07.10.2016"))
                .description1("desc3").ecgAtTheEventTime("No").finalDiagnosis("finalDiagnosis2").cieSymptomsDuration("cieSymptomsDuration7").build(),
                Subject.builder().subjectCode("subject7").subjectId("subjectId7").clinicalStudyCode("studyId7").build());
        return newArrayList(ciEvent1, ciEvent2, ciEvent3, ciEvent4, ciEvent5, ciEvent6, ciEvent7);
    }

    public static List<CIEvent> generateRandomCIEventList(long count) {
        final Random random = new Random();
        final ArrayList<CIEvent> res = new ArrayList<>();
        for (long i = 0; i < count; i++) {
            res.add(new CIEvent(CIEventRaw.builder()
                    .aeNumber(random.nextInt())
                    .angiographyDate(new Date(random.nextInt()))
                    .id(UUID.randomUUID().toString())
                    .subjectId(UUID.randomUUID().toString())
                    .startDate(new Date(random.nextInt()))
                    .term(UUID.randomUUID().toString())
                    .ischemicSymptoms(UUID.randomUUID().toString())
                    .cieSymptomsDuration(UUID.randomUUID().toString())
                    .symptPromptUnschedHospit(UUID.randomUUID().toString())
                    .eventSuspDueToStentThromb(UUID.randomUUID().toString())
                    .previousEcgAvailable(UUID.randomUUID().toString())
                    .previousEcgDate(new Date(random.nextInt()))
                    .ecgAtTheEventTime(UUID.randomUUID().toString())
                    .noEcgAtTheEventTime(UUID.randomUUID().toString())
                    .localCardiacBiomarkersDrawn(UUID.randomUUID().toString())
                    .coronaryAngiography(UUID.randomUUID().toString())
                    .finalDiagnosis(UUID.randomUUID().toString())
                    .otherDiagnosis(UUID.randomUUID().toString())
                    .description1(UUID.randomUUID().toString())
                    .description2(UUID.randomUUID().toString())
                    .description3(UUID.randomUUID().toString())
                    .description4(UUID.randomUUID().toString())
                    .description5(UUID.randomUUID().toString())
                    .build(),
                    Subject.builder()
                    .subjectId(UUID.randomUUID().toString())
                    .subjectCode(UUID.randomUUID().toString())
                    .clinicalStudyCode(UUID.randomUUID().toString())
                    .studyPart(UUID.randomUUID().toString())
                    .datasetId(UUID.randomUUID().toString())
                    .attendedAnalysisVisits(UUID.randomUUID().toString())
                    .durationOnStudy(random.nextInt())
                    .randomised(UUID.randomUUID().toString())
                    .dateOfRandomisation(new Date(random.nextInt()))
                    .firstTreatmentDate(new Date(random.nextInt()))
                    .lastTreatmentDate(new Date(random.nextInt()))
                    .withdrawal(UUID.randomUUID().toString())
                    .dateOfWithdrawal(new Date(random.nextInt()))
                    .reasonForWithdrawal(UUID.randomUUID().toString())
                    .deathFlag(UUID.randomUUID().toString())
                    .phase(UUID.randomUUID().toString())
                    .dateOfDeath(new Date(random.nextInt()))
                    .plannedArm(UUID.randomUUID().toString())
                    .actualArm(UUID.randomUUID().toString())
                    .doseCohort(UUID.randomUUID().toString())
                    .otherCohort(UUID.randomUUID().toString())
                    .sex(UUID.randomUUID().toString())
                    .race(UUID.randomUUID().toString())
                    .ethnicGroup(UUID.randomUUID().toString())
                    .age(random.nextInt())
                    .siteId(UUID.randomUUID().toString())
                    .region(UUID.randomUUID().toString())
                    .country(UUID.randomUUID().toString())
                    .specifiedEthnicGroup(UUID.randomUUID().toString())
                    .medicalHistories(Sets.newHashSet(UUID.randomUUID().toString()))
                            .build()));
        }
        return res;
    }
}
