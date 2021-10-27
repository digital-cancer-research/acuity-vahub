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
import com.acuity.visualisations.rawdatamodel.dataproviders.VitalDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.VitalRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.acuity.va.security.acl.domain.Datasets;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;

import static com.google.common.collect.Lists.newArrayList;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VitalModuleMetadataTest {
    @InjectMocks
    private VitalModuleMetadata moduleMetadata;
    @Mock
    private VitalDatasetsDataProvider datasetsDataProvider;
    @Mock
    private DoDCommonService doDCommonService;

    @Before
    public void setUp() {
        moduleMetadata.datasetsDataProvider = newArrayList(datasetsDataProvider);
    }

    @Test
    public void shouldGetMetadata() {
        //Given
        when(datasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(
                new Vital(new VitalRaw(), new Subject()),
                new Vital(new VitalRaw(), new Subject())));
        final HashMap<String, String> cols = new HashMap<>();
        cols.put("column1", "title 1");
        cols.put("column2", "title 2");
        when(doDCommonService.getDoDColumns(any(Column.DatasetType.class), anyCollection())).thenReturn(cols);

        //When
        MetadataItem result = moduleMetadata.getMetadataItem(Constants.DATASETS);

        //Then
        assertThat(result.getKey()).isEqualTo("vitals-java");
        String json = result.build();
        assertThat(json).contains("\"count\": 2");
        assertThat(json).contains("\"hasData\": true");
        assertThatJson(json).node("vitals-java.detailsOnDemandColumns").isEqualTo(newArrayList("column1", "column2"));
//        assertThatJson(json).node("labs-java." + AVAILABLE_YAXIS_OPTIONS).isEqualTo(newArrayList(
//                "REF_RANGE_NORM_VALUE",
//                "TIMES_UPPER_REF_VALUE",
//                "TIMES_LOWER_REF_VALUE",
//                "ABSOLUTE_CHANGE_FROM_BASELINE",
//                "PERCENTAGE_CHANGE_FROM_BASELINE",
//                "ACTUAL_VALUE"));
    }
}
