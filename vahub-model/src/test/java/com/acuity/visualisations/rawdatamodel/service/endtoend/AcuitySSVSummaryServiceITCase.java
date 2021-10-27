package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.service.ssv.SSVSummaryService;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputSSVSummaryData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputSSVSummaryMetadata;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.groups.Tuple;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuitySSVSummaryServiceITCase {
    @Autowired
    private SSVSummaryService ssvSummaryService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldReturnCorrectSubjectSummaries() {

        String subjectId = "d19e5667e5da4e6098484fe6e81c8901";

        OutputSSVSummaryData subjectDetails = ssvSummaryService.getSingleSubjectData(DUMMY_ACUITY_DATASETS, subjectId);

        softly.assertThat(subjectDetails.getSubjectId()).isEqualTo("E0000100107");
        softly.assertThat(Collections.singletonList(subjectDetails.getStudy())).flatExtracting(Map::keySet).containsOnly(
                "eventId", "drugProjectName", "studyPart", "studyName",
                "centerNumber", "datasetName",
                "dateOfWithdrawal", "dateOfDeath", "dateOfRandomisation",
                "studyId", "firstTreatmentDate", "reasonForWithdrawal", "otherCohort");

        softly.assertThat(Collections.singletonList(subjectDetails.getStudy()))
                .extracting(e -> e.get("centre"), e -> e.get("centerNumber"),
                        e -> e.get("studyId"), e -> e.get("studyName"), e -> e.get("datasetName"), e -> e.get("studyPart"),
                        e -> e.get("dateOfWithdrawal"), e -> e.get("reasonForWithdrawal"), e -> e.get("otherCohort"))
                .contains(
                        Tuple.tuple(null, "1", "D1234C00001", "D1234C00001", "D1234C00001", "B", null, "No Withdrawal/Completion", null)
                );

        softly.assertThat(Collections.singletonList(subjectDetails.getDemography())).flatExtracting(Map::keySet).containsOnly(
                "country", "sex", "race", "height", "weight", "age", "eventId", "ethnicGroup");


        softly.assertThat(Collections.singletonList(subjectDetails.getDemography()))
                .extracting(e -> e.get("country"), e -> e.get("region"), e -> e.get("sex"),
                        e -> e.get("race"), e -> e.get("height"))
                .contains(
                        Tuple.tuple(null, null, "Male", "White", "176")
                );


        assertThat(subjectDetails.getMedicalHistories()).isEqualTo("");
    }

    @Test
    public void shouldReturnCorrectSubjectDetailMetadata() {

        OutputSSVSummaryMetadata subjectDetailMetadata = ssvSummaryService.getSingleSubjectMetadata(DUMMY_ACUITY_DATASETS);

        softly.assertThat(subjectDetailMetadata.getMedicalHistories()).isFalse();

        softly.assertThat(new ArrayList<>(subjectDetailMetadata.getDemography().keySet()))
                .containsOnly("sex", "race", "height", "weight", "age");

        softly.assertThat(new ArrayList<>(subjectDetailMetadata.getStudy().keySet()))
                .containsOnly("studyName", "studyPart", "dateOfWithdrawal", "reasonForWithdrawal", "centerNumber", "dateOfRandomisation",
                        "drugProjectName", "firstTreatmentDate", "studyId", "datasetName", "dateOfDeath");

    }
}
