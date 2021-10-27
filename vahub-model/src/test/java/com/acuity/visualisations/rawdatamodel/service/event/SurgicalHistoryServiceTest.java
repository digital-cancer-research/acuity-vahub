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

import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.SurgicalHistoryDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.SurgicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.test.annotation.SpringITTest;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.SurgicalHistoryRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurgicalHistory;
import com.acuity.va.security.acl.domain.Datasets;
import com.google.common.collect.ImmutableList;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.groups.Tuple;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDateTime;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringITTest
public class SurgicalHistoryServiceTest {
    @Autowired
    private SurgicalHistoryService surgicalHistoryService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private SurgicalHistoryDatasetsDataProvider surgicalDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Subject subject = Subject.builder().subjectId("sid1").studyPart("part").clinicalStudyCode("code").build();
    private static SurgicalHistory sh1 = new SurgicalHistory(SurgicalHistoryRaw.builder().id("shid1").preferredTerm("t1")
            .surgicalProcedure("procedure1").start(toDateTime("2015-01-20T08:00:00")).build(), subject);
    private static SurgicalHistory sh2 = new SurgicalHistory(SurgicalHistoryRaw.builder().id("shid2").build(), subject);
    private static SurgicalHistory sh3 = new SurgicalHistory(SurgicalHistoryRaw.builder().id("shid3").preferredTerm("t2").build(), subject);

    static final List<SurgicalHistory> SURGICAL_HISTORIES = ImmutableList.of(sh1);

    @Test
    public void testGetSingleSubjectData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(surgicalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(sh1));

        List<Map<String, String>> singleSubjectData = surgicalHistoryService.getSingleSubjectData(DATASETS, "sid1", SurgicalHistoryFilters.empty());

        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "preferredTerm", "start", "surgicalProcedure", "eventId"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("preferredTerm"), e -> e.get("start"), e -> e.get("surgicalProcedure"))
                .contains(
                        Tuple.tuple("t1", "2015-01-20T08:00:00", "procedure1")
                );
    }

    @Test
    public void testGetDetailsOnDemandData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(surgicalDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(sh1, sh2, sh3));

        List<Map<String, String>> detailsOnDemandData = surgicalHistoryService.getDetailsOnDemandData(DATASETS, "sid1", SurgicalHistoryFilters.empty());

        softly.assertThat(detailsOnDemandData).flatExtracting(Map::keySet).containsOnly(
                "hlt", "eventId", "soc", "preferredTerm", "start", "studyId", "currentMedication", "surgicalProcedure",
                "studyPart", "subjectId"
        );
        softly.assertThat(detailsOnDemandData)
                .hasSize(2)
                .extracting(e -> e.get("hlt"), e -> e.get("eventId"), e -> e.get("soc"), e -> e.get("preferredTerm"),
                        e -> e.get("start"), e -> e.get("studyId"), e -> e.get("currentMedication"), e -> e.get("surgicalProcedure"),
                        e -> e.get("studyPart"), e -> e.get("subjectId"))
                .containsOnly(
                        Tuple.tuple(null, "shid1", null, "t1", "2015-01-20T08:00:00", "code", null, "procedure1", "part", null),
                        Tuple.tuple(null, "shid3", null, "t2", null, "code", null, null, "part", null)
                );
    }
}
