package com.acuity.visualisations.rest.test.config;

import com.acuity.visualisations.rest.config.ApplicationWeb;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;

import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;

@SpringBootApplication
@ComponentScan(
    basePackages = {"com.acuity.visualisations.rest.resources", "com.acuity.visualisations.rest.util"},
    excludeFilters = @Filter(type = ASSIGNABLE_TYPE,
            value = {ApplicationWeb.class})
)
@EnableAutoConfiguration(exclude = {
    org.springframework.boot.autoconfigure.security.SecurityAutoConfiguration.class,
    org.springframework.boot.actuate.autoconfigure.ManagementWebSecurityAutoConfiguration.class})
public class ApplicationBootOnlyWebDisableSecurity extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApplicationBootOnlyWebDisableSecurity.class);
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(new Object[]{ApplicationBootOnlyWebDisableSecurity.class}, args);
    }
}
