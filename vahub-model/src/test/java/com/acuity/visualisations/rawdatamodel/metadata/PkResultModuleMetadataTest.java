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
import com.acuity.visualisations.rawdatamodel.axes.AxisOption;
import com.acuity.visualisations.rawdatamodel.axes.AxisOptions;
import com.acuity.visualisations.rawdatamodel.dataproviders.PkResultDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PkResultFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.service.event.PkResultService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.acuity.visualisations.rawdatamodel.util.Column.DatasetType;
import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_YAXIS_OPTIONS;
import static com.google.common.collect.Lists.newArrayList;
import static net.javacrumbs.jsonunit.fluent.JsonFluentAssert.assertThatJson;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

public class PkResultModuleMetadataTest {

    @InjectMocks
    private PkResultModuleMetadata pkResultModuleMetadata;
    @Mock
    private PkResultDatasetsDataProvider pkResultDatasetsDataProvider;
    @Mock
    private DoDCommonService doDCommonService;
    @Mock
    private PkResultService pkResultService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        pkResultModuleMetadata.datasetsDataProvider = newArrayList(pkResultDatasetsDataProvider);
    }

    @Test
    public void shouldGetMetadataContainsOnlyVisits() {
        when(pkResultDatasetsDataProvider.loadData(DUMMY_ACUITY_DATASETS)).thenReturn(newArrayList(
                new PkResult(PkResultRaw.builder().visit("Cycle 1 Day 100").build(), new Subject()),
                new PkResult(PkResultRaw.builder().visit("Cycle 2 Day 1").build(), new Subject()),
                new PkResult(new PkResultRaw(), new Subject())));
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollectionOf(PkResult.class))).thenReturn(new HashMap<>());
        when(pkResultService.getAvailableBoxPlotXAxis(any(Datasets.class), eq(PkResultFilters.empty()), eq(PopulationFilters.empty())))
                .thenReturn(new AxisOptions<>(Collections.singletonList(new AxisOption<>(PkResultGroupByOptions.DOSE)), true, null));

        MetadataItem metadataItem = pkResultModuleMetadata.getMetadataItem(DUMMY_ACUITY_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("pkResult");
        String json = metadataItem.build();
        softly.assertThat(json).contains("\"count\": 3");
        softly.assertThat(json).contains("\"hasData\": true");
        softly.assertThat(json).contains("\"timepointType\": \"VISIT\"");
        assertThatJson(json).node("pkResult." + AVAILABLE_YAXIS_OPTIONS).isEqualTo(newArrayList(
                "PARAMETER_VALUE"));
    }

    @Test
    public void shouldGetMetadataEmptyTimepoints()  {
        when(pkResultDatasetsDataProvider.loadData(DUMMY_ACUITY_DATASETS)).thenReturn(newArrayList(
                new PkResult(PkResultRaw.builder().treatmentCycle("Cycle 0").build(), new Subject()),
                new PkResult(new PkResultRaw(), new Subject()),
                new PkResult(new PkResultRaw(), new Subject())));
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollectionOf(PkResult.class))).thenReturn(new HashMap<>());

        MetadataItem metadataItem = pkResultModuleMetadata.getMetadataItem(DUMMY_ACUITY_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("pkResult");
        String json = metadataItem.build();
        softly.assertThat(json).contains("\"timepointType\": \"null\"");
        softly.assertThat(json).contains("\"hasData\": false");
    }

    @Test
    public void shouldGetMetadataContainsOnlyDaysAndCycles() {
        when(pkResultDatasetsDataProvider.loadData(DUMMY_ACUITY_DATASETS)).thenReturn(newArrayList(
                new PkResult(PkResultRaw.builder().treatmentCycle("Cycle 0").build(), new Subject()),
                new PkResult(PkResultRaw.builder().protocolScheduleStartDay("1").build(), new Subject()),
                new PkResult(new PkResultRaw(), new Subject())));
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollectionOf(PkResult.class))).thenReturn(new HashMap<>());
        when(pkResultService.getAvailableBoxPlotXAxis(any(Datasets.class), eq(PkResultFilters.empty()), eq(PopulationFilters.empty())))
                .thenReturn(new AxisOptions<>(Collections.singletonList(new AxisOption<>(PkResultGroupByOptions.DOSE)), true, null));

        MetadataItem metadataItem = pkResultModuleMetadata.getMetadataItem(DUMMY_ACUITY_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("pkResult");
        String json = metadataItem.build();
        softly.assertThat(json).contains("\"timepointType\": \"CYCLE_DAY\"");
        softly.assertThat(json).contains("\"hasData\": true");
    }

    @Test
    public void shouldGetMetadataNoData() {
        when(pkResultDatasetsDataProvider.loadData(DUMMY_ACUITY_DATASETS)).thenReturn(new ArrayList<>());
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollectionOf(PkResult.class))).thenReturn(new HashMap<>());

        MetadataItem metadataItem = pkResultModuleMetadata.getMetadataItem(DUMMY_ACUITY_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("pkResult");
        String json = metadataItem.build();
        softly.assertThat(json).contains("\"count\": 0");
        softly.assertThat(json).contains("\"hasData\": false");
        assertThatJson(json).node("pkResult." + AVAILABLE_YAXIS_OPTIONS).isEqualTo(newArrayList(
                "PARAMETER_VALUE"));
    }

    @Test
    public void shouldHasNoDataWhenEmptyXAxis() {
        when(pkResultDatasetsDataProvider.loadData(DUMMY_ACUITY_DATASETS))
                .thenReturn(Collections.singletonList(new PkResult(PkResultRaw.builder().visit("Cycle 2 Day 1").build(), new Subject())));
        when(pkResultService.getAvailableBoxPlotXAxis(any(Datasets.class), eq(PkResultFilters.empty()), eq(PopulationFilters.empty())))
                .thenReturn(new AxisOptions<>(Collections.emptyList(), true, null));

        MetadataItem metadataItem = pkResultModuleMetadata.getMetadataItem(DUMMY_ACUITY_DATASETS);

        softly.assertThat(metadataItem.getKey()).isEqualTo("pkResult");
        String json = metadataItem.build();
        softly.assertThat(json).contains("\"count\": 1");
        softly.assertThat(json).contains("\"hasData\": false");
    }
}
