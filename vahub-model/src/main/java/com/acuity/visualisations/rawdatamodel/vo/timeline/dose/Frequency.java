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
 * @author ksnd199
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"})
@ToString
public class Frequency implements Serializable {

    private static final Frequency NONE = new Frequency("N/A", 0);

    /**
     * CDash equivalent of frequencyAndUnit, 1-per-day = QD
     * <p>
     * See RCT-3938
     */
    private String name;
    /**
     * CDash daily normalised freq, calculated in etl Ie old frequency_daily_normalized, ie 1-per-hour = 24, 1-per-day = 1
     * <p>
     * See RCT-3938
     */
    private double rank;

    /**
     * used to calculate the DosePerDay
     *
     * @return
     */
    @JsonIgnore
    public boolean isValid() {
        return name != null;
    }

    public static Frequency none() {
        return NONE;
    }
}
