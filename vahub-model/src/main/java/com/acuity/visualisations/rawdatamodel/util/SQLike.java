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

package com.acuity.visualisations.rawdatamodel.util;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.groupingByConcurrent;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

/**
 *
 * @author ksnd199
 */
public final class SQLike<T> {

    private Stream<T> stream;

    private SQLike(Stream<T> stream) {
        this.stream = stream;
    }

    public static <T> SQLike<T> on(Stream<T> stream) {
        return new SQLike<T>(stream);
    }

    public <K> List<PartitionByCount<K>> partitionByAndCount(Function<? super T, ? extends K> classifier) {

        return stream.collect(
                groupingBy(classifier, counting())
        ).entrySet().stream().map(es -> new PartitionByCount<K>(es.getKey(), es.getValue())).collect(toList());
    }

    public <K> List<PartitionByCount<K>> partitionByAndDistinctCount(Function<? super T, ? extends K> classifier) {

        return stream.collect(
                groupingBy(classifier, collectingAndThen(Collectors.toSet(), (set) -> Long.valueOf(set.size())))
        ).entrySet().stream().map(es -> new PartitionByCount<K>(es.getKey(), es.getValue())).collect(toList());
    }

    public <K, U> List<PartitionByCount<K>> partitionByMapAndDistinctCount(
            Function<? super T, ? extends K> classifier,
            Function<? super T, ? extends U> mapper) {

        return stream.collect(
                groupingBy(classifier, mapping(mapper, collectingAndThen(Collectors.toSet(), (set) -> Long.valueOf(set.size()))))
        ).entrySet().stream().map(es -> new PartitionByCount<K>(es.getKey(), es.getValue())).collect(toList());
    }

    public <K> List<PartitionByCount<K>> concurrentPartitionByAndCount(Function<? super T, ? extends K> classifier) {

        return stream.collect(
                groupingByConcurrent(classifier, counting())
        ).entrySet().stream().map(es -> new PartitionByCount<K>(es.getKey(), es.getValue())).collect(toList());
    }

    public <K> List<PartitionByCount<K>> concurrentPartitionByAndDistinctCount(Function<? super T, ? extends K> classifier) {

        return stream.collect(
                groupingByConcurrent(classifier, collectingAndThen(Collectors.toSet(), (set) -> Long.valueOf(set.size())))
        ).entrySet().stream().map(es -> new PartitionByCount<K>(es.getKey(), es.getValue())).collect(toList());
    }

    public <K, U> List<PartitionByCount<K>> concurrentPartitionByMapAndDistinctCount(
            Function<? super T, ? extends K> classifier,
            Function<? super T, ? extends U> mapper) {

        return stream.collect(
                groupingByConcurrent(classifier, mapping(mapper, collectingAndThen(Collectors.toSet(), (set) -> Long.valueOf(set.size()))))
        ).entrySet().stream().map(es -> new PartitionByCount<K>(es.getKey(), es.getValue())).collect(toList());
    }

    public <K, A, R, J> List<PartitionByValue<K, J>> partitionByAndThen(Function<? super T, ? extends K> classifier,
            Collector<T, A, R> downstream, Function<R, J> finisher) {

        return stream.collect(
                groupingBy(classifier, collectingAndThen(downstream, finisher))
        ).entrySet().stream().map(es -> new PartitionByValue<K, J>(es.getKey(), es.getValue())).collect(toList());
    }
}
