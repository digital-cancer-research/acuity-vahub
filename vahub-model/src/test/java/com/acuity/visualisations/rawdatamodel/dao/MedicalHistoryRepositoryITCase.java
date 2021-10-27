package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.vo.MedicalHistoryRaw;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_VA_ID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class MedicalHistoryRepositoryITCase {

    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetMedicalHistoriesByDatasetIdWithCorrectValue() {
        List<MedicalHistoryRaw> events = medicalHistoryRepository.getRawData(DUMMY_2_ACUITY_VA_ID);
        softly.assertThat(events.size()).isEqualTo(26);

        MedicalHistoryRaw medicalHistory = events.stream().filter(t -> t.getId().equals("ce26bf9805fb4e98a9cffbbd41a63af6")).findFirst().get();

        softly.assertThat(medicalHistory.getSubjectId()).isEqualTo("1355a239bf074e479a20f8efb379d6cb");
        softly.assertThat(medicalHistory.getConditionStatus()).isNull();
        softly.assertThat(medicalHistory.getTerm()).isNull();
        softly.assertThat(medicalHistory.getPreferredTerm()).isNull();
        softly.assertThat(medicalHistory.getStart()).isNull();
        softly.assertThat(medicalHistory.getEnd()).isNull();
    }
}
