package com.acuity.visualisations.config.async.executor.context;

import org.slf4j.MDC;

import java.util.Map;

public class MDCTaskContextCapturer implements TaskContextCapturer {
    @Override
    public TaskContext capture() {
        Map<String, String> context = MDC.getCopyOfContextMap();

        if (context == null) {
            return TaskContext.EMPTY;
        }

        return new TaskContext() {
            @Override
            public void setup() {
                MDC.setContextMap(context);
            }

            @Override
            public void teardown() {
                MDC.clear();
            }
        };
    }
}
