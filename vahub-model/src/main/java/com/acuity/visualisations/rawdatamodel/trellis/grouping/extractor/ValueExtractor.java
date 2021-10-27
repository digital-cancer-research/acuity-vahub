package com.acuity.visualisations.rawdatamodel.trellis.grouping.extractor;

public interface ValueExtractor<T, G> {
    /**
     * Returns {@link G} type value extracted from {@param object}.
     *
     * @param object an object to extract some value from
     * @return {@link G} type value extracted from {@param object}
     */
    G extractFrom(T object);
}
