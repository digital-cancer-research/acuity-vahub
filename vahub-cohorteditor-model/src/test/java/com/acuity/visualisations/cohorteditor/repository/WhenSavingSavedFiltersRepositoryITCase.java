package com.acuity.visualisations.cohorteditor.repository;

import com.acuity.visualisations.cohorteditor.ApplicationCohortEditorConfigITCase;
import com.acuity.visualisations.cohorteditor.builder.SavedFilterBuilder;
import com.acuity.visualisations.cohorteditor.builder.SavedFilterVOBuilder;
import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import com.acuity.visualisations.cohorteditor.entity.SavedFilterInstance;
import com.acuity.visualisations.cohorteditor.service.SavedFilterService;
import com.acuity.visualisations.cohorteditor.vo.SavedFilterVO;
import com.acuity.visualisations.cohorteditor.vo.UserVO;
import com.acuity.visualisations.config.annotation.TransactionalOracleITTest;
import com.acuity.visualisations.config.config.ApplicationModelConfigITCase;
import com.acuity.visualisations.config.util.TestConstants;
import com.acuity.visualisations.rawdatamodel.filters.AeFilters;
import com.acuity.visualisations.rawdatamodel.filters.LabFilters;
import com.acuity.visualisations.rawdatamodel.filters.PopulationFilters;
import com.acuity.visualisations.rawdatamodel.filters.SetFilter;
import com.acuity.va.security.acl.domain.DetectDataset;
import com.acuity.va.security.acl.domain.AcuityDataset;
import com.acuity.va.security.acl.domain.AcuitySidDetails;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_VA_ID;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

