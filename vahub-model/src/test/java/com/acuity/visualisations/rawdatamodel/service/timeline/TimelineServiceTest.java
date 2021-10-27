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
import com.acuity.visualisations.rawdatamodel.axes.TAxes;
import com.acuity.visualisations.rawdatamodel.dataproviders.AeIncidenceDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.AeSeverityChangeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.CardiacDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.ConmedDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DoseDiscDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DrugDoseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.ExacerbationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.LabDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.LungFunctionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PatientDataDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.VitalDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.config.DataProviderConfiguration;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.CardiacFilters;
import com.acuity.visualisations.rawdatamodel.filters.ConmedFilters;
import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.filters.ExacerbationFilters;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.LungFunctionFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.filters.VitalFilters;
import com.acuity.visualisations.rawdatamodel.service.StudyInfoService;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.TimelineTrack;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverity;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.CardiacRaw;
import com.acuity.visualisations.rawdatamodel.vo.ConmedRaw;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.ExacerbationRaw;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.LungFunctionRaw;
import com.acuity.visualisations.rawdatamodel.vo.PatientDataRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.VitalRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Conmed;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Lab;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class, DataProviderConfiguration.class})
public class TimelineServiceTest {
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Autowired
    private TimelineService timelineService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean(name = "aeIncidenceDatasetsDataProvider")
    private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsDataProvider;
    @MockBean
    private AeSeverityChangeDatasetsDataProvider aeSeverityChangeDatasetsDataProvider;
    @MockBean
    private ConmedDatasetsDataProvider conmedDatasetsDataProvider;
    @MockBean
    private DrugDoseDatasetsDataProvider drugDoseDatasetsDataProvider;
    @MockBean
    private CardiacDatasetsDataProvider cardiacDatasetsDataProvider;
    @MockBean
    private LabDatasetsDataProvider labDatasetsDataProvider;
    @MockBean
    private VitalDatasetsDataProvider vitalDatasetsDataProvider;
    @MockBean
    private ExacerbationDatasetsDataProvider exacerbationDatasetsDataProvider;
    @MockBean
    private LungFunctionDatasetsDataProvider lungFunctionDatasetsDataProvider;
    @MockBean
    private PatientDataDatasetsDataProvider patientDataDatasetsDataProvider;
    @MockBean
    private InfoService mockInfoService;
    @MockBean
    private DoseDiscDatasetsDataProvider doseDiscDatasetsDataProvider;
    @MockBean
    private StudyInfoService studyInfoService;

    private static final HashMap<String, Date> DRUG_FIRST_DOSE_DATE = new HashMap<>();
    private static final Subject SUBJECT1;
    private static final Subject SUBJECT2;

    static {
        DRUG_FIRST_DOSE_DATE.put("drug1", toDate("01.09.2015"));
        SUBJECT1 = Subject.builder().subjectId("id1").subjectCode("code1").studyPart("A")
                .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
                .dateOfRandomisation(DateUtils.toDate("02.08.2015"))
                .lastTreatmentDate(DateUtils.toDate("09.08.2016"))
                .durationOnStudy(113)
                .drugFirstDoseDate(DRUG_FIRST_DOSE_DATE).build();
        SUBJECT2 = Subject.builder().subjectId("id2").subjectCode("code2").studyPart("A")
                .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
                .lastTreatmentDate(DateUtils.toDate("09.08.2016"))
                .durationOnStudy(182).build();
    }

