package com.acuity.visualisations.rawdatamodel.statistics.filters;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class FilterSummaryStatisticsCollector<T, N extends FilterSummaryStatistics<T>> implements Collector<Object, N, N> {

    private final Class<N> statisticsClazz;

    public FilterSummaryStatisticsCollector(Class<N> clazz) {
        this.statisticsClazz = clazz;
    }

    @Override
    public Supplier<N> supplier() {

        return () -> {
            try {
                return statisticsClazz.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        };

    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.CONCURRENT);
    }

    @Override
    public BiConsumer<N, Object> accumulator() {
        return FilterSummaryStatistics::accept;
    }

    @Override
    public BinaryOperator<N> combiner() {
        return (l, r) -> {
            l.combine(r);
            return l;
        };
    }

    // Is it possible to make a generic producer here?
    @Override
    public Function<N, N> finisher() {
        return n -> (N) n.build();
    }
}
