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
import com.acuity.visualisations.rawdatamodel.dataproviders.SecondTimeOfProgressionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.SecondTimeOfProgressionFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.SecondTimeOfProgressionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.SecondTimeOfProgression;
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
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class SecondTimeOfProgressionServiceTest {

    @Autowired
    private SecondTimeOfProgressionService secondTimeOfProgressionService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private SecondTimeOfProgressionDatasetsDataProvider secondTimeOfProgressionDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Subject subject1 = Subject.builder().subjectId("sid1").firstTreatmentDate(toDate("2015-03-02")).build();

    private static SecondTimeOfProgression p1 = new SecondTimeOfProgression(SecondTimeOfProgressionRaw.builder().id("stp1")
            .visitDate(toDateTime("2015-01-20T00:00:00")).assessmentPerformed("ap1").reason("r1")
            .scanDate(toDate("2015-02-01T00:00:00")).investigatorAsmt("i1").progressionType("t1").progressionMeor("t2").other("o1").build(), subject1);

    public static final List<SecondTimeOfProgression> SECOND_TIME_OF_PROGRESSION_EVENTS = newArrayList(p1);

    @Test
    public void testGetPastSingleSubjectData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject1));
        when(secondTimeOfProgressionDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(new ArrayList<>());

        List<Map<String, String>> singleSubjectData = secondTimeOfProgressionService.getSingleSubjectData(DATASETS, "sid1",
                SecondTimeOfProgressionFilters.empty());

        softly.assertThat(singleSubjectData).hasSize(1);
        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "visitDate", "assessmentPerformed", "reason", "scanDate", "scanDate",
                "investigatorAsmt", "progressionType", "progressionMeor", "other"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("visitDate"), e -> e.get("assessmentPerformed"), e -> e.get("reason"), e -> e.get("scanDate"),
                        e -> e.get("investigatorAsmt"), e -> e.get("progressionType"), e -> e.get("progressionMeor"), e -> e.get("other"))
                .contains(
//                        Tuple.tuple("2015-01-20T00:00:00", "ap1", "r1", "2015-02-01T00:00:00", "i1", "t1", "t2", "o1")
                        Tuple.tuple(NOT_IMPLEMENTED, NOT_IMPLEMENTED, NOT_IMPLEMENTED, NOT_IMPLEMENTED,
                                NOT_IMPLEMENTED, NOT_IMPLEMENTED, NOT_IMPLEMENTED, NOT_IMPLEMENTED)
                );
    }
}
