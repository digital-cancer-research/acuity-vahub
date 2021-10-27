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

package com.acuity.visualisations.cohorteditor.vo;

import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import com.acuity.visualisations.cohorteditor.entity.SavedFilterInstance;
import java.io.Serializable;
import java.util.List;
import lombok.Data;


import static com.google.common.collect.Lists.newArrayList;

@Data
public class SavedFilterVO implements Serializable {
    private SavedFilter savedFilter;
    private List<SavedFilterInstance> cohortFilters = newArrayList();
    private List<UserVO> sharedWith = newArrayList();

    public SavedFilter getSavedFilter() {
        if (savedFilter != null) {
            SavedFilter savedFilterSlim = new SavedFilter(savedFilter.getName(), savedFilter.getCreatedDate(), savedFilter.getOwner());
            savedFilterSlim.setOperator(savedFilter.getOperator());
            savedFilterSlim.setId(savedFilter.getId());
            savedFilterSlim.setDatasetId(savedFilter.getDatasetId());
            savedFilterSlim.setDatasetClass(savedFilter.getDatasetClass());
            return savedFilterSlim;
        }
        return null;
    }
}
