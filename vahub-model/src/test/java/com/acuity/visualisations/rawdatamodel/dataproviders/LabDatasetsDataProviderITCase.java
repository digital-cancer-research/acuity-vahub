package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProviderAwareTest;
import com.acuity.visualisations.rawdatamodel.dataproviders.config.DataProviderConfiguration;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.DetectDataset;
import com.acuity.va.security.acl.domain.AcuityDataset;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_VA_ID;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;
import static org.assertj.core.api.Assertions.assertThat;

@TransactionalOracleITTest
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class, DataProviderConfiguration.class})
public class LabDatasetsDataProviderITCase extends DataProviderAwareTest {

    @Autowired
    private LabDatasetsDataProvider labDatasetsDataProvider;

    private Dataset acuityDataset = new AcuityDataset(DUMMY_ACUITY_VA_ID);
    private Dataset detectDataset = new DetectDataset(DUMMY_DETECT_VA_ID);

    @Test
    public void testLoadDataFromAcuityUploadedFile() throws Exception {

        Collection<Lab> result = labDatasetsDataProvider.loadData(new Datasets(acuityDataset));

        assertThat(result).hasSize(38574);
    }

    @Test
    @Ignore
    public void testLoadDataFromDetectUploadedFile() throws Exception {

        Collection<Lab> result = labDatasetsDataProvider.loadData(new Datasets(detectDataset));

        assertThat(result).hasSize(46136);
    }
}
