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
import com.acuity.visualisations.rawdatamodel.vo.DiseaseExtentRaw;
import com.acuity.visualisations.rawdatamodel.vo.PathologyRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DiseaseExtent;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Pathology;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SubjectExt;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class SubjectExtDatasetsDataProviderTest {

    private Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1").firstTreatmentDate(toDate("2000-04-05"))
            .lastTreatmentDate(toDate("2000-05-20")).build();
    private Subject subject2 = Subject.builder().subjectId("subjectId2").subjectCode("subject2").firstTreatmentDate(toDate("2000-01-10"))
            .lastTreatmentDate(toDate("2000-05-21")).build();
    private Subject subject3 = Subject.builder().subjectId("subjectId3").subjectCode("subject3").firstTreatmentDate(toDate("2000-02-21"))
            .lastTreatmentDate(toDate("2000-05-21")).build();
    private List<Subject> population = Arrays.asList(subject1, subject2, subject3);

    private List<DiseaseExtent> diseaseExtents = newArrayList(new DiseaseExtent(DiseaseExtentRaw.builder()
                    .recentProgressionDate(toDate("2000-03-01")).build(), subject1),
            new DiseaseExtent(DiseaseExtentRaw.builder().recentProgressionDate(toDate("2000-03-10")).build(), subject1),
            new DiseaseExtent(DiseaseExtentRaw.builder().build(), subject1),
            new DiseaseExtent(DiseaseExtentRaw.builder().recentProgressionDate(toDate("2000-01-01")).build(), subject2),
            // after first treatment date, must not be considered
            new DiseaseExtent(DiseaseExtentRaw.builder().recentProgressionDate(toDate("2000-04-01")).build(), subject3));

    private List<Pathology> pathologies = newArrayList(new Pathology(PathologyRaw.builder()
                    .date(toDate("2000-03-05")).build(), subject1),
            new Pathology(PathologyRaw.builder().date(toDate("2000-03-20")).build(), subject1),
            new Pathology(PathologyRaw.builder().build(), subject1),
            new Pathology(PathologyRaw.builder().date(toDate("2000-01-05")).build(), subject2),
            // after first treatment date, must not be considered
            new Pathology(PathologyRaw.builder().date(toDate("2000-04-10")).build(), subject3));

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private DiseaseExtentDatasetsDataProvider diseaseExtentDatasetsDataProvider;
    @MockBean
    private PathologyDatasetsDataProvider pathologyDatasetsDataProvider;
    @Autowired
    private SubjectExtDatasetsDataProvider subjectExtDatasetsDataProvider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetData() throws Exception {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);
        when(diseaseExtentDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(diseaseExtents);
        when(pathologyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(pathologies);
        final Collection<SubjectExt> res = subjectExtDatasetsDataProvider.loadData(DUMMY_ACUITY_DATASETS);
        softly.assertThat(res).extracting(SubjectExt::getSubjectId,
                s -> s.getEvent().getRecentProgressionDate(),
                s -> s.getEvent().getDiagnosisDate(),
                s -> s.getEvent().getDaysFromDiagnosisDate())
                .containsExactly(
                        tuple("subjectId1", toDate("2000-03-10"), toDate("2000-03-05"), 31),
                        tuple("subjectId2", toDate("2000-01-01"), toDate("2000-01-05"), 5),
                        tuple("subjectId3", null, null, null));
    }
}
