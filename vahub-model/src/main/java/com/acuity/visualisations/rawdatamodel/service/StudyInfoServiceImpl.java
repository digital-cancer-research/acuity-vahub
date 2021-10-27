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

package com.acuity.visualisations.rawdatamodel.service;

import com.acuity.visualisations.rawdatamodel.dao.StudyInfoRepository;
import com.acuity.visualisations.rawdatamodel.vo.StudyInfo;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuityObjectIdentityImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Comparator;
import java.util.Optional;

@Service
public class StudyInfoServiceImpl implements StudyInfoService {
    @Autowired
    private StudyInfoRepository studyInfoRepository;

    @Override
    public Optional<StudyInfo> getStudyInfo(Datasets datasets) {
        return datasets.getDatasets().stream()
                .map(AcuityObjectIdentityImpl::getId)
                .map(studyInfoRepository::getRawData)
                .flatMap(Collection::stream).max(Comparator.comparing(StudyInfo::getLastUpdatedDate));
    }
}
