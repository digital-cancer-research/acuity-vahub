package com.acuity.visualisations.rawdatamodel.dataset.info;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.va.security.acl.domain.vasecurity.DatasetInfo;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASET;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class WhenRunningVAsecurityInfoDatasetRepositoryQueriesITCase {

    @Autowired
    private InfoDatasetRepository detectInfoDatasetRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetDatasetInfoForStudy() {


        DatasetInfo datasetInfo = detectInfoDatasetRepository.getDatasetInfo(DUMMY_ACUITY_DATASET);

        softly.assertThat(datasetInfo).isNotNull();
        softly.assertThat(datasetInfo.getId()).isEqualTo(DUMMY_ACUITY_DATASET.getId());
        softly.assertThat(datasetInfo.getName().trim()).isEqualTo("D1234C00001");
        softly.assertThat(datasetInfo.getDrugProgramme()).isEqualTo("demo");
        softly.assertThat(datasetInfo.getClinicalStudy()).isEqualTo("D1234C00001");
        softly.assertThat(datasetInfo.getAddedDate()).isEqualToIgnoringHours("2018-12-25");
        softly.assertThat(datasetInfo.getAddedBy()).isNull();
    }

}
