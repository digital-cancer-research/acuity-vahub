package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Assessment;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_RECIST_DATASETS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AssessmentDatasetsDataProviderITCase {
    @Autowired
    private AssessmentDatasetsDataProvider dataProvider;

    @Test
    public void testLoadDataFromUploadedFile() throws Exception {

        Collection<Assessment> result = dataProvider.loadData(DUMMY_ACUITY_RECIST_DATASETS);

        assertThat(result).hasSize(466);

        List<Assessment> tlsPerSubject = result.stream()
                .filter(e -> "8092cad631ab45f98e11f53b35ffa6d3".equals(e.getSubjectId()))
                .collect(Collectors.toList());

        assertThat(tlsPerSubject.size()).isEqualTo(4);
        assertThat(tlsPerSubject.get(0).getEvent().getBaselineDate()).isInSameDayAs("2015-03-09");

    }
}
