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

package com.acuity.visualisations.common.util;

import com.acuity.va.security.acl.domain.AcuitySidDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

/**
 * Class wrapping SecurityContextHolder allowing for easier testing by injecting mock Security service into tests
 *
 * @author glen
 */
@Service
@Slf4j
public class Security {
    /**
     * Gets the current Authentication logged into the system
     */
    public Authentication getAuthentication() {
        log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Gets the current AcuityUserDetails logged into the system
     */
    public AcuitySidDetails getAcuityUserDetails() {
        log.info(SecurityContextHolder.getContext().getAuthentication().toString());
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AcuitySidDetails) {
            return (AcuitySidDetails) authentication;
        } else {
            return (AcuitySidDetails) authentication.getPrincipal();
        }
    }

    /**
     * Gets the current user logged into the system
     */
    public String getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("No Authentication found in spring context");
        } else {
            Object principal = authentication.getPrincipal();
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername();
            } else {
                return principal.toString();
            }
        }
    }
}
