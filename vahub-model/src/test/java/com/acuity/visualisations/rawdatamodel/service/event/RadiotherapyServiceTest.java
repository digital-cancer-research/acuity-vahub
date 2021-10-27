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
import com.acuity.visualisations.rawdatamodel.dataproviders.RadiotherapyDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.RadiotherapyFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.RadiotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;
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
public class RadiotherapyServiceTest {

    @Autowired
    private RadiotherapyService radiotherapyService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private RadiotherapyDatasetsDataProvider radiotherapyDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Subject subject1 = Subject.builder().subjectId("sid1").firstTreatmentDate(toDateTime("2015-03-02T00:00:00")).build();

    private static Radiotherapy r1 = new Radiotherapy(RadiotherapyRaw.builder().id("rid1").startDate(toDateTime("2015-01-20T00:00:00"))
            .endDate(toDateTime("2015-02-01T00:00:00")).siteOrRegion("site1").dose(5.0).numOfDoses(2).timeStatus("Previous")
            .treatmentStatus("first").concomitantChemoRadiotherapy("yes").build(), subject1);
    private static Radiotherapy r2 = new Radiotherapy(RadiotherapyRaw.builder().id("rid2").startDate(toDateTime("2015-01-09T00:00:00"))
            .endDate(toDateTime("2015-03-12T00:00:00")).siteOrRegion("site1").numOfDoses(2).timeStatus("Previous").treatmentStatus("first")
            .concomitantChemoRadiotherapy("no").build(), subject1);
    private static Radiotherapy r3 = new Radiotherapy(RadiotherapyRaw.builder().id("rid2").startDate(toDateTime("2015-01-09T00:00:00"))
            .endDate(toDateTime("2015-03-12T00:00:00")).siteOrRegion("region1").dose(2.0).timeStatus("Previous").treatmentStatus("second")
            .concomitantChemoRadiotherapy("no").build(), subject1);
    private static Radiotherapy r4 = new Radiotherapy(RadiotherapyRaw.builder().id("rid3").startDate(toDateTime("2015-01-09T00:00:00"))
            .siteOrRegion("site1").timeStatus("Previous").treatmentStatus("second")
            .concomitantChemoRadiotherapy("yes").build(), subject1);
    private static Radiotherapy r5 = new Radiotherapy(RadiotherapyRaw.builder().id("rid4").startDate(toDateTime("2015-01-09T00:00:00"))
            .siteOrRegion("site1").dose(4.0).numOfDoses(4).timeStatus("second").treatmentStatus("status5").concomitantChemoRadiotherapy("no")
            .build(), subject1);

    public static final List<Radiotherapy> RADIOTHERAPIES = newArrayList(r1, r2, r3, r4, r5);

    @Test
    public void testGetPastSingleSubjectData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject1));
        when(radiotherapyDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(RADIOTHERAPIES);

        List<Map<String, String>> singleSubjectData = radiotherapyService.getSingleSubjectData(DATASETS, "sid1", RadiotherapyFilters.empty());

        softly.assertThat(singleSubjectData).hasSize(4);
        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "startDate", "endDate", "siteOrRegion", "dose", "numOfDoses", "getTotalGrays",
                "treatmentStatus", "concomitantChemoRadiotherapy", "eventId"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("startDate"), e -> e.get("endDate"), e -> e.get("siteOrRegion"), e -> e.get("dose"),
                        e -> e.get("numOfDoses"), e -> e.get("getTotalGrays"), e -> e.get("treatmentStatus"), e -> e.get("concomitantChemoRadiotherapy"))
                .contains(
                        Tuple.tuple("2015-01-20T00:00:00", "2015-02-01T00:00:00", "site1", "5", "2", "10.0", "first", NOT_IMPLEMENTED),
                        Tuple.tuple("2015-01-09T00:00:00", "2015-03-12T00:00:00", "site1", null, "2", "", "first", NOT_IMPLEMENTED),
                        Tuple.tuple("2015-01-09T00:00:00", "2015-03-12T00:00:00", "region1", "2", null, "", "second", NOT_IMPLEMENTED),
                        Tuple.tuple("2015-01-09T00:00:00", null, "site1", null, null, "", "second", NOT_IMPLEMENTED)
                );
    }
}
