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
import com.acuity.visualisations.rawdatamodel.vo.AeSummariesTable.AeSummariesCell;
import com.acuity.visualisations.rawdatamodel.vo.AeSummariesTable.AeSummariesRow;
import com.acuity.visualisations.rawdatamodel.vo.AeSummariesTable.GroupingType;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectAwareWrapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDateTime;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AeSummariesServiceTest {
    private static final Subject SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS;
    private static final Subject SUBJECT2_WITH_DEFAULT_GROUPINGS_AND_GROUPS;
    private static final Subject SUBJECT3_WITH_DEFAULT_GROUPINGS_AND_GROUPS;
    private static final Subject SUBJECT4_WITH_DEFAULT_GROUPINGS_AND_GROUPS;
    private static final Subject SUBJECT5_WITH_DEFAULT_GROUPINGS_AND_GROUPS;
    private static final Subject SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS;

    private static final Subject SUBJECT1_WITH_DOSE_GROUPINGS_AND_DOSE_GROUPS_ONLY;
    private static final Subject SUBJECT2_WITH_DOSE_GROUPINGS_AND_DOSE_GROUPS_ONLY;
    private static final Subject SUBJECT3_WITH_DOSE_GROUPINGS_AND_DOSE_GROUPS_ONLY;
    private static final Subject SUBJECT4_WITH_DOSE_GROUPINGS_AND_DOSE_GROUPS_ONLY;

    private static final Subject SUBJECT1_WITH_GROUPINGS_AND_GROUPS;
    private static final Subject SUBJECT2_WITH_GROUPINGS_AND_GROUPS;
    private static final Subject SUBJECT3_WITH_GROUPINGS_AND_GROUPS;
    private static final Subject SUBJECT4_WITH_GROUPINGS_AND_GROUPS;

    private static final AeSeverity SEVERITY_1 = AeSeverity.builder().severityNum(1).webappSeverity("CTC Grade 1").build();
    private static final AeSeverity SEVERITY_2 = AeSeverity.builder().severityNum(2).webappSeverity("CTC Grade 2").build();
    private static final AeSeverity SEVERITY_3 = AeSeverity.builder().severityNum(3).webappSeverity("CTC Grade 3").build();
    private static final AeSeverity SEVERITY_5 = AeSeverity.builder().severityNum(5).webappSeverity("CTC Grade 5").build();
    private static final HashMap<String, String> DRUG_DOSED1 = new HashMap<String, String>() {
        {
            put("drug1", "Yes");
            put("drug2", "Yes");
        }
    };
    private static final HashMap<String, String> DRUG_DOSED2 = new HashMap<String, String>() {
        {
            put("drug1", "Yes");
        }
    };

    private static final Map<String, String> DRUG_CAUSALITY1 = new HashMap<String, String>() {
        {
            put("drug1", "Yes");
        }
    };
    private static final Map<String, String> DRUG_CAUSALITY2 = new HashMap<String, String>() {
        {
            put("drug2", "POSSIBLY RELATED");
        }
    };
    private static final Map<String, String> DRUG_CAUSALITY3 = new HashMap<String, String>() {
        {
            put("drug2", "1");
        }
    };
    private static final Map<String, String> DRUG_CAUSALITY4 = new HashMap<String, String>() {
        {
            put("drug2", "Possible");
        }
    };
    private static final Map<String, String> DRUG_CAUSALITY5 = new HashMap<String, String>() {
        {
            put("drug2", "Related");
        }
    };
    private static final Map<String, String> DRUG_CAUSALITY6 = new HashMap<String, String>() {
        {
            put("drug2", "Relate");
        }
    };
    private static final Map<String, String> DRUG_ACTION_TAKEN1 = new HashMap<String, String>() {
        {
            put("drug1", "Drug WITHDRAWN");
        }
    };
    private static final Map<String, String> DRUG_ACTION_TAKEN2 = new HashMap<String, String>() {
        {
            put("drug2", "Drug PERMANENTLY DISCONTINUED");
        }
    };
    private static final Map<String, String> DRUG_ACTION_TAKEN3 = new HashMap<String, String>() {
        {
            put("drug2", "PERMANENTLY sTOPPED");
        }
    };
    private static final Map<String, String> DRUG_ACTION_TAKEN4 = new HashMap<String, String>() {
        {
            put("drug2", "nothing");
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
    private static final AeRaw RAW_EVENT2 = AeRaw.builder().id("id2")
            .drugsCausality(DRUG_CAUSALITY1)
            .serious("Yes")
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_1).startDate(toDateTime("03.08.2015 03:00:00"))
                                    .endDate(toDateTime("04.08.2015 03:00:00")).build()
                    )
            )
            .subjectId("sid2").build();
    private static final AeRaw RAW_EVENT3 = AeRaw.builder().id("id3")
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
    private static final AeRaw RAW_EVENT4 = AeRaw.builder().id("id4")
            .drugsCausality(DRUG_CAUSALITY2)
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_5).startDate(toDateTime("04.08.2015 03:00:00"))
                                    .endDate(toDateTime("05.08.2015 03:00:00")).build(),
                            AeSeverityRaw.builder().severity(SEVERITY_2).startDate(toDateTime("06.08.2015 03:00:00"))
                                    .endDate(toDateTime("07.08.2015 03:00:00")).drugsActionTaken(DRUG_ACTION_TAKEN2).build()
                    )
            )
            .serious("aeser")
            .pt("pt_1")
            .soc("soc_1")
            .subjectId("sid4").build();
    private static final AeRaw RAW_EVENT5 = AeRaw.builder().id("id5")
            .drugsCausality(DRUG_CAUSALITY3)
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_3).startDate(toDateTime("04.08.2015 03:00:00"))
                                    .endDate(toDateTime("05.08.2015 03:00:00")).drugsActionTaken(DRUG_ACTION_TAKEN3).build(),
                            AeSeverityRaw.builder().severity(SEVERITY_2).startDate(toDateTime("06.08.2015 03:00:00"))
                                    .endDate(toDateTime("07.08.2015 03:00:00")).build()
                    )
            )
            .serious("y")
            .subjectId("sid5").build();
    private static final AeRaw RAW_EVENT6 = AeRaw.builder().id("id6")
            .drugsCausality(DRUG_CAUSALITY4)
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_5).startDate(toDateTime("04.08.2015 03:00:00"))
                                    .endDate(toDateTime("05.08.2015 03:00:00")).drugsActionTaken(DRUG_ACTION_TAKEN4).build(),
                            AeSeverityRaw.builder().severity(SEVERITY_2).startDate(toDateTime("06.08.2015 03:00:00"))
                                    .endDate(toDateTime("07.08.2015 03:00:00")).build()
                    )
            )
            .serious("1")
            .pt("pt_2")
            .soc("soc_2")
            .subjectId("sid6").build();
    private static final AeRaw RAW_EVENT7 = AeRaw.builder().id("id7")
            .drugsCausality(DRUG_CAUSALITY5)
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_3).startDate(toDateTime("06.08.2015 03:00:00"))
                                    .endDate(toDateTime("07.08.2015 03:00:00")).build()
                    )
            )
            .serious("1")
            .subjectId("sid5").build();
    private static final AeRaw RAW_EVENT8 = AeRaw.builder().id("id8")
            .drugsCausality(DRUG_CAUSALITY6)
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_5).startDate(toDateTime("04.08.2015 03:00:00"))
                                    .endDate(toDateTime("05.08.2015 03:00:00")).build(),
                            AeSeverityRaw.builder().severity(SEVERITY_2).startDate(toDateTime("06.08.2015 03:00:00"))
                                    .endDate(toDateTime("07.08.2015 03:00:00")).build()
                    )
            )
            .serious("0")
            .pt("pt_1")
            .soc("soc_1")
            .subjectId("sid6").build();
    private static final AeRaw RAW_EVENT9 = AeRaw.builder().id("id9")
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(SEVERITY_3).startDate(toDateTime("04.08.2015 03:00:00"))
                                    .endDate(toDateTime("05.08.2015 03:00:00")).drugsActionTaken(DRUG_ACTION_TAKEN2).build(),
                            AeSeverityRaw.builder().severity(SEVERITY_5).startDate(toDateTime("06.08.2015 03:00:00"))
                                    .endDate(toDateTime("07.08.2015 03:00:00")).drugsActionTaken(DRUG_ACTION_TAKEN1).build()
                    )
            )
            .serious("0")
            .subjectId("sid6").build();

    static {

        SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS = Subject.builder().subjectId("sid1").subjectCode("E01").clinicalStudyCode("D0001C00001")
                .studyPart("A")
                .otherCohort("Default group")
                .otherGrouping("Cohort")
                .firstTreatmentDate(toDate("01.08.2015"))
                .drugsDosed(DRUG_DOSED1)
                .build();

        SUBJECT2_WITH_DEFAULT_GROUPINGS_AND_GROUPS = Subject.builder().subjectId("sid2").subjectCode("E02").clinicalStudyCode("D0001C00001")
                .studyPart("B")
                .otherCohort("Default group")
                .otherGrouping("Cohort")
                .firstTreatmentDate(toDate("01.09.2015 04:00:00"))
                .drugsDosed(DRUG_DOSED1)
                .build();

        SUBJECT3_WITH_DEFAULT_GROUPINGS_AND_GROUPS = Subject.builder().subjectId("sid3").subjectCode("E03").clinicalStudyCode("D0001C00002")
                .studyPart("A")
                .otherCohort("Default group")
                .otherGrouping("Cohort")
                .firstTreatmentDate(toDate("01.07.2015"))
                .drugsDosed(DRUG_DOSED1)
                .build();
        SUBJECT4_WITH_DEFAULT_GROUPINGS_AND_GROUPS = Subject.builder().subjectId("sid4").subjectCode("E04").clinicalStudyCode("D0001C00002")
                .studyPart("A")
                .otherCohort("Default group")
                .otherGrouping("Cohort")
                .firstTreatmentDate(toDate("01.07.2015"))
                .drugsDosed(DRUG_DOSED2)
                .build();
        SUBJECT5_WITH_DEFAULT_GROUPINGS_AND_GROUPS = Subject.builder().subjectId("sid5").subjectCode("E05").clinicalStudyCode("D0001C00002")
                .studyPart("A")
                .otherCohort("Default group")
                .otherGrouping("Cohort")
                .firstTreatmentDate(toDate("01.07.2015"))
                .drugsDosed(DRUG_DOSED2)
                .build();
        SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS = Subject.builder().subjectId("sid6").subjectCode("E06").clinicalStudyCode("D0001C00002")
                .studyPart("A")
                .otherCohort("Default group")
                .otherGrouping("Cohort")
                .firstTreatmentDate(toDate("01.07.2015"))
                .drugsDosed(DRUG_DOSED2)
                .build();

        SUBJECT1_WITH_DOSE_GROUPINGS_AND_DOSE_GROUPS_ONLY = Subject.builder().subjectId("sid1").subjectCode("E01").clinicalStudyCode("D0001C00001")
                .studyPart("A")
                .doseCohort("Dose cohort")
                .doseGrouping("Dose grouping")
                .firstTreatmentDate(toDate("01.07.2015"))
                .drugsDosed(DRUG_DOSED2)
                .build();
        SUBJECT2_WITH_DOSE_GROUPINGS_AND_DOSE_GROUPS_ONLY = Subject.builder().subjectId("sid2").subjectCode("E02").clinicalStudyCode("D0001C00001")
                .studyPart("A")
                .doseCohort("Dose cohort")
                .doseGrouping("Dose grouping")
                .firstTreatmentDate(toDate("01.07.2015"))
                .drugsDosed(DRUG_DOSED2)
                .build();
        SUBJECT3_WITH_DOSE_GROUPINGS_AND_DOSE_GROUPS_ONLY = Subject.builder().subjectId("sid3").subjectCode("E03").clinicalStudyCode("D0001C00001")
                .studyPart("D")
                .doseCohort("Dose cohort")
                .doseGrouping("Dose grouping")
                .firstTreatmentDate(toDate("01.07.2015"))
                .drugsDosed(DRUG_DOSED2)
                .build();
        SUBJECT4_WITH_DOSE_GROUPINGS_AND_DOSE_GROUPS_ONLY = Subject.builder().subjectId("sid4").subjectCode("E04").clinicalStudyCode("D0001C00003")
                .studyPart("C")
                .doseCohort("Dose cohort")
                .doseGrouping("Dose grouping")
                .firstTreatmentDate(toDate("01.07.2015"))
                .drugsDosed(DRUG_DOSED2)
                .build();

        SUBJECT1_WITH_GROUPINGS_AND_GROUPS = Subject.builder().subjectId("sid1").subjectCode("E01").clinicalStudyCode("D0001C00001")
                .studyPart("A")
                .doseCohort("Dose cohort")
                .doseGrouping("Dose grouping")
                .otherGrouping("Other grouping")
                .otherCohort("Other cohort")
                .firstTreatmentDate(toDate("01.07.2015"))
                .drugsDosed(DRUG_DOSED2)
                .build();
        SUBJECT2_WITH_GROUPINGS_AND_GROUPS = Subject.builder().subjectId("sid2").subjectCode("E02").clinicalStudyCode("D0001C00001")
                .studyPart("A")
                .doseCohort("Dose cohort")
                .doseGrouping("Dose grouping")
                .otherGrouping("Other grouping")
                .otherCohort("Other cohort")
                .firstTreatmentDate(toDate("01.07.2015"))
                .drugsDosed(DRUG_DOSED2)
                .build();
        SUBJECT3_WITH_GROUPINGS_AND_GROUPS = Subject.builder().subjectId("sid3").subjectCode("E03").clinicalStudyCode("D0001C00001")
                .studyPart("C")
                .doseCohort("Dose cohort")
                .doseGrouping("Dose grouping")
                .otherGrouping("Other grouping")
                .otherCohort("Other cohort")
                .firstTreatmentDate(toDate("01.07.2015"))
                .drugsDosed(DRUG_DOSED2)
                .build();
        SUBJECT4_WITH_GROUPINGS_AND_GROUPS = Subject.builder().subjectId("sid4").subjectCode("E04").clinicalStudyCode("D0001C00004")
                .studyPart("A")
                .doseCohort("Dose cohort")
                .doseGrouping("Dose grouping")
                .otherGrouping("Other grouping")
                .otherCohort("Other cohort")
                .firstTreatmentDate(toDate("01.07.2015"))
                .drugsDosed(DRUG_DOSED2)
                .build();
    }

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Autowired
    private AeSummariesAnyService aeSummariesAnyService;
    @Autowired
    private AeSummariesMstCmnService aeSummariesMstCmnService;

    @MockBean(name = "aeIncidenceDatasetsDataProvider")
    private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsDataProvider;
    @MockBean
    private AeSeverityChangeDatasetsDataProvider aeSeverityChangeDatasetsDataProvider;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

