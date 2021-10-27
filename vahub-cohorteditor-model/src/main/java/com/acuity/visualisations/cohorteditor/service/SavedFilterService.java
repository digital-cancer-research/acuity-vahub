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

package com.acuity.visualisations.cohorteditor.service;

import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import com.acuity.visualisations.cohorteditor.repository.SavedFilterRepository;
import com.acuity.visualisations.cohorteditor.util.SavedFilterVOConverter;
import com.acuity.visualisations.cohorteditor.vo.SavedFilterVO;
import com.acuity.visualisations.common.util.Security;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import java.util.List;
import java.util.Set;

import com.acuity.va.security.auth.common.ISecurityResourceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import static com.acuity.va.security.acl.permissions.AcuityPermissions.VIEW_VISUALISATIONS;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@Transactional(readOnly = true)
public class SavedFilterService {

    @Autowired
    private SavedFilterRepository savedFilterRepository;
    @Autowired
    private CohortSubjectService cohortSubjectService;
    @Autowired
    private Security security;
    @Autowired
    private SavedFilterVOConverter savedFilterVOConverter;
    @Autowired
    private ISecurityResourceClient securityResourceClient;

    public List<String> getDistinctSubjects(Datasets datasets, Long savedFilterId) {
        SavedFilter savedFilter = savedFilterRepository.loadTreeById(savedFilterId);
        return cohortSubjectService.getDistinctSubjectIds(datasets, savedFilter.getFilters(), savedFilter.getOperator());
    }

    public List<SavedFilterVO> listByUserAndDatasets(List<Dataset> datasets) {
        String user = security.getUser();

        Set<String> datasetIdsInRequest = datasets.stream().map(Dataset::getId).map(String::valueOf).collect(toSet());
        List<SavedFilter> savedFilters = savedFilterRepository.listByUser(user).stream()
                                                              .filter(sf -> stream(sf.getDatasetId()
                                                                                     .split(SavedFilterVOConverter.DATASET_DELIMITER))
                                                                      .allMatch(datasetIdsInRequest::contains))
                                                              .collect(toList());

        boolean hasPermission = securityResourceClient.hasPermissionForUser(user, datasets, VIEW_VISUALISATIONS.getMask());
        if (!hasPermission) {
            return newArrayList();
        }
        return savedFilters.stream().map(savedFilterVOConverter::toVo).collect(toList());
    }

    @Transactional
    public List<SavedFilterVO> saveAndListSavedFilters(Datasets datasets, SavedFilterVO savedFilterVO) {
        savedFilterRepository.saveVO(datasets, savedFilterVO);
        return listByUserAndDatasets(datasets.getDatasetsList());
    }

    @Transactional
    public void deleteSavedFilters(Long savedFilterId) {
        savedFilterRepository.delete(savedFilterId);
    }
}
