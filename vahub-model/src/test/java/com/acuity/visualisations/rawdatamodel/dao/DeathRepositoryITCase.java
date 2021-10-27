package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.DeathRaw;
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
public class DeathRepositoryITCase {

    @Autowired
    private DeathRepository deathRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetDeathByDatasetIdWithCorrectValue() {
        List<DeathRaw> events = deathRepository.getRawData(DUMMY_2_ACUITY_VA_ID);
        softly.assertThat(events.size()).isEqualTo(46);

        DeathRaw death = events.stream().filter(t -> t.getId().equals("dfdd79cda53545ba9212ddf6b4a60cda")).findFirst().get();

        softly.assertThat(death.getSubjectId()).isEqualTo("a055cc9f55bf4010a7cf618a93e96130");
        softly.assertThat(death.getDeathCause()).isEqualTo("HEADACHE");
        softly.assertThat(DaysUtil.truncLocalTime(death.getDateOfDeath())).isInSameDayAs("2015-11-22");
        softly.assertThat(death.getDesignation()).isEqualTo("Primary");
    }
}
