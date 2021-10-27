package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityEventCategoryValue;
import com.acuity.visualisations.rawdatamodel.vo.EventCategoryValue;
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
public class AeRepositoryITCase {

    @Autowired
    private AeRepository aeRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetRawData() {
        //When
        List<AeRaw> result = aeRepository.getRawData(DUMMY_ACUITY_VA_ID);

        //Then
        softly.assertThat(result.size()).isEqualTo(2076);
    }

    @Test
    public void shouldGetRawDrugsCausality() {
        //When
        List<EventCategoryValue> result = aeRepository.getDistinctDrugsCausality(DUMMY_ACUITY_VA_ID);

        //Then
        softly.assertThat(result.size()).isEqualTo(4168);
    }

    @Test
    public void shouldGetRawDrugsActionTaken() {
        //When
        List<AeSeverityEventCategoryValue> result = aeRepository.getDistinctDrugsActionTaken(DUMMY_ACUITY_VA_ID);

        //Then
        softly.assertThat(result.size()).isEqualTo(5096);
    }
}
