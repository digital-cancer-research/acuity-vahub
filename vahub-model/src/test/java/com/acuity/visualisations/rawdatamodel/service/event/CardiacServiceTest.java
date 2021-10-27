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

package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.test.annotation.SpringITTest;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.CardiacRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;

@RunWith(SpringRunner.class)
@SpringITTest
public class CardiacServiceTest {
    private DoDCommonService tableService = new DoDCommonService();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Subject subject1 = Subject.builder()
            .subjectId("sid1")
            .clinicalStudyCode("STUDYID001")
            .studyPart("A")
            .subjectCode("E01")
            .actualArm("Placebo")
            .drugFirstDoseDate("Placebo", DateUtils.toDate("01.08.2015"))
            .firstTreatmentDate(toDate("2014-12-02"))
            .dateOfRandomisation(toDate("2015-01-02"))
            .build();

    private static Cardiac cardiac1 = new Cardiac(CardiacRaw.builder()
            .id("c1")
            .measurementCategory("MC1")
            .measurementName("MN1")
            .measurementTimePoint(new Date())
            .analysisVisit(1.23)
            .visitNumber(2.34)
            .resultValue(3.45)
            .resultUnit("mg/ml")
            .baselineValue(4.56)
            .changeFromBaselineRaw(56.7)
            .baselineFlag("Y")
            .method("Met")
            .protocolScheduleTimepoint("Sch")
            .clinicallySignificant("Sig")
            .dateOfLastDose(new Date())
            .lastDoseAmount("Amnt")
            .atrialFibrillation("At")
            .sinusRhythm("SR")
            .reasonNoSinusRhythm("NoSR")
            .heartRhythm("HR")
            .heartRhythmOther("HRO")
            .extraSystoles("ES")
            .specifyExtraSystoles("SES")
            .typeOfConduction("TOC")
            .conduction("Cnd")
            .reasonAbnormalConduction("RAC")
            .sttChanges("STT")
            .stSegment("STS")
            .wave("Wav")
            .beatGroupNumber(1)
            .beatNumberWithinBeatGroup(7)
            .numberOfBeatsInAverageBeat(8)
            .beatGroupLengthInSec(6.78)
            .comment("Cmnt")
            .build().runPrecalculations(), subject1);

    @Test
    public void shouldGetAcuityDetailsOnDemandColumnsInCorrectOrder() {
        List<Cardiac> cardiacs = Collections.singletonList(cardiac1);
        Map<String, String> columns = tableService.getDoDColumns(Column.DatasetType.ACUITY, cardiacs);
        softly.assertThat(columns.keySet()).containsExactly(
                "studyId", "studyPart", "subjectId",
                "measurementCategory", "measurementName", "measurementTimePoint",
                "daysOnStudy", "visitNumber",
                "resultValue", "resultUnit",
                "baselineValue", "changeFromBaseline", "percentChangeFromBaseline", "baselineFlag",
                "clinicallySignificant", "protocolScheduleTimepoint", "method",
                "dateOfLastDose", "lastDoseAmount",
                "atrialFibrillation", "sinusRhythm", "reasonNoSinusRhythm",
                "heartRhythm", "heartRhythmOther", "extraSystoles", "specifyExtraSystoles",
                "typeOfConduction", "conduction", "reasonAbnormalConduction",
                "sttChanges", "stSegment", "wave",
                "beatGroupNumber", "beatNumberWithinBeatGroup", "numberOfBeatsInAverageBeat",
                "beatGroupLengthInSec", "comment");

        softly.assertThat(columns.values()).containsExactly(
                "Study id", "Study part", "Subject id",
                "Measurement category", "Measurement name", "Measurement time point",
                "Days on study", "Visit number",
                "Result value", "Result unit",
                "Baseline value", "Change from baseline", "Percent change from baseline", "Baseline flag",
                "Clinically significant", "Protocol schedule timepoint", "Method",
                "Date of last drug dose", "Last drug dose amount",
                "Atrial fibrillation", "Sinus rhythm", "Reason, no sinus rhythm",
                "Heart rhythm", "Heart rhythm, other", "Extra systoles", "Specify extra systoles",
                "Type of conduction", "Conduction", "Reason, abnormal conduction",
                "ST-T changes", "ST segment", "T-wave",
                "Beat group number", "Beat number within beat group", "Number of beats in average beat",
                "Beat group length (sec)", "Cardiologist comment");
    }
}
