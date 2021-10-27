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
import com.acuity.visualisations.rawdatamodel.dataproviders.LungFunctionDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.LungFunctionRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.LungFunction;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class LungFunctionModuleMetadataTest {
    @InjectMocks
    private LungFunctionModuleMetadata moduleMetadata;

    @Mock
    private LungFunctionDatasetsDataProvider datasetsDataProvider;

    @Mock
    private DoDCommonService doDCommonService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        moduleMetadata.datasetsDataProvider = Collections.singletonList(datasetsDataProvider);
    }

    @Test
    public void shouldGetMetadata() {
        final HashMap<String, String> cols = new HashMap<>();
        cols.put("column1", "title 1");
        cols.put("column2", "title 2");
        when(doDCommonService.getDoDColumns(any(Column.DatasetType.class), anyCollection())).thenReturn(cols);


        when(datasetsDataProvider.loadData(any(Datasets.class))).thenReturn(Arrays.asList(
                new LungFunction(LungFunctionRaw.builder().build(), Subject.builder().build()),
                new LungFunction(LungFunctionRaw.builder().build(), Subject.builder().build())
        ));

        MetadataItem result = moduleMetadata.getMetadataItem(Constants.DATASETS);
        softly.assertThat(result.getKey()).isEqualTo("lungFunction-java");

        String json = result.build();
        softly.assertThat(json).contains("\"count\": 2");
        softly.assertThat(json).contains("\"hasData\": true");
        assertThatJson(json).node("lungFunction-java.detailsOnDemandColumns").isEqualTo(Arrays.asList("column1", "column2"));

        assertThatJson(json).node("lungFunction-java.availableYAxisOptions").isEqualTo(Arrays.asList(
                "ACTUAL_VALUE",
                "ABSOLUTE_CHANGE_FROM_BASELINE",
                "PERCENTAGE_CHANGE_FROM_BASELINE"
        ));

    }
}
