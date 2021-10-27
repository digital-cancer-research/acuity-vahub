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
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverity;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;

import static com.acuity.visualisations.rawdatamodel.generators.SubjectGenerator.SUBJECT1;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

public abstract class AEGenerator {

    private static AeSeverity aeSeverity1 = AeSeverity.builder().severity("sev1").severityNum(1).build();
    private static AeSeverityRaw aeSeverityRaw1 = AeSeverityRaw.builder().id("aes1").aeId("id1").severity(aeSeverity1)
            .ongoing(true).startDate(DaysUtil.toDate("2000-01-01"))
            .endDate(DaysUtil.toDate("2000-01-02")).drugsActionTaken(newHashMap()).build();
    public static Ae AE1 = new Ae(AeRaw.builder().id("id1").pt("pt1").hlt("hlt1").soc("soc1")
                        .specialInterestGroups(newArrayList("sig1")).aeNumber(1)
                        .aeSeverities(newArrayList(aeSeverityRaw1)).actionTaken("act1").serious("serious")
                        .aeOfSpecialInterest("interest1").causality("causaluty1").comment("comment1").text("text1")
                        .doseLimitingToxicity("doseLimit1").immuneMediated("immune1").build(), SUBJECT1);
}
