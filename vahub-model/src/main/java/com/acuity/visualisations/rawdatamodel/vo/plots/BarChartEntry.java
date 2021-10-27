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

package com.acuity.visualisations.rawdatamodel.vo.plots;

import com.acuity.visualisations.rawdatamodel.vo.HasStringId;
import com.acuity.visualisations.rawdatamodel.vo.HasSubject;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Comparator;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BarChartEntry<T extends HasStringId & HasSubject> implements Comparable<BarChartEntry<T>> {
    private Object category;
    private Double value;
    private Integer totalSubjects;
    @JsonIgnore
    private Collection<T> eventSet;

    public BarChartEntry(Object category, Double value) {
        this(category, value, null, null);
    }

    @Override
    public int compareTo(BarChartEntry<T> o) {
        if (o.getCategory() instanceof Comparable && this.getCategory() instanceof Comparable) {
            return Comparator.<Comparable>naturalOrder().compare((Comparable) this.getCategory(), (Comparable) o.getCategory());
        } else {
            return 0;
        }
    }
}
