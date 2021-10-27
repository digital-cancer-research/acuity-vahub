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

package com.acuity.visualisations.rawdatamodel.vo;

import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author ksnd199
 */
public class AeTest {

    private final AeSeverity SEVERITY_1 = AeSeverity.builder().severityNum(1).webappSeverity("CTC Grade 1").build();
    private final AeSeverity SEVERITY_2 = AeSeverity.builder().severityNum(2).webappSeverity("CTC Grade 2").build();
    private final AeSeverity SEVERITY_3 = AeSeverity.builder().severityNum(3).webappSeverity("CTC Grade 3").build();

    private final Map<String, Date> drugFirstDoseDate1 = ImmutableMap.<String, Date>builder().
            put("drug1", toDate("01.08.2015")).
            put("drug2", toDate("01.10.2015")).build();

    private Subject SUBJECT1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
            .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("02.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2016"))
            .drugFirstDoseDate(drugFirstDoseDate1).build();
    private Subject SUBJECT2 = Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test")
            .firstTreatmentDate(DateUtils.toDate("02.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("03.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2015"))
            .drugFirstDoseDate(drugFirstDoseDate1).build();

    private Ae ae1 = new Ae(AeRaw.builder().id("1").aeNumber(1).
            aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_1).
                                    startDate(toDate("01.08.2015")).endDate(toDate("03.08.2015")).build(),
                            AeSeverityRaw.builder().severity(SEVERITY_2).
                                    startDate(toDate("04.08.2015")).endDate(toDate("05.08.2015")).build(),
                            AeSeverityRaw.builder().severity(SEVERITY_2).
                                    startDate(null).endDate(null).build()
                    )
            ).
            subjectId("sid1").build(), SUBJECT1);
    private Ae ae2 = new Ae(AeRaw.builder().id("2").aeNumber(2).
            aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_3).startDate(toDate("01.08.2015")).endDate(toDate("03.08.2015")).build()
                    )
            ).
            subjectId("sid2").build(), SUBJECT2);
    private Ae ae3 = new Ae(AeRaw.builder().id("3").aeNumber(3).subjectId("sid1").build(), SUBJECT2);

    // Duration tests
    private List<AeSeverityRaw> severities = newArrayList(
            AeSeverityRaw.builder().severity(SEVERITY_3).startDate(toDate("01.08.2015")).endDate(toDate("03.08.2015")).build()
    );

    private Ae aeDuration11 = new Ae(AeRaw.builder().id("2").calcDurationIfNull(true).duration(10).
            aeSeverities(severities).subjectId("sid2").build(), SUBJECT1);
    private Ae aeDuration12 = new Ae(AeRaw.builder().id("2").calcDurationIfNull(true).duration(null).
            aeSeverities(severities).subjectId("sid2").build(), SUBJECT1);
    private Ae aeDuration13 = new Ae(AeRaw.builder().id("2").calcDurationIfNull(false).duration(11).
            aeSeverities(severities).subjectId("sid2").build(), SUBJECT1);
    private Ae aeDuration14 = new Ae(AeRaw.builder().id("2").calcDurationIfNull(false).duration(null).
            aeSeverities(severities).subjectId("sid2").build(), SUBJECT1);

    @Test
    public void shouldDurationForAe1() {

        Integer duration = aeDuration11.getDuration();
        assertThat(duration).isEqualTo(10);
    }

    @Test
    public void shouldDurationForAe2() {

        Integer duration = aeDuration12.getDuration();
        assertThat(duration).isEqualTo(3);
    }

    @Test
    public void shouldDurationForAe3() {

        Integer duration = aeDuration13.getDuration();
        assertThat(duration).isEqualTo(11);
    }

    @Test
    public void shouldDurationForAe4() {

        Integer duration = aeDuration14.getDuration();
        assertThat(duration).isNull();
    }

    @Test
    public void shouldGetStartDate() {

        Date minStartDate1 = ae1.getStartDate();
        assertThat(minStartDate1).isInSameDayAs("2015-08-01");

        Date minStartDate2 = ae2.getStartDate();
        assertThat(minStartDate2).isInSameDayAs("2015-08-01");

        Date minStartDate3 = ae3.getStartDate();
        assertThat(minStartDate3).isNull();
    }

    @Test
    public void shouldGetEndDate() {

        Date maxEndDate1 = ae1.getEndDate();
        assertThat(maxEndDate1).isInSameDayAs("2015-08-05");

        Date maxEndDate2 = ae2.getEndDate();
        assertThat(maxEndDate2).isInSameDayAs("2015-08-03");

        Date maxEndDate3 = ae3.getEndDate();
        assertThat(maxEndDate3).isNull();
    }

    @Test
    public void shouldGetEndDatePriorToRandomisation() {

        String endDatePriorToRandomisation1 = ae1.getEndDatePriorToRandomisation();
        assertThat(endDatePriorToRandomisation1).isEqualTo("No");

        String endDatePriorToRandomisation2 = ae2.getEndDatePriorToRandomisation();
        assertThat(endDatePriorToRandomisation2).isEqualTo("No");

        String endDatePriorToRandomisation3 = ae3.getEndDatePriorToRandomisation();
        assertThat(endDatePriorToRandomisation3).isNull();
    }

    @Test
    public void shouldGetStartDatePriorToRandomisation() {

        String endDatePriorToRandomisation1 = ae1.getStartDatePriorToRandomisation();
        assertThat(endDatePriorToRandomisation1).isEqualTo("Yes");

        String endDatePriorToRandomisation2 = ae2.getStartDatePriorToRandomisation();
        assertThat(endDatePriorToRandomisation2).isEqualTo("Yes");

        String endDatePriorToRandomisation3 = ae3.getStartDatePriorToRandomisation();
        assertThat(endDatePriorToRandomisation3).isNull();
    }

    @Test
    public void shouldGetDaysOnStudyAtStart() {

        Integer daysOnStudyAtStart1 = ae1.getDaysOnStudyAtStart();
        assertThat(daysOnStudyAtStart1).isEqualTo(0);

        Integer daysOnStudyAtStart2 = ae2.getDaysOnStudyAtStart();
        assertThat(daysOnStudyAtStart2).isEqualTo(-1);

        Integer daysOnStudyAtStart3 = ae3.getDaysOnStudyAtStart();
        assertThat(daysOnStudyAtStart3).isNull();
    }

    @Test
    public void shouldGetDaysOnStudyAtEnd() {

        Integer daysOnStudyAtEnd1 = ae1.getDaysOnStudyAtEnd();
        assertThat(daysOnStudyAtEnd1).isEqualTo(4);

        Integer daysOnStudyAtEnd2 = ae2.getDaysOnStudyAtEnd();
        assertThat(daysOnStudyAtEnd2).isEqualTo(1);

        Integer daysOnStudyAtEnd3 = ae3.getDaysOnStudyAtEnd();
        assertThat(daysOnStudyAtEnd3).isNull();
    }
}
