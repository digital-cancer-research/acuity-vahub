package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.event.LabService;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.BoxPlotTests;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.LabTests;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.RangeChartTests;
import com.acuity.visualisations.rawdatamodel.suites.interfaces.ShiftPlotTests;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LabGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputRangeChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.RangeChartSeries;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedShiftPlot;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.groups.Tuple;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
@Category(LabTests.class)
public class AcuityLabServiceITCase {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private LabService labService;

    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotData() {
        // Given

        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();

        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .build();
        // When
        List<TrellisedBoxPlot<Lab, LabGroupByOptions>> result = labService.getBoxPlot(
                DUMMY_ACUITY_DATASETS, settingsFiltered, LabFilters.empty(), PopulationFilters.empty());

        // Then
        assertThat(result).hasSize(83);
    }

    /**
     * IMPORTANT: The technique used in the MyBatis SQL, calculated the upper whisker to be the value less than
     * upperQuartile + (1.5 * IQR). When it should be the value less than OR EQUAL TO. Hence there are differences
     * in the way the whiskers are calculated. See here for a description of the algorithm:
     * https://graphpad.com/support/faq/five-ways-to-plot-whiskers-in-box-and-whisker-plots/
     */
    @Test
    @Category(BoxPlotTests.class)
    public void shouldUseTheCorrectPercentileMethod() {
        // Given
        LabFilters labFilters = new LabFilters();
        labFilters.setLabcode(new SetFilter<>(newArrayList("Gamma-Glutamyltransferase")));
        labFilters.setLabUnit(new SetFilter<>(newArrayList("U/L")));

        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();

        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .build();

        // When
        List<TrellisedBoxPlot<Lab, LabGroupByOptions>> result = labService.getBoxPlot(
                DUMMY_ACUITY_DATASETS, settingsFiltered, labFilters, PopulationFilters.empty());

        // Then
        assertThat(result.get(0).getStats())
                .filteredOn(stat -> stat.getX().equals("34"))
                .extracting("lowerWhisker", "lowerQuartile", "median", "upperQuartile", "upperWhisker")
                .contains(tuple(18.0, 21.22, 25.0, 88.5, 176.0));
    }

    // Test taken from SQL implementation
    @Test
    @Category(BoxPlotTests.class)
    public void shouldGetBoxPlotDataRegressionTest() {
        // Given

        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ABSOLUTE_CHANGE_FROM_BASELINE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();

        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(LabGroupByOptions.MEASUREMENT, "Alanine Aminotransferase (U/L)")
                .build();

        // When
        List<TrellisedBoxPlot<Lab, LabGroupByOptions>> result = labService.getBoxPlot(
                DUMMY_ACUITY_DATASETS, settingsFiltered, LabFilters.empty(), PopulationFilters.empty());

        // Then
        softly.assertThat(result.get(0).getStats()).hasSize(112);
        softly.assertThat(result.get(0).getStats()).extracting("lowerQuartile").startsWith(0.0).endsWith(12.);
        softly.assertThat(result.get(0).getStats()).extracting("median").startsWith(0.0).endsWith(12.);
        softly.assertThat(result.get(0).getStats()).extracting("upperQuartile").startsWith(0.0).endsWith(12.);
    }

    @Test
    @Category(ShiftPlotTests.class)
    public void shouldGetShiftPlotData() {
        // Given
        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.BASELINE_VALUE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.ARM.getGroupByOptionAndParams())
                .build();

        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .build();
        // When
        List<TrellisedShiftPlot<Lab, LabGroupByOptions>> result = labService.getShiftPlot(
                DUMMY_ACUITY_DATASETS, settingsFiltered, LabFilters.empty(), PopulationFilters.empty());

        // Then
        assertThat(result).hasSize(83);
    }

    @Category(RangeChartTests.class)
    @Test
    // This is a pseudo regression test because only one test existed in SQL and there was a bug where it ignored the units when trellising. The assertions
    // are therefore different
    public void shouldGetRangePlotPseudoRegressionTest() {
        // Given

        ChartGroupByOptions<Lab, LabGroupByOptions> settings = ChartGroupByOptions.<Lab, LabGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, LabGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, LabGroupByOptions.PERCENTAGE_CHANGE_FROM_BASELINE.getGroupByOptionAndParams())
                .withTrellisOption(LabGroupByOptions.MEASUREMENT.getGroupByOptionAndParams())
                .build();

        final ChartGroupByOptionsFiltered<Lab, LabGroupByOptions> settingsFiltered = ChartGroupByOptionsFiltered.builder(settings)
                .withFilterByTrellisOption(LabGroupByOptions.MEASUREMENT, "Alanine Aminotransferase (% change)")
                .build();
        // When
        List<TrellisedRangePlot<Lab, LabGroupByOptions>> result = labService.getRangePlot(
                DUMMY_ACUITY_DATASETS, settingsFiltered, LabFilters.empty(), PopulationFilters.empty(), StatType.MEDIAN);

        // Then
        List<OutputRangeChartEntry> flattenedResult = result.get(0).getData().stream()
                //.filter(s -> s.getName().equals("Alanine Aminotransferase (U/L)"))
                .map(RangeChartSeries::getData)
                .findFirst()
                .get();
        softly.assertThat(flattenedResult).hasSize(115);
        softly.assertThat(flattenedResult).extracting("dataPoints", "y", "stdErr", "min", "max")
                .startsWith(
                        Tuple.tuple(54, 0.0, 2.28, -60.34, 39.58),
                        Tuple.tuple(1, 0.0, 0.0, 0.0, 0.0)
                )
                .endsWith(Tuple.tuple(1, 85.71, 0.0, 85.71, 85.71));
    }
}

