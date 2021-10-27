package com.acuity.visualisations.rawdatamodel.service.filters;

import com.acuity.visualisations.rawdatamodel.dataproviders.PopulationDatasetsDataProvider;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.vo.FilterQuery;
import com.acuity.visualisations.rawdatamodel.vo.Subject;
import com.acuity.va.security.acl.domain.Datasets;
import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collection;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASETS;

public class PopulationITCase {

    @Autowired
    protected PopulationRawDataFilterService filterService;

    @Autowired
    protected PopulationDatasetsDataProvider populationDatasetsDataProvider;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    protected PopulationFilters getAvailablePopulationFiltersFromEmptyFilters() {
        return getAvailablePopulationFilters(PopulationFilters.empty());
    }

    protected PopulationFilters getAvailablePopulationFilters(PopulationFilters populationFilters) {
        return PopulationITCase.this.getAvailablePopulationFilters(populationFilters, DUMMY_ACUITY_DATASETS);
    }

    protected PopulationFilters getAvailablePopulationFilters(PopulationFilters populationFilters, Datasets datasets) {
        try {
            Collection<Subject> population = populationDatasetsDataProvider.loadData(datasets);
            FilterQuery<Subject> filteredQuery = new FilterQuery<>(population, populationFilters);
            return filterService.getAvailableFilters(filteredQuery);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
