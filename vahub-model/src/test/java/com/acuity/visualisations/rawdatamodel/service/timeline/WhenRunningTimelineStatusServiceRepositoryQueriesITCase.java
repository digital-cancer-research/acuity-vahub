package com.acuity.visualisations.rawdatamodel.service.timeline;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.statussummary.SubjectStatusSummary;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static org.assertj.core.api.Assertions.offset;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class WhenRunningTimelineStatusServiceRepositoryQueriesITCase {

    private static final String NORMAL_SUBJECT_ID = "9cf6f3e6d0a147fa98f6dfb003d4baee";
    private static final String NORMAL_SUBJECT = "E0000100116";
    private static final String DIED_SUBJECT_ID = "96e171ba9679408faf63a8d5ac8814bf";
    private static final String DIED_SUBJECT = "E000010016";
    private PopulationFilters emptyPopulationFilters = new PopulationFilters();
    private DateDayHour emptyDateDayHour = new DateDayHour();

    @Autowired
    private StatusSummaryTimelineService timelineStatusService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldListStatusSummaries() {
        List<SubjectStatusSummary> statusSummaries = timelineStatusService
                .getStatusSummaries(DUMMY_ACUITY_DATASETS, emptyPopulationFilters, DayZeroType.DAYS_SINCE_FIRST_DOSE, StringUtils.EMPTY);

        softly.assertThat(statusSummaries).hasSize(124);

        SubjectStatusSummary subject1 = statusSummaries.stream().filter(ss -> ss.getSubjectId().equals(NORMAL_SUBJECT_ID)).findFirst().get();
        SubjectStatusSummary subjectDied = statusSummaries.stream().filter(ss -> ss.getSubjectId().equals(DIED_SUBJECT_ID)).findFirst().get();

        softly.assertThat(subject1.getSubject()).isEqualTo(NORMAL_SUBJECT);
        softly.assertThat(subject1.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);
        softly.assertThat(subject1.getDeath()).isEqualTo(emptyDateDayHour);
        softly.assertThat(subject1.getOngoing()).isNotNull();
        softly.assertThat(subject1.getDrugs()).hasSize(1);

        softly.assertThat(subjectDied.getSubject()).isEqualTo(DIED_SUBJECT);
        softly.assertThat(subjectDied.getSubjectId()).isEqualTo(DIED_SUBJECT_ID);
        softly.assertThat(subjectDied.getDeath()).isNotNull();
        softly.assertThat(subjectDied.getDrugs()).hasSize(1);

        softly.assertThat(subjectDied.getPhases()).hasSize(1);
        System.out.println(subjectDied.getPhases().get(0).toString());
        softly.assertThat(subjectDied.getPhases().get(0).getStart().getDayHour()).isEqualTo(0.42708f, offset(0.01));
        softly.assertThat(subjectDied.getPhases().get(0).getStart().getStudyDayHourAsString()).isEqualTo("1d 10:15");
        softly.assertThat(subjectDied.getPhases().get(0).getEnd().getDayHour()).isEqualTo(45.00f, offset(0.01));
        softly.assertThat(subjectDied.getPhases().get(0).getEnd().getStudyDayHourAsString()).isEqualTo("45d 23:59");

    }

    @Test
    public void shouldListStatusSummariesForStudy() {
        List<SubjectStatusSummary> statusSummaries = timelineStatusService
                .getStatusSummaries(DUMMY_ACUITY_DATASETS, emptyPopulationFilters, DayZeroType.DAYS_SINCE_STUDY_DAY, StringUtils.EMPTY);

        softly.assertThat(statusSummaries).hasSize(124);

        SubjectStatusSummary subject1 = statusSummaries.stream().filter(ss -> ss.getSubjectId().equals(NORMAL_SUBJECT_ID)).findFirst().get();
        SubjectStatusSummary subjectDied = statusSummaries.stream().filter(ss -> ss.getSubjectId().equals(DIED_SUBJECT_ID)).findFirst().get();

        softly.assertThat(subject1.getSubject()).isEqualTo(NORMAL_SUBJECT);
        softly.assertThat(subject1.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);
        softly.assertThat(subject1.getDeath()).isEqualTo(emptyDateDayHour);
        softly.assertThat(subject1.getOngoing()).isNotNull();
        softly.assertThat(subject1.getDrugs()).hasSize(1);

        softly.assertThat(subjectDied.getSubject()).isEqualTo(DIED_SUBJECT);
        softly.assertThat(subjectDied.getSubjectId()).isEqualTo(DIED_SUBJECT_ID);
        softly.assertThat(subjectDied.getDeath()).isNotNull();
        softly.assertThat(subjectDied.getDrugs()).hasSize(1);
    }

}
