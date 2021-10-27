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

import com.acuity.visualisations.rawdatamodel.vo.plots.ShiftPlotCalculationObject;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Comparator;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
@Builder
public final class OutputShiftPlotEntry implements Comparable<OutputShiftPlotEntry>, Serializable {
    private Double x;
    private Double low;
    private Double high;
    private String unit;


    public static OutputShiftPlotEntry of(Double x, ShiftPlotCalculationObject entry) {
        final Object unit = entry.getUnit();
        return new OutputShiftPlotEntry(x, entry.getLow(), entry.getHigh(), unit == null ? "" : unit.toString());
    }

    @Override
    public int compareTo(OutputShiftPlotEntry o) {
        return Comparator.comparing(OutputShiftPlotEntry::getX).compare(this, o);
    }
}
