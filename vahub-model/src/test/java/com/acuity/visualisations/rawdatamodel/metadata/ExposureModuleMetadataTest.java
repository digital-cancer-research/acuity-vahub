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
import com.acuity.visualisations.rawdatamodel.dataproviders.ExposureDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.service.event.ExposureService;
import com.acuity.visualisations.rawdatamodel.service.dod.DoDCommonService;
import com.acuity.visualisations.rawdatamodel.vo.ExposureRaw;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.visualisations.rawdatamodel.vo.exposure.Cycle;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
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
import static com.google.common.collect.Lists.newArrayList;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Mockito.when;

public class ExposureModuleMetadataTest {

    @InjectMocks
    private ExposureModuleMetadata exposureModuleMetadata;
    @Mock
    private ExposureDatasetsDataProvider exposureDatasetsDataProvider;
    @Mock
    private DoDCommonService doDCommonService;
    @Mock
    private ExposureService exposureService;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();
    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        exposureModuleMetadata.datasetsDataProvider = newArrayList(exposureDatasetsDataProvider);
    }

    private static final String AGGREGATION_TYPES = "aggregationTypes";

    @Test
    public void shouldGetMetadata() {
        when(exposureDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList(
                new Exposure(ExposureRaw.builder().cycle(Cycle.builder().isNotAllDrugDatesEmpty(false).build()).build(), new Subject()),
                new Exposure(ExposureRaw.builder().cycle(Cycle.builder().isNotAllDrugDatesEmpty(false).build()).build(), new Subject()),
                new Exposure(ExposureRaw.builder().cycle(Cycle.builder().isNotAllDrugDatesEmpty(false).build()).build(), new Subject())));
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());

        MetadataItem metadataItem = exposureModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("exposure");
        softly.assertThat(metadataItem.build()).contains("\"count\": 3");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": true");
        softly.assertThat(metadataItem.build()).contains("\"hasDaysMapped\": false");
        softly.assertThat(metadataItem.build()).contains("\"hasDosesMapped\": false");
        softly.assertThat(metadataItem.build()).contains("\"hasVisitsMapped\": false");

        final JsonArray aggregationTypesAsJson = metadataItem.getItemObject().getAsJsonArray(AGGREGATION_TYPES);
        final ArrayList aggregationTypes = new Gson().fromJson(aggregationTypesAsJson, ArrayList.class);
        softly.assertThat(aggregationTypes).containsExactly("SUBJECT_CYCLE");
    }

    @Test
    public void shouldGetMetadataWithAllFieldsMapped() {
        when(exposureDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList(
                new Exposure(ExposureRaw.builder().protocolScheduleDay(1).analyte("AZD").visitNumber(1).treatment("50 mg")
                        .cycle(Cycle.builder().analyte("AZD").visit(1).isNotAllDrugDatesEmpty(false).build())
                        .build(), Subject.builder().subjectId("id1").subjectCode("E001").build()),
                new Exposure(ExposureRaw.builder().protocolScheduleDay(1).treatmentCycle("Cycle 0")
                        .cycle(Cycle.builder().treatmentCycle("Cycle 0").isNotAllDrugDatesEmpty(false).build()).build(), new Subject()),
                new Exposure(ExposureRaw.builder().protocolScheduleDay(1)
                        .cycle(Cycle.builder().isNotAllDrugDatesEmpty(false).build()).build(), new Subject())));
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());

        MetadataItem metadataItem = exposureModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("exposure");
        softly.assertThat(metadataItem.build()).contains("\"count\": 3");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": true");
        softly.assertThat(metadataItem.build()).contains("\"hasDaysMapped\": true");
        softly.assertThat(metadataItem.build()).contains("\"hasDosesMapped\": true");
        softly.assertThat(metadataItem.build()).contains("\"hasVisitsMapped\": true");

        final JsonArray aggregationTypesAsJson = metadataItem.getItemObject().getAsJsonArray(AGGREGATION_TYPES);
        final ArrayList aggregationTypes = new Gson().fromJson(aggregationTypesAsJson, ArrayList.class);
        softly.assertThat(aggregationTypes).containsExactly(
                "SUBJECT_CYCLE", "SUBJECT", "ANALYTE", "DOSE", "VISIT", "CYCLE", "DOSE_PER_VISIT", "DOSE_PER_CYCLE");
    }

    @Test
    public void shouldGetMetadataOnlyVisitsMapped() {
        when(exposureDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList(
                new Exposure(ExposureRaw.builder().protocolScheduleDay(1).visitNumber(1)
                        .cycle(Cycle.builder().visit(1).isNotAllDrugDatesEmpty(false).build())
                        .build(), Subject.builder().subjectId("id1").subjectCode("E001").build()),
                new Exposure(ExposureRaw.builder().analyte("AZD").protocolScheduleDay(1)
                        .cycle(Cycle.builder().analyte("AZD").isNotAllDrugDatesEmpty(false).build()).build(), new Subject()),
                new Exposure(ExposureRaw.builder().protocolScheduleDay(1)
                        .cycle(Cycle.builder().isNotAllDrugDatesEmpty(false).build()).build(), new Subject())));
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());

        MetadataItem metadataItem = exposureModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);

        final JsonArray aggregationTypesAsJson = metadataItem.getItemObject().getAsJsonArray(AGGREGATION_TYPES);
        final ArrayList aggregationTypes = new Gson().fromJson(aggregationTypesAsJson, ArrayList.class);
        softly.assertThat(aggregationTypes).containsExactly("SUBJECT_CYCLE", "SUBJECT", "ANALYTE", "VISIT");
    }
    @Test
    public void shouldGetMetadataVisitsAndDosesMapped() {
        when(exposureDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList(
                new Exposure(ExposureRaw.builder().protocolScheduleDay(1).visitNumber(1).treatment("10 mg")
                        .cycle(Cycle.builder().visit(1).isNotAllDrugDatesEmpty(false).build())
                        .build(), Subject.builder().subjectId("id1").subjectCode("E001").build()),
                new Exposure(ExposureRaw.builder().analyte("AZD").protocolScheduleDay(1)
                        .cycle(Cycle.builder().analyte("AZD").isNotAllDrugDatesEmpty(false).build()).build(), new Subject()),
                new Exposure(ExposureRaw.builder().protocolScheduleDay(1)
                        .cycle(Cycle.builder().isNotAllDrugDatesEmpty(false).build()).build(), new Subject())));
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());

        MetadataItem metadataItem = exposureModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);

        final JsonArray aggregationTypesAsJson = metadataItem.getItemObject().getAsJsonArray(AGGREGATION_TYPES);
        final ArrayList aggregationTypes = new Gson().fromJson(aggregationTypesAsJson, ArrayList.class);
        softly.assertThat(aggregationTypes).containsExactly("SUBJECT_CYCLE", "SUBJECT", "ANALYTE", "DOSE", "VISIT", "DOSE_PER_VISIT");
    }

    @Test
    public void shouldGetMetadataNoVisitsNoCycles() {
        when(exposureDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(newArrayList(
                new Exposure(ExposureRaw.builder().cycle(Cycle.builder().isNotAllDrugDatesEmpty(false).build())
                        .protocolScheduleDay(1).treatment("10 mg").build(), Subject.builder().subjectId("id1").subjectCode("E001").build()),
                new Exposure(ExposureRaw.builder().analyte("AZD").protocolScheduleDay(1)
                        .cycle(Cycle.builder().analyte("AZD").isNotAllDrugDatesEmpty(false).build()).build(), new Subject()),
                new Exposure(ExposureRaw.builder().protocolScheduleDay(1)
                        .cycle(Cycle.builder().isNotAllDrugDatesEmpty(false).build()).build(), new Subject())));
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());

        MetadataItem metadataItem = exposureModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);

        final JsonArray aggregationTypesAsJson = metadataItem.getItemObject().getAsJsonArray(AGGREGATION_TYPES);
        final ArrayList aggregationTypes = new Gson().fromJson(aggregationTypesAsJson, ArrayList.class);
        softly.assertThat(aggregationTypes).containsExactly("SUBJECT_CYCLE", "SUBJECT", "ANALYTE", "DOSE");
    }

    @Test
    public void shouldGetMetadataNoData() {
        when(exposureDatasetsDataProvider.loadData(DUMMY_DETECT_DATASETS)).thenReturn(new ArrayList<>());
        when(doDCommonService.getDoDColumns(any(DatasetType.class), anyCollection())).thenReturn(new HashMap());

        MetadataItem metadataItem = exposureModuleMetadata.getMetadataItem(DUMMY_DETECT_DATASETS);
        softly.assertThat(metadataItem.getKey()).isEqualTo("exposure");
        softly.assertThat(metadataItem.build()).contains("\"count\": 0");
        softly.assertThat(metadataItem.build()).contains("\"hasData\": false");
        softly.assertThat(metadataItem.build()).contains("\"hasDaysMapped\": false");
        softly.assertThat(metadataItem.build()).contains("\"hasDosesMapped\": false");
        softly.assertThat(metadataItem.build()).contains("\"hasVisitsMapped\": false");

        final JsonArray aggregationTypesAsJson = metadataItem.getItemObject().getAsJsonArray(AGGREGATION_TYPES);
        final ArrayList aggregationTypes = new Gson().fromJson(aggregationTypesAsJson, ArrayList.class);
        softly.assertThat(aggregationTypes).isEmpty();
    }
}
