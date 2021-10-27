package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.dataproviders.config.DataProviderConfiguration;
import com.acuity.visualisations.rawdatamodel.vo.RenalRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuityDataset;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
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
public class RenalDatasetsDataProviderITCase {

    @Autowired
    private RenalDatasetsDataProvider renalDatasetsDataProvider;

    private Dataset acuityDataset = new AcuityDataset(DUMMY_ACUITY_VA_ID);

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    private Collection<Renal> result;

    @Before
    public void setUp() throws Exception {
        result = renalDatasetsDataProvider.loadData(new Datasets(acuityDataset));
    }

    @Test
    public void testLoadDataFromAcuity() {
        assertThat(result).hasSize(3875);
    }

    @Test
    public void testLoadDataFromAcuityForDirectRenal() {

        RenalRaw renal = result.stream()
                .filter(r -> r.getSubjectId().equals("1c00ecdc4d4d4b2fae451af5d3acb711")
                        && r.getEvent().getVisitNumber() == 2)
                .findFirst().get().getEvent();

        softly.assertThat(renal.getSubjectId()).isEqualTo("1c00ecdc4d4d4b2fae451af5d3acb711");
        softly.assertThat(renal.getCkdStageName()).isEqualTo("CKD Stage 1");
        softly.assertThat(renal.getCkdStage()).isEqualTo(1);
        softly.assertThat(renal.getLabCode()).isEqualTo("Creatinine Clearance");
        softly.assertThat(renal.getResultValue()).isEqualTo(234.51);
        softly.assertThat(renal.getUnit()).isEqualTo("ml/min");
        softly.assertThat(renal.getVisitNumber()).isEqualTo(2);
    }

    @Test
    public void testLoadDataFromAcuityForAcuityCalculatedRenalWithUmolUnit() {

        RenalRaw renalEGRF = result.stream()
                .filter(r -> r.getSubjectId().equals("8c4ac8e70edd4da59d0c970af168b8c7")
                        && r.getEvent().getLabCode().contains("eGFR")
                        && r.getEvent().getVisitNumber() == 10)
                .findFirst().get().getEvent();
        RenalRaw renalCG = result.stream()
                .filter(r -> r.getSubjectId().equals("8c4ac8e70edd4da59d0c970af168b8c7")
                        && r.getEvent().getLabCode().contains("C-G")
                        && r.getEvent().getVisitNumber() == 10)
                .findFirst().get().getEvent();

        softly.assertThat(renalEGRF.getLabCode()).isEqualTo("ACUITY Calculated CrCl, eGFR");
        softly.assertThat(renalEGRF.getResultValue()).isEqualTo(108.74);
        softly.assertThat(renalEGRF.getUnit()).isEqualTo("mL/min");
        softly.assertThat(renalEGRF.getCkdStage()).isEqualTo(1);
        softly.assertThat(renalEGRF.getCkdStageName()).isEqualTo("CKD Stage 1");

        softly.assertThat(renalCG.getLabCode()).isEqualTo("ACUITY Calculated CrCl, C-G");
        softly.assertThat(renalCG.getResultValue()).isEqualTo(143.82);
        softly.assertThat(renalCG.getUnit()).isEqualTo("mL/min");
        softly.assertThat(renalCG.getCkdStage()).isEqualTo(1);
        softly.assertThat(renalCG.getCkdStageName()).isEqualTo("CKD Stage 1");
    }
}
