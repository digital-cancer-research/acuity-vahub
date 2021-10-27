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

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinMax implements Serializable {

    private Object min;
    private Object max;

    /**
     * Is this a null object
     */
    public boolean isNull() {
        return min == null && max == null;
    }

    /**
     * Is this not a null object
     */
    public boolean isNotNull() {
        return !isNull();
    }

    /**
     * Is this not a null object
     */
    public static boolean isNotNull(MinMax minMax) {
        return minMax != null && minMax.isNotNull();
    }
}
