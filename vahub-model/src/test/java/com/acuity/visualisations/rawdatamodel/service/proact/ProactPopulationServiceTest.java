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

package com.acuity.visualisations.rawdatamodel.service.proact;

import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.util.DaysUtil;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.proact.ProactPatient;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProactPopulationServiceTest {

    @Mock
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @InjectMocks
    private ProactPopulationService proactPopulationService;

    @Test
    public void shouldReturnPatientList() {
        when(populationDatasetsDataProvider.loadData(eq(DUMMY_ACUITY_DATASETS))).thenReturn(Arrays.asList(
                Subject.builder()
                        .subjectCode("E01")
                        .subjectId("id1")
                        .race("Race")
                        .sex("sex")
                        .country("country")
                        .centerNumber("с1")
                        .dateOfBirth(DaysUtil.toDate("2015-12-28"))
                        .enrollVisitDate(DaysUtil.toDate("2015-12-29"))
                        .firstTreatmentDate(DaysUtil.toDate("2015-12-30"))
                        .build(),
                Subject.builder()
                        .subjectCode("E02")
                        .subjectId("id2")
                        .build()
        ));
        List<ProactPatient> proactPatientList = proactPopulationService.getProactPatientList(DUMMY_ACUITY_DATASETS);
        Assertions.assertThat(proactPatientList).hasSize(2)
                .extracting(
                        ProactPatient::getSubjectCode,
                        ProactPatient::getPatientId,
                        ProactPatient::getSubjectCode,
                        ProactPatient::getRace,
                        ProactPatient::getSex,
                        ProactPatient::getCountry,
                        ProactPatient::getCentre,
                        ProactPatient::getBirthDate,
                        ProactPatient::getFirstVisitDate,
                        ProactPatient::getFirstDoseDate)
                .contains(
                        tuple("E01", "id1", "E01", "Race", "sex", "country", "с1",
                                DaysUtil.toDate("2015-12-28"), DaysUtil.toDate("2015-12-29"), DaysUtil.toDate("2015-12-30"))
                );
    }
}
