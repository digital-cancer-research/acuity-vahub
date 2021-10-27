/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.acuity.visualisations.rawdatamodel.service.event;

import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.dataproviders.LungFunctionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.test.annotation.SpringITTest;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItemRange;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.LungFunctionRaw;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputRangeChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.RangeChartSeries;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.StatType;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBoxPlot;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedRangePlot;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.ImmutableList;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.groups.Tuple;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_LUNG_FUNC_DATASET;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_LUNG_FUNC_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions.ARM;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions.MEASUREMENT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions.MEASUREMENT_TIME_POINT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions.TimestampType;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.LungFunctionGroupByOptions.VISIT_NUMBER;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringITTest
public class LungFunctionServiceTest {

    @Autowired
    private LungFunctionService lungFunctionService;

    @MockBean
    private LungFunctionDatasetsDataProvider lungFunctionDatasetsDataProvider;

    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @MockBean
    private InfoService mockInfoService;

    private DoDCommonService doDCommonService = new DoDCommonService();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();


    private final Subject subject1 = Subject.builder().clinicalStudyCode(
            String.valueOf(DUMMY_ACUITY_LUNG_FUNC_DATASET.getId()))
            .subjectId("subject1")
            .actualArm("Placebo")
            .subjectCode("subject1")
            .firstTreatmentDate(toDate("10.11.2012"))
            .build();

    private final LungFunction lung1 = new LungFunction(LungFunctionRaw.builder()
            .visit(2.0)
            .value(1.23)
            .measurementNameRaw("FEV1 (%)")
            .measurementTimePoint(toDate("12.12.2012"))
            .build().runPrecalculations(), subject1);

    private final LungFunction lung2 = new LungFunction(LungFunctionRaw.builder()
            .visit(2.0)
            .value(2.34)
            .measurementNameRaw("FEV1 (%)")
            .measurementTimePoint(toDate("12.12.2012"))
            .build().runPrecalculations(), subject1);

    private final LungFunction lung3 = new LungFunction(LungFunctionRaw.builder()
            .visit(2.0)
            .value(2.34)
            .measurementNameRaw("FEV1 (%)")
            .measurementTimePoint(toDate("12.12.2012"))
            .visitDate(toDate("15.12.2012"))
            .build().runPrecalculations(), subject1);
    private final LungFunction lung4 = new LungFunction(
            LungFunctionRaw.builder()
                    .visit(2.0)
                    .value(2.34)
                    .measurementNameRaw("FEV1 (%)")
                    .measurementTimePoint(toDate("12.12.2012"))
                    .visitDate(toDate("15.12.2012"))
                    .protocolScheduleTimepoint("timepoint")
                    .unit("%")
                    .baselineValue(1.0)
                    .baselineFlag("F")
                    .build()
                    .runPrecalculations(),
            subject1);
    private final LungFunction lung5 = new LungFunction(
            LungFunctionRaw.builder()
                    .visit(2.0)
                    .value(2.34)
                    .measurementNameRaw("FEV1 (L)")
                    .measurementTimePoint(toDate("12.12.2012"))
                    .visitDate(toDate("15.12.2012"))
                    .protocolScheduleTimepoint("timepoint")
                    .unit("%")
                    .baselineValue(1.0)
                    .baselineFlag("F").build()
                    .runPrecalculations(),
            subject1);
    private final LungFunction lung6 = new LungFunction(
            LungFunctionRaw.builder()
                    .value(2.34)
                    .measurementNameRaw("FEV1 (L)")
                    .measurementTimePoint(toDate("13.12.2012"))
                    .visitDate(toDate("18.12.2012"))
                    .protocolScheduleTimepoint("timepoint")
                    .unit("%")
                    .baselineValue(1.0)
                    .baselineFlag("F")
                    .build()
                    .runPrecalculations(),
            subject1);
    private final LungFunction lung7 = new LungFunction(
            LungFunctionRaw.builder()
                    .value(2.34)
                    .visit(3.0)
                    .measurementNameRaw("FEV1 (L)")
                    .measurementTimePoint(toDate("13.12.2012"))
                    .protocolScheduleTimepoint("timepoint")
                    .unit("%")
                    .baselineValue(1.0)
                    .baselineFlag("F")
                    .build()
                    .runPrecalculations(),
            subject1);

