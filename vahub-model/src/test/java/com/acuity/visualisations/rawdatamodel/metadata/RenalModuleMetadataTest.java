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
import com.acuity.visualisations.rawdatamodel.dataproviders.RenalDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.service.event.LabService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.RenalRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Renal;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;
import java.util.HashMap;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RenalModuleMetadataTest {
    @InjectMocks
    private RenalModuleMetadata renalModuleMetadata;
    @Mock
    private RenalDatasetsDataProvider renalDatasetsDataProvider;
    @Mock
    private DoDCommonService doDCommonService;
    @Mock
    private AeService aeService;
    @Mock
    private LabService labService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        renalModuleMetadata.datasetsDataProvider = newArrayList(renalDatasetsDataProvider);
    }

    @Test
    public void shouldGetMetadata() {
        when(renalDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList(
                new Renal(RenalRaw.builder().build(), Subject.builder().build()),
                new Renal(RenalRaw.builder().build(), Subject.builder().build()),
                new Renal(RenalRaw.builder().build(), Subject.builder().build())));
        when(doDCommonService.getDoDColumns(any(Column.DatasetType.class), anyCollection())).thenReturn(new HashMap());
        when(aeService.getJumpToAesSocs(DUMMY_DETECT_DATASETS, RenalModuleMetadata.AES_EVT_SOC)).thenReturn(Collections.emptySet());
        when(labService.getJumpToNormalizedLabs(DUMMY_DETECT_DATASETS, RenalModuleMetadata.DETECT_LAB_CODES))
                .thenReturn(Collections.emptySet());

        MetadataItem metadataItem = renalModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("renal-java");
        String json = metadataItem.build();
        softly.assertThat(json).contains("\"count\": 3");
        softly.assertThat(json).contains("\"hasData\": true");
        assertThatJson(json).node("renal-java.yAxisOptionsForBoxPlot").isEqualTo(newArrayList(
                "ACTUAL_VALUE",
                "ABSOLUTE_CHANGE_FROM_BASELINE",
                "PERCENTAGE_CHANGE_FROM_BASELINE",
                "TIMES_UPPER_REF_VALUE",
                "TIMES_LOWER_REF_VALUE",
                "REF_RANGE_NORM_VALUE"
        ));
        softly.assertThat(json).contains("\"socs\": []");
        softly.assertThat(json).contains("\"labCodes\": []");
        assertThatJson(json).node("renal-java.availableYAxisOptionsForCKDBarChart").isEqualTo(Collections.singletonList(
                "PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED"));
    }

    @Test
    public void shouldGetMetadataNoData() {
        when(renalDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList());
        when(doDCommonService.getDoDColumns(any(Column.DatasetType.class), anyCollection())).thenReturn(new HashMap());

        MetadataItem metadataItem = renalModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("renal-java");
        String json = metadataItem.build();
        softly.assertThat(json).contains("\"count\": 0");
        softly.assertThat(json).contains("\"hasData\": false");
        assertThatJson(json).node("renal-java.availableYAxisOptionsForCKDBarChart").isEqualTo(Collections.singletonList(
                "PERCENTAGE_OF_SUBJECTS_100_PERCENT_STACKED"));
    }
}
