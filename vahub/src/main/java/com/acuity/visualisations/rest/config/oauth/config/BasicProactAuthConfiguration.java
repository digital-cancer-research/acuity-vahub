package com.acuity.visualisations.rest.config.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@Order(3)
public class BasicProactAuthConfiguration extends WebSecurityConfigurerAdapter {


    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.inMemoryAuthentication().withUser("proact").password("proactbas1c").roles("PROACT_USER");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.antMatcher("/resources/proact/**")
                .authorizeRequests()
                .anyRequest().hasRole("PROACT_USER")
                .and()
                .httpBasic();
    }
}
