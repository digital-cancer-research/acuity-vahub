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

import java.util.Date;

/**
 * Gets the min max date for a list of dates, but allows nulls
 */
@NoArgsConstructor
public class DateSummaryStatistics implements DateConsumer {

    private long count;
    private boolean hasNulls;
    private Date min = null;
    private Date max = null;

    /**
     * Records a new {@code Date} value into the summary information.
     *
     * @param value the input value
     */
    @Override
    public void accept(Date value) {
        ++count;

        if (value != null) {
            if (min == null) {
                min = value;
            } else if (value.before(min)) {
                min = value;
            }

            if (max == null) {
                max = value;
            } else if (value.after(max)) {
                max = value;
            }
        } else {
            hasNulls = true;
        }
    }

    /**
     * Combines the state of another {@code DateSummaryStatistics} into this one.
     *
     * @param other another {@code DateSummaryStatistics}
     * @throws NullPointerException if {@code other} is null
     */
    public void combine(DateSummaryStatistics other) {                
        if (other != null) {
            count += other.count;
            hasNulls = other.hasNulls || hasNulls;
            
            if (min == null) {
                min = other.min;
            } else if (other.min.before(min)) {
                min = other.min;
            }

            if (max == null) {
                max = other.max;
            } else if (other.max.after(max)) {
                max = other.max;
            }
        }
    }

    /**
     * Returns the count of values recorded.
     *
     * @return the count of values
     */
    public final long getCount() {
        return count;
    }

    /**
     * Returns the minimum value recorded, or {@code new Date(Long.MAX_VALUE)} if no values have been recorded.
     *
     * @return the minimum value, or {@code new Date(Long.MAX_VALUE)} if none
     */
    public final Date getMin() {
        return min;
    }

    /**
     * Returns the maximum value recorded, or {@code new Date(Long.MAX_VALUE)} if no values have been recorded
     *
     * @return the maximum value, or {@code new Date(Long.MAX_VALUE)} if none
     */
    public final Date getMax() {
        return max;
    }
    
    public final boolean hasNulls() {
        return hasNulls;
    }
    
    public DateSummaryStatistics build() {
        return this;
    }

    /**
     * Returns a non-empty string representation of this object suitable for debugging. The exact presentation format is unspecified and may vary between
     * implementations and versions.
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        return String.format(
                "%s{count=%d, min=%s, max=%s}",
                this.getClass().getSimpleName(),
                getCount(),
                getMin(),
                getMax());
    }
}
