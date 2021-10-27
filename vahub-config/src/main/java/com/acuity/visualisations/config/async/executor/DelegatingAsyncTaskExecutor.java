package com.acuity.visualisations.config.async.executor;

import com.acuity.visualisations.config.async.executor.context.TaskContext;
import com.acuity.visualisations.config.async.executor.context.TaskContextCapturer;
import org.springframework.core.task.AsyncTaskExecutor;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class DelegatingAsyncTaskExecutor implements AsyncTaskExecutor {

    private final AsyncTaskExecutor delegate;
    private final Collection<TaskContextCapturer> capturers;

    public DelegatingAsyncTaskExecutor(AsyncTaskExecutor delegate, Collection<TaskContextCapturer> capturers) {
        this.delegate = delegate;
        this.capturers = capturers;
    }

    public DelegatingAsyncTaskExecutor(AsyncTaskExecutor delegate) {
        this(delegate, Collections.emptySet());
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        delegate.execute(wrap(task), startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return delegate.submit(wrap(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return delegate.submit(wrap(task));
    }

    @Override
    public void execute(Runnable task) {
        delegate.execute(wrap(task));
    }

    private Runnable wrap(Runnable runnable) {
        Collection<TaskContext> contexts = capture(capturers);

        return () -> {
            try {
                contexts.forEach(TaskContext::setup);
                runnable.run();
            } finally {
                contexts.forEach(TaskContext::teardown);
            }
        };
    }

    private <T> Callable<T> wrap(Callable<T> callable) {
        Collection<TaskContext> contexts = capture(capturers);

        return () -> {
            try {
                contexts.forEach(TaskContext::setup);
                return callable.call();
            } finally {
                contexts.forEach(TaskContext::teardown);
            }
        };
    }

    private Collection<TaskContext> capture(Collection<TaskContextCapturer> capturers) {
        return capturers.stream()
                .map(TaskContextCapturer::capture)
                .collect(Collectors.toList());
    }
}
