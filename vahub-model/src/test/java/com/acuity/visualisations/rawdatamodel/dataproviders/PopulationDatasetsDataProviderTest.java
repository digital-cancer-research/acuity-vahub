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

package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.common.lookup.BeanLookupService;
import com.acuity.visualisations.rawdatamodel.dao.DeathRepository;
import com.acuity.visualisations.rawdatamodel.dao.DoseDiscRepository;
import com.acuity.visualisations.rawdatamodel.dao.DrugDoseRepository;
import com.acuity.visualisations.rawdatamodel.dao.PopulationRepository;
import com.acuity.visualisations.rawdatamodel.dao.StudyInfoRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProviderAwareTest;
import com.acuity.visualisations.rawdatamodel.dataproviders.config.DataProviderConfiguration;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.DoseDiscRaw;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.GroupType;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuityDataset;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ResolvableType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASET_42;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDate;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDateTime;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class, DataProviderConfiguration.class})
public class PopulationDatasetsDataProviderTest extends DataProviderAwareTest {

    @Autowired
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @MockBean(name = "acuityPopulationRawDataRepository")
    private PopulationRepository populationRepository;

    @MockBean
    private DrugDoseRepository drugDoseRepository;

    @MockBean
    private DeathRepository deathRepository;

    @MockBean
    private DoseDiscRepository doseDiscRepository;

    @MockBean
    private StudyInfoRepository studyInfoRepository;

