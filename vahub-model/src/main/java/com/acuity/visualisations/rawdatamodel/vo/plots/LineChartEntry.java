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

/*
* Note: this class has an ordering that is
* inconsistent with equals.
* */

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class LineChartEntry implements Comparable<LineChartEntry> {
    private Object x;
    private Object y;
    private Object name;
    private Object colorBy;
    private Object sortBy;

    @Override
    public int compareTo(LineChartEntry o) {
        if (o.getSortBy() instanceof String && this.getSortBy() instanceof String) {
            return AlphanumEmptyLastComparator.getInstance().compare((String) this.getSortBy(), (String) o.getSortBy());
        } else {
            if (o.getSortBy() instanceof Comparable && this.getSortBy() instanceof Comparable) {
                return Comparator.<Comparable>naturalOrder().compare((Comparable) this.getSortBy(), (Comparable) o.getSortBy());
            } else {
                return 0;
            }
        }
    }
}

