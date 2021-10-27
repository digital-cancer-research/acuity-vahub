package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.ExposureFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.service.event.ExposureService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.TrellisedLineFloatChart;
import com.acuity.visualisations.rawdatamodel.vo.exposure.ExposureTooltip;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.NAME;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.ORDER_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityExposureServiceITCase {
    @Autowired
    private ExposureService exposureService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldGetAverageExposuresForLinePlotWhenEmptyFilters() throws Exception {

        //When
        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> linecharts = exposureService.getLineChart(DUMMY_2_ACUITY_DATASETS,
                getDefaultExposureSettings(), ExposureFilters.empty(), PopulationFilters.empty());
        TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, ? extends OutputLineChartData> linechart = linecharts.get(0);

        //Then
        assertThat(linechart.getData().size()).isEqualTo(898);
        List<OutputLineChartData> data = linechart.getData().stream().filter(d -> d.getSeriesBy().toString()
                .startsWith("E0000100218")).collect(Collectors.toList());
        assertThat(data).hasSize(8);
        assertThat(data).filteredOn(d -> d.getSeriesBy().toString()
                .equals("E0000100218, Cycle 1, AZD1234, drug administration date 2014-12-08"))
                .flatExtracting(OutputLineChartData::getSeries)
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY).containsExactly(
                tuple(0.0, 5.9),
                tuple(0.5, 98.55),
                tuple(1.0, 123.43),
                tuple(2.0, 85.17),
                tuple(4.0, 51.02),
                tuple(8.0, 34.34),
                tuple(12.0, 21.64),
                tuple(16.0, 16.8),
                tuple(24.0, 5.44),
                tuple(48.0, 4.9)
        );

    }

    private static ChartGroupByOptions<Exposure, ExposureGroupByOptions> getDefaultExposureSettings() {
        return ChartGroupByOptions.<Exposure, ExposureGroupByOptions>builder()
                .withOption(SERIES_BY, ExposureGroupByOptions.SUBJECT_CYCLE.getGroupByOptionAndParams())
                .withOption(COLOR_BY, ExposureGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .withOption(NAME, ExposureGroupByOptions.ALL_INFO.getGroupByOptionAndParams())
                .withOption(X_AXIS, ExposureGroupByOptions.TIME_FROM_ADMINISTRATION.getGroupByOptionAndParams())
                .withOption(Y_AXIS, ExposureGroupByOptions.ANALYTE_CONCENTRATION.getGroupByOptionAndParams())
                .withOption(ORDER_BY, ExposureGroupByOptions.TIME_FROM_ADMINISTRATION.getGroupByOptionAndParams())
                .build();
    }

    @Test
    public void shouldGetExposuresForLinePlotWithExposureFilter() {
        ExposureFilters filters = new ExposureFilters();
        filters.setAnalyteConcentration(new RangeFilter<>(3.0, 5.17));
        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> linecharts =
                exposureService.getLineChart(DUMMY_2_ACUITY_DATASETS, getDefaultExposureSettings(), filters,
                        PopulationFilters.empty());
        TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, ? extends OutputLineChartData> linechart = linecharts.get(0);

        assertThat(linechart.getData().size()).isEqualTo(218);
        List<Number> concentrations = linechart.getData().stream()
                .flatMap(l -> l.getSeries().stream())
                .map(l -> (Number) l.getY())
                .collect(Collectors.toList());

        for (int i = 0; i < concentrations.size(); i++) {
            assertThat(3.0 <= concentrations.get(i).doubleValue()).isTrue();
            assertThat(5.17 >= concentrations.get(i).doubleValue()).isTrue();
        }
    }

    @Test
    public void shouldGetExposuresSelection() {
        final HashMap<ExposureGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem1 = new HashMap<>();
        selectedItem1.put(SERIES_BY, "E0000100240, Cycle 0, X9876, drug administration date 2014-11-18");
        selectedItem1.put(X_AXIS, "48.0");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem2 = new HashMap<>();
        selectedItem2.put(SERIES_BY, "E0000100240, Cycle 2, X9876, drug administration date 2014-12-16");
        selectedItem2.put(X_AXIS, "1.0");

        SelectionDetail selectionDetails = exposureService.getSelectionDetails(DUMMY_2_ACUITY_DATASETS,
                ExposureFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(getDefaultExposureSettings().limitedBySettings(SERIES_BY, X_AXIS),
                        newArrayList(ChartSelectionItem.of(selectedTrellises, selectedItem1),
                                ChartSelectionItem.of(selectedTrellises, selectedItem2))));

        softly.assertThat(selectionDetails.getEventIds().size()).isEqualTo(2);
        softly.assertThat(selectionDetails.getSubjectIds().size()).isEqualTo(1);
    }

    @Test
    public void shouldGetExposuresForLinePlotWithPopulationsFilter() {
        PopulationFilters filters = new PopulationFilters();
        filters.setAge(new RangeFilter<>(20, 30));
        List<TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, OutputLineChartData>> linecharts =
                exposureService.getLineChart(DUMMY_2_ACUITY_DATASETS, getDefaultExposureSettings(), ExposureFilters.empty(), filters);
        TrellisedLineFloatChart<Exposure, ExposureGroupByOptions, ? extends OutputLineChartData> linechart = linecharts.get(0);

        assertThat(linechart.getData()).hasSize(162);
        assertThat(linechart.getData().stream().flatMap(e -> e.getSeries().stream())
                .map(e -> ((ExposureTooltip) e.getName()).getColorByValue()).distinct().collect(Collectors.toList())).containsExactlyInAnyOrder(
                "E000010043",
                "E0000100255",
                "E0000100296",
                "E0000100242",
                "E0000100183",
                "E000010045",
                "E0000100276",
                "E0000100271",
                "E0000100151",
                "E0000100125",
                "E000010081",
                "E0000100105",
                "E000010025",
                "E00001005",
                "E0000100287",
                "E0000100185",
                "E0000100274",
                "E0000100107",
                "E0000100207",
                "E000010096",
                "E000010077",
                "E000010079"
        );
    }

}
