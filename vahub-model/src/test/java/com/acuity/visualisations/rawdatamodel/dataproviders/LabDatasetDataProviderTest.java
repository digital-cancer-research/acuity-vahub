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

package com.acuity.visualisations.rawdatamodel.dataproviders;

import com.acuity.visualisations.common.lookup.BeanLookupService;
import com.acuity.visualisations.rawdatamodel.dao.DeathRepository;
import com.acuity.visualisations.rawdatamodel.dao.DoseDiscRepository;
import com.acuity.visualisations.rawdatamodel.dao.DrugDoseRepository;
import com.acuity.visualisations.rawdatamodel.dao.LabRepository;
import com.acuity.visualisations.rawdatamodel.dao.DeviceRepository;
import com.acuity.visualisations.rawdatamodel.dao.StudyInfoRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProviderAwareTest;
import com.acuity.visualisations.rawdatamodel.dataproviders.config.DataProviderConfiguration;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.Device;
import com.acuity.visualisations.rawdatamodel.vo.LabRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.ResolvableType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class, DataProviderConfiguration.class})
public class LabDatasetDataProviderTest extends DataProviderAwareTest {
    @Autowired
    private LabDatasetsDataProvider labDatasetsDataProvider;

    @MockBean
    private DeviceRepository deviceRepository;

    @Autowired
    private BeanLookupService beanLookupService;

    @MockBean
    private LabRepository labRepository;

    @MockBean
    private DrugDoseRepository drugDoseRepository;

    @MockBean
    private DeathRepository deathRepository;

    @MockBean
    private DoseDiscRepository doseDiscRepository;

    @MockBean
    private StudyInfoRepository studyInfoRepository;

    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetRawDataWithDevices() {
        when(deviceRepository.getRawData(any(Long.class))).thenReturn(getDevices());
        when(beanLookupService.get(any(Dataset.class), any(ResolvableType.class))).thenReturn(labRepository);
        when(labRepository.getRawData(DUMMY_ACUITY_DATASET.getId())).thenReturn(getLabsRaw());
        when(populationDatasetsDataProvider.loadData(new Datasets(DUMMY_ACUITY_DATASET))).thenReturn(getSubjects());
        when(studyInfoRepository.getRawData(DUMMY_ACUITY_DATASET.getId())).thenReturn(Collections.emptyList());
        final List<LabRaw> result = new ArrayList(labDatasetsDataProvider.getData(DUMMY_ACUITY_DATASET));
        assertThat(result).hasSize(4);

        assertThat(result.get(0).getDeviceName()).isEqualTo("First");
        assertThat(result.get(0).getDeviceVersion()).isEqualTo("v1");
        assertThat(result.get(1).getDeviceName()).isEqualTo("Second");
        assertThat(result.get(1).getDeviceVersion()).isEqualTo("v2");
        assertThat(result.get(2).getDeviceName()).isEqualTo("Third");
        assertThat(result.get(2).getDeviceVersion()).isEqualTo("v3");
        assertThat(result.get(3).getDeviceName()).isNull();
        assertThat(result.get(3).getDeviceVersion()).isNull();
    }

    @Test
    public void testGetRawDataWithDaysSinceFirstDose() {
        when(deviceRepository.getRawData(any(Long.class))).thenReturn(getDevices());
        when(beanLookupService.get(any(Dataset.class), any(ResolvableType.class))).thenReturn(labRepository);
        when(labRepository.getRawData(DUMMY_ACUITY_DATASET.getId())).thenReturn(getLabsRaw());
        when(populationDatasetsDataProvider.loadData(new Datasets(DUMMY_ACUITY_DATASET))).thenReturn(getSubjects());
        final List<LabRaw> result = new ArrayList(labDatasetsDataProvider.getData(DUMMY_ACUITY_DATASET));
        assertThat(result).hasSize(4);

        assertThat(result.get(0).getDaysSinceFirstDose()).isNull();
        assertThat(result.get(1).getDaysSinceFirstDose()).isEqualTo(2);
        assertThat(result.get(2).getDaysSinceFirstDose()).isEqualTo(4);
        assertThat(result.get(3).getDaysSinceFirstDose()).isNull();
    }

    private List<Device> getDevices() {
        return Arrays.asList(Device.builder().id("11").name("First").version("v1").build(),
                Device.builder().id("22").name("Second").version("v2").build(),
                Device.builder().id("33").name("Third").version("v3").build());
    }

    private List<LabRaw> getLabsRaw() {
        return Arrays.asList(LabRaw.builder().id("1").subjectId("subj1").sourceId("11").build(),
                LabRaw.builder().id("2").subjectId("subj1").measurementTimePoint(DateUtils.toDate("03.08.2015")).sourceId("22").build(),
                LabRaw.builder().id("3").subjectId("subj2").measurementTimePoint(DateUtils.toDate("06.08.2015")).sourceId("33").build(),
                LabRaw.builder().id("4").subjectId("subj2").build());
    }

    private List<Subject> getSubjects() {
        return Arrays.asList(Subject.builder().subjectId("subj1").firstTreatmentDate(DateUtils.toDate("01.08.2015")).build(),
                Subject.builder().subjectId("subj2").firstTreatmentDate(DateUtils.toDate("02.08.2015")).build());
    }
}
