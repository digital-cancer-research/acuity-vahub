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

package com.acuity.visualisations.cohorteditor.entity;

import com.acuity.visualisations.cohorteditor.builder.SavedFilterBuilder;
import com.acuity.visualisations.cohorteditor.util.FiltersObjectMapper;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuityDataset;
import org.junit.Test;

import static java.lang.String.valueOf;
import static org.assertj.core.api.Assertions.assertThat;

/**
 *
 * @author ksnd199
 */
public class WhenCreatedSavedFiltersTest {
    @Test
    public void shouldCreateSavedFilter() {

        PopulationFilters populationFilters = new PopulationFilters();
        SavedFilter savedFilter = new SavedFilterBuilder("name")
                .withOwner("glen")
                .addCohortFilter(populationFilters)
                .forDataset(new AcuityDataset(1L))
                .grantPermission("ksnd1991")
                .build();

        assertThat(savedFilter.getDatasetId()).isEqualTo(valueOf(Datasets.toAcuityDataset(1L).getDatasetsList().get(0).getId()));
        assertThat(savedFilter.getName()).isEqualTo("name");
        assertThat(savedFilter.getFilters()).hasSize(1);
        assertThat(savedFilter.getFilters().get(0)).isEqualTo(populationFilters);
    }

    @Test
    public void shouldGetCorrectFilters() {

        PopulationFilters populationFilters = new PopulationFilters();
        SavedFilterInstance savedFilterInstance = new SavedFilterInstance();
        savedFilterInstance.setFilterView(SavedFilterInstance.FilterTable.POPULATION);
        savedFilterInstance.setJson(FiltersObjectMapper.toString(populationFilters));

        assertThat(savedFilterInstance.getFilters()).isEqualTo(populationFilters);
    }

    @Test
    public void shouldGetCorrectFiltersFromSavedFilter() {

        PopulationFilters populationFilters = new PopulationFilters();
        SavedFilterInstance savedFilterInstance = new SavedFilterInstance();
        savedFilterInstance.setFilterView(SavedFilterInstance.FilterTable.POPULATION);
        savedFilterInstance.setJson(FiltersObjectMapper.toString(populationFilters));

        SavedFilter savedFilter = new SavedFilter();
        savedFilter.addSavedFilterInstance(savedFilterInstance);

        assertThat(savedFilter.getFilters()).hasSize(1);
        assertThat(savedFilter.getFilters().get(0)).isEqualTo(populationFilters);
    }
}
