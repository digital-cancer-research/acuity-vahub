package com.acuity.visualisations.config.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

/**
 * Async doesnt work with concurrent task executors in tests.  So use SimpleAsyncTaskExecutor
 * for unit testing with H2
 * 
 * @author ksnd199
 */
@Configuration
public class ApplicationEnableTestExecutorConfig {

    @Bean
    public TaskExecutor detectExecutor() {
        return new SimpleAsyncTaskExecutor();
    }
}
