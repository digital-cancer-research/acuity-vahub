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

import com.acuity.visualisations.rawdatamodel.dataproviders.LiverDiagDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.LiverDiagRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverDiag;
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
public class LiverDiagServiceTest {
    private static final String SUBJECT_ID = "sid1";
    private static final String STUDY_CODE = "code1";
    private static final String STUDY_PART = "A";

    private static final String LIVER_DIAG_INV = "Liver biopsy";
    private static final String LIVER_DIAG_INV_SPEC = "Bile Cytology";
    private static final String LIVER_DIAG_INV_DATE_STRING = "2017-03-22T00:00:00";
    private static final Date LIVER_DIAG_INV_DATE = DateUtils.toDateTime(LIVER_DIAG_INV_DATE_STRING);
    private static final Integer STUDY_DAY_LIVER_DIAG_INV = 21;
    private static final String LIVER_DIAG_INV_RESULT = "atypical cells";
    private static final Integer POTENTIAL_HYS_LAW_CASE_NUM = 1;

    @Autowired
    private LiverDiagService liverDiagService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private LiverDiagDatasetsDataProvider liverDiagDatasetsDataProvider;
    @Autowired
    private DoDCommonService doDCommonService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private Subject SUBJECT1 = Subject.builder().subjectId(SUBJECT_ID).subjectCode(SUBJECT_ID).clinicalStudyCode(STUDY_CODE).studyPart(STUDY_PART)
            .firstTreatmentDate(DateUtils.toDate("01.03.2017"))
            .dateOfRandomisation(DateUtils.toDate("02.08.2015"))
            .lastTreatmentDate(DateUtils.toDate("09.08.2016")).build();

    private LiverDiag LIVER_DIAG = new LiverDiag(LiverDiagRaw.builder().liverDiagInv(LIVER_DIAG_INV).liverDiagInvSpec(LIVER_DIAG_INV_SPEC)
            .liverDiagInvDate(LIVER_DIAG_INV_DATE).liverDiagInvResult(LIVER_DIAG_INV_RESULT).potentialHysLawCaseNum(POTENTIAL_HYS_LAW_CASE_NUM)
            .build(), SUBJECT1);

    @Test
    public void shouldGetAcuityDetailsOnDemandColumnsInCorrectOrder() {
        when(liverDiagDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(LIVER_DIAG));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT1));

        Map<String, String> columns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, Collections.singletonList(LIVER_DIAG));

        softly.assertThat(columns.keySet())
                .containsExactly("studyId", "studyPart", "subjectId", "liverDiagInv", "liverDiagInvSpec", "liverDiagInvDate",
                        "studyDayLiverDiagInv", "liverDiagInvResult", "potentialHysLawCaseNum");

        softly.assertThat(columns.values())
                .containsExactly("Study id", "Study part", "Subject id", "Liver diagnostic investigation", "Liver diagnostic investigation specification",
                        "Liver diagnostic investigation date", "Study day at liver diagnostic investigation", "Liver diagnostic investigation results",
                        "Potential Hy's law case number");
    }

    @Test
    public void shouldGetAcuityDetailsOnDemandData() {
        when(liverDiagDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(LIVER_DIAG));
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT1));

        List<Map<String, String>> result = doDCommonService.getColumnData(Column.DatasetType.ACUITY, Collections.singletonList(LIVER_DIAG));
        softly.assertThat(result)
                .hasSize(1)
                .extracting("studyId", "studyPart", "subjectId", "liverDiagInv", "liverDiagInvSpec", "liverDiagInvDate",
                        "studyDayLiverDiagInv", "liverDiagInvResult", "potentialHysLawCaseNum")
                .contains(tuple(STUDY_CODE, STUDY_PART, SUBJECT_ID, LIVER_DIAG_INV, LIVER_DIAG_INV_SPEC, LIVER_DIAG_INV_DATE_STRING,
                        STUDY_DAY_LIVER_DIAG_INV.toString(),
                        LIVER_DIAG_INV_RESULT, POTENTIAL_HYS_LAW_CASE_NUM.toString()));
    }
}
