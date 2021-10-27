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

package com.acuity.visualisations.cohorteditor.builder;

import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import com.acuity.visualisations.cohorteditor.entity.SavedFilterInstance;
import com.acuity.visualisations.cohorteditor.vo.SavedFilterVO;
import com.acuity.visualisations.cohorteditor.vo.UserVO;
import com.acuity.visualisations.rawdatamodel.filters.Filters;

import java.util.Date;
import java.util.List;

/**
 *
 * @author ksnd199
 */
public class SavedFilterVOBuilder {

    private SavedFilter savedFilter;
    private SavedFilterVO savedFilterVO;

    public SavedFilterVOBuilder(String name) {
        savedFilter = new SavedFilter(name, new Date(), "");
        savedFilterVO = new SavedFilterVO();
        savedFilterVO.setSavedFilter(savedFilter);
    }

    public SavedFilterVOBuilder(SavedFilter savedFilter) {
        savedFilter = new SavedFilter(savedFilter.getName(), savedFilter.getCreatedDate(), savedFilter.getOwner());
        savedFilterVO = new SavedFilterVO();
        savedFilterVO.setSavedFilter(savedFilter);
    }

    public SavedFilterVOBuilder withId(Long id) {
        savedFilter.setId(id);

        return this;
    }

    public SavedFilterVOBuilder withOperator(SavedFilter.Operator operator) {
        savedFilter.setOperator(operator);

        return this;
    }

    public SavedFilterVOBuilder addCohortFilter(Filters filters) {
        SavedFilterInstance savedFilterInstance = new SavedFilterInstance(savedFilter, SavedFilter.Type.COHORT, filters);
        savedFilterInstance.setSavedFilter(null);

        savedFilterVO.getCohortFilters().add(savedFilterInstance);

        return this;
    }

    public SavedFilterVOBuilder addEventFilter(Filters filters) {
        SavedFilterInstance savedFilterInstance = new SavedFilterInstance(savedFilter, SavedFilter.Type.EVENT, filters);
        savedFilterInstance.setSavedFilter(null);

        savedFilterVO.getCohortFilters().add(savedFilterInstance);

        return this;
    }

    public SavedFilterVOBuilder addSharedWith(List<UserVO> users) {
        savedFilterVO.setSharedWith(users);

        return this;
    }

    public SavedFilterVO build() {
        return savedFilterVO;
    }
}
