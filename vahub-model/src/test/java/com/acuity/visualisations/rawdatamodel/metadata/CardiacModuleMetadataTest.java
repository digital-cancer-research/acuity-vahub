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
import com.acuity.visualisations.rawdatamodel.dataproviders.CardiacDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.event.AeService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.util.Column;
import com.acuity.visualisations.rawdatamodel.vo.CardiacRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Cardiac;
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

import static com.acuity.visualisations.rawdatamodel.Constants.DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CardiacModuleMetadataTest {
    @InjectMocks
    private CardiacModuleMetadata moduleMetadata;
    @Mock
    private CardiacDatasetsDataProvider datasetsDataProvider;
    @Mock
    private DoDCommonService tableService;
    @Mock
    private AeService aeService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        moduleMetadata.datasetsDataProvider = newArrayList(datasetsDataProvider);
    }

    @Test
    public void shouldGetMetadata() {
        when(datasetsDataProvider.loadData(any(Datasets.class))).thenReturn(newArrayList(
                new Cardiac(CardiacRaw.builder().build(), Subject.builder().build()),
                new Cardiac(CardiacRaw.builder().build(), Subject.builder().build())));
        final HashMap<String, String> cols = new HashMap<>();
        cols.put("column1", "title 1");
        cols.put("column2", "title 2");
        when(tableService.getDoDColumns(any(Column.DatasetType.class), anyCollection())).thenReturn(cols);

        when(aeService.getJumpToAesSocs(any(), any())).thenReturn(Collections.singleton("CARD"));

        MetadataItem result = moduleMetadata.getMetadataItem(DATASETS);

        softly.assertThat(result.getKey()).isEqualTo("cardiac");
        String json = result.build();
        softly.assertThat(json).contains("\"count\": 2");
        softly.assertThat(json).contains("\"hasData\": true");
        assertThatJson(json).node("cardiac.socs").isEqualTo(Arrays.asList(
                "CARD"
        ));
        assertThatJson(json).node("cardiac.availableYAxisOptions").
                isEqualTo(Arrays.asList(
                        "ACTUAL_VALUE",
                        "ABSOLUTE_CHANGE_FROM_BASELINE",
                        "PERCENTAGE_CHANGE_FROM_BASELINE"
                ));
    }
}