    private static final AeSeverity SEVERITY_1 = AeSeverity.builder().severityNum(1).webappSeverity("CTC Grade 1").build();
    private static final Ae AE = new Ae(AeRaw.builder().id("id1").text("text1").pt("pt1").soc("soc1").hlt("hlt1")
            .aeSeverities(Collections.singletonList(AeSeverityRaw.builder().id("1").severity(SEVERITY_1).startDate(new Date())
                    .endDate(new Date()).build())).calcDurationIfNull(true).subjectId("sid1").build(), SUBJECT1);
    private static final Conmed CONMED = new Conmed(ConmedRaw.builder().id("c1").medicationName("drug1").dose(4d)
            .startDate(new Date()).endDate(new Date()).treatmentReason("reason1").doseUnits("mg").doseFrequency("1").atcCode("J05AB").build(), SUBJECT1);
    private static final DrugDose DRUG_DOSE = new DrugDose(DrugDoseRaw.builder().id("ddid1").drug("drug1").dose(1.0).doseUnit("mg")
            .startDate(new Date()).endDate(new Date()).frequencyName("BID").actionTaken("Dose reduction").reasonForActionTaken("reason1").build(), SUBJECT1);
    private static final Cardiac CARDIAC1 = new Cardiac(CardiacRaw.builder().id("c1").measurementName("MN1").measurementTimePoint(new Date())
            .resultValue(3.45).build().runPrecalculations(), SUBJECT1);
    private static final Lab LAB1 = new Lab(LabRaw.builder().id("lid1").labCode("code1").value(7.0).refLow(3.0).refHigh(8.0).unit("%")
            .measurementTimePoint(new Date()).build()
//            .runPrecalculations()
            , SUBJECT1);
    private static final Vital VITAL = new Vital(VitalRaw.builder().id("vital1").visitNumber(1.).resultValue(61.).measurementDate(new Date())
            .build().runPrecalculations(), SUBJECT1);
    private static final Exacerbation EXACERBATION = new Exacerbation(ExacerbationRaw.builder().id("1").exacerbationClassification("classification_1")
            .duration(10).build(), SUBJECT1);
    private static final LungFunction LUNG_FUNCTION = new LungFunction(LungFunctionRaw.builder().visit(2.0).value(1.23)
//            .calcMeasurementName(false) todo check me
            .measurementNameRaw("FEV1P").measurementTimePoint(new Date()).build(), SUBJECT1);

    private static final PatientData PATIENT_DATA = new PatientData(PatientDataRaw.builder().id("1").build(), SUBJECT1);

    private static final Cardiac CARDIAC2 = new Cardiac(CardiacRaw.builder().id("c1").measurementName("MN1")
            .resultValue(3.45).build().runPrecalculations(), SUBJECT2);
    private static final Cardiac CARDIAC3 = new Cardiac(CardiacRaw.builder().id("c1").measurementName("MN2")
            .resultValue(3.45).measurementTimePoint(new Date()).build().runPrecalculations(), SUBJECT2);
    private static final Lab LAB2 = new Lab(LabRaw.builder().id("lid1").labCode("code1").value(7.0).refLow(3.0).refHigh(8.0)
            .unit("%").build(), SUBJECT2);


