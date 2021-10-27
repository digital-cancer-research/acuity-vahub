/*
 * Copyright 2021 The University of Manchester
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
