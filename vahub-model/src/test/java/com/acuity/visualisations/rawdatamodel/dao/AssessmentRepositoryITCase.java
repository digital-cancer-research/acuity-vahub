package com.acuity.visualisations.rawdatamodel.dao;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.NoSuchElementException;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NOT_IMPLEMENTED;
import static com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw.Response.NOT_EVALUABLE;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AssessmentRepositoryITCase {

    @Autowired
    private AssessmentRepository assessmentRepository;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetAllTargetLesionsByDatasetId() {
        List<AssessmentRaw> events = assessmentRepository.getRawData(DUMMY_ACUITY_VA_ID);
        softly.assertThat(events.size()).isEqualTo(466);
    }

    @Test
    public void shouldGetTargetLesionsByDatasetIdWithCorrectValues() {
        AssessmentRaw lesion = assessmentRepository.getRawData(DUMMY_ACUITY_VA_ID).stream()
                .filter(l -> "1691b739ead444118528d8ed1c17169d".equals(l.getId()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cannot find element that must exist"));
        softly.assertThat(lesion.getSubjectId()).isEqualTo("4b3b6c30139f4fd38da2515c9904f27d");
        softly.assertThat(lesion.getAssessmentDate()).isNull();
        softly.assertThat(lesion.getVisitNumber()).isEqualTo(5);
        softly.assertThat(lesion.getLesionSite()).isEqualTo("Local Lymph Nodes");
        softly.assertThat(lesion.getNewLesionSinceBaseline()).isEqualTo("FALSE");
        softly.assertThat(lesion.getAssessmentMethod()).isEqualTo(NOT_IMPLEMENTED);
        softly.assertThat(lesion.getResponse()).isEqualTo(NOT_EVALUABLE.getName());
        softly.assertThat(lesion.getResponseRank()).isEqualTo(6);
    }
}
