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
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Date;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author ksnd199
 */
public class VitalsTest {

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
  
    private Vital vital1 = new Vital(VitalRaw.builder().id("1").calcChangeFromBaselineIfNull(true).calcDaysSinceFirstDoseIfNull(true).
            baseline(10.).resultValue(20.).measurementDate(toDate("04.08.2015")).daysSinceFirstDose(null).changeFromBaselineRaw(null).
            subjectId("sid1").build().runPrecalculations(), SUBJECT1);
    private Vital vital2 = new Vital(VitalRaw.builder().id("1").calcChangeFromBaselineIfNull(true).calcDaysSinceFirstDoseIfNull(true).
            baseline(10.).resultValue(14.).measurementDate(toDate("04.08.2015")).daysSinceFirstDose(5).changeFromBaselineRaw(4.).
            subjectId("sid1").build().runPrecalculations(), SUBJECT1);
    private Vital vital3 =  new Vital(VitalRaw.builder().id("1").calcChangeFromBaselineIfNull(false).calcDaysSinceFirstDoseIfNull(false).
            baseline(10.).resultValue(15.).measurementDate(toDate("04.08.2015")).daysSinceFirstDose(6).changeFromBaselineRaw(5.).
            subjectId("sid1").build().runPrecalculations(), SUBJECT2);
    private Vital vital4 =  new Vital(VitalRaw.builder().id("1").calcChangeFromBaselineIfNull(false).calcDaysSinceFirstDoseIfNull(false).
            baseline(10.).resultValue(20.).measurementDate(toDate("04.08.2015")).daysSinceFirstDose(null).changeFromBaselineRaw(null).
            subjectId("sid1").build().runPrecalculations(), SUBJECT2);

    @Test
    public void shouldCalcDaysSinceFirstDoseForVital1() {

        Integer daysSinceFirstDose = vital1.getDaysSinceFirstDose();
        assertThat(daysSinceFirstDose).isEqualTo(3);
    }
    
    @Test
    public void shouldCalcDaysSinceFirstDoseForVital2() {

        Integer daysSinceFirstDose = vital2.getDaysSinceFirstDose();
        assertThat(daysSinceFirstDose).isEqualTo(5);
    }
    
    @Test
    public void shouldCalcDaysSinceFirstDoseForVital3() {

        Integer daysSinceFirstDose = vital3.getDaysSinceFirstDose();
        assertThat(daysSinceFirstDose).isEqualTo(6);
    }
    
    @Test
    public void shouldCalcDaysSinceFirstDoseForVital4() {

        Integer daysSinceFirstDose = vital4.getDaysSinceFirstDose();
        assertThat(daysSinceFirstDose).isNull();
    }
    
    @Test
    public void shouldCalcChangeFromBaselineForVital1() {

        Double changeFromBaseline = vital1.getChangeFromBaseline();
        assertThat(changeFromBaseline).isEqualTo(10.);
    }
    
    @Test
    public void shouldntCalcChangeFromBaselineForVital2() {

        Double changeFromBaseline = vital2.getChangeFromBaseline();
        assertThat(changeFromBaseline).isEqualTo(4.);
    }
    
    @Test
    public void shouldntCalcChangeFromBaselineForVital3() {

        Double changeFromBaseline = vital3.getChangeFromBaseline();
        assertThat(changeFromBaseline).isEqualTo(5.);
    }
    
    @Test
    public void shouldntCalcChangeFromBaselineForVital4() {

        Double changeFromBaseline = vital4.getChangeFromBaseline();
        assertThat(changeFromBaseline).isNull();
    }
    
    @Test
    public void shouldCalcPercentChangeFromBaselineForVital1() {

        Double percentChangeFromBaseline = vital1.getPercentChangeFromBaseline();
        assertThat(percentChangeFromBaseline).isEqualTo(100.);
    }
    
    @Test
    public void shouldntCalcPercentChangeFromBaselineForVital2() {

        Double percentChangeFromBaseline = vital2.getPercentChangeFromBaseline();
        assertThat(percentChangeFromBaseline).isEqualTo(40.);
    }
    
    @Test
    public void shouldntCalcPercentChangeFromBaselineForVital3() {

        Double percentChangeFromBaseline = vital3.getPercentChangeFromBaseline();
        assertThat(percentChangeFromBaseline).isEqualTo(50.);
    }
    
    @Test
    public void shouldntCalcPercentChangeFromBaselineForVital4() {

        Double percentChangeFromBaseline = vital4.getPercentChangeFromBaseline();
        assertThat(percentChangeFromBaseline).isNull();
    }
}
