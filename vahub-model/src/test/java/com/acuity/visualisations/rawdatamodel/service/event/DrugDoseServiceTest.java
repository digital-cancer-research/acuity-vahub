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

import com.acuity.visualisations.rawdatamodel.dataproviders.DrugDoseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.DrugDoseFilters;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.groups.Tuple;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
@SpringBootTest(classes = TestConfig.class)
public class DrugDoseServiceTest {
    @Autowired
    private DrugDoseService drugDoseService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private DrugDoseDatasetsDataProvider drugDoseDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Subject subject = Subject.builder().subjectId("sid1").firstTreatmentDate(toDateTime("2011-10-03T00:00:00")).build();

    private static DrugDose dd1 = new DrugDose(DrugDoseRaw.builder().id("ddid1").drug("drug1").dose(1.0).doseUnit("mg")
            .startDate(toDateTime("2011-10-03T00:00:00")).endDate(toDateTime("2011-10-04T23:59:59"))
            .frequencyName("BID").actionTaken("Dose reduction").reasonForActionTaken("reason1").build(), subject);
    private static DrugDose dd2 = new DrugDose(DrugDoseRaw.builder().id("ddid2").drug("drug1").doseUnit("mg/mL")
            .startDate(toDateTime("2011-10-05T00:00:00")).endDate(toDateTime("2011-11-04T23:59:59"))
            .frequencyName("QD").actionTaken("Dose increased").reasonForActionTaken("reason1").build(), subject);
    private static DrugDose dd3 = new DrugDose(DrugDoseRaw.builder().id("ddid4").drug("drug2").dose(0.0).doseUnit("mg/mL")
            .startDate(toDateTime("2011-10-07T00:00:00")).endDate(toDateTime("2011-10-10T00:00:00"))
            .frequencyName("BID").actionTaken("Dose not changed").reasonForActionTaken("reason2").build(), subject);

    private static final List<DrugDose> DOSES = newArrayList(dd1, dd2, dd3);

    @Test
    public void testGetSingleSubjectData() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(drugDoseDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(DOSES);

        List<Map<String, String>> singleSubjectData = drugDoseService.getSingleSubjectData(DATASETS, "sid1", DrugDoseFilters.empty());

        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "getStudyDay", "startDate", "endDate", "drug", "dose", "doseUnit", "frequencyName", "actionTaken", "reasonForActionTaken", "eventId"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("getStudyDay"), e -> e.get("drug"), e -> e.get("startDate"), e -> e.get("endDate"), e -> e.get("dose"),
                        e -> e.get("doseUnit"), e -> e.get("frequencyName"), e -> e.get("actionTaken"), e -> e.get("reasonForActionTaken"))
                .contains(
                        Tuple.tuple("0", "drug1", "2011-10-03T00:00:00", "2011-10-04T23:59:59", "1", "mg", "BID", "Dose reduction", "reason1"),
                        Tuple.tuple("2", "drug1", "2011-10-05T00:00:00", "2011-11-04T23:59:59", null, "mg/mL", "QD", "Dose increased", "reason1"),
                        Tuple.tuple("4", "drug2", "2011-10-07T00:00:00", "2011-10-10T00:00:00", "0", "mg/mL", "BID", "Dose not changed", "reason2")
                );
    }

}
