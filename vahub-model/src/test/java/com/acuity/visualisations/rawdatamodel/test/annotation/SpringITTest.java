package com.acuity.visualisations.rawdatamodel.test.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.acuity.visualisations.rawdatamodel.dataproviders.config.DataProviderConfiguration;
import com.acuity.visualisations.rawdatamodel.test.TestConfig;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

/**
 *
 * @author glen
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ActiveProfiles(profiles = {"it"})
@ContextConfiguration(classes = {TestConfig.class, DataProviderConfiguration.class})
@TestPropertySource(properties = "env.name=mock")
public @interface SpringITTest {
}
