package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collection;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class WhenRunningAcuityPopulationRepositoryQueriesITCase extends PopulationITCase {

    @Test
    public void shouldListSexesWithEmptyFilter() {
        // Given
        PopulationFilters availableFilters = getAvailablePopulationFiltersFromEmptyFilters();
        // When
        assertThat(availableFilters.getSex().getValues()).containsExactlyInAnyOrder("Male", "Female");
    }

    @Test
    public void shouldListSexesWithSexFilter() {
        // Given
        PopulationFilters flt = new PopulationFilters();
        flt.setSex(new SetFilter<>(newArrayList("Male"), false));
        PopulationFilters availableFilters = getAvailablePopulationFilters(flt);
        // When
        assertThat(availableFilters.getSex().getValues()).containsExactlyInAnyOrder("Male");
    }

    @Test
    public void shouldGetPatientList() throws Exception {
        // Given
        // When
        Collection<Subject> population = populationDatasetsDataProvider.loadData(DUMMY_ACUITY_DATASETS);

        // Then
        assertThat(population).hasSize(124);
        assertThat(population).extracting("subjectCode").contains("E0000100229");
    }
}
