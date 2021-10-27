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
import com.acuity.visualisations.rawdatamodel.dataproviders.VitalDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.VitalRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Vital;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions.ABSOLUTE_CHANGE_FROM_BASELINE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions.ACTUAL_VALUE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.VitalGroupByOptions.PERCENTAGE_CHANGE_FROM_BASELINE;

/**
 * Created by knml167 on 5/22/2017.
 */
@Service
public class VitalModuleMetadata extends AbstractModuleMetadata<VitalRaw, Vital> {

    @Autowired
    private VitalDatasetsDataProvider vitalDatasetsDataProvider;

    @Override
    protected DatasetsDataProvider<VitalRaw, Vital> getEventDataProvider() {
        return vitalDatasetsDataProvider;
    }

    @Override
    protected String tab() {
        return "vitals-java";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        metadataItem = super.buildMetadataItem(metadataItem, datasets);
        metadataItem.add("availableYAxisOptionsForMeanRangePlot", Arrays.asList(ABSOLUTE_CHANGE_FROM_BASELINE,
                PERCENTAGE_CHANGE_FROM_BASELINE,
                ACTUAL_VALUE));
        metadataItem.add("availableMeasurementsOverTimeChartYAxisOptions", Arrays.asList(
                ACTUAL_VALUE,
                ABSOLUTE_CHANGE_FROM_BASELINE,
                PERCENTAGE_CHANGE_FROM_BASELINE
        ));
        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.add("availableYAxisOptionsForMeanRangePlot", Collections.emptyList());
        metadataItem.add("availableMeasurementsOverTimeChartYAxisOptions", Collections.emptyList());
        return metadataItem;
    }
}
