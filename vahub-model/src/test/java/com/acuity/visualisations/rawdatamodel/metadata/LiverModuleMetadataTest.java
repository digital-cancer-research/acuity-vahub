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
import com.acuity.visualisations.rawdatamodel.dataproviders.LiverDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.service.event.LabService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.vo.LiverRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Liver;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASET;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.when;

public class LiverModuleMetadataTest {

    @InjectMocks
    private LiverModuleMetadata liverModuleMetadata;
    @Mock
    private LiverDatasetsDataProvider liverDatasetsDataProvider;
    @Mock
    private DoDCommonService doDCommonService;
    @Mock
    private LabService labService;
    @Mock
    private AeService aeService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        liverModuleMetadata.datasetsDataProvider = newArrayList(liverDatasetsDataProvider);
    }

    private Subject subject1 = Subject.builder().clinicalStudyCode(String.valueOf(DUMMY_DETECT_DATASET.getId()))
            .subjectId("subject1").subjectCode("subject1").actualArm("Placebo").build();

    @Test
    public void shouldGetMetadata() {
        when(liverDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList(
                new Liver(LiverRaw.builder().id("liver1").labCode("l1").normalizedLabCode("AST")
                        .value(12d).refHigh(1d).unit("mg").baselineFlag("Y").studyPeriods("SP")
                        .build().runPrecalculations(), subject1),
                new Liver(LiverRaw.builder().id("liver2").labCode("l2").normalizedLabCode("AST")
                        .value(11d).refHigh(1d).unit("mg").baselineFlag("Y").studyPeriods("SP")
                        .build().runPrecalculations(), subject1),
                new Liver(LiverRaw.builder().id("liver3").labCode("l3").normalizedLabCode("XXX")
                        .value(13d).refHigh(1d).unit("mg").baselineFlag("Y").studyPeriods("SP")
                        .build().runPrecalculations(), subject1)));
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());


        MetadataItem metadataItem = liverModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("liver");
        softly.assertThat(metadataItem.build()).contains("\"count\": 3");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": true");
    }

    @Test
    public void shouldGetMetadataNoData() {
        when(liverDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList());
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());

        MetadataItem metadataItem = liverModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("liver");
        softly.assertThat(metadataItem.build()).contains("\"count\": 0");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": false");
    }
}
