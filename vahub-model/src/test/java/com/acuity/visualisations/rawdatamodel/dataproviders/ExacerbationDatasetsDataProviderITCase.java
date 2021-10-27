package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProviderAwareTest;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_DATASETS;
import static org.assertj.core.api.Assertions.assertThat;

@TransactionalOracleITTest
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
public class ExacerbationDatasetsDataProviderITCase extends DataProviderAwareTest {

    @Autowired
    private ExacerbationDatasetsDataProvider exacerbationDataProvider;

    @Test
    public void testLoadDataFromAcuityUploadedFile() throws Exception {
        //When
        Collection<Exacerbation> result = exacerbationDataProvider.loadData(DUMMY_2_ACUITY_DATASETS);

        //Than
        assertThat(result).hasSize(61);
    }
}
