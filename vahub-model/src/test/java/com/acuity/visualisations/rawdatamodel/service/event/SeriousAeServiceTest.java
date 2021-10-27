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

import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.SeriousAeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.test.annotation.SpringITTest;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.SeriousAeRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SeriousAe;
import com.acuity.va.security.acl.domain.Datasets;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringITTest
public class SeriousAeServiceTest {
    private static final String SUBJECT_ID = "sid1";
    private static final String STUDY_CODE = "code1";
    private static final String STUDY_PART = "A";

    private static final Integer AE_NUMBER = 2;
    private static final String AE = "ae1";
    private static final String PT = "pt1";
    private static final String START_DATE_STRING = "2015-08-03T00:00:00";
    private static final Date START_DATE = DateUtils.toDateTime(START_DATE_STRING);
    private static final String END_DATE_STRING = "2015-08-08T00:00:00";
    private static final Date END_DATE = DateUtils.toDateTime(END_DATE_STRING);
    private static final Integer DAYS_FROM_FIRST_DOSE_TO_CRITERIA = 2;
    private static final String PRIMARY_DEATH_CAUSE = "cause1";
    private static final String SECONDARY_DEATH_CAUSE = "cause2";
    private static final String OTHER_MEDICATION = "Yes";
    private static final String CAUSED_BY_OTHER_MEDICATION = "No";
    private static final String STUDY_PROCEDURE = "Yes";
    private static final String CAUSED_BY_STUDY = "No";
    private static final String DESCRIPTION = "description1";
    private static final String RESULT_IN_DEATH = "No";
    private static final String HOSPITALIZATION_REQUIRED = "No";
    private static final String CONGENITAL_ANOMALY = "No";
    private static final String LIFE_THREATENING = "Yes";
    private static final String DISABILITY = "Yes";
    private static final String OTHER_SERIOUS_EVENT = "No";
    private static final String AD = "ad";
    private static final String CAUSED_BY_AD = "Yes";
    private static final String AD1 = "ad2";
    private static final String CAUSED_BY_AD1 = "No";
    private static final String AD2 = "ad5";
    private static final String CAUSED_BY_AD2 = "No";

    @Autowired
    private SeriousAeService seriousAeService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private SeriousAeDatasetsDataProvider seriousAeDatasetsDataProvider;

