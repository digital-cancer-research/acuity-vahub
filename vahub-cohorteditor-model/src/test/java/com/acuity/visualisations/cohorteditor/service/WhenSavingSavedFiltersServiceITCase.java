package com.acuity.visualisations.cohorteditor.service;

import com.acuity.visualisations.cohorteditor.ApplicationCohortEditorConfigITCase;
import com.acuity.visualisations.cohorteditor.builder.SavedFilterBuilder;
import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import com.acuity.visualisations.cohorteditor.repository.SavedFilterRepository;
import com.acuity.visualisations.cohorteditor.vo.SavedFilterVO;
import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.Datasets;
import com.acuity.va.security.acl.domain.AcuitySidDetails;
import com.acuity.va.security.auth.common.ISecurityResourceClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.*;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 *
 * @author ksnd199
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ApplicationModelConfigITCase.class, ApplicationCohortEditorConfigITCase.class})
@TransactionalOracleITTest
@Rollback
public class WhenSavingSavedFiltersServiceITCase {

    @Autowired
    private SavedFilterRepository savedFilterRepository;
    @Autowired
    private SavedFilterService savedFilterService;
    @MockBean // Using MockBean throws exceptions
    private ISecurityResourceClient securityResourceClient;

    private long id;
    private long id2;
    private static final Datasets MULTIPLE_DATASETS = new Datasets(DUMMY_ACUITY_ALCOHOL_DATASET, DUMMY_ACUITY_LUNG_FUNC_DATASET, DUMMY_ACUITY_CVOT_DATASET);


    @Before
    public void init() {
        AeFilters aes = new AeFilters();
        aes.setActionTaken(new SetFilter(newArrayList("sfdsdf")));

        SavedFilter savedFilter = new SavedFilterBuilder("name")
                .withOwner("glen")
                .addCohortFilter(aes)
                .forDataset(DUMMY_ACUITY_DATASET)
                .grantPermission("ksnd199")
                .build();
        SavedFilter savedFilter2 = new SavedFilterBuilder("name2")
                .withOwner("glen")
                .addCohortFilter(aes)
                .forDataset(DUMMY_ACUITY_DATASET)
                .grantPermission("ksnd199")
                .build();
        SavedFilter savedFilter3 = new SavedFilterBuilder("name3")
                .withOwner("sam")
                .addCohortFilter(aes)
                .forDataset(DUMMY_ACUITY_DATASET)
                .grantPermission("kdbg488")
                .build();

        id = savedFilterRepository.save(savedFilter).getId();
        id2 = savedFilterRepository.save(savedFilter2).getId();
        savedFilterRepository.save(savedFilter3).getId();

        initMocks(this);
        when(securityResourceClient.hasPermissionForUser(anyString(), anyList(), anyInt())).thenReturn(true);
    }

    @Test
    public void shouldGetDistinctSubjectsForEmptyPopulationFilters() {

        PopulationFilters emptyPopulationFilters = PopulationFilters.empty();

        SavedFilter emptyPopSavedFilter = new SavedFilterBuilder("empty population")
                .withOwner("glen")
                .addCohortFilter(emptyPopulationFilters)
                .forDatasets(DUMMY_DETECT_DATASETS)
                .grantPermission("test1").build();
        Long emptyPopSavedFilterId = savedFilterRepository.save(emptyPopSavedFilter).getId();

        List<String> distinctSubjects = savedFilterService.getDistinctSubjects(DUMMY_DETECT_DATASETS, emptyPopSavedFilterId);

        assertThat(distinctSubjects).hasSize(197);
    }

    @Test
    public void shouldGetDistinctSubjectsForMalesOnlyPopulationFilters() {

        PopulationFilters malesPopulationFilters = new PopulationFilters();
        malesPopulationFilters.setSex(new SetFilter(newArrayList("M")));

        SavedFilter emptyPopSavedFilter = new SavedFilterBuilder("males population")
                .withOwner("glen")
                .addCohortFilter(malesPopulationFilters)
                .forDatasets(DUMMY_DETECT_DATASETS)
                .grantPermission("test1")
                .build();
        Long malesPopSavedFilterId = savedFilterRepository.save(emptyPopSavedFilter).getId();

        List<String> distinctSubjects = savedFilterService.getDistinctSubjects(DUMMY_DETECT_DATASETS, malesPopSavedFilterId);

        assertThat(distinctSubjects).hasSize(116);
    }

