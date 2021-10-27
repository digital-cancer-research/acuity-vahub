package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.RadiotherapyRaw;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDateTime;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class RadiotherapyRepositoryITCase {

    @Autowired
    private RadiotherapyRepository radiotherapyRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetRadiotherapyByDatasetId() {
        List<RadiotherapyRaw> events = radiotherapyRepository.getRawData(DUMMY_ACUITY_VA_ID);
        softly.assertThat(events.size()).isEqualTo(313);
    }

    @Test
    public void shouldGetRadiotherapyByDatasetIdWithCorrectValue() {
        RadiotherapyRaw radiotherapy = radiotherapyRepository.getRawData(DUMMY_ACUITY_VA_ID).stream()
                .filter(r -> r.getId().equals("e60148936eca4854bc162a04da5e13ba")).findAny().get();
        softly.assertThat(radiotherapy.getSubjectId()).isEqualTo("ffbc67330b8f4d5aa965d42b90587c5f");
        softly.assertThat(radiotherapy.getStartDate()).isInSameDayAs(toDateTime("2012-10-03T00:0:00"));
        softly.assertThat(DaysUtil.truncLocalTime(radiotherapy.getEndDate())).isInSameDayAs("2012-10-23");
        softly.assertThat(radiotherapy.getGiven()).isEqualTo("TRUE");
        softly.assertThat(radiotherapy.getSiteOrRegion()).isEqualTo("brain");
        softly.assertThat(radiotherapy.getDose()).isEqualTo(5.0);
        softly.assertThat(radiotherapy.getTreatmentStatus()).isEqualTo("Third");
        softly.assertThat(radiotherapy.getNumOfDoses()).isEqualTo(15);
    }

}
