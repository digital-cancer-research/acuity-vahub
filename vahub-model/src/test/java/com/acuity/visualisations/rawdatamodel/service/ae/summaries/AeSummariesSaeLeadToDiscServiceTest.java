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

package com.acuity.visualisations.rawdatamodel.service.ae.summaries;

import com.acuity.visualisations.rawdatamodel.dataproviders.AeIncidenceDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.AeSeverityChangeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverity;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSummariesTable;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDateTime;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AeSummariesSaeLeadToDiscServiceTest {


    private static final Subject SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS;

    private static final AeSeverity SEVERITY_1 = AeSeverity.builder().severityNum(1).webappSeverity("CTC Grade 1").build();
    private static final AeSeverity SEVERITY_2 = AeSeverity.builder().severityNum(2).webappSeverity("CTC Grade 2").build();
    private static final AeSeverity SEVERITY_3 = AeSeverity.builder().severityNum(3).webappSeverity("CTC Grade 3").build();
    private static final AeSeverity SEVERITY_5 = AeSeverity.builder().severityNum(5).webappSeverity("CTC Grade 5").build();
    private static final HashMap<String, Date> DRUG_LAST_DOSE_DATE1 = new HashMap<>();
    private static final HashMap<String, String> DRUG_DOSED1 = new HashMap<String, String>() {
        {
            put("drug1", "Yes");
            put("drug2", "Yes");
        }
    };

    private static final Map<String, String> DRUG_CAUSALITY1 = new HashMap<String, String>() {
        {
            put("drug1", "Yes");
        }
    };


    private static final Map<String, String> DRUG_ACTION_TAKEN1 = new HashMap<String, String>() {
        {
            put("drug1", "Drug WITHDRAWN");
        }
    };

    private static final AeRaw RAW_EVENT1 = AeRaw.builder().id("id1")
            .drugsCausality(DRUG_CAUSALITY1)
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDateTime("01.06.2015 03:00:00"))
                                    .endDate(toDateTime("03.08.2015 03:00:00")).build(),
                            AeSeverityRaw.builder().severity(SEVERITY_3).startDate(toDateTime("04.08.2015 03:00:00"))
                                    .endDate(toDateTime("05.08.2015 03:00:00")).build()
                    )
            )
            .serious("no")
            .build();

    private static final AeRaw RAW_EVENT2 = AeRaw.builder().id("id3")
            .drugsCausality(DRUG_CAUSALITY1)
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_5).startDate(toDateTime("04.08.2015 03:00:00"))
                                    .endDate(toDateTime("05.08.2015 03:00:00")).drugsActionTaken(DRUG_ACTION_TAKEN1).build(),
                            AeSeverityRaw.builder().severity(SEVERITY_2).startDate(toDateTime("06.08.2015 03:00:00"))
                                    .endDate(toDateTime("07.08.2015 03:00:00")).build()
                    )
            )
            .serious("1")
            .pt("pt_1")
            .soc("soc_1")
            .subjectId("sid3").build();

    static {
        DRUG_LAST_DOSE_DATE1.put("drug1", toDate("01.09.2015 03:40:04"));

        SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS = Subject.builder().subjectId("sid1").subjectCode("E01").clinicalStudyCode("D0001C00001")
                .studyPart("A")
                .otherCohort("Default group")
                .otherGrouping("Cohort")
                .firstTreatmentDate(toDate("01.08.2015"))
                .drugsDosed(DRUG_DOSED1)
                .build();


    }

    @Autowired
    private AeSummariesSaeLeadToDiscService aeSummariesSaeLeadToDiscService;

    @MockBean(name = "aeIncidenceDatasetsDataProvider")
    private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsDataProvider;
    @MockBean
    private AeSeverityChangeDatasetsDataProvider aeSeverityChangeDatasetsDataProvider;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldHaveOnlyOneRowForAnyAeWhenNoRelatedAe() {

        List<Ae> aes = asList(new Ae(RAW_EVENT1, SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS));
        List<Subject> subjects = asList(SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS);


        mockProviders(aes, subjects);

        List<AeSummariesTable> tables = aeSummariesSaeLeadToDiscService.getAesSummariesTable(DUMMY_ACUITY_DATASETS);

        softly.assertThat(tables).extracting(AeSummariesTable::getRows)
                .containsExactly(
                        asList(AeSummariesTable.AeSummariesRow.builder().rowDescription("").soc("Any AE").pt("Any AE").drug("")
                                .cells(asList(new AeSummariesTable.AeSummariesCell("Default group", "Cohort", 0, 0, AeSummariesTable.GroupingType.NONE, "A")))
                                .build()));
    }

    @Test
    public void shouldHaveSocRows() {

        List<Ae> aes = asList(new Ae(RAW_EVENT2, SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS));
        List<Subject> subjects = asList(SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS);


        mockProviders(aes, subjects);

        List<AeSummariesTable> tables = aeSummariesSaeLeadToDiscService.getAesSummariesTable(DUMMY_ACUITY_DATASETS);

        softly.assertThat(tables.get(0).getRows()).contains(AeSummariesTable.AeSummariesRow.builder().rowDescription("").soc("soc_1").pt("pt_1").drug("drug1")
                .cells(asList(new AeSummariesTable.AeSummariesCell("Default group", "Cohort", 1, 100, AeSummariesTable.GroupingType.NONE, "A")))
                .build());
    }

    private void mockProviders(List<Ae> aes, List<Subject> subjects) {
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(aeSeverityChangeDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjects);
    }
}
