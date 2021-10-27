package com.acuity.visualisations.rawdatamodel.statistics.collectors;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 *
 * @author ksnd199
 */
public class DoubleSummaryStatisticsCollector implements Collector<Double, DoubleSummaryStatistics, DoubleSummaryStatistics> {

    public static Collector<Double, DoubleSummaryStatistics, DoubleSummaryStatistics> toDoubleSummaryStatistics() {
        return new DoubleSummaryStatisticsCollector();
    }

    @Override
    public Supplier supplier() {
        return DoubleSummaryStatistics::new;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.CONCURRENT);
    }

    @Override
    public BiConsumer<DoubleSummaryStatistics, Double> accumulator() {
        return (builder, t) -> builder.accept(t);
    }

    @Override
    public BinaryOperator<DoubleSummaryStatistics> combiner() {
        return (l, r) -> {
            l.combine(r);
            return l;
        };
    }

    @Override
    public Function<DoubleSummaryStatistics, DoubleSummaryStatistics> finisher() {
        return (dss) -> dss.build();
    }
}
