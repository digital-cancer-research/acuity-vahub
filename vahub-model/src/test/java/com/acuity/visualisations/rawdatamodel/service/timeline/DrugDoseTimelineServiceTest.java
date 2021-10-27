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

package com.acuity.visualisations.rawdatamodel.service.timeline;

import com.acuity.visualisations.common.vo.DayZeroType;
import com.acuity.visualisations.rawdatamodel.dataproviders.DoseDiscDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DrugDoseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.DoseDiscRaw;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.DosingSummaryEvent;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.MaxDoseType;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.PeriodType;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.SubjectDosingSummary;
import com.acuity.visualisations.rawdatamodel.vo.timeline.dose.SubjectDrugDosingSummary;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static org.assertj.core.util.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class DrugDoseTimelineServiceTest {
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Autowired
    private DrugDoseTimelineService drugDoseTimelineService;

    @MockBean
    private DrugDoseDatasetsDataProvider drugDoseDatasetsDataProvider;

    @MockBean
    private DoseDiscDatasetsDataProvider doseDiscDatasetsDataProvider;

    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    private static final Subject SUBJECT_1_ACUITY = Subject.builder().subjectId("sid2").clinicalStudyCode("STUDYID001").studyPart("A")
            .subjectCode("E02").actualArm("Placebo").drugFirstDoseDate("Placebo", DateUtils.toDate("01.08.2015"))
            .firstTreatmentDate(toDate("2014-12-02")).dateOfRandomisation(toDate("2015-01-02")).lastEtlDate(toDate("2014-12-21"))
            .studyInfo(StudyInfo.builder().datasetType(StudyInfo.DatasetType.ACUITY).build()).build();

    private static final DrugDose DD_1_ACUITY = new DrugDose(DrugDoseRaw.builder().drug("drug1").dose(7.).doseUnit("mg")
            .startDate(toDate("2014-12-02")).endDate(toDate("2014-12-10")).build(), SUBJECT_1_ACUITY);
    private static final DrugDose DD_2_ACUITY = new DrugDose(DrugDoseRaw.builder().drug("drug1").dose(5.).doseUnit("mg")
            .startDate(toDate("2014-12-02")).endDate(toDate("2014-12-10")).build(), SUBJECT_1_ACUITY);
    private static final DrugDose DD_3_ACUITY = new DrugDose(DrugDoseRaw.builder().drug("drug1").dose(3.).doseUnit("mg")
            .startDate(toDate("2014-12-11")).build(), SUBJECT_1_ACUITY);
    private static final DrugDose DD_4_ACUITY = new DrugDose(DrugDoseRaw.builder().drug("drug1").dose(1.).doseUnit("mg")
            .startDate(toDate("2014-12-13")).endDate(toDate("2014-12-15")).build(), SUBJECT_1_ACUITY);
    private static final DrugDose DD_5_ACUITY = new DrugDose(DrugDoseRaw.builder().drug("drug1").dose(0.5).doseUnit("mg")
            .startDate(toDate("2014-12-13")).build(), SUBJECT_1_ACUITY);
    private static final DrugDose DD_6_ACUITY = new DrugDose(DrugDoseRaw.builder().drug("drug2").dose(0.0).doseUnit("mg")
            .startDate(toDate("2014-12-18")).periodType(PeriodType.INACTIVE.getDbValue()).build(), SUBJECT_1_ACUITY);
    private static final DrugDose DD_7_ACUITY = new DrugDose(DrugDoseRaw.builder().drug("drug1").dose(0.0).doseUnit("mg")
            .startDate(toDate("2014-12-18")).endDate(toDate("2014-12-11"))
            .periodType(PeriodType.ACTIVE.getDbValue()).build(), SUBJECT_1_ACUITY);
    private static final DrugDose DD_8_ACUITY = new DrugDose(DrugDoseRaw.builder().drug("drug1").dose(0.0).doseUnit("mg")
            .startDate(toDate("2014-12-18")).endDate(toDate("2014-12-20"))
            .periodType(PeriodType.ACTIVE.getDbValue()).build(), SUBJECT_1_ACUITY);

    private static final DoseDisc DOSE_DISC_1 = new DoseDisc(DoseDiscRaw.builder().subjectId("id1").studyDrug("drug1")
            .discDate(toDate("2014-12-17")).build(), SUBJECT_1_ACUITY);
    private static final DoseDisc DOSE_DISC_2 = new DoseDisc(DoseDiscRaw.builder().subjectId("id1").studyDrug("drug1")
            .discDate(toDate("2014-12-11")).build(), SUBJECT_1_ACUITY);
    private static final DoseDisc DOSE_DISC_3 = new DoseDisc(DoseDiscRaw.builder().subjectId("id1").studyDrug("drug1")
            .discDate(toDate("2014-12-12")).build(), SUBJECT_1_ACUITY);
    private static final DoseDisc DOSE_DISC_4 = new DoseDisc(DoseDiscRaw.builder().subjectId("id1").studyDrug("drug2")
            .discDate(toDate("2014-12-20")).build(), SUBJECT_1_ACUITY);
    private static final DoseDisc DOSE_DISC_5 = new DoseDisc(DoseDiscRaw.builder().subjectId("id1").studyDrug("drug1")
            .discDate(toDate("2014-12-19")).build(), SUBJECT_1_ACUITY);

    @Test
    public void shouldGetDrugDoseEventsInCorrectOrderWhenEqualDrugDateAndFrequencyForAcuityDatasets() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJECT_1_ACUITY));
        when(drugDoseDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(DD_1_ACUITY, DD_2_ACUITY));
        when(doseDiscDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.emptyList());

        List<SubjectDosingSummary> dosingSummaries = drugDoseTimelineService.getDosingSummaries(DATASETS,
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null, MaxDoseType.PER_SUBJECT,
                DrugDoseFilters.empty(), PopulationFilters.empty());
        SubjectDosingSummary subjectDosingSummary = dosingSummaries.get(0);
        List<DosingSummaryEvent> events = subjectDosingSummary.getEvents();

        softly.assertThat(events).hasSize(2);
        softly.assertThat(events).extracting("start.dayHour").containsOnly(0.0, 8.0);
        softly.assertThat(events).extracting("end.dayHour").containsOnly(8.0, 19.0);
        softly.assertThat(events).extracting("ongoing").containsOnly(false, true);
        softly.assertThat(events).extracting("active").containsOnly(true, false);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("drug").containsOnly("drug1");
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("dose").containsExactly(7.0, 5.0, 0.0);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("doseUnit").containsSequence("mg", "mg");
    }

    @Test
    public void shouldGetDosingSummaryEventsWhenIntersectingDosesForAcuityDatasets() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJECT_1_ACUITY));
        when(drugDoseDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(DD_3_ACUITY, DD_4_ACUITY));
        when(doseDiscDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(DOSE_DISC_1));

        List<SubjectDosingSummary> subjectDosingSummaries = drugDoseTimelineService.getDosingSummaries(DATASETS,
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null, MaxDoseType.PER_SUBJECT,
                DrugDoseFilters.empty(), PopulationFilters.empty());
        SubjectDosingSummary subjectDosingSummary = subjectDosingSummaries.get(0);
        List<DosingSummaryEvent> events = subjectDosingSummary.getEvents();

        softly.assertThat(events).hasSize(3);
        softly.assertThat(events).extracting("start.dayHour").containsOnly(9.0, 11.0, 13.0);
        softly.assertThat(events).extracting("end.dayHour").containsOnly(11.0, 13.0, 15.0);
        softly.assertThat(events).extracting("ongoing").containsOnly(false);
        softly.assertThat(events).extracting("active").containsSequence(true, true, false);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("drug").containsSequence("drug1", "drug1");
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("dose").containsSequence(3.0, 1.0);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("doseUnit").containsSequence("mg", "mg");
    }

    @Test
    public void shouldGetDosingSummaryEventsWhenIntersectingDosesWithTheSameStartDatesForAcuityDatasets() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJECT_1_ACUITY));
        when(drugDoseDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(DD_3_ACUITY, DD_4_ACUITY, DD_5_ACUITY));
        when(doseDiscDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(DOSE_DISC_1));

        List<SubjectDosingSummary> subjectDosingSummaries = drugDoseTimelineService.getDosingSummaries(DATASETS,
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null, MaxDoseType.PER_SUBJECT,
                DrugDoseFilters.empty(), PopulationFilters.empty());
        SubjectDosingSummary subjectDosingSummary = subjectDosingSummaries.get(0);
        List<DosingSummaryEvent> events = subjectDosingSummary.getEvents();

        softly.assertThat(events).hasSize(3);
        softly.assertThat(events).extracting("start.dayHour").containsOnly(9.0, 11.0, 13.0);
        softly.assertThat(events).extracting("end.dayHour").containsOnly(11.0, 13.0, 15.0);
        softly.assertThat(events).extracting("ongoing").containsOnly(false);
        softly.assertThat(events).extracting("active").containsSequence(true, true, true);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("drug").containsSequence("drug1", "drug1", "drug1");
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("dose").containsSequence(3.0, 1.0, 0.5);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("doseUnit").containsSequence("mg", "mg", "mg");
    }

    @Test
    public void shouldGetDosingSummaryByDrugWhenSeveralDiscontinuationDatesForAcuityDatasets() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJECT_1_ACUITY));
        when(drugDoseDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(DD_1_ACUITY, DD_5_ACUITY));
        when(doseDiscDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(DOSE_DISC_1, DOSE_DISC_2, DOSE_DISC_3));

        List<SubjectDrugDosingSummary> subjectDosingSummaries = drugDoseTimelineService.getDosingSummariesByDrug(DATASETS,
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null, MaxDoseType.PER_SUBJECT,
                DrugDoseFilters.empty(), PopulationFilters.empty());
        SubjectDrugDosingSummary subjectDrugDosingSummary = subjectDosingSummaries.get(0);
        List<DosingSummaryEvent> events = subjectDrugDosingSummary.getDrugs().get(0).getEvents();

        softly.assertThat(events).hasSize(3);
        softly.assertThat(events).extracting("start.dayHour").containsOnly(0.0, 8.0, 11.0);
        softly.assertThat(events).extracting("end.dayHour").containsOnly(8.0, 9.0, 15.0);
        softly.assertThat(events).extracting("ongoing").containsOnly(false, false, false);
        softly.assertThat(events).extracting("active").containsSequence(true, false, true);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("drug").containsSequence("drug1", "drug1", "drug1");
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("dose").containsSequence(7.0, 0.0, 0.5);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("doseUnit").containsSequence("mg", null, "mg");
    }

    @Test
    public void shouldGetDosingSummaryEventsWhenHaveInactivePeriodAfterDiscontinuationForAcuityDatasets() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJECT_1_ACUITY));
        when(drugDoseDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(DD_1_ACUITY, DD_4_ACUITY, DD_6_ACUITY));
        when(doseDiscDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(DOSE_DISC_1, DOSE_DISC_3));

        List<SubjectDrugDosingSummary> subjectDosingSummaries = drugDoseTimelineService.getDosingSummariesByDrug(DATASETS,
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null, MaxDoseType.PER_SUBJECT,
                DrugDoseFilters.empty(), PopulationFilters.empty());
        List<DosingSummaryEvent> events = subjectDosingSummaries.get(0).getDrugs().get(0).getEvents();

        softly.assertThat(events).hasSize(4);
        softly.assertThat(events).extracting("start.dayHour").containsOnly(0.0, 8.0, 11.0, 13.0);
        softly.assertThat(events).extracting("end.dayHour").containsOnly(8.0, 10.0, 13.0, 15.0);
        softly.assertThat(events).extracting("ongoing").containsOnly(false, false, false, false);
        softly.assertThat(events).extracting("active").containsSequence(true, false, true, false);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("drug").containsSequence("drug1", "drug1", "drug1");
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("dose").containsSequence(7.0, 0.0, 1.0);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("doseUnit").containsSequence("mg", null, "mg");
    }

    @Test
    public void shouldGetOngoingDosingSummaryEventsAcuityDatasets() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJECT_1_ACUITY));
        when(drugDoseDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(DD_4_ACUITY, DD_6_ACUITY));
        when(doseDiscDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(DOSE_DISC_4));

        List<SubjectDosingSummary> subjectDosingSummaries = drugDoseTimelineService.getDosingSummaries(DATASETS,
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null, MaxDoseType.PER_SUBJECT,
                DrugDoseFilters.empty(), PopulationFilters.empty());
        SubjectDosingSummary subjectDosingSummary = subjectDosingSummaries.get(0);
        List<DosingSummaryEvent> events = subjectDosingSummary.getEvents();

        softly.assertThat(events).hasSize(3);
        softly.assertThat(events).extracting("start.dayHour").containsOnly(11.0, 13.0, 16.0);
        softly.assertThat(events).extracting("end.dayHour").containsOnly(13.0, 16.0, 19.0);
        softly.assertThat(events).extracting("ongoing").containsOnly(false, false, true);
        softly.assertThat(events).extracting("active").containsSequence(true, false, false);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("drug").containsSequence("drug1", "drug1", "drug1", "drug2");
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("dose").containsSequence(1.0, 0.0, 0.0, 0.0);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("doseUnit").containsSequence("mg", null, null, "mg");
    }

    @Test
    public void shouldGetDiscontinuedDosingSummaryEventsOnlyForSelectedDrugsAcuityDatasets() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJECT_1_ACUITY));
        when(drugDoseDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(DD_6_ACUITY));
        when(doseDiscDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(DOSE_DISC_4, DOSE_DISC_5));

        DrugDoseFilters drugDoseFilters = new DrugDoseFilters();
        drugDoseFilters.setStudyDrug(new SetFilter<>(newArrayList("drug2")));
        List<SubjectDosingSummary> subjectDosingSummaries = drugDoseTimelineService.getDosingSummaries(DATASETS,
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null, MaxDoseType.PER_SUBJECT,
                drugDoseFilters, PopulationFilters.empty());
        SubjectDosingSummary subjectDosingSummary = subjectDosingSummaries.get(0);
        List<DosingSummaryEvent> events = subjectDosingSummary.getEvents();

        softly.assertThat(events).hasSize(1);
        softly.assertThat(events).extracting("start.dayHour").containsOnly(16.0);
        softly.assertThat(events).extracting("end.dayHour").containsOnly(18.0);
        softly.assertThat(events).extracting("ongoing").containsOnly(false);
        softly.assertThat(events).extracting("active").containsSequence(false);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("drug").containsSequence("drug2");
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("dose").containsSequence(0.0);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("doseUnit").containsSequence("mg");
    }

    @Test
    public void shouldSkipEventsWithInvalidDates() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJECT_1_ACUITY));
        when(drugDoseDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(DD_1_ACUITY, DD_7_ACUITY));

        List<SubjectDosingSummary> subjectDosingSummaries = drugDoseTimelineService.getDosingSummaries(DATASETS,
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null, MaxDoseType.PER_SUBJECT,
                DrugDoseFilters.empty(), PopulationFilters.empty());
        SubjectDosingSummary subjectDosingSummary = subjectDosingSummaries.get(0);
        List<DosingSummaryEvent> events = subjectDosingSummary.getEvents();

        softly.assertThat(events).hasSize(2);
        softly.assertThat(events).extracting("start.dayHour").containsOnly(0.0, 8.0);
        softly.assertThat(events).extracting("end.dayHour").containsOnly(8.0, 19.0);
        softly.assertThat(events).extracting("ongoing").containsOnly(false, true);
        softly.assertThat(events).extracting("active").containsSequence(true, false);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("drug").containsSequence("drug1");
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("dose").containsSequence(7.0, 0.0);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("doseUnit").containsSequence("mg", null);
    }

    /**
     * Discontinuation date is between active and inactive period,
     * in this situation the end of inactive period should be calculated as the date of dosing event end
     * not the discontinuation date
     */
    @Test
    public void shouldSelectCorrectInactivePeriodEndDate() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJECT_1_ACUITY));
        when(drugDoseDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(DD_1_ACUITY, DD_8_ACUITY));
        when(doseDiscDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(DOSE_DISC_2));

        List<SubjectDrugDosingSummary> subjectDosingSummaries = drugDoseTimelineService.getDosingSummariesByDrug(DATASETS,
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null, MaxDoseType.PER_SUBJECT,
                DrugDoseFilters.empty(), PopulationFilters.empty());
        SubjectDrugDosingSummary subjectDrugDosingSummary = subjectDosingSummaries.get(0);
        List<DosingSummaryEvent> events = subjectDrugDosingSummary.getDrugs().get(0).getEvents();

        softly.assertThat(events).hasSize(2);
        softly.assertThat(events).extracting("start.dayHour").containsOnly(0.0, 8.0);
        softly.assertThat(events).extracting("end.dayHour").containsOnly(8.0, 18.0);
        softly.assertThat(events).extracting("ongoing").containsOnly(false, false);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("drug").containsSequence("drug1", "drug1");
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("dose").containsSequence(7.0, 0.0);
        softly.assertThat(events).flatExtracting("drugDoses")
                .extracting("doseUnit").containsSequence("mg", "mg");
    }
}
