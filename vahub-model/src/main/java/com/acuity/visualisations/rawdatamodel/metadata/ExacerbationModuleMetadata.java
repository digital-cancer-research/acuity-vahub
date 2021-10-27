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
import com.acuity.visualisations.rawdatamodel.vo.ExacerbationRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exacerbation;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

import static com.acuity.visualisations.rawdatamodel.axes.BinCountType.COUNT_INCLUDING_DURATION;
import static com.acuity.visualisations.rawdatamodel.axes.BinCountType.COUNT_START_DATES_ONLY;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.COUNT_OF_EVENTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.COUNT_OF_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.CUMULATIVE_COUNT_OF_EVENTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.CUMULATIVE_COUNT_OF_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_ALL_EVENTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_ALL_SUBJECTS;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_EVENTS_WITHIN_PLOT;
import static com.acuity.visualisations.rawdatamodel.axes.CountType.PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT;

@Service
public class ExacerbationModuleMetadata extends AbstractModuleMetadata<ExacerbationRaw, Exacerbation> {

    @Override
    protected String tab() {
        return "exacerbation";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        metadataItem = super.buildMetadataItem(metadataItem, datasets);
        metadataItem.add("availableYAxisOptionsForGroupedBarChart", Arrays.asList(
                COUNT_OF_SUBJECTS,
                COUNT_OF_EVENTS,
                PERCENTAGE_OF_ALL_SUBJECTS,
                PERCENTAGE_OF_ALL_EVENTS,
                PERCENTAGE_OF_SUBJECTS_WITHIN_PLOT,
                PERCENTAGE_OF_EVENTS_WITHIN_PLOT));
        metadataItem.add("availableOverTimeChartYAxisOptions", Arrays.asList(COUNT_INCLUDING_DURATION,
                COUNT_START_DATES_ONLY));

        metadataItem.add("availableYAxisOptionsForLineChart", Arrays.asList(
                COUNT_OF_EVENTS,
                CUMULATIVE_COUNT_OF_EVENTS,
                COUNT_OF_SUBJECTS,
                CUMULATIVE_COUNT_OF_SUBJECTS
        ));

        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.add("availableYAxisOptionsForGroupedBarChart", Collections.emptyList());
        metadataItem.add("availableOverTimeChartYAxisOptions", Collections.emptyList());
        metadataItem.add("availableYAxisOptionsForLineChart", Collections.emptyList());
        return metadataItem;
    }
}
