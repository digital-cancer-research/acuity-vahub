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

package com.acuity.visualisations.rawdatamodel.util;

import com.acuity.visualisations.rawdatamodel.vo.CvotEndpointRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import com.google.common.collect.Sets;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by knml167 on 6/15/2017.
 */
public final class CvotEndpointGenerator {
    private CvotEndpointGenerator() {
    }

    public static List<CvotEndpoint> generateRandomCvotEndpointList(long count, long subjectCount, int categoriesDictSize,
                                                                    int startDateYearFrom, int startDateYearTo) {

        final Random random = new Random();

        List<List<String>> cat = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<String> dict = new ArrayList<>();
            for (int j = 0; j < categoriesDictSize; j++) {
                dict.add(UUID.randomUUID().toString());
            }
            cat.add(dict);
        }
        final ArrayList<Subject> subjects = new ArrayList<>();
        for (long i = 0; i < subjectCount; i++) {
            subjects.add(Subject.builder()
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
                    .build());
        }

        final ArrayList<CvotEndpoint> res = new ArrayList<>();
        for (long i = 0; i < count; i++) {
            Subject subject = subjects.get(random.nextInt(subjects.size()));

            final int from = (int) LocalDate.of(startDateYearFrom, 1, 1).toEpochDay();
            final int to = (int) LocalDate.of(startDateYearTo, 12, 31).toEpochDay();
            long randomDay = from + random.nextInt(to - from);
            final LocalDate start = LocalDate.ofEpochDay(randomDay);

            res.add(new CvotEndpoint(CvotEndpointRaw.builder()
                    .id(UUID.randomUUID().toString())
                    .subjectId(subject.getSubjectId())
                    .aeNumber(random.nextInt())
                    .startDate(Date.from(start.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()))
                    .term(UUID.randomUUID().toString())
                    .category1(cat.get(0).get(random.nextInt(categoriesDictSize)))
                    .category2(cat.get(1).get(random.nextInt(categoriesDictSize)))
                    .category3(cat.get(2).get(random.nextInt(categoriesDictSize)))
                    .description1(cat.get(3).get(random.nextInt(categoriesDictSize)))
                    .description2(cat.get(4).get(random.nextInt(categoriesDictSize)))
                    .description3(cat.get(5).get(random.nextInt(categoriesDictSize)))
                    .build(),
                    subject
            ));
        }
        return res;
    }
}
