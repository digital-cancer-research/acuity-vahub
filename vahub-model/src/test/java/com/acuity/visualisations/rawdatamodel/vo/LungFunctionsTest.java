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

import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import org.junit.Test;

import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static org.assertj.core.api.Assertions.assertThat;

public class LungFunctionsTest {

    private Subject SUBJECT1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
            .baselineDate(toDate("01.08.2015")).firstTreatmentDate(toDate("01.08.2015")).build();
    private Subject SUBJECT2 = Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test")
            .baselineDate(toDate("02.08.2015")).firstTreatmentDate(toDate("03.08.2015")).build();

    //acuity
    private LungFunction lf1 = new LungFunction(LungFunctionRaw.builder().id("1").calcChangeFromBaselineIfNull(true)
            .calcDaysSinceFirstDoseIfNull(true).visitDate(toDate("02.08.2015"))
            .value(3.19).baselineValue(2.18).measurementNameRaw("FVC (%)").protocolScheduleTimepoint("Pre-Bronchodilator")
            .measurementTimePoint(toDate("02.08.2015"))
            .build().runPrecalculations(), SUBJECT1);

    //detect
    private LungFunction lf2 = new LungFunction(LungFunctionRaw.builder().id("4").calcChangeFromBaselineIfNull(true)
            .calcDaysSinceFirstDoseIfNull(true).visitDate(toDate("07.08.2015"))
            .value(3.19).baselineValue(4.1).measurementNameRaw("FEV1 (L)").protocolScheduleTimepoint("Pre-Bronchodilator")
            .measurementTimePoint(toDate("07.08.2015")).build().runPrecalculations(), SUBJECT2);

    @Test
    public void shouldCalcDaysSinceFirstDoseForLf1() {
        Integer daysSinceFirstDose = lf1.getDaysSinceFirstDose();
        assertThat(daysSinceFirstDose).isEqualTo(1);
    }

    @Test
    public void shouldCalcDaysSinceFirstDoseForLf2() {
        Integer daysSinceFirstDose = lf2.getDaysSinceFirstDose();
        assertThat(daysSinceFirstDose).isEqualTo(4);
    }

    @Test
    public void shouldCalcChangeFromBaselineForLf1() {
        Double changeFromBaseline = lf1.getChangeFromBaseline();
        assertThat(changeFromBaseline).isEqualTo(1.01);
    }

    @Test
    public void shouldntCalcChangeFromBaselineForLf2() {
        Double changeFromBaseline = lf2.getChangeFromBaseline();
        assertThat(changeFromBaseline).isEqualTo(-0.91);
    }

    @Test
    public void shouldCalcCalcUnitLf1() {
        String unit = lf1.getUnit();
        assertThat(unit).isEqualTo("%");
    }

    @Test
    public void shouldReturnUnitForLf2() {
        String unit = lf2.getUnit();
        assertThat(unit).isEqualTo("L");
    }

    @Test
    public void shouldCalcCalcMeasurementNameForLf1() {
        String measurementName = lf1.getMeasurementName();
        assertThat(measurementName).isEqualTo("FVC");
    }

    @Test
    public void shouldReturnMeasurementNameForForLf2() {
        String measurementName = lf2.getMeasurementName();
        assertThat(measurementName).isEqualTo("FEV1");
    }

}
