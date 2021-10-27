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

import com.acuity.visualisations.rawdatamodel.dataproviders.AssessmentDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.TargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.AssessedTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Assessment;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.TargetLesion;
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

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TargetLesionServiceTest {

    @Autowired
    private TargetLesionService targetLesionService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private TargetLesionDatasetsDataProvider targetLesionDatasetsDataProvider;
    @MockBean
    private AssessmentDatasetsDataProvider assessmentDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();


    private static Subject subject;
    private static TargetLesion tl11, tl12, tl21, tl22, tl31, tl32;
    private static Assessment as1, as2, as3;
    private static AssessedTargetLesion atl1, atl2, atl3;

    static {
        Map<String, Date> firstDoseDate = new HashMap<>();
        firstDoseDate.put("drug1", toDate("2015-01-20"));

        subject = Subject.builder().subjectId("sid1").firstTreatmentDate(toDate("2015-01-20")).build();

        tl11 = new TargetLesion(TargetLesionRaw.builder().id("tl11").subjectId("sid1").baseline(true).lesionNumber("1")
                .lesionDate(toDate("2015-01-15")).lesionDiameter(10).lesionsDiameterPerAssessment(25)
                .sumChangeFromMinimum(0).lesionSite("Neck")
                .visitDate(toDate("2015-01-16")).visitNumber(1).build(), subject);
        tl12 = new TargetLesion(TargetLesionRaw.builder().id("tl12").subjectId("sid1").lesionNumber("2")
                .lesionDate(toDate("2015-01-15")).lesionDiameter(15).lesionsDiameterPerAssessment(25)
                .sumChangeFromMinimum(0).lesionSite("Liver")
                .visitDate(toDate("2015-01-16")).visitNumber(1).build(), subject);
        tl21 = new TargetLesion(TargetLesionRaw.builder().id("tl21").subjectId("sid1").lesionNumber("1")
                .lesionDate(toDate("2015-02-15")).lesionDiameter(13).lesionSite("Neck").lesionsDiameterPerAssessment(23)
                .sumChangeFromMinimum(-2)
                .visitDate(toDate("2015-02-16")).visitNumber(2).build(), subject);
        tl22 = new TargetLesion(TargetLesionRaw.builder().id("tl22").subjectId("sid1").lesionNumber("2")
                .lesionDate(toDate("2015-02-15")).lesionDiameter(10).lesionsDiameterPerAssessment(23)
                .sumChangeFromMinimum(-2).lesionSite("Liver")
                .visitDate(toDate("2015-02-16")).visitNumber(2).build(), subject);
        tl31 = new TargetLesion(TargetLesionRaw.builder().id("tl31").subjectId("sid1").lesionNumber("1")
                .lesionDate(toDate("2015-03-15")).lesionDiameter(8).lesionsDiameterPerAssessment(18)
                .sumChangeFromMinimum(-5).lesionSite("Neck")
                .visitDate(toDate("2015-03-16")).visitNumber(3).build(), subject);
        tl32 = new TargetLesion(TargetLesionRaw.builder().id("tl32").subjectId("sid1").lesionNumber("2")
                .lesionDate(toDate("2015-03-15")).lesionDiameter(10).lesionsDiameterPerAssessment(18)
                .sumChangeFromMinimum(-5).lesionSite("Liver")
                .visitDate(toDate("2015-03-16")).visitNumber(3).build(), subject);

        as1 = new Assessment(AssessmentRaw.builder().id("as01").newLesionSinceBaseline("True").responseRank(5).visitNumber(1)
                .visitDate(toDate("2015-01-16")).assessmentDate(toDate("2015-01-16")).subjectId("sid1").build(), subject);
        as2 = new Assessment(AssessmentRaw.builder().id("as02").newLesionSinceBaseline("False").responseRank(4).visitNumber(2)
                .visitDate(toDate("2015-02-16")).assessmentDate(toDate("2015-02-16")).subjectId("sid1").build(), subject);
        as3 = new Assessment(AssessmentRaw.builder().id("as03").newLesionSinceBaseline("No").responseRank(3).visitNumber(3)
                .visitDate(toDate("2015-03-16")).assessmentDate(toDate("2015-03-16")).subjectId("sid1").build(), subject);

        atl1 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("antl1")
                .targetLesionRaw(tl21.getEvent()).assessmentRaw(as1.getEvent()).build(), subject);
        atl2 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("antl2")
                .targetLesionRaw(tl22.getEvent()).assessmentRaw(as1.getEvent()).build(), subject);
        atl3 = new AssessedTargetLesion(AssessedTargetLesionRaw.builder().id("antl3")
                .targetLesionRaw(tl32.getEvent()).assessmentRaw(as2.getEvent()).build(), subject);
    }

    public static final List<TargetLesion> TARGET_LESIONS = newArrayList(tl11, tl12, tl21, tl22, tl31, tl32);
    static final List<Assessment> ASSESSMENTS = newArrayList(as1, as2, as3);

    static final List<AssessedTargetLesion> ASSESSED_TL = newArrayList(atl1, atl2, atl3);

    @Test
    public void testGetSingleSubjectData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(targetLesionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(TARGET_LESIONS);

        List<Map<String, String>> singleSubjectData = targetLesionService.getSingleSubjectData(DATASETS, "sid1");

        softly.assertThat(singleSubjectData).hasSize(6);
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("lesionDate"), e -> e.get("studyDay"), e -> e.get("visitNumber"), e -> e.get("lesionNumber"),
                        e -> e.get("methodOfAssessment"), e -> e.get("lesionSite"), e -> e.get("locationWithinSiteSpecification"),
                        e -> e.get("lesionDiameter"), e -> e.get("lesionNoLongerMeasurable"), e -> e.get("lesionIntervention"),
                        e -> e.get("lesionsDiameterPerAssessment"), e -> e.get("percentageChangeFromBaseline"), e -> e.get("percentagesumChangeFromMinimum"),
                        e -> e.get("sumChangeFromMinimum"), e -> e.get("calculatedResponse"))
                .containsSequence(
                        Tuple.tuple("2015-01-15T00:00:00", "-5", "1", "1", "NOT IMPLEMENTED", "Neck", "NOT IMPLEMENTED",
                                "10", "NOT IMPLEMENTED", "NOT IMPLEMENTED", "25", null, null, "0", "NOT IMPLEMENTED"),
                        Tuple.tuple("2015-01-15T00:00:00", "-5", "1", "2", "NOT IMPLEMENTED", "Liver", "NOT IMPLEMENTED",
                                "15", "NOT IMPLEMENTED", "NOT IMPLEMENTED", "25", null, null, "0", "NOT IMPLEMENTED"),
                        Tuple.tuple("2015-02-15T00:00:00", "26", "2", "1", "NOT IMPLEMENTED", "Neck", "NOT IMPLEMENTED",
                                "13", "NOT IMPLEMENTED", "NOT IMPLEMENTED", "23", null, null, "-2", "NOT IMPLEMENTED"),
                        Tuple.tuple("2015-02-15T00:00:00", "26", "2", "2", "NOT IMPLEMENTED", "Liver", "NOT IMPLEMENTED",
                                "10", "NOT IMPLEMENTED", "NOT IMPLEMENTED", "23", null, null, "-2", "NOT IMPLEMENTED"),
                        Tuple.tuple("2015-03-15T00:00:00", "54", "3", "1", "NOT IMPLEMENTED", "Neck", "NOT IMPLEMENTED",
                                "8", "NOT IMPLEMENTED", "NOT IMPLEMENTED", "18", null, null, "-5", "NOT IMPLEMENTED"),
                        Tuple.tuple("2015-03-15T00:00:00", "54", "3", "2", "NOT IMPLEMENTED", "Liver", "NOT IMPLEMENTED",
                                "10", "NOT IMPLEMENTED", "NOT IMPLEMENTED", "18", null, null, "-5", "NOT IMPLEMENTED")
                );

    }
}
