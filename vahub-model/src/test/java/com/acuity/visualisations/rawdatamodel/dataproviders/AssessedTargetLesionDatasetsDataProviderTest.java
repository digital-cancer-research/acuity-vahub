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

import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import com.acuity.visualisations.rawdatamodel.vo.NonTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Assessment;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.TargetLesion;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NO;
import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class AssessedTargetLesionDatasetsDataProviderTest {

    private Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1").firstTreatmentDate(toDate("2000-04-05"))
            .lastTreatmentDate(toDate("2000-05-20")).build();
    private Subject subject2 = Subject.builder().subjectId("subjectId2").subjectCode("subject2").firstTreatmentDate(toDate("2000-01-10"))
            .lastTreatmentDate(toDate("2000-05-21")).build();
    private Subject subject3 = Subject.builder().subjectId("subjectId3").subjectCode("subject3").firstTreatmentDate(toDate("2000-02-21"))
            .lastTreatmentDate(toDate("2000-05-21")).build();
    private List<Subject> population = Arrays.asList(subject1, subject2, subject3);

    private Date visit1Date =  DaysUtil.toDate("2000-05-15");
    private Date visit2Date =  DaysUtil.toDate("2000-05-25");

    private TargetLesion tl11 = new TargetLesion(TargetLesionRaw.builder().id("tl11Id").subjectId(subject1.getId())
            .visitNumber(1).visitDate(visit1Date).lesionDiameter(10).lesionsDiameterPerAssessment(15)
            .sumPercentageChangeFromBaseline(0.0).baseline(true).lesionCountAtVisit(2).bestPercentageChange(false)
            .lesionNumber("1").lesionDate(visit1Date).build(), subject1);
    private TargetLesion tl12 = new TargetLesion(TargetLesionRaw.builder().id("tl12Id").subjectId(subject1.getId())
            .visitNumber(1).visitDate(visit1Date).lesionDiameter(5).lesionsDiameterPerAssessment(15)
            .sumPercentageChangeFromBaseline(0.0).baseline(true).lesionCountAtVisit(2).bestPercentageChange(false)
            .lesionNumber("2").lesionDate(visit1Date).build(), subject1);
    private TargetLesion tl21 = new TargetLesion(TargetLesionRaw.builder().id("tl21Id").subjectId(subject1.getId())
            .baseline(false).lesionCountAtVisit(2).visitNumber(2).visitDate(visit2Date)
            .lesionDiameter(8).lesionsDiameterPerAssessment(12)
            .lesionNumber("1").lesionDate(visit2Date).sumPercentageChangeFromBaseline(-20.0)
            .bestPercentageChange(false).build(), subject1);
    private TargetLesion tl22 = new TargetLesion(TargetLesionRaw.builder().id("tl22Id").subjectId(subject1.getId())
            .baseline(false).lesionCountAtVisit(2).visitNumber(2).visitDate(visit2Date)
            .lesionDiameter(4).lesionsDiameterPerAssessment(12)
            .lesionNumber("2").lesionDate(visit2Date).sumPercentageChangeFromBaseline(-20.0)
            .bestPercentageChange(false).build(), subject1);
    private TargetLesion tl1s2 = new TargetLesion(TargetLesionRaw.builder().id("tl1s2").subjectId(subject2.getId())
            .baseline(false).lesionCountAtVisit(1).visitNumber(1).visitDate(visit1Date)
            .lesionDiameter(4).lesionsDiameterPerAssessment(4)
            .lesionNumber("1").lesionDate(visit1Date).sumPercentageChangeFromBaseline(-15.0)
            .bestPercentageChange(false).build(), subject2);
    private TargetLesion tl2s2 = new TargetLesion(TargetLesionRaw.builder().id("tl2s2").subjectId(subject2.getId())
            .baseline(false).lesionCountAtVisit(1).visitNumber(2).visitDate(visit2Date)
            .lesionDiameter(3).lesionsDiameterPerAssessment(3)
            .lesionNumber("1").lesionDate(visit2Date).sumPercentageChangeFromBaseline(-18.0)
            .bestPercentageChange(false).build(), subject2);

    private List<TargetLesion> targetLesions = newArrayList(tl11, tl12, tl21, tl22, tl1s2, tl2s2);

    private NonTargetLesionRaw ntl1 = NonTargetLesionRaw.builder().id("ntl1Id").subjectId(subject1.getId())
            .response("Complete response").responseShort("CR").visitNumber(1).visitDate(visit1Date).build();
    private NonTargetLesionRaw ntl2 = NonTargetLesionRaw.builder().id("ntl2Id").subjectId(subject1.getId())
            .response("NE").responseShort("NE").visitNumber(2).visitDate(visit2Date).build();

    private List<NonTargetLesionRaw> nonTargetLesions = newArrayList(ntl1, ntl2);

    private Assessment a1 = new Assessment(AssessmentRaw.builder().id("a1Id").subjectId(subject1.getId())
            .visitDate(visit1Date).visitNumber(1).response("Stable Disease").assessmentFrequency(2).build(), subject1);
    // 2 assessments per one visit, which is wrong data, but one of them has wrong visit date and must be filtered out
    private Assessment a21 = new Assessment(AssessmentRaw.builder().id("a1Id").subjectId(subject1.getId())
            .visitDate(DaysUtil.addDays(visit2Date, -2)).visitNumber(2).response("Complete Response").build(), subject1);
    private Assessment a22 = new Assessment(AssessmentRaw.builder().id("a1Id").subjectId(subject1.getId())
            .visitDate(visit2Date).visitNumber(2).response("Partial Response").build(), subject1);
    private Assessment a1s2 = new Assessment(AssessmentRaw.builder().id("a1s2Id").subjectId(subject2.getId())
            .visitDate(visit1Date).visitNumber(1).response("Partial Response").build(), subject2);
    private Assessment a2s2 = new Assessment(AssessmentRaw.builder().id("a2s2Id").subjectId(subject2.getId())
            .visitDate(visit2Date).visitNumber(2).response("Complete Response").build(), subject2);

    private List<Assessment> assessments = newArrayList(a1, a21, a22, a1s2, a2s2);

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private AssessmentDatasetsDataProvider assessmentDatasetsDataProvider;
    @MockBean
    private TargetLesionDatasetsDataProvider targetLesionDatasetsDataProvider;
    @MockBean
    private NonTargetLesionDatasetsDataProvider nonTargetLesionDatasetsDataProvider;
    @Autowired
    private AssessedTargetLesionDatasetsDataProvider assessedTargetLesionDatasetsDataProvider;

    @Test
    public void testLoadData() throws Exception {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);
        when(assessmentDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(assessments);
        when(targetLesionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(targetLesions);
        when(nonTargetLesionDatasetsDataProvider.getData(any(Dataset.class))).thenReturn(nonTargetLesions);
        final Collection<AssessedTargetLesion> res = assessedTargetLesionDatasetsDataProvider.loadData(DUMMY_ACUITY_DATASETS);
        softly.assertThat(res).extracting(AssessedTargetLesion::getSubjectId,
                s -> s.getEvent().getTargetLesionRaw().getId(),
                s -> s.getEvent().getAssessmentRaw().getVisitNumber(),
                s -> s.getEvent().getAssessmentRaw().getVisitDate(),
                s -> s.getEvent().getAssessmentRaw().getResponse(),
                s -> s.getEvent().getResponse(),
                s -> s.getEvent().getBestResponse(),
                s -> s.getEvent().getNtlResponse(),
                s -> s.getEvent().getNonTargetLesionsPresent(),
                s -> s.getEvent().getBestResponseEvent().getTargetLesionRaw().getId(),
                s -> s.getEvent().getAssessmentFrequency())
                .containsExactly(
                        tuple("subjectId1", "tl11Id", 1, visit1Date, "Stable Disease", "Stable Disease",
                                "Partial Response", "CR", YES, "tl21Id", 2),
                        tuple("subjectId1", "tl12Id", 1, visit1Date, "Stable Disease", "Stable Disease",
                                "Partial Response", "CR", YES, "tl21Id", 2),
                        tuple("subjectId1", "tl21Id", 2, visit2Date, "Partial Response", "Partial Response",
                                "Partial Response", "NE", YES, "tl21Id", 2),
                        tuple("subjectId1", "tl22Id", 2, visit2Date, "Partial Response", "Partial Response",
                                "Partial Response", "NE", YES, "tl21Id", 2),
                        tuple("subjectId2", "tl1s2", 1, visit1Date, "Partial Response", "Partial Response",
                                "Complete Response", "Missing", NO, "tl2s2", 2),
                        tuple("subjectId2", "tl2s2", 2, visit2Date, "Complete Response", "Complete Response",
                                "Complete Response", "Missing", NO, "tl2s2", 2));
    }

    @Test
    public void testLoadDataByVisit() throws Exception {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);
        when(assessmentDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(assessments);
        when(targetLesionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(targetLesions);
        when(nonTargetLesionDatasetsDataProvider.getData(any(Dataset.class))).thenReturn(nonTargetLesions);
        final Collection<AssessedTargetLesion> res = assessedTargetLesionDatasetsDataProvider.loadDataByVisit(DUMMY_ACUITY_DATASETS);
        softly.assertThat(res).extracting(AssessedTargetLesion::getSubjectId,
                s -> s.getEvent().getTargetLesionRaw().getId(),
                s -> s.getEvent().getAssessmentRaw().getVisitNumber(),
                s -> s.getEvent().getAssessmentRaw().getVisitDate(),
                s -> s.getEvent().getAssessmentRaw().getResponse(),
                s -> s.getEvent().getNtlResponse(),
                s -> s.getEvent().getNonTargetLesionsPresent())
                .containsExactly(
                        tuple("subjectId1", "tl11Id", 1, visit1Date, "Stable Disease", "CR", YES),
                        tuple("subjectId1", "tl21Id", 2, visit2Date, "Partial Response", "NE", YES),
                        tuple("subjectId2", "tl1s2", 1, visit1Date, "Partial Response", "Missing", NO),
                        tuple("subjectId2", "tl2s2", 2, visit2Date, "Complete Response", "Missing", NO));
    }
}
