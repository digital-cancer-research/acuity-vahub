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
