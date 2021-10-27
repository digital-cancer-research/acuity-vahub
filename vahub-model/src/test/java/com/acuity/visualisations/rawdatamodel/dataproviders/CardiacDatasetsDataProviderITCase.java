package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProviderAwareTest;
import com.acuity.visualisations.rawdatamodel.vo.CardiacRaw;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASET;

@TransactionalOracleITTest
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
public class CardiacDatasetsDataProviderITCase extends DataProviderAwareTest {

    @Autowired
    private CardiacDatasetsDataProvider cardiacDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    // Adopted from WhenRunningCardiacFilterRepositoryITCase
    public void testAcuityDataset() {
        Collection<CardiacRaw> result = cardiacDatasetsDataProvider.getData(DUMMY_ACUITY_DATASET);
        softly.assertThat(result).hasSize(4867);
    }
}
