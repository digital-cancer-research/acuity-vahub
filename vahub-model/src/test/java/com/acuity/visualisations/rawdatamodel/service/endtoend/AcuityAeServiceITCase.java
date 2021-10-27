package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.util.AlphanumEmptyLastComparator;
import com.acuity.visualisations.rawdatamodel.vo.AeDetailLevel;
import com.acuity.visualisations.rawdatamodel.vo.AesTable;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_SPECIAL_INTEREST_GROUP_DATASETS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.COUNT_OF_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityAeServiceITCase {
    @Autowired
    private AeService aeService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    //    @Test
//    public void shouldGetBarChartWhenColorByIsAll() {
//        //When
//        List<TrellisedBarChart<CIEventGroupByOptions>> result =
//                ciEventService.getBarChart(Constants.CEREBRO_DATASETS, new HashSet<>(), CIEventFilters.empty(),
//                        PopulationFilters.empty(), COUNT_OF_SUBJECTS, FINAL_DIAGNOSIS.getAttribute());
//
//        //Then
//        softly.assertThat(result.size()).isEqualTo(1);
//        List<? extends OutputBarChartData> data = result.get(0).getData();
//        softly.assertThat(data.size()).isEqualTo(1);
//        softly.assertThat(data.get(0).getCategories()).contains("Cerebrovascular Ischemia", "Anoxic depolarization", "Not Confirmed",
//                "Focal Brain Ischemia", "Cerebral Ischemia", "CI", "Brain Ischemia", "Ischemia");
//        softly.assertThat(data.get(0).getName()).isEqualTo("");
//        List<OutputBarChartEntry> series = data.get(0).getSeries();
//        softly.assertThat(series.size()).isEqualTo(8);
//        series.stream().forEach(s -> {
//            softly.assertThat(Arrays.asList("Cerebrovascular Ischemia", "Anoxic depolarization", "Not Confirmed",
//                    "Focal Brain Ischemia", "Cerebral Ischemia", "CI", "Brain Ischemia", "Ischemia")).contains(s.getCategory());
//            softly.assertThat(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)).contains(s.getRank());
//
//        });
//        softly.assertThat(series.get(0).getTotalSubjects()).isEqualTo(6);
//        softly.assertThat(series.get(0).getValue()).isEqualTo(6.0);
//    }
//
    @Test
    public void shouldGetSubjectCountsForPTEventsPerIncidence() {
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.PT.getGroupByOptionAndParams());
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        List<TrellisedBarChart<Ae, AeGroupByOptions>> result = aeService.getBarChart(
                DUMMY_ACUITY_DATASETS, settingsWithFilterBy.build(),
                AeFilters.empty(), PopulationFilters.empty(), COUNT_OF_SUBJECTS
        );

        softly.assertThat(result).hasSize(1);
        softly.assertThat(result.get(0).getData()).hasSize(6);
        softly.assertThat(result.get(0).getData()).extracting("name")
                .containsOnly("(Empty)", "CTC Grade 1", "CTC Grade 2", "CTC Grade 3", "CTC Grade 4", "CTC Grade 5");
        softly.assertThat(result.get(0).getData().get(0).getSeries().stream() // Grade 1
                .map(OutputBarChartEntry::getCategory).sorted().collect(toList())).startsWith("(Empty)", "ABDOMINAL DISCOMFORT").endsWith("WOUND");
        softly.assertThat(result.get(0).getData().get(1).getSeries().stream() // Grade 2
                .map(OutputBarChartEntry::getRank).collect(toList()))
                .isEqualTo(result.get(0).getData().get(1).getSeries().stream().map(OutputBarChartEntry::getRank).sorted().collect(toList()));
        softly.assertThat(result.get(0).getData().get(1).getSeries().stream() // Grade 2
                .map(OutputBarChartEntry::getCategory).sorted().collect(toList()))
                .startsWith("ABDOMINAL DISTENSION").endsWith("WHITE BLOOD CELL COUNT DECREASED");
        softly.assertThat(result.get(0).getData().get(2).getSeries().stream() // Grade 3
                .map(OutputBarChartEntry::getRank).collect(toList()))
                .isEqualTo(result.get(0).getData().get(2).getSeries().stream().map(OutputBarChartEntry::getRank).sorted().collect(toList()));
        softly.assertThat(result.get(0).getData().get(2).getSeries().stream() // Grade 3
                .map(OutputBarChartEntry::getCategory).sorted().collect(toList()))
                .startsWith("ABDOMINAL PAIN").endsWith("WEIGHT DECREASED");
        softly.assertThat(result.get(0).getData().get(3).getSeries().stream() // Grade 4
                .map(OutputBarChartEntry::getRank).collect(toList()))
                .isEqualTo(result.get(0).getData().get(3).getSeries().stream().map(OutputBarChartEntry::getRank).sorted().collect(toList()));
        softly.assertThat(result.get(0).getData().get(3).getSeries().stream() // Grade 4
                .map(OutputBarChartEntry::getCategory).sorted().collect(Collectors.toList()))
                .startsWith("ATYPICAL PNEUMONIA").endsWith("NEUTROPENIA");
        softly.assertThat(result.get(0).getData().get(4).getSeries().stream() // Grade 5
                .map(OutputBarChartEntry::getRank).collect(toList()))
                .isEqualTo(result.get(0).getData().get(4).getSeries().stream().map(OutputBarChartEntry::getRank).sorted().collect(toList()));
    }

    @Test
    public void shouldGetSubjectCountsForPTEventsPerSeverityChange() {
        AeFilters aeFilters = AeFilters.empty();
        aeFilters.setAeDetailLevel(AeDetailLevel.PER_SEVERITY_CHANGE);

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.PT.getGroupByOptionAndParams());
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        List<TrellisedBarChart<Ae, AeGroupByOptions>> result = aeService.getBarChart(
                DUMMY_ACUITY_DATASETS,
                settingsWithFilterBy.build(),
                aeFilters, PopulationFilters.empty(), COUNT_OF_SUBJECTS);

        softly.assertThat(result).hasSize(1);
        softly.assertThat(result.get(0).getData()).hasSize(6);
        softly.assertThat(result.get(0).getData()).extracting(OutputBarChartData::getName)
                .isSortedAccordingTo(AlphanumEmptyLastComparator.getInstance())
                .containsExactlyInAnyOrder("CTC Grade 1", "CTC Grade 2", "CTC Grade 3", "CTC Grade 4", "CTC Grade 5", "(Empty)");
        softly.assertThat(result.get(0).getData()).filteredOn(e -> e.getName().equals("CTC Grade 1"))
                .flatExtracting(OutputBarChartData::getSeries).extracting(OutputBarChartEntry::getCategory, OutputBarChartEntry::getValue)
                .startsWith(
                        tuple("DIARRHOEA", 44.0),
                        tuple("ALOPECIA", 30.0)
                );
        softly.assertThat(result.get(0).getData()).filteredOn(e -> e.getName().equals("CTC Grade 2"))
                .flatExtracting(OutputBarChartData::getSeries).extracting(OutputBarChartEntry::getCategory, OutputBarChartEntry::getValue)
                .startsWith(
                        tuple("DIARRHOEA", 17.0),
                        tuple("ALOPECIA", 36.0)
                );
        softly.assertThat(result.get(0).getData()).filteredOn(e -> e.getName().equals("CTC Grade 3"))
                .flatExtracting(OutputBarChartData::getSeries).extracting(OutputBarChartEntry::getCategory, OutputBarChartEntry::getValue)
                .startsWith(
                        tuple("DIARRHOEA", 8.0),
                        tuple("RASH", 9.0)
                );
        softly.assertThat(result.get(0).getData()).filteredOn(e -> e.getName().equals("CTC Grade 4"))
                .flatExtracting(OutputBarChartData::getSeries).extracting(OutputBarChartEntry::getCategory, OutputBarChartEntry::getValue)
                .startsWith(
                        tuple("DIARRHOEA", 1.0),
                        tuple("NEUTROPENIA", 4.0)
                );
        softly.assertThat(result.get(0).getData()).filteredOn(e -> e.getName().equals("CTC Grade 5"))
                .flatExtracting(OutputBarChartData::getSeries).extracting(OutputBarChartEntry::getCategory, OutputBarChartEntry::getValue)
                .startsWith(
                        tuple("RENAL FAILURE ACUTE", 1.0),
                        tuple("RESPIRATORY FAILURE", 1.0)
                );
        softly.assertThat(result.get(0).getData()).filteredOn(e -> e.getName().equals("(Empty)"))
                .flatExtracting(OutputBarChartData::getSeries).extracting(OutputBarChartEntry::getCategory, OutputBarChartEntry::getValue)
                .containsOnly(
                        tuple("(Empty)", 4.0)
                );
    }

    @Test
    public void shouldGetSubjectPercentagesOverAllForPTEvents() {

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.PT.getGroupByOptionAndParams());
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        List<TrellisedBarChart<Ae, AeGroupByOptions>> result = aeService.getBarChart(
                DUMMY_ACUITY_DATASETS,
                settingsWithFilterBy.build(),
                AeFilters.empty(), PopulationFilters.empty(), COUNT_OF_SUBJECTS);

        softly.assertThat(result).hasSize(1);
        softly.assertThat(result.get(0).getData()).extracting("name")
                .containsExactlyInAnyOrder("CTC Grade 1", "CTC Grade 2", "CTC Grade 3", "CTC Grade 4", "CTC Grade 5", "(Empty)");
    }

    @Test
    public void shouldGetSubjectPercentagesOverTrellisForPTEvents() {
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.PT.getGroupByOptionAndParams());
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        List<TrellisedBarChart<Ae, AeGroupByOptions>> result = aeService.getBarChart(
                DUMMY_ACUITY_DATASETS,
                settingsWithFilterBy.build(),
                AeFilters.empty(), PopulationFilters.empty(), PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT
        );

        softly.assertThat(result).hasSize(1);
        softly.assertThat(result.get(0).getData())
                .extracting("name").containsExactlyInAnyOrder("CTC Grade 1", "CTC Grade 2", "CTC Grade 3", "CTC Grade 4", "CTC Grade 5", "(Empty)");
    }

    @Test
    public void shouldGetSubjectPercentagesOverTrellisForPTEventsWithCorrectCounts() {

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, AeGroupByOptions.PT.getGroupByOptionAndParams());
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        List<TrellisedBarChart<Ae, AeGroupByOptions>> result = aeService.getBarChart(
                DUMMY_ACUITY_DATASETS,
                settingsWithFilterBy.build(),
                AeFilters.empty(), PopulationFilters.empty(), PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT
        );

        softly.assertThat(result).hasSize(1);
        softly.assertThat(result.get(0).getData().get(0).getSeries()).filteredOn("category", "ABDOMINAL PAIN").extracting("value").containsOnly(12.9);
        softly.assertThat(result.get(0).getData().get(0).getSeries()).filteredOn("category", "ABDOMINAL PAIN LOWER").extracting("value").containsOnly(1.61);
        softly.assertThat(result.get(0).getData().get(0).getSeries()).filteredOn("category", "ABDOMINAL PAIN UPPER").extracting("value").containsOnly(10.48);
    }

    @Test
    public void shouldGetSubjectCountsForPTEvents() throws InterruptedException, ExecutionException {
        //Given
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, AeGroupByOptions.PT.getGroupByOptionAndParams());
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        List<TrellisedBarChart<Ae, AeGroupByOptions>> result = aeService.getBarChart(
                DUMMY_ACUITY_DATASETS,
                settingsWithFilterBy.build(),
                AeFilters.empty(), PopulationFilters.empty(), COUNT_OF_SUBJECTS
        );

        softly.assertThat(result).hasSize(1);
        softly.assertThat(result.get(0).getData().get(0).getCategories()).hasSize(355);
    }

    @Test
    public void shouldGetPTAeTableData() {
        List<AesTable> result = aeService.getAesTableData(DUMMY_ACUITY_DATASETS, AeGroupByOptions.PT, AeFilters.empty(), PopulationFilters.empty());

        // Then -- boundary test
        assertThat(result)
                .hasSize(499)
                .extracting("term", "grade", "treatmentArm", "subjectCountPerGrade", "subjectCountPerTerm",
                        "subjectCountPerArm", "noIncidenceCount")
                .startsWith(tuple("ABDOMINAL DISCOMFORT", "CTC Grade 1", "All", 1, 1, 124, 123))
                .contains(tuple("ACNE", "CTC Grade 1", "All", 1, 2, 124, 122))
                .contains(tuple("CHAPPED LIPS", "CTC Grade 1", "All", 1, 1, 124, 123))
                .endsWith(tuple("WOUND", "CTC Grade 1", "All", 1, 1, 124, 123));
    }

    @Test
    public void shouldGetSOCAeTableData() {
        List<AesTable> result = aeService.getAesTableData(DUMMY_ACUITY_DATASETS, AeGroupByOptions.SOC, AeFilters.empty(), PopulationFilters.empty());

        // Then -- boundary test
        assertThat(result)
                .hasSize(69)
                .extracting("term", "grade", "treatmentArm", "subjectCountPerGrade", "subjectCountPerTerm",
                        "subjectCountPerArm", "noIncidenceCount")
                .startsWith(tuple("BLOOD", "CTC Grade 1", "All", 10, 59, 124, 65))
                .contains(tuple("INJ&P", "CTC Grade 1", "All", 5, 16, 124, 108))
                .contains(tuple("NEOPL", "CTC Grade 1", "All", 1, 2, 124, 122))
                .endsWith(tuple("VASC", "CTC Grade 3", "All", 3, 20, 124, 104));
    }

    @Test
    public void shouldGetHLTAeTableData() {
        List<AesTable> result = aeService.getAesTableData(DUMMY_ACUITY_DATASETS, AeGroupByOptions.HLT, AeFilters.empty(), PopulationFilters.empty());

        // Then -- boundary test
        assertThat(result)
                .hasSize(339)
                .extracting("term", "grade", "treatmentArm", "subjectCountPerGrade", "subjectCountPerTerm",
                        "subjectCountPerArm", "noIncidenceCount")
                .startsWith(tuple("ACNES", "CTC Grade 1", "All", 7, 15, 124, 109))
                .contains(tuple("FEMALE REPRODUCTIVE TRACT INFECTIONS", "CTC Grade 2", "All", 1, 3, 124, 121))
                .contains(tuple("HEARING LOSSES", "CTC Grade 1", "All", 1, 1, 124, 123))
                .endsWith(tuple("WHITE BLOOD CELL ANALYSES", "CTC Grade 2", "All", 2, 3, 124, 121));
    }

    @Test
    public void shouldGetAeTableDataWithAEsFilters() {
        AeFilters aeFilters = new AeFilters();
        aeFilters.setPt(new SetFilter<>(newArrayList("ABDOMINAL DISTENSION")));

        List<AesTable> result = aeService.getAesTableData(DUMMY_ACUITY_DATASETS, AeGroupByOptions.PT, aeFilters, PopulationFilters.empty());

        // Then
        assertThat(result)
                .hasSize(1)
                .extracting("term", "grade", "treatmentArm", "subjectCountPerGrade", "subjectCountPerTerm",
                        "subjectCountPerArm", "noIncidenceCount")
                .contains(tuple("ABDOMINAL DISTENSION", "CTC Grade 2", "All", 1, 1, 124, 123));
    }

    @Test
    public void shouldGetAeTableDataWithPopulationFilters() {

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSex(new SetFilter<>(newArrayList("Male")));

        List<AesTable> result = aeService.getAesTableData(
                DUMMY_ACUITY_DATASETS, AeGroupByOptions.PT, AeFilters.empty(), populationFilters);

        // Then -- boundary test
        assertThat(result)
                .hasSize(315)
                .extracting("term", "grade", "treatmentArm", "subjectCountPerGrade", "subjectCountPerTerm",
                        "subjectCountPerArm", "noIncidenceCount")
                .startsWith(tuple("ABDOMINAL DISTENSION", "CTC Grade 2", "All", 1, 1, 62, 61))
                .contains(tuple("COUGH", "CTC Grade 1", "All", 8, 12, 62, 50))
                .endsWith(tuple("WOUND", "CTC Grade 1", "All", 1, 1, 62, 61));
    }

    @Test
    public void shouldGetSpecialInterestGroupAeTableData() {

        List<AesTable> result = aeService.getAesTableData(
                DUMMY_ACUITY_SPECIAL_INTEREST_GROUP_DATASETS, AeGroupByOptions.SPECIAL_INTEREST_GROUP, AeFilters.empty(), PopulationFilters.empty());

        // Then
        assertThat(result)
                .hasSize(6)
                .extracting("term", "grade", "treatmentArm", "subjectCountPerGrade", "subjectCountPerTerm",
                        "subjectCountPerArm", "noIncidenceCount")
                .startsWith(tuple("Default group", "CTC Grade 1", "All", 10, 124, 124, 0))
                .contains(tuple("Default group", "CTC Grade 4", "All", 9, 124, 124, 0))
                .endsWith(tuple("Default group", "No severity grade recorded", "All", 4, 124, 124, 0));
    }
}