    @Test
    public void shouldGetAllOptions() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT1));

        List<TAxes<DayZeroType>> result = timelineService.getAvailableOptions(DUMMY_ACUITY_DATASETS);

        softly.assertThat(result).containsExactlyInAnyOrder(
                new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE, null, null),
                new TAxes<>(DayZeroType.DAYS_SINCE_RANDOMISATION, null, null)
        );
    }

    @Test
    public void shouldGetAvailableOptions() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT2));

        List<TAxes<DayZeroType>> result = timelineService.getAvailableOptions(DUMMY_ACUITY_DATASETS);

        softly.assertThat(result).containsOnly(
                new TAxes<>(DayZeroType.DAYS_SINCE_FIRST_DOSE, null, null)
        );
    }

    @Test
    public void shouldGetAvailableTracks() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT1));
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(AE));
        when(conmedDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(CONMED));
        when(drugDoseDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(DRUG_DOSE));
        when(cardiacDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(CARDIAC1));
        when(labDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(LAB1));
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(VITAL));
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(EXACERBATION));
        when(lungFunctionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(LUNG_FUNCTION));
        when(patientDataDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(PATIENT_DATA));

        List<String> availableTracks = timelineService.getAvailableTracks(DUMMY_ACUITY_DATASETS);

        softly.assertThat(availableTracks).containsExactlyInAnyOrder("AES", "CONMEDS", "DOSING", "ECG", "LABS",
                "STATUS_SUMMARY", "VITALS", "EXACERBATIONS", "SPIROMETRY", "PATIENT_DATA");
    }

    @Test
    public void shouldGetNotEmptyTracks() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT1, SUBJECT2));
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(AE));
        when(conmedDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(CONMED));
        when(drugDoseDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(DRUG_DOSE));
        when(cardiacDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(CARDIAC2));
        when(labDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(LAB2));
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(VITAL));
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(EXACERBATION));
        when(lungFunctionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(LUNG_FUNCTION));
        when(patientDataDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(PATIENT_DATA));
        List<String> availableTracks = timelineService.getAvailableTracks(DUMMY_ACUITY_DATASETS);

        softly.assertThat(availableTracks).containsExactlyInAnyOrder("AES", "CONMEDS", "DOSING", "STATUS_SUMMARY",
                "VITALS", "EXACERBATIONS", "SPIROMETRY", "PATIENT_DATA");
    }

    @Test
    public void shouldGetSubjectIds() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT1));
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(AE));
        when(conmedDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(CONMED));
        when(drugDoseDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(DRUG_DOSE));
        when(cardiacDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(CARDIAC1));
        when(labDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(LAB1));
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(VITAL));
        when(exacerbationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(EXACERBATION));
        when(lungFunctionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(LUNG_FUNCTION));
        when(patientDataDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(PATIENT_DATA));

        List<String> result = timelineService.getSubjectsSortedByStudyDuration(DUMMY_ACUITY_DATASETS,
                PopulationFilters.empty(), newArrayList(TimelineTrack.AES, TimelineTrack.LABS), DayZeroType.DAYS_SINCE_FIRST_DOSE,
                null, AeFilters.empty(), ConmedFilters.empty(), DrugDoseFilters.empty(), CardiacFilters.empty(), LabFilters.empty(),
                LungFunctionFilters.empty(), ExacerbationFilters.empty(), VitalFilters.empty());

        softly.assertThat(result).containsOnly(SUBJECT1.getSubjectCode());
    }

    @Test
    public void shouldGetSubjectIdsSortedByStudyDuration() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT1, SUBJECT2));

        List<String> result = timelineService.getSubjectsSortedByStudyDuration(DUMMY_ACUITY_DATASETS,
                PopulationFilters.empty(), newArrayList(TimelineTrack.STATUS_SUMMARY, TimelineTrack.AES, TimelineTrack.LABS),
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null, AeFilters.empty(),
                ConmedFilters.empty(), DrugDoseFilters.empty(), CardiacFilters.empty(), LabFilters.empty(),
                LungFunctionFilters.empty(), ExacerbationFilters.empty(), VitalFilters.empty());

        softly.assertThat(result).hasSize(2);
        softly.assertThat(result).containsExactly(SUBJECT2.getSubjectCode(), SUBJECT1.getSubjectCode());
    }

    @Test
    public void shouldGetSubjectIdsStatusSummaryTrack() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT1, SUBJECT2));

        List<String> result = timelineService.getSubjectsSortedByStudyDuration(DUMMY_ACUITY_DATASETS,
                PopulationFilters.empty(), newArrayList(TimelineTrack.STATUS_SUMMARY, TimelineTrack.AES, TimelineTrack.LABS),
                DayZeroType.DAYS_SINCE_FIRST_DOSE, null, AeFilters.empty(),
                ConmedFilters.empty(), DrugDoseFilters.empty(), CardiacFilters.empty(), LabFilters.empty(),
                LungFunctionFilters.empty(), ExacerbationFilters.empty(), VitalFilters.empty());

        softly.assertThat(result).hasSize(2);
        softly.assertThat(result).containsExactly(SUBJECT2.getSubjectCode(), SUBJECT1.getSubjectCode());
    }

    @Test
    public void shouldGetSubjectIdsSelectedTracks() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT1, SUBJECT2));
        when(vitalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(VITAL));
        when(cardiacDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(CARDIAC2));
        when(labDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(LAB2));

        List<String> result = timelineService.getSubjectsSortedByStudyDuration(DUMMY_ACUITY_DATASETS,
                PopulationFilters.empty(), newArrayList(TimelineTrack.VITALS), DayZeroType.DAYS_SINCE_FIRST_DOSE, null,
                AeFilters.empty(), ConmedFilters.empty(), DrugDoseFilters.empty(), CardiacFilters.empty(), LabFilters.empty(),
                LungFunctionFilters.empty(), ExacerbationFilters.empty(), VitalFilters.empty());

        softly.assertThat(result).containsOnly(SUBJECT1.getSubjectCode());
    }

    @Test
    public void shouldGetSubjectIdsWithPopulationFilters() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT1, SUBJECT2));
        when(cardiacDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(CARDIAC1, CARDIAC2, CARDIAC3));

        PopulationFilters filters = new PopulationFilters();
        filters.setSubjectId(new SetFilter<>(Collections.singletonList(SUBJECT2.getSubjectCode())));

        List<String> result = timelineService.getSubjectsSortedByStudyDuration(DUMMY_ACUITY_DATASETS,
                filters, newArrayList(TimelineTrack.ECG), DayZeroType.DAYS_SINCE_FIRST_DOSE, null,
                AeFilters.empty(), ConmedFilters.empty(), DrugDoseFilters.empty(), CardiacFilters.empty(), LabFilters.empty(),
                LungFunctionFilters.empty(), ExacerbationFilters.empty(), VitalFilters.empty());

        softly.assertThat(result).containsOnly(SUBJECT2.getSubjectCode());
    }

    @Test
    public void shouldGetSubjectIdsWithEventFilters() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT1, SUBJECT2));
        when(cardiacDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(CARDIAC1, CARDIAC2, CARDIAC3));

        CardiacFilters filters = new CardiacFilters();
        filters.setMeasurementName(new SetFilter<>(Collections.singletonList("MN2")));

        List<String> result = timelineService.getSubjectsSortedByStudyDuration(DUMMY_ACUITY_DATASETS,
                PopulationFilters.empty(), newArrayList(TimelineTrack.ECG), DayZeroType.DAYS_SINCE_FIRST_DOSE, null,
                AeFilters.empty(), ConmedFilters.empty(), DrugDoseFilters.empty(), filters, LabFilters.empty(),
                LungFunctionFilters.empty(), ExacerbationFilters.empty(), VitalFilters.empty());

        softly.assertThat(result).containsOnly(SUBJECT2.getSubjectCode());
    }

    @Test
    public void shouldGetSubjectIdsDaysSinceRandomisation() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT1, SUBJECT2));
        when(cardiacDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(CARDIAC1, CARDIAC2, CARDIAC3));

        List<String> result = timelineService.getSubjectsSortedByStudyDuration(DUMMY_ACUITY_DATASETS,
                PopulationFilters.empty(), newArrayList(TimelineTrack.ECG), DayZeroType.DAYS_SINCE_RANDOMISATION, null,
                AeFilters.empty(), ConmedFilters.empty(), DrugDoseFilters.empty(), CardiacFilters.empty(), LabFilters.empty(),
                LungFunctionFilters.empty(), ExacerbationFilters.empty(), VitalFilters.empty());

        softly.assertThat(result).containsOnly(SUBJECT1.getSubjectCode());
    }

    @Test
    public void shouldGetSubjectIdsDaysSinceFirstTreatment() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT1, SUBJECT2));
        when(cardiacDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(CARDIAC1, CARDIAC2, CARDIAC3));

        List<String> result = timelineService.getSubjectsSortedByStudyDuration(DUMMY_ACUITY_DATASETS,
                PopulationFilters.empty(), newArrayList(TimelineTrack.ECG), DayZeroType.DAYS_SINCE_FIRST_TREATMENT, "drug1",
                AeFilters.empty(), ConmedFilters.empty(), DrugDoseFilters.empty(), CardiacFilters.empty(), LabFilters.empty(),
                LungFunctionFilters.empty(), ExacerbationFilters.empty(), VitalFilters.empty());

        softly.assertThat(result).containsOnly(SUBJECT1.getSubjectCode());
    }

    @Test
    public void shouldGetEmptySubjectIds() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(SUBJECT1, SUBJECT2));
        when(cardiacDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(CARDIAC1, CARDIAC2, CARDIAC3));

        List<String> result = timelineService.getSubjectsSortedByStudyDuration(DUMMY_ACUITY_DATASETS,
                PopulationFilters.empty(), newArrayList(TimelineTrack.ECG), DayZeroType.DAYS_SINCE_FIRST_TREATMENT, "drug2",
                AeFilters.empty(), ConmedFilters.empty(), DrugDoseFilters.empty(), CardiacFilters.empty(), LabFilters.empty(),
                LungFunctionFilters.empty(), ExacerbationFilters.empty(), VitalFilters.empty());

        softly.assertThat(result).hasSize(0);
    }
}
