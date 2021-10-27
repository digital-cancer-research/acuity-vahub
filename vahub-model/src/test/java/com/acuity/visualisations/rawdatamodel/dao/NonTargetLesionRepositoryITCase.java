package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.NonTargetLesionRaw;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.NoSuchElementException;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class NonTargetLesionRepositoryITCase {

    @Autowired
    private NonTargetLesionRepository nonTargetLesionRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetConmedsByDatasetIdWithCorrectValue() {
        List<NonTargetLesionRaw> nonTargetLesionRaws = nonTargetLesionRepository.getRawData(DUMMY_ACUITY_VA_ID);
        softly.assertThat(nonTargetLesionRaws.size()).isEqualTo(212);

        NonTargetLesionRaw nonTargetLesionRaw = nonTargetLesionRaws.stream()
                .filter(c -> "f3e6254fd46a4ae6a08c8dd1e4f723c3".equals(c.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cannot find element that must exist"));

        softly.assertThat(nonTargetLesionRaw.getSubjectId()).isEqualTo("a554a7a468314db0884c46b3064436db");
        softly.assertThat(nonTargetLesionRaw.getLesionDate()).isInSameDayAs(DaysUtil.toDate("2015-03-15"));
        softly.assertThat(nonTargetLesionRaw.getVisitNumber()).isEqualTo(0);
        softly.assertThat(nonTargetLesionRaw.getLesionSite()).isEqualTo("Central Nervous System (Brain/Spinal Cord/Ophthalmic)");
        softly.assertThat(nonTargetLesionRaw.getVisitDate()).isInSameDayAs(DaysUtil.toDate("2015-03-11"));
    }
}
