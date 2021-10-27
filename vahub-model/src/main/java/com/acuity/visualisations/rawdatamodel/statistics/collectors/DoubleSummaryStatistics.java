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
 * Copied from javas DoubleSummaryStatistics but to allow nulls and have a hasNulls attribute.
 *
 * maybe need to put nulls here so it returns nulls if no doubles passed in private double min = Double.POSITIVE_INFINITY; private double max =
 * Double.NEGATIVE_INFINITY;
 */
@NoArgsConstructor
public class DoubleSummaryStatistics implements DoubleConsumer {

    private long count;
    private double sum;
    private boolean hasNulls;
    private boolean hasOneNoneNull = false;
    private double sumCompensation; // Low order bits of sum
    private double simpleSum; // Used to compute right sum for non-finite inputs
    private double min = 0;
    private double max = 0;

    /**
     * Records another value into the summary information.
     *
     * @param value the input value
     */
    @Override
    public void accept(Double value) {
        if (value != null) {
            hasOneNoneNull = true;
            ++count;
            simpleSum += value;
            sumWithCompensation(value);
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
    public void combine(DoubleSummaryStatistics other) {
        hasNulls = other.hasNulls || hasNulls;
        hasOneNoneNull = other.hasOneNoneNull || hasOneNoneNull;
        count += other.count;
        simpleSum += other.simpleSum;
        sumWithCompensation(other.sum);
        sumWithCompensation(other.sumCompensation);
        min = Math.min(min, other.min);
        max = Math.max(max, other.max);
    }

    /**
     * Incorporate a new double value using Kahan summation / compensated summation.
     */
    private void sumWithCompensation(double value) {
        double tmp = value - sumCompensation;
        double velvel = sum + tmp; // Little wolf of rounding error
        sumCompensation = (velvel - sum) - tmp;
        sum = velvel;
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
    public final double getSum() {
        // Better error bounds to add both terms as the final sum
        double tmp = sum + sumCompensation;
        if (Double.isNaN(tmp) && Double.isInfinite(simpleSum)) {
        // If the compensated sum is spuriously NaN from
        // accumulating one or more same-signed infinite values,
        // return the correctly-signed infinity stored in
        // simpleSum.        
            return simpleSum;
        } else {
            return tmp;
        }
    }

    /**
     * Returns the minimum recorded value, {@code Double.NaN} if any recorded value was NaN or {@code Double.POSITIVE_INFINITY} if no values were recorded.
     * Unlike the numerical comparison operators, this method considers negative zero to be strictly smaller than positive zero.
     *
     * @return the minimum recorded value, {@code Double.NaN} if any recorded value was NaN or {@code Double.POSITIVE_INFINITY} if no values were recorded
     */
    public final Double getMin() {
        return hasOneNoneNull ? min : null;
    }

    /**
     * Returns the maximum recorded value, {@code Double.NaN} if any recorded value was NaN or {@code Double.NEGATIVE_INFINITY} if no values were recorded.
     * Unlike the numerical comparison operators, this method considers negative zero to be strictly smaller than positive zero.
     *
     * @return the maximum recorded value, {@code Double.NaN} if any recorded value was NaN or {@code Double.NEGATIVE_INFINITY} if no values were recorded
     */
    public final Double getMax() {
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
        return getCount() > 0 ? getSum() / getCount() : null;
    }

    public final boolean hasNulls() {
        return hasNulls;
    }

    public DoubleSummaryStatistics build() {
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
                "%s{count=%d, sum=%f, min=%f, average=%f, max=%f}",
                this.getClass().getSimpleName(),
                getCount(),
                getSum(),
                getMin(),
                getAverage(),
                getMax());
    }
}
