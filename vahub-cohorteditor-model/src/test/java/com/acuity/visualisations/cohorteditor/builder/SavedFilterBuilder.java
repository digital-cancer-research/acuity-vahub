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
import com.acuity.visualisations.cohorteditor.entity.SavedFilterPermission;
import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;

import java.util.Date;

/**
 *
 * @author ksnd199
 */
public class SavedFilterBuilder {

    private SavedFilter savedFilter;

    public SavedFilterBuilder(String name) {
        savedFilter = new SavedFilter(name, new Date(), "");
    }

    public SavedFilterBuilder withOwner(String owner) {
        savedFilter.setOwner(owner);

        return this;
    }
    
    public SavedFilterBuilder withId(Long id) {
        savedFilter.setId(id);

        return this;
    }

    public SavedFilterBuilder addCohortFilter(Filters filters) {
        savedFilter.addSavedFilterInstance(new SavedFilterInstance(savedFilter, SavedFilter.Type.COHORT, filters));
        
        return this;
    }

    public SavedFilterBuilder addEventFilter(Filters filters) {
        savedFilter.addSavedFilterInstance(new SavedFilterInstance(savedFilter, SavedFilter.Type.EVENT, filters));
        
        return this;
    }

    public SavedFilterBuilder grantPermission(String permissionPrid) {
        savedFilter.addSavedFilterPermission(new SavedFilterPermission(savedFilter, permissionPrid));
        
        return this;
    }

    public SavedFilterBuilder forDatasets(Datasets datasets) {
        datasets.getDatasetsList().forEach(ds -> {
            savedFilter.setDatasetId(String.valueOf(ds.getId()));
            savedFilter.setDatasetClass(ds.getClass().getSimpleName());
        });

        return this;
    }

    public SavedFilterBuilder forDataset(Dataset dataset) {
        savedFilter.setDatasetId(String.valueOf(dataset.getId()));
        savedFilter.setDatasetClass(dataset.getClass().getSimpleName());
        return this;
    }

    public SavedFilter build() {
        return savedFilter;
    }
}
