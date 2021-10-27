package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.CerebrovascularFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.event.CerebrovascularService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CerebrovascularGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.ColoredOutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;
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

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.COUNT_OF_EVENTS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static jersey.repackaged.com.google.common.collect.Sets.newHashSet;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityCerebrovascularITCase {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private CerebrovascularService cerebrovascularService;

    @Test
    public void testGetBarChartWhenColorByIsAll() {
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Cerebrovascular, CerebrovascularGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, CerebrovascularGroupByOptions.EVENT_TYPE.getGroupByOptionAndParams());
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Cerebrovascular, CerebrovascularGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
        //When
        List<TrellisedBarChart<Cerebrovascular, CerebrovascularGroupByOptions>> result = cerebrovascularService.getBarChart(
                DUMMY_ACUITY_DATASETS, settingsWithFilterBy.build(), new CerebrovascularFilters(), new PopulationFilters(),
                COUNT_OF_EVENTS);

        //Then
        softly.assertThat(result.size()).isEqualTo(1);
        List<? extends OutputBarChartData> data = result.get(0).getData();
        softly.assertThat(data.size()).isEqualTo(1);
        softly.assertThat(data.get(0).getCategories()).contains("Undetermined", "Primary Ischemic Stroke", "(Empty)",
                "Primary Intracranial Hemorrhage", "Transient Ischaemic Attack (TIA)");
        softly.assertThat(data.get(0).getName()).isEqualTo("All");
        List<OutputBarChartEntry> series = data.get(0).getSeries();
        softly.assertThat(series.size()).isEqualTo(5);
        series.stream().forEach(s -> {
            softly.assertThat(Arrays.asList("Undetermined", "Primary Ischemic Stroke", "(Empty)",
                    "Primary Intracranial Hemorrhage", "Transient Ischaemic Attack (TIA)")).contains(s.getCategory());
            softly.assertThat(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8)).contains(s.getRank());

        });
        softly.assertThat(series.get(0).getTotalSubjects()).isEqualTo(5);
        softly.assertThat(series.get(0).getValue()).isEqualTo(5.0);
    }

    @Test
    public void testGetBarChartWhenColorByIsTraumatic() {
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Cerebrovascular, CerebrovascularGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, CerebrovascularGroupByOptions.TRAUMATIC.getGroupByOptionAndParams());
        settings.withOption(COLOR_BY, CerebrovascularGroupByOptions.TRAUMATIC.getGroupByOptionAndParams());
        //final ChartGroupByOptions.GroupByOptionAndParams<CerebrovascularGroupByOptions> trellis = TRAUMATIC.getGroupByOptionAndParams();
        //settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Cerebrovascular, CerebrovascularGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
        //settingsWithFilterBy.withFilterByTrellisOption(TRAUMATIC, Arrays.asList("Yes", "No", "(Empty)"));
        //When
        List<TrellisedBarChart<Cerebrovascular, CerebrovascularGroupByOptions>> result
                = cerebrovascularService.getBarChart(DUMMY_ACUITY_DATASETS, settingsWithFilterBy.build(),
                CerebrovascularFilters.empty(), PopulationFilters.empty(), COUNT_OF_EVENTS);

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
    public void testGetSelectionDetailsNoTrellis() {
        //Given
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Cerebrovascular, CerebrovascularGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, CerebrovascularGroupByOptions.TRAUMATIC.getGroupByOptionAndParams());
        settings.withOption(COLOR_BY, CerebrovascularGroupByOptions.EVENT_TYPE.getGroupByOptionAndParams());
        final HashMap<CerebrovascularGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(COLOR_BY, "Undetermined");
        selectedItems.put(X_AXIS, "No");
        //When
        SelectionDetail result = cerebrovascularService.getSelectionDetails(DUMMY_ACUITY_DATASETS,
                new CerebrovascularFilters(), new PopulationFilters(), ChartSelection.of(settings.build(), Collections.singletonList(
                        ChartSelectionItem.of(selectedTrellises, selectedItems)
                )));

        //Then
        softly.assertThat(result.getEventIds().size()).isEqualTo(5);
        softly.assertThat(result.getSubjectIds().size()).isEqualTo(5);
    }

    @Test
    public void testGetSubjects() {
        //Given
        CerebrovascularFilters filters = new CerebrovascularFilters();
        filters.setEventType(new SetFilter<>(newHashSet("Primary Intracranial Hemorrhage")));

        //When
        List<String> result = cerebrovascularService.getSubjects(DUMMY_ACUITY_DATASETS, filters, PopulationFilters.empty());

        //Then
        softly.assertThat(result.size()).isEqualTo(5);
    }

    @Test
    public void shouldGetLineBarChartBinnedByDaysSinsFirstDose() {
/*
        Set<TrellisOptions<CerebrovascularGroupByOptions>> trellis = newHashSet(cerebrovascularService.getTrellisOptions(DUMMY_CVOT_DATASETS,
                CerebrovascularFilters.empty(), PopulationFilters.empty()).stream()
                .filter((TrellisOptions o) -> o.getTrellisedBy().equals(CerebrovascularGroupByOptions.TRAUMATIC))
                .findFirst().get());
        
        System.out.println(trellis);

        List<TrellisedOvertime<CerebrovascularGroupByOptions>> overtime = cerebrovascularService.getLineBarChart(DUMMY_CVOT_DATASETS,
                trellis, CerebrovascularFilters.empty(), PopulationFilters.empty(),
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build());

        OutputOvertimeData chart = overtime.get(0).getData();

        List<Integer> ranks = IntStream.rangeClosed(1, chart.getCategories().size()).boxed().collect(Collectors.toList());
        
        softly.assertThat(chart.getCategories()).hasSize(265);
        softly.assertThat(chart.getCategories()).startsWith("-787");
        softly.assertThat(chart.getCategories()).endsWith("-523");
        
        
        System.out.println(chart.getSeries().get(1));
        softly.assertThat(chart.getSeries()).hasSize(3);
        softly.assertThat(chart.getSeries()).extracting(OutputBarChartData::getName).containsExactly("(Empty)", "Yes", "No");

        softly.assertThat(chart.getSeries().get(0).getSeries()).contains(new OutputBarChartEntry("-772", 16, 1.0, 1));

        softly.assertThat(chart.getSeries().get(1).getCategories()).startsWith("-787").endsWith("-523");
        softly.assertThat(chart.getSeries().get(1).getSeries()).contains(
                new OutputBarChartEntry("-787", 1, 1.0, 1),
                new OutputBarChartEntry("-780", 8, 1.0, 1));

        List<OutputBarChartEntry> line = chart.getLines().get(0).getSeries();
        softly.assertThat(line).hasSize(265);
        softly.assertThat(line.stream().map(OutputBarChartEntry::getRank).collect(Collectors.toList()))
                .containsExactlyElementsOf(ranks);
        softly.assertThat(line).extracting(OutputBarChartEntry::getCategory).startsWith("-787").endsWith("-523");
        softly.assertThat(line).extracting(OutputBarChartEntry::getValue).containsOnly(124.0);*/
    }
}
