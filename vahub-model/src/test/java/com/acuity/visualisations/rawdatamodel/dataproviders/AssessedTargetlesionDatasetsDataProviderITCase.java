package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
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
public class AssessedTargetlesionDatasetsDataProviderITCase {

    @Autowired
    private AssessedTargetLesionDatasetsDataProvider dataProvider;

    /**
     * IT test required as {@link AssessedTargetLesion} is an aggregated entity which has no own repository.
     * So, it is required to check that data loaded.
     * @throws Exception
     */
    @Test
    public void testLoadDataFromUploadedFile() throws Exception {

        Collection<AssessedTargetLesion> result = dataProvider.loadData(DUMMY_ACUITY_RECIST_DATASETS);

        assertThat(result).hasSize(1029);

        List<AssessedTargetLesion> atlsPerSubject = result.stream()
                .filter(e -> "8092cad631ab45f98e11f53b35ffa6d3".equals(e.getSubjectId()))
                .collect(Collectors.toList());

        assertThat(atlsPerSubject.size()).isEqualTo(12);

        // baseline
        List<AssessedTargetLesion> blAtlsPerSubject = atlsPerSubject.stream()
                .filter(e -> e.getEvent().isBaseline())
                .collect(Collectors.toList());

        assertThat(blAtlsPerSubject.size()).isEqualTo(3);
        AssessedTargetLesionRaw baseline = blAtlsPerSubject.stream().findAny().get().getEvent();

        assertThat(baseline.getLesionsDiameterPerAssessment()).isEqualTo(99);
        assertThat(baseline.getResponse())
                .isEqualTo(AssessmentRaw.Response.STABLE_DISEASE.getName());
        assertThat(baseline.getBestResponse())
                .isEqualTo(AssessmentRaw.Response.PARTIAL_RESPONSE.getName());





    }




}
