package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.SeriousAeRaw;
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
public class SeriousAeRepositoryITCase {

    @Autowired
    private SeriousAeRepository seriousAeRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetSeriousAeByDatasetIdWithCorrectValue() {
        List<SeriousAeRaw> events = seriousAeRepository.getRawData(DUMMY_ACUITY_VA_ID);
        softly.assertThat(events.size()).isEqualTo(6);

        SeriousAeRaw seriousAe = events.stream().filter(t -> t.getId().equals("8bc12b4ce0a2401a8c77a54be2550948")).findFirst().get();

        softly.assertThat(seriousAe.getSubjectId()).isEqualTo("1c2ca43e644546be9c9616d2a08cfe0c");
        softly.assertThat(seriousAe.getAe()).isEqualTo("NEUTROPENIA");
        softly.assertThat(seriousAe.getBecomeSeriousDate()).isInSameDayAs(DaysUtil.toDate("2014-11-21"));

    }
}
