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
import com.acuity.visualisations.rawdatamodel.dataproviders.DrugDoseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.TherapyFilters;
import com.acuity.visualisations.rawdatamodel.service.event.TumourColumnRangeService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.vo.ChemotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.DrugDoseRaw;
import com.acuity.visualisations.rawdatamodel.vo.RadiotherapyRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.TumourTherapy;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Chemotherapy;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.DrugDose;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Radiotherapy;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

public class TumourTherapyModuleMetadataTest {

    @InjectMocks
    @Spy
    private TumourTherapyModuleMetadata therapyModuleMetadata;
    @Mock
    private DrugDoseDatasetsDataProvider drugDoseDatasetsDataProvider;
    @Mock
    private DoDCommonService doDCommonService;
    @Mock
    private TumourColumnRangeService tumourColumnRangeService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        therapyModuleMetadata.datasetsDataProvider = newArrayList(drugDoseDatasetsDataProvider);

    }

    @Test
    public void shouldGetMetadata() throws Exception {
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());
        doReturn(newArrayList(new Chemotherapy(new ChemotherapyRaw(), new Subject())))
                .when(therapyModuleMetadata).getPreviousChemotherapy(DUMMY_DETECT_DATASETS);
        doReturn(newArrayList(new Radiotherapy(new RadiotherapyRaw(), new Subject()),
                new Radiotherapy(new RadiotherapyRaw(), new Subject())))
                .when(therapyModuleMetadata).getPreviousRadiotherapy(DUMMY_DETECT_DATASETS);
        when(drugDoseDatasetsDataProvider.loadDosesForTumourColumnRangeService(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList(
                new DrugDose(DrugDoseRaw.builder().dose(20.0).build(), new Subject()),
                new DrugDose(DrugDoseRaw.builder().dose(20.0).build(), new Subject()),
                new DrugDose(DrugDoseRaw.builder().dose(20.0).build(), new Subject())));
        Map<String, List<TumourTherapy>> subjectsLastTherapy = new HashMap<>();
        subjectsLastTherapy.put("id1", Collections.singletonList(TumourTherapy.builder().build()));
        when(tumourColumnRangeService.getSubjectLastTherapy(DUMMY_DETECT_DATASETS, TherapyFilters.empty(),
                PopulationFilters.empty())).thenReturn(subjectsLastTherapy);

        MetadataItem metadataItem = therapyModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("tumour-therapy");
        softly.assertThat(metadataItem.build()).contains("\"count\": 6");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": true");
        softly.assertThat(metadataItem.build()).contains("\"hasPriorTherapy\": true");
    }


    @Test
    public void shouldGetMetadataNoData() throws Exception {
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());
        when(drugDoseDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList());
        when(tumourColumnRangeService.getSubjectLastTherapy(DUMMY_DETECT_DATASETS, TherapyFilters.empty(),
                PopulationFilters.empty())).thenReturn(new HashMap<>());

        doReturn(newArrayList())
                .when(therapyModuleMetadata).getPreviousChemotherapy(DUMMY_DETECT_DATASETS);
        doReturn(newArrayList())
                .when(therapyModuleMetadata).getPreviousRadiotherapy(DUMMY_DETECT_DATASETS);

        MetadataItem metadataItem = therapyModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("tumour-therapy");
        softly.assertThat(metadataItem.build()).contains("\"count\": 0");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": false");
        softly.assertThat(metadataItem.build()).contains("\"hasPriorTherapy\": false");
    }
}
