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

import com.acuity.visualisations.common.lookup.CacheableDataProvider;
import com.acuity.visualisations.rawdatamodel.dao.NonTargetLesionRepository;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.NonTargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TargetLesionRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.TargetLesion;
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

import static com.acuity.visualisations.rawdatamodel.Constants.DATASET;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class NonTargetLesionDatasetsDataProviderTest {

    private Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1").firstTreatmentDate(toDate("2000-04-05"))
            .baselineDate(toDate("2000-04-06")).build();
    private Subject subject2 = Subject.builder().subjectId("subjectId2").subjectCode("subject2").firstTreatmentDate(toDate("2000-01-10"))
            .baselineDate(toDate("2000-01-09")).build();

    private List<Subject> population = Arrays.asList(subject1, subject2);

    private Date visit1Date =  DaysUtil.toDate("2000-05-15");
    private Date visit2Date =  DaysUtil.toDate("2000-05-25");

    private NonTargetLesionRaw ntl1 = NonTargetLesionRaw.builder().id("ntl1Id").subjectId(subject1.getId())
            .response("Complete Response").responseShort("CR").build();
    private NonTargetLesionRaw ntl2 = NonTargetLesionRaw.builder().id("ntl2Id").subjectId(subject1.getId())
            .response("Not Evaluable").responseShort("NE").build();
    private NonTargetLesionRaw ntl3 = NonTargetLesionRaw.builder().id("ntl3Id").subjectId(subject2.getId())
            .response("Not Evaluated").responseShort("Not Evaluated").build();

    private List<NonTargetLesionRaw> nonTargetLesions = newArrayList(ntl1, ntl2, ntl3);

    private TargetLesion tl1 = new TargetLesion(TargetLesionRaw.builder().id("tl1Id").subjectId(subject1.getId())
            .visitNumber(1).visitDate(visit1Date).lesionDiameter(10).lesionsDiameterPerAssessment(15)
            .sumPercentageChangeFromBaseline(0.0).baseline(true).lesionCountAtVisit(2).bestPercentageChange(false)
            .lesionNumber("1").lesionDate(visit1Date).build(), subject1);
    private TargetLesion tl2 = new TargetLesion(TargetLesionRaw.builder().id("tl2Id").subjectId(subject2.getId())
            .visitNumber(1).visitDate(visit2Date).lesionDiameter(5).lesionsDiameterPerAssessment(15)
            .sumPercentageChangeFromBaseline(0.0).baseline(true).lesionCountAtVisit(2).bestPercentageChange(false)
            .lesionNumber("2").lesionDate(visit2Date).build(), subject2);

    private List<TargetLesion> targetLesions = newArrayList(tl1, tl2);

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private TargetLesionDatasetsDataProvider targetLesionDatasetsDataProvider;
    @MockBean
    private CacheableDataProvider cacheableDataProvider;
    @MockBean
    private NonTargetLesionRepository nonTargetLesionRepository;
    @Autowired
    private NonTargetLesionDatasetsDataProvider nonTargetLesionDatasetsDataProvider;

    @Test
    public void testLoadData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);
        when(targetLesionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(targetLesions);
        when(cacheableDataProvider.getData(eq(NonTargetLesionRaw.class), eq(DATASET), any())).thenReturn(nonTargetLesions);

        final Collection<NonTargetLesionRaw> res = nonTargetLesionDatasetsDataProvider.getData(DATASET);
        softly.assertThat(res).extracting(NonTargetLesionRaw::getSubjectId,
                NonTargetLesionRaw::getId,
                NonTargetLesionRaw::getResponse,
                NonTargetLesionRaw::getResponseShort,
                NonTargetLesionRaw::getBaselineDate)
                .containsExactly(
                        tuple("subjectId1", "ntl1Id", "Complete Response", "CR", visit1Date),
                        tuple("subjectId1", "ntl2Id", "Not Evaluable", "Not Evaluable", visit1Date),
                        tuple("subjectId2", "ntl3Id", "Not Evaluated", "Not Evaluated", visit2Date));
    }
}
