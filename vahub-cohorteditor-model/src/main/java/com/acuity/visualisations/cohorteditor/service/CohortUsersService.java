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
import com.acuity.visualisations.cohorteditor.vo.UserVO;
import com.acuity.visualisations.common.util.Security;
import com.acuity.va.security.acl.domain.Dataset;
import com.acuity.va.security.acl.domain.AcuitySidDetails;
import com.acuity.va.security.auth.common.ISecurityResourceClient;
import com.acuity.va.security.common.service.PeopleResourceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.collect.Maps.newHashMap;

@Service
@Slf4j
public class CohortUsersService {

    @Autowired
    private PeopleResourceClient peopleResourceClient;
    @Autowired
    private ISecurityResourceClient securityResourceClient;
    @Autowired
    private Security security;

    private Map<String, String> pridFullnameCache = newHashMap();

    public List<UserVO> getSharedWith(SavedFilter savedFilter) {
        return savedFilter.getPermissions().stream()
                .map(p -> toUserVo(p.getId(), p.getPrid()))
                .collect(Collectors.toList());
    }

    public List<AcuitySidDetails> getDatasetUsersExcludingCurrentUser(List<Dataset> datasets) throws IOException, IllegalAccessException {
        return securityResourceClient.getUsersForDatasets(datasets).stream()
            .filter(r -> !r.getSidAsString().equals(security.getUser()))
            .sorted(Comparator.comparing(AcuitySidDetails::getFullName))
            .collect(Collectors.toList());
    }

    private UserVO toUserVo(Long id, String prid) {
        // Avoid going to Active Directory all the time, store a cache of the user's full name
        if (!pridFullnameCache.containsKey(prid)) {
            try {
                pridFullnameCache.put(prid, peopleResourceClient.getFullName(prid));
            } catch (Exception e) {
                log.info("User is no longer in the ACUITY system", e);
            }
        }
        return new UserVO(id, prid, pridFullnameCache.get(prid));
    }
}
