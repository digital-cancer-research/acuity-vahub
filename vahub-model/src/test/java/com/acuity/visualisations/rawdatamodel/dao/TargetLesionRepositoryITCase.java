package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import org.assertj.core.api.Assertions;
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
public class TargetLesionRepositoryITCase {

    @Autowired
    private TargetLesionRepository targetLesionRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetAllTargetLesionsByDatasetId() {
        List<TargetLesionRaw> events = targetLesionRepository.getRawData(DUMMY_ACUITY_VA_ID);
        Assertions.assertThat(events.size()).isEqualTo(1059);
    }

    @Test
    public void shouldGetTargetLesionsByDatasetIdWithCorrectValues() {
        TargetLesionRaw lesion = targetLesionRepository.getRawData(DUMMY_ACUITY_VA_ID).stream()
                .filter(l -> "c780ba3d096d400eabb6fa4124592e78".equals(l.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cannot find element that must exist"));
        Assertions.assertThat(lesion.getSubjectId()).isEqualTo("8b95c8814dbd45de9085b88fc560916a");
        Assertions.assertThat(lesion.getLesionSite()).isEqualTo("Spleen");
        Assertions.assertThat(lesion.getLesionDiameter()).isEqualTo(25);
        Assertions.assertThat(lesion.getLesionNumber()).isEqualTo("1");
        Assertions.assertThat(lesion.getLesionDate()).isInSameDayAs("2014-12-20");
        Assertions.assertThat(lesion.getVisitDate()).isInSameDayAs("2014-12-16");
    }
}
