package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.LabTests;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
@Category(LabTests.class)
public class LabRepositoryITCase {

    @Autowired
    private LabRepository labRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetLabsByDatasetIdWithCorrectValue() {

        List<LabRaw> events = labRepository.getRawData(DUMMY_ACUITY_VA_ID);
        softly.assertThat(events.size()).isEqualTo(38574);

        LabRaw lab = events.stream().filter(t -> t.getId().equals("000363410de643a9a5c5e3795ca8ef63")).findFirst().get();

        softly.assertThat(lab.getSubjectId()).isEqualTo("40ff2d723bca4187a992020342e3a138");
        softly.assertThat(lab.getCategory()).isEqualTo("Chemistry");
        softly.assertThat(lab.getLabCode()).isEqualTo("Creatinine");
        softly.assertThat(lab.getMeasurementTimePoint()).isInSameDayAs(DaysUtil.toDate("2015-01-07"));
        softly.assertThat(lab.getVisitNumber()).isEqualTo(14.);
        softly.assertThat(lab.getValue()).isEqualTo(47.20);
        softly.assertThat(lab.getUnit()).isEqualTo("umol/L");
        softly.assertThat(lab.getRefHigh()).isEqualTo(88.);
        softly.assertThat(lab.getRefLow()).isEqualTo(40.);
        softly.assertThat(lab.getProtocolScheduleTimepoint()).isNull();
        softly.assertThat(lab.getValueDipstick()).isNull();
        softly.assertThat(lab.getComment()).isNull();
    }
}
