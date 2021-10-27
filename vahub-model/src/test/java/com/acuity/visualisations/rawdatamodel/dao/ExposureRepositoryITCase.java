package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.vo.ExposureRaw;
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
public class ExposureRepositoryITCase {

    @Autowired
    private ExposureRepository exposureRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetExposureByDatasetId() {
        List<ExposureRaw> events = exposureRepository.getRawData(DUMMY_2_ACUITY_VA_ID);
        softly.assertThat(events.size()).isEqualTo(8980);
    }

    @Test
    public void shouldGetExposureByDatasetIdWithCorrectValue() {
        ExposureRaw exposure = exposureRepository.getRawData(DUMMY_2_ACUITY_VA_ID)
                .stream().filter(t -> t.getId().equals("d4144d142b2c4a10b62dd1a58f98a418")).findFirst().get();
        softly.assertThat(exposure.getId()).isEqualTo("d4144d142b2c4a10b62dd1a58f98a418");
        softly.assertThat(exposure.getSubjectId()).isEqualTo("e49b75e6c6694163a22209729890ac5c");
        softly.assertThat(exposure.getAnalyteConcentration()).isEqualTo(26.57940247);
        softly.assertThat(exposure.getAnalyteUnit()).isEqualTo("ng/ml");
    }

}
