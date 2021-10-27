package com.acuity.visualisations.rest.config.oauth.jwt;


import com.acuity.va.security.acl.domain.AcuitySidDetails;
import com.acuity.va.security.auth.common.ISecurityResourceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;

/**
 * Used for checking the token from the request and supply the UserDetails if the token is valid
 */
@Component
@Slf4j
public class JwtAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    @Autowired
    private ISecurityResourceClient securityResourceClient;

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        // default implementation ignored
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {
        JwtAuthenticationToken jwtAuthenticationToken = (JwtAuthenticationToken) authentication;
        AcuitySidDetails acuityUserDetails;
        try {
            acuityUserDetails = securityResourceClient
                    .loadUserByUsername(jwtAuthenticationToken.getPrincipal().toString().toLowerCase());
        } catch (IllegalAccessException | IOException | HttpClientErrorException | HttpServerErrorException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        log.info("acuityUserDetails" + acuityUserDetails);
        return new AcuitySidDetails(acuityUserDetails.getSidAsString(), acuityUserDetails.getFullName(), acuityUserDetails.getAuthorities());
    }
}
