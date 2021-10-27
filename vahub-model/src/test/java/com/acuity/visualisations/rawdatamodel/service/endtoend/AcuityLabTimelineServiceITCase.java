package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.timeline.LabTimelineService;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.LabTests;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.Categories;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.Labcodes;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.SubjectLabsCategories;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.SubjectLabsDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.labs.SubjectLabsSummary;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
@Category(LabTests.class)
public class AcuityLabTimelineServiceITCase {

    @Autowired
    private LabTimelineService labTimelineService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    // Adopted from WhenRunningTimelineLabsServiceRepositoryQueriesITCase
    public void shouldListLabsDetailsForSubject() {
        final String subject = "E000010016";
        final String subjectId = "96e171ba9679408faf63a8d5ac8814bf";

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singleton(subject)));

        List<SubjectLabsDetail> labsDetails = labTimelineService
                .getTimelineDetails(DUMMY_ACUITY_DATASETS, LabFilters.empty(), populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        softly.assertThat(labsDetails).hasSize(1);

        SubjectLabsDetail subjectLabDetail = labsDetails.stream().filter(labs -> labs.getSubjectId().equals(subjectId)).findFirst().get();

        softly.assertThat(subjectLabDetail.getSubject()).isEqualTo(subject);
        softly.assertThat(subjectLabDetail.getSubjectId()).isEqualTo(subjectId);

        Labcodes albuminLabcode = subjectLabDetail.getLabcodes().stream().filter(code -> code.getLabcode().equals("Potassium")).findFirst().get();

        softly.assertThat(albuminLabcode.getEvents()).extracting("valueRaw").containsOnly(4.18, 4.13, 4.05, 4.03, 4.02, 4.29);
        softly.assertThat(albuminLabcode.getEvents()).extracting("baselineValue").containsOnly(4.18);
        softly.assertThat(albuminLabcode.getEvents()).extracting("unitRaw").containsOnly("mmol/L");

        softly.assertThat(albuminLabcode.getEvents()).extracting("valueChangeFromBaseline").containsOnly(0.0, -0.05, -0.13, -0.15, -0.16, 0.11);
        softly.assertThat(albuminLabcode.getEvents()).extracting("unitChangeFromBaseline").containsOnly("mmol/L");

        softly.assertThat(albuminLabcode.getEvents()).extracting("valuePercentChangeFromBaseline").containsOnly(0.0, -1.2, -3.11, -3.59, -3.83, 2.63);
        softly.assertThat(albuminLabcode.getEvents()).extracting("unitPercentChangeFromBaseline").containsOnly("%");

        softly.assertThat(albuminLabcode.getEvents()).extracting("visitNumber").containsOnly(1., 2., 3., 4., 5., 99.);
        softly.assertThat(albuminLabcode.getEvents()).extracting("numBelowReferenceRange").containsOnly(0);
        softly.assertThat(albuminLabcode.getEvents()).extracting("numAboveReferenceRange").containsOnly(0);
        softly.assertThat(albuminLabcode.getEvents()).extracting("start.dayHour").containsOnly(-6.5, 0.5, 7.5, 14.5, 28.5, 36.5);
    }


    @Test
    // Adopted from WhenRunningTimelineLabsServiceRepositoryQueriesITCase
    public void shouldListLabsDetails() {
        final String subject = "E000010016";
        final String subjectId = "96e171ba9679408faf63a8d5ac8814bf";
        final String subject2 = "E0000100116";
        final String subject2Id = "9cf6f3e6d0a147fa98f6dfb003d4baee";

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Arrays.asList(subject, subject2)));

        List<SubjectLabsDetail> labsDetails = labTimelineService
                .getTimelineDetails(DUMMY_ACUITY_DATASETS, LabFilters.empty(), populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectLabsDetail subjectLabDetail = labsDetails.stream().filter(labs -> labs.getSubjectId().equals(subjectId)).findFirst().get();

        softly.assertThat(subjectLabDetail.getSubject()).isEqualTo(subject);
        softly.assertThat(subjectLabDetail.getSubjectId()).isEqualTo(subjectId);


        Labcodes albuminLabcode = subjectLabDetail.getLabcodes().stream().filter(code -> code.getLabcode().equals("Potassium")).findFirst().get();

        softly.assertThat(albuminLabcode.getRefHigh()).isEqualTo(5.6);
        softly.assertThat(albuminLabcode.getRefLow()).isEqualTo(3.5);
        softly.assertThat(albuminLabcode.getEvents()).extracting("valueRaw").containsOnly(4.18, 4.13, 4.05, 4.03, 4.02, 4.29);
        softly.assertThat(albuminLabcode.getEvents()).extracting("baselineValue").containsOnly(4.18);
        softly.assertThat(albuminLabcode.getEvents()).extracting("unitRaw").containsOnly("mmol/L");

        softly.assertThat(albuminLabcode.getEvents()).extracting("valueChangeFromBaseline").containsOnly(0.0, -0.05, -0.13, -0.15, -0.16, 0.11);
        softly.assertThat(albuminLabcode.getEvents()).extracting("unitChangeFromBaseline").containsOnly("mmol/L");

        softly.assertThat(albuminLabcode.getEvents()).extracting("valuePercentChangeFromBaseline").containsOnly(0.0, -1.2, -3.11, -3.59, -3.83, 2.63);
        softly.assertThat(albuminLabcode.getEvents()).extracting("unitPercentChangeFromBaseline").containsOnly("%");

        softly.assertThat(albuminLabcode.getEvents()).extracting("visitNumber").containsOnly(1., 2., 3., 4., 5., 99.);
        softly.assertThat(albuminLabcode.getEvents()).extracting("numBelowReferenceRange").containsOnly(0);
        softly.assertThat(albuminLabcode.getEvents()).extracting("numAboveReferenceRange").containsOnly(0);
        softly.assertThat(albuminLabcode.getEvents()).extracting("start.dayHour").containsOnly(-6.5, 0.5, 7.5, 14.5, 28.5, 36.5);
        softly.assertThat(albuminLabcode.getEvents()).extracting("start.doseDayHour").containsOnly(-6.5, 0.5, 7.5, 14.5, 28.5, 36.5);


        SubjectLabsDetail subject2LabDetail = labsDetails.stream().filter(labs -> labs.getSubjectId().equals(subject2Id)).findFirst().get();

        softly.assertThat(subject2LabDetail.getSubject()).isEqualTo(subject2);
        softly.assertThat(subject2LabDetail.getSubjectId()).isEqualTo(subject2Id);

        Labcodes glucoseLabcode2 = subject2LabDetail.getLabcodes().stream()
                .filter(code -> code.getLabcode().equals("Neutrophils, Particle Concentration")).findFirst().get();

        softly.assertThat(glucoseLabcode2.getRefHigh()).isEqualTo(7.0);
        softly.assertThat(glucoseLabcode2.getRefLow()).isEqualTo(2.0);
        softly.assertThat(glucoseLabcode2.getEvents()).extracting("valueRaw").containsSequence(4.5, 7.9, 9.9, 6.0);
        softly.assertThat(glucoseLabcode2.getEvents()).extracting("baselineValue").containsOnly(4.5);
        softly.assertThat(glucoseLabcode2.getEvents()).extracting("unitRaw").containsOnly("10**9/L");

        softly.assertThat(glucoseLabcode2.getEvents()).extracting("valueChangeFromBaseline").containsSequence(0.0, 3.4, 5.4, 1.5);
        softly.assertThat(glucoseLabcode2.getEvents()).extracting("unitChangeFromBaseline").containsOnly("10**9/L");

        softly.assertThat(glucoseLabcode2.getEvents()).extracting("valuePercentChangeFromBaseline").startsWith(0.0, 75.56, 120.0, 33.33);
        softly.assertThat(glucoseLabcode2.getEvents()).extracting("unitPercentChangeFromBaseline").containsOnly("%");

        softly.assertThat(glucoseLabcode2.getEvents()).extracting("visitNumber").containsSequence(1., 2., 3., 99.);
    }

    @Test
    // Adopted from WhenRunningTimelineLabsServiceRepositoryQueriesITCase
    public void shouldListLabsSummaryCategories() {
        final String subject = "E000010016";
        final String subjectId = "96e171ba9679408faf63a8d5ac8814bf";
        final String subject2 = "E0000100116";
        final String subject2Id = "9cf6f3e6d0a147fa98f6dfb003d4baee";

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Arrays.asList(subject, subject2)));

        List<SubjectLabsCategories> labsCategories = labTimelineService
                .getTimelineCategories(DUMMY_ACUITY_DATASETS, LabFilters.empty(), populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectLabsCategories subjectLabsCategories = labsCategories.stream().filter(labs -> labs.getSubjectId().equals(subjectId)).findFirst().get();

        softly.assertThat(subjectLabsCategories.getSubject()).isEqualTo(subject);
        softly.assertThat(subjectLabsCategories.getSubjectId()).isEqualTo(subjectId);

        Categories defaultGroup = subjectLabsCategories.getLabcodes().stream().filter(code -> code.getCategory().equals("Hematology")).findFirst().get();

        softly.assertThat(defaultGroup.getEvents()).extracting("visitNumber").containsSequence(1., 2., 3., 4., 5., 99.);
        softly.assertThat(defaultGroup.getEvents()).extracting("numBelowReferenceRange").containsSequence(2, 1, 2, 2, 2, 2);
        softly.assertThat(defaultGroup.getEvents()).extracting("numAboveReferenceRange").containsSequence(0, 0, 0, 1, 0, 0);
        softly.assertThat(defaultGroup.getEvents()).extracting("start.dayHour").containsOnly(-6.5, 0.5, 7.5, 14.5, 28.5, 36.5);

        SubjectLabsCategories subjectLabsCategories2 = labsCategories.stream().filter(labs -> labs.getSubjectId().equals(subject2Id)).findFirst().get();

        softly.assertThat(subjectLabsCategories2.getSubject()).isEqualTo(subject2);
        softly.assertThat(subjectLabsCategories2.getSubjectId()).isEqualTo(subject2Id);

        Categories defaultGroup2 = subjectLabsCategories2.getLabcodes().stream().filter(code -> code.getCategory().equals("Hematology")).findFirst().get();

        softly.assertThat(defaultGroup2.getEvents()).extracting("visitNumber").containsSequence(1., 2., 3., 99.);
        softly.assertThat(defaultGroup2.getEvents()).extracting("numBelowReferenceRange").containsSequence(0, 0, 1, 1);
        softly.assertThat(defaultGroup2.getEvents()).extracting("numAboveReferenceRange").containsSequence(0, 2, 2, 0);
        softly.assertThat(defaultGroup2.getEvents()).extracting("start.dayHour").containsOnly(-0.5, 0.5, 7.5, 14.5);

    }

    @Test
    // Adopted from WhenRunningTimelineLabsServiceRepositoryQueriesITCase
    public void shouldListLabsSummaryCategoriesForSubject() {
        final String subject = "E000010016";
        final String subjectId = "96e171ba9679408faf63a8d5ac8814bf";

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singleton(subject)));

        List<SubjectLabsCategories> labsCategories = labTimelineService
                .getTimelineCategories(DUMMY_ACUITY_DATASETS, LabFilters.empty(), populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectLabsCategories subjectLabsCategories = labsCategories.stream().filter(labs -> labs.getSubjectId().equals(subjectId)).findFirst().get();

        softly.assertThat(subjectLabsCategories.getSubject()).isEqualTo(subject);
        softly.assertThat(subjectLabsCategories.getSubjectId()).isEqualTo(subjectId);

        Categories chemistry = subjectLabsCategories.getLabcodes().stream().filter(code -> code.getCategory().equals("Chemistry")).findFirst().get();

        softly.assertThat(chemistry.getEvents()).extracting("visitNumber").containsSequence(1., 2., 3., 4., 5., 99.);
        softly.assertThat(chemistry.getEvents()).extracting("numBelowReferenceRange").containsSequence(0, 0, 0, 0, 0, 0);
        softly.assertThat(chemistry.getEvents()).extracting("numAboveReferenceRange").containsSequence(0, 1, 3, 3, 1, 1);
        softly.assertThat(chemistry.getEvents()).extracting("start.dayHour").containsOnly(-6.5, 0.5, 7.5, 14.5, 28.5, 36.5);

        Categories hematology = subjectLabsCategories.getLabcodes().stream().filter(code -> code.getCategory().equals("Hematology")).findFirst().get();

        softly.assertThat(hematology.getEvents()).extracting("visitNumber").containsSequence(1., 2., 3., 4., 5., 99.);
        softly.assertThat(hematology.getEvents()).extracting("numBelowReferenceRange").containsSequence(2, 1, 2, 2, 2, 2);
        softly.assertThat(hematology.getEvents()).extracting("numAboveReferenceRange").containsSequence(0, 0, 0, 1, 0, 0);
        softly.assertThat(hematology.getEvents()).extracting("start.dayHour").containsOnly(-6.5, 0.5, 7.5, 14.5, 28.5, 36.5);
    }

    @Test
    // Adopted from WhenRunningTimelineLabsServiceRepositoryQueriesITCase
    public void shouldListLabsSummary() {
        final String subject = "E000010016";
        final String subjectId = "96e171ba9679408faf63a8d5ac8814bf";
        final String subject2 = "E0000100116";
        final String subject2Id = "9cf6f3e6d0a147fa98f6dfb003d4baee";

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Arrays.asList(subject, subject2)));

        List<SubjectLabsSummary> labsSummaries = labTimelineService
                .getTimelineSummaries(DUMMY_ACUITY_DATASETS, LabFilters.empty(), populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectLabsSummary subjectLabsSummary = labsSummaries.stream().filter(labs -> labs.getSubjectId().equals(subjectId)).findFirst().get();

        softly.assertThat(subjectLabsSummary.getSubject()).isEqualTo(subject);
        softly.assertThat(subjectLabsSummary.getSubjectId()).isEqualTo(subjectId);

        softly.assertThat(subjectLabsSummary.getEvents()).extracting("visitNumber").containsSequence(1., 2., 3., 4., 5., 99.);
        softly.assertThat(subjectLabsSummary.getEvents()).extracting("numBelowReferenceRange").containsSequence(2, 1, 2, 2, 2, 2);
        softly.assertThat(subjectLabsSummary.getEvents()).extracting("numAboveReferenceRange").containsSequence(0, 1, 3, 4, 1, 1);
        softly.assertThat(subjectLabsSummary.getEvents()).extracting("start.dayHour").containsOnly(-6.5, 0.5, 7.5, 14.5, 28.5, 36.5);
        softly.assertThat(subjectLabsSummary.getEvents()).extracting("start.studyDayHourAsString")
                .containsOnly("-6d 12:00", "1d 12:00", "8d 12:00", "15d 12:00", "29d 12:00", "37d 12:00");

        SubjectLabsSummary subjectLabsSummary2 = labsSummaries.stream().filter(labs -> labs.getSubjectId().equals(subject2Id)).findFirst().get();

        softly.assertThat(subjectLabsSummary2.getSubject()).isEqualTo(subject2);
        softly.assertThat(subjectLabsSummary2.getSubjectId()).isEqualTo(subject2Id);

        softly.assertThat(subjectLabsSummary2.getEvents()).extracting("visitNumber").containsSequence(1., 2., 3., 99.);
        softly.assertThat(subjectLabsSummary2.getEvents()).extracting("numBelowReferenceRange").containsSequence(0, 0, 1, 1);
        softly.assertThat(subjectLabsSummary2.getEvents()).extracting("numAboveReferenceRange").containsSequence(4, 7, 7, 6);
        softly.assertThat(subjectLabsSummary2.getEvents()).extracting("start.dayHour").containsOnly(-0.5, 0.5, 7.5, 14.5);
        softly.assertThat(subjectLabsSummary2.getEvents()).extracting("start.studyDayHourAsString")
                .containsOnly("-0d 12:00", "1d 12:00", "8d 12:00", "15d 12:00");
    }

    @Test
    // Adopted from WhenRunningTimelineLabsServiceRepositoryQueriesITCase
    public void shouldListLabsSummaryForStudyDate() {

        final String subject = "E000010016";
        final String subjectId = "96e171ba9679408faf63a8d5ac8814bf";
        final String subject2 = "E0000100116";
        final String subject2Id = "9cf6f3e6d0a147fa98f6dfb003d4baee";

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Arrays.asList(subject, subject2)));

        List<SubjectLabsSummary> labsSummaries = labTimelineService
                .getTimelineSummaries(DUMMY_ACUITY_DATASETS, LabFilters.empty(), populationFilters,
                        DayZeroType.DAYS_SINCE_STUDY_DAY, null);


        SubjectLabsSummary subjectLabsSummary = labsSummaries.stream().filter(labs -> labs.getSubjectId().equals(subjectId)).findFirst().get();

        softly.assertThat(subjectLabsSummary.getSubject()).isEqualTo(subject);
        softly.assertThat(subjectLabsSummary.getSubjectId()).isEqualTo(subjectId);

        softly.assertThat(subjectLabsSummary.getEvents()).extracting("visitNumber").containsSequence(1., 2., 3., 4., 5., 99.);
        softly.assertThat(subjectLabsSummary.getEvents()).extracting("numBelowReferenceRange").containsSequence(2, 1, 2, 2, 2, 2);
        softly.assertThat(subjectLabsSummary.getEvents()).extracting("numAboveReferenceRange").containsSequence(0, 1, 3, 4, 1, 1);
        softly.assertThat(subjectLabsSummary.getEvents()).extracting("start.dayHour").containsOnly(-5.5, 1.5, 8.5, 15.5, 29.5, 37.5);

        SubjectLabsSummary subjectLabsSummary2 = labsSummaries.stream().filter(labs -> labs.getSubjectId().equals(subject2Id)).findFirst().get();

        softly.assertThat(subjectLabsSummary2.getSubject()).isEqualTo(subject2);
        softly.assertThat(subjectLabsSummary2.getSubjectId()).isEqualTo(subject2Id);

        softly.assertThat(subjectLabsSummary2.getEvents()).extracting("visitNumber").containsSequence(1., 2., 3., 99.);
        softly.assertThat(subjectLabsSummary2.getEvents()).extracting("numBelowReferenceRange").containsOnly(0, 1);
        softly.assertThat(subjectLabsSummary2.getEvents()).extracting("numAboveReferenceRange").containsSequence(4, 7, 7, 6);
        softly.assertThat(subjectLabsSummary2.getEvents()).extracting("start.dayHour").containsOnly(0.5, 1.5, 8.5, 15.5);
    }

    @Test
    // Adopted from WhenRunningTimelineLabsServiceRepositoryQueriesITCase
    public void shouldListLabsSummaryFromSubject() {
        final String subject = "E000010016";
        final String subjectId = "96e171ba9679408faf63a8d5ac8814bf";

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singleton(subject)));

        List<SubjectLabsSummary> labsSummaries = labTimelineService
                .getTimelineSummaries(DUMMY_ACUITY_DATASETS, LabFilters.empty(), populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE, null);


        SubjectLabsSummary subjectLabsSummary = labsSummaries.stream().filter(labs -> labs.getSubjectId().equals(subjectId)).findFirst().get();

        softly.assertThat(subjectLabsSummary.getSubject()).isEqualTo(subject);
        softly.assertThat(subjectLabsSummary.getSubjectId()).isEqualTo(subjectId);

        softly.assertThat(subjectLabsSummary.getEvents()).extracting("visitNumber").containsSequence(1., 2., 3., 4., 5., 99.);
        softly.assertThat(subjectLabsSummary.getEvents()).extracting("numBelowReferenceRange").containsSequence(2, 1, 2, 2, 2, 2);
        softly.assertThat(subjectLabsSummary.getEvents()).extracting("numAboveReferenceRange").containsSequence(0, 1, 3, 4, 1, 1);
        softly.assertThat(subjectLabsSummary.getEvents()).extracting("start.dayHour").containsOnly(-6.5, 0.5, 7.5, 14.5, 28.5, 36.5);
    }

}
