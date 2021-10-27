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

package com.acuity.visualisations.rawdatamodel.vo.compatibility.linechart;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


import static java.util.stream.Collectors.toList;

@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class OutputErrorLineChartData extends OutputLineChartData {
    public OutputErrorLineChartData(Object seriesBy, List<OutputErrorLineChartEntry> series) {
        super(seriesBy, series.stream().map(ser -> (OutputLineChartEntry) ser).collect(toList()));
    }
}
