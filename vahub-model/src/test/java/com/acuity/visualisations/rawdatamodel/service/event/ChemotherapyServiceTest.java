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

import com.acuity.visualisations.rawdatamodel.dataproviders.ChemotherapyDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.ChemotherapyFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.ChemotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;
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
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDateTime;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class ChemotherapyServiceTest {

    @Autowired
    private PastChemotherapyService pastChemotherapyService;
    @Autowired
    private PostChemotherapyService postChemotherapyService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private ChemotherapyDatasetsDataProvider chemotherapyDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Subject subject = Subject.builder().subjectId("sid1").firstTreatmentDate(toDateTime("2015-03-05T00:00:00")).build();

    private static Chemotherapy ch1 = new Chemotherapy(ChemotherapyRaw.builder().id("chid1").startDate(toDateTime("2015-01-20T00:00:01"))
            .endDate(toDateTime("2015-03-01T00:00:00")).timeStatus("previous").therapyReason("reason1").agent("agent1").therapyClass("class1")
            .treatmentStatus("status2").numOfCycles(1).route("route1").bestResponse("response1").failureReason("reason2").build(), subject);
    private static Chemotherapy ch2 = new Chemotherapy(ChemotherapyRaw.builder().id("chid2").startDate(toDateTime("2015-01-21T00:00:01"))
            .timeStatus("previous").therapyReason("reason1").agent("agent2").therapyClass("class2").treatmentStatus("status2").numOfCycles(2)
            .route("route1").bestResponse("response1").failureReason("reason2").build(), subject);
    private static Chemotherapy ch3 = new Chemotherapy(ChemotherapyRaw.builder().id("chid3").startDate(toDateTime("2015-01-15T00:00:01"))
            .endDate(toDateTime("2015-03-05T00:00:01")).timeStatus("current").therapyReason("reason1").agent("agent1").therapyClass("class1")
            .treatmentStatus("status2").numOfCycles(3).route("route1").bestResponse("response1").failureReason("reason2").build(), subject);
    private static Chemotherapy ch4 = new Chemotherapy(ChemotherapyRaw.builder().id("chid4").startDate(toDateTime("2015-01-20T00:00:01"))
            .endDate(toDateTime("2015-03-05T00:00:01")).timeStatus("Post").therapyReason("reason4").agent("agent4").therapyClass("class4")
            .treatmentStatus("status4").numOfCycles(4).route("route4").bestResponse("response4").failureReason("reason4").build(), subject);
    private static Chemotherapy ch5 = new Chemotherapy(ChemotherapyRaw.builder().id("chid5").timeStatus("post").therapyReason("reason5")
            .agent("agent5").therapyClass("class5").treatmentStatus("status5").numOfCycles(5).route("route5").bestResponse("response5")
            .failureReason("reason5").build(), subject);

    public static final List<Chemotherapy> CHEMOTHERAPIES = newArrayList(ch1, ch2, ch3, ch4, ch5);

    @Before
    public void initMocks() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(chemotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(CHEMOTHERAPIES);
    }

    @Test
    public void testGetPastSingleSubjectData() {

        List<Map<String, String>> singleSubjectData = pastChemotherapyService.getSingleSubjectData(DATASETS, "sid1", ChemotherapyFilters.empty());

        softly.assertThat(singleSubjectData).hasSize(2);
        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "startDate", "endDate", "therapyReason", "agent", "therapyClass", "treatmentStatus",
                "numOfCycles", "route", "bestResponse", "failureReason", "eventId"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("startDate"), e -> e.get("endDate"), e -> e.get("therapyReason"), e -> e.get("agent"),
                        e -> e.get("therapyClass"), e -> e.get("treatmentStatus"), e -> e.get("numOfCycles"), e -> e.get("route"),
                        e -> e.get("bestResponse"), e -> e.get("failureReason"))
                .contains(
                        Tuple.tuple("2015-01-20T00:00:01", "2015-03-01T00:00:00", "reason1", "agent1", "class1",
                                "status2", "1", "route1", "response1", "reason2"),
                        Tuple.tuple("2015-01-21T00:00:01", null, "reason1", "agent2", "class2", "status2", "2",
                                "route1", "response1", "reason2")
                );
    }

    @Test
    public void testGetCurrentSingleSubjectData() {

        List<Map<String, String>> singleSubjectData = postChemotherapyService.getSingleSubjectData(DATASETS, "sid1", ChemotherapyFilters.empty());

        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "startDate", "endDate", "therapyReason", "agent", "therapyClass", "treatmentStatus",
                "numOfCycles", "route", "bestResponse", "failureReason", "eventId"
        );

        softly.assertThat(singleSubjectData).hasSize(2);
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("startDate"), e -> e.get("endDate"), e -> e.get("therapyReason"), e -> e.get("agent"),
                        e -> e.get("therapyClass"), e -> e.get("treatmentStatus"), e -> e.get("numOfCycles"), e -> e.get("route"),
                        e -> e.get("bestResponse"), e -> e.get("failureReason"))
                .contains(
                        Tuple.tuple("2015-01-20T00:00:01", "2015-03-05T00:00:01", "reason4", "agent4", "class4",
                                "status4", "4", "route4", "response4", "reason4"),
                        Tuple.tuple(null, null, "reason5", "agent5", "class5", "status5", "5", "route5", "response5", "reason5")
                );
    }
}
