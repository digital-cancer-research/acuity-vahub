package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.ChemotherapyRaw;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class ChemotherapyRepositoryITCase {

    @Autowired
    private ChemotherapyRepository chemotherapyRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetChemotherapyByDatasetId() {
        List<ChemotherapyRaw> events = chemotherapyRepository.getRawData(DUMMY_ACUITY_VA_ID);
        softly.assertThat(events.size()).isEqualTo(818);
    }

    @Test
    public void shouldGetChemotherapyByDatasetIdWithCorrectValue() {
        ChemotherapyRaw chemotherapy = chemotherapyRepository.getRawData(DUMMY_ACUITY_VA_ID).stream()
                .filter(c -> c.getId().equals("c3f6b428598447deb00fe32450dc435a")).findAny().get();
        softly.assertThat(chemotherapy.getSubjectId()).isEqualTo("08ec400e0a694717bc378b370ac59926");
        softly.assertThat(DaysUtil.truncLocalTime(chemotherapy.getStartDate())).isInSameDayAs("2013-11-09");
        softly.assertThat(DaysUtil.truncLocalTime(chemotherapy.getEndDate())).isInSameDayAs("2014-07-17");
        softly.assertThat(chemotherapy.getPreferredMed()).isEqualTo("CISPLATIN");
        softly.assertThat(chemotherapy.getTherapyClass()).isEqualTo("Cytotoxic Chemotherapy");
        softly.assertThat(chemotherapy.getTreatmentStatus()).isEqualTo("Second Line");
        softly.assertThat(chemotherapy.getBestResponse()).isEqualTo("Progression");
        softly.assertThat(chemotherapy.getFailureReason()).isEqualTo("Progresssion following Completion of Therapy");
    }

}
