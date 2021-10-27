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
import com.acuity.visualisations.rawdatamodel.vo.CtDnaRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;

import java.util.Arrays;
import java.util.List;

public abstract class CtDnaGenerator {
    private static final String SAMPLE_DATE_1 = "2000-01-10";

    private static final String GENE_1 = "g1";
    private static final String MUT_1 = "m1";

    public static List<CtDna> generateCtDnaList() {
        Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1")
                .firstTreatmentDate(DaysUtil.toDate("2000-01-01")).build();
        CtDna ctDna11 = new CtDna(CtDnaRaw.builder().id("cId11").subjectId(subject1.getId())
                .sampleDate(DaysUtil.toDate(SAMPLE_DATE_1)).gene(GENE_1).mutation(MUT_1)
                .trackedMutation("YES").reportedVaf(0.321).reportedVafCalculated(0.321)
                .reportedVafPercent(32.1).reportedVafCalculatedPercent(32.1).visitNumber(1.).visitName("visit1")
                .build(), subject1);

        return Arrays.asList(ctDna11);
    }
}
