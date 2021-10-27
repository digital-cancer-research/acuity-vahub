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
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.AssessmentRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Assessment;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NOT_IMPLEMENTED;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AssessmentServiceTest {
    @Autowired
    private AssessmentService assessmentService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private AssessmentDatasetsDataProvider assessmentDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Subject subject = Subject.builder().subjectId("sid1").baselineDate(toDate("2015-01-20T00:00:00"))
            .firstTreatmentDate(toDate("2015-01-20T00:00:00")).build();
    private static Assessment as1 = new Assessment(AssessmentRaw.builder().id("as1").baselineDate(toDate("2015-01-20T00:00:00"))
            .assessmentDate(DateUtils.toDateTime("2015-01-20T00:00:00"))
            .visitDate((DateUtils.toDateTime("2015-01-20T00:00:00"))).visitNumber(1).lesionSite("Neck").newLesionSinceBaseline("Yes").build(), subject);
    private static Assessment as2 = new Assessment(AssessmentRaw.builder().id("as2").baselineDate(toDate("2015-01-20T00:00:00"))
            .assessmentDate(DateUtils.toDateTime("2015-01-20T00:00:01"))
            .visitDate((DateUtils.toDateTime("2015-01-20T00:00:01"))).visitNumber(2).lesionSite("Bone").newLesionSinceBaseline("True").build(), subject);
    private static Assessment as3 = new Assessment(AssessmentRaw.builder().id("as3").baselineDate(toDate("2015-01-20T00:00:00"))
            .assessmentDate(DateUtils.toDateTime("2015-01-22T00:00:00"))
            .visitDate((DateUtils.toDateTime("2015-01-22T00:00:00"))).visitNumber(3).lesionSite("Neck").newLesionSinceBaseline("No").build(), subject);
    private static Assessment as4 = new Assessment(AssessmentRaw.builder().id("as4").baselineDate(toDate("2015-01-20T00:00:00"))
            .assessmentDate(DateUtils.toDateTime("2015-01-20T00:00:00"))
            .visitDate((DateUtils.toDateTime("2015-01-20T00:00:00"))).visitNumber(4).newLesionSinceBaseline("False").build(), subject);
    private static Assessment as5 = new Assessment(AssessmentRaw.builder().id("as5").baselineDate(toDate("2015-01-20T00:00:00"))
            .assessmentDate(DateUtils.toDateTime("2015-02-22T00:00:00"))
            .visitDate((DateUtils.toDateTime("2015-02-22T00:00:00"))).visitNumber(5).lesionSite("Neck").build(), subject);
    private static Assessment as6 = new Assessment(AssessmentRaw.builder().id("as6").baselineDate(toDate("2015-01-20T00:00:00"))
            .assessmentDate(DateUtils.toDateTime("2015-01-19T23:59:59"))
            .visitDate((DateUtils.toDateTime("2015-01-19T23:59:59"))).visitNumber(6).lesionSite("Bone").newLesionSinceBaseline("No").build(), subject);
    private static Assessment as7 = new Assessment(AssessmentRaw.builder().id("as7").baselineDate(toDate("2015-01-20T00:00:00"))
            .visitNumber(7).lesionSite("Neck").newLesionSinceBaseline("Yes").build(), subject);
    private static Assessment as8 = new Assessment(AssessmentRaw.builder().id("as8").baselineDate(toDate("2015-01-20T00:00:00"))
            .visitDate(DateUtils.toDateTime("2015-06-20T00:00:00")).build(), subject);
    private static Assessment as9 = new Assessment(AssessmentRaw.builder().id("as9").baselineDate(toDate("2015-01-25T00:00:00"))
            .visitDate(DateUtils.toDateTime("2015-01-22T00:00:00")).build(), subject); // before baseline
    private static Assessment as10 = new Assessment(AssessmentRaw.builder().id("as10").baselineDate(toDate("2015-01-20T00:00:00"))
            .assessmentDate(DateUtils.toDateTime("2015-02-25T00:00:00"))
            .visitDate((DateUtils.toDateTime("2015-02-17T00:00:00"))).visitNumber(7).lesionSite("Neck").build(), subject); // check sort by visit date

    public static final List<Assessment> ASSESSMENTS = newArrayList(as1, as2, as3, as4, as5, as6, as7, as8, as9, as10);

    @Before
    public void initMocks() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(assessmentDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(ASSESSMENTS);
    }

    @Test
    public void testGetSingleSubjectData() {

        List<Map<String, String>> singleSubjectData = assessmentService.getSingleSubjectData(DATASETS, "sid1");

        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "assessmentDate", "visitDate", "studyDay", "visitNumber", "lesionSite", "assessmentMethod", "newLesionResponse",  "eventId"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("assessmentDate"), e -> e.get("visitDate"), e -> e.get("studyDay"), e -> e.get("visitNumber"), e -> e.get("lesionSite"),
                        e -> e.get("assessmentMethod"), e -> e.get("newLesionResponse"))
                .containsExactly(
                        Tuple.tuple("2015-01-20T00:00:00", "2015-01-20T00:00:00", "0", "1", "Neck", NOT_IMPLEMENTED, "Yes"),
                        Tuple.tuple("2015-01-20T00:00:00", "2015-01-20T00:00:00", "0", "4", null, NOT_IMPLEMENTED, "No"),
                        Tuple.tuple("2015-01-20T00:00:01", "2015-01-20T00:00:01", "0", "2", "Bone", NOT_IMPLEMENTED, "Yes"),
                        Tuple.tuple("2015-01-22T00:00:00", "2015-01-22T00:00:00", "2", "3", "Neck", NOT_IMPLEMENTED, "No"),
                        Tuple.tuple("2015-02-25T00:00:00", "2015-02-17T00:00:00", "28", "7", "Neck", NOT_IMPLEMENTED, ""),
                        Tuple.tuple("2015-02-22T00:00:00", "2015-02-22T00:00:00", "33", "5", "Neck", NOT_IMPLEMENTED, ""),
                        Tuple.tuple(null, "2015-06-20T00:00:00", "151", null, null, NOT_IMPLEMENTED, "")
                );
    }
}
