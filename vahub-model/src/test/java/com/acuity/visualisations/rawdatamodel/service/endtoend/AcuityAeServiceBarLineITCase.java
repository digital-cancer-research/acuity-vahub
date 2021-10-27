package com.acuity.visualisations.rawdatamodel.service.endtoend;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.vo.AeDetailLevel;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.AeGroupByOptions.OVERTIME_DURATION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class AcuityAeServiceBarLineITCase {

    private static final Logger LOG = LoggerFactory.getLogger(AcuityAeServiceBarLineITCase.class);
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private AeService aeService;

    @Test
    public void shouldGetXAxisOptions() {

        AxisOptions<AeGroupByOptions> result = aeService.getAvailableOverTimeChartXAxis(DUMMY_ACUITY_DATASETS, AeFilters.empty(), PopulationFilters.empty());
        assertThat(result.getOptions()).extracting(AxisOption::getGroupByOption).containsOnly(OVERTIME_DURATION);
    }

    @Test
    public void shouldGetSelectionInChartWithAllSubjects() {
       // SelectionBox box = new SelectionBox(19.6, 28.48, 60.0, 142.7);
        AeFilters aesFilters = new AeFilters();
        aesFilters.setAeDetailLevel(AeDetailLevel.PER_SEVERITY_CHANGE);

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, AeGroupByOptions.OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_INCL_DURATION, true)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()));
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        final HashMap<AeGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(COLOR_BY, "CTC Grade 1");
        selectedItems.put(X_AXIS, "300");


        final SelectionDetail selectionDetails = aeService
                .getSelectionDetails(DUMMY_ACUITY_DATASETS, aesFilters, PopulationFilters.empty(), ChartSelection.of(settings.build(), Collections.singletonList(
                        ChartSelectionItem.of(selectedTrellises, selectedItems)
                )));

        softly.assertThat(selectionDetails.getTotalSubjects()).isEqualTo(124);
    }

    @Test
    public void shouldGetAllSubjectsCountedInFirstBinWithAesFilterIrrespectiveOfTheAesFilter() {
        AeFilters aeFilters = new AeFilters();
        aeFilters.setPt(new SetFilter(newArrayList("RHINALGIA")));
        aeFilters.setAeDetailLevel(AeDetailLevel.PER_INCIDENCE);


        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_INCL_DURATION, true)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.WEEKS_SINCE_FIRST_DOSE)
                        .build()));

        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        List<TrellisedOvertime<Ae, AeGroupByOptions>> overtime = aeService.getLineBarChart(DUMMY_ACUITY_DATASETS,
                settingsWithFilterBy.build(), aeFilters, PopulationFilters.empty());

        softly.assertThat(overtime).hasSize(1);
        softly.assertThat(overtime.get(0).getData().getLines().get(0).getSeries()).filteredOn("rank", 1).extracting("value").contains(115.0);
    }

    @Test
    public void shouldGetTrellisOptionsForFromStartDate() {

        List<TrellisOptions<AeGroupByOptions>> trellisOptions = aeService.getTrellisOptions(DUMMY_ACUITY_DATASETS, AeFilters.empty(), PopulationFilters.empty());

        softly.assertThat(trellisOptions).hasSize(0);
    }

    @Test
    public void shouldGetBinnedStartDatesByGradeForSingleSubjectSingleEventType() {
        //Given
        AeFilters aeFilters = new AeFilters();
        aeFilters.setPt(new SetFilter<>(newArrayList("RHINALGIA")));
        aeFilters.setAeDetailLevel(AeDetailLevel.PER_INCIDENCE);

        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(newArrayList("E0000100232")));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_INCL_DURATION, false)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()));

        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        //When
        List<TrellisedOvertime<Ae, AeGroupByOptions>> overtime = aeService.getLineBarChart(DUMMY_ACUITY_DATASETS,
                settingsWithFilterBy.build(), aeFilters, PopulationFilters.empty());

        //Then
        softly.assertThat(overtime).hasSize(1);
        softly.assertThat(overtime.get(0).getData().getSeries()).hasSize(3);
        softly.assertThat(overtime.get(0).getData().getSeries().get(2).getCategories()).hasSize(85);
        softly.assertThat(overtime.get(0).getData().getSeries().get(1).getCategories()).hasSize(85);
        softly.assertThat(overtime.get(0).getData().getSeries().get(0).getCategories()).hasSize(85);
    }
