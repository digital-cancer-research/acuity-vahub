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
import com.acuity.visualisations.rawdatamodel.dataproviders.SurvivalStatusDatasesDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.SurvivalStatusFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.SurvivalStatusRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SurvivalStatus;
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

import java.util.ArrayList;
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
public class SurvivalStatusServiceTest {
    @Autowired
    private SurvivalStatusService survivalStatusService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private SurvivalStatusDatasesDataProvider survivalStatusDatasesDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Subject subject = Subject.builder().subjectId("sid1").build();
    private static SurvivalStatus ss1 = new SurvivalStatus(SurvivalStatusRaw.builder().id("ssid1").visitDate(toDateTime("2017-10-10T00:00:00"))
            .survivalStatus("status1").lastAliveDate(toDateTime("2017-10-10T00:00:00")).build(), subject);
    private static SurvivalStatus ss2 = new SurvivalStatus(SurvivalStatusRaw.builder().id("ssid2")
            .survivalStatus("status2").lastAliveDate(toDateTime("2017-10-17T08:00:00")).build(), subject);
    private static SurvivalStatus ss3 = new SurvivalStatus(SurvivalStatusRaw.builder().id("ssid3").visitDate(toDateTime("2017-10-10T00:00:00"))
            .build(), subject);

    public static final List<SurvivalStatus> SURVIVAL_STATUSES = newArrayList(ss1, ss2, ss3);

    @Test
    public void testGetSingleSubjectData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(survivalStatusDatasesDataProvider.loadData(any(Datasets.class))).thenReturn(new ArrayList<>());

        List<Map<String, String>> singleSubjectData = survivalStatusService.getSingleSubjectData(DATASETS, "sid1",
                SurvivalStatusFilters.empty());

        softly.assertThat(singleSubjectData).hasSize(1);
        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "visitDate", "survivalStatus", "lastAliveDate"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("visitDate"), e -> e.get("survivalStatus"), e -> e.get("lastAliveDate"))
                .contains(
//                        Tuple.tuple("2017-10-10T00:00:00", "status1", "2017-10-10T00:00:00"),
//                        Tuple.tuple(null, "status2", "2017-10-17T08:00:00"),
//                        Tuple.tuple("2017-10-10T00:00:00", null, null)
                        Tuple.tuple(NOT_IMPLEMENTED, NOT_IMPLEMENTED, NOT_IMPLEMENTED)
                );
    }

}
