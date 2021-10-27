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

package com.acuity.visualisations.cohorteditor.repository;

import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import com.acuity.visualisations.cohorteditor.util.SavedFilterVOConverter;
import com.acuity.visualisations.cohorteditor.vo.SavedFilterVO;
import com.acuity.va.security.acl.domain.Datasets;
import org.springframework.beans.factory.annotation.Autowired;


public class SavedFilterRepositoryImpl implements SavedFilterRepositoryCustom {

    @Autowired
    private SavedFilterRepository savedFilterRepository;
    @Autowired
    private SavedFilterVOConverter savedFilterVOConverter;

    /*
     * Pre load collections, hibernate cant fetch join 2+ collections
     */
    @Override
    public SavedFilter loadTreeById(Long id) {
        SavedFilter savedFilter = savedFilterRepository.findOne(id);
        savedFilter.getInstances().size();
        savedFilter.getPermissions().size();

        return savedFilter;
    }

    /*
     * Update or insert
     */
    @Override
    public Long saveVO(Datasets datasets, SavedFilterVO savedFilterVO) {
        SavedFilter savedFilter = savedFilterVOConverter.fromVO(datasets, savedFilterVO);
        SavedFilter insertedSavedFilter = savedFilterRepository.save(savedFilter);
        return insertedSavedFilter.getId();
    }
}
