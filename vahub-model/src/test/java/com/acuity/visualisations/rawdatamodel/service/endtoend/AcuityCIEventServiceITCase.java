package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.CIEventFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.event.CIEventService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CIEventGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.ColoredOutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CIEvent;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.COUNT_OF_EVENTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.COUNT_OF_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.google.common.collect.Sets.newHashSet;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityCIEventServiceITCase {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private CIEventService ciEventService;

    @Test
    public void shouldGetBarChartWhenColorByIsAll() {
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CIEvent, CIEventGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, CIEventGroupByOptions.FINAL_DIAGNOSIS.getGroupByOptionAndParams());
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<CIEvent, CIEventGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        //When
        List<TrellisedBarChart<CIEvent, CIEventGroupByOptions>> result =
                ciEventService.getBarChart(DUMMY_ACUITY_DATASETS, settingsWithFilterBy.build(), CIEventFilters.empty(),
                        PopulationFilters.empty(), COUNT_OF_SUBJECTS);

        //Then
        softly.assertThat(result.size()).isEqualTo(1);
        List<? extends OutputBarChartData> data = result.get(0).getData();
        softly.assertThat(data.size()).isEqualTo(1);
        softly.assertThat(data.get(0).getCategories()).contains("Brain Ischemia", "Focal Brain Ischemia", "Unstable Angina",
                "Cerebrovascular Ischemia", "ST Elevation Myocardial Infarction (STEMI)", "Non-ST Elevation Myocardial Infarction (NSTEMI)",
                "Other", "Not Confirmed", "Cerebral Ischemia", "Ischemia", "Stable Angina");
        softly.assertThat(data.get(0).getName()).isEqualTo("All");
        List<OutputBarChartEntry> series = data.get(0).getSeries();
        softly.assertThat(series.size()).isEqualTo(11);
        series.forEach(s -> {
            softly.assertThat(Arrays.asList("Brain Ischemia", "Focal Brain Ischemia", "Unstable Angina",
                    "Cerebrovascular Ischemia", "ST Elevation Myocardial Infarction (STEMI)", "Non-ST Elevation Myocardial Infarction (NSTEMI)",
                    "Other", "Not Confirmed", "Cerebral Ischemia", "Ischemia", "Stable Angina")).contains(s.getCategory());
            softly.assertThat(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11)).contains(s.getRank());

        });
        softly.assertThat(series.get(0).getTotalSubjects()).isEqualTo(12);
        softly.assertThat(series.get(0).getValue()).isEqualTo(12.0);
    }

    @Test
    public void shouldGetBarChartWhenColorByIsPreviousEcgAvailable() {
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CIEvent, CIEventGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, CIEventGroupByOptions.PREVIOUS_ECG_AVAILABLE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, CIEventGroupByOptions.PREVIOUS_ECG_AVAILABLE.getGroupByOptionAndParams());
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<CIEvent, CIEventGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        //When
        List<TrellisedBarChart<CIEvent, CIEventGroupByOptions>> result =
                ciEventService.getBarChart(DUMMY_ACUITY_DATASETS, settingsWithFilterBy.build(),
                        new CIEventFilters(), new PopulationFilters(), COUNT_OF_EVENTS);

        //Then
        softly.assertThat(result.size()).isEqualTo(1);
        List<? extends ColoredOutputBarChartData> data = (List<? extends ColoredOutputBarChartData>) result.get(0).getData();
        softly.assertThat(data.size()).isEqualTo(3);
        softly.assertThat(data.get(0).getCategories()).contains("Yes", "No", "(Empty)");
        data.stream().forEach(d -> {
            softly.assertThat(Arrays.asList("Yes", "No", "(Empty)")).contains(d.getName());
            softly.assertThat(Arrays.asList("#4363D8", "#FE8C01", "#B1C8ED")).contains(d.getColor());
            softly.assertThat(d.getSeries().size()).isEqualTo(1);
            softly.assertThat(d.getSeries().get(0).getCategory()).isEqualTo(d.getName());
        });
    }

    @Test
    public void testGetSelectionDetails() {
        //Given
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CIEvent, CIEventGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, CIEventGroupByOptions.CI_SYMPTOMS_DURATION.getGroupByOptionAndParams());
        settings.withOption(COLOR_BY, CIEventGroupByOptions.FINAL_DIAGNOSIS.getGroupByOptionAndParams());
        final HashMap<CIEventGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(COLOR_BY, "Cerebral Ischemia");
        selectedItems.put(X_AXIS, "24 hours");
        //When
        SelectionDetail result = ciEventService.getSelectionDetails(DUMMY_ACUITY_DATASETS, new CIEventFilters(),
                new PopulationFilters(), ChartSelection.of(settings.build(), Collections.singletonList(
                        ChartSelectionItem.of(selectedTrellises, selectedItems)
                )));

        //Then
        softly.assertThat(result.getEventIds().size()).isEqualTo(1);
        softly.assertThat(result.getSubjectIds().size()).isEqualTo(1);
    }

    @Test
    public void shouldGetSubjects() {
        //Given
        CIEventFilters filters = new CIEventFilters();
        filters.setFinalDiagnosis(new SetFilter<>(newHashSet("Ischemia")));

        //When
        List<String> result = ciEventService.getSubjects(DUMMY_ACUITY_DATASETS, filters, PopulationFilters.empty());

        //Then
        softly.assertThat(result.size()).isEqualTo(1);
    }

    public void shouldGetBarChart() {

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CIEvent, CIEventGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, CIEventGroupByOptions.EVENT_SUSP_DUE_TO_STENT_THROMB.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, CIEventGroupByOptions.FINAL_DIAGNOSIS.getGroupByOptionAndParams());
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<CIEvent, CIEventGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        //When
        List<TrellisedBarChart<CIEvent, CIEventGroupByOptions>> barChart =
                ciEventService.getBarChart(DUMMY_ACUITY_DATASETS, settingsWithFilterBy.build(),
                        new CIEventFilters(), new PopulationFilters(), COUNT_OF_EVENTS);

        List<? extends OutputBarChartData> chart = barChart.get(0).getData();
        softly.assertThat(chart).hasSize(3);
        OutputBarChartData data1 = chart.get(0);

        softly.assertThat(data1.getName()).isEqualTo("No");
        String[] allCategories = {"Brain Ischemia",
                "Cerebral Ischemia",
                "Cerebrovascular Ischemia",
                "Focal Brain Ischemia",
                "Anoxic depolarization",
                "CI",
                "Ischemia",
                "Not Confirmed"};
        softly.assertThat(data1.getCategories()).containsExactly(allCategories);
        softly.assertThat(data1.getSeries()).extracting(OutputBarChartEntry::getCategory)
                .containsExactly("Brain Ischemia",
                        "Cerebral Ischemia",
                        "Cerebrovascular Ischemia",
                        "CI",
                        "Ischemia",
                        "Not Confirmed");
        softly.assertThat(data1.getSeries()).extracting(OutputBarChartEntry::getRank)
                .containsExactly(1, 2, 3, 6, 7, 8);
        softly.assertThat(data1.getSeries()).extracting(OutputBarChartEntry::getValue).containsExactly(1.0, 1.0, 3.0, 1.0, 2.0, 1.0);

        OutputBarChartData data2 = chart.get(1);

        softly.assertThat(data2.getName()).isEqualTo("Yes");
        softly.assertThat(data2.getCategories()).containsExactly(allCategories);
        softly.assertThat(data2.getSeries()).extracting(OutputBarChartEntry::getCategory)
                .containsExactly(allCategories);
        softly.assertThat(data2.getSeries()).extracting(OutputBarChartEntry::getRank)
                .containsExactlyElementsOf(IntStream.rangeClosed(1, data2.getCategories().size())
                        .boxed().collect(Collectors.toList()));
        softly.assertThat(data2.getSeries()).extracting(OutputBarChartEntry::getValue).containsExactly(2.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0);

        OutputBarChartData data3 = chart.get(2);

        softly.assertThat(data3.getName()).isEqualTo("(Empty)");
        softly.assertThat(data3.getCategories()).containsExactly(allCategories);
        softly.assertThat(data3.getSeries()).extracting(OutputBarChartEntry::getCategory)
                .containsExactly("Brain Ischemia",
                        "Cerebral Ischemia",
                        "Cerebrovascular Ischemia",
                        "Focal Brain Ischemia",
                        "Anoxic depolarization",
                        "CI",
                        "Not Confirmed");
        softly.assertThat(data3.getSeries()).extracting(OutputBarChartEntry::getRank)
                .containsExactly(1, 2, 3, 4, 5, 6, 8);
        softly.assertThat(data3.getSeries()).extracting(OutputBarChartEntry::getValue).containsExactly(7.0, 8.0, 6.0, 9.0, 2.0, 1.0, 1.0);
    }

}
