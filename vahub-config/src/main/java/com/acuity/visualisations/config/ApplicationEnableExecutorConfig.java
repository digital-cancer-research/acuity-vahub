package com.acuity.visualisations.config;

import com.acuity.visualisations.config.async.AsyncConfig;
import com.acuity.visualisations.config.async.SchedulingConfig;
import com.acuity.visualisations.config.async.executor.TaskExecutorConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author ksnd199
 */
@Configuration
@Import({
        TaskExecutorConfig.class,
        SchedulingConfig.class,
        AsyncConfig.class
})
public class ApplicationEnableExecutorConfig {
    
}
