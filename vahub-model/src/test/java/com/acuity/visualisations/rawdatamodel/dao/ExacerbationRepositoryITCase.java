package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.vo.ExacerbationRaw;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_VA_ID;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class ExacerbationRepositoryITCase {

    @Autowired
    private ExacerbationRepository exacerbationRepository;

    @Test
    public void shouldGetRawData() {
        //When
        List<ExacerbationRaw> result = exacerbationRepository.getRawData(DUMMY_2_ACUITY_VA_ID);

        //Then
        assertThat(result.size()).isEqualTo(61);
    }
}
