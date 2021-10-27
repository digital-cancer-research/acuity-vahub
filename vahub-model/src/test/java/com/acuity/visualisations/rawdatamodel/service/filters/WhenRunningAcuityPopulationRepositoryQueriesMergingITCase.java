package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

import static com.acuity.visualisations.config.util.TestConstants.MULTI_DUMMY_ACUITY_DATASETS;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class})
@TransactionalOracleITTest
public class WhenRunningAcuityPopulationRepositoryQueriesMergingITCase extends PopulationITCase {

    @Test
    public void shouldListStudyIdsWithEmptyFilter() {
        // Given
        PopulationFilters filters = getAvailablePopulationFilters(PopulationFilters.empty(), MULTI_DUMMY_ACUITY_DATASETS);
        // When
        softly.assertThat(filters.getStudyIdentifier().getValues()).containsExactlyInAnyOrder("D1234C00001", "Dummy");
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(248);
    }

    @Test
    public void shouldFilterByStudyId() {
        // Given
        PopulationFilters populationFilters = PopulationFilters.empty();
        populationFilters.setStudyIdentifier(new SetFilter<>(Collections.singletonList("D1234C00001")));
        PopulationFilters filters = getAvailablePopulationFilters(populationFilters, MULTI_DUMMY_ACUITY_DATASETS);
        // When
        softly.assertThat(filters.getStudyIdentifier().getValues()).containsExactlyInAnyOrder("D1234C00001");
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(124);
    }

    @Test
    public void shouldListSexesWithEmptyFilter() {
        // Given
        PopulationFilters filters = getAvailablePopulationFilters(PopulationFilters.empty(), MULTI_DUMMY_ACUITY_DATASETS);
        // When
        softly.assertThat(filters.getSex().getValues()).containsExactlyInAnyOrder("Male", "Female");
        softly.assertThat(filters.getMatchedItemsCount()).isEqualTo(248);
    }

    @Test
    public void shouldListSexesWithSexFilter() {
        // Given
        PopulationFilters populationFilters = PopulationFilters.empty();
        populationFilters.setSex(new SetFilter<>(Collections.singletonList("Male")));
        PopulationFilters filters = getAvailablePopulationFilters(populationFilters, MULTI_DUMMY_ACUITY_DATASETS);
        // When
        softly.assertThat(filters.getSex().getValues()).containsExactly("Male");
    }
}
