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
import com.acuity.visualisations.rawdatamodel.dataproviders.AssessedTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.AssessedTargetLesionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.generators.AssessedTargetLesionGenerator;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ATLGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import com.acuity.visualisations.rawdatamodel.vo.NonTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.OutputLineChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart.TrellisedLineFloatChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_2_ACUITY_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.MISSING;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TumourLineChartServiceTest {

    @Autowired
    private TumourLineChartService tumourService;
    @Autowired
    private DoDCommonService doDCommonService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private AssessedTargetLesionDatasetsDataProvider tumourDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    private List<AssessedTargetLesion> tumours = AssessedTargetLesionGenerator.generateTumours();
    private List<Subject> population = AssessedTargetLesionGenerator.generateTumourPopulation();

    @Test
    public void getAvailableLineChartXAxisOptionsTest() {
        //Given
        when(tumourDatasetsDataProvider.loadDataByVisit(any(Datasets.class))).thenReturn(tumours);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);

        //When
        AxisOptions<ATLGroupByOptions> result
                = tumourService.getAvailableLineChartXAxis(DATASETS, AssessedTargetLesionFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(result.getOptions()).extracting(AxisOption::getGroupByOption).containsOnly(
                ATLGroupByOptions.DAYS_SINCE_FIRST_DOSE, ATLGroupByOptions.ASSESSMENT_WEEK_WITH_BASELINE);
    }

    @Test
    public void testGetLineChartSinceFirstDose() {

        when(tumourDatasetsDataProvider.loadDataByVisit(any(Datasets.class))).thenReturn(tumours);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<AssessedTargetLesion, ATLGroupByOptions> settings = getLineChartSettings();
        settings.withOption(X_AXIS, ATLGroupByOptions.DAYS_SINCE_FIRST_DOSE.getGroupByOptionAndParams());

        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<AssessedTargetLesion, ATLGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        // When
        List<TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, OutputLineChartData>> lineChart = tumourService
                .getTumourAllChangesOnLinechart(DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(),
                        PopulationFilters.empty(), settingsWithFilterBy.build());

        TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, ? extends OutputLineChartData> data = lineChart.get(0);
        softly.assertThat(data.getData()).extracting(OutputLineChartData::getSeriesBy)
                .containsExactlyInAnyOrder("subject1", "subject2", "subject4");
        softly.assertThat(data.getData().stream().filter(d -> d.getSeriesBy().equals("subject2")).findAny().get().getSeries())
                .extracting(o -> o.getX().toString(), OutputLineChartEntry::getY, OutputLineChartEntry::getName, OutputLineChartEntry::getColor)
                .containsExactly(tuple("27", -30.0, "Partial Response", "#0000FF"),
                        tuple("35", -80.0, "Complete Response", "#800080"),
                        tuple("69", -100.0, "Partial Response", "#0000FF"));
    }

    @Test
    public void testGetLineChartByWeekNumbers() {

        when(tumourDatasetsDataProvider.loadDataByVisit(any(Datasets.class))).thenReturn(tumours);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<AssessedTargetLesion, ATLGroupByOptions> settings = getLineChartSettings();
        settings.withOption(X_AXIS, ATLGroupByOptions.ASSESSMENT_WEEK_WITH_BASELINE.getGroupByOptionAndParams());
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<AssessedTargetLesion, ATLGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        // When
        List<TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, OutputLineChartData>> lineChart = tumourService
                .getTumourAllChangesOnLinechart(DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(),
                        PopulationFilters.empty(), settingsWithFilterBy.build());

        TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, ? extends OutputLineChartData> data = lineChart.get(0);
        softly.assertThat(data.getData()).extracting(OutputLineChartData::getSeriesBy)
                .containsExactlyInAnyOrder("subject1", "subject2", "subject4");
        softly.assertThat(data.getData().stream().filter(d -> d.getSeriesBy().equals("subject2")).findAny().get().getSeries())
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY, OutputLineChartEntry::getName, OutputLineChartEntry::getColor)
                .containsExactly(tuple("Baseline", -30.0, "Partial Response", "#0000FF"),
                        tuple("Week 4", -80.0, "Complete Response", "#800080"),
                        tuple("Week 10", -100.0, "Partial Response", "#0000FF"));


        softly.assertThat(data.getData().stream().filter(d -> d.getSeriesBy().equals("subject1")).findAny().get().getSeries())
                .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY, OutputLineChartEntry::getName, OutputLineChartEntry::getColor)
                .containsExactly(tuple("Baseline", 0.0, "Partial Response", "#0000FF"),
                        tuple("Week 4", -25.0, "Partial Response", "#0000FF"));
    }

    @Test
    public void testGetLineChartSelectionForPercentChange() {

        when(tumourDatasetsDataProvider.loadDataByVisit(any(Datasets.class))).thenReturn(tumours);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);

        final HashMap<ATLGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem1 = new HashMap<>();
        selectedItem1.put(SERIES_BY, "subject1");
        selectedItem1.put(X_AXIS, 50);
        selectedItem1.put(Y_AXIS, -20.0);

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem2 = new HashMap<>();
        selectedItem2.put(SERIES_BY, "subject2");
        selectedItem2.put(X_AXIS, 35);
        selectedItem2.put(Y_AXIS, -80); // selection works with both floating point and integer values

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem3 = new HashMap<>();
        selectedItem3.put(SERIES_BY, "subject3");
        selectedItem3.put(X_AXIS, 9);
        selectedItem3.put(Y_AXIS, -30.0);

        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem4 = new HashMap<>();
        selectedItem4.put(SERIES_BY, "subject2");
        selectedItem4.put(X_AXIS, 69);
        selectedItem4.put(Y_AXIS, -100.0);

        SelectionDetail selectionDetails = tumourService.getLineChartSelectionDetails(DUMMY_2_ACUITY_DATASETS,
                AssessedTargetLesionFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(getDefaultLineChartSelectionSettings(), newArrayList(ChartSelectionItem.of(selectedTrellises, selectedItem1),
                        ChartSelectionItem.of(selectedTrellises, selectedItem2), ChartSelectionItem.of(selectedTrellises, selectedItem3),
                        ChartSelectionItem.of(selectedTrellises, selectedItem4))));

        softly.assertThat(selectionDetails).extracting(s -> s.getEventIds().size(), s -> s.getSubjectIds().size(), SelectionDetail::getTotalEvents,
                SelectionDetail::getTotalSubjects).containsExactly(4, 3, 9, 4);
    }

    @Test
    public void shouldGetDetailsOnDemand() {

        AssessedTargetLesion atlWithNewLesions = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("atl1")
                .targetLesionRaw(TargetLesionRaw.builder().visitNumber(1).lesionDate(DaysUtil.toDate("2000-05-15")).build())
                .assessmentRaw(AssessmentRaw.builder().newLesionSinceBaseline("TRUE").responseShort("PR").build())
                .nonTargetLesionRaw(NonTargetLesionRaw.builder().responseShort("PD").build())
                .build(),
                Subject.builder().subjectId("subjectId1").subjectCode("subject1").studyPart("part1").clinicalStudyCode("study1").build());

        AssessedTargetLesion atlNoNewLesionsInfo = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("atl2")
                .targetLesionRaw(TargetLesionRaw.builder().visitNumber(2).lesionDate(DaysUtil.toDate("2000-05-25")).build())
                .assessmentRaw(AssessmentRaw.builder().build())
                .nonTargetLesionRaw(NonTargetLesionRaw.builder().responseShort("CR").build())
                .build(),
                Subject.builder().subjectId("subjectId1").subjectCode("subject1").studyPart("part1").clinicalStudyCode("study1").build());

        AssessedTargetLesion atlNoAssessmentNoNtl = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("atl3")
                .targetLesionRaw(TargetLesionRaw.builder().lesionNumber("1").visitNumber(1)
                        .lesionDate(DaysUtil.toDate("2000-01-01")).build()).build(),
                Subject.builder().subjectId("subjectId2").subjectCode("subject2").studyPart("part1").clinicalStudyCode("study1").build());

        List<AssessedTargetLesion> atls = newArrayList(atlWithNewLesions, atlNoNewLesionsInfo, atlNoAssessmentNoNtl);
        Set<String> atlIds = atls.stream().map(AssessedTargetLesion::getId).collect(toSet());

        // When
        when(tumourDatasetsDataProvider.loadDataByVisit(any(Datasets.class))).thenReturn(atls);
        List<Map<String, String>> doDData = tumourService.getDetailsOnDemandData(DUMMY_ACUITY_DATASETS, atlIds,
                Collections.emptyList(), 0, Integer.MAX_VALUE);

        // Then
        AssessedTargetLesion atl = atls.get(0);
        Map<String, String> dod = doDData.get(0);

        softly.assertThat(doDData).hasSize(3);
        softly.assertThat(dod.size()).isEqualTo(8);

        softly.assertThat(atl.getSubject().getSubjectCode()).isEqualTo(dod.get("subjectId"));
        softly.assertThat(atl.getSubject().getClinicalStudyCode()).isEqualTo(dod.get("studyId"));
        softly.assertThat(atl.getEvent().getVisitNumber().toString()).isEqualTo(dod.get("visitNumber"));
        softly.assertThat("Yes").isEqualTo(dod.get("newLesions"));
        softly.assertThat("PR").isEqualTo(dod.get("overallResponse"));
        softly.assertThat("PD").isEqualTo(dod.get("ntlResponse"));

        Map<String, String> dodNoAsmtInfo = doDData.get(1);
        softly.assertThat(MISSING).isEqualTo(dodNoAsmtInfo.get("newLesions"));
        softly.assertThat(MISSING).isEqualTo(dodNoAsmtInfo.get("overallResponse"));
        softly.assertThat("CR").isEqualTo(dodNoAsmtInfo.get("ntlResponse"));

        Map<String, String> dodNoAssessment = doDData.get(2);
        softly.assertThat(MISSING).isEqualTo(dodNoAssessment.get("newLesions"));
        softly.assertThat(MISSING).isEqualTo(dodNoAssessment.get("overallResponse"));
        softly.assertThat(MISSING).isEqualTo(dodNoAssessment.get("ntlResponse"));
    }

    @Test
    public void testGetLineChartByLesion() {

        Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1").firstTreatmentDate(DaysUtil.toDate("2000-04-05"))
                .baselineDate(DaysUtil.toDate("2000-05-01")).build();
        Subject subject2 = Subject.builder().subjectId("subjectId2").subjectCode("subject2").firstTreatmentDate(DaysUtil.toDate("2000-01-01"))
                .baselineDate(DaysUtil.toDate("2000-01-01")).build();

        ArrayList<AssessedTargetLesion> notGroupedByVisit = newArrayList(new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id11").subjectId("subjectId1")
                        .targetLesionRaw(TargetLesionRaw.builder()
                                .visitNumber(1).visitDate(DaysUtil.toDate("2000-05-15")).lesionNumber("1")
                                .lesionDate(DaysUtil.toDate("2000-05-15")).lesionDiameter(10)
                                .lesionPercentageChangeFromBaseline(20.).build())
                        .bestResponse("Partial Response").response("Partial Response")
                        .assessmentFrequency(2)
                        .build(), subject1),
                new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id12").subjectId("subjectId1")
                        .targetLesionRaw(TargetLesionRaw.builder()
                                .visitNumber(1).visitDate(DaysUtil.toDate("2000-05-15")).lesionNumber("2")
                                .lesionDate(DaysUtil.toDate("2000-05-15")).lesionDiameter(15)
                                .lesionPercentageChangeFromBaseline(30.).build())
                        .assessmentFrequency(2)
                        .build(), subject1),
                new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id21").subjectId("subjectId1")
                        .targetLesionRaw(TargetLesionRaw.builder()
                                .visitNumber(2).visitDate(DaysUtil.toDate("2000-05-25")).lesionNumber("1")
                                .lesionDiameter(5).lesionPercentageChangeFromBaseline(10.)
                                .lesionDate(DaysUtil.toDate("2000-05-25")).build())
                        .bestResponse("Partial Response")
                        .assessmentFrequency(2)
                        .response("Partial Response")
                        .build(), subject1),
                new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id22").subjectId("subjectId1")
                        .targetLesionRaw(TargetLesionRaw.builder()
                                .visitNumber(2).visitDate(DaysUtil.toDate("2000-05-25")).lesionNumber("2")
                                .lesionDate(DaysUtil.toDate("2000-05-25")).lesionDiameter(8)
                                .lesionPercentageChangeFromBaseline(16.).build())
                        .assessmentFrequency(2)
                        .bestResponse("Partial Response").response("Partial Response")
                        .build(), subject1),
                new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id3").subjectId("subjectId2")
                        .targetLesionRaw(TargetLesionRaw.builder()
                                                        .baseline(true)
                                .visitNumber(1).visitDate(DaysUtil.toDate("2000-01-10")).lesionNumber("1")
                                .lesionDate(DaysUtil.toDate("2000-01-10")).lesionDiameter(17)
                                .lesionPercentageChangeFromBaseline(35.).build())
                        .assessmentFrequency(2)
                        .bestResponse("Partial Response").response("Partial Response")
                        .build(), subject2),
                new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("id4").subjectId("subjectId2")
                        .targetLesionRaw(TargetLesionRaw.builder()
                                .visitNumber(2).visitDate(DaysUtil.toDate("2000-01-20")).lesionNumber("1")
                                .lesionDate(DaysUtil.toDate("2000-01-20")).lesionDiameter(19)
                                .lesionPercentageChangeFromBaseline(40.).build())
                        .assessmentFrequency(2)
                        .bestResponse("Partial Response").response("Partial Response")
                        .build(), subject2));

        when(tumourDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(notGroupedByVisit);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<AssessedTargetLesion, ATLGroupByOptions> settingsWithDaysOption
                = getLineChartByLesionSettings(ATLGroupByOptions.DAYS_SINCE_FIRST_DOSE);

        // When
        List<TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, OutputLineChartData>> lineChart = tumourService
                .getTumourChangesByLesionOnLinechart(DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(),
                        PopulationFilters.empty(), ChartGroupByOptionsFiltered.builder(settingsWithDaysOption.build()).build());

        TrellisedLineFloatChart<AssessedTargetLesion, ATLGroupByOptions, ? extends OutputLineChartData> data = lineChart.get(0);
        softly.assertThat(data.getData()).extracting(OutputLineChartData::getSeriesBy)
                .containsExactlyInAnyOrder("subject1 lesion 1", "subject1 lesion 2", "subject2 lesion 1");
        softly.assertThat(data.getData().stream().filter(d -> d.getSeriesBy().equals("subject1 lesion 1")).findAny().get().getSeries())
                .extracting(o -> o.getX().toString(), OutputLineChartEntry::getY, OutputLineChartEntry::getName, OutputLineChartEntry::getColor)
                .containsExactly(tuple("40", 20.0, "Partial Response", "#0000FF"),
                        tuple("50", 10.0, "Partial Response", "#0000FF"));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<AssessedTargetLesion, ATLGroupByOptions> settingsWithWeeksOption
                = getLineChartByLesionSettings(ATLGroupByOptions.ASSESSMENT_WEEK_WITH_BASELINE);

        // When
        lineChart = tumourService.getTumourChangesByLesionOnLinechart(DUMMY_2_ACUITY_DATASETS, AssessedTargetLesionFilters.empty(),
                        PopulationFilters.empty(), ChartGroupByOptionsFiltered.builder(settingsWithWeeksOption.build()).build());
        data = lineChart.get(0);
        softly.assertThat(data.getData()).extracting(OutputLineChartData::getSeriesBy)
              .containsExactlyInAnyOrder("subject1 lesion 1", "subject1 lesion 2", "subject2 lesion 1");
        softly.assertThat(data.getData().stream().filter(d -> d.getSeriesBy().equals("subject1 lesion 1")).findAny().get().getSeries())
              .extracting(OutputLineChartEntry::getX, OutputLineChartEntry::getY, OutputLineChartEntry::getName, OutputLineChartEntry::getColor)
              .containsExactly(
                      tuple("Week 2", 20., "Partial Response", "#0000FF"),
                      tuple("Week 4", 10., "Partial Response", "#0000FF"));
    }

    private ChartGroupByOptions.ChartGroupBySettingsBuilder<AssessedTargetLesion, ATLGroupByOptions> getLineChartSettings() {
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<AssessedTargetLesion, ATLGroupByOptions> settings = ChartGroupByOptions.builder();

        settings.withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, ATLGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, ATLGroupByOptions.ASSESSMENT_RESPONSE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.NAME, ATLGroupByOptions.ASSESSMENT_RESPONSE.getGroupByOptionAndParams())
                .withOption(Y_AXIS, ATLGroupByOptions.PERCENTAGE_CHANGE.getGroupByOptionAndParams());
        return settings;
    }

    private ChartGroupByOptions.ChartGroupBySettingsBuilder<AssessedTargetLesion, ATLGroupByOptions> getLineChartByLesionSettings(ATLGroupByOptions atlGroupByOptions) {
        final ChartGroupByOptions.ChartGroupBySettingsBuilder<AssessedTargetLesion, ATLGroupByOptions> settings = ChartGroupByOptions.builder();

        settings.withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, ATLGroupByOptions.SUBJECT_LESION.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY, ATLGroupByOptions.ASSESSMENT_RESPONSE.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.NAME, ATLGroupByOptions.ASSESSMENT_RESPONSE.getGroupByOptionAndParams())
                .withOption(Y_AXIS, ATLGroupByOptions.LESION_PERCENTAGE_CHANGE.getGroupByOptionAndParams())
                .withOption(X_AXIS, atlGroupByOptions.getGroupByOptionAndParams());
        return settings;
    }

    private ChartGroupByOptions<AssessedTargetLesion, ATLGroupByOptions> getDefaultLineChartSelectionSettings() {
        return ChartGroupByOptions.<AssessedTargetLesion, ATLGroupByOptions>builder()
                .withOption(SERIES_BY, ATLGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .withOption(X_AXIS, ATLGroupByOptions.DAYS_SINCE_FIRST_DOSE.getGroupByOptionAndParams())
                .withOption(Y_AXIS, ATLGroupByOptions.PERCENTAGE_CHANGE.getGroupByOptionAndParams())
                .build();
    }
}
