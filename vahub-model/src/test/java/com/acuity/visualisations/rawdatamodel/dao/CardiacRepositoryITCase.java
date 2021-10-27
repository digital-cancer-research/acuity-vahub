package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dao.cardiac.CardiacDecgRawDataRepository;
import com.acuity.visualisations.rawdatamodel.dao.cardiac.CardiacEcgRawDataRepository;
import com.acuity.visualisations.rawdatamodel.dao.cardiac.CardiacLvefRawDataRepository;
import com.acuity.visualisations.rawdatamodel.vo.CardiacDecgRaw;
import com.acuity.visualisations.rawdatamodel.vo.CardiacRaw;
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
public class CardiacRepositoryITCase {

    @Autowired
    private CardiacEcgRawDataRepository acuityCardiacEcgRepository;

    @Autowired
    private CardiacDecgRawDataRepository acuityCardiacDecgRepository;

    @Autowired
    private CardiacLvefRawDataRepository acuityCardiacLvefRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetEcgRawData() {
        List<CardiacRaw> result = acuityCardiacEcgRepository.getRawData(DUMMY_ACUITY_VA_ID);
        softly.assertThat(result.size()).isEqualTo(4580);
    }

    @Test
    public void shouldGetDecgRawData() {
        List<CardiacDecgRaw> result = acuityCardiacDecgRepository.getRawData(DUMMY_ACUITY_VA_ID);
        softly.assertThat(result.size()).isEqualTo(0);
    }

    @Test
    public void shouldGetLvefRawData() {
        List<CardiacRaw> result = acuityCardiacLvefRepository.getRawData(DUMMY_ACUITY_VA_ID);
        softly.assertThat(result.size()).isEqualTo(287);
    }
}
