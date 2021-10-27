package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.TargetLesion;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.groups.Tuple;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_RECIST_DATASETS;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class TargetLesionDatasetsDataProviderITCase {

    @Autowired
    private TargetLesionDatasetsDataProvider dataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    /**
     * IT test required as {@link TargetLesion} is has number of calculated fields
     * So, it is required to check that data is loaded.
     */
    @Test
    public void testLoadDataFromUploadedFile() {

        Collection<TargetLesion> result = dataProvider.loadData(DUMMY_ACUITY_RECIST_DATASETS);

        softly.assertThat(result).hasSize(1055);

        List<TargetLesion> tlsPerSubject = result.stream()
                .filter(e -> "8092cad631ab45f98e11f53b35ffa6d3".equals(e.getSubjectId()))
                .collect(Collectors.toList());

        softly.assertThat(tlsPerSubject.size()).isEqualTo(12);

        // baseline
        List<TargetLesion> blTlsPerSubject = tlsPerSubject.stream()
                .filter(e -> e.getEvent().isBaseline())
                .collect(Collectors.toList());

        softly.assertThat(blTlsPerSubject).hasSize(3);
        TargetLesionRaw baseline = blTlsPerSubject.stream().findAny().get().getEvent();

        softly.assertThat(baseline).extracting(TargetLesionRaw::getSumPercentageChangeFromBaseline,
                TargetLesionRaw::getSumPercentageChangeFromMinimum,
                TargetLesionRaw::getSumChangeFromMinimum).containsExactly(0.00, 0.00, 0);

        // next visit
        TargetLesionRaw tlVisit1 = tlsPerSubject.stream()
                .filter(e -> e.getEvent().getVisitNumber() == 1).findAny().get().getEvent();

        softly.assertThat(tlVisit1).extracting(TargetLesionRaw::getLesionsDiameterPerAssessment, TargetLesionRaw::getSumPercentageChangeFromBaseline,
                TargetLesionRaw::getSumPercentageChangeFromMinimum, TargetLesionRaw::getSumChangeFromMinimum)
                .containsExactly(73, -26.26, -26.26, -26);

        // other visit
        TargetLesionRaw tlVisit3 = tlsPerSubject.stream()
                .filter(e -> e.getEvent().getVisitNumber() == 3).findAny().get().getEvent();

        softly.assertThat(tlVisit3).extracting(TargetLesionRaw::getLesionsDiameterPerAssessment, TargetLesionRaw::getSumPercentageChangeFromBaseline,
                TargetLesionRaw::getSumPercentageChangeFromMinimum, TargetLesionRaw::getSumChangeFromMinimum)
                .containsExactly(52, -47.47, 1.96, 1);

    }

    @Test
    public void testLoadDataForSubjectWithNullBaselineDiameters() {

        Collection<TargetLesion> result = dataProvider.loadData(DUMMY_ACUITY_RECIST_DATASETS);

        List<TargetLesion> atlsPerSubject = result.stream()
                .filter(e -> "8092cad631ab45f98e11f53b35ffa6d3".equals(e.getSubjectId()))
                .collect(Collectors.toList());

        softly.assertThat(atlsPerSubject.size()).isEqualTo(12);

        softly.assertThat(atlsPerSubject)
                .extracting(t -> t.getEvent().getVisitNumber(),
                        t -> t.getEvent().getLesionDiameter(), t -> t.getEvent().getLesionsDiameterPerAssessment(),
                        t -> t.getEvent().isBaseline(), t -> t.getEvent().getSumPercentageChangeFromBaseline(),
                        t -> t.getEvent().getSumChangeFromMinimum(), t -> t.getEvent().getSumPercentageChangeFromMinimum())
                .containsSequence(
                        Tuple.tuple(0, 48, 99, true, 0.0, 0, 0.0),
                        Tuple.tuple(0, 23, 99, true, 0.0, 0, 0.0),
                        Tuple.tuple(0, 28, 99, true, 0.0, 0, 0.0),
                        Tuple.tuple(1, 35, 73, false, -26.26, -26, -26.26),
                        Tuple.tuple(1, 17, 73, false, -26.26, -26, -26.26),
                        Tuple.tuple(1, 21, 73, false, -26.26, -26, -26.26),
                        Tuple.tuple(2, 30, 51, false, -48.48, -22, -30.14),
                        Tuple.tuple(2, 5, 51, false, -48.48, -22, -30.14),
                        Tuple.tuple(2, 16, 51, false, -48.48, -22, -30.14),
                        Tuple.tuple(3, 32, 52, false, -47.47, 1, 1.96),
                        Tuple.tuple(3, 6, 52, false, -47.47, 1, 1.96),
                        Tuple.tuple(3, 14, 52, false, -47.47, 1, 1.96)
                );
        softly.assertThat(atlsPerSubject.get(0).getEvent().getLesionDate()).isInSameDayAs(DaysUtil.toDate("2015-03-09"));

    }

    @Test
    public void testLoadDataForSubjectWithNullAssessmentDiameters() {

        Collection<TargetLesion> result = dataProvider.loadData(DUMMY_ACUITY_RECIST_DATASETS);

        List<TargetLesion> atlsPerSubject = result.stream()
                .filter(e -> "8092cad631ab45f98e11f53b35ffa6d3".equals(e.getSubjectId()))
                .collect(Collectors.toList());

        softly.assertThat(atlsPerSubject.size()).isEqualTo(12);

        softly.assertThat(atlsPerSubject)
                .extracting(t -> t.getEvent().getVisitNumber(),
                        t -> t.getEvent().getLesionDiameter(), t -> t.getEvent().getLesionsDiameterPerAssessment(),
                        t -> t.getEvent().isBaseline(), t -> t.getEvent().getSumPercentageChangeFromBaseline(),
                        t -> t.getEvent().getSumChangeFromMinimum(), t -> t.getEvent().getSumPercentageChangeFromMinimum())
                .containsSequence(
                        Tuple.tuple(0, 48, 99, true, 0.0, 0, 0.0),
                        Tuple.tuple(0, 23, 99, true, 0.0, 0, 0.0),
                        Tuple.tuple(0, 28, 99, true, 0.0, 0, 0.0),
                        Tuple.tuple(1, 35, 73, false, -26.26, -26, -26.26),
                        Tuple.tuple(1, 17, 73, false, -26.26, -26, -26.26),
                        Tuple.tuple(1, 21, 73, false, -26.26, -26, -26.26),
                        Tuple.tuple(2, 30, 51, false, -48.48, -22, -30.14),
                        Tuple.tuple(2, 5, 51, false, -48.48, -22, -30.14),
                        Tuple.tuple(2, 16, 51, false, -48.48, -22, -30.14),
                        Tuple.tuple(3, 32, 52, false, -47.47, 1, 1.96),
                        Tuple.tuple(3, 6, 52, false, -47.47, 1, 1.96),
                        Tuple.tuple(3, 14, 52, false, -47.47, 1, 1.96)
                );
    }
}
