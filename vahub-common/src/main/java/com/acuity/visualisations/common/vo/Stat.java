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

package com.acuity.visualisations.common.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Set;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Set of statistical values used for box-and-whiskers plots - please see more here:
 *
 * http://en.wikipedia.org/wiki/Box_plot
 * http://www.mathsisfun.com/data/quartiles.html
 */
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Stat implements Serializable {

    /**
     * day, week or visit number
     */
    private Number x;

    /** the middle value of the set of data, or from 25% to 75% - please see more here
     *  http://en.wikipedia.org/wiki/Quartile
     */
    private Double median;

    /**
     * from 75% to 100% - please see more here http://en.wikipedia.org/wiki/Quartile
     */
    private Double upperQuartile;

    /**
     * from 0% to 25% - please see more here http://en.wikipedia.org/wiki/Quartile
     */
    private Double lowerQuartile;

    /**
     * the highest datum still within 1.5 IQR of the upper quartile (Tukey)
     */
    private Double upperWhisker;

    /**
     * the lowest datum still within 1.5 IQR of the lower quartile (Tukey)
     */
    private Double lowerWhisker;

    /**
     * Set of observation points that is distant from other observations.
     */
    private Set<Double> outliers;
}
