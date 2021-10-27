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

package com.acuity.visualisations.rawdatamodel.service.timeline;

import com.acuity.visualisations.rawdatamodel.dataproviders.PatientDataDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PatientDataFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.patient.PatientDataEvent;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.patient.PatientDataEventDetails;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.patient.PatientDataTests;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.patient.PatientDataTestsDetails;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.patient.SubjectPatientDataDetail;
import com.acuity.visualisations.rawdatamodel.service.timeline.data.patient.SubjectPatientDataSummary;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.GroupByOption;
import com.acuity.visualisations.rawdatamodel.vo.PatientDataRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PatientData;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDate;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param.DRUG_NAME;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.Param.TIMESTAMP_TYPE;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.TimestampType.DAYS_HOURS_SINCE_FIRST_DOSE;
import static com.acuity.visualisations.rawdatamodel.vo.GroupByOption.TimestampType.DAYS_HOURS_SINCE_FIRST_DOSE_OF_DRUG;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class TimelinePatientDataServiceTest {

    @Autowired
    private TimelinePatientDataService patientDataService;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private PatientDataDatasetsDataProvider patientDataDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    private HashMap<String, Date> drugs1 = new HashMap<>();
    {
        drugs1.put("Drug1", toDate("2000-01-10"));
        drugs1.put("Drug2", toDate("2000-01-15 18:00"));
    }

    private HashMap<String, Date> drugs2 = new HashMap<>();
    {
        drugs2.put("Drug1", toDate("2000-03-05 10:20"));
        drugs2.put("Drug2", toDate("2000-03-01 15:00"));
    }

    private Subject subject1 = Subject.builder().subjectId("subjectId1").subjectCode("subject1")
            .firstTreatmentDate(toDate("2000-01-10")).drugFirstDoseDate(drugs1)
            .dateOfRandomisation(toDate("2000-01-20")).build();
    private Subject subject2 = Subject.builder().subjectId("subjectId2").subjectCode("subject2")
            .firstTreatmentDate(toDate("2000-03-01 15:00")).drugFirstDoseDate(drugs2)
            .dateOfRandomisation(toDate("2000-01-20")).build();
    private List<Subject> subjects = Arrays.asList(subject2, subject1);

    private PatientData patientData1 = new PatientData(PatientDataRaw.builder()
            .id("pr1")
            .subjectId("subjectId1")
            .measurementName("activity")
            .unit("step")
            .value(1000.0)
            .measurementDate(toDate("2000-01-20 20:15"))
            .build(), subject1);
    private PatientData patientData2 = new PatientData(PatientDataRaw.builder()
            .id("pr2")
            .subjectId("subjectId2")
            .measurementName("pulse")
            .unit("heartbeat")
            .value(70.0)
            .measurementDate(toDate("2000-03-03 12:15"))
            .build(), subject2);
    private PatientData patientData3 = new PatientData(PatientDataRaw.builder()
            .id("pr3")
            .subjectId("subjectId2")
            .measurementName("activity")
            .unit("step")
            .value(500.0)
            .measurementDate(toDate("2000-03-03 18:00"))
            .build(), subject2);
    private PatientData patientData4 = new PatientData(PatientDataRaw.builder()
            .id("pr4")
            .subjectId("subjectId1")
            .measurementName("activity")
            .unit("step")
            .value(8000.0)
            .measurementDate(toDate("2000-05-10 00:00"))
            .build(), subject1);
    private PatientData patientData5 = new PatientData(PatientDataRaw.builder()
            .id("pr5")
            .subjectId("subjectId2")
            .measurementName("pulse")
            .unit("heartbeat")
            .value(85.0)
            .measurementDate(toDate("2000-03-02 00:00"))
            .build(), subject2);
    private PatientData patientData6 = new PatientData(PatientDataRaw.builder()
            .id("pr6")
            .subjectId("subjectId1")
            .measurementName("activity")
            .unit("step")
            .value(5000.0)
            .measurementDate(toDate("2000-04-10 00:00"))
            .build(), subject1);

    private List<PatientData> patientDataList = newArrayList(patientData1, patientData2, patientData3,
            patientData4, patientData5, patientData6);

    @Before
    public void setUp() {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(subjects);
        when(patientDataDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(patientDataList);
    }

    @Test
    public void testGetPatientDataSummaries() {

        final List<SubjectPatientDataSummary> patientDataSummaries = patientDataService.getPatientDataSummaries(DUMMY_DETECT_DATASETS,
                GroupByOption.Params.builder().with(TIMESTAMP_TYPE, DAYS_HOURS_SINCE_FIRST_DOSE).build(),
                PatientDataFilters.empty(),
                PopulationFilters.empty());

        softly.assertThat(patientDataSummaries).extracting(SubjectPatientDataSummary::getSubject)
                .containsExactly(subject1.getSubjectCode(), subject2.getSubjectCode());
        softly.assertThat(patientDataSummaries).filteredOn(s -> s.getSubject().equals(subject2.getSubjectCode()))
                .flatExtracting(SubjectPatientDataSummary::getEvents).extracting(PatientDataEvent::getNumberOfEvents)
                .contains(2, 1);
        softly.assertThat(patientDataSummaries).filteredOn(s -> s.getSubject().equals(subject2.getSubjectCode()))
                .flatExtracting(SubjectPatientDataSummary::getEvents).flatExtracting(PatientDataEvent::getDetails)
                .extracting(PatientDataEventDetails::getMeasurementName, PatientDataEventDetails::getValue,
                        PatientDataEventDetails::getUnit).containsExactlyInAnyOrder(
                                tuple("pulse", 70.0, "heartbeat"),
                                tuple("activity", 500.0, "step"),
                                tuple("pulse", 85.0, "heartbeat"));
        softly.assertThat(patientDataSummaries).filteredOn(s -> s.getSubject().equals(subject2.getSubjectCode()))
                .flatExtracting(SubjectPatientDataSummary::getEvents)
                .extracting(e -> e.getStart().getDayHourAsString(), e -> e.getStart().getStudyDayHourAsString())
                .containsExactlyInAnyOrder(tuple("2d", "3d"), tuple("1d", "2d"));

        softly.assertThat(patientDataSummaries).filteredOn(s -> s.getSubject().equals(subject2.getSubjectCode()))
                .flatExtracting(SubjectPatientDataSummary::getEvents)
                .flatExtracting(PatientDataEvent::getDetails)
                .extracting(e -> e.getStartDate().getDayHourAsString(), e -> e.getStartDate().getStudyDayHourAsString())
                .containsExactlyInAnyOrder(tuple("2d 12:15", "3d 12:15"),
                        tuple("2d 18:00", "3d 18:00"),
                        tuple("1d 00:00", "2d 00:00"));
    }

    @Test
    public void testGetPatientDataSummariesSinceFirstDose() {

        final List<SubjectPatientDataSummary> patientDataSummaries = patientDataService.getPatientDataSummaries(DUMMY_DETECT_DATASETS,
                GroupByOption.Params.builder().with(TIMESTAMP_TYPE, DAYS_HOURS_SINCE_FIRST_DOSE_OF_DRUG)
                        .with(DRUG_NAME, "Drug1").build(),
                PatientDataFilters.empty(),
                PopulationFilters.empty());

        softly.assertThat(patientDataSummaries).filteredOn(s -> s.getSubject().equals(subject2.getSubjectCode()))
                .flatExtracting(SubjectPatientDataSummary::getEvents)
                .extracting(e -> e.getStart().getDayHourAsString(), e -> e.getStart().getStudyDayHourAsString())
                .containsExactlyInAnyOrder(tuple("-2d", "3d"), tuple("-3d", "2d"));

        softly.assertThat(patientDataSummaries).filteredOn(s -> s.getSubject().equals(subject2.getSubjectCode()))
                .flatExtracting(SubjectPatientDataSummary::getEvents)
                .flatExtracting(PatientDataEvent::getDetails)
                .extracting(e -> e.getStartDate().getDayHourAsString(), e -> e.getStartDate().getStudyDayHourAsString())
                .containsExactlyInAnyOrder(tuple("-1d 11:45", "3d 12:15"),
                        tuple("-1d 06:00", "3d 18:00"),
                        tuple("-3d 00:00", "2d 00:00"));
    }

    @Test
    public void testGetPatientDataDetails() {

        final List<SubjectPatientDataDetail> patientDataSummaries = patientDataService.getPatientDataDetails(DUMMY_DETECT_DATASETS,
                GroupByOption.Params.builder().with(TIMESTAMP_TYPE, DAYS_HOURS_SINCE_FIRST_DOSE).build(),
                newHashSet(subject1.getId(), subject2.getId()),
                PatientDataFilters.empty(),
                PopulationFilters.empty());
        softly.assertThat(patientDataSummaries).filteredOn(s -> s.getSubject().equals(subject2.getSubjectCode()))
                .flatExtracting(SubjectPatientDataDetail::getTests).extracting(PatientDataTests::getTestName)
                .containsExactly("activity", "pulse");
        softly.assertThat(patientDataSummaries).filteredOn(s -> s.getSubject().equals(subject2.getSubjectCode()))
                .flatExtracting(SubjectPatientDataDetail::getTests).flatExtracting(PatientDataTests::getDetails)
                .extracting(PatientDataTestsDetails::getValue, PatientDataTestsDetails::getUnit,
                        d -> d.getStartDate().getDayHourAsString(), d -> d.getStartDate().getStudyDayHourAsString())
                .containsExactlyInAnyOrder(
                tuple(500.0, "step", "2d 18:00", "3d 18:00"),
                tuple(70.0, "heartbeat", "2d 12:15", "3d 12:15"),
                tuple(85.0, "heartbeat", "1d 00:00", "2d 00:00"));
    }
}
