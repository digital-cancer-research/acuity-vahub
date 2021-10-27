package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.visualisations.rawdatamodel.service.timeline.VitalsTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.vitals.SubjectVitalsDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.vitals.SubjectVitalsSummary;
import com.acuity.visualisations.rawdatamodel.vo.timeline.vitals.VitalsSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.vitals.VitalsTests;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityVitalsTimelineServiceITCase {

    private static final String NORMAL_SUBJECT_ID = "9cf6f3e6d0a147fa98f6dfb003d4baee";
    private static final String NORMAL_SUBJECT = "E0000100116";

    @Autowired
    private VitalsTimelineService vitalsTimelineService;
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    // Adopted from WhenRunningVitalsServiceQueriesITCase
    public void shouldListDetailForNormalSubject() {
        List<SubjectVitalsDetail> subjectDetails = vitalsTimelineService.getVitalsDetails(
                DUMMY_ACUITY_DATASETS, VitalFilters.empty(), PopulationFilters.empty(),
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectVitalsDetail normalSubjectDetail = subjectDetails.stream()
                .filter(aesd -> aesd.getSubjectId().equals(NORMAL_SUBJECT_ID))
                .findFirst()
                .get();
        softly.assertThat(normalSubjectDetail.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);
        softly.assertThat(normalSubjectDetail.getSubject()).isEqualTo(NORMAL_SUBJECT);
        softly.assertThat(normalSubjectDetail.getSex()).isEqualTo("Female");

        VitalsTests test = normalSubjectDetail.getTests().get(0);

        softly.assertThat(test.getBaseline().getDate()).isInSameDayAs("2015-02-16");
        softly.assertThat(test.getBaseline().getStudyDayHourAsString()).isEqualTo("-12d 12:00");
    }

    @Test
    // Adopted from WhenRunningVitalsServiceQueriesITCase
    public void shouldListSummaryForNormalSubject() {

        List<SubjectVitalsSummary> subjectVitalsSummaries = vitalsTimelineService.getVitalsSummaries(
                DUMMY_ACUITY_DATASETS, VitalFilters.empty(), PopulationFilters.empty(),
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        softly.assertThat(subjectVitalsSummaries).hasSize(123);

        SubjectVitalsSummary normalSubjectSummary = subjectVitalsSummaries.stream()
                .filter(aesd -> aesd.getSubjectId().equals(NORMAL_SUBJECT_ID))
                .findFirst()
                .get();

        softly.assertThat(normalSubjectSummary.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);
        softly.assertThat(normalSubjectSummary.getSubject()).isEqualTo(NORMAL_SUBJECT);

        softly.assertThat(normalSubjectSummary.getSex()).isEqualTo("Female");
        softly.assertThat(normalSubjectSummary.getBaseline().getDate()).isInSameDayAs("2015-02-28");

        List<VitalsSummaryEvent> events = normalSubjectSummary.getEvents();

        softly.assertThat(events).extracting("maxValuePercentChange").containsOnly(14.29, 14.29, -11.36, 0.0);
        softly.assertThat(events).extracting("visitNumber").containsOnly(3., 1., 99., 2.);
        softly.assertThat(events).extracting("start.dayHour").containsOnly(7.5, -12.5, 14.5, 0.5);
        softly.assertThat(events).extracting("start.studyDayHourAsString").containsOnly("8d 12:00", "1d 12:00", "15d 12:00", "-12d 12:00");
    }

    @Test
    // Adopted from WhenRunningVitalsServiceQueriesITCase
    public void shouldListSummaryForNormalSubjectForStudyDate() {

        List<SubjectVitalsSummary> subjectVitalsSummaries = vitalsTimelineService.getVitalsSummaries(
                DUMMY_ACUITY_DATASETS,
                VitalFilters.empty(), PopulationFilters.empty(),
                DayZeroType.DAYS_SINCE_STUDY_DAY, null);

        softly.assertThat(subjectVitalsSummaries).hasSize(123);

        SubjectVitalsSummary normalSubjectSummary = subjectVitalsSummaries.stream()
                .filter(aesd -> aesd.getSubjectId().equals(NORMAL_SUBJECT_ID))
                .findFirst()
                .get();
        softly.assertThat(normalSubjectSummary.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);
        softly.assertThat(normalSubjectSummary.getSubject()).isEqualTo(NORMAL_SUBJECT);

        softly.assertThat(normalSubjectSummary.getSex()).isEqualTo("Female");
        softly.assertThat(normalSubjectSummary.getBaseline().getDate()).isInSameDayAs("2015-02-28");

        List<VitalsSummaryEvent> events = normalSubjectSummary.getEvents();

        softly.assertThat(events).extracting("maxValuePercentChange").containsOnly(0.0, 14.29, 14.29, -11.36);
        softly.assertThat(events).extracting("visitNumber").containsOnly(3., 1., 99., 2.);
        softly.assertThat(events).extracting("start.dayHour").containsOnly(-11.5, 8.5, 15.5, 1.5);
    }

    @Test
    public void shouldListSummaryForNormalSubjectForDrugTreatmentDate() {

        List<SubjectVitalsSummary> subjectVitalsSummaries = vitalsTimelineService.getVitalsSummaries(
                DUMMY_ACUITY_DATASETS,
                VitalFilters.empty(), PopulationFilters.empty(),
                DayZeroType.DAYS_SINCE_FIRST_TREATMENT, "AZD1234");

        softly.assertThat(subjectVitalsSummaries).hasSize(123);

        SubjectVitalsSummary normalSubjectSummary = subjectVitalsSummaries.stream()
                .filter(s -> s.getSubjectId().equals("3824bea4759042d3b9cbcb9fa45f6b28"))
                .findFirst()
                .get();

        softly.assertThat(normalSubjectSummary.getSubjectId()).isEqualTo("3824bea4759042d3b9cbcb9fa45f6b28");
        softly.assertThat(normalSubjectSummary.getSubject()).isEqualTo("E0000100130");

        List<VitalsSummaryEvent> events = normalSubjectSummary.getEvents();
        softly.assertThat(events).extracting("start.dayHour").contains(-27.5, 0.5, 11.5);
    }

    @Test
    // Adopted from WhenRunningVitalsServiceQueriesITCase
    public void shouldListSummaryForNormalSubjectForSubject() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(NORMAL_SUBJECT)));

        List<SubjectVitalsSummary> subjectVitalsSummaries = vitalsTimelineService.getVitalsSummaries(
                DUMMY_ACUITY_DATASETS, VitalFilters.empty(), populationFilters,
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        softly.assertThat(subjectVitalsSummaries).hasSize(1);

        SubjectVitalsSummary normalSubjectSummary = subjectVitalsSummaries.get(0);
        softly.assertThat(normalSubjectSummary.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);
        softly.assertThat(normalSubjectSummary.getSubject()).isEqualTo(NORMAL_SUBJECT);

        softly.assertThat(normalSubjectSummary.getSex()).isEqualTo("Female");
        softly.assertThat(normalSubjectSummary.getBaseline().getDate()).isInSameDayAs("2015-02-16");

        List<VitalsSummaryEvent> events = normalSubjectSummary.getEvents();

        softly.assertThat(events).extracting("maxValuePercentChange").containsOnly(14.29, 14.29, -11.36, 0.0);
        softly.assertThat(events).extracting("visitNumber").containsOnly(3., 1., 99., 2.);
        softly.assertThat(events).extracting("start.dayHour").containsOnly(7.5, -12.5, 14.5, 0.5);
    }
}
