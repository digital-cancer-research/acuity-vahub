package com.acuity.visualisations.config.async.executor;

import com.acuity.visualisations.config.async.executor.context.MDCTaskContextCapturer;
import com.acuity.visualisations.config.async.executor.context.SecurityTaskContextCapturer;
import com.acuity.visualisations.config.async.executor.context.TaskContextCapturer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.concurrent.DelegatingSecurityContextScheduledExecutorService;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Configuration
public class TaskExecutorConfig {

    @Bean("scheduledTaskExecutor")
    public Executor scheduledTaskExecutor() {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(16);
        return new DelegatingSecurityContextScheduledExecutorService(executor);
    }

    @Bean("asyncTaskExecutor")
    public AsyncTaskExecutor delegatingSecurityContextAsyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        executor.setCorePoolSize(16);
        executor.setQueueCapacity(1000);
        executor.setWaitForTasksToCompleteOnShutdown(true);

        executor.initialize();

        Collection<TaskContextCapturer> capturers = Arrays.asList(
                new SecurityTaskContextCapturer(),
                new MDCTaskContextCapturer()
        );

        return new DelegatingAsyncTaskExecutor(executor, capturers);
    }

}
