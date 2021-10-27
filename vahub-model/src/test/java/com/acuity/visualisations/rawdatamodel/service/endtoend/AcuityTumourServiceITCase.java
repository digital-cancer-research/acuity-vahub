package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.filters.AssessedTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.RangeFilter;
import com.acuity.visualisations.rawdatamodel.filters.TherapyFilters;
import com.acuity.visualisations.rawdatamodel.service.event.TumourColumnRangeService;
import com.acuity.visualisations.rawdatamodel.service.event.TumourLineChartService;
import com.acuity.visualisations.rawdatamodel.service.event.TumourWaterfallService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ATLGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AssessmentAxisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.Bin;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.IntBin;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.TumourTherapyGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TumourTherapy;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputColumnRangeChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputColumnRangeChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputWaterfallEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedColumnRangeChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedWaterfallChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.TrellisedLineFloatChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.GRAY;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.RED;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AssessmentAxisOptions.AssessmentType.BEST_CHANGE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.TumourTherapyGroupByOptions.MOST_RECENT_THERAPY;
import static org.assertj.core.groups.Tuple.tuple;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityTumourServiceITCase {
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private TumourWaterfallService tumourWaterfallService;
    @Autowired
    private TumourLineChartService tumourLineChartService;
    @Autowired
    private TumourColumnRangeService tumourColumnRangeService;

    @Test
    public void shouldGetTumoursBestChangeOnWaterfallWhenEmptyFilters() {
        List<TrellisedWaterfallChart<AssessedTargetLesion, ATLGroupByOptions>> waterfallChart =
                tumourWaterfallService.getTumourDataOnWaterfall(DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(), PopulationFilters.empty(),
                        getDefaultWaterfallSettingsFiltered());

        softly.assertThat(waterfallChart).hasSize(1);
        OutputWaterfallEntry entry = waterfallChart.get(0).getData().getEntries().get(0);
        softly.assertThat(waterfallChart.get(0).getData().getXCategories().size()).isEqualTo(78);
        softly.assertThat(waterfallChart.get(0).getData().getEntries().size()).isEqualTo(78);
        softly.assertThat(entry.getName()).isEqualTo("Progressive Disease");
        softly.assertThat(entry.getX()).isEqualTo(0);
        softly.assertThat(entry.getY()).isEqualTo(103.7);
        softly.assertThat(entry.getColor()).isEqualTo(RED.getCode());
        System.out.println(waterfallChart.get(0).getData().getXCategories());
        softly.assertThat(waterfallChart.get(0).getData().getXCategories().get(0)).isEqualTo("E000010084");
    }

    @Test
    public void shouldGetTumoursWeek12OnWaterfall() {
        List<TrellisedWaterfallChart<AssessedTargetLesion, ATLGroupByOptions>> waterfallChart =
                tumourWaterfallService.getTumourDataOnWaterfall(DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(), PopulationFilters.empty(),
                        getWaterfallSettingsFiltered("WEEK"));

        softly.assertThat(waterfallChart).hasSize(1);
        OutputWaterfallEntry entry = waterfallChart.get(0).getData().getEntries().get(0);
        softly.assertThat(waterfallChart.get(0).getData().getXCategories().size()).isEqualTo(47);
        softly.assertThat(waterfallChart.get(0).getData().getEntries().size()).isEqualTo(47);
        softly.assertThat(entry)
                .extracting(OutputWaterfallEntry::getName, OutputWaterfallEntry::getX, OutputWaterfallEntry::getY, OutputWaterfallEntry::getColor)
                .containsExactly("Progressive Disease", 0, 134.09, RED.getCode());
        softly.assertThat(waterfallChart.get(0).getData().getXCategories().get(0)).isEqualTo("E0000100181");
    }

    @Test
    public void testGetAvailableWaterfallYAxis() {

        AssessmentAxisOptions<ATLGroupByOptions> yAxis = tumourWaterfallService.getAvailableWaterfallYAxis(DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(),
                PopulationFilters.empty());
        softly.assertThat(yAxis.getWeeks()).containsExactly(6, 12, 18, 24, 30, 36, 42, 48);
        softly.assertThat(yAxis.getAssessmentTypes()).containsExactlyInAnyOrder(BEST_CHANGE);
        softly.assertThat(yAxis.getOptions()).extracting(AxisOption::getGroupByOption)
                .containsExactly(ATLGroupByOptions.PERCENTAGE_CHANGE);
    }

    @Test
    public void shouldGetTumoursForWaterfallWithDescOrderData() {
        List<TrellisedWaterfallChart<AssessedTargetLesion, ATLGroupByOptions>> waterfallChart =
                tumourWaterfallService.getTumourDataOnWaterfall(DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(), PopulationFilters.empty(),
                        getDefaultWaterfallSettingsFiltered());

        softly.assertThat(waterfallChart).hasSize(1);
        List<OutputWaterfallEntry> entries = waterfallChart.get(0).getData().getEntries();
        for (int i = 1; i < entries.size(); i++) {
            double prev = entries.get(i - 1).getY();
            double current = entries.get(i).getY();
            softly.assertThat(prev >= current).isTrue();
        }
    }

    @Test
    public void shouldGetTumoursForWaterfallWithNonEmptyTumourFilter() {
        AssessedTargetLesionFilters filters = new AssessedTargetLesionFilters();
        filters.setBestPercentageChangeFromBaseline(new RangeFilter<>(-10.0, 10.0));
        List<TrellisedWaterfallChart<AssessedTargetLesion, ATLGroupByOptions>> waterfallChart =
                tumourWaterfallService.getTumourDataOnWaterfall(DUMMY_2_ACUITY_DATASETS, filters, PopulationFilters.empty(), getDefaultWaterfallSettingsFiltered());

        softly.assertThat(waterfallChart).hasSize(1);
        List<OutputWaterfallEntry> entries = waterfallChart.get(0).getData().getEntries();

        softly.assertThat(waterfallChart.get(0).getData().getXCategories().size()).isEqualTo(14);
        softly.assertThat(waterfallChart.get(0).getData().getEntries().size()).isEqualTo(14);
        for (int i = 1; i < entries.size(); i++) {
            double bestPercentage = entries.get(i - 1).getY();
            softly.assertThat(-10.0 <= bestPercentage && bestPercentage <= 10.0).isTrue();
        }
    }

    @Test
    public void shouldGetTumoursForWaterfallWithNonEmptyPopulationFilter() {
        PopulationFilters filters = new PopulationFilters();
        filters.setAge(new RangeFilter<>(20, 30));
        List<TrellisedWaterfallChart<AssessedTargetLesion, ATLGroupByOptions>> waterfallChart =
                tumourWaterfallService.getTumourDataOnWaterfall(DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(), filters,
                        getDefaultWaterfallSettingsFiltered());

        softly.assertThat(waterfallChart).hasSize(1);
        softly.assertThat(waterfallChart.get(0).getData().getXCategories().size()).isEqualTo(17);
        softly.assertThat(waterfallChart.get(0).getData().getEntries().size()).isEqualTo(17);
        softly.assertThat(waterfallChart.get(0).getData().getXCategories().contains("E000010084")).isFalse();
    }

    @Test
    public void shouldGetTumoursForLinechartWhenEmptyFilters() {
        List<TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, OutputLineChartData>> linecharts = tumourLineChartService.getTumourAllChangesOnLinechart(
                DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(), PopulationFilters.empty(), getDefaultLinechartSettingsFiltered());

        final TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, ? extends OutputLineChartData> trellisedLineCharts = linecharts.get(0);
        softly.assertThat(trellisedLineCharts.getTrellisedBy().size()).isEqualTo(0);
        softly.assertThat(trellisedLineCharts.getData().size()).isEqualTo(78);

        OutputLineChartData lineChartData = trellisedLineCharts.getData().stream().filter(d -> "E000010081".equals(d.getSeriesBy())).findAny().get();

        softly.assertThat(lineChartData.getSeries()).extracting(OutputLineChartEntry::getColor).containsOnly(GRAY.getCode(), RED.getCode());
        softly.assertThat(lineChartData.getSeries())
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY, OutputLineChartEntry::getName, OutputLineChartEntry::getColor)
                .containsExactly(
                        tuple(Bin.newInstance(-33, 1), 0.0d, "Stable Disease", "#808080"),
                        tuple(Bin.newInstance(16, 1), -5.21d, "Stable Disease", "#808080"),
                        tuple(Bin.newInstance(46, 1), 17.71d, "Stable Disease", "#808080"),
                        tuple(Bin.newInstance(91, 1), 57.29d, "Progressive Disease", "#FF0000"),
                        tuple(Bin.newInstance(134, 1), 73.96d, "Progressive Disease", "#FF0000")
                );
    }

    @Test
    public void shouldGetAbsoluteSumForLinechartWhenEmptyFilters() {
        List<TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, OutputLineChartData>> linecharts = tumourLineChartService.getTumourAllChangesOnLinechart(
                DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(), PopulationFilters.empty(),
                getLinechartSettingsPatched(getLinechartYAxisSettings(ATLGroupByOptions.ABSOLUTE_SUM)));

        final TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, ? extends OutputLineChartData> trellisedLineCharts = linecharts.get(0);
        softly.assertThat(trellisedLineCharts.getTrellisedBy().size()).isEqualTo(0);
        softly.assertThat(trellisedLineCharts.getData().size()).isEqualTo(79);

        OutputLineChartData lineChartData = trellisedLineCharts.getData().stream().filter(d -> "E000010081".equals(d.getSeriesBy())).findAny().get();

        softly.assertThat(lineChartData.getSeries()).extracting(OutputLineChartEntry::getColor).containsOnly(GRAY.getCode(), RED.getCode());
        softly.assertThat(lineChartData.getSeries())
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY, OutputLineChartEntry::getName, OutputLineChartEntry::getColor)
                .containsExactly(
                        tuple(Bin.newInstance(-33, 1), 96, "Stable Disease", "#808080"),
                        tuple(Bin.newInstance(16, 1), 91, "Stable Disease", "#808080"),
                        tuple(Bin.newInstance(46, 1), 113, "Stable Disease", "#808080"),
                        tuple(Bin.newInstance(91, 1), 151, "Progressive Disease", "#FF0000"),
                        tuple(Bin.newInstance(134, 1), 167, "Progressive Disease", "#FF0000")
                );
    }

    @Test
    public void shouldGetAbsoluteChangesForLinechartWhenEmptyFilters() {
        List<TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, OutputLineChartData>> linecharts = tumourLineChartService.getTumourAllChangesOnLinechart(
                DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(), PopulationFilters.empty(),
                getLinechartSettingsPatched(getLinechartYAxisSettings(ATLGroupByOptions.ABSOLUTE_CHANGE)));

        final TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, ? extends OutputLineChartData> trellisedLineCharts = linecharts.get(0);
        softly.assertThat(trellisedLineCharts.getTrellisedBy().size()).isEqualTo(0);
        softly.assertThat(trellisedLineCharts.getData().size()).isEqualTo(78);

        OutputLineChartData lineChartData = trellisedLineCharts.getData().stream().filter(d -> "E000010081".equals(d.getSeriesBy())).findAny().get();

        softly.assertThat(lineChartData.getSeries()).extracting(OutputLineChartEntry::getColor).containsOnly(GRAY.getCode(), RED.getCode());
        softly.assertThat(lineChartData.getSeries())
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY, OutputLineChartEntry::getName, OutputLineChartEntry::getColor)
                .containsExactly(
                        tuple(Bin.newInstance(-33, 1), 0, "Stable Disease", "#808080"),
                        tuple(Bin.newInstance(16, 1), -5, "Stable Disease", "#808080"),
                        tuple(Bin.newInstance(46, 1), 17, "Stable Disease", "#808080"),
                        tuple(Bin.newInstance(91, 1), 55, "Progressive Disease", "#FF0000"),
                        tuple(Bin.newInstance(134, 1), 71, "Progressive Disease", "#FF0000")
                );
    }

    @Test
    public void shouldGetTumoursForLinechartWithXDataAscOrderedOverTimeForEveryLine() {
        TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, ? extends OutputLineChartData> linecharts = tumourLineChartService.getTumourAllChangesOnLinechart(
                DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(), PopulationFilters.empty(), getDefaultLinechartSettingsFiltered()).get(0);
        List<? extends OutputLineChartData> data = linecharts.getData();
        data.forEach(line -> {
            List<OutputLineChartEntry> series = line.getSeries();
            for (int i = 1; i < series.size(); i++) {
                IntBin prev = (IntBin) series.get(i - 1).getX();
                IntBin current = (IntBin) series.get(i).getX();
                softly.assertThat(current).isGreaterThan(prev);
            }
        });
    }

    @Test
    public void shouldGetFilteredTumoursForLinechartWithNonEmptyTumourFilter() {
        AssessedTargetLesionFilters filters = new AssessedTargetLesionFilters();
        filters.setBestPercentageChangeFromBaseline(new RangeFilter<>(-10.0, 10.0));
        TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, ? extends OutputLineChartData> linecharts = tumourLineChartService.getTumourAllChangesOnLinechart(
                DUMMY_2_ACUITY_DATASETS, filters, PopulationFilters.empty(), getDefaultLinechartSettingsFiltered()).get(0);

        softly.assertThat(linecharts.getData().size()).isEqualTo(14);
        softly.assertThat(linecharts.getTrellisedBy().size()).isEqualTo(0);
    }

    @Test
    public void shouldGetFilteredTumoursForLinechartWithNonEmptyPopulationFilter() {
        PopulationFilters filters = new PopulationFilters();
        filters.setAge(new RangeFilter<>(20, 30));
        TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, ? extends OutputLineChartData> linecharts = tumourLineChartService.getTumourAllChangesOnLinechart(
                DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(), filters, getDefaultLinechartSettingsFiltered()).get(0);

        softly.assertThat(linecharts.getData().size()).isEqualTo(17);
        softly.assertThat(linecharts.getTrellisedBy().size()).isEqualTo(0);
    }

    @Test
    public void shouldGetAllTumourTherapyOnColumnRangeWhenEmptyFilters() {

        ChartGroupByOptions<Subject, PopulationGroupByOptions> settings = ChartGroupByOptions.<Subject, PopulationGroupByOptions>builder()
                .build();
        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = ChartGroupByOptions.<TumourTherapy, TumourTherapyGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, MOST_RECENT_THERAPY.getGroupByOptionAndParams())
                .build();

        final List<TrellisedColumnRangeChart<Subject, PopulationGroupByOptions>> rangeChart = tumourColumnRangeService
                .getTumourTherapyOnColumnRange(DUMMY_2_ACUITY_DATASETS, TherapyFilters.empty(), PopulationFilters.empty(),
                        ChartGroupByOptionsFiltered.builder(settings).build(), ChartGroupByOptionsFiltered.builder(therapiesSettings).build());

        softly.assertThat(rangeChart).hasSize(1);
        OutputColumnRangeChartData data = rangeChart.get(0).getData();

        softly.assertThat(rangeChart.get(0).getData().getCategories().size()).isEqualTo(124);
        softly.assertThat(rangeChart.get(0).getData().getData().size()).isEqualTo(246);

        // negative direction
        OutputColumnRangeChartEntry entry1 = data.getData().get(0);
        softly.assertThat(rangeChart.get(0).getData().getCategories().get(0)).isEqualTo("E000010099");
        softly.assertThat(entry1.getX()).isEqualTo(0);
        softly.assertThat(entry1.getHigh()).isEqualTo(-26);
        softly.assertThat(entry1.getLow()).isEqualTo(-45);
        softly.assertThat(entry1.getTherapies()).containsOnly("Radiotherapy");

        // positive direction
        OutputColumnRangeChartEntry entry2 = data.getData().get(1);
        softly.assertThat(entry2.getX()).isEqualTo(0);
        softly.assertThat(entry2.getHigh()).isEqualTo(6);
        softly.assertThat(entry2.getLow()).isEqualTo(0);
        softly.assertThat(entry2.getTherapies()).containsOnly("AZD1234, X9876");
    }

    private ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> getDefaultWaterfallSettingsFiltered() {
        return getWaterfallSettingsFiltered("BEST_CHANGE");
    }

    private ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> getWaterfallSettingsFiltered(String yAxisParam) {
        ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> settings = ChartGroupByOptions.<AssessedTargetLesion, ATLGroupByOptions>builder()
                .withOption(Y_AXIS,
                        ATLGroupByOptions.PERCENTAGE_CHANGE.getGroupByOptionAndParams(GroupByOption.Params.builder()
                                .with(GroupByOption.Param.ASSESSMENT_TYPE, yAxisParam)
                                .with(GroupByOption.Param.WEEK_NUMBER, 12)
                                .build()))
                .withOption(X_AXIS, ATLGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .withOption(COLOR_BY, ATLGroupByOptions.BEST_RESPONSE.getGroupByOptionAndParams())
                .build();
        return ChartGroupByOptionsFiltered.builder(settings).build();
    }

    private ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> getDefaultLinechartSettingsFiltered() {
        return getLinechartSettingsPatched(getLinechartYAxisSettings(ATLGroupByOptions.PERCENTAGE_CHANGE));
    }

    private ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> getLinechartSettingsPatched(ChartGroupByOptionsFiltered<AssessedTargetLesion,
            ATLGroupByOptions> input) {
        ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> settings = ChartGroupByOptions.<AssessedTargetLesion, ATLGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, ATLGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, ATLGroupByOptions.ASSESSMENT_RESPONSE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.NAME, ATLGroupByOptions.ASSESSMENT_RESPONSE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, ATLGroupByOptions.DAYS_SINCE_FIRST_DOSE.getGroupByOptionAndParams(
                        GroupByOption.Params.builder()
                                .with(GroupByOption.Param.BIN_SIZE, 1)
                                .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                                .build()))
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                        input.getSettings().getOptions().getOrDefault(Y_AXIS, ATLGroupByOptions.PERCENTAGE_CHANGE.getGroupByOptionAndParams()))
                .build();
        return ChartGroupByOptionsFiltered.builder(settings).build();
    }

    private ChartGroupByOptionsFiltered<AssessedTargetLesion, ATLGroupByOptions> getLinechartYAxisSettings(ATLGroupByOptions value) {
        ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> settings = ChartGroupByOptions.<AssessedTargetLesion, ATLGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, value.getGroupByOptionAndParams()).build();
        return ChartGroupByOptionsFiltered.builder(settings).build();
    }

    public static class TumourSelectionItem extends ChartSelectionItem<AssessedTargetLesion, ATLGroupByOptions> implements Comparable<TumourSelectionItem> {

        TumourSelectionItem(Map<ATLGroupByOptions, Object> selectedTrellises, Map<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems) {
            super(selectedTrellises, selectedItems);
        }

        @Override
        public int compareTo(TumourSelectionItem that) {
            if (that == null) {
                return -1;
            }

            if (this.equals(that)) {
                return 0;
            }

            return this.hashCode() > that.hashCode() ? 1 : -1;
        }
    }

    @Test
    public void shouldGetSelectionDetailsForLineChart() {
        ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> tldSettings = ChartGroupByOptions.<AssessedTargetLesion, ATLGroupByOptions>builder()
                .withOption(SERIES_BY, ATLGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .withOption(X_AXIS, ATLGroupByOptions.DAYS_SINCE_FIRST_DOSE.getGroupByOptionAndParams(
                        GroupByOption.Params.builder()
                                .with(GroupByOption.Param.BIN_SIZE, 1)
                                .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                                .build()))
                .withOption(Y_AXIS, ATLGroupByOptions.PERCENTAGE_CHANGE.getGroupByOptionAndParams())
                .build();
        Map selectedTrellises = Collections.emptyMap();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem1 = new HashMap<>();
        selectedItem1.put(X_AXIS, 33);
        selectedItem1.put(Y_AXIS, 26.34);
        selectedItem1.put(SERIES_BY, "E0000100209");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem2 = new HashMap<>();
        selectedItem2.put(X_AXIS, 191);
        selectedItem2.put(Y_AXIS, 123.53);
        selectedItem2.put(SERIES_BY, "E000010096");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem3 = new HashMap<>();
        selectedItem3.put(X_AXIS, 229);
        selectedItem3.put(Y_AXIS, 177.94);
        selectedItem3.put(SERIES_BY, "E000010096");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem4 = new HashMap<>();
        selectedItem4.put(X_AXIS, -6);
        selectedItem4.put(Y_AXIS, 0.0);
        selectedItem4.put(SERIES_BY, "E000010072");

        List<ChartSelectionItem<AssessedTargetLesion, ATLGroupByOptions>> items = new ArrayList<>();
        items.add(ChartSelectionItem.of(selectedTrellises, selectedItem1));
        items.add(ChartSelectionItem.of(selectedTrellises, selectedItem2));
        items.add(ChartSelectionItem.of(selectedTrellises, selectedItem3));
        items.add(ChartSelectionItem.of(selectedTrellises, selectedItem4));
        SelectionDetail selectionDetails = tumourLineChartService.getLineChartSelectionDetails(DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(tldSettings, items));

        softly.assertThat(selectionDetails.getSubjectIds()).hasSize(3);
        softly.assertThat(selectionDetails.getEventIds()).hasSize(4);
        softly.assertThat(selectionDetails.getTotalSubjects()).isEqualTo(124);
        softly.assertThat(selectionDetails.getTotalEvents()).isEqualTo(356);
    }
}
