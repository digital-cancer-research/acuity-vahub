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
import com.acuity.visualisations.rawdatamodel.dataproviders.CtDnaDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.event.CtDnaService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.CtDnaGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.CtDnaRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.CtDna;
import com.google.gson.JsonElement;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_YAXIS_OPTIONS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.HAS_TRACKED_MUTATIONS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.NO;
import static com.acuity.visualisations.rawdatamodel.util.Constants.TRACKED_MUTATIONS_STRING;
import static com.acuity.visualisations.rawdatamodel.util.Constants.YES;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.when;

public class CtDnaModuleMetadataTest {

    @InjectMocks
    private CtDnaModuleMetadata ctDnaModuleMetadata;
    @Mock
    private CtDnaDatasetsDataProvider ctDnaDatasetsDataProvider;
    @Mock
    private DoDCommonService doDCommonService;
    @Mock
    private CtDnaService ctDnaService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ctDnaModuleMetadata.datasetsDataProvider = newArrayList(ctDnaDatasetsDataProvider);
    }

    @Test
    public void shouldGetMetadata() {
        when(ctDnaDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList(
                new CtDna(CtDnaRaw.builder().build(), new Subject()),
                new CtDna(CtDnaRaw.builder().trackedMutation(NO).build(), new Subject()),
                new CtDna(CtDnaRaw.builder().trackedMutation(YES).build(), new Subject())));
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollectionOf(CtDna.class)))
                .thenReturn(new HashMap<>());

        MetadataItem metadataItem = ctDnaModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("ctdna");
        softly.assertThat(metadataItem.build()).contains("\"count\": 3");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": true");
        softly.assertThat(metadataItem.build()).contains("\"" + HAS_TRACKED_MUTATIONS + "\": true");
        softly.assertThat(metadataItem.build()).contains(String.format("\"" + TRACKED_MUTATIONS_STRING + "\": \"%s\"", CtDna.ONLY_TRACKED_MUTATIONS));
    }

    @Test
    public void shouldGetMetadataNoData() {
        when(ctDnaDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(new ArrayList<>());
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollectionOf(CtDna.class))).thenReturn(new HashMap<>());

        MetadataItem metadataItem = ctDnaModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("ctdna");
        softly.assertThat(metadataItem.build()).contains("\"count\": 0");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": false");
        softly.assertThat(metadataItem.build()).contains("\"" + HAS_TRACKED_MUTATIONS + "\": false");
        softly.assertThat(metadataItem.build()).contains(String.format("\"" + TRACKED_MUTATIONS_STRING + "\": \"%s\"", CtDna.ONLY_TRACKED_MUTATIONS));
    }

    @Test
    public void shouldGetPercentageOptionFirst() {
        MetadataItem metadataItem = ctDnaModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        JsonElement firstYOption = metadataItem.getItemObject().get(AVAILABLE_YAXIS_OPTIONS).getAsJsonArray().get(0);
        softly.assertThat(firstYOption.toString().equals(CtDnaGroupByOptions.VARIANT_ALLELE_FREQUENCY_PERCENT.toString()));
    }
}
