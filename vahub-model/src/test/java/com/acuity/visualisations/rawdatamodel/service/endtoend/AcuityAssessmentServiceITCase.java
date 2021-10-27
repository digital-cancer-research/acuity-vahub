package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.AssessmentFilters;
import com.acuity.visualisations.rawdatamodel.service.event.AssessmentService;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NOT_IMPLEMENTED;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityAssessmentServiceITCase {

    private static final String SUBJECT_ID = "3bcfce2e4f80485f80d0be83422e62ac";
    private static final String ASSESSMENT_DATE = "assessmentDate";
    private static final String VISIT_DATE = "visitDate";
    private static final String STUDY_DAY = "studyDay";
    private static final String METHOD = "assessmentMethod";
    private static final String LESION_SITE = "lesionSite";
    private static final String RESPONSE = "newLesionResponse";
    private static final String VISIT_NUMBER = "visitNumber";

    @Autowired
    private AssessmentService assessmentService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetSSVAssessmentData() {
        List<Map<String, String>> ssvData =
                assessmentService.getSingleSubjectData(DUMMY_ACUITY_DATASETS, SUBJECT_ID, AssessmentFilters.empty());

        softly.assertThat(ssvData).hasSize(2);
        Map<String, String> line = ssvData.stream()
                .filter(m -> "1".equals(m.get(VISIT_NUMBER)))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("Cannot find element that must exist"));
        softly.assertThat(line.keySet()).contains(ASSESSMENT_DATE, VISIT_DATE, STUDY_DAY, METHOD, LESION_SITE, RESPONSE, VISIT_NUMBER);

        softly.assertThat(line.get(METHOD)).isEqualTo(NOT_IMPLEMENTED);
        softly.assertThat(line.get(ASSESSMENT_DATE)).isNull();
        softly.assertThat(line.get(VISIT_DATE)).isEqualTo("2014-12-29T12:00:00");
        softly.assertThat(line.get(RESPONSE)).isEqualTo("No");
        softly.assertThat(line.get(VISIT_NUMBER)).isEqualTo("1");
        softly.assertThat(line.get(LESION_SITE)).isEqualTo("Breast");
    }

}
