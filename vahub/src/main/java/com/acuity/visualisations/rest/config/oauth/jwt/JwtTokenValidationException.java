package com.acuity.visualisations.rest.config.oauth.jwt;


import org.springframework.security.core.AuthenticationException;

public class JwtTokenValidationException extends AuthenticationException {

    public JwtTokenValidationException(String msg) {
        super(msg);
    }
}
