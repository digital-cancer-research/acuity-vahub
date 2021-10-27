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

package com.acuity.visualisations.rawdatamodel.service;

import com.acuity.visualisations.rawdatamodel.dao.StudyInfoRepository;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuityDataset;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.context.ContextConfiguration;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class StudyInfoServiceImplTest {

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Mock
    private StudyInfoRepository studyInfoRepository;

    @InjectMocks
    private StudyInfoServiceImpl studyInfoService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void shouldReturnStudyInfoWithLatestData() throws Exception {
        when(studyInfoRepository.getRawData(1))
                .thenReturn(Collections.singletonList(
                        StudyInfo.builder().lastUpdatedDate(SIMPLE_DATE_FORMAT.parse("2014-02-11")).build()));
        when(studyInfoRepository.getRawData(2))
                .thenReturn(Collections.singletonList(
                        StudyInfo.builder().lastUpdatedDate(SIMPLE_DATE_FORMAT.parse("2015-01-01")).build()));
        Date lastDate = SIMPLE_DATE_FORMAT.parse("2016-01-01");
        when(studyInfoRepository.getRawData(3))
                .thenReturn(Collections.singletonList(
                        StudyInfo.builder().lastUpdatedDate(lastDate).build()));

        Optional<StudyInfo> studyInfo = studyInfoService.getStudyInfo(new Datasets(new AcuityDataset(1L), new AcuityDataset(2L),
                new AcuityDataset(3L)));
        softly.assertThat(studyInfo.isPresent()).isTrue();
        softly.assertThat(studyInfo.get().getLastUpdatedDate()).isEqualTo(lastDate);
    }
}
