package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.timeline.ConmedsTimelineService;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.ConmedEventsByClass;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.ConmedEventsByDrug;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.ConmedSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.SubjectConmedByClass;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.SubjectConmedByDrug;
import com.acuity.visualisations.rawdatamodel.vo.timeline.conmeds.SubjectConmedSummary;
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
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.emptyList;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityConmedsTimelineServiceITCase {

    private static final String ONGOING_SUBJECT_ID = "c7cbf94ff3b54e7cbad2becfe40a7368";
    private static final String ONGOING_SUBJECT = "E000010075";
    private static final String DIED_SUBJECT_ID = "5d5170d4583944f2bb48b2934d14bd06";
    private static final String DIED_SUBJECT = "E000010093";

    @Autowired
    private ConmedsTimelineService conmedsTimelineService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    // Adopted from WhenRunningTimelineConmedsServiceITCase
    public void shouldListSubjectsWithConmeds() {
        ConmedFilters conmedsFilters = ConmedFilters.empty();
        conmedsFilters.setMedicationName(new SetFilter<>(newArrayList("RANITIDINE")));
        List<String> subjectIds = conmedsTimelineService.getSubjects(DUMMY_ACUITY_DATASETS, conmedsFilters, PopulationFilters.empty());
        softly.assertThat(subjectIds).hasSize(78);
    }

    @Test
    // Adopted from WhenRunningTimelineConmedsServiceITCase
    public void shouldListConmedsSummaryForDiedSubject() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(DIED_SUBJECT)));

        List<SubjectConmedSummary> subjectConmedSummarys = conmedsTimelineService
                .getConmedsSummaries(DUMMY_ACUITY_DATASETS,
                        ConmedFilters.empty(), populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectConmedSummary diedSubjectSummary = subjectConmedSummarys.stream()
                .filter(aesd -> aesd.getSubjectId().equals(DIED_SUBJECT_ID)).findFirst().get();
        softly.assertThat(diedSubjectSummary.getSubjectId()).isEqualTo(DIED_SUBJECT_ID);
        softly.assertThat(diedSubjectSummary.getSubject()).isEqualTo(DIED_SUBJECT);

        List<ConmedSummaryEvent> events = diedSubjectSummary.getEvents();

        softly.assertThat(events).extracting("start.dayHour")
                .containsExactly(-1184.99999,
                        -1108.99999,
                        -742.99999,
                        -377.99999,
                        1.0E-5,
                        15.00001,
                        35.00001,
                        42.00001,
                        42.99999,
                        114.00001,
                        127.99999,
                        142.00001,
                        268.00001,
                        268.99999,
                        402.00001,
                        402.99999,
                        534.00001);
        softly.assertThat(events).extracting("end.dayHour")
                .containsExactly(-1108.99999,
                        -742.99999,
                        -377.99999,
                        1.0E-5,
                        15.00001,
                        35.00001,
                        42.00001,
                        42.99999,
                        114.00001,
                        127.99999,
                        142.00001,
                        268.00001,
                        268.99999,
                        402.00001,
                        402.99999,
                        534.00001,
                        634.0);
        softly.assertThat(events).extracting("start.studyDayHourAsString").containsExactly(
                "-1184d 23:59",
                "-1108d 23:59",
                "-742d 23:59",
                "-377d 23:59",
                "1d 00:00",
                "16d 00:00",
                "36d 00:00",
                "43d 00:00",
                "43d 23:59",
                "115d 00:00",
                "128d 23:59",
                "143d 00:00",
                "269d 00:00",
                "269d 23:59",
                "403d 00:00",
                "403d 23:59",
                "535d 00:00");
        softly.assertThat(events).extracting("end.studyDayHourAsString")
                .containsExactly("-1108d 23:59",
                        "-742d 23:59",
                        "-377d 23:59",
                        "1d 00:00",
                        "16d 00:00",
                        "36d 00:00",
                        "43d 00:00",
                        "43d 23:59",
                        "115d 00:00",
                        "128d 23:59",
                        "143d 00:00",
                        "269d 00:00",
                        "269d 23:59",
                        "403d 00:00",
                        "403d 23:59",
                        "535d 00:00",
                        "635d 00:00");
        softly.assertThat(events).extracting("ongoing").contains(true, false);
        softly.assertThat(events).extracting("numberOfConmeds")
                .containsExactly(1, 2, 5, 6, 9, 10, 11, 12, 11, 13, 12, 13, 14, 13, 14, 13, 14);
        softly.assertThat(events.get(0).getConmeds()).extracting("conmed").containsExactly("ZOMETA");
        softly.assertThat(events.get(0).getConmeds()).extracting("indications").containsOnly(newArrayList("Bone metastases"));
        softly.assertThat(events.get(0).getConmeds()).extracting("doses").containsOnly(newArrayList(), newArrayList());
        softly.assertThat(events.get(0).getConmeds()).extracting("frequencies").containsOnly(newArrayList("2"), newArrayList("2"));
    }

    @Test
    // Adopted from WhenRunningTimelineConmedsServiceITCase
    public void shouldListConmedsSummaryForDiedSubjectStudyDate() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(DIED_SUBJECT)));

        List<SubjectConmedSummary> subjectConmedSummarys = conmedsTimelineService
                .getConmedsSummaries(DUMMY_ACUITY_DATASETS,
                        ConmedFilters.empty(), populationFilters,
                        DayZeroType.DAYS_SINCE_STUDY_DAY, null);


        SubjectConmedSummary diedSubjectSummary = subjectConmedSummarys.stream()
                .filter(aesd -> aesd.getSubjectId().equals(DIED_SUBJECT_ID)).findFirst().get();
        softly.assertThat(diedSubjectSummary.getSubjectId()).isEqualTo(DIED_SUBJECT_ID);
        softly.assertThat(diedSubjectSummary.getSubject()).isEqualTo(DIED_SUBJECT);

        List<ConmedSummaryEvent> events = diedSubjectSummary.getEvents();

        softly.assertThat(events).extracting("start.dayHour")
                .containsExactly(-1183.99999,
                        -1107.99999,
                        -741.99999,
                        -376.99999,
                        1.00001,
                        16.00001,
                        36.00001,
                        43.00001,
                        43.99999,
                        115.00001,
                        128.99999,
                        143.00001,
                        269.00001,
                        269.99999,
                        403.00001,
                        403.99999,
                        535.00001);
        softly.assertThat(events).extracting("end.dayHour")
                .containsExactly(-1107.99999,
                        -741.99999,
                        -376.99999,
                        1.00001,
                        16.00001,
                        36.00001,
                        43.00001,
                        43.99999,
                        115.00001,
                        128.99999,
                        143.00001,
                        269.00001,
                        269.99999,
                        403.00001,
                        403.99999,
                        535.00001,
                        635.0);
        softly.assertThat(events).extracting("ongoing").containsOnly(true, false);
        softly.assertThat(events).extracting("numberOfConmeds").containsExactly(1, 2, 5, 6, 9, 10, 11, 12, 11, 13, 12, 13, 14, 13, 14, 13, 14);
        softly.assertThat(events.get(0).getConmeds()).extracting("conmed").containsExactly("ZOMETA");
        softly.assertThat(events.get(0).getConmeds()).extracting("indications").containsExactly(newArrayList("Bone metastases"));
        softly.assertThat(events.get(0).getConmeds()).extracting("doses").containsExactly(emptyList());
        softly.assertThat(events.get(0).getConmeds()).extracting("frequencies").containsExactly(newArrayList("2"));

        softly.assertThat(events.get(1).getConmeds()).extracting("conmed").containsExactly("LORAZEPAM", "ZOMETA");
        softly.assertThat(events.get(2).getConmeds()).extracting("conmed")
                .containsExactly("IBUPROFEN", "LORAZEPAM", "OMEPRAZOL", "PARACETAMOL", "ZOMETA");
    }

    @Test
    // Adopted from WhenRunningTimelineConmedsServiceITCase
    public void shouldListConmedsSummaryForOngoingSubject() {
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(ONGOING_SUBJECT)));

        List<SubjectConmedSummary> subjectConmedSummarys = conmedsTimelineService
                .getConmedsSummaries(DUMMY_ACUITY_DATASETS,
                        ConmedFilters.empty(), populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectConmedSummary ongoingSubjectSummary = subjectConmedSummarys.stream()
                .filter(aesd -> aesd.getSubjectId().equals(ONGOING_SUBJECT_ID)).findFirst().get();
        softly.assertThat(ongoingSubjectSummary.getSubjectId()).isEqualTo(ONGOING_SUBJECT_ID);
        softly.assertThat(ongoingSubjectSummary.getSubject()).isEqualTo(ONGOING_SUBJECT);

        List<ConmedSummaryEvent> events = ongoingSubjectSummary.getEvents();

        softly.assertThat(events).extracting("start.dayHour").containsExactly(-751.99999,
                -202.99999,
                -14.99999,
                1.0E-5,
                6.00001,
                13.00001,
                41.99999,
                42.00001,
                42.99999,
                69.00001,
                69.99999,
                83.00001);
        softly.assertThat(events).extracting("end.dayHour").containsExactly(-202.99999,
                -14.99999,
                1.0E-5,
                6.00001,
                13.00001,
                41.99999,
                42.00001,
                42.99999,
                69.00001,
                69.99999,
                83.00001,
                1824.0);
        softly.assertThat(events).extracting("duration").containsExactly(550, 189, 16, 7, 8, 29, 2, 1, 28, 1, 15, 1742);
        softly.assertThat(events).extracting("ongoing").containsExactly(false, false, false, false,
                false, false, false, false, false, false, false, true);
        softly.assertThat(events).extracting("numberOfConmeds").containsExactly(4, 5, 6, 10, 12, 13, 10, 13, 10, 11, 7, 9);
        softly.assertThat(events.get(0).getConmeds()).extracting("conmed")
                .containsExactly("LOMPER", "QUIMODRIL", "TRIPTIZOL", "VENLAFAXINE");
        softly.assertThat(events.get(0).getConmeds()).extracting("doses")
                .containsExactly(newArrayList(), newArrayList(), newArrayList(10.0), newArrayList(50.0));
        softly.assertThat(events.get(0).getConmeds()).extracting("frequencies").containsOnly(newArrayList("2"));

        softly.assertThat(events.get(1).getConmeds()).extracting("conmed")
                .containsExactly("LOMPER", "NAPROXEN", "QUIMODRIL", "TRIPTIZOL", "VENLAFAXINE");
        softly.assertThat(events.get(2).getConmeds()).extracting("conmed")
                .containsExactly("LOMPER", "NAPROXEN", "OMEPRAZOL", "QUIMODRIL", "TRIPTIZOL", "VENLAFAXINE");
    }

    @Test
    // Adopted from WhenRunningTimelineConmedsServiceITCase
    public void shouldListConmedsByClassForDiedSubject() {
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(DIED_SUBJECT)));

        List<SubjectConmedByClass> subjectConmedByClass = conmedsTimelineService
                .getConmedsByClass(DUMMY_ACUITY_DATASETS,
                        ConmedFilters.empty(), populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectConmedByClass diedSubjectSummary = subjectConmedByClass.stream()
                .filter(aesd -> aesd.getSubjectId().equals(DIED_SUBJECT_ID)).findFirst().get();
        softly.assertThat(diedSubjectSummary.getSubjectId()).isEqualTo(DIED_SUBJECT_ID);
        softly.assertThat(diedSubjectSummary.getSubject()).isEqualTo(DIED_SUBJECT);

        List<ConmedEventsByClass> classEvents = diedSubjectSummary.getConmedClasses();

        softly.assertThat(classEvents).hasSize(16);

        ConmedEventsByClass simvastatin = classEvents.stream()
                .filter(c -> c.getConmedClass().equals("SIMVASTATIN")).findFirst().get();

        softly.assertThat(simvastatin.getEvents()).extracting("start.dayHour").containsExactly(42.00001, 142.00001);
        softly.assertThat(simvastatin.getEvents()).extracting("end.dayHour").doesNotContainNull();
        softly.assertThat(simvastatin.getEvents()).extracting("ongoing").containsExactly(false, true);
        softly.assertThat(simvastatin.getEvents()).extracting("numberOfConmeds").containsExactly(1, 1);
        softly.assertThat(simvastatin.getEvents().get(0).getConmeds()).extracting("conmed").containsExactly("SIMVASTATIN");
        softly.assertThat(simvastatin.getEvents().get(0).getConmeds()).extracting("doses")
                .containsExactly(newArrayList(10.0));
        softly.assertThat(simvastatin.getEvents().get(0).getConmeds()).extracting("indications")
                .containsExactly(newArrayList("Dislipidemy"));
        softly.assertThat(simvastatin.getEvents().get(0).getConmeds()).extracting("frequencies")
                .containsExactly(newArrayList("2"));
    }

    @Test
    // Adopted from WhenRunningTimelineConmedsServiceITCase
    public void shouldListConmedsByClassForOngoingSubject() {
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(ONGOING_SUBJECT)));

        List<SubjectConmedByClass> subjectConmedByClass = conmedsTimelineService
                .getConmedsByClass(DUMMY_ACUITY_DATASETS,
                        ConmedFilters.empty(), populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectConmedByClass ongoingSubjectSummary = subjectConmedByClass.stream()
                .filter(aesd -> aesd.getSubjectId().equals(ONGOING_SUBJECT_ID)).findFirst().get();
        softly.assertThat(ongoingSubjectSummary.getSubjectId()).isEqualTo(ONGOING_SUBJECT_ID);
        softly.assertThat(ongoingSubjectSummary.getSubject()).isEqualTo(ONGOING_SUBJECT);

        List<ConmedEventsByClass> classEvents = ongoingSubjectSummary.getConmedClasses();

        softly.assertThat(classEvents).hasSize(19);

        ConmedEventsByClass betamethasone = classEvents.stream()
                .filter(c -> c.getConmedClass().equals("AMITRIPTYLINE")).findFirst().get();

        softly.assertThat(betamethasone.getEvents()).extracting("start.dayHour").containsExactly(-751.99999);
        softly.assertThat(betamethasone.getEvents()).extracting("start.doseDayHour").containsExactly(-751.99999);
        softly.assertThat(betamethasone.getEvents()).extracting("ongoing").containsExactly(true);
        softly.assertThat(betamethasone.getEvents()).extracting("numberOfConmeds").containsExactly(1);
        softly.assertThat(betamethasone.getEvents().get(0).getConmeds()).extracting("conmed").containsExactly("TRIPTIZOL");
        softly.assertThat(betamethasone.getEvents().get(0).getConmeds()).extracting("doses").containsOnly(newArrayList(10.0));
        softly.assertThat(betamethasone.getEvents().get(0).getConmeds()).extracting("frequencies").containsOnly(newArrayList("2"));
    }

    @Test
    // Adopted from WhenRunningTimelineConmedsServiceITCase
    public void shouldListConmedsByDrugForDiedSubject() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(DIED_SUBJECT)));

        List<SubjectConmedByDrug> subjectConmedByDrug = conmedsTimelineService
                .getConmedsByDrug(DUMMY_ACUITY_DATASETS,
                        ConmedFilters.empty(), populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectConmedByDrug ongoingSubjectSummary = subjectConmedByDrug.stream()
                .filter(aesd -> aesd.getSubjectId().equals(DIED_SUBJECT_ID)).findFirst().get();
        softly.assertThat(ongoingSubjectSummary.getSubjectId()).isEqualTo(DIED_SUBJECT_ID);
        softly.assertThat(ongoingSubjectSummary.getSubject()).isEqualTo(DIED_SUBJECT);

        List<ConmedEventsByDrug> medicationEvents = ongoingSubjectSummary.getConmedMedications();

        softly.assertThat(medicationEvents).hasSize(17);

        ConmedEventsByDrug paracetamolClass = medicationEvents.stream()
                .filter(m -> m.getConmedMedication().equals("FLUMIL (ACETYLCYSTEINE)")).findFirst().get();

        softly.assertThat(paracetamolClass.getConmedMedication()).isEqualTo("FLUMIL (ACETYLCYSTEINE)");
        softly.assertThat(paracetamolClass.getEvents()).hasSize(1);
        softly.assertThat(paracetamolClass.getEvents()).extracting("start.dayHour").containsExactly(534.00001);
        softly.assertThat(paracetamolClass.getEvents()).extracting("end.dayHour").doesNotContainNull();
        softly.assertThat(paracetamolClass.getEvents()).extracting("start.doseDayHour").containsExactly(534.00001);
        softly.assertThat(paracetamolClass.getEvents()).extracting("end.doseDayHour").doesNotContainNull();
        softly.assertThat(paracetamolClass.getEvents()).extracting("conmed").containsExactly("FLUMIL (ACETYLCYSTEINE)");
        softly.assertThat(paracetamolClass.getEvents()).extracting("indication").containsExactly("upper way respiratory infection");
        softly.assertThat(paracetamolClass.getEvents()).extracting("dose").containsExactly(1600.0);
        softly.assertThat(paracetamolClass.getEvents()).extracting("frequency").containsExactly("2");
        softly.assertThat(paracetamolClass.getEvents()).extracting("ongoing").containsExactly(true);
    }

    @Test
    // Adopted from WhenRunningTimelineConmedsServiceITCase
    public void shouldListConmedsByDrugForOnGoingSubject() {
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(Collections.singletonList(ONGOING_SUBJECT)));

        List<SubjectConmedByDrug> subjectConmedByDrug = conmedsTimelineService
                .getConmedsByDrug(DUMMY_ACUITY_DATASETS,
                        ConmedFilters.empty(), populationFilters,
                        DayZeroType.DAYS_SINCE_FIRST_DOSE, null);

        SubjectConmedByDrug ongoingSubjectSummary = subjectConmedByDrug.stream()
                .filter(aesd -> aesd.getSubjectId().equals(ONGOING_SUBJECT_ID)).findFirst().get();
        softly.assertThat(ongoingSubjectSummary.getSubjectId()).isEqualTo(ONGOING_SUBJECT_ID);
        softly.assertThat(ongoingSubjectSummary.getSubject()).isEqualTo(ONGOING_SUBJECT);

        List<ConmedEventsByDrug> medicationEvents = ongoingSubjectSummary.getConmedMedications();

        softly.assertThat(medicationEvents).hasSize(19);

        ConmedEventsByDrug triptizol = medicationEvents.stream()
                .filter(m -> m.getConmedMedication().equals("TRIPTIZOL")).findFirst().get();

        softly.assertThat(triptizol.getConmedMedication()).isEqualTo("TRIPTIZOL");
        softly.assertThat(triptizol.getEvents()).extracting("start.dayHour").containsExactly(-751.99999);
        softly.assertThat(triptizol.getEvents()).extracting("conmed").containsExactly("TRIPTIZOL");
        softly.assertThat(triptizol.getEvents()).extracting("dose").containsExactly(10.0);
        softly.assertThat(triptizol.getEvents()).extracting("ongoing").containsExactly(true);

        ConmedEventsByDrug diproderm = medicationEvents.stream()
                .filter(m -> m.getConmedMedication().equals("DIPRODERM")).findFirst().get();

        softly.assertThat(diproderm.getConmedMedication()).isEqualTo("DIPRODERM");
        softly.assertThat(diproderm.getEvents()).extracting("start.dayHour").containsExactly(6.00001);
        softly.assertThat(diproderm.getEvents()).extracting("end.dayHour").containsExactly(69.99999);
        softly.assertThat(diproderm.getEvents()).extracting("conmed").containsExactly("DIPRODERM");
        softly.assertThat(diproderm.getEvents()).extracting("dose").containsNull();
        softly.assertThat(diproderm.getEvents()).extracting("ongoing").containsExactly(false);

    }
}
