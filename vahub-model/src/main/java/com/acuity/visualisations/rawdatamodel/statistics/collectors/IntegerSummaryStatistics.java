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

import lombok.NoArgsConstructor;

/**
 * Copied from javas IntegerSummaryStatistics but to allow nulls and have a hasNulls attribute
 *
 * maybe need to put nulls here so it returns nulls if no integers passed in private double min = Double.POSITIVE_INFINITY; private double max =
 * Double.NEGATIVE_INFINITY;
 *
 */
@NoArgsConstructor
public class IntegerSummaryStatistics implements IntegerConsumer {

    private int count;
    private int sum;
    private boolean hasNulls;
    private boolean hasOneNoneNull = false;
    private int min = 0;
    private int max = 0;

    /**
     * Records another value into the summary information.
     *
     * @param value the input value
     */
    @Override
    public void accept(Integer value) {
        if (value != null) {
            ++count;
            hasOneNoneNull = true;
            min = Math.min(min, value);
            max = Math.max(max, value);
        } else {
            hasNulls = true;
        }
    }

    /**
     * Combines the state of another {@code DoubleSummaryStatistics} into this one.
     *
     * @param other another {@code DoubleSummaryStatistics}
     * @throws NullPointerException if {@code other} is null
     */
    public void combine(IntegerSummaryStatistics other) {
        hasNulls = other.hasNulls || hasNulls;
        hasOneNoneNull = other.hasOneNoneNull || hasOneNoneNull;
        count += other.count;
        min = Math.min(min, other.min);
        max = Math.max(max, other.max);
    }

    /**
     * Return the count of values recorded.
     *
     * @return the count of values
     */
    public final long getCount() {
        return count;
    }

    /**
     * Returns the sum of values recorded, or zero if no values have been recorded.
     *
     * If any recorded value is a NaN or the sum is at any point a NaN then the sum will be NaN.
     *
     * <p>
     * The value of a floating-point sum is a function both of the input values as well as the order of addition operations. The order of addition operations of
     * this method is intentionally not defined to allow for implementation flexibility to improve the speed and accuracy of the computed result.
     *
     * In particular, this method may be implemented using compensated summation or other technique to reduce the error bound in the numerical sum compared to a
     * simple summation of {@code double} values.
     *
     * @apiNote Values sorted by increasing absolute magnitude tend to yield more accurate results.
     *
     * @return the sum of values, or zero if none
     */
    public final int getSum() {
        return sum;
    }

    /**
     * Returns the minimum recorded value, {@code Double.NaN} if any recorded value was NaN or {@code Double.POSITIVE_INFINITY} if no values were recorded.
     * Unlike the numerical comparison operators, this method considers negative zero to be strictly smaller than positive zero.
     *
     * @return the minimum recorded value, {@code Double.NaN} if any recorded value was NaN or {@code Double.POSITIVE_INFINITY} if no values were recorded
     */
    public final Integer getMin() {
        return hasOneNoneNull ? min : null;
    }

    /**
     * Returns the maximum recorded value, {@code Double.NaN} if any recorded value was NaN or {@code Double.NEGATIVE_INFINITY} if no values were recorded.
     * Unlike the numerical comparison operators, this method considers negative zero to be strictly smaller than positive zero.
     *
     * @return the maximum recorded value, {@code Double.NaN} if any recorded value was NaN or {@code Double.NEGATIVE_INFINITY} if no values were recorded
     */
    public final Integer getMax() {
        return hasOneNoneNull ? max : null;
    }

    /**
     * Returns the arithmetic mean of values recorded, or zero if no values have been recorded.
     *
     * If any recorded value is a NaN or the sum is at any point a NaN then the average will be code NaN.
     *
     * <p>
     * The average returned can vary depending upon the order in which values are recorded.
     *
     * This method may be implemented using compensated summation or other technique to reduce the error bound in the {@link #getSum
     * numerical sum} used to compute the average.
     *
     * @apiNote Values sorted by increasing absolute magnitude tend to yield more accurate results.
     *
     * @return the arithmetic mean of values, or zero if none
     */
    public final Double getAverage() {
        if (!hasOneNoneNull) {
            return null;
        }
        return getCount() > 0 ? (double) getSum() / getCount() : null;
    }

    public final boolean hasNulls() {
        return hasNulls;
    }

    public IntegerSummaryStatistics build() {
        return this;
    }

    /**
     * {@inheritDoc}
     *
     * Returns a non-empty string representation of this object suitable for debugging. The exact presentation format is unspecified and may vary between
     * implementations and versions.
     */
    @Override
    public String toString() {
        return String.format(
                "%s{count=%s, sum=%s, min=%s, average=%s, max=%s}",
                this.getClass().getSimpleName(),
                getCount(),
                getSum(),
                getMin(),
                getAverage(),
                getMax());
    }
}
