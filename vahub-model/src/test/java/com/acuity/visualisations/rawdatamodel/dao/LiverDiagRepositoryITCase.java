package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.LiverDiagRaw;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.NoSuchElementException;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_LIVER_VA_ID;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
// TODO this test requires data in dataset
@Ignore
public class LiverDiagRepositoryITCase {

    @Autowired
    private LiverDiagRepository liverDiagRepository;
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetLiverDiagByDatasetIdWithCorrectValue() {
        List<LiverDiagRaw> events = liverDiagRepository.getRawData(DUMMY_ACUITY_LIVER_VA_ID);
        softly.assertThat(events.size()).isEqualTo(13);

        LiverDiagRaw liverDiag = events.stream()
                .filter(c -> "28893a8a9a884afba13cc38259e01de9".equals(c.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cannot find element that must exist"));

        softly.assertThat(liverDiag.getSubjectId()).isEqualTo("5d11c4c762e4486c8bfc3915fd29d2c2");
        softly.assertThat(liverDiag.getLiverDiagInv()).isEqualTo("X-Ray");
        softly.assertThat(liverDiag.getLiverDiagInvSpec()).isNull();
        softly.assertThat(DaysUtil.truncLocalTime(liverDiag.getLiverDiagInvDate())).isInSameDayAs("2017-02-03");
        softly.assertThat(liverDiag.getLiverDiagInvResult()).isEqualTo("stone");
        softly.assertThat(liverDiag.getPotentialHysLawCaseNum()).isEqualTo(1);
    }
}
