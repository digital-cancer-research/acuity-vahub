package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.LiverRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuityDataset;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class LiverDatasetsDataProviderITCase {
    @Autowired
    private LiverDatasetsDataProvider dataProvider;

    private Dataset acuityDataset = new AcuityDataset(DUMMY_ACUITY_VA_ID);

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testLoadDataFromAcuityUploadedFile() {

        Collection<Liver> result = dataProvider.loadData(new Datasets(acuityDataset));

        softly.assertThat(result).hasSize(6005);

        List<Liver> liversPerSubject = result.stream()
                .filter(e -> "11ff859bfed84222bf6cdf3f8fc53128".equals(e.getSubjectId()))
                .collect(Collectors.toList());

        softly.assertThat(liversPerSubject.size()).isEqualTo(304);

        Liver liver = liversPerSubject.stream()
                .filter(e -> "ce69fd0d77f24e23aa83931a361b8e1d".equals(e.getId()))
                .findFirst().get();

        softly.assertThat(liver.getEvent().getBaselineValue()).isEqualTo(22.);
        softly.assertThat(liver.getEvent().getBaselineFlag()).isEqualTo("N");

    }

    @Test
    public void shouldGetLiversByDatasetIdWithCorrectValue() {
        Collection<Liver> events = dataProvider.loadData(DUMMY_ACUITY_DATASETS);

        softly.assertThat(events.size()).isEqualTo(6005);

        LiverRaw liver = events.stream()
                .filter(t -> t.getId().equals("c08c0a56df7044c9b94dd62e865bd4f0"))
                .findFirst().get().getEvent();

        softly.assertThat(liver.getSubjectId()).isEqualTo("08ec400e0a694717bc378b370ac59926");
        softly.assertThat(liver.getCategory()).isEqualTo("Chemistry");
        softly.assertThat(liver.getLabCode()).isEqualTo("Alanine Aminotransferase");
        softly.assertThat(liver.getMeasurementTimePoint()).isInSameDayAs(DaysUtil.toDate("2015-05-06"));
        softly.assertThat(liver.getVisitNumber()).isEqualTo(9.);
        softly.assertThat(liver.getValue()).isEqualTo(32);
        softly.assertThat(liver.getUnit()).isEqualTo("U/L");
        softly.assertThat(liver.getRefHigh()).isEqualTo(30);
        softly.assertThat(liver.getRefLow()).isEqualTo(6);
        softly.assertThat(liver.getProtocolScheduleTimepoint()).isNull();
        softly.assertThat(liver.getValueDipstick()).isNull();
        softly.assertThat(liver.getComment()).isNull();
        softly.assertThat(liver.getVisitDescription()).isNull();
        softly.assertThat(liver.getDaysSinceFirstDose()).isEqualTo(63);
    }
}
