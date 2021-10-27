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

import com.acuity.visualisations.rawdatamodel.dataproviders.AssessedTargetLesionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.generators.AssessedTargetLesionGenerator;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.plots.SelectionDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.AssessedTargetLesion;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class AssessedTargetLesionServiceTest {
    private List<AssessedTargetLesion> tumours = AssessedTargetLesionGenerator.generateTumours();
    private List<Subject> population = AssessedTargetLesionGenerator.generateTumourPopulation();

    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private AssessedTargetLesionDatasetsDataProvider tumourDatasetsDataProvider;

    @Autowired
    private TumourLineChartService tumourService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void getSelectionBySubjectIds() {
        when(tumourDatasetsDataProvider.loadDataByVisit(any(Datasets.class))).thenReturn(tumours);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(population);
        Set<String> subjectIds = new HashSet<>();
        subjectIds.add("subjectId1");
        subjectIds.add("subjectId2");
        final SelectionDetail selectionDetail = tumourService.getSelectionBySubjectIds(DATASETS, subjectIds);
        softly.assertThat(selectionDetail.getTotalEvents()).isEqualTo(9);
        softly.assertThat(selectionDetail.getEventIds().size()).isEqualTo(5);
        softly.assertThat(selectionDetail.getEventIds()).containsOnly("id1", "id3", "id4", "id5", "id6");
        softly.assertThat(selectionDetail.getSubjectIds().size()).isEqualTo(2);
        softly.assertThat(selectionDetail.getTotalSubjects()).isEqualTo(4);
    }
}
