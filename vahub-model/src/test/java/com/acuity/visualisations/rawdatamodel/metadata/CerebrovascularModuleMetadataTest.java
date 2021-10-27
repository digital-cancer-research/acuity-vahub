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
import com.acuity.visualisations.rawdatamodel.Constants;
import com.acuity.visualisations.rawdatamodel.dataproviders.CerebrovascularDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.vo.CerebrovascularRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cerebrovascular;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_BAR_LINE_YAXIS_OPTIONS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_YAXIS_OPTIONS;
import static com.google.common.collect.Lists.newArrayList;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.when;

public class CerebrovascularModuleMetadataTest {

    @InjectMocks
    private CerebrovascularModuleMetadata moduleMetadata;
    @Mock
    private CerebrovascularDatasetsDataProvider datasetsDataProvider;
    @Mock
    private DoDCommonService doDCommonService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        moduleMetadata.datasetsDataProvider = newArrayList(datasetsDataProvider);
    }

    @Test
    public void shouldGetMetadata() {
        //Given
        when(datasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(
                new Cerebrovascular(new CerebrovascularRaw(), new Subject()),
                new Cerebrovascular(new CerebrovascularRaw(), new Subject())));

        final HashMap<String, String> cols = new HashMap<>();
        cols.put("column1", "title 1");
        cols.put("column2", "title 2");

        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(cols);

        //When
        MetadataItem result = moduleMetadata.getMetadataItem(Constants.DATASETS);

        //Then
        softly.assertThat(result.getKey()).isEqualTo("cerebrovascular");
        String json = result.build();
        softly.assertThat(json).contains("\"count\": 2");
        softly.assertThat(json).contains("\"hasData\": true");
        assertThatJson(json).node("cerebrovascular.detailsOnDemandColumns").isEqualTo(newArrayList("column1", "column2"));
        assertThatJson(json).node("cerebrovascular." + AVAILABLE_BAR_LINE_YAXIS_OPTIONS).isEqualTo(newArrayList("COUNT_START_DATES_ONLY"));
        assertThatJson(json).node("cerebrovascular." + AVAILABLE_YAXIS_OPTIONS).isEqualTo(newArrayList(
                "COUNT_OF_SUBJECTS",
                "COUNT_OF_EVENTS",
                "PERCENTAGE_OF_ALL_SUBJECTS",
                "PERCENTAGE_OF_ALL_EVENTS",
                "PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT",
                "PERCENTAGE_OF_EVENTS_WITHIN_PLOT"));
    }
}
