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

package com.acuity.visualisations.rawdatamodel.filters;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.Validate;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class RangeFilter<T extends Comparable<T>> implements Filter<T>, HideableFilter {

    @Getter
    private T from;

    @Getter
    private T to;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Boolean includeEmptyValues = null;

    public void setFrom(T from) {
        this.from = from;
    }

    public void setTo(T to) {
        this.to = to;
    }

    public RangeFilter(T from, T to) {
        this.from = from;
        this.to = to;
        includeEmptyValues = false;
    }

    @Override
    public boolean isValid() {
        return (to != null || from != null) || includeEmptyValues != null;
    }

    @Override
    public void complete(Filter<T> filter) {
        Validate.isTrue(filter instanceof RangeFilter);
        RangeFilter<T> rangeFilter = (RangeFilter<T>) filter;
        if (rangeFilter.getIncludeEmptyValues() != null && rangeFilter.getIncludeEmptyValues()) {
            includeEmptyValues = true;
        }
        completeWithValue(rangeFilter.to);
        completeWithValue(rangeFilter.from);
    }

    @Override
    public void completeWithValue(T value) {

        if (value == null) {
            includeEmptyValues = true;
        } else {
            if (getFrom() == null) {
                setFrom(value);
            }
            if (getTo() == null) {
                setTo(value);
            }
            if (ObjectUtils.compare(from, value) > 0) {
                setFrom(value);
            }
            if (ObjectUtils.compare(to, value) < 0) {
                setTo(value);
            }
        }
    }

    @Override
    public boolean canBeHidden() {
        boolean canBeHidden = false;
        if (this.getFrom() == null && this.getTo() == null) {
            canBeHidden = true;
        }
        return canBeHidden;
    }
}
