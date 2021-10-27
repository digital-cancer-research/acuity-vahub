package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.vo.CvotEndpointRaw;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class CvotEndpointRepositoryITCase {
    @Autowired
    private CvotEndpointRepository cvotEndpointRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetRawData() {
        //When
        List<CvotEndpointRaw> result = cvotEndpointRepository.getRawData(DUMMY_ACUITY_VA_ID);

        //Then
        softly.assertThat(result.size()).isEqualTo(41);
    }
}
