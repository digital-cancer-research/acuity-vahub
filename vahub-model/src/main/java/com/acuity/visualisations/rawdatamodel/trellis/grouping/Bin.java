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

package com.acuity.visualisations.rawdatamodel.trellis.grouping;

import com.acuity.visualisations.common.util.ObjectConvertor;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

import static com.acuity.visualisations.rawdatamodel.util.Attributes.DEFAULT_EMPTY_VALUE;
import static com.acuity.visualisations.rawdatamodel.util.Attributes.defaultNullableValue;
@EqualsAndHashCode(of = {"start", "end"})
public abstract class Bin<T extends Comparable<T>> implements Comparable<Bin>, Serializable {

    public abstract T getStart();

    public abstract T getEnd();

    public static Bin empty() {
        return EmptyBin.newBin();
    }

    public static Bin newInstance(Object value, Integer binSize) {
        if (value == null || DEFAULT_EMPTY_VALUE.equals(value)) {
            return Bin.empty();
        } else if (value instanceof Number) {
            Integer intValue = ObjectConvertor.toInt(value);
            return IntBin.newInstance(intValue, binSize);
        } else if (value instanceof Date) {
            return DateBin.newInstance((Date) value, binSize);
        } else if (value instanceof Optional<?>) {
            return newInstance(((Optional<?>) value).orElse(null), binSize);
        } else if (value instanceof Bin) {
            return (Bin) value;
        }
        throw new IllegalArgumentException("Bin can be of type Number or Date");
    }

    @Override
    public String toString() {
        return String.valueOf(defaultNullableValue(isEmpty() ? null
                : getStart().equals(getEnd()) ? getOneArgString() : getTwoArgsString()));
    }

    @Override
    public int compareTo(Bin other) {
        if (this.isEmpty() && other.isEmpty()) {
            return 0;
        } else if (this.isEmpty()) {
            return 1;
        } else if (other.isEmpty()) {
            return -1;
        } else if (!(other.getStart().getClass().isAssignableFrom(this.getStart().getClass()))) {
            throw new ClassCastException("Cannot compare Bins");
        }
        return this.getStart().compareTo((T) other.getStart());
    }

    public abstract <B extends Bin<T>> B getNextBin();

    protected abstract String getOneArgString();

    protected abstract String getTwoArgsString();

    public abstract int getSize();

    public boolean isEmpty() {
        return this instanceof EmptyBin;
    }

}
