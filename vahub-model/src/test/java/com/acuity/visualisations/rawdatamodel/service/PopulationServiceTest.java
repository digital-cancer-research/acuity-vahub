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

package com.acuity.visualisations.rawdatamodel.service;

import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.service.dod.SortAttrs;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.GroupByOptionAndParams;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.Patient;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.AGE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.CENTRE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.COUNTRY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.DATE_OF_DEATH;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.DEATH;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.DURATION_ON_STUDY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.FIRST_TREATMENT_DATE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.HEIGHT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.NONE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.RACE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.RANDOMISATION_DATE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.REASON_FOR_WITHDRAWAL;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.SEX;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.STUDY_CODE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.STUDY_NAME;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.STUDY_PART_ID;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.WEIGHT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.WITHDRAWAL;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PopulationServiceTest {

    @Autowired
    private PopulationService populationService;
    @Autowired
    private DoDCommonService doDCommonService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final Subject SUBJECT1;
    private static final Subject SUBJECT2;
    private static final Subject SUBJECT3;
    private static final Subject SUBJECT_YES_SAFETY;

    public static final List<Subject> SUBJECTS;

    private static final Subject SUBJECT_WITH_DRUG1;

    private static final Subject SUBJECT_WITH_DRUG2;

    static {
        SUBJECT1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
                .safetyPopulation("N").build();
        SUBJECT2 = Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test")
                .safetyPopulation("N").build();
        SUBJECT_WITH_DRUG1 = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
                .safetyPopulation("N")
                .drugFirstDoseDate("drug1", DateUtils.toDate("01.08.2015"))
                .drugFirstDoseDate("drug2", DateUtils.toDate("01.10.2015"))
                .build();
        SUBJECT_WITH_DRUG2 = Subject.builder().subjectId("sid2")
                .drugFirstDoseDate("drug1", DateUtils.toDate("01.08.2015"))
                .drugFirstDoseDate("drug2", DateUtils.toDate("01.10.2015"))
                .subjectCode("E02").datasetId("test")
                .safetyPopulation("N").build();
        SUBJECT_YES_SAFETY = Subject.builder().subjectId("sid3").subjectCode("E03").datasetId("test")
                .safetyPopulation("Y").build();
        SUBJECT3 = Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test").sex("Female")
                .height(165.).weight(62.).race("White").country("Russia").age(66).plannedArm("planed_arm").actualArm("actual_arm").build();
        //Arrays.asList("filter_1--N", "filter_2--Y", "filter_3--null, filter_4--null")
        HashMap<String, String> studySpecificFilters1 = new HashMap<>();
        studySpecificFilters1.put("filter_1", "Y");
        //Arrays.asList("filter_1--Y", "filter_2--null")
        HashMap<String, String> studySpecificFilters2 = new HashMap<>();
        studySpecificFilters2.put("filter_1", "Y");
        //Arrays.asList("filter_1--Y", "filter_2--null")
        SUBJECTS = newArrayList(Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
                        .datasetName("dataset1").sex("Male").height(188.).weight(70.).race("Asian").age(55).siteId("1")
                        .centerNumber("11").dateOfRandomisation(DateUtils.toDate("01.06.2015")).deathFlag("Yes")
                        .dateOfDeath(DateUtils.toDate("01.06.2017")).durationOnStudy(372)
                        .studyPart("A").clinicalStudyName("Study name").clinicalStudyCode("100B").firstTreatmentDate(DateUtils.toDate("20.02.2015"))
                        .lastTreatmentDate(DateUtils.toDate("20.02.2016"))
                        .drugDosed("drug1", "Yes")
                        .drugDosed("drug2", "No")
                        .withdrawal("Yes").dateOfWithdrawal(DateUtils.toDate("01.06.2016")).reasonForWithdrawal("some reason")
                        .plannedArm("planed_arm").actualArm("actual_arm").country("China").region("Asia")
                        .drugDiscontinued("drug1", "Yes")
                        .drugDiscontinued("drug2", "No")
                        .drugDiscontinuationMainReason("drug1", "reason_1")
                        .drugDiscontinuationMainReason("drug2", "reason_2")
                        .build(),
                Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test").sex("Female").clinicalStudyCode("100B")
                        .height(165.).weight(62.).race("White").age(66).build(),
                Subject.builder().subjectId("sid3").subjectCode("E02").datasetId("test").sex("Female").clinicalStudyCode("100B")
                        .height(165.).weight(62.).race("White").age(66).build());
    }

    @Test
    public void shouldntGetHasSafetyAsNoInPopulationForOnlyOneNo() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT1, SUBJECT_YES_SAFETY));

        boolean hasSafetyAsNoInPopulation = populationService.hasSafetyAsNoInPopulation(DATASETS);

        assertThat(hasSafetyAsNoInPopulation).isFalse();
    }

    @Test
    public void shouldntGetHasSafetyAsNoInPopulationForOnlyOneNoAgain() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT1));

        boolean hasSafetyAsNoInPopulation = populationService.hasSafetyAsNoInPopulation(DATASETS);

        assertThat(hasSafetyAsNoInPopulation).isFalse();
    }

    @Test
    public void shouldGetHasSafetyAsNoInPopulationForOnlyTwoNo() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT1, SUBJECT2, SUBJECT_YES_SAFETY));

        boolean hasSafetyAsNoInPopulation = populationService.hasSafetyAsNoInPopulation(DATASETS);

        assertThat(hasSafetyAsNoInPopulation).isTrue();
    }

    @Test
    public void shouldntGetHasSafetyAsNoInPopulationForOnlyYes() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT_YES_SAFETY));

        boolean hasSafetyAsNoInPopulation = populationService.hasSafetyAsNoInPopulation(DATASETS);

        assertThat(hasSafetyAsNoInPopulation).isFalse();
    }

    @Test
    public void shouldGetPatientList() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT1, SUBJECT2, SUBJECT_YES_SAFETY));

        List<Patient> patientList = populationService.getPatientList(DATASETS);

        assertThat(patientList).hasSize(3);
        assertThat(patientList).extracting("subjectCode").contains("E01", "E02", "E03");
    }

    @Test
    public void testGetSingleSubjectData() {

        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);

        List<Map<String, String>> singleSubjectData = populationService.getSingleSubjectData(DATASETS, "sid2", PopulationFilters.empty());

        Map<String, String> outputRow = new HashMap<>();
        outputRow.put("sex", "Female");
        outputRow.put("race", "White");
        outputRow.put("age", "66");
        outputRow.put("weight", "62");
        outputRow.put("height", "165");
        outputRow.put("eventId", "sid2");

        assertThat(singleSubjectData).hasSize(1);
        assertThat(singleSubjectData.get(0).entrySet()).hasSize(6);
        assertThat(singleSubjectData.get(0).entrySet()).containsAll(outputRow.entrySet());
    }

    @Test
    public void shouldReturnDrugsInSortedOrder() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(SUBJECT_WITH_DRUG1, SUBJECT_WITH_DRUG2));

        AxisOptions<PopulationGroupByOptions> axis = populationService.getAvailableBarChartXAxisOptions(DATASETS, PopulationFilters.empty());
        assertThat(axis.getDrugs()).isSorted();
    }

    @Test
    public void shouldGetAllBarChartXAxisForAcuityDatasets() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);

        AxisOptions<PopulationGroupByOptions> axis = populationService.getAvailableBarChartXAxisOptions(DATASETS, PopulationFilters.empty());
        List<AxisOption<PopulationGroupByOptions>> options = axis.getOptions();
        softly.assertThat(options).hasSize(18);
        softly.assertThat(options).extracting(AxisOption::getGroupByOption).containsExactly(
                NONE,
                STUDY_CODE,
                STUDY_NAME,
                STUDY_PART_ID,
                DURATION_ON_STUDY,
                RANDOMISATION_DATE,
                WITHDRAWAL,
                REASON_FOR_WITHDRAWAL,
                CENTRE,
                COUNTRY,
                SEX,
                RACE,
                AGE,
                WEIGHT,
                HEIGHT,
                FIRST_TREATMENT_DATE,
                DEATH,
                DATE_OF_DEATH);
    }

    @Test
    public void shouldGetAllBarChartColorByOptionsForAcuityDatasets() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);

        List<TrellisOptions<PopulationGroupByOptions>> options = populationService.getBarChartColorByOptions(DATASETS, PopulationFilters.empty());

        softly.assertThat(options).hasSize(17);
        softly.assertThat(options).extracting(TrellisOptions::getTrellisOptions).asList().containsExactly(
                asList("100B"),
                asList("Study name", Attributes.DEFAULT_EMPTY_VALUE),
                asList("A", Attributes.DEFAULT_EMPTY_VALUE),
                asList("372-372", Attributes.DEFAULT_EMPTY_VALUE),
                asList("01-JUN-15 - 01-JUN-15", Attributes.DEFAULT_EMPTY_VALUE),
                asList("Yes", Attributes.DEFAULT_EMPTY_VALUE),
                asList("some reason", Attributes.DEFAULT_EMPTY_VALUE),
                asList("11", Attributes.DEFAULT_EMPTY_VALUE),
                asList("China", Attributes.DEFAULT_EMPTY_VALUE),
                asList("Female", "Male"),
                asList("Asian", "White"),
                asList("55-55", "66-66"),
                asList("62-62", "70-70"),
                asList("165-165", "188-188"),
                asList("20-FEB-15 - 20-FEB-15", Attributes.DEFAULT_EMPTY_VALUE),
                asList("Yes", Attributes.DEFAULT_EMPTY_VALUE),
                asList("01-JUN-17 - 01-JUN-17", Attributes.DEFAULT_EMPTY_VALUE));
    }

    @Test
    public void shouldGetBarChartColorByOptionsForAcuityDatasets() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(asList(SUBJECT3));

        List<TrellisOptions<PopulationGroupByOptions>> options = populationService.getBarChartColorByOptions(DATASETS, PopulationFilters.empty());

        softly.assertThat(options).hasSize(6);
        softly.assertThat(options).extracting(TrellisOptions::getTrellisOptions).asList()
                .containsExactly(
                        asList("Russia"),
                        asList("Female"),
                        asList("White"),
                        asList("66-66"),
                        asList("62-62"),
                        asList("165-165"));
    }

    @Test
    public void shouldGetBarChart() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);

        Map<ChartGroupBySetting, GroupByOptionAndParams<Subject, PopulationGroupByOptions>> options = new HashMap<>();
        options.put(COLOR_BY, new GroupByOptionAndParams(PopulationGroupByOptions.STUDY_CODE, new HashMap<>()));
        options.put(X_AXIS, new GroupByOptionAndParams(PopulationGroupByOptions.WITHDRAWAL, new HashMap<>()));

        ChartGroupByOptions<Subject, PopulationGroupByOptions> sett = new ChartGroupByOptions(options, new HashSet<>());
        ChartGroupByOptionsFiltered<Subject, PopulationGroupByOptions> settings = new ChartGroupByOptionsFiltered(sett, new ArrayList<>());

        List<TrellisedBarChart<Subject, PopulationGroupByOptions>> result = populationService.getBarChart(DATASETS,
                settings, PopulationFilters.empty(), CountType.COUNT_OF_SUBJECTS);

        softly.assertThat(result).size().isEqualTo(1);
        softly.assertThat(result.get(0).getTrellisedBy()).isEmpty();

        List<? extends OutputBarChartData> data = result.get(0).getData();
        softly.assertThat(data).size().isEqualTo(1);
        softly.assertThat(data.get(0).getName()).isEqualTo("100B");
        softly.assertThat(data.get(0).getCategories()).containsExactlyInAnyOrder(Attributes.DEFAULT_EMPTY_VALUE, "Yes");

        List<OutputBarChartEntry> series = data.get(0).getSeries();
        softly.assertThat(series).size().isEqualTo(2);

        softly.assertThat(series.get(0).getCategory()).isEqualTo("Yes");
        softly.assertThat(series.get(0).getValue()).isEqualTo(1.0);
        softly.assertThat(series.get(0).getTotalSubjects()).isEqualTo(1);
        softly.assertThat(series.get(0).getRank()).isEqualTo(1);

        softly.assertThat(series.get(1).getCategory()).isEqualTo(Attributes.DEFAULT_EMPTY_VALUE);
        softly.assertThat(series.get(1).getValue()).isEqualTo(2.0);
        softly.assertThat(series.get(1).getTotalSubjects()).isEqualTo(2);
        softly.assertThat(series.get(1).getRank()).isEqualTo(2);
    }

    @Test
    public void shouldGetBarChartRanged() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);

        Map<ChartGroupBySetting, GroupByOptionAndParams<Subject, PopulationGroupByOptions>> options = new HashMap<>();
        options.put(COLOR_BY, new GroupByOptionAndParams(PopulationGroupByOptions.STUDY_CODE, new HashMap<>()));
        options.put(X_AXIS, new GroupByOptionAndParams(PopulationGroupByOptions.DATE_OF_DEATH, new HashMap<>()));

        ChartGroupByOptions<Subject, PopulationGroupByOptions> sett = new ChartGroupByOptions(options, new HashSet<>());
        ChartGroupByOptionsFiltered<Subject, PopulationGroupByOptions> settings = new ChartGroupByOptionsFiltered(sett, new ArrayList<>());

        List<TrellisedBarChart<Subject, PopulationGroupByOptions>> result = populationService.getBarChart(DATASETS,
                settings, PopulationFilters.empty(), CountType.COUNT_OF_SUBJECTS);

        softly.assertThat(result).size().isEqualTo(1);
        softly.assertThat(result.get(0).getTrellisedBy()).isEmpty();

        List<? extends OutputBarChartData> data = result.get(0).getData();
        softly.assertThat(data).size().isEqualTo(1);
        softly.assertThat(data.get(0).getName()).isEqualTo("100B");
        softly.assertThat(data.get(0).getCategories()).containsExactly("01-JUN-17 - 01-JUN-17", "(Empty)");

        List<OutputBarChartEntry> series = data.get(0).getSeries();
        softly.assertThat(series).size().isEqualTo(2);
        softly.assertThat(series.get(0).getCategory()).isEqualTo("01-JUN-17 - 01-JUN-17");
        softly.assertThat(series.get(0).getValue()).isEqualTo(1.0);
        softly.assertThat(series.get(0).getTotalSubjects()).isEqualTo(1);
        softly.assertThat(series.get(0).getRank()).isEqualTo(1);

        softly.assertThat(series.get(1).getCategory()).isEqualTo("(Empty)");
        softly.assertThat(series.get(1).getValue()).isEqualTo(2.0);
        softly.assertThat(series.get(1).getTotalSubjects()).isEqualTo(2);
        softly.assertThat(series.get(1).getRank()).isEqualTo(2);
    }

    @Test
    public void shouldGetBarChartSelection() {
        // Given
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Subject, PopulationGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, PopulationGroupByOptions.SEX.getGroupByOptionAndParams());
        final HashMap<PopulationGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(COLOR_BY, "Male");

        // When
        SelectionDetail selectionDetails = populationService.getSelectionDetails(DATASETS, PopulationFilters.empty(),
                ChartSelection.of(settings.build(), Collections.singletonList(
                        ChartSelectionItem.of(selectedTrellises, selectedItems)
                )));

        // Then
        softly.assertThat(selectionDetails.getEventIds()).containsOnly("sid1");
        softly.assertThat(selectionDetails.getSubjectIds()).containsOnly("sid1");
        softly.assertThat(selectionDetails.getTotalEvents()).isEqualTo(3);
        softly.assertThat(selectionDetails.getTotalSubjects()).isEqualTo(3);
    }

    @Test
    public void shouldWriteSelectedDetailsOnDemandCsv() throws IOException {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        populationService.writeSelectedDetailsOnDemandCsv(DATASETS, Collections.singleton("sid1"), new OutputStreamWriter(baos));
        String[] lines = baos.toString().split("\n");
        softly.assertThat(lines.length).isEqualTo(2);
        String[] subjData = lines[1].split(",");
        softly.assertThat(subjData[0]).isEqualTo("100B");
        softly.assertThat(subjData[1]).isEqualTo("A");
        softly.assertThat(subjData[7]).isEqualTo("some reason");
    }

    @Test
    public void shouldWriteEmptyCsvIfNoSubjectsExist() throws IOException {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        populationService.writeSelectedDetailsOnDemandCsv(DATASETS, Collections.emptySet(), new OutputStreamWriter(baos));
        softly.assertThat(baos.toString()).isEmpty();
    }

    @Test
    public void shouldGetDoDColumnsForAcuity() {
        final Map<String, String> doDColumns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, SUBJECTS);

        softly.assertThat(doDColumns).hasSize(21);
        softly.assertThat(doDColumns.keySet()).containsOnly(
                "studyId",
                "studyPart",
                "subjectId",
                "durationOnStudy",
                "dateOfRandomisation",
                "withdrawal",
                "dateOfWithdrawal",
                "reasonForWithdrawal",
                "deathFlag",
                "dateOfDeath",
                "drug2--drugsDosed",
                "drug1--drugsDosed",
                "drug2--drugsDiscontinued",
                "drug1--drugsDiscontinued",
                "drug2--drugDiscontinuationMainReason",
                "drug1--drugDiscontinuationMainReason",
                "sex",
                "race",
                "age",
                "centerNumber",
                "country"
        );
        softly.assertThat(doDColumns.values()).containsOnly(
                "Study id",
                "Study Part id",
                "Subject id",
                "Duration on Study",
                "Date of Randomisation",
                "Withdrawal/Completion",
                "Date of Withdrawal/Completion",
                "Main Reason for Withdrawal/Completion",
                "Death",
                "Date of death",
                "drug2 Dosed",
                "drug1 Dosed",
                "drug2 Discontinued",
                "drug1 Discontinued",
                "drug2 Main Reason for Discontinuation",
                "drug1 Main Reason for Discontinuation",
                "Sex",
                "Race",
                "Age",
                "Centre",
                "Country"
        );
    }

    @Test
    public void shouldGetDoDForPopulationForAcuity() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);

        Set<String> ids = SUBJECTS.stream().map(Subject::getId).collect(toSet());

        List<Map<String, String>> doDData = populationService.getDetailsOnDemandData(DATASETS, ids, Collections.singletonList(new SortAttrs("eventId", false)), 0, Integer.MAX_VALUE);

        softly.assertThat(doDData).hasSize(SUBJECTS.size());

        Subject subject = SUBJECTS.get(0);
        Map<String, String> dod = doDData.stream().filter(d -> Objects.equals(d.get("eventId"), subject.getSubjectId())).findAny().get();

        softly.assertThat(dod.size()).isEqualTo(27);
        softly.assertThat(subject.getSubjectId()).isEqualTo(dod.get("eventId"));
        softly.assertThat(subject.getRace()).isEqualTo(dod.get("race"));
        softly.assertThat(subject.getDurationOnStudy().toString()).isEqualTo(dod.get("durationOnStudy"));
        softly.assertThat(DaysUtil.toString(subject.getDateOfDeath())).isEqualTo(dateSubstring(dod.get("dateOfDeath").toString()));
        softly.assertThat(subject.getSex()).isEqualTo(dod.get("sex"));
        softly.assertThat(subject.getCenterNumber()).isEqualTo(dod.get("centerNumber"));
        softly.assertThat(subject.getCountry()).isEqualTo(dod.get("country"));
        softly.assertThat(subject.getDoseCohort()).isEqualTo(dod.get("doseCohort"));
        softly.assertThat(DaysUtil.toString(subject.getDateOfWithdrawal())).isEqualTo(dateSubstring(dod.get("dateOfWithdrawal").toString()));
        softly.assertThat(subject.getWithdrawal()).isEqualTo(dod.get("withdrawal"));
        softly.assertThat(DaysUtil.toString(subject.getDateOfRandomisation())).isEqualTo(dateSubstring(dod.get("dateOfRandomisation").toString()));
        softly.assertThat(subject.getStudyPart()).isEqualTo(dod.get("studyPart"));
        softly.assertThat(subject.getClinicalStudyCode()).isEqualTo(dod.get("studyId"));
        softly.assertThat(subject.getReasonForWithdrawal()).isEqualTo(dod.get("reasonForWithdrawal"));
        softly.assertThat(subject.getAge().toString()).isEqualTo(dod.get("age"));
        softly.assertThat(subject.getOtherCohort()).isEqualTo(dod.get("otherCohort"));
        softly.assertThat(subject.getEthnicGroup()).isEqualTo(dod.get("ethnicGroup"));
        softly.assertThat(subject.getRandomised()).isEqualTo(dod.get("randomised"));
        Map<String, String> drugDosed = subject.getDrugsDosed();
        List<String> drugs = new ArrayList<>(drugDosed.keySet());
        softly.assertThat(subject.getDrugsDosed().get(drugs.get(0))).isEqualTo(dod.get(drugs.get(0) + "--drugsDosed"));
        softly.assertThat(subject.getDrugsDosed().get(drugs.get(1))).isEqualTo(dod.get(drugs.get(1) + "--drugsDosed"));
        drugs = new ArrayList<>(subject.getDrugsDiscontinued().keySet());
        softly.assertThat(subject.getDrugsDiscontinued().get(drugs.get(0))).isEqualTo(dod.get(drugs.get(0) + "--drugsDiscontinued"));
        softly.assertThat(subject.getDrugsDiscontinued().get(drugs.get(1))).isEqualTo(dod.get(drugs.get(1) + "--drugsDiscontinued"));
        drugs = new ArrayList<>(subject.getDrugDiscontinuationMainReason().keySet());
        softly.assertThat(subject.getDrugDiscontinuationMainReason().get(drugs.get(0))).isEqualTo(dod.get(drugs.get(0) + "--drugDiscontinuationMainReason"));
        softly.assertThat(subject.getDrugDiscontinuationMainReason().get(drugs.get(1))).isEqualTo(dod.get(drugs.get(1) + "--drugDiscontinuationMainReason"));
    }

    private String dateSubstring(String date) {
        return date.substring(0, date.indexOf("T"));
    }
}
