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
