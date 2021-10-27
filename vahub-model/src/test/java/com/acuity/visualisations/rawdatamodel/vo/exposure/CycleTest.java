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

package com.acuity.visualisations.rawdatamodel.vo.exposure;

import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.ExposureRaw;
import org.assertj.core.api.Assertions;
import org.junit.Test;

public class CycleTest {

    @Test
    public void shouldEqualsExposureCycleWithNullDrugAdministrationDateAndDiffVisit() {
        //Given
        ExposureRaw exposureRaw1 = ExposureRaw.builder().analyte("analyte_1").treatmentCycle("cycle_1").visitNumber(3)
                .cycle(Cycle.builder().treatmentCycle("cycle_1").analyte("analyte_1").visit(3).isNotAllDrugDatesEmpty(false).build()).build();
        ExposureRaw exposureRaw2 = ExposureRaw.builder().analyte("analyte_1").treatmentCycle("cycle_1").visitNumber(4)
                .cycle(Cycle.builder().treatmentCycle("cycle_1").analyte("analyte_1").visit(4).isNotAllDrugDatesEmpty(false).build()).build();

        //When
        boolean result = exposureRaw1.getCycle().equals(exposureRaw2.getCycle());

        //Then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void shouldEqualsExposureCycleWithNullVisitAndDiffDrugAdministrationDate() {
        //Given
        ExposureRaw exposureRaw1 = ExposureRaw.builder().analyte("analyte_1").treatmentCycle("cycle_1")
                .drugAdministrationDate(DateUtils.toDate("01.01.2000"))
                .cycle(Cycle.builder().treatmentCycle("cycle_1").analyte("analyte_1").visit(3)
                        .drugAdministrationDate(DateUtils.toDate("01.01.2000")).isNotAllDrugDatesEmpty(true).build()).build();
        ExposureRaw exposureRaw2 = ExposureRaw.builder().analyte("analyte_1").treatmentCycle("cycle_1")
                .drugAdministrationDate(DateUtils.toDate("01.09.2000"))
                .cycle(Cycle.builder().treatmentCycle("cycle_1").analyte("analyte_1").visit(3)
                        .drugAdministrationDate(DateUtils.toDate("01.09.2000")).isNotAllDrugDatesEmpty(true).build()).build();

        //When
        boolean result = exposureRaw1.getCycle().equals(exposureRaw2.getCycle());

        //Then
        Assertions.assertThat(result).isFalse();
    }

    @Test
    public void shouldEqualsExposureCycleWithNullDrugAdministrationDateAndVisit() {
        //Given
        ExposureRaw exposureRaw1 = ExposureRaw.builder().analyte("analyte_1").treatmentCycle("cycle_1")
                .cycle(Cycle.builder().treatmentCycle("cycle_1").analyte("analyte_1").isNotAllDrugDatesEmpty(false).build()).build();
        ExposureRaw exposureRaw2 = ExposureRaw.builder().analyte("analyte_1").treatmentCycle("cycle_1")
                .cycle(Cycle.builder().treatmentCycle("cycle_1").analyte("analyte_1").isNotAllDrugDatesEmpty(false).build()).build();

        //When
        boolean result = exposureRaw1.getCycle().equals(exposureRaw2.getCycle());

        //Then
        Assertions.assertThat(result).isTrue();
    }

    @Test
    public void shouldEqualsExposureCycleWithVisitGreaterThen127() {
        //Given
        ExposureRaw exposureRaw1 = ExposureRaw.builder().analyte("analyte_1").treatmentCycle("cycle_1").visitNumber(300)
                .cycle(Cycle.builder().treatmentCycle("cycle_1").analyte("analyte_1").visit(300).isNotAllDrugDatesEmpty(false).build()).build();
        ExposureRaw exposureRaw2 = ExposureRaw.builder().analyte("analyte_1").treatmentCycle("cycle_1").visitNumber(300)
                .cycle(Cycle.builder().treatmentCycle("cycle_1").analyte("analyte_1").visit(300).isNotAllDrugDatesEmpty(false).build()).build();

        //When
        boolean result = exposureRaw1.getCycle().equals(exposureRaw2.getCycle());

        //Then
        Assertions.assertThat(result).isTrue();
    }
}
