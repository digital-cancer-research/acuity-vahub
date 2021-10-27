package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.CvotEndpointFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.event.CvotEndpointService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CvotEndpointGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputOvertimeData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.COUNT_OF_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static jersey.repackaged.com.google.common.collect.Sets.newHashSet;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityCvotEndpointServiceITCase {
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private CvotEndpointService cvotEndpointService;

    @Test
    public void shouldGetBarChartWhenColorByIsAll() {
        //Given
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, CvotEndpointGroupByOptions.CATEGORY_3.getGroupByOptionAndParams());
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
        //When
        List<TrellisedBarChart<CvotEndpoint, CvotEndpointGroupByOptions>> result =
                cvotEndpointService.getBarChart(DUMMY_ACUITY_DATASETS, settingsWithFilterBy.build(), CvotEndpointFilters.empty(),
                        PopulationFilters.empty(), COUNT_OF_SUBJECTS);

        //Then
        softly.assertThat(result.size()).isEqualTo(1);
        List<? extends OutputBarChartData> data = result.get(0).getData();
        softly.assertThat(data.size()).isEqualTo(1);
        softly.assertThat(data.get(0).getCategories().size()).isEqualTo(4);
        softly.assertThat(data.get(0).getName()).isEqualTo("All");
        List<OutputBarChartEntry> series = data.get(0).getSeries();
        softly.assertThat(series.size()).isEqualTo(4);
        softly.assertThat(series.get(0).getTotalSubjects()).isEqualTo(11);
        softly.assertThat(series.get(0).getValue()).isEqualTo(11.0);
        softly.assertThat(series.get(0).getRank()).isEqualTo(1);
    }

    @Test
    public void shouldGetLineBarChartBinnedByDaysSinsFirstDose() {
        //Given
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, CvotEndpointGroupByOptions.DESCRIPTION_3.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, new ChartGroupByOptions.GroupByOptionAndParams<>(CvotEndpointGroupByOptions.START_DATE,
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 5)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE).build()
        ));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        //When
        List<TrellisedOvertime<CvotEndpoint, CvotEndpointGroupByOptions>> overtime = cvotEndpointService.getLineBarChart(DUMMY_ACUITY_DATASETS,
                settingsWithFilterBy.build(), CvotEndpointFilters.empty(), PopulationFilters.empty());

        //Then
        OutputOvertimeData chart = overtime.get(0).getData();

        List<Integer> ranks = IntStream.rangeClosed(1, chart.getCategories().size()).boxed().collect(Collectors.toList());

        softly.assertThat(chart.getCategories()).hasSize(303);
        softly.assertThat(chart.getCategories()).startsWith("-235 - -231");
        softly.assertThat(chart.getCategories()).endsWith("1275 - 1279");
        softly.assertThat(chart.getSeries()).hasSize(3);
        softly.assertThat(chart.getSeries()).extracting(OutputBarChartData::getName).containsExactly("A", "B", "C");

        softly.assertThat(chart.getSeries().get(2).getSeries()).contains(new OutputBarChartEntry("90 - 94", 66, 1.0, 1));

        softly.assertThat(chart.getSeries().get(0).getSeries()).contains(
                new OutputBarChartEntry("-235 - -231", 1, 1.0, 1),
                new OutputBarChartEntry("1200 - 1204", 288, 1.0, 1)
        );


        List<OutputBarChartEntry> line = chart.getLines().get(0).getSeries();
        softly.assertThat(line).hasSize(303);
        softly.assertThat(line.stream().map(OutputBarChartEntry::getRank).collect(Collectors.toList()))
                .containsExactlyElementsOf(ranks);
        softly.assertThat(line.get(0).getCategory()).isEqualTo("-235 - -231");
        softly.assertThat(line.get(0).getValue()).isEqualTo(124.0);
        softly.assertThat(line.get(1).getCategory()).isEqualTo("-230 - -226");
        softly.assertThat(line.get(1).getValue()).isEqualTo(124.0);

        softly.assertThat(chart.getSeries().stream().flatMap(s -> s.getSeries().stream())
                .filter(e -> e.getCategory().equals("90 - 94"))
                .mapToDouble(OutputBarChartEntry::getTotalSubjects).sum()).isEqualTo(3.0);
    }


    @Test
    public void shouldGetSubjects() {
        //Given
        CvotEndpointFilters filters = new CvotEndpointFilters();
        filters.setCategory3(new SetFilter<>(newHashSet("BLUE CATEGORY")));

        //When
        List<String> result = cvotEndpointService.getSubjects(DUMMY_ACUITY_DATASETS, filters, PopulationFilters.empty());

        //Then
        softly.assertThat(result.size()).isEqualTo(8);
    }


    @Test
    public void shouldGetLineBarChartBinnedByFirstDateEmptyTrellises() {
        //Given
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, new ChartGroupByOptions.GroupByOptionAndParams<>(CvotEndpointGroupByOptions.START_DATE,
                GroupByOption.Params.builder().with(GroupByOption.Param.BIN_SIZE, 50)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DATE).build()
        ));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        //When
        List<TrellisedOvertime<CvotEndpoint, CvotEndpointGroupByOptions>> overtime = cvotEndpointService.getLineBarChart(DUMMY_ACUITY_DATASETS,
                settingsWithFilterBy.build(), CvotEndpointFilters.empty(), PopulationFilters.empty());

        //Then

        OutputOvertimeData chart = overtime.get(0).getData();

        List<Integer> ranks = IntStream.rangeClosed(1, chart.getCategories().size()).boxed().collect(Collectors.toList());

        softly.assertThat(chart.getCategories()).hasSize(30);
        softly.assertThat(chart.getCategories()).startsWith("2014-08-18 - 2014-10-06");
        softly.assertThat(chart.getCategories()).endsWith("2018-08-07 - 2018-09-25");
        softly.assertThat(chart.getSeries()).hasSize(1);
        softly.assertThat(chart.getSeries().get(0).getSeries()).hasSize(19);

        List<OutputBarChartEntry> line = chart.getLines().get(0).getSeries();
        softly.assertThat(line).hasSize(30);
        softly.assertThat(line.stream().map(OutputBarChartEntry::getRank).collect(Collectors.toList()))
                .containsExactlyElementsOf(ranks);
        softly.assertThat(line).extracting(OutputBarChartEntry::getCategory).startsWith("2014-08-18 - 2014-10-06");
        softly.assertThat(line).extracting(OutputBarChartEntry::getCategory).endsWith("2018-08-07 - 2018-09-25");
        softly.assertThat(line).extracting(OutputBarChartEntry::getValue).contains(124., 122., 118., 112., 108.,
                102., 94., 92., 84., 74., 70., 67., 64., 62., 59.);
    }

    @Test
    public void shouldGetSelectionDetails() {
        //Given
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, CvotEndpointGroupByOptions.CATEGORY_3.getGroupByOptionAndParams());
        settings.withOption(COLOR_BY, CvotEndpointGroupByOptions.TERM.getGroupByOptionAndParams());
        //When
        final HashMap<CvotEndpointGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(COLOR_BY, "TMI");
        selectedItems.put(X_AXIS, "BLUE CATEGORY");

        //When
        SelectionDetail result = cvotEndpointService.getSelectionDetails(DUMMY_ACUITY_DATASETS,
                new CvotEndpointFilters(), new PopulationFilters(), ChartSelection.of(settings.build(), Collections.singletonList(
                        ChartSelectionItem.of(selectedTrellises, selectedItems)
                )));

        //Then
        softly.assertThat(result.getEventIds().size()).isEqualTo(2);
        softly.assertThat(result.getSubjectIds().size()).isEqualTo(2);
    }

    @Test
    public void shouldGetLineBarChartSelection() {
        //Given
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<CvotEndpoint, CvotEndpointGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, new ChartGroupByOptions.GroupByOptionAndParams<>(CvotEndpointGroupByOptions.START_DATE, GroupByOption.Params.builder()
                .with(GroupByOption.Param.BIN_SIZE, 10)
                .with(GroupByOption.Param.BIN_INCL_DURATION, false).build()));
        settings.withOption(COLOR_BY, CvotEndpointGroupByOptions.CATEGORY_3.getGroupByOptionAndParams());
        final HashMap<CvotEndpointGroupByOptions, Object> selectedTrellises = new HashMap<>();
        Collection<ChartSelectionItem<CvotEndpoint, CvotEndpointGroupByOptions>> selectedItemsList = new ArrayList<>();

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems1 = new HashMap<>();
        selectedItems1.put(COLOR_BY, "GREEN CATEGORY");
        selectedItems1.put(X_AXIS, "2015-09-12 - 2015-09-21");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems2 = new HashMap<>();
        selectedItems2.put(COLOR_BY, "RED CATEGORY");
        selectedItems2.put(X_AXIS, "2016-06-08 - 2015-06-17");

        selectedItemsList.add(ChartSelectionItem.of(selectedTrellises, selectedItems1));
        selectedItemsList.add(ChartSelectionItem.of(selectedTrellises, selectedItems2));

        //When
        SelectionDetail selectionDetail = cvotEndpointService.getSelectionDetails(DUMMY_ACUITY_DATASETS,
                CvotEndpointFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(settings.build(), selectedItemsList));

        //Then
        softly.assertThat(selectionDetail.getTotalEvents()).isEqualTo(41);
        softly.assertThat(selectionDetail.getTotalSubjects()).isEqualTo(124);
        softly.assertThat(selectionDetail.getEventIds()).hasSize(2);
        softly.assertThat(selectionDetail.getSubjectIds()).hasSize(2);
    }
}
