package com.acuity.visualisations.config.annotation;

import com.github.springtestdbunit.DbUnitTestExecutionListener;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Transactional
@Rollback(true)
@ActiveProfiles(profiles = {"h2", "NoScheduledJobs"})
@TestExecutionListeners({
        DependencyInjectionTestExecutionListener.class,        
        TransactionalTestExecutionListener.class,
        DbUnitTestExecutionListener.class})
public @interface TransactionalH2Test {
}
