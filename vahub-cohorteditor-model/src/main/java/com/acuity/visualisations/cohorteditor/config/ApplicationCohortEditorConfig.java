package com.acuity.visualisations.cohorteditor.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableAutoConfiguration
@ComponentScan({"com.acuity.visualisations.cohorteditor", "com.acuity.va.security.common.service"})
@EnableJpaRepositories("com.acuity.visualisations.cohorteditor.repository")
@EntityScan("com.acuity.visualisations.cohorteditor.entity")
public class ApplicationCohortEditorConfig {
}
