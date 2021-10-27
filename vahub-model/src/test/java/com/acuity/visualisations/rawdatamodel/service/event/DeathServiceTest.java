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

import com.acuity.visualisations.rawdatamodel.dataproviders.DeathDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.DeathRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Death;
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
import java.util.List;
import java.util.Map;

import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class DeathServiceTest {
    @Autowired
    private DeathService deathService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private DeathDatasetsDataProvider deathDatasetsDataProvider;

    private DoDCommonService doDCommonService = new DoDCommonService();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final String SUBJECT_ID = "sid1";
    private static final String STUDY_CODE = "code1";
    private static final String STUDY_PART = "A";

    private static final String DEATH_CAUSE = "cause1";
    private static final String AUTOPSY_PERFORMED = "Yes";
    private static final String DESIGNATION = "designation1";
    private static final String DEATH_RELATED_TO_DISEASE = "Yes";
    private static final String HLT = "hlt";
    private static final String LLT = "llt";
    private static final String PT = "pt";
    private static final String SOC = "soc";
    private static final Integer DAYS_FROM_FIRST_DOSE_TO_DEATH = 9;
    private static final String DEATH_DATE_STRING = "2015-08-10T00:00:00";
    private static final Date DEATH_DATE = DateUtils.toDateTime(DEATH_DATE_STRING);

    private Subject SUBJECT1 = Subject.builder().subjectId(SUBJECT_ID).subjectCode(SUBJECT_ID).clinicalStudyCode(STUDY_CODE).studyPart(STUDY_PART)
            .firstTreatmentDate(DateUtils.toDate("01.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("02.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2016")).build();
    private Death DEATH1 = new Death(DeathRaw.builder().deathCause(DEATH_CAUSE).autopsyPerformed(AUTOPSY_PERFORMED).designation(DESIGNATION)
            .diseaseUnderInvestigationDeath(DEATH_RELATED_TO_DISEASE).hlt(HLT).llt(LLT).preferredTerm(PT).soc(SOC).dateOfDeath(DEATH_DATE).build(), SUBJECT1);

    @Test
    public void shouldGetAcuityDetailsOnDemandColumnsInCorrectOrder() {
        when(deathDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(DEATH1));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT1));

        Map<String, String> columns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, Collections.singletonList(DEATH1));

        softly.assertThat(columns.keySet())
                .containsExactly("studyId", "studyPart", "subjectId", "deathCause", "dateOfDeath", "daysFromFirstDoseToDeath",
                        "autopsyPerformed", "designation", "diseaseUnderInvestigationDeath", "hlt", "llt", "preferredTerm", "soc");

        softly.assertThat(columns.values())
                .containsExactly("Study id", "Study part", "Subject id", "Cause of death", "Date of death", "Days from first dose to death",
                        "Autopsy performed", "Designation", "Death related to disease under investigation", "MedDRA HLT", "MedDRA LLT",
                        "MedDRA PT", "MedDRA SOC");
    }

    @Test
    public void shouldGetAcuityDetailsOnDemandData() {
        when(deathDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(DEATH1));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT1));

        List<Map<String, String>> result = doDCommonService.getColumnData(Column.DatasetType.ACUITY, Collections.singletonList(DEATH1));
        softly.assertThat(result)
                .hasSize(1)
                .extracting("studyId", "studyPart", "subjectId", "deathCause", "dateOfDeath", "daysFromFirstDoseToDeath",
                        "autopsyPerformed", "designation", "diseaseUnderInvestigationDeath", "hlt", "llt", "preferredTerm", "soc")
                .contains(tuple(STUDY_CODE, STUDY_PART, SUBJECT_ID, DEATH_CAUSE, DEATH_DATE_STRING, DAYS_FROM_FIRST_DOSE_TO_DEATH.toString(),
                        AUTOPSY_PERFORMED, DESIGNATION, DEATH_RELATED_TO_DISEASE, HLT, LLT, PT, SOC));
    }
}
