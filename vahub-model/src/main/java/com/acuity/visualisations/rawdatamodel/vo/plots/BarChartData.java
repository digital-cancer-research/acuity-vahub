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

import com.acuity.visualisations.rawdatamodel.util.AlphanumEmptyLastComparator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Comparator;
import java.util.List;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class BarChartData implements Comparable<BarChartData> {
    private Object name;
    private List<?> categories;
    private List<? extends BarChartEntry> series;

    @Override
    public int compareTo(BarChartData o) {
        if (o.getName() instanceof String && this.getName() instanceof String) {
            return AlphanumEmptyLastComparator.getInstance().compare((String) this.getName(), (String) o.getName());
        } else {
            if (o.getName() instanceof Comparable && this.getName() instanceof Comparable) {
                return Comparator.<Comparable>naturalOrder().compare((Comparable) this.getName(), (Comparable) o.getName());
            } else {
                return 0;
            }
        }
    }
}
