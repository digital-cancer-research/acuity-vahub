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
 * Original source: com.acuity.visualisations.model.timeline.doses.vo.DoseAndFrequency
 * @author ksnd199
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"drug", "dose", "doseUnit", "frequency"})
@ToString
public class DoseAndFrequency implements Serializable {

    /**
     * Drug, ie STDY4321
     */
    private String drug;
    /**
     * Total dose, ie 20
     */
    private Double dose;
    /**
     * Units of dose, ie mg
     */
    private String doseUnit;
    /**
     * Frequency
     */
    private Frequency frequency;

    /**
     * used to calculate the DosePerDay
     *
     * @return
     */
    @JsonIgnore
    public boolean hasFrequency() {
        return frequency != null && frequency.isValid();
    }

    /**
     * frequency use frequencyRank * dose.
     *
     * If 2-per-day on 20mg would be DosePerDay = 40mg
     *
     * @return dose per day
     */
    @JsonIgnore
    public double getDosePerDay() {
            return hasFrequency() ? 0 : frequency.getRank() * dose;
    }

    /**
     * Freq is null, so just use dose
     */
    @JsonIgnore
    public double getDosePerAdmin() {
        return dose;
    }
}
