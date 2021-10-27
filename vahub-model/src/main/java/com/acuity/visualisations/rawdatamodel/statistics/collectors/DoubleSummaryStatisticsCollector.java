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
