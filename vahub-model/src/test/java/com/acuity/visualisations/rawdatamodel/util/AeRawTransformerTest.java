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

import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverity;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author ksnd199
 */
public class AeRawTransformerTest {

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

    private AeRaw aeRaw1 = AeRaw.builder().id("ae1").aeNumber(1).
            aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().id("severity1").severity(SEVERITY_1).
                                    startDate(toDate("01.08.2015")).endDate(toDate("03.08.2015")).build(),
                            AeSeverityRaw.builder().id("severity2").severity(null).
                                    startDate(toDate("04.08.2015")).endDate(toDate("05.08.2015")).build(),
                            AeSeverityRaw.builder().id("severity3").severity(SEVERITY_2).
                                    startDate(null).endDate(null).build()
                    )
            ).
            subjectId("sid1").build();
    private AeRaw aeRaw2 = AeRaw.builder().id("2").aeNumber(2).aeOfSpecialInterest("special2").
            aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().id("severity4").severity(SEVERITY_3).
                                    startDate(toDate("01.08.2015")).endDate(toDate("03.08.2015")).build()
                    )
            ).
            subjectId("sid2").build();
    private AeRaw aeRaw3 = AeRaw.builder().id("3").aeNumber(3).subjectId("sid1").build();

    @Test
    public void shouldTransformDataFromIncidenceToSeverityChange() {
        Collection<AeRaw> aes = AeRawTransformer.transformToSeverityChange(newArrayList(aeRaw1, aeRaw2, aeRaw3), false);

        List<AeRaw> aesRaws = newArrayList(aes);

        assertThat(aesRaws).hasSize(4);
        assertThat(aesRaws.get(0).getId()).isEqualTo("severity1");
        assertThat(aesRaws.get(0).getAeNumber()).isEqualTo(1);
        assertThat(aesRaws.get(0).getMinStartDate()).isInSameDayAs("2015-08-01");
        assertThat(aesRaws.get(0).getMaxEndDate()).isInSameDayAs("2015-08-03");
        assertThat(aesRaws.get(0).getMaxAeSeverity()).isEqualTo(SEVERITY_1.getWebappSeverity());

        assertThat(aesRaws.get(1).getId()).isEqualTo("severity2");
        assertThat(aesRaws.get(1).getMinStartDate()).isInSameDayAs("2015-08-04");
        assertThat(aesRaws.get(1).getMaxEndDate()).isInSameDayAs("2015-08-05");
        assertThat(aesRaws.get(1).getMaxAeSeverity()).isNull();

        assertThat(aesRaws.get(2).getId()).isEqualTo("severity3");
        assertThat(aesRaws.get(2).getAeOfSpecialInterest()).isNull();
        assertThat(aesRaws.get(2).getMinStartDate()).isNull();
        assertThat(aesRaws.get(2).getMaxEndDate()).isNull();
        assertThat(aesRaws.get(2).getMaxAeSeverity()).isEqualTo(SEVERITY_2.getWebappSeverity());

        assertThat(aesRaws.get(3).getId()).isEqualTo("severity4");
        assertThat(aesRaws.get(3).getAeOfSpecialInterest()).isEqualTo("special2");
        assertThat(aesRaws.get(3).getMinStartDate()).isInSameDayAs("2015-08-01");
        assertThat(aesRaws.get(3).getMaxEndDate()).isInSameDayAs("2015-08-03");
    }
}
