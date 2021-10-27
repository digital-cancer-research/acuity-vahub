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

import com.acuity.visualisations.rawdatamodel.dao.CvotEndpointRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DataProvider;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.CvotEndpointRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CvotEndpoint;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
public class CvotEndpointDatasetsDataProviderTest {

    public static final List<CvotEndpointRaw> EVENTS_RAW = new ArrayList<>();
    public static final List<CvotEndpointRaw> EVENTS_RAW_IN = new ArrayList<>();
    public static final List<Subject> SUBJECTS = new ArrayList<>();
    public static final List<Subject> SUBJECTS_IN = new ArrayList<>();
    public static final Subject SUBJECT_EXTRA = Subject.builder().subjectId("sid_").subjectCode("E0X").datasetId("test").build();

    static {
        EVENTS_RAW_IN.add(CvotEndpointRaw.builder().aeNumber(1).category1("cat1").subjectId("sid1").build());
        EVENTS_RAW_IN.add(CvotEndpointRaw.builder().aeNumber(2).category1("cat2").subjectId("sid2").build());
        EVENTS_RAW_IN.add(CvotEndpointRaw.builder().aeNumber(3).category1("cat3").subjectId("sid1").build());
        EVENTS_RAW_IN.add(CvotEndpointRaw.builder().aeNumber(4).category1("cat4").subjectId("sid3").build());
        EVENTS_RAW.addAll(EVENTS_RAW_IN);
        EVENTS_RAW.add(CvotEndpointRaw.builder().aeNumber(5).category1("cat5").subjectId("unknown").build());

        SUBJECTS_IN.add(Subject.builder().subjectId("sid1").subjectCode("E01").datasetId("test").build());
        SUBJECTS_IN.add(Subject.builder().subjectId("sid2").subjectCode("E02").datasetId("test").build());
        SUBJECTS_IN.add(Subject.builder().subjectId("sid3").subjectCode("E03").datasetId("test").build());
        SUBJECTS.addAll(SUBJECTS_IN);
        SUBJECTS.add(SUBJECT_EXTRA);
    }

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @MockBean
    private DataProvider dataProvider;
    @MockBean
    private PopulationDatasetsDataProvider populationDatasetsDataProvider;
    @MockBean
    private CvotEndpointRepository cvotEndpointRepository;
    @Autowired
    private CvotEndpointDatasetsDataProvider cvotEndpointDatasetsDataProvider;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }


    @Test
    public void shouldGetData() throws Exception {
        when(populationDatasetsDataProvider.loadData(any(Datasets.class))).thenReturn(SUBJECTS);
        when(dataProvider.getData(eq(CvotEndpointRaw.class), any(), any())).thenReturn(EVENTS_RAW);
        final Collection<CvotEndpoint> res = cvotEndpointDatasetsDataProvider.loadData(DUMMY_ACUITY_DATASETS);
        softly.assertThat(res).extracting("subject").contains(SUBJECTS_IN.toArray());
        softly.assertThat(res).extracting("subject").doesNotContain(SUBJECT_EXTRA);
        softly.assertThat(res).extracting("event").containsExactlyInAnyOrder(EVENTS_RAW_IN.toArray());
        softly.assertThat(res).allMatch(e -> Objects.equals(e.getEvent().getSubjectId(), e.getSubject().getSubjectId()));
    }
}
