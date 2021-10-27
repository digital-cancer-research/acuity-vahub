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

import com.acuity.visualisations.rawdatamodel.dataproviders.LiverRiskDatasetDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.LiverRiskFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.LiverRiskRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LiverRisk;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.groups.Tuple;
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
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_LIVER_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class LiverRiskServiceTest {

    @Autowired
    private LiverRiskService liverRiskService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private LiverRiskDatasetDataProvider liverRiskDatasetDataProvider;

    private DoDCommonService doDCommonService = new DoDCommonService();

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static final Subject SUBJ_1 = Subject.builder()
            .clinicalStudyCode("code1")
            .studyPart("part1")
            .subjectId("subj-1")
            .subjectCode("subj-1")
            .dateOfRandomisation(toDate("01.01.2000"))
            .firstTreatmentDate(toDate("01.01.2000"))
            .build();

    private static final LiverRisk LIVER_RISK_1 = new LiverRisk(
            LiverRiskRaw.builder()
                    .id("id1")
                    .subjectId("subj-1")
                    .value("value_1")
                    .comment("comment_1")
                    .details("details_1")
                    .referencePeriod("preference_period_1")
                    .occurrence("occurrence_1")
                    .startDate(toDate("05.01.2000"))
                    .stopDate(toDate("10.01.2000"))
                    .potentialHysLawCaseNum(1)
                    .build(), SUBJ_1);

    private static final LiverRisk LIVER_RISK_2 = new LiverRisk(
            LiverRiskRaw.builder()
                    .id("id2")
                    .subjectId("subj-1")
                    .value("value_2")
                    .comment("comment_2")
                    .details("details_2")
                    .referencePeriod("preference_period_2")
                    .occurrence("occurrence_2")
                    .startDate(toDate("17.01.2000"))
                    .stopDate(toDate("20.01.2000"))
                    .potentialHysLawCaseNum(10)
                    .build(), SUBJ_1);

    private static final LiverRisk LIVER_RISK_3 = new LiverRisk(
            LiverRiskRaw.builder()
                    .id("id3")
                    .subjectId("subj-1")
                    .comment("comment_3")
                    .build(), SUBJ_1);

    private static final List<LiverRisk> LIVER_RISK_LIST = Arrays.asList(LIVER_RISK_1, LIVER_RISK_2, LIVER_RISK_3);

    @Test
    public void shouldGetAllDetailsOnDemandColumns() {
        // When
        Map<String, String> columns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, LIVER_RISK_LIST);

        // Then
        softly.assertThat(columns.keySet())
                .containsExactly("studyId", "studyPart", "subjectId", "potentialHysLawCaseNum", "value",
                        "occurrence", "referencePeriod", "details", "startDate", "stopDate",
                        "studyDayAtStart", "studyDayAtStop", "comment");
        softly.assertThat(columns.values())
                .containsExactly("Study id", "Study part", "Subject id", "Potential Hy's law case number",
                        "Liver risk factor", "Liver risk factor occurrence", "Liver risk factor reference period",
                        "Liver risk factor details", "Start date", "Stop date", "Study day at liver risk factor start",
                        "Study day at liver risk factor stop", "Liver risk factor comment");
    }

    @Test
    public void shouldGetDetailsOnDemandData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(SUBJ_1));
        when(liverRiskDatasetDataProvider.loadData(any(Datasets.class))).thenReturn(LIVER_RISK_LIST);
        List<Map<String, String>> result = liverRiskService
                .getDetailsOnDemandData(DUMMY_LIVER_DATASETS, SUBJ_1.getSubjectId(), LiverRiskFilters.empty());

        softly.assertThat(result).hasSize(3);
        softly.assertThat(result).flatExtracting(Map::keySet).containsOnly(
                "studyId", "studyPart", "subjectId", "eventId", "potentialHysLawCaseNum", "value",
                "occurrence", "referencePeriod", "details", "startDate", "stopDate",
                "studyDayAtStart", "studyDayAtStop", "comment"
        );
        softly.assertThat(result)
                .extracting(e -> e.get("studyId"), e -> e.get("studyPart"), e -> e.get("subjectId"), e -> e.get("eventId"),
                        e -> e.get("potentialHysLawCaseNum"), e -> e.get("value"), e -> e.get("occurrence"), e -> e.get("referencePeriod"),
                        e -> e.get("details"), e -> e.get("startDate"), e -> e.get("stopDate"), e -> e.get("studyDayAtStart"),
                        e -> e.get("studyDayAtStop"), e -> e.get("comment"))
                .contains(
                        Tuple.tuple("code1", "part1", "subj-1", "id1", "1", "value_1", "occurrence_1", "preference_period_1", "details_1",
                                "2000-01-05T00:00:00", "2000-01-10T00:00:00", "4", "9", "comment_1"),
                        Tuple.tuple("code1", "part1", "subj-1", "id2", "10", "value_2", "occurrence_2", "preference_period_2", "details_2",
                                "2000-01-17T00:00:00", "2000-01-20T00:00:00", "16", "19", "comment_2"),
                        Tuple.tuple("code1", "part1", "subj-1", "id3", null, null, null, null, null, null, null, null, null, "comment_3")
                );
    }

    @Test
    public void shouldGetAvailableDetailsOnDemandColumns() {
        // When
        Map<String, String> columns = doDCommonService.getDoDColumns(Column.DatasetType.ACUITY, Collections.singleton(LIVER_RISK_3));

        // Then
        softly.assertThat(columns.keySet())
                .containsExactly("studyId", "studyPart", "subjectId", "comment");
        softly.assertThat(columns.values())
                .containsExactly("Study id", "Study part", "Subject id", "Liver risk factor comment");
    }
}
