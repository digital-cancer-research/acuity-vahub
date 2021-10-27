package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverDiag;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_LIVER_DATASETS;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
// TODO this test requires data in dataset
@Ignore
public class LiverDiagDatasetsDataProviderITCase {
    @Autowired
    private LiverDiagDatasetsDataProvider liverDiagDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testLoadDataFromAcuityUploadedFile() {
        Collection<LiverDiag> result = liverDiagDatasetsDataProvider.loadData(DUMMY_LIVER_DATASETS);

        softly.assertThat(result).hasSize(13);

        List<LiverDiag> liverDiagsPerSubject = result.stream()
                .filter(e -> "b984ebffdcb94518bfe2b7e3f9ff32fe".equals(e.getSubjectId()))
                .collect(Collectors.toList());

        softly.assertThat(liverDiagsPerSubject.size()).isEqualTo(3);

        LiverDiag liverDiag = liverDiagsPerSubject.stream()
                .filter(e -> "103de1ac52624c9fbb09698cfbd6ed64".equals(e.getId()))
                .findFirst().get();

        softly.assertThat(liverDiag.getEvent().getLiverDiagInv()).isEqualTo("Tox screening for ethanol");
        softly.assertThat(liverDiag.getEvent().getLiverDiagInvSpec()).isNull();
        softly.assertThat(DaysUtil.truncLocalTime(liverDiag.getEvent().getLiverDiagInvDate())).isInSameDayAs("2017-02-18");
        softly.assertThat(liverDiag.getEvent().getLiverDiagInvResult()).isNull();
        softly.assertThat(liverDiag.getEvent().getPotentialHysLawCaseNum()).isNull();
    }
}
