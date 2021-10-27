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

package com.acuity.visualisations.rawdatamodel.service.dod;

import com.acuity.visualisations.rawdatamodel.generators.CIEventGenerator;
import com.acuity.visualisations.rawdatamodel.generators.CtDnaGenerator;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.google.common.collect.Comparators;
import lombok.SneakyThrows;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class DoDCommonServiceTest {

    private static final String SORT_ATTRIBUTE = "resultValue";
    private DoDCommonService doDCommonService = new DoDCommonService();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    // Can't use Double.parseDouble as it throws exception for specific Double locales (where "," is used as divider).
    private DecimalFormat decimalFormat = new DecimalFormat();

    @Test
    public void shouldGetDoD() {
        final List<CIEvent> ciEvents = CIEventGenerator.generateCIEventListOfSevenWithEcgAtTheEventTimeAndFinalDiagnosis();

        final List<Map<String, String>> doDData = doDCommonService.getColumnData(DatasetType.ACUITY, ciEvents, Collections.emptyList(), 0, Integer.MAX_VALUE, true);

        softly.assertThat(doDData).hasSize(ciEvents.size());

        for (int i = 0; i < ciEvents.size(); i++) {
            final CIEvent ciEvent = ciEvents.get(i);
            final Map<String, String> dod = doDData.get(i);
            softly.assertThat(ciEvent.getEvent().getId()).isEqualTo(dod.get("eventId"));
            softly.assertThat(ciEvent.getSubject().getSubjectCode()).isEqualTo(dod.get("subjectId"));
            softly.assertThat(ciEvent.getSubject().getClinicalStudyCode()).isEqualTo(dod.get("studyId"));
            softly.assertThat(ciEvent.getEvent().getDescription1()).isEqualTo(dod.get("description1"));
            softly.assertThat(ciEvent.getEvent().getEcgAtTheEventTime()).isEqualTo(dod.get("ecgAtTheEventTime"));
            softly.assertThat(ciEvent.getEvent().getFinalDiagnosis()).isEqualTo(dod.get("finalDiagnosis"));
            assertThat(ciEvent.getEvent().getStartDate()).isInSameDayAs(dod.get("startDate"));
        }
    }

    @Test
    public void shouldGetDoDSortedLimited() {
        final List<CIEvent> ciEvents = CIEventGenerator.generateCIEventListOfSevenWithEcgAtTheEventTimeAndFinalDiagnosis();

        final List<Map<String, String>> doDData1 = doDCommonService.getColumnData(DatasetType.ACUITY, ciEvents, Collections.singletonList(new SortAttrs("studyId", false)), 0, 1, true);

        softly.assertThat(doDData1).hasSize(1);
        softly.assertThat(doDData1.get(0).get("studyId")).isEqualTo("studyId1");

        final List<Map<String, String>> doDData2 = doDCommonService.getColumnData(DatasetType.ACUITY, ciEvents, Collections.singletonList(new SortAttrs("studyId", true)), 0, 1, true);

        softly.assertThat(doDData2).hasSize(1);
        softly.assertThat(doDData2.get(0).get("studyId")).isEqualTo("studyId7");

        final List<Map<String, String>> doDData3 = doDCommonService.getColumnData(DatasetType.ACUITY, ciEvents, Collections.singletonList(new SortAttrs("cieSymptomsDuration", false)), 0, 3, true);

        softly.assertThat(doDData3).hasSize(3);
        softly.assertThat(doDData3.get(0).get("studyId")).isEqualTo("studyId7");
    }

    @Test
    public void shouldGetDoDSortedComplicatedComparator() {
        final List<CIEvent> ciEvents = CIEventGenerator.generateCIEventListOfSevenWithEcgAtTheEventTimeAndFinalDiagnosis();

        final List<Map<String, String>> doDData = doDCommonService.getColumnData(DatasetType.ACUITY, ciEvents,
                Arrays.asList(new SortAttrs("subjectId", false), new SortAttrs("finalDiagnosis", true)),
                0, Integer.MAX_VALUE, true);

        softly.assertThat(doDData).hasSize(7);
        softly.assertThat(doDData.get(0).get("subjectId")).isEqualTo("subject1");
        softly.assertThat(doDData.get(0).get("finalDiagnosis")).isEqualTo("finalDiagnosis2");
        softly.assertThat(doDData.get(1).get("subjectId")).isEqualTo("subject1");
        softly.assertThat(doDData.get(1).get("finalDiagnosis")).isEqualTo("finalDiagnosis1");
    }

    @Test
    public void shouldGetCIEventDoDColumns() {
        final List<CIEvent> ciEvents = CIEventGenerator.generateCIEventListOfSevenWithEcgAtTheEventTimeAndFinalDiagnosis();

        final Map<String, String> doDColumns = doDCommonService.getDoDColumns(DatasetType.ACUITY, ciEvents);

        softly.assertThat(doDColumns.keySet()).containsExactly(
                "studyId",
                "subjectId",
                "finalDiagnosis",
                "startDate",
                "startTime",
                "aeNumber",
                "cieSymptomsDuration",
                "ecgAtTheEventTime",
                "description1");
        softly.assertThat(doDColumns.values()).containsExactly(
                "Study id",
                "Subject id",
                "Final diagnosis",
                "Start date",
                "Start time",
                "Ae number",
                "Cie symptoms duration",
                "Ecg at the event time",
                "Description 1");
    }

    @Test
    public void shouldGetCtDnaDoDColumns() {
        final List<CtDna> ctDnaEvents = CtDnaGenerator.generateCtDnaList();

        final Map<String, String> doDColumns = doDCommonService.getDoDColumns(DatasetType.ACUITY, ctDnaEvents);

        softly.assertThat(doDColumns.keySet()).containsExactly(
                "subjectId",
                "sampleDate",
                "visitNumber",
                "visitName",
                "gene",
                "mutation",
                "trackedMutation",
                "reportedVafPercent",
                "reportedVaf");
        softly.assertThat(doDColumns.values()).containsExactly(
                "Subject id",
                "Sample date",
                "Visit number",
                "Visit name",
                "Gene",
                "Mutation",
                "Tracked mutation",
                "Variant allele frequency (percentage)",
                "Variant allele frequency (fraction)");
    }

    @Test
    public void shouldSortColumns() {
        Subject subject = Subject.builder()
                .subjectId("sid1")
                .build();
        Lab lab1 = new Lab(LabRaw.builder()
                .value(0.9)
                .build(), subject);
        Lab lab2 = new Lab(LabRaw.builder()
                .value(0.8)
                .build(), subject);
        Lab lab3 = new Lab(LabRaw.builder()
                .value(0.74)
                .build(), subject);
        Lab lab4 = new Lab(LabRaw.builder()
                .value(0.83)
                .build(), subject);
        Lab lab5 = new Lab(LabRaw.builder()
                .value(9.0)
                .build(), subject);
        Lab lab6 = new Lab(LabRaw.builder()
                .value(14.0)
                .build(), subject);
        Lab lab7 = new Lab(LabRaw.builder()
                .value(-5.34)
                .build(), subject);
        Lab lab8 = new Lab(LabRaw.builder()
                .value(-7.84)
                .build(), subject);

        Lab[] labs = {lab1, lab2, lab3, lab4, lab5, lab6, lab7, lab8};
        final List<Map<String, String>> doDColumns = doDCommonService.getColumnData(DatasetType.ACUITY, Arrays.asList(labs), Collections
                .singletonList(new SortAttrs(SORT_ATTRIBUTE, false)), 0, 100, true);
        List<BigDecimal> resultValues = doDColumns.stream()
                .map(m -> parseToBigDecimal(m.get(SORT_ATTRIBUTE)))
                .collect(Collectors.toList());

        softly.assertThat(Comparators.isInOrder(resultValues, BigDecimal::compareTo)).isTrue();
    }

    @SneakyThrows
    private BigDecimal parseToBigDecimal(String m) {
        Number value = decimalFormat.parse(m);
        return value instanceof Long
                ? BigDecimal.valueOf((Long) value)
                : BigDecimal.valueOf((Double) value);
    }

    @Test
    @Ignore
    public void testPerformance() {
        final List<CIEvent> ciEvents = CIEventGenerator.generateRandomCIEventList(500000);
        StopWatch stopWatch = new StopWatch();
        System.out.println("calc1");
        //for profiling
        //Thread.sleep(3000);
        System.out.println("calc2");
        stopWatch.start("1");
        final Map<String, String> doDColumns = doDCommonService.getDoDColumns(DatasetType.ACUITY, ciEvents);
        stopWatch.stop();
        stopWatch.start("2");
        final Map<String, String> doDColumns2 = doDCommonService.getDoDColumns(DatasetType.ACUITY, ciEvents);
        stopWatch.stop();
        stopWatch.start("3");
        final Map<String, String> doDColumns3 = doDCommonService.getDoDColumns(DatasetType.ACUITY, ciEvents);
        stopWatch.stop();
        System.out.println("calc3");
        stopWatch.start("4");
        final List<Map<String, String>> data = doDCommonService.getColumnData(DatasetType.ACUITY, ciEvents,
                Collections.emptyList(), 10000, 100000, true);
        stopWatch.stop();
        System.out.println(stopWatch.prettyPrint());
    }
}
