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

import com.acuity.visualisations.rawdatamodel.dataproviders.NonTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.NonTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.NonTargetLesion;
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
public class NonTargetLesionServiceTest {

    @Autowired
    private NonTargetLesionService nonTargetLesionService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private NonTargetLesionDatasetsDataProvider nonTargetLesionDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();


    private static Subject subject;
    private static NonTargetLesion ntl1, ntl2, ntl3, ntl4, ntl5;

    static {

        subject = Subject.builder().subjectId("sid1").baselineDate(toDate("2015-01-18T00:00:00")).firstTreatmentDate(toDate("2015-01-17T00:00:00")).build();

        ntl1 = new NonTargetLesion(NonTargetLesionRaw.builder().id("ntl1").subjectId("sid1").lesionSite("Neck")
                .baselineDate(toDate("2015-01-20T00:00:00")).response("Complete Response")
                .lesionDate(toDate("2015-01-15T00:00:00")).visitDate(toDate("2015-01-16T00:00:00")).visitNumber(1).build(), subject);
        ntl2 = new NonTargetLesion(NonTargetLesionRaw.builder().id("ntl2").subjectId("sid1").lesionSite("Bone")
                .baselineDate(toDate("2015-01-20T00:00:00")).response("Partial Response")
                .lesionDate(toDate("2015-01-20T00:00:00")).visitDate(toDate("2015-02-16T00:00:00")).visitNumber(2).build(), subject);
        ntl3 = new NonTargetLesion(NonTargetLesionRaw.builder().id("ntl3").subjectId("sid1").lesionSite("Breast")
                .baselineDate(toDate("2015-01-20T00:00:00")).response("Other")
                .lesionDate(toDate("2015-01-26T00:00:00")).visitDate(toDate("2015-02-16T00:00:00")).visitNumber(2).build(), subject);
        ntl4 = new NonTargetLesion(NonTargetLesionRaw.builder().id("ntl4").subjectId("sid1").lesionSite("Breast")
                .baselineDate(toDate("2015-01-20T00:00:00")).response("Complete Response")
                .visitDate(toDate("2015-02-16T00:00:00")).visitNumber(2).build(), subject);
        // before target lesion baseline but after subject's baseline, must be excluded
        ntl5 = new NonTargetLesion(NonTargetLesionRaw.builder().id("ntl5").subjectId("sid1").lesionSite("Breast")
                .baselineDate(toDate("2015-01-20T00:00:00")).response("Complete Response")
                .visitDate(toDate("2015-01-19T00:00:00")).visitNumber(0).build(), subject);
    }

    public static final List<NonTargetLesion> NON_TARGET_LESIONS = newArrayList(ntl1, ntl2, ntl3, ntl4, ntl5);

    @Before
    public void setUp() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(nonTargetLesionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(NON_TARGET_LESIONS);
    }

    @Test
    public void testGetSingleSubjectData() {

        List<Map<String, String>> singleSubjectData = nonTargetLesionService.getSingleSubjectData(DATASETS, "sid1");

        softly.assertThat(singleSubjectData).hasSize(2);
        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "lesionDate", "studyDay", "visitNumber", "lesionSite", "assessmentMethod", "response", "eventId"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("lesionDate"), e -> e.get("studyDay"), e -> e.get("visitNumber"), e -> e.get("lesionSite"),
                        e -> e.get("assessmentMethod"), e -> e.get("response"))
                .contains(
                        Tuple.tuple("2015-01-20T00:00:00", "3", "2", "Bone", NOT_IMPLEMENTED, "Partial Response"),
                        Tuple.tuple("2015-01-26T00:00:00", "9", "2", "Breast", NOT_IMPLEMENTED, "Other")
                );
    }
}
