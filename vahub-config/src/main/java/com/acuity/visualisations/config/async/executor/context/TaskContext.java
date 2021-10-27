package com.acuity.visualisations.config.async.executor.context;

public interface TaskContext {

    TaskContext EMPTY = new TaskContext() {
        @Override
        public void setup() {
            // default implementation ignored
        }

        @Override
        public void teardown() {
            // default implementation ignored
        }
    };

    void setup();

    void teardown();

}
