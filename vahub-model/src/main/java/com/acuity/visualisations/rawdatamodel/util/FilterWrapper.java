package com.acuity.visualisations.rawdatamodel.util;

import com.googlecode.cqengine.ConcurrentIndexedCollection;
import com.googlecode.cqengine.IndexedCollection;
import com.googlecode.cqengine.persistence.wrapping.WrappingPersistence;

import java.util.Collection;


public final class FilterWrapper {

    private FilterWrapper() {

    }

    /**
     * Wraps a list to it can be queried by cqengine
     */
    public static <T> IndexedCollection<T> wrap(Collection<T> events) {

        return new ConcurrentIndexedCollection(
                WrappingPersistence.aroundCollection(events)
        );
    }
}
