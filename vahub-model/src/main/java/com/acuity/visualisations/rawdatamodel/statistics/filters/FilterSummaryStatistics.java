package com.acuity.visualisations.rawdatamodel.statistics.filters;

import com.acuity.visualisations.rawdatamodel.filters.Filters;
import com.acuity.visualisations.rawdatamodel.statistics.overtime.EventConsumer;

public interface FilterSummaryStatistics<T> extends EventConsumer {
    @Override
    void accept(Object value);

    void combine(FilterSummaryStatistics<T> other);

    Filters<T> getFilters();

    int count();

    FilterSummaryStatistics<T> build();

    FilterSummaryStatistics<T> newStatistics();
}