//Groupings tests

    @Test
    public void shouldReturnAesSummariesMostCommonTableWithTotalGroupWhenBothOfGroupings() {
        List<Ae> aes = asList(new Ae(RAW_EVENT5, SUBJECT1_WITH_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT6, SUBJECT2_WITH_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT3, SUBJECT3_WITH_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT4, SUBJECT4_WITH_GROUPINGS_AND_GROUPS));
        List<Subject> subjects = aes.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList());

        mockProviders(aes, subjects);

        List<AeSummariesTable> table = aeSummariesMstCmnService.getAesSummariesTable(DUMMY_ACUITY_DATASETS);

        softly.assertThat(table.size()).isEqualTo(4);
        softly.assertThat(table).extracting(AeSummariesTable::getDatasetName).containsExactly("D0001C00001", "D0001C00001", "D0001C00004", "");
        softly.assertThat(table).extracting(AeSummariesTable::getCountDosedSubject).containsExactly(2L, 1L, 1L, 0L);
        softly.assertThat(table).extracting(AeSummariesTable::getCohortCounts).containsExactly(
                newHashSet(new AeSummariesTable.AeSummariesCohortCount("Dose cohort", "Dose grouping", GroupingType.DOSE, "A", 2),
                        new AeSummariesTable.AeSummariesCohortCount("Other cohort", "Other grouping", GroupingType.NONE, "A", 2)),
                newHashSet(new AeSummariesTable.AeSummariesCohortCount("Dose cohort", "Dose grouping", GroupingType.DOSE, "C", 1),
                        new AeSummariesTable.AeSummariesCohortCount("Other cohort", "Other grouping", GroupingType.NONE, "C", 1)),
                newHashSet(new AeSummariesTable.AeSummariesCohortCount("Dose cohort", "Dose grouping", GroupingType.DOSE, "A", 1),
                        new AeSummariesTable.AeSummariesCohortCount("Other cohort", "Other grouping", GroupingType.NONE, "A", 1)),
                newHashSet(new AeSummariesTable.AeSummariesCohortCount("TOTAL", "", GroupingType.NONE, "", 4)));
    }

    @Test
    public void shouldReturnAnyAeRowAndTwoPtRowsCorrectlyForMostCommonWhenNoPtsInEvents() {
        List<Ae> aes = asList(new Ae(RAW_EVENT5, SUBJECT1_WITH_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT6, SUBJECT2_WITH_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT3, SUBJECT3_WITH_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT4, SUBJECT4_WITH_GROUPINGS_AND_GROUPS));
        List<Subject> subjects = aes.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList());

        mockProviders(aes, subjects);

        List<AeSummariesTable> table = aeSummariesMstCmnService.getAesSummariesTable(DUMMY_ACUITY_DATASETS);
        softly.assertThat(table).extracting(a -> a.getRows().size()).containsExactly(3, 3, 3, 3);
        softly.assertThat(table).extracting(a -> {
            System.out.println(a.getRows().get(0).getCells().get(0));
            return a.getRows().get(0).getCells().get(0).getValue();
        }).containsExactly(1, 1, 1, 3);

    }


    @Test
    public void shouldReturnAesSummariesAnyCategoryTableWhenBothOfGroupings() {
        List<Ae> aes = asList(new Ae(RAW_EVENT5, SUBJECT1_WITH_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT6, SUBJECT2_WITH_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT3, SUBJECT3_WITH_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT4, SUBJECT4_WITH_GROUPINGS_AND_GROUPS));
        List<Subject> subjects = aes.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList());

        mockProviders(aes, subjects);

        List<AeSummariesTable> table = aeSummariesAnyService.getAesSummariesTable(DUMMY_ACUITY_DATASETS);

        softly.assertThat(table.size()).isEqualTo(3);
        softly.assertThat(table).extracting(AeSummariesTable::getDatasetName).containsExactly("D0001C00001", "D0001C00001", "D0001C00004");
        softly.assertThat(table).extracting(AeSummariesTable::getCountDosedSubject).containsExactly(2L, 1L, 1L);
        softly.assertThat(table).extracting(AeSummariesTable::getCohortCounts).containsExactly(
                newHashSet(new AeSummariesTable.AeSummariesCohortCount("Dose cohort", "Dose grouping", GroupingType.DOSE, "A", 2),
                        new AeSummariesTable.AeSummariesCohortCount("Other cohort", "Other grouping", GroupingType.NONE, "A", 2)),
                newHashSet(new AeSummariesTable.AeSummariesCohortCount("Dose cohort", "Dose grouping", GroupingType.DOSE, "C", 1),
                        new AeSummariesTable.AeSummariesCohortCount("Other cohort", "Other grouping", GroupingType.NONE, "C", 1)),
                newHashSet(new AeSummariesTable.AeSummariesCohortCount("Dose cohort", "Dose grouping", GroupingType.DOSE, "A", 1),
                        new AeSummariesTable.AeSummariesCohortCount("Other cohort", "Other grouping", GroupingType.NONE, "A", 1)));
    }

    @Test
    public void shouldReturnAesSummariesAnyCategoryTableWhenDoseGroupingOnly() {
        List<Ae> aes = asList(new Ae(RAW_EVENT5, SUBJECT1_WITH_DOSE_GROUPINGS_AND_DOSE_GROUPS_ONLY),
                new Ae(RAW_EVENT6, SUBJECT2_WITH_DOSE_GROUPINGS_AND_DOSE_GROUPS_ONLY),
                new Ae(RAW_EVENT3, SUBJECT3_WITH_DOSE_GROUPINGS_AND_DOSE_GROUPS_ONLY),
                new Ae(RAW_EVENT4, SUBJECT4_WITH_DOSE_GROUPINGS_AND_DOSE_GROUPS_ONLY));
        List<Subject> subjects = aes.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList());

        mockProviders(aes, subjects);

        List<AeSummariesTable> table = aeSummariesAnyService.getAesSummariesTable(DUMMY_ACUITY_DATASETS);

        softly.assertThat(table.size()).isEqualTo(3);
        softly.assertThat(table).extracting(AeSummariesTable::getDatasetName).containsExactly("D0001C00001", "D0001C00001", "D0001C00003");
        softly.assertThat(table).extracting(AeSummariesTable::getCountDosedSubject).containsExactly(2L, 1L, 1L);
        softly.assertThat(table).extracting(AeSummariesTable::getCohortCounts).containsExactly(
                Collections.singleton(new AeSummariesTable.AeSummariesCohortCount("Dose cohort", "Dose grouping", GroupingType.DOSE, "A", 2)),
                Collections.singleton(new AeSummariesTable.AeSummariesCohortCount("Dose cohort", "Dose grouping", GroupingType.DOSE, "D", 1)),
                Collections.singleton(new AeSummariesTable.AeSummariesCohortCount("Dose cohort", "Dose grouping", GroupingType.DOSE, "C", 1)));
    }

    @Test
    public void shouldReturnAesSummariesAnyCategoryTableWhenNoGrouping() {
        List<Ae> aes = asList(new Ae(RAW_EVENT1, SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT2, SUBJECT2_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT3, SUBJECT3_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT4, SUBJECT4_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT5, SUBJECT5_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT6, SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS));
        List<Subject> subjects = aes.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList());

        mockProviders(aes, subjects);

        List<AeSummariesTable> table = aeSummariesAnyService.getAesSummariesTable(DUMMY_ACUITY_DATASETS);

        softly.assertThat(table.size()).isEqualTo(3);
        softly.assertThat(table).extracting(AeSummariesTable::getDatasetName).containsExactly("D0001C00001", "D0001C00001", "D0001C00002");
        softly.assertThat(table).extracting(AeSummariesTable::getCountDosedSubject).containsExactly(1L, 1L, 4L);
        softly.assertThat(table).extracting(AeSummariesTable::getCohortCounts).containsExactly(
                Collections.singleton(new AeSummariesTable.AeSummariesCohortCount("Default group", "Cohort", GroupingType.NONE, "A", 1)),
                Collections.singleton(new AeSummariesTable.AeSummariesCohortCount("Default group", "Cohort", GroupingType.NONE, "B", 1)),
                Collections.singleton(new AeSummariesTable.AeSummariesCohortCount("Default group", "Cohort", GroupingType.NONE, "A", 4)));
    }

    //Row tests

    @Test
    public void shouldReturnAnyAERowInAnyCategoryTable() {
        List<Ae> aes = asList(new Ae(RAW_EVENT1, SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT2, SUBJECT2_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT3, SUBJECT3_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT4, SUBJECT4_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT5, SUBJECT5_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT6, SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS));
        List<Subject> subjects = aes.stream().map(SubjectAwareWrapper::getSubject).collect(Collectors.toList());

        mockProviders(aes, subjects);

        List<AeSummariesTable> table = aeSummariesAnyService.getAesSummariesTable(DUMMY_ACUITY_DATASETS);

        softly.assertThat(table.get(2).getRows().get(0)).isEqualTo(createAnyAeRow(4, 100.0, GroupingType.NONE, "A"));
    }

    @Test
    public void shouldReturnCausallyRelatedRowsInAnyCategoryTableFor2Drugs() {
        List<Ae> aes = asList(new Ae(RAW_EVENT1, SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT2, SUBJECT2_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT3, SUBJECT3_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT4, SUBJECT4_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT5, SUBJECT5_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT6, SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT7, SUBJECT5_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT8, SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS));
        List<Subject> subjects = aes.stream().map(SubjectAwareWrapper::getSubject).distinct().collect(Collectors.toList());

        mockProviders(aes, subjects);

        List<AeSummariesTable> table = aeSummariesAnyService.getAesSummariesTable(DUMMY_ACUITY_DATASETS);

        softly.assertThat(table.get(2).getRows()).containsAll(createCausallyRelatedRowsFor2Drugs(asList(4, 3, 1),
                asList(100d, 75d, 25d), GroupingType.NONE, asList("A", "A", "A")));
    }

    @Test
    public void shouldReturnCTCGrade3OrHigherRowsInAnyCategoryTableFor2Drugs() {
        List<Ae> aes = asList(new Ae(RAW_EVENT1, SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT2, SUBJECT2_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT3, SUBJECT3_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT4, SUBJECT4_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT5, SUBJECT5_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT6, SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT7, SUBJECT5_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT8, SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS));
        List<Subject> subjects = aes.stream().map(SubjectAwareWrapper::getSubject).distinct().collect(Collectors.toList());

        mockProviders(aes, subjects);

        List<AeSummariesTable> table = aeSummariesAnyService.getAesSummariesTable(DUMMY_ACUITY_DATASETS);

        softly.assertThat(table.get(2).getRows()).containsAll(
                createCTCGrade3OrHigherRowsFor2Drugs(asList(4, 4, 3, 1), asList(100d, 100d, 75d, 25d),
                        GroupingType.NONE, asList("A", "A", "A", "A")));
    }

    @Test
    public void shouldReturnDeathOutcomeRowsInAnyCategoryTable() {
        List<Ae> aes = asList(new Ae(RAW_EVENT1, SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT2, SUBJECT2_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT3, SUBJECT3_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT4, SUBJECT4_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT5, SUBJECT5_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT6, SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT7, SUBJECT5_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT8, SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS));
        List<Subject> subjects = aes.stream().map(SubjectAwareWrapper::getSubject).distinct().collect(Collectors.toList());

        mockProviders(aes, subjects);

        List<AeSummariesTable> table = aeSummariesAnyService.getAesSummariesTable(DUMMY_ACUITY_DATASETS);

        softly.assertThat(table.get(2).getRows()).containsAll(
                createDeathOutcomeRowsFor2Drugs(asList(3, 3, 2, 1), asList(75d, 75d, 50d, 25d), GroupingType.NONE,
                        asList("A", "A", "A", "A")));
    }

    @Test
    public void shouldReturnLeadingToDiscontinuationRowsInAnyCategoryTable() {
        List<Ae> aes = asList(new Ae(RAW_EVENT1, SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT2, SUBJECT2_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT3, SUBJECT3_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT4, SUBJECT4_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT5, SUBJECT5_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT6, SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT7, SUBJECT5_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT8, SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS));
        List<Subject> subjects = aes.stream().map(SubjectAwareWrapper::getSubject).distinct().collect(Collectors.toList());

        mockProviders(aes, subjects);

        List<AeSummariesTable> table = aeSummariesAnyService.getAesSummariesTable(DUMMY_ACUITY_DATASETS);

        softly.assertThat(table.get(2).getRows()).containsAll(
                createLeadingToDiscontinuationRowsFor2Drugs(
                        asList(3, 3, 2, 2, 1, 1), asList(75d, 75d, 50d, 50d, 25d, 25d), GroupingType.NONE,
                        asList("A", "A", "A", "A", "A", "A")));
    }

    @Test
    public void shouldReturnSAERowsInAnyCategoryTable() {
        List<Ae> aes = asList(new Ae(RAW_EVENT1, SUBJECT1_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT2, SUBJECT2_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT3, SUBJECT3_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT4, SUBJECT4_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT5, SUBJECT5_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT6, SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT7, SUBJECT5_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT8, SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS),
                new Ae(RAW_EVENT9, SUBJECT6_WITH_DEFAULT_GROUPINGS_AND_GROUPS));
        List<Subject> subjects = aes.stream().map(SubjectAwareWrapper::getSubject).distinct().collect(Collectors.toList());

        mockProviders(aes, subjects);

        List<AeSummariesTable> table = aeSummariesAnyService.getAesSummariesTable(DUMMY_ACUITY_DATASETS);

        softly.assertThat(table.get(2).getRows()).containsAll(
                createSAERowsFor2Drugs(
                        asList(4, 4, 3, 3, 3, 2, 2, 1, 1, 1), asList(100d, 100d, 75d, 75d, 75d, 50d, 50d, 25d, 25d, 25d),
                        GroupingType.NONE, asList("A", "A", "A", "A", "A", "A", "A", "A", "A", "A")));
    }

    private AeSummariesRow createAnyAeRow(int value, double percentage, GroupingType groupingType, String studyPart) {
        return AeSummariesRow.builder()
                .rowDescription("Any AE ")
                .cells(asList(new AeSummariesCell("Default group", "Cohort", value, percentage, groupingType, studyPart)))
                .build();
    }

    private List<AeSummariesRow> createCausallyRelatedRowsFor2Drugs(List<Integer> values, List<Double> percentages,
                                                                    GroupingType groupingType, List<String> studyParts) {
        return asList(AeSummariesRow.builder()
                        .rowDescription("Any AE causally related to any study treatment")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(0), percentages.get(0), groupingType, studyParts.get(0))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any AE causally related to drug2")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(1), percentages.get(1), groupingType, studyParts.get(1))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any AE causally related to drug1")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(2), percentages.get(2), groupingType, studyParts.get(2))))
                        .build());
    }

    private List<AeSummariesRow> createCTCGrade3OrHigherRowsFor2Drugs(List<Integer> values, List<Double> percentages,
                                                                      GroupingType groupingType, List<String> studyParts) {
        return asList(AeSummariesRow.builder()
                        .rowDescription("Any AE of CTC grade 3 or higher ")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(0), percentages.get(0), groupingType, studyParts.get(0))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any AE of CTC grade 3 or higher, causally related to any study treatment")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(1), percentages.get(1), groupingType, studyParts.get(1))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any AE of CTC grade 3 or higher, causally related to drug2")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(2), percentages.get(2), groupingType, studyParts.get(2))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any AE of CTC grade 3 or higher, causally related to drug1")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(3), percentages.get(3), groupingType, studyParts.get(3))))
                        .build());
    }

    private List<AeSummariesRow> createLeadingToDiscontinuationRowsFor2Drugs(List<Integer> values, List<Double> percentages,
                                                                             GroupingType groupingType, List<String> studyParts) {
        return asList(AeSummariesRow.builder()
                        .rowDescription("Any AE leading to discontinuation of any study treatment")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(0), percentages.get(0), groupingType, studyParts.get(0))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any AE leading to discontinuation of any study treatment, causally related to any study treatment")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(1), percentages.get(1), groupingType, studyParts.get(1))))
                        .build(),
                AeSummariesRow.builder()
                        .rowDescription("Any AE leading to discontinuation of drug2")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(2), percentages.get(2), groupingType, studyParts.get(2))))
                        .build(),
                AeSummariesRow.builder()
                        .rowDescription("Any AE leading to discontinuation of drug2, causally related to drug2")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(3), percentages.get(3), groupingType, studyParts.get(3))))
                        .build(),
                AeSummariesRow.builder()
                        .rowDescription("Any AE leading to discontinuation of drug1")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(4), percentages.get(4), groupingType, studyParts.get(4))))
                        .build(),
                AeSummariesRow.builder()
                        .rowDescription("Any AE leading to discontinuation of drug1, causally related to drug1")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(5), percentages.get(5), groupingType, studyParts.get(5))))
                        .build());
    }

    private List<AeSummariesRow> createSAERowsFor2Drugs(List<Integer> values, List<Double> percentages, GroupingType groupingType, List<String> studyParts) {
        return asList(
                AeSummariesRow.builder()
                        .rowDescription("Any SAE (including events with outcome = Death) ")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(0), percentages.get(0), groupingType, studyParts.get(0))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any SAE (including events with outcome = Death), causally related to any study treatment")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(1), percentages.get(1), groupingType, studyParts.get(1))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any SAE leading to discontinuation of any study treatment")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(2), percentages.get(2), groupingType, studyParts.get(2))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any SAE leading to discontinuation of any study treatment, causally related to any study treatment")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(3), percentages.get(3), groupingType, studyParts.get(3))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any SAE (including events with outcome = Death), causally related to drug2")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(4), percentages.get(4), groupingType, studyParts.get(4))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any SAE leading to discontinuation of drug2")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(5), percentages.get(5), groupingType, studyParts.get(5))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any SAE leading to discontinuation of drug2, causally related to drug2")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(6), percentages.get(6), groupingType, studyParts.get(6))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any SAE (including events with outcome = Death), causally related to drug1")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(7), percentages.get(7), groupingType, studyParts.get(7))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any SAE leading to discontinuation of drug1")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(8), percentages.get(8), groupingType, studyParts.get(8))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any SAE leading to discontinuation of drug1, causally related to drug1")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(9), percentages.get(9), groupingType, studyParts.get(9))))
                        .build()
        );
    }

    private List<AeSummariesRow> createDeathOutcomeRowsFor2Drugs(List<Integer> values, List<Double> percentages,
                                                                 GroupingType groupingType, List<String> studyParts) {
        return asList(AeSummariesRow.builder()
                        .rowDescription("Any AE with outcome = Death ")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(0), percentages.get(0), groupingType, studyParts.get(0))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any AE with outcome = Death, causally related to any study treatment")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(1), percentages.get(1), groupingType, studyParts.get(1))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any AE with outcome = Death, causally related to drug2")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(2), percentages.get(2), groupingType, studyParts.get(2))))
                        .build(),

                AeSummariesRow.builder()
                        .rowDescription("Any AE with outcome = Death, causally related to drug1")
                        .cells(asList(new AeSummariesCell("Default group", "Cohort", values.get(3), percentages.get(3), groupingType, studyParts.get(3))))
                        .build());
    }

    private void mockProviders(List<Ae> aes, List<Subject> subjects) {
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(aeSeverityChangeDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(aes);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjects);
    }

}
