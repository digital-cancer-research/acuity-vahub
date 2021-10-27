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

package com.acuity.visualisations.rest.config.oauth.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.DefaultHeader;
import io.jsonwebtoken.impl.DefaultJwtParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.Map;
import java.util.stream.StreamSupport;

/**
 * Filter that orchestrates authentication by using supplied Azure access_token (JWT token)
 *
 */
@Slf4j
public class AzureJwtAuthenticationTokenFilter extends AbstractAuthenticationProcessingFilter {
    private static final String AZURE_CONFIG_URL = "https://login.microsoftonline.com/common/.well-known/openid-configuration";
    private static final String NAME = "unique_name";
    private static final String JWKS_URI_PARAMETER_NAME = "jwks_uri";
    private static final String KEY_ID_PARAMETER_NAME = "kid";
    private static final String KEYS_PARAMETER_NAME = "keys";
    private static final String X_509_CERTIFICATE_CHAIN = "x5c";
    private static final String X_509_CERTIFICATE_NAME = "X.509";

    private RestOperations restTemplate = new RestTemplate();

    @Value("${azure.appIdUri:}")
    private String appIdUri;

    public AzureJwtAuthenticationTokenFilter() {
        super("/**");
        this.setAuthenticationSuccessHandler(new JwtAuthenticationSuccessHandler());
        this.setAuthenticationFailureHandler(new JwtAuthenticationFailureHandler());
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        String header = ((HttpServletRequest) req).getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotEmpty(header) && header.startsWith(OAuth2AccessToken.BEARER_TYPE)) {
            super.doFilter(req, res, chain);
        } else {
            chain.doFilter(req, res);
        }
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        String authToken = header.substring(7);

        Claims tokenClaims = parseAndValidateToken(authToken);
        if (null != tokenClaims) {
            log.debug("Access token is valid");
            JwtAuthenticationToken authRequest = new JwtAuthenticationToken(tokenClaims.get(NAME));
            return getAuthenticationManager().authenticate(authRequest);
        } else {
            log.debug("Access token is invalid");
            throw new JwtTokenValidationException("Invalid token");
        }
    }

    private Claims parseAndValidateToken(String authToken) {
        try {
            String certificate = retrieveCertificate(authToken);

            byte[] certChain = Base64.getDecoder().decode(certificate);
            InputStream in = new ByteArrayInputStream(certChain);

            CertificateFactory certFactory = CertificateFactory.getInstance(X_509_CERTIFICATE_NAME);
            X509Certificate cert = (X509Certificate) certFactory.generateCertificate(in);
            PublicKey pubKeyNew = cert.getPublicKey();
            return Jwts.parser()
                    .setSigningKey(pubKeyNew)
                    .requireAudience(appIdUri)
                    .parseClaimsJws(authToken).getBody();
        } catch (JwtException jwtException) {
            log.error("JWT validation failed.", jwtException);
            return null;
        } catch (CertificateException e) {
            log.error("Unable to generate certificate.", e);
            return null;
        } catch (Exception e) {
            log.error("Exception caught.", e);
            return null;
        }
    }

    private String retrieveCertificate(String authToken) throws IOException {

        String unsignedToken = authToken.substring(0, authToken.lastIndexOf(".") + 1);
        DefaultJwtParser parser = new DefaultJwtParser();
        Jwt<?, ?> jwt = parser.parse(unsignedToken);
        DefaultHeader headerClaims = (DefaultHeader) jwt.getHeader();
        log.debug("HeaderClaims: {}", headerClaims);

        ResponseEntity<Map> azureConfigResponse = restTemplate.getForEntity(AZURE_CONFIG_URL, Map.class);

        String keysUrl = azureConfigResponse.getBody().get(JWKS_URI_PARAMETER_NAME).toString();

        ResponseEntity<String> azureKeysResponse = restTemplate.getForEntity(keysUrl, String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        final JsonNode arrNode = objectMapper.readTree(azureKeysResponse.getBody()).get(KEYS_PARAMETER_NAME);

        return StreamSupport.stream(arrNode.spliterator(), false)
                .filter(o -> headerClaims.get(KEY_ID_PARAMETER_NAME).equals(o.findPath(KEY_ID_PARAMETER_NAME).asText()))
                .map(o -> o.findPath(X_509_CERTIFICATE_CHAIN).elements().next().asText())
                .findAny()
                .orElse("");
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        super.successfulAuthentication(request, response, chain, authResult);
        chain.doFilter(request, response);
    }
}
