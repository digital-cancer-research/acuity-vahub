package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dataproviders.config.DataProviderConfiguration;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SeriousAe;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_CARDIAC_DATASETS;

@TransactionalOracleITTest
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class, DataProviderConfiguration.class})
public class SeriousAeDatasetsDataProviderITCase {
    @Autowired
    private SeriousAeDatasetsDataProvider seriousAeDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testLoadDataFromAcuityUploadedFile() {
        Collection<SeriousAe> result = seriousAeDatasetsDataProvider.loadData(DUMMY_ACUITY_CARDIAC_DATASETS);

        softly.assertThat(result).hasSize(6);
    }
}