    @Test
    public void shouldGetAvailableBoxPlotXAxis() {

        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1));
        when(lungFunctionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(lung1));

        AxisOptions<LungFunctionGroupByOptions> options = lungFunctionService.getAvailableBoxPlotXAxis(
                DUMMY_ACUITY_LUNG_FUNC_DATASETS, LungFunctionFilters.empty(), PopulationFilters.empty());

        AxisOption<LungFunctionGroupByOptions> measurementOverTimeOption = options.getOptions().get(1);

        softly.assertThat(measurementOverTimeOption.getGroupByOption().name()).isEqualTo("MEASUREMENT_TIME_POINT");
        softly.assertThat(measurementOverTimeOption.isBinableOption()).isTrue();

    }

    @Test
    public void shouldGetAvailableMeanRangeChartXAxis() {

        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1));
        when(lungFunctionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(lung3));

        AxisOptions<LungFunctionGroupByOptions> options = lungFunctionService.getAvailableRangePlotXAxis(
                DUMMY_ACUITY_LUNG_FUNC_DATASETS, LungFunctionFilters.empty(), PopulationFilters.empty());

        softly.assertThat(options.getOptions())
                .extracting(AxisOption::getGroupByOption)
                .containsExactlyInAnyOrder(VISIT_NUMBER, MEASUREMENT_TIME_POINT);
    }

    @Test
    public void shouldGetAvailableBoxPlotXAxisWithVisitDescription() {
        final LungFunction lung = new LungFunction(LungFunctionRaw.builder()
                .visit(2.0)
                .visitDescription("Some Detail")
                .value(1.23)
                .measurementNameRaw("FEV1 (%)")
                .measurementTimePoint(toDate("12.12.2012"))
                .build().runPrecalculations(), subject1);

        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1));
        when(lungFunctionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(lung));

        AxisOptions<LungFunctionGroupByOptions> options = lungFunctionService.getAvailableBoxPlotXAxis(
                DUMMY_ACUITY_LUNG_FUNC_DATASETS, LungFunctionFilters.empty(), PopulationFilters.empty());

        AxisOption<LungFunctionGroupByOptions> measurementOverTimeOption = options.getOptions().get(1);

        softly.assertThat(measurementOverTimeOption.getGroupByOption().name()).isEqualTo("VISIT_DESCRIPTION");
    }


    @Test
    public void shouldGetBoxPlot() {

        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1));
        when(lungFunctionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(lung1));

        ChartGroupByOptions<LungFunction, LungFunctionGroupByOptions> settings =
                ChartGroupByOptions.<LungFunction, LungFunctionGroupByOptions>builder()
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS,
                                LungFunctionGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                                LungFunctionGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                        .withTrellisOption(MEASUREMENT.getGroupByOptionAndParams())
                        .withTrellisOption(ARM.getGroupByOptionAndParams())
                        .build();

        final HashMap<LungFunctionGroupByOptions, Object> filterByTrellis = new HashMap<>();
        filterByTrellis.put(MEASUREMENT, "FEV1 (%)");
        filterByTrellis.put(ARM, "Placebo");

        List<TrellisedBoxPlot<LungFunction, LungFunctionGroupByOptions>> result = lungFunctionService.getBoxPlot(
                DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                ChartGroupByOptionsFiltered.builder(settings).withFilterByTrellisOption(filterByTrellis).build(),
                LungFunctionFilters.empty(),
                PopulationFilters.empty()
        );

        softly.assertThat(result.get(0).getTrellisedBy())
                .extracting("trellisedBy")
                .containsOnly(ARM, MEASUREMENT);
        softly.assertThat(result.get(0).getStats()).hasSize(1);
        softly.assertThat(result.get(0).getStats().get(0).getX()).isEqualTo("2");
    }

    @Test
    public void shouldGetTrellisOptions() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1));
        when(lungFunctionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(lung1));

        List<TrellisOptions<LungFunctionGroupByOptions>> result = lungFunctionService.getTrellisOptions(
                DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                LungFunctionFilters.empty(),
                PopulationFilters.empty(),
                LungFunctionGroupByOptions.ACTUAL_VALUE
        );

        softly.assertThat(result).hasSize(2);

        softly.assertThat(result).containsExactlyInAnyOrder(
                new TrellisOptions<>(MEASUREMENT, Collections.singletonList("FEV1 (%)")),
                new TrellisOptions<>(ARM, Collections.singletonList("Placebo"))
        );

    }

    @Test
    public void shouldGetSelection() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1));
        when(lungFunctionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(lung1, lung2));

        ChartGroupByOptions<LungFunction, LungFunctionGroupByOptions> settings =
                ChartGroupByOptions.<LungFunction, LungFunctionGroupByOptions>builder()
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS,
                                LungFunctionGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                                LungFunctionGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                        .withTrellisOption(MEASUREMENT.getGroupByOptionAndParams())
                        .withTrellisOption(ARM.getGroupByOptionAndParams())
                        .build();

        HashMap<LungFunctionGroupByOptions, Object> selectedTrellises = new HashMap<>();
        selectedTrellises.put(MEASUREMENT, "FEV1 (%)");
        selectedTrellises.put(ARM, "Placebo");

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(X_AXIS, "2");

        final List<ChartSelectionItemRange<LungFunction, LungFunctionGroupByOptions, Double>> selectionItems =
                Collections.singletonList(
                        ChartSelectionItemRange.of(selectedTrellises, selectedItems, 1.0, 2.0)
                );

        SelectionDetail selectionDetail = lungFunctionService.getRangedSelectionDetails(
                DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                LungFunctionFilters.empty(),
                PopulationFilters.empty(),
                ChartSelection.of(settings, selectionItems)
        );

        softly.assertThat(selectionDetail.getEventIds()).hasSize(1);
        softly.assertThat(selectionDetail.getSubjectIds()).hasSize(1);
    }

    @Test
    public void shouldGetAllAcuityDetailsOnDemandColumnsInCorrectOrder() {

        List<LungFunction> exacerbation = Collections.singletonList(lung4);

        Map<String, String> columns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, exacerbation);

        softly.assertThat(columns.keySet())
                .containsExactly("studyId", "subjectId",
                        "measurementName", "measurementTimePoint",
                        "daysOnStudy", "protocolScheduleTimepoint", "visitNumber",
                        "resultValue", "resultUnit",
                        "baselineValue", "changeFromBaseline", "percentChangeFromBaseline", "baselineFlag");

        softly.assertThat(columns.values())
                .containsExactly("Study id", "Subject id",
                        "Measurement name", "Measurement time point",
                        "Days on study", "Protocol schedule timepoint", "Visit number",
                        "Result value", "Result unit",
                        "Baseline value", "Change from baseline", "Percent change from baseline", "Baseline flag");
    }

    @Test
    public void statsDataCalculationRoundingTest() {
        LungFunction lungFunction1 = new LungFunction(
                LungFunctionRaw.builder()
                        .value(2.51)
                        .measurementNameRaw("FEF25_75 (L/s)")
                        .visit(1.0)
                        .build(),
                subject1);
        LungFunction lungFunction2 = new LungFunction(
                LungFunctionRaw.builder()
                        .value(2.68)
                        .measurementNameRaw("FEF25_75 (L/s)")
                        .visit(1.0)
                        .build(),
                subject1);

        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Collections.singleton(subject1));
        when(lungFunctionDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(lungFunction1, lungFunction2));

        ChartGroupByOptions<LungFunction, LungFunctionGroupByOptions> settings =
                ChartGroupByOptions.<LungFunction, LungFunctionGroupByOptions>builder()
                        .withOption(X_AXIS, VISIT_NUMBER.getGroupByOptionAndParams())
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                                LungFunctionGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                        .withTrellisOption(MEASUREMENT.getGroupByOptionAndParams())
                        .build();

        HashMap<LungFunctionGroupByOptions, Object> filterByTrellis = new HashMap<>();
        filterByTrellis.put(MEASUREMENT, "FEF25_75 (L/s)");

        List<TrellisedRangePlot<LungFunction, LungFunctionGroupByOptions>> result =
                lungFunctionService.getRangePlot(
                        DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        ChartGroupByOptionsFiltered.builder(settings)
                                .withFilterByTrellisOption(filterByTrellis).build(),
                        LungFunctionFilters.empty(),
                        PopulationFilters.empty(),
                        StatType.MEDIAN
                );

        OutputRangeChartEntry entry = (OutputRangeChartEntry) (result.get(0).getData().get(0).getData().get(0));

        softly.assertThat(entry.getMin()).isEqualTo(2.51);
        softly.assertThat(entry.getMax()).isEqualTo(2.68);
        softly.assertThat(entry.getY()).isEqualTo(2.59);
    }

    @Test
    public void measurementTimePointShouldBeExcludedIfAxisLimitedToVisits() {

        Subject subject = Subject.builder()
                .studyInfo(StudyInfo.builder().lastUpdatedDate(toDate("10.11.2012")).limitXAxisToVisitNumber(true).build())
                .subjectId("subject1")
                .subjectCode("subject1")
                .firstTreatmentDate(toDate("10.11.2012"))
                .build();

        LungFunction lung = new LungFunction(LungFunctionRaw.builder()
                .id("lung01")
                .visit(2.0)
                .measurementTimePoint(toDate("12.12.2012"))
                .measurementNameRaw("FEF25_75 (L/s)")
                .build(), subject);

        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Collections.singleton(subject));
        when(lungFunctionDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Collections.singleton(lung));

        LungFunctionFilters filters = (LungFunctionFilters) lungFunctionService.getAvailableFilters(
                DUMMY_ACUITY_LUNG_FUNC_DATASETS, LungFunctionFilters.empty(), PopulationFilters.empty());
        softly.assertThat(filters.getMeasurementTimePoint().getFrom()).isNull();
        softly.assertThat(filters.getMeasurementTimePoint().getTo()).isNull();
        softly.assertThat(filters.getDaysOnStudy().getFrom()).isNotNull();
        softly.assertThat(filters.getDaysOnStudy().getTo()).isNotNull();
        softly.assertThat(filters.getMeasurementName().getValues()).isNotEmpty();

        List<Map<String, String>> values = lungFunctionService.getDetailsOnDemandData(
                DUMMY_ACUITY_LUNG_FUNC_DATASETS, Collections.singleton("lung01"), ImmutableList.of(new SortAttrs(null, true)), 0, 9);
        softly.assertThat(values).hasSize(1);
        softly.assertThat(values.get(0).get("measurementName")).isNotNull();
        softly.assertThat(values.get(0).get("measurementTimePoint")).isNull();
        softly.assertThat(values.get(0).get("daysOnStudy")).isNotNull();
    }

    @Test
    public void shouldGetDataWhenVisitDateXAxisSelected() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Collections.singleton(subject1));
        when(lungFunctionDatasetsDataProvider.loadData(any(Datasets.class)))
                .thenReturn(Arrays.asList(lung1, lung3));

        ChartGroupByOptions<LungFunction, LungFunctionGroupByOptions> settings =
                ChartGroupByOptions.<LungFunction, LungFunctionGroupByOptions>builder()
                        .withOption(X_AXIS,
                                LungFunctionGroupByOptions.VISIT_DATE.getGroupByOptionAndParams(GroupByOption.Params.builder()
                                        .with(GroupByOption.Param.BIN_SIZE, 1)
                                        .with(GroupByOption.Param.TIMESTAMP_TYPE, TimestampType.DATE)
                                        .build()))
                        .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                                LungFunctionGroupByOptions.ACTUAL_VALUE.getGroupByOptionAndParams())
                        .withTrellisOption(MEASUREMENT.getGroupByOptionAndParams())
                        .build();

        HashMap<LungFunctionGroupByOptions, Object> filterByTrellis = new HashMap<>();
        filterByTrellis.put(MEASUREMENT, "FEV1 (%)");

        List<TrellisedRangePlot<LungFunction, LungFunctionGroupByOptions>> result =
                lungFunctionService.getRangePlot(
                        DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        ChartGroupByOptionsFiltered.builder(settings)
                                .withFilterByTrellisOption(filterByTrellis).build(),
                        LungFunctionFilters.empty(),
                        PopulationFilters.empty(),
                        StatType.MEDIAN
                );

        OutputRangeChartEntry entry = (OutputRangeChartEntry) (result.get(0).getData().get(0).getData().get(0));

        List<OutputRangeChartEntry> flattenedResult = result.get(0).getData().stream()
                .map(RangeChartSeries::getData)
                .findFirst()
                .orElse(Collections.emptyList());
        softly.assertThat(flattenedResult).hasSize(1);
        softly.assertThat(flattenedResult).extracting("dataPoints", "x", "xRank", "y", "stdErr", "min", "max")
                .contains(Tuple.tuple(1, "2012-12-15", 0.0, 2.34, null, 2.34, 2.34));
    }
}
