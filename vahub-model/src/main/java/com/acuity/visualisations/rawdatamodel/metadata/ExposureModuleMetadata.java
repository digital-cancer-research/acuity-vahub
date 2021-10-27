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
import com.acuity.visualisations.rawdatamodel.service.ssv.ColorInitializer;
import com.acuity.visualisations.rawdatamodel.trellis.TrellisOptions;
import com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions;
import com.acuity.visualisations.rawdatamodel.util.TrellisUtil;
import com.acuity.visualisations.rawdatamodel.vo.ExposureRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Exposure;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.ANALYTE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.CYCLE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.DOSE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.DOSE_PER_CYCLE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.DOSE_PER_VISIT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.SUBJECT;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.SUBJECT_CYCLE;
import static com.acuity.visualisations.rawdatamodel.trellis.grouping.ExposureGroupByOptions.VISIT;
import static java.util.stream.Collectors.toList;

@Service
public class ExposureModuleMetadata extends AbstractModuleColoringMetadata<ExposureRaw, Exposure> {
    private static final String HAS_DOSES_MAPPED = "hasDosesMapped";
    private static final String HAS_DAYS_MAPPED = "hasDaysMapped";
    private static final String HAS_VISITS_MAPPED = "hasVisitsMapped";
    private static final String AGGREGATION_TYPES = "aggregationTypes";

    @Autowired
    @Qualifier("exposureService")
    private ColorInitializer colorInitializer;

    @Override
    protected String tab() {
        return "exposure";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        Collection<Exposure> data = getData(datasets);
        metadataItem = super.buildMetadataItem(metadataItem, datasets, data);
        final boolean hasDaysMapped = data.stream().anyMatch(e -> e.getEvent().getProtocolScheduleDay() != null);
        final List<ExposureGroupByOptions> availableAggregationOptions = TrellisUtil.getTrellisOptions(data,
                SUBJECT_CYCLE, SUBJECT, ANALYTE, DOSE, VISIT, CYCLE).stream()
                .map(TrellisOptions::getTrellisedBy)
                .collect(toList());
        addComplexAggregationOptions(availableAggregationOptions, Arrays.asList(DOSE_PER_VISIT, DOSE_PER_CYCLE));
        metadataItem.addProperty(HAS_DAYS_MAPPED, hasDaysMapped);
        metadataItem.addProperty(HAS_DOSES_MAPPED, availableAggregationOptions.contains(DOSE));
        metadataItem.addProperty(HAS_VISITS_MAPPED, availableAggregationOptions.contains(VISIT));
        metadataItem.add(AGGREGATION_TYPES, availableAggregationOptions);
        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.addProperty(HAS_DOSES_MAPPED, false);
        metadataItem.addProperty(HAS_DAYS_MAPPED, false);
        metadataItem.addProperty(HAS_VISITS_MAPPED, false);
        metadataItem.add(AGGREGATION_TYPES, Collections.emptyList());
        return metadataItem;
    }

    /**
     * This method adds complex aggregation option only if all parts of the option are present in availableAggregationOptions list.
     * eg. adds DOSE_PER_CYCLE only if DOSE and CYCLE options are present.
     *
     * @param availableAggregationOptions - available non-complex aggregation options (eg. DOSE, CYCLE, etc)
     * @param complexOptions              - options composed from several options(eg. DOSE_PER_CYCLE)
     */
    private void addComplexAggregationOptions(List<ExposureGroupByOptions> availableAggregationOptions, List<ExposureGroupByOptions> complexOptions) {
        complexOptions.forEach(aggregationOption -> {
            final List<ExposureGroupByOptions> parts = Arrays.stream(aggregationOption.toString().split("_PER_"))
                    .map(ExposureGroupByOptions::valueOf)
                    .collect(toList());
            if (availableAggregationOptions.containsAll(parts)) {
                availableAggregationOptions.add(aggregationOption);
            }
        });

    }

    @Override
    ColorInitializer getColorInitializer() {
        return colorInitializer;
    }
}
