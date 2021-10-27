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

package com.acuity.visualisations.rawdatamodel.metadata;

import com.acuity.visualisations.common.study.metadata.MetadataItem;
import com.acuity.visualisations.rawdatamodel.dao.StudyInfoRepository;
import com.acuity.visualisations.rawdatamodel.dataproviders.QtProlongationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.QtProlongationRaw;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfoAdministrationDetail;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.QtProlongation;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.HashMap;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_VA_ID;
import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Collections.emptyList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.when;

public class QtProlongationModuleMetadataTest {
    @InjectMocks
    private QtProlongationModuleMetadata qtProlongationModuleMetadata;
    @Mock
    private QtProlongationDatasetsDataProvider qtProlongationDatasetsDataProvider;
    @Mock
    private DoDCommonService doDCommonService;
    @Mock
    private StudyInfoRepository studyInfoRepository;
    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        qtProlongationModuleMetadata.datasetsDataProvider
                = newArrayList(qtProlongationDatasetsDataProvider);
        when(studyInfoRepository.getStudyInfoByDatasetIds(Collections.singleton(DUMMY_ACUITY_VA_ID)))
                .thenReturn(Collections.singletonList(
                        new StudyInfoAdministrationDetail(DUMMY_ACUITY_VA_ID, "ds", "cbiods", TRUE)));
    }

    @Test
    public void shouldGetMetadata() {
        when(qtProlongationDatasetsDataProvider.loadData(DUMMY_ACUITY_DATASETS)).thenReturn(newArrayList(
                new QtProlongation(QtProlongationRaw.builder().build(), new Subject()),
                new QtProlongation(QtProlongationRaw.builder().alertLevel("High").build(), new Subject()),
                new QtProlongation(QtProlongationRaw.builder().alertLevel("Low").build(), new Subject())));
        when(doDCommonService.getDoDColumns(any(Column.DatasetType.class), anyCollectionOf(QtProlongation.class)))
                .thenReturn(new HashMap<>());


        MetadataItem metadataItem = qtProlongationModuleMetadata.getMetadataItem(DUMMY_ACUITY_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("qt-prolongation");
        softly.assertThat(metadataItem.build()).contains("\"count\": 3");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": true");
    }

    @Test
    public void shouldGetMetadataNoData() {
        when(qtProlongationDatasetsDataProvider.loadData(DUMMY_ACUITY_DATASETS)).thenReturn(emptyList());
        when(doDCommonService.getDoDColumns(any(Column.DatasetType.class), anyCollectionOf(QtProlongation.class)))
                .thenReturn(new HashMap<>());

        MetadataItem metadataItem = qtProlongationModuleMetadata.getMetadataItem(DUMMY_ACUITY_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("qt-prolongation");
        softly.assertThat(metadataItem.build()).contains("\"count\": 0");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": false");
    }

    @Test
    public void shouldNotGetMetadataWhenAmlDisabled() {
        when(qtProlongationDatasetsDataProvider.loadData(DUMMY_ACUITY_DATASETS)).thenReturn(emptyList());
        when(studyInfoRepository.getStudyInfoByDatasetIds(Collections.singleton(DUMMY_ACUITY_VA_ID)))
                .thenReturn(Collections.singletonList(
                        new StudyInfoAdministrationDetail(DUMMY_ACUITY_VA_ID, "ds", "cbiods", FALSE)));

        MetadataItem metadataItem = qtProlongationModuleMetadata.getMetadataItem(DUMMY_ACUITY_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("qt-prolongation");
        softly.assertThat(metadataItem.build()).contains("\"count\": \"N/A\"");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": false");
    }
}
