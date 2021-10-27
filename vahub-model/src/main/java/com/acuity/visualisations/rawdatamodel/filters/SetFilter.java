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

import com.acuity.visualisations.rawdatamodel.util.AlphanumComparator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class SetFilter<T extends Comparable<T>> implements Filter<T>, HideableFilter {

    @Getter
    @JsonIgnore
    private final Set<T> values = new HashSet<T>();

    @JsonProperty("values")
    public List<T> getSortedValues() {
        return values.stream().sorted(new AlphanumComparator<>()).collect(Collectors.toList());
    }

    @JsonProperty("values")
    public void setSortedValues(List<T> sorted) {
        values.clear();
        values.addAll(sorted);
    }

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private Boolean includeEmptyValues = false;

    public SetFilter(Collection<T> values) {
        if (values != null) {
            values.forEach(this::completeWithValue);
        }
    }

    public SetFilter(Set<T> values, boolean includeEmptyValues) {
        this(values);
        this.includeEmptyValues = includeEmptyValues;
    }

    public SetFilter(List<T> values, boolean includeEmptyValues) {
        this(values);
        this.includeEmptyValues = includeEmptyValues;
    }

    @Override
    public boolean isValid() {
        return (values != null && !values.isEmpty()) || includeEmptyValues;
    }

    public void complete(Filter<T> filter) {
        Validate.isTrue(filter instanceof SetFilter);
        SetFilter<T> setFilter = (SetFilter<T>) filter;
        if (setFilter.getIncludeEmptyValues()) {
            includeEmptyValues = true;
        }
        values.addAll(setFilter.getValues());
    }

    @Override
    public void completeWithValue(T value) {
        values.add(value);
        if (value == null) {
            includeEmptyValues = true;
        }
    }

    @Override
    public boolean canBeHidden() {
        boolean canBeHidden = false;
        boolean isEmpty = CollectionUtils.isEmpty(this.getValues());
        boolean onlyContainsNull = this.getValues().size() == 1
                && (this.getValues().contains("(Empty)") || this.getValues().contains(null));
        if (isEmpty || onlyContainsNull) {
            canBeHidden = true;
        }
        return canBeHidden;
    }
}