    private DoDCommonService doDCommonService = new DoDCommonService();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private Subject SUBJECT1 = Subject.builder().subjectId(SUBJECT_ID).subjectCode(SUBJECT_ID).clinicalStudyCode(STUDY_CODE).studyPart(STUDY_PART)
            .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("02.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2016")).build();

    private SeriousAe SERIOUS_AE = new SeriousAe(SeriousAeRaw.builder().id("id1").num(AE_NUMBER).ae(AE).pt(PT).startDate(START_DATE)
            .endDate(END_DATE).primaryDeathCause(PRIMARY_DEATH_CAUSE).secondaryDeathCause(SECONDARY_DEATH_CAUSE).otherMedication(OTHER_MEDICATION)
            .causedByOtherMedication(CAUSED_BY_OTHER_MEDICATION).studyProcedure(STUDY_PROCEDURE).causedByStudy(CAUSED_BY_STUDY).description(DESCRIPTION)
            .resultInDeath(RESULT_IN_DEATH).hospitalizationRequired(HOSPITALIZATION_REQUIRED).congenitalAnomaly(CONGENITAL_ANOMALY)
            .lifeThreatening(LIFE_THREATENING).disability(DISABILITY).otherSeriousEvent(OTHER_SERIOUS_EVENT).ad(AD).causedByAD(CAUSED_BY_AD).ad1(AD1)
            .causedByAD1(CAUSED_BY_AD1).ad2(AD2).causedByAD2(CAUSED_BY_AD2).becomeSeriousDate(START_DATE).findOutDate(END_DATE)
            .hospitalizationDate(END_DATE).dischargeDate(END_DATE)
            .build(), SUBJECT1);

    private SeriousAe SERIOUS_AE_2 = new SeriousAe(SeriousAeRaw.builder().id("id2").num(AE_NUMBER).ae(AE).pt(PT)
            .endDate(END_DATE).primaryDeathCause(PRIMARY_DEATH_CAUSE).secondaryDeathCause(SECONDARY_DEATH_CAUSE).otherMedication(OTHER_MEDICATION)
            .causedByOtherMedication(CAUSED_BY_OTHER_MEDICATION).studyProcedure(STUDY_PROCEDURE).causedByStudy(CAUSED_BY_STUDY).description(DESCRIPTION)
            .resultInDeath(RESULT_IN_DEATH).hospitalizationRequired(HOSPITALIZATION_REQUIRED).congenitalAnomaly(CONGENITAL_ANOMALY)
            .lifeThreatening(LIFE_THREATENING).disability(DISABILITY).otherSeriousEvent(OTHER_SERIOUS_EVENT).ad(AD).causedByAD(CAUSED_BY_AD).ad1(AD1)
            .causedByAD1(CAUSED_BY_AD1).ad2(AD2).causedByAD2(CAUSED_BY_AD2).becomeSeriousDate(START_DATE).findOutDate(END_DATE)
            .hospitalizationDate(END_DATE).dischargeDate(END_DATE)
            .build(), SUBJECT1);

    private SeriousAe SERIOUS_AE_3 = new SeriousAe(SeriousAeRaw.builder().id("id3").pt("Asthma")
            .endDate(END_DATE).build(), SUBJECT1);

    private SeriousAe SERIOUS_AE_4 = new SeriousAe(SeriousAeRaw.builder().id("id3").pt("asthma")
            .startDate(START_DATE).endDate(END_DATE).build(), SUBJECT1);

    @Test
    public void shouldGetAcuityDetailsOnDemandColumnsInCorrectOrder() {
        when(seriousAeDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SERIOUS_AE));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT1));

        Map<String, String> columns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, Collections.singletonList(SERIOUS_AE));

        softly.assertThat(columns.keySet())
                .containsExactly("studyId", "studyPart", "subjectId", "num", "ae", "startDate",
                        "endDate", "resultInDeath", "hospitalizationRequired", "congenitalAnomaly", "lifeThreatening",
                        "disability", "otherSeriousEvent", "hospitalizationDate", "dischargeDate", "pt", "becomeSeriousDate",
                        "daysFromFirstDoseToCriteria", "findOutDate", "description", "primaryDeathCause", "secondaryDeathCause",
                        "ad", "causedByAD", "ad1", "causedByAD1", "ad2", "causedByAD2", "otherMedication", "causedByOtherMedication",
                        "studyProcedure", "causedByStudy");

        softly.assertThat(columns.values())
                .containsExactly("Study id", "Study part", "Subject id", "AE Number", "Adverse event", "AE start date", "AE end date",
                        "Results in death", "Requires or prolongs hospitalization", "Congenital anomaly or birth defect",
                        "Life threatening", "Persist. or sign. disability/incapacity", "Other medically important serious event",
                        "Date of hospitalization", "Date of discharge", "Preferred term", "Date AE met criteria for serious AE",
                        "Days from first dose to AE met criteria", "Date investigator aware of serious AE", "AE description",
                        "Primary cause of death", "Secondary cause of death", "Additional Drug", "AE Caused by Additional Drug",
                        "Additional Drug 1", "AE Caused by Additional Drug 1", "Additional Drug 2", "AE Caused by Additional Drug 2",
                        "Other medication", "AE caused by other medication", "Study procedure(s)", "AE caused by study procedure(s)");
    }

    @Test
    public void shouldGetAcuityDetailsOnDemandData() {
        when(seriousAeDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SERIOUS_AE));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT1));

        List<Map<String, String>> result = doDCommonService.getColumnData(Column.DatasetType.ACUITY, Collections.singletonList(SERIOUS_AE));
        softly.assertThat(result)
                .hasSize(1)
                .extracting("studyId", "studyPart", "subjectId", "num", "ae", "startDate",
                        "endDate", "resultInDeath", "hospitalizationRequired", "congenitalAnomaly", "lifeThreatening",
                        "disability", "otherSeriousEvent", "hospitalizationDate", "dischargeDate", "pt", "becomeSeriousDate",
                        "daysFromFirstDoseToCriteria", "findOutDate", "description", "primaryDeathCause", "secondaryDeathCause",
                        "ad", "causedByAD", "ad1", "causedByAD1", "ad2", "causedByAD2", "otherMedication", "causedByOtherMedication",
                        "studyProcedure", "causedByStudy")
                .contains(tuple(STUDY_CODE, STUDY_PART, SUBJECT_ID, AE_NUMBER.toString(), AE, START_DATE_STRING, END_DATE_STRING, RESULT_IN_DEATH,
                        HOSPITALIZATION_REQUIRED, CONGENITAL_ANOMALY, LIFE_THREATENING, DISABILITY, OTHER_SERIOUS_EVENT, END_DATE_STRING,
                        END_DATE_STRING, PT, START_DATE_STRING, DAYS_FROM_FIRST_DOSE_TO_CRITERIA.toString(), END_DATE_STRING, DESCRIPTION, PRIMARY_DEATH_CAUSE,
                        SECONDARY_DEATH_CAUSE, AD, CAUSED_BY_AD, AD1, CAUSED_BY_AD1, AD2, CAUSED_BY_AD2, OTHER_MEDICATION,
                        CAUSED_BY_OTHER_MEDICATION, STUDY_PROCEDURE, CAUSED_BY_STUDY));
    }

}
