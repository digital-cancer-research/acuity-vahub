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

import com.acuity.visualisations.rawdatamodel.dataproviders.PathologyDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PathologyFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.PathologyRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Pathology;
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
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NOT_IMPLEMENTED;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDateTime;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class PathologyServiceTest {
    @Autowired
    private PathologyService pathologyService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private PathologyDatasetsDataProvider pathologyDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Subject subject = Subject.builder().subjectId("sid1").subjectCode("sc1").build();

    private static Pathology p1 = new Pathology(PathologyRaw.builder().id("p1").date(toDateTime("2017-02-10T00:00:11"))
            .determMethod("method1").hisType("type1").tumourType("type1").tumourLocation("location1")
            .tumourGrade("grade1").primTumour("T1").nodesStatus("N2").metastasesStatus("M3").stage("IA").build(), subject);

    public static final List<Pathology> PATHOLOGIES = newArrayList(p1);

    @Test
    public void testGetSingleSubjectDataById() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(pathologyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(p1));

        List<Map<String, String>> singleSubjectData = pathologyService.getSingleSubjectData(DATASETS, "sid1", PathologyFilters.empty());

        softly.assertThat(singleSubjectData).hasSize(1);
        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "date", "determMethod", "hisType", "tumourType", "tumourLocation", "tumourGrade", "primTumour", "nodesStatus",
                "metastasesStatus", "stage", "eventId"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("date"), e -> e.get("determMethod"), e -> e.get("hisType"), e -> e.get("tumourType"),
                        e -> e.get("tumourLocation"), e -> e.get("tumourGrade"), e -> e.get("primTumour"), e -> e.get("nodesStatus"),
                        e -> e.get("metastasesStatus"), e -> e.get("stage"))
                .contains(
                        Tuple.tuple("2017-02-10T00:00:11", "method1", "type1", NOT_IMPLEMENTED, "location1", "grade1", "T1", "N2", "M3", "IA")
                );
    }

    @Test
    public void testGetSingleSubjectDataByCode() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(pathologyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(p1));

        List<Map<String, String>> singleSubjectData = pathologyService.getSingleSubjectData(DATASETS, "sc1", PathologyFilters.empty());

        softly.assertThat(singleSubjectData).hasSize(1);
        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "date", "determMethod", "hisType", "tumourType", "tumourLocation", "tumourGrade", "primTumour", "nodesStatus",
                "metastasesStatus", "stage", "eventId"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("date"), e -> e.get("determMethod"), e -> e.get("hisType"), e -> e.get("tumourType"),
                        e -> e.get("tumourLocation"), e -> e.get("tumourGrade"), e -> e.get("primTumour"), e -> e.get("nodesStatus"),
                        e -> e.get("metastasesStatus"), e -> e.get("stage"))
                .contains(
                        Tuple.tuple("2017-02-10T00:00:11", "method1", "type1", NOT_IMPLEMENTED, "location1", "grade1", "T1", "N2", "M3", "IA")
                );
    }
}
