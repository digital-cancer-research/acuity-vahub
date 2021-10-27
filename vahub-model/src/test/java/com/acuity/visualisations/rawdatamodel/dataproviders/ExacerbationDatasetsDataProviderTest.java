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
import com.acuity.visualisations.rawdatamodel.dao.ExacerbationRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.config.DataProviderConfiguration;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.util.DateUtils;
import com.acuity.visualisations.rawdatamodel.vo.ExacerbationRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
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

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {TestConfig.class, DataProviderConfiguration.class})
public class ExacerbationDatasetsDataProviderTest {

    @Autowired
    private ExacerbationDatasetsDataProvider exacerbationDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @MockBean
    private ExacerbationRepository exacerbationRepository;

    @Autowired
    private BeanLookupService beanLookupService;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    private static final Subject SUBJECT = Subject.builder().subjectId("sid1").firstTreatmentDate(DateUtils.toDate("01.08.2015"))
            .dateOfRandomisation(DateUtils.toDate("01.09.2015")).build();

    private static final ExacerbationRaw EXACERBATION_1 = ExacerbationRaw.builder().id("id1")
            .startDate(DateUtils.toDate("03.08.2015")).endDate(DateUtils.toDate("05.08.2015")).subjectId("sid1").build();
    private static final ExacerbationRaw EXACERBATION_2 = ExacerbationRaw.builder().id("id2")
            .startDate(DateUtils.toDate("31.08.2015")).endDate(DateUtils.toDate("02.09.2015")).subjectId("sid1").build();

    private static final List EXACERBATIONS = Arrays.asList(EXACERBATION_1, EXACERBATION_2);

    @Test
    public void shouldGetData() {
        when(beanLookupService.get(any(Dataset.class), any(ResolvableType.class))).thenReturn(exacerbationRepository);
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Collections.singletonList(SUBJECT));
        when(exacerbationRepository.getRawData(anyLong())).thenReturn(EXACERBATIONS);

        List<Exacerbation> result = new ArrayList<>(exacerbationDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS));

        softly.assertThat(result).hasSize(2);

        softly.assertThat(result.get(0).getDaysOnStudyAtStart()).isEqualTo(2);
        softly.assertThat(result.get(0).getDaysOnStudyAtEnd()).isEqualTo(4);
        softly.assertThat(result.get(0).getDuration()).isEqualTo(3);
        softly.assertThat(result.get(0).getStartPriorToRandomisation()).isEqualTo("Yes");
        softly.assertThat(result.get(0).getEndPriorToRandomisation()).isEqualTo("Yes");

        softly.assertThat(result.get(1).getDaysOnStudyAtStart()).isEqualTo(30);
        softly.assertThat(result.get(1).getDaysOnStudyAtEnd()).isEqualTo(32);
        softly.assertThat(result.get(1).getDuration()).isEqualTo(3);
        softly.assertThat(result.get(1).getStartPriorToRandomisation()).isEqualTo("Yes");
        softly.assertThat(result.get(1).getEndPriorToRandomisation()).isEqualTo("No");
    }
}
