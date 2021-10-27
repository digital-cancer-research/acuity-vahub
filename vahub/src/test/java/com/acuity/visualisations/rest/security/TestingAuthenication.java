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

package com.acuity.visualisations.rest.security;

import com.acuity.va.security.acl.domain.AcuitySidDetails;
import com.google.common.collect.Lists;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 *
 */
public class TestingAuthenication extends TestingAuthenticationToken {
    private static long serialVersionUID = -2350894794129784771L;
    private static List<GrantedAuthority> roles = Lists.newArrayList(new SimpleGrantedAuthority("ACL_ADMINISTRATOR"));

    public TestingAuthenication(String principal) {
        super(new AcuitySidDetails(principal, principal, roles), "pass", roles);
        setAuthenticated(true);
    }

    public TestingAuthenication(String principal, List<String> newRoles) {
        super(new AcuitySidDetails(principal, principal, roles), "pass", newRoles.stream().map(r -> new SimpleGrantedAuthority(r)).collect(toList()));
        setAuthenticated(true);
    }
}
