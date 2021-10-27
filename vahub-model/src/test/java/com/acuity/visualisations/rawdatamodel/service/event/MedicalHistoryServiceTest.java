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

import com.acuity.visualisations.rawdatamodel.dataproviders.MedicalHistoryDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.MedicalHistoryFilters;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.service.dod.SsvCommonService;
import com.acuity.visualisations.rawdatamodel.service.filters.MedicalHistoryFilterService;
import com.acuity.visualisations.rawdatamodel.service.filters.PopulationRawDataFilterService;
import com.acuity.visualisations.rawdatamodel.vo.FilterResult;
import com.acuity.visualisations.rawdatamodel.vo.MedicalHistoryRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.MedicalHistory;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.assertj.core.groups.Tuple;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class MedicalHistoryServiceTest {

    @InjectMocks
    private PastMedicalHistoryService pastMedicalHistoryService;
    @InjectMocks
    private CurrentMedicalHistoryService currentMedicalHistoryService;

    @Mock
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @Mock
    private MedicalHistoryDatasetsDataProvider medicalHistoryDatasetsDataProvider;
    @Spy
    private List<MedicalHistoryDatasetsDataProvider> eventDataProviders = new ArrayList<>();
    @Mock
    private MedicalHistoryFilterService medicalHistoryFilterService;
    @Spy
    private PopulationRawDataFilterService populationRawDataFilterService;
    @Spy
    private SsvCommonService ssvCommonService;
    @Spy
    private DoDCommonService doDCommonService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private static Subject subject = Subject.builder().subjectId("sid1").firstTreatmentDate(toDate("2015-05-01")).build();


    // concurrent conditional status, end date after first treatment date
    private static MedicalHistory mh1 = new MedicalHistory(MedicalHistoryRaw.builder().id("mhid1")
            .conditionStatus("Yes").preferredTerm("t1")
            .term("term1").start(toDate("2015-01-20")).end(toDate("2015-06-01")).build(), subject);
    // past conditional status, end date before first treatment date
    private static MedicalHistory mh2 = new MedicalHistory(MedicalHistoryRaw.builder().id("mhid2")
            .conditionStatus("No").preferredTerm("t2")
            .term("term2").start(toDate("2015-02-10")).build(), subject);
    // concurrent conditional status, end date after first treatment date
    private static MedicalHistory mh3 = new MedicalHistory(MedicalHistoryRaw.builder().id("mhid3")
            .conditionStatus("Current").preferredTerm("t3")
            .term("term3").start(toDate("2015-03-01")).end(toDate("2015-06-12")).build(), subject);
    // past conditional status, end date before first treatment date
    private static MedicalHistory mh4 = new MedicalHistory(MedicalHistoryRaw.builder().id("mhid4")
            .conditionStatus("Past").preferredTerm("t4")
            .term("term4").start(toDate("2015-01-05")).end(toDate("2015-03-01")).build(), subject);
    // unknown conditional status, end date before first treatment date
    private static MedicalHistory mh5 = new MedicalHistory(MedicalHistoryRaw.builder().id("mhid5")
            .conditionStatus("Other").preferredTerm("t5")
            .term("term5").start(toDate("2015-01-06")).end(toDate("2015-02-12")).build(), subject);
    // empty conditional status, end date equal to first treatment date
    private static MedicalHistory mh6 = new MedicalHistory(MedicalHistoryRaw.builder().id("mhid6").preferredTerm("t6")
            .term("term6").start(toDate("2015-01-20")).end(toDate("2015-05-01")).build(), subject);
    // empty conditional status, end date before first treatment date
    private static MedicalHistory mh7 = new MedicalHistory(MedicalHistoryRaw.builder().id("mhid7").preferredTerm("t7")
            .term("term7").start(toDate("2015-01-20")).end(toDate("2015-04-30")).build(), subject);
    // empty conditional status, end date after first treatment date
    private static MedicalHistory mh8 = new MedicalHistory(MedicalHistoryRaw.builder().id("mhid8").preferredTerm("t8")
            .term("term8").start(toDate("2015-01-20")).end(toDate("2015-05-02")).build(), subject);
    // empty conditional status, empty end date
    private static MedicalHistory mh9 = new MedicalHistory(MedicalHistoryRaw.builder().id("mhid9").preferredTerm("t9")
            .term("term9").start(toDate("2015-01-20")).build(), subject);
    // concurrent conditional status, end date before first treatment date
    private static MedicalHistory mh10 = new MedicalHistory(MedicalHistoryRaw.builder().id("mhid10").preferredTerm("t10")
            .conditionStatus("Yes")
            .term("term10").start(toDate("2015-01-20")).end(toDate("2015-02-02")).build(), subject);
    // empty conditional status, empty dates
    private static MedicalHistory mh11 = new MedicalHistory(MedicalHistoryRaw.builder().id("mhid11").build(), subject);

    public static final List<MedicalHistory> MEDICAL_HISTORIES = newArrayList(mh1, mh2, mh3, mh4, mh5, mh6, mh7, mh8, mh9, mh10);

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singleton(subject));
        when(medicalHistoryFilterService.query(any()))
                .thenReturn(new FilterResult<MedicalHistory>(null).withResults(MEDICAL_HISTORIES, MEDICAL_HISTORIES));
        eventDataProviders.add(medicalHistoryDatasetsDataProvider);
    }

    @Test
    public void testGetPastSingleSubjectData() {

        when(medicalHistoryFilterService.query(any()))
                .thenReturn(new FilterResult<MedicalHistory>(null).withResults(MEDICAL_HISTORIES, MEDICAL_HISTORIES));

        List<Map<String, String>> singleSubjectData = pastMedicalHistoryService.getSingleSubjectData(DATASETS, "sid1");

        singleSubjectData.forEach(row -> softly.assertThat(row.keySet()).containsExactlyInAnyOrder(
                "start", "end", "preferredTerm", "term", "eventId"
        ));

        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("start"), e -> e.get("end"), e -> e.get("preferredTerm"), e -> e.get("term"))
                .containsExactlyInAnyOrder(
                        Tuple.tuple("2015-02-10", null, "t2", "term2"),
                        Tuple.tuple("2015-01-05", "2015-03-01", "t4", "term4"),
                        Tuple.tuple("2015-01-06", "2015-02-12", "t5", "term5"),
                        Tuple.tuple("2015-01-20", "2015-04-30", "t7", "term7")
                );
    }

    @Test
    public void testGetCurrentSingleSubjectData() {

        when(medicalHistoryFilterService.query(any()))
                .thenReturn(new FilterResult<MedicalHistory>(null).withResults(MEDICAL_HISTORIES, MEDICAL_HISTORIES));

        List<Map<String, String>> singleSubjectData = currentMedicalHistoryService.getSingleSubjectData(DATASETS, "sid1");

        singleSubjectData.forEach(row -> softly.assertThat(row.keySet()).containsExactlyInAnyOrder(
                "start", "end", "preferredTerm", "term", "eventId"
        ));

        softly.assertThat(singleSubjectData)
                .extracting(e -> e.get("start"), e -> e.get("end"), e -> e.get("preferredTerm"), e -> e.get("term"))
                .containsExactlyInAnyOrder(
                        Tuple.tuple("2015-01-20", "2015-06-01", "t1", "term1"),
                        Tuple.tuple("2015-03-01", "2015-06-12", "t3", "term3"),
                        Tuple.tuple("2015-01-20", "2015-05-01", "t6", "term6"),
                        Tuple.tuple("2015-01-20", "2015-05-02", "t8", "term8"),
                        Tuple.tuple("2015-01-20", null, "t9", "term9"),
                        Tuple.tuple("2015-01-20", "2015-02-02", "t10", "term10")
                );
    }

    @Test
    public void testGetDetailsOnDemandData() {
        when(medicalHistoryFilterService.query(any()))
                .thenReturn(new FilterResult<MedicalHistory>(null).withResults(newArrayList(mh1, mh2, mh11), newArrayList(mh1, mh2, mh11)));

        List<Map<String, String>> detailsOnDemandData = currentMedicalHistoryService.getDetailsOnDemandData(DATASETS,
                "sid1", MedicalHistoryFilters.empty());

        softly.assertThat(detailsOnDemandData).flatExtracting(Map::keySet).containsOnly(
                "eventId", "soc", "start", "subjectId", "studyPart", "hlt", "conditionStatus", "preferredTerm", "term",
                "end", "studyId", "currentMedication", "category"
        );
        softly.assertThat(detailsOnDemandData)
                .extracting(e -> e.get("eventId"), e -> e.get("soc"), e -> e.get("start"), e -> e.get("subjectId"), e -> e.get("studyPart"),
                        e -> e.get("hlt"), e -> e.get("conditionStatus"), e -> e.get("preferredTerm"), e -> e.get("term"),
                        e -> e.get("end"), e -> e.get("studyId"), e -> e.get("currentMedication"), e -> e.get("category"))
                .containsExactlyInAnyOrder(
                        Tuple.tuple("mhid1", null, "2015-01-20T00:00:00", null, null, null, "Yes", "t1", "term1", "2015-06-01T00:00:00",
                                null, null, null),
                        Tuple.tuple("mhid2", null, "2015-02-10T00:00:00", null, null, null, "No", "t2", "term2", null,
                                null, null, null)
                );
    }
}