//

    @Test
    public void shouldCountSubjectTotalBeforeDayZeroAsTheSameAsDayZero() {
        AeFilters aeFilters = new AeFilters();
        aeFilters.setAeDetailLevel(AeDetailLevel.PER_SEVERITY_CHANGE);

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_INCL_DURATION, true)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.WEEKS_SINCE_RANDOMISATION)
                        .build()));

        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());


        List<TrellisedOvertime<Ae, AeGroupByOptions>> overtime = aeService.getLineBarChart(DUMMY_ACUITY_DATASETS,
                settingsWithFilterBy.build(), aeFilters, PopulationFilters.empty());

        softly.assertThat(overtime).hasSize(1);
        softly.assertThat(overtime.get(0).getData().getLines().get(0).getSeries()).filteredOn("rank", 1).extracting("value").contains(116.0);
    }

    @Test
    public void shouldGetTimePeriodsIgnoringTimeComponent() {
        //Given
        PopulationFilters populationFilters = new PopulationFilters();
        populationFilters.setSubjectId(new SetFilter<>(newArrayList("E0000100174")));

        AeFilters aeFilters = new AeFilters();
        aeFilters.setAeDetailLevel(AeDetailLevel.PER_INCIDENCE);

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_INCL_DURATION, false)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.WEEKS_SINCE_FIRST_DOSE)
                        .build()));

        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());


        List<TrellisedOvertime<Ae, AeGroupByOptions>> overtimeWeeks = aeService.getLineBarChart(DUMMY_ACUITY_DATASETS,
                settingsWithFilterBy.build(), aeFilters, populationFilters);

        softly.assertThat(overtimeWeeks.get(0).getData().getSeries().get(2).getSeries()).extracting(s -> s.getCategory()).contains("84").doesNotContain("83");

        settings.withOption(X_AXIS, AeGroupByOptions.OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_INCL_DURATION, false)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()));

        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy2 =
                ChartGroupByOptionsFiltered.builder(settings.build());
        List<TrellisedOvertime<Ae, AeGroupByOptions>> overtimeDays = aeService.getLineBarChart(DUMMY_ACUITY_DATASETS,
                settingsWithFilterBy2.build(), aeFilters, populationFilters);

        softly.assertThat(overtimeDays.get(0).getData().getSeries().get(0).getSeries()).extracting(s -> s.getCategory()).contains("588").doesNotContain("587");
    }

    @Test
    public void shouldGetSubjectLineAtMidValue() {
        AeFilters aeFilters = new AeFilters();
        aeFilters.setAeDetailLevel(AeDetailLevel.PER_SEVERITY_CHANGE);

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_INCL_DURATION, true)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.WEEKS_SINCE_FIRST_DOSE)
                        .build()));

        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());


        final List<TrellisedOvertime<Ae, AeGroupByOptions>> result = aeService
                .getLineBarChart(DUMMY_ACUITY_DATASETS, settingsWithFilterBy.build(),
                        aeFilters, PopulationFilters.empty());

        softly.assertThat(result.get(0).getData().getLines())
                .flatExtracting("series")
                .filteredOn("category", "129")
                .extracting("value")
                .contains(59.0);
    }

    @Test
    public void shouldGetFilteredCountsForIncludingDurationOption() {
        AeFilters aesFilters = new AeFilters();
        aesFilters.setPt(new SetFilter<>(newArrayList("NECK PAIN")));
        aesFilters.setAeDetailLevel(AeDetailLevel.PER_SEVERITY_CHANGE);

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_INCL_DURATION, true)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.WEEKS_SINCE_FIRST_DOSE)
                        .build()));

        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        final List<TrellisedOvertime<Ae, AeGroupByOptions>> result = aeService
                .getLineBarChart(DUMMY_ACUITY_DATASETS, settingsWithFilterBy.build(), aesFilters, PopulationFilters.empty());

        softly.assertThat(result.get(0).getData().getSeries())
                .flatExtracting("series")
                .extracting("value")
                .containsOnly(1.0);
    }

    @Test
    public void shouldGetBarLineValuesWhenTrellisOptionIsAllWithCorrectColor() {
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Ae, AeGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, AeGroupByOptions.MAX_SEVERITY_GRADE.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, AeGroupByOptions.OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_INCL_DURATION, true)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.WEEKS_SINCE_FIRST_DOSE)
                        .build()));

        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Ae, AeGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        final List<TrellisedOvertime<Ae, AeGroupByOptions>> result = aeService
                .getLineBarChart(DUMMY_ACUITY_DATASETS, settingsWithFilterBy.build(), AeFilters.empty(), PopulationFilters.empty());

/*
        softly.assertThat(result.get(0).getData().getSeries().stream()
                .filter(line -> "".equals(line.getName())).filter(line -> "#CC6677".equals(line.getColor())).findFirst().isPresent()).isTrue();
*/
    }

}
