package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.service.PopulationService;
import com.acuity.visualisations.rawdatamodel.vo.Patient;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityPopulationServiceITCase {

    @Autowired
    private PopulationService populationService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetHasSafetyAsNoInPopulation() {
        // Given
        // When
        Boolean hasSafetyAsNoInPopulation = populationService.hasSafetyAsNoInPopulation(DUMMY_ACUITY_DATASETS);

        // Then
        assertThat(hasSafetyAsNoInPopulation).isFalse();
    }

    @Test
    public void shouldGetPatientList() {
        // Given
        // When
        List<Patient> patientList = populationService.getPatientList(DUMMY_ACUITY_DATASETS);

        // Then
        assertThat(patientList).hasSize(124);
        assertThat(patientList).extracting("subjectCode").contains("E0000100229");
    }
}
