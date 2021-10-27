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

import com.acuity.visualisations.rawdatamodel.dataproviders.DiseaseExtentDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DiseaseExtentFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.DiseaseExtentRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DiseaseExtent;
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
import static com.acuity.visualisations.rawdatamodel.util.DateUtils.toDateTime;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class DiseaseExtentServiceTest {

    @Autowired
    private DiseaseExtentService diseaseExtentService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private DiseaseExtentDatasetsDataProvider diseaseExtentDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Subject subject = Subject.builder().subjectId("sid1").firstTreatmentDate(toDate("2015-02-10")).build();

    private static DiseaseExtent de1 = new DiseaseExtent(DiseaseExtentRaw.builder().id("deid1").recurrenceOfEarlierCancer("0")
            .recentProgressionDate(toDateTime("2015-03-01T00:00:00")).siteLocalMetaDisease("local")
            .otherLocAdvSites("site1").localOrMetastaticCancer("both").otherMetastaticSites("site2").build(), subject);
    private static DiseaseExtent de2 = new DiseaseExtent(DiseaseExtentRaw.builder().id("deid2").recurrenceOfEarlierCancer("1")
            .recentProgressionDate(toDateTime("2015-03-04T00:00:00")).siteLocalMetaDisease("meta").otherLocAdvSites("site2")
            .localOrMetastaticCancer("local").otherMetastaticSites("site3").build(), subject);

    public static final List<DiseaseExtent> DISEASE_EXTENTS = newArrayList(de1, de2);

    @Test
    public void testGetSingleSubjectData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(diseaseExtentDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(DISEASE_EXTENTS);

        List<Map<String, String>> singleSubjectData = diseaseExtentService.getSingleSubjectData(DATASETS, "sid1", DiseaseExtentFilters.empty());

        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "recurrenceOfEarlierCancer", "recentProgressionDate", "siteLocalMetaDisease", "otherLocAdvSites",
                "localOrMetastaticCancer", "otherMetastaticSites", "eventId"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("recurrenceOfEarlierCancer"), e -> e.get("recentProgressionDate"), e -> e.get("siteLocalMetaDisease"),
                        e -> e.get("otherLocAdvSites"), e -> e.get("localOrMetastaticCancer"), e -> e.get("otherMetastaticSites"))
                .contains(
                        Tuple.tuple("0", "2015-03-01T00:00:00", "local", "site1", "both", "site2"),
                        Tuple.tuple("1", "2015-03-04T00:00:00", "meta", "site2", "local", "site3")
                );
    }
}
