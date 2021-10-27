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

package com.acuity.visualisations.rawdatamodel.vo.timeline.dose;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * Average percentage change in the total drug amount.
 *
 * <code>
 * Drug 1.  Day 0 10mg,   Day 2 7mg,                Day 20 0mg
 * Drug 2.                           Day 10 100mg,  Day 20 0mg
 * <p>
 * Total Drugs 110.
 * <p>
 * Day 2 % Change.
 * <p>
 * ie So drug 1 change is ((7 - 10) / 10) * 100 = -30%.
 * ie So drug 2 change is ((0 - 100)/ 100) * 100 = -100%.
 * <p>
 * So average change is -30+ -100 = -130 / 2 = -65%
 * <p>
 * Day 10 % Change.
 * <p>
 * ie So drug 1 change is ((7 - 10)/ 10) * 100 = -30%.
 * ie So drug 2 change is ((100 - 100)/ 100) * 100 = 0%
 * <p>
 * So average change is -30 + 0 = 30 / 2 = -15%
 * <p>
 * Day 20 % Change.
 * <p>
 * ie So drug 1 change is ((0 - 10)/ 10) * 100 = -100%.
 * ie So drug 2 change is ((0 - 100)/ 100) * 100 = -100%
 * <p>
 * So average change is -100+ -100 = -200 / 2 = -100%
 * </code>
 *
 * @author ksnd199
 */
@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PercentChange implements Serializable {

    private Double perDay;
    private Double perAdmin;

    public static PercentChange inactive() {
        return new PercentChange(-100., -100.);
    }

    public static PercentChange max() {
        return new PercentChange(0., 0.);
    }

    @JsonIgnore
    public boolean isActive() {
        return perAdmin != null && perAdmin != -100.;
    }
}
