package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ATLGroupByOptions.ASSESSMENT_WEEK;

public class ATLGroupByOptionsTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testAssessmentWeek() {

        Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1").baselineDate(DaysUtil.toDate("2014-11-08")).build();

        AssessedTargetLesion t1 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id1").subjectId("subjectId1")
                .targetLesionRaw(TargetLesionRaw.builder().lesionDate(DaysUtil.toDate("2015-01-31")).build())
                .assessmentFrequency(8).build(), subject1);

        AssessedTargetLesion t2 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id2").subjectId("subjectId1")
                .targetLesionRaw(TargetLesionRaw.builder().lesionDate(DaysUtil.toDate("2015-02-01")).build())
                .assessmentFrequency(8).build(), subject1);

        softly.assertThat(ASSESSMENT_WEEK.getAttribute().getFunction().apply(t1)).isEqualTo(8);
        softly.assertThat(ASSESSMENT_WEEK.getAttribute().getFunction().apply(t2)).isEqualTo(16);
    }

    @Test
    public void testAssessmentWeekMiddleOfOddFrequency() {

        Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1").baselineDate(DaysUtil.toDate("2014-01-01")).build();

        AssessedTargetLesion t1 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id3").subjectId("subjectId1")
                .targetLesionRaw(TargetLesionRaw.builder().lesionDate(DaysUtil.toDate("2014-01-02")).build())

                .assessmentFrequency(1).build(), subject1);

        AssessedTargetLesion t2 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id3").subjectId("subjectId1")
                .targetLesionRaw(TargetLesionRaw.builder().lesionDate(DaysUtil.toDate("2014-01-11")).build())
                .assessmentFrequency(1).build(), subject1);


        AssessedTargetLesion t3 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id1").subjectId("subjectId1")
                .targetLesionRaw(TargetLesionRaw.builder().lesionDate(DaysUtil.toDate("2014-01-18")).build())
                .assessmentFrequency(1).build(), subject1);

        AssessedTargetLesion t4 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id2").subjectId("subjectId1")
                .targetLesionRaw(TargetLesionRaw.builder().lesionDate(DaysUtil.toDate("2014-01-19")).build())
                .assessmentFrequency(1).build(), subject1);

        softly.assertThat(ASSESSMENT_WEEK.getAttribute().getFunction().apply(t1)).isEqualTo(1);
        softly.assertThat(ASSESSMENT_WEEK.getAttribute().getFunction().apply(t2)).isEqualTo(1);
        softly.assertThat(ASSESSMENT_WEEK.getAttribute().getFunction().apply(t3)).isEqualTo(2);
        softly.assertThat(ASSESSMENT_WEEK.getAttribute().getFunction().apply(t4)).isEqualTo(3);
    }
}
