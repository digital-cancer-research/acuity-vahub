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

package com.acuity.visualisations.rawdatamodel.service.ssv;


import com.acuity.visualisations.rawdatamodel.dataproviders.AeSeverityChangeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataset.info.InfoService;
import com.acuity.visualisations.rawdatamodel.dataproviders.AeIncidenceDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.AssessedNonTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.AssessedTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.AssessmentDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.ChemotherapyDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.ConmedDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DeathDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DiseaseExtentDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DoseDiscDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DrugDoseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.LabDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.MedicalHistoryDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.NonTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PathologyDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.RadiotherapyDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.SecondTimeOfProgressionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.SeriousAeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.SurgicalHistoryDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.SurvivalStatusDatasesDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.TargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.service.event.AssessmentServiceTest.ASSESSMENTS;
import static com.acuity.visualisations.rawdatamodel.service.event.ChemotherapyServiceTest.CHEMOTHERAPIES;
import static com.acuity.visualisations.rawdatamodel.service.event.DiseaseExtentServiceTest.DISEASE_EXTENTS;
import static com.acuity.visualisations.rawdatamodel.service.event.MedicalHistoryServiceTest.MEDICAL_HISTORIES;
import static com.acuity.visualisations.rawdatamodel.service.event.NonTargetLesionServiceTest.NON_TARGET_LESIONS;
import static com.acuity.visualisations.rawdatamodel.service.event.PathologyServiceTest.PATHOLOGIES;
import static com.acuity.visualisations.rawdatamodel.service.event.PatientOutcomeSummaryServiceTest.DEATHS;
import static com.acuity.visualisations.rawdatamodel.service.event.PatientOutcomeSummaryServiceTest.DOSE_DISCS;
import static com.acuity.visualisations.rawdatamodel.service.event.PatientOutcomeSummaryServiceTest.SERIOUS_AES;
import static com.acuity.visualisations.rawdatamodel.service.event.PatientOutcomeSummaryServiceTest.TUMOURS;
import static com.acuity.visualisations.rawdatamodel.service.PopulationServiceTest.SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.service.event.RadiotherapyServiceTest.RADIOTHERAPIES;
import static com.acuity.visualisations.rawdatamodel.service.event.SecondTimeOfProgressionServiceTest.SECOND_TIME_OF_PROGRESSION_EVENTS;
import static com.acuity.visualisations.rawdatamodel.service.event.SurvivalStatusServiceTest.SURVIVAL_STATUSES;
import static com.acuity.visualisations.rawdatamodel.service.event.TargetLesionServiceTest.TARGET_LESIONS;
import static com.acuity.visualisations.rawdatamodel.service.ssv.SingleSubjectViewSummaryService.SsvTableMetadata;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.stream.Collectors.toList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class SingleSubjectViewSummaryServiceTest {

    @Autowired
    private SingleSubjectViewSummaryService ssvSummaryService;

    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private DeathDatasetsDataProvider deathDatasetsDataProvider;
    @MockBean
    private DoseDiscDatasetsDataProvider doseDiscDatasetsDataProvider;
    @MockBean
    private DrugDoseDatasetsDataProvider drugDoseDatasetsDataProvider;
    @MockBean
    private SeriousAeDatasetsDataProvider seriousAeDatasetsDataProvider;
    @MockBean
    private MedicalHistoryDatasetsDataProvider medicalHistoryDatasetsDataProvider;
    @MockBean
    private SurgicalHistoryDatasetsDataProvider surgicalHistoryDatasetsDataProvider;
    @MockBean
    private ConmedDatasetsDataProvider conmedDatasetsDataProvider;
    @MockBean
    private PathologyDatasetsDataProvider pathologyDatasetsDataProvider;
    @MockBean
    private DiseaseExtentDatasetsDataProvider disextDatasetsDataProvider;
    @MockBean
    private LabDatasetsDataProvider labDatasetsDataProvider;
    @MockBean
    private RadiotherapyDatasetsDataProvider radiotherapyDatasetsDataProvider;
    @MockBean
    private ChemotherapyDatasetsDataProvider chemotherapyDatasetsDataProvider;
    @MockBean
    private AssessedNonTargetLesionDatasetsDataProvider assessedNonTargetLesionDatasetsDataProvider;
    @MockBean
    private AssessedTargetLesionDatasetsDataProvider assessedTargetLesionDatasetsDataProvider;
    @MockBean
    private TargetLesionDatasetsDataProvider targetLesionDatasetsDataProvider;
    @MockBean
    private NonTargetLesionDatasetsDataProvider nonTargetLesionDatasetsDataProvider;
    @MockBean
    private AssessmentDatasetsDataProvider assessmentDatasetsDataProvider;
    @MockBean(name = "aeIncidenceDatasetsDataProvider")
    private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsDataProvider;
    @MockBean
    private SecondTimeOfProgressionDatasetsDataProvider secondTimeOfProgressionDatasetsDataProvider;
    @MockBean
    private SurvivalStatusDatasesDataProvider survivalStatusDatasesDataProvider;
    @MockBean
    private InfoService mockInfoService;
    @MockBean
    private AeSeverityChangeDatasetsDataProvider aeSeverityChangeDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void initMocks() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);
        when(deathDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(DEATHS);
        when(doseDiscDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(DOSE_DISCS);
        when(seriousAeDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SERIOUS_AES);
        when(medicalHistoryDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(MEDICAL_HISTORIES);
        when(nonTargetLesionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(NON_TARGET_LESIONS);
        when(assessmentDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(ASSESSMENTS);
        when(nonTargetLesionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(NON_TARGET_LESIONS);
        when(assessedTargetLesionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(TUMOURS);
        when(targetLesionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(TARGET_LESIONS);
        when(pathologyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(PATHOLOGIES);
        when(disextDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(DISEASE_EXTENTS);
        when(chemotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(CHEMOTHERAPIES);
        when(radiotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(RADIOTHERAPIES);
        when(secondTimeOfProgressionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SECOND_TIME_OF_PROGRESSION_EVENTS);
        when(survivalStatusDatasesDataProvider.loadData(any(Datasets.class))).thenReturn(SURVIVAL_STATUSES);
    }

    private static List<SsvTableMetadata> expectedMetadata = newArrayList(
            SsvTableMetadata.builder().name("demography").displayName("DEMOGRAPHY").headerName("DEMOGRAPHY").subheaderName("DEMOGRAPHY")
                    .order(1.0).build(),
            SsvTableMetadata.builder().name("outcomeSummary").displayName("PATIENT OUTCOME SUMMARY").headerName("PATIENT OUTCOME SUMMARY")
                    .subheaderName("PATIENT OUTCOME SUMMARY").order(2.0).build(),
            SsvTableMetadata.builder().name("pastMedicalHistory").displayName("PAST MEDICAL HISTORY").headerName("PATIENT HISTORY")
                    .subheaderName("PAST MEDICAL HISTORY").order(3.0).build(),
            SsvTableMetadata.builder().name("surgicalHistory").displayName("SURGICAL HISTORY").headerName("PATIENT HISTORY")
                    .subheaderName("SURGICAL HISTORY").order(4.0).build(),
            SsvTableMetadata.builder().name("currentMedicalHistory").displayName("CONCURRENT CONDITIONS AT STUDY ENTRY")
                    .headerName("PATIENT HISTORY").subheaderName("CONCURRENT CONDITIONS AT STUDY ENTRY").order(5.0).build(),
            SsvTableMetadata.builder().name("conmeds").displayName("CONMEDS").headerName("PATIENT HISTORY").subheaderName("CONMEDS")
                    .order(6.0).build(),
            SsvTableMetadata.builder().name("pathgen").displayName("PATHGEN").headerName("PATIENT HISTORY").subheaderName("PATHGEN")
                    .order(7.0).hasTumourAccess(true).build(),
            SsvTableMetadata.builder().name("disExt").displayName("EXTENT OF DISEASE AT STUDY ENTRY (DISEXT)").headerName("PATIENT HISTORY")
                    .subheaderName("EXTENT OF DISEASE AT STUDY ENTRY (DISEXT)").hasTumourAccess(true).order(8.0).build(),
            SsvTableMetadata.builder().name("pastChemotherapy").displayName("PRIOR ANTI-CANCER THERAPY (CAPRX)").headerName("PATIENT HISTORY")
                    .subheaderName("PRIOR THERAPIES").order(9.0).hasTumourAccess(true).build(),
            SsvTableMetadata.builder().name("radiotherapy").displayName("PRIOR ANTI-CANCER RADIOTHERAPY (CAPRX R)").headerName("PATIENT HISTORY")
                    .subheaderName("PRIOR THERAPIES").order(10.0).hasTumourAccess(true).build(),
            SsvTableMetadata.builder().name("labs").displayName("OUT OF RANGE LAB DATA OF CLINICAL SIGNIFICANCE").headerName("PATIENT HISTORY")
                    .subheaderName("OUT OF RANGE LAB DATA OF CLINICAL SIGNIFICANCE").order(11.0).build(),
            SsvTableMetadata.builder().name("drugDose").displayName("STUDY DRUG ADMINISTRATION").headerName("STUDY DRUG")
                    .subheaderName("STUDY DRUG ADMINISTRATION").order(12.0).build(),
            SsvTableMetadata.builder().name("doseDisc").displayName("DISCONTINUATION OF DRUG").headerName("STUDY DRUG")
                    .subheaderName("DISCONTINUATION OF DRUG").order(13.0).build(),
            SsvTableMetadata.builder().name("doseLimiting").displayName("DOSE LIMITING TOXICITIES (DLT)").headerName("STUDY DRUG")
                    .subheaderName("DOSE LIMITING TOXICITIES (DLT)").order(14.0).build(),
            SsvTableMetadata.builder().name("adverseEvents").displayName("ADVERSE EVENTS").headerName("ADVERSE EVENTS")
                    .subheaderName("ADVERSE EVENTS").order(15.0).build(),
            SsvTableMetadata.builder().name("targetlesion").displayName("TARGET LESION").headerName("RECIST RESPONSE").subheaderName("TARGET LESION")
                    .order(16.0).hasTumourAccess(true).build(),
            SsvTableMetadata.builder().name("nontargetLesion").displayName("NON-TARGET LESION").headerName("RECIST RESPONSE")
                    .subheaderName("NON-TARGET LESION").order(17.0).hasTumourAccess(true).build(),
            SsvTableMetadata.builder().name("newLesion").displayName("NEW LESIONS").headerName("RECIST RESPONSE")
                    .subheaderName("NEW LESIONS").order(18.0).hasTumourAccess(true).build(),
            SsvTableMetadata.builder().name("postChemotherapy").displayName("POST ANTI-CANCER THERAPY (CAPRX)").headerName("FOLLOW UP")
                    .subheaderName("POST STUDY THERAPIES").order(19.0).hasTumourAccess(true).build(),
            SsvTableMetadata.builder().name("secondTimeOfProgression").displayName("SECOND TIME OF PROGRESSION").headerName("FOLLOW UP")
                    .subheaderName("POST IP FOLLOW UP (PFS2 & SURVIVAL)").order(20.0).hasTumourAccess(true).build(),
            SsvTableMetadata.builder().name("survivalStatus").displayName("SURVIVAL STATUS").headerName("FOLLOW UP")
                    .subheaderName("POST IP FOLLOW UP (PFS2 & SURVIVAL)").order(21.0).hasTumourAccess(true).build()
    );

    @Test
    public void testGetMetadata() {
        List<SsvTableMetadata> metadata = ssvSummaryService.getMetadata(DATASETS, true);
        softly.assertThat(metadata).hasSize(21);
        softly.assertThat(metadata).extracting("name").containsAll(expectedMetadata.stream().map(SsvTableMetadata::getName).collect(toList()));
        softly.assertThat(metadata).extracting("displayName")
                .containsAll(expectedMetadata.stream().map(SsvTableMetadata::getDisplayName).collect(toList()));
        softly.assertThat(metadata).extracting("order")
                .containsAll(expectedMetadata.stream().map(SsvTableMetadata::getOrder).collect(toList()));
        softly.assertThat(metadata).extracting("headerName")
                .containsAll(expectedMetadata.stream().map(SsvTableMetadata::getHeaderName).collect(toList()));
        softly.assertThat(metadata).extracting("subheaderName")
                .containsAll(expectedMetadata.stream().map(SsvTableMetadata::getSubheaderName).collect(toList()));
        softly.assertThat(metadata).extracting("hasTumourAccess")
                .containsExactlyElementsOf(expectedMetadata.stream().map(SsvTableMetadata::getHasTumourAccess).collect(toList()));

    }

    @Test
    public void testGetDataWithTumourAccess() {

        Map<String, List<Map<String, String>>> data = ssvSummaryService.getData(DATASETS, "sid1", true);
        softly.assertThat(data).hasSize(21);
        softly.assertThat(data.get("nontargetLesion")).hasSize(2);
        softly.assertThat(data.get("targetlesion")).hasSize(6);
        softly.assertThat(data.get("newLesion")).hasSize(7);
        softly.assertThat(data.get("outcomeSummary")).hasSize(8);
        softly.assertThat(data.get("pathgen")).hasSize(1);
        softly.assertThat(data.get("disExt")).hasSize(2);
        softly.assertThat(data.get("pastChemotherapy")).hasSize(2);
        softly.assertThat(data.get("radiotherapy")).hasSize(4);
        softly.assertThat(data.get("postChemotherapy")).hasSize(2);
        softly.assertThat(data.get("secondTimeOfProgression")).hasSize(1);
        softly.assertThat(data.get("survivalStatus")).hasSize(1);
    }

    @Test
    public void testGetDataWithoutTumourAccess() {

        Map<String, List<Map<String, String>>> data = ssvSummaryService.getData(DATASETS, "sid1", false);
        softly.assertThat(data).hasSize(21);
        softly.assertThat(data.get("nontargetLesion")).hasSize(0);
        softly.assertThat(data.get("targetlesion")).hasSize(0);
        softly.assertThat(data.get("newLesion")).hasSize(0);
        softly.assertThat(data.get("outcomeSummary")).hasSize(7);
        softly.assertThat(data.get("pathgen")).hasSize(0);
        softly.assertThat(data.get("disExt")).hasSize(0);
        softly.assertThat(data.get("pastChemotherapy")).hasSize(0);
        softly.assertThat(data.get("radiotherapy")).hasSize(0);
        softly.assertThat(data.get("postChemotherapy")).hasSize(0);
        softly.assertThat(data.get("secondTimeOfProgression")).hasSize(0);
        softly.assertThat(data.get("survivalStatus")).hasSize(0);
    }

    @Test
    public void testGetHeaderData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);

        List<Map<String, String>> headerData = ssvSummaryService.getHeaderData(DATASETS, "sid1");

        softly.assertThat(headerData).hasSize(10);
        softly.assertThat(headerData).extracting("name").containsExactly("patientId", "studyDrug", "startDate",
                "deathDate", "studyId", "withdrawalDate", "studyName", "withdrawalReason", "studyPart", "datasetName");
        softly.assertThat(headerData).extracting("displayName").containsExactly("PATIENT ID", "STUDY DRUG", "FIRST TREATMENT DAY",
                "DEATH DATE", "STUDY ID", "WITHDRAWAL / COMPLETION DATE", "STUDY NAME", "REASON FOR WITHDRAWAL / COMPLETION", "STUDY PART", "DATASET");

        softly.assertThat(headerData).extracting("value").containsExactly("E01", "drug1", "2015-02-20",
                "2017-06-01", "100B", "2016-06-01", "Study name", "some reason", "A", "dataset1");
    }

    @Test
    public void testGetHeaderDataWhenNoWithdrawal() {

        ArrayList<Subject> subjects = newArrayList(Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
                .datasetName("dataset1").sex("Male")
                .height(188.).weight(70.).race("Asian").age(55)
                .studyPart("A").clinicalStudyName("Study name").clinicalStudyCode("100B").firstTreatmentDate(DateUtils.toDate("20.02.2015"))
                .drugsDosed(Collections.singletonMap("drug1", "Yes"))
                .reasonForWithdrawal("No Withdrawal/Completion")
                .withdrawal("No")
                .build());

        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjects);

        List<Map<String, String>> headerData = ssvSummaryService.getHeaderData(DATASETS, "sid1");

        softly.assertThat(headerData).hasSize(9);
        softly.assertThat(headerData).extracting("name").containsExactly("patientId", "studyDrug", "startDate",
                "deathDate", "studyId", "withdrawalReason", "studyName",  "studyPart", "datasetName");
        softly.assertThat(headerData).extracting("displayName").containsExactly("PATIENT ID", "STUDY DRUG", "FIRST TREATMENT DAY",
                "DEATH DATE", "STUDY ID", "REASON FOR WITHDRAWAL / COMPLETION", "STUDY NAME", "STUDY PART", "DATASET");

        softly.assertThat(headerData).extracting("value").containsExactly("E01", "drug1", "2015-02-20",
                "N/A", "100B", "No Withdrawal/Completion", "Study name", "A", "dataset1");

    }
}
