package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.event.ExacerbationService;
import com.acuity.visualisations.rawdatamodel.service.timeline.ExacerbationsTimelineService;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.LabTests;
import com.acuity.visualisations.rawdatamodel.vo.timeline.exacerbations.ExacerbationSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.exacerbations.SubjectExacerbationSummary;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_DATASETS;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
@Category(LabTests.class)
public class AcuityExacerbationTimelineServiceITCase {

    @Autowired
    private ExacerbationsTimelineService exacerbationsTimelineService;

    @Autowired
    private ExacerbationService exacerbationService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private String ONGOING_SUBJECT_ID = "2e7377157d0b419ca58afd098946de57";
    private String ONGOING_SUBJECT = "E0000100105";
    private String SUBJECT_WITH_ONGOING_EVENT = "E0000100159";

    @Test
    public void shouldListExacerbationSummaryForOngoingSubject() {

        List<SubjectExacerbationSummary> subjectExacerbationSummarys = exacerbationsTimelineService.getExacerbationsSummary(
                DUMMY_2_ACUITY_DATASETS, DayZeroType.DAYS_SINCE_FIRST_DOSE,
                null, PopulationFilters.empty(), ExacerbationFilters.empty());

        SubjectExacerbationSummary ongoingSubjectSummary = subjectExacerbationSummarys.stream()
                .filter(aesd -> aesd.getSubjectId().equals(ONGOING_SUBJECT_ID)).findFirst().get();
        softly.assertThat(ongoingSubjectSummary.getSubject()).isEqualTo(ONGOING_SUBJECT);
        softly.assertThat(ongoingSubjectSummary.getSubjectId()).isEqualTo(ONGOING_SUBJECT_ID);

        List<ExacerbationSummaryEvent> events = ongoingSubjectSummary.getEvents();

        softly.assertThat(events).extracting("start.dayHour").containsSequence(61.00001, 92.00001);
        softly.assertThat(events).extracting("end.dayHour").containsSequence(1681.79466, 132.99999);
        softly.assertThat(events).extracting("start.studyDayHourAsString").containsSequence("62d 00:00", "93d 00:00");
        softly.assertThat(events).extracting("end.studyDayHourAsString").containsSequence("1682d 19:04", "133d 23:59");
        softly.assertThat(events).extracting("ongoing").containsOnly(false);
        softly.assertThat(events).extracting("severityGrade").containsOnly("(B) Moderate", "(C) Severe");
    }

    @Test
    public void shouldListExacerbationSummaryForOngoingSubjectForStudyDate() {

        List<SubjectExacerbationSummary> subjectExacerbationSummarys = exacerbationsTimelineService
                .getExacerbationsSummary(DUMMY_2_ACUITY_DATASETS, DayZeroType.DAYS_SINCE_STUDY_DAY,
                        null, PopulationFilters.empty(), ExacerbationFilters.empty());

        SubjectExacerbationSummary ongoingSubjectSummary = subjectExacerbationSummarys.stream()
                .filter(aesd -> aesd.getSubjectId().equals(ONGOING_SUBJECT_ID)).findFirst().get();
        softly.assertThat(ongoingSubjectSummary.getSubjectId()).isEqualTo(ONGOING_SUBJECT_ID);
        softly.assertThat(ongoingSubjectSummary.getSubject()).isEqualTo(ONGOING_SUBJECT);

        List<ExacerbationSummaryEvent> events = ongoingSubjectSummary.getEvents();

        softly.assertThat(events).extracting("start.dayHour").containsSequence(62.00001, 93.00001);
        softly.assertThat(events).extracting("end.dayHour").containsSequence(1682.79466, 133.99999);
        softly.assertThat(events).extracting("ongoing").containsOnly(false);
        softly.assertThat(events).extracting("severityGrade").containsOnly("(B) Moderate", "(C) Severe");
    }

    @Test
    public void shouldListExacerbationSummaryForOngoingSubjectWithOngoingEvent() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(SUBJECT_WITH_ONGOING_EVENT)));
        List<SubjectExacerbationSummary> subjectExacerbationSummarys = exacerbationsTimelineService
                .getExacerbationsSummary(DUMMY_2_ACUITY_DATASETS, DayZeroType.DAYS_SINCE_STUDY_DAY,
                        null, populationFilters, ExacerbationFilters.empty());

        SubjectExacerbationSummary ongoingSubjectSummary = subjectExacerbationSummarys.stream()
                .filter(aesd -> aesd.getSubject().equals(SUBJECT_WITH_ONGOING_EVENT)).findFirst().get();
        softly.assertThat(ongoingSubjectSummary.getSubject()).isEqualTo(SUBJECT_WITH_ONGOING_EVENT);

        List<ExacerbationSummaryEvent> events = ongoingSubjectSummary.getEvents();

        softly.assertThat(events).extracting("start.dayHour").containsSequence(-45.99999, 9.00001);
        softly.assertThat(events.get(1)).extracting("end.dayHour").allMatch(v -> ((Number) v).doubleValue() > 1500);
        softly.assertThat(events).extracting("ongoing").containsOnly(false, true);
        softly.assertThat(events).extracting("severityGrade").containsOnly("(A) Mild", "(C) Severe");
    }

    @Test
    public void shouldListExacerbationSummaryForOngoingSubjectbySubject() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(ONGOING_SUBJECT)));
        List<SubjectExacerbationSummary> subjectExacerbationSummarys = exacerbationsTimelineService.getExacerbationsSummary(
                DUMMY_2_ACUITY_DATASETS, DayZeroType.DAYS_SINCE_FIRST_DOSE, null,
                populationFilters, ExacerbationFilters.empty());

        SubjectExacerbationSummary ongoingSubjectSummary = subjectExacerbationSummarys.stream()
                .filter(aesd -> aesd.getSubjectId().equals(ONGOING_SUBJECT_ID)).findFirst().get();
        softly.assertThat(ongoingSubjectSummary.getSubjectId()).isEqualTo(ONGOING_SUBJECT_ID);
        softly.assertThat(ongoingSubjectSummary.getSubject()).isEqualTo(ONGOING_SUBJECT);

        List<ExacerbationSummaryEvent> events = ongoingSubjectSummary.getEvents();

        softly.assertThat(events).extracting("start.dayHour").containsSequence(61.00001, 92.00001);
        softly.assertThat(events).extracting("end.dayHour").containsSequence(1681.79466, 132.99999);
        softly.assertThat(events).extracting("ongoing").containsOnly(false);
        softly.assertThat(events).extracting("severityGrade").containsOnly("(B) Moderate", "(C) Severe");
    }

    @Test
    public void shouldGetSubjectsWithExacerbationData() throws Exception {

        List<String> subjects = exacerbationService.getSubjects(DUMMY_2_ACUITY_DATASETS, ExacerbationFilters.empty(), PopulationFilters.empty());

        softly.assertThat(subjects).hasSize(29);
    }

}
