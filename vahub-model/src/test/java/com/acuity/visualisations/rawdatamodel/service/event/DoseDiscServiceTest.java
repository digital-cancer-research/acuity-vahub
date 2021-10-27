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

import com.acuity.visualisations.rawdatamodel.dataproviders.DoseDiscDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DoseDiscFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.DoseDiscRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DoseDisc;
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
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class DoseDiscServiceTest {

    @Autowired
    private DoseDiscService doseDiscService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private DoseDiscDatasetsDataProvider doseDiscDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Subject subject = Subject.builder().subjectId("sid1").firstTreatmentDate(toDate("2015-02-10")).build();

    private static DoseDisc dd1 = new DoseDisc(DoseDiscRaw.builder().id("ddid1").discDate(toDateTime("2015-03-04T23:59:59"))
            .discReason("Adverse Event").ipDiscSpec("due to patients and investigators decision").studyDrug("drug")
            .subjectDecisionSpec("subj dec spec").subjectDecisionSpecOther("subj dec spec other").build(), subject);
    private static DoseDisc dd2 = new DoseDisc(DoseDiscRaw.builder().id("ddid2").discDate(toDateTime("2015-03-04T23:59:58"))
            .discReason("Adverse Event").ipDiscSpec("due to patients and investigators decision").studyDrug("drug")
            .subjectDecisionSpec("subj dec spec").subjectDecisionSpecOther("subj dec spec other").build(), subject);
    private static DoseDisc dd3 = new DoseDisc(DoseDiscRaw.builder().id("ddid3").discDate(toDateTime("2015-03-03T23:59:59"))
            .discReason("Other").ipDiscSpec("no").subjectDecisionSpec("subj dec spec").studyDrug("drug")
            .subjectDecisionSpecOther("subj dec spec other").build(), subject);

    @Test
    public void testGetSingleSubjectData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(doseDiscDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(dd1, dd2, dd3));

        List<Map<String, String>> singleSubjectData = doseDiscService.getSingleSubjectData(DATASETS, "sid1",
                DoseDiscFilters.empty());

        softly.assertThat(singleSubjectData).hasSize(3);
        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "studyDrug", "permanentDisc", "discDate", "durationOnTherapy", "discReason", "eventId"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("studyDrug"), e -> e.get("permanentDisc"), e -> e.get("discDate"), e -> e.get("durationOnTherapy"),
                        e -> e.get("discReason"))
                .containsSequence(
                        Tuple.tuple("drug", NOT_IMPLEMENTED, "2015-03-03T23:59:59", "22", "Other"),
                        Tuple.tuple("drug", NOT_IMPLEMENTED, "2015-03-04T23:59:58", "23", "Adverse Event"),
                        Tuple.tuple("drug", NOT_IMPLEMENTED, "2015-03-04T23:59:59", "23", "Adverse Event")
                );
    }
}
