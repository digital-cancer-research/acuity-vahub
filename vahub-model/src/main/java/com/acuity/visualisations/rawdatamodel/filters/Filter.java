package com.acuity.visualisations.rawdatamodel.filters;

import java.io.Serializable;

public interface Filter<T extends Comparable<T>> extends Serializable {

    boolean isValid();
    
    Boolean getIncludeEmptyValues();

    void complete(Filter<T> rangeFilter);

    void completeWithValue(T value);

    static Filter newFilter(Class filterClass) {
        try {
            return (Filter) filterClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ignored) {
            return null;
        }
    }
}
