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
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.dataproviders.ExacerbationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.config.DataProviderConfiguration;
import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOption;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptionsFiltered;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelection;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartSelectionItem;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PopulationGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.Attributes;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.ExacerbationRaw;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputBarChartEntry;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.OutputOvertimeData;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedBarChart;
import com.acuity.visualisations.rawdatamodel.vo.compatibility.TrellisedOvertime;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.acuity.va.security.acl.domain.Datasets;
import org.apache.commons.lang3.time.DateUtils;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_LUNG_FUNC_DATASET;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_LUNG_FUNC_DATASETS;
import static com.acuity.visualisations.rawdatamodel.Constants.DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.COLOR_BY;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ChartGroupByOptions.ChartGroupBySetting.X_AXIS;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions.OVERTIME_DURATION;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExacerbationGroupByOptions.PLANNED_TREATMENT_ARM;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toSet;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class, DataProviderConfiguration.class})
public class ExacerbationServiceTest {

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    private final Subject subject1 = Subject.builder()
            .clinicalStudyCode(String.valueOf(DUMMY_ACUITY_LUNG_FUNC_DATASET.getId()))
            .subjectId("sid1").subjectCode("E01").datasetId("test")
            .datasetName("dataset1").sex("Male").height(188.).weight(70.).race("Asian").age(55).siteId("1").centerNumber("11")
            .dateOfRandomisation(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("01.06.2015")).deathFlag("Yes")
            .dateOfDeath(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("01.06.2017")).durationOnStudy(372)
            .studyPart("A").clinicalStudyName("Study name").clinicalStudyCode("100B")
            .firstTreatmentDate(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("20.02.2015"))
            .lastTreatmentDate(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("20.02.2016"))
            .withdrawal("Yes").dateOfWithdrawal(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("01.06.2016")).reasonForWithdrawal("some reason")
            .plannedArm("planed_arm").actualArm("actual_arm").country("China").region("Asia").subjectId("subject1")
            .drugDiscontinued("drug1", "Yes")
            .drugDiscontinued("drug2", "No")
            .drugDiscontinuationMainReason("drug1", "reason_1")
            .drugDiscontinuationMainReason("drug2", "reason_2")
            .subjectCode("subject1")
            .studyInfo(StudyInfo.builder().randomisedPopulation(true).build())
            .studyLeaveDate(toDate("01.06.2017")).build();
    private final Subject subject2 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_LUNG_FUNC_DATASET.getId()))
            .withdrawal("No").plannedArm("planed_arm").actualArm("actual_arm").country("China").region("Asia")
            .subjectId("subject2").subjectCode("subject2")
            .studyInfo(StudyInfo.builder().randomisedPopulation(true).build()).build();
    private final Subject subject3 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_LUNG_FUNC_DATASET.getId()))
            .subjectId("subject3").clinicalStudyName("Study name").firstTreatmentDate(toDate("01.08.2015"))
            .dateOfRandomisation(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("01.06.2015"))
            .dateOfWithdrawal(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("01.06.2019"))
            .country("Russia").lastTreatmentDate(toDate("09.08.2016")).plannedArm("PLACEBO").actualArm("TEST")
            .studyInfo(StudyInfo.builder().randomisedPopulation(true).build()).subjectCode("subject1").build();
    private final Subject subject4 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_LUNG_FUNC_DATASET.getId()))
            .subjectId("subject2").firstTreatmentDate(toDate("01.08.2015"))
            .studyInfo(StudyInfo.builder().randomisedPopulation(true).build())
            .dateOfWithdrawal(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("01.06.2019"))
            .country("USA").dateOfRandomisation(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("01.06.2016"))
            .lastTreatmentDate(toDate("09.08.2016")).plannedArm("PLACEBO2").actualArm("TEST").subjectCode("subject2").build();
    private final Subject subject5 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_LUNG_FUNC_DATASET.getId()))
            .subjectId("subject5").firstTreatmentDate(toDate("01.08.2015"))
            .studyInfo(StudyInfo.builder().randomisedPopulation(true).build())
            .dateOfWithdrawal(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("01.06.2019"))
            .dateOfRandomisation(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("01.06.2018"))
            .lastTreatmentDate(toDate("09.08.2016")).plannedArm("PLACEBO2").actualArm("TEST").subjectCode("subject2").build();
    private final Subject subject6 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_LUNG_FUNC_DATASET.getId()))
            .clinicalStudyName("Study name").withdrawal("No").plannedArm("planed_arm").actualArm("actual_arm").country("Italy").region("Europe")
            .subjectId("subject6").subjectCode("subject6").firstTreatmentDate(toDate("01.06.2015")).studyLeaveDate(toDate("10.09.2017")).build();
    private final Subject subject7 = Subject.builder()
            .clinicalStudyCode(String.valueOf(DUMMY_ACUITY_LUNG_FUNC_DATASET.getId()))
            .subjectId("sid7").subjectCode("E07").datasetId("test")
            .dateOfRandomisation(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("01.06.2015"))
            .studyPart("A").clinicalStudyName("Study name").clinicalStudyCode("100B")
            .firstTreatmentDate(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("20.02.2015"))
            .lastTreatmentDate(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("20.02.2016"))
            .withdrawal("Yes").dateOfWithdrawal(com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate("01.06.2016")).reasonForWithdrawal("some reason")
            .drugDosed("drug_1", "Yes")
            .drugDosed("drug_2", "No")
            .studyInfo(StudyInfo.builder().randomisedPopulation(true).build())
            .subjectCode("subject7").build();
    private final Exacerbation exacerb_1 = new Exacerbation(ExacerbationRaw.builder()
            .id("1")
            .exacerbationClassification("classification_1")
            .studyPeriods("studyPeriod_1")
            .antibioticsTreatment("antibiotics_trt_2")
            .daysOnStudyAtStart(3)
            .daysOnStudyAtEnd(13)
            .depotCorticosteroidTreatment("depot_gcs_trt_1")
            .duration(10)
            .emergencyRoomVisit("emergency_room_visit_1")
            .hospitalisation("hospitalisation_1")
            .startDate(toDate("02.01.2000"))
            .endDate(toDate("12.01.2000"))
            .startPriorToRandomisation("start_prior_to_randomisation_1")
            .endPriorToRandomisation("end_prior_to_randomisation_1")
            .increasedInhaledCorticosteroidTreatment("inc_inhaled_cort_trt_1")
            .systemicCorticosteroidTreatment("sys_cort_trt_1")
            .build(), subject1);
    private final Exacerbation exacerb_2 = new Exacerbation(ExacerbationRaw.builder()
            .id("2")
            .exacerbationClassification("classification_2")
            .studyPeriods("studyPeriod_2")
            .antibioticsTreatment("antibiotics_trt_2")
            .daysOnStudyAtStart(1)
            .daysOnStudyAtEnd(2)
            .depotCorticosteroidTreatment("depot_gcs_trt_2")
            .duration(1)
            .emergencyRoomVisit("emergency_room_visit_2")
            .hospitalisation("hospitalisation_2")
            .startDate(toDate("01.01.2000"))
            .endDate(toDate("02.01.2000"))
            .startPriorToRandomisation("start_prior_to_randomisation_2")
            .endPriorToRandomisation("end_prior_to_randomisation_2")
            .increasedInhaledCorticosteroidTreatment("inc_inhaled_cort_trt_2")
            .systemicCorticosteroidTreatment("sys_cort_trt_2")
            .build(), subject1);
    private final Exacerbation exacerb_3 = new Exacerbation(ExacerbationRaw.builder().id("3")
            .build(), subject1);
    private final Exacerbation exacerb_4 = new Exacerbation(ExacerbationRaw.builder().id("4").hospitalisation("hosp_4")
            .antibioticsTreatment("antibio_4").build(), subject1);
    private final Exacerbation exacerb_5 = new Exacerbation(ExacerbationRaw.builder().id("5")
            .studyPeriods("studyPeriod_2")
            .build(), subject2);
    private final Exacerbation exacerb_7 = new Exacerbation(ExacerbationRaw.builder()
            .id("7")
            .exacerbationClassification("classification_2")
            .studyPeriods("studyPeriod_2")
            .antibioticsTreatment("antibiotics_trt_2")
            .daysOnStudyAtStart(1)
            .daysOnStudyAtEnd(2)
            .depotCorticosteroidTreatment("depot_gcs_trt_2")
            .duration(1)
            .emergencyRoomVisit("emergency_room_visit_2")
            .hospitalisation("hospitalisation_2")
            .startDate(toDate("01.01.2000"))
            .endDate(toDate("02.01.2000"))
            .startPriorToRandomisation("start_prior_to_randomisation_2")
            .endPriorToRandomisation("end_prior_to_randomisation_2").build(), subject3);
    private final Exacerbation exacerb_8 = new Exacerbation(ExacerbationRaw.builder()
            .id("8")
            .studyPeriods("studyPeriod_2")
            .daysOnStudyAtStart(1)
            .daysOnStudyAtEnd(2)
            .duration(1)
            .emergencyRoomVisit("emergency_room_visit_2")
            .hospitalisation("hospitalisation_2")
            .startDate(toDate("01.01.2000"))
            .endDate(toDate("02.01.2000"))
            .startPriorToRandomisation("start_prior_to_randomisation_2")
            .endPriorToRandomisation("end_prior_to_randomisation_2").build(), subject4);
    private final Exacerbation exacerb_9 = new Exacerbation(ExacerbationRaw.builder()
            .id("9")
            .studyPeriods("studyPeriod_2")
            .daysOnStudyAtStart(1)
            .daysOnStudyAtEnd(2)
            .duration(1)
            .emergencyRoomVisit("emergency_room_visit_2")
            .hospitalisation("hospitalisation_2")
            .startDate(toDate("01.01.2003"))
            .endDate(toDate("02.01.2004"))
            .startPriorToRandomisation("start_prior_to_randomisation_2")
            .endPriorToRandomisation("end_prior_to_randomisation_2").build(), subject5);
    private final Exacerbation exacerb_10 = new Exacerbation(ExacerbationRaw.builder().id("10").exacerbationClassification("classification_1")
            .startDate(toDate("27.05.2017")).endDate(toDate("30.05.2017")).build(), subject1);
    private final Exacerbation exacerb_11 = new Exacerbation(ExacerbationRaw.builder().id("11").exacerbationClassification("classification_2")
            .startDate(toDate("30.05.2017")).build(), subject1);
    private final Exacerbation exacerb_12 = new Exacerbation(ExacerbationRaw.builder().id("12").exacerbationClassification("classification_1")
            .startDate(toDate("09.09.2017")).build(), subject6);

    @Autowired
    private ExacerbationService exacerbationService;
    @MockBean
    private ExacerbationDatasetsDataProvider exacerbationDatasetsDataProvider;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    private DoDCommonService doDCommonService = new DoDCommonService();

    @Test
    public void shouldGetAvailableFilters() {
        //Given
        List<Exacerbation> events = Arrays.asList(exacerb_1, exacerb_2, exacerb_3);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(subject1, subject2));

        //When
        ExacerbationFilters result = (ExacerbationFilters) exacerbationService.getAvailableFilters(
                DUMMY_ACUITY_LUNG_FUNC_DATASETS, ExacerbationFilters.empty(), PopulationFilters.empty());

        //Then
