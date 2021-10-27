package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuityDataset;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class ChemotherapyDatasetsDataProviderITCase {

    @Autowired
    private ChemotherapyDatasetsDataProvider chemotherapyDatasetsDataProvider;

    private Dataset dataset = new AcuityDataset(DUMMY_ACUITY_VA_ID);

    @Test
    @Ignore("useless")
    public void testLoadDataFromUploadedFile() throws Exception {

        Collection<Chemotherapy> result = chemotherapyDatasetsDataProvider.loadData(new Datasets(dataset));

        assertThat(result).hasSize(818);
        //this might fails very unexpectedly
        assertThat(result.stream().findFirst().get().getSubject().getSubjectCode()).isEqualTo("E000010099");
        assertThat(result.stream().findFirst().get().getStartDate()).isInSameDayAs("2010-08-16");
        assertThat(result.stream().findFirst().get().getEndDate()).isInSameDayAs("2010-09-27");

    }

}
