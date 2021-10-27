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
import com.acuity.visualisations.rawdatamodel.dataproviders.BiomarkerDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.event.BiomarkerService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.vo.biomarker.BiomarkerRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Biomarker;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.when;

public class BiomarkerModuleMetadataTest {

    @InjectMocks
    private BiomarkerModuleMetadata biomarkerModuleMetadata;
    @Mock
    private BiomarkerDatasetsDataProvider biomarkerDatasetsDataProvider;
    @Mock
    private DoDCommonService doDCommonService;
    @Mock
    private BiomarkerService biomarkerService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        biomarkerModuleMetadata.datasetsDataProvider = newArrayList(biomarkerDatasetsDataProvider);        
    }

    @Test
    public void shouldGetMetadata() {
        when(biomarkerDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList(
                new Biomarker(new BiomarkerRaw(), new Subject()),
                new Biomarker(new BiomarkerRaw(), new Subject()),
                new Biomarker(new BiomarkerRaw(), new Subject())));
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());
        Map<String, List<String>> profiles = new HashMap();
        profiles.put("profile1", new ArrayList<>());
        when(biomarkerService.getCBioProfiles(any(), any())).thenReturn(profiles);

        MetadataItem metadataItem = biomarkerModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("biomarker");
        softly.assertThat(metadataItem.build()).contains("\"count\": 3");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": true");
        softly.assertThat(metadataItem.build()).contains("\"enableCBioLink\": true");
    }

    @Test
    public void shouldGetMetadataNoData() {
        when(biomarkerDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList());
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());

        MetadataItem metadataItem = biomarkerModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("biomarker");
        softly.assertThat(metadataItem.build()).contains("\"count\": 0");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": false");
        softly.assertThat(metadataItem.build()).contains("\"enableCBioLink\": false");
    }
}