//    softly.assertThat(result.getStudyPeriods().getValues()).containsOnly(null, "studyPeriod_1", "studyPeriod_2");
        softly.assertThat(result.getExacerbationClassification().getValues()).containsOnly(null, "classification_1", "classification_2");
        softly.assertThat(result.getAntibioticsTreatment().getValues()).containsOnly(null, "antibiotics_trt_2");
        softly.assertThat(result.getEmergencyRoomVisit().getValues()).containsOnly(null, "emergency_room_visit_1", "emergency_room_visit_2");
        softly.assertThat(result.getHospitalisation().getValues()).containsOnly(null, "hospitalisation_2", "hospitalisation_1");
        softly.assertThat(result.getStartPriorToRandomisation().getValues())
                .containsOnly(null, "start_prior_to_randomisation_2", "start_prior_to_randomisation_1");
        softly.assertThat(result.getEndPriorToRandomisation().getValues()).containsOnly(null, "end_prior_to_randomisation_1", "end_prior_to_randomisation_2");
        softly.assertThat(result.getSystemicCorticosteroidTreatment().getValues()).containsOnly(null, "sys_cort_trt_1", "sys_cort_trt_2");
        softly.assertThat(result.getDepotCorticosteroidTreatment().getValues()).containsOnly(null, "depot_gcs_trt_2", "depot_gcs_trt_1");
        softly.assertThat(result.getIncreasedInhaledCorticosteroidTreatment().getValues())
                .containsOnly(null, "inc_inhaled_cort_trt_2", "inc_inhaled_cort_trt_1");
        softly.assertThat(result.getStartDate().getFrom()).isInSameDayAs(toDate("01.01.2000"));
        softly.assertThat(result.getStartDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(toDate("02.01.2000"), 1), -1));
        softly.assertThat(result.getEndDate().getFrom()).isInSameDayAs(toDate("02.01.2000"));
        softly.assertThat(result.getEndDate().getTo()).isInSameDayAs(
                DateUtils.addMilliseconds(DaysUtil.addDays(toDate("12.01.2000"), 1), -1));
        softly.assertThat(result.getDuration().getFrom()).isEqualTo(1);
        softly.assertThat(result.getDuration().getTo()).isEqualTo(10);
        softly.assertThat(result.getDaysOnStudyAtStart().getFrom()).isEqualTo(1);
        softly.assertThat(result.getDaysOnStudyAtStart().getTo()).isEqualTo(3);
        softly.assertThat(result.getDaysOnStudyAtEnd().getFrom()).isEqualTo(2);
        softly.assertThat(result.getDaysOnStudyAtEnd().getTo()).isEqualTo(13);
    }

    @Test
    public void shouldGetSubjects() {
        //Given
        List<Exacerbation> events = Arrays.asList(exacerb_1, exacerb_2, exacerb_3);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(subject1, subject2));

        //When
        List<String> results = exacerbationService.getSubjects(DUMMY_ACUITY_LUNG_FUNC_DATASETS, ExacerbationFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(results).hasSize(1);
        softly.assertThat(results).containsOnly("subject1");
    }

    @Test
    public void shouldGetAllBarChartXAxisForAcuityDatasets() {
        //Given
        List<Exacerbation> events = Arrays.asList(exacerb_1, exacerb_2, exacerb_3);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject1));

        //When
        AxisOptions<ExacerbationGroupByOptions> result =
                exacerbationService.getAvailableBarChartXAxis(
                        DUMMY_ACUITY_LUNG_FUNC_DATASETS, ExacerbationFilters.empty(), PopulationFilters.empty());

        //Then
        List<AxisOption<ExacerbationGroupByOptions>> options = result.getOptions();
        softly.assertThat(options).hasSize(17);
        softly.assertThat(options).extracting(AxisOption::getGroupByOption).containsExactly(
                ExacerbationGroupByOptions.NONE,
                ExacerbationGroupByOptions.STUDY_ID,
                ExacerbationGroupByOptions.STUDY_NAME,
                ExacerbationGroupByOptions.STUDY_PART_ID,
                ExacerbationGroupByOptions.DURATION_ON_STUDY,
                ExacerbationGroupByOptions.RANDOMISATION_DATE,
                ExacerbationGroupByOptions.WITHDRAWAL,
                ExacerbationGroupByOptions.REASON_FOR_WITHDRAWAL,
                ExacerbationGroupByOptions.CENTRE,
                ExacerbationGroupByOptions.SEX,
                ExacerbationGroupByOptions.RACE,
                ExacerbationGroupByOptions.AGE,
                ExacerbationGroupByOptions.WEIGHT,
                ExacerbationGroupByOptions.HEIGHT,
                ExacerbationGroupByOptions.FIRST_TREATMENT_DATE,
                ExacerbationGroupByOptions.DEATH,
                ExacerbationGroupByOptions.DATE_OF_DEATH);
    }

    @Test
    public void shouldGetAvailableBarChartXAxisForAcuityDatasets() {
        //Given
        List<Exacerbation> events = Collections.singletonList(exacerb_5);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject2));

        //When
        AxisOptions<ExacerbationGroupByOptions> result =
                exacerbationService.getAvailableBarChartXAxis(
                        DUMMY_ACUITY_LUNG_FUNC_DATASETS, ExacerbationFilters.empty(), PopulationFilters.empty());

        //Then
        List<AxisOption<ExacerbationGroupByOptions>> options = result.getOptions();
        softly.assertThat(options).hasSize(3);
        softly.assertThat(options).extracting(AxisOption::getGroupByOption).containsExactly(
                ExacerbationGroupByOptions.NONE,
                ExacerbationGroupByOptions.STUDY_ID,
                ExacerbationGroupByOptions.WITHDRAWAL);
    }

    @Test
    public void shouldGetAllBarChartColorByOptions() {
        //Given
        List<Exacerbation> events = Arrays.asList(exacerb_1, exacerb_2, exacerb_3);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(subject1));

        //When
        List<TrellisOptions<ExacerbationGroupByOptions>> result = exacerbationService.getBarChartColorByOptions(
                DUMMY_ACUITY_LUNG_FUNC_DATASETS, ExacerbationFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(result).hasSize(7);
        softly.assertThat(result).extracting(TrellisOptions::getTrellisOptions).asList().containsExactly(
                asList("classification_1", "classification_2", Attributes.DEFAULT_EMPTY_VALUE),
                asList("hospitalisation_1", "hospitalisation_2", Attributes.DEFAULT_EMPTY_VALUE),
                asList("emergency_room_visit_1", "emergency_room_visit_2", Attributes.DEFAULT_EMPTY_VALUE),
                asList("depot_gcs_trt_1", "depot_gcs_trt_2", Attributes.DEFAULT_EMPTY_VALUE),
                asList("inc_inhaled_cort_trt_1", "inc_inhaled_cort_trt_2", Attributes.DEFAULT_EMPTY_VALUE),
                asList("sys_cort_trt_1", "sys_cort_trt_2", Attributes.DEFAULT_EMPTY_VALUE),
                asList("antibiotics_trt_2", Attributes.DEFAULT_EMPTY_VALUE));
    }

    @Test
    public void shouldGetAvailableBarChartColorByOptions() {
        //Given
        List<Exacerbation> events = Arrays.asList(exacerb_3, exacerb_4);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(subject1));

        //When
        List<TrellisOptions<ExacerbationGroupByOptions>> result = exacerbationService.getBarChartColorByOptions(
                DUMMY_ACUITY_LUNG_FUNC_DATASETS, ExacerbationFilters.empty(), PopulationFilters.empty());

        //Then
        softly.assertThat(result).hasSize(2);
        softly.assertThat(result).extracting(TrellisOptions::getTrellisOptions).asList().containsExactly(
                asList("hosp_4", Attributes.DEFAULT_EMPTY_VALUE),
                asList("antibio_4", Attributes.DEFAULT_EMPTY_VALUE));
    }

    @Test
    public void shouldGetBarChart() {
        //Given
        List<Exacerbation> events = Arrays.asList(exacerb_1, exacerb_2, exacerb_3, exacerb_4, exacerb_5);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(subject1, subject2));

        Map<ChartGroupByOptions.ChartGroupBySetting, ChartGroupByOptions.GroupByOptionAndParams<Subject, PopulationGroupByOptions>> options = new HashMap<>();
        options.put(COLOR_BY, new ChartGroupByOptions.GroupByOptionAndParams(ExacerbationGroupByOptions.HOSPITALISATION, new HashMap<>()));
        options.put(X_AXIS, new ChartGroupByOptions.GroupByOptionAndParams(ExacerbationGroupByOptions.WITHDRAWAL, new HashMap<>()));

        ChartGroupByOptions<Exacerbation, ExacerbationGroupByOptions> sett = new ChartGroupByOptions(options, new HashSet<>());
        ChartGroupByOptionsFiltered<Exacerbation, ExacerbationGroupByOptions> settings = new ChartGroupByOptionsFiltered(sett, new ArrayList<>());

        List<TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>> result = exacerbationService.getBarChart(DETECT_DATASETS,
                settings, ExacerbationFilters.empty(), PopulationFilters.empty(), CountType.COUNT_OF_SUBJECTS);

        softly.assertThat(result).size().isEqualTo(1);
        softly.assertThat(result.get(0).getTrellisedBy()).isEmpty();

        List<? extends OutputBarChartData> data = result.get(0).getData();
        softly.assertThat(data).size().isEqualTo(4);
        softly.assertThat(data.get(0).getName()).isEqualTo("hosp_4");
        softly.assertThat(data.get(0).getCategories()).containsExactlyInAnyOrder("Yes", "No");

        List<OutputBarChartEntry> series = data.get(0).getSeries();
        softly.assertThat(series).size().isEqualTo(1);
        softly.assertThat(series.get(0).getCategory()).isEqualTo("Yes");
        softly.assertThat(series.get(0).getValue()).isEqualTo(1.0);
        softly.assertThat(series.get(0).getTotalSubjects()).isEqualTo(1);
        softly.assertThat(series.get(0).getRank()).isEqualTo(1);

        softly.assertThat(data.get(1).getName()).isEqualTo("hospitalisation_1");
        softly.assertThat(data.get(1).getCategories()).containsExactlyInAnyOrder("Yes", "No");

        series = data.get(1).getSeries();
        softly.assertThat(series).size().isEqualTo(1);
        softly.assertThat(series.get(0).getCategory()).isEqualTo("Yes");
        softly.assertThat(series.get(0).getValue()).isEqualTo(1.0);
        softly.assertThat(series.get(0).getTotalSubjects()).isEqualTo(1);
        softly.assertThat(series.get(0).getRank()).isEqualTo(1);

        softly.assertThat(data.get(2).getName()).isEqualTo("hospitalisation_2");
        softly.assertThat(data.get(2).getCategories()).containsExactlyInAnyOrder("Yes", "No");

        series = data.get(2).getSeries();
        softly.assertThat(series).size().isEqualTo(1);
        softly.assertThat(series.get(0).getCategory()).isEqualTo("Yes");
        softly.assertThat(series.get(0).getValue()).isEqualTo(1.0);
        softly.assertThat(series.get(0).getTotalSubjects()).isEqualTo(1);
        softly.assertThat(series.get(0).getRank()).isEqualTo(1);

        softly.assertThat(data.get(3).getName()).isEqualTo(Attributes.DEFAULT_EMPTY_VALUE);
        softly.assertThat(data.get(3).getCategories()).containsExactlyInAnyOrder("Yes", "No");

        series = data.get(3).getSeries();
        softly.assertThat(series).size().isEqualTo(2);
        softly.assertThat(series.get(1).getCategory()).isEqualTo("No");
        softly.assertThat(series.get(1).getValue()).isEqualTo(1.0);
        softly.assertThat(series.get(1).getTotalSubjects()).isEqualTo(1);
        softly.assertThat(series.get(1).getRank()).isEqualTo(2);

        softly.assertThat(series.get(0).getCategory()).isEqualTo("Yes");
        softly.assertThat(series.get(0).getValue()).isEqualTo(1.0);
        softly.assertThat(series.get(0).getTotalSubjects()).isEqualTo(1);
        softly.assertThat(series.get(0).getRank()).isEqualTo(1);
    }

    @Test
    public void shouldGetBarChartSelection() {
        // Given
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(exacerb_1, exacerb_2, exacerb_3));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(subject1));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Exacerbation, ExacerbationGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, ExacerbationGroupByOptions.AGE.getGroupByOptionAndParams());
        settings.withOption(COLOR_BY, ExacerbationGroupByOptions.HOSPITALISATION.getGroupByOptionAndParams());
        final HashMap<ExacerbationGroupByOptions, Object> selectedTrellises = new HashMap<>();
        final HashMap<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(COLOR_BY, "hospitalisation_1");
        selectedItems.put(X_AXIS, "55-55");

        // When
        SelectionDetail selectionDetails = exacerbationService.getSelectionDetails(DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                ExacerbationFilters.empty(), PopulationFilters.empty(), ChartSelection.of(settings.build(),
                        Collections.singletonList(ChartSelectionItem.of(selectedTrellises, selectedItems))));

        // Then
        softly.assertThat(selectionDetails.getEventIds()).containsOnly("1");
        softly.assertThat(selectionDetails.getSubjectIds()).containsOnly("subject1");
        softly.assertThat(selectionDetails.getTotalEvents()).isEqualTo(3);
        softly.assertThat(selectionDetails.getTotalSubjects()).isEqualTo(1);
    }

    @Test
    public void shouldGetAllAcuityDetailsOnDemandColumnsInCorrectOrder() {
        // Given
        List<Exacerbation> exacerbations = Arrays.asList(exacerb_1, exacerb_2);

        // When
        Map<String, String> columns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, exacerbations);

        // Then
        softly.assertThat(columns.keySet())
                .containsExactly("studyId", "studyPart", "subjectId", "exacerbationClassification", "startDate", "endDate",
                        "daysOnStudyAtStart", "daysOnStudyAtEnd", "duration", "startPriorToRandomisation", "endPriorToRandomisation",
                        "hospitalisation", "emergencyRoomVisit", "antibioticsTreatment", "depotCorticosteroidTreatment",
                        "systemicCorticosteroidTreatment", "increasedInhaledCorticosteroidTreatment");
        softly.assertThat(columns.values())
                .containsExactly("Study id", "Study part", "Subject id", "Exacerbation Classification", "Start Date", "End Date",
                        "Days On Study At Start", "Days On Study At End", "Duration", "Start Prior To Randomisation",
                        "End Prior To Randomisation", "Hospitalisation", "Emergency Room Visit", "Antibiotics Treatment",
                        "Depot Corticosteroid Treatment", "Systemic Corticosteroid Treatment", "Increased Inhaled Corticosteroid Treatment");
    }

    @Test
    public void shouldGetAcuityDetailsOnDemandColumnsInCorrectOrder() {
        // Given
        List<Exacerbation> exacerbation = Collections.singletonList(exacerb_3);

        // When
        Map<String, String> columns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, exacerbation);

        // Then
        softly.assertThat(columns.keySet())
                .containsExactly("studyId", "studyPart", "subjectId");
        softly.assertThat(columns.values())
                .containsExactly("Study id", "Study part", "Subject id");
    }

    @Test
    public void shouldGetAcuityDetailsOnDemand() {
        // Given
        List<Exacerbation> exacerbations = Arrays.asList(exacerb_1, exacerb_2);
        Set<String> labsIds = exacerbations.stream().map(Exacerbation::getId).collect(toSet());
        when(exacerbationDatasetsDataProvider.loadData(any())).thenReturn(exacerbations);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(subject1));

        // When
        List<Map<String, String>> doDData = exacerbationService.getDetailsOnDemandData(DUMMY_ACUITY_DATASETS, labsIds, Collections.emptyList(), 0, Integer.MAX_VALUE);

        // Then
        Exacerbation exacerbation = exacerb_2;
        Map<String, String> dod = doDData.get(0);

        softly.assertThat(doDData).hasSize(exacerbations.size());
        softly.assertThat(dod.size()).isEqualTo(18);
        softly.assertThat(exacerbation.getSubjectCode()).isEqualTo(dod.get("subjectId"));
        softly.assertThat(exacerbation.getStudyId()).isEqualTo(dod.get("studyId"));
        softly.assertThat(exacerbation.getEvent().getStartDate()).isInSameDayAs(dod.get("startDate"));
        softly.assertThat(exacerbation.getEvent().getEndDate()).isInSameDayAs(dod.get("endDate"));
        softly.assertThat(exacerbation.getEvent().getDaysOnStudyAtStart()).isEqualTo(Integer.valueOf(dod.get("daysOnStudyAtStart")));
        softly.assertThat(exacerbation.getEvent().getDaysOnStudyAtEnd()).isEqualTo(Integer.valueOf(dod.get("daysOnStudyAtEnd")));
        softly.assertThat(exacerbation.getEvent().getDuration()).isEqualTo(Integer.valueOf(dod.get("duration")));
        softly.assertThat(exacerbation.getEvent().getStartPriorToRandomisation()).isEqualTo(dod.get("startPriorToRandomisation"));
        softly.assertThat(exacerbation.getEvent().getEndPriorToRandomisation()).isEqualTo(dod.get("endPriorToRandomisation"));
        softly.assertThat(exacerbation.getEvent().getExacerbationClassification()).isEqualTo(dod.get("exacerbationClassification"));
        softly.assertThat(exacerbation.getEvent().getHospitalisation()).isEqualTo(dod.get("hospitalisation"));
        softly.assertThat(exacerbation.getEvent().getEmergencyRoomVisit()).isEqualTo(dod.get("emergencyRoomVisit"));
        softly.assertThat(exacerbation.getEvent().getAntibioticsTreatment()).isEqualTo(dod.get("antibioticsTreatment"));
        softly.assertThat(exacerbation.getEvent().getIncreasedInhaledCorticosteroidTreatment()).isEqualTo(dod.get("increasedInhaledCorticosteroidTreatment"));
        softly.assertThat(exacerbation.getEvent().getDepotCorticosteroidTreatment()).isEqualTo(dod.get("depotCorticosteroidTreatment"));
        softly.assertThat(exacerbation.getEvent().getSystemicCorticosteroidTreatment()).isEqualTo(dod.get("systemicCorticosteroidTreatment"));
    }

    @Test
    public void shouldGetAvailableOverTimeXAxisOptions() {

        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(exacerb_1, exacerb_2));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1));

        AxisOptions<ExacerbationGroupByOptions> availableOvertimeXAxis =
                exacerbationService.getAvailableOverTimeChartXAxis(DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        ExacerbationFilters.empty(), PopulationFilters.empty());


        softly.assertThat(availableOvertimeXAxis.isHasRandomization()).isTrue();
        softly.assertThat(availableOvertimeXAxis.getOptions())
                .extracting(AxisOption::getGroupByOption, AxisOption::isBinableOption, AxisOption::isTimestampOption, AxisOption::isSupportsDuration)
                .containsExactly(
                        tuple(OVERTIME_DURATION, true, true, true)
                );
    }

    @Test
    public void shouldGetLineBarChartColorsByOptions() {
        //Given
        List<Exacerbation> events = Arrays.asList(exacerb_1, exacerb_2);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1));

        List<TrellisOptions<ExacerbationGroupByOptions>> colorByOptions = exacerbationService
                .getLineBarChartColorByOptions(DUMMY_ACUITY_LUNG_FUNC_DATASETS, ExacerbationFilters.empty(), PopulationFilters.empty());
        softly.assertThat(colorByOptions).hasSize(7);
        softly.assertThat(colorByOptions).extracting(TrellisOptions::getTrellisedBy).contains(
                ExacerbationGroupByOptions.EXACERBATION_SEVERITY,
                ExacerbationGroupByOptions.HOSPITALISATION,
                ExacerbationGroupByOptions.EMERGENCY_ROOM_VISIT,
                ExacerbationGroupByOptions.DEPOT_CORTICOSTEROID_TREATMENT,
                ExacerbationGroupByOptions.INCREASED_INHALED_CORTICOSTEROID_TREATMENT,
                ExacerbationGroupByOptions.SYSTEMIC_CORTICOSTEROID_TREATMENT,
                ExacerbationGroupByOptions.ANTIBIOTICS_TREATMENT);
        softly.assertThat(colorByOptions).extracting(TrellisOptions::getTrellisOptions).asList().containsExactly(
                Arrays.asList("classification_1", "classification_2"),
                Arrays.asList("hospitalisation_1", "hospitalisation_2"),
                Arrays.asList("emergency_room_visit_1", "emergency_room_visit_2"),
                Arrays.asList("depot_gcs_trt_1", "depot_gcs_trt_2"),
                Arrays.asList("inc_inhaled_cort_trt_1", "inc_inhaled_cort_trt_2"),
                Arrays.asList("sys_cort_trt_1", "sys_cort_trt_2"),
                Arrays.asList("antibiotics_trt_2"));
    }

    @Test
    public void shouldGetLineBarChartValuesBinnedByDaysSinceFirstDoseWithInclDuration() {
        //Given
        List<Exacerbation> events = Arrays.asList(exacerb_10, exacerb_11, exacerb_12);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1, subject6));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Exacerbation, ExacerbationGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, ExacerbationGroupByOptions.EXACERBATION_SEVERITY.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_INCL_DURATION, true)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Exacerbation, ExacerbationGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        List<TrellisedOvertime<Exacerbation, ExacerbationGroupByOptions>> lineBarChartValues = exacerbationService
                .getLineBarChart(DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        settingsWithFilterBy.build(), ExacerbationFilters.empty(), PopulationFilters.empty());

        OutputOvertimeData data = lineBarChartValues.get(0).getData();
        softly.assertThat(data.getCategories()).hasSize(6);
        softly.assertThat(data.getCategories()).containsExactlyInAnyOrder(
                "827",
                "828",
                "829",
                "830",
                "831",
                "832"
        );

        softly.assertThat(data.getSeries()).hasSize(2);
        softly.assertThat(data.getSeries()).extracting(OutputBarChartData::getName).
                containsExactly("classification_1", "classification_2");

        softly.assertThat(data.getSeries().get(0).getCategories()).containsExactly(
                "827",
                "828",
                "829",
                "830",
                "831",
                "832"
        );
        softly.assertThat(data.getSeries().get(0).getSeries()).extracting("category", "rank", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple("827", 1, 1.0, 1),
                tuple("828", 2, 1.0, 1),
                tuple("829", 3, 1.0, 1),
                tuple("830", 4, 1.0, 1),
                tuple("831", 5, 1.0, 1),
                tuple("832", 6, 1.0, 1)
        );
        softly.assertThat(data.getSeries().get(1).getCategories()).containsExactly(
                "827",
                "828",
                "829",
                "830",
                "831",
                "832"
        );
        softly.assertThat(data.getSeries().get(1).getSeries()).extracting("category", "rank", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple("830", 4, 1.0, 1),
                tuple("831", 5, 1.0, 1),
                tuple("832", 6, 1.0, 1)
        );

        List<OutputBarChartEntry> line = data.getLines().get(0).getSeries();
        softly.assertThat(line).hasSize(6);
        softly.assertThat(line).extracting("category", "rank", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple("827", 1, 2.0, 2),
                tuple("828", 2, 2.0, 2),
                tuple("829", 3, 2.0, 2),
                tuple("830", 4, 2.0, 2),
                tuple("831", 5, 2.0, 2),
                tuple("832", 6, 2.0, 2)
        );
    }

    @Test
    public void shouldGetLineBarChartValuesBinnedByDaysSinceFirstDoseWithExclDuration() {
        //Given
        List<Exacerbation> events = Arrays.asList(exacerb_10, exacerb_11, exacerb_12);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1, subject6));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Exacerbation, ExacerbationGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, ExacerbationGroupByOptions.EXACERBATION_SEVERITY.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_INCL_DURATION, false)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Exacerbation, ExacerbationGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());

        List<TrellisedOvertime<Exacerbation, ExacerbationGroupByOptions>> lineBarChartValues = exacerbationService
                .getLineBarChart(DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        settingsWithFilterBy.build(), ExacerbationFilters.empty(), PopulationFilters.empty());

        OutputOvertimeData data = lineBarChartValues.get(0).getData();
        softly.assertThat(data.getCategories()).hasSize(5);
        softly.assertThat(data.getCategories()).containsExactlyInAnyOrder(
                "827",
                "828",
                "829",
                "830",
                "831"
        );

        softly.assertThat(data.getSeries()).hasSize(2);
        softly.assertThat(data.getSeries()).extracting(OutputBarChartData::getName).
                containsExactly("classification_1", "classification_2");

        softly.assertThat(data.getSeries().get(0).getCategories()).containsExactly(
                "827",
                "828",
                "829",
                "830",
                "831"
        );
        softly.assertThat(data.getSeries().get(0).getSeries()).extracting("category", "rank", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple("827", 1, 1.0, 1),
                tuple("831", 5, 1.0, 1)
        );
        softly.assertThat(data.getSeries().get(1).getCategories()).containsExactly(
                "827",
                "828",
                "829",
                "830",
                "831"
        );
        softly.assertThat(data.getSeries().get(1).getSeries()).extracting("category", "rank", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple("830", 4, 1.0, 1)
        );

        List<OutputBarChartEntry> line = data.getLines().get(0).getSeries();
        softly.assertThat(line).hasSize(5);
        softly.assertThat(line).extracting("category", "rank", "value", "totalSubjects").containsExactlyInAnyOrder(
                tuple("827", 1, 2.0, 2),
                tuple("828", 2, 2.0, 2),
                tuple("829", 3, 2.0, 2),
                tuple("830", 4, 2.0, 2),
                tuple("831", 5, 2.0, 2)
        );
    }

    @Test
    public void shouldGetOverTimeLineBarChartSelection() {
        // Given
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(exacerb_10, exacerb_11, exacerb_12));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(subject1, subject6));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Exacerbation, ExacerbationGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_INCL_DURATION, false)
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.DAYS_SINCE_FIRST_DOSE)
                        .build()))
                .withOption(ChartGroupByOptions.ChartGroupBySetting.COLOR_BY,
                        ExacerbationGroupByOptions.EXACERBATION_SEVERITY.getGroupByOptionAndParams());
        Map<ExacerbationGroupByOptions, Object> selectedTrellises = new HashMap<>();
        Map<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(X_AXIS, 827);
        selectedItems.put(COLOR_BY, "classification_1");

        SelectionDetail selectionResult = exacerbationService
                .getOverTimeLineBarChartSelectionDetails(DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        ExacerbationFilters.empty(), PopulationFilters.empty(),
                        ChartSelection.of(settings.build(), Collections.singleton(ChartSelectionItem.of(selectedTrellises,
                                selectedItems))));
        softly.assertThat(selectionResult.getEventIds()).hasSize(1);
        softly.assertThat(selectionResult.getSubjectIds()).hasSize(1);
        softly.assertThat(selectionResult.getTotalEvents()).isEqualTo(3);
        softly.assertThat(selectionResult.getTotalSubjects()).isEqualTo(2);
    }

    @Test
    public void shouldGetOnsetLineChartColorsByOptions() {
        //Given
        List<Exacerbation> events = Arrays.asList(exacerb_7, exacerb_8);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject3, subject4));

        List<TrellisOptions<ExacerbationGroupByOptions>> colorByOptions = exacerbationService
                .getOnsetLineChartColorByOptions(DUMMY_ACUITY_LUNG_FUNC_DATASETS, ExacerbationFilters.empty(), PopulationFilters.empty());
        softly.assertThat(colorByOptions).hasSize(1);
        softly.assertThat(colorByOptions).extracting(TrellisOptions::getTrellisedBy).contains(
                ExacerbationGroupByOptions.PLANNED_TREATMENT_ARM);
        softly.assertThat(colorByOptions).extracting(TrellisOptions::getTrellisOptions).isEqualTo(Arrays.asList(
                Arrays.asList("PLACEBO", "PLACEBO2")));


    }

    @Test
    public void shouldGetOnsetLineChartValuesWithCumulativeCountType() {
        //Given
        List<Exacerbation> events = Arrays.asList(exacerb_7, exacerb_8, exacerb_9);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject3, subject4, subject5));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Exacerbation, ExacerbationGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, ExacerbationGroupByOptions.PLANNED_TREATMENT_ARM.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.BIN_INCL_DURATION, true)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.WEEKS_SINCE_FIRST_DOSE)
                        .build()));
        final ChartGroupByOptions.GroupByOptionAndParams<Exacerbation, ExacerbationGroupByOptions> trellis =
                ExacerbationGroupByOptions.PLANNED_TREATMENT_ARM.getGroupByOptionAndParams();
        settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Exacerbation, ExacerbationGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
        settingsWithFilterBy.withFilterByTrellisOption(ExacerbationGroupByOptions.PLANNED_TREATMENT_ARM, "test");

        List<TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>> onsetLineChartValues = exacerbationService
                .getOnsetLineChartValues(DUMMY_ACUITY_LUNG_FUNC_DATASETS, settingsWithFilterBy.build(), ExacerbationFilters.empty(),
                        PopulationFilters.empty(), CountType.CUMULATIVE_COUNT_OF_EVENTS);
        softly.assertThat(onsetLineChartValues).extracting(TrellisedBarChart::getTrellisedBy).hasSize(2);
        softly.assertThat(onsetLineChartValues).extracting(TrellisedBarChart::getTrellisedBy)
                .contains(
                        Collections.singletonList(TrellisOption.of(PLANNED_TREATMENT_ARM, "PLACEBO2")),
                        Collections.singletonList(TrellisOption.of(PLANNED_TREATMENT_ARM, "PLACEBO")));
        softly.assertThat(onsetLineChartValues.get(0).getData().get(0).getSeries().get(0))
                .isEqualTo(new OutputBarChartEntry("-813", 1, 1.0d, 1));
        softly.assertThat(onsetLineChartValues.get(1).getData().get(0).getSeries().get(1))
                .isEqualTo(new OutputBarChartEntry("-812", 2, 1.0d, 1));
    }

    @Test
    public void shouldGetOnSetLineChartSelectionDetails() {
        //Given
        List<Exacerbation> events = Arrays.asList(exacerb_7, exacerb_8);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject3, subject4));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Exacerbation, ExacerbationGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(X_AXIS, OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.WEEKS_SINCE_FIRST_DOSE)
                        .build()));
        final ChartGroupByOptions.GroupByOptionAndParams<Exacerbation, ExacerbationGroupByOptions> trellis =
                ExacerbationGroupByOptions.PLANNED_TREATMENT_ARM.getGroupByOptionAndParams();
        settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        Map<ExacerbationGroupByOptions, Object> selectedTrelises = new HashMap<>();
        Map<ChartGroupByOptions.ChartGroupBySetting, Object> selectedItems = new HashMap<>();
        selectedItems.put(X_AXIS, "-813");
        selectedTrelises.put(PLANNED_TREATMENT_ARM, "PLACEBO");

        SelectionDetail selectionResult = exacerbationService
                .getOnSetLineChartSelectionDetails(DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        ExacerbationFilters.empty(), PopulationFilters.empty(),
                        ChartSelection.of(settings.build(), Collections.singleton(ChartSelectionItem.of(selectedTrelises,
                                selectedItems))), CountType.COUNT_OF_EVENTS);
        softly.assertThat(selectionResult.getEventIds()).hasSize(1);
        softly.assertThat(selectionResult.getSubjectIds()).hasSize(1);
        softly.assertThat(selectionResult.getTotalEvents()).isEqualTo(2);
        softly.assertThat(selectionResult.getTotalSubjects()).isEqualTo(2);

    }

    @Test
    public void shouldGetAvailableOnsetLineChartXAxis() {
        //Given
        List<Exacerbation> events = Arrays.asList(exacerb_1, exacerb_2, exacerb_3);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject1, subject2));

        AxisOptions<ExacerbationGroupByOptions> axisOptions = exacerbationService
                .getAvailableOnsetLineChartXAxis(DUMMY_ACUITY_LUNG_FUNC_DATASETS, ExacerbationFilters.empty(), PopulationFilters.empty());
        softly.assertThat(axisOptions.getDrugs()).isEmpty();
        softly.assertThat(axisOptions.isHasRandomization()).isTrue();
        softly.assertThat(axisOptions.getOptions())
                .extracting(AxisOption::getGroupByOption, AxisOption::isBinableOption, AxisOption::isTimestampOption, AxisOption::isSupportsDuration)
                .containsExactly(
                        tuple(OVERTIME_DURATION, true, true, true)
                );

    }

    @Test
    public void shouldGetOnsetLineChartValues() {
        //Given
        List<Exacerbation> events = Arrays.asList(exacerb_7, exacerb_8);
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(events);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(subject3, subject4));

        final ChartGroupByOptions.ChartGroupBySettingsBuilder<Exacerbation, ExacerbationGroupByOptions> settings = ChartGroupByOptions.builder();
        settings.withOption(COLOR_BY, ExacerbationGroupByOptions.PLANNED_TREATMENT_ARM.getGroupByOptionAndParams());
        settings.withOption(X_AXIS, OVERTIME_DURATION.getGroupByOptionAndParams(
                GroupByOption.Params.builder()
                        .with(GroupByOption.Param.BIN_SIZE, 1)
                        .with(GroupByOption.Param.TIMESTAMP_TYPE, GroupByOption.TimestampType.WEEKS_SINCE_FIRST_DOSE)
                        .build()));
        final ChartGroupByOptions.GroupByOptionAndParams<Exacerbation, ExacerbationGroupByOptions> trellis =
                ExacerbationGroupByOptions.PLANNED_TREATMENT_ARM.getGroupByOptionAndParams();
        settings.withTrellisOptions(new HashSet<>(Collections.singletonList(trellis)));
        final ChartGroupByOptionsFiltered.ChartGroupBySettingsFilteredBuilder<Exacerbation, ExacerbationGroupByOptions> settingsWithFilterBy =
                ChartGroupByOptionsFiltered.builder(settings.build());
        settingsWithFilterBy.withFilterByTrellisOption(ExacerbationGroupByOptions.PLANNED_TREATMENT_ARM, "test");

        List<TrellisedBarChart<Exacerbation, ExacerbationGroupByOptions>> onsetLineChartValues = exacerbationService
                .getOnsetLineChartValues(DUMMY_ACUITY_LUNG_FUNC_DATASETS,
                        settingsWithFilterBy.build(), ExacerbationFilters.empty(), PopulationFilters.empty(), CountType.COUNT_OF_EVENTS);
        softly.assertThat(onsetLineChartValues).extracting(TrellisedBarChart::getTrellisedBy).hasSize(2);
        softly.assertThat(onsetLineChartValues).extracting(TrellisedBarChart::getTrellisedBy)
                .contains(
                        Collections.singletonList(TrellisOption.of(PLANNED_TREATMENT_ARM, "PLACEBO")),
                        Collections.singletonList(TrellisOption.of(PLANNED_TREATMENT_ARM, "PLACEBO2")));
        softly.assertThat(onsetLineChartValues.get(0).getData().get(0).getSeries().get(0))
                .isEqualTo(new OutputBarChartEntry("-813", 1, 1.0d, 1));
    }



}
