package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.axes.TAxes;
import com.acuity.visualisations.rawdatamodel.filters.*;
import com.acuity.visualisations.rawdatamodel.service.timeline.TimelineService;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.*;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityTimelineServiceITCase {
    @Autowired
    private TimelineService timelineService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetAvailableOptions() {
        List<TAxes<DayZeroType>> options = timelineService.getAvailableOptions(MULTI_DUMMY_ACUITY_DATASETS);

        assertThat(options).containsExactlyInAnyOrder(
                new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE, null, null),
                new TAxes<>(DayZeroType.DAYS_SINCE_RANDOMISATION, null, null),
                new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_TREATMENT, null, "AZD1234"),
                new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_TREATMENT, null, "X9876")
        );
    }

    @Test
    public void shouldGetAvailableTracks() {
        List<String> availableTracks = timelineService.getAvailableTracks(DUMMY_ACUITY_DATASETS);

        softly.assertThat(availableTracks).containsOnly("AES", "CONMEDS", "DOSING", "ECG", "LABS", "STATUS_SUMMARY", "VITALS");
    }

    @Test
    public void shouldListStatusSummaries() {
        List<String> statusSummaries =
                timelineService.getSubjectsSortedByStudyDuration(
                        DUMMY_ACUITY_DATASETS,
                        PopulationFilters.empty(),
                        Collections.singletonList(TimelineTrack.STATUS_SUMMARY),
                        DayZeroType.DAYS_SINCE_FIRST_DOSE,
                        null,
                        AeFilters.empty(),
                        ConmedFilters.empty(),
                        DrugDoseFilters.empty(),
                        CardiacFilters.empty(),
                        LabFilters.empty(),
                        LungFunctionFilters.empty(),
                        ExacerbationFilters.empty(),
                        VitalFilters.empty());

        assertThat(statusSummaries).containsSequence("E000010043", "E0000100229", "E0000100143", "E0000100296");
    }

    @Test
    public void shouldListStatusSummariesWithRandomisationDate() {
        List<String> statusSummaries =
                timelineService.getSubjectsSortedByStudyDuration(
                        DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        PopulationFilters.empty(),
                        Collections.singletonList(TimelineTrack.STATUS_SUMMARY),
                        DayZeroType.DAYS_SINCE_RANDOMISATION,
                        null,
                        AeFilters.empty(),
                        ConmedFilters.empty(),
                        DrugDoseFilters.empty(),
                        CardiacFilters.empty(),
                        LabFilters.empty(),
                        LungFunctionFilters.empty(),
                        ExacerbationFilters.empty(),
                        VitalFilters.empty());

        Collections.sort(statusSummaries);
        assertThat(statusSummaries).hasSize(116);
        assertThat(statusSummaries).doesNotContain("E0000020", "E0000021", "E0000032");
    }


    @Test
    public void shouldListStatusSummariesWithAesTrack() {
        List<String> statusSummaries =
                timelineService.getSubjectsSortedByStudyDuration(
                        DUMMY_ACUITY_DATASETS,
                        PopulationFilters.empty(),
                        newArrayList(TimelineTrack.STATUS_SUMMARY, TimelineTrack.AES),
                        DayZeroType.DAYS_SINCE_FIRST_DOSE,
                        null,
                        AeFilters.empty(),
                        ConmedFilters.empty(),
                        DrugDoseFilters.empty(),
                        CardiacFilters.empty(),
                        LabFilters.empty(),
                        LungFunctionFilters.empty(),
                        ExacerbationFilters.empty(),
                        VitalFilters.empty());

        softly.assertThat(statusSummaries).hasSize(124);
    }

    @Test
    public void shouldListStatusSummariesWithOnlyAesTrack() {
        List<String> statusSummaries =
                timelineService.getSubjectsSortedByStudyDuration(
                        DUMMY_ACUITY_DATASETS,
                        PopulationFilters.empty(),
                        Collections.singletonList(TimelineTrack.AES),
                        DayZeroType.DAYS_SINCE_FIRST_DOSE,
                        null,
                        AeFilters.empty(),
                        ConmedFilters.empty(),
                        DrugDoseFilters.empty(),
                        CardiacFilters.empty(),
                        LabFilters.empty(),
                        LungFunctionFilters.empty(),
                        ExacerbationFilters.empty(),
                        VitalFilters.empty());

        softly.assertThat(statusSummaries).hasSize(122);
    }
}
