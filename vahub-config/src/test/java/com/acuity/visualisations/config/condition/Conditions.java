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

package com.acuity.visualisations.config.condition;

import java.util.List;
import org.assertj.core.api.Condition;

/**
 * Common assertj conditions
 *
 * @author ksnd199
 */
public final class Conditions {

    private Conditions() {
    }

    public static Condition<Integer> between(Integer from, Integer to) {
        return new IntRangeCondition(from, to);
    }

    public static Condition<Integer> betweenInclusiveOf(Integer from, Integer to) {
        return new IntRangeCondition(from - 1, to + 1);
    }

    public static Condition<Double> between(Double from, Double to) {
        return new DoubleRangeCondition(from, to);
    }

    public static Condition<Double> betweenInclusiveOf(Double from, Double to) {
        return new DoubleRangeCondition(from - 0.1, to + 0.1);
    }
    
    public static Condition<Float> between(Float from, Float to) {
        return new FloatRangeCondition(from, to);
    }

    public static Condition<Float> betweenInclusiveOf(Float from, Float to) {
        return new FloatRangeCondition(from - 0.1f, to + 0.1f);
    }

    public static Condition<Long> between(Long from, Long to) {
        return new LongRangeCondition(from, to);
    }

    public static Condition<Long> betweenInclusiveOf(Long from, Long to) {
        return new LongRangeCondition(from - 1, to + 1);
    }

    public static Condition<Integer> greaterThan(Integer value) {
        return new GreaterThanIntegerCondition(value);
    }

    public static Condition<Integer> greaterThanOrEqualTo(Integer value) {
        return new GreaterThanIntegerCondition(value - 1);
    }

    public static Condition<Double> greaterThan(Double value) {
        return new GreaterThanDoubleCondition(value);
    }

    public static Condition<Double> greaterThanOrEqualTo(Double value) {
        return new GreaterThanDoubleCondition(value - 1);
    }
    
    public static Condition<Float> greaterThanOrEqualTo(Float value) {
        return new GreaterThanFloatCondition(value - 1);
    }

    public static Condition<Integer> lessThan(Integer value) {
        return new LessThanIntegerCondition(value);
    }

    public static Condition<Integer> lessThanOrEqualTo(Integer value) {
        return new LessThanIntegerCondition(value + 1);
    }
    
    public static Condition<Float> lessThanOrEqualTo(Float value) {
        return new LessThanFloatCondition(value + 1);
    }

    public static Condition<Double> lessThan(Double value) {
        return new LessThanDoubleCondition(value);
    }

    public static Condition<Double> lessThanOrEqualTo(Double value) {
        return new LessThanDoubleCondition(value + 1);
    }

    public static Condition<String> in(List<String> myList) {
        return new ListContainsCondition<String>(myList);
    }

    public static Condition<String> notNull() {
        return new NotNullStringCondition();
    }
}
