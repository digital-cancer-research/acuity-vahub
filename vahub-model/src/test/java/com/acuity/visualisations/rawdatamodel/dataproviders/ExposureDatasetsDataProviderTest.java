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
import com.acuity.visualisations.rawdatamodel.dao.ExposureRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProviderAwareTest;
import com.acuity.visualisations.rawdatamodel.dataproviders.config.DataProviderConfiguration;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.ExposureRaw;
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
public class ExposureDatasetsDataProviderTest extends DataProviderAwareTest {
    @Autowired
    private ExposureDatasetsDataProvider exposureDatasetsDataProvider;
    @Autowired
    private BeanLookupService beanLookupService;
    @MockBean
    private ExposureRepository exposureRepository;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetRawData() {
        when(beanLookupService.get(any(Dataset.class), any(ResolvableType.class))).thenReturn(exposureRepository);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(getSubjects());
        when(exposureRepository.getRawData(DUMMY_ACUITY_DATASET.getId())).thenReturn(getExposureRaw());
        final List<ExposureRaw> result = new ArrayList<>(exposureDatasetsDataProvider.getData(DUMMY_ACUITY_DATASET));
        assertThat(result).hasSize(4);

        assertThat(result.get(0).getTimeFromAdministration()).isEqualTo(2.17);
        assertThat(result.get(0).getCycle().getIsNotAllDrugDatesEmpty()).isTrue();
        assertThat(result.get(1).getTimeFromAdministration()).isEqualTo(2);
        assertThat(result.get(1).getCycle().getIsNotAllDrugDatesEmpty()).isTrue();
        assertThat(result.get(2).getTimeFromAdministration()).isEqualTo(1.25);
        assertThat(result.get(2).getCycle().getIsNotAllDrugDatesEmpty()).isTrue();
        assertThat(result.get(3).getTimeFromAdministration()).isEqualTo(0);
        assertThat(result.get(3).getCycle().getIsNotAllDrugDatesEmpty()).isTrue();
    }

    private List<ExposureRaw> getExposureRaw() {
        return Arrays.asList(ExposureRaw.builder().id("1").subjectId("subj1").nominalHour(1.).nominalMinute(70).build(),
                ExposureRaw.builder().id("2").subjectId("subj1").nominalHour(2.).drugAdministrationDate(DateUtils.toDate("06.08.2015")).build(),
                ExposureRaw.builder().id("3").subjectId("subj2").nominalMinute(75).build(),
                ExposureRaw.builder().id("4").subjectId("subj2").build());
    }

    private List<Subject> getSubjects() {
        return Collections.singletonList(Subject.builder().subjectId("sid1").firstTreatmentDate(DateUtils.toDate("01.08.2015")).build());
    }
}
