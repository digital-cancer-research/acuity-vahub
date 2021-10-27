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
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.dataproviders.PkResultWithResponseDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PkResultFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.PkResultWithResponseService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.PkResultRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import com.acuity.va.security.acl.domain.Datasets;
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
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class PkResultWithResponseModuleMetadataTest {

    @InjectMocks
    private PkResultWithResponseModuleMetadata pkResultWithResponseModuleMetadata;
    @Mock
    private PkResultWithResponseDatasetsDataProvider pkResultWithResponseDatasetsDataProvider;
    @Mock
    private DoDCommonService doDCommonService;
    @Mock
    private PkResultWithResponseService pkResultWithResponseService;

    private static PkResult pkResult = new PkResult(PkResultRaw.builder().treatmentCycle("Cycle 0").bestOverallResponse("bor")
            .parameter("par").parameterValue(5d).parameterUnit("mg")
            .analyte("analyte").visitNumber(5).treatmentCycle("cycle")
            .treatment(5.5d).protocolScheduleStartDay("4").visit("visit 1")
            .actualDose(4.2d).build(), new Subject());

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        pkResultWithResponseModuleMetadata.datasetsDataProvider
                = newArrayList(pkResultWithResponseDatasetsDataProvider);

        when(pkResultWithResponseDatasetsDataProvider.loadData(DUMMY_ACUITY_DATASETS)).thenReturn(newArrayList(pkResult));
        when(pkResultWithResponseService.getAvailableBoxPlotXAxis(any(Datasets.class), eq(PkResultFilters.empty()), eq(PopulationFilters.empty())))
                .thenReturn(new AxisOptions<>(Collections.emptyList(), true, null));
    }

    @Test
    public void shouldGetAllDodColumns() {
        when(doDCommonService.getDoDColumns(any(Column.DatasetType.class), anyCollectionOf(PkResult.class)))
                .thenReturn(generateColumnList());

        MetadataItem metadataItem = pkResultWithResponseModuleMetadata.getMetadataItem(DUMMY_ACUITY_DATASETS);
        String json = metadataItem.build();

        softly.assertThat(json).contains("bestOverallResponse", "actualDose", "visit", "protocolScheduleStartDay",
                "cycle", "treatment", "analyte", "parameterUnit", "parameterValue", "parameter");
    }

    @Test
    public void shouldHasNoDataWhenEmptyXAxisOption() {

        MetadataItem metadataItem = pkResultWithResponseModuleMetadata.getMetadataItem(DUMMY_ACUITY_DATASETS);
        String json = metadataItem.build();

        softly.assertThat(json).contains("\"hasData\": false");
    }


    private HashMap<String, String> generateColumnList() {
        HashMap<String, String> dods = new HashMap<>();
        dods.put("bestOverallResponse", "bestOverallResponse");
        dods.put("actualDose", "actualDose");
        dods.put("visit", "visit");
        dods.put("protocolScheduleStartDay", "protocolScheduleStartDay");
        dods.put("cycle", "cycle");
        dods.put("treatment", "treatment");
        dods.put("analyte", "analyte");
        dods.put("parameterUnit", "parameterUnit");
        dods.put("parameterValue", "parameterValue");
        dods.put("parameter", "parameter");
        return dods;
    }
}
