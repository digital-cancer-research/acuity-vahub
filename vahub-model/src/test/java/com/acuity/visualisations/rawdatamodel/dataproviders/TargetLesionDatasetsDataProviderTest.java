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
import com.acuity.visualisations.rawdatamodel.dao.TargetLesionRepository;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
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
import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class TargetLesionDatasetsDataProviderTest {

    private Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1").firstTreatmentDate(toDate("2000-04-05"))
            .baselineDate(toDate("2000-04-06")).build();
    private Subject subject2 = Subject.builder().subjectId("subjectId2").subjectCode("subject2").firstTreatmentDate(toDate("2000-01-10"))
            .baselineDate(toDate("2000-01-09")).build();

    private List<Subject> population = Arrays.asList(subject1, subject2);

    private Date visit1Date =  DaysUtil.toDate("2000-05-15");
    private Date visit2Date =  DaysUtil.toDate("2000-05-25");
    private Date visit3Date =  DaysUtil.toDate("2000-05-30");

    private TargetLesionRaw tls1v1l1 = TargetLesionRaw.builder().id("tls1v1l1").subjectId(subject1.getId())
            .visitNumber(1).visitDate(visit1Date).lesionDiameter(10)
            .lesionNumber("1").lesionDate(visit1Date).build();
    private TargetLesionRaw tls1v1l2 = TargetLesionRaw.builder().id("tls1v1l2").subjectId(subject1.getId())
            .visitNumber(1).visitDate(visit1Date).lesionDiameter(5)
            .lesionNumber("2").lesionDate(visit1Date).build();

    private TargetLesionRaw tls1v2l1 = TargetLesionRaw.builder().id("tls1v2l1").subjectId(subject1.getId())
            .visitNumber(2).visitDate(visit2Date).lesionDiameter(8)
            .lesionNumber("1").lesionDate(visit2Date).build();
    private TargetLesionRaw tls1v2l2 = TargetLesionRaw.builder().id("tls1v2l2").subjectId(subject1.getId())
            .visitNumber(2).visitDate(visit2Date).lesionDiameter(4)
            .lesionNumber("2").lesionDate(visit2Date).build();

    private TargetLesionRaw tls1v3l1 = TargetLesionRaw.builder().id("tls1v3l1").subjectId(subject1.getId())
            .visitNumber(3).visitDate(visit3Date).lesionDiameter(7)
            .lesionNumber("1").lesionDate(visit3Date).build();
    private TargetLesionRaw tls1v3l2 = TargetLesionRaw.builder().id("tls1v3l2").subjectId(subject1.getId())
            .visitNumber(3).visitDate(visit3Date).lesionDiameter(4)
            .lesionNumber("2").lesionDate(visit3Date).build();

    private TargetLesionRaw tls2 = TargetLesionRaw.builder().id("tls2").subjectId(subject2.getId())
            .visitNumber(1).visitDate(visit1Date).lesionDiameter(18)
            .lesionNumber("1").lesionDate(visit1Date).build();

    private List<TargetLesionRaw> targetLesions = newArrayList(tls1v1l1, tls1v1l2, tls1v2l1, tls1v2l2,
            tls1v3l1, tls1v3l2, tls2);

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Autowired
    private TargetLesionDatasetsDataProvider targetLesionDatasetsDataProvider;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private CacheableDataProvider cacheableDataProvider;
    @MockBean
    private TargetLesionRepository targetLesionRepository;

    @Test
    public void testLoadData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);
        when(cacheableDataProvider.getData(eq(TargetLesionRaw.class), eq(DATASET), any())).thenReturn(targetLesions);

        final Collection<TargetLesion> res = targetLesionDatasetsDataProvider.loadData(DATASETS);
        softly.assertThat(res).extracting(TargetLesion::getSubjectId,
                TargetLesion::getId,
                tl -> tl.getEvent().getLesionDiameter(),
                tl -> tl.getEvent().getBaselineLesionDiameter(),
                tl -> tl.getEvent().getLesionsDiameterPerAssessment(),
                tl -> tl.getEvent().getLesionPercentageChangeFromBaseline(),
                tl -> tl.getEvent().getSumPercentageChangeFromBaseline(),
                tl -> tl.getEvent().getSumPercentageChangeFromMinimum(),
                tl -> tl.getEvent().getSumBestPercentageChangeFromBaseline(),
                tl -> tl.getEvent().getSumBaselineDiameter(),
                tl -> tl.getEvent().getLesionCountAtVisit(),
                tl -> tl.getEvent().getLesionCountAtBaseline(),
                tl -> tl.getEvent().isBaseline(),
                tl -> tl.getEvent().isBestPercentageChange())
                .containsExactly(
                        tuple("subjectId1", "tls1v1l1", 10, 10, 15, 0., 0., 0., -26.67, 15, 2, 2, true, false),
                        tuple("subjectId1", "tls1v1l2", 5, 5, 15, 0.0, 0.0, 0.0, -26.67, 15, 2, 2, true, false),
                        tuple("subjectId1", "tls1v2l1", 8, 10, 12, -20., -20., -20., -26.67, 15, 2, 2, false, false),
                        tuple("subjectId1", "tls1v2l2", 4, 5, 12, -20., -20., -20., -26.67, 15, 2, 2, false, false),
                        tuple("subjectId1", "tls1v3l1", 7, 10, 11, -30., -26.67, -8.33, -26.67, 15, 2, 2, false, true),
                        tuple("subjectId1", "tls1v3l2", 4, 5, 11, -20., -26.67, -8.33, -26.67, 15, 2, 2, false, true),
                        tuple("subjectId2", "tls2", 18, 18, 18, 0., 0., 0., null, 18, 1, 1, true, false));
    }
}
