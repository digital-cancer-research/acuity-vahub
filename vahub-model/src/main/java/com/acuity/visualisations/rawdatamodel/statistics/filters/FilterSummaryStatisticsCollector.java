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
