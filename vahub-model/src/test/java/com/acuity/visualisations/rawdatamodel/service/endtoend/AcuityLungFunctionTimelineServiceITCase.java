package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.timeline.LungFunctionTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.LungFunctionCodes;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.SubjectLungFunctionDetail;
import com.acuity.visualisations.rawdatamodel.vo.timeline.lungfunction.SubjectLungFunctionSummary;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_LUNG_FUNC_DATASETS;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityLungFunctionTimelineServiceITCase {

    @Autowired
    private LungFunctionTimelineService timelineLungFunctionService;

    private String NORMAL_SUBJECT_ID = "2d54dac971774007a1ec4c4583543dbc";
    private String NORMAL_SUBJECT = "E0000100102";

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldListLungFunctionSummary() {
        List<SubjectLungFunctionSummary> lungFunctionSummaries =
                timelineLungFunctionService.getLungFunctionSummaries(DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        LungFunctionFilters.empty(),
                        PopulationFilters.empty(),
                        DayZeroType.DAYS_SINCE_FIRST_DOSE,
                        null);

        SubjectLungFunctionSummary normalSubjectLungFunctionSummary = lungFunctionSummaries.stream().filter(labs -> labs.getSubjectId()
                .equals(NORMAL_SUBJECT_ID)).findFirst().get();


        softly.assertThat(normalSubjectLungFunctionSummary.getSubject()).isEqualTo(NORMAL_SUBJECT);

        softly.assertThat(normalSubjectLungFunctionSummary.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);

        softly.assertThat(normalSubjectLungFunctionSummary.getEvents()).extracting("maxValuePercentChange")
                .containsExactly(-30.36, 0.0, 80.0, 37.14, 0.0, -38.5, 22.86);
        softly.assertThat(normalSubjectLungFunctionSummary.getEvents()).extracting("visitNumber").
                containsExactlyInAnyOrder(1.0, 2.0, 3.0, 4.0, 5.0, 6.01, 7.0);
        softly.assertThat(normalSubjectLungFunctionSummary.getEvents()).extracting("start.dayHour").
                containsExactlyInAnyOrder(-61.5, -47.5, -33.5, -16.5, -5.5, 8.5, 22.5);
        softly.assertThat(normalSubjectLungFunctionSummary.getEvents()).extracting("start.studyDayHourAsString").
                containsExactlyInAnyOrder("-61d 12:00",
                        "-47d 12:00",
                        "-33d 12:00",
                        "-16d 12:00",
                        "-5d 12:00",
                        "9d 12:00",
                        "23d 12:00");

        lungFunctionSummaries.forEach(System.out::println);
    }

    @Test
    public void shouldListLungFunctionSummaryForStudyDate() {
        List<SubjectLungFunctionSummary> lungFunctionSummaries = timelineLungFunctionService
                .getLungFunctionSummaries(DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        LungFunctionFilters.empty(),
                        PopulationFilters.empty(),
                        DayZeroType.DAYS_SINCE_STUDY_DAY,
                        null);

        SubjectLungFunctionSummary normalSubjectLungFunctionSummary = lungFunctionSummaries.stream()
                .filter(labs -> labs.getSubjectId().equals(NORMAL_SUBJECT_ID)).findFirst().get();

        softly.assertThat(normalSubjectLungFunctionSummary.getSubject()).isEqualTo(NORMAL_SUBJECT);
        softly.assertThat(normalSubjectLungFunctionSummary.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);

        softly.assertThat(normalSubjectLungFunctionSummary.getEvents()).extracting("visitNumber").
                containsExactlyInAnyOrder(1.0, 2.0, 3.0, 4.0, 5.0, 6.01, 7.0);
        softly.assertThat(normalSubjectLungFunctionSummary.getEvents()).extracting("start.dayHour").
                containsExactlyInAnyOrder(-60.5, -46.5, -32.5, -15.5, -4.5, 9.5, 23.5);

        lungFunctionSummaries.forEach(System.out::println);
    }

    @Test
    public void shouldListLungFunctionSummaryBySubject() {
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(NORMAL_SUBJECT)));

        List<SubjectLungFunctionSummary> lungFunctionSummaries = timelineLungFunctionService
                .getLungFunctionSummaries(DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        LungFunctionFilters.empty(),
                        populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE,
                        null);

        SubjectLungFunctionSummary normalSubjectLungFunctionSummary = lungFunctionSummaries.stream()
                .filter(labs -> labs.getSubjectId().equals(NORMAL_SUBJECT_ID)).findFirst().get();

        softly.assertThat(normalSubjectLungFunctionSummary.getSubject()).isEqualTo(NORMAL_SUBJECT);
        softly.assertThat(normalSubjectLungFunctionSummary.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);

        softly.assertThat(normalSubjectLungFunctionSummary.getEvents()).extracting("visitNumber").
                containsExactlyInAnyOrder(1.0, 2.0, 3.0, 4.0, 5.0, 6.01, 7.0);
        softly.assertThat(normalSubjectLungFunctionSummary.getEvents()).extracting("start.dayHour").
                containsExactlyInAnyOrder(-61.5, -47.5, -33.5, -16.5, -5.5, 8.5, 22.5);
    }

    @Test
    public void shouldListLungFunctionDetails() {
        List<SubjectLungFunctionDetail> lungFunctionDetails = timelineLungFunctionService
                .getLungFunctionDetails(DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        LungFunctionFilters.empty(),
                        PopulationFilters.empty(),
                        DayZeroType.DAYS_SINCE_FIRST_DOSE,
                        null);

        SubjectLungFunctionDetail normalSubjectLungFunctionDetail = lungFunctionDetails.stream()
                .filter(labs -> labs.getSubjectId().equals(NORMAL_SUBJECT_ID)).findFirst().get();
        LungFunctionCodes fev1Code = normalSubjectLungFunctionDetail.getLungFunctionCodes().stream()
                .filter(code -> code.getCode().equals("FEV1 (L) Pre-Bronchodilator")).findFirst().get();
        LungFunctionCodes ppfvcCode = normalSubjectLungFunctionDetail.getLungFunctionCodes().stream()
                .filter(code -> code.getCode().equals("FVC (L) Pre-Bronchodilator")).findFirst().get();

        softly.assertThat(normalSubjectLungFunctionDetail.getSubject()).isEqualTo(NORMAL_SUBJECT);
        softly.assertThat(normalSubjectLungFunctionDetail.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);

        softly.assertThat(fev1Code.getCode()).isEqualTo("FEV1 (L) Pre-Bronchodilator");
        softly.assertThat(fev1Code.getEvents()).extracting("baselineValue").containsOnly(1.56, 1.56, 1.56);
        softly.assertThat(fev1Code.getEvents()).extracting("unitRaw").containsOnly("L");

        softly.assertThat(fev1Code.getEvents()).extracting("valueChangeFromBaseline").containsOnly(-0.17, 0.0, -0.51);
        softly.assertThat(fev1Code.getEvents()).extracting("unitChangeFromBaseline").containsOnly("L");

        softly.assertThat(fev1Code.getEvents()).extracting("visitNumber").containsOnly(1.0, 2.0, 6.01);
        softly.assertThat(fev1Code.getEvents()).extracting("start.dayHour").containsOnly(-61.5, -47.5, 8.5);


        softly.assertThat(ppfvcCode.getCode()).isEqualTo("FVC (L) Pre-Bronchodilator");
        softly.assertThat(ppfvcCode.getEvents()).extracting("valueRaw").containsOnly(2.83, 3.87, 2.38);
        softly.assertThat(ppfvcCode.getEvents()).extracting("baselineValue").containsOnly(3.87, 3.87, 3.87);
        softly.assertThat(ppfvcCode.getEvents()).extracting("unitRaw").containsOnly("L");

        softly.assertThat(ppfvcCode.getEvents()).extracting("valueChangeFromBaseline").containsOnly(-1.04, 0.0, -1.49);
        softly.assertThat(ppfvcCode.getEvents()).extracting("unitChangeFromBaseline").containsOnly("L");

        softly.assertThat(ppfvcCode.getEvents()).extracting("valuePercentChangeFromBaseline").containsOnly(-26.87, 0.0, -38.5);
        softly.assertThat(ppfvcCode.getEvents()).extracting("unitPercentChangeFromBaseline").containsOnly("%");

        softly.assertThat(ppfvcCode.getEvents()).extracting("visitNumber").containsOnly(1.0, 2.0, 6.01);
        softly.assertThat(ppfvcCode.getEvents()).extracting("start.dayHour").containsOnly(-61.5, -47.5, 8.5);

        lungFunctionDetails.forEach(System.out::println);
    }

    @Test
    public void shouldListLungFunctionDetailsForSubject() {
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(NORMAL_SUBJECT)));

        List<SubjectLungFunctionDetail> lungFunctionDetails = timelineLungFunctionService
                .getLungFunctionDetails(DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        LungFunctionFilters.empty(),
                        populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE,
                        null);

        SubjectLungFunctionDetail normalSubjectLungFunctionDetail = lungFunctionDetails.stream()
                .filter(labs -> labs.getSubjectId().equals(NORMAL_SUBJECT_ID)).findFirst().get();
        LungFunctionCodes fev1Code = normalSubjectLungFunctionDetail.getLungFunctionCodes().stream()
                .filter(code -> code.getCode().equals("FEV1 (L) Pre-Bronchodilator")).findFirst().get();

        softly.assertThat(normalSubjectLungFunctionDetail.getSubject()).isEqualTo(NORMAL_SUBJECT);
        softly.assertThat(normalSubjectLungFunctionDetail.getSubjectId()).isEqualTo(NORMAL_SUBJECT_ID);

        softly.assertThat(fev1Code.getCode()).isEqualTo("FEV1 (L) Pre-Bronchodilator");
        softly.assertThat(fev1Code.getEvents()).extracting("baselineValue").containsOnly(1.56, 1.56, 1.56);
        softly.assertThat(fev1Code.getEvents()).extracting("unitRaw").containsOnly("L");

        softly.assertThat(fev1Code.getEvents()).extracting("valueChangeFromBaseline").containsOnly(-0.17, 0.0, -0.51);
        softly.assertThat(fev1Code.getEvents()).extracting("unitChangeFromBaseline").containsOnly("L");

        softly.assertThat(fev1Code.getEvents()).extracting("valuePercentChangeFromBaseline").containsOnly(-10.9, 0.0, -32.69);
        softly.assertThat(fev1Code.getEvents()).extracting("unitPercentChangeFromBaseline").containsOnly("%");

        softly.assertThat(fev1Code.getEvents()).extracting("visitNumber").containsOnly(1.0, 2.0, 6.01);
        softly.assertThat(fev1Code.getEvents()).extracting("start.dayHour").containsOnly(-61.5, -47.5, 8.5);
    }

    @Test
    public void shouldGiveMeasurementNamesWithProtocolSchedule() {
        // Given
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(NORMAL_SUBJECT)));

        // When
        List<SubjectLungFunctionDetail> lungFunctionDetails = timelineLungFunctionService
                .getLungFunctionDetails(DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        LungFunctionFilters.empty(),
                        populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE,
                        null);

        // Then
        softly.assertThat(lungFunctionDetails.get(0).getLungFunctionCodes())
                .extracting("code")
                .containsOnly("FEV1 (%) Post-Bronchodilator",
                        "FVC (L) Pre-Bronchodilator",
                        "FEV1 (%) Pre-Bronchodilator",
                        "FEV1 (L) Post-Bronchodilator",
                        "FVC (L) Post-Bronchodilator",
                        "FEV1 (L) Pre-Bronchodilator",
                        "FVC (L) 2 hours post dose.",
                        "FEV1 (L) Post-Bronchodilator (Pre-dose)",
                        "FEV1 (%) Post-Bronchodilator (Pre-dose)",
                        "FEV1 (L) 2 hours post dose.",
                        "FEV1 (%) 2 hours post dose.",
                        "FVC (L) Post-Bronchodilator (Pre-dose)",
                        "FEV1 (L) Pre-Bronchodilator (Pre-dose)",
                        "FEV1 (%) Pre-Bronchodilator (Pre-dose)",
                        "FVC (L) Pre-Bronchodilator (Pre-dose)");
    }
}

