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

import com.acuity.visualisations.rawdatamodel.dataproviders.AeIncidenceDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.AeSeverityChangeDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.DrugDoseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverity;
import com.acuity.visualisations.rawdatamodel.vo.AeSeverityRaw;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
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
import java.util.LinkedHashMap;
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
public class DoseLimitingServiceTest {
    @Autowired
    private DoseLimitingService doseLimitingService;
    @MockBean(name = "aeIncidenceDatasetsDataProvider")
    private AeIncidenceDatasetsDataProvider aeIncidenceDatasetsDataProvider;
    @MockBean(name = "aeSeverityChangeDatasetsDataProvider")
    private AeSeverityChangeDatasetsDataProvider aeSeverityChangeDatasetsDataProvider;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private DrugDoseDatasetsDataProvider drugDoseDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Map<String, String> drugs = new LinkedHashMap<>();
    private static Subject subject = Subject.builder().subjectId("sid1").drugsDosed(drugs).build();

    private static AeSeverity as1 = AeSeverity.builder().severityNum(1).webappSeverity("CTC Grade 1").build();
    private static AeSeverity as2 = AeSeverity.builder().severityNum(2).webappSeverity("CTC Grade 2").build();
    private static AeSeverity as3 = AeSeverity.builder().severityNum(3).webappSeverity("CTC Grade 3").build();
    private static Ae ae1 = new Ae(AeRaw.builder()
            .id("aeid1")
            .doseLimitingToxicity("yes")
            .pt("ae1")
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(as1).startDate(toDateTime("2017-10-10T00:00:00"))
                                    .endDate(toDateTime("2017-10-15T00:00:00")).build(),
                            AeSeverityRaw.builder().severity(as2).startDate(toDateTime("2017-10-12T00:00:00"))
                                    .endDate(toDateTime("2017-10-15T00:10:00")).build()
                    )
            ).build(), subject);
    private static Ae ae2 = new Ae(AeRaw.builder()
            .id("aeid2")
            .doseLimitingToxicity("yes")
            .pt("ae2")
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(as1)
                                    .endDate(toDateTime("2017-10-15T00:00:00")).build(),
                            AeSeverityRaw.builder().severity(as2)
                                    .endDate(toDateTime("2017-10-15T00:00:00")).build()
                    )
            ).build(), subject);
    private static Ae ae3 = new Ae(AeRaw.builder().id("aeid3").doseLimitingToxicity("no").pt("ae3").build(), subject);
    private static Ae ae4 = new Ae(AeRaw.builder().id("aeid4").pt("ae4").build(), subject);
    private static Ae ae5 = new Ae(AeRaw.builder()
            .id("aeid5")
            .doseLimitingToxicity("no")
            .pt("ae5")
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(as1).startDate(toDateTime("2017-10-10T00:00:00"))
                                    .endDate(toDateTime("2017-10-15T00:00:00")).build(),
                            AeSeverityRaw.builder().severity(as3).startDate(toDateTime("2017-10-16T00:00:00"))
                                    .endDate(toDateTime("2017-10-15T00:00:00")).build()
                    )
            ).build(), subject);
    private static Ae ae6 = new Ae(AeRaw.builder()
            .id("aeid6")
            .doseLimitingToxicity("yes")
            .pt("ae6")
            .aeSeverities(
                    newArrayList(
                            AeSeverityRaw.builder().severity(as1).startDate(toDateTime("2016-10-10T00:00:02"))
                                    .endDate(toDateTime("2016-12-15T00:00:59")).build(),
                            AeSeverityRaw.builder().severity(as3).startDate(toDateTime("2016-10-10T00:00:01"))
                                    .endDate(toDateTime("2016-12-15T00:00:00")).build()
                    )
            ).build(), subject);

    private static DrugDose dd1 = new DrugDose(DrugDoseRaw.builder().id("dd1").drug("drug1").dose(2.0).doseUnit("mg").periodType("active_dosing")
            .startDate(toDateTime("2017-10-10T00:00:00")).endDate(toDateTime("2017-10-11T00:00:00")).build(), subject);
    private static DrugDose dd2 = new DrugDose(DrugDoseRaw.builder().id("dd2").drug("drug2").dose(2.0).doseUnit("mg").periodType("inactive_dosing")
            .startDate(toDateTime("2017-10-09T00:00:00")).endDate(toDateTime("2017-10-11T00:00:00")).build(), subject);
    private static DrugDose dd3 = new DrugDose(DrugDoseRaw.builder().id("dd3").drug("drug3").dose(3.0).doseUnit("mg").periodType("active_dosing")
            .startDate(toDateTime("2017-10-09T00:00:00")).endDate(toDateTime("2017-10-12T00:00:00")).build(), subject);
    private static DrugDose dd4 = new DrugDose(DrugDoseRaw.builder().id("dd4").drug("drug4").dose(2.0).doseUnit("mg").periodType("active_dosing")
            .startDate(toDateTime("2016-10-09T00:00:00")).endDate(toDateTime("2016-10-11T00:00:00")).build(), subject);
    private static DrugDose dd5 = new DrugDose(DrugDoseRaw.builder().id("dd4").drug("drug5").dose(2.0).doseUnit("mg").periodType("active_dosing")
            .build(), subject);

    private static final List<Ae> AES = newArrayList(ae1, ae2, ae3, ae4, ae5, ae6);
    private static final List<DrugDose> DDS = newArrayList(dd1, dd2, dd3, dd4, dd5);

    @Before
    public void initMocks() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(aeIncidenceDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(AES);
        when(drugDoseDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(DDS);
    }

    @Test
    public void testGetSingleSubjectData() {
        List<Map<String, String>> singleSubjectData = doseLimitingService.getSingleSubjectData(DATASETS, "sid1");

        softly.assertThat(singleSubjectData).hasSize(4);
        softly.assertThat(singleSubjectData).flatExtracting(Map::keySet).containsOnly(
                "study drug", "dose", "dose unit", "ae", "start date", "end date", "dlt", "protocol definition"
        );
        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("study drug"), e -> e.get("dose"), e -> e.get("dose unit"), e -> e.get("ae"),
                        e -> e.get("start date"), e -> e.get("end date"), e -> e.get("dlt"), e -> e.get("protocol definition"))
                .containsSequence(
                        Tuple.tuple("drug4", "2.0", "mg", "ae6", "2016-10-10T00:00:01", "2016-12-15T00:00:59", "yes", NOT_IMPLEMENTED),
                        Tuple.tuple("drug1", "2.0", "mg", "ae1", "2017-10-10T00:00:00", "2017-10-15T00:10:00", "yes", NOT_IMPLEMENTED),
                        Tuple.tuple("drug3", "3.0", "mg", "ae1", "2017-10-10T00:00:00", "2017-10-15T00:10:00", "yes", NOT_IMPLEMENTED),
                        Tuple.tuple("", "", "", "ae2", "", "2017-10-15T00:00:00", "yes", NOT_IMPLEMENTED)
                );
    }
}
