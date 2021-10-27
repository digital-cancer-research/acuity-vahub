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
import com.acuity.visualisations.common.study.metadata.ModuleMetadata;
import com.acuity.visualisations.rawdatamodel.service.StudyInfoService;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

import static com.acuity.visualisations.rawdatamodel.util.DaysUtil.toDateTimeString;


@Service
public class StudyInfoMetadata implements ModuleMetadata {

    @Autowired
    private StudyInfoService studyInfoService;

    private static final String STUDY_INFO_KEY = "studyInfo";

    @Override
    public MetadataItem getMetadataItem(Datasets datasets) {
        MetadataItem metadataItem = new MetadataItem(STUDY_INFO_KEY);
        Optional<StudyInfo> studyInfo = studyInfoService.getStudyInfo(datasets);
        Date lastUpdatedDate = studyInfo.map(StudyInfo::getLastUpdatedDate)
                .orElseThrow(() -> new RuntimeException(String.format(
                        "Unable to build metadata for datasets %s: %s metadata cannot be built",
                        datasets.getIdsAsString(), STUDY_INFO_KEY)));
        metadataItem.addProperty("lastUpdatedDate", toDateTimeString(lastUpdatedDate));
        return metadataItem;
    }

    @Override
    public MetadataItem getNonMergeableMetadataItem(Datasets datasets) {
        return getMetadataItem(datasets);
    }
}
