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

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

import static com.acuity.visualisations.rawdatamodel.util.ObjectUtil.allNull;

@Data
@AllArgsConstructor
@EqualsAndHashCode
public final class ShiftPlotEntry implements Serializable {
    private Double x;
    private Double low;
    private Double high;

    private ShiftPlotEntry() {
    }

    public static ShiftPlotEntry empty() {
        return new ShiftPlotEntry();
    }

    public boolean isNotEmpty() {
        return !allNull(x, low, high);
    }
}
