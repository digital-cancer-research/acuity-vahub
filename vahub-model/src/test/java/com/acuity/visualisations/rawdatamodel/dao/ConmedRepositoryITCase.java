package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.ConmedRaw;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.NoSuchElementException;
import java.util.Set;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class ConmedRepositoryITCase {

    @Autowired
    private ConmedRepository conmedRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetConmedsByDatasetIdWithCorrectValue() {
        Set<ConmedRaw> conmeds = conmedRepository.getRawData(DUMMY_ACUITY_VA_ID);
        softly.assertThat(conmeds.size()).isEqualTo(8750);

        ConmedRaw conmed = conmeds.stream()
                .filter(c -> "60b9a9639ccc45ddb8f4fa2e2a71ece1".equals(c.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cannot find element that must exist"));

        softly.assertThat(conmed.getSubjectId()).isEqualTo("243568336f43479ca67984c33d111a4d");
        softly.assertThat(conmed.getMedicationName()).isEqualTo("ANTIALLERSIN");
        softly.assertThat(DaysUtil.truncLocalTime(conmed.getStartDate())).isInSameDayAs(DaysUtil.truncLocalTime(DaysUtil.toDate("2015-02-08")));
        softly.assertThat(DaysUtil.truncLocalTime(conmed.getEndDate())).isInSameDayAs(DaysUtil.truncLocalTime(DaysUtil.toDate("2015-02-08")));
        softly.assertThat(conmed.getTreatmentReason()).isEqualTo("premedication");
        softly.assertThat(conmed.getAtcCode()).isEqualTo("R06AD");
    }
}
