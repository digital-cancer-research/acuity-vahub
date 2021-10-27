package com.acuity.visualisations.rest.config.oauth.jwt;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


/**
 * Holder for JWT token from the request.
 * <p/>
 * Other fields aren't used but necessary to comply to the contracts of AbstractUserDetailsAuthenticationProvider
 */
public class JwtAuthenticationToken extends UsernamePasswordAuthenticationToken {


    public JwtAuthenticationToken(Object principal) {
        super(principal, null);
    }
}
