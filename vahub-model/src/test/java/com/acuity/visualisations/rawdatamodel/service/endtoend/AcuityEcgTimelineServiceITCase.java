package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.CardiacFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.timeline.EcgTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.day.hour.DateDayHour;
import com.acuity.visualisations.rawdatamodel.vo.timeline.ecg.EcgSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.ecg.EcgTest;
import com.acuity.visualisations.rawdatamodel.vo.timeline.ecg.SubjectEcgDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.ecg.SubjectEcgSummary;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityEcgTimelineServiceITCase {
    private String NORMAL_SUBJECT_ID = "2e028dbfd09b4c65afd3b7af4d6d7c53";
    private String NORMAL_SUBJECT = "E0000100165";

    private String NORMAL_SUBJECT_WITH_DIFFERENT_BASELINE_DATES = "E0000100107";
    private String NORMAL_SUBJECT_WITH_DIFFERENT_BASELINE_DATES_ID = "d19e5667e5da4e6098484fe6e81c8901";

    @Autowired
    private EcgTimelineService ecgTimelineService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldListEcgSummaryForNormalSubject() {
        List<SubjectEcgSummary> subjectEcgSummaries = ecgTimelineService.getSummaries(DUMMY_ACUITY_DATASETS,
                CardiacFilters.empty(), PopulationFilters.empty(), DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        softly.assertThat(subjectEcgSummaries).hasSize(122);

        SubjectEcgSummary normalSubjectSummary = subjectEcgSummaries
                .stream()
                .filter(aesd -> aesd.getSubjectId().equals(NORMAL_SUBJECT_ID))
                .findFirst()
                .orElse(null);
        softly.assertThat(normalSubjectSummary).isNotNull();
        softly.assertThat(normalSubjectSummary.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);
        softly.assertThat(normalSubjectSummary.getSubject()).isEqualTo(NORMAL_SUBJECT);

        softly.assertThat(normalSubjectSummary.getSex()).isEqualTo("Female");
        softly.assertThat(normalSubjectSummary.getBaseline().getDayHour()).isEqualTo(-0.5);
        softly.assertThat(normalSubjectSummary.getBaseline().getDate()).isInSameDayAs("2015-03-23");

        List<EcgSummaryEvent> events = normalSubjectSummary.getEvents();

        softly.assertThat(events).extracting("maxValuePercentChange").containsOnly(7.95, 14.77, 14.39, 13.64, 16.41, 5.68,
                10.1, 9.34, 20.71, 12.88, 11.36, 0.0);
        softly.assertThat(events).extracting("qtcfValue").containsOnly(432.0, 423.0, 418.0, 421.0, 433.0, 445.0, 420.0,
                425.0, 429.0, 425.0, 441.0, 411.0);
        softly.assertThat(events).extracting("qtcfUnit").containsOnly("ms");
        softly.assertThat(events).extracting("qtcfChange").containsOnly(0.0,
                -9.0, -14.0, -11.0, 13.0, 1.0, 0.0, 9.0, -21.0, -7.0, -3.0, -12.0);
        softly.assertThat(events).extracting("visitNumber").containsOnly(5., 8., 47., 1., 41., 29., 11., 23., 53., 99., 14., 301., 17., 35.);
        softly.assertThat(events).extracting("abnormality").containsOnly("No");
        softly.assertThat(events).extracting("significant").containsNull();
        softly.assertThat(events).extracting("start.dayHour").containsOnly(419.5,
                140.5, 28.5, 56.5, 308.5, -0.5, 252.5, 84.5, 474.5, 561.5, 364.5, 196.5, 112.5, 531.5);
        softly.assertThat(events).extracting("start.studyDayHourAsString").containsOnly("309d 12:00", "365d 12:00",
                "420d 12:00", "475d 12:00", "29d 12:00", "85d 12:00", "253d 12:00", "57d 12:00", "113d 12:00",
                "532d 12:00", "562d 12:00", "197d 12:00", "-0d 12:00", "141d 12:00");
    }

    @Test
    public void shouldListEcgSummaryForNormalSubjectForStudyDate() {
        List<SubjectEcgSummary> subjectEcgSummaries = ecgTimelineService.getSummaries(DUMMY_ACUITY_DATASETS,
                CardiacFilters.empty(), PopulationFilters.empty(), DayZeroType.DAYS_SINCE_STUDY_DAY, null);

        softly.assertThat(subjectEcgSummaries).hasSize(122);

        SubjectEcgSummary normalSubjectSummary = subjectEcgSummaries
                .stream()
                .filter(aesd -> aesd.getSubjectId().equals(NORMAL_SUBJECT_ID))
                .findFirst()
                .orElse(null);
        softly.assertThat(normalSubjectSummary).isNotNull();
        softly.assertThat(normalSubjectSummary.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);
        softly.assertThat(normalSubjectSummary.getSubject()).isEqualTo(NORMAL_SUBJECT);

        softly.assertThat(normalSubjectSummary.getSex()).isEqualTo("Female");
        softly.assertThat(normalSubjectSummary.getBaseline().getDayHour()).isEqualTo(0.5);
        softly.assertThat(normalSubjectSummary.getBaseline().getDate()).isInSameDayAs("2015-03-23");

        List<EcgSummaryEvent> events = normalSubjectSummary.getEvents();

        softly.assertThat(events).extracting("maxValuePercentChange").containsOnly(7.95, 14.77, 14.39, 13.64, 16.41, 5.68,
                10.1, 9.34, 20.71, 12.88, 11.36, 0.0);
        softly.assertThat(events).extracting("qtcfValue").containsOnly(432.0, 421.0, 445.0, 423.0, 418.0, 441.0, 425.0, 420.0,
                429.0, 411.0, 433.0);
        softly.assertThat(events).extracting("qtcfUnit").containsOnly("ms");
        softly.assertThat(events).extracting("qtcfChange").containsOnly(-12.0, 0.0, -11.0, 13.0, -9.0, -14.0, 9.0, -7.0, -3.0, -21.0, 1.0);
        softly.assertThat(events).extracting("visitNumber").containsOnly(301., 99., 5., 29., 41., 53., 11., 1., 47., 14., 8., 17., 35., 23.);
        softly.assertThat(events).extracting("abnormality").containsOnly("No");
        softly.assertThat(events).extracting("significant").containsNull();
        softly.assertThat(events).extracting("start.dayHour").containsOnly(0.5,
                475.5, 85.5, 420.5, 562.5, 29.5, 253.5, 365.5, 532.5, 113.5, 57.5, 141.5, 309.5, 197.5);
    }

    @Test
    public void shouldListEcgSummaryForNormalSubjectForSubject() {
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(NORMAL_SUBJECT)));

        List<SubjectEcgSummary> subjectEcgSummaries = ecgTimelineService.getSummaries(DUMMY_ACUITY_DATASETS,
                CardiacFilters.empty(), populationFilters, DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        softly.assertThat(subjectEcgSummaries).hasSize(1);

        SubjectEcgSummary normalSubjectSummary = subjectEcgSummaries
                .stream()
                .filter(aesd -> aesd.getSubjectId().equals(NORMAL_SUBJECT_ID))
                .findFirst()
                .orElse(null);
        softly.assertThat(normalSubjectSummary).isNotNull();
        softly.assertThat(normalSubjectSummary.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);
        softly.assertThat(normalSubjectSummary.getSubject()).isEqualTo(NORMAL_SUBJECT);

        softly.assertThat(normalSubjectSummary.getSex()).isEqualTo("Female");
        softly.assertThat(normalSubjectSummary.getBaseline().getDayHour()).isEqualTo(-0.5);
        softly.assertThat(normalSubjectSummary.getBaseline().getDate()).isInSameDayAs("2015-03-23");

        List<EcgSummaryEvent> events = normalSubjectSummary.getEvents();

        softly.assertThat(events).extracting("maxValuePercentChange").containsOnly(0.0, 7.95, 14.77, 14.39, 13.64, 16.41, 5.68,
                10.1, 9.34, 20.71, 12.88, 11.36);
        softly.assertThat(events).extracting("qtcfValue").containsOnly(432.0, 445.0, 421.0, 423.0, 425.0, 441.0, 418.0, 429.0,
                420.0, 411.0, 433.0);
        softly.assertThat(events).extracting("qtcfChange").containsOnly(-7.0, 9.0, -14.0, 0.0, -3.0, -12.0, 1.0, 13.0, -11.0,
                -9.0, -21.0);
        softly.assertThat(events).extracting("visitNumber").containsOnly(11., 53., 47., 5., 99., 29., 41., 1., 301., 23., 17., 35., 8., 14.);
        softly.assertThat(events).extracting("abnormality").containsOnly("No");
        softly.assertThat(events).extracting("significant").containsNull();
        softly.assertThat(events).extracting("start.dayHour").containsOnly(531.5, -0.5, 28.5, 561.5, 252.5, 364.5, 56.5,
                112.5, 140.5, 308.5, 196.5, 84.5, 474.5, 419.5);
    }

    @Test
    public void shouldListEcgDetailForNormalSubject() {
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(NORMAL_SUBJECT)));

        List<SubjectEcgDetail> subjectEcgDetails = ecgTimelineService.getDetails(DUMMY_ACUITY_DATASETS,
                CardiacFilters.empty(), populationFilters, DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectEcgDetail normalSubjectDetail = subjectEcgDetails
                .stream()
                .filter(aesd -> aesd.getSubjectId().equals(NORMAL_SUBJECT_ID))
                .findFirst()
                .orElse(null);
        softly.assertThat(normalSubjectDetail).isNotNull();
        softly.assertThat(normalSubjectDetail.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);
        softly.assertThat(normalSubjectDetail.getSubject()).isEqualTo(NORMAL_SUBJECT);
        softly.assertThat(normalSubjectDetail.getTests())
                .extracting(EcgTest::getBaseline)
                .extracting(DateDayHour::getDayHour)
                .containsOnly(-0.5);
        normalSubjectDetail.getTests().stream()
                .map(EcgTest::getBaseline)
                .map(DateDayHour::getDate)
                .forEach(date -> softly.assertThat(date).isInSameDayAs("2015-03-23"));

        EcgTest qtcf = normalSubjectDetail.getTests().stream()
                .filter(test -> test.getTestName().equals("QTcF - Fridericia's Correction Formula"))
                .findFirst()
                .orElse(null);

        softly.assertThat(qtcf).isNotNull();
        softly.assertThat(qtcf.getEvents()).extracting("start.dayHour").containsOnly(112.5,
                308.5, 364.5, 196.5, -0.5, 531.5, 84.5, 56.5, 561.5, 140.5, 252.5, 28.5, 419.5, 474.5);
        softly.assertThat(qtcf.getEvents()).extracting("start.doseDayHour").containsOnly(112.5,
                308.5, 364.5, 196.5, -0.5, 531.5, 84.5, 56.5, 561.5, 140.5, 252.5, 28.5, 419.5, 474.5);
        softly.assertThat(qtcf.getEvents()).extracting("visitNumber").containsOnly(8., 53., 5., 29., 17., 99., 41., 23., 35., 14., 11.,
                47., 301., 1.);
        softly.assertThat(qtcf.getEvents()).extracting("abnormality").containsOnly("No");
        softly.assertThat(qtcf.getEvents()).extracting("significant").containsNull();

        softly.assertThat(qtcf.getEvents()).extracting("baselineFlag").containsOnlyOnce(true);

        softly.assertThat(qtcf.getEvents()).extracting("baselineValue").containsOnly(432.);
        softly.assertThat(qtcf.getEvents()).extracting("valueRaw").containsOnly(429.0, 433.0, 420.0, 441.0, 425.0, 445.0,
                432.0, 423.0, 418.0, 421.0, 411.0);
        softly.assertThat(qtcf.getEvents()).extracting("unitRaw").containsOnly("ms");
        softly.assertThat(qtcf.getEvents()).extracting("valueChangeFromBaseline").containsOnly(13.0, -14.0, 1.0, -21.0, -7.0,
                -3.0, -12.0, 9.0, -9.0, 0.0, -11.0);
        softly.assertThat(qtcf.getEvents()).extracting("unitChangeFromBaseline").containsOnly("ms");
        softly.assertThat(qtcf.getEvents()).extracting("valuePercentChangeFromBaseline").containsOnly(3.01, 0.0, -2.78, -1.62,
                0.23, -3.24, -4.86, -2.55, -2.08, 2.08, -0.69);
        softly.assertThat(qtcf.getEvents()).extracting("unitPercentChangeFromBaseline").containsOnly("%");
    }

    @Test
    public void shouldListEcgDetailWithDifferentBaselineDatesForNormalSubject() {
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(NORMAL_SUBJECT_WITH_DIFFERENT_BASELINE_DATES)));

        List<SubjectEcgDetail> subjectEcgDetails = ecgTimelineService.getDetails(DUMMY_ACUITY_DATASETS,
                CardiacFilters.empty(), populationFilters, DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        assertThat(subjectEcgDetails).hasSize(1);
        SubjectEcgDetail normalSubjectDetail = subjectEcgDetails.get(0);
        softly.assertThat(normalSubjectDetail.getSubjectId()).isEqualTo(NORMAL_SUBJECT_WITH_DIFFERENT_BASELINE_DATES_ID);
        softly.assertThat(normalSubjectDetail.getSubject()).isEqualTo(NORMAL_SUBJECT_WITH_DIFFERENT_BASELINE_DATES);

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        softly.assertThat(normalSubjectDetail.getTests().stream()
                .map(EcgTest::getBaseline)
                .map(DateDayHour::getDate)
                .map(format::format)
                .distinct())
                .containsExactlyInAnyOrder("2014-12-21 12:00:00", "2015-08-04 12:00:00");
    }

    @Test
    public void shouldListEcgSummaryWithBaselineClosestToTheFirstDoseDateNormalSubject() {
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(NORMAL_SUBJECT_WITH_DIFFERENT_BASELINE_DATES)));

        List<SubjectEcgSummary> subjectEcgDetails = ecgTimelineService.getSummaries(DUMMY_ACUITY_DATASETS,
                CardiacFilters.empty(), populationFilters, DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        assertThat(subjectEcgDetails).hasSize(1);
        SubjectEcgSummary normalSubjectSummary = subjectEcgDetails.get(0);
        softly.assertThat(normalSubjectSummary.getSubjectId()).isEqualTo(NORMAL_SUBJECT_WITH_DIFFERENT_BASELINE_DATES_ID);
        softly.assertThat(normalSubjectSummary.getSubject()).isEqualTo(NORMAL_SUBJECT_WITH_DIFFERENT_BASELINE_DATES);

        softly.assertThat(normalSubjectSummary.getBaseline().getDate()).isInSameDayAs("2014-12-21");
    }
}
