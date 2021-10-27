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

import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.google.common.collect.Lists.newArrayList;
import java.util.Date;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author ksnd199
 */
public class AeRawTest {

    private final AeSeverity SEVERITY_1 = AeSeverity.builder().severityNum(1).webappSeverity("CTC Grade 1").build();
    private final AeSeverity SEVERITY_2 = AeSeverity.builder().severityNum(2).webappSeverity("CTC Grade 2").build();
    private final AeSeverity SEVERITY_3 = AeSeverity.builder().severityNum(3).webappSeverity("CTC Grade 3").build();
  
    private AeRaw aeRaw1 = AeRaw.builder().id("1").aeNumber(1).
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
            subjectId("sid1").build();
    private AeRaw aeRaw2 = AeRaw.builder().id("2").aeNumber(2).
            aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_3).
                                    startDate(toDate("01.08.2015")).endDate(toDate("03.08.2015")).build()
                    )
            ).
            subjectId("sid2").build();
    private AeRaw aeRaw3 = AeRaw.builder().id("3").aeNumber(3).subjectId("sid1").build();

    @Test
    public void shouldGetMinStartDate() {

        Date minStartDate1 = aeRaw1.getMinStartDate();
        assertThat(minStartDate1).isInSameDayAs("2015-08-01");
        
        Date minStartDate2 = aeRaw2.getMinStartDate();
        assertThat(minStartDate2).isInSameDayAs("2015-08-01");
        
        Date minStartDate3 = aeRaw3.getMinStartDate();
        assertThat(minStartDate3).isNull();
    }
    
    @Test
    public void shouldGetMaxEndDate() {

        Date maxEndDate1 = aeRaw1.getMaxEndDate();
        assertThat(maxEndDate1).isInSameDayAs("2015-08-05");
        
        Date maxEndDate2 = aeRaw2.getMaxEndDate();
        assertThat(maxEndDate2).isInSameDayAs("2015-08-03");
        
        Date maxEndDate3 = aeRaw3.getMaxEndDate();
        assertThat(maxEndDate3).isNull();
    }
    
    @Test
    public void shouldGetMaxAeSeverity() {

        String maxAeSeverity1 = aeRaw1.getMaxAeSeverity();
        assertThat(maxAeSeverity1).isEqualTo("CTC Grade 2");
        
        String maxAeSeverity2 = aeRaw2.getMaxAeSeverity();
        assertThat(maxAeSeverity2).isEqualTo("CTC Grade 3");
        
        String maxAeSeverity3 = aeRaw3.getMaxAeSeverity();
        assertThat(maxAeSeverity3).isNull();
    }
}