    @Autowired
    private BeanLookupService beanLookupService;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void testLoadDataFromUploadedFile() {

        Subject subject1 = getSubjectBuilder().subjectId("subj1").sex("m").dateOfBirth(toDateTime("1960-12-12T00:00:00"))
                .firstTreatmentDate(toDateTime("2011-02-09T00:00:00")).datasetId("d1").build();
        Subject subject2 = getSubjectBuilder().subjectId("subj2").sex("f").datasetId("d1").build();
        Subject subject3 = getSubjectBuilder().subjectId("subj3").sex("m").datasetId("d1").build();
        List<Subject> population = newArrayList(subject1, subject2, subject3);
        long currentTimestamp = System.currentTimeMillis();
        Date date1 = new Date(currentTimestamp - 10 * 24 * 60 * 60 * 1000);
        Date date2 = new Date(currentTimestamp - 5 * 24 * 60 * 60 * 1000);

        DrugDoseRaw drug1 = DrugDoseRaw.builder().subjectId("subj1").drug(null)
                .dose(50.).doseUnit("mg").frequencyName("BID").startDate(toDateTime("2011-02-09T00:00:00"))
                .endDate(toDateTime("2011-02-10T00:00:00")).build();
        DrugDoseRaw drug5 = DrugDoseRaw.builder().subjectId("subj1").drug(null)
                .dose(25.).doseUnit("mg").frequencyName("BID").startDate(toDateTime("2011-02-24T00:00:00"))
                .endDate(toDateTime("2011-02-29T00:00:00")).build();
        DrugDoseRaw drug2 = DrugDoseRaw.builder().subjectId("subj1").drug("drug2").dose(1.)
                .startDate(toDateTime("2011-03-01T00:00:00")).endDate(toDateTime("2011-03-15T00:00:00")).build();
        DrugDoseRaw drug3 = DrugDoseRaw.builder().subjectId("subj2").drug("drug1")
                .dose(5.).doseUnit("mg").frequencyName("BID").startDate(toDateTime("2011-03-01T00:00:00"))
                .endDate(toDateTime("2011-04-10T00:00:00")).build();
        DrugDoseRaw drug4 = DrugDoseRaw.builder().subjectId("subj2").drug("drug2").dose(0.).build();
        List<DrugDoseRaw> drugDoseRaws = newArrayList(drug1, drug2, drug3, drug4, drug5);

        DoseDiscRaw drugDisc1 = DoseDiscRaw.builder().subjectId("subj1").studyDrug(null).discDate(date1).discReason("Other").build();
        DoseDiscRaw drugDisc2 = DoseDiscRaw.builder().subjectId("subj2").studyDrug("drug1").discDate(date2).discReason("AE").build();
        DoseDiscRaw drugDisc3 = DoseDiscRaw.builder().subjectId("subj2").studyDrug("drug2").discDate(date2).discReason("AE").build();
        List<DoseDiscRaw> drugsDiscontinued = newArrayList(drugDisc1, drugDisc2, drugDisc3);

        Subject.SubjectEthnicGroup ethnicGroup1 = Subject.SubjectEthnicGroup.builder().subjectId("subj1").ethnicGroup("eth_group1")
                .specifiedEthnicGroup("specified_eth_group1").build();
        Subject.SubjectEthnicGroup ethnicGroup2 = Subject.SubjectEthnicGroup.builder().subjectId("subj2").ethnicGroup("eth_group2")
                .specifiedEthnicGroup("specified_eth_group2").build();
        List<Subject.SubjectEthnicGroup> ethnicGroups = newArrayList(ethnicGroup1, ethnicGroup2);
        Subject.SubjectVitalsInfo vitalsInfo1 = Subject.SubjectVitalsInfo.builder().subjectId("subj1").testDate(date1).testName("weight").testValue(51.).build();
        Subject.SubjectVitalsInfo vitalsInfo2 = Subject.SubjectVitalsInfo.builder().subjectId("subj1").testDate(date2).testName("weight").testValue(56.).build();
        Subject.SubjectVitalsInfo vitalsInfo3 = Subject.SubjectVitalsInfo.builder().subjectId("subj1").testDate(date1).testName("height").testValue(176.).build();
        Subject.SubjectVitalsInfo vitalsInfo4 = Subject.SubjectVitalsInfo.builder().subjectId("subj2").testName("weight").testValue(47.).build();
        Subject.SubjectVitalsInfo vitalsInfo5 = Subject.SubjectVitalsInfo.builder().subjectId("subj2").testName("height").testValue(159.).build();
        Subject.SubjectVitalsInfo vitalsInfo6 = Subject.SubjectVitalsInfo.builder().subjectId("subj2").testDate(date2).testName("height").testValue(160.).build();
        List<Subject.SubjectVitalsInfo> vitalsInfos = newArrayList(vitalsInfo1, vitalsInfo2, vitalsInfo3, vitalsInfo4, vitalsInfo5, vitalsInfo6);
        Subject.SubjectGroup group1 = Subject.SubjectGroup.builder().subjectId("subj1").groupPreferredName("name1").groupingName("grouping_name1")
                .groupName("name").groupIndex(26).groupType(GroupType.DOSE).build();
        Subject.SubjectGroup group2 = Subject.SubjectGroup.builder().subjectId("subj2").groupPreferredName("name2").groupingName("grouping_name2")
                .groupName("name").groupIndex(29).groupType(GroupType.NONE).build();
        List<Subject.SubjectGroup> groups = newArrayList(group1, group2);

        StudyInfo studyInfo = StudyInfo.builder().lastUpdatedDate(toDateTime("2015-04-10T00:00:00")).build();

        when(beanLookupService.get(any(Dataset.class), any(Class.class))).thenReturn(populationRepository);
        when(beanLookupService.get(any(Dataset.class), any(ResolvableType.class))).thenReturn(populationRepository);
        when(populationRepository.getRawData(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(population);
        when(doseDiscRepository.getRawData(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(drugsDiscontinued);
        when(drugDoseRepository.getRawData(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(drugDoseRaws);
        when(populationRepository.getSubjectEthnicGroup(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(ethnicGroups);
        when(populationRepository.getSubjectVitalsInfo(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(vitalsInfos);
        when(populationRepository.getSubjectGroup(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(groups);
        when(studyInfoRepository.getRawData(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(Collections.singletonList(studyInfo));
        when(studyInfoRepository.getRawData(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(Collections.emptyList());

        List<Subject> result = new ArrayList<>(populationDatasetsDataProvider.loadData(new Datasets(DUMMY_ACUITY_DATASET_42)));

        softly.assertThat(result).hasSize(3);

        softly.assertThat(result.get(0).getSubjectId()).isEqualTo(subject1.getSubjectId());
        softly.assertThat(result.get(0).getSex()).isEqualTo(subject1.getSex());
        softly.assertThat(result.get(0).getAge()).isEqualTo(50);
        softly.assertThat(result.get(0).getDrugsDosed().keySet()).containsExactlyInAnyOrder("drug1", "drug2", "");
        softly.assertThat(result.get(0).getDrugsDosed().get("drug1")).isEqualTo("No");
        softly.assertThat(result.get(0).getDrugsDosed().get("drug2")).isEqualTo("Yes");
        softly.assertThat(result.get(0).getDrugsDosed().get("")).isEqualTo("Yes");
        softly.assertThat(result.get(0).getDrugsMaxDoses().keySet()).containsExactlyInAnyOrder("", "drug1", "drug2");
        softly.assertThat(result.get(0).getDrugsMaxDoses().get("")).isEqualTo("50 mg");
        softly.assertThat(result.get(0).getDrugsMaxDoses().get("drug1")).isNull();
        softly.assertThat(result.get(0).getDrugsMaxDoses().get("drug2")).isEqualTo("1");
        softly.assertThat(result.get(0).getDrugsMaxFrequencies().keySet()).containsExactlyInAnyOrder("", "drug1", "drug2");
        softly.assertThat(result.get(0).getDrugsMaxFrequencies().get("")).isEqualTo("BID");
        softly.assertThat(result.get(0).getDrugsMaxFrequencies().get("drug1")).isNull();
        softly.assertThat(result.get(0).getDrugsMaxFrequencies().get("drug2")).isNull();
        softly.assertThat(result.get(0).getDrugsDiscontinued().keySet()).containsExactlyInAnyOrder("drug1", "drug2", "");
        softly.assertThat(result.get(0).getDrugsDiscontinued().get("drug1")).isEqualTo("No");
        softly.assertThat(result.get(0).getDrugsDiscontinued().get("drug2")).isEqualTo("No");
        softly.assertThat(result.get(0).getDrugsDiscontinued().get("")).isEqualTo("Yes");
        softly.assertThat(result.get(0).getDrugDiscontinuationMainReason().keySet()).containsExactlyInAnyOrder("", "drug1", "drug2");
        softly.assertThat(result.get(0).getDrugDiscontinuationMainReason().get("")).isEqualTo(drugDisc1.getDiscReason());
        softly.assertThat(result.get(0).getDrugDiscontinuationMainReason().get("drug1")).isNull();
        softly.assertThat(result.get(0).getDrugDiscontinuationMainReason().get("drug2")).isNull();
        softly.assertThat(result.get(0).getDrugDiscontinuationDate().keySet()).containsExactlyInAnyOrder("", "drug1", "drug2");
        softly.assertThat(result.get(0).getDrugDiscontinuationDate().get("")).isEqualTo(drugDisc1.getDiscDate());
        softly.assertThat(result.get(0).getDrugDiscontinuationDate().get("drug1")).isNull();
        softly.assertThat(result.get(0).getDrugDiscontinuationDate().get("drug2")).isNull();
        softly.assertThat(result.get(0).getLastTreatmentDate()).isInSameDayAs("2011-03-15");
        softly.assertThat(result.get(0).getEthnicGroup()).isEqualTo("eth_group1");
        softly.assertThat(result.get(0).getSpecifiedEthnicGroup()).isEqualTo("specified_eth_group1");
        softly.assertThat(result.get(0).getWeight()).isEqualTo(56.);
        softly.assertThat(result.get(0).getHeight()).isEqualTo(176.);
        softly.assertThat(result.get(0).getDoseGrouping()).isEqualTo("grouping_name1");
        softly.assertThat(result.get(0).getOtherGrouping()).isEqualTo("Cohort");
        softly.assertThat(result.get(0).getDoseCohort()).isEqualTo("(Z)name1");
        softly.assertThat(result.get(0).getOtherCohort()).isEqualTo("Default group");

        softly.assertThat(result.get(1).getSubjectId()).isEqualTo(subject2.getSubjectId());
        softly.assertThat(result.get(1).getDrugsDosed().get("drug1")).isEqualTo("Yes");
        softly.assertThat(result.get(1).getDrugsDosed().get("drug2")).isEqualTo("No");
        softly.assertThat(result.get(1).getDrugsDosed().get("")).isEqualTo("No");
        softly.assertThat(result.get(1).getDrugsDiscontinued().get("")).isEqualTo("No");
        softly.assertThat(result.get(1).getDrugsDiscontinued().get("drug1")).isEqualTo("Yes");
        softly.assertThat(result.get(1).getDrugsDiscontinued().get("drug2")).isEqualTo("Yes");
        softly.assertThat(result.get(1).getEthnicGroup()).isEqualTo("eth_group2");
        softly.assertThat(result.get(1).getSpecifiedEthnicGroup()).isEqualTo("specified_eth_group2");
        softly.assertThat(result.get(1).getWeight()).isEqualTo(47.);
        softly.assertThat(result.get(1).getHeight()).isEqualTo(160.);
        softly.assertThat(result.get(1).getDoseGrouping()).isEqualTo("Cohort");
        softly.assertThat(result.get(1).getOtherGrouping()).isEqualTo("grouping_name2");
        softly.assertThat(result.get(1).getDoseCohort()).isEqualTo("Default group");
        softly.assertThat(result.get(1).getOtherCohort()).isEqualTo("(AC)name2");
    }

    @Test
    public void testLoadDataMergeDatasets() {

        Subject subject1 = Subject.builder()
                .clinicalStudyCode("1")
                .datasetId("Dataset1")
                .subjectId("subj1Id")
                .subjectCode("subject1")
                .sex("m")
                .age(50)
                .build();
        Subject subject2 = Subject.builder()
                .clinicalStudyCode("2")
                .datasetId("Dataset2")
                .subjectId("subj2Id")
                .subjectCode("subject1")
                .sex("m")
                .age(60)
                .build();
        Subject subject3 = Subject.builder()
                .clinicalStudyCode("3")
                .datasetId("Dataset4")
                .subjectId("subj3Id")
                .subjectCode("subject5")
                .sex("f")
                .age(40)
                .build();


        DrugDoseRaw drug11 = DrugDoseRaw.builder().subjectId("subj1Id").drug("drug1").dose(1.).startDate(toDateTime("2011-02-10T00:00:00"))
                .endDate(toDateTime("2011-02-17T00:00:00")).build();
        DrugDoseRaw drug12 = DrugDoseRaw.builder().subjectId("subj1Id").drug("drug2").dose(0.).startDate(toDateTime("2011-02-11T00:00:00"))
                .endDate(toDateTime("2011-02-17T00:00:00")).build();
        DrugDoseRaw drug21 = DrugDoseRaw.builder().subjectId("subj2Id").drug("drug1").dose(0.).startDate(toDateTime("2011-02-14T00:00:00"))
                .endDate(toDateTime("2011-02-20T00:00:00")).build();
        DrugDoseRaw drug22 = DrugDoseRaw.builder().subjectId("subj2Id").drug("drug3").dose(0.).startDate(toDateTime("2011-02-15T00:00:00"))
                .endDate(toDateTime("2011-02-22T00:00:00")).build();
        DrugDoseRaw drug23 = DrugDoseRaw.builder().subjectId("subj2Id").drug("drug4").dose(2.).startDate(toDateTime("2011-02-16T00:00:00"))
                .endDate(toDateTime("2011-02-21T00:00:00")).build();

        DoseDiscRaw drugDisc11 = DoseDiscRaw.builder().subjectId("subj1Id").studyDrug("drug1").discDate(toDateTime("2011-03-04T00:00:00")).build();

        when(beanLookupService.get(any(Dataset.class), any(Class.class))).thenReturn(populationRepository);
        when(beanLookupService.get(any(Dataset.class), any(ResolvableType.class))).thenReturn(populationRepository);
        when(populationRepository.getRawData(1L)).thenReturn(newArrayList(subject1));
        when(populationRepository.getRawData(2L)).thenReturn(newArrayList(subject2));
        when(populationRepository.getRawData(3L)).thenReturn(newArrayList(subject3));
        when(drugDoseRepository.getRawData(1L)).thenReturn(newArrayList(drug11, drug12));
        when(drugDoseRepository.getRawData(2L)).thenReturn(newArrayList(drug21, drug22, drug23));
        when(doseDiscRepository.getRawData(1L)).thenReturn(newArrayList(drugDisc11));
        when(doseDiscRepository.getRawData(2L)).thenReturn(Collections.emptyList());
        when(studyInfoRepository.getRawData(1L)).thenReturn(Collections.emptyList());
        when(studyInfoRepository.getRawData(2L)).thenReturn(Collections.emptyList());

        List<Subject> result = new ArrayList<>(populationDatasetsDataProvider.loadData(new Datasets(
                new AcuityDataset(1L),
                new AcuityDataset(2L),
                new AcuityDataset(3L))));

        softly.assertThat(result).hasSize(3);

        softly.assertThat(result.get(0).getSubjectId()).isEqualTo(subject1.getSubjectId());
        softly.assertThat(result.get(0).getSubjectCode()).isEqualTo("Dataset1-subject1");
        softly.assertThat(result.get(0).getRawSubject()).isEqualTo("subject1");
        softly.assertThat(result.get(0).isMerged()).isTrue();
        softly.assertThat(result.get(0).getDrugsDosed().keySet()).containsExactlyInAnyOrder("drug1", "drug2", "drug3", "drug4");
        softly.assertThat(result.get(0).getDrugsDosed().get("drug1")).isEqualTo("Yes");
        softly.assertThat(result.get(0).getDrugsDosed().get("drug2")).isEqualTo("No");
        softly.assertThat(result.get(0).getDrugsDosed().get("drug3")).isEqualTo("No");
        softly.assertThat(result.get(0).getDrugsDosed().get("drug4")).isEqualTo("No");
        softly.assertThat(result.get(0).getDrugsDiscontinued().keySet()).containsExactlyInAnyOrder("drug1", "drug2", "drug3", "drug4");
        softly.assertThat(result.get(0).getDrugsDiscontinued().get("drug1")).isEqualTo("Yes");
        softly.assertThat(result.get(0).getDrugsDiscontinued().get("drug2")).isEqualTo("No");
        softly.assertThat(result.get(0).getDrugsDiscontinued().get("drug3")).isEqualTo("No");
        softly.assertThat(result.get(0).getDrugsDiscontinued().get("drug4")).isEqualTo("No");

        softly.assertThat(result.get(1).getSubjectId()).isEqualTo(subject2.getSubjectId());
        softly.assertThat(result.get(1).getSubjectCode()).isEqualTo("Dataset2-subject1");
        softly.assertThat(result.get(1).getRawSubject()).isEqualTo("subject1");
        softly.assertThat(result.get(1).isMerged()).isTrue();
        softly.assertThat(result.get(1).getDrugsDosed().keySet()).containsExactlyInAnyOrder("drug1", "drug2", "drug3", "drug4");
        softly.assertThat(result.get(1).getDrugsDosed().get("drug1")).isEqualTo("No");
        softly.assertThat(result.get(1).getDrugsDosed().get("drug2")).isEqualTo("No");
        softly.assertThat(result.get(1).getDrugsDosed().get("drug3")).isEqualTo("No");
        softly.assertThat(result.get(1).getDrugsDosed().get("drug4")).isEqualTo("Yes");
        softly.assertThat(result.get(1).getDrugsDiscontinued().keySet()).containsExactlyInAnyOrder("drug1", "drug2", "drug3", "drug4");
        softly.assertThat(result.get(1).getDrugsDiscontinued().get("drug1")).isEqualTo("No");
        softly.assertThat(result.get(1).getDrugsDiscontinued().get("drug2")).isEqualTo("No");
        softly.assertThat(result.get(1).getDrugsDiscontinued().get("drug3")).isEqualTo("No");
        softly.assertThat(result.get(1).getDrugsDiscontinued().get("drug4")).isEqualTo("No");

        softly.assertThat(result.get(2).getSubjectId()).isEqualTo(subject3.getSubjectId());
        softly.assertThat(result.get(2).getSubjectCode()).isEqualTo("Dataset4-subject5");
        softly.assertThat(result.get(2).getRawSubject()).isEqualTo("subject5");
        softly.assertThat(result.get(2).isMerged()).isTrue();

        List<Subject> resultForOneDataSet
                = new ArrayList<>(populationDatasetsDataProvider.loadData(new Datasets(new AcuityDataset(3L))));
        softly.assertThat(resultForOneDataSet.get(0).getSubjectId()).isEqualTo(subject3.getSubjectId());
        softly.assertThat(resultForOneDataSet.get(0).getSubjectCode()).isEqualTo("subject5");
        softly.assertThat(resultForOneDataSet.get(0).getRawSubject()).isEqualTo("subject5");
        softly.assertThat(resultForOneDataSet.get(0).isMerged()).isFalse();
    }

    @Test
    public void shouldReturnCorrectGroupings() {
        Subject subject1 = getSubjectBuilder().subjectId("subj1").datasetId("d1").build();
        Subject subject2 = getSubjectBuilder().subjectId("subj2").datasetId("d1").build();
        Subject subject3 = getSubjectBuilder().subjectId("subj3").datasetId("d1").build();
        Subject subject4 = getSubjectBuilder().subjectId("subj4").datasetId("d1").build();

        Subject.SubjectGroup doseGroup1 = Subject.SubjectGroup.builder().subjectId("subj1").groupType(GroupType.DOSE).groupingName("grouping_name1")
                .groupIndex(3).groupPreferredName("preferred_name").groupDefaultName("default_name").groupName("group_name").build();
        Subject.SubjectGroup otherGroup1 = Subject.SubjectGroup.builder().subjectId("subj1").groupType(GroupType.NONE)
                .groupingName("grouping_name2").groupIndex(3).groupName("group_name").build();
        Subject.SubjectGroup doseGroup2 = Subject.SubjectGroup.builder().subjectId("subj2").groupingName("grouping_name2")
                .groupType(GroupType.DOSE).build();
        Subject.SubjectGroup otherGroup2 = Subject.SubjectGroup.builder().subjectId("subj3").groupType(GroupType.NONE).groupIndex(30).build();

        when(beanLookupService.get(any(Dataset.class), any(Class.class))).thenReturn(populationRepository);
        when(beanLookupService.get(any(Dataset.class), any(ResolvableType.class))).thenReturn(populationRepository);
        when(populationRepository.getRawData(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(newArrayList(subject1, subject2, subject3, subject4));
        when(populationRepository.getSubjectGroup(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(newArrayList(doseGroup1, doseGroup2, otherGroup1, otherGroup2));
        when(studyInfoRepository.getRawData(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(Collections.emptyList());

        List<Subject> result = new ArrayList<>(populationDatasetsDataProvider.loadData(new Datasets(DUMMY_ACUITY_DATASET_42)));

        softly.assertThat(result).hasSize(4);
        softly.assertThat(result.get(0).getSubjectId()).isEqualTo(subject1.getSubjectId());
        softly.assertThat(result.get(0).getDoseCohort()).isEqualTo("(C)preferred_name");
        softly.assertThat(result.get(0).getOtherCohort()).isEqualTo("(C)group_name");
        softly.assertThat(result.get(0).getDoseGrouping()).isEqualTo("grouping_name1");
        softly.assertThat(result.get(0).getOtherGrouping()).isEqualTo("grouping_name2");

        softly.assertThat(result.get(1).getSubjectId()).isEqualTo(subject2.getSubjectId());
        softly.assertThat(result.get(1).getDoseCohort()).isEqualTo("Default group");
        softly.assertThat(result.get(1).getOtherCohort()).isEqualTo("Default group");
        softly.assertThat(result.get(1).getDoseGrouping()).isEqualTo("grouping_name2");
        softly.assertThat(result.get(1).getOtherGrouping()).isEqualTo("Cohort");

        softly.assertThat(result.get(2).getSubjectId()).isEqualTo(subject3.getSubjectId());
        softly.assertThat(result.get(2).getDoseCohort()).isEqualTo("Default group");
        softly.assertThat(result.get(2).getOtherCohort()).isEqualTo("(AD)Default group");
        softly.assertThat(result.get(2).getDoseGrouping()).isEqualTo("Cohort");
        softly.assertThat(result.get(2).getOtherGrouping()).isEqualTo("Cohort");

        softly.assertThat(result.get(3).getSubjectId()).isEqualTo(subject4.getSubjectId());
        softly.assertThat(result.get(3).getDoseCohort()).isEqualTo("Default group");
        softly.assertThat(result.get(3).getOtherCohort()).isEqualTo(null);
        softly.assertThat(result.get(3).getDoseGrouping()).isEqualTo("Cohort");
        softly.assertThat(result.get(3).getOtherGrouping()).isEqualTo(null);
    }

    @Test
    public void shouldReturnCorrectDrugDosed() {
        Subject subject1 = getSubjectBuilder().subjectId("subj1").datasetId("d1").lastEtlDate(toDateTime("2011-02-23T00:00:00")).build();
        Subject subject2 = getSubjectBuilder().subjectId("subj2").datasetId("d1").lastEtlDate(toDateTime("2011-02-23T00:00:00")).build();

        DrugDoseRaw drug1 = DrugDoseRaw.builder().subjectId("subj1").drug("drug1").dose(1.).startDate(toDateTime("2011-02-10T00:00:00"))
                .endDate(toDateTime("2011-02-11T00:00:00")).build();
        DrugDoseRaw drug2 = DrugDoseRaw.builder().subjectId("subj1").drug("drug1").dose(2.).startDate(toDateTime("2011-02-11T00:00:00"))
                .endDate(toDateTime("2011-02-17T00:00:00")).doseUnit("mg").build();

        DrugDoseRaw drug3 = DrugDoseRaw.builder().subjectId("subj1").drug("drug2").dose(3.).startDate(toDateTime("2011-02-14T00:00:00"))
                .endDate(toDateTime("2011-02-20T00:00:00")).build();
        DrugDoseRaw drug4 = DrugDoseRaw.builder().subjectId("subj1").drug("drug2").dose(4.).startDate(toDateTime("2011-02-15T00:00:00"))
                .endDate(toDateTime("2011-02-16T00:00:00")).doseUnit("mg").build();
        DrugDoseRaw drug5 = DrugDoseRaw.builder().subjectId("subj1").drug("drug2").dose(5.).startDate(toDateTime("2011-02-20T00:00:00"))
                .frequencyName("BID").build();
        DrugDoseRaw drug6 = DrugDoseRaw.builder().subjectId("subj1").drug("drug2").dose(0.).startDate(toDateTime("2011-02-22T00:00:00"))
                .endDate(toDateTime("2011-02-27T00:00:00")).doseUnit("mg").frequencyName("BID").build();

        DrugDoseRaw drug7 = DrugDoseRaw.builder().subjectId("subj2").drug("drug1").dose(4.).startDate(toDateTime("2011-02-15T00:00:00"))
                .endDate(toDateTime("2011-02-16T00:00:00")).doseUnit("mg").frequencyName("BID").build();
        DrugDoseRaw drug8 = DrugDoseRaw.builder().subjectId("subj2").drug("drug1").dose(1.).startDate(toDateTime("2011-02-18T00:00:00"))
                .endDate(toDateTime("2011-02-21T00:00:00")).doseUnit("mg").frequencyName("BID").build();

        List<DrugDoseRaw> drugs = newArrayList(drug1, drug2, drug3, drug4, drug5, drug6, drug7, drug8);

        when(beanLookupService.get(any(Dataset.class), any(Class.class))).thenReturn(populationRepository);
        when(beanLookupService.get(any(Dataset.class), any(ResolvableType.class))).thenReturn(populationRepository);
        when(populationRepository.getRawData(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(newArrayList(subject1, subject2));
        when(drugDoseRepository.getRawData(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(drugs);
        when(studyInfoRepository.getRawData(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(Collections.emptyList());

        List<Subject> result = new ArrayList<>(populationDatasetsDataProvider.loadData(new Datasets(DUMMY_ACUITY_DATASET_42)));
        softly.assertThat(result).hasSize(2);
        softly.assertThat(result.get(0).getSubjectId()).isEqualTo(subject1.getSubjectId());
        softly.assertThat(result.get(0).getDrugsDosed().keySet()).containsExactlyInAnyOrder("drug1", "drug2");
        softly.assertThat(result.get(0).getDrugsDosed().get("drug1")).isEqualTo("Yes");
        softly.assertThat(result.get(0).getDrugsDosed().get("drug2")).isEqualTo("Yes");
        softly.assertThat(result.get(0).getDrugFirstDoseDate().get("drug1")).isInSameDayAs(toDateTime("2011-02-10T00:00:00"));
        softly.assertThat(result.get(0).getDrugFirstDoseDate().get("drug2")).isInSameDayAs(toDateTime("2011-02-14T00:00:00"));
        softly.assertThat(result.get(0).getDrugsMaxDoses().get("drug1")).isEqualTo("2 mg");
        softly.assertThat(result.get(0).getDrugsMaxDoses().get("drug2")).isEqualTo("5");
        softly.assertThat(result.get(0).getDrugsMaxFrequencies().get("drug1")).isNull();
        softly.assertThat(result.get(0).getDrugsMaxFrequencies().get("drug2")).isEqualTo("BID");
        softly.assertThat(result.get(0).getDrugTotalDurationExclBreaks().get("drug1")).isEqualTo(8);
        softly.assertThat(result.get(0).getDrugTotalDurationExclBreaks().get("drug2")).isEqualTo(10);
        softly.assertThat(result.get(0).getDrugTotalDurationInclBreaks().get("drug1")).isEqualTo(8);
        softly.assertThat(result.get(0).getDrugTotalDurationInclBreaks().get("drug2")).isEqualTo(10);

        softly.assertThat(result.get(1).getSubjectId()).isEqualTo(subject2.getSubjectId());
        softly.assertThat(result.get(1).getDrugsDosed().keySet()).containsExactlyInAnyOrder("drug1", "drug2");
        softly.assertThat(result.get(1).getDrugsDosed().get("drug1")).isEqualTo("Yes");
        softly.assertThat(result.get(1).getDrugsDosed().get("drug2")).isEqualTo("No");
        softly.assertThat(result.get(1).getDrugFirstDoseDate().get("drug1")).isInSameDayAs(toDateTime("2011-02-15T00:00:00"));
        softly.assertThat(result.get(1).getDrugsMaxDoses().get("drug1")).isEqualTo("4 mg");
        softly.assertThat(result.get(1).getDrugsMaxFrequencies().get("drug1")).isEqualTo("BID");
        softly.assertThat(result.get(1).getDrugTotalDurationExclBreaks().get("drug1")).isEqualTo(6);
        softly.assertThat(result.get(1).getDrugTotalDurationInclBreaks().get("drug1")).isEqualTo(7);
    }

    @Test
    public void shouldReturnCorrectDosesDisc() {
        Subject subject1 = getSubjectBuilder().subjectId("subj1").datasetId("d1").lastEtlDate(toDateTime("2012-02-23T00:00:00")).build();
        Subject subject2 = getSubjectBuilder().subjectId("subj2").datasetId("d1").lastEtlDate(toDateTime("2012-02-23T00:00:00")).build();

        DrugDoseRaw drug1 = DrugDoseRaw.builder().subjectId(subject1.getSubjectId()).drug("drug1").dose(1.).startDate(toDateTime("2011-02-10T00:00:00")).build();
        DrugDoseRaw drug2 = DrugDoseRaw.builder().subjectId(subject1.getSubjectId()).drug("drug1").dose(4.).startDate(toDateTime("2011-03-03T00:00:00")).build();
        DrugDoseRaw drug3 = DrugDoseRaw.builder().subjectId(subject1.getSubjectId()).drug("drug2").dose(2.).startDate(toDateTime("2011-03-01T00:00:00")).build();
        DrugDoseRaw drug4 = DrugDoseRaw.builder().subjectId(subject2.getSubjectId()).drug("drug2").dose(2.).startDate(toDateTime("2011-02-20T00:00:00")).build();
        DoseDiscRaw doseDisc1 = DoseDiscRaw.builder().subjectId(subject1.getSubjectId()).studyDrug("drug1")
                .discDate(toDateTime("2011-02-13T00:00:00")).discReason("reason1").build();
        DoseDiscRaw doseDisc2 = DoseDiscRaw.builder().subjectId(subject1.getSubjectId()).studyDrug("drug1")
                .discDate(toDateTime("2011-03-11T00:00:00")).discReason("reason2").build();
        DoseDiscRaw doseDisc3 = DoseDiscRaw.builder().subjectId(subject1.getSubjectId()).studyDrug("drug2")
                .discDate(toDateTime("2011-03-02T00:00:00")).discReason("reason3").build();
        DoseDiscRaw doseDisc4 = DoseDiscRaw.builder().subjectId(subject2.getSubjectId()).studyDrug("drug2")
                .discDate(toDateTime("2011-02-21T00:00:00")).discReason("reason4").build();

        List<DrugDoseRaw> drugDoses = newArrayList(drug1, drug2, drug3, drug4);
        List<DoseDiscRaw> dosesDisc = newArrayList(doseDisc1, doseDisc2, doseDisc3, doseDisc4);

        when(beanLookupService.get(any(Dataset.class), any(Class.class))).thenReturn(populationRepository);
        when(beanLookupService.get(any(Dataset.class), any(ResolvableType.class))).thenReturn(populationRepository);
        when(populationRepository.getRawData(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(newArrayList(subject1, subject2));
        when(drugDoseRepository.getRawData(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(drugDoses);
        when(doseDiscRepository.getRawData(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(dosesDisc);
        when(studyInfoRepository.getRawData(DUMMY_ACUITY_DATASET_42.getId())).thenReturn(Collections.emptyList());

        List<Subject> result = new ArrayList<>(populationDatasetsDataProvider.loadData(new Datasets(DUMMY_ACUITY_DATASET_42)));
        softly.assertThat(result).hasSize(2);
        softly.assertThat(result.get(0).getSubjectId()).isEqualTo(subject1.getSubjectId());
        softly.assertThat(result.get(0).getDrugsDiscontinued().keySet()).containsExactlyInAnyOrder("drug1", "drug2");
        softly.assertThat(result.get(0).getDrugsDiscontinued().get("drug1")).isEqualTo("Yes");
        softly.assertThat(result.get(0).getDrugsDiscontinued().get("drug2")).isEqualTo("Yes");
        softly.assertThat(result.get(0).getDrugDiscontinuationDate().get("drug1")).isInSameDayAs(toDate("11.03.2011"));
        softly.assertThat(result.get(0).getDrugDiscontinuationDate().get("drug2")).isInSameDayAs(toDate("02.03.2011"));
        softly.assertThat(result.get(0).getDrugDiscontinuationMainReason().get("drug1")).isEqualTo("reason2");
        softly.assertThat(result.get(0).getDrugDiscontinuationMainReason().get("drug2")).isEqualTo("reason3");

        softly.assertThat(result.get(1).getSubjectId()).isEqualTo(subject2.getSubjectId());
        softly.assertThat(result.get(1).getDrugsDiscontinued().keySet()).containsExactlyInAnyOrder("drug1", "drug2");
        softly.assertThat(result.get(1).getDrugsDiscontinued().get("drug1")).isEqualTo("No");
        softly.assertThat(result.get(1).getDrugsDiscontinued().get("drug2")).isEqualTo("Yes");
        softly.assertThat(result.get(1).getDrugDiscontinuationDate().get("drug1")).isNull();
        softly.assertThat(result.get(1).getDrugDiscontinuationDate().get("drug2")).isInSameDayAs(toDate("21.02.2011"));
        softly.assertThat(result.get(1).getDrugDiscontinuationMainReason().get("drug1")).isNull();
        softly.assertThat(result.get(1).getDrugDiscontinuationMainReason().get("drug2")).isEqualTo("reason4");
    }

    private Subject.SubjectBuilder getSubjectBuilder() {
        return Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_ACUITY_DATASET_42.getId()));
    }
}
