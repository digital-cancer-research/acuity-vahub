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

package com.acuity.visualisations.rawdatamodel.vo.compatibility;

import com.acuity.visualisations.rawdatamodel.vo.plots.BarChartEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TooltipInfoOutputBarChartEntry extends OutputBarChartEntry implements Serializable {
    private Map<String, Object> tooltip;

    public TooltipInfoOutputBarChartEntry(BarChartEntry entry, int rank) {
        super(Objects.toString(((Map) entry.getCategory()).keySet().iterator().next()),
                rank, entry.getValue(), entry.getTotalSubjects());
        this.tooltip = ((Map) entry.getCategory());
    }

    // TODO this constructor works different than previous one; please fix it when possible.
    public TooltipInfoOutputBarChartEntry(BarChartEntry entry, int rank, Map<String, Object> tooltipData) {
        super(entry.getCategory().toString(), rank, entry.getValue(), entry.getTotalSubjects());
        this.tooltip = tooltipData;
    }
}
