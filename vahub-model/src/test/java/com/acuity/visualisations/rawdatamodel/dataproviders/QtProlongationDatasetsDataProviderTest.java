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
import com.acuity.visualisations.rawdatamodel.dao.QtProlongationRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProviderAwareTest;
import com.acuity.visualisations.rawdatamodel.dataproviders.config.DataProviderConfiguration;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.QtProlongationRaw;
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
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class, DataProviderConfiguration.class})
public class QtProlongationDatasetsDataProviderTest extends DataProviderAwareTest {
    @Autowired
    private QtProlongationDatasetsDataProvider qtProlongationDatasetsDataProvider;

    @Autowired
    private BeanLookupService beanLookupService;

    @MockBean
    private QtProlongationRepository qtProlongationRepository;

    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetRawDataWithDaysOnStudy() {
        when(beanLookupService.get(any(Dataset.class), any(ResolvableType.class))).thenReturn(qtProlongationRepository);
        when(qtProlongationRepository.getRawData(DUMMY_ACUITY_DATASET.getId())).thenReturn(getQtPrlongationsRaw());
        when(populationDatasetsDataProvider.loadData(new Datasets(DUMMY_ACUITY_DATASET))).thenReturn(getSubjects());
        final List<QtProlongationRaw> result = new ArrayList<>(qtProlongationDatasetsDataProvider.getData(DUMMY_ACUITY_DATASET));
        assertThat(result).hasSize(2);

        assertThat(result.get(0).getDaysOnStudy()).isNull();
        assertThat(result.get(1).getDaysOnStudy()).isEqualTo(2);
    }

    private List<Subject> getSubjects() {
        return Arrays.asList(Subject.builder().subjectId("subj1").firstTreatmentDate(DateUtils.toDate("01.08.2015")).build(),
                Subject.builder().subjectId("subj2").firstTreatmentDate(DateUtils.toDate("02.08.2015")).build());
    }

    private List<QtProlongationRaw> getQtPrlongationsRaw() {
        return Arrays.asList(QtProlongationRaw.builder().id("1").subjectId("subj1").measurementTimePoint(DateUtils.toDate("03.08.2015")).build(),
                QtProlongationRaw.builder().id("2").subjectId("subj2").measurementTimePoint(DateUtils.toDate("04.08.2015"))
                        .doseFirstDate(DateUtils.toDate("02.08.2015")).build());
    }
}
