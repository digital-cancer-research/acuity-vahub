package com.acuity.visualisations.config.async.executor.context;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityTaskContextCapturer implements TaskContextCapturer {

    @Override
    public TaskContext capture() {
        SecurityContext context = SecurityContextHolder.getContext();

        return new TaskContext() {
            private SecurityContext original;

            @Override
            public void setup() {
                this.original = SecurityContextHolder.getContext();
                SecurityContextHolder.setContext(context);
            }

            @Override
            public void teardown() {
                SecurityContextHolder.setContext(original);
                this.original = null;
            }
        };
    }
}
