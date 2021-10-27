package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedNonTargetLesion;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_RECIST_DATASETS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AssessedNonTargetlesionDatasetsDataProviderITCase {

    @Autowired
    private AssessedNonTargetLesionDatasetsDataProvider dataProvider;

    /**
     * IT test required as {@link com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion}
     * is an aggregated entity which has no own repository. So, it is required to check that data loaded.
     * @throws Exception
     */
    @Test
    public void testLoadDataFromUploadedFile() throws Exception {

        Collection<AssessedNonTargetLesion> result = dataProvider.loadData(DUMMY_ACUITY_RECIST_DATASETS);

        assertThat(result).hasSize(212);
    }




}
