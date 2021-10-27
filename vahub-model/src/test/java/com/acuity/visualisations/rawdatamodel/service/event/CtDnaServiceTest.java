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
import com.acuity.visualisations.rawdatamodel.dataproviders.CtDnaDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.CtDnaFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.compatibility.CtDnaLineChartColoringService;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.CtDnaRaw;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.TrellisedLineFloatChart;
import com.acuity.visualisations.rawdatamodel.vo.ctdna.SubjectGeneMutationVaf;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.COLORS_NO_GREEN;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.BLACK;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions.GENE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions.MUTATION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions.SAMPLE_DATE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions.VISIT_DATE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions.VISIT_NUMBER;
import static com.acuity.visualisations.rawdatamodel.util.Constants.FORMATTING_THREE_DECIMAL_PLACES;
import static com.acuity.visualisations.rawdatamodel.util.Constants.FORMATTING_TWO_DECIMAL_PLACES;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class CtDnaServiceTest {
    private static final String GENE_1 = "g1";
    private static final String MUT_1 = "m1";
    private static final String GENE_2 = "g2";
    private static final String MUT_2 = "m2";
    private static final String SAMPLE_DATE_1 = "2000-01-10";
    private static final String SAMPLE_DATE_2 = "2000-01-15";
    private static final String SAMPLE_DATE_3 = "2000-01-30";
    private static final String SAMPLE_DATE_4 = "2000-01-31";
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Autowired
    private CtDnaService ctDnaService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private CtDnaDatasetsDataProvider ctDnaDatasetsDataProvider;
    @MockBean
    private CtDnaLineChartColoringService coloringService;

    private Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1")
            .firstTreatmentDate(DaysUtil.toDate("2000-01-01")).build();
    private Subject subject2 = Subject.builder().subjectId("subjectId2").subjectCode("subject2")
            .firstTreatmentDate(DaysUtil.toDate("2000-01-20")).build();

    private CtDna ctDna11 = new CtDna(CtDnaRaw.builder().id("cId11").subjectId(subject1.getId())
            .sampleDate(DaysUtil.toDate(SAMPLE_DATE_1)).gene(GENE_1).mutation(MUT_1)
            .trackedMutation("YES").reportedVaf(0.321).reportedVafCalculated(0.321)
            .reportedVafPercent(32.1).reportedVafCalculatedPercent(32.1).visitNumber(1.).build(), subject1);
    private CtDna ctDna12 = new CtDna(CtDnaRaw.builder().id("cId12").subjectId(subject1.getId())
            .sampleDate(DaysUtil.toDate(SAMPLE_DATE_2)).gene(GENE_1).mutation(MUT_1)
            .trackedMutation("YES").reportedVaf(0.5).reportedVafCalculated(0.5)
            .reportedVafPercent(50.).reportedVafCalculatedPercent(50.).build(), subject1);
    private CtDna ctDna13 = new CtDna(CtDnaRaw.builder().id("cId13").subjectId(subject1.getId())
            .sampleDate(DaysUtil.toDate(SAMPLE_DATE_3)).gene(GENE_1).mutation(MUT_1)
            .trackedMutation("YES").reportedVaf(0.666).reportedVafCalculated(0.666)
            .reportedVafPercent(66.6).reportedVafCalculatedPercent(66.6).visitNumber(3.).build(), subject1);

    private CtDna ctDna21 = new CtDna(CtDnaRaw.builder().id("cId21").subjectId(subject1.getId())
            .trackedMutation("YES").reportedVaf(0.1).reportedVafCalculated(0.1)
            .reportedVafPercent(10.).reportedVafCalculatedPercent(10.).visitNumber(4.)
            .sampleDate(DaysUtil.toDate(SAMPLE_DATE_1)).gene(GENE_2).mutation(MUT_1).build(), subject1);
    private CtDna ctDna22 = new CtDna(CtDnaRaw.builder().id("cId22").subjectId(subject1.getId())
            .sampleDate(DaysUtil.toDate(SAMPLE_DATE_2)).gene(GENE_2).mutation(MUT_1)
            .trackedMutation("YES").reportedVaf(0.052).reportedVafCalculated(0.052)
            .reportedVafPercent(5.2).reportedVafCalculatedPercent(5.2).visitNumber(5.).build(), subject1);
    private CtDna ctDna23 = new CtDna(CtDnaRaw.builder().id("cId22").subjectId(subject1.getId())
            .sampleDate(DaysUtil.toDate(SAMPLE_DATE_3)).visitNumber(6.).gene(GENE_2).mutation(MUT_1)
            .trackedMutation("NO").reportedVaf(0.05).reportedVafCalculated(0.05)
            .reportedVafPercent(5.).reportedVafCalculatedPercent(5.).build(), subject1);

    private CtDna ctDna31 = new CtDna(CtDnaRaw.builder().id("cId31").subjectId(subject2.getId())
            .sampleDate(DaysUtil.toDate(SAMPLE_DATE_4)).gene(GENE_1).mutation(MUT_2)
            .trackedMutation("YES").reportedVaf(0.8).reportedVafCalculated(0.8)
            .reportedVafPercent(80.).reportedVafCalculatedPercent(80.).visitNumber(7.).build(), subject2);

    private CtDna ctDna32 = new CtDna(CtDnaRaw.builder().id("cId32").subjectId(subject2.getId())
            .sampleDate(DaysUtil.toDate(SAMPLE_DATE_3)).gene(GENE_1).mutation(MUT_2)
            .trackedMutation("YES").reportedVaf(0.0001).reportedVafCalculated(0.002)
            .reportedVafPercent(.01).reportedVafCalculatedPercent(.2).visitNumber(6.).build(), subject2);

    // should be filtered out
    private CtDna ctDnaVafCalculatedLessThenThreshold = new CtDna(CtDnaRaw.builder()
            .id("cId41").subjectId(subject2.getId())
            .sampleDate(DaysUtil.toDate(SAMPLE_DATE_1)).gene(GENE_1).mutation(MUT_1)
            .trackedMutation("YES").reportedVafCalculated(0.).visitNumber(1.)
            .reportedVafCalculatedPercent(0.).build(), subject2);

    // results in NAME option
    private final SubjectGeneMutationVaf subjectGeneMutationVaf1
            = SubjectGeneMutationVaf.builder()
                                    .subjectCode(subject1.getSubjectCode())
                                    .gene(GENE_1)
                                    .mutation(MUT_1)
                                    .vaf(0.321)
                                    .vafPercent(32.1)
                                    .build();
    private final SubjectGeneMutationVaf subjectGeneMutationVaf2
            = SubjectGeneMutationVaf.builder()
                                    .subjectCode(subject1.getSubjectCode())
                                    .gene(GENE_1)
                                    .mutation(MUT_1)
                                    .vaf(0.5)
                                    .vafPercent(50.)
                                    .build();
    private final SubjectGeneMutationVaf subjectGeneMutationVaf3
            = SubjectGeneMutationVaf.builder()
                                    .subjectCode(subject1.getSubjectCode())
                                    .gene(GENE_1)
                                    .mutation(MUT_1)
                                    .vaf(0.666)
                                    .vafPercent(66.6)
                                    .build();
    private final SubjectGeneMutationVaf subjectGeneMutationVaf4
            = SubjectGeneMutationVaf.builder()
                                    .subjectCode(subject1.getSubjectCode())
                                    .gene(GENE_2)
                                    .mutation(MUT_1)
                                    .vaf(0.1)
                                    .vafPercent(10.)
                                    .build();
    private final SubjectGeneMutationVaf subjectGeneMutationVaf5
            = SubjectGeneMutationVaf.builder()
                                    .subjectCode(subject1.getSubjectCode())
                                    .gene(GENE_2)
                                    .mutation(MUT_1)
                                    .vaf(0.052)
                                    .vafPercent(5.2)
                                    .build();
    private final SubjectGeneMutationVaf subjectGeneMutationVaf6
            = SubjectGeneMutationVaf.builder()
                                    .subjectCode(subject1.getSubjectCode())
                                    .gene(GENE_2)
                                    .mutation(MUT_1)
                                    .vaf(0.05)
                                    .vafPercent(5.)
                                    .build();
    private final SubjectGeneMutationVaf subjectGeneMutationVaf7
            = SubjectGeneMutationVaf.builder()
                                    .subjectCode(subject2.getSubjectCode())
                                    .gene(GENE_1)
                                    .mutation(MUT_2)
                                    .vaf(0.0001)
                                    .vafPercent(0.01)
                                    .build();
    private final SubjectGeneMutationVaf subjectGeneMutationVaf8
            = SubjectGeneMutationVaf.builder()
                                    .subjectCode(subject2.getSubjectCode())
                                    .gene(GENE_1)
                                    .mutation(MUT_2)
                                    .vaf(0.8)
                                    .vafPercent(80.)
                                    .build();

    @Before
    public void setUp() {
        when(ctDnaDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(ctDna11, ctDna12, ctDna13,
                ctDna21, ctDna22, ctDna23, ctDna31, ctDna32, ctDnaVafCalculatedLessThenThreshold));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1, subject2));
        when(coloringService.getColor(any(),  any())).thenReturn(BLACK.getCode());
    }

    @Test
    public void testGetAvailableLineChartXAxis() {
        AxisOptions<CtDnaGroupByOptions> axisOptions = ctDnaService.getAvailableLineChartXAxis(DATASETS,
                CtDnaFilters.empty(), PopulationFilters.empty());
        softly.assertThat(axisOptions.getOptions()).extracting(AxisOption::getGroupByOption,
                AxisOption::isBinableOption, AxisOption::isTimestampOption)
                .containsExactly(tuple(SAMPLE_DATE, false, true),
                        tuple(VISIT_NUMBER, false, false),
                        tuple(VISIT_DATE, false, false));
    }

    @Test
    public void testGetBoxPlotColorBy() {
        List<TrellisOptions<CtDnaGroupByOptions>> colorBy = ctDnaService.getColorBy(DATASETS,
                CtDnaFilters.empty(), PopulationFilters.empty());
        softly.assertThat(colorBy).extracting(TrellisOptions::getTrellisedBy)
                .containsExactly(CtDnaGroupByOptions.SUBJECT, CtDnaGroupByOptions.GENE, CtDnaGroupByOptions.MUTATION);
    }

    @Test
    public void testGetLineChartSampleDate() {
        List<TrellisedLineFloatChart<CtDna, CtDnaGroupByOptions, OutputLineChartData>> lineChart =
                ctDnaService.getLineChart(DATASETS, getLineChartSettings(),
                        CtDnaFilters.empty(), PopulationFilters.empty());
        softly.assertThat(lineChart.get(0).getData()).hasSize(3);
        softly.assertThat(lineChart.get(0).getData()).filteredOn(d -> d.getSeriesBy().equals("subject1, g1, m1"))
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getX,
                OutputLineChartEntry::getY, OutputLineChartEntry::getName).containsExactly(
                tuple(9, 0.321, subjectGeneMutationVaf1),
                tuple(14, 0.5, subjectGeneMutationVaf2),
                tuple(29, 0.666, subjectGeneMutationVaf3));
        softly.assertThat(lineChart.get(0).getData()).filteredOn(d -> d.getSeriesBy().equals("subject1, g2, m1"))
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getX,
                OutputLineChartEntry::getY, OutputLineChartEntry::getName).containsExactly(
                tuple(9, 0.1, subjectGeneMutationVaf4),
                tuple(14, 0.052, subjectGeneMutationVaf5),
                tuple(29, 0.05, subjectGeneMutationVaf6));
        softly.assertThat(lineChart.get(0).getData()).filteredOn(d -> d.getSeriesBy().equals("subject2, g1, m2"))
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getX,
                OutputLineChartEntry::getY, OutputLineChartEntry::getName)
                .containsExactly(
                        tuple(10, 0.002, subjectGeneMutationVaf7),
                        tuple(11, 0.8, subjectGeneMutationVaf8)
                );
    }

    @Test
    public void testGetLineChartVisitDate() {
        List<TrellisedLineFloatChart<CtDna, CtDnaGroupByOptions, OutputLineChartData>> lineChart =
                ctDnaService.getLineChart(DATASETS, getLineChartSettingsXAxis(VISIT_DATE),
                        CtDnaFilters.empty(), PopulationFilters.empty());
        softly.assertThat(lineChart.get(0).getData()).hasSize(3);
        softly.assertThat(lineChart.get(0).getData()).filteredOn(d -> d.getSeriesBy().equals("subject1, g1, m1"))
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getX,
                OutputLineChartEntry::getY, OutputLineChartEntry::getName).containsExactly(
                tuple("2000-01-10", 0.321, subjectGeneMutationVaf1),
                tuple("2000-01-15", 0.5, subjectGeneMutationVaf2),
                tuple("2000-01-30", 0.666, subjectGeneMutationVaf3));
        softly.assertThat(lineChart.get(0).getData()).filteredOn(d -> d.getSeriesBy().equals("subject1, g2, m1"))
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getX,
                OutputLineChartEntry::getY, OutputLineChartEntry::getName).containsExactly(
                tuple("2000-01-10", 0.1, subjectGeneMutationVaf4),
                tuple("2000-01-15", 0.052, subjectGeneMutationVaf5),
                tuple("2000-01-30", 0.05, subjectGeneMutationVaf6));
        softly.assertThat(lineChart.get(0).getData()).filteredOn(d -> d.getSeriesBy().equals("subject2, g1, m2"))
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getX,
                OutputLineChartEntry::getY, OutputLineChartEntry::getName)
                .containsExactly(
                        tuple("2000-01-30", 0.002, subjectGeneMutationVaf7),
                        tuple("2000-01-31", 0.8, subjectGeneMutationVaf8)
                );
    }

    @Test
    public void testGetLineChartVisitNumber() {
        List<TrellisedLineFloatChart<CtDna, CtDnaGroupByOptions, OutputLineChartData>> lineChart =
                ctDnaService.getLineChart(DATASETS, getLineChartSettingsXAxis(VISIT_NUMBER),
                        CtDnaFilters.empty(), PopulationFilters.empty());
        softly.assertThat(lineChart.get(0).getData()).hasSize(3);
        softly.assertThat(lineChart.get(0).getData()).filteredOn(d -> d.getSeriesBy().equals("subject1, g1, m1"))
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getX,
                OutputLineChartEntry::getY, OutputLineChartEntry::getName).containsExactly(
                tuple(1., 0.321, subjectGeneMutationVaf1),
                tuple(3., 0.666, subjectGeneMutationVaf3));
        softly.assertThat(lineChart.get(0).getData()).filteredOn(d -> d.getSeriesBy().equals("subject1, g2, m1"))
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getX,
                OutputLineChartEntry::getY, OutputLineChartEntry::getName).containsExactly(
                tuple(4., 0.1, subjectGeneMutationVaf4),
                tuple(5., 0.052, subjectGeneMutationVaf5),
                tuple(6., 0.05, subjectGeneMutationVaf6));
        softly.assertThat(lineChart.get(0).getData()).filteredOn(d -> d.getSeriesBy().equals("subject2, g1, m2"))
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getX,
                OutputLineChartEntry::getY, OutputLineChartEntry::getName)
                .containsExactly(
                        tuple(6., 0.002, subjectGeneMutationVaf7),
                        tuple(7., 0.8, subjectGeneMutationVaf8)
                );
    }

    @Test
    public void testGetLineChartInPercent() {
        List<TrellisedLineFloatChart<CtDna, CtDnaGroupByOptions, OutputLineChartData>> lineChart =
                ctDnaService.getLineChart(DATASETS, getLineChartSettings(true),
                        CtDnaFilters.empty(), PopulationFilters.empty());
        softly.assertThat(lineChart.get(0).getData()).hasSize(3);
        softly.assertThat(lineChart.get(0).getData()).filteredOn(d -> d.getSeriesBy().equals("subject1, g1, m1"))
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getX,
                OutputLineChartEntry::getY, OutputLineChartEntry::getName).containsExactly(
                tuple(9, 32.1, subjectGeneMutationVaf1),
                tuple(14, 50., subjectGeneMutationVaf2),
                tuple(29, 66.6, subjectGeneMutationVaf3));
        softly.assertThat(lineChart.get(0).getData()).filteredOn(d -> d.getSeriesBy().equals("subject1, g2, m1"))
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getX,
                OutputLineChartEntry::getY, OutputLineChartEntry::getName).containsExactly(
                tuple(9, 10., subjectGeneMutationVaf4),
                tuple(14, 5.2, subjectGeneMutationVaf5),
                tuple(29, 5., subjectGeneMutationVaf6));
        softly.assertThat(lineChart.get(0).getData()).filteredOn(d -> d.getSeriesBy().equals("subject2, g1, m2"))
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getX,
                OutputLineChartEntry::getY, OutputLineChartEntry::getName)
                .containsExactly(
                        tuple(10, 0.2, subjectGeneMutationVaf7),
                        tuple(11, 80., subjectGeneMutationVaf8)
                );
    }

    @Test
    public void shouldGetLineChartColoredByGene() {
        List<OutputLineChartData> lineChartData = getLineChartColoredByY(GENE, GENE_1, GENE_2);
        softly.assertThat(lineChartData)
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getColor)
                .containsExactly(COLORS_NO_GREEN[0], COLORS_NO_GREEN[0], COLORS_NO_GREEN[0],
                        COLORS_NO_GREEN[1], COLORS_NO_GREEN[1], COLORS_NO_GREEN[1],
                        COLORS_NO_GREEN[0], COLORS_NO_GREEN[0]);
    }

    @Test
    public void shouldGetLineChartColoredBySubject() {
        List<OutputLineChartData> lineChartData = getLineChartColoredByY(CtDnaGroupByOptions.SUBJECT, subject1.getSubjectCode(), subject2.getSubjectCode());
        softly.assertThat(lineChartData)
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getColor)
                .containsExactly(COLORS_NO_GREEN[0], COLORS_NO_GREEN[0], COLORS_NO_GREEN[0],
                        COLORS_NO_GREEN[0], COLORS_NO_GREEN[0], COLORS_NO_GREEN[0],
                        COLORS_NO_GREEN[1], COLORS_NO_GREEN[1]);
    }

    @Test
    public void shouldGetLineChartColoredByMutation() {
        List<OutputLineChartData> lineChartData = getLineChartColoredByY(MUTATION, MUT_1, MUT_2);
        softly.assertThat(lineChartData)
                .flatExtracting(OutputLineChartData::getSeries).extracting(OutputLineChartEntry::getColor)
                .containsExactly(COLORS_NO_GREEN[0], COLORS_NO_GREEN[0], COLORS_NO_GREEN[0],
                        COLORS_NO_GREEN[0], COLORS_NO_GREEN[0], COLORS_NO_GREEN[0],
                        COLORS_NO_GREEN[1], COLORS_NO_GREEN[1]);
    }


    private List<OutputLineChartData> getLineChartColoredByY(CtDnaGroupByOptions colorByY,
                                                             String colorGroup1, String colorGroup2) {

        //Given
        when(coloringService.getColor(eq(colorGroup1),  anyObject())).thenReturn(COLORS_NO_GREEN[0]);
        when(coloringService.getColor(eq(colorGroup2),  anyObject())).thenReturn(COLORS_NO_GREEN[1]);

        ChartGroupByOptions<CtDna, CtDnaGroupByOptions> settings = getLineChartSettings(colorByY);

        List<TrellisedLineFloatChart<CtDna, CtDnaGroupByOptions, OutputLineChartData>> lineChart =
                ctDnaService.getLineChart(DATASETS, settings,
                        CtDnaFilters.empty(), PopulationFilters.empty());

        return lineChart.get(0).getData().stream().sorted(Comparator.comparing(l -> l.getSeriesBy().toString()))
                .collect(Collectors.toList());
    }

    private ChartGroupByOptions<CtDna, CtDnaGroupByOptions> getLineChartSettings(CtDnaGroupByOptions colorBy, boolean showInPercent) {
        return ChartGroupByOptions.<CtDna, CtDnaGroupByOptions>builder()
                .withOption(SERIES_BY, CtDnaGroupByOptions.SUBJECT_GENE_MUT.getGroupByOptionAndParams())
                .withOption(COLOR_BY, colorBy.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.NAME, CtDnaGroupByOptions.SUBJECT_GENE_MUT_VAF.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.X_AXIS, CtDnaGroupByOptions.SAMPLE_DATE
                        .getGroupByOptionAndParams(GroupByOption.Params.builder()
                                .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                                .build()))
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS,
                        showInPercent
                                ? CtDnaGroupByOptions.VARIANT_ALLELE_FREQUENCY_PERCENT.getGroupByOptionAndParams()
                                : CtDnaGroupByOptions.VARIANT_ALLELE_FREQUENCY.getGroupByOptionAndParams())
                .build();
    }

    private ChartGroupByOptions<CtDna, CtDnaGroupByOptions> getLineChartSettings(CtDnaGroupByOptions colorBy) {
        return getLineChartSettings(colorBy, false);
    }

    private ChartGroupByOptions<CtDna, CtDnaGroupByOptions> getLineChartSettings() {
        return getLineChartSettings(CtDnaGroupByOptions.SUBJECT);
    }

    private ChartGroupByOptions<CtDna, CtDnaGroupByOptions> getLineChartSettingsXAxis(CtDnaGroupByOptions xOption) {
        return getLineChartSettings().toBuilder().withOption
                (ChartGroupByOptions.ChartGroupBySetting.X_AXIS, xOption.getGroupByOptionAndParams()).build();
    }

    private ChartGroupByOptions<CtDna, CtDnaGroupByOptions> getLineChartSettings(boolean showInPercent) {
        return getLineChartSettings(CtDnaGroupByOptions.SUBJECT, showInPercent);
    }

    @Test
    public void testGetLineChartSelectionForCtDna() {

        final HashMap<CtDnaGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem1 = new HashMap<>();
        selectedItem1.put(SERIES_BY, "subject1, g1, m1");
        selectedItem1.put(X_AXIS, 1);

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem2 = new HashMap<>();
        selectedItem2.put(SERIES_BY, "subject1, g1, m1");
        selectedItem2.put(X_AXIS, 3);

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem3 = new HashMap<>();
        selectedItem3.put(SERIES_BY, "subject2, g1, m2");
        selectedItem3.put(X_AXIS, 6);

        SelectionDetail selectionDetails = ctDnaService.getLineChartSelectionDetails(DUMMY_2_ACUITY_DATASETS,
                CtDnaFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(getDefaultLineChartSelectionSettings(), newArrayList(ChartSelectionItem.of(selectedTrellises, selectedItem1),
                        ChartSelectionItem.of(selectedTrellises, selectedItem2), ChartSelectionItem.of(selectedTrellises, selectedItem3))));

        softly.assertThat(selectionDetails).extracting(s -> s.getEventIds().size(), s -> s.getSubjectIds().size(), SelectionDetail::getTotalEvents,
                SelectionDetail::getTotalSubjects).containsExactly(3, 2, 9, 2);
        softly.assertThat(selectionDetails).extracting(SelectionDetail::getEventIds)
              .containsExactly(newHashSet("cId13", "cId32", "cId11"));
    }

    private ChartGroupByOptions<CtDna, CtDnaGroupByOptions> getDefaultLineChartSelectionSettings() {
        return ChartGroupByOptions.<CtDna, CtDnaGroupByOptions>builder()
                .withOption(SERIES_BY, CtDnaGroupByOptions.SUBJECT_GENE_MUT.getGroupByOptionAndParams())
                .withOption(X_AXIS, CtDnaGroupByOptions.VISIT_NUMBER.getGroupByOptionAndParams())
                .build();
    }

    @Test
    public void shouldGetDetectDetailsOnDemand() {
        List<CtDna> ctDnaList = Arrays.asList(ctDna11, ctDna12, ctDna13);

        Set<String> ctDnaIds = ctDnaList.stream().map(CtDna::getId).collect(toSet());

        List<Map<String, String>> doDData = ctDnaService.getDetailsOnDemandData(DUMMY_DETECT_DATASETS, ctDnaIds,
                Collections.emptyList(), 0, Integer.MAX_VALUE);
        CtDna ctDna = ctDnaList.get(0);
        Map<String, String> dod = doDData.get(0);
        softly.assertThat(doDData).hasSize(ctDnaList.size());
        softly.assertThat(dod.size()).isEqualTo(12);
        softly.assertThat(ctDna.getSubject().getSubjectCode()).isEqualTo(dod.get("subjectId"));
        softly.assertThat(ctDna.getSubject().getClinicalStudyCode()).isEqualTo(dod.get("studyId"));
        softly.assertThat(ctDna.getEvent().getGene()).isEqualTo(dod.get("gene"));
        softly.assertThat(ctDna.getEvent().getMutation()).isEqualTo(dod.get("mutation"));
        softly.assertThat(ctDna.getEvent().getTrackedMutation()).isEqualTo(dod.get("trackedMutation"));
        softly.assertThat(String.format(FORMATTING_TWO_DECIMAL_PLACES, ctDna.getEvent().getReportedVafPercent()))
              .isEqualTo(dod.get("reportedVafPercent"));
        softly.assertThat(String.format(FORMATTING_THREE_DECIMAL_PLACES, ctDna.getEvent().getReportedVaf()))
              .isEqualTo(dod.get("reportedVaf"));
        softly.assertThat(ctDna.getEvent().getVisitNumber()).isEqualTo(Double.parseDouble(dod.get("visitNumber")));
        softly.assertThat(ctDna.getEvent().getVisitName()).isEqualTo(dod.get("visitName"));
        softly.assertThat(ctDna.getEvent().getSampleDate()).isInSameDayAs(dod.get("sampleDate"));
    }
}
