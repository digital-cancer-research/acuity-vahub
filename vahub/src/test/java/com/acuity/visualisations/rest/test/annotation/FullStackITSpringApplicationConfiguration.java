package com.acuity.visualisations.rest.test.annotation;

import com.acuity.visualisations.config.ApplicationModelConfig;
import com.acuity.visualisations.rest.config.ApplicationWeb;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author glen
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"NoScheduledJobs", "it", "local-no-security"})
@ContextConfiguration(classes = {ApplicationModelConfig.class, ApplicationWeb.class})
@Transactional
public @interface FullStackITSpringApplicationConfiguration {
}
