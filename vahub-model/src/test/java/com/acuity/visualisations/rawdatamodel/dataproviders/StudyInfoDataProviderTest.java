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

import com.acuity.visualisations.common.lookup.CacheableDataProvider;
import com.acuity.visualisations.rawdatamodel.dao.PopulationRepository;
import com.acuity.visualisations.rawdatamodel.dao.StudyInfoRepository;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.AcuityDataset;
import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class StudyInfoDataProviderTest {

    @Mock
    private StudyInfoRepository studyInfoRepository;
    @Mock
    private PopulationRepository populationRepository;
    @Mock
    private CacheableDataProvider dataProvider;

    @InjectMocks
    private StudyInfoDataProvider studyInfoDataProvider;

    private final StudyInfo dataset1StudyInfo = StudyInfo.builder()
            .studyName("Study")
            .datasetName("Dataset")
            .build();

    private final List<Subject> subjects = Lists.newArrayList(
            Subject.builder().subjectId("E01").firstTreatmentDate(new Date()).build(),
            Subject.builder().subjectId("E02").firstTreatmentDate(new Date()).build(),
            Subject.builder().subjectId("E03").build()
    );

    @SuppressWarnings("unchecked")
    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        when(studyInfoRepository.getRawData(Mockito.anyLong())).thenReturn(Collections.emptyList());
        when(studyInfoRepository.getRawData(1L)).thenReturn(Collections.singletonList(dataset1StudyInfo));

        when(populationRepository.getRawData(Mockito.anyLong())).thenReturn(Collections.emptyList());
        when(populationRepository.getRawData(1L)).thenReturn(subjects);

        when(dataProvider.getData(Mockito.any(), Mockito.any(), Mockito.any()))
                .thenAnswer(invocation -> invocation.getArgumentAt(2, Function.class)
                        .apply(invocation.getArgumentAt(1, Dataset.class)));
    }

    @Test
    public void shouldReturnEmptyListWhenNoStudyInfoFound() {
        Collection<StudyInfo> studies = studyInfoDataProvider.getData(new AcuityDataset(2L));
        assertThat(studies).isEmpty();
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void shouldReturnStudyInfoWithCorrectNumberOfDosedSubjects() {
        Collection<StudyInfo> studies = studyInfoDataProvider.getData(new AcuityDataset(1L));
        assertThat(studies).hasSize(1);
        assertThat(studies.stream().findAny().get().getNumberOfDosedSubjects()).isEqualTo(2);
    }
}
