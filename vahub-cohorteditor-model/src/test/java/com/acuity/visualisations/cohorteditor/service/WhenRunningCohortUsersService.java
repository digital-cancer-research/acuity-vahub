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

package com.acuity.visualisations.cohorteditor.service;

import com.acuity.visualisations.cohorteditor.entity.SavedFilter;
import com.acuity.visualisations.cohorteditor.entity.SavedFilterPermission;
import com.acuity.visualisations.cohorteditor.vo.UserVO;
import com.acuity.visualisations.common.util.Security;
import com.acuity.va.security.acl.domain.AcuitySidDetails;
import com.acuity.va.security.auth.common.ISecurityResourceClient;
import com.acuity.va.security.common.service.PeopleResourceClient;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;

import static com.acuity.visualisations.config.util.TestConstants.DUMMY_DETECT_DATASET;
import static com.acuity.visualisations.config.util.TestConstants.DUMMY_ACUITY_DATASET;
import static com.google.common.collect.Lists.newArrayList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WhenRunningCohortUsersService {

    @Mock
    private PeopleResourceClient mockPeopleResourceClient;
    @Mock
    private Security mockSecurity;
    @Mock
    private ISecurityResourceClient mockSecurityResourceClient;
    @InjectMocks
    private CohortUsersService cohortUsersService;

    private static final String PRID_1 = "kdbg488";
    private static final String PRID_2 = "ksnd199";
    private static final String PRID_3 = "knml167";
    private static final String FULL_NAME_1 = "Bentley, Samuel";
    private static final String FULL_NAME_2 = "Drinkwater, Glen";
    private static final String FULL_NAME_3 = "Orlov, Nikolay";

    @Test
    public void shouldConvertPridsUserVO() {
        // Given
        SavedFilter savedFilter = new SavedFilter();
        savedFilter.setPermissions(Lists.newArrayList(new SavedFilterPermission(1L, savedFilter, PRID_1)));
        when(mockPeopleResourceClient.getFullName(eq(PRID_1))).thenReturn(FULL_NAME_1);

        // When
        List<UserVO> result = cohortUsersService.getSharedWith(savedFilter);

        // Then
        assertThat(result).extracting("sid", "fullName").containsExactly(tuple(PRID_1, FULL_NAME_1));
    }

    @Test
    public void shouldGetDatasetUsersNotIncludingCurrentUser() throws IOException, IllegalAccessException {
        // Given
        when(mockSecurity.getUser()).thenReturn(PRID_1);
        when(mockSecurityResourceClient.getUsersForDatasets(eq(newArrayList(DUMMY_DETECT_DATASET)))).thenReturn(
                newArrayList(new AcuitySidDetails(PRID_2, FULL_NAME_2))
        );

        // When
        List<AcuitySidDetails> result = cohortUsersService.getDatasetUsersExcludingCurrentUser(newArrayList(DUMMY_DETECT_DATASET));

        // Then
        assertThat(result).extracting("sid", "fullName").containsExactly(tuple(PRID_2, FULL_NAME_2));
    }

    @Test
    public void shouldGetDatasetUsersNotIncludingCurrentUserForMultipleDatasets() throws IOException, IllegalAccessException {
        // Given
        when(mockSecurity.getUser()).thenReturn(PRID_1);
        when(mockSecurityResourceClient.getUsersForDatasets(eq(newArrayList(DUMMY_DETECT_DATASET, DUMMY_ACUITY_DATASET)))).thenReturn(
                newArrayList(new AcuitySidDetails(PRID_2, FULL_NAME_2), new AcuitySidDetails(PRID_3, FULL_NAME_3))
        );

        // When
        List<AcuitySidDetails> result = cohortUsersService.getDatasetUsersExcludingCurrentUser(newArrayList(DUMMY_DETECT_DATASET, DUMMY_ACUITY_DATASET));

        // Then
        assertThat(result).extracting("sid", "fullName").containsOnly(tuple(PRID_2, FULL_NAME_2), tuple(PRID_3, FULL_NAME_3));
    }

    @Test
    public void shouldGetOrderedDatasetUsers() throws IOException, IllegalAccessException {
        // Given
        when(mockSecurity.getUser()).thenReturn(PRID_1);
        when(mockSecurityResourceClient.getUsersForDatasets(eq(newArrayList(DUMMY_DETECT_DATASET)))).thenReturn(
                newArrayList(new AcuitySidDetails(PRID_3, FULL_NAME_3), new AcuitySidDetails(PRID_2, FULL_NAME_2))
        );

        // When
        List<AcuitySidDetails> result = cohortUsersService.getDatasetUsersExcludingCurrentUser(newArrayList(DUMMY_DETECT_DATASET));

        // Then
        assertThat(result)
                .extracting("sid", "fullName")
                .containsExactly(tuple(PRID_2, FULL_NAME_2), tuple(PRID_3, FULL_NAME_3));
    }
}
