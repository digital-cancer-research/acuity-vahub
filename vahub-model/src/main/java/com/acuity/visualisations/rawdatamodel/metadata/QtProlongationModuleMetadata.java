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
import com.acuity.visualisations.rawdatamodel.dao.StudyInfoRepository;
import com.acuity.visualisations.rawdatamodel.vo.QtProlongationRaw;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfoAdministrationDetail;
import com.acuity.visualisations.rawdatamodel.vo.wrappers.QtProlongation;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.acuity.visualisations.rawdatamodel.util.Constants.AVAILABLE_YAXIS_OPTIONS;
import static com.google.common.collect.Lists.newArrayList;

@Service
public class QtProlongationModuleMetadata extends AbstractModuleMetadata<QtProlongationRaw, QtProlongation> {
    private static final String QT_PROLONGATION_INFO_KEY = "qt-prolongation";

    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Override
    protected String tab() {
        return QT_PROLONGATION_INFO_KEY;
    }

    @Override
    protected MetadataItem buildMetadataItem(MetadataItem metadataItem, Datasets datasets) {
        List<Long> datasetIdsWithAmlEnabled = studyInfoRepository.getStudyInfoByDatasetIds(datasets.getIds()).stream()
                .filter(StudyInfoAdministrationDetail::getAmlEnabled)
                .map(StudyInfoAdministrationDetail::getStudyId).collect(Collectors.toList());
        if (datasetIdsWithAmlEnabled.isEmpty()) {
            metadataItem = buildErrorMetadataItem(metadataItem);
        } else {
            Datasets ds = Datasets.toAcuityDataset(datasetIdsWithAmlEnabled);
            metadataItem = super.buildMetadataItem(metadataItem, ds);
            metadataItem.add(AVAILABLE_YAXIS_OPTIONS, newArrayList(CountType.COUNT_OF_EVENTS));
        }
        return metadataItem;
    }

    @Override
    protected MetadataItem buildErrorMetadataItem(MetadataItem metadataItem) {
        return super.buildErrorMetadataItem(metadataItem)
                    .add(AVAILABLE_YAXIS_OPTIONS, Collections.emptyList());
    }
}