    @Test
    public void shouldListMySavedFilterVOInCorrectOrder() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("ksnd199"));

        // When
        List<SavedFilterVO> savedFilters = savedFilterService.listByUserAndDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        // Then
        assertThat(savedFilters).extracting("savedFilter.name").contains("name2", "name");
        assertThat(savedFilters).extracting("savedFilter.id").doesNotContainNull();
    }

    @Test
    public void shouldSaveAndListMySavedFilterVOInCorrectOrder() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("ksnd199"));
        SavedFilter savedFilter3 = new SavedFilterBuilder("name3")
                .withOwner("ksnd199")
                .addCohortFilter(PopulationFilters.empty())
                .forDataset(DUMMY_ACUITY_DATASET)
                .grantPermission("ksnd199")
                .build();

        savedFilterRepository.save(savedFilter3).getId();

        // When
        List<SavedFilterVO> savedFilters = savedFilterService.listByUserAndDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        // Then
        assertThat(savedFilters).extracting("savedFilter.name").contains("name3", "name2", "name");
        assertThat(savedFilters).extracting("savedFilter.id").doesNotContainNull();
    }

    @Test
    public void shouldListSavedFiltersSharedWithMeInCorrectOrder() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("kdbg488"));

        // When
        List<SavedFilterVO> savedFilters = savedFilterService.listByUserAndDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        // Then
        assertThat(savedFilters).extracting("savedFilter.name").contains("name3");
        assertThat(savedFilters).extracting("savedFilter.id").doesNotContainNull();
    }

    @Test
    public void shouldSaveAndListSavedFilterSharedWithMeInCorrectOrder() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("kdbg488"));
        SavedFilter savedFilter3 = new SavedFilterBuilder("name4")
                .withOwner("sam")
                .addCohortFilter(PopulationFilters.empty())
                .forDataset(DUMMY_ACUITY_DATASET)
                .grantPermission("kdbg488")
                .build();

        savedFilterRepository.save(savedFilter3).getId();

        // When
        List<SavedFilterVO> savedFilters = savedFilterService.listByUserAndDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        // Then
        assertThat(savedFilters).extracting("savedFilter.name").contains("name3", "name4");
        assertThat(savedFilters).extracting("savedFilter.id").doesNotContainNull();
    }

    @Test
    public void shouldListSavedFilterSharedWithMeThatIHavePermissionToView() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("kdbg488"));
        SavedFilter savedFilter3 = new SavedFilterBuilder("name4")
                .withOwner("sam")
                .addCohortFilter(PopulationFilters.empty())
                .forDataset(DUMMY_ACUITY_DATASET)
                .grantPermission("kdbg488")
                .build();

        savedFilterRepository.save(savedFilter3).getId();

        // When
        List<SavedFilterVO> savedFilters = savedFilterService.listByUserAndDatasets(DUMMY_ACUITY_DATASETS.getDatasetsList());

        // Then
        assertThat(savedFilters).extracting("savedFilter.name").contains("name4", "name3");
        assertThat(savedFilters).extracting("savedFilter.id").doesNotContainNull();
    }

    @Test
    public void shouldDelete() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("glen"));

        // When
        savedFilterService.deleteSavedFilters(id);

        // Then
        List<SavedFilter> savedFilters = savedFilterRepository.findByName("name");
        assertThat(savedFilters).isEmpty();
    }

    @Test
    public void shouldCascadeDeletePermissionOrphansOnUpdate() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("kdbg488"));
        AeFilters aes = new AeFilters();
        aes.setActionTaken(new SetFilter(newArrayList("sfdsdf")));
        SavedFilter insertedFilter = new SavedFilterBuilder("updateTest")
                .withOwner("kdbg488")
                .addCohortFilter(aes)
                .forDataset(DUMMY_DETECT_DATASET)
                .grantPermission("kdbg488")
                .grantPermission("ksnd199")
                .build();
        SavedFilter savedFilter = savedFilterRepository.save(insertedFilter);

        // When
        savedFilter.getPermissions().remove(0);
        savedFilterRepository.save(savedFilter);

        // Then
        List<SavedFilter> result = savedFilterRepository.listByUser("kdbg488");
        assertThat(result)
                .filteredOn(r -> r.getName().equals("updateTest"))
                .extracting("permissions")
                .hasSize(1);
    }

    @Test
    public void shouldCascadeDeleteInstanceOrphansOnUpdate() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("kdbg488"));
        AeFilters aes = new AeFilters();
        PopulationFilters pop = new PopulationFilters();
        aes.setActionTaken(new SetFilter(newArrayList("sfdsdf")));
        pop.setSubjectId(new SetFilter<>(newArrayList("subj-1")));
        SavedFilter insertedFilter = new SavedFilterBuilder("updateTest")
                .withOwner("kdbg488")
                .addCohortFilter(aes)
                .addCohortFilter(pop)
                .forDataset(DUMMY_DETECT_DATASET)
                .grantPermission("kdbg488")
                .build();
        SavedFilter savedFilter = savedFilterRepository.save(insertedFilter);

        // When
        savedFilter.getInstances().remove(0);
        savedFilterRepository.save(savedFilter);

        // Then
        List<SavedFilter> result = savedFilterRepository.listByUser("kdbg488");
        assertThat(result)
                .filteredOn(r -> r.getName().equals("updateTest"))
                .extracting("instances")
                .hasSize(1);
    }

    @Test
    public void shouldListFiltersForMultipleDatasets() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("kdbg488"));
        createMultipleDatasetSavedFilter();

        // When
        List<SavedFilterVO> result = savedFilterService.listByUserAndDatasets(MULTIPLE_DATASETS.getDatasetsList());

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    public void shouldListFiltersWithMultipleDatasetsForUserWithAccessToSingleDataset() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("kdbg488"));
        createMultipleDatasetSavedFilter();
        List<Dataset> datasetIHavePermissionToView = newArrayList(MULTIPLE_DATASETS.getDatasetsList().get(0));

        // When
        List<SavedFilterVO> result = savedFilterService.listByUserAndDatasets(datasetIHavePermissionToView);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    public void shouldListFiltersWithMultipleDatasetsForUserWithAccessToSomeOfTheDatasets() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("kdbg488"));
        createMultipleDatasetSavedFilter();
        List<Dataset> datasetIHavePermissionToView = newArrayList(MULTIPLE_DATASETS.getDatasetsList().subList(0, 2));

        // When
        List<SavedFilterVO> result = savedFilterService.listByUserAndDatasets(datasetIHavePermissionToView);

        // Then
        assertThat(result).hasSize(1);
    }

    @Test
    public void shouldNotListFiltersWithMultipleDatasetsWhenUsersSingleDatasetIsNotIncluedInTheFilter() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("kdbg488"));
        createMultipleDatasetSavedFilter();
        Datasets notAllowedDatasets = new Datasets(DUMMY_ACUITY_DOSE_DATASET);
        List<Dataset> datasetIHavePermissionToView = newArrayList(notAllowedDatasets.getDatasetsList());

        // When
        List<SavedFilterVO> result = savedFilterService.listByUserAndDatasets(datasetIHavePermissionToView);

        // Then -- should return 0, because DUMMY_ACUITY_DOSE_DATASET is included in the request, but is not in the filter
        assertThat(result).hasSize(0);
    }

    @Test
    public void shouldNotListFiltersWithMultipleDatasetsWhenUsersDatasetIsNotIncluedInTheFilter() {
        // Given
        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("kdbg488"));
        createMultipleDatasetSavedFilter();
        Datasets notAllowedDatasets =
                new Datasets(DUMMY_ACUITY_ALCOHOL_DATASET, DUMMY_ACUITY_LUNG_FUNC_DATASET, DUMMY_ACUITY_CVOT_DATASET, DUMMY_ACUITY_DOSE_DATASET);
        List<Dataset> datasetIHavePermissionToView = newArrayList(notAllowedDatasets.getDatasetsList());

        // When
        List<SavedFilterVO> result = savedFilterService.listByUserAndDatasets(datasetIHavePermissionToView);

        // Then -- should return 0, because DUMMY_ACUITY_DOSE_DATASET is included in the request, but is not in the filter
        assertThat(result).hasSize(0);
    }

    private void createMultipleDatasetSavedFilter() {
        SavedFilter savedFilter = new SavedFilterBuilder("multi").withOwner("kdbg488").addCohortFilter(AeFilters.empty()).grantPermission("kdbg488")
                .forDatasets(MULTIPLE_DATASETS)
                .build();
        savedFilterRepository.save(savedFilter);
    }
}
