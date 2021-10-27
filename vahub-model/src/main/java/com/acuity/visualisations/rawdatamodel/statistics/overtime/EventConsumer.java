package com.acuity.visualisations.rawdatamodel.statistics.overtime;

@FunctionalInterface
public interface EventConsumer {

    void accept(Object e);

}
