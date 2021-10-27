package com.acuity.visualisations.rawdatamodel.statistics.collectors;

import java.util.Date;
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
public class DateSummaryStatisticsCollector implements Collector<Date, DateSummaryStatistics, DateSummaryStatistics> {

    public static Collector<Date, DateSummaryStatistics, DateSummaryStatistics> toDateSummaryStatistics() {
        return new DateSummaryStatisticsCollector();
    }

    @Override
    public Supplier supplier() {
        return DateSummaryStatistics::new;
    }

    @Override
    public Set<Characteristics> characteristics() {
        return EnumSet.of(Characteristics.CONCURRENT);
    }

    @Override
    public BiConsumer<DateSummaryStatistics, Date> accumulator() {
        return (builder, t) -> builder.accept(t);
    }

    @Override
    public BinaryOperator<DateSummaryStatistics> combiner() {
        return (l, r) -> {
            l.combine(r);
            return l;
        };
    }

    @Override
    public Function<DateSummaryStatistics, DateSummaryStatistics> finisher() {
        return (dss) -> dss.build();
    }
}
