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

import com.acuity.visualisations.rawdatamodel.dataproviders.ChemotherapyDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DrugDoseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.RadiotherapyDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.SubjectExtDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.TherapyFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.TumourTherapyGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.ChemotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.RadiotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.SubjectExtRaw;
import com.acuity.visualisations.rawdatamodel.vo.TumourTherapy;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputColumnRangeChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputColumnRangeChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputMarkEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedColumnRangeChart;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectExt;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.service.compatibility.ColoringService.Colors.LIGHTSEAGREEN;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.END;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.SERIES_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.START;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.Y_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.DOSE_COHORT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.MAX_DOSE_PER_ADMIN_OF_DRUG;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions.OTHER_COHORT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.TumourTherapyGroupByOptions.ALL_PRIOR_THERAPIES;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.TumourTherapyGroupByOptions.MOST_RECENT_THERAPY;
import static com.acuity.visualisations.rawdatamodel.util.Constants.ALL;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NO;
import static com.acuity.visualisations.rawdatamodel.util.Constants.SUMMARY;
import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy.RADIOTHERAPY_LABEL;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TumourColumnRangeServiceTest {

    @Autowired
    private TumourColumnRangeService tumourService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private ChemotherapyDatasetsDataProvider chemotherapyDatasetsDataProvider;
    @MockBean
    private RadiotherapyDatasetsDataProvider radiotherapyDatasetsDataProvider;
    @MockBean
    private DrugDoseDatasetsDataProvider drugDoseDatasetsDataProvider;
    @MockBean
    private SubjectExtDatasetsDataProvider subjectExtDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);
        when(subjectExtDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjectsExt);
        when(radiotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(radioData);
        when(chemotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(chemoData);
        when(drugDoseDatasetsDataProvider.loadDosesForTumourColumnRangeService(any(Datasets.class))).thenReturn(doses);
    }

    private Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1").firstTreatmentDate(toDate("2000-04-05"))
            .lastTreatmentDate(toDate("2000-05-20")).build();
    private Subject subject2 = Subject.builder().subjectId("subjectId2").subjectCode("subject2").firstTreatmentDate(toDate("2000-01-10"))
            .lastTreatmentDate(toDate("2000-05-21")).build();
    private Subject subject3 = Subject.builder().subjectId("subjectId3").subjectCode("subject3").firstTreatmentDate(toDate("2000-02-21"))
            .lastTreatmentDate(toDate("2000-05-21")).build();
    private List<Subject> population = Arrays.asList(subject1, subject2, subject3);

    private List<Chemotherapy> chemoData = Arrays.asList(
            new Chemotherapy(ChemotherapyRaw.builder().id("chId1").subjectId(subject1.getSubjectId())
                    .startDate(toDate("2000-02-20")).endDate(toDate("2000-03-30"))
                    .therapyClass("class 1").preferredMed("med 2").treatmentStatus("status 1").bestResponse("bestResponse")
                    .failureReason("failureReason")
                    .numOfCycles(5)
                    .build(),
                    subject1),

            new Chemotherapy(ChemotherapyRaw.builder().id("chId2").subjectId(subject2.getSubjectId())
                    .endDate(toDate("2000-01-09"))
                    .therapyClass("class 1").preferredMed("med 1").bestResponse("bestResponse 2")
                    .failureReason("failureReason 2").numOfCycles(10).build(), subject2),

            new Chemotherapy(ChemotherapyRaw.builder().id("chId3").subjectId(subject3.getSubjectId())
                    .startDate(toDate("2000-02-10")).endDate(subject3.getFirstTreatmentDate()).build(), subject3),

            new Chemotherapy(ChemotherapyRaw.builder().id("chId4").subjectId(subject3.getSubjectId())
                    .startDate(toDate("2000-01-01")).endDate(toDate("2000-01-10"))
                    .preferredMed("med 1").build(), subject3));

    private List<Radiotherapy> radioData = Arrays.asList(new Radiotherapy(RadiotherapyRaw.builder().id("radId1").subjectId(subject1.getSubjectId())
                    .startDate(toDate("2000-01-01")).endDate(toDate("2000-03-20"))
                    .dose(10.2).numOfDoses(5).treatmentStatus("status 2").build(), subject1),
            new Radiotherapy(RadiotherapyRaw.builder().id("radId2").subjectId(subject2.getSubjectId())
                    .endDate(toDate("1999-12-01"))
                    .dose(2.5).numOfDoses(6).treatmentStatus("status 3")
                    .build(), subject2),
            new Radiotherapy(RadiotherapyRaw.builder().id("radId3").subjectId(subject2.getSubjectId())
                    .startDate(toDate("2000-01-05")).endDate(toDate("2000-01-09"))
                    .treatmentStatus("status 4").build(), subject2));

    private List<DrugDose> doses = newArrayList(new DrugDose(DrugDoseRaw.builder().id("doseId1").dose(25.0).build(), subject1),
            new DrugDose(DrugDoseRaw.builder().id("doseId2").dose(10.0).build(), subject2),
            new DrugDose(DrugDoseRaw.builder().id("doseId3").build(), subject2), // inactive dose
            new DrugDose(DrugDoseRaw.builder().id("doseId4").dose(50.0).build(), subject3));

    private List<SubjectExt> subjectsExt = newArrayList(new SubjectExt(SubjectExtRaw.builder().subjectId(subject1.getId())
                    .diagnosisDate(toDate("1999-01-01")).daysFromDiagnosisDate(458).recentProgressionDate(toDate("1999-01-01"))
                    .build(), subject1),
            new SubjectExt(SubjectExtRaw.builder().subjectId(subject2.getId()).diagnosisDate(toDate("1999-01-02"))
                    .daysFromDiagnosisDate(372).recentProgressionDate(toDate("1998-11-01")).build(), subject2),
            new SubjectExt(SubjectExtRaw.builder().subjectId(subject3.getId()).diagnosisDate(toDate("1999-01-03"))
                    .daysFromDiagnosisDate(414).recentProgressionDate(toDate("1999-05-01")).build(), subject3));

    private List<SubjectExt> subjectsExtEmptyDates = newArrayList(new SubjectExt(
            SubjectExtRaw.builder().subjectId(subject1.getId()).build(), subject1),
            new SubjectExt(SubjectExtRaw.builder().subjectId(subject2.getId()).build(), subject2),
            new SubjectExt(SubjectExtRaw.builder().subjectId(subject3.getId()).build(), subject3));

    @Test
    public void testGetTumourTherapyOnColumnRange() {

        when(subjectExtDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjectsExtEmptyDates);

        OutputColumnRangeChartData data = testGetTumourTherapyOnColumnRange(MOST_RECENT_THERAPY);
        softly.assertThat(data.getCategories()).containsExactly("subject3", "subject2", "subject1");
        softly.assertThat(data.getData()).extracting(OutputColumnRangeChartEntry::getX,
                OutputColumnRangeChartEntry::getLow,
                OutputColumnRangeChartEntry::getHigh,
                OutputColumnRangeChartEntry::isNoStartDate,
                OutputColumnRangeChartEntry::getColor)
                .containsExactly(
                        tuple(0, -2, 0, false, LIGHTSEAGREEN.getCode()),
                        tuple(0, 0, 12, false, LIGHTSEAGREEN.getCode()),
                        tuple(1, -7, -1, true, LIGHTSEAGREEN.getCode()),
                        tuple(1, 0, 18, false, LIGHTSEAGREEN.getCode()),
                        tuple(2, -14, -1, false, LIGHTSEAGREEN.getCode()),
                        tuple(2, 0, 6, false, LIGHTSEAGREEN.getCode()));
    }

    /**
     * For MOST_RECENT_THERAPY setting the line of the last therapy consists of 3 merged periods of following events:
     * chId11, rad1, chId12.
     * When rad1 is filtered out, the line must be split into 2 (for chId11 and chId12), because they do not cross
     */
    @Test
    public void testGetLastTherapiesFiltered() {

        List<Chemotherapy> chemoData = Arrays.asList(
                new Chemotherapy(ChemotherapyRaw.builder().id("chId11").subjectId(subject1.getSubjectId())
                        .startDate(toDate("2000-01-01")).endDate(toDate("2000-01-20"))
                        .preferredMed("med 1").build(), subject1),

                new Chemotherapy(ChemotherapyRaw.builder().id("chId12").subjectId(subject1.getSubjectId())
                        .startDate(toDate("2000-02-01")).endDate(toDate("2000-02-20"))
                        .preferredMed("med 1").build(), subject1),

                // does not belong to the previous therapy line
                new Chemotherapy(ChemotherapyRaw.builder().id("chId13").subjectId(subject1.getSubjectId())
                        .startDate(toDate("1999-12-01")).endDate(toDate("1999-12-20"))
                        .preferredMed("med 1").build(), subject1));

        List<Radiotherapy> radioData = newArrayList(new Radiotherapy(RadiotherapyRaw.builder().id("radId1").subjectId(subject1.getSubjectId())
                        .startDate(toDate("2000-01-10")).endDate(toDate("2000-02-10")).build(), subject1));

        when(radiotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(radioData);
        when(chemotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(chemoData);

        ChartGroupByOptions<Subject, PopulationGroupByOptions> tocSettings = ChartGroupByOptions.<Subject, PopulationGroupByOptions>builder().build();
        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(MOST_RECENT_THERAPY);

        PopulationFilters populationFilters = PopulationFilters.empty();
        populationFilters.getSubjectId().completeWithValue(subject1.getSubjectCode());
        TherapyFilters therapyFilters = TherapyFilters.empty();
        therapyFilters.getTherapyDescription().completeWithValue("med 1");

        List<TrellisedColumnRangeChart<Subject, PopulationGroupByOptions>> output = tumourService.getTumourTherapyOnColumnRange(DATASETS,
                therapyFilters, populationFilters, ChartGroupByOptionsFiltered.builder(tocSettings).build(),
                ChartGroupByOptionsFiltered.builder(therapiesSettings).build());
        OutputColumnRangeChartData data = output.get(0).getData();

        softly.assertThat(data.getCategories()).containsExactly("subject1");
        softly.assertThat(data.getData()).extracting(OutputColumnRangeChartEntry::getX,
                OutputColumnRangeChartEntry::getLow,
                OutputColumnRangeChartEntry::getHigh,
                OutputColumnRangeChartEntry::getName,
                o -> o.getTherapies().size(),
                o -> o.getTherapies().stream().findFirst().orElse(""))
                .containsExactly(
                        tuple(0, -10, -7, "med 1", 1, "Chemotherapy: med 1"),
                        tuple(0, -14, -11, "med 1", 1, "Chemotherapy: med 1"),
                        tuple(0, 0, 6, "All", 0, ""));
    }

    @Test
    public void testGetTumourTherapyOnColumnRangeAllTherapiesCase() {

        when(subjectExtDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjectsExtEmptyDates);

        OutputColumnRangeChartData data = testGetTumourTherapyOnColumnRange(ALL_PRIOR_THERAPIES);
        softly.assertThat(data.getCategories()).containsExactly("subject3", "subject3", "subject3", "subject2", "subject2",
                "subject2", "subject2", "subject1", "subject1", "subject1");
        softly.assertThat(data.getData()).extracting(OutputColumnRangeChartEntry::getX,
                OutputColumnRangeChartEntry::getLow,
                OutputColumnRangeChartEntry::getHigh,
                OutputColumnRangeChartEntry::isNoStartDate,
                OutputColumnRangeChartEntry::getColor,
                OutputColumnRangeChartEntry::getName)
                .containsExactly(
                        tuple(0, -8, -6, false, "#20B2AA", SUMMARY),
                        tuple(0, -2, 0, false, "#20B2AA", SUMMARY),
                        tuple(0, 0, 12, false, "#20B2AA", ALL),
                        tuple(1, -8, -6, false, "#F58231", "med 1"),
                        tuple(2, -2, 0, false, "#88CCEE", "(empty)"),
                        tuple(3, -7, -1, true, "#20B2AA", SUMMARY),
                        tuple(3, 0, 18, false, "#20B2AA", ALL),
                        tuple(4, -7, -1, true, "#F58231", "med 1"),
                        tuple(5, -7, -6, true, "#DE606C", RADIOTHERAPY_LABEL),
                        tuple(6, -1, -1, false, "#DE606C", RADIOTHERAPY_LABEL),
                        tuple(7, -14, -1, false, "#20B2AA", SUMMARY),
                        tuple(7, 0, 6, false, "#20B2AA", ALL),
                        tuple(8, -14, -3, false, "#DE606C", RADIOTHERAPY_LABEL),
                        tuple(9, -7, -1, false, "#F9DA00", "med 2")
                );
    }

    @Test
    public void testGetTumourTherapyOnColumnRangeNoStartDateAllTherapiesCase1() {

        List<Chemotherapy> chemoData = newArrayList(
                new Chemotherapy(ChemotherapyRaw.builder().id("chId2").subjectId(subject2.getSubjectId())
                        .endDate(toDate("2000-01-09")).preferredMed("med 1").build(), subject2));

        List<Radiotherapy> radioData = newArrayList(
                new Radiotherapy(RadiotherapyRaw.builder().id("radId2").subjectId(subject2.getSubjectId())
                        .endDate(toDate("1999-12-01")).build(), subject2));

        List<DrugDose> doses = newArrayList(new DrugDose(DrugDoseRaw.builder().id("doseId2").dose(10.0).build(), subject2));

        List<SubjectExt> subjectsExt = newArrayList(new SubjectExt(SubjectExtRaw.builder().subjectId(subject2.getId())
                        .diagnosisDate(toDate("2000-01-01")).build(), subject2));

        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject2));
        when(subjectExtDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjectsExt);
        when(radiotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(radioData);
        when(chemotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(chemoData);
        when(drugDoseDatasetsDataProvider.loadDosesForTumourColumnRangeService(any(Datasets.class))).thenReturn(doses);

        OutputColumnRangeChartData data = testGetTumourTherapyOnColumnRange(ALL_PRIOR_THERAPIES);
        softly.assertThat(data.getCategories()).containsExactly("subject2", "subject2", "subject2");
        softly.assertThat(data.getData()).extracting(OutputColumnRangeChartEntry::getX,
                OutputColumnRangeChartEntry::getLow,
                OutputColumnRangeChartEntry::getHigh,
                OutputColumnRangeChartEntry::isNoStartDate,
                OutputColumnRangeChartEntry::getName)
                .containsExactly(
                        tuple(0, -7, -1, true, SUMMARY),
                        tuple(0, 0, 18, false, ALL),
                        tuple(1, -7, -1, true, "med 1"),
                        tuple(2, -7, -6, true, RADIOTHERAPY_LABEL)
                );
    }

    @Test
    public void testGetTumourTherapyOnColumnRangeNoStartDateAllTherapiesCase2() {

        List<Chemotherapy> chemoData = newArrayList(
                new Chemotherapy(ChemotherapyRaw.builder().id("chId2").subjectId(subject2.getSubjectId())
                        .endDate(toDate("2000-01-09")).preferredMed("med 1").build(), subject2));

        List<Radiotherapy> radioData = newArrayList(
                new Radiotherapy(RadiotherapyRaw.builder().id("radId2").subjectId(subject2.getSubjectId())
                        .endDate(toDate("1999-12-01")).build(), subject2),
                new Radiotherapy(RadiotherapyRaw.builder().id("radId3").subjectId(subject2.getSubjectId())
                        .startDate(toDate("1999-11-01")).endDate(toDate("2000-01-01")).build(), subject2));

        List<DrugDose> doses = newArrayList(new DrugDose(DrugDoseRaw.builder().id("doseId2").dose(10.0).build(), subject2));

        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject2));
        when(subjectExtDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjectsExtEmptyDates);
        when(radiotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(radioData);
        when(chemotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(chemoData);
        when(drugDoseDatasetsDataProvider.loadDosesForTumourColumnRangeService(any(Datasets.class))).thenReturn(doses);

        OutputColumnRangeChartData data = testGetTumourTherapyOnColumnRange(ALL_PRIOR_THERAPIES);
        softly.assertThat(data.getCategories()).containsExactly("subject2", "subject2", "subject2", "subject2");
        softly.assertThat(data.getData()).extracting(OutputColumnRangeChartEntry::getX,
                OutputColumnRangeChartEntry::getLow,
                OutputColumnRangeChartEntry::getHigh,
                OutputColumnRangeChartEntry::isNoStartDate,
                OutputColumnRangeChartEntry::getName)
                .containsExactly(
                        tuple(0, -10, -1, true, SUMMARY),
                        tuple(0, 0, 18, false, ALL),
                        tuple(1, -10, -1, true, "med 1"),
                        tuple(2, -10, -6, true, RADIOTHERAPY_LABEL),
                        tuple(3, -10, -2, false, RADIOTHERAPY_LABEL)
                );
    }

    @Test
    public void testGetTumourTherapyOnColumnRangeWithoutPreviousTherapyMostRecentTherapyCase() {
        testGetTumourTherapyOnColumnRangeWithoutPreviousTherapy(MOST_RECENT_THERAPY);
    }

    @Test
    public void testGetTumourTherapyOnColumnRangeWithoutPreviousTherapyAllTherapiesCase() {
        testGetTumourTherapyOnColumnRangeWithoutPreviousTherapy(ALL_PRIOR_THERAPIES);
    }

    @Test
    public void testGetTooltipWithEmptyPreferredMedMostRecentTherapyCase() {
        OutputColumnRangeChartData data =  testGetTooltipWithEmptyPreferredMed(MOST_RECENT_THERAPY);

        softly.assertThat(data.getData().get(0).getTherapies()).containsExactly("Chemotherapy: (empty)");
        softly.assertThat(data.getData().get(2).getTherapies()).containsExactly("Radiotherapy", "Chemotherapy: med 1, (empty)");
    }

    @Test
    public void testGetTooltipWithEmptyPreferredMedAllTherapiesCase() {
        OutputColumnRangeChartData data = testGetTooltipWithEmptyPreferredMed(ALL_PRIOR_THERAPIES);

        softly.assertThat(data.getData().get(5).getTherapies()).containsExactly("Radiotherapy", "Chemotherapy: med 1, (empty)");
        softly.assertThat(data.getData().get(7).getTherapies()).containsExactly("Chemotherapy: (empty)");
        softly.assertThat(data.getData().get(8).getTherapies()).containsExactly("Chemotherapy: med 1");
        softly.assertThat(data.getData().get(9).getTherapies()).containsExactly("Radiotherapy");
    }

    @Test
    public void testGetTumourTherapyOnColumnRangeColorByDoseCohort() {

        Subject subject1c = subject1.toBuilder().doseCohort("cohort 1").build();
        Subject subject2c = subject2.toBuilder().doseCohort("cohort 2").build();
        Subject subject3c = subject3.toBuilder().doseCohort("cohort 1").build();

        List<Subject> population = Arrays.asList(subject1c, subject2c, subject3c);
        List<SubjectExt> subjectsExt = newArrayList(new SubjectExt(SubjectExtRaw.builder().build(), subject1c),
                new SubjectExt(SubjectExtRaw.builder().build(), subject2c),
                new SubjectExt(SubjectExtRaw.builder().build(), subject3c));

        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);
        when(subjectExtDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjectsExt);
        when(radiotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(new ArrayList<>());
        when(chemotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(new ArrayList<>());
        when(drugDoseDatasetsDataProvider.loadDosesForTumourColumnRangeService(any(Datasets.class))).thenReturn(new ArrayList<>());

        ChartGroupByOptions<Subject, PopulationGroupByOptions> tocSettings = ChartGroupByOptions.<Subject, PopulationGroupByOptions>builder()
                .withOption(COLOR_BY, DOSE_COHORT.getGroupByOptionAndParams())
                .build();
        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(MOST_RECENT_THERAPY);

        final List<TrellisedColumnRangeChart<Subject, PopulationGroupByOptions>> output = tumourService.getTumourTherapyOnColumnRange(DATASETS,
                TherapyFilters.empty(), PopulationFilters.empty(), ChartGroupByOptionsFiltered.builder(tocSettings).build(),
                ChartGroupByOptionsFiltered.builder(therapiesSettings).build());

        softly.assertThat(output).hasSize(1);

        OutputColumnRangeChartData data = output.get(0).getData();
        softly.assertThat(data.getCategories()).containsExactly("subject3", "subject2", "subject1");

        softly.assertThat(data.getData()).extracting(OutputColumnRangeChartEntry::getX,
                OutputColumnRangeChartEntry::getLow,
                OutputColumnRangeChartEntry::getHigh,
                OutputColumnRangeChartEntry::isNoStartDate,
                OutputColumnRangeChartEntry::getName,
                OutputColumnRangeChartEntry::getColor)
                .containsExactly(
                        tuple(0, 0, 12, false, "cohort 1", "#DE606C"),
                        tuple(1, 0, 18, false, "cohort 2", "#F9DA00"),
                        tuple(2, 0, 6, false, "cohort 1", "#DE606C"));
    }

    @Test
    public void testGetTumourTherapyOnColumnRangeColorByMaxDose() {
        final String drug1 = "Drug1";
        final String dose_10mg = "10 mg";
        Map<String, String> drugDoses = Collections.singletonMap(drug1, dose_10mg);

        Map<String, String> drugDosedAllDrug = Collections.singletonMap(drug1, YES);
        Map<String, String> drugDosedWithoutFirst = Collections.singletonMap(drug1, NO);

        Subject subject1c = subject1.toBuilder().drugsMaxDoses(drugDoses).drugsDosed(drugDosedAllDrug).build();
        Subject subject2c = subject2.toBuilder().drugsMaxDoses(drugDoses).drugsDosed(drugDosedAllDrug).build();
        Subject subject3c = subject3.toBuilder().drugsMaxDoses(Collections.singletonMap(drug1, "(Empty)"))
                .drugsDosed(drugDosedWithoutFirst).build();

        List<Subject> population = Arrays.asList(subject1c, subject2c, subject3c);
        List<SubjectExt> subjectsExt = newArrayList(new SubjectExt(SubjectExtRaw.builder().build(), subject1c),
                new SubjectExt(SubjectExtRaw.builder().build(), subject2c),
                new SubjectExt(SubjectExtRaw.builder().build(), subject3c));

        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);
        when(subjectExtDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjectsExt);
        when(radiotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(new ArrayList<>());
        when(chemotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(new ArrayList<>());
        when(drugDoseDatasetsDataProvider.loadDosesForTumourColumnRangeService(any(Datasets.class))).thenReturn(new ArrayList<>());

        ChartGroupByOptions<Subject, PopulationGroupByOptions> tocSettings = ChartGroupByOptions.<Subject, PopulationGroupByOptions>builder()
                .withOption(COLOR_BY, MAX_DOSE_PER_ADMIN_OF_DRUG.getGroupByOptionAndParams(GroupByOption.Params.builder()
                        .with(GroupByOption.Param.DRUG_NAME, drug1).build()))
                .build();
        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(MOST_RECENT_THERAPY);

        final List<TrellisedColumnRangeChart<Subject, PopulationGroupByOptions>> output = tumourService.getTumourTherapyOnColumnRange(DATASETS,
                TherapyFilters.empty(), PopulationFilters.empty(), ChartGroupByOptionsFiltered.builder(tocSettings).build(),
                ChartGroupByOptionsFiltered.builder(therapiesSettings).build());

        softly.assertThat(output).hasSize(1);

        OutputColumnRangeChartData data = output.get(0).getData();
        softly.assertThat(data.getCategories()).containsExactly("subject3", "subject2", "subject1");

        softly.assertThat(data.getData()).extracting(OutputColumnRangeChartEntry::getX,
                OutputColumnRangeChartEntry::getLow,
                OutputColumnRangeChartEntry::getHigh,
                OutputColumnRangeChartEntry::isNoStartDate,
                OutputColumnRangeChartEntry::getName,
                OutputColumnRangeChartEntry::getColor)
                .containsExactly(
                        tuple(0, 0, 12, false, "(Empty)", "#88CCEE"),
                        tuple(1, 0, 18, false, dose_10mg, "#DE606C"),
                        tuple(2, 0, 6, false, dose_10mg, "#DE606C"));
    }

    @Test
    public void testGetTOCColorBy() {

        Map<String, String> drugsDosed1 = new HashMap<>();
        drugsDosed1.put("Drug 1", "No");
        drugsDosed1.put("Drug 2", "Yes");
        drugsDosed1.put("Drug 3", "No");
        Map<String, String> drugsDosed2 = new HashMap<>();
        drugsDosed2.put("Drug 1", "No");
        drugsDosed2.put("Drug 2", "No");
        drugsDosed2.put("Drug 3", "Yes");
        Map<String, String> drugsNotDosed = new HashMap<>();
        drugsNotDosed.put("Drug 1", "No");
        drugsNotDosed.put("Drug 2", "No");
        drugsNotDosed.put("Drug 3", "No");

        Subject subject1c = subject1.toBuilder().doseCohort("cohort 1").otherCohort("Default group").drugsDosed(drugsDosed1).build();
        Subject subject2c = subject2.toBuilder().doseCohort("cohort 2").otherCohort("other cohort 2").drugsDosed(drugsDosed2).build();
        Subject subject3c = subject3.toBuilder().doseCohort("cohort 1").otherCohort("Default group").drugsDosed(drugsNotDosed).build();

        List<Subject> population = Arrays.asList(subject1c, subject2c, subject3c);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);

        List<TrellisOptions<PopulationGroupByOptions>> availableTOCColorBy = tumourService.getTOCColorBy(DATASETS, PopulationFilters.empty());
        softly.assertThat(availableTOCColorBy).extracting(TrellisOptions::getTrellisedBy, TrellisOptions::getTrellisOptions)
                .containsExactlyInAnyOrder(tuple(DOSE_COHORT, newArrayList("cohort 1", "cohort 2")),
                        tuple(OTHER_COHORT, newArrayList("Default group", "other cohort 2")),
                        tuple(MAX_DOSE_PER_ADMIN_OF_DRUG, new ArrayList()),
                        tuple(MAX_DOSE_PER_ADMIN_OF_DRUG, new ArrayList()));
        softly.assertThat(availableTOCColorBy.stream().filter(c -> c.getTrellisedBy().equals(MAX_DOSE_PER_ADMIN_OF_DRUG)).collect(Collectors.toList()))
                .extracting(o -> ((TumourColumnRangeService.TrellisOptionsWithDrug) o).getDrug()).containsExactlyInAnyOrder("Drug 2", "Drug 3");
    }

    @Test
    public void testGetTOCColorByDefaultGroupOnly() {
        Subject subject1c = subject1.toBuilder().doseCohort("Default group").otherCohort("Default group").build();
        Subject subject2c = subject2.toBuilder().doseCohort("Default group").otherCohort("Default group").build();
        Subject subject3c = subject3.toBuilder().doseCohort("Default group").otherCohort("Default group").build();

        List<Subject> population = Arrays.asList(subject1c, subject2c, subject3c);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);

        List<TrellisOptions<PopulationGroupByOptions>> availableTOCColorBy = tumourService.getTOCColorBy(DATASETS, PopulationFilters.empty());
        softly.assertThat(availableTOCColorBy).isEmpty();
    }

    @Test
    public void testGetTOCColorByDefaultGroupFiltered() {
        Subject subject1c = subject1.toBuilder().doseCohort("Default group").otherCohort("Default group").build();
        Subject subject2c = subject2.toBuilder().doseCohort("Default group").otherCohort("Default group").build();
        Subject subject3c = subject3.toBuilder().doseCohort("Group 1").otherCohort("Other group 1").build();

        List<Subject> population = Arrays.asList(subject1c, subject2c, subject3c);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);

        PopulationFilters populationFilters = PopulationFilters.empty();
        populationFilters.getSubjectId().completeWithValue("subject1");

        List<TrellisOptions<PopulationGroupByOptions>> availableTOCColorBy = tumourService.getTOCColorBy(DATASETS, populationFilters);

        softly.assertThat(availableTOCColorBy).extracting(TrellisOptions::getTrellisedBy, TrellisOptions::getTrellisOptions)
                .containsExactlyInAnyOrder(tuple(DOSE_COHORT, newArrayList("Default group")),
                        tuple(OTHER_COHORT, newArrayList("Default group")));
    }

    @Test
    public void testGetAvailableTherapyFiltersMostRecentTherapyCase() {
        TherapyFilters availableFilters = testGetAvailableTherapyFilters(MOST_RECENT_THERAPY);

        // 3 doses that are > 0 and 6 last therapies (2 chemos merged to one)
        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(9);
    }

    @Test
    public void testGetAvailableTherapyFiltersAllTherapiesCase() {
        TherapyFilters availableFilters = testGetAvailableTherapyFilters(ALL_PRIOR_THERAPIES);

        // 3 doses that are > 0 and 7 last therapies
        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(10);
    }

    @Test
    public void testGetAvailableTherapyFiltersSameDayAsFirstTreatmentDate() {

        List<Radiotherapy> radioData = Collections.singletonList(
                new Radiotherapy(RadiotherapyRaw.builder().id("radId1").subjectId(subject1.getSubjectId())
                .startDate(toDate("2000-01-01")).endDate(toDate("2000-04-05 15:00"))
                .dose(10.2).numOfDoses(5).treatmentStatus("status 2").build(), subject1));

        when(radiotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(radioData);
        when(chemotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(new ArrayList<>());
        when(drugDoseDatasetsDataProvider.loadDosesForTumourColumnRangeService(any(Datasets.class))).thenReturn(new ArrayList<>());

        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(ALL_PRIOR_THERAPIES);

        TherapyFilters availableFilters = tumourService.getAvailableTherapyFilters(DATASETS, TherapyFilters.empty(),
                PopulationFilters.empty(), ChartGroupByOptionsFiltered.builder(therapiesSettings).build());

        // 0 events because radId1's end time was later than first treatment date
        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(0);
    }

    @Test
    public void testGetAvailableTherapyFiltersRadiotherapyNotSelectedMostRecentTherapyCase() {

        List<DrugDose> doses = newArrayList(new DrugDose(DrugDoseRaw.builder().dose(50.0).build(), subject1),
                new DrugDose(DrugDoseRaw.builder().dose(20.0).build(), subject2),
                new DrugDose(DrugDoseRaw.builder().dose(50.0).build(), subject2),
                new DrugDose(DrugDoseRaw.builder().dose(30.0).build(), subject3));

        when(drugDoseDatasetsDataProvider.loadDosesForTumourColumnRangeService(any(Datasets.class))).thenReturn(doses);

        TherapyFilters filters = TherapyFilters.empty();
        filters.getTherapyDescription().completeWithValue("med 1");
        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(MOST_RECENT_THERAPY);

        TherapyFilters availableFilters = tumourService.getAvailableTherapyFilters(DATASETS, filters, PopulationFilters.empty(),
                ChartGroupByOptionsFiltered.builder(therapiesSettings).build());
        softly.assertThat(availableFilters.getChemotherapyClass().getSortedValues()).containsExactly("class 1");
        softly.assertThat(availableFilters.getChemotherapyClass().getIncludeEmptyValues()).isFalse();
        softly.assertThat(availableFilters.getRadiationDose().getFrom()).isNull();
        softly.assertThat(availableFilters.getRadiationDose().getTo()).isNull();
        softly.assertThat(availableFilters.getRadiationDose().getIncludeEmptyValues()).isNull();
        softly.assertThat(availableFilters.getChemoTherapyStatus().getSortedValues()).containsExactlyElementsOf(singletonList(null));
        softly.assertThat(availableFilters.getRadioTherapyStatus().getSortedValues()).isEmpty();
        softly.assertThat(availableFilters.getTherapyDescription().getSortedValues()).containsExactly("med 1");
        softly.assertThat(availableFilters.getReasonForChemotherapyFailure().getSortedValues()).containsExactly("failureReason 2");
        softly.assertThat(availableFilters.getChemotherapyBestResponse().getSortedValues()).containsExactly("bestResponse 2");
        softly.assertThat(availableFilters.getNumberOfChemotherapyCycles().getFrom()).isEqualTo(10);
        softly.assertThat(availableFilters.getNumberOfChemotherapyCycles().getTo()).isEqualTo(10);

        // 4 doses + 1 last therapy. chId4 is not included, because it was not in the last therapies list before filtering
        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(5);
    }

    @Test
    public void testGetAvailableTherapyFiltersRadiotherapyNotSelectedAllTherapiesCase() {

        List<DrugDose> doses = newArrayList(new DrugDose(DrugDoseRaw.builder().dose(50.0).build(), subject1),
                new DrugDose(DrugDoseRaw.builder().dose(20.0).build(), subject2),
                new DrugDose(DrugDoseRaw.builder().dose(50.0).build(), subject2),
                new DrugDose(DrugDoseRaw.builder().dose(30.0).build(), subject3));

        when(drugDoseDatasetsDataProvider.loadDosesForTumourColumnRangeService(any(Datasets.class))).thenReturn(doses);

        TherapyFilters filters = TherapyFilters.empty();
        filters.getTherapyDescription().completeWithValue("med 1");
        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(ALL_PRIOR_THERAPIES);

        TherapyFilters availableFilters = tumourService.getAvailableTherapyFilters(DATASETS, filters, PopulationFilters.empty(),
                ChartGroupByOptionsFiltered.builder(therapiesSettings).build());
        softly.assertThat(availableFilters.getChemotherapyClass().getSortedValues()).containsExactly("class 1", null);
        softly.assertThat(availableFilters.getChemotherapyClass().getIncludeEmptyValues()).isTrue();
        softly.assertThat(availableFilters.getRadiationDose().getFrom()).isNull();
        softly.assertThat(availableFilters.getRadiationDose().getTo()).isNull();
        softly.assertThat(availableFilters.getRadiationDose().getIncludeEmptyValues()).isNull();
        softly.assertThat(availableFilters.getChemoTherapyStatus().getSortedValues()).containsExactlyElementsOf(singletonList(null));
        softly.assertThat(availableFilters.getRadioTherapyStatus().getSortedValues()).isEmpty();
        softly.assertThat(availableFilters.getTherapyDescription().getSortedValues()).containsExactly("med 1");
        softly.assertThat(availableFilters.getReasonForChemotherapyFailure().getSortedValues()).containsExactly("failureReason 2", null);
        softly.assertThat(availableFilters.getChemotherapyBestResponse().getSortedValues()).containsExactly("bestResponse 2", null);
        softly.assertThat(availableFilters.getNumberOfChemotherapyCycles().getFrom()).isEqualTo(10);
        softly.assertThat(availableFilters.getNumberOfChemotherapyCycles().getTo()).isEqualTo(10);

        // 4 doses + 2 therapies with "med 1"
        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(6);
    }

    @Test
    public void testGetAvailableTherapyFiltersFilteredByChemoEmptyDescriptionMostRecentTherapyCase() {
        testGetAvailableTherapyFiltersFilteredByChemoEmptyDescription(MOST_RECENT_THERAPY);
    }

    @Test
    public void testGetAvailableTherapyFiltersFilteredByChemoEmptyDescriptionAllTherapiesCase() {
        testGetAvailableTherapyFiltersFilteredByChemoEmptyDescription(ALL_PRIOR_THERAPIES);
}

    private void testGetAvailableTherapyFiltersFilteredByChemoEmptyDescription(TumourTherapyGroupByOptions therapies) {

        when(chemotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(new Chemotherapy(ChemotherapyRaw.builder().id("chId")
                .subjectId(subject2.getSubjectId())
                .endDate(toDate("2000-01-09")).build(), subject2)));
        when(drugDoseDatasetsDataProvider.loadDosesForTumourColumnRangeService(any(Datasets.class))).thenReturn(new ArrayList<>());

        TherapyFilters filters = TherapyFilters.empty();
        filters.getTherapyDescription().completeWithValue(null);
        filters.getTherapyDescription().setSortedValues(new ArrayList<>());
        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(therapies);

        TherapyFilters availableFilters = tumourService.getAvailableTherapyFilters(DATASETS, filters, PopulationFilters.empty(),
                ChartGroupByOptionsFiltered.builder(therapiesSettings).build());
        softly.assertThat(availableFilters.getTherapyDescription().getSortedValues()).containsExactlyElementsOf(singletonList(null));
        // only 1 chemo event
        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(1);
    }

    @Test
    public void testGetAvailableTherapyFiltersFilteredBySubjectExt() {

        TherapyFilters filters = TherapyFilters.empty();
        filters.getDaysFromDiagnosisDate().setTo(450);
        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(ALL_PRIOR_THERAPIES);

        TherapyFilters availableFilters = tumourService.getAvailableTherapyFilters(DATASETS, filters, PopulationFilters.empty(),
                ChartGroupByOptionsFiltered.builder(therapiesSettings).build());
        softly.assertThat(availableFilters.getDaysFromDiagnosisDate().getFrom()).isEqualTo(372);
        softly.assertThat(availableFilters.getDaysFromDiagnosisDate().getTo()).isEqualTo(414);
        softly.assertThat(availableFilters.getTherapyDescription().getValues()).containsExactlyInAnyOrder("med 1", "Radiotherapy", null);

        // all events of subject1 are filtered out
        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(7);
    }

    @Test
    public void testGetAvailableTherapyFiltersFilteredBySubjectExtAllFilteredOut() {

        TherapyFilters filters = TherapyFilters.empty();
        filters.getDaysFromDiagnosisDate().setTo(100);
        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(ALL_PRIOR_THERAPIES);

        TherapyFilters availableFilters = tumourService.getAvailableTherapyFilters(DATASETS, filters, PopulationFilters.empty(),
                ChartGroupByOptionsFiltered.builder(therapiesSettings).build());

        softly.assertThat(availableFilters.getMatchedItemsCount()).isEqualTo(0);
    }

    @Test
    public void testGetSelectionDetailsForLastTherapies() {

        when(subjectExtDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjectsExtEmptyDates);

        // 1 radiotherapy and 1 chemotherapy
        final HashMap<TumourTherapyGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem1 = new HashMap<>();
        selectedItem1.put(SERIES_BY, MOST_RECENT_THERAPY);
        selectedItem1.put(Y_AXIS, "subject1");
        selectedItem1.put(START, -14);
        selectedItem1.put(END, -1);

        // 2 radiotherapies and 1 chemotherapy
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem2 = new HashMap<>();
        selectedItem2.put(SERIES_BY, MOST_RECENT_THERAPY);
        selectedItem2.put(Y_AXIS, "subject2");
        selectedItem2.put(START, -7);
        selectedItem2.put(END, -1);

        // 1 active dose
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem3 = new HashMap<>();
        selectedItem3.put(SERIES_BY, MOST_RECENT_THERAPY);
        selectedItem3.put(Y_AXIS, "subject2");
        selectedItem3.put(START, 0);
        selectedItem3.put(END, 18);

        SelectionDetail selectionDetails = tumourService.getSelectionDetails(DATASETS,
                TherapyFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(buildTherapiesPlotSelectionSettings(MOST_RECENT_THERAPY), newArrayList(
                        ChartSelectionItem.of(selectedTrellises, selectedItem1),
                        ChartSelectionItem.of(selectedTrellises, selectedItem2),
                        ChartSelectionItem.of(selectedTrellises, selectedItem3))));

        softly.assertThat(selectionDetails).extracting(s -> s.getEventIds().size(), s -> s.getSubjectIds().size(), SelectionDetail::getTotalEvents,
                SelectionDetail::getTotalSubjects).containsExactly(6, 2, 0, 3);
    }

    @Test
    public void testGetSelectionDetailsForAllTherapies() {

        when(subjectExtDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjectsExtEmptyDates);

        // 1 chemotherapy
        final HashMap<TumourTherapyGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem1 = new HashMap<>();
        selectedItem1.put(SERIES_BY, ALL_PRIOR_THERAPIES);
        selectedItem1.put(Y_AXIS, "subject1");
        selectedItem1.put(START, -7);
        selectedItem1.put(END, -1);

        // 1 chemotherapy
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem2 = new HashMap<>();
        selectedItem2.put(SERIES_BY, ALL_PRIOR_THERAPIES);
        selectedItem2.put(Y_AXIS, "subject2");
        selectedItem2.put(START, -7);
        selectedItem2.put(END, -1);

        // 1 active dose
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItem3 = new HashMap<>();
        selectedItem3.put(SERIES_BY, ALL_PRIOR_THERAPIES);
        selectedItem3.put(Y_AXIS, "subject2");
        selectedItem3.put(START, 0);
        selectedItem3.put(END, 18);

        SelectionDetail selectionDetails = tumourService.getSelectionDetails(DATASETS,
                TherapyFilters.empty(), PopulationFilters.empty(),
                ChartSelection.of(buildTherapiesPlotSelectionSettings(ALL_PRIOR_THERAPIES), newArrayList(
                        ChartSelectionItem.of(selectedTrellises, selectedItem1),
                        ChartSelectionItem.of(selectedTrellises, selectedItem2),
                        ChartSelectionItem.of(selectedTrellises, selectedItem3))));

        softly.assertThat(selectionDetails).extracting(s -> s.getEventIds().size(), s -> s.getSubjectIds().size(), SelectionDetail::getTotalEvents,
                SelectionDetail::getTotalSubjects).containsExactly(3, 2, 0, 3);
    }

    private ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> buildTherapiesPlotSelectionSettings(TumourTherapyGroupByOptions therapies) {
        return ChartGroupByOptions.<TumourTherapy, TumourTherapyGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, therapies.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.Y_AXIS, TumourTherapyGroupByOptions.SUBJECT.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.START, TumourTherapyGroupByOptions.START.getGroupByOptionAndParams())
                .withOption(ChartGroupByOptions.ChartGroupBySetting.END, TumourTherapyGroupByOptions.END.getGroupByOptionAndParams())
                .build();
    }

    private ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> buildTherapiesPlotSettings(TumourTherapyGroupByOptions therapies) {
        return ChartGroupByOptions.<TumourTherapy, TumourTherapyGroupByOptions>builder()
                .withOption(ChartGroupByOptions.ChartGroupBySetting.SERIES_BY, therapies.getGroupByOptionAndParams())
                .build();
    }

    private OutputColumnRangeChartData testGetTumourTherapyOnColumnRange(TumourTherapyGroupByOptions therapies) {

        when(drugDoseDatasetsDataProvider.loadDosesForTumourColumnRangeService(any(Datasets.class))).thenReturn(new ArrayList<>());

        ChartGroupByOptions<Subject, PopulationGroupByOptions> tocSettings = ChartGroupByOptions.<Subject, PopulationGroupByOptions>builder()
                .build();
        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(therapies);

        final List<TrellisedColumnRangeChart<Subject, PopulationGroupByOptions>> output = tumourService.getTumourTherapyOnColumnRange(DATASETS,
                TherapyFilters.empty(), PopulationFilters.empty(), ChartGroupByOptionsFiltered.builder(tocSettings).build(),
                ChartGroupByOptionsFiltered.builder(therapiesSettings).build());

        softly.assertThat(output).hasSize(1);

        return output.get(0).getData();
    }

    private void testGetTumourTherapyOnColumnRangeWithoutPreviousTherapy(TumourTherapyGroupByOptions therapies) {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(subject1, subject2));
        when(chemotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(new ArrayList<>());
        when(radiotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(new ArrayList<>());

        ChartGroupByOptions<Subject, PopulationGroupByOptions> tocSettings = ChartGroupByOptions.<Subject, PopulationGroupByOptions>builder()
                .build();
        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(therapies);

        final List<TrellisedColumnRangeChart<Subject, PopulationGroupByOptions>> output = tumourService.getTumourTherapyOnColumnRange(DATASETS,
                TherapyFilters.empty(), PopulationFilters.empty(), ChartGroupByOptionsFiltered.builder(tocSettings).build(),
                ChartGroupByOptionsFiltered.builder(therapiesSettings).build());

        softly.assertThat(output).hasSize(1);

        OutputColumnRangeChartData data = output.get(0).getData();
        softly.assertThat(data.getCategories()).containsExactly("subject2", "subject1");
        softly.assertThat(data.getData()).extracting(OutputColumnRangeChartEntry::getX,
                OutputColumnRangeChartEntry::getLow,
                OutputColumnRangeChartEntry::getHigh,
                OutputColumnRangeChartEntry::isNoStartDate,
                OutputColumnRangeChartEntry::getColor)
                .containsExactly(
                        tuple(0, 0, 18, false, LIGHTSEAGREEN.getCode()),
                        tuple(1, 0, 6, false, LIGHTSEAGREEN.getCode()));
    }

    private OutputColumnRangeChartData testGetTooltipWithEmptyPreferredMed(TumourTherapyGroupByOptions therapies) {
        ArrayList<Chemotherapy> chemoExt = new ArrayList<>(chemoData);
        chemoExt.add(0, new Chemotherapy(ChemotherapyRaw.builder().id("chId5").subjectId(subject2.getSubjectId())
                .endDate(toDate("2000-01-09"))
                .therapyClass("class 1").bestResponse("bestResponse 2")
                .failureReason("failureReason 2").numOfCycles(10).build(), subject2));

        when(chemotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(chemoExt);

        ChartGroupByOptions<Subject, PopulationGroupByOptions> tocSettings = ChartGroupByOptions.<Subject, PopulationGroupByOptions>builder().build();
        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(therapies);

        final List<TrellisedColumnRangeChart<Subject, PopulationGroupByOptions>> output = tumourService.getTumourTherapyOnColumnRange(DATASETS,
                TherapyFilters.empty(), PopulationFilters.empty(), ChartGroupByOptionsFiltered.builder(tocSettings).build(),
                ChartGroupByOptionsFiltered.builder(therapiesSettings).build());

        return output.get(0).getData();
    }

    private TherapyFilters testGetAvailableTherapyFilters(TumourTherapyGroupByOptions therapies) {

        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(therapies);

        TherapyFilters availableFilters = tumourService.getAvailableTherapyFilters(DATASETS, TherapyFilters.empty(),
                PopulationFilters.empty(), ChartGroupByOptionsFiltered.builder(therapiesSettings).build());
        softly.assertThat(availableFilters.getChemotherapyClass().getSortedValues()).containsExactly("class 1", null);
        softly.assertThat(availableFilters.getChemotherapyClass().getIncludeEmptyValues()).isTrue();
        softly.assertThat(availableFilters.getRadiationDose().getFrom()).isEqualTo(15.0);
        softly.assertThat(availableFilters.getRadiationDose().getTo()).isEqualTo(51.0);
        softly.assertThat(availableFilters.getRadiationDose().getIncludeEmptyValues()).isTrue();
        softly.assertThat(availableFilters.getChemoTherapyStatus().getSortedValues()).containsExactly("status 1", null);
        softly.assertThat(availableFilters.getRadioTherapyStatus().getSortedValues()).containsExactly("status 2", "status 3", "status 4");
        softly.assertThat(availableFilters.getTherapyDescription().getSortedValues()).containsExactly("Radiotherapy", "med 1", "med 2", null);
        softly.assertThat(availableFilters.getReasonForChemotherapyFailure().getSortedValues()).containsExactly("failureReason", "failureReason 2", null);
        softly.assertThat(availableFilters.getChemotherapyBestResponse().getSortedValues()).containsExactly("bestResponse", "bestResponse 2", null);
        softly.assertThat(availableFilters.getNumberOfChemotherapyCycles().getFrom()).isEqualTo(5);
        softly.assertThat(availableFilters.getNumberOfChemotherapyCycles().getTo()).isEqualTo(10);
        softly.assertThat(availableFilters.getRecentProgressionDate().getFrom()).isInSameDayAs(toDate("1998-11-01"));
        softly.assertThat(availableFilters.getRecentProgressionDate().getTo()).isInSameDayAs(toDate("1999-05-01 23:59"));
        softly.assertThat(availableFilters.getRecentProgressionDate().getIncludeEmptyValues()).isEqualTo(null);
        softly.assertThat(availableFilters.getDiagnosisDate().getFrom()).isInSameDayAs(toDate("1999-01-01"));
        softly.assertThat(availableFilters.getDiagnosisDate().getTo()).isInSameDayAs(toDate("1999-01-03 23:59"));
        softly.assertThat(availableFilters.getDiagnosisDate().getIncludeEmptyValues()).isEqualTo(null);
        softly.assertThat(availableFilters.getDaysFromDiagnosisDate().getFrom()).isEqualTo(372);
        softly.assertThat(availableFilters.getDaysFromDiagnosisDate().getTo()).isEqualTo(458);
        softly.assertThat(availableFilters.getDaysFromDiagnosisDate().getIncludeEmptyValues()).isEqualTo(null);

        return availableFilters;
    }

    @Test
    public void testGetDiagnosisDates() {

        ChartGroupByOptions<Subject, PopulationGroupByOptions> tocSettings = ChartGroupByOptions.<Subject, PopulationGroupByOptions>builder()
                .build();
        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(MOST_RECENT_THERAPY);

        final List<TrellisedColumnRangeChart<Subject, PopulationGroupByOptions>> output = tumourService.getTumourTherapyOnColumnRange(DATASETS,
                TherapyFilters.empty(), PopulationFilters.empty(), ChartGroupByOptionsFiltered.builder(tocSettings).build(),
                ChartGroupByOptionsFiltered.builder(therapiesSettings).build());
        softly.assertThat(output).hasSize(1);
        final List<OutputMarkEntry> diagnosisDates = output.get(0).getData().getDiagnosisDates();
        softly.assertThat(diagnosisDates).extracting(OutputMarkEntry::getX,
                OutputMarkEntry::getY,
                OutputMarkEntry::getName)
                .containsExactly(
                        tuple(0, -60, "1999-01-03"),
                        tuple(1, -54, "1999-01-02"),
                        tuple(2, -66, "1999-01-01"));
    }

    @Test
    public void testAllTherapiesWithDiagnosisDates() {

        when(subjectExtDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(
                new SubjectExt(SubjectExtRaw.builder().subjectId(subject1.getId()).build(), subject1),
                new SubjectExt(SubjectExtRaw.builder().subjectId(subject2.getId())
                        .diagnosisDate(toDate("1999-11-15")).build(), subject2),
                new SubjectExt(SubjectExtRaw.builder().subjectId(subject3.getId()).build(), subject3)));
        when(drugDoseDatasetsDataProvider.loadDosesForTumourColumnRangeService(any(Datasets.class))).thenReturn(new ArrayList<>());

        OutputColumnRangeChartData data = testGetTumourTherapyOnColumnRange(ALL_PRIOR_THERAPIES);
        List<OutputColumnRangeChartEntry> subj2AllPriorTherapies = data.getData().stream().filter(e -> e.getX() == 3)
                .filter(e -> e.getLow().intValue() < 0).collect(Collectors.toList());
        // radId2 - no start date, radId3 has start date, chId2 - no start date;
        // set start date to the earliest end date minus 1 week
        softly.assertThat(subj2AllPriorTherapies).hasSize(1);
        softly.assertThat(subj2AllPriorTherapies.get(0)).extracting(
                OutputColumnRangeChartEntry::getLow,
                OutputColumnRangeChartEntry::getHigh,
                OutputColumnRangeChartEntry::isNoStartDate)
                .containsExactly(-7, -1, true);
    }

    @Test
    public void testGetMostRecentProgressionDates() {

        ChartGroupByOptions<Subject, PopulationGroupByOptions> tocSettings = ChartGroupByOptions.<Subject, PopulationGroupByOptions>builder()
                .build();
        ChartGroupByOptions<TumourTherapy, TumourTherapyGroupByOptions> therapiesSettings = buildTherapiesPlotSettings(MOST_RECENT_THERAPY);

        List<TrellisedColumnRangeChart<Subject, PopulationGroupByOptions>> output = tumourService.getTumourTherapyOnColumnRange(DATASETS,
                TherapyFilters.empty(), PopulationFilters.empty(), ChartGroupByOptionsFiltered.builder(tocSettings).build(),
                ChartGroupByOptionsFiltered.builder(therapiesSettings).build());
        softly.assertThat(output).hasSize(1);
        List<OutputMarkEntry> progressionDates = output.get(0).getData().getProgressionDates();
        softly.assertThat(progressionDates).extracting(OutputMarkEntry::getX,
                OutputMarkEntry::getY,
                OutputMarkEntry::getName)
                .containsExactly(
                        tuple(0, -43, "1999-05-01"),
                        tuple(1, -63, "1998-11-01"),
                        tuple(2, -66, "1999-01-01"));

        therapiesSettings = buildTherapiesPlotSettings(ALL_PRIOR_THERAPIES);
        output = tumourService.getTumourTherapyOnColumnRange(DATASETS,
                TherapyFilters.empty(), PopulationFilters.empty(), ChartGroupByOptionsFiltered.builder(tocSettings).build(),
                ChartGroupByOptionsFiltered.builder(therapiesSettings).build());
        softly.assertThat(output).hasSize(1);
        progressionDates = output.get(0).getData().getProgressionDates();
        softly.assertThat(progressionDates).extracting(OutputMarkEntry::getX,
                OutputMarkEntry::getY,
                OutputMarkEntry::getName)
                .containsExactly(
                        tuple(0, -43, "1999-05-01"),
                        tuple(3, -63, "1998-11-01"),
                        tuple(7, -66, "1999-01-01"));
    }

}