/**
 *
 * @author ksnd199
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApplicationModelConfigITCase.class, ApplicationCohortEditorConfigITCase.class})
@TransactionalOracleITTest
@Rollback
@Slf4j
public class WhenSavingSavedFiltersRepositoryITCase {
    @Autowired
    private SavedFilterRepository savedFilterRepository;
    @Autowired
    private SavedFilterService savedFilterService;

    private long id;
    private long id2;

    @Before
    public void init() {
        AeFilters aes = new AeFilters();
        aes.setActionTaken(new SetFilter(newArrayList("sfdsdf")));

        SavedFilter savedFilter = new SavedFilterBuilder("name")
                .withOwner("glen")
                .addCohortFilter(aes)
                .forDataset(new AcuityDataset(1L))
                .grantPermission("ksnd1991")
                .build();
        SavedFilter savedFilter2 = new SavedFilterBuilder("name2")
                .withOwner("glen")
                .addCohortFilter(aes)
                .forDataset(new AcuityDataset(1L))
                .grantPermission("ksnd1991")
                .build();
        SavedFilter savedFilter3 = new SavedFilterBuilder("name3")
                .withOwner("sam")
                .addCohortFilter(aes)
                .forDataset(new AcuityDataset(1L))
                .grantPermission("kdbg488")
                .build();
        SavedFilter savedFilter4 = new SavedFilterBuilder("name4")
                .withOwner("sam")
                .addCohortFilter(aes)
                .forDataset(new AcuityDataset(2L))
                .grantPermission("kdbg488")
                .build();

        id = savedFilterRepository.save(savedFilter).getId();
        id2 = savedFilterRepository.save(savedFilter2).getId();
        savedFilterRepository.save(savedFilter3);
        savedFilterRepository.save(savedFilter4);
    }

    //@Test
    public void shouldGetSubjectIdsForEmptyPopAndAesOperatorAnd() {
        print(id);
    }

    @Test
    public void shouldOnlyUpdateNameOperatorPermissionsAndCohorts() {
        boolean exists = savedFilterRepository.exists(id);

        if (exists) {

            SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("glen"));

            LabFilters labs = new LabFilters();
            SavedFilterVO savedFilterVO = new SavedFilterVOBuilder("name updated")
                    .withId(id)
                    .withOperator(SavedFilter.Operator.AND)
                    .addCohortFilter(labs)
                    .addSharedWith(Lists.newArrayList(new UserVO(null, "kdbg488", "Bentley, Samuel")))
                    .build();

            Long saveVOId = savedFilterRepository.saveVO(TestConstants.DUMMY_DETECT_DATASETS, savedFilterVO);

            print(saveVOId);

            SavedFilter loadedSavedFilter = savedFilterRepository.loadTreeById(saveVOId);

            assertThat(loadedSavedFilter.getName()).isEqualTo("name updated");
            assertThat(loadedSavedFilter.getOperator()).isEqualTo(SavedFilter.Operator.AND);
            assertThat(loadedSavedFilter.getInstances()).hasSize(1);
            assertThat(loadedSavedFilter.getInstances().get(0).getFilterView()).isEqualTo(SavedFilterInstance.FilterTable.LABS);
            assertThat(loadedSavedFilter.getInstances().get(0).getType()).isEqualTo(SavedFilter.Type.COHORT);
            assertThat(loadedSavedFilter.getPermissions()).extracting("prid").containsOnly("kdbg488");

        } else {
            fail(id + " doesnt exist");
        }
    }

    @Test
    public void shouldInsertNew() {

        LabFilters labs = new LabFilters();
        List<UserVO> permissions = newArrayList(
                new UserVO(null, "insertprid1", "insert 1 full name"),
                new UserVO(null, "insertprid2", "insert 2 full name")
        );
        SavedFilterVO savedFilterVO = new SavedFilterVOBuilder("inserted name")
                .addCohortFilter(labs)
                .addSharedWith(permissions)
                .build();

        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("insertprid3"));

        Long saveVOId = savedFilterRepository.saveVO(TestConstants.DUMMY_DETECT_DATASETS, savedFilterVO);

        print(saveVOId);

        SavedFilter loadedSavedFilter = savedFilterRepository.loadTreeById(saveVOId);

        assertThat(loadedSavedFilter.getName()).isEqualTo("inserted name");
        assertThat(loadedSavedFilter.getOwner()).isEqualTo("insertprid3");
        assertThat(loadedSavedFilter.getOperator()).isEqualTo(SavedFilter.Operator.AND);
        assertThat(loadedSavedFilter.getInstances()).hasSize(1);
        assertThat(loadedSavedFilter.getInstances()).hasSize(1);
        assertThat(loadedSavedFilter.getInstances().get(0).getFilterView()).isEqualTo(SavedFilterInstance.FilterTable.LABS);
        assertThat(loadedSavedFilter.getInstances().get(0).getType()).isEqualTo(SavedFilter.Type.COHORT);
        assertThat(loadedSavedFilter.getDatasetId()).isEqualTo(DUMMY_DETECT_VA_ID);
        assertThat(loadedSavedFilter.getDatasetClass()).isEqualTo(DetectDataset.class.getSimpleName());
        assertThat(loadedSavedFilter.getPermissions()).hasSize(2);
        assertThat(loadedSavedFilter.getPermissions()).extracting("prid").contains("insertprid1", "insertprid2");
    }

    @Test
    public void shouldUpdateSavedFilter() {
        // Given
        String newJson = "{\"subjectId\":{\"values\":[\"E000010010\"]}}";

        // Given -- Insert filter
        PopulationFilters populationFilters = new PopulationFilters();
        List<UserVO> sharedWith = newArrayList(
                new UserVO(null, "updateprid1", "update 1 full name"),
                new UserVO(null, "updateprid2", "update 2 full name")
        );
        SavedFilterVO savedFilterVO = new SavedFilterVOBuilder("pop filter")
                .addCohortFilter(populationFilters)
                .addSharedWith(sharedWith)
                .build();
        SecurityContextHolder.getContext().setAuthentication(AcuitySidDetails.toUser("updateprid3"));
        long id = savedFilterRepository.saveVO(TestConstants.DUMMY_DETECT_DATASETS, savedFilterVO);

        // Given -- Get saved filter
        SavedFilter savedFilter = savedFilterRepository.loadTreeById(id);
        savedFilterVO.setSavedFilter(savedFilter);

        // When
        // When -- Change saved filter
        SavedFilterInstance savedFilterInstance = new SavedFilterInstance();
        savedFilterInstance.setJson(newJson);
        savedFilterVO.setCohortFilters(newArrayList(savedFilterInstance));

        // When -- Update changed filter
        savedFilterRepository.saveVO(TestConstants.DUMMY_DETECT_DATASETS, savedFilterVO);

        // Then
        List<SavedFilter> result =  savedFilterRepository.listByUser("updateprid3");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getInstances()).hasSize(1).extracting("json").isEqualTo(newArrayList(newJson));
        assertThat(result.get(0).getPermissions()).extracting("prid").contains("updateprid1", "updateprid2");
    }

    @Test
    public void shouldListFiltersCreatedByMe() {
        List<SavedFilter> savedFilters = savedFilterRepository.listByUser("sam");

        assertThat(savedFilters).hasSize(2);
    }

    void print(long id) {
        print(savedFilterRepository.loadTreeById(id));
    }

    void print(SavedFilter savedFilter) {

        log.info("SavedFilter:", savedFilter);
        log.info("--------------------------------");
        log.info("" + savedFilter);
        log.info("" + savedFilter.getDatasetId());
        log.info("" + savedFilter.getDatasetClass());
        log.info("" + savedFilter.getInstances());
        log.info("" + savedFilter.getPermissions());
        savedFilter.getInstances().forEach(i -> log.info("" + i.getFilters()));
    }

    void print(SavedFilterVO savedFilter) {

        log.info("SavedFilterVO:", savedFilter);
        log.info("--------------------------------");
        log.info("" + savedFilter);
        log.info("" + savedFilter.getCohortFilters());
    }
}
