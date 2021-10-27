package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.SurgicalHistoryRaw;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.NoSuchElementException;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_VA_ID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class SurgicalHistoryRepositoryITCase {

    @Autowired
    private SurgicalHistoryRepository surgicalHistoryRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetSurgicalHistoriesByDatasetIdWithCorrectValue() {
        List<SurgicalHistoryRaw> events = surgicalHistoryRepository.getRawData(DUMMY_2_ACUITY_VA_ID);
        softly.assertThat(events.size()).isEqualTo(36);

        SurgicalHistoryRaw surgicalHistory = events.stream()
                .filter(t -> t.getId().equals("8f747c82c05144589dd5986bac7754bc"))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cannot find element that must exist"));

        softly.assertThat(surgicalHistory.getSubjectId()).isEqualTo("14507838b41d4f028aa8c989a41c025f");
        softly.assertThat(surgicalHistory.getSurgicalProcedure()).isEqualTo("BILATERAL LYMPH NODE DISSECTION");
        softly.assertThat(surgicalHistory.getPreferredTerm()).isEqualTo("LYMPHADENECTOMY");
        softly.assertThat(surgicalHistory.getCurrentMedication()).isNull();
        softly.assertThat(surgicalHistory.getStart()).isInSameDayAs(DaysUtil.toDate("2014-08-31"));
    }
}
