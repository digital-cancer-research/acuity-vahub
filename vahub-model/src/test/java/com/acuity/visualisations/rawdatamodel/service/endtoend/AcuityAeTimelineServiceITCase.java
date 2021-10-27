package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.timeline.AeTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.aes.AeDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.aes.AeDetailEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.aes.AeMaxCtcEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.aes.SubjectAesDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.aes.SubjectAesSummary;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDateTime;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityAeTimelineServiceITCase {
    private static final String NORMAL_SUBJECT = "E0000100177";

    @Autowired
    private AeTimelineService aeTimelineService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldListAesDetail() {
        List<SubjectAesDetail> aesDetails = aeTimelineService.getAesDetails(DUMMY_ACUITY_DATASETS,
                AeFilters.empty(), PopulationFilters.empty(), DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectAesDetail normalSubjectDetail = aesDetails.stream().filter(aesd -> aesd.getSubject().equals(NORMAL_SUBJECT)).findFirst().get();
        softly.assertThat(normalSubjectDetail.getSubject()).isEqualTo(NORMAL_SUBJECT);
        assertThat1130552881(normalSubjectDetail);
    }

    @Test
    public void shouldListAesDetailForStudyDate() {
        List<SubjectAesDetail> aesDetails = aeTimelineService.getAesDetails(DUMMY_ACUITY_DATASETS,
                AeFilters.empty(), PopulationFilters.empty(), DayZeroType.DAYS_SINCE_STUDY_DAY, null);

        SubjectAesDetail ongoingSubjectDetail = aesDetails.stream().filter(aesd -> aesd.getSubject().equals(NORMAL_SUBJECT)).findFirst().get();
        softly.assertThat(ongoingSubjectDetail.getSubject()).isEqualTo(NORMAL_SUBJECT);

        softly.assertThat(ongoingSubjectDetail.getAes()).hasSize(4);

        AeDetail arthragliaAe = ongoingSubjectDetail.getAes().stream().filter(aes -> aes.getPt().equals("LYMPHOEDEMA")).findFirst().get();

        softly.assertThat(arthragliaAe.getPt()).isEqualTo("LYMPHOEDEMA");
        softly.assertThat(arthragliaAe.getSoc()).isEqualTo("VASC");

        softly.assertThat(arthragliaAe.getEvents()).hasSize(1);

        AeDetailEvent aeEvent = arthragliaAe.getEvents().get(0);
        softly.assertThat(aeEvent.getPt()).isEqualTo("LYMPHOEDEMA");
        softly.assertThat(aeEvent.getSeverityGrade()).isEqualTo("CTC Grade 2");
        softly.assertThat(aeEvent.getSeverityGradeNum()).isEqualTo(2);
        softly.assertThat(aeEvent.getStart().getDayHour()).isEqualTo(192.00001);
        softly.assertThat(aeEvent.getStart().getStudyDayHourAsString()).isEqualTo("192d 00:00");
        softly.assertThat(aeEvent.getStart().getDate()).isInSameDayAs("2015-08-26");
        softly.assertThat(aeEvent.getEnd().getDayHour()).isEqualTo(346.99999);
        softly.assertThat(aeEvent.getEnd().getStudyDayHourAsString()).isEqualTo("346d 23:59");

        softly.assertThat(ZonedDateTime.ofInstant(aeEvent.getEnd().getDate().toInstant(), ZoneId.of("GMT")))
                .isEqualToIgnoringHours(ZonedDateTime.ofInstant(toDateTime("27.01.2016 02:59").toInstant(), ZoneId.of("GMT")));

        softly.assertThat(aeEvent.getDuration()).isEqualTo(155);
        softly.assertThat(aeEvent.isOngoing()).isFalse();
        softly.assertThat(aeEvent.isImputedEndDate()).isFalse();
        softly.assertThat(aeEvent.getSerious()).isEqualTo("No");
        softly.assertThat(aeEvent.getCausality()).isEqualTo("AZD1234: No, Additional_drug: Yes");
        softly.assertThat(aeEvent.getActionTaken()).isEqualTo("AZD1234: None, AZD1234: Dose Not Changed, Additional_drug: None, Additional_drug: Dose Not Changed");
    }

    @Test
    public void shouldListAesDetailForSubject() {
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(NORMAL_SUBJECT)));

        List<SubjectAesDetail> aesDetails = aeTimelineService
                .getAesDetails(DUMMY_ACUITY_DATASETS, AeFilters.empty(), populationFilters, DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        softly.assertThat(aesDetails).hasSize(1);

        SubjectAesDetail ongoingSubjectDetail = aesDetails.get(0);

        softly.assertThat(ongoingSubjectDetail.getSubject()).isEqualTo(NORMAL_SUBJECT);
        assertThat1130552881(ongoingSubjectDetail);
    }

    @Test
    public void shouldListAesSummariesForSubjectFromPrototype() {
        List<SubjectAesSummary> aesSummaries = aeTimelineService.getAesSummaries(DUMMY_ACUITY_DATASETS,
                AeFilters.empty(), PopulationFilters.empty(), DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectAesSummary subjectSummary = aesSummaries.stream().filter(aesd -> aesd.getSubject().equals(NORMAL_SUBJECT)).findFirst().get();

        assertThat1200302334(subjectSummary);
    }

    @Test
    public void shouldListAesSummariesFromPrototype() {
        List<SubjectAesSummary> aesSummaries = aeTimelineService.getAesSummaries(DUMMY_ACUITY_DATASETS,
                AeFilters.empty(), PopulationFilters.empty(), DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectAesSummary subjectSummary = aesSummaries.stream().filter(aesd -> aesd.getSubject().equals(NORMAL_SUBJECT)).findFirst().get();

        assertThat1200302334(subjectSummary);
    }

    private void assertThat1200302334(SubjectAesSummary subjectAesSummary) {
        assertThat(subjectAesSummary.getEvents()).hasSize(5);

        AeMaxCtcEvent firstMaxCtcEvent = subjectAesSummary.getEvents().get(0);

        softly.assertThat(firstMaxCtcEvent.getMaxSeverityGrade()).isEqualTo("CTC Grade 1");
        softly.assertThat(firstMaxCtcEvent.getMaxSeverityGradeNum()).isEqualTo(1);
        softly.assertThat(firstMaxCtcEvent.getStart().getDayHour()).isEqualTo(21.00001);
        softly.assertThat(firstMaxCtcEvent.getStart().getDate()).isInSameDayAs("2015-03-09");
        softly.assertThat(firstMaxCtcEvent.getEnd().getDayHour()).isEqualTo(31.99999);

        softly.assertThat(ZonedDateTime.ofInstant(firstMaxCtcEvent.getEnd().getDate().toInstant(), ZoneId.of("GMT")))
                .isEqualToIgnoringHours(ZonedDateTime.ofInstant(toDate("19.03.2015").toInstant(), ZoneId.of("GMT")));

        softly.assertThat(firstMaxCtcEvent.getDuration()).isEqualTo(11);
        softly.assertThat(firstMaxCtcEvent.isOngoing()).isFalse();
        softly.assertThat(firstMaxCtcEvent.isImputedEndDate()).isFalse();
        softly.assertThat(firstMaxCtcEvent.getNumberOfEvents()).isEqualTo(1);

        softly.assertThat(subjectAesSummary.getEvents()).extracting("maxSeverityGradeNum").containsExactly(1, 2, 2, 2, 2);
        softly.assertThat(subjectAesSummary.getEvents()).extracting("numberOfEvents").containsExactly(1, 1, 2, 3, 2);
        softly.assertThat(subjectAesSummary.getEvents().get(0).getPts()).containsExactly("PRURITUS");
        softly.assertThat(subjectAesSummary.getEvents().get(1).getPts()).containsOnly("NAIL DISORDER");
        softly.assertThat(subjectAesSummary.getEvents()).extracting("start.dayHour").containsExactly(21.00001, 70.00001, 191.00001, 284.00001, 303.99999);
        softly.assertThat(subjectAesSummary.getEvents()).extracting("start.date").doesNotContainNull();
        softly.assertThat(subjectAesSummary.getEvents()).extracting("end.dayHour").containsExactly(31.99999, 191.00001, 284.00001, 303.99999, 345.99999);
        softly.assertThat(subjectAesSummary.getEvents()).extracting("end.date").doesNotContainNull();
        softly.assertThat(subjectAesSummary.getEvents()).extracting("ongoing").containsOnly(false);
    }

    private void assertThat1130552881(SubjectAesDetail subjectAesDetail) {
        softly.assertThat(subjectAesDetail.getAes()).hasSize(4);

        AeDetail asthmaAe = subjectAesDetail.getAes().stream().filter(aes -> aes.getPt().equals("LYMPHOEDEMA")).findFirst().get();

        softly.assertThat(asthmaAe.getPt()).isEqualTo("LYMPHOEDEMA");
        softly.assertThat(asthmaAe.getHlt()).isEqualTo("LYMPHOEDEMAS");

        softly.assertThat(asthmaAe.getEvents()).hasSize(1);

        AeDetailEvent aeEvent = asthmaAe.getEvents().get(0);
        softly.assertThat(aeEvent.getPt()).isEqualTo("LYMPHOEDEMA");
        softly.assertThat(aeEvent.getSeverityGrade()).isEqualTo("CTC Grade 2");
        softly.assertThat(aeEvent.getSeverityGradeNum()).isEqualTo(2);
        softly.assertThat(aeEvent.getStart().getDayHour()).isEqualTo(191.00001);
        softly.assertThat(aeEvent.getStart().getStudyDayHourAsString()).isEqualTo("192d 00:00");
        softly.assertThat(aeEvent.getStart().getDate()).isInSameDayAs("2015-08-26");
        softly.assertThat(aeEvent.getEnd().getDayHour()).isEqualTo(345.99999);
        softly.assertThat(aeEvent.getEnd().getStudyDayHourAsString()).isEqualTo("346d 23:59");

        softly.assertThat(ZonedDateTime.ofInstant(aeEvent.getEnd().getDate().toInstant(), ZoneId.of("GMT")))
                .isEqualToIgnoringHours(ZonedDateTime.ofInstant(toDateTime("27.01.2016 02:59").toInstant(), ZoneId.of("GMT")));

        softly.assertThat(aeEvent.getDuration()).isEqualTo(155);
        softly.assertThat(aeEvent.isOngoing()).isFalse();
        softly.assertThat(aeEvent.isImputedEndDate()).isFalse();
        softly.assertThat(aeEvent.getSerious()).isEqualTo("No");
        softly.assertThat(aeEvent.getCausality()).isEqualTo("AZD1234: No, Additional_drug: Yes");
        softly.assertThat(aeEvent.getActionTaken()).isEqualTo("AZD1234: None, AZD1234: Dose Not Changed, Additional_drug: None, Additional_drug: Dose Not Changed");
    }
}
