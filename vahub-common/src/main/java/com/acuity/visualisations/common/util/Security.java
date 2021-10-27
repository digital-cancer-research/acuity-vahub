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
