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
