package com.acuity.visualisations.rest.config.oauth.config;

import com.acuity.visualisations.rest.config.oauth.jwt.AzureJwtAuthenticationTokenFilter;
import com.acuity.visualisations.rest.config.oauth.jwt.JwtAuthenticationProvider;
import com.acuity.va.security.auth.azure.AzureSecurityAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collections;

@Order(97)
@Configuration
@EnableOAuth2Client
@Profile("azure-sso")
public class AzureJwtWebSecurityConfiguration extends AzureSecurityAutoConfiguration {

    @Autowired
    private JwtAuthenticationProvider authenticationProvider;

    @Bean
    @Override
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(authenticationProvider));
    }

    private AzureJwtAuthenticationTokenFilter azureJwtAuthenticationTokenFilter() {
        AzureJwtAuthenticationTokenFilter authenticationTokenFilter = new AzureJwtAuthenticationTokenFilter();
        authenticationTokenFilter.setAuthenticationManager(authenticationManager());
        return authenticationTokenFilter;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        http.addFilterBefore(azureJwtAuthenticationTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }
}
