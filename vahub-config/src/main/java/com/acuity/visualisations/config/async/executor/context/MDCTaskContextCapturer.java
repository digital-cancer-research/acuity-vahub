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
