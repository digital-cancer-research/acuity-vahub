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
import com.acuity.visualisations.rawdatamodel.axes.CountType;
import com.acuity.visualisations.rawdatamodel.dataproviders.AeIncidenceDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.dataproviders.common.DatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.vo.AeRaw;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.Ae;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_BAR_LINE_YAXIS_OPTIONS;
import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_YAXIS_OPTIONS;
import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by knml167 on 5/22/2017.
 */
@Service
public class AeModuleMetadata extends AbstractModuleMetadata<AeRaw, Ae> {

    @Autowired
    protected AeIncidenceDatasetsDataProvider aeIncidenceDatasetsDataProvider;

    @Override
    protected DatasetsDataProvider<AeRaw, Ae> getEventDataProvider() {
        return aeIncidenceDatasetsDataProvider;
    }

    @Override
    public MetadataItem getNonMergeableMetadataItem(Datasets datasets) {
        return getMetadataItem(datasets);
    }

    @Override
    protected String tab() {
        return "aes";
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        Collection<Ae> data = getEventDataProvider().loadData(datasets);
        metadataItem = super.buildMetadataItem(metadataItem, datasets, data);
        metadataItem.addProperty("hasCustomGroups", data.stream()
                .filter(ae -> ae.getEvent() != null && ae.getEvent().getSpecialInterestGroups() != null)
                .flatMap(ae -> ae.getEvent().getSpecialInterestGroups().stream())
                .filter(Objects::nonNull).distinct().count() > 0);
        String aeSeverityType = data.stream()
                .filter(ae -> ae.getEvent() != null && ae.getEvent().getAeSeverities() != null)
                .flatMap(ae -> ae.getEvent().getAeSeverities().stream())
                .filter(Objects::nonNull)
                .anyMatch(aes -> {
                    final String severity = aes.getSeverity() == null ? null : aes.getSeverity().getSeverity();
                    return severity != null && severity.toUpperCase().startsWith("CTC GRADE");
                }) ? "CTC_GRADES" : "AE_INTENSITY";
        metadataItem.addProperty("aeSeverityType", aeSeverityType);
        metadataItem.add(AVAILABLE_YAXIS_OPTIONS, Arrays.stream(CountType.values()).map(Enum::toString).collect(Collectors.toList()));
        metadataItem.add(AVAILABLE_BAR_LINE_YAXIS_OPTIONS, newArrayList("COUNT_INCLUDING_DURATION", "COUNT_START_DATES_ONLY"));
        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        metadataItem = super.buildErrorMetadataItem(metadataItem);
        metadataItem.add(AVAILABLE_YAXIS_OPTIONS, new ArrayList<>());
        return metadataItem;
    }
}
