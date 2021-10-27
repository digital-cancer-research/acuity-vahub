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
import com.acuity.visualisations.rawdatamodel.trellis.grouping.PkResultGroupByOptions;
import com.acuity.visualisations.rawdatamodel.vo.PkResultRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.PkResult;
import com.acuity.va.security.acl.domain.Datasets;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_YAXIS_OPTIONS;

public abstract class PkResultAbstractModuleMetadata extends AbstractModuleMetadata<PkResultRaw, PkResult> {

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        Collection<PkResult> data = getData(datasets);
        final MetadataItem metadata = super.buildMetadataItem(metadataItem, datasets, data);

        final boolean hasNotEmptyCycleAndDay = data.stream().map(t -> t.getEvent().getTreatmentCycle()).anyMatch(Objects::nonNull)
                && data.stream().map(t -> t.getEvent().getProtocolScheduleStartDay()).anyMatch(Objects::nonNull);
        final boolean hasNotEmptyVisit = data.stream().map(t -> t.getEvent().getVisit()).anyMatch(Objects::nonNull);
        final boolean hasNotEmptyVisitNumber = data.stream().map(t -> t.getEvent().getVisitNumber()).anyMatch(Objects::nonNull);

        final boolean hasData = (hasNotEmptyCycleAndDay || hasNotEmptyVisit || hasNotEmptyVisitNumber) && hasXAxis(datasets);

        final PkResultGroupByOptions timepointType = hasNotEmptyCycleAndDay ? PkResultGroupByOptions.CYCLE_DAY
                : hasNotEmptyVisit ? PkResultGroupByOptions.VISIT
                : hasNotEmptyVisitNumber ? PkResultGroupByOptions.VISIT_NUMBER : null;

        metadataItem
                .addProperty("hasData", hasData)
                .addProperty("timepointType", String.valueOf(timepointType));
        metadata.add(AVAILABLE_YAXIS_OPTIONS, Collections.singletonList(PkResultGroupByOptions.PARAMETER_VALUE));
        return metadataItem;
    }

    @Override
    protected void enrichWithDataMetadata(MetadataItem metadataItem, int eventsCount) {
        metadataItem.addProperty("count", eventsCount);
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        return super.buildErrorMetadataItem(metadataItem)
                .add(AVAILABLE_YAXIS_OPTIONS, Collections.emptyList());
    }

    protected  abstract boolean hasXAxis(Datasets datasets);

}
