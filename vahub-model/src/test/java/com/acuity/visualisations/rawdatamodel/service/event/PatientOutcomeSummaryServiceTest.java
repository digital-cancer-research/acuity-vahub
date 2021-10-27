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

import com.acuity.visualisations.rawdatamodel.dataproviders.AssessedTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DeathDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DoseDiscDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.SeriousAeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.DeathRaw;
import com.acuity.visualisations.rawdatamodel.vo.DoseDiscRaw;
import com.acuity.visualisations.rawdatamodel.vo.SeriousAeRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Death;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SeriousAe;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PatientOutcomeSummaryServiceTest {

    private static final String[] COLUMN_NAMES = {"date", "event", "outcome", "reason"};
    private static final String[] COLUMN_DESCRIPTIONS = {"Date", "Event", "Outcome", "Reason"};

    private static Subject subject = Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test")
            .firstTreatmentDate(DateUtils.toDate("01.03.2015"))
            .lastTreatmentDate(DateUtils.toDate("01.01.2016")).build();
    private static Subject subject2 = Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test")
            .firstTreatmentDate(DateUtils.toDate("01.03.2014"))
            .lastTreatmentDate(DateUtils.toDate("10.01.2016")).build();

    private static Death deathPrimary = new Death(DeathRaw.builder().id("did1").subjectId("sid1")
            .dateOfDeath(DateUtils.toDate("01.01.2016")).designation("Primary")
            .deathCause("cause1").build(), subject);
    private static Death deathSecondary = new Death(DeathRaw.builder().id("did1").subjectId("sid1")
            .dateOfDeath(DateUtils.toDate("01.01.2016")).designation("Secondary")
            .build(), subject);

    private static SeriousAe sae1 = new SeriousAe(SeriousAeRaw.builder().id("saeid1").ae("Adverse event 1")
            .becomeSeriousDate(DateUtils.toDate("02.10.2015")).build(), subject);
    private static SeriousAe sae2 = new SeriousAe(SeriousAeRaw.builder().id("saeid2").ae("Adverse event 2")
            .build(), subject);
    private static SeriousAe sae3 = new SeriousAe(SeriousAeRaw.builder().id("saeid3").ae("Adverse event 3")
            .becomeSeriousDate(DateUtils.toDate("08.08.2015")).build(), subject);
    private static SeriousAe sae4OtherSubject = new SeriousAe(SeriousAeRaw.builder().id("saeid4").ae("Adverse event 4")
            .becomeSeriousDate(DateUtils.toDate("08.08.2015")).build(), subject2);

    private static DoseDisc doseDisc1 = new DoseDisc(DoseDiscRaw.builder().id("dd1id").studyDrug("DRUG001")
            .discReason("reason1").discDate(DateUtils.toDate("21.11.2015")).build(), subject);
    private static DoseDisc doseDisc2 = new DoseDisc(DoseDiscRaw.builder().id("dd2id").studyDrug("DRUG002")
            .build(), subject);

    private static AssessedTargetLesion tumour1 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("t1id").bestResponse("PD")
            .targetLesionRaw(TargetLesionRaw.builder().bestPercentageChange(false).visitDate(DateUtils.toDate("20.06.2015")).build()).build(), subject);
    private static AssessedTargetLesion tumour2 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("t2id").bestResponse("PR")
            .targetLesionRaw(TargetLesionRaw.builder().bestPercentageChange(true).visitDate(DateUtils.toDate("22.07.2015")).build()).build(), subject);
    private static AssessedTargetLesion tumour3 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("t3id").bestResponse("PR")
            .targetLesionRaw(TargetLesionRaw.builder().bestPercentageChange(true).build())
            .build(), subject);

    static final ArrayList<Subject> SUBJECTS = newArrayList(subject, subject2);
    public static final ArrayList<Death> DEATHS = newArrayList(deathPrimary, deathSecondary);
    public static final ArrayList<DoseDisc> DOSE_DISCS = newArrayList(doseDisc1, doseDisc2);
    public static final ArrayList<AssessedTargetLesion> TUMOURS = newArrayList(tumour1, tumour2);
    public static final ArrayList<SeriousAe> SERIOUS_AES = newArrayList(sae1, sae2, sae3, sae4OtherSubject);

    @Autowired
    private PatientOutcomeSummaryService patientSummaryService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private DeathDatasetsDataProvider deathDatasetsDataProvider;
    @MockBean
    private DoseDiscDatasetsDataProvider doseDiscDatasetsDataProvider;
    @MockBean
    private AssessedTargetLesionDatasetsDataProvider tumourDatasetsDataProvider;
    @MockBean
    private SeriousAeDatasetsDataProvider seriousAeDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testGetSingleSubjectData() {
        ///Given
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);
        when(deathDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(DEATHS);
        when(doseDiscDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(DOSE_DISCS);
        when(tumourDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(TUMOURS);
        when(seriousAeDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SERIOUS_AES);

        //When
        List<Map<String, String>> result = patientSummaryService.getSingleSubjectData(DATASETS, "sid1", true);

        //Then
        Map<String, String> expectedDeath1 = getSummaryRow("2016-01-01", "DEATH", "YES", "Primary cause: cause1");
        Map<String, String> expectedDeath2 = getSummaryRow("", "", "", "Secondary cause: not specified");
        Map<String, String> expectedBestResponse = getSummaryRow("2015-07-22", "BEST RECIST RESPONSE", "PR", "");
        Map<String, String> expectedSae1 = getSummaryRow("2015-08-08", "SERIOUS AE", "Adverse event 3", "", "");
        Map<String, String> expectedSae2 = getSummaryRow("2015-10-02", "SERIOUS AE", "Adverse event 1", "", "");
        Map<String, String> expectedSae3 = getSummaryRow("", "SERIOUS AE", "Adverse event 2", "", "");
        Map<String, String> expectedDisc1 = getSummaryRow("2015-11-21", "DRUG001 DISCONTINUATION", "", "reason1");
        Map<String, String> expectedDisc2 = getSummaryRow("", "DRUG002 DISCONTINUATION", "", "");

        softly.assertThat(result).hasSize(8);
        softly.assertThat(result.get(0)).containsAllEntriesOf(expectedDeath1);
        softly.assertThat(result.get(1)).containsAllEntriesOf(expectedDeath2);
        softly.assertThat(result.get(2)).containsAllEntriesOf(expectedBestResponse);
        softly.assertThat(result.get(3)).containsAllEntriesOf(expectedSae1);
        softly.assertThat(result.get(4)).containsAllEntriesOf(expectedSae2);
        softly.assertThat(result.get(5)).containsAllEntriesOf(expectedSae3);
        softly.assertThat(result.get(6)).containsAllEntriesOf(expectedDisc1);
        softly.assertThat(result.get(7)).containsAllEntriesOf(expectedDisc2);
    }

    @Test
    public void testGetSingleSubjectDataNoDataEmptyVisitDate() {
        ///Given
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);
        when(deathDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(new ArrayList<>());
        when(doseDiscDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(new ArrayList<>());
        when(tumourDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(tumour3));
        when(seriousAeDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(new ArrayList<>());

        //When
        List<Map<String, String>> result = patientSummaryService.getSingleSubjectData(DATASETS, "sid1", true);

        //Then
        Map<String, String> expectedDeath = getSummaryRow("", "DEATH", "NO", "");
        Map<String, String> expectedBestResponse = getSummaryRow("", "BEST RECIST RESPONSE", "PR", "");

        softly.assertThat(result).hasSize(2);
        softly.assertThat(result.get(0)).containsAllEntriesOf(expectedDeath);
        softly.assertThat(result.get(1)).containsAllEntriesOf(expectedBestResponse);
    }

    @Test
    public void testGetSsvColumns() {
        Map<String, String> ssvColumns = patientSummaryService.getSingleSubjectColumns(DatasetType.ACUITY);
        softly.assertThat(ssvColumns.keySet()).contains(COLUMN_NAMES);
        softly.assertThat(ssvColumns.values()).contains(COLUMN_DESCRIPTIONS);
    }

    private Map<String, String> getSummaryRow(String... values) {
        return IntStream.range(0, Integer.min(values.length, COLUMN_NAMES.length)).boxed()
                .collect(Collectors.toMap(i -> COLUMN_NAMES[i], i -> values[i] == null ? "" : values[i]));
    }
}
