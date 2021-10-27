package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_DATASETS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class LungFunctionDatasetsDataProviderITCase {
    @Autowired
    private LungFunctionDatasetsDataProvider dataProvider;

    @Test
    public void testLoadDataFromAcuityUploadedFile() {

        Collection<LungFunction> result = dataProvider.loadData(DUMMY_2_ACUITY_DATASETS);

        assertThat(result).hasSize(450);

        List<LungFunction> lfsPerSubject = result.stream()
                .filter(e -> "7d9e4e1c2ded4402a45c9520a288bb2a".equals(e.getSubjectId()))
                .collect(Collectors.toList());

        assertThat(lfsPerSubject.size()).isEqualTo(63);

        LungFunction lf = lfsPerSubject.stream()
                .filter(e -> "aafbcbd0dd454871bb84a9ba9632bfb2".equals(e.getId()))
                .findFirst().get();

        assertThat(lf.getEvent().getBaselineValue()).isEqualTo(0.25);
        assertThat(lf.getEvent().getBaselineFlag()).isEqualTo("Y");
    }
}
