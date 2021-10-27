package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProviderAwareTest;
import com.acuity.visualisations.rawdatamodel.dataproviders.config.DataProviderConfiguration;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuityDataset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;
import static org.assertj.core.api.Assertions.assertThat;

@TransactionalOracleITTest
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class, DataProviderConfiguration.class})
public class VitalDatasetsDataProviderITCase extends DataProviderAwareTest {

    @Autowired
    private VitalDatasetsDataProvider vitalDatasetsDataProvider;

    private Dataset acuityDataset = new AcuityDataset(DUMMY_ACUITY_VA_ID);

    @Test
    public void testLoadDataFromAcuityUploadedFile() throws Exception {

        Collection<Vital> result = vitalDatasetsDataProvider.loadData(new Datasets(acuityDataset));

        Vital vital = result.stream().filter(a -> a.getId().equals("ad994f9978bc4818950a78fe920d6248")).findFirst().get();
        assertThat(vital.getBaselineValue()).isEqualTo(81);
        assertThat(vital.getEvent().getBaselineFlag()).isEqualTo("Y");

    }


}
