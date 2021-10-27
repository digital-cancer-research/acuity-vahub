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

package com.acuity.visualisations.cohorteditor.util;

import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import com.acuity.visualisations.cohorteditor.entity.SavedFilterInstance;
import com.acuity.visualisations.cohorteditor.entity.SavedFilterPermission;
import com.acuity.visualisations.cohorteditor.repository.SavedFilterRepository;
import com.acuity.visualisations.cohorteditor.service.CohortUsersService;
import com.acuity.visualisations.cohorteditor.vo.SavedFilterVO;
import com.acuity.visualisations.cohorteditor.vo.UserVO;
import com.acuity.visualisations.common.util.Security;
import com.acuity.va.security.acl.domain.Datasets;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import static java.util.stream.Collectors.toList;

@Service
public class SavedFilterVOConverter {
    public static final String DATASET_DELIMITER = "::";

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SavedFilterRepository savedFilterRepository;
    @Autowired
    private Security security;
    @Autowired
    private CohortUsersService cohortUsersService;

    public SavedFilterVO toVo(SavedFilter savedFilter) {
        SavedFilterVO savedFilterVO = new SavedFilterVO();
        if (isUserNotAnOwner(savedFilter)) {
            entityManager.detach(savedFilter);
            savedFilter.setName(getExtendedSavedFilterName(savedFilter));
        }
        savedFilterVO.setSavedFilter(savedFilter);
        
        List<SavedFilterInstance> cohortFilters = savedFilter.getInstances().stream().filter(sf -> sf.getType() == SavedFilter.Type.COHORT).collect(toList());
        savedFilterVO.setCohortFilters(cohortFilters);

        List<UserVO> users = cohortUsersService.getSharedWith(savedFilter);
        savedFilterVO.setSharedWith(users);
        return savedFilterVO;        
    }

    public SavedFilter fromVO(Datasets datasets, SavedFilterVO savedFilterVO) {
        final SavedFilter savedFilter = getSavedFilter(savedFilterVO);
        if (savedFilter.getId() != null) {
            // update
            if (isUserNotAnOwner(savedFilter)) {
                entityManager.persist(savedFilter);
            }
            String filterName = getRecoveredSavedFilterName(savedFilter, savedFilterVO.getSavedFilter().getName());
            savedFilter.setName(filterName);
            savedFilter.setOperator(savedFilterVO.getSavedFilter().getOperator());
            savedFilter.setPermissions(getSavedFilterPermissions(savedFilterVO, savedFilter));
        } else {
            // insert
            String dataSetClass = datasets.getDatasets().stream()
                                          .map(ds -> ds.getClass().getSimpleName())
                                          .sorted()
                                          .distinct()
                                          .collect(Collectors.joining(DATASET_DELIMITER));
            savedFilter.setDatasetId(StringUtils.strip(datasets.getIdsAsString(), DATASET_DELIMITER));
            savedFilter.setDatasetClass(dataSetClass);
            savedFilter.setOwner(security.getUser());
            savedFilter.setPermissions(getSavedFilterPermissions(savedFilterVO, savedFilter));
        }

        List<SavedFilterInstance> cohortFilters = savedFilterVO.getCohortFilters();

        cohortFilters.forEach(cf -> {
            cf.setSavedFilter(savedFilter);
            cf.setType(SavedFilter.Type.COHORT);
        });

        // overwriting all filters here, need to change this once the Type.EVENT filters are added
        savedFilter.setInstances(cohortFilters);
        return savedFilter;
    }

    private List<SavedFilterPermission> getSavedFilterPermissions(SavedFilterVO savedFilterVO, SavedFilter savedFilter) {
        return savedFilterVO.getSharedWith().stream()
                        .map(p -> new SavedFilterPermission(p.getId(), savedFilter, p.getSidAsString()))
                        .collect(toList());
    }

    // gets it from db or from the vo
    private SavedFilter getSavedFilter(SavedFilterVO savedFilterVO) {
        Long id = savedFilterVO.getSavedFilter().getId();

        return (id != null && savedFilterRepository.exists(id))
                ? savedFilterRepository.loadTreeById(id) : savedFilterVO.getSavedFilter();
    }

    private String getExtendedSavedFilterName(SavedFilter savedFilter) {
        return String.format("%s (%s)", savedFilter.getName(), savedFilter.getOwner());
    }

    private String getRecoveredSavedFilterName(SavedFilter savedFilter, String name) {
        return name.replace(String.format(" (%s)", savedFilter.getOwner()), "");
    }

    private boolean isUserNotAnOwner(SavedFilter savedFilter) {
        return !security.getUser().equals(savedFilter.getOwner());
    }
}
